package com.dilip.capturesign

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dilip.capturesign.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetSignature.setOnClickListener {
            val intent = Intent(this, CaptureSignature::class.java)
            startActivityForResult(intent, SIGNATURE_ACTIVITY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGNATURE_ACTIVITY && resultCode == RESULT_OK && data != null) {
            val status = data.getStringExtra("status")
            if ("done".equals(status, ignoreCase = true)) {
                val path = data.getStringExtra("path")
                if (path != null) {
                    val imgFile = File(path)
                    if (imgFile.exists()) {
                        val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                        binding.signaturePreview.setImageBitmap(myBitmap)
                    }
                }
                Toast.makeText(this, getString(R.string.msg_signature_saved), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val SIGNATURE_ACTIVITY = 1
    }
}
