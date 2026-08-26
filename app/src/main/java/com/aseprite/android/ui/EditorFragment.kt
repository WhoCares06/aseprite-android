package com.aseprite.android.ui

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.aseprite.android.AsepriteCore
import com.aseprite.android.databinding.FragmentEditorBinding

class EditorFragment : Fragment() {

    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!
    private var spritePtr: Long = 0
    private var currentFrame = 0
    private var currentLayer = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spritePtr = arguments?.getLong("sprite_ptr", 0) ?: 0

        if (spritePtr == 0L) {
            Toast.makeText(requireContext(), "Invalid sprite", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        setupUI()
        updateFrameInfo()
    }

    private fun setupUI() {
        binding.canvasView.setOnTouchListener { v, event ->
            handleTouch(v, event)
            true
        }

        binding.btnPrevFrame.setOnClickListener { previousFrame() }
        binding.btnNextFrame.setOnClickListener { nextFrame() }
        binding.btnAddLayer.setOnClickListener { addLayer() }
        binding.btnUndo.setOnClickListener { undo() }
        binding.btnRedo.setOnClickListener { redo() }

        binding.seekFrame.setOnSeekBarChangeListener(object : androidx.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: androidx.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentFrame = progress
                    renderCurrentFrame()
                }
            }
            override fun onStartTrackingTouch(seekBar: androidx.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: androidx.widget.SeekBar?) {}
        })
    }

    private fun handleTouch(view: View, event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // Draw pixel
                AsepriteCore().setPixel(spritePtr, currentFrame, currentLayer, x, y, 0xFF000000) // Black
                renderCurrentFrame()
            }
        }
        return true
    }

    private fun renderCurrentFrame() {
        val bitmap = AsepriteCore().renderFrame(spritePtr, currentFrame)
        bitmap?.let { binding.canvasView.setImageBitmap(it) }
    }

    private fun previousFrame() {
        if (currentFrame > 0) {
            currentFrame--
            binding.seekFrame.progress = currentFrame
            renderCurrentFrame()
            updateFrameInfo()
        }
    }

    private fun nextFrame() {
        val frameCount = AsepriteCore().getFrameCount(spritePtr)
        if (currentFrame < frameCount - 1) {
            currentFrame++
            binding.seekFrame.progress = currentFrame
            renderCurrentFrame()
            updateFrameInfo()
        }
    }

    private fun addLayer() {
        val layerPtr = AsepriteCore().createLayer(spritePtr, "Layer ${currentLayer + 1}")
        if (layerPtr != 0L) {
            currentLayer++
            updateLayerInfo()
            Toast.makeText(requireContext(), "Layer added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun undo() {
        if (AsepriteCore().canUndo(spritePtr)) {
            AsepriteCore().undo(spritePtr)
            renderCurrentFrame()
            Toast.makeText(requireContext(), "Undo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun redo() {
        if (AsepriteCore().canRedo(spritePtr)) {
            AsepriteCore().redo(spritePtr)
            renderCurrentFrame()
            Toast.makeText(requireContext(), "Redo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFrameInfo() {
        val frameCount = AsepriteCore().getFrameCount(spritePtr)
        binding.tvFrameInfo.text = "Frame ${currentFrame + 1} / $frameCount"
        binding.seekFrame.max = frameCount - 1
    }

    private fun updateLayerInfo() {
        val layerCount = AsepriteCore().getLayerCount(spritePtr)
        binding.tvLayerInfo.text = "Layer ${currentLayer + 1} / $layerCount"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}