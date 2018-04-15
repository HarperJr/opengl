package utils;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

public final class TextureUtils {

    private static final int RED_CHANNEL_OFFSET = 0;
    private static final int GREEN_CHANNEL_OFFSET = 1;
    private static final int BLUE_CHANNEL_OFFSET = 2;
    private static final int ALPHA_CHANNEL_OFFSET = 3;

    private static final BufferedImage missingTexture = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
    private static final HashMap<String, Integer> texturesMap = new HashMap<>();
    private static final String sourceFolder = "textures/";
    private static final String missingTextureName = "Missing.png";
    private static int texture;

    public static int getMissingTexture() {
        return loadTexture(missingTextureName);
    }

    public static int loadTexture(String path) {

        if (texturesMap.containsKey(path)) {
            return Integer.valueOf(texturesMap.get(path));
        }

        texture = GL11.glGenTextures();
        loadTexture(getImageFromPath(path), texture);
        texturesMap.put(path, texture);

        return texture;
    }

    private static void loadTexture(BufferedImage img, int index) {

        if (img == null) throw new IllegalStateException("Unable to load texture!");

        final int width = img.getWidth();
        final int height = img.getHeight();
        final byte[] pixelsBuffer = new byte[width * height * 4];
        final int[] pixels = new int[width * height];

        img.getRGB(0, 0, width, height, pixels, 0, width);

        for (int i = 0; i < pixels.length; i++) {
            int a = pixels[i] >> 24 & 0xff;
            int b = pixels[i] >> 16 & 0xff;
            int g = pixels[i] >> 8 & 0xff;
            int r = pixels[i] & 0xff;

            pixelsBuffer[i * 4 + RED_CHANNEL_OFFSET] = (byte) r;
            pixelsBuffer[i * 4 + GREEN_CHANNEL_OFFSET] = (byte) g;
            pixelsBuffer[i * 4 + BLUE_CHANNEL_OFFSET] = (byte) b;
            pixelsBuffer[i * 4 + ALPHA_CHANNEL_OFFSET] = (byte) a;
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, index);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(pixelsBuffer.length);
        byteBuffer.put(pixelsBuffer);
        byteBuffer.flip();
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, byteBuffer);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        byteBuffer.clear();
    }

    private static BufferedImage getImageFromPath(String path) {

        if (path.equals(missingTextureName)) {
            Graphics g = missingTexture.getGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, 16, 16);
            g.dispose();
            return missingTexture;
        }

        try (InputStream stream = TextureUtils.class.getResourceAsStream(sourceFolder + path)) {
            BufferedImage img;

            if (path.endsWith(".tga")) {
                img = TargaReader.read(stream);
            } else img = ImageIO.read(stream);

            return img;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load texture!" + path);
        }
    }

}
