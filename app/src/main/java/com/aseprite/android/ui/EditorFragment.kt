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
import kotlinx.coroutines.launch

class EditorFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_editor, container, false)
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
        // Setup toolbar buttons
        requireView().findViewById<View>(R.id.btn_undo).setOnClickListener { AsepriteCore.undo(spritePtr) }
        requireView().findViewById<View>(R.id.btn_redo).setOnClickListener { AsepriteCore.redo(spritePtr) }
        requireView().findViewById<View>(R.id.btn_zoom_in).setOnClickListener { zoomLevel *= 1.2f; invalidateCanvas() }
        requireView().findViewById<View>(R.id.btn_zoom_out).setOnClickListener { zoomLevel /= 1.2f; invalidateCanvas() }
        requireView().findViewById<View>(R.id.btn_grid).setOnClickListener { toggleGrid() }
        requireView().findViewById<View>(R.id.btn_onion_skin).setOnClickListener { toggleOnionSkin() }
        
        // Frame navigation
        requireView().findViewById<View>(R.id.btn_prev_frame).setOnClickListener { 
            if (currentFrame > 0) { currentFrame--; updateFrameInfo(); invalidateCanvas() }
        }
        requireView().findViewById<View>(R.id.btn_next_frame).setOnClickListener { 
            val frameCount = AsepriteCore.getFrameCount(spritePtr)
            if (currentFrame < frameCount - 1) { currentFrame++; updateFrameInfo(); invalidateCanvas() }
        }
        
        // Layer navigation
        requireView().findViewById<View>(R.id.btn_add_layer).setOnClickListener { 
            AsepriteCore.createLayer(spritePtr, "Layer ${AsepriteCore.getLayerCount(spritePtr) + 1}") 
        }
        
        // Initial render
        lifecycleScope.launch {
            renderCurrentFrame()
        }
    }
    
    private fun toggleGrid() {
        // TODO: Implement grid toggle
    }
    
    private fun toggleOnionSkin() {
        // TODO: Implement onion skin toggle
    }

    private fun renderCurrentFrame() {
        val bitmap = AsepriteCore.renderFrame(spritePtr, currentFrame)
        bitmap?.let {
            requireView().findViewById<com.aseprite.android.ui.CanvasView>(R.id.editor_canvas).setBitmap(it)
        }
    }
    
    private fun updateFrameInfo() {
        requireView().findViewById<android.widget.TextView>(R.id.frame_info).text = "${currentFrame + 1} / ${AsepriteCore.getFrameCount(spritePtr)}"
    }
    
    private fun invalidateCanvas() {
        requireView().findViewById<com.aseprite.android.ui.CanvasView>(R.id.editor_canvas).invalidate()
        renderCurrentFrame()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
    }
}