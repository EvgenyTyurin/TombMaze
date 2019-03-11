package evgenyt.tombmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**
 *  Parameters and procedures of 3D graphics
*/

class Graph3D {

    /** Camera settings */
    private static final float CAMERA_NEAR = 0.1f;
    private static final float CAMERA_FAR = 300;
    private static final Vector3 CAMERA_POS_INIT = new Vector3(9.5f, 9.5f, 10.5f);
    private static final Vector3 CAMERA_POS_INIT2 = new Vector3(9.5f, 9.5f, 0.5f);
    static final Vector3 CAMERA_LOOK_INIT_AT = new Vector3(10.5f, 10.5f, 0.5f);
    private static final float CAMERA_VIEW_ANGLE = 75;
    static final float CAMERA_ROTATION_SPEED = 2f;
    static final float CAMERA_MOVE_SPEED = 0.05f;

    /** Maze objects settings */
    // private static final float WALL_WIDTH = 1;
    // private static final float WALL_HEIGHT = 1;
    private static final float WALL_DEPTH = 1;
    private static final float WALL_BOUNDS_PLUS = 0.1f;

    /** Textures */
    private static final String WALL_TEXTURE = "bricks.png";
    private static final String FLOOR_TEXTURE = "stones.png";
    private static final String ROOF_TEXTURE = "tiles.png";
    private static final String WOOD_TEXTURE = "wood.jpg";

    /** 3D models */
    private static final String KEY_OBJ = "key.obj";

    /** Doors settings */
    private static final float DOOR_WIDTH = 1f;
    private static final float DOOR_HEIGHT = 1f;
    private static final float DOOR_DEPTH = 1.f;
    private static final float DOOR_BOUNDS_PLUS = 0.1f;

    /** Key settings */
    private static final float KEY_WIDTH = 0.3f;
    private static final float KEY_HEIGHT = 0.3f;
    private static final float KEY_DEPTH = 0.3f;
    private static final float KEY_BOUNDS_PLUS = 0.1f;

    /** Prize settings */
    private static final float PRIZE_WIDTH = 0.5f;
    private static final float PRIZE_HEIGHT = 0.5f;
    private static final float PRIZE_DEPTH = 0.5f;
    private static final float PRIZE_BOUNDS_PLUS = 0.1f;

    /** 3D materials */
    private static Material wallMaterial = getMaterial(WALL_TEXTURE);
    private static Material floorMaterial = getMaterial(FLOOR_TEXTURE);
    private static Material roofMaterial = getMaterial(ROOF_TEXTURE);
    private static Material doorMaterial = getMaterial(WOOD_TEXTURE);
    private static Material blueMaterial =
            new Material(ColorAttribute.createDiffuse(Color.BLUE));

    /** Textures*/
    static Texture keyImageTexture = new Texture("keycard.png");

    /** @return  3D material by texture file (texture repeated)*/
    private static Material getMaterial(String textureFile){
        Texture texture = new Texture(textureFile);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureAttribute textureAttribute = TextureAttribute.createDiffuse(texture);
        return new Material(textureAttribute);
    }

    /** @return  model instance additional info */
    static InstanceData getInstanceData(ModelInstance instance) {
        if (instance.userData == null)
            return null;
        else
            return (InstanceData) instance.userData;
    }

    /** @return Type of maze object*/
    static ObjType getObjType(ModelInstance instance) {
        if (instance == null)
            return ObjType.NULL;
        InstanceData instanceData = (InstanceData) instance.userData;
        if (instanceData == null)
            return ObjType.NULL;
        else
            return instanceData.getObjType();
    }

    /** @return object in collection that 2D bounds collide with position */
    static ModelInstance collision2D(ArrayList<ModelInstance> objects3D, Vector3 position) {
        for (ModelInstance obj3D : objects3D) {
            if (collision2Dobject(obj3D, position))
                return obj3D;
        }
        return null;
    }

    /** @return true if 2D bounds of 3D object contains position */
    private static boolean collision2Dobject(ModelInstance object, Vector3 position) {
        InstanceData instanceData = (InstanceData) object.userData;
        if (instanceData == null)
            return false;
        Rectangle bounds = instanceData.getBounds2D();
        return bounds.contains(position.x, position.y);
    }

    /** @return New player falling camera */
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

