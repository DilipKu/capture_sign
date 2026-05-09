package com.dilip.capturesign;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dilip.capturesign.databinding.ActivityMainBinding;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final int SIGNATURE_ACTIVITY = 1;
    private ActivityMainBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        binding.btnGetSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CaptureSignature.class);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGNATURE_ACTIVITY && resultCode == RESULT_OK && data != null) {
            String status = data.getStringExtra("status");
            if ("done".equalsIgnoreCase(status)) {
                String path = data.getStringExtra("path");
                if (path != null) {
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        binding.signaturePreview.setImageBitmap(myBitmap);
                    }
                }
                Toast.makeText(this, getString(R.string.msg_signature_saved), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
