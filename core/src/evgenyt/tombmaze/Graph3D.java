package evgenyt.tombmaze;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/***

    Parameters of 3D graphics

 */

class Graph3D {

    // Camera settings
    static final float CAMERA_NEAR = 0.1f;
    static final float CAMERA_FAR = 300;
    static final Vector3 CAMERA_POS_INIT = new Vector3(-0, -3, 0);
    static final Vector3 CAMERA_LOOK_INIT_AT = new Vector3(0, 0, 0);
    static final float CAMERA_VIEW_ANGLE = 75;

    // Wall settings
    static final float WALL_WIDTH = 1;
    static final float WALL_HEIGHT = 1;
    static final float WALL_DEPTH = 1;
    static final float WALL_BOUNDS_PLUS = 0.6f;

    // Detects collision of set of objects with position
    static boolean collision (ArrayList<ModelInstance> walls, Vector3 position) {
        for (ModelInstance wall : walls) {
            Rectangle bounds = (Rectangle) wall.userData;
            if (bounds.contains(position.x, position.y))
                return true;
        }
        return false;
    }
}
