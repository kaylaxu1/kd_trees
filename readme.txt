We used a node data type. Our node follows the symbol table structure, in which every point
(containing a x and a y coordinate) is linked to a value of a generic data type.
Every point is stored inside a node which has a link to the left bottom subtree
"lb" and the right top subtree "rt". Each node also stores a bounding rectangle
"rect" to locate it in space.

Range search algorithm: we implemented a queue for range(), in which we start at the root
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

nearest neighbor search in a k-d tree: We first calculated the squared distance between
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
