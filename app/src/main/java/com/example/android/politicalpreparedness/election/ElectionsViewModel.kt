package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import timber.log.Timber


enum class CivicsAPIStatus { LOADING, ERROR, DONE }

class ElectionsViewModel(private val database: ElectionDao, private val apiService: CivicsApiService) : ViewModel() {

    private val _civicAPIStatus = MutableLiveData<CivicsAPIStatus>()
    val civicAPIStatus: LiveData<CivicsAPIStatus>
        get() = _civicAPIStatus

    private val _isDBLoading = MutableLiveData<Boolean>()
    val isDBLoading: LiveData<Boolean>
        get() = _isDBLoading

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo

    init {
        fetchUpcomingElectionResponseFromAPI()
        fetchSavedElectionResponseFromDB()
    }

    private fun fetchUpcomingElectionResponseFromAPI() {
        viewModelScope.launch {
            _civicAPIStatus.value = CivicsAPIStatus.LOADING
            try {
                val response = apiService.getElections()
                _civicAPIStatus.value = CivicsAPIStatus.DONE
                _upcomingElections.value = response.elections

            } catch (e: Exception) {
                Timber.e("Error: %s", e.localizedMessage)
                _civicAPIStatus.value = CivicsAPIStatus.ERROR
                _upcomingElections.value = ArrayList()
            }
        }
    }

    private fun fetchSavedElectionResponseFromDB() {
        _isDBLoading.value = true
        viewModelScope.launch {
            _savedElections.value = database.getElectionElements()
            _isDBLoading.value = false
        }
    }

    fun displayVoterInfo(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun displayVoterInfoComplete() {
        _navigateToVoterInfo.value = null
    }

    fun refresh() {
        fetchSavedElectionResponseFromDB()
    }
}