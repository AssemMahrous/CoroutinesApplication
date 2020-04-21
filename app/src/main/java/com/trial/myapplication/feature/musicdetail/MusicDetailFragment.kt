package com.trial.myapplication.feature.musicdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.trial.myapplication.BuildConfig
import com.trial.myapplication.R
import com.trial.myapplication.core.util.ImageLoader
import kotlinx.android.synthetic.main.fragment_music_detail.*

/**
 * A simple [Fragment] subclass.
 */
class MusicDetailFragment : Fragment() {
    val args: MusicDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imgLoader =
            ImageLoader(context)
        val loader: Int = R.drawable.ic_launcher_foreground
        val imageUrl = BuildConfig.SERVER_URL + args.photo
        imgLoader.DisplayImage(imageUrl, loader, img_item)
        tv_song_name.text = args.songName
        tv_artist_name.text = args.artistName
        tv_song_type.text = args.songType
    }
}
