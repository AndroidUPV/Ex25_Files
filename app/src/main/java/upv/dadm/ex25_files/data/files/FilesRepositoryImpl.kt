/*
 * Copyright (c) 2022-2023 Universitat Politècnica de València
 * Authors: David de Andrés and Juan Carlos Ruiz
 *          Fault-Tolerant Systems
 *          Instituto ITACA
 *          Universitat Politècnica de València
 *
 * Distributed under MIT license
 * (See accompanying file LICENSE.txt)
 */

package upv.dadm.ex25_files.data.files

import android.content.Intent
import android.os.Environment
import upv.dadm.ex25_files.model.Picture
import upv.dadm.ex25_files.utils.ExternalStorageNotReadable
import upv.dadm.ex25_files.utils.ExternalStorageNotWritable
import javax.inject.Inject

/**
 * Repository for reading and updating the content of text files,and creating and displaying
 * PNG image files in different storage options.
 * It implements the FilesRepository interface.
 */
// @Inject enables Hilt to provide the required dependencies
class FilesRepositoryImpl @Inject constructor(
    private val filesDataSource: FilesDataSource
) : FilesRepository {

    /**
     * Returns the content of a resource file.
     */
    override suspend fun getResourceFileContent(): Result<String> =
        filesDataSource.getResourceFileContent()

    /**
     * Returns the content of an asset file.
     */
    override suspend fun getAssetFileContent(): Result<String> =
        filesDataSource.getAssetFileContent()

    /**
     * Returns the content of a text file from private internal storage.
     */
    override suspend fun getPrivateInternalFileContent(): Result<String> =
        filesDataSource.getPrivateInternalFileContent()

    /**
     * Updates the content of a text file from private internal storage.
     */
    override suspend fun setPrivateInternalFileContent(content: String): Result<Boolean> =
        filesDataSource.setPrivateInternalFileContent(content)

    /**
     * Returns the content of a text file cached in private internal storage.
     */
    override suspend fun getPrivateInternalCacheFileContent(): Result<String> =
        filesDataSource.getPrivateInternalCacheFileContent()

    /**
     * Updates the content of a text file cached in private internal storage.
     */
    override suspend fun setPrivateInternalCacheFileContent(content: String): Result<Boolean> =
        filesDataSource.setPrivateInternalCacheFileContent(content)

    /**
     * Returns the content of a text file from private external storage.
     */
    override suspend fun getPrivateExternalFileContent(): Result<String> =
        // Execute the operation if external storage is in read mode
        if (isExternalStorageReadable())
            filesDataSource.getPrivateExternalFileContent()
        // Display and error message otherwise
        else
            Result.failure(ExternalStorageNotReadable())

    /**
     * Updates the content of a text file from private external storage.
     */
    override suspend fun setPrivateExternalFileContent(content: String): Result<Boolean> =
        // Execute the operation if external storage is in write mode
        if (isExternalStorageWritable())
            filesDataSource.setPrivateExternalFileContent(content)
        // Display and error message otherwise
        else
            Result.failure(ExternalStorageNotWritable())

    /**
     * Returns the content of a text file cached in private external storage.
     */
    override suspend fun getPrivateExternalCacheFileContent(): Result<String> =
        // Execute the operation if external storage is in read mode
        if (isExternalStorageReadable())
            filesDataSource.getPrivateExternalCacheFileContent()
        // Display and error message otherwise
        else
            Result.failure(ExternalStorageNotReadable())

    /**
     * Updates the content of a text file cached in private external storage.
     */
    override suspend fun setPrivateExternalCacheFileContent(content: String): Result<Boolean> =
        // Execute the operation if external storage is in write mode
        if (isExternalStorageWritable())
            filesDataSource.setPrivateExternalCacheFileContent(content)
        // Display and error message otherwise
        else
            Result.failure(ExternalStorageNotWritable())

    /**
     * Returns a list of PNG images from private external storage.
     */
    override suspend fun getPrivateExternalPictureFiles(): Result<List<Picture>?> =
        // Execute the operation if external storage is in read mode
        if (isExternalStorageReadable())
            filesDataSource.getPrivateExternalPictureFiles()
        // Display and error message otherwise
        else
            Result.failure(ExternalStorageNotReadable())

    /**
     * Creates a new PNG image file in private external storage.
     */
    override suspend fun setPrivateExternalPictureFileContent(): Result<Boolean> =
        // Execute the operation if external storage is in write mode
        if (isExternalStorageWritable())
            filesDataSource.setPrivateExternalPictureFileContent()
        // Display and error message otherwise
        else
            Result.failure(ExternalStorageNotWritable())

    /**
     * Returns a list of PNG images from public shared storage.
     */
    override suspend fun getPublicExternalPictureFiles(): Result<List<Picture>?> =
        filesDataSource.getPublicExternalPictureFiles()

    /**
     * Creates a new PNG image file in public shared storage.
     */
    override suspend fun setPublicExternalPictureFileContent(): Result<Boolean> =
        filesDataSource.setPublicExternalPictureFileContent()

    /**
     * Returns the content of a text file from public shared storage.
     */
    override suspend fun getPublicExternalOtherFile(intent: Intent): Result<String> =
        filesDataSource.getPublicExternalOtherFile(intent)

    /**
     * Updates the content of a text file from public shared storage.
     */
    override suspend fun setPublicExternalOtherFileContent(
        intent: Intent,
        content: String
    ): Result<Boolean> =
        filesDataSource.setPublicExternalOtherFileContent(intent, content)

    // Checks whether the external storage is in write mode
    private fun isExternalStorageWritable() =
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    // Checks whether the external storage is in read mode
    private fun isExternalStorageReadable() =
        Environment.getExternalStorageState() in setOf(
            Environment.MEDIA_MOUNTED,
            Environment.MEDIA_MOUNTED_READ_ONLY
        )
}