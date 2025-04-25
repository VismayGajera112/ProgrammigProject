import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

class Point {
    double x, y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

class MutableDouble {
    double value;

    MutableDouble(double value) {
        this.value = value;
    }
}

public class ClosestPair {
    static int index1 = -1, index2 = -1;

    // Brute Force Approach (O(n^2))
    public static double bruteForce(Point[] points) {
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double dist = distance(points[i], points[j]);
                if (dist < minDist) {
                    minDist = dist;
                    index1 = i;
                    index2 = j;
                }
            }
        }
        return minDist;
    }

    // Custom Merge Sort for sorting points by x-coordinate
    private static void mergeSort(Point[] points, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(points, left, mid);
            mergeSort(points, mid + 1, right);
            merge(points, left, mid, right);
        }
    }

    private static void merge(Point[] points, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        Point[] leftArr = new Point[n1];
        Point[] rightArr = new Point[n2];

        for (int i = 0; i < n1; i++)
            leftArr[i] = points[left + i];
        for (int j = 0; j < n2; j++)
            rightArr[j] = points[mid + 1 + j];

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (leftArr[i].x <= rightArr[j].x) {
                points[k++] = leftArr[i++];
            } else {
                points[k++] = rightArr[j++];
            }
        }

        while (i < n1)
            points[k++] = leftArr[i++];
        while (j < n2)
            points[k++] = rightArr[j++];
    }

    // Divide and Conquer Approach (O(n log n))
    public static double closestPair(Point[] points) {
        mergeSort(points, 0, points.length - 1);
        return closestPairRecursive(points, 0, points.length - 1);
    }

    private static double closestPairRecursive(Point[] points, int left, int right) {
        if (right - left <= 3) {
            return bruteForce(java.util.Arrays.copyOfRange(points, left, right + 1));
        }

        int mid = (left + right) / 2;
        double leftDist = closestPairRecursive(points, left, mid);
        double rightDist = closestPairRecursive(points, mid + 1, right);
        MutableDouble minDist = new MutableDouble(Math.min(leftDist, rightDist));

        return mergeStep(points, left, right, mid, minDist);
    }

    private static double mergeStep(Point[] points, int left, int right, int mid, MutableDouble minDist) {
        Point midPoint = points[mid];
        Point[] strip = java.util.Arrays.stream(points, left, right + 1)
                .filter(p -> Math.abs(p.x - midPoint.x) < minDist.value)
                .sorted(Comparator.comparingDouble(p -> p.y))
                .toArray(Point[]::new);

        for (int i = 0; i < strip.length; i++) {
            for (int j = i + 1; j < strip.length && (strip[j].y - strip[i].y) < minDist.value; j++) {
                double dist = distance(strip[i], strip[j]);
                if (dist < minDist.value) {
                    minDist.value = dist;
                    index1 = i;
                    index2 = j;
                }
            }
        }
        return minDist.value;
    }

    private static double distance(Point p1, Point p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    public static void main(String[] args) {
        int[] sizes = { 10000, 20000, 30000, 40000, 50000, 60000, 70000, 80000, 90000, 100000 };
        int iterations = 10;
        Random rand = new Random();

        try (FileWriter writer = new FileWriter("results.txt")) {
            for (int size : sizes) {
                double totalTimeBrute = 0;
                double totalTimeDivide = 0;

                for (int i = 0; i < iterations; i++) {

                    Point[] points = new Point[size];
                    Set<String> seen = new HashSet<>();

                    // Generate unique random points
                    int j = 0;
                    while (j < size) {
                        double x = rand.nextDouble() * 1000000;
                        double y = rand.nextDouble() * 1000000;
                        String key = x + "," + y;

                        if (!seen.contains(key)) {
                            points[j] = new Point(x, y);
                            seen.add(key);
                            j++;
                        }
                    }

                    long startBrute = System.nanoTime();
                    bruteForce(points);
                    long endBrute = System.nanoTime();
                    totalTimeBrute += (endBrute - startBrute) / 1e6;

                    long startDivide = System.nanoTime();
                    closestPair(points);
                    long endDivide = System.nanoTime();
                    totalTimeDivide += (endDivide - startDivide) / 1e6;
                }

                System.out.println("Final Results for n = " + size);
                System.out.println("Avg Brute Force RT: " + (totalTimeBrute / iterations) + " ms");
                System.out.println("Avg Divide & Conquer RT: " + (totalTimeDivide / iterations) + " ms");
                System.out.println("--------------------------------------------");

                writer.write("Final Results for n = " + size + "\n");
                writer.write("Avg Brute Force RT: " + (totalTimeBrute / iterations) + " ms\n");
                writer.write("Avg Divide & Conquer RT: " + (totalTimeDivide / iterations) + " ms\n");
                writer.write("--------------------------------------------\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Uncomment the following lines to test the brute force and divide and conquer methods on random points
         
        // Test the brute force method
        // Point[] testPoints = new Point[10];
        // for (int i = 0; i < testPoints.length; i++) {
        // testPoints[i] = new Point(rand.nextDouble() * 1000, rand.nextDouble() *
        // 1000);
        // }

        // double bruteForceResult = bruteForce(testPoints);
        // System.out.println("Brute Force Result: " + bruteForceResult);
        // System.out.println("Closest pair indices: " + index1 + " and " + index2);
        // System.out.println("Closest pair points: (" + testPoints[index1].x + ", " +
        // testPoints[index1].y + ") and (" + testPoints[index2].x + ", " +
        // testPoints[index2].y + ")");

        // // Test the divide and conquer method
        // index1 = -1;
        // index2 = -1;
        // double divideResult = closestPair(testPoints);
        // System.out.println("Divide and Conquer Result: " + divideResult);
        // System.out.println("Closest pair indices: " + index1 + " and " + index2);
        // System.out.println("Closest pair points: (" + testPoints[index1].x + ", " +
        // testPoints[index1].y + ") and (" + testPoints[index2].x + ", " +
        // testPoints[index2].y + ")");
    }
}
