package geometry;

import geometry.loaders.Material;
import renderers.MeshRenderer;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

    private final String name;

    protected MeshRenderer meshRenderer;
    protected Material material;

    private int renderMode;
    private int vertexArrayIndex;

    private int indicesCount;

    public Mesh(String n) {
        name = n;
        meshRenderer = new MeshRenderer(this);
        renderMode = GL11.GL_TRIANGLES;
    }

    public void initialize(FloatBuffer buffer, IntBuffer indices) {

        indicesCount = indices.capacity();
        vertexArrayIndex = meshRenderer.initVertexArrayObject(buffer, indices);
    }

    public void addComponent(Object comp) {
        if (comp instanceof Material) material = (Material) comp;
    }

    public int getVertexArrayIndex() {
        return vertexArrayIndex;
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    public Material getMaterial() {
        return material;
    }

    public MeshRenderer getRenderer() {
        return meshRenderer;
    }

    public int getRenderMode() {
        return renderMode;
    }

    public String getName() {
        return name;
    }

}
