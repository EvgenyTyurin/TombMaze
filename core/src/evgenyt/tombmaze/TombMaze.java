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
    private ArrayList<ModelInstance> mazeObjects;
	private Environment environment;
	private Stage stage;
	private Label label;
	private String[] mazes = {"maze01.txt", "maze02.txt"};
	private int mazeIdx = 0;
	private boolean cameraFalling;

	// Run point
	@Override
	public void create () {
		// 3D rendering batch
		modelBatch = new ModelBatch();

		// Create light
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f,
				1.0f));

		// Create HUD
		stage = new Stage();
		label = new Label("", PhoneScreen.HUD_LABEL_STYLE);
		label.setPosition(10, 10);
		stage.addActor(label);

		// Enter labyrinth
        newMaze();
	}

	/** Player enters new maze */
	private void newMaze() {
	    if (mazeIdx > mazes.length - 1)
	        mazeIdx = 0;
        // Create camera
        camera = Graph3D.getPlayerCamera();
        // Create labyrinth
        mazeObjects = Maze.createMaze(mazes[mazeIdx]);
        mazeIdx++;
        cameraFalling = true;
    }

	/** Player move to new position */
	private void playerMove(Vector3 newPos) {
		camera.position.set(newPos);
	}

	/** Player want to move to new position */
	private void wantMove(Vector3 newPos) {
		ModelInstance instance = Graph3D.collision2D(mazeObjects, newPos);
		ObjType objType = Graph3D.getObjType(instance);
        switch (objType) {
			case WALL: break;
			case DOOR: InstanceData instanceData = Graph3D.getInstanceData(instance);
						Vector3 position = instance.transform.getTranslation(new Vector3());
						if (position.z < -0) {
							instance.userData = null;
						} else {
							instanceData.setSpeedZ(-0.01f);
						}
						break;
			case PRIZE: newMaze();
					    break;
			default: playerMove(newPos);
		}
	}

	/** Handle user input */
	private void handleInput() {
        // If falling or nothing pressed - exit
        if (cameraFalling || !Gdx.input.isTouched())
            return;
		// Get X and Y of user click
		float touchX = Gdx.input.getX();
		float touchY = PhoneScreen.flipY(Gdx.input.getY());
		// Move back or forward
		if (touchX > 500 && touchX < 1500) {
		    // Calculate new camera position
			float moveScale;
			if (touchY > PhoneScreen.CENTER_Y)
				moveScale = Graph3D.CAMERA_MOVE_SPEED;
			else
				moveScale = -Graph3D.CAMERA_MOVE_SPEED;
			Vector3 newPos = new Vector3();
            newPos.set(camera.direction).scl(moveScale);
            newPos.add(camera.position);
            wantMove(newPos);
		} else {
		    // Rotate
            if (touchY > PhoneScreen.CENTER_Y) {
				if (touchX < PhoneScreen.CENTER_X)
					camera.rotate(Vector3.Z, Graph3D.CAMERA_ROTATION_SPEED);
                else
					camera.rotate(Vector3.Z, -Graph3D.CAMERA_ROTATION_SPEED);
            } else {
                // Strafe
                // Calculate new camera position
                Vector3 newPos = new Vector3();
                newPos.set(camera.direction).scl(Graph3D.CAMERA_MOVE_SPEED);
				if (touchX > PhoneScreen.CENTER_X)
					newPos.rotate(Vector3.Z, -90);
				else
					newPos.rotate(Vector3.Z, 90);
                newPos.add(camera.position);
                wantMove(newPos);
            }
		}
    }

	// Update scene
	private void update() {
        handleInput();
        for (ModelInstance mazeObject : mazeObjects) {
        	InstanceData instanceData = (InstanceData) mazeObject.userData;
        	if (instanceData != null) {
				mazeObject.transform.translate(0, 0, instanceData.getSpeedZ());
			}
		}
        if ( cameraFalling && camera.position.z > 0.5f) {
        	camera.position.z -= 0.1f;
			camera.lookAt(Graph3D.CAMERA_LOOK_INIT_AT);
		} else {
        	if (cameraFalling) {
				cameraFalling = false;
				camera = Graph3D.getPlayerCamera2();
			}
		}
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
		for (ModelInstance wall : mazeObjects)
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
