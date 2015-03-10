class Mover { 
  PVector location;
  PVector velocity;
  PVector gravity;
  
  float boarderX;
  float boarderZ;
  
  float radius;

  Mover() {
    location = new PVector(0, 0, 0); 
    velocity = new PVector(0.5, 0, 1);
    gravity = new PVector(0, 1, 0);
    
    boarderX = (width / 3.5) / 2;
    boarderZ = (width / 3.5) / 2;
    
    radius = 20;
  }

  void update() {
    velocity.mult(velocity.dot(gravity) / velocity.magSq());
    
    location.add(velocity);
  }

  void display() {
    fill(255);
    stroke(0);
    
    translate(location.x, -25, location.z);
    
    sphere(radius);
  }
  
  void checkEdges() {
    if (location.x <  -boarderX || location.x > boarderX)  {
      velocity.x = -velocity.x;
    } if (location.z < -boarderZ  || location.z > boarderZ) { 
      velocity.z = -velocity.z;
    }
  }
}

