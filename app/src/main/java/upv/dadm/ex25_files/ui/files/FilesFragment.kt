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

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import upv.dadm.ex25_files.R
import upv.dadm.ex25_files.databinding.FragmentFilesBinding
import upv.dadm.ex25_files.utils.ExternalStorageNotReadableException
import upv.dadm.ex25_files.utils.ExternalStorageNotWritableException
import java.io.FileNotFoundException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Displays a dropdown menu for the user to select the storage space to be accessed.
 * The contents of the text file are displayed in an EditText field,
 * whereas a RecyclerView displays a thumbnail and name of PNG images in the Images storage space.
 * A Button enables the user to save the new contents of the file or create a new image file
 * when accessing the Images storage space.
 */
// The Hilt annotation @AndroidEntryPoint is required to receive dependencies from its parent class
@AndroidEntryPoint
class FilesFragment : Fragment(R.layout.fragment_files) {

    // Defines constants identifying the required permission
    object PermissionsTypes {
        const val NO_PERMISSION = "no_permission"
        const val READ_PUBLIC_IMAGES = "read_public_images"
        const val WRITE_PUBLIC_IMAGES = "write_public_images"
    }

    // Reference to the ViewModel holding information about file content
    private val viewModel: FilesViewModel by viewModels()

    // Reference to the ViewModel holding information about the required permission
    private val permissionViewModel: PermissionViewModel by activityViewModels()

    // Backing property to resource binding
    private var _binding: FragmentFilesBinding? = null

    // Property valid between onCreateView() and onDestroyView()
    private val binding
        get() = _binding!!

    // Holds a reference to the RecyclerView's adapter
    private lateinit var adapter: PictureAdapter

