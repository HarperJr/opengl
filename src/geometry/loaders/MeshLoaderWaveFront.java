package geometry.loaders;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector2f;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MeshLoaderWaveFront extends MeshLoader {

    private static final String VERTEX_PREFIX = "v ";
    private static final String TEXCOORDS_PREFIX = "vt ";
    private static final String NORMAL_PREFIX = "vn ";
    private static final String FACE_PREFIX = "f ";
    private static final String NEW_MATERIAL_PREFIX = "newmtl ";
    private static final String MATERIAL_PREFIX = "mtllib ";

    private static final String DISSOLVE_FACTOR_PREFIX = "d ";
    private static final String SPECULAR_FACTOR_PREFIX = "Ns ";
    private static final String COLOR_AMBIENT_PREFIX = "Ka ";
    private static final String COLOR_DIFFUSE_PREFIX = "Kd ";
    private static final String COLOR_SPECULAR_PREFIX = "Ks ";
    private static final String COLOR_EMISSIVE_PREFIX = "Ke ";

    private static final String MAP_AMBIENT_PREFIX = "map_Ka ";
    private static final String MAP_DIFFUSE_PREFIX = "map_Kd ";
    private static final String MAP_SPECULAR_PREFIX = "map_Ks ";

    @Override
    protected void processObjectLine(String line) throws IllegalArgumentException {
        process(line, VERTEX_PREFIX, mdf -> {
            Vector3f vertex = (Vector3f) toVector(mdf.substring(2).trim());
            processedVertices.add(vertex);
        });

        process(line, TEXCOORDS_PREFIX, mdf -> {
            Vector2f texCoord = (Vector2f) toVector(mdf.substring(3).trim());
            processedTextureCoords.add(texCoord);
        });

        process(line, NORMAL_PREFIX, mdf -> {
            Vector3f normal = (Vector3f) toVector(mdf.substring(3).trim());
            processedNormals.add(normal);
        });

        process(line, FACE_PREFIX, mdf -> {
            List<String> face = Arrays.stream(mdf.substring(2).trim().split(" ")).collect(Collectors.toList());
            if (face.size() > 3) triangulatePolygon(face);
            face.forEach(f -> {
                if (!f.contains("/")) processedIndices.add(Integer.parseInt(f) - 1);
                else Arrays.stream(f.split("/")).mapToInt(i -> Integer.parseInt(i) - 1).forEach(processedIndices::add);
            });
        });

        process(line, MATERIAL_PREFIX, mdf -> loadMaterial(mdf.substring(7).trim()));
    }

    @Override
    protected void processMaterialLine(String line) throws IllegalArgumentException {
        process(line, SPECULAR_FACTOR_PREFIX, mdf -> getMaterial().setSpecularFactor(Float.parseFloat(mdf.substring(3).trim())));

        process(line, DISSOLVE_FACTOR_PREFIX, mdf -> getMaterial().setDissolveFactor(Float.parseFloat(mdf.substring(2).trim())));

        process(line, COLOR_AMBIENT_PREFIX, mdf -> getMaterial().setAmbientColor((Vector3f) toVector(mdf.substring(3).trim())));

        process(line, COLOR_DIFFUSE_PREFIX, mdf -> getMaterial().setDiffuseColor((Vector3f) toVector(mdf.substring(3).trim())));

        process(line, COLOR_SPECULAR_PREFIX, mdf -> getMaterial().setSpecularColor((Vector3f) toVector(mdf.substring(3).trim())));

        process(line, COLOR_EMISSIVE_PREFIX, mdf -> getMaterial().setEmissiveColor((Vector3f) toVector(mdf.substring(3).trim())));

        process(line, MAP_AMBIENT_PREFIX, mdf -> getMaterial().setAmbientTextureMap(mdf.substring(7).trim()));

        process(line, MAP_DIFFUSE_PREFIX, mdf -> getMaterial().setDiffuseTextureMap(mdf.substring(7).trim()));

        process(line, MAP_SPECULAR_PREFIX, mdf -> getMaterial().setSpecularTextureMap(mdf.substring(7).trim()));

        process(line, NEW_MATERIAL_PREFIX, mdf -> materials.add(new Material(mdf.substring(7).trim())));
    }

}
