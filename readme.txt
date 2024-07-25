Programming Assignment 5: K-d Trees


/* *****************************************************************************
 *  Describe the Node data type you used to implement the
 *  2d-tree data structure.
 **************************************************************************** */
We just used the node data type which we got from the 226 website for the root
node. Our node follows the symbol table structure, in which every point
(containing a x and a y coordinate) is linked to a value of a generic data type.
Every point is stored inside a node which has a link to the left bottom subtree
"lb" and the right top subtree "rt". Each node also stores a bounding rectangle
"rect" to locate it in space.

/* *****************************************************************************
 *  Describe your method for range search in a k-d tree.
 **************************************************************************** */
We implemented a queue for range(), in which we start at the root
of the tree then traverse the left and right subtrees after
comparing the value (x or y coordinates based whether the level
is vertical or horizontal) with the minimum and maximum values of the rectangle.

We used the optimization with the intersection of the splitting line and the
rectangle, which works as follows:
If our examined node is less than our max of our query rectangle (x or y comparison
depending on the level), we check the right subtree of the current node.
If the node's coordinate (either the x or y coordinate comparison based on level)
is greater than the min of the query rectangle, we check the left subtree.
Therefore, if the current point of the node we're looking at is out of the range
(> xmax or < xmin), we don't call range–aka we do not traverse its subtrees.
Both subtrees may be traversed when the point of the node is within the rectangle's
bounds.

Every time we look at a node, we check to see if the point is contained in the
query rectangle and if it is, we add it to our queue.

/* *****************************************************************************
 *  Describe your method for nearest neighbor search in a k-d tree.
 **************************************************************************** */
We implemented nearest() by first calculating the squared distance between
the query point and every node point.

Then, accounting for the horizontal or vertical level through a level variable
(odd levels check x coordinates, even levels check odd coordinates)
We look at the left subtree first if the query point is on the node's left or
bottom, and we look at the right subtree first if the query point is to the
node's right or top.

We designed a variable 'closest' that stores the closest distance calculated
and updates every time a closer distance between a new x node point and the
query point is obtained. We used a recursive algorithm to traverse the tree,
and we prune off a node and its subtree if the distance of the query point to
the bounding box of the node's point is greater than the current closest distance.

/* *****************************************************************************
 *  How many nearest-neighbor calculations can your PointST implementation
 *  perform per second for input1M.txt (1 million points), where the query
 *  points are random points in the unit square?
 *
 *  Fill in the table below, rounding each value to use one digit after
 *  the decimal point. Use at least 1 second of CPU time. Do not use -Xint.
 *  (Do not count the time to read the points or to build the 2d-tree.)
 *
 *  Repeat the same question but with your KdTreeST implementation.
 *
 **************************************************************************** */


                 # calls to         /   CPU time     =   # calls to nearest()
                 client nearest()       (seconds)        per second
                ------------------------------------------------------
PointST:      250                       21.396 s         11.68442699570013

KdTreeST:     18000000                  85.271 s         211091.69588723013


Note: more calls per second indicates better performance.


/* *****************************************************************************
 *  Known bugs / limitations.
 **************************************************************************** */
n/a


/* *****************************************************************************
 *  Describe any serious problems you encountered.
 **************************************************************************** */
We encountered lots of problems when we were writing range() and nearest() for
kd tree because we had some redundant code involving if and else if statements
that were fixed after we cleared up our logic and wrote simpler conditional
statements. We were also confused with the conditionals that involved comparing
points to the boundaries of the rectangle.



/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback
 *  on how helpful the class meeting was and on how much you learned
 * from doing the assignment, and whether you enjoyed doing it.
 **************************************************************************** */
We were not given a lot of time or resources like office hours for this
assignment so we were very tight on time and stressed because of this!
