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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**

    Parameters and procedures of 3D graphics

*/

class Graph3D {

    /** Camera settings */
    private static final float CAMERA_NEAR = 0.1f;
    private static final float CAMERA_FAR = 300;
    private static final Vector3 CAMERA_POS_INIT = new Vector3(1.5f, 1.5f, 20.5f);
    private static final Vector3 CAMERA_POS_INIT2 = new Vector3(1.5f, 1.5f, 0.5f);
    static final Vector3 CAMERA_LOOK_INIT_AT = new Vector3(2, 2, 0.5f);
    private static final float CAMERA_VIEW_ANGLE = 75;

    /** Wall settings */
    // private static final float WALL_WIDTH = 1;
    // private static final float WALL_HEIGHT = 1;
    private static final float WALL_DEPTH = 1;
    private static final float WALL_BOUNDS_PLUS = 0.1f;
    private static final String WALL_TEXTURE = "texture.png";

    /** Prize settings */
    private static final float PRIZE_WIDTH = 0.5f;
    private static final float PRIZE_HEIGHT = 0.5f;
    private static final float PRIZE_DEPTH = 0.5f;
    private static final float PRIZE_BOUNDS_PLUS = 0.1f;


    /** 3D materials */
    private static Material wallMaterial;
    private static Material blueMaterial;

    /** Loads textures from files to memory, creates materials */
    static void loadTexturesAndMaterials(){
        // Wall material
        Texture wallTexture = new Texture(WALL_TEXTURE);
        wallTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        wallTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureAttribute textureAttribute = TextureAttribute.createDiffuse(wallTexture);
        wallMaterial = new Material(textureAttribute);
        // Blue material
        blueMaterial = new Material(ColorAttribute.createDiffuse(Color.BLUE));
    }

    /** @return collision type */
    static ObjType collisionType(ArrayList<ModelInstance> objects3D, Vector3 position) {
        ModelInstance instance = collision2D(objects3D, position);
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
    static boolean collision2Dobject(ModelInstance object, Vector3 position) {
        InstanceData instanceData = (InstanceData) object.userData;
        Rectangle bounds = instanceData.getBounds2D();
        if (bounds.contains(position.x, position.y))
            return true;
        else
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

    /** @return New player camera */
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


    /** @return 3D prize object at x,y  */
    static ModelInstance buildPrize(float x, float y) {
        ModelBuilder modelBuilder = new ModelBuilder();
        /* Simple box
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
        return (modelInstance);
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
