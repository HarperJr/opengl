package geometry;

import geometry.loaders.Material;
import geometry.loaders.MeshLoader;
import geometry.loaders.MeshLoaderWaveFront;


public class MeshFactory implements IMeshFactory {

    private MeshLoader waveFrontLoader = new MeshLoaderWaveFront();

    @Override
    public Mesh create(String name) {
        if (name.equals("")) throw new IllegalStateException("Mesh name is empty!");

        MeshLoader loader;
        Mesh mesh = new Mesh(name);

        if (name.endsWith(".obj")) {
            loader = waveFrontLoader;
        } else throw new IllegalStateException("Unable to load mesh: unsupported format!");

        loader.loadMesh(name);

        Material mat = loader.getMaterial();
        if (mat == null) mat = new Material("material " + name);

        mesh.addComponent(mat);
        mesh.initialize(loader.getVerticesBuffer(), loader.getIndicesBuffer());

        return mesh;
    }
}
