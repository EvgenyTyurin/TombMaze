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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
	private Stage HUDStage;
	private Label HUDLabel;
	private String[] mazes = {"maze01.txt", "maze02.txt"};
	private int mazeIdx = 0;
	private boolean cameraFalling;
	private int keysNum;

	/** App run point */
	@Override
	public void create () {
		// 3D rendering batch
		modelBatch = new ModelBatch();

		// Create light
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f,
				1.0f));

		// Create HUD
		HUDStage = new Stage();
		HUDLabel = new Label("", PhoneScreen.HUD_LABEL_STYLE);

		// Enter labyrinth
        newMaze();
	}

	/** Form a hood with new player state */
	private void updateHUD() {
		HUDStage.clear();
		HUDLabel.setPosition(10, 10);
		HUDStage.addActor(HUDLabel);
		for (int keyCounter = 1; keyCounter <= keysNum; keyCounter++) {
			Image keyImage = new Image(Graph3D.keyImageTexture);
			keyImage.setPosition(10 + (keyCounter - 1) * 210, 10);
			HUDStage.addActor(keyImage);
		}
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
        keysNum = 0;
		updateHUD();
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
			case DOOR:
				if (instance != null && keysNum > 0) {
					keysNum--;
					updateHUD();
					InstanceData instanceData = Graph3D.getInstanceData(instance);
					instanceData.setSpeedZ(-0.005f);
					instanceData.setObjType(ObjType.DOOR_SLIDING);
				}
				break;
			case DOOR_SLIDING: break;
			case PRIZE:
				newMaze();
				break;
			case KEY:
				keysNum++;
				updateHUD();
				mazeObjects.remove(instance);
				playerMove(newPos);
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

	/** Update scene */
	private void update() {
	    // User input
        handleInput();
        // Maze objects action
        for (ModelInstance mazeObject : mazeObjects) {
        	InstanceData instanceData = Graph3D.getInstanceData(mazeObject);
        	if (instanceData != null) {
				mazeObject.transform.translate(0, 0, instanceData.getSpeedZ());
				Vector3 position = mazeObject.transform.getTranslation(new Vector3());
				if (position.z < -0.5f) {
					mazeObject.userData = null;
				}
			}
		}
		// Camera actions
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
        // Hud update
        HUDLabel.setText("X=" + camera.position.x +
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
		HUDStage.draw();
	}

	// Exit point
	@Override
	public void dispose () {
		modelBatch.dispose();
	}
}
