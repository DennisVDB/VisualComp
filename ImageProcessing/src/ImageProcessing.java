import processing.core.PApplet;
import processing.core.PImage;

public class ImageProcessing extends PApplet {
    private PImage img;
    private HScrollbar brightnessBar;
    private HScrollbar colorBar;

    public void setup() {
        size(800, 600);
        img = loadImage("board1.jpg");
//        img = loadImage("chess.jpg");

        brightnessBar = new HScrollbar(this, 0, 580, 800, 20);
        colorBar = new HScrollbar(this, 0, 560, 800, 20);
    }

    public void draw() {
        background(color(0, 0, 0));

        PImage result = createImage(img.width, img.height, RGB);

        double brightnessThreshold = brightnessBar.getPos() * 256;
        double colorThreshold = colorBar.getPos() * 256;

        //System.out.println(colorThreshold);

        for (int i = 0; i < img.width * img.height; i++) {
            if (brightness(img.pixels[i]) < brightnessThreshold && hue(img.pixels[i]) < colorThreshold) {
                result.pixels[i] = img.pixels[i];
            }
        }

        result = gaussianBlur(result);
        result = sobel(result);
        image(img, 0, 0);
        result = hough(result);

//        image(result, 0, 0);
//        image(img, 0, 0);


        brightnessBar.display();
        brightnessBar.update();

        colorBar.display();
        colorBar.update();
    }

    private PImage gaussianBlur(PImage img) {
        float[][] kernel = {{9, 12, 9},
                            {12, 15, 12},
                            {9, 12, 9}};

        return convolute(img, kernel);
    }

    private PImage sobel(PImage img) {
        float[][] hKernel = {{0, 1, 0},
                             {0, 0, 0},
                             {0, -1, 0}};

        float[][] vKernel = {{0, 0, 0},
                             {1, 0, -1},
                             {0, 0, 0}};

        int N = 3;
        float max = 0;

        float sum_h = 0;
        float sum_v = 0;
        float sum;
        float[] buffer = new float[img.width * img.height];

        PImage result = createImage(img.width, img.height, ALPHA);

        // clear the image
        for (int i = 0; i < img.width * img.height; i++) {
            result.pixels[i] = color(0);
        }

        for (int i = 2; i < img.width - 2; i++) {
            for (int j = 2; j < img.height - 2; j++) {
                for (int k = -N/2; k <= N/2; k++) {
                    for (int l = -N/2; l <= N/2; l++) {
                        sum_v += brightness(img.pixels[(j + l) * img.width + (i + k)]) * vKernel[k + 1][l + 1];
                        sum_h += brightness(img.pixels[(j + l) * img.width + (i + k)]) * hKernel[k + 1][l + 1];
                    }
                }

                sum = sqrt(pow(sum_h, 2) + pow(sum_v, 2));

                if (sum > max) {
                    max = sum;
                }

                buffer[j * img.width + i] = sum;

                sum_v = 0;
                sum_h = 0;
            }
        }

        for (int i = 2; i < img.width - 2; i++) {
            for (int j = 2; j < img.height - 2; j++) {
                if (buffer[j * img.width + i] > (int)(max * 0.3f)) {
                    result.pixels[j * img.width + i] = color(255);
                } else {
                    result.pixels[j * img.width + i] = color(0);
                }
            }
        }

        return result;
    }

    private PImage hough(PImage img) {
        float discretizationStepsPhi = 0.06f;
        float discretizationStepsR = 2.5f;

        int r;

        int phiDim = (int) (Math.PI / discretizationStepsPhi);
        int rDim = (int) (((img.width + img.height) * 2 + 1) / discretizationStepsR);

        int offset = (rDim - 1) / 2;

        int[] accumulator = new int[(phiDim + 2) * (rDim + 2)];

        for (int x = 0; x < img.width; x++) {
            for (int y = 0; y < img.height; y++) {
                if (brightness(img.pixels[y * img.width + x]) != 0) {
                    for (int i = 0; i < phiDim; i++) {
                        r = (int) ((x * cos(i * discretizationStepsPhi) + y * sin(i * discretizationStepsPhi)) / discretizationStepsR);
                        r += offset;
                        accumulator[(i + 1) * (rDim + 2) + (r + 1)] += 1;
                    }
                }
            }
        }

        plotLines(img, discretizationStepsPhi, discretizationStepsR, rDim, accumulator);

        PImage houghImg = createImage(rDim + 2, phiDim + 2, ALPHA);
        for (int i = 0; i < accumulator.length; i++) {
            houghImg.pixels[i] = color(min(255, accumulator[i]));
        }

        houghImg.updatePixels();
        houghImg.resize(img.width, img.height);

        return houghImg;
    }

    private void plotLines(PImage img, float discretizationStepsPhi, float discretizationStepsR, float rDim, int[] accumulator) {
        for (int i = 0; i < accumulator.length; i++) {
            if (accumulator[i] > 300) {
                // first, compute back the (r, phi) polar coordinates:
                int accPhi = (int) (i / (rDim + 2)) - 1;
                int accR = (int) (i - (accPhi + 1) * (rDim + 2) - 1);
                float r = (accR - (rDim - 1) * 0.5f) * discretizationStepsR;
                float phi = accPhi * discretizationStepsPhi;

                // Cartesian equation of a line: y = ax + b
                // in polar, y = (-cos(phi)/sin(phi))x + (r/sin(phi))
                // => y = 0 : x = r / cos(phi)
                // => x = 0 : y = r / sin(phi)
                // compute the intersection of this line with the 4 borders of // the image
                int x0 = 0;
                int y0 = (int) (r / sin(phi));
                int x1 = (int) (r / cos(phi));
                int y1 = 0;
                int x2 = img.width;
                int y2 = (int) (-cos(phi) / sin(phi) * x2 + r / sin(phi));
                int y3 = img.width;
                int x3 = (int) (-(y3 - r / sin(phi)) * (sin(phi) / cos(phi)));

                // Finally, plot the lines
                stroke(204,102,0); if (y0 > 0) {
                    if (x1 > 0) {
                        line(x0, y0, x1, y1);
                    } else if (y2 > 0) {
                        line(x0, y0, x2, y2);
                    } else {
                        line(x0, y0, x3, y3);
                    }
                } else {
                    if (x1 > 0) {
                        if (y2 > 0) {
                            line(x1, y1, x2, y2);
                        } else {
                            line(x1, y1, x3, y3);
                        }
                    } else {
                        line(x2, y2, x3, y3);
                    }
                }
            }
        }
    }

    private PImage convolute(PImage img, float[][] kernel) {
        int N = 3;

        float weight = 1.f;
        int intensities = 0;

        // create a greyscale image (type: ALPHA) for output
        PImage result = createImage(img.width, img.height, ALPHA);

        for (int i = 0; i < img.width * img.height; i++) {
            result.pixels[i] = color(255);
        }

        for (int i = 1; i < img.width - 1; i++) {
            for (int j = 1; j < img.height - 1; j++) {
                for (int k = -N/2; k < N/2; k++) {
                    for (int l = -N/2; l < N/2; l++) {
                        intensities += brightness(img.pixels[(j + l) * img.width + (i + k)]) * kernel[k + 1][l + 1];
                    }
                }

                result.pixels[j * img.width + i] = color(intensities / weight);
                intensities = 0;
            }
        }

        return result;
    }
}
