import processing.core.PApplet;
import processing.core.PImage;

public class ImageProcessing extends PApplet {
    private PImage img;
    private HScrollbar brightnessBar;
    private HScrollbar colorBar;

    public void setup() {
        size(800, 600);
        img = loadImage("board1.jpg");

        brightnessBar = new HScrollbar(this, 0, 580, 800, 20);
        colorBar = new HScrollbar(this, 0, 560, 800, 20);
    }

    public void draw() {
        background(color(0, 0, 0));

        PImage result = createImage(width, height, RGB);

        double brightnessThreshold = brightnessBar.getPos() * 256;
        double colorThreshold = colorBar.getPos() * 256;

        //System.out.println(colorThreshold);

        for (int i = 0; i < img.width * img.height; i++) {
            if (brightness(img.pixels[i]) < brightnessThreshold && hue(img.pixels[i]) < colorThreshold) {
                result.pixels[i] = img.pixels[i];
            }
        }

        result = sobel(result);
        result = hough(result);

        System.out.println("display");

        image(result, 0, 0);

        brightnessBar.display();
        brightnessBar.update();

        colorBar.display();
        colorBar.update();
    }

    private PImage gaussianBlur(PImage img) {
        float[][] kernel = {{9, 12, 9},
                            {12, 15, 12},
                            {9, 12, 9}};

//        float[][] kernel = {{0, 0, 0},
//                {0, 2, 0},
//                {0, 0, 0}};

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

    private PImage hough(PImage edgeImg) {
        float discretizationStepsPhi = 0.06f;
        float discretizationStepsR = 2.5f;

        float phi;
        float r;

        int phiDim = (int) (Math.PI / discretizationStepsPhi);
        int rDim = (int) (((edgeImg.width + edgeImg.height) * 2 + 1) / discretizationStepsR);

        int[] accumulator = new int[(phiDim + 2) * (rDim + 2)];

        for (int y = 0; y < edgeImg.height; y++) {
            for (int x = 0; x < edgeImg.width; x++) {
                if (brightness(edgeImg.pixels[y * edgeImg.width + x]) != 0) {
                    for (int i = 0; i < phiDim; i++) {
                        phi = i * discretizationStepsPhi;
                        r = x * cos(phi) + y * sin(phi);
                        for (int j = 0; j < rDim - 1; j++) {
                            r += (rDim - 1) / 2.f;
                            if (r >= j * discretizationStepsR && r < (j + 1) * discretizationStepsR) {
                                accumulator[j * rDim + i] += 1;
                            }
                        }
                    }
                }
            }
        }

        PImage houghImg = createImage(rDim + 2, phiDim + 2, ALPHA);
        for (int i = 0; i < accumulator.length; i++) {
            if (accumulator[i] > 0) {
                System.out.println(accumulator[i]);
            }
            houghImg.pixels[i] = color(min(255, accumulator[i]));
        }

        houghImg.updatePixels();

        return houghImg;
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
