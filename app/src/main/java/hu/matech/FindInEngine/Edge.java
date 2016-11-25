package hu.matech.FindInEngine;

/**
 * Created by akos on 2016.09.09..
 */
public class Edge {
    protected Node a;
    protected Node b;
    protected double weight = 0;

    public Edge(Node a, Node b) {
        this.a = a;
        this.b = b;

        this.weight = a.distance(b);
        a.addConnection(this);
        b.addConnection(this);
    }

    public void delete(){
        a.removeConnection(this);
        b.removeConnection(this);
        a = null;
        b = null;
    }

    public Node getNeighbor(Node n){
        if (n == a){
            return  b;
        } else if (n == b) {
            return a;
        } else {
            return null;
        }
    }

    public Node[] getNeighbors(){
        return new Node[]{a, b};
    }

    public boolean isConnectionOf(Node n) {
        return (a == n || b == n);
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String toString(){
        return a + " - " + b;
    }
}
