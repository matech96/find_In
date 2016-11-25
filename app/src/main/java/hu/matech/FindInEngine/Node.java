package hu.matech.FindInEngine;

import java.util.ArrayList;

import hu.matech.FindInEngine.Coordinates;

/**
 * It is a point in the graph, that can be connected to other points(Node).
 */
public class Node {
    protected Coordinates cords;
    protected NodeType type;
    protected ArrayList<Edge> connections = new ArrayList<>();

    /**
     * Create a Node at the given Coordinates.
     *
     * @param cords Here will be the node.
     * @param type  Type of the Node. Check {@link NodeType}.
     */
    public Node(Coordinates cords, NodeType type) {
        this.cords = cords;
        this.type = type;
    }

    /**
     * Create a Node at the given Coordinates. Default {@link NodeType} is ROOM.
     *
     * @param cords Here will be the node.
     */
    public Node(Coordinates cords) {
        this(cords, NodeType.ROOM);
    }

    /**
     * Create a Node at the given x and y location.
     *
     * @param x
     * @param y
     * @param type Type of the Node.
     */
    public Node(double x, double y, NodeType type) {
        this(new Coordinates(x, y), type);
    }

    /**
     * Create a Node at the given x and y location. Default {@link NodeType} in FLOOR.
     *
     * @param x
     * @param y
     */
    public Node(double x, double y) {
        this(x, y, NodeType.FLOOR);
    }

    /**
     * Connect the Node to the Edge. (It is called by {@link Edge#Edge(Node, Node) }). Don't use it manually.
     *
     * @param edge Edge to add.
     */
    void addConnection(Edge edge) {
        if (!getNeighbors().contains(edge.getNeighbor(this))) {
            connections.add(edge);
        }
    }

    /**
     * Return an ArrayList of Edge to witch the Node is connected.
     *
     * @return ArrayList of Edge to witch the Node is connected.
     */
    public ArrayList<Edge> getConnections() {
        return connections;
    }

    /**
     * Removes the given Edge e. (It is called by {@link Edge#Edge(Node, Node) }). Don't use it manually.
     *
     * @param e This Edge will be removed.
     */
    public void removeConnection(Edge e) {
        connections.remove(e);
    }

    /**
     * Delete all Edge .It will take care of the neighbors.
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
     *
     * @param b To witch the distance will be computed.
     * @return The distance between this and b.
     */
    public double distance(Node b) {
        return distance(b.getCords());
    }

    public double distance(double x, double y) {
        return distance(new Coordinates(x, y));
    }

    public double distance(Coordinates c) {
        Coordinates distVec = this.cords.sub(c);
        double x = distVec.getX();
        double y = distVec.getY();
        return Math.sqrt(x * x + y * y);
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    /**
     * Return an ArrayList of Edge to witch the Node is connected.
     *
     * @return ArrayList of Edge to witch the Node is connected.
     */
    public ArrayList<Node> getNeighbors() {
        ArrayList<Node> res = new ArrayList<>();
        for (Edge c : connections) {
            Node neighbor = c.getNeighbor(this);
            res.add(neighbor);
        }
        return res;
    }

    public Coordinates getCords() {
        return cords;
    }

    /**
     * Change the Coordinates to cords.
     *
     * @param cords This will be the new Coordinates of the Node.
     */
    public void setCords(Coordinates cords) {
        this.cords = cords;
    }

    /**
     * Change the Coordinates to the give values.
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    public void setCords(double x, double y) {
        this.cords = new Coordinates(x, y);
    }

    /**
     * @return double The x coordinate of the Node.
     */
    public double getX() {
        return cords.getX();
    }

    /**
     * @return double The y coordinate of the Node.
     */
    public double getY() {
        return cords.getY();
    }

    /**
     * Same as {@link Coordinates#toString()}
     */
    public String toString() {
        return cords.toString() + type;
    }

    /**
     * Return the Edge which two ends are this and Node n. If there is none return null;
     *
     * @param n Should bee neighbor of this.
     * @return Edge which two ends are this and Node n.
     */
    public Edge getEdgeToNeighbor(Node n) {
        Edge res = null;
        for (Edge e : connections) {
            if (e.getNeighbor(this) == n) {
                res = e;
                break;
            }
        }
        return res;
    }
}
