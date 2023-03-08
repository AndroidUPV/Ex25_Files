/*
 * Copyright (c) 2022
 * David de Andrés and Juan Carlos Ruiz
 * Development of apps for mobile devices
 * Universitat Politècnica de València
 */

package upv.dadm.ex25_files.data.files

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import upv.dadm.ex25_files.R
import upv.dadm.ex25_files.model.Picture
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * DataSource for reading and updating the content of text files,and creating and displaying
 * PNG image files in different storage options.
 * It implements the FilesDataSource interface.
 */
class FilesDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FilesDataSource {

    // Constants for the names of the different files to manage
    private object FileNames {
        const val ASSET_FILE_NAME = "assets_file"
        const val PRIVATE_INTERNAL_FILE_NAME = "private_internal_file"
        const val PRIVATE_INTERNAL_CACHE_FILE_NAME = "private_internal_cache_file"
        const val PRIVATE_EXTERNAL_FILE_NAME = "private_external_file"
        const val PRIVATE_EXTERNAL_CACHE_FILE_NAME = "private_external_cache_file"
    }

    /**
     * Returns the content of a resource file.
     */
    override suspend fun getResourceFileContent(): Result<String> =
        // Operations must be moved to an IO optimised thread
        withContext(Dispatchers.IO) {
            try {
                // Open a stream to read a resource file
                context.resources.openRawResource(R.raw.resource_file).bufferedReader()
                    .use { reader ->
                        // Completely read the reader as a String
                        Result.success(reader.readText())
                    }
            } catch (exception: Resources.NotFoundException) {
                Result.failure(exception)
            }
        }

    /**
     * Returns the content of an asset file.
     */
    override suspend fun getAssetFileContent(): Result<String> =
        // Operations must be moved to an IO optimised thread
        withContext(Dispatchers.IO) {
            try {
                // Open a stream to read an asset file
                context.assets.open(FileNames.ASSET_FILE_NAME).bufferedReader().use { reader ->
                    // Completely read the reader as a String
                    Result.success(reader.readText())
                }
            } catch (exception: IOException) {
                Result.failure(exception)
            }
        }

    /**
     * Returns the content of a text file from private internal storage.
     */
    override suspend fun getPrivateInternalFileContent(): Result<String> =
        readFile(File(context.filesDir, FileNames.PRIVATE_INTERNAL_FILE_NAME))

    /**
     * Returns the content of a text file cached in private internal storage.
     */
    override suspend fun getPrivateInternalCacheFileContent(): Result<String> =
        readFile(File(context.cacheDir, FileNames.PRIVATE_INTERNAL_CACHE_FILE_NAME))

    /**
     * Updates the content of a text file from private internal storage.
     */
    override suspend fun setPrivateInternalFileContent(content: String): Result<Boolean> =
        writeFile(File(context.filesDir, FileNames.PRIVATE_INTERNAL_FILE_NAME), content)

    /**
     * Updates the content of a text file cached in private internal storage.
     */
    override suspend fun setPrivateInternalCacheFileContent(content: String): Result<Boolean> =
        writeFile(File(context.cacheDir, FileNames.PRIVATE_INTERNAL_CACHE_FILE_NAME), content)

    /**
     * Gets the content of a text file.
     */
    private suspend fun readFile(file: File): Result<String> =
        // Operations must be moved to an IO optimised thread
        withContext(Dispatchers.IO) {
            try {
                // Open a stream to read a file
                file.bufferedReader().use { reader ->
                    // Completely read the reader as a String
                    Result.success(reader.readText())
                }
            } catch (exception: FileNotFoundException) {
                Result.failure(exception)
            }
        }

    /**
     * Returns the content of a text file from private external storage.
     */
    override suspend fun getPrivateExternalFileContent(): Result<String> =
        readFile(File(context.getExternalFilesDir(null), FileNames.PRIVATE_EXTERNAL_FILE_NAME))

    /**
     * Returns the content of a text file cached in private external storage.
     */
    override suspend fun getPrivateExternalCacheFileContent(): Result<String> =
        readFile(File(context.externalCacheDir, FileNames.PRIVATE_EXTERNAL_CACHE_FILE_NAME))

    /**
     * Updates the content of a given file.
     */
    private suspend fun writeFile(
        file: File,
        content: String
    ): Result<Boolean> =
        // Operations must be moved to an IO optimised thread
        withContext(Dispatchers.IO) {
            try {
                // Open a writer to write a file
                file.bufferedWriter().use { writer ->
                    writer.write(content)
                }
                Result.success(true)
            } catch (exception: IOException) {
                Result.failure(exception)
            }
        }

    /**
     * Updates the content of a text file from private external storage.
     */
    override suspend fun setPrivateExternalFileContent(content: String): Result<Boolean> =
        writeFile(
            File(context.getExternalFilesDir(null), FileNames.PRIVATE_EXTERNAL_FILE_NAME),
            content
        )

    /**
     * Updates the content of a text file cached in private external storage.
     */
    override suspend fun setPrivateExternalCacheFileContent(content: String): Result<Boolean> =
        writeFile(
            File(context.externalCacheDir, FileNames.PRIVATE_EXTERNAL_CACHE_FILE_NAME),
            content
        )

    /**
     * Returns a list of PNG images from private external storage.
     */
    override suspend fun getPrivateExternalPictureFiles(): Result<List<Picture>?> =
        // Operations must be moved to an IO optimised thread
        withContext(Dispatchers.IO) {
            try {
                Result.success(
                    // Get all files from DIRECTORY_PICTURES
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.listFiles { file ->
                        // Filter those ending with ".png"
                        file.name.endsWith(".png")
                    }?.map { file ->
                        // Extract the URi and name of those files
                        val uri = Uri.fromFile(file)
                        Picture(uri, uri.lastPathSegment!!)
                    }
                )
            } catch (exception: Exception) {
                Result.failure(IOException())
            }
        }

