/**
 * Created by akos on 2016.09.06..
 */
package hu.matech.FindInEngine;

import org.json.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import hu.matech.FindInEngine.Coordinates;


/**
 * This class is designed to process a graph to make it capable of rout planning.
 * @version 1.0
 */
public class RoutFinder {
    protected HashMap<String, RNode> places = new HashMap<>();

    protected class RNode extends Node {
        protected int level;
        protected Rout rout = new Rout(this);
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
            rout.add(step);
        }

        public Rout getRout() {
            return rout;
        }

        public void setRout(Rout rout) {
            this.rout = rout;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public void addRout(Rout rout) {
            this.rout.add(rout);
        }

//        public ArrayList<Edge> getEdgesInLevel(int level) {
//            ArrayList<Edge> res = new ArrayList<>();
//            for (Edge e : connections)
//        }
    }

    public int getLevelOfNode(Node node) {
        return ((RNode) node).getLevel();
    }

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
     * Return every place (for example rooms) in a HashMap(key - String name of the place, value RNode(type of {@link Node}))
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
     * Reads the data from a JSON file.
     * @param is InputStream for the source file which contains the graph.
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

                if (name.equals("")) {  //No name is an alias for "-f"
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
                            type = NodeType.ELAVATOR;
                            break;
                        case 's':
                            type = NodeType.STAIRHOUSE;
                            break;
                        default:
                            break;
                    }
                    newNode.setType(type);
                    if (name.length() > 3) {
                        name = name.substring(3);
                    } else {
                        name = name + String.valueOf(places.size());    //If name is not given generate one.
                    }
                    // Connect it by the rule to other levels.
                    if (type == NodeType.ELAVATOR || type == NodeType.STAIRHOUSE) {
//                        ArrayList<String> keys = new ArrayList<>(places.keySet());
//                        Collections.sort(keys);


                        String toSearch = (name + '_');
                        for (String listed : places.keySet()) {
//                            Set<String> set = places.keySet().stream().filter(s -> s.startsWith("address")).collect(Collectors.toSet());
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
                    throw new RuntimeException("2 nodes has the same name. Only special nodes f.e. elevators can have the same name!" + name);
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


    /**
     * Find the fastest Rout between String from and String to.
     * @param from
     * @param to
     * @return a Rout between from and to, if there is any. Returns null if there is none.
     */
    public Rout findRout(String from, String to){
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
            Rout minRout = null;            //
            for (RNode node : places.values()) {
                if (node.isDistanceSet()){  //Check every Node inside the circle.
                    ArrayList<Edge> neighbors = node.getConnections();
                    for (Edge edge : neighbors){
                        RNode neighbor = (RNode) edge.getNeighbor(node);
                        if (!neighbor.isDistanceSet()){ //If someone has a neighbor outside the circle
                            double neighborDistance = edge.getWeight() + node.getDistance();    //Calculate the distance between the neighbor and from Nodes
                            if ((minNeighbor == null) || (minDistance > neighborDistance)) {    //We search the smallest.
                                minNeighbor = neighbor;
                                minDistance = neighborDistance;
                                minRout = new Rout(node.getRout());
                                minRout.add(edge);
                            }
                        }
                    }
                }
            }
            //If there is no neighbor left, no route exists.
            if (minNeighbor != null) {
                minNeighbor.setDistance(minDistance);
                minNeighbor.setRout(minRout);
            } else {
                return null;
            }
        }


        Rout res = new Rout(t.getRout());      //make a copy
        for (RNode node : places.values()){                 //clean up
            node.setDistance(-1);
            node.setRout(new Rout(node));
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
