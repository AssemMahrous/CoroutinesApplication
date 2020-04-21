package com.trial.myapplication.feature.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.trial.myapplication.R
import com.trial.myapplication.core.data.model.MusicPayloadItem
import kotlinx.android.synthetic.main.fragment_music.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class MusicFragment : Fragment() {
    private val viewModel by viewModels<MusicViewModel> {
        MusicViewModelFactory()
    }
    private val adapter = MusicAdapter {
        openMusicDetails(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music, container, false)
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        music_list.adapter = adapter
        viewModel.searchResult.observe(
            this.viewLifecycleOwner,
            Observer {
                handleSearchResult(it)
            })
        music_filter.doAfterTextChanged {
            lifecycleScope.launch {
                viewModel.queryChannel.send(it.toString())
            }
        }
    }

    private fun openMusicDetails(it: MusicPayloadItem) {
        val action = MusicFragmentDirections.actionMusicFragmentToMusicDetailFragment(
            photo = it.cover?.large,
            artistName = it.mainArtist?.name,
            songName = it.title,
            songType = it.type
        )
        findNavController().navigate(action)
    }

    private fun handleSearchResult(it: SearchResult) {
        when (it) {
            is ValidResult -> {
                adapter.submitList(it.result)
                otherResultText.visibility = View.GONE
            }
            is ErrorResult -> {
                adapter.submitList(emptyList())
                otherResultText.visibility = View.VISIBLE
                otherResultText.setText(R.string.search_error)

            }
            is EmptyResult -> {
                adapter.submitList(emptyList())
                otherResultText.visibility = View.VISIBLE
                otherResultText.setText(R.string.empty_result)
            }
            is EmptyQuery -> {
                adapter.submitList(emptyList())
                otherResultText.visibility = View.VISIBLE
                otherResultText.setText(R.string.not_enough_characters)
            }
            is TerminalError -> {
                // Something wen't terribly wrong!
                println("Our Flow terminated unexpectedly, so we're bailing!")
                Toast.makeText(
                    context,
                    "Unexpected error in SearchRepository!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
