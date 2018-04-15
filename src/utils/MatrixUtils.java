package utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

public final class MatrixUtils {

    private static final Queue<Matrix4f> matrixStack = new ArrayDeque<>();
    private static final Matrix4f projectionMatrix = new Matrix4f();
    private static final Matrix4f identitiedMatrix = new Matrix4f();
    private static final FloatBuffer projectionMatrixBuffer = BufferUtils.createFloatBuffer(16);
    private static final FloatBuffer modelViewMatrixBuffer = BufferUtils.createFloatBuffer(16);

    public static void setPerspective(float fov, float aspect, float near, float far) {
        final float ratio = 1f / (float) Math.tan(Math.toRadians(fov / 2f));
        final float yScale = ratio * aspect;
        final float xScale = ratio;
        final float frustum = far - near;

        Matrix4f.setZero(projectionMatrix);

        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = -(far + near) / frustum;
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -(2 * near * far) / frustum;
    }

    public static void setOrtho(float left, float right, float bottom, float top, float near, float far) {
        final float yScale = 2f / (right - left);
        final float xScale = 2f / (top - bottom);
        final float frustum = -2f / (far - near);

        Matrix4f.setZero(projectionMatrix);

        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = frustum;
        projectionMatrix.m30 = -(right + left) / (right - left);
        projectionMatrix.m31 = -(top + bottom) / (top - bottom);
        projectionMatrix.m32 = -(far + near) / (far - near);
        projectionMatrix.m33 = 1;
    }

    public static void pushMatrix() {
        Matrix4f mat = new Matrix4f();
        Matrix4f.setIdentity(mat);
        if (!matrixStack.isEmpty()) mat = getMatrixInStack();

        addMatrixInStack(mat);
    }

    public static void popMatrix() {
        matrixStack.poll();
    }

    public static void translate(float i, float j, float k) {
        getMatrixInStack().translate(VectorUtils.create(i, j, k));
    }

    public static void rotate(float a, float i, float j, float k) {
        getMatrixInStack().rotate(a, VectorUtils.create(i, j, k));
    }

    public static void scale(float i, float j, float k) {
        getMatrixInStack().scale(VectorUtils.create(i, j, k));
    }

    public static FloatBuffer getProjectionMatrixAsBuffer() {
        projectionMatrixBuffer.clear();
        projectionMatrix.store(projectionMatrixBuffer);
        projectionMatrixBuffer.flip();
        return projectionMatrixBuffer;
    }

    public static FloatBuffer getModelViewMatrixAsBuffer() {
        modelViewMatrixBuffer.clear();
        Matrix4f mat = identitiedMatrix;
        Matrix4f.setIdentity(mat);
        if (!matrixStack.isEmpty()) mat = getMatrixInStack();

        mat.store(modelViewMatrixBuffer);
        modelViewMatrixBuffer.flip();
        return modelViewMatrixBuffer;
    }

    private static void addMatrixInStack(Matrix4f mat) {
        matrixStack.add(mat);
    }

    private static Matrix4f getMatrixInStack() {
        return matrixStack.peek();
    }
}
