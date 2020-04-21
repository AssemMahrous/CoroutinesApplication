package com.trial.myapplication.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.trial.myapplication.BuildConfig
import com.trial.myapplication.core.data.model.Cover
import com.trial.myapplication.core.data.model.MainArtist
import com.trial.myapplication.core.data.model.MusicPayloadItem
import com.trial.myapplication.core.util.Constants.LIMIT
import com.trial.myapplication.core.util.Constants.QUERY
import com.trial.myapplication.core.util.Constants.SEARCH_URL
import com.trial.myapplication.core.util.Constants.TOKEN_URL
import com.trial.myapplication.core.util.RequestHandler
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import org.json.JSONArray
import org.json.JSONObject


class MusicViewModel : ViewModel() {
    companion object {
        const val SEARCH_DELAY_MS = 500L
        const val MIN_QUERY_LENGTH = 2
    }
    var token: String? = null

    @ExperimentalCoroutinesApi
    internal val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

    @FlowPreview
    @ExperimentalCoroutinesApi
    val internalSearchResult = queryChannel
        .asFlow()
        .debounce(SEARCH_DELAY_MS)
        .mapLatest {
            try {
                if (it.length >= MIN_QUERY_LENGTH) {
                    if (token == null) {
                        val data =
                            withContext(Dispatchers.IO) {
                                RequestHandler.sendPost(
                                    BuildConfig.SERVER_URL + TOKEN_URL
                                )
                            }
                        token = JSONObject(data).getString("accessToken")
                    }

                    val postDataParams = JSONObject()
                    postDataParams.put(QUERY, it)
                    postDataParams.put(LIMIT, "20")
                    val urlParameters = arrayOf(
                        QUERY,
                        it,
                        LIMIT,
                        "20"
                    )
                    val searchData =
                        withContext(Dispatchers.IO) {
                            RequestHandler().sendGet(
                                BuildConfig.SERVER_URL + SEARCH_URL,
                                token,
                                urlParameters
                            )
                        }
                    if (searchData.isNotEmpty()) {
                        ValidResult(parseData(searchData))
                    } else {
                        EmptyResult
                    }
                } else {
                    EmptyQuery
                }
            } catch (e: Throwable) {
                if (e is CancellationException) {
                    println("Search was cancelled!")
                    throw e
                } else {
                    ErrorResult(e)
                }
            }
        }
        .catch { emit(TerminalError) }

    @FlowPreview
    @ExperimentalCoroutinesApi
    val searchResult = internalSearchResult.asLiveData()

    private fun parseData(searchData: String): List<MusicPayloadItem> {
        val list = ArrayList<MusicPayloadItem>()
        val jsonArray = JSONArray(searchData)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val cover = try {
                Cover(
                    jsonObject.getJSONObject("cover").getString("default"),
                    jsonObject.getJSONObject("cover").getString("large"),
                    jsonObject.getJSONObject("cover").getString("medium"),
                    jsonObject.getJSONObject("cover").getString("small"),
                    jsonObject.getJSONObject("cover").getString("template"),
                    jsonObject.getJSONObject("cover").getString("tiny")
                )
            } catch (e: Exception) {
                null
            }

            val artist = try {
                MainArtist(
                    0,
                    jsonObject.getJSONObject("mainArtist").getString("name")
                )
            } catch (e: Exception) {
                null
            }
            val title = try {
                jsonObject.getString("title")
            } catch (e: Exception) {
                null
            }
            val type = try {
                jsonObject.getString("type")
            } catch (e: Exception) {
                null
            }
            list.add(
                MusicPayloadItem(
                    id = jsonObject.getInt("id"),
                    cover = cover,
                    mainArtist = artist,
                    title = title,
                    type = type
                )
            )
        }
        return list
    }
}

@Suppress("UNCHECKED_CAST")
class MusicViewModelFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (MusicViewModel() as T)
}

sealed class SearchResult
class ValidResult(val result: List<MusicPayloadItem>) : SearchResult()
object EmptyResult : SearchResult()
object EmptyQuery : SearchResult()
class ErrorResult(val e: Throwable) : SearchResult()
object TerminalError : SearchResult()
