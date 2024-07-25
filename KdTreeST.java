import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class KdTreeST<Value> {
    // construct an empty symbol table of points
    private Node root;
    private int size; // size of the symbol table (number of pts)

    // construct a Kd tree root node, initialize size
    public KdTreeST() {
        root = new Node();
        size = 0;
    }

    private class Node {
        private Point2D p;     // the point
        private Value val;     // the symbol table maps the point to this value
        private RectHV rect;   // the axis-aligned rectangle corresponding to this node
        private Node lb;       // the left/bottom subtree
        private Node rt;       // the right/top subtree
    }

        // is the symbol table empty?
        public boolean isEmpty() {
            return size == 0;
        }

        // number of points
        public int size() {
            return size;
        }

        // associate the value val with point p
        public void put(Point2D p, Value val) {
            if (p == null || val == null)
                throw new IllegalArgumentException("Null key");

            RectHV rect = new RectHV(Double.NEGATIVE_INFINITY,
                                     Double.NEGATIVE_INFINITY,
                                     Double.POSITIVE_INFINITY,
                                     Double.POSITIVE_INFINITY);
            root = put(root, p, val, 1, rect);
        }

        // finds location to put point p
        private Node put(Node x, Point2D p, Value val, int level, RectHV rect) {
            if (x == null || isEmpty()) {
                Node ret = new Node();
                ret.p = p;
                ret.val = val;
                ret.rect = rect;
                size++;
                return ret;
            }
            double xC = p.x();
            double yC = p.y();

            // overwrite if equal
            if (xC == x.p.x() && yC == x.p.y())  x.val = val;

            else if (level % 2 == 1) {
                if (xC < x.p.x()) {
                    x.lb = put(x.lb, p, val, level + 1,
                               new RectHV(rect.xmin(), rect.ymin(), x.p.x(),
                                          rect.ymax()));
                }
                else x.rt = put(x.rt, p, val, level + 1,
                                new RectHV(x.p.x(), rect.ymin(), rect.xmax(),
                                           rect.ymax()));
            }

            else {
                if (yC < x.p.y()) x.lb = put(x.lb, p, val, level + 1,
                                            new RectHV(rect.xmin(), rect.ymin(),
                                                       rect.xmax(), x.p.y()));
                else if (yC >= x.p.y()) x.rt = put(x.rt, p, val, level + 1,
                                new RectHV(rect.xmin(), x.p.y(), rect.xmax(),
                                           rect.ymax()));
            }
            return x;
        }

        // value associated with point p
        public Value get(Point2D p) {
            if (p == null)
                throw new IllegalArgumentException("Point is null!");

            return get(root, p, 1);
        }

        // traverse the tree to get the value
        private Value get(Node x, Point2D p, int level) {
            if (isEmpty() || x == null) {
                return null;
            }
            double xC = p.x(); // x coordinate of point we're trying to insert
            double yC = p.y(); // y coordinate of point we're trying to insert

            if (x.p.equals(p))
                return x.val;
            else {
                if (level % 2 == 1) {
                    if (xC < x.p.x()) return get(x.lb, p, level + 1);
                    else return get(x.rt, p, level + 1);
                }
                else {
                    if (yC < x.p.y()) return get(x.lb, p, level + 1);
                    else return get(x.rt, p, level + 1);
                }
            }
        }

        // does the symbol table contain point p?
        public boolean contains(Point2D p) {
            if (p == null) throw new IllegalArgumentException("Point is null!");
            return get(p) != null;
        }

        // all points in the symbol table
        public Iterable<Point2D> points() {
            Queue<Point2D> keys = new Queue<Point2D>();
            if (isEmpty())
                return keys;

            Queue<Node> queue = new Queue<Node>();
            queue.enqueue(root);
            while (!queue.isEmpty()) {
                Node x = queue.dequeue();
                if (x == null) continue;
                keys.enqueue(x.p);
                queue.enqueue(x.lb);
                queue.enqueue(x.rt);
            }
            return keys;
        }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("Argument is null");

        Queue<Point2D> queue = new Queue<Point2D>();

        if (isEmpty())
            return queue; // returns empty queue

        range(root, rect, queue, true);
        return queue;
    }

    // find points in range of a query rectangle
    private void range(Node x, RectHV rect, Queue<Point2D> queue,
                       boolean vertical) {
        if (x == null)
            return;

        if (rect.contains(x.p)) {
            queue.enqueue(x.p);
        }

        double xC = x.p.x();
        double yC = x.p.y();
        if (vertical) {
            if (xC <= rect.xmax())
                range(x.rt, rect, queue, false);
            if (xC >= rect.xmin())
                range(x.lb, rect, queue, false);
        }

        else {  // horizontal line segments
            if (yC <= rect.ymax())
                range(x.rt, rect, queue, true);
            if (yC >= rect.ymin())
                range(x.lb, rect, queue, true);
        }
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("Point is null");
        if (isEmpty())
            return null;
        double dist = root.p.distanceSquaredTo(p);
        return nearest(root, p, root.p, dist, 1);
    }

    // finds closest point; prunes if closest point cannot exist in subtree
    private Point2D nearest(Node x, Point2D query, Point2D closest,
                            double closestDist, int level) {
        if (x == null)
            return closest;

        // prune off the point (cannot possibly be closest)
        if (x.rect.distanceSquaredTo(query) > closestDist) {
            return closest;
        }

        double queryToPointDist = x.p.distanceSquaredTo(query);

        Point2D pointClosest = closest;
        double closeDist = closestDist;
        if (queryToPointDist < closestDist) {
            pointClosest = x.p;
            closeDist = queryToPointDist;
        }
        double diff;
        if (level % 2 == 1) {
            diff = query.x() - x.p.x();
        }
        else diff = query.y() - x.p.y();

        if (diff < 0) {
            // look at left first if query is on the node's left side
            closest = nearest(x.lb, query, pointClosest, closeDist, level + 1);
            closest = nearest(x.rt, query, closest,
                              closest.distanceSquaredTo(query), level + 1);
        }

        else {
            // look at right subtree first if query is above the node
            closest = nearest(x.rt, query, pointClosest, closeDist, level + 1);
            closest = nearest(x.lb, query, closest,
                              closest.distanceSquaredTo(query), level + 1);
        }

        return closest;
    }

    // unit testing (required)
    public static void main(String[] args) {
        KdTreeST<Integer> tree = new KdTreeST<>();
        Point2D p1 = new Point2D(1, 3); // root
        Point2D p2 = new Point2D(3, 1); // should be right half subplane
        Point2D p3 = new Point2D(3, 2); // p3 should be top half of right half
        Point2D p4 = new Point2D(2, 0); // should go to the left of p3
        tree.put(p1, 1);
        tree.put(p2, 5);
        tree.put(p3, 3);
        tree.put(p4, 4);

        StdOut.println(tree.contains(p1)); // should print true
        StdOut.println(tree.contains(new Point2D(4, 7))); // should print false
        StdOut.println(tree.points());

        StdOut.println(tree.get(p1));
        StdOut.println(tree.get(p2));
        StdOut.println(tree.get(p3));
        StdOut.println(tree.get(p4));
        StdOut.println("Is tree empty? " + tree.isEmpty());

        StdOut.println("Tree SIZE: " + tree.size());

        // below code should contain points p2 and p4
        RectHV rect = new RectHV(0, 0, 3, 1);
        StdOut.println(tree.range(rect));

        Point2D p5 = new Point2D(3, -100);
        // should print p5
        StdOut.println("Nearest Neighbor: " + tree.nearest(p5));

        StdOut.println("Should be null:" + tree.get(p5));

        // testing time to nearest
        In in = new In(args[0]);
        int index = 1;
        KdTreeST<Integer> tree2 = new KdTreeST<>();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D pt = new Point2D(x, y);
            tree2.put(pt, index);
            index++;
        }

        Point2D query = new Point2D(0.103, 0.149);
        StdOut.println("NEAREST POINT TIGERFILE TEST: " + tree2.nearest(query));

        int m = 18000000;
        Stopwatch stopwatch = new Stopwatch();
        for (int i = 0; i < m; i++) {
            double x = StdRandom.uniformDouble(0.0, 1.0);
            double y = StdRandom.uniformDouble(0.0, 1.0);
            tree2.nearest(new Point2D(x, y));
        }
        double t = stopwatch.elapsedTime();
        StdOut.println("CPU Elapsed time: " + t);
        StdOut.print("Estimate of the number of calls per second: " + m/t);

        StdOut.println("Tree 2 contains? "
                               + tree2.contains(new Point2D(0.32, 0.75)));

        Point2D a = new Point2D(0.75, 0.875);
        Point2D b = new Point2D(1.0, 0.375);
        Point2D c = new Point2D(0.25, 0.125);
        Point2D d = new Point2D(0.125, 0.25);
        Point2D e = new Point2D(0.5, 0.75);
        Point2D f = new Point2D(0.625, 0.75);
        Point2D g = new Point2D(0.5, 0.125);
        Point2D h = new Point2D(0.75, 0.5);
        Point2D i = new Point2D(0.5, 0.625);
        Point2D j = new Point2D(0.625, 0.875);
        Point2D k = new Point2D(0.375, 1.0);
        Point2D lL = new Point2D(0.375, 0.25);
        Point2D m1 = new Point2D(0.125, 0.5);
        Point2D n = new Point2D(0.25, 0.625);
        Point2D oO = new Point2D(0.875, 0.375);

        KdTreeST<String> tree3 = new KdTreeST<>();
        tree3.put(a, "A");
        tree3.put(b, "B");
        tree3.put(c, "C");
        tree3.put(d, "D");
        tree3.put(e, "E");
        tree3.put(f, "F");
        tree3.put(g, "G");
        tree3.put(h, "H");
        tree3.put(i, "I");
        tree3.put(j, "J");
        tree3.put(k, "K");
        tree3.put(lL, "L");
        tree3.put(m1, "M");
        tree3.put(n, "N");
        tree3.put(oO, "O");

        StdOut.println("CONTAINS: " + tree3.contains(new Point2D(0.25, 0.75)));
        Point2D query1 = new Point2D(0.625, 0.125);
        Point2D nearest = tree3.nearest(query1);
        StdOut.println("NEAREST: " + nearest);
        StdOut.println("SQUARED DISTANCE" + query1.distanceSquaredTo(nearest));

        RectHV rectTest = new RectHV(0.5, 0.0, 0.75, 1);
        StdOut.println("Within Range:");
        StdOut.println(tree3.range(rectTest));
        for (Point2D p: tree3.range(rectTest))
            StdOut.print(tree3.get(p) + " ");

        StdOut.println("Tree 3 points:" + tree3.points());
        StdOut.println("Tree 3 with duplicate points:" + tree3.points());
        StdOut.println("Contains?" + tree3.contains(new Point2D(0.5, 0.48)));
        StdOut.println("Get? " + tree3.get(g));

        RectHV rect1 = new RectHV(0.039, 0.272, 0.666, 0.57);
        StdOut.println("Range of input tree: " + tree3.range(rect1));
    }
}

