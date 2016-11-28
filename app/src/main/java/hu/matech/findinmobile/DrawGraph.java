//package hu.matech.findinmobile;
//
//import android.content.Context;
//import android.content.res.AssetManager;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.ScaleGestureDetector;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Collection;
//
//import hu.matech.FindInEngine.Coordinates;
//import hu.matech.FindInEngine.Edge;
//import hu.matech.FindInEngine.Node;
//import hu.matech.FindInEngine.RoutFinder;
//
///**
// * Created by matech on 2016.10.24..
// */
//
//public class DrawGraph extends View {
//    RoutFinder rf;
//    int level = 1;
//
//    public DrawGraph(Context context) {
//        super(context);
//        setUpMap(context);
//    }
//
//    public DrawGraph(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        setUpMap(context);
//    }
//
//    public DrawGraph(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        setUpMap(context);
//    }
//
//    private void setUpMap(Context context) {
////        FrameLayout map = (FrameLayout) findViewById(R.id.map);
////        DrawGraph graph = (DrawGraph) findViewById(R.id.graph);
////        ImageView pic = (ImageView) findViewById(R.id.pic);
//
//
//        rf = new RoutFinder();
//        float offsetX = 0;
//        float offsetY = 0;
//        float numLevels =2;
//        for (int i = 1; i <= numLevels; i++) {
//            try {
//                InputStream is = context.getAssets().open("map_level_" + i + ".json");
//                rf.addLevel(is, i);
//                for (Node n : rf.getPlaces().values()) {
//                    Coordinates c = n.getCords();
//                    float x = (float) c.getX() - offsetX;
//                    float y = (float) c.getY() - offsetY;
//                    n.setCords(x, y);
//                }
//                is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        canvas.save();
//
//        ImageView pic = (ImageView) findViewById(R.id.pic);
//        pic.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        pic.setImageResource(R.drawable.map_level_1);
//
//
//        float lineSize = 10;
//        float nodeSize = 20;
//
//        Paint blue = new Paint();
//        blue.setColor(Color.BLUE);
//        blue.setStyle(Paint.Style.FILL);
//        blue.setStrokeWidth(lineSize);
//        blue.setAntiAlias(true);
//
//        for (Node n : rf.getNodesInLevel(level).values()) {
//            Coordinates c = n.getCords();
//            float x = (float) c.getX();
//            float y = (float) c.getY();
//            switch (n.getType()) {
//                case FLOOR:
//                    canvas.drawCircle(x, y,lineSize/2, blue);
//                    break;
//                case ELEVATOR:
//                    canvas.drawCircle(x, y,nodeSize, blue);
//                    break;
//                case STAIRHOUSE:
//                    canvas.drawCircle(x, y,nodeSize, blue);
//                    break;
//                case ROOM:
//                    canvas.drawCircle(x, y,nodeSize, blue);
//                    break;
//            }
//        }
//
//
//        for (Edge e : rf.getEdgesInLevel(level)) {
//            Node[] neighbors = e.getNeighbors();
//            Node a = neighbors[0];
//            Node b = neighbors[1];
//
//            Coordinates aC = a.getCords();
//            float aX = (float) aC.getX();
//            float aY = (float) aC.getY();
//            Coordinates bC = b.getCords();
//            float bX = (float) bC.getX();
//            float bY = (float) bC.getY();
//
//            canvas.drawLine(aX, aY, bX, bY, blue);
//        }
//        canvas.restore();
//    }
//
//}
