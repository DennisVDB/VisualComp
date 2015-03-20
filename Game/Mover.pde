class Mover { 
  PVector location;
  PVector velocity;

  float boarderX;
  float boarderZ;

  float radius;

  Mover(float boxWidth, float boxDepth, float boxThickness, float radius) {  
    float offset = -(radius + boxThickness / 2);
    this.location = new PVector(0, offset, 0); 
    
    this.velocity = new PVector(0, 0, 0);

    this.boarderX = (boxWidth / 2) - radius;
    this.boarderZ = (boxDepth / 2) - radius;
    
    this.radius = radius;
  }

  void update(float angleX, float angleZ) {
    PVector deltaGravity = accountForGravity(velocity, angleX, angleZ);
    PVector deltaFriction = accountForFriction(velocity);
    
    velocity.x += deltaGravity.x + deltaFriction.x;
    velocity.z += deltaGravity.z + deltaFriction.z;

    location.add(velocity);
  }

  void display() {
    fill(255);
    stroke(0);
    
    pushMatrix();

    translate(location.x, location.y, location.z);

    sphere(radius);
    
    popMatrix();
  }

  void checkEdges() {
    if (location.x <  -boarderX) {
      location.x = -boarderX;
      velocity.x = -velocity.x;
    } else if  (location.x > boarderX) {
      location.x = boarderX;
      velocity.x = -velocity.x;
    } 

    if (location.z < -boarderZ) {
      location.z = -boarderZ;
      velocity.z = -velocity.z;
    } else if (location.z > boarderZ) { 
      location.z = boarderZ;
      velocity.z = -velocity.z;
    }
  }

  PVector accountForGravity(PVector velocity, float angleX, float angleZ) { 
   float gravity = 3;
    
    return (new PVector(gravity * sin(angleZ), 0, gravity * sin(angleX)));
  }

  PVector accountForFriction(PVector velocity) {    
    float normalForce = 1;
    float mu = .5;
    float frictionMagnitude = normalForce * mu; 
    
    PVector friction = velocity.get(); 
    friction.mult(-1);
    friction.normalize(); 
    friction.mult(frictionMagnitude);
    
    return (new PVector(friction.x, 0, friction.z));
  }
}

