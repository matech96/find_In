package hu.matech.FindInEngine;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by akos on 2016.09.09..
 */
public class NodeTest {
    Node a = new Node(0, 0);
    Node b = new Node(1, 4);
    Node c = new Node(1, 2);
    Edge e = new Edge(a, b);
    Edge f = new Edge(c, b);

    @Test
    public void Connections() throws Exception {
        assertEquals(b, a.getNeighbors().get(0));
        a.addConnection(e);
        a.addConnection(e);
        a.addConnection(e);
        assertEquals(1, a.getConnections().size());

        a.clearConnections();
        assertTrue(a.getConnections().isEmpty());
        assertEquals(b.getConnections().get(0), f);
        e = new Edge(a, b); //Restore order
    }

    @Test
    public void distance() throws Exception {
        assertEquals(4.12, a.distance(b), 1e-2);
    }

    @Test
    public void getNeighbors() throws Exception {
        ArrayList<Node> neighbors = b.getNeighbors();
        assertTrue(neighbors.contains(a));
        assertTrue(neighbors.contains(c));
        assertEquals(2, neighbors.size());
    }

    @Test
    public void getEdgeToNeighbor() {
        assertEquals(e, b.getEdgeToNeighbor(a));
        assertEquals(f, b.getEdgeToNeighbor(c));
    }

}