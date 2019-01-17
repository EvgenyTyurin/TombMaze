package evgenyt.tombmaze;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import java.util.ArrayList;

/*
	Tomb Maze game
	2019 Evgeny Tyurin
*/

public class TombMaze extends ApplicationAdapter {

	private PerspectiveCamera camera;
	private ModelBatch modelBatch;
    private ArrayList<ModelInstance> walls;
	private Environment environment;
	private Stage stage;
	private Label label;

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

		// 3D rendering batch
		modelBatch = new ModelBatch();

		// Create labyrinth
        walls = Maze.createMaze(3, 3);

		// Create light
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f,
				1.0f));

		// HUD
		stage = new Stage();
		label = new Label("", PhoneScreen.HUD_LABEL_STYLE);
		label.setPosition(10, 10);
		stage.addActor(label);

	}

	// Handle user input
	private void handleInput() {
        // If nothing pressed - exit
        if (!Gdx.input.isTouched())
            return;
		// Get X and Y of user click
		float touchX = Gdx.input.getX();
		float touchY = PhoneScreen.flipY(Gdx.input.getY());
		// Move back or forward
		if (touchX > 500 && touchX < 1500) {
		    // Calculate new camera position
			float moveScale;
			if (touchY > PhoneScreen.CENTER_Y)
				moveScale = 0.1f;
			else
				moveScale = -0.1f;
			Vector3 newPos = new Vector3();
            newPos.set(camera.direction).scl(moveScale);
            newPos.add(camera.position);
            // If no collisionFlour - move camera
            if (!Graph3D.collisionFlour(walls, newPos))
                camera.position.set(newPos);
		} else {
		    // Rotate
            if (touchY > PhoneScreen.CENTER_Y) {
                float angle;
                if (touchX < PhoneScreen.CENTER_X)
                    angle = 1;
                else
                    angle = -1;
                camera.rotate(Vector3.Z, angle);
            } else {
                // Strafe
                // Calculate new camera position
                float moveScale;
                float angle;
                if (touchX > PhoneScreen.CENTER_X) {
                    moveScale = 0.1f;
                    angle = -90;
                }
                else {
                    moveScale = 0.1f;
                    angle = 90;
                }
                Vector3 newPos = new Vector3();
                newPos.set(camera.direction).scl(moveScale);
                newPos.rotate(Vector3.Z, angle);
                newPos.add(camera.position);
                // If no collisionFlour - move camera
                if (!Graph3D.collisionFlour(walls, newPos))
                    camera.position.set(newPos);
            }
		}
    }

	// Update scene
	private void update() {
        handleInput();
		camera.update();
        label.setText("X=" + camera.position.x +
                " Y: " + camera.position.y +
                " Angle: " + camera.direction);
	}

	// Render scene
	@Override
	public void render () {

		// Prepare to render
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// Update scene
		update();

		// Render 3D models
		modelBatch.begin(camera);
		for (ModelInstance wall : walls)
		    modelBatch.render(wall, environment);
		modelBatch.end();

		// Draw HUD
		stage.draw();

	}

	// Exit point
	@Override
	public void dispose () {
		modelBatch.dispose();
	}
}
