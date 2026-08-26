package com.aseprite.android.ui

import android.graphics.Canvas
import android.os.Bundle
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = com.aseprite.android.databinding.FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spritePtr = arguments?.getLong("spritePtr", 0) ?: 0
        if (spritePtr == 0) {
            requireActivity().finish()
            return
        }

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
        val frameCount = core.getFrameCount(spritePtr)
        binding.frameSeekBar.max = max(0, frameCount - 1)
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

        // Tool buttons
        binding.btnUndo.setOnClickListener { core.undo(spritePtr); renderFrame() }
        binding.btnRedo.setOnClickListener { core.redo(spritePtr); renderFrame() }
        binding.btnAddLayer.setOnClickListener { addLayer() }
        binding.btnDeleteLayer.setOnClickListener { deleteLayer() }

        // Layer spinner
        updateLayerSpinner()
    }

    private fun drawAt(x: Int, y: Int) {
        val core = AsepriteCore.getInstance()
        core.setPixel(spritePtr, currentFrame, 0, x, y, 0xFF000000) // Black for now
        renderFrame()
    }

    private fun renderFrame() {
        val core = AsepriteCore.getInstance()
        val bitmap = core.renderFrame(spritePtr, currentFrame)
        bitmap?.let {
            binding.editorCanvas.setImageBitmap(it)
        }
    }

    private fun addLayer() {
        val core = AsepriteCore.getInstance()
        val layerPtr = core.createLayer(spritePtr, "Layer ${core.getLayerCount(spritePtr) + 1}")
        if (layerPtr > 0) {
            updateLayerSpinner()
        }
    }

    private fun deleteLayer() {
        val core = AsepriteCore.getInstance()
        if (core.getLayerCount(spritePtr) > 1) {
            core.deleteLayer(spritePtr, 0)
            updateLayerSpinner()
        }
    }

    private fun updateLayerSpinner() {
        val core = AsepriteCore.getInstance()
        val count = core.getLayerCount(spritePtr)
        // TODO: Update layer spinner adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}