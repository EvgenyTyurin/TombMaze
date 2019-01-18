package evgenyt.tombmaze;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

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
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        TextureAttribute textureAttribute = TextureAttribute.createDiffuse(texture);
        Material material = new Material(textureAttribute);
        /* simple box method
        Model wallModel = modelBuilder.createBox(width, Graph3D.WALL_HEIGHT,
                Graph3D.WALL_DEPTH, material,
                VertexAttributes.Usage.Position |
                        VertexAttributes.Usage.TextureCoordinates);
        */
        modelBuilder.begin();
        MeshPartBuilder meshBuilder = modelBuilder.part("box", GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.TextureCoordinates, material);
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
        Model wallModel = modelBuilder.end();
        ModelInstance wall = new ModelInstance(wallModel, x, y, 0);
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
        mazeData[1][0] = 0;
        mazeData[2][0] = 1;
        mazeData[0][1] = 1;
        mazeData[1][1] = 1;
        mazeData[2][1] = 1;
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
