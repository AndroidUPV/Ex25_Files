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

package upv.dadm.ex25_files.ui.files

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import upv.dadm.ex25_files.data.files.FilesRepository
import upv.dadm.ex25_files.model.Picture
import javax.inject.Inject

/**
 * A FilesUiState object containing
 * whether to display the text file contents (true) or
 * the PNG images in the Images storage space (false),
 * whether the file is editable,
 * and the visibility of the save button
 */
data class FilesUiState(
    val isFileContentVisible: Boolean,
    val isFileContentEditable: Boolean,
    val isSaveButtonVisible: Boolean
)

/**
 * Holds information about the file to be displayed.
 */
// The Hilt annotation @HiltEntryPoint is required to receive dependencies from its parent class
@HiltViewModel
class FilesViewModel @Inject constructor(
    private val filesRepository: FilesRepository
) : ViewModel() {

    // Backing property for the contents of the text file
    private val _fileContent = MutableStateFlow("")

    // Contents of the text file
    val fileContent = _fileContent.asStateFlow()

    // UI state (mutable): display text/images, text is editable, and save button is visible
    private val _uiState = MutableStateFlow(
        FilesUiState(
            isFileContentVisible = false,
            isFileContentEditable = false,
            isSaveButtonVisible = false
        )
    )

    // UI state (immutable)
    val uiState = _uiState.asStateFlow()

    // Backing property for the kind of error received (null - no error)
    private val _error = MutableStateFlow<Throwable?>(null)

    // Kind of error received (null - no error)
    val error = _error.asStateFlow()

    // Backing property for the list of Picture objects to display
    private val _pictures = MutableStateFlow<List<Picture>?>(null)

    // List of Picture objects to display
    val picturesUri = _pictures.asStateFlow()

    // Backing property for the text to save in a text file in public shared storage
    private val _textPublicOtherFile = MutableStateFlow("")

    /**
     * Get the content of a text resource file (/res/raw).
     */
    fun loadResourceFile() =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            // Get the content of the file
            filesRepository.getResourceFileContent()
                // Check the result
                .fold(
                    onSuccess = { result ->
                        _fileContent.update { result }
                        // Text is not editable
                        enableVisibilityOnly()
                    },
                    onFailure = { exception ->
                        _fileContent.update { "" }
                        _error.update { exception }
                        disableVisibilityEditionAndSaving()
                    }
                )

        }

    /**
     * Get the content of a text asset file (/assets).
     */
    fun loadAssetFile() =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            // Get the file content
            filesRepository.getAssetFileContent()
                // Check the results
                .fold(
                    onSuccess = { result ->
                        _fileContent.update { result }
                        // Text is not editable
                        enableVisibilityOnly()
                    },
                    onFailure = { exception ->
                        _fileContent.update { "" }
                        _error.update { exception }
                        disableVisibilityEditionAndSaving()
                    }
                )
        }

    /**
     * Get the content of a text file from private internal storage
     * (/data/data/upv.dadm.ex25_files/files).
     */
    fun loadPrivateInternalFile() =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            // Get the file content
            filesRepository.getPrivateInternalFileContent()
                // Check the result
                .fold(
                    onSuccess = { result -> _fileContent.update { result } },
                    onFailure = { exception ->
                        _fileContent.update { "" }
                        _error.update { exception }
                    }
                )
            // The file content can be edited
            enableVisibilityEditionAndSaving()
        }

    /**
     * Update the content of a text file from private internal storage
     * (/data/data/upv.dadm.ex25_files/files).
     */
    fun savePrivateInternalFile(fileContent: String) =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            _fileContent.update { fileContent }
            // Save the file content
            filesRepository.setPrivateInternalFileContent(fileContent)
                // Check result
                .fold(
                    onSuccess = {}, // Nothing to do
                    onFailure = { exception -> _error.update { exception } }
                )
        }

    /**
     * Get the content of a text file cached in private internal storage
     * (/data/data/upv.dadm.ex25_files/cache).
     */
    fun loadPrivateInternalCacheFile() =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            // Get the file content
            filesRepository.getPrivateInternalCacheFileContent()
                // Check the result
                .fold(
                    onSuccess = { result -> _fileContent.update { result } },
                    onFailure = { exception ->
                        _fileContent.update { "" }
                        _error.update { exception }
                    }
                )
            // The file content can be edited
            enableVisibilityEditionAndSaving()
        }

    /**
     * Update the content of a text file cached in private internal storage
     * (/data/data/upv.dadm.ex25_files/cache).
     */
    fun savePrivateInternalCacheFile(fileContent: String) =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            _fileContent.update { fileContent }
            // Save the file content
            filesRepository.setPrivateInternalCacheFileContent(fileContent)
                // Check the result
                .fold(
                    onSuccess = {}, // Nothing to do
                    onFailure = { exception -> _error.update { exception } }
                )
        }

    /**
     * Get the content of a text file from private external storage
     * (/sdcard/data/upv.dadm.ex25_files/files).
     */
    fun loadPrivateExternalFile() =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            // Get the file content
            filesRepository.getPrivateExternalFileContent()
                // Check the result
                .fold(
                    onSuccess = { result -> _fileContent.update { result } },
                    onFailure = { exception ->
                        _fileContent.update { "" }
                        _error.update { exception }
                    }
                )
            // The file content can be edited
            enableVisibilityEditionAndSaving()
        }

    /**
     * Update the content of a text file from private external storage
     * (/sdcard/data/upv.dadm.ex25_files/files).
     */
    fun savePrivateExternalFile(fileContent: String) =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            _fileContent.update { fileContent }
            // Save the file content
            filesRepository.setPrivateExternalFileContent(fileContent)
                // Check the result
                .fold(
                    onSuccess = {}, // Nothing to do
                    onFailure = { exception -> _error.update { exception } }
                )
        }

    /**
     * Get the content of a text file cached in private external storage
     * (/sdcard/data/upv.dadm.ex25_files/cache).
     */
    fun loadPrivateExternalCacheFile() =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            // Load the file content
            filesRepository.getPrivateExternalCacheFileContent()
                // Check the result
                .fold(
                    onSuccess = { result -> _fileContent.update { result } },
                    onFailure = { exception ->
                        _fileContent.update { "" }
                        _error.update { exception }
                    }
                )
            // The file content can be edited
            enableVisibilityEditionAndSaving()
        }

    /**
     * Update the content of a text file cached in private external storage
     * (/sdcard/data/upv.dadm.ex25_files/cache).
     */
    fun savePrivateExternalCacheFile(fileContent: String) =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            _fileContent.update { fileContent }
            // Save the file content
            filesRepository.setPrivateExternalCacheFileContent(fileContent)
                // Check the result
                .fold(
                    onSuccess = {}, // Nothing to do
                    onFailure = { exception -> _error.update { exception } }
                )
        }

    /**
     * Get a list of PNG images (uri and name) in private external storage
     * (/sdcard/data/upv.dadm.ex25_files/files/Pictures).
     */
    fun loadPrivateExternalPictureFiles() =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            // Load the list of Pictures
            filesRepository.getPrivateExternalPictureFiles()
                // Check the result
                .fold(
                    onSuccess = { result -> _pictures.update { result } },
                    onFailure = { exception -> _error.update { exception } }
                )
            // Enable the creation of a new PNG image file
            enableSavingOnly()
        }

    /**
     * Create a new PNG image file in private external storage
     * (/sdcard/data/upv.dadm.ex25_files/files/Pictures).
     */
    fun savePrivateExternalPictureFile() =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            // Save the PNG image file
            filesRepository.setPrivateExternalPictureFileContent()
                // Check the result
                .fold(
                    onSuccess = {
                        // Reload the list of images to display the new one
                        loadPrivateExternalPictureFiles()
                    },
                    onFailure = { exception -> _error.update { exception } }
                )
        }

    /**
     * Get a list of PNG images (uri and name) in public shared storage
     * (/sdcard/Pictures).
     */
    fun loadPublicPictureFiles() =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            // Load the list of Pictures
            filesRepository.getPublicExternalPictureFiles()
                // Check the result
                .fold(
                    onSuccess = { result -> _pictures.update { result } },
                    onFailure = { exception -> _error.update { exception } }
                )
            // Enable the creation of a new PNG image file
            enableSavingOnly()
        }

    /**
     * Create a new PNG image file in public shared storage
     * (/sdcard/Pictures).
     */
    fun savePublicPictureFiles() =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            // Save the PNG image file
            filesRepository.setPublicExternalPictureFileContent()
                // Check the result
                .fold(
                    onSuccess = { loadPublicPictureFiles() },
                    onFailure = { exception -> _error.value = exception }
                )
        }

    /**
     * Get the content of a text file from public shared storage
     * (/sdcard/Download).
     */
    fun loadPublicOtherFile(intent: Intent?) =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            if (intent != null) {
                // Load the file content
                filesRepository.getPublicExternalOtherFile(intent)
                    // Check the result
                    .fold(
                        onSuccess = { result -> _fileContent.update { result } },
                        onFailure = { exception ->
                            _fileContent.update { "" }
                            _error.update { exception }
                        }
                    )
            } else {
                // The load operation is cancelled
                _fileContent.update { "" }
            }
            // The file content can be edited
            enableVisibilityEditionAndSaving()
        }

    /**
     * Update the content of a text file from public shared storage
     * (/sdcard/Download).
     */
    fun savePublicOtherFile(intent: Intent) =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            _fileContent.update { _textPublicOtherFile.value }
            // Save the file content
            filesRepository.setPublicExternalOtherFileContent(intent, _textPublicOtherFile.value)
                // Check the result
                .fold(
                    onSuccess = { }, // Nothing to do
                    onFailure = { exception -> _error.update { exception } }
                )
        }

    /**
     * Sets the text to write to a text file in public shared storage.
     */
    fun setPublicOtherFileText(text: String) {
        _textPublicOtherFile.update { text }
    }

    /**
     * Makes visible and enables the edit text field and the save button.
     */
    private fun enableVisibilityEditionAndSaving() =
        _uiState.update {
            FilesUiState(
                isFileContentVisible = true,
                isFileContentEditable = true,
                isSaveButtonVisible = true
            )
        }


    /**
     * Makes visible the edit text file, but it cannot be edited (thus, the save button is hidden).
     */
    private fun enableVisibilityOnly() =
        _uiState.update {
            FilesUiState(
                isFileContentVisible = true,
                isFileContentEditable = false,
                isSaveButtonVisible = false
            )
        }

    /**
     * Hides the edit text and the save button.
     */
    private fun disableVisibilityEditionAndSaving() =
        _uiState.update {
            FilesUiState(
                isFileContentVisible = false,
                isFileContentEditable = false,
                isSaveButtonVisible = false
            )
        }

    /**
     * Displays the RecyclerView and the save button.
     */
    private fun enableSavingOnly() =
        _uiState.update {
            FilesUiState(
                isFileContentVisible = false,
                isFileContentEditable = false,
                isSaveButtonVisible = true
            )
        }

    /**
     * Clears the error received.
     */
    fun clearError() {
        _error.value = null
    }
}