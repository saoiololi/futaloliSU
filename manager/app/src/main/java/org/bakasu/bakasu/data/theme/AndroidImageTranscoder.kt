package org.bakasu.bakasu.data.theme

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object AndroidImageTranscoder : ImageTranscoder {
    @Suppress("DEPRECATION")
    override fun transcode(bytes: ByteArray, maxDimension: Int): ByteArray? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        val width = bounds.outWidth
        val height = bounds.outHeight
        if (width <= 0 || height <= 0) return null

        var sample = 1
        while (maxOf(width, height) / (sample * 2) >= maxDimension) {
            sample *= 2
        }

        val decoded = BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size,
            BitmapFactory.Options().apply { inSampleSize = sample },
        ) ?: return null

        val longEdge = maxOf(decoded.width, decoded.height)
        val scaled = if (longEdge > maxDimension) {
            val ratio = maxDimension.toFloat() / longEdge
            Bitmap.createScaledBitmap(
                decoded,
                (decoded.width * ratio).toInt().coerceAtLeast(1),
                (decoded.height * ratio).toInt().coerceAtLeast(1),
                true,
            ).also { if (it !== decoded) decoded.recycle() }
        } else {
            decoded
        }

        val out = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.WEBP, TRANSCODE_QUALITY, out)
        scaled.recycle()
        return out.toByteArray()
    }

    private const val TRANSCODE_QUALITY = 90
}
