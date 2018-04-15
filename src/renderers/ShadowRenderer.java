package renderers;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import utils.ShaderUtils;

public class ShadowRenderer implements IRenderer {

    private int shadowFrameBufferObject;

    @Override
    public void render() {
        GL20.glUseProgram(ShaderUtils.getShadowShaderProgram());

        //TODO render shadows
    }

    private void initFrameBufferObject() {
        shadowFrameBufferObject = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_RENDERBUFFER, shadowFrameBufferObject);

        //TODO to something

        GL30.glBindFramebuffer(GL30.GL_RENDERBUFFER, 0);
    }
}
