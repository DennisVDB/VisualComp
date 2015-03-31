class Tree {
  private PShape tree;
  private PVector position;
  private float radius;

  public Tree(PVector position) {
    this.tree = loadShape("data/tree.obj");
    this.tree.scale(10);
    
    this.position = position;
    this.radius = 50;
  }
  
  public void display() { 
    pushMatrix();
        
    translate(position.x, 0, position.z);
    
    /* Rotate in order to be in the right position. */
    rotateX(PI);
    
    shape(tree);
    
    popMatrix();
  }
  
  public PVector getPosition() {
    return new PVector(position.x, position.y, position.z);
  }
    
  public float getRadius() {
    return radius;
  }
}

