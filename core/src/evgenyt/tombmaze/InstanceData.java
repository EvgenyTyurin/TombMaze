package evgenyt.tombmaze;

import com.badlogic.gdx.math.Rectangle;

/**
 * Additional data for 3D object
 */

class InstanceData {

    private final ObjType objType;
    private Rectangle bounds2D;
    private float speedZ = 0;

    InstanceData(ObjType objType, Rectangle bounds2D) {
        this.bounds2D = bounds2D;
        this.objType = objType;
    }

    Rectangle getBounds2D() {
        return bounds2D;
    }

    public ObjType getObjType() {
        return objType;
    }

    public float getSpeedZ() {
        return speedZ;
    }

    public void setSpeedZ(float speedZ) {
        this.speedZ = speedZ;
    }
}
