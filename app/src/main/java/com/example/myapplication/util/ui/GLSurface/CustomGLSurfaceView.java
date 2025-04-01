package com.example.myapplication.util.ui.GLSurface;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomGLSurfaceView extends GLSurfaceView {

    private BubbleRenderer bubbleRenderer;

    public CustomGLSurfaceView(Context context) {
        super(context);
    }

    public CustomGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBubbleRenderer(BubbleRenderer renderer) {
        this.bubbleRenderer = renderer;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();
            float normalizedX = (2.0f * x / getWidth() - 1.0f);
            float normalizedY = -(2.0f * y / getHeight() - 1.0f);

            if (bubbleRenderer != null && bubbleRenderer.handleTouchEvent(normalizedX, normalizedY)) {
                requestRender();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}