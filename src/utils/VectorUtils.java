package utils;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayDeque;
import java.util.Queue;


public final class VectorUtils {

    private static final Queue<Vector2f> vectors2fList = new ArrayDeque<>();
    private static final Queue<Vector3f> vectors3fList = new ArrayDeque<>();

    public static void clear() {
        vectors2fList.clear();
        vectors3fList.clear();
    }

    public static Vector2f create(float i, float j) {
        Vector2f vec = new Vector2f(i, j);
        vectors2fList.add(vec);
        return vec;
    }

    public static Vector3f create(float i, float j, float k) {
        Vector3f vec = new Vector3f(i, j, k);
        vectors3fList.add(vec);
        return vec;
    }

    public static float getDistance(Vector3f from, Vector3f to) {
        Vector3f deltaVec = create(to.x - from.x, to.y - from.y, to.z - from.z);
        return deltaVec.length();
    }

    public static float getDistance(Vector2f from, Vector2f to) {
        Vector2f deltaVec = create(to.x - from.x, to.y - from.y);
        return deltaVec.length();
    }

    public static float getDotProduct(Vector3f vecV, Vector3f vecU) {
        return vecV.x * vecU.x + vecV.y * vecU.y + vecV.z * vecU.z;
    }

    public static Vector3f getCrossProduct(Vector3f vecV, Vector3f vecU) {
        return create(vecV.y * vecU.z - vecV.z * vecU.y, vecV.x * vecU.z + vecV.z * vecU.x, vecV.x * vecU.y - vecV.y * vecV.x);
    }

}
