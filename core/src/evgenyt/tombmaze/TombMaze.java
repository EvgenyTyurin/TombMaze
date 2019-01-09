package evgenyt.tombmaze;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/*
	Tomb Maze game
	2019 Evgeny Tyurin
*/

public class TombMaze extends ApplicationAdapter {

	private PerspectiveCamera camera;
	private ModelBatch modelBatch;
	private Model wallModel;
    private ArrayList<ModelInstance> walls;
	private Environment environment;

	// Run point
	@Override
	public void create () {
		// Create camera
		camera = new PerspectiveCamera(Graph3D.CAMERA_VIEW_ANGLE, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		camera.position.set(Graph3D.CAMERA_POS_INIT);
		camera.lookAt(Graph3D.CAMERA_LOOK_INIT_AT);
		camera.near = Graph3D.CAMERA_NEAR;
		camera.far = Graph3D.CAMERA_FAR;

		// Create 3D model
		modelBatch = new ModelBatch();
		ModelBuilder modelBuilder = new ModelBuilder();
		wallModel = modelBuilder.createBox(Graph3D.WALL_WIDTH, Graph3D.WALL_HEIGHT,
                Graph3D.WALL_DEPTH, new Material(ColorAttribute.createDiffuse(Color.BLUE)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

		// Create labyrinth
        walls = new ArrayList<ModelInstance>();
        addWall(5, 5);

		// Create light
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f,
				1.0f));

	}

	// Add wall to labyrinth
	private void addWall(float x, float y) {
	    ModelInstance wall = new ModelInstance(wallModel, x, y, 0);
	    wall.userData = new Rectangle(x - Graph3D.WALL_BOUNDS_PLUS, y - Graph3D.WALL_BOUNDS_PLUS,
                Graph3D.WALL_WIDTH + Graph3D.WALL_BOUNDS_PLUS,
                Graph3D.WALL_DEPTH + Graph3D.WALL_BOUNDS_PLUS);
	    walls.add(wall);
    }

	// Handle user input
	private void handleInput() {
        // If nothing pressed - exit
        if (!Gdx.input.isTouched())
            return;
		// Get X and Y of user click
		float touchX = Gdx.input.getY();
		float touchY = Gdx.input.getX();
		// Move
		if (touchY < 500 || touchY > 1500) {
			// camera.translate(0.01f, 0, 0);
			float moveScale;
			if (touchY < 500)
				moveScale = 0.1f;
			else
				moveScale = -0.1f;
			Vector3 newPos = new Vector3();
            newPos.set(camera.direction).scl(moveScale);
            newPos.add(camera.position);
            boolean collision = false;
            for (ModelInstance wall : walls) {
                Rectangle bounds = (Rectangle) wall.userData;
                if (bounds.contains(newPos.x, newPos.y)) {
                    collision = true;
                    break;
                }
            }
            if (!collision) {
                camera.position.set(newPos);
                // camera.position.add(camVector);
            }
		} else {
		// Rotate
			float angle;
			if (touchX < PhoneScreen.CENTER_X)
				angle = 1;
			else
				angle = -1;
			camera.rotate(Vector3.Z, angle);
		}
    }

	// Update scene
	private void update(float deltaTime) {
        handleInput();
		camera.update();
	}

	// Render scene
	@Override
	public void render () {

		// Prepare to render
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// Update scene
		update(Gdx.graphics.getDeltaTime());

		// Render 3D models
		modelBatch.begin(camera);
		for (ModelInstance wall : walls)
		    modelBatch.render(wall, environment);
		modelBatch.end();
	}

	// Exit point
	@Override
	public void dispose () {
		modelBatch.dispose();
		wallModel.dispose();
	}
}
