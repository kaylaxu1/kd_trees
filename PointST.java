import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class PointST<Value> {
    private RedBlackBST<Point2D, Value> points; // symbol table of points
    // construct an empty symbol table of points
    public PointST() {
        points = new RedBlackBST<>();
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return points.isEmpty();
    }

    // number of points
    public int size() {
        return points.size();
    }

    // associate the value val with point p
    public void put(Point2D p, Value val) {
        if (p == null || val == null)
            throw new IllegalArgumentException("Point is null!");
        points.put(p, val);
    }

    // value associated with point p
    public Value get(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("Point is null!");
        return points.get(p);
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("Point is null!");
        return points.contains(p);
    }

    // all points in the symbol table
    public Iterable<Point2D> points() {
        return points.keys();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("Point is null!");

        Stack<Point2D> stack = new Stack<Point2D>();
        for (Point2D p: points.keys()) {
            if (rect.contains(p))
                stack.push(p);
        }
        return stack;
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        if (points.isEmpty())
            return null;

        if (p == null)
            throw new IllegalArgumentException("Point is null!");

        double closestDistance = Double.POSITIVE_INFINITY;

        Point2D closestPoint = null;
        for (Point2D pt: points.keys()) {
            double dist = pt.distanceSquaredTo(p);
            if (dist < closestDistance) {
                closestDistance = dist;
                closestPoint = pt;
            }
        }
        return closestPoint;
    }

    // unit testing (required)
    public static void main(String[] args) {
        Point2D p1 = new Point2D(-5, 6);
        Point2D p2 = new Point2D(3, 6);
        Point2D p3 = new Point2D(10, 6);

        PointST<Integer> points = new PointST<>();
        points.put(p1, 1);
        points.put(p2, 1);
        points.put(p3, 1);

        StdOut.println("Empty?" + points.isEmpty());
        StdOut.println("Points: " + points.points());
        StdOut.println("Size: " + points.size());
        StdOut.println("Contains p1? " + points.contains(p1));
        StdOut.println("Nearest point to p1: " + points.nearest(p1));
        StdOut.println("Value of p1: " + points.get(p1));

        // should have (-5, 6) and (3, 6)
        RectHV rect = new RectHV(-256, 128, 384, 1024);
        StdOut.println(points.range(rect));

        // testing time to nearest

        In in = new In(args[0]);
        int index = 1;
        PointST<Integer> tree2 = new PointST<>();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D pt = new Point2D(x, y);
            tree2.put(pt, index);
            index++;
        }

        int m = 250;
        Stopwatch stopwatch = new Stopwatch();
        for (int i = 0; i < m; i++) {
            double x = StdRandom.uniformDouble(0.0, 1.0);
            double y = StdRandom.uniformDouble(0.0, 1.0);
            tree2.nearest(new Point2D(x, y));
        }
        double t = stopwatch.elapsedTime();
        StdOut.println("CPU elapsed time: " + t);
        StdOut.print("Estimate of the number of calls per second: " + m/t);
    }

}
