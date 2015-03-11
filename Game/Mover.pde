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
    velocity = accountForGravity(velocity, angleX, angleZ);
    velocity = accountForFriction(velocity);

    location.add(velocity);
  }

  void display() {
    fill(255);
    stroke(0);

    translate(location.x, location.y, location.z);

    sphere(radius);
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
    return (new PVector(velocity.x - .5 * sin(angleZ), 0, velocity.z + .5 * sin(angleX)));
  }

  PVector accountForFriction(PVector velocity) {    
    float normalForce = 1;
    float mu = 0.01;
    float frictionMagnitude = normalForce * mu; 
    
    PVector friction = velocity.get(); 
    friction.mult(-1);
    friction.normalize(); 
    friction.mult(frictionMagnitude);
    
    return (new PVector(velocity.x + friction.x, 0, velocity.z + friction.z));
  }
}

