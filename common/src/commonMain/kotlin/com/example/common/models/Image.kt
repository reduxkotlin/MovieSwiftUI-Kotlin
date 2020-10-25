package com.example.common.models

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ImageData(
    @Transient
    val id: Uuid = uuid4(),
    val aspect_ratio: Float,
    val file_path: String,
    val height: Int,
    val width: Int
)
