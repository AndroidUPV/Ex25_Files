/*
 * Copyright (c) 2022
 * David de Andrés and Juan Carlos Ruiz
 * Development of apps for mobile devices
 * Universitat Politècnica de València
 */

package upv.dadm.ex25_files.ui.files

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import upv.dadm.ex25_files.data.files.FilesRepository
import upv.dadm.ex25_files.model.Picture
import javax.inject.Inject

/**
 * Holds information about the file to be displayed.
 */
// The Hilt annotation @HiltEntryPoint is required to receive dependencies from its parent class
@HiltViewModel
class FilesViewModel @Inject constructor(
    private val filesRepository: FilesRepository
) : ViewModel() {

    // Backing property for the contents of the text file
    private val _fileContent = MutableLiveData<String>()

    // Contents of the text file
    val fileContent: LiveData<String>
        get() = _fileContent

    // Backing property for whether the file is editable
    private val _isFileContentEditable = MutableLiveData(false)

    // Flag stating whether the file is editable
    val isFileContentEditable: LiveData<Boolean>
        get() = _isFileContentEditable

    // Backing property for whether to display the text file contents (true) or
    // the PNG images in the Images storage space (false)
    private val _isFileContentVisible = MutableLiveData(false)

    // Flag stating whether to display the text file contents (true) or
    // the PNG images in the Images storage space (false)
    val isFileContentVisible: LiveData<Boolean>
        get() = _isFileContentVisible

    // Backing property for the visibility of the save button
    private val _isSaveButtonVisible = MutableLiveData(false)

    // Visibility of the save button
    val isSaveButtonVisible: LiveData<Boolean>
        get() = _isSaveButtonVisible

    // Backing property for the kind of error received (null - no error)
    private val _error = MutableLiveData<Throwable?>()

    // Kind of error received (null - no error)
    val error: LiveData<Throwable?>
        get() = _error

    // Backing property for the list of Picture objects to display
    private val _pictures = MutableLiveData<List<Picture>?>()

    // List of Picture objects to display
    val picturesUri: LiveData<List<Picture>?>
        get() = _pictures

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
                        _fileContent.value = result
                        // Text is not editable
                        enableVisibilityOnly()
                    },
                    onFailure = { exception ->
                        _fileContent.value = ""
                        _error.value = exception
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
                        _fileContent.value = result
                        // Text is not editable
                        enableVisibilityOnly()
                    },
                    onFailure = { exception ->
                        _fileContent.value = ""
                        _error.value = exception
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
                // CHeck the result
                .fold(
                    onSuccess = { result ->
                        _fileContent.value = result
                        // The file content can be edited
                        enableVisibilityEditionAndSaving()
                    },
                    onFailure = { exception ->
                        _error.value = exception
                        _fileContent.value = ""
                        disableVisibilityEditionAndSaving()
                    }
                )
        }

    /**
     * Update the content of a text file from private internal storage
     * (/data/data/upv.dadm.ex25_files/files).
     */
    fun savePrivateInternalFile(fileContent: String) =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            // Save the file content
            filesRepository.setPrivateInternalFileContent(fileContent)
                // Check result
                .fold(
                    onSuccess = {}, // Nothing to do
                    onFailure = { exception -> _error.value = exception }
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
                    onSuccess = { result ->
                        _fileContent.value = result
                        // The file content can be edited
                        enableVisibilityEditionAndSaving()
                    },
                    onFailure = { exception ->
                        _fileContent.value = ""
                        _error.value = exception
                        disableVisibilityEditionAndSaving()
                    }
                )
        }

    /**
     * Update the content of a text file cached in private internal storage
     * (/data/data/upv.dadm.ex25_files/cache).
     */
    fun savePrivateInternalCacheFile(fileContent: String) =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            // Save the file content
            filesRepository.setPrivateInternalCacheFileContent(fileContent)
                // Check the result
                .fold(
                    onSuccess = {}, // Nothing to do
                    onFailure = { exception -> _error.value = exception }
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
                    onSuccess = { result ->
                        _fileContent.value = result
                    },
                    onFailure = { exception ->
                        _error.value = exception
                        _fileContent.value = ""
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
            // Save the file content
            filesRepository.setPrivateExternalFileContent(fileContent)
                // Check the result
                .fold(
                    onSuccess = {}, // Nothing to do
                    onFailure = { exception -> _error.value = exception }
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
                    onSuccess = { result ->
                        _fileContent.value = result
                    },
                    onFailure = { exception ->
                        _fileContent.value = ""
                        _error.value = exception
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
            // Save the file content
            filesRepository.setPrivateExternalCacheFileContent(fileContent)
                // Check the result
                .fold(
                    onSuccess = {}, // Nothing to do
                    onFailure = { exception -> _error.value = exception }
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
                    onSuccess = { result ->
                        _pictures.value = result
                        // Enable the creation of a new PNG image file
                        enableSavingOnly()
                    },
                    onFailure = { exception -> _error.value = exception }
                )
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
                    onFailure = { exception -> _error.value = exception }
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
                    onSuccess = { result ->
                        _pictures.value = result
                        // Enable the creation of a new PNG image file
                        enableSavingOnly()
                    },
                    onFailure = { exception -> _error.value = exception }
                )
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
                    onSuccess = { }, // Nothing to do
                    onFailure = { exception -> _error.value = exception }
                )
        }

    /**
     * Get the content of a text file from public shared storage
     * (/sdcard/Download).
     */
    fun loadPublicOtherFile(intent: Intent) =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            // Load the file content
            filesRepository.getPublicExternalOtherFile(intent)
                // Check the result
                .fold(
                    onSuccess = { result ->
                        _fileContent.value = result
                    },
                    onFailure = { exception ->
                        _error.value = exception
                        _fileContent.value = ""
                    }
                )
            // The file content can be edited
            enableVisibilityEditionAndSaving()
        }

    /**
     * Update the content of a text file from public shared storage
     * (/sdcard/Download).
     */
    fun savePublicOtherFile(intent: Intent, content: String) =
        // As it is a blocking operation it should be executed in a thread
        viewModelScope.launch {
            // Save the file content
            filesRepository.setPublicExternalOtherFileContent(intent, content)
                // Check the result
                .fold(
                    onSuccess = { }, // Nothing to do
                    onFailure = { exception -> _error.value = exception }
                )
        }

    /**
     * Makes visible and enables the edit text field and the save button.
     */
    private fun enableVisibilityEditionAndSaving() {
        _isFileContentVisible.value = true
        _isFileContentEditable.value = true
        _isSaveButtonVisible.value = true
    }

    /**
     * Makes visible the edit text file, but it cannot be edited (thus, the save button is hidden).
     */
    private fun enableVisibilityOnly() {
        _isFileContentVisible.value = true
        _isFileContentEditable.value = false
        _isSaveButtonVisible.value = false
    }

    /**
     * Hide the edit text and the save button.
     */
    private fun disableVisibilityEditionAndSaving() {
        _isFileContentVisible.value = false
        _isFileContentEditable.value = false
        _isSaveButtonVisible.value = false
    }

    /**
     * Display the RecyclerView and the save button.
     */
    private fun enableSavingOnly() {
        _isFileContentVisible.value = false
        _isFileContentEditable.value = false
        _isSaveButtonVisible.value = true
    }

    fun clearError() {
        _error.value = null
    }
}