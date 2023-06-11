package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch
import timber.log.Timber


class VoterInfoViewModel(private val dataSource: ElectionDao, private val apiService: CivicsApiService) : ViewModel() {

    private val _civicAPIStatus = MutableLiveData<CivicsAPIStatus>()
    val civicAPIStatus: LiveData<CivicsAPIStatus>
        get() = _civicAPIStatus

    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election>
        get() = _election

    private val _administrationBody = MutableLiveData<AdministrationBody>()
    val administrationBody: LiveData<AdministrationBody>
        get() = _administrationBody

    private val _url = MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url

    private val _isElectionSaved = MutableLiveData<Boolean>()
    val isElectionSaved: LiveData<Boolean>
        get() = _isElectionSaved

    fun getVoterInformation(electionId: Int, division: Division) {
        viewModelScope.launch {
            _civicAPIStatus.value = CivicsAPIStatus.LOADING
            try {
                val savedElection = dataSource.getElectionElement(electionId)
                _isElectionSaved.value = savedElection != null

                val address = "${division.state}, ${division.country}"
                val voterInfoResponse = apiService.getVoterInfo(address, electionId)
                _election.value = voterInfoResponse.election
                _administrationBody.value = voterInfoResponse.state?.first()?.electionAdministrationBody
                _civicAPIStatus.value = CivicsAPIStatus.DONE
            } catch (e: Exception) {
                Timber.e("ERR: ${e.message.toString()}")
                _civicAPIStatus.value = CivicsAPIStatus.ERROR
            }
        }
    }

    fun navigateToUrl(url: String) {
        _url.value = url
    }

    fun navigateToUrlCompleted() {
        _url.value = null
    }

    fun toggleFollowOrUnfollowElection() {
        viewModelScope.launch {
            _election.value?.let {
                if (_isElectionSaved.value == true) {
                    dataSource.deleteElectionElement(it)
                    _isElectionSaved.value = false
                } else {
                    dataSource.insertElectionElement(it)
                    _isElectionSaved.value = true
                }
            }
        }
    }
}