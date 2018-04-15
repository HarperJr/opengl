package renderers;

import geometry.Mesh;
import geometry.loaders.Material;
import org.lwjgl.opengl.*;
import utils.MatrixUtils;
import utils.ShaderUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MeshRenderer implements IRenderer {

    private final Mesh mesh;

    public MeshRenderer(Mesh m) {
        if (m == null) throw new IllegalStateException("Unable to get mesh reference " + m.getName());
        mesh = m;
    }

    @Override
    public void render() {
        GL20.glUseProgram(ShaderUtils.getShaderProgram());

        GL30.glBindVertexArray(mesh.getVertexArrayIndex());

        GL20.glUniformMatrix4(ShaderUtils.PROJECTION_MATRIX_UNIFORM, false, MatrixUtils.getProjectionMatrixAsBuffer());
        GL20.glUniformMatrix4(ShaderUtils.MODELVIEW_MATRIX_UNIFORM, false, MatrixUtils.getModelViewMatrixAsBuffer());

        Material meshMaterial = mesh.getMaterial();

        GL20.glUniformMatrix4(ShaderUtils.MATERIAL_TABLE_UNIFORM, false, meshMaterial.getMaterialAsBuffer());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getMaterial().getAmbientTextureMap());
        GL20.glUniform1i(ShaderUtils.SAMPLER_TEXTURE_AMBIENT_UNIFORM, 0);

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getMaterial().getDiffuseTextureMap());
        GL20.glUniform1i(ShaderUtils.SAMPLER_TEXTURE_DIFFUSE_UNIFORM, 1);

        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getMaterial().getSpecularTextureMap());
        GL20.glUniform1i(ShaderUtils.SAMPLER_TEXTURE_SPECULAR_UNIFORM, 2);

        GL20.glEnableVertexAttribArray(ShaderUtils.POSITION_ATTRIBUTE);
        GL20.glEnableVertexAttribArray(ShaderUtils.TEXTURE_COORD_ATTRIBUTE);
        GL20.glEnableVertexAttribArray(ShaderUtils.NORMAL_ATTRIBUTE);

        GL11.glDrawElements(mesh.getRenderMode(), mesh.getIndicesCount(), GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(ShaderUtils.NORMAL_ATTRIBUTE);
        GL20.glDisableVertexAttribArray(ShaderUtils.TEXTURE_COORD_ATTRIBUTE);
        GL20.glDisableVertexAttribArray(ShaderUtils.POSITION_ATTRIBUTE);

        GL30.glBindVertexArray(0);
    }

    public int initVertexArrayObject(FloatBuffer buffer, IntBuffer indices) {

        int vertexArrayIndex = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vertexArrayIndex);

        int vertexBuffer = GL15.glGenBuffers();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffer);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(ShaderUtils.POSITION_ATTRIBUTE, 3, GL11.GL_FLOAT, false, 32, 0L);
        GL20.glVertexAttribPointer(ShaderUtils.TEXTURE_COORD_ATTRIBUTE, 2, GL11.GL_FLOAT, false, 32, 12L);
        GL20.glVertexAttribPointer(ShaderUtils.NORMAL_ATTRIBUTE, 3, GL11.GL_FLOAT, false, 32, 20L);

        int indicesBuffer = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        buffer.clear();
        indices.clear();

        return vertexArrayIndex;
    }
}
