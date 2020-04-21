package com.trial.myapplication.core.data.model

data class MusicPayloadItem(
    val cover: Cover?,
    val id: Int,
    val mainArtist: MainArtist?,
    val title: String?,
    val type: String?
)

data class MainArtist(
    val id: Int,
    val name: String
)

data class Cover(
    val default: String,
    val large: String,
    val medium: String,
    val small: String,
    val template: String,
    val tiny: String
)