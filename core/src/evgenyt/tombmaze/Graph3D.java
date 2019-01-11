package evgenyt.tombmaze;

import com.badlogic.gdx.math.Vector3;

/***

    Parameters of 3D graphics

 */

class Graph3D {

    // Camera settings
    static final float CAMERA_NEAR = 0.1f;
    static final float CAMERA_FAR = 300;
    static final Vector3 CAMERA_POS_INIT = new Vector3(-10, -10, 0);
    static final Vector3 CAMERA_LOOK_INIT_AT = new Vector3(0, 0, 0);
    static final float CAMERA_VIEW_ANGLE = 75;

    // Wall settings
    static final float WALL_WIDTH = 1;
    static final float WALL_HEIGHT = 1;
    static final float WALL_DEPTH = 1;
    static final float WALL_BOUNDS_PLUS = 0.6f;

}
