/*
 * Copyright (c) 2022
 * David de Andrés and Juan Carlos Ruiz
 * Development of apps for mobile devices
 * Universitat Politècnica de València
 */

package upv.dadm.ex25_files.data.files

import android.os.Environment
import javax.inject.Inject

class FilesRepositoryImpl @Inject constructor(
    private val filesDataSource: FilesDataSource
) : FilesRepository {

    override suspend fun getResourceFileContent(): Result<String> =
        filesDataSource.getResourceFileContent()

    override suspend fun getAssetFileContent(): Result<String> =
        filesDataSource.getAssetFileContent()

    override suspend fun getPrivateInternalFileContent(): Result<String> =
        filesDataSource.getPrivateInternalFileContent()

    override suspend fun setPrivateInternalFileContent(content: String) =
        filesDataSource.setPrivateInternalFileContent(content)

    override suspend fun getPrivateInternalCacheFileContent(): Result<String> =
        filesDataSource.getPrivateInternalCacheFileContent()

    override suspend fun setPrivateInternalCacheFileContent(content: String) =
        filesDataSource.setPrivateInternalCacheFileContent(content)

    private fun isExternalStorageWritable() =
        Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    private fun isExternalStorageReadable() =
        Environment.getExternalStorageState() in setOf(
            Environment.MEDIA_MOUNTED,
            Environment.MEDIA_MOUNTED_READ_ONLY
        )
}