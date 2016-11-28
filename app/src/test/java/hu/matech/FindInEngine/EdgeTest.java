package hu.matech.FindInEngine;

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

    public EdgeTest(){
        a = new Node(0, 0);
        b = new Node(1, 4);
        e = new Edge(a, b);
    }

    @Test
    public void getNeighbor() throws Exception {
        assertEquals(e.getNeighbor(a), b);
        assertEquals(e.getNeighbor(b), a);
    }

    @Test
    public void getNeighbors() throws Exception {
        Node[] arr = {a, b};
        assertArrayEquals(e.getNeighbors(), arr);
    }

}