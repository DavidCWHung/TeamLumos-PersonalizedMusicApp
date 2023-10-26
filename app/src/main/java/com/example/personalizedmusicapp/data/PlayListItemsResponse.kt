package com.example.personalizedmusicapp.data

data class PlayListItemsResponse(
    val etag: String,
    val items: List<Item>,
    val kind: String,
//    val pageInfo: PdfDocument.PageInfo
)