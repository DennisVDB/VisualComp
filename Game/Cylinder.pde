class Cylinder {
  float cylinderBaseSize = 50; 
  float cylinderHeight = 50;
  int cylinderResolution = 40;
  
  PVector position;
  
  PShape cylinder = new PShape();

  public Cylinder(PVector position) {
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

    cylinder.endShape();   
    /* --- */
    
    /* Cylinder top */
    PShape cylinderTop = createShape();
    cylinderTop.beginShape(TRIANGLE_FAN);
    
    cylinderTop.vertex(0, 0, 0);
    
     for (int i = 0; i < x.length; i++) { 
      cylinderTop.vertex(x[i], y[i], 0);
      cylinderTop.vertex(x[i], y[i], 0);
    }
    
    cylinderTop.endShape(); 
    /* --- */
    
    /* Cylinder bottom */
    PShape cylinderBottom = createShape();
    cylinderBottom.beginShape(TRIANGLE_FAN);
    
    cylinderBottom.vertex(0, 0, cylinderHeight);
    
     for (int i = 0; i < x.length; i++) { 
      cylinderBottom.vertex(x[i], y[i], cylinderHeight);
      cylinderBottom.vertex(x[i], y[i], cylinderHeight);
    }
    
    cylinderBottom.endShape(); 
    /* --- */
    
    cylinder.addChild(cylinderBody);
    cylinder.addChild(cylinderTop);
    cylinder.addChild(cylinderBottom);
  }
  
  public PShape getShape() {
    return cylinder;
  }
  
  public PVector getPosition() {
    return position;
  }
}

