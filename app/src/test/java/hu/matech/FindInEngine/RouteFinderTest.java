package hu.matech.FindInEngine;

import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by akos on 2016.09.07..
 */
public class RouteFinderTest {
    double delta = 1e-5;
    RouteFinder rf;

    @Before
    public void setUp() throws Exception {
        rf = new RouteFinder();
        rf.loadAllObjectOnLevel(getClass().getClassLoader().getResourceAsStream("toTest.json"), 1);
        rf.loadAllObjectOnLevel(getClass().getClassLoader().getResourceAsStream("toTest2.json"), 2);
    }

    @Test(expected = FileNotFoundException.class)
    public void invalidInput() throws Exception {
        rf.loadAllObjectOnLevel("hi.json", 2);
    }

    @org.junit.Test
    public void addLevel() throws Exception {
        //Is all node stored?
        assertEquals(18, rf.getPlaces().size());

        //On levels too?
        assertEquals(14, rf.getNodesInLevel(1).size());
        assertEquals(4, rf.getNodesInLevel(2).size());

        //On good level?
        assertEquals(rf.getLevelOfNode(rf.get("13")), 1);
        assertEquals(rf.getLevelOfNode(rf.get("22")), 2);

        ArrayList<Edge> edgesInLevel = rf.getEdgesInLevel(1);
        assertEquals(28, edgesInLevel.size());
        edgesInLevel = rf.getEdgesInLevel(2);
        assertEquals(2, edgesInLevel.size());


        //Check coordinates
        Coordinates coor = rf.places.get("10").getCords();
        assertEquals(1386, coor.getX(),delta);
        assertEquals(627, coor.getY(), delta);

        Node n = rf.places.get("13");
        coor = n.getCords();
        assertEquals(1359, coor.getX(), delta);
        assertEquals(102, coor.getY(), delta);

        //Check types
        assertEquals(NodeType.ROOM, n.getType());
    }

    @org.junit.Test
    public void findRout() throws Exception {
        Route route = rf.findRout("11", "9");
        assertEquals(1, route.numEdges());
        assertEquals(rf.get("11").distance(rf.get("9")), route.length(), delta);

        route = rf.findRout("6", "13");
        double godLength = 0;
        godLength += rf.get("6").distance(rf.get("4"));
        godLength += rf.get("4").distance(rf.get("3"));
        godLength += rf.get("3").distance(rf.get("13"));
        assertEquals(godLength, route.length(), 1e-5);

        route = rf.findRout("6", "22");
        assertEquals(6, route.numEdges());
    }

}