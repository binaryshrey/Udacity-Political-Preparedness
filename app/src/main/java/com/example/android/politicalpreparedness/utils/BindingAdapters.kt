package com.example.android.politicalpreparedness.utils

import android.view.View
import android.widget.*
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.CivicsAPIStatus
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.model.Representative
import java.text.SimpleDateFormat
import java.util.*


// set RecyclerView list data
@BindingAdapter("rvListData")
fun RecyclerView.setElectionData(data: List<Election>?) {
    val adapter = adapter as ElectionListAdapter
    adapter.submitList(data)
}


// set ImageView based on CivicsAPIStatus value
@BindingAdapter("civicsAPIStatus")
fun civicsAPIStatus(statusImageView: ImageView, status: CivicsAPIStatus?) {
    when (status) {
        CivicsAPIStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        CivicsAPIStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.network_error)
        }
        CivicsAPIStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
        else -> statusImageView.visibility = View.GONE
    }
}


// extension function on TextView to set Election Day
@BindingAdapter("setElectionDay")
fun TextView.setElectionDay(date: Date?) {
    val calendar = Calendar.getInstance()
    date?.let {
        calendar.time = it
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        text = dateFormat.format(calendar.time)
    }
}


// extension function on Button view to set button text to follow or unfollow
@BindingAdapter("undoFollowElection")
fun Button.undoFollowElection(hasSaved: Boolean?) {
    hasSaved?.let {
        text = if (it) "unfollow election" else "follow election"
    }
}


// set RecyclerView representative list data
@BindingAdapter("representativeList")
fun RecyclerView.setRepresentativeData(data: List<Representative>?) {
    val adapter = adapter as RepresentativeListAdapter
    adapter.submitList(data)
}


// use Glide to set profile image on ImageView
@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        Glide.with(view.context)
            .load(uri)
            .placeholder(R.drawable.acc)
            .error(R.drawable.acc)
            .circleCrop()
            .into(view)
    }
}


// set spinner value
@BindingAdapter("newValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

// set spinner value
@InverseBindingAdapter(attribute = "stateValue")
fun Spinner.getNewValue(): String {
    val states: Array<String> = resources.getStringArray(R.array.states)
    return states[this.selectedItemPosition]
}


@BindingAdapter("stateValueAttrChanged")
fun setStateListener(spinner: Spinner, stateChange: InverseBindingListener?) {
    if (stateChange == null) {
        spinner.onItemSelectedListener = null
    } else {
        val listener: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stateChange.onChange()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                stateChange.onChange()
            }
        }
        spinner.onItemSelectedListener = listener
    }
}


// extension function on TextView to set Election Day
@BindingAdapter("APIStatusTV")
fun TextView.setStatus(status: CivicsAPIStatus?) {
    when (status) {
        CivicsAPIStatus.LOADING -> text = "Loading…"
        CivicsAPIStatus.DONE -> text = ""
        CivicsAPIStatus.ERROR -> text = "Error when retrieving representatives"
        else -> visibility = View.INVISIBLE
    }
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
    return adapter as ArrayAdapter<T>
}