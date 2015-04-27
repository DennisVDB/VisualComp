import java.text.DecimalFormat;

float depth = 1000;

PGraphics infoWindow;
PGraphics barWindow;
PGraphics scoreBoard;

float boxWidth;
float boxDepth;
float boxThickness;

int infoWindowHeight;
int infoWindowWidth;
int barWindowHeight;
int barWindowWidth;
int scoreBoardWidth;
int scoreBoardHeight;

double [] score;

float angleX = 0;
float angleY = 0;
float angleZ = 0;
float speed = 800;
float maxRotation = PI / 3.0;

float previewMoverRadius;
float previewCylinderRadius;

Mover mover;
// ArrayList<Cylinder> obstacles = new ArrayList<Cylinder>();
ArrayList<Tree> obstacles = new ArrayList<Tree>();

boolean editMode = false;

void setup() { 
  size(800, 600, P3D); 
  noStroke();
  
  score = new double[2];

  boxWidth = width / 2;
  boxDepth = width / 2;
  boxThickness = 10;

  mover = new Mover(boxWidth, boxDepth, boxThickness, 20);
  
  infoWindowHeight = height / 4;
  infoWindowWidth = width;
  scoreBoardHeight = height / 4;
  scoreBoardWidth = height / 5;
  barWindowWidth =infoWindowWidth - (infoWindowHeight+height/5+30);
  barWindowHeight = infoWindowHeight -50;
  
  infoWindow = createGraphics(infoWindowWidth, infoWindowHeight, P2D);
  barWindow = createGraphics(barWindowWidth, barWindowHeight, P2D);
  scoreBoard = createGraphics(scoreBoardWidth, scoreBoardHeight, P2D);
  
  previewMoverRadius = (20 * infoWindowHeight) / boxWidth;
  previewCylinderRadius = (100 * infoWindowHeight) / boxWidth;

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


  background(MAX_INT); // white

  camera();
  drawInfoWindow();
  drawBarWindow();
  drawScoreBoard();
image(infoWindow, 0, height - infoWindowHeight );
image(scoreBoard,infoWindowHeight+20, height-infoWindowHeight);
image(barWindow,(infoWindowHeight+10+height/5+15),height-infoWindowHeight+10);
  directionalLight(51, 102, 126, 0, 1, 1); 
  ambientLight(102, 102, 102);
  if (!editMode) {  
    camera(width / 2, height / 2 - elevation, depth, width / 2, height / 2, 0, 0, 1, 0);

    /* Center of the screen. */
    translate(width/2, height/2, 0);


    /* Angle of the board. */
    rotateX(-angleX);
    rotateZ(angleZ);

    fill(200, 200, 200);
    box(boxWidth, boxThickness, boxDepth);

    mover.update(angleX, angleZ);
   for(int i =0;i<2;i++){
     score[i]=mover.computeScore(obstacles)[i];
   }
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
    translate(width/2, height/2, 0);

    /*
     * Rotate in order for the
     * plate to face the camera.
     */
    rotateX(-PI/2);

    fill(200, 200, 200);
    box(boxWidth, boxThickness, boxDepth);

    mover.display();

    for (Tree c : obstacles) {                
      c.display();
    }
  }
  
  //  mover.computeScore(obstacles);
  
}

void drawInfoWindow() {
  float x, z;
  
  infoWindow.beginDraw();
  infoWindow.noStroke();
  infoWindow.fill(54,100,139);
  infoWindow.background(255,222,173);

  infoWindow.rect(0, 0, infoWindowHeight,infoWindowHeight);
  
  pushMatrix();

  translate(infoWindowHeight / 2, infoWindowHeight / 2);

  PVector moverPosition = mover.getPosition();
  
  x = map(moverPosition.x, -boxWidth / 2, boxWidth / 2, 0, infoWindowHeight);
  z = map(moverPosition.z, -boxDepth / 2, boxDepth / 2, 0, infoWindowHeight);
  
  infoWindow.noStroke();
  infoWindow.fill(255,0,0);
  infoWindow.ellipse(x, z, previewMoverRadius, previewMoverRadius);

  for (Tree c : obstacles) {
    PVector cylinderPosition = c.getPosition();
    
    x = map(cylinderPosition.x, -boxWidth / 2, boxWidth / 2, 0, infoWindowHeight);
    z = map(cylinderPosition.z, -boxDepth / 2, boxDepth / 2, 0, infoWindowHeight);
    infoWindow.fill(255,222,173);
    infoWindow.ellipse(x, z, previewCylinderRadius, previewCylinderRadius);
    
  }

  popMatrix();

  infoWindow.endDraw();
}

void drawBarWindow(){
barWindow.beginDraw();
barWindow.background(254,254,226);
barWindow.fill(54,100,139);
int i =0;
while(i<score[1]){
barWindow.rect(0,barWindowHeight-(5*i/100),10,5);
i+=100;
}
barWindow.endDraw();

}

void drawScoreBoard(){
scoreBoard.beginDraw();
scoreBoard.background(255,222,173);
scoreBoard.noFill();
  scoreBoard.stroke(255);
  scoreBoard.strokeWeight(5);
  scoreBoard.rect(0,5,scoreBoardWidth,scoreBoardHeight-10);
  scoreBoard.fill(0,0,0);
  PVector v = mover.getVelocity();
double velocity = roundDouble(sqrt(v.x*v.x+v.y*v.y));
String s = "Vitesse :\n"+ velocity;
scoreBoard.text(s,5,20);

double totalScore = roundDouble(score[1]);
String s1 = "Total score :\n" + totalScore;
scoreBoard.text(s1,5,55);

double tempScore = roundDouble(score[0]);
String s2 = "Last score :\n" +tempScore;
scoreBoard.text(s2,5,90);


scoreBoard.endDraw();

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

    Tree tree = new Tree(position);

    /* 
     * Check if the cylinder is not in the way the ball
     * and is on the board.
     */
    if (!mover.checkCylinderCollision(tree) &&
      position.x >= -boxWidth/2 && position.x <= boxWidth/2 &&
      position.z >= -boxDepth/2 && position.z <= boxDepth/2) {
      obstacles.add(tree);
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

private double roundDouble(double value){
DecimalFormat df = new DecimalFormat("#####.000"); 
String str = df.format(value);
return Double.parseDouble(str.replace(',', '.')); 

}
