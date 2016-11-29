package hu.matech.FindInEngine;

import org.junit.Before;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Created by akos on 2016.09.09..
 */
public class EdgeTest {
    Edge e;
    Node a;
    Node b;
    Node c;

    @BeforeMethod
    public void setUp() {
        a = new Node(0, 0);
        b = new Node(0, 4);
        e = new Edge(a, b);
        c = new Node(3,3);
    }

    @Test
    public void getNeighbor() throws Exception {
        assertEquals(b, e.getNeighbor(a));
        assertEquals(a, e.getNeighbor(b));
        assertEquals(null, e.getNeighbor(c));
    }

    @Test
    public void delete() {
        e.delete();
        assertEquals(0, a.getNeighbors().size());
        assertEquals(0, b.getNeighbors().size());
    }

    @Test
    public void isConnectionOf() {
        assertEquals(true, e.isConnectionOf(a));
        assertEquals(true, e.isConnectionOf(b));
        assertEquals(false, e.isConnectionOf(c));
    }

    @Test
    public void getNeighbors() throws Exception {
        Node[] arr = {a, b};
        assertArrayEquals(e.getNeighbors(), arr);
    }

    @Test
    public void weights() {
        assertEquals(4., e.getWeight(), 1e-5);
        e.setWeight(5.);
        assertEquals(5., e.getWeight());
//        assertEquals(a.toString()+" - "+b.toString(), e.toString());
    }

}