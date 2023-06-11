package com.example.android.politicalpreparedness.launch

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding

class LaunchFragment : Fragment() {

    private lateinit var binding : FragmentLaunchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_launch, container, false)
        binding.lifecycleOwner = this
        setupOnClickListeners()

        return binding.root
    }

    private fun setupOnClickListeners() {
        binding.repButton.setOnClickListener {
            findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToRepresentativeFragment())
        }
        binding.upcomingButton.setOnClickListener {
            findNavController().navigate(LaunchFragmentDirections.actionLaunchFragmentToElectionsFragment())
        }
    }
}