    /** @return New player camera after falling */
    static PerspectiveCamera getPlayerCamera2() {
        PerspectiveCamera camera = new PerspectiveCamera(CAMERA_VIEW_ANGLE,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(CAMERA_POS_INIT2);
        camera.rotate(90, 1, 0, 0);
        camera.lookAt(CAMERA_LOOK_INIT_AT);
        camera.near = CAMERA_NEAR;
        camera.far = CAMERA_FAR;
        return camera;
    }

    /** @return floor 3D object */
    static ModelInstance buildFloor(float size) {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createRect(0, 0, 0f,
                size, 0, 0f,
                size, size, 0f,
                0, size, 0f,
                0f, 1f, 0f,
                floorMaterial, VertexAttributes.Usage.Position |
                        VertexAttributes.Usage.TextureCoordinates);
        // Trick to make texture repeat
        Matrix3 mat = new Matrix3();
        mat.scl(size);
        model.meshes.get(0).transformUV(mat);
        return new ModelInstance(model, 0f, 0f, 0f);
    }

    /** @return roof 3D object */
    static ModelInstance buildRoof(float size) {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createRect(0, 0, 1f,
                0, size, 1f,
                size, size, 1f,
                size, 0, 1f,
                0f, 1f, 0f,
                roofMaterial, VertexAttributes.Usage.Position |
                        VertexAttributes.Usage.TextureCoordinates);

        // Trick to make texture repeat
        Matrix3 mat = new Matrix3();
        mat.scl(size);
        model.meshes.get(0).transformUV(mat);
        return new ModelInstance(model, 0f, 0f, 0f);
    }

    /** @return 3D door object */
    static ModelInstance buildDoor(float x, float y) {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createBox(DOOR_WIDTH, DOOR_DEPTH, DOOR_HEIGHT, doorMaterial,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.TextureCoordinates);
        float doorX = x - DOOR_WIDTH / 2;
        float doorY = y - DOOR_DEPTH / 2;
        float doorZ = DOOR_HEIGHT / 2;
        ModelInstance modelInstance = new ModelInstance(model, doorX, doorY, doorZ);
        modelInstance.userData = new InstanceData(ObjType.DOOR,
                new Rectangle(x - DOOR_WIDTH - DOOR_BOUNDS_PLUS,
                        y - DOOR_DEPTH - DOOR_BOUNDS_PLUS,
                        DOOR_WIDTH + DOOR_BOUNDS_PLUS * 2,
                        DOOR_DEPTH + DOOR_BOUNDS_PLUS * 2));
        return modelInstance;
    }

    /** @return 3D key object */
    static ModelInstance buildKey(float x, float y) {
        /*
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createBox(KEY_WIDTH, KEY_DEPTH, KEY_HEIGHT, blueMaterial,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
                */
        ModelLoader loader = new ObjLoader();
        Model model = loader.loadModel(Gdx.files.internal(KEY_OBJ));
        float doorX = x - KEY_WIDTH * 3 / 2;
        float doorY = y - KEY_DEPTH * 3 / 2;
        float doorZ = KEY_HEIGHT / 2;
        ModelInstance modelInstance = new ModelInstance(model, doorX, doorY, doorZ);
        modelInstance.userData = new InstanceData(ObjType.KEY,
                new Rectangle(x - 1 +KEY_WIDTH * 3 / 2 - KEY_BOUNDS_PLUS,
                        y - 1 + KEY_DEPTH * 3 / 2 - KEY_BOUNDS_PLUS,
                        KEY_WIDTH + KEY_BOUNDS_PLUS * 2,
                        KEY_DEPTH + KEY_BOUNDS_PLUS * 2));
        return modelInstance;
    }

    /** @return 3D prize object at x,y  */
    static ModelInstance buildPrize(float x, float y) {
        /* Simple box
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createBox(PRIZE_WIDTH, PRIZE_HEIGHT, PRIZE_DEPTH, blueMaterial,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        */
        ModelLoader loader = new ObjLoader();
        Model model = loader.loadModel(Gdx.files.internal("prize.obj"));
        float prizeX = x - PRIZE_WIDTH;
        float prizeY = y - PRIZE_DEPTH;
        float prizeZ = PRIZE_HEIGHT / 2;
        ModelInstance modelInstance = new ModelInstance(model, prizeX, prizeY,  prizeZ);
        modelInstance.userData = new InstanceData(ObjType.PRIZE,
                new Rectangle(prizeX - PRIZE_BOUNDS_PLUS, prizeY - PRIZE_BOUNDS_PLUS,
                        PRIZE_WIDTH + PRIZE_BOUNDS_PLUS * 2,
                        PRIZE_DEPTH + WALL_BOUNDS_PLUS * 2));
        return modelInstance;
    }

    /** @return 3D wall objects at x,y with width long */
    static ModelInstance buildWall(float x, float y, float width) {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder meshBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.TextureCoordinates, wallMaterial);
        meshBuilder.setUVRange(0,0,width,1);
        Vector3 corner00 = new Vector3(0, 0, 0);
        Vector3 corner10 = new Vector3(width, 0, 0);
        Vector3 corner11 = new Vector3(width, 0, 1);
        Vector3 corner01 = new Vector3(0, 0, 1);
        Vector3 normal = new Vector3(0, 1 , 0);
        meshBuilder.rect(corner00, corner10, corner11, corner01, normal);
        corner00 = new Vector3(0, 1, 1);
        corner10 = new Vector3(width, 1, 1);
        corner11 = new Vector3(width, 1, 0);
        corner01 = new Vector3(0, 1, 0);
        normal = new Vector3(0, 1 , 0);
        meshBuilder.rect(corner00, corner10, corner11, corner01, normal);
        meshBuilder.setUVRange(0,0,1,1);
        corner00 = new Vector3(width, 0, 0);
        corner10 = new Vector3(width, 1, 0);
        corner11 = new Vector3(width, 1, 1);
        corner01 = new Vector3(width, 0, 1);
        normal = new Vector3(0, 1 , 0);
        meshBuilder.rect(corner00, corner10, corner11, corner01, normal);
        corner00 = new Vector3(0, 0, 1);
        corner10 = new Vector3(0, 1, 1);
        corner11 = new Vector3(0, 1, 0);
        corner01 = new Vector3(0, 0, 0);
        normal = new Vector3(0, 1 , 0);
        meshBuilder.rect(corner00, corner10, corner11, corner01, normal);
        corner00 = new Vector3(0, 0, 1);
        corner10 = new Vector3(width, 0, 1);
        corner11 = new Vector3(width, 1, 1);
        corner01 = new Vector3(0, 1, 1);
        normal = new Vector3(0, 1 , 0);
        meshBuilder.rect(corner00, corner10, corner11, corner01, normal);
        Model wallModel = modelBuilder.end();
        ModelInstance wall = new ModelInstance(wallModel, x, y, 0);
        wall.userData = new InstanceData(ObjType.WALL, new Rectangle(x - WALL_BOUNDS_PLUS,
                y - WALL_BOUNDS_PLUS,
                width + WALL_BOUNDS_PLUS * 2,
                WALL_DEPTH + WALL_BOUNDS_PLUS * 2));
        return (wall);
    }

}
