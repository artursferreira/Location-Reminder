package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.P])
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var remindersListViewModel: RemindersListViewModel

    private lateinit var reminderDataSource: FakeDataSource

    private val reminderList =
        mutableListOf(ReminderDTO("Title", "Description", "Location", 0.0, 0.0))

    @Before
    fun setupViewModel() {
        stopKoin()
        reminderDataSource = FakeDataSource()
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), reminderDataSource)
    }

    @Test
    fun getRemindersList() {
        reminderDataSource.reminders = reminderList
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue()).isNotEmpty()
        assertThat(remindersListViewModel.remindersList.getOrAwaitValue()).hasSize(reminderList.size)
    }

    @Test
    fun checkLoading() {
        reminderDataSource.reminders = reminderList
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showLoading.getOrAwaitValue()).isTrue()
    }

    @Test
    fun returnError() {
        reminderDataSource.reminders = null
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showSnackBar.getOrAwaitValue()).isEqualTo("Reminders not found")
    }

}