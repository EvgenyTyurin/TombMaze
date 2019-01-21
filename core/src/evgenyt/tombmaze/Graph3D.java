package evgenyt.tombmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**

    Parameters of 3D graphics

 */

class Graph3D {

    /** Camera settings */
    private static final float CAMERA_NEAR = 0.1f;
    private static final float CAMERA_FAR = 300;
    private static final Vector3 CAMERA_POS_INIT = new Vector3(1.5f, 1.5f, 0.5f);
    private static final Vector3 CAMERA_LOOK_INIT_AT = new Vector3(2, 2, 0.5f);
    private static final float CAMERA_VIEW_ANGLE = 75;

    /** Wall settings */
    static final float WALL_WIDTH = 1;
    static final float WALL_HEIGHT = 1;
    static final float WALL_DEPTH = 1;
    static final float WALL_BOUNDS_PLUS = 0.1f;

    /** @return true if detects collision of 2d bounds of objects with position (x,y,0) */
    static boolean collisionFlour(ArrayList<ModelInstance> walls, Vector3 position) {
        for (ModelInstance wall : walls) {
            Rectangle bounds = (Rectangle) wall.userData;
            if (bounds.contains(position.x, position.y))
                return true;
        }
        return false;
    }

    /** @return New player camera */
    static PerspectiveCamera getPlayerCamera() {
        PerspectiveCamera camera = new PerspectiveCamera(CAMERA_VIEW_ANGLE,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(CAMERA_POS_INIT);
        camera.rotate(90, 1, 0, 0);
        camera.lookAt(CAMERA_LOOK_INIT_AT);
        camera.near = CAMERA_NEAR;
        camera.far = CAMERA_FAR;
        return camera;
    }

}
