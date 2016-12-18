package hu.matech.FindInEngine;

import java.util.ArrayList;

/**
 * This contains a series of {@link Edge}. It guaranties that the {@link Edge}s are in a valid rout,
 * if the structure of the graph hasn't been changed since the adding.
 * @version 1.0
 */
public class Route {
    protected ArrayList<Edge> steps = new ArrayList<>();
    protected Node last = null;
    protected Node start = null;

    /**
     * Create a new {@link Route} starting from startingPoint.
     * @param startingPoint The {@link Route} starts here.
     */
    public Route(Node startingPoint) {
        start = startingPoint;
        last = startingPoint;
    }

    /**
     * Copies an other {@link Route} instance.
     * @param other The instance to copy.
     */
    public Route(Route other) {
        steps = new ArrayList<>(other.steps);
        last = other.last;
    }

    /**
     * Checks is the {@link Edge} in the {@link Route}.
     * @param e The {@link Edge} to search.
     * @return True if it is in the Route false if it isn't.
     */
    public boolean contains(Edge e) {
        return steps.contains(e);
    }

    /**
     * Checks is the {@link Node} in the {@link Route}.
     * @param n The {@link Node} to search.
     * @return True if it is in the Route false if it isn't.
     */
    public boolean contains(Node n) {
        boolean res = false;
        for (Edge e : steps) {
            if (e.isConnectionOf(n)) {
                res = true;
                break;
            }
        }
        return res;
    }

    /**
     * Returns the neighbors of the given {@link Node} in this route.
     * @param n
     * @return
     */
    public Node[] getNeighborsInRoute(Node n) {
        ArrayList<Node> res = new ArrayList<>();
        for (Edge e : steps) {
            Node neighbor = e.getNeighborOf(n);
            if (neighbor != null) {
                res.add(neighbor);
                if (res.size() > 1) {
                    break;
                }
            }
        }
        return res.toArray(new Node[res.size()]);
    }

    /**
     * Simulate you are going on the route. You are in a and go to b. You go to the same direction,
     * its c. Returns c. If a or b is not part of the route or a nd b are not neighbors or b is the
     * last {@link Node}, returns null.
     * @param a
     * @param b
     * @return
     */
    public Node getNextNodeInRoute(Node a, Node b) {
        Node[] neighborsOfB = getNeighborsInRoute(b);
        if (neighborsOfB.length != 2) {
            return null;
        } else {
            if (neighborsOfB[0] == a) {
                return neighborsOfB[1];
            } else if (neighborsOfB[1] == a) {
                return neighborsOfB[0];
            } else {
                return null;
            }
        }
    }

    /**
     * Returns an ArrayList of {@link Edge} included by the route
     * @return ArrayList of {@link Edge} included by the route.
     */
    public ArrayList<Edge> getSteps(){
        return steps;
    }

    /**
     * Add an {@link Edge} to the end.
     * @param edge {@link Edge} to add.
     * @throws RuntimeException If the {@link Edge} cannot be connected to the last point of the
     * {@link Route}.
     */
    public void add(Edge edge) throws RuntimeException{
        if (!last.getConnections().contains(edge)){
            throw new RuntimeException("The edge cannot be connected to the last point of the rout!");
        }
        last = edge.getNeighborOf(last);
        steps.add(edge);
    }

    /**
     * Add a {@link Route} to the end. The route's {@link #getStartingPoint() starting point} and this
     * {@link #getEndingPoint() ending point} muss be equal.
     * @param route {@link Route} to add.
     * @throws RuntimeException If the {@link Route} cannot be connected to the last point of the {@link Route}.
     */
    public void add(Route route) throws RuntimeException{
        if (route.numEdges() == 0){
            if (route.start != last){
                this.steps.addAll(route.steps);
                last = route.last;
            } else {
                throw new RuntimeException("The given rout cannot be connected to the last" +
                        "point of the existing rout!");
            }
        }
    }

    /**
     * Returns with the number of {@link Edge} in the rout.
     * @return The number of edges in the rout.
     */
    public int numEdges(){
        return steps.size();
    }

    /**
     * Summarizes the lengths of the {@link Edge}s in the rout.
     * @return Sum of lengths of the {@link Edge}s in the rout.
     */
    public  double length(){
        double res = 0;
        for (Edge e : steps){
            res += e.getWeight();
        }
        return res;
    }

    /**
     * Returns a String which each rows has an {@link Edge} of the rout.
     * @return String which each rows has an {@link Edge} of the rout.
     */
    public String toString(){
        String res = "";
        for (Edge e : steps){
            res += e + "\n";
        }
        return res;
    }

    /**
     * Returns {@link Node} you have given in the {@link #Route(Node) constructor}.
     * @return {@link Node} you have given in the {@link #Route(Node) constructor}.
     */
    public Node getStartingPoint(){
        return start;
    }

    /**
     * Returns the {@link Node} to which you can connect.
     * @return The {@link Node} to which you can connect.
     */
    public Node getEndingPoint(){
        return last;
    }
}
