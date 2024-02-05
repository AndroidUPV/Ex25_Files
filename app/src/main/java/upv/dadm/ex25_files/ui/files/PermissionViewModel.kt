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

import android.Manifest
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Holds information about the required permission and the associated rationale dialog.
 */
class PermissionViewModel : ViewModel() {

    // Backing property for the required permission
    private val _requiredPermission =
        MutableStateFlow(Manifest.permission.READ_EXTERNAL_STORAGE)

    // Required permission
    val requiredPermission = _requiredPermission.asStateFlow()

    // Backing property for the flag signaling the user has understood the rationale for the required permission
    private val _isMessageUnderstood = MutableStateFlow(false)

    // Flag signaling the user has understood the rationale for the required permission
    val isMessageUnderstood = _isMessageUnderstood.asStateFlow()

    /**
     * Sets the required permission.
     */
    fun setRequiredPermission(permission: String) {
        _requiredPermission.update { permission }
    }

    /**
     * Sets whether the rationale has been understood.
     */
    fun setMessageUnderstood(isUnderstood: Boolean) {
        _isMessageUnderstood.update { isUnderstood }
    }
}