package com.dilip.capturesign

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dilip.capturesign.databinding.ActivityCaptureSignatureBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.math.abs

class CaptureSignature : AppCompatActivity() {

    private lateinit var binding: ActivityCaptureSignatureBinding
    private lateinit var signatureView: SignatureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaptureSignatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signatureView = SignatureView(this, null)
        binding.signatureContainer.addView(signatureView)

        binding.clear.setOnClickListener {
            signatureView.clear()
            binding.getsign.isEnabled = false
            binding.undo.isEnabled = false
        }

        binding.undo.setOnClickListener {
            signatureView.undo()
            binding.getsign.isEnabled = !signatureView.isEmpty()
            binding.undo.isEnabled = !signatureView.isEmpty()
        }

        binding.cancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        binding.getsign.setOnClickListener {
            if (validateInput()) {
                saveSignatureWithPermission()
            }
        }

        binding.getsign.isEnabled = false
        binding.undo.isEnabled = false
    }

    private fun validateInput(): Boolean {
        if (binding.yourName.text.toString().trim().isEmpty()) {
            binding.nameInputLayout.error = getString(R.string.error_name_required)
            return false
        }
        binding.nameInputLayout.error = null
        return true
    }

    private fun saveSignatureWithPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        } else {
            saveSignature()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            saveSignature()
        }
    }

    private fun saveSignature() {
        val bitmap = signatureView.getBitmap()
        val folder = getExternalFilesDir("Signatures")
        if (folder != null && !folder.exists()) {
            folder.mkdirs()
        }

        val fileName = UUID.randomUUID().toString() + ".png"
        val file = File(folder, fileName)

        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                MediaStore.Images.Media.insertImage(
                    contentResolver,
                    bitmap,
                    "Signature_$fileName",
                    "Captured Signature"
                )

                val intent = Intent().apply {
                    putExtra("status", "done")
                    putExtra("path", file.absolutePath)
                }
                setResult(RESULT_OK, intent)
                finish()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.error_save_failed), Toast.LENGTH_SHORT).show()
        }
    }

    inner class SignatureView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
        private val paint = Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = STROKE_WIDTH
        }
        private val paths = ArrayList<Path>()
        private var currentPath: Path? = null
        private var lastX = 0f
        private var lastY = 0f

        fun clear() {
            paths.clear()
            currentPath?.reset()
            invalidate()
        }

        fun undo() {
            if (paths.isNotEmpty()) {
                paths.removeAt(paths.size - 1)
                invalidate()
            }
        }

        fun isEmpty(): Boolean = paths.isEmpty()

        fun getBitmap(): Bitmap {
            val width = if (this.width > 0) this.width else 1
            val height = if (this.height > 0) this.height else 1
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)
            draw(canvas)
            return bitmap
        }

        override fun onDraw(canvas: Canvas) {
            for (p in paths) {
                canvas.drawPath(p, paint)
            }
            currentPath?.let {
                canvas.drawPath(it, paint)
            }
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val x = event.x
            val y = event.y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    currentPath = Path().apply {
                        moveTo(x, y)
                    }
                    lastX = x
                    lastY = y
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = abs(x - lastX)
                    val dy = abs(y - lastY)
                    if (dx >= 4 || dy >= 4) {
                        currentPath?.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2)
                        lastX = x
                        lastY = y
                    }
                }
                MotionEvent.ACTION_UP -> {
                    currentPath?.lineTo(x, y)
                    currentPath?.let { paths.add(it) }
                    currentPath = null
                    binding.getsign.isEnabled = true
                    binding.undo.isEnabled = true
                }
                else -> return false
            }

            invalidate()
            return true
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val STROKE_WIDTH = 10f
    }
}
