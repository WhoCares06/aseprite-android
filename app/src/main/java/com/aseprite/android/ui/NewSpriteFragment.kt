package com.aseprite.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aseprite.android.R

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
            
            // Create sprite via AsepriteCore and navigate to editor
            val core = com.aseprite.android.AsepriteCore.getInstance()
            val spritePtr = core.createSprite(width, height, colorMode)
            if (spritePtr > 0) {
                val action = NewSpriteFragmentDirections.actionNewSpriteFragmentToEditorFragment(spritePtr)
                findNavController().navigate(action)
            }
        }
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}