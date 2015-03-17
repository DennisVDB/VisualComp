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

void setup() { 
  size(displayWidth, displayHeight, P3D); 
  noStroke();
  
  boxWidth = width / 2;
  boxDepth = width / 2;
  boxThickness = 10;
  
  mover = new Mover(boxWidth, boxDepth, boxThickness, 20);
}

void draw() {  
  camera(width / 2, height / 2, depth, width / 2, height / 2, 0, 0, 1, 0); 

  directionalLight(50, 100, 125, 0, -1, 0); 
  ambientLight(102, 102, 102);

  background(MAX_INT); // white
    
  translate(width/2, height/2, 0);
        
  /* Negative angle for a more natural movement. */
  rotateX(-angleX);
  rotateZ(angleZ);

//  rotateY(angleY);
   
  box(boxWidth, boxThickness, boxDepth);
  
  mover.update(angleX, angleZ);
  mover.checkEdges();
  mover.display();
}

//void keyPressed() { 
//  if (key == CODED) {
//    if (keyCode == LEFT) { 
//      angleY += PI / 180.0;
//    } else if (keyCode == RIGHT) {
//      angleY -= PI / 180.0;
//    }
//  }
//}

void mouseDragged() {
  angleX += map(mouseY - pmouseY, -height, height, -speed / 100, speed / 100);
  angleZ += map(mouseX - pmouseX, -width, width, -speed / 100, speed / 100);  
  
  angleX = limitRotation(angleX, maxRotation);
  angleZ = limitRotation(angleZ, maxRotation);
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


