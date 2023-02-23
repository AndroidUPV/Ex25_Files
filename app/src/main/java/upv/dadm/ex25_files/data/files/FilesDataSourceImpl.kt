/*
 * Copyright (c) 2022
 * David de Andrés and Juan Carlos Ruiz
 * Development of apps for mobile devices
 * Universitat Politècnica de València
 */

package upv.dadm.ex25_files.data.files

import android.content.Context
import android.content.res.Resources
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import upv.dadm.ex25_files.R
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

class FilesDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FilesDataSource {

    private object FileNames {
        const val ASSET_FILE_NAME = "assets_file"
        const val PRIVATE_INTERNAL_FILE_NAME = "private_internal_file"
        const val PRIVATE_INTERNAL_CACHE_FILE_NAME = "private_internal_cache_file"
        const val PRIVATE_EXTERNAL_FILE_NAME = "private_external_file"
        const val PRIVATE_EXTERNAL_CACHE_FILE_NAME = "private_external_cache_file"
    }

    override suspend fun getResourceFileContent(): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                context.resources.openRawResource(R.raw.resource_file).bufferedReader()
                    .use { reader ->
                        Result.success(reader.readText())
                    }
            } catch (exception: Resources.NotFoundException) {
                Result.failure(exception)
            }
        }

    override suspend fun getAssetFileContent(): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                context.assets.open(FileNames.ASSET_FILE_NAME).bufferedReader().use { reader ->
                    Result.success(reader.readText())
                }
            } catch (exception: IOException) {
                Result.failure(exception)
            }
        }

    private fun readPrivateInternalFile(fileName: String): Result<String> =
        try {
            context.openFileInput(fileName).bufferedReader().use { reader ->
                Result.success(reader.readText())
            }
        } catch (exception: FileNotFoundException) {
            Result.failure(exception)
        }

    override suspend fun getPrivateInternalFileContent(): Result<String> =
        readPrivateInternalFile(FileNames.PRIVATE_INTERNAL_FILE_NAME)

    override suspend fun getPrivateInternalCacheFileContent(): Result<String> =
        readPrivateInternalFile(FileNames.PRIVATE_INTERNAL_CACHE_FILE_NAME)

    private fun writePrivateInternalFile(fileName: String, content: String) =
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { outputStream ->
            outputStream.write(content.toByteArray())
        }

    override suspend fun setPrivateInternalFileContent(content: String) =
        writePrivateInternalFile(FileNames.PRIVATE_INTERNAL_FILE_NAME, content)

    override suspend fun setPrivateInternalCacheFileContent(content: String) =
        writePrivateInternalFile(FileNames.PRIVATE_INTERNAL_CACHE_FILE_NAME, content)

    private fun readPrivateExternalFile(fileName: String): Result<String> =
        try {
            File(context.getExternalFilesDir(null), fileName).bufferedReader().use { reader ->
                Result.success(reader.readText())
            }
        } catch (exception: FileNotFoundException) {
            Result.failure(exception)
        }

    override suspend fun getPrivateExternalFileContent(): Result<String> =
        readPrivateExternalFile(FileNames.PRIVATE_EXTERNAL_FILE_NAME)

    override suspend fun getPrivateExternalCacheFileContent(): Result<String> =
        readPrivateExternalFile(FileNames.PRIVATE_EXTERNAL_CACHE_FILE_NAME)

    private fun writePrivateExternalFile(fileName: String, content: String) =
        File(context.getExternalFilesDir(null), fileName).bufferedWriter().use { writer ->
            writer.write(content)
        }

    override suspend fun setPrivateExternalFileContent(content: String) =
        writePrivateExternalFile(FileNames.PRIVATE_EXTERNAL_FILE_NAME, content)


    override suspend fun setPrivateExternalCacheFileContent(content: String) =
        writePrivateExternalFile(FileNames.PRIVATE_EXTERNAL_CACHE_FILE_NAME, content)

}