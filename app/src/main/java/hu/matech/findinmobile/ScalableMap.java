package hu.matech.findinmobile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import hu.matech.FindInEngine.Coordinates;
import hu.matech.FindInEngine.Edge;
import hu.matech.FindInEngine.Node;
import hu.matech.FindInEngine.NodeType;
import hu.matech.FindInEngine.Route;
import hu.matech.FindInEngine.RouteFinder;

/**
 * Controls a ScalableMap
 */

public class ScalableMap extends SubsamplingScaleImageView {
    static RouteFinder rf;

    static Node clicked;

    public static Route getToShow() {
        return toShow;
    }

    public static Route toShow;

    int level = 1;
    int levelMax;
    int levelMin;

    public ScalableMap(Context context, AttributeSet attr) {
        super(context, attr);
        setUpMap(context);
    }

    public ScalableMap(Context context) {
        super(context);
        setUpMap(context);
    }

    private void setUpMap(Context context) {
        redrawPicture();

        rf = new RouteFinder();
        float offsetX = 0;
        float offsetY = 0;
        float numLevels =2;
        for (int i = 1; i <= numLevels; i++) {
            try {
                InputStream is = context.getAssets().open("map_level_" + i + ".json");
                rf.loadAllObjectOnLevel(is, i);
                for (Node n : rf.getPlaces().values()) {
                    Coordinates c = n.getCords();
                    float x = (float) c.getX() - offsetX;
                    float y = (float) c.getY() - offsetY;
                    n.setCords(x, y);
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Changes the picture what is shown. Should call if level was changed.
     */
    protected void redrawPicture() {
        ScalableMap map = (ScalableMap) findViewById(R.id.map);
        levelMax = 2;
        levelMin = 1;
        switch (level) {
            case 1:
                map.setImage(ImageSource.resource(R.drawable.map_level_1));
                break;
            case 2:
                map.setImage(ImageSource.resource(R.drawable.map_level_2));
                break;
        }
    }

    /**
     * Returns the name of the {@link Node}s on all levels.
     * @return The name of the {@link Node}s on all levels.
     */
    public String [] getPlaces() {
        return rf.getPlaces().keySet().toArray(new String[0]);
    }

    /**
     * Returns the level which is displayed.
     * @return The level which is displayed.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Same as {@link ScalableMap#goToLevel(int, boolean)}. But the resetLevel parameter is false.
     * @param l
     */
    public void goToLevel(int l) {
        goToLevel(l, false);
    }

    /**
     * Displays the given level.
     * @param l Level to display.
     * @param resetSize If false keeps the scale and possession, if true uses the default.
     */
    public void goToLevel(int l, boolean resetSize) {
        if (levelMin <= l && l <= levelMax && l != level) {
            level = l;
            float scale = getScale();
            PointF center = getCenter();
            redrawPicture();
            invalidate();
            if (!resetSize) {
                setScaleAndCenter(scale, center);
            }
        }
    }

    /**
     * Is there a level above us?
     */
    public boolean isLevelUp() {
        return level < levelMax;
    }

    /**
     * Is there a level bellow us?
     */
    public boolean isLevelDown() {
        return level > levelMin;
    }

    /**
     * Displays the level above us, if there is any.
     */
    public void levelUp() {
        goToLevel(level+1);
    }

    /**
     * Displays the level bellow us, if there is any.
     */
    public void levelDown() {
        goToLevel(level-1);
    }

    /**
     * Same as {@link #findClosestRoomNodeInLevel(Coordinates, float)}. But {@link Coordinates} is
     * created from x and y and radius is 100.
     * @param x The x coordinate of the center.
     * @param y The y coordinate of the center.
     * @return Name of the {@link Node} closest to the center.
     */
    private String findClosestRoomNodeInLevel(float x, float y) {
        return findClosestRoomNodeInLevel(new Coordinates(x, y), 100);
    }

    /**
     * Returns the name of the {@link Node} closest to the c in circle whit the given radius. If
     * there is no {@link Node} in the circle returns an empty String.
     * @param c Center of the circle.
     * @return Name of the {@link Node} closest to the center.
     */
    private String findClosestRoomNodeInLevel(Coordinates c, float radius) {
        String minName = "";
        for (Map.Entry<String, ? extends Node> e : rf.getNodesInLevel(level).entrySet()) {
            Node n = e.getValue();
            if (n.getType() != NodeType.FLOOR) {
                float dist = (float) n.distance(c);
                if (dist < radius) {
                    radius = dist;
                    minName = e.getKey();
                }
            }
        }
        return minName;
    }

    /**
     * "Casts" {@link Coordinates} to PointF.
     */
    private PointF coordinatesToPointF(Coordinates c) {
        return new PointF((float) c.getX(), (float) c.getY());
    }

    /**
     * First calls {@link #findClosestRoomNodeInLevel(float, float)}, if returns empty String, this
     * function returns null. If not and a {@link Route} is shown, checks f with
     * {@link Node#isElavatorOrStairhouse()}, if true displays the level tho which the {@link Route}
     * leads. If false marks the {@link Node} as clicked
     * @param f The point to investigate.
     * @return The name returned by {@link #findClosestRoomNodeInLevel(float, float)}.
     */
    public String clickHere(PointF f) {
        String name = findClosestRoomNodeInLevel(f.x, f.y);
        if (name == "") {
            clicked = null;
            return null;
        } else {
            Node newClicked = rf.get(name);
            if (toShow != null && newClicked.isElavatorOrStairhouse()) {
                int newLevel = toWhichFloorDoesItGo(newClicked);
                goToLevel(newLevel);
                return null;
            } else {
                clicked = newClicked;
            }
        }
        invalidate();
        return name;
    }

    /**
     * Displays the level where the room is and scales on it. If name is null deselect selected room.
     * @param name
     */
    public void highlightRoom(String name) {
        if (rf.get(name) == null) {
            clicked = null;
            invalidate();
        } else {
            clicked = rf.get(name);
            invalidate();
            goToLevel(rf.getLevelOfNode(clicked), true);
            if (isReady()) {
                animateScaleAndCenter(2f, coordinatesToPointF(clicked.getCords())).start();
            } else {
                setScaleAndCenter(2f, coordinatesToPointF(clicked.getCords()));
            }
        }
    }

    /**
     * Plans and displays the fastest {@link Route} between the {@link Node}s to which the names refer.
     * @return False if there exist no {@link Route}.
     */
    public boolean showRoute(String from, String to) {
        Route route = rf.findRout(from, to);
        if (route == null) {
            return false;
        } else {
            toShow = route;
            clicked = null;
            invalidate();
            return true;
        }
    }

    /**
     * Removes any kind of graphic from the picture.
     */
    public void dontShowAnything() {
        toShow = null;
        clicked = null;
    }

    /**
     * If a {@link Route} is displayed and the given {@link Node} is an elevator or stairhouse
     * returns the level to which it leads on the {@link Route}. If there is no {@link Route} or n is
     * not elevator or stairhouse returns the level of n.
     * @param n
     * @return
     */
    public int toWhichFloorDoesItGo(Node n) {
        if (toShow == null || !n.isElavatorOrStairhouse()) {
            return rf.getLevelOfNode(n);
        } else {
            Node[] neighborsOfN = toShow.getNeighborsInRoute(n);
            Node a = n;
            Node b = null;
            if (neighborsOfN.length == 1) {
                b = neighborsOfN[0];
            } else if (neighborsOfN.length == 2) {
                Node candidate1 = neighborsOfN[0];
                Node candidate2 = neighborsOfN[1];
                if (candidate1.isElavatorOrStairhouse()) {
                    b = candidate1;
                } else {
                    b = candidate2;
                }
            } else {
                return rf.getLevelOfNode(n);
            }
            while (b.isElavatorOrStairhouse()) {
                Node c = toShow.getNextNodeInRoute(a, b);
                a = b;
                b = c;
            }
            return rf.getLevelOfNode(b);
        }
    }

    /**
     * Draws {@link Route} and highlighted {@link Node}s.
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        //canvas.save();


        float lineSize = 10;
        float nodeSize = 20;

        Paint color = new Paint();
        color.setStyle(Paint.Style.FILL);
        color.setStrokeWidth(lineSize);
        color.setAntiAlias(true);

        Paint textColor = new Paint();
        textColor.setTextAlign(Paint.Align.CENTER);
        textColor.setTextSize(2*nodeSize);
        textColor.setColor(Color.BLACK);
        //Draw edges
        for (Edge e : rf.getEdgesInLevel(level)) {
            Node[] neighbors = e.getNeighbors();
            Node a = neighbors[0];
            Node b = neighbors[1];

            Coordinates aC = a.getCords();
            float aX = (float) aC.getX();
            float aY = (float) aC.getY();
            PointF aCoord = sourceToViewCoord(aX, aY);
            aX = aCoord.x;
            aY = aCoord.y;
            Coordinates bC = b.getCords();
            float bX = (float) bC.getX();
            float bY = (float) bC.getY();
            PointF bCoord = sourceToViewCoord(bX, bY);
            bX = bCoord.x;
            bY = bCoord.y;

            if (toShow != null && toShow.contains(e)) {
                color.setColor(Color.GREEN);
            } else {
                color.setColor(-1);
            }
            if (color.getColor() != -1) {
                canvas.drawLine(aX, aY, bX, bY, color);
            }
        }

        //Draw nodes
        for (Node n : rf.getNodesInLevel(level).values()) {
            Coordinates c = n.getCords();
            float x = (float) c.getX();
            float y = (float) c.getY();
            PointF vCoord = sourceToViewCoord(x, y);
            x = vCoord.x;
            y = vCoord.y;
            if (clicked == n) {
                color.setColor(Color.YELLOW);
            } else if (toShow != null && toShow.contains(n)) {
                color.setColor(Color.GREEN);
            } else {
                color.setColor(-1);
            }

            if (color.getColor() != -1) {
                switch (n.getType()) {
                    case FLOOR:
                        canvas.drawCircle(x, y, lineSize / 2, color);
                        break;
                    case ELEVATOR:
                        canvas.drawCircle(x, y, 2*nodeSize, color);
                        int targetLevel = toWhichFloorDoesItGo(n);
                        canvas.drawText(String.valueOf(targetLevel), x, y, textColor);
                        break;
                    case STAIRHOUSE:
                        canvas.drawCircle(x, y, 2*nodeSize, color);
                        targetLevel = toWhichFloorDoesItGo(n);
                        canvas.drawText(String.valueOf(targetLevel), x, y, textColor);
                        break;
                    case ROOM:
                        canvas.drawCircle(x, y, nodeSize, color);
                        break;
                }
            }
        }
        //canvas.restore();
    }
}
