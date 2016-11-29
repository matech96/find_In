/**
 * Created by akos on 2016.09.06..
 */
package hu.matech.FindInEngine;

import org.json.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * This class is designed to process a graph to make it capable of rout planning.
 * @version 1.0
 */
public class RouteFinder {
    protected HashMap<String, RNode> places = new HashMap<>();

    protected class RNode extends Node {
        protected int level;
        protected Route route = new Route(this);
        protected double distance = -1;

        public RNode(Coordinates cords, NodeType type, int level) {
            super(cords, type);
            this.level = level;
        }

        public RNode(Coordinates cords, int level) {
            super(cords);
            this.level = level;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public boolean isDistanceSet(){
            return distance + 1 > 1e-2;
        }

        public void addStep(Edge step){
            route.add(step);
        }

        public Route getRoute() {
            return route;
        }

        public void setRoute(Route route) {
            this.route = route;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public void addRout(Route route) {
            this.route.add(route);
        }
    }

    /**
     * Returns on which level is the {@link Node}.
     * @param node To examine.
     * @return Tho level of the {@link Node}.
     */
    public int getLevelOfNode(Node node) {
        return ((RNode) node).getLevel();
    }

    /**
     * Returns a list of {@link Edge}s which both end are on the given level.
     * @param level
     * @return
     */
    public ArrayList<Edge> getEdgesInLevel(int level) {
        ArrayList<Edge> res = new ArrayList<>();
        for (RNode n : ((HashMap<String, RNode>)getNodesInLevel(level)).values()) {
            for (Edge e : n.getConnections()) {
                if (((RNode)e.getNeighbor(n)).getLevel() == level) {
                    res.add(e);
                }
            }
        }
        return res;
    }

    /**
     * Returns a list of {@link Node}s which are on the given level.
     * @param level
     * @return
     */
    public HashMap<String, ? extends Node> getNodesInLevel(int level) {
        HashMap<String, RNode> res = new HashMap<>();
        for (Map.Entry<String, RNode> e : places.entrySet()) {
            if (e.getValue().getLevel() == level) {
                res.put(e.getKey(), e.getValue());
            }
        }
        return res;
    }

    /**
     * Return every place (for example rooms) in a HashMap(key - String name of the place, value
     * RNode(type of {@link Node}))
     * @return
     */
    public HashMap<String, RNode> getPlaces() {
        return places;
    }

    /**
     * Reads the data from a JSON file.
     * @param fileName Name of the file.
     */
    public void addLevel(String fileName, int level){
        FileInputStream is = null;
        try{
            is = new FileInputStream(fileName);
            addLevel(is, level);
        }catch (Exception e){
            System.out.println(e);
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Reads the data from a JSON file and connects it to the upper and bellowed levels.
     * @param is InputStream for the source file which contains the graph.
     * @param level The level of the graph.
     */
    public void addLevel(InputStream is, int level){
        HashMap<String,  Node> nodesInLevelUp = (HashMap<String, Node>) getNodesInLevel(level + 1);
        HashMap<String, Node> nodesInLevelDown = (HashMap<String, Node>) getNodesInLevel(level - 1);
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String s = br.readLine();
            JSONObject json = new JSONObject(s);

            //Read the Node
            JSONArray nodes = json.getJSONArray("nodes");

            HashMap<Integer, RNode> id_node = new HashMap<>();
            for (int i = 0; i < nodes.length(); i++) {
                //Read out the data
                JSONObject node = (JSONObject) nodes.get(i);
                int id = (Integer) node.get("id");
                double x = node.getDouble("x");
                double y = node.getDouble("y");
                Coordinates cords = new Coordinates(x, y);
                String name = (String) node.get("title");

                //Create Node
                RNode newNode = new RNode(cords, level);

                //No name is an alias for "-f"
                if (name.equals("")) {
                    name = "-f";
                }

                if (name.startsWith("-")) {
                    //Set type of the Node
                    NodeType type = NodeType.ROOM;
                    char typeChar = name.charAt(1);
                    switch (typeChar) {
                        case 'f':
                            type = NodeType.FLOOR;
                            break;
                        case 'e':
                            type = NodeType.ELEVATOR;
                            break;
                        case 's':
                            type = NodeType.STAIRHOUSE;
                            break;
                        default:
                            break;
                    }
                    newNode.setType(type);

                    // Set the name if it is give, if it isn't generate one.
                    if (name.length() > 3) {
                        name = name.substring(3);
                    } else {
                        name = name + String.valueOf(places.size());
                    }

                    // Connect it by the rule to other levels.
                    if (type == NodeType.ELEVATOR || type == NodeType.STAIRHOUSE) {
                        String toSearch = (name + '_');
                        for (String listed : places.keySet()) {
                            if (listed.startsWith(toSearch)) {
                                Node a = nodesInLevelUp.get(listed);
                                Node b = nodesInLevelDown.get(listed);
                                if (a != null) {
                                    new Edge(newNode, a);
                                }
                                if (b != null) {
                                    new Edge(newNode, b);
                                }
                            }
                        }
                    }
                    name += '_' + String.valueOf(places.size()); //Make sure every node is saved
                }

                //Add the new node to list
                if (places.keySet().contains(name)) {
                    throw new RuntimeException("2 nodes has the same name. Only special nodes" +
                            " f.e. elevators can have the same name! The problematic name: " + name);
                }
                places.put(name, newNode);
                id_node.put(id, newNode);
            }

            //Read the Edges
            JSONArray edges = json.getJSONArray("edges");
            for (int i = 0; i < edges.length(); i++) {
                JSONObject edge = (JSONObject) edges.get(i);
                int a_id = (Integer) edge.get("source");
                int b_id = (Integer) edge.get("target");
                RNode a = id_node.get(a_id);
                RNode b = id_node.get(b_id);

                new Edge(a, b);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**zz
     * @param from
     * @param to
     * @return a Rout between from and to, if there is any. Returns null if there is none.
     */
    public Route findRout(String from, String to){
        //Ford-algorithm

        RNode f = places.get(from);
        RNode t = places.get(to);

        if (f == null || t == null) {
            return null;
        }

        f.setDistance(0);    //put starting point inside the circle
        while (!t.isDistanceSet()){
            RNode minNeighbor = null;       //this is going to bee in the circle when this turn is over
            double minDistance = -1;        //
            Route minRoute = null;            //

            for (RNode node : places.values()) {
                //Check every Node inside the circle.
                if (node.isDistanceSet()) {
                    ArrayList<Edge> neighbors = node.getConnections();
                    for (Edge edge : neighbors){
                        RNode neighbor = (RNode) edge.getNeighbor(node);

                        //If someone has a neighbor outside the circle
                        if (!neighbor.isDistanceSet()) {
                            //Calculate the distance between the neighbor and Node from
                            double neighborDistance = edge.getWeight() + node.getDistance();
                            //We search the smallest.
                            if ((minNeighbor == null) || (minDistance > neighborDistance)) {
                                minNeighbor = neighbor;
                                minDistance = neighborDistance;
                                minRoute = new Route(node.getRoute());
                                minRoute.add(edge);
                            }
                        }
                    }
                }
            }

            //If there is no neighbor left, no route exists.
            if (minNeighbor != null) {
                minNeighbor.setDistance(minDistance);
                minNeighbor.setRoute(minRoute);
            } else {
                return null;
            }
        }

        // Clean up
        Route res = new Route(t.getRoute());
        for (RNode node : places.values()){
            node.setDistance(-1);
            node.setRoute(new Route(node));
        }

        if (res.length() == 0) {
            return null;
        } else {
            return res;
        }
    }

    public Node get(String name){
        return places.get(name);
    }
}
