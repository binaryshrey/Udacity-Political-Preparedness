package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment : Fragment() {

    private lateinit var binding : FragmentElectionBinding
    private val electionsViewModel: ElectionsViewModel by activityViewModels {
        ElectionsViewModelFactory(ElectionDatabase.getInstance(requireContext()).electionDao, CivicsApi.retrofitService)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.electionsViewModel = electionsViewModel

        setupAdapter()
        setupObservers()

        return binding.root
    }

    private fun setupObservers() {
        electionsViewModel.navigateToVoterInfo.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                navigateToDetailFragment(it)
                electionsViewModel.displayVoterInfoComplete()
            }
        })
    }

    private fun setupAdapter() {
        binding.upcomingRV.adapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
            electionsViewModel.displayVoterInfo(it)
        })

        binding.savedRV.adapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
            electionsViewModel.displayVoterInfo(it)
        })
    }

    private fun navigateToDetailFragment(election: Election) {
        findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election.id, election.division))
    }


    override fun onResume() {
        super.onResume()
        electionsViewModel.refresh()
    }
}