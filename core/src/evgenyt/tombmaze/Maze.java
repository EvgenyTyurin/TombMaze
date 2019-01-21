package evgenyt.tombmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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

    /** @return 3D wall */
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

    /** @return maze data, loaded from file */
    private static int[][] loadMaze(String mazeFile) {
        int width = 0;
        int height = 0;
        FileHandle fileHandle = Gdx.files.internal(mazeFile);
        String strMaze = fileHandle.readString();
        String[] strings = strMaze.split("\n");
        String[] mazeSize = strings[0].split(",");
        width = Integer.valueOf(mazeSize[0]);
        height = Integer.valueOf(mazeSize[1]);
        System.out.println("loadMaze: width=" + width + " height=" + height);
        int[][] mazeData = new int[width][height];
        for (int y = 0; y < height; y++) {
            String[] cells = strings[y + 1].split(",");
            System.out.println("loadMaze: cells=" + cells);
            for (int x = 0; x < width; x++) {
                mazeData[x][y] = Integer.valueOf(cells[x]);
                System.out.println("loadMaze: mazeData[" + x + "][" + y + "]=" + mazeData[x][y]);
            }
        }
        System.out.println("loadMaze: Maze loaded.");
        return mazeData;
    }

    /** @return 3d walls list by loaded maze data from file */
    static ArrayList<ModelInstance> createMaze(String mazeFile) {
        int[][] mazeData = loadMaze(mazeFile);
        ArrayList<ModelInstance> walls = new ArrayList<ModelInstance>();
        // Build walls
        for (int y = 0; y < mazeData.length; y++) {
            int xBegin = 0;
            int xEnd = 0;
            int blockWidth = 0;
            while (xEnd < mazeData.length){
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
