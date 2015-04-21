import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.ArrayList;

public class Game extends PApplet {

    float depth = 1000;

    PGraphics infoWindow;

    float boxWidth;
    float boxDepth;
    float boxThickness;

    int infoWindowHeight;
    int infoWindowWidth;

    float angleX = 0;
    float angleZ = 0;
    float speed = 800;
    float maxRotation = PI / 3.0f;

    float previewMoverRadius;
    float previewCylinderRadius;

    Mover mover;
    ArrayList<Tree> obstacles = new ArrayList<>();

    boolean editMode = false;

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"Game"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }

    public void setup() {
        size(displayWidth, displayHeight, P3D);
        noStroke();

        boxWidth = width / 2;
        boxDepth = width / 2;
        boxThickness = 10;

        mover = new Mover(this, boxWidth, boxDepth, boxThickness, 20);

        infoWindowHeight = height / 4;
        infoWindowWidth = width;
        infoWindow = createGraphics(infoWindowWidth, infoWindowHeight, P2D);

        previewMoverRadius = (20 * infoWindowHeight) / boxWidth;
        previewCylinderRadius = (100 * infoWindowHeight) / boxWidth;
    }

    public void draw() {
  /*
   * The elevation of the camera creates projection problems when
   * getting coordinates from the mouse. Therefore we remove this
   * elevation while in "edit" mode.
   */
        float elevation = 800;

        if (editMode) {
            elevation = 0;
        }

        directionalLight(51, 102, 126, 0, 1, 1);
        ambientLight(102, 102, 102);

        background(MAX_INT); // white

        camera();
        drawInfoWindow();
        image(infoWindow, 0, height - infoWindowHeight - 100);

        if (!editMode) {
            camera(width / 2, height / 2 - elevation, depth, width / 2, height / 2, 0, 0, 1, 0);

    /* Center of the screen. */
            translate(width / 2, height / 2, 0);


    /* Angle of the board. */
            rotateX(-angleX);
            rotateZ(angleZ);

            fill(200, 200, 200);
            box(boxWidth, boxThickness, boxDepth);

            mover.update(angleX, angleZ);

    /*
     * Display the cylinders and handle
     * the collisions between the ball and
     * the cylinders.
     */
            for (Tree c : obstacles) {
                c.display();
                mover.handleCylinderCollision(c);
            }

            mover.checkEdges();

            mover.display();
        } else {
            camera();

    /* Center of the screen. */
            translate(width / 2, height / 2, 0);

    /*
     * Rotate in order for the
     * plate to face the camera.
     */
            rotateX(-PI / 2);

            fill(200, 200, 200);
            box(boxWidth, boxThickness, boxDepth);

            mover.display();

            obstacles.forEach(Tree::display);
        }
    }

    public void drawInfoWindow() {
        float x, z;

        infoWindow.beginDraw();

        infoWindow.background(127);

        infoWindow.rect(0, 0, infoWindowHeight, infoWindowHeight);

        pushMatrix();

        translate(infoWindowHeight / 2, infoWindowHeight / 2);

        PVector moverPosition = mover.getPosition();

        x = map(moverPosition.x, -boxWidth / 2, boxWidth / 2, 0, infoWindowHeight);
        z = map(moverPosition.z, -boxDepth / 2, boxDepth / 2, 0, infoWindowHeight);

        infoWindow.ellipse(x, z, previewMoverRadius, previewMoverRadius);

        for (Tree c : obstacles) {
            PVector cylinderPosition = c.getPosition();

            x = map(cylinderPosition.x, -boxWidth / 2, boxWidth / 2, 0, infoWindowHeight);
            z = map(cylinderPosition.z, -boxDepth / 2, boxDepth / 2, 0, infoWindowHeight);

            infoWindow.ellipse(x, z, previewCylinderRadius, previewCylinderRadius);
        }

        popMatrix();

        infoWindow.endDraw();
    }

    public void keyPressed() {
        if (key == CODED) {
            if (keyCode == SHIFT) {
                editMode = true;
            }
        }
    }

    public void keyReleased() {
        if (key == CODED) {
            if (keyCode == SHIFT) {
                editMode = false;
            }
        }
    }

    public void mouseDragged() {
  /*
   * The delta of the angle is determined
   * from the difference between the current
   * mouse position and the previous. It is then
   * mapped on a interval whose size is determined
   * by the speed. This allows us to have faster
   * movements as the speed increases.
   */
        angleX += map(mouseY - pmouseY, -height, height, -speed / 100, speed / 100);
        angleZ += map(mouseX - pmouseX, -width, width, -speed / 100, speed / 100);

        angleX = limitRotation(angleX, maxRotation);
        angleZ = limitRotation(angleZ, maxRotation);
    }

    public void mouseClicked() {
        if (editMode) {
            PVector position = new PVector(mouseX - width / 2, 0, mouseY - height / 2);

            Tree tree = new Tree(this, position);

    /*
     * Check if the cylinder is not in the way the ball
     * and is on the board.
     */
            if (!mover.checkCylinderCollision(tree) &&
                    position.x >= -boxWidth / 2 && position.x <= boxWidth / 2 &&
                    position.z >= -boxDepth / 2 && position.z <= boxDepth / 2) {
                obstacles.add(tree);
            }
        }
    }

    public void mouseWheel(MouseEvent event) {
        speed += event.getCount();

        if (speed < 200) {
            speed = 200;
        } else if (speed > 2000) {
            speed = 2000;
        }
    }

    public float limitRotation(float angle, float maxRotation) {
        angle = angle < -maxRotation ? -maxRotation : angle;
        angle = angle > maxRotation ? maxRotation : angle;

        return angle;
    }
}
