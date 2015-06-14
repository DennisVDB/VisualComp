import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

class Tree extends PApplet {
    PApplet parent;

    private PShape tree;
    private PVector position;
    private float radius;

    public Tree(PApplet parent, PVector position) {
        this.parent = parent;

        this.tree = parent.loadShape("tree.obj");
        this.tree.scale(10);

        this.position = position;
        this.radius = 50;
    }

    public void display() {
        parent.pushMatrix();

        parent.translate(position.x, 0, position.z);

    /* Rotate in order to be in the right position. */
        parent.rotateX(PI);

        parent.shape(tree);

        parent.popMatrix();
    }

    public PVector getPosition() {
        return new PVector(position.x, position.y, position.z);
    }

    public float getRadius() {
        return radius;
    }
}