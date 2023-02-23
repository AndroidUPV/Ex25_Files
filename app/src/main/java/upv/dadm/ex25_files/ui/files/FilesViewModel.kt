/*
 * Copyright (c) 2022
 * David de Andrés and Juan Carlos Ruiz
 * Development of apps for mobile devices
 * Universitat Politècnica de València
 */

package upv.dadm.ex25_files.ui.files

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import upv.dadm.ex25_files.data.files.FilesRepository
import javax.inject.Inject

@HiltViewModel
class FilesViewModel @Inject constructor(
    private val filesRepository: FilesRepository
) : ViewModel() {

    private val _fileContent = MutableLiveData<String>()
    val fileContent: LiveData<String>
        get() = _fileContent

    private val _isFileContentEditable = MutableLiveData(false)
    val isFileContentEditable: LiveData<Boolean>
        get() = _isFileContentEditable

    private val _error = MutableLiveData<Throwable?>()
    val error: LiveData<Throwable?>
        get() = _error

    fun loadResourceFile() {
        viewModelScope.launch {
            filesRepository.getResourceFileContent().fold(
                onSuccess = { result -> _fileContent.value = result },
                onFailure = { exception ->
                    _fileContent.value = ""
                    _error.value = exception
                }
            )
            _isFileContentEditable.value = false
        }
    }

    fun loadAssetFile() {
        viewModelScope.launch {
            filesRepository.getAssetFileContent().fold(
                onSuccess = { result -> _fileContent.value = result },
                onFailure = { exception ->
                    _fileContent.value = ""
                    _error.value = exception
                }
            )
            _isFileContentEditable.value = false
        }
    }

    fun loadPrivateInternalFile() {
        viewModelScope.launch {
            filesRepository.getPrivateInternalFileContent().fold(
                onSuccess = { result -> _fileContent.value = result },
                onFailure = { exception ->
                    _error.value = exception
                    _fileContent.value = ""
                }
            )
            _isFileContentEditable.value = true
        }
    }

    fun savePrivateInternalFile(fileContent: String) {
        viewModelScope.launch {
            filesRepository.setPrivateInternalFileContent(fileContent)
        }
    }

    fun loadPrivateInternalCacheFile() {
        viewModelScope.launch {
            filesRepository.getPrivateInternalCacheFileContent().fold(
                onSuccess = { result -> _fileContent.value = result },
                onFailure = { exception ->
                    _fileContent.value = ""
                    _error.value = exception
                }
            )
            _isFileContentEditable.value = true
        }
    }

    fun savePrivateInternalCacheFile(fileContent: String) {
        viewModelScope.launch {
            filesRepository.setPrivateInternalCacheFileContent(fileContent)
        }
    }
}