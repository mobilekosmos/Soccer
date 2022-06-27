package com.mobilekosmos.android.clubs.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ClubEntity(
    @field:Json(name = "name") val name: String,
    @field:Json(name = "country") val country: String,
    @field:Json(name = "value") val value: Int,
    @field:Json(name = "image") val image: String,
    @field:Json(name = "european_titles") val european_titles: Short
) : Parcelable