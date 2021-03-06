void setup() {
  size(1000, 1000, P2D);
}

float scale = 1; 
float rX = 0;
float rY = 0;

float dimX = 100;
float dimY = 150;
float dimZ = 300;

void draw() {
  background(255,255,255);
  My3DPoint eye = new My3DPoint(0, 0, -5000);
  My3DPoint origin = new My3DPoint(0, 0, 0);
  My3DBox input3DBox = new My3DBox(origin, dimX, dimY, dimZ);
  
  float[][] t = translationMatrix(dimX/(-2),dimY/(-2),dimZ/(-2));
  input3DBox = transformBox(input3DBox, t);
  
  float[][] transform1 = rotateXMatrix(rX);
  input3DBox = transformBox(input3DBox, transform1);
  
  float[][] transform2 = rotateYMatrix(rY);
  input3DBox = transformBox(input3DBox, transform2);
  
  float[][] transform3 = scaleMatrix(scale, scale, scale);
  input3DBox = transformBox(input3DBox, transform3);
  
  float[][] transform4= translationMatrix(width/2 - dimX/2, height/2 - dimY/2, 0);
  input3DBox = transformBox(input3DBox, transform4);
  
  projectBox(eye, input3DBox).render();
}

void mouseDragged() {
  scale += map(mouseY - pmouseY, -height, height, -1, 1);
}

void keyPressed() {
  if (key == CODED) {
    if(keyCode == UP) {
      rX -= PI/180;
    }
    else if (keyCode == DOWN) {
      rX += PI/180;
    }
     if(keyCode == RIGHT) {
      rY -= PI/180;
    }
     if(keyCode == LEFT) {
      rY += PI/180;
    }
  }
}

class My2DPoint {
  float x;
  float y;
  My2DPoint(float x, float y) {
    this.x = x;
    this.y = y;
  }
}

class My3DPoint {
  float x;
  float y;
  float z;
  My3DPoint(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}

My2DPoint projectPoint(My3DPoint eye, My3DPoint p) {
  return new My2DPoint((p.x-eye.x)*(-eye.z/(p.z-eye.z)), (p.y-eye.y)*(-eye.z/(p.z-eye.z)));
}

class My2DBox {
  My2DPoint[] s;
  My2DBox(My2DPoint[] s) {
    this.s = s;
  }
  void render() {
    strokeWeight(4);
    stroke(0,255,0);
    line(s[4].x, s[4].y, s[5].x, s[5].y);
    strokeWeight(4);
    stroke(0,255,0);
    line(s[4].x, s[4].y, s[7].x, s[7].y);
    strokeWeight(4);
    stroke(0,255,0);
    line(s[5].x, s[5].y, s[6].x, s[6].y);
    strokeWeight(4);
    stroke(0,255,0);
    line(s[6].x, s[6].y, s[7].x, s[7].y);
    
    strokeWeight(4);
    stroke(0,0,255);
    line(s[0].x, s[0].y, s[4].x, s[4].y);
    strokeWeight(4);
    stroke(0,0,255);
    line(s[1].x, s[1].y, s[5].x, s[5].y);
    strokeWeight(4);
    stroke(0,0,255);
    line(s[2].x, s[2].y, s[6].x, s[6].y);
     strokeWeight(4);
    stroke(0,0,255);
    line(s[3].x, s[3].y, s[7].x, s[7].y);
    
    strokeWeight(4);
    stroke(255,0,0);
    line(s[0].x, s[0].y, s[1].x, s[1].y);
    strokeWeight(4);
    stroke(255,0,0);
    line(s[0].x, s[0].y, s[3].x, s[3].y);
    strokeWeight(4);
    stroke(255,0,0);
    line(s[1].x, s[1].y, s[2].x, s[2].y);  
    strokeWeight(4);
    stroke(255,0,0);
    line(s[2].x, s[2].y, s[3].x, s[3].y);
    
  }
}

class My3DBox {
  My3DPoint[] p;
  My3DBox(My3DPoint origin, float dimX, float dimY, float dimZ) {
    float x = origin.x;
    float y = origin.y;
    float z = origin.z;
    this.p = new My3DPoint[] {
      new My3DPoint(x, y+dimY, z+dimZ), 
      new My3DPoint(x, y, z+dimZ), 
      new My3DPoint(x+dimX, y, z+dimZ), 
      new My3DPoint(x+dimX, y+dimY, z+dimZ), 
      new My3DPoint(x, y+dimY, z), 
      origin, 
      new My3DPoint(x+dimX, y, z), 
      new My3DPoint(x+dimX, y+dimY, z)
    };
  }
  My3DBox(My3DPoint[] p) {
    this.p = p;
  }
}

My2DBox projectBox (My3DPoint eye, My3DBox box) {
  My2DPoint[] s = new My2DPoint[8];
  for (int i = 0; i< 8; i++) {
    s[i] = projectPoint(eye, box.p[i]);
  }
  return new My2DBox(s);
}

float[] homogeneous3DPoint (My3DPoint p) {
  float[] result = {
    p.x, p.y, p.z, 1
  };
  return result;
}

float[][] rotateXMatrix(float angle) {
  return(new float[][] {
    {1, 0, 0, 0}, 
    {0, cos(angle), sin(angle), 0}, 
    {0, -sin(angle), cos(angle), 0}, 
    {0, 0, 0, 1}});
}

float[][] rotateYMatrix(float angle) {
  return(new float[][] {
    {cos(angle), 0, -sin(angle), 0}, 
    {0, 1, 0, 0}, 
    {sin(angle), 0, cos(angle), 0}, 
    {0, 0, 0, 1}});
}

float[][] rotateZMatrix(float angle) {
  return(new float[][] {
    {cos(angle), sin(angle), 0, 0}, 
    {-sin(angle), cos(angle), 0, 0}, 
    {0, 0, 1, 0}, 
    {0, 0, 0, 1}});
}

float[][] scaleMatrix(float x, float y, float z) {
  return(new float[][] {
    {x, 0, 0, 0}, 
    {0, y, 0, 0}, 
    {0, 0, z, 0}, 
    {0, 0, 0, 1}});
}

float[][] translationMatrix(float x, float y, float z) {
  return(new float[][] {
    {1, 0, 0, x}, 
    {0, 1, 0, y}, 
    {0, 0, 1, z}, 
    {0, 0, 0, 1}});
}

float[] matrixProduct(float[][] a, float[] b) {
  float[] result = {0,0,0,0};
  for(int i = 0; i<4; i++){
    for(int j = 0; j<4; j++) {
      result[i] += a[i][j] * b[j];
    }
  }
  return result;
}

My3DBox transformBox(My3DBox box, float[][] transformMatrix) {
  My3DPoint[] points = new My3DPoint[8];
  for(int i = 0; i<8; i++){
    points[i] = euclidian3DPoint(matrixProduct(transformMatrix,homogeneous3DPoint(box.p[i])));
  }
  return new My3DBox(points);
}

My3DPoint euclidian3DPoint (float[] a){
  My3DPoint result = new My3DPoint(a[0]/a[3], a[1]/a[3], a[2]/a[3]);
  return result;
}
