package utils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class ShaderUtils {

    public static final int POSITION_ATTRIBUTE = 0;
    public static final int TEXTURE_COORD_ATTRIBUTE = 1;
    public static final int NORMAL_ATTRIBUTE = 2;

    private static final int LOG_INFO_LENGTH = 512;

    private static final String SHADERS_SOURCE_FOLDER = "shaders/";
    private static final String VERTEX_SHADER_PATH = SHADERS_SOURCE_FOLDER + "vertexShader.glsl";
    private static final String FRAG_SHADER_PATH = SHADERS_SOURCE_FOLDER + "fragmentShader.glsl";

    private static final String SHADOW_VERTEX_SHADER_PATH = SHADERS_SOURCE_FOLDER + "shadowVertexShader.glsl";
    private static final String SHADOW_FRAG_SHADER_PATH = SHADERS_SOURCE_FOLDER + "shadowFragmentShader.glsl";

    public static int PROJECTION_MATRIX_UNIFORM;
    public static int MODELVIEW_MATRIX_UNIFORM;
    public static int SAMPLER_TEXTURE_AMBIENT_UNIFORM;
    public static int SAMPLER_TEXTURE_DIFFUSE_UNIFORM;
    public static int SAMPLER_TEXTURE_SPECULAR_UNIFORM;

    public static int MATERIAL_TABLE_UNIFORM;

    private static int shaderProgram;
    private static int shadowShaderProgram;

    public static void initializeShaderProgram() {

        final int vertexShader = createShader(GL20.GL_VERTEX_SHADER, getShaderSourceFromPath(VERTEX_SHADER_PATH));
        final int fragShader = createShader(GL20.GL_FRAGMENT_SHADER, getShaderSourceFromPath(FRAG_SHADER_PATH));

        shaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shaderProgram, vertexShader);
        GL20.glAttachShader(shaderProgram, fragShader);

        GL20.glBindAttribLocation(shaderProgram, POSITION_ATTRIBUTE, "a_vertex.position");
        GL20.glBindAttribLocation(shaderProgram, TEXTURE_COORD_ATTRIBUTE, "a_vertex.texcoord");
        GL20.glBindAttribLocation(shaderProgram, NORMAL_ATTRIBUTE, "a_vertex.normal");

        GL20.glLinkProgram(shaderProgram);

        int status = GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS);
        if (status == GL11.GL_FALSE) {
            String info = GL20.glGetProgramInfoLog(shaderProgram, LOG_INFO_LENGTH);
            GL20.glDeleteProgram(shaderProgram);
            throw new IllegalStateException("Unable to link program: " + info);
        }

        PROJECTION_MATRIX_UNIFORM = getUniform(shaderProgram, "u_matrices.p_matrix");
        MODELVIEW_MATRIX_UNIFORM = getUniform(shaderProgram, "u_matrices.mv_matrix");
        SAMPLER_TEXTURE_AMBIENT_UNIFORM = getUniform(shaderProgram, "u_textures.ambient");
        SAMPLER_TEXTURE_DIFFUSE_UNIFORM = getUniform(shaderProgram, "u_textures.diffuse");
        SAMPLER_TEXTURE_SPECULAR_UNIFORM = getUniform(shaderProgram, "u_textures.specular");

        MATERIAL_TABLE_UNIFORM = getUniform(shaderProgram, "u_material_table");

        final int shadowVertexShader = createShader(GL20.GL_VERTEX_SHADER, getShaderSourceFromPath(SHADOW_VERTEX_SHADER_PATH));
        final int shadowFragShader = createShader(GL20.GL_FRAGMENT_SHADER, getShaderSourceFromPath(SHADOW_FRAG_SHADER_PATH));

        shadowShaderProgram = GL20.glCreateProgram();
        GL20.glAttachShader(shadowShaderProgram, shadowVertexShader);
        GL20.glAttachShader(shadowShaderProgram, shadowFragShader);

        GL20.glLinkProgram(shadowShaderProgram);
    }


    public static int getShaderProgram() {
        return shaderProgram;
    }

    public static int getShadowShaderProgram() {
        return shadowShaderProgram;
    }

    private static int getUniform(int program, CharSequence name) {
        return GL20.glGetUniformLocation(program, name);
    }

    private static int createShader(int shaderType, String shaderSource) {

        final int shader = GL20.glCreateShader(shaderType);
        GL20.glShaderSource(shader, shaderSource);
        GL20.glCompileShader(shader);

        int status = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
        if (status == GL11.GL_FALSE) {
            String info = GL20.glGetShaderInfoLog(shader, LOG_INFO_LENGTH);
            GL20.glDeleteShader(shader);
            throw new IllegalStateException("Unable to compile shader: " + info);
        }
        return shader;
    }

    private static String getShaderSourceFromPath(String path) {

        StringBuilder stringBuilder = new StringBuilder();
        InputStream stream = ShaderUtils.class.getResourceAsStream(path);
        if (stream == null) throw new IllegalStateException("Unable to find shader file!");

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("#")) {
                    stringBuilder.append(line).append("\n");
                    continue;
                }
                stringBuilder.append(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
