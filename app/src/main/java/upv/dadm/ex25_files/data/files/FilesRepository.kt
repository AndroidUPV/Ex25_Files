/*
 * Copyright (c) 2022
 * David de Andrés and Juan Carlos Ruiz
 * Development of apps for mobile devices
 * Universitat Politècnica de València
 */

package upv.dadm.ex25_files.data.files

interface FilesRepository {
    suspend fun getResourceFileContent(): Result<String>
    suspend fun getAssetFileContent(): Result<String>
    suspend fun getPrivateInternalFileContent(): Result<String>
    suspend fun setPrivateInternalFileContent(content: String)
    suspend fun getPrivateInternalCacheFileContent(): Result<String>
    suspend fun setPrivateInternalCacheFileContent(content: String)
}