package com.example.personalizedmusicapp.data

import kotlinx.coroutines.flow.Flow

data class PlayListItemsResponse(
    val etag: String,
    val items: List<Item>,
    val kind: String,
//    val pageInfo: PdfDocument.PageInfo
)