package com.aseprite.android.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aseprite.android.R

class OpenSpriteFragment : Fragment() {
    private var _binding: com.aseprite.android.databinding.FragmentOpenSpriteBinding? = null
    private val binding get() = _binding!!
    
    private val openDocumentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            val uri = result.data.data ?: return@registerForActivityResult
            openSpriteFromUri(uri)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = com.aseprite.android.databinding.FragmentOpenSpriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnOpenFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
                val initialDir = requireActivity().getExternalFilesDir(null)
                if (initialDir != null) {
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.fromFile(initialDir))
                }
            }
            openDocumentLauncher.launch(intent)
        }
    }
    
    private fun openSpriteFromUri(uri: Uri) {
        val core = com.aseprite.android.AsepriteCore.getInstance()
        val path = uri.toString() // Simplified - in real app would need content resolver
        val spritePtr = core.openSprite(path)
        if (spritePtr > 0) {
            val action = OpenSpriteFragmentDirections.actionOpenSpriteFragmentToEditorFragment(spritePtr)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}