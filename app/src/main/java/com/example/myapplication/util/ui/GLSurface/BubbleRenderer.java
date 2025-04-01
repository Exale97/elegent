package com.example.myapplication.util.ui.GLSurface;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BubbleRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "BubbleRenderer";
    private static final int SEGMENTS = 30;
    private static final float UNIT_RADIUS = 1.0f;
    // 增加点击判定范围（建议可调至2.0f以提高灵敏度）
    private static final float CLICK_TOLERANCE = 2.0f;

    private final float[] mvpMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private int program;
    private int textProgram;
    private FloatBuffer circleVertexBuffer;
    private FloatBuffer checkMarkBuffer;
    private final Random random = new Random();
    private final long startTime;

    // 文本纹理相关
    private Map<String, Integer> textureMap = new HashMap<>();
    private FloatBuffer textureVertexBuffer;
    private FloatBuffer textureCoordBuffer;

    private class Bubble {
        float x, y;
        float radius;
        float[] color;
        String label;
        boolean selected;
        float pulseOffset;
        float pulseSpeed;

        Bubble(float x, float y, float radius, float[] color, String label) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
            this.label = label;
            this.selected = false;
            this.pulseOffset = random.nextFloat() * (float) Math.PI * 2;
            this.pulseSpeed = 0.5f + random.nextFloat();
        }

        float getAnimatedRadius(float time) {
            if (selected) {
                return radius * (1.0f + 0.1f * (float) Math.sin(time * pulseSpeed + pulseOffset));
            } else {
                return radius * (1.0f + 0.05f * (float) Math.sin(time * pulseSpeed + pulseOffset));
            }
        }
    }

    private final List<Bubble> bubbles = new ArrayList<>();
    private final float[] modelMatrix = new float[16];

    public BubbleRenderer() {
        createVertexData();
        createCheckMark();
        createTextureBuffers();
        setupBubbles();
        startTime = System.currentTimeMillis();
    }

    private void setupBubbles() {
        String[] domains = {"技术", "艺术", "科学", "文化", "体育"};
        float[][] colors = {
                {1.0f, 0.3f, 0.3f, 0.9f},
                {1.0f, 0.2f, 0.8f, 0.9f},
                {0.7f, 0.3f, 1.0f, 0.9f},
                {0.3f, 0.7f, 1.0f, 0.9f},
                {0.3f, 0.9f, 0.3f, 0.9f}
        };

        // 缩小气泡间距，修改坐标值
        bubbles.add(new Bubble(-0.45f, 0.4f, 0.25f, colors[0], domains[0]));
        bubbles.add(new Bubble(0.45f, 0.4f, 0.22f, colors[1], domains[1]));
        bubbles.add(new Bubble(0.0f, 0.0f, 0.30f, colors[2], domains[2]));
        bubbles.add(new Bubble(-0.45f, -0.4f, 0.24f, colors[3], domains[3]));
        bubbles.add(new Bubble(0.45f, -0.4f, 0.27f, colors[4], domains[4]));
    }

    private void createVertexData() {
        float[] circleCoords = new float[(SEGMENTS + 2) * 3];
        circleCoords[0] = 0f;
        circleCoords[1] = 0f;
        circleCoords[2] = 0f;

        for (int i = 1; i <= SEGMENTS + 1; i++) {
            float theta = (float) (2.0f * Math.PI * (i - 1) / SEGMENTS);
            circleCoords[i * 3] = (float) (UNIT_RADIUS * Math.cos(theta));
            circleCoords[i * 3 + 1] = (float) (UNIT_RADIUS * Math.sin(theta));
            circleCoords[i * 3 + 2] = 0f;
        }

        ByteBuffer bb = ByteBuffer.allocateDirect(circleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        circleVertexBuffer = bb.asFloatBuffer();
        circleVertexBuffer.put(circleCoords);
        circleVertexBuffer.position(0);
    }

    private void createCheckMark() {
        // 勾选标记的顶点坐标
        float[] checkMarkCoords = {
                -0.5f, 0.0f, 0.0f,
                -0.2f, -0.3f, 0.0f,
                0.5f, 0.5f, 0.0f
        };

        ByteBuffer bb = ByteBuffer.allocateDirect(checkMarkCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        checkMarkBuffer = bb.asFloatBuffer();
        checkMarkBuffer.put(checkMarkCoords);
        checkMarkBuffer.position(0);
    }

    private void createTextureBuffers() {
        // 用于渲染纹理的矩形
        float[] textureVertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                -0.5f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.0f
        };

        ByteBuffer bb = ByteBuffer.allocateDirect(textureVertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        textureVertexBuffer = bb.asFloatBuffer();
        textureVertexBuffer.put(textureVertices);
        textureVertexBuffer.position(0);

        // 纹理坐标
        float[] textureCoords = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f
        };

        bb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        textureCoordBuffer = bb.asFloatBuffer();
        textureCoordBuffer.put(textureCoords);
        textureCoordBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // 气泡着色器程序
        String vertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 vPosition;" +
                        "void main() {" +
                        "  gl_Position = uMVPMatrix * vPosition;" +
                        "}";
        String fragmentShaderCode =
                "precision mediump float;" +
                        "uniform vec4 vColor;" +
                        "void main() {" +
                        "  gl_FragColor = vColor;" +
                        "}";

        // 文字纹理着色器程序
        String textVertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 aPosition;" +
                        "attribute vec2 aTextureCoord;" +
                        "varying vec2 vTextureCoord;" +
                        "void main() {" +
                        "  gl_Position = uMVPMatrix * aPosition;" +
                        "  vTextureCoord = aTextureCoord;" +
                        "}";
        String textFragmentShaderCode =
                "precision mediump float;" +
                        "varying vec2 vTextureCoord;" +
                        "uniform sampler2D sTexture;" +
                        "void main() {" +
                        "  gl_FragColor = texture2D(sTexture, vTextureCoord);" +
                        "}";

        // 编译着色器程序
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        int textVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, textVertexShaderCode);
        int textFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, textFragmentShaderCode);

        // 创建气泡程序
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        // 创建文字纹理程序
        textProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(textProgram, textVertexShader);
        GLES20.glAttachShader(textProgram, textFragmentShader);
        GLES20.glLinkProgram(textProgram);

        // 为每个气泡创建文字纹理
        for (Bubble bubble : bubbles) {
            createTextTexture(bubble.label);
        }
    }

    private void createTextTexture(String text) {
        if (textureMap.containsKey(text)) {
            return; // 已经创建过该文字的纹理
        }

        Paint textPaint = new Paint();
        textPaint.setTextSize(50);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        // 使用 FontMetrics 调整基线，使文字垂直居中
        Paint.FontMetrics fm = textPaint.getFontMetrics();

        int width = 128;
        int height = 64;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        float baseLine = (height - fm.bottom - fm.top) / 2;
        canvas.drawText(text, width / 2f, baseLine, textPaint);

        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();

        textureMap.put(text, textureHandle[0]);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        float time = (System.currentTimeMillis() - startTime) / 1000.0f;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        for (Bubble bubble : bubbles) {
            drawBubble(bubble, time);
            drawText(bubble, time);
            if (bubble.selected) {
                // 使用当前动画矩阵进行绘制
                drawCheckMark(bubble, getCurrentMvpMatrix(bubble, time));
            }
        }
    }

    private float[] getCurrentMvpMatrix(Bubble bubble, float time) {
        float animRadius = bubble.getAnimatedRadius(time);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, bubble.x, bubble.y, 0);
        Matrix.scaleM(modelMatrix, 0, animRadius, animRadius, 1.0f);
        float[] currentMvpMatrix = new float[16];
        Matrix.multiplyMM(currentMvpMatrix, 0, this.mvpMatrix, 0, modelMatrix, 0);
        return currentMvpMatrix;
    }

    private void drawBubble(Bubble bubble, float time) {
        GLES20.glUseProgram(program);
        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        int colorHandle = GLES20.glGetUniformLocation(program, "vColor");
        int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");

        GLES20.glEnableVertexAttribArray(positionHandle);

        float animRadius = bubble.getAnimatedRadius(time);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, bubble.x, bubble.y, 0);
        Matrix.scaleM(modelMatrix, 0, animRadius, animRadius, 1.0f);
        float[] currentMvpMatrix = new float[16];
        Matrix.multiplyMM(currentMvpMatrix, 0, this.mvpMatrix, 0, modelMatrix, 0);

        GLES20.glUniform4fv(colorHandle, 1, bubble.color, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, currentMvpMatrix, 0);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, circleVertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, SEGMENTS + 2);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private void drawCheckMark(Bubble bubble, float[] mvpMatrix) {
        float[] checkMarkColor = {1.0f, 1.0f, 1.0f, 1.0f};

        float scale = bubble.radius * 0.5f;
        float[] vertices = {
                -0.3f * scale, 0.0f, 0.0f,
                -0.1f * scale, -0.2f * scale, 0.0f,
                0.3f * scale, 0.2f * scale, 0.0f
        };

        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        int colorHandle = GLES20.glGetUniformLocation(program, "vColor");
        int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        GLES20.glUniform4fv(colorHandle, 1, checkMarkColor, 0);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glLineWidth(5.0f);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 3);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private void drawText(Bubble bubble, float time) {
        GLES20.glUseProgram(textProgram);
        int positionHandle = GLES20.glGetAttribLocation(textProgram, "aPosition");
        int textureCoordHandle = GLES20.glGetAttribLocation(textProgram, "aTextureCoord");
        int mvpMatrixHandle = GLES20.glGetUniformLocation(textProgram, "uMVPMatrix");
        int textureHandle = GLES20.glGetUniformLocation(textProgram, "sTexture");

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(textureCoordHandle);

        float textScale = bubble.radius * 0.8f;
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, bubble.x, bubble.y, 0);
        Matrix.scaleM(modelMatrix, 0, textScale, textScale / 2, 1.0f);
        float[] currentMvpMatrix = new float[16];
        Matrix.multiplyMM(currentMvpMatrix, 0, this.mvpMatrix, 0, modelMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureMap.get(bubble.label));
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, currentMvpMatrix, 0);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, textureVertexBuffer);
        GLES20.glVertexAttribPointer(textureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureCoordBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(textureCoordHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1, 1, -1, 1);
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }

    // 修改 handleTouchEvent，使用当前动画半径作为检测依据
    public boolean handleTouchEvent(float normalizedX, float normalizedY) {
        for (Bubble bubble : bubbles) {
            float dx = normalizedX - bubble.x;
            float dy = normalizedY - bubble.y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            // 使用原始半径作为检测区域（可根据需要调整一个很小的容差，比如1.0f）
            if (distance <= bubble.radius) {
                bubble.selected = !bubble.selected;
                if (bubble.selected) {
                    for (int i = 0; i < 3; i++) {
                        bubble.color[i] = Math.min(1.0f, bubble.color[i] * 1.3f);
                    }
                    bubble.color[3] = 1.0f;
                } else {
                    for (int i = 0; i < 3; i++) {
                        bubble.color[i] = Math.max(0.0f, bubble.color[i] / 1.3f);
                    }
                    bubble.color[3] = 0.9f;
                }
                return true;
            }
        }
        return false;
    }

    public List<String> getSelectedDomains() {
        List<String> selectedDomains = new ArrayList<>();
        for (Bubble bubble : bubbles) {
            if (bubble.selected) {
                selectedDomains.add(bubble.label);
            }
        }
        return selectedDomains;
    }
}
