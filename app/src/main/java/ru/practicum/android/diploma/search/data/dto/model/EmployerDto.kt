package ru.practicum.android.diploma.search.data.dto.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EmployerDto(
    val id: String,
    val name: String,
    @SerializedName("logo_urls") val logoUrls: LogoUrls? = null,
) : Serializable
