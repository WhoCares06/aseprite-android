package com.aseprite.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class OpenSpriteFragment : Fragment() {
    private var _binding: com.aseprite.android.databinding.FragmentOpenSpriteBinding? = null
    private val binding get() = _binding!!

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
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, requireActivity().getExternalFilesDir(null)?.toUri())
            }
            startActivityForResult(intent, 1002)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1002 && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data ?: return
            // Return the URI to the activity
            val intent = Intent().apply {
                putExtra("uri", uri.toString())
            }
            activity?.setResult(Activity.RESULT_OK, intent)
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}