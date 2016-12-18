package hu.matech.FindInEngine;

/**
 * Represents an undirected connection between 2 {@link Node}. It can also have a weight.
 */
public class Edge {
    protected Node a;
    protected Node b;
    protected double weight = 0;

    /**
     * Create an edge between the given {@link Node}s. The weight is set to the distance of the 2
     * {@link Node}s. The {@link Node}s will be also notified.
     * @param a The {@link Node} connecting to b.
     * @param b The {@link Node} connecting to a.
     */
    public Edge(Node a, Node b) {
        this.a = a;
        this.b = b;

        this.weight = a.distance(b);
        a.addConnection(this);
        b.addConnection(this);
    }

    /**
     * Removes this edge. The {@link Node}s which are connected will be notified.
     */
    public void delete(){
        a.removeConnection(this);
        b.removeConnection(this);
        a = null;
        b = null;
    }

    /**
     * Returns the neighbor of n connected by this edge.
     * @param n The {@link Node} which neighbor you want.
     * @return The neighbor of n connected by this edge.
     */
    public Node getNeighborOf(Node n){
        if (n == a){
            return  b;
        } else if (n == b) {
            return a;
        } else {
            return null;
        }
    }

    /**
     * Returns both {@link Node} connected by this edge.
     * @return Both {@link Node} connected by this edge.
     */
    public Node[] getNeighbors(){
        return new Node[]{a, b};
    }

    /**
     * True if one of the ends is n.
     * @param n The {@link Node} you want to test.
     * @return True if one of the ends is n.
     */
    public boolean isConnectionOf(Node n) {
        return (a == n || b == n);
    }

    /**
     * Returns the distance between the 2 ends.
     * @return The distance between the 2 ends.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Overrides the weight set by the constructor.
     * @param weight The value to set.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Uses the format: a + " - " + b
     */
    public String toString(){
        return a + " - " + b;
    }
}
