float depth = 1000;

float boxWidth;
float boxDepth;
float boxThickness;

float angleX = 0;
float angleY = 0;
float angleZ = 0;
float speed = 800;
float maxRotation = PI / 3.0;

Mover mover;
ArrayList<Cylinder> obstacles = new ArrayList<Cylinder>();

boolean editMode = false;

void setup() { 
  size(displayWidth, displayHeight, P3D); 
  noStroke();

  boxWidth = width / 2;
  boxDepth = width / 2;
  boxThickness = 10;

  mover = new Mover(boxWidth, boxDepth, boxThickness, 20);
}

void draw() {  
  /* 
   * The elevation of the camera creates projection problems when
   * getting coordinates from the mouse. Therefore we remove this 
   * elevation while in "edit" mode.
   */
  float elevation = 800;

  if (editMode) {
    elevation = 0;
  }

  camera(width / 2, height / 2 - elevation, depth, width / 2, height / 2, 0, 0, 1, 0);

  directionalLight(51, 102, 126, 0, 1, 1); 
  ambientLight(102, 102, 102);

  background(MAX_INT); // white

  /* Center of the screen. */
  translate(width/2, height/2, 0);

  if (!editMode) {    
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
    for (Cylinder c : obstacles) {
      c.display();
      mover.handleCylinderCollision(c);
    }

    mover.checkEdges();

    mover.display();
  } else {    
    /*
     * Rotate in order for the
     * plate to face the camera.
     */
    rotateX(-PI/2);

    fill(200, 200, 200);
    box(boxWidth, boxThickness, boxDepth);

    mover.display();

    for (Cylinder c : obstacles) {                
      c.display();
    }
  }
}

void keyPressed() { 
  if (key == CODED) {
    if (keyCode == SHIFT) { 
      editMode = true;
    }
  }
}

void keyReleased() { 
  if (key == CODED) {
    if (keyCode == SHIFT) { 
      editMode = false;
    }
  }
}

void mouseDragged() {
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

void mouseClicked() {
  if (editMode) {
    PVector position = new PVector(mouseX - width/2, 0, mouseY - height/2);

    Cylinder cylinder = new Cylinder(position);

    /* 
     * Check if the cylinder is not in the way the ball
     * and is on the board.
     */
    if (!mover.checkCylinderCollision(cylinder) &&
      position.x >= -boxWidth/2 && position.x <= boxWidth/2 &&
      position.z >= -boxDepth/2 && position.z <= boxDepth/2) {
      obstacles.add(cylinder);
    }
  }
}

void mouseWheel(MouseEvent event) {
  speed += event.getCount();

  if (speed < 200) {
    speed = 200;
  } else if (speed > 2000) {
    speed = 2000;
  }
}

float limitRotation(float angle, float maxRotation) {
  angle = angle < -maxRotation ? -maxRotation : angle; 
  angle = angle > maxRotation ? maxRotation : angle;

  return angle;
}

