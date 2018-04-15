package geometry.loaders;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import utils.VectorUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class MeshLoader {

    private static final int VECTOR2F_COORDS = 2;
    private static final int VECTOR3F_COORDS = 3;

    private static final int VERTEX_INDEX_OFFSET = 0;
    private static final int TEXTURE_COORDS_INDEX_OFFSET = 1;
    private static final int NORMAL_INDEX_OFFSET = 2;

    private static final int BUFFER_STRIDE = 8;
    private static final int VERTEX_BUFFER_OFFSET = 0;
    private static final int TEXTURE_COORDS_BUFFER_OFFSET = 3;
    private static final int NORMAL_BUFFER_OFFSET = 5;

    private boolean excludeTextureCoords;
    private boolean excludeNormals;

    protected static final String sourceFolder = "meshes/";
    protected static final Queue<Material> materials = new ArrayDeque<>();

    final List<Vector3f> processedVertices = new ArrayList<>();
    final List<Vector2f> processedTextureCoords = new ArrayList<>();
    final List<Vector3f> processedNormals = new ArrayList<>();
    final List<Integer> processedIndices = new ArrayList<>();

    private final Queue<Integer> indices = new ArrayDeque<>();

    private float[] vertexBufferArray;

    protected abstract void processObjectLine(String line) throws IllegalArgumentException;

    protected abstract void processMaterialLine(String line) throws IllegalArgumentException;

    public Material getMaterial() {
        return materials.peek();
    }

    public void loadMesh(String path) throws RuntimeException {
        final String sourcePath = sourceFolder + path;
        InputStream stream = MeshLoader.class.getResourceAsStream(sourcePath);
        if (stream == null) throw new IllegalStateException("Unable to find object file " + sourcePath);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String line;
            while ((line = bufferedReader.readLine()) != null)
                try {
                    processObjectLine(line);
                } catch (IllegalArgumentException illegalArgumentEx) {
                    throw new IllegalStateException("Unable to load object file " + sourcePath);
                }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        excludeTextureCoords = processedTextureCoords.isEmpty();
        excludeNormals = processedNormals.isEmpty();

        loadProcessedData();
        clear();
    }

    protected void loadMaterial(String path) throws RuntimeException {
        final String sourcePath = sourceFolder + path;

        InputStream stream = MeshLoader.class.getResourceAsStream(sourcePath);
        if (stream == null) return;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                processMaterialLine(line);
            }

        } catch (IllegalArgumentException illegalArgumentEx) {
            throw new IllegalStateException("Unable to load material file" + sourcePath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadProcessedData() {
        final int stride = 1 + (excludeTextureCoords ? 0 : 1) + (excludeNormals ? 0 : 1);

        vertexBufferArray = new float[processedVertices.size() * BUFFER_STRIDE];

        Stream.iterate(0, i -> i < processedIndices.size(), i -> i += stride).forEach(i -> {
            int vertexIndex = processedIndices.get(i + VERTEX_INDEX_OFFSET);
            if (vertexIndex > processedVertices.size()) vertexIndex -= processedVertices.size();

            addIndex(vertexIndex);

            if (isVertexUnused(vertexIndex)) {

                addVertex(vertexIndex);

                int texCoordIndex = processedIndices.get(i + TEXTURE_COORDS_INDEX_OFFSET);
                if (texCoordIndex > processedTextureCoords.size()) texCoordIndex -= processedTextureCoords.size();
                addTexCoord(vertexIndex, texCoordIndex);

                int normalIndex = processedIndices.get(i + NORMAL_INDEX_OFFSET);
                if (normalIndex > processedNormals.size()) normalIndex -= processedNormals.size();
                addNormal(vertexIndex, normalIndex);
            }
        });
    }

    public IntBuffer getIndicesBuffer() {
        IntBuffer indicesBuffer = BufferUtils.createIntBuffer(indices.size());
        indices.forEach(indicesBuffer::put);
        indicesBuffer.flip();
        indices.clear();
        return indicesBuffer;
    }

    public FloatBuffer getVerticesBuffer() {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(vertexBufferArray.length);
        floatBuffer.put(vertexBufferArray);
        floatBuffer.flip();
        return floatBuffer;
    }

    protected void process(String line, String prefix, Consumer<String> action) {
        try {
            if (line.startsWith(prefix)) action.accept(line);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected Vector toVector(String meshDataFragment) {
        try {
            var vecData = Arrays.stream(meshDataFragment.trim().split(" ")).map(Float::parseFloat).toArray();
            if (vecData.length == VECTOR2F_COORDS) {
                return VectorUtils.create((float) vecData[0], (float) vecData[1]);
            }
            if (vecData.length == VECTOR3F_COORDS) {
                return VectorUtils.create((float) vecData[0], (float) vecData[1], (float) vecData[2]);
            }
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsEx) {
            arrayIndexOutOfBoundsEx.printStackTrace();
        }
        return VectorUtils.create(0f, 0f, 0f);
    }

    protected void triangulatePolygon(List<String> face) {
        //TODO have to triangulate n polygons even with gaps
    }

    private boolean isVertexUnused(int i) {
        return processedVertices.get(i) != null;
    }

    private void addIndex(int i) {
        indices.add(i);
    }

    private void addVertex(int vertexIndex) {
        Vector3f vertex = processedVertices.get(vertexIndex);
        vertexBufferArray[vertexIndex * BUFFER_STRIDE + VERTEX_BUFFER_OFFSET] = vertex.x;
        vertexBufferArray[vertexIndex * BUFFER_STRIDE + VERTEX_BUFFER_OFFSET + 1] = vertex.y;
        vertexBufferArray[vertexIndex * BUFFER_STRIDE + VERTEX_BUFFER_OFFSET + 2] = vertex.z;
        processedVertices.set(vertexIndex, null);
    }

    private void addTexCoord(int vertexIndex, int textureCoordIndex) {
        if (excludeTextureCoords) {
            for (int i = 0; i < 2; i++) {
                vertexBufferArray[vertexIndex * BUFFER_STRIDE + TEXTURE_COORDS_BUFFER_OFFSET + i] = 0.0f;
            }
        }
        Vector2f texCoord = processedTextureCoords.get(textureCoordIndex);
        vertexBufferArray[vertexIndex * BUFFER_STRIDE + TEXTURE_COORDS_BUFFER_OFFSET] = texCoord.x;
        vertexBufferArray[vertexIndex * BUFFER_STRIDE + TEXTURE_COORDS_BUFFER_OFFSET + 1] = texCoord.y;
    }

    private void addNormal(int vertexIndex, int normalIndex) {
        if (excludeNormals) {
            for (int i = 0; i < 3; i++) {
                vertexBufferArray[vertexIndex * BUFFER_STRIDE + NORMAL_BUFFER_OFFSET + i] = 0.0f;
            }
        }
        Vector3f normal = processedNormals.get(normalIndex);
        vertexBufferArray[vertexIndex * BUFFER_STRIDE + NORMAL_BUFFER_OFFSET] = normal.x;
        vertexBufferArray[vertexIndex * BUFFER_STRIDE + NORMAL_BUFFER_OFFSET + 1] = normal.y;
        vertexBufferArray[vertexIndex * BUFFER_STRIDE + NORMAL_BUFFER_OFFSET + 2] = normal.z;
    }

    private void clear() {
        processedVertices.clear();
        processedTextureCoords.clear();
        processedNormals.clear();
        processedIndices.clear();
    }

}
