class Cylinder {
  PShape cylinder = new PShape();

  public Cylinder(float cylinderBase, float cylinderHeight, int cylinderResolution) {
    float angle;
    float[] x = new float[cylinderResolution + 1]; 
    float[] y = new float[cylinderResolution + 1];

    //get the x and y position on a circle for all the sides
    for (int i = 0; i < x.length; i++) {
      angle = (TWO_PI / cylinderResolution) * i; 
      x[i] = sin(angle) * cylinderBaseSize; 
      y[i] = cos(angle) * cylinderBaseSize;
    }

    cylinder = createShape();
    cylinder.beginShape(QUAD_STRIP);

    //draw the border of the cylinder
    for (int i = 0; i < x.length; i++) { 
      cylinder.vertex(x[i], y[i], 0); 
      cylinder.vertex(x[i], y[i], cylinderHeight);
    }

    cylinder.endShape();
  }
  
  public PShape getShape() {
    return cylinder;
  }
}

