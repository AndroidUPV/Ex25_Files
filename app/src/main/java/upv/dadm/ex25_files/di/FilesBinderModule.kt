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

/**
 * Hilt module that determines how to provide required dependencies for interfaces.
 */
@Module
// The Hilt annotation @SingletonComponent creates and destroy instances following the lifecycle of the Application
@InstallIn(SingletonComponent::class)
abstract class FilesBinderModule {

    /**
     * Provides an instance of a FilesDataSource.
     */
    @Binds
    @Singleton
    abstract fun bindFilesDataSource(
        filesDataSourceImpl: FilesDataSourceImpl
    ): FilesDataSource

    /**
     * Provides an instance of FilesRepository.
     */
    @Binds
    @Singleton
    abstract fun bindFilesRepository(
        filesRepositoryImpl: FilesRepositoryImpl
    ): FilesRepository
}