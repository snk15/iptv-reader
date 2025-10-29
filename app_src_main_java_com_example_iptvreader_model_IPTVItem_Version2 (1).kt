package com.example.iptvreader.model

data class IPTVItem(
    val title: String,
    val url: String,
    val duration: Int = -1,
    val tvgId: String? = null,
    val tvgName: String? = null,
    val tvgLogo: String? = null,
    val groupTitle: String? = null
)