package hu.matech.FindInEngine;

import static org.junit.Assert.*;

/**
 * Created by akos on 2016.09.07..
 */
public class RouteFinderTest {
    double delta = 1e-5;

    @org.junit.Test
    public void addLevel() throws Exception {
        //Run first
        RouteFinder test = new RouteFinder();
        test.addLevel(getClass().getClassLoader().getResourceAsStream("toTest.json"), 1);

        test.addLevel(getClass().getClassLoader().getResourceAsStream("toTest2.json"), 2);

        //Is all node stored?
        assertEquals(10, test.getPlaces().size());

        //On levels too?
        assertEquals(7, test.getNodesInLevel(1).size());
        assertEquals(3, test.getNodesInLevel(2).size());

        //Check coordinates
        Coordinates coor = test.places.get("3").getCords();
        assertEquals(-103, coor.getX(),delta);
        assertEquals(443, coor.getY(), delta);

        Node n = test.places.get("4");
        coor = n.getCords();
        assertEquals(291, coor.getX(), delta);
        assertEquals(789, coor.getY(), delta);

        //Check types
        assertEquals(NodeType.ROOM, n.getType());

         n = test.places.get("to_4");
        assertEquals(NodeType.ELEVATOR, n.getType());

    }

    @org.junit.Test
    public void findRout() throws Exception {
        RouteFinder test = new RouteFinder();
        test.addLevel(getClass().getClassLoader().getResourceAsStream("toTest.json"),1);

        Route route = test.findRout("3", "4");
        assertEquals(2, route.numEdges());
        double godLength = 0;
        godLength += test.get("3").distance(test.get("to_4"));
        godLength += test.get("to_4").distance(test.get("4"));
        assertEquals(godLength, route.length(), 1e-5);

        route = test.findRout("2_1", "3");
        //System.out.println(rout);
        assertEquals(1, route.numEdges());
        godLength = 0;
        godLength += test.get("2_1").distance(test.get("3"));
        assertEquals(godLength, route.length(), 1e-5);
    }

}