package evgenyt.tombmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Maze builder
 */

class Maze {

    /** @return maze data, loaded from file */
    private static int[][] loadMaze(String mazeFile) {
        int width;
        int height;
        FileHandle fileHandle = Gdx.files.internal(mazeFile);
        String strMaze = fileHandle.readString();
        String[] strings = strMaze.split("\n");
        String[] mazeSize = strings[0].split(",");
        width = Integer.valueOf(mazeSize[0]);
        height = Integer.valueOf(mazeSize[1]);
        int[][] mazeData = new int[width][height];
        for (int y = 0; y < height; y++) {
            String[] cells = strings[y + 1].split(",");
            for (int x = 0; x < width; x++) {
                mazeData[x][y] = Integer.valueOf(cells[x]);
            }
        }
        return mazeData;
    }

    /** @return 3d walls list by loaded maze data from file */
    static ArrayList<ModelInstance> createMaze(String mazeFile) {
        // Load maze from file
        int[][] mazeData = loadMaze(mazeFile);
        ArrayList<ModelInstance> mazeObjs = new ArrayList<ModelInstance>();
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
                        mazeObjs.add(Graph3D.buildWall(xBegin, y, blockWidth));
                    blockWidth = 0;
                    xBegin = xEnd + 1;
                    xEnd = xBegin;
                }
            }
            if (blockWidth > 0)
                mazeObjs.add(Graph3D.buildWall(xBegin, y, blockWidth));
        }
        // Build special objects: prize, doors & etc
        for (int y = 0; y < mazeData.length; y++) {
            for (int x = 0; x < mazeData.length; x++) {
                switch (mazeData[x][y]) {
                    case 2: mazeObjs.add(Graph3D.buildPrize(x + 1, y + 1));
                            break;
                    case 3: mazeObjs.add(Graph3D.buildDoor(x + 1, y + 1));
                            break;
                }
            }
        }
        // Add floor and roof for more comfort
        mazeObjs.add(Graph3D.buildFloor(mazeData.length));
        mazeObjs.add(Graph3D.buildRoof(mazeData.length));
        return mazeObjs;
    }
}
