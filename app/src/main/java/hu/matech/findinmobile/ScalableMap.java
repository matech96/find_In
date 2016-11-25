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
import hu.matech.FindInEngine.Rout;
import hu.matech.FindInEngine.RoutFinder;

/**
 * Created by matech on 2016.11.04..
 */

public class ScalableMap extends SubsamplingScaleImageView {
    static RoutFinder rf;

    static Node clicked;

    public static Rout getToShow() {
        return toShow;
    }

    public static Rout toShow;

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
//        FrameLayout map = (FrameLayout) findViewById(R.id.map);
//        DrawGraph graph = (DrawGraph) findViewById(R.id.graph);
//        ImageView pic = (ImageView) findViewById(R.id.pic);

        redrawPicture();

        rf = new RoutFinder();
        float offsetX = 0;
        float offsetY = 0;
        float numLevels =2;
        for (int i = 1; i <= numLevels; i++) {
            try {
                InputStream is = context.getAssets().open("map_level_" + i + ".json");
                rf.addLevel(is, i);
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

    public void redrawPicture() {
        ScalableMap map = (ScalableMap) findViewById(R.id.map);
//        map.setImage(ImageSource.asset("map_level_" + String.valueOf(level) + ".jpg"));
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

    public String [] getPlaces() {
        return rf.getPlaces().keySet().toArray(new String[0]);
    }

    public int getLevel() {
        return level;
    }

    public void goToLevel(int l) {
        goToLevel(l, false);
    }

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

    public boolean isLevelUp() {
        return level < levelMax;
    }

    public boolean isLevelDown() {
        return level > levelMin;
    }

    public void levelUp() {
        goToLevel(level+1);
    }

    public void levelDown() {
        goToLevel(level-1);
    }

    private String findClosestRoomNodeInLevel(float x, float y) {
        return findClosestRoomNodeInLevel(new Coordinates(x, y));
    }

    private String findClosestRoomNodeInLevel(Coordinates c) {
        float minDist = 100;
        String minName = "";
        for (Map.Entry<String, ? extends Node> e : rf.getNodesInLevel(level).entrySet()) {
            Node n = e.getValue();
            if (n.getType() != NodeType.FLOOR) {
                float dist = (float) n.distance(c);
                if (dist < minDist) {
                    minDist = dist;
                    minName = e.getKey();
                }
            }
        }
        return minName;
    }

    private PointF coordinatesToPointF(Coordinates c) {
        return new PointF((float) c.getX(), (float) c.getY());
    }

    public String clickHere(PointF f) {
        String name = findClosestRoomNodeInLevel(f.x, f.y);
        if (name == "") {
            clicked = null;
        } else {
            Node newClicked = rf.get(name);
            if (toShow != null && (newClicked.getType() == NodeType.ELAVATOR || newClicked.getType() == NodeType.STAIRHOUSE)) {
                int newLevel = toWhichFloorDoesItGo(newClicked);
                goToLevel(newLevel);
                return null;
            } else if (newClicked == clicked) {
                highlightRoom(name);
            } else {
                clicked = newClicked;
            }
        }
        invalidate();
        return name;
    }

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

    public boolean showRoute(String from, String to) {
        Rout route = rf.findRout(from, to);
        if (route == null) {
            return false;
        } else {
            toShow = route;
            clicked = null;
            invalidate();
            return true;
        }
    }

    public void dontShowRoute() {
        toShow = null;
        clicked = null;
    }

    public int toWhichFloorDoesItGo(Node n) {
        if (toShow == null || (n.getType() != NodeType.ELAVATOR && n.getType() != NodeType.STAIRHOUSE)) {
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
                if (candidate1.getType() == NodeType.ELAVATOR || candidate1.getType() == NodeType.STAIRHOUSE) {
                    b = candidate1;
                } else {
                    b = candidate2;
                }
            } else {
                return rf.getLevelOfNode(n);
            }
            while (b.getType() == NodeType.ELAVATOR || b.getType() == NodeType.STAIRHOUSE) {
                Node c = toShow.getNextNodeInRoute(a, b);
                a = b;
                b = c;
            }
            return rf.getLevelOfNode(b);
        }
    }

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
                    case ELAVATOR:
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
