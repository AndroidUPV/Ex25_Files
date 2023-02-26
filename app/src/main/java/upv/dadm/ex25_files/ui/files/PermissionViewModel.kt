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

/**
 * Holds information about the required permission and the associated rationale dialog.
 */
class PermissionViewModel : ViewModel() {

    // Backing property for the required permission
    private val _requiredPermission =
        MutableLiveData(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    // Required permission
    val requiredPermission: LiveData<String>
        get() = _requiredPermission

    // Backing property for the flag signaling the user has understood the rationale for the required permission
    private val _isMessageUnderstood = MutableLiveData(false)

    // Flag signaling the user has understood the rationale for the required permission
    val isMessageUnderstood: LiveData<Boolean>
        get() = _isMessageUnderstood

    /**
     * Sets the required permission.
     */
    fun setRequiredPermission(permission: String) {
        _requiredPermission.value = permission
    }

    /**
     * Sets whether the rationale has been understood.
     */
    fun setMessageUnderstood(isUnderstood: Boolean) {
        _isMessageUnderstood.value = isUnderstood
    }
}