    // Shows a generic dialog asking the user for the required permission and performs the
    // desired task if the permission is granted
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted)
                when (permissionViewModel.requiredPermission.value) {
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE ->
                        // Display PNG images from public shared storage
                        viewModel.loadPublicPictureFiles()

                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                        // Update the text file content in public shared storage
                        viewModel.savePublicPictureFiles()

                    else -> {} // Nothing to do for other permissions
                }
            // Display a message if permission are denied
            else Snackbar.make(binding.root, R.string.no_permission, Snackbar.LENGTH_SHORT).show()
        }

    // Shows a generic form for choosing a text file and reads its content if the operation is confirmed
    private val readPublicOtherLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // If RESULT_OK then result.data is the Intent to load the file
            // Otherwise result.data is null and the TextBox will be empty
            viewModel.loadPublicOtherFile(result.data)
        }

    // Shows a generic form for choosing a text file and updates its content if the operation is confirmed
    private val writePublicOtherLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (result.data != null)
                    viewModel.savePublicOtherFile(result.data!!)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Get the automatically generated view binding for the layout resource
        _binding = FragmentFilesBinding.bind(view)

        // Adapter for the RecyclerView with Vertical LinearLayoutManager
        adapter = PictureAdapter()
        binding.recyclerView.adapter = adapter

        setupDropDownMenu()
        setupSaveButton()
        setupObservers()
    }

    /**
     * Executes the required action when the save button is clicked.
     */
    private fun setupSaveButton() {

        binding.bSave.setOnClickListener { _ ->
            // Determine the action according to the text displayed in the dropdown menu
            when (binding.tvFileStorageType.editableText.toString()) {
                // Update the content of the file in private internal storage
                getString(R.string.private_internal) ->
                    viewModel.savePrivateInternalFile(binding.etFileContents.text.toString())
                // Update the content of the file cached in private internal storage
                getString(R.string.private_internal_cache) ->
                    viewModel.savePrivateInternalCacheFile(binding.etFileContents.text.toString())
                // Update the content of the file in private external storage
                getString(R.string.private_external) ->
                    viewModel.savePrivateExternalFile(binding.etFileContents.text.toString())
                // Update the content of the file cached in private external storage
                getString(R.string.private_external_cache) ->
                    viewModel.savePrivateExternalCacheFile(binding.etFileContents.text.toString())
                // Create a new PNG image file in private external storage
                getString(R.string.private_external_pictures) ->
                    viewModel.savePrivateExternalPictureFile()
                // Create a new PNG image file in public shared storage
                getString(R.string.public_media) -> {
                    // Sets the required permission
                    permissionViewModel.setRequiredPermission(
                        getRequiredPermission(PermissionsTypes.WRITE_PUBLIC_IMAGES)
                    )
                    // Check whether the required permission is granted
                    if (isPermissionGranted(permissionViewModel.requiredPermission.value))
                    // Create the PNG image file in public shared storage
                        viewModel.savePublicPictureFiles()
                    // Check whether a rationale about the needs for this permission must be displayed
                    else if (shouldShowRequestPermissionRationale(permissionViewModel.requiredPermission.value))
                    // Show a dialog with the required rationale
                        findNavController().navigate(R.id.actionShowRationaleDialogFragment)
                    // Request the required permission from the user
                    else {
                        requestPermissionLauncher.launch(permissionViewModel.requiredPermission.value)
                    }
                }
                // Update the content of the file in public shared storage
                getString(R.string.public_others) ->
                    setPublicExternalOtherFileContent()
                // Do nothing for other entries in the dropdown menu
                else -> {}
            }
            // Hide the soft keyboard
            hideKeyboard(binding.bSave)
        }
    }

    /**
     * Executes an action whenever an element from the dropdown menu is clicked.
     */
    private fun setupDropDownMenu() {
        binding.tvFileStorageType.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, _ ->
                // Determine the action according to text displayed
                when (binding.tvFileStorageType.editableText.toString()) {
                    // Load the contents of a resource file
                    getString(R.string.resources) -> viewModel.loadResourceFile()
                    // Load the contents of an asset file
                    getString(R.string.assets) -> viewModel.loadAssetFile()
                    // Load the contents of a text file from private internal storage
                    getString(R.string.private_internal) -> viewModel.loadPrivateInternalFile()
                    // Load the contents of a text file cached in private internal storage
                    getString(R.string.private_internal_cache) -> viewModel.loadPrivateInternalCacheFile()
                    // Load the contents of a text file from private external storage
                    getString(R.string.private_external) -> viewModel.loadPrivateExternalFile()
                    // Load the contents of a text file cached in private external storage
                    getString(R.string.private_external_cache) -> viewModel.loadPrivateExternalCacheFile()
                    // Load the PNG images from private external storage
                    getString(R.string.private_external_pictures) -> viewModel.loadPrivateExternalPictureFiles()
                    // Load the PNG images from public shared storage
                    getString(R.string.public_media) -> {
                        // Sets the required permission
                        permissionViewModel.setRequiredPermission(
                            getRequiredPermission(PermissionsTypes.READ_PUBLIC_IMAGES)
                        )
                        // Check whether the required permission is granted
                        if (isPermissionGranted(permissionViewModel.requiredPermission.value))
                        // Get the content of the file from public shared storage
                            viewModel.loadPublicPictureFiles()
                        // Check whether a rationale about the needs for this permission must be displayed
                        else if (shouldShowRequestPermissionRationale(permissionViewModel.requiredPermission.value))
                        // Show a dialog with the required rationale
                            findNavController().navigate(R.id.actionShowRationaleDialogFragment)
                        // Request the required permission from the user
                        else requestPermissionLauncher.launch(permissionViewModel.requiredPermission.value)
                    }
                    // Load the contents of a text file from public shared storage
                    getString(R.string.public_others) -> {
                        getPublicExternalOtherFile()
                    }
                }
                // Hide the soft keyboard
                hideKeyboard(binding.tvFileStorageType)
            }
    }

    /**
     * Sets up observers to react to changes in the UI state.
     */
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Display the content of the selected text file
                viewModel.fileContent.collect { fileContent ->
                    binding.etFileContents.setText(fileContent)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    // Prevent the edition of files that cannot be updated
                    binding.etFileContents.isEnabled = uiState.isFileContentEditable
                    // Update the visibility of the edit text and RecyclerView according to the selected action
                    binding.tilFileContent.visibility =
                        if (uiState.isFileContentVisible) View.VISIBLE else View.INVISIBLE
                    binding.recyclerView.visibility =
                        if (uiState.isFileContentVisible) View.INVISIBLE else View.VISIBLE
                    // Update the visibility of the save button according to the selected action
                    binding.bSave.visibility =
                        if (uiState.isSaveButtonVisible) View.VISIBLE else View.INVISIBLE
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Update the list of pictures to display
                viewModel.picturesUri.collect { pictures ->
                    adapter.submitList(pictures)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Show the generic dialog to request permissions from the user
                // after the rationale dialog has been understood
                permissionViewModel.isMessageUnderstood.collect { isUnderstood ->
                    if (isUnderstood) {
                        when (permissionViewModel.requiredPermission.value) {
                            android.Manifest.permission.READ_MEDIA_IMAGES,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE ->
                                // Show the generic dialog to ask for permissions
                                requestPermissionLauncher.launch(permissionViewModel.requiredPermission.value)

                            else -> {}
                        }
                        // Clear the understood flag
                        permissionViewModel.setMessageUnderstood(false)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Display an error message when something has gone wrong
                viewModel.error.collect { error ->
                    if (error != null) {
                        // Select the message according to the received exception
                        val message = when (error) {
                            is Resources.NotFoundException -> getString(R.string.resource_not_found)
                            is FileNotFoundException -> getString(R.string.file_not_found)
                            is ExternalStorageNotReadableException -> getString(R.string.external_storage_unreadable)
                            is ExternalStorageNotWritableException -> getString(R.string.external_storage_unwritable)
                            else -> getString(R.string.unknown_error)
                        }
                        // Display the message
                        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                        // Clear the error flag
                        viewModel.clearError()
                    }
                }
            }
        }
    }

    /**
     * Determines the required permission for the desired action.
     */
    private fun getRequiredPermission(permissionType: String): String =
        when (permissionType) {
            // Reading multimedia images from public storage require different permissions
            // depending on the version of Android used
            PermissionsTypes.READ_PUBLIC_IMAGES ->
                if (Build.VERSION.SDK_INT > 32)
                    android.Manifest.permission.READ_MEDIA_IMAGES
                else {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                }
            // Writing multimedia images in public storage only require permissions for
            // version of Android below API 29
            PermissionsTypes.WRITE_PUBLIC_IMAGES ->
                if (Build.VERSION.SDK_INT < 29) {
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                } else {
                    PermissionsTypes.NO_PERMISSION
                }
            // No permission required whatsoever
            else -> PermissionsTypes.NO_PERMISSION
        }

    /**
     * Checks whether the required permission is already granted.
     */
    private fun isPermissionGranted(permission: String): Boolean =
        if (permission == PermissionsTypes.NO_PERMISSION)
            true
        else checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED

    /**
     * Launches a generic dialog for the user to select (and name)
     * a file in which to save the desired content.
     */
    private fun setPublicExternalOtherFileContent() {
        // Implicit Intent to create a text document with a given name
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("text/plain")
            .putExtra(
                Intent.EXTRA_TITLE, "public_other_storage_${
                    LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm"))
                }.txt"
            )
        // Set the text to write in a text file in public shared storage
        viewModel.setPublicOtherFileText(binding.etFileContents.text.toString())
        // Launch the Intent if there is any activity that can handle it
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            writePublicOtherLauncher.launch(intent)
        } else {
            // Show a message if it is not possible to handle that Intent
            Snackbar.make(binding.root, R.string.no_activity, Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Launches a generic dialog for the user to select a text file and display its content.
     */
    private fun getPublicExternalOtherFile() {
        // Implicit Intent to open a text file
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("text/plain")
        // Launch the Intent if there is any activity that can handle it
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            readPublicOtherLauncher.launch(intent)
        } else {
            // Show a message if it is not possible to handle that Intent
            Snackbar.make(binding.root, R.string.no_activity, Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Hides the soft keyboard.
     */
    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(requireContext(), InputMethodManager::class.java)
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear resources to make them eligible for garbage collection
        _binding = null
    }
}