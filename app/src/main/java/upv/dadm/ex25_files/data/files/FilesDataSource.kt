/*
 * Copyright (c) 2022-2024 Universitat Politècnica de València
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
import upv.dadm.ex25_files.model.Picture

/**
 * Interface declaring the methods that the DataSource exposes to Repositories.
 */
interface FilesDataSource {

    /**
     * Returns the content of a resource file.
     */
    suspend fun getResourceFileContent(): Result<String>

    /**
     * Returns the content of an asset file.
     */
    suspend fun getAssetFileContent(): Result<String>

    /**
     * Returns the content of a text file from private internal storage.
     */
    suspend fun getPrivateInternalFileContent(): Result<String>

    /**
     * Updates the content of a text file from private internal storage.
     */
    suspend fun setPrivateInternalFileContent(content: String): Result<Boolean>

    /**
     * Returns the content of a text file cached in private internal storage.
     */
    suspend fun getPrivateInternalCacheFileContent(): Result<String>

    /**
     * Updates the content of a text file cached in private internal storage.
     */
    suspend fun setPrivateInternalCacheFileContent(content: String): Result<Boolean>

    /**
     * Returns the content of a text file from private external storage.
     */
    suspend fun getPrivateExternalFileContent(): Result<String>

    /**
     * Updates the content of a text file from private external storage.
     */
    suspend fun setPrivateExternalFileContent(content: String): Result<Boolean>

    /**
     * Returns the content of a text file cached in private external storage.
     */
    suspend fun getPrivateExternalCacheFileContent(): Result<String>

    /**
     * Updates the content of a text file cached in private external storage.
     */
    suspend fun setPrivateExternalCacheFileContent(content: String): Result<Boolean>

    /**
     * Returns a list of PNG images from private external storage.
     */
    suspend fun getPrivateExternalPictureFiles(): Result<List<Picture>?>

    /**
     * Creates a new PNG image file in private external storage.
     */
    suspend fun setPrivateExternalPictureFileContent(): Result<Boolean>

    /**
     * Returns a list of PNG images from public shared storage.
     */
    suspend fun getPublicExternalPictureFiles(): Result<List<Picture>?>

    /**
     * Creates a new PNG image file in public shared storage.
     */
    suspend fun setPublicExternalPictureFileContent(): Result<Boolean>

    /**
     * Returns the content of a text file from public shared storage.
     */
    suspend fun getPublicExternalOtherFile(intent: Intent): Result<String>

    /**
     * Updates the content of a text file from public shared storage.
     */
    suspend fun setPublicExternalOtherFileContent(intent: Intent, content: String): Result<Boolean>
}