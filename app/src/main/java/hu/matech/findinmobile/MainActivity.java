package hu.matech.findinmobile;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set up stuff
        setContentView(R.layout.activity_main);

        //Set up the map
        final ScalableMap map = (ScalableMap) findViewById(R.id.map);
        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (map.isReady()) {
                    PointF sCoord = map.viewToSourceCoord(e.getX(), e.getY());
                    AutoCompleteTextView search = (AutoCompleteTextView) findViewById(R.id.search);

                    ArrayAdapter adapter = (ArrayAdapter) search.getAdapter();
                    search.setAdapter(null);
                    String room = map.clickHere(sCoord);

                    if (room != null) {
                        search.setText(room);
                        selectRoom(search.getText().toString());
                    } else {
                        redrawLevelButtons();
                        LinearLayout placeCard = (LinearLayout) findViewById(R.id.placeCard);
                        placeCard.setVisibility(View.GONE);
                    }
                    search.setAdapter(adapter);
                }
                return true;
            }
        });
        map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        //Set up upper search bar
        AutoCompleteTextView search = (AutoCompleteTextView) findViewById(R.id.search);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                             @Override
                                             public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                                 selectRoom(v.getText().toString());
                                                 return false;
                                             }
                                         });
        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                                                selectRoom((String) parent.getItemAtPosition(position));
                                            }
        });

        //Set up place card search box
        AutoCompleteTextView PLsearch = (AutoCompleteTextView) findViewById(R.id.PLsearch);
        PLsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                ScalableMap map = (ScalableMap) findViewById(R.id.map);
                AutoCompleteTextView search = (AutoCompleteTextView) findViewById(R.id.search);
                map.showRoute(v.getText().toString(), search.getText().toString());

                selectRoom(v.getText().toString());
                return false;
            }
        });
        PLsearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScalableMap map = (ScalableMap) findViewById(R.id.map);
//                map.highlightRoom((String) parent.getItemAtPosition(position));
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                AutoCompleteTextView search = (AutoCompleteTextView) findViewById(R.id.search);
                map.showRoute((String) parent.getItemAtPosition(position), search.getText().toString());
                selectRoom((String) parent.getItemAtPosition(position));

            }
        });


        String[] places = map.getPlaces();
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this,android.R.layout.simple_list_item_1,places);
        search.setAdapter(adapter);
        search.setThreshold(1);
        PLsearch.setAdapter(adapter);
        PLsearch.setThreshold(1);

        redrawLevelButtons();
    }

    public void selectRoom(String name) {
        ScalableMap map = (ScalableMap) findViewById(R.id.map);

        LinearLayout ll = (LinearLayout) findViewById(R.id.placeCard);
        ll.setVisibility(View.VISIBLE);

        map.highlightRoom(name);
        redrawLevelButtons();
    }

    public void handelButtonClick(View v) {
        AutoCompleteTextView from = (AutoCompleteTextView) findViewById(R.id.search);
        AutoCompleteTextView to = (AutoCompleteTextView) findViewById(R.id.PLsearch);

        ScalableMap map = (ScalableMap) findViewById(R.id.map);
        map.showRoute(from.getText().toString(), to.getText().toString());
        selectRoom(to.getText().toString());
    }

    public void levelUp(View v) {
        ScalableMap map = (ScalableMap) findViewById(R.id.map);
        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!map.isReady()) {
            return;
        }
        map.levelUp();
        redrawLevelButtons();
    }

    public void levelDown(View v) {
        ScalableMap map = (ScalableMap) findViewById(R.id.map);
        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!map.isReady()) {
            return;
        }
        map.levelDown();
        redrawLevelButtons();
    }

    public void redrawLevelButtons() {
        ScalableMap map = (ScalableMap) findViewById(R.id.map);
        FloatingActionButton up = (FloatingActionButton) findViewById(R.id.up);
        FloatingActionButton down = (FloatingActionButton) findViewById(R.id.down);
        TextView level = (TextView) findViewById(R.id.levelTextView);

        level.setText(String.valueOf(map.getLevel()));

        if (map.isLevelUp()) {
            up.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        } else {
            up.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        }
        if (map.isLevelDown()) {
            down.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        } else {
            down.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
        }
    }

    @Override
    public void onBackPressed() {
        LinearLayout placeCard = (LinearLayout) findViewById(R.id.placeCard);
        if (placeCard.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            ScalableMap map = (ScalableMap) findViewById(R.id.map);
            map.dontShowAnything();
            placeCard.setVisibility(View.GONE);

        }
    }
}
