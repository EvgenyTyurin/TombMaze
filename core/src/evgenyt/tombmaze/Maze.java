package evgenyt.tombmaze;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

/**
 * Maze class
 */

class Maze {

    private static int[][] mazeData;

    // Build 3D wall
    private static ModelInstance buildWall(float x, float y, float width) {
        ModelBuilder modelBuilder = new ModelBuilder();
        Texture texture = new Texture("texture.png");
        TextureAttribute textureAttribute = TextureAttribute.createDiffuse(texture);
        Material material = new Material(textureAttribute);
        Model wallModel = modelBuilder.createBox(width, Graph3D.WALL_HEIGHT,
                Graph3D.WALL_DEPTH, material,
                VertexAttributes.Usage.Position |
                        VertexAttributes.Usage.TextureCoordinates);
        ModelInstance wall = new ModelInstance(wallModel, x + width / 2,
                y + Graph3D.WALL_DEPTH / 2, 0);
        wall.userData = new Rectangle(x - Graph3D.WALL_BOUNDS_PLUS,
                y - Graph3D.WALL_BOUNDS_PLUS,
                width + Graph3D.WALL_BOUNDS_PLUS * 2,
                Graph3D.WALL_DEPTH + Graph3D.WALL_BOUNDS_PLUS * 2);
        return (wall);
    }

    // Create maze data and build 3d walls
    static ArrayList<ModelInstance> createMaze(int width, int height) {
        ArrayList<ModelInstance> walls = new ArrayList<ModelInstance>();
        mazeData = new int[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                mazeData[x][y] = 0;
        mazeData[0][0] = 1;
        mazeData[1][0] = 1;
        mazeData[2][0] = 1;
        mazeData[0][1] = 1;
        mazeData[1][1] = 0;
        mazeData[2][1] = 0;
        mazeData[0][2] = 1;
        mazeData[1][2] = 1;
        mazeData[2][2] = 1;
        // Build walls
        for (int y = 0; y < height; y++) {
            int xBegin = 0;
            int xEnd = 0;
            int blockWidth = 0;
            while (xEnd < width){
                if (mazeData[xEnd][y] == 1) {
                    blockWidth++;
                    xEnd++;
                } else {
                    if (blockWidth > 0)
                        walls.add(buildWall(xBegin, y, blockWidth));
                    blockWidth = 0;
                    xBegin = xEnd + 1;
                    xEnd = xBegin;
                }
            }
            if (blockWidth > 0)
                walls.add(buildWall(xBegin, y, blockWidth));
        }
        return walls;
    }
}
