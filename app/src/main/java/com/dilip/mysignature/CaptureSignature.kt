package com.dilip.mysignature

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class CaptureSignature : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val nameState = remember { mutableStateOf("") }
                val paths = remember { mutableStateListOf<Path>() }
                
                SignatureCaptureScreen(
                    name = nameState.value,
                    onNameChange = { nameState.value = it },
                    paths = paths,
                    onSave = { bitmap ->
                        if (nameState.value.trim().isEmpty()) {
                            Toast.makeText(this@CaptureSignature, "Name is required", Toast.LENGTH_SHORT).show()
                        } else {
                            checkPermissionAndSave(bitmap)
                        }
                    },
                    onCancel = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }

    private fun checkPermissionAndSave(bitmap: Bitmap) {
        val permission = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        } else {
            null
        }

        if (permission != null && ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                100
            )
        } else {
            saveSignature(bitmap)
        }
    }

    private fun saveSignature(bitmap: Bitmap) {
        val fileName = "Signature_${UUID.randomUUID()}.png"
        
        try {
            val imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveImageInQ(bitmap, fileName)
            } else {
                saveImageInLegacy(bitmap, fileName)
            }

            if (imageUri != null) {
                val intent = Intent().apply {
                    putExtra("status", "done")
                    putExtra("uri", imageUri.toString())
                }
                setResult(RESULT_OK, intent)
                finish()
            } else {
                throw IOException("Failed to create MediaStore entry")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving signature", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageInQ(bitmap: Bitmap, fileName: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Signatures")
        }
        
        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        
        uri?.let {
            resolver.openOutputStream(it).use { out ->
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
            }
        }
        return uri
    }

    private fun saveImageInLegacy(bitmap: Bitmap, fileName: String): Uri? {
        val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Signatures")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folder, fileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DATA, file.absolutePath)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }
}

@Composable
fun SignatureCaptureScreen(
    name: String,
    onNameChange: (String) -> Unit,
    paths: MutableList<Path>,
    onSave: (Bitmap) -> Unit,
    onCancel: () -> Unit
) {
    val currentPathState = remember { mutableStateOf<Path?>(null) }
    val drawScopeSizeState = remember { mutableStateOf(IntSize.Zero) }

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Capture Signature") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Your Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Please sign below:", style = MaterialTheme.typography.subtitle1)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .border(1.dp, Color.Black, RoundedCornerShape(4.dp))
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPathState.value = Path().apply {
                                    moveTo(offset.x, offset.y)
                                }
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                currentPathState.value?.lineTo(change.position.x, change.position.y)
                            },
                            onDragEnd = {
                                currentPathState.value?.let { paths.add(it) }
                                currentPathState.value = null
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawScopeSizeState.value = IntSize(size.width.toInt(), size.height.toInt())
                    paths.forEach { path ->
                        drawPath(
                            path = path,
                            color = Color.Black,
                            style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                    currentPathState.value?.let { path ->
                        drawPath(
                            path = path,
                            color = Color.Black,
                            style = Stroke(width = 8f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", maxLines = 1)
                }
                
                OutlinedButton(
                    onClick = { paths.clear() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear", maxLines = 1)
                }

                OutlinedButton(
                    onClick = {
                        if (paths.isNotEmpty()) {
                            paths.removeAt(paths.size - 1)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = paths.isNotEmpty()
                ) {
                    Text("Undo", maxLines = 1)
                }

                Button(
                    onClick = {
                        val bitmap = createBitmapFromPaths(paths, drawScopeSizeState.value)
                        onSave(bitmap)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = paths.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                ) {
                    Text("Save", maxLines = 1)
                }
            }
        }
    }
}

fun createBitmapFromPaths(paths: List<Path>, size: IntSize): Bitmap {
    val width = if (size.width > 0) size.width else 1
    val height = if (size.height > 0) size.height else 1
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)
    
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        style = android.graphics.Paint.Style.STROKE
        strokeWidth = 8f
        strokeCap = android.graphics.Paint.Cap.ROUND
        strokeJoin = android.graphics.Paint.Join.ROUND
        isAntiAlias = true
    }

    paths.forEach { path ->
        canvas.drawPath(path.asAndroidPath(), paint)
    }
    
    return bitmap
}
