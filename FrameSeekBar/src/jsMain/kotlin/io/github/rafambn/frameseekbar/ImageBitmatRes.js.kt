package io.github.rafambn.frameseekbar

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Resource
import org.jetbrains.skia.Image

actual fun ByteArray.toImageBitmap(): ImageBitmap = Image.makeFromEncoded(this).toComposeImageBitmap()

actual fun isSyncResourceLoadingSupported(): Boolean = false

@OptIn(ExperimentalResourceApi::class)
actual fun Resource.readBytesSync(): ByteArray = throw UnsupportedOperationException()