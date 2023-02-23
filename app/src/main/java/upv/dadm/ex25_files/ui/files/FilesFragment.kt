/*
 * Copyright (c) 2022
 * David de Andrés and Juan Carlos Ruiz
 * Development of apps for mobile devices
 * Universitat Politècnica de València
 */

package upv.dadm.ex25_files.ui.files

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import upv.dadm.ex25_files.R
import upv.dadm.ex25_files.databinding.FragmentFilesBinding
import java.io.FileNotFoundException
import java.io.IOException

@AndroidEntryPoint
class FilesFragment : Fragment(R.layout.fragment_files) {

    private val viewModel: FilesViewModel by viewModels()

    private var _binding: FragmentFilesBinding? = null
    private val binding
        get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFilesBinding.bind(view)

        binding.tvFileStorageType.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, _, _ ->
                when (binding.tvFileStorageType.editableText.toString()) {
                    getString(R.string.resources) -> viewModel.loadResourceFile()
                    getString(R.string.assets) -> viewModel.loadAssetFile()
                    getString(R.string.private_internal) -> viewModel.loadPrivateInternalFile()
                    getString(R.string.private_internal_cache) -> viewModel.loadPrivateInternalCacheFile()
                    else -> {}
                }
                hideKeyboard(binding.tvFileStorageType)
            }

        binding.bSave.setOnClickListener {
            when (binding.tvFileStorageType.editableText.toString()) {
                getString(R.string.private_internal) ->
                    viewModel.savePrivateInternalFile(binding.etFileContents.text.toString())
                getString(R.string.private_internal_cache) ->
                    viewModel.savePrivateInternalCacheFile(binding.etFileContents.text.toString())
                else -> {}
            }
            hideKeyboard(binding.bSave)
        }

        viewModel.fileContent.observe(viewLifecycleOwner) { fileContent ->
            binding.etFileContents.setText(fileContent)
        }
        viewModel.isFileContentEditable.observe(viewLifecycleOwner) { isEditable ->
            binding.etFileContents.isEnabled = isEditable
            binding.bSave.visibility = if (isEditable) View.VISIBLE else View.INVISIBLE
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                val message = when(error) {
                    is Resources.NotFoundException -> getString(R.string.resource_not_found)
                    is FileNotFoundException -> getString(R.string.file_not_found)
                    else -> getString(R.string.unknown_error)
                }
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(requireContext(), InputMethodManager::class.java)
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}