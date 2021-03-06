package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel
import junit.framework.Assert.assertEquals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.P])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    private lateinit var reminderDataSource: FakeDataSource

    private lateinit var applicationContext: Application

    private val reminder =
        ReminderDataItem("Title", "Description", "Location", 0.0, 0.0)

    @Before
    fun setupViewModel() {
        stopKoin()
        reminderDataSource = FakeDataSource()
        applicationContext = ApplicationProvider.getApplicationContext()
        saveReminderViewModel =
            SaveReminderViewModel(applicationContext, reminderDataSource)
    }

    @Test
    fun saveReminder_check_loading() = mainCoroutineRule.runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(reminder)
        assertThat(saveReminderViewModel.showLoading.value).isTrue()
        mainCoroutineRule.resumeDispatcher()
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue()).isFalse()
    }

    @Test
    fun saveReminder_showToast() = mainCoroutineRule.runBlockingTest {
        saveReminderViewModel.saveReminder(reminder)
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue()).isFalse()
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue()).isEqualTo(
            applicationContext.getString(
                R.string.reminder_saved
            )
        )
    }

    @Test
    fun saveReminder_navigateBack() = mainCoroutineRule.runBlockingTest {
        saveReminderViewModel.saveReminder(reminder)
        assertThat(saveReminderViewModel.navigationCommand.getOrAwaitValue()).isEqualTo(
            NavigationCommand.Back
        )
    }

    @Test
    fun testOnClear() = mainCoroutineRule.runBlockingTest {
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.onClear()
        mainCoroutineRule.resumeDispatcher()
        assertThat(saveReminderViewModel.reminderTitle.getOrAwaitValue()).isNull()
        assertThat(saveReminderViewModel.reminderDescription.getOrAwaitValue()).isNull()
        assertThat(saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue()).isNull()
        assertThat(saveReminderViewModel.selectedPOI.getOrAwaitValue()).isNull()
        assertThat(saveReminderViewModel.latitude.getOrAwaitValue()).isNull()
        assertThat(saveReminderViewModel.longitude.getOrAwaitValue()).isNull()
    }

    @Test
    fun validateEnteredData_nullTitle_shouldReturnError() = mainCoroutineRule.runBlockingTest {
        reminder.title = null
        saveReminderViewModel.validateEnteredData(reminder)
        assertEquals(
            R.string.err_enter_title,
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        )
    }

    @Test
    fun validateEnteredData_nullLocation_shouldReturnError() = mainCoroutineRule.runBlockingTest {
        reminder.location = null
        saveReminderViewModel.validateEnteredData(reminder)
        assertEquals(
            R.string.err_select_location,
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        )
    }

    @Test
    fun shouldReturnErrorValidateAndSaveReminder() = mainCoroutineRule.runBlockingTest {
        reminder.title = null
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(reminder)
        mainCoroutineRule.resumeDispatcher()
        assertEquals(
            R.string.err_enter_title,
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue()
        )
    }

}