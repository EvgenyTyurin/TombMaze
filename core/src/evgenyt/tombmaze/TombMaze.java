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
import com.badlogic.gdx.math.Vector3;

/*
	Tomb Maze game
	2019 Evgeny Turin

 */

public class TombMaze extends ApplicationAdapter {

	private PerspectiveCamera camera;
	private ModelBatch modelBatch;
	private Model box;
	private ModelInstance boxInstance;
	private Environment environment;

	// Run point
	@Override
	public void create () {
		// Create camera
		camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		camera.position.set(0f, 0f, 3f);
		camera.lookAt(0f, 0f, 0f);
		camera.near = 0.1f;
		camera.far = 300f;

		// Create 3D model
		modelBatch = new ModelBatch();
		ModelBuilder modelBuilder = new ModelBuilder();
		box = modelBuilder.createBox(2f, 2f, 2f,
				new Material(ColorAttribute.createDiffuse(Color.BLUE)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
		boxInstance = new ModelInstance(box, 0, 0, 0);

		// Create light
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f,
				1.0f));

	}

	// Update scene
	private void update() {
		camera.rotateAround(Vector3.Zero, new Vector3(0,1,0),1f);
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
		update();

		// Render 3D models
		modelBatch.begin(camera);
		modelBatch.render(boxInstance, environment);
		modelBatch.end();
	}

	// Exit point
	@Override
	public void dispose () {
		modelBatch.dispose();
		box.dispose();
	}
}
