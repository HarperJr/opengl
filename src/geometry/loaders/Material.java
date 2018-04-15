package geometry.loaders;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import utils.TextureUtils;
import utils.VectorUtils;

import java.nio.FloatBuffer;

public class Material {

    private static final Vector3f whiteSolid = VectorUtils.create(1f, 1f, 1f);
    private static final Vector3f blackSolid = VectorUtils.create(0f, 0f, 0f);

    private final String name;

    private final FloatBuffer materialBuffer;

    private float specularFactor;
    private float dissolveFactor;

    private Vector3f ambientColor;
    private Vector3f diffuseColor;
    private Vector3f specularColor;
    private Vector3f emissiveColor;

    private int ambientTextureMap;
    private int diffuseTextureMap;
    private int specularTextureMap;

    public Material(String n) {
        name = n;

        materialBuffer = BufferUtils.createFloatBuffer(16);

        specularFactor = 1f;
        ambientColor = whiteSolid;
        diffuseColor = whiteSolid;
        specularColor = blackSolid;
        emissiveColor = blackSolid;

        ambientTextureMap = TextureUtils.getMissingTexture();
        diffuseTextureMap = TextureUtils.getMissingTexture();
        specularTextureMap = TextureUtils.getMissingTexture();
    }

    public void setSpecularFactor(float f) {
        specularFactor = f;
    }

    public void setDissolveFactor(float f) {
        dissolveFactor = f;
    }

    public void setAmbientColor(Vector3f ambient) {
        ambientColor = ambient;
    }

    public void setDiffuseColor(Vector3f diffuse) {
        diffuseColor = diffuse;
    }

    public void setSpecularColor(Vector3f specular) {
        specularColor = specular;
    }

    public void setEmissiveColor(Vector3f emissive) {
        emissiveColor = emissive;
    }

    public void setAmbientTextureMap(String path) {
        ambientTextureMap = TextureUtils.loadTexture(path);
    }

    public void setDiffuseTextureMap(String path) {
        diffuseTextureMap = TextureUtils.loadTexture(path);
    }

    public void setSpecularTextureMap(String path) {
        specularTextureMap = TextureUtils.loadTexture(path);
    }

    public FloatBuffer getMaterialAsBuffer() {
        materialBuffer.clear();
        float[] materialTable = new float[]{
                ambientColor.x, ambientColor.y, ambientColor.z, dissolveFactor,
                diffuseColor.x, diffuseColor.y, diffuseColor.z, 1.0f,
                specularColor.x, specularColor.y, specularColor.z, specularFactor,
                emissiveColor.x, emissiveColor.y, emissiveColor.z, 1.0f
        };
        materialBuffer.put(materialTable);
        materialBuffer.flip();
        return materialBuffer;
    }

    public int getAmbientTextureMap() {
        return ambientTextureMap;
    }

    public int getDiffuseTextureMap() {
        return diffuseTextureMap;
    }

    public int getSpecularTextureMap() {
        return specularTextureMap;
    }

    public String getName() {
        return name;
    }
}
