package hu.matech.FindInEngine;

import org.junit.Before;
import org.junit.Test;
import org.testng.annotations.BeforeMethod;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by akos on 2016.09.09..
 */
public class NodeTest {
    Node a;
    Node b;
    Node c;
    Edge e;
    Edge f;

    @Before
    public void setUp() throws Exception {
        a = new Node(new Coordinates(0, 0), NodeType.ELEVATOR);
        b = new Node(new Coordinates(1, 4));
        c = new Node(1, 2);
        e = new Edge(a, b);
        f = new Edge(c, b);
    }

    @Test
    public void type() {
        assertEquals(NodeType.ELEVATOR, a.getType());
        assertEquals(NodeType.ROOM, b.getType());
        c.setType(NodeType.STAIRHOUSE);
        assertEquals(NodeType.STAIRHOUSE, c.getType());
    }

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