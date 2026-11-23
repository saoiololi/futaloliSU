package org.bakasu.bakasu.data.theme

fun interface ImageTranscoder {
    fun transcode(bytes: ByteArray, maxDimension: Int): ByteArray?
}
