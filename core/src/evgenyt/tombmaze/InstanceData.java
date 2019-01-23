package evgenyt.tombmaze;

import com.badlogic.gdx.math.Rectangle;

/**
 * Additional data for 3D object
 */

class InstanceData {

    private final ObjType objType;
    private final Rectangle bounds2D;

    InstanceData(ObjType objType, Rectangle bounds2D) {
        this.bounds2D = bounds2D;
        this.objType = objType;
    }

    Rectangle getBounds2D() {
        return bounds2D;
    }

}
