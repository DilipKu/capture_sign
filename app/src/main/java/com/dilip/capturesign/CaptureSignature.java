package com.dilip.capturesign;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dilip.capturesign.databinding.ActivityCaptureSignatureBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CaptureSignature extends AppCompatActivity {

    private ActivityCaptureSignatureBinding binding;
    private SignatureView signatureView;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCaptureSignatureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        signatureView = new SignatureView(this, null);
        binding.signatureContainer.addView(signatureView);

        binding.clear.setOnClickListener(v -> {
            signatureView.clear();
            binding.getsign.setEnabled(false);
            binding.undo.setEnabled(false);
        });

        binding.undo.setOnClickListener(v -> {
            signatureView.undo();
            binding.getsign.setEnabled(!signatureView.isEmpty());
            binding.undo.setEnabled(!signatureView.isEmpty());
        });

        binding.cancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        binding.getsign.setOnClickListener(v -> {
            if (validateInput()) {
                saveSignatureWithPermission();
            }
        });

        binding.getsign.setEnabled(false);
        binding.undo.setEnabled(false);
    }

    private boolean validateInput() {
        if (binding.yourName.getText().toString().trim().isEmpty()) {
            binding.nameInputLayout.setError(getString(R.string.error_name_required));
            return false;
        }
        binding.nameInputLayout.setError(null);
        return true;
    }

    private void saveSignatureWithPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            saveSignature();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            saveSignature();
        }
    }

    private void saveSignature() {
        Bitmap bitmap = signatureView.getBitmap();
        File folder = getExternalFilesDir("Signatures");
        if (folder != null && !folder.exists()) {
            folder.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + ".png";
        File file = new File(folder, fileName);

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Signature_" + fileName, "Captured Signature");

            Intent intent = new Intent();
            intent.putExtra("status", "done");
            intent.putExtra("path", file.getAbsolutePath());
            setResult(RESULT_OK, intent);
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.error_save_failed), Toast.LENGTH_SHORT).show();
        }
    }

    public class SignatureView extends View {
        private static final float STROKE_WIDTH = 10f;
        private final Paint paint = new Paint();
        private final List<Path> paths = new ArrayList<>();
        private Path currentPath;
        private float lastX, lastY;

        public SignatureView(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void clear() {
            paths.clear();
            if (currentPath != null) currentPath.reset();
            invalidate();
        }

        public void undo() {
            if (!paths.isEmpty()) {
                paths.remove(paths.size() - 1);
                invalidate();
            }
        }

        public boolean isEmpty() {
            return paths.isEmpty();
        }

        public Bitmap getBitmap() {
            int width = getWidth() > 0 ? getWidth() : 1;
            int height = getHeight() > 0 ? getHeight() : 1;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.WHITE);
            draw(canvas);
            return bitmap;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            for (Path p : paths) {
                canvas.drawPath(p, paint);
            }
            if (currentPath != null) {
                canvas.drawPath(currentPath, paint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    currentPath = new Path();
                    currentPath.moveTo(x, y);
                    lastX = x;
                    lastY = y;
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float dx = Math.abs(x - lastX);
                    float dy = Math.abs(y - lastY);
                    if (dx >= 4 || dy >= 4) {
                        currentPath.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
                        lastX = x;
                        lastY = y;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    currentPath.lineTo(x, y);
                    paths.add(currentPath);
                    currentPath = null;
                    binding.getsign.setEnabled(true);
                    binding.undo.setEnabled(true);
                    break;
                default:
                    return false;
            }

            invalidate();
            return true;
        }
    }
}
