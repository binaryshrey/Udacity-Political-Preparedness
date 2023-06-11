package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.election.CivicsAPIStatus
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch


class RepresentativeViewModel(private val savedHandle: SavedStateHandle) : ViewModel() {

    private val _representativeVMAPIStatus = MutableLiveData<CivicsAPIStatus>()
    val representativeVMAPIStatus: LiveData<CivicsAPIStatus>
        get() = _representativeVMAPIStatus

    private val _representativesList = MutableLiveData<List<Representative>>()
    val representativesList: LiveData<List<Representative>>
        get() = _representativesList

    private val _completeAddress = MutableLiveData<Address>()
    val completeAddress: LiveData<Address>
        get() = _completeAddress


    // initialize VM variables
    init {
        val list: List<Representative>? = savedHandle["representatives"]
        if (list != null) {
            _representativesList.value = list!!
        }
        _completeAddress.value = Address("", null, "", "", "")
    }

    //get complete list of representatives via retrofit call
    fun getRepresentativesList(address: Address?) {
        viewModelScope.launch {
            _representativeVMAPIStatus.value = CivicsAPIStatus.LOADING
            _representativesList.value = arrayListOf()

            if (address != null) {
                try {
                    _completeAddress.value = address!!
                    val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(_completeAddress.value?.toFormattedString()!!)
                    _representativesList.value = offices.flatMap { office -> office.getRepresentatives(officials) }
                    savedHandle["representatives"] = _representativesList.value
                    _representativeVMAPIStatus.value = CivicsAPIStatus.DONE
                } catch (e: Exception) {
                    _representativeVMAPIStatus.value = CivicsAPIStatus.ERROR
                }
            }
        }
    }

    //get complete list of representatives via retrofit call with address
    fun getRepresentativesListWithAddress() {
        getRepresentativesList(_completeAddress.value)
    }
}
