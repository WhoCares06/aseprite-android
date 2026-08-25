package com.aseprite.android.ui

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.aseprite.android.AsepriteCore
import com.aseprite.android.databinding.FragmentEditorBinding
import kotlinx.coroutines.launch

class EditorFragment : Fragment() {

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!
    private var spritePtr: Long = 0
    private var currentFrame = 0
    private var currentLayer = 0
    private var zoomLevel = 1.0f
    private var panX = 0f
    private var panY = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get sprite from arguments
        arguments?.getLong("sprite_ptr")?.let { ptr ->
            spritePtr = ptr
            setupEditor()
        } ?: run {
            Toast.makeText(requireContext(), "No sprite loaded", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }
    
    private fun setupEditor() {
        // Setup canvas touch handling
        binding.editorCanvas.setOnTouchListener { v, event ->
            handleCanvasTouch(event)
            true
        }
        
        // Setup toolbar buttons
        binding.btnUndo.setOnClickListener { AsepriteCore.undo(spritePtr) }
        binding.btnRedo.setOnClickListener { AsepriteCore.redo(spritePtr) }
        binding.btnZoomIn.setOnClickListener { zoomLevel *= 1.2f; invalidateCanvas() }
        binding.btnZoomOut.setOnClickListener { zoomLevel /= 1.2f; invalidateCanvas() }
        binding.btnGrid.setOnClickListener { binding.editorCanvas.toggleGrid() }
        binding.btnOnionSkin.setOnClickListener { binding.editorCanvas.toggleOnionSkin() }
        
        // Frame navigation
        binding.btnPrevFrame.setOnClickListener { 
            if (currentFrame > 0) { currentFrame--; updateFrameInfo(); invalidateCanvas() }
        }
        binding.btnNextFrame.setOnClickListener { 
            val frameCount = AsepriteCore.getFrameCount(spritePtr)
            if (currentFrame < frameCount - 1) { currentFrame++; updateFrameInfo(); invalidateCanvas() }
        }
        
        // Layer navigation
        binding.btnAddLayer.setOnClickListener { AsepriteCore.createLayer(spritePtr, "Layer ${AsepriteCore.getLayerCount(spritePtr) + 1}") }
        
        // Initial render
        lifecycleScope.launch {
            renderCurrentFrame()
        }
    }
    
    private fun handleCanvasTouch(event: MotionEvent): Boolean {
        // TODO: Implement drawing, panning, selection
        return true
    }
    
    private fun renderCurrentFrame() {
        val bitmap = AsepriteCore.renderFrame(spritePtr, currentFrame)
        bitmap?.let {
            binding.editorCanvas.setBitmap(it)
        }
    }
    
    private fun updateFrameInfo() {
        binding.frameInfo.text = "${currentFrame + 1} / ${AsepriteCore.getFrameCount(spritePtr)}"
    }
    
    private fun invalidateCanvas() {
        binding.editorCanvas.invalidate()
        renderCurrentFrame()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}