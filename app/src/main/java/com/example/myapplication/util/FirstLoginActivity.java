package com.example.myapplication.util;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.util.ui.GLSurface.BubbleRenderer;
import com.example.myapplication.util.ui.GLSurface.CustomGLSurfaceView;

import java.util.List;

public class FirstLoginActivity extends AppCompatActivity {

    private CustomGLSurfaceView glSurfaceView;
    private BubbleRenderer bubbleRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_login_set_domains);

        FrameLayout bubbleContainer = findViewById(R.id.bubble_container);

        glSurfaceView = new CustomGLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);

        bubbleRenderer = new BubbleRenderer();
        glSurfaceView.setRenderer(bubbleRenderer);
        glSurfaceView.setBubbleRenderer(bubbleRenderer);

        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        bubbleContainer.addView(glSurfaceView);

        Button btnConfirm = findViewById(R.id.button_confirm);
        btnConfirm.setOnClickListener(view -> {
            List<String> selectedDomains = bubbleRenderer.getSelectedDomains();

            if (selectedDomains.size() != 2) {
                Toast.makeText(this, "请选择两个领域", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(FirstLoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (glSurfaceView != null) {
            glSurfaceView.onResume();
        }
    }
}