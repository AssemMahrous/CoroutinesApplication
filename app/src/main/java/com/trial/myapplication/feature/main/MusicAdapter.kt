package com.trial.myapplication.feature.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.trial.myapplication.BuildConfig
import com.trial.myapplication.R
import com.trial.myapplication.core.data.model.MusicPayloadItem
import com.trial.myapplication.core.util.ImageLoader
import kotlinx.android.synthetic.main.music_list_item.view.*

class MusicAdapter(
    val listener: (data: MusicPayloadItem) -> Unit
) : ListAdapter<MusicPayloadItem, MusicAdapter.MusicViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.music_list_item, parent, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            item: MusicPayloadItem,
            listener: (item: MusicPayloadItem) -> Unit
        ) = with(itemView) {
            val imgLoader =
                ImageLoader(itemView.context)
            val loader: Int = R.drawable.ic_launcher_foreground
            val imageUrl = BuildConfig.SERVER_URL + item.cover?.medium
            imgLoader.DisplayImage(imageUrl, loader, img_item)
            tv_song_name.text = item.title
            tv_artist_name.text = item.mainArtist?.name
            tv_song_type.text = item.type
            itemView.setOnClickListener { listener(item) }
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<MusicPayloadItem>() {
            override fun areItemsTheSame(
                oldItem: MusicPayloadItem,
                newItem: MusicPayloadItem
            ): Boolean =
                oldItem.title == newItem.title

            override fun areContentsTheSame(
                oldItem: MusicPayloadItem,
                newItem: MusicPayloadItem
            ): Boolean =
                oldItem == newItem
        }
    }
}