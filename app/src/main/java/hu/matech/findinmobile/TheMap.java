//package hu.matech.findinmobile;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.ScaleGestureDetector;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//
///**
// * Created by matech on 2016.10.25..
// */
//
//public class TheMap extends FrameLayout {
//
//    private float mPosX;
//    private float mPosY;
//
//    private float mLastTouchX;
//    private float mLastTouchY;
//    private ScaleGestureDetector mScaleDetector;
//    private float mScaleFactor = 1.f;
//
//    private static final int INVALID_POINTER_ID = -1;
//
//    // The ‘active pointer’ is the one currently moving our object.
//    private int mActivePointerId = INVALID_POINTER_ID;
//
//    public TheMap(Context context) {
//        super(context);
//        // Create our ScaleGestureDetector
//        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
//    }
//
//    public TheMap(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        // Create our ScaleGestureDetector
//        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
//    }
//
//    public TheMap(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        // Create our ScaleGestureDetector
//        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        FrameLayout map = (FrameLayout) findViewById(R.id.map);
//        // Let the ScaleGestureDetector inspect all events.
//        mScaleDetector.onTouchEvent(ev);
//
//        final int action = ev.getAction();
//        switch (action & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN: {
//                final float x = ev.getX();
//                final float y = ev.getY();
//
//                mLastTouchX = x;
//                mLastTouchY = y;
//                mActivePointerId = ev.getPointerId(0);
//                break;
//            }
//
//            case MotionEvent.ACTION_MOVE: {
//                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
//                final float x = ev.getX(pointerIndex);
//                final float y = ev.getY(pointerIndex);
//
//                // Only move if the ScaleGestureDetector isn't processing a gesture.
//                if (!mScaleDetector.isInProgress()) {
//                    final float dx = x - mLastTouchX;
//                    final float dy = y - mLastTouchY;
//
//                    mPosX += dx;
//                    mPosY += dy;
//                    map.setX(mPosX);
//                    map.setY(mPosY);
//                    correctPosition();
//
//                    invalidate();
//                }
//
//                mLastTouchX = x;
//                mLastTouchY = y;
//
//                break;
//            }
//
//            case MotionEvent.ACTION_UP: {
//                mActivePointerId = INVALID_POINTER_ID;
//                break;
//            }
//
//            case MotionEvent.ACTION_CANCEL: {
//                mActivePointerId = INVALID_POINTER_ID;
//                break;
//            }
//
//            case MotionEvent.ACTION_POINTER_UP: {
//                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
//                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
//                final int pointerId = ev.getPointerId(pointerIndex);
//                if (pointerId == mActivePointerId) {
//                    // This was our active pointer going up. Choose a new
//                    // active pointer and adjust accordingly.
//                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
//                    mLastTouchX = ev.getX(newPointerIndex);
//                    mLastTouchY = ev.getY(newPointerIndex);
//                    mActivePointerId = ev.getPointerId(newPointerIndex);
//                }
//                break;
//            }
//        }
//
//        return true;
//    }
//
//    private void correctPosition() {
//        FrameLayout map = (FrameLayout) findViewById(R.id.map);
//        float w = (float) getWidth();
//        float h = (float) getHeight();
//        float mapW = (float) map.getWidth();
//        float mapH = (float) map.getHeight();
//        float scale = map.getScaleX();
//        float scaleOffsetW = getX() + (1-scale) * mapW / 2;
//        float scaleOffsetH = getY() + (1-scale) * mapH / 2;
//        mapW *= scale;
//        mapH *= scale;
//
//        mPosX = Math.min(Math.max(mPosX, w-mapW-scaleOffsetW), -scaleOffsetW);
//        mPosY = Math.min(Math.max(mPosY, h-mapH-scaleOffsetH), -scaleOffsetH);
//        map.setX(mPosX);
//        map.setY(mPosY);
//
//    }
//
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//            FrameLayout map = (FrameLayout) findViewById(R.id.map);
//            mScaleFactor *= detector.getScaleFactor();
//
//            // Don't let the object get too small or too large.
//            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
//            map.setScaleX(mScaleFactor);
//            map.setScaleY(mScaleFactor);
//            correctPosition();
//
////            DrawGraph graph = (DrawGraph) findViewById(R.id.graph);
////            graph.invalidate();
//            return true;
//        }
//    }
//}
