package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division

class VoterInfoFragment : Fragment() {

    private lateinit var binding : FragmentVoterInfoBinding
    private val voterInfoViewModel: VoterInfoViewModel by activityViewModels {
        VoterInfoViewModelFactory(ElectionDatabase.getInstance(requireContext()).electionDao, CivicsApi.retrofitService)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val voterInfoFragmentArgs = VoterInfoFragmentArgs.fromBundle(requireArguments())
        val electionId = voterInfoFragmentArgs.argElectionId
        val division = voterInfoFragmentArgs.argDivision

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.voterInfoViewModel = voterInfoViewModel

        getVoterInfo(electionId,division)
        setupObservers()

        return binding.root
    }


    private fun setupObservers() {
        voterInfoViewModel.civicAPIStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == CivicsAPIStatus.ERROR) {
                    showErrorDialog()
                }
            }
        })

        voterInfoViewModel.url.observe(viewLifecycleOwner, Observer { it ->
            it?.let {
                loadURL(it)
                voterInfoViewModel.navigateToUrlCompleted()
            }
        })
    }


    private fun getVoterInfo(electionId: Int, division: Division) {
        voterInfoViewModel.getVoterInformation(electionId, division)
    }


    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Error")
            .setMessage("Error retrieving voter's information. Click OK to go back!")
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
                findNavController().popBackStack()
            }.show()
    }


    private fun loadURL(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}