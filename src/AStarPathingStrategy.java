import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStarPathingStrategy
        implements PathingStrategy
{
    public int manhattanDistance(Point p1, Point p2)
    {
        return 10 * (Math.abs(p2.x - p1.x) + Math.abs(p2.y - p1.y));
    }

    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        // canPassThrough checks if the argument (Point) is within bounds and not an obstacle
        // withinReach checks if the argument points are 1 away from each other
        // potentialNeighbors takes in an argument point and returns a stream builder of its neighbors (only cardinal right now)
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(Node::getF)
                .thenComparing((n1, n2) -> n2.getG() - n1.getG()));
        HashMap<Point, Node> openMap = new HashMap<>();
        HashMap<Point, Node> closedMap = new HashMap<>();
        Node startNode = new Node(start, manhattanDistance(start, end), 0, manhattanDistance(start, end), null);
        closedMap.put(start, startNode);
        Node currentNode = startNode;
        List<Point> path = new LinkedList<>();
        List<Node> toAdd = new LinkedList<>();
        List<Node> toAddDiagonal;
        List<Node> toAddCardinal;
        boolean nextToEnd = false;
        BiPredicate<Point, Point> isDiagonal = (p1, p2) -> Math.abs(p1.x - p2.x) >= 1 && Math.abs(p1.y - p2.y) >= 1;

        while(!nextToEnd)
        {
            Node finalCurrentNode = currentNode;
            toAdd.clear();
            toAddDiagonal = potentialNeighbors.apply(currentNode.getPoint()).filter(point -> !closedMap.containsKey(point) &&
                    canPassThrough.test(point) &&
                    withinReach.test(finalCurrentNode.getPoint(), point)
                    && isDiagonal.test(point, finalCurrentNode.point)
            ).map(point -> new Node(point, manhattanDistance(point, end),
                    finalCurrentNode.getG() + 14,
                    finalCurrentNode.getG() + 14 + manhattanDistance(point, end),
                    finalCurrentNode))
                    .collect(Collectors.toList());

            toAddCardinal = potentialNeighbors.apply(currentNode.getPoint()).filter(point -> !closedMap.containsKey(point) &&
                    canPassThrough.test(point) &&
                    withinReach.test(finalCurrentNode.getPoint(), point)
                    && !isDiagonal.test(point, finalCurrentNode.point)
            ).map(point -> new Node(point, manhattanDistance(point, end),
                    finalCurrentNode.getG() + 10,
                    finalCurrentNode.getG() + 10 + manhattanDistance(point, end),
                    finalCurrentNode))
                    .collect(Collectors.toList());
            toAdd.addAll(toAddCardinal);
            toAdd.addAll(toAddDiagonal);

            for (Node temp : toAdd)
            {
                if (!openList.contains(temp)){
                    openList.add(temp);
                    openMap.put(temp.getPoint(), temp);
                }
                // this will replace a node already explored with an updated version if better path determined through it
                else {
                    if (temp.getG() < openMap.get(temp.getPoint()).getG()){
                        openList.remove(temp);
                        openMap.replace(temp.getPoint(), temp);
                        // nodes are equal via point--not g value
                        openList.add(temp);
                    }
                }
            }
            closedMap.put(currentNode.getPoint(), currentNode);
            currentNode = openList.poll();
            // currentNode only null if no path possible
            if (currentNode == null)
                return path;
            if (currentNode.getPoint().equals(end) || potentialNeighbors.apply(currentNode.getPoint()).anyMatch(p1 -> p1 == end))
                nextToEnd = true;
        }
        // the only prevNode == null is the start node
        while (currentNode.getPrevNode().getPrevNode() != null)
        {
            path.add(0, currentNode.getPrevNode().getPoint());
            currentNode = currentNode.getPrevNode();
        }
        return path;
    }


    private class Node {

        private Point point;
        private int h;
        private int g;
        private int f;
        private Node prevNode;

        public Node(Point point, int h, int g, int f, Node prevNode)
        {
            this.point = point;
            this.h = h;
            this.g = g;
            this.f = f;
            this.prevNode = prevNode;
        }

        public Point getPoint() { return point; }
        public Node getPrevNode() { return prevNode; }
        public int getH() { return h; }
        public int getG() { return g; }
        public int getF() { return f; }

        public boolean equals(Node o)
        {
            return point.equals(o.point);
        }

    }

}

