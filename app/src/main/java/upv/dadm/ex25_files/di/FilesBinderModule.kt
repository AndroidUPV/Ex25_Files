/*
 * Copyright (c) 2022
 * David de Andrés and Juan Carlos Ruiz
 * Development of apps for mobile devices
 * Universitat Politècnica de València
 */

package upv.dadm.ex25_files.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import upv.dadm.ex25_files.data.files.FilesDataSource
import upv.dadm.ex25_files.data.files.FilesDataSourceImpl
import upv.dadm.ex25_files.data.files.FilesRepository
import upv.dadm.ex25_files.data.files.FilesRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FilesBinderModule {

    @Binds
    @Singleton
    abstract fun bindFilesDataSource(
        filesDataSourceImpl: FilesDataSourceImpl
    ): FilesDataSource

    @Binds
    @Singleton
    abstract fun bindFilesRepository(
        filesRepositoryImpl: FilesRepositoryImpl
    ): FilesRepository

}