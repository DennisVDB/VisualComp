import processing.core.PApplet;
import processing.core.PVector;

class Mover extends PApplet {
    private PApplet parent;

    private PVector position;
    private PVector velocity;

    private float boarderX;
    private float boarderZ;

    private float radius;

    private float offset;

    private float DELTA = 0.01f;

    public Mover(PApplet parent, float boxWidth, float boxDepth, float boxThickness, float radius) {
        this.parent = parent;

        offset = -(radius + boxThickness / 2);

        this.position = new PVector(0, 0, 0);

        this.velocity = new PVector(0, 0, 0);

        this.boarderX = (boxWidth / 2) - radius;
        this.boarderZ = (boxDepth / 2) - radius;

        this.radius = radius;
    }

    public void update(float angleX, float angleZ) {
        PVector deltaGravity = accountForGravity(velocity, angleX, angleZ);
        PVector deltaFriction = accountForFriction(velocity);

        velocity.x += deltaGravity.x + deltaFriction.x;
        velocity.z += deltaGravity.z + deltaFriction.z;

        position.add(velocity);
    }

    public void display() {
        parent.pushMatrix();

        parent.translate(position.x, offset, position.z);

        parent.fill(255, 0, 0);
        parent.sphere(radius);

        parent.popMatrix();
    }

    /**
     * Check if the ball is colliding with
     * the boarders and manage the collision.
     * The ball is also moved to the boarder in
     * order to avoid that it goes to far between
     * two displays.
     */
    public void checkEdges() {
        if (position.x <  -boarderX) {
            position.x = -boarderX;
            velocity.x = -velocity.x;
        } else if  (position.x > boarderX) {
            position.x = boarderX;
            velocity.x = -velocity.x;
        }

        if (position.z < -boarderZ) {
            position.z = -boarderZ;
            velocity.z = -velocity.z;
        } else if (position.z > boarderZ) {
            position.z = boarderZ;
            velocity.z = -velocity.z;
        }
    }

    private PVector accountForGravity(PVector velocity, float angleX, float angleZ) {
        float gravity = 3;

        return (new PVector(gravity * sin(angleZ), 0, gravity * sin(angleX)));
    }

    private PVector accountForFriction(PVector velocity) {
        float normalForce = 1;
        float mu = .5f;
        float frictionMagnitude = normalForce * mu;

        PVector friction = velocity.get();
        friction.mult(-1);
        friction.normalize();
        friction.mult(frictionMagnitude);

        return (new PVector(friction.x, 0, friction.z));
    }

    public boolean checkCylinderCollision(Tree tree) {
        double centerDistance = position.dist(tree.getPosition());

        double distance = centerDistance - (radius + tree.getRadius());

        return distance <= 0;
    }

    /**
     * Handles the collision with a cylinder.
     * When a collision occurs the ball is moved
     * to the limit in order to avoid that the ball is
     * drawn inside the cylinder.
     */
    public void handleCylinderCollision(Tree tree) {
        if (checkCylinderCollision(tree)) {
            PVector normal = new PVector(position.x, position.y, position.z);
            normal.sub(tree.getPosition());

            PVector newPosition = new PVector(normal.x, normal.y, normal.z);

      /* Add delta to avoid the ball to get stuck. */
            newPosition.setMag(radius + tree.getRadius() + DELTA);
            newPosition.add(tree.getPosition());
            position = newPosition;

            normal.normalize();

            PVector currentVelocity = new PVector(velocity.x, velocity.y, velocity.z);
            PVector velocityDelta = new PVector(normal.x, normal.y, normal.z);
            velocityDelta.mult(2 * (velocity.dot(normal)));

            velocity.sub(velocityDelta);
        }
    }

    public PVector getPosition() {
        return position;
    }
}