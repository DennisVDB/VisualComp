import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.video.Capture;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class HoughTransform extends PApplet {
    Capture cam;
    private PImage img;
    private HScrollbar brightnessBar;
    private HScrollbar colorBar;

    public void setup() {
//        size(640, 480);
//        String[] cameras = Capture.list();
//        if (cameras.length == 0) {
//            println("There are no cameras available for capture.");
//            exit(); } else {
//            println("Available cameras:");
//
//            for (int i = 0; i < cameras.length; i++) {
//                println(cameras[i]);
//            }
//
//            cam = new Capture(this, cameras[0]);
//            cam.start();
//        }

        size(800, 600);
        img = loadImage("board1.jpg");

        brightnessBar = new HScrollbar(this, 0, 580, 800, 20);
        colorBar = new HScrollbar(this, 0, 560, 800, 20);
    }

    public void draw() {
//        if (cam.available() == true) {
//            cam.read();
//        }
//
//        img = cam.get();
//        image(img, 0, 0);

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

        for (int i = 0; i < 3; i++) {
            result = gaussianBlur(result);
        }
        result = sobel(result);
        image(result, 0, 0);
        ArrayList<PVector> lines = hough(result);

        ArrayList<PVector> intersections = getIntersections(lines);
        plotIntersections(intersections);

//        image(result, 0, 0);
//        image(img, 0, 0);


        brightnessBar.display();
        brightnessBar.update();

        colorBar.display();
        colorBar.update();
    }

    private ArrayList<PVector> getIntersections(ArrayList<PVector> lines) {
        ArrayList<PVector> intersections = new ArrayList<PVector>();

        float x, y, d;
        PVector line1, line2;

        for (int i = 0; i < lines.size() - 1; i++) {
            line1 = lines.get(i);
            for (int j = i + 1; j < lines.size(); j++) {
                line2 = lines.get(j);
                d = cos(line2.y) * sin(line1.y) - cos(line1.y) * sin(line2.y);
                x = (line2.x * sin(line1.y) - line1.x * sin(line2.y)) / d;
                y = (-line2.x * cos(line1.y) + line1.x * cos(line2.y)) / d;

                intersections.add(new PVector(x, y));
            }
        }

        return intersections;
    }

    private void plotIntersections(ArrayList<PVector> intersections) {
        fill(255, 128, 0);

        for(PVector intersection : intersections) {
            ellipse(intersection.x, intersection.y, 10, 10);
        }
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
                for (int k = -N / 2; k <= N / 2; k++) {
                    for (int l = -N / 2; l <= N / 2; l++) {
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
                if (buffer[j * img.width + i] > (int) (max * 0.3f)) {
                    result.pixels[j * img.width + i] = color(255);
                } else {
                    result.pixels[j * img.width + i] = color(0);
                }
            }
        }

        return result;
    }

    private ArrayList<PVector> hough(PImage img) {
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

//        PImage houghImg = createImage(rDim + 2, phiDim + 2, ALPHA);
//        for (int i = 0; i < accumulator.length; i++) {
//            houghImg.pixels[i] = color(min(255, accumulator[i]));
//        }
//
//        houghImg.updatePixels();
//        houghImg.resize(640, 480);

        ArrayList<Integer> bestCandidates = new ArrayList<Integer>();

        optimizeCandidates(accumulator, bestCandidates);

        Collections.sort(bestCandidates, new HoughComparator(accumulator));

        ArrayList<PVector> lines = createLines(bestCandidates);

        plotLines(lines, img.width);

        return lines;
    }

    private void optimizeCandidates(int[] accumulator, ArrayList<Integer> bestCandidates) {
        float discretizationStepsPhi = 0.06f;
        float discretizationStepsR = 2.5f;

        int phiDim = (int) (Math.PI / discretizationStepsPhi);
        int rDim = (int) (((img.width + img.height) * 2 + 1) / discretizationStepsR);

        // size of the region we search for a local maximum
        int neighbourhood = 10;

        // only search around lines with more that this amount of votes
        // (to be adapted to your image)
        int minVotes = 200;
        for (int accR = 0; accR < rDim; accR++) {
            for (int accPhi = 0; accPhi < phiDim; accPhi++) {
                // compute current index in the accumulator
                int i = (accPhi + 1) * (rDim + 2) + accR + 1;

                if (accumulator[i] > minVotes) {
                    boolean bestCandidate = true;

                    // iterate over the neighbourhood
                    for (int dPhi = -neighbourhood / 2; dPhi < neighbourhood / 2 + 1; dPhi++) {
                        // check we are not outside the image
                        if (accPhi + dPhi < 0 || accPhi + dPhi >= phiDim) {
                            continue;
                        }

                        for (int dR = -neighbourhood / 2; dR < neighbourhood / 2 + 1; dR++) {
                            // check we are not outside the image
                            if (accR + dR < 0 || accR + dR >= rDim) {
                                continue;
                            }

                            int neighbourI = (accPhi + dPhi + 1) * (rDim + 2) + accR + dR + 1;

                            if (accumulator[i] < accumulator[neighbourI]) {
                                // the current idx is not a local maximum!
                                bestCandidate = false;
                                break;
                            }

                        }
                        if (!bestCandidate) {
                            break;
                        }

                    }
                    if (bestCandidate) {
                        // the current idx *is* a local maximum
                        bestCandidates.add(i);
                    }
                }
            }
        }
    }

    private ArrayList<PVector> createLines(ArrayList<Integer> bestCandidates) {
        float discretizationStepsPhi = 0.06f;
        float discretizationStepsR = 2.5f;
        int rDim = (int) (((img.width + img.height) * 2 + 1) / discretizationStepsR);

        ArrayList<PVector> lines = new ArrayList<PVector>();

        for(Integer i : bestCandidates) {
            // first, compute back the (r, phi) polar coordinates:
            int accPhi = i / (rDim + 2) - 1;
            int accR = i - (accPhi + 1) * (rDim + 2) - 1;
            float r = (accR - (rDim - 1) * 0.5f) * discretizationStepsR;
            float phi = accPhi * discretizationStepsPhi;

            lines.add(new PVector(r, phi));
        }

        return lines;
    }

    private void plotLines(ArrayList<PVector> lines, int width) {
        for (PVector line : lines) {
            float r = line.x;
            float phi = line.y;

            // Cartesian equation of a line: y = ax + b
            // in polar, y = (-cos(phi)/sin(phi))x + (r/sin(phi))
            // => y = 0 : x = r / cos(phi)
            // => x = 0 : y = r / sin(phi)
            // compute the intersection of this line with the 4 borders of // the image
            int x0 = 0;
            int y0 = (int) (r / sin(phi));
            int x1 = (int) (r / cos(phi));
            int y1 = 0;
            int x2 = width;
            int y2 = (int) (-cos(phi) / sin(phi) * x2 + r / sin(phi));
            int y3 = width;
            int x3 = (int) (-(y3 - r / sin(phi)) * (sin(phi) / cos(phi)));

            stroke(204, 102, 0);
            if (y0 > 0) {
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
                for (int k = -N / 2; k < N / 2; k++) {
                    for (int l = -N / 2; l < N / 2; l++) {
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