import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

class Cylinder extends PApplet {
    private PApplet parent;

    private float cylinderBaseSize = 50;
    private float cylinderHeight = 50;
    private int cylinderResolution = 40;

    private PVector position;

    private PShape cylinder = new PShape();

    public Cylinder(PApplet parent, PVector position) {
        this.parent = parent;
        this.position = position;

        float angle;
        float[] x = new float[cylinderResolution + 1];
        float[] y = new float[cylinderResolution + 1];

        //get the x and y position on a circle for all the sides
        for (int i = 0; i < x.length; i++) {
            angle = (TWO_PI / cylinderResolution) * i;
            x[i] = sin(angle) * cylinderBaseSize;
            y[i] = cos(angle) * cylinderBaseSize;
        }

        cylinder = createShape(GROUP);

    /* Cylinder body */
        PShape cylinderBody = createShape();
        cylinderBody.beginShape(QUAD_STRIP);

        //draw the border of the cylinder
        for (int i = 0; i < x.length; i++) {
            cylinderBody.vertex(x[i], y[i], 0);
            cylinderBody.vertex(x[i], y[i], cylinderHeight);
        }

        cylinderBody.endShape();

    /* Cylinder top */
        PShape cylinderTop = createShape();
        cylinderTop.beginShape(TRIANGLE_FAN);

        cylinderTop.vertex(0, 0, 0);

        for (int i = 0; i < x.length; i++) {
            cylinderTop.vertex(x[i], y[i], 0);
        }

        cylinderTop.endShape();

    /* Cylinder bottom */
        PShape cylinderBottom = createShape();
        cylinderBottom.beginShape(TRIANGLE_FAN);

        cylinderBottom.vertex(0, 0, cylinderHeight);

        for (int i = 0; i < x.length; i++) {
            cylinderBottom.vertex(x[i], y[i], cylinderHeight);
        }

        cylinderBottom.endShape();

    /* Assemble the parts. */
        cylinder.addChild(cylinderBody);
        cylinder.addChild(cylinderTop);
        cylinder.addChild(cylinderBottom);

        cylinder.setFill(color(0, 127, 0));
    }

    public void display() {
        pushMatrix();

        translate(position.x, 0, position.z);

    /* Rotate in order to be in the right position. */
        rotateX(PI / 2);

        shape(cylinder);

        popMatrix();
    }

    public PVector getPosition() {
        return new PVector(position.x, position.y, position.z);
    }

    public float getRadius() {
        return cylinderBaseSize;
    }
}
