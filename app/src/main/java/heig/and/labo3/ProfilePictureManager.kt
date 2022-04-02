package heig.and.labo3

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

class ProfilePictureManager {
    companion object {
        fun createPicUri(context: Context, directory: File) : Uri {
            val userPictureFile = File.createTempFile("user_picture", ".png", directory).apply {
                createNewFile()
            }
            return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", userPictureFile)
        }

        suspend fun correctRotation(contentResolver: ContentResolver, picUri: Uri) : Bitmap = withContext(Dispatchers.Default) {
            val bitmap = getBitmap(contentResolver, picUri)
            val metadata = ExifInterface(contentResolver.openInputStream(picUri)!!)
            val rotation: Int = metadata.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            val rotationInDegrees = exifToDegrees(rotation)
            val matrix = Matrix()
            if (rotation != 0) {
                matrix.postRotate(rotationInDegrees.toFloat())
            }
            return@withContext Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        private suspend fun getBitmap(cr: ContentResolver, url: Uri?): Bitmap = withContext(Dispatchers.IO) {
            val input: InputStream? = cr.openInputStream(url!!)
            val bitmap = BitmapFactory.decodeStream(input)
            input!!.close()
            return@withContext bitmap
        }

        private fun exifToDegrees(exifOrientation: Int): Int {
            return when (exifOrientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> {
                    90
                }
                ExifInterface.ORIENTATION_ROTATE_180 -> {
                    180
                }
                ExifInterface.ORIENTATION_ROTATE_270 -> {
                    270
                }
                else -> 0
            }
        }
    }
}