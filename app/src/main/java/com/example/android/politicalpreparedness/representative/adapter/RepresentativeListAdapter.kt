package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ItemRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.Representative


class RepresentativeListAdapter: ListAdapter<Representative, RepresentativeListAdapter.ViewHolder>(RepresentativeDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(val binding: ItemRepresentativeBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Representative) {
            binding.representative = item
            binding.avatarIV.setImageResource(R.drawable.acc)

            item.official.channels?.let { showAllLinks(it, binding, itemView) }
            item.official.urls?.let { showWebLinks(it, binding, itemView) }

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRepresentativeBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
}



class RepresentativeDiffCallback : DiffUtil.ItemCallback<Representative>() {
    override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        return ((oldItem.office == newItem.office) && (oldItem.official == newItem.official))
    }

    override fun areContentsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        return oldItem == newItem
    }
}


private fun showWebLinks(urls: List<String>, binding: ItemRepresentativeBinding, itemView: View) {
    checkLinkAvailiblity(binding.webIV , urls.first(), itemView)
}


private fun showAllLinks(channels: List<Channel>, binding: ItemRepresentativeBinding, itemView: View) {

    val twitterUrl = getTwitterURL(channels)
    if (!twitterUrl.isNullOrBlank()) { checkLinkAvailiblity(binding.twitterIV , twitterUrl, itemView) }

    val facebookUrl = getFBURL(channels)
    if (!facebookUrl.isNullOrBlank()) { checkLinkAvailiblity(binding.fbIV , facebookUrl, itemView) }

}


private fun getFBURL(channels: List<Channel>): String? {
    return channels.filter { channel -> channel.type == "Facebook" }
        .map { channel -> "https://www.facebook.com/${channel.id}" }
        .firstOrNull()
}

private fun getTwitterURL(channels: List<Channel>): String? {
    return channels.filter { channel -> channel.type == "Twitter" }
        .map { channel -> "https://www.twitter.com/${channel.id}" }
        .firstOrNull()
}

private fun checkLinkAvailiblity(view: ImageView, url: String, itemView: View) {
    view.visibility = View.VISIBLE
    view.setOnClickListener { openURLViaIntent(url, itemView) }
}

private fun openURLViaIntent(url: String, itemView: View) {
    if(!url.isNullOrEmpty()){
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }

}