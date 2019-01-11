package evgenyt.tombmaze;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

/**
 * Maze class
 */

public class Maze {

    static int[][] mazeData;
    static ArrayList<ModelInstance> walls;

    // Build 3D wall
    static void buildWall(int x, int y, int width) {
        ModelBuilder modelBuilder = new ModelBuilder();
        Model wallModel = modelBuilder.createBox(width, Graph3D.WALL_HEIGHT,
                Graph3D.WALL_DEPTH, new Material(ColorAttribute.createDiffuse(Color.BLUE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ModelInstance wall = new ModelInstance(wallModel, x, y, 0);
        wall.userData = new Rectangle(x - Graph3D.WALL_BOUNDS_PLUS,
                y - Graph3D.WALL_BOUNDS_PLUS,
                width + Graph3D.WALL_BOUNDS_PLUS,
                Graph3D.WALL_DEPTH + Graph3D.WALL_BOUNDS_PLUS);
        walls.add(wall);
    }

    // Create maze data and build 3d walls
    static void createMaze(int width, int height) {
        walls = new ArrayList<ModelInstance>();
        mazeData = new int[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                mazeData[x][y] = 1;
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
                        buildWall(xBegin, y, blockWidth);
                    blockWidth = 0;
                    xBegin = xEnd + 1;
                    xEnd = xBegin;
                }
            }
            if (blockWidth > 0)
                buildWall(xBegin, y, blockWidth);
        }
    }
}
