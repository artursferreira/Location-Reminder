package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import org.junit.Assert.*

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase
    private var reminder: ReminderDTO = ReminderDTO("Title", "Description", "Location", 0.0, 0.0)

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()


    @Test
    fun remindersDao_getReminders() = runBlockingTest {
        database.reminderDao().saveReminder(reminder)
        val reminders = database.reminderDao().getReminders()
        assertNotNull(reminders)
        assertEquals(1, reminders.size)
    }

    @Test
    fun remindersDao_getReminderById() = runBlockingTest {
        database.reminderDao().saveReminder(reminder)
        val savedReminder = database.reminderDao().getReminderById(reminder.id)
        assertNotNull(savedReminder)
        assertEquals(reminder.title, savedReminder?.title)
        assertEquals(reminder.description, savedReminder?.description)
        assertEquals(reminder.location, savedReminder?.location)
        assertEquals(reminder.latitude, savedReminder?.latitude)
        assertEquals(reminder.longitude, savedReminder?.longitude)
        assertEquals(reminder.id, savedReminder?.id)
    }

    @Test
    fun remindersDao_getReminderByIdError() = runBlockingTest {
        database.reminderDao().saveReminder(reminder)
        val savedReminder = database.reminderDao().getReminderById("reminderId")
        assertNull(savedReminder)
    }

    @Test
    fun remindersDao_saveReminder() = runBlockingTest {
        database.reminderDao().saveReminder(reminder)
        val savedReminder = database.reminderDao().getReminderById(reminder.id)
        assertNotNull(savedReminder)
    }

    @Test
    fun remindersDao_deleteAllReminders() = runBlockingTest {
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().deleteAllReminders()
        val savedReminder = database.reminderDao().getReminders()
        assertEquals(0, savedReminder.size)
    }

}