    /**
     * Creates a new PNG image file in private external storage.
     */
    override suspend fun setPrivateExternalPictureFileContent(): Result<Boolean> =
        // Operations must be moved to an IO optimised thread
        withContext(Dispatchers.IO) {
            try {
                // Get a Bitmap from a resource file. This is the image to store.
                val bitmap =
                    BitmapFactory.decodeStream(context.resources.openRawResource(R.raw.android_hi))
                // Open a stream to write the file
                File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "andy_${
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm"))
                    }.png"
                ).outputStream().use { fileOutputsStream ->
                    // Write the image into the stream
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputsStream)
                }
                Result.success(true)
            } catch (exception: Exception) {
                Result.failure(IOException())
            }
        }

    /**
     * Returns a list of PNG images from public shared storage.
     */
    override suspend fun getPublicExternalPictureFiles(): Result<List<Picture>?> =
        // Operations must be moved to an IO optimised thread
        withContext(Dispatchers.IO) {
            // Information (columns) to get
            val projection =
                arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME)
            // Filter to apply
            val selection = MediaStore.Images.Media.MIME_TYPE + "= ?"
            // Value to use in the filter
            val arguments = arrayOf("image/png")
            // Results ordered by
            val order = MediaStore.Images.Media.DISPLAY_NAME + " ASC"

            val list = mutableListOf<Picture>()
            try {
                // Apply the query to a ContentResolver
                val cursor = context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    arguments,
                    order
                )
                if (cursor != null) {
                    // Go through all the information provided by the query
                    while (cursor.moveToNext()) {
                        list.add(
                            Picture(
                                // Get the URI of the PNG image file
                                ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    cursor.getLong(0)
                                ),
                                // Get the name of the PNG image file
                                cursor.getString(1)
                            )
                        )
                    }
                    // Close the cursor
                    cursor.close()
                }
                Result.success(list.toList())

            } catch (exception: IOException) {
                Result.failure(exception)
            }
        }

    /**
     * Creates a new PNG image file in public shared storage.
     */
    override suspend fun setPublicExternalPictureFileContent(): Result<Boolean> =
        // Operations must be moved to an IO optimised thread
        withContext(Dispatchers.IO) {
            // Stores the values for the new PNG image file to create
            val contentValues = ContentValues()
            // Set the file name
            contentValues.put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "andy_${
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm"))
                }.png"
            )
            // Set the file type
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            // Set the pending flag to state that the image file is not created yet (for Android API > 28)
            if (Build.VERSION.SDK_INT > 28) contentValues.put(MediaStore.Images.Media.IS_PENDING, 1)
            // Insert the new file in the table of the ContentProvider
            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            if (uri == null) {
                Result.failure(IOException())
            } else {
                try {
                    // Get a Bitmap from a resource file. This is the image to store.
                    val bitmap =
                        BitmapFactory.decodeStream(context.resources.openRawResource(R.raw.android_hi))
                    // Add the Bitmap to the ContentResolver using the provided stream
                    context.contentResolver.openOutputStream(uri).use { outputsStream ->
                        // Write the image into the stream
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputsStream)
                    }
                    // Clear the pending flag to state that the image file is already created (for Android API > 28)
                    if (Build.VERSION.SDK_INT > 28) {
                        contentValues.clear()
                        contentValues.put(
                            MediaStore.Images.Media.IS_PENDING,
                            0
                        )
                        // Update the ContentProvider with the cleared flag
                        context.contentResolver.update(uri, contentValues, null, null)
                    }
                    Result.success(true)
                } catch (exception: FileNotFoundException) {
                    Result.failure(exception)
                } catch (exception: Exception) {
                    Result.failure(IOException())
                }
            }

        }

    /**
     * Returns the content of a text file from public shared storage.
     */
    override suspend fun getPublicExternalOtherFile(intent: Intent): Result<String> =
        // Operations must be moved to an IO optimised thread
        withContext(Dispatchers.IO) {
            try {
                // Use the ContentResolver to get a FileDescriptor for the provided URI
                context.contentResolver.openFileDescriptor(intent.data!!, "r")
                    .use { parcelFileDescriptor ->
                        // Open an input stream to read the file content
                        FileInputStream(parcelFileDescriptor?.fileDescriptor).use { fileInputStream ->
                            fileInputStream.bufferedReader().use { reader ->
                                // Completely read the reader as a String
                                Result.success(reader.readText())
                            }
                        }
                    }
            } catch (exception: FileNotFoundException) {
                Result.failure(exception)
            } catch (exception: IOException) {
                Result.failure(exception)
            }
        }

    /**
     * Updates the content of a text file from public shared storage.
     */
    override suspend fun setPublicExternalOtherFileContent(
        intent: Intent,
        content: String
    ): Result<Boolean> =
        // Operations must be moved to an IO optimised thread
        withContext(Dispatchers.IO) {
            try {
                // Use the ContentResolver to get a FileDescriptor for the provided URI
                context.contentResolver.openFileDescriptor(intent.data!!, "w")
                    .use { parcelFileDescriptor ->
                        // Open an output stream to update the file content
                        FileOutputStream(parcelFileDescriptor?.fileDescriptor).use { fileOutputStream ->
                            // Write the ocntent of the file
                            fileOutputStream.write(content.toByteArray())
                        }
                    }
                Result.success(true)
            } catch (exception: FileNotFoundException) {
                Result.failure(exception)
            } catch (exception: IOException) {
                Result.failure(exception)
            }
        }

}