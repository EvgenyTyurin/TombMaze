package evgenyt.tombmaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/*
    Screen functions and variables
*/

class PhoneScreen {

    static float CENTER_X = Gdx.graphics.getWidth() / 2;
    static float CENTER_Y = Gdx.graphics.getHeight() / 2;
    private static BitmapFont LABEL_FONT = new BitmapFont(Gdx.files.internal("verdana.fnt"));
    static com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle HUD_LABEL_STYLE =
            new com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle(LABEL_FONT, Color.BLACK);

    // Convert Y from user input to screen Y-coordinate
    static float flipY(float y) {
        return Gdx.graphics.getHeight() - y;
    }

}
