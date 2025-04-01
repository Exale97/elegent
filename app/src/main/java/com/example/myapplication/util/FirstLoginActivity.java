package com.example.myapplication.util;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.util.ui.GLSurface.BubbleRenderer;

import java.util.List;

public class FirstLoginActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private BubbleRenderer bubbleRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_login_set_domains);

        FrameLayout bubbleContainer = findViewById(R.id.bubble_container);

        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(2);

        bubbleRenderer = new BubbleRenderer();
        glSurfaceView.setRenderer(bubbleRenderer);

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
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                float normalizedX = (2.0f * x / glSurfaceView.getWidth() - 1.0f);
                float normalizedY = -(2.0f * y / glSurfaceView.getHeight() - 1.0f);

                if (bubbleRenderer.handleTouchEvent(normalizedX, normalizedY)) {
                    glSurfaceView.requestRender();
                }
                return true;
        }
        return super.onTouchEvent(e);
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