package com.aseprite.android.ui

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.aseprite.android.AsepriteCore
import kotlinx.coroutines.launch

class EditorFragment : Fragment() {
    private var _binding: com.aseprite.android.databinding.FragmentEditorBinding? = null
    private val binding get() = _binding!!
    private var spritePtr: Long = 0
    private var currentFrame = 0
    private val TAG = "EditorFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = com.aseprite.android.databinding.FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spritePtr = arguments?.getLong("spritePtr", 0) ?: 0
        if (spritePtr == 0L) {
            Log.e(TAG, "Invalid spritePtr received, finishing activity")
            requireActivity().finish()
            return
        }

        Log.d(TAG, "EditorFragment created with spritePtr=$spritePtr")
        setupCanvas()
        setupControls()
        renderFrame()
    }

    private fun setupCanvas() {
        binding.editorCanvas.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE -> {
                    val x = (event.x / v.scaleX).toInt()
                    val y = (event.y / v.scaleY).toInt()
                    drawAt(x, y)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupControls() {
        val core = AsepriteCore.getInstance()

        // Frame slider
        try {
            val frameCount = core.getFrameCount(spritePtr)
            binding.frameSeekBar.max = maxOf(0, frameCount - 1)
            binding.frameSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        currentFrame = progress
                        renderFrame()
                    }
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up frame slider", e)
        }

        // Tool buttons
        binding.btnUndo.setOnClickListener { 
            try { core.undo(spritePtr); renderFrame() } catch (e: Exception) { Log.e(TAG, "Undo failed", e) }
        }
        binding.btnRedo.setOnClickListener { 
            try { core.redo(spritePtr); renderFrame() } catch (e: Exception) { Log.e(TAG, "Redo failed", e) }
        }
        binding.btnAddLayer.setOnClickListener { 
            try { addLayer() } catch (e: Exception) { Log.e(TAG, "Add layer failed", e) }
        }
        binding.btnDeleteLayer.setOnClickListener { 
            try { deleteLayer() } catch (e: Exception) { Log.e(TAG, "Delete layer failed", e) }
        }

        // Layer spinner
        updateLayerSpinner()
    }

    private fun drawAt(x: Int, y: Int) {
        try {
            val core = AsepriteCore.getInstance()
            core.setPixel(spritePtr, currentFrame, 0, x, y, 0xFF000000.toInt()) // Black for now
            renderFrame()
        } catch (e: Exception) {
            Log.e(TAG, "Error drawing at ($x,$y)", e)
        }
    }

    private fun renderFrame() {
        try {
            val core = AsepriteCore.getInstance()
            val bitmap = core.renderFrame(spritePtr, currentFrame)
            bitmap?.let {
                binding.editorCanvas.setImageBitmap(it)
            } ?: Log.w(TAG, "renderFrame returned null bitmap")
        } catch (e: Exception) {
            Log.e(TAG, "Error rendering frame", e)
        }
    }

    private fun addLayer() {
        try {
            val core = AsepriteCore.getInstance()
            val layerPtr = core.createLayer(spritePtr, "Layer ${core.getLayerCount(spritePtr) + 1}")
            if (layerPtr > 0) {
                updateLayerSpinner()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error adding layer", e)
        }
    }

    private fun deleteLayer() {
        try {
            val core = AsepriteCore.getInstance()
            if (core.getLayerCount(spritePtr) > 1) {
                core.deleteLayer(spritePtr, 0)
                updateLayerSpinner()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting layer", e)
        }
    }

    private fun updateLayerSpinner() {
        try {
            val core = AsepriteCore.getInstance()
            val count = core.getLayerCount(spritePtr)
            // TODO: Update layer spinner adapter
            Log.d(TAG, "Layer count: $count")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating layer spinner", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}