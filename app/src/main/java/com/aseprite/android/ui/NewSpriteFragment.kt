package com.aseprite.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class NewSpriteFragment : Fragment() {
    private var _binding: com.aseprite.android.databinding.FragmentNewSpriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = com.aseprite.android.databinding.FragmentNewSpriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCreate.setOnClickListener {
            val width = binding.etWidth.text.toString().toIntOrNull() ?: 256
            val height = binding.etHeight.text.toString().toIntOrNull() ?: 256
            val colorMode = when (binding.spinnerColorMode.selectedItemPosition) {
                1 -> 1 // Grayscale
                2 -> 2 // Indexed
                else -> 0 // RGBA
            }
            val intent = Intent().apply {
                putExtra("width", width)
                putExtra("height", height)
                putExtra("colorMode", colorMode)
            }
            activity?.setResult(Activity.RESULT_OK, intent)
            activity?.finish()
        }
        binding.btnCancel.setOnClickListener {
            activity?.setResult(Activity.RESULT_CANCELED)
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}