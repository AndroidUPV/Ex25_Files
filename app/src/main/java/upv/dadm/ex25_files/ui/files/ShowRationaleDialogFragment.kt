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

package upv.dadm.ex25_files.ui.files

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import upv.dadm.ex25_files.R

/**
 * Displays the user a dialog with a description of why permissions must be granted
 * for a given for the desired functionality.
 */
class ShowRationaleDialogFragment : DialogFragment() {

    // Reference to the ViewModel shared between fragments
    private val viewModel: PermissionViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title: String
        val message: String
        // Configure the dialog's title and description according to the required permission
        when (viewModel.requiredPermission.value) {
            android.Manifest.permission.READ_MEDIA_IMAGES -> {
                title = getString(R.string.title_read_media_images)
                message = getString(R.string.message_read_media_images)
            }
            android.Manifest.permission.READ_EXTERNAL_STORAGE -> {
                title = getString(R.string.title_read_external_storage)
                message = getString(R.string.message_read_external_storage)
            }
            else -> {
                title = getString(R.string.title_read_media_images)
                message = getString(R.string.message_read_media_images)
            }
        }
        // Create the dialog with a single Button to dismiss it
        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.understood) { _, _ ->
                viewModel.setMessageUnderstood(true)
            }
            .create()
    }
}