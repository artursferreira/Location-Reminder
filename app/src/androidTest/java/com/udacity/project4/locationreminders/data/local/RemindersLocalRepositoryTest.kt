package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import junit.framework.Assert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersDatabase: RemindersDatabase
    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private var reminder: ReminderDTO = ReminderDTO("Title", "Description", "Location", 0.0, 0.0)

    @Before
    fun setup() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        remindersLocalRepository = RemindersLocalRepository(remindersDatabase.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDB() = remindersDatabase.close()

    @Test
    fun testGetReminders() = mainCoroutineRule.runBlockingTest {
        remindersLocalRepository.saveReminder(reminder)
        val remindersList = remindersLocalRepository.getReminders() as Result.Success<List<ReminderDTO>>
        assertEquals(1, remindersList.data.size)
        assertEquals(reminder.title, remindersList.data.first().title)
        assertEquals(reminder.description, remindersList.data.first().description)
        assertEquals(reminder.location, remindersList.data.first().location)
        assertEquals(reminder.latitude, remindersList.data.first().latitude)
        assertEquals(reminder.longitude, remindersList.data.first().longitude)
        assertEquals(reminder.id, remindersList.data.first().id)
    }

    @Test
    fun testSaveReminders() = mainCoroutineRule.runBlockingTest {
        remindersLocalRepository.saveReminder(reminder)
        val remindersList = remindersLocalRepository.getReminders() as Result.Success<List<ReminderDTO>>
        assertEquals(1, remindersList.data.size)
        assertEquals(reminder.id, remindersList.data.first().id)
        assertEquals(reminder.description, remindersList.data.first().description)
        assertEquals(reminder.location, remindersList.data.first().location)
        assertEquals(reminder.latitude, remindersList.data.first().latitude)
        assertEquals(reminder.longitude, remindersList.data.first().longitude)
    }

    @Test
    fun testGetReminder() = mainCoroutineRule.runBlockingTest {
        remindersLocalRepository.saveReminder(reminder)
        val reminderResult =
            remindersLocalRepository.getReminder(reminder.id) as Result.Success<ReminderDTO>
        assertNotNull(reminderResult)
        assertEquals(reminder.id, reminderResult.data.id)
        assertEquals(reminder.description, reminderResult.data.description)
        assertEquals(reminder.location, reminderResult.data.location)
        assertEquals(reminder.latitude, reminderResult.data.latitude)
        assertEquals(reminder.longitude, reminderResult.data.longitude)
    }

    @Test
    fun testGetReminderError() = mainCoroutineRule.runBlockingTest {
        remindersLocalRepository.saveReminder(reminder)
        val reminderResult = remindersLocalRepository.getReminder(" ") as Result.Error
        assertNotNull(reminderResult)
        assertEquals("Reminder not found!", reminderResult.message)
        assertNull(reminderResult.statusCode)
    }

    @Test
    fun testDeleteAllReminders() = mainCoroutineRule.runBlockingTest {
        remindersLocalRepository.saveReminder(reminder)
        remindersLocalRepository.deleteAllReminders()
        val remindersList = remindersLocalRepository.getReminders() as Result.Success<List<ReminderDTO>>
        assertEquals(0, remindersList.data.size)
    }

}