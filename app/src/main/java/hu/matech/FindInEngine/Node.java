package hu.matech.FindInEngine;

import java.util.ArrayList;

/**
 * It is a point in the graph, that can be connected to other points(Node).
 */
public class Node {
    protected Coordinates cords;
    protected NodeType type;
    protected ArrayList<Edge> connections = new ArrayList<>();

    /**
     * Create a Node at the given Coordinates and type.
     * @param cords Here will be the node.
     * @param type  Type of the Node. Check {@link NodeType}.
     */
    public Node(Coordinates cords, NodeType type) {
        this.cords = cords;
        this.type = type;
    }

    /**
     * Create a Node at the given Coordinates. Default {@link NodeType} is ROOM.
     * @param cords Here will be the node.
     */
    public Node(Coordinates cords) {
        this(cords, NodeType.ROOM);
    }

    /**
     * Create a Node at the given x and y location and type.
     * @param x
     * @param y
     * @param type Type of the Node.
     */
    public Node(double x, double y, NodeType type) {
        this(new Coordinates(x, y), type);
    }

    /**
     * Create a Node at the given x and y location. Default {@link NodeType} in ROOM.
     * @param x
     * @param y
     */
    public Node(double x, double y) {
        this(x, y, NodeType.ROOM);
    }

    /**
     * Connect the Node to the Edge. (It is called by {@link Edge#Edge(Node, Node) }). Don't use it manually.
     * @param edge Edge to add.
     */
    void addConnection(Edge edge) {
        if (!getNeighbors().contains(edge.getNeighborOf(this))) {
            connections.add(edge);
        }
    }

    /**
     * Return an ArrayList of {@link Edge} to witch the Node is connected.
     *
     * @return ArrayList of {@link Edge}s to witch the Node is connected.
     */
    public ArrayList<Edge> getConnections() {
        return connections;
    }

    /**
     * Removes the given {@link Edge} e. (It is called by {@link Edge#Edge(Node, Node) }). Don't use
     * it manually. If you want to remove a connection call {@link Edge#delete()}.
     * @param e This Edge will be removed.
     */
    void removeConnection(Edge e) {
        connections.remove(e);
    }

    /**
     * Delete all {@link Edge}. It will take care of the neighbors.
     */
    public void clearConnections() {
        ArrayList<Edge> copy = new ArrayList<>(connections);
        for (Edge e : copy) {
            e.delete();
        }
        connections.clear();
    }

    /**
     * Computes the distance between this and the given Node.
     * @param b To witch the distance will be computed.
     * @return The distance between this and b.
     */
    public double distance(Node b) {
        return distance(b.getCords());
    }

    /**
     * Computes the distance between this and the given node, defined by x and y.
     * @param x The nodes x coordinate to witch the distance will be computed.
     * @param y The nodes y coordinate to witch the distance will be computed.
     * @return The distance between this and the given node, defined by x and y.
     */
    public double distance(double x, double y) {
        return distance(new Coordinates(x, y));
    }

    /**
     * Computes the distance between this and the given Node, defined by the {@link Coordinates} c.
     * @param c To witch the distance will be computed.
     * @return The distance between this and c.
     */
    public double distance(Coordinates c) {
        Coordinates distVec = this.cords.returnSub(c);
        double x = distVec.getX();
        double y = distVec.getY();
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Returns the type of the node.
     * @return The type of the node.
     */
    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    /**
     * Return an ArrayList of {@link Edge} to witch the Node is connected.
     * @return ArrayList of {@link Edge} to witch the Node is connected.
     */
    public ArrayList<Node> getNeighbors() {
        ArrayList<Node> res = new ArrayList<>();
        for (Edge c : connections) {
            Node neighbor = c.getNeighborOf(this);
            res.add(neighbor);
        }
        return res;
    }

    /**
     * Returns the {@link Coordinates} of this node.
     * @return
     */
    public Coordinates getCords() {
        return cords;
    }

    /**
     * Change the {@link Coordinates} to cords.
     * @param cords This will be the new {@link Coordinates} of the Node.
     */
    public void setCords(Coordinates cords) {
        this.cords = cords;
    }

    /**
     * Change the {@link Coordinates}, defined by x and y.
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public void setCords(double x, double y) {
        this.cords = new Coordinates(x, y);
    }

    /**
     * @return The x coordinate of the Node.
     */
    public double getX() {
        return cords.getX();
    }

    /**
     * @return The y coordinate of the Node.
     */
    public double getY() {
        return cords.getY();
    }

    /**
     * Uses the format: cords + type
     */
    public String toString() {
        return cords.toString() + type;
    }

    /**
     * Returns the {@link Edge} which two ends are this and Node n. If there is none returns null.
     * @param n Should bee neighbor of this.
     * @return {@link Edge} which two ends are this and Node n. If there is none return null.
     */
    public Edge getEdgeToNeighbor(Node n) {
        Edge res = null;
        for (Edge e : connections) {
            if (e.getNeighborOf(this) == n) {
                res = e;
                break;
            }
        }
        return res;
    }

    /**
     * Returns true if the nodes type is {@link NodeType#ELEVATOR} or {@link NodeType#STAIRHOUSE}.
     * @return True if the nodes type is {@link NodeType#ELEVATOR} or {@link NodeType#STAIRHOUSE}.
     */
    public boolean isElavatorOrStairhouse() {
        return (getType() == NodeType.ELEVATOR || getType() == NodeType.STAIRHOUSE);
    }
}
