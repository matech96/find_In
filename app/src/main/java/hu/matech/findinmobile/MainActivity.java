package hu.matech.findinmobile;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set up stuff
        setContentView(R.layout.activity_main);

        ///LEGACY///
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
        ////////////

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
                    search.setText(room);
                    search.setAdapter(adapter);

                    if (!room.equals("")) {
                        selectRoom(search.getText().toString());
                    } else {
                        LinearLayout placeCard = (LinearLayout) findViewById(R.id.placeCard);
                        placeCard.setVisibility(View.GONE);
                    }
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

        map.highlightRoom(name);
        redrawLevelButtons();

        LinearLayout ll = (LinearLayout) findViewById(R.id.placeCard);
//        int toMove = -ll.getHeight();
//        TranslateAnimation tr = new TranslateAnimation(0, 0, 0, toMove);
//        tr.setDuration(500);
//        tr.setFillAfter(true);
        ll.setVisibility(View.VISIBLE);
//        ll.startAnimation(tr);
//        ll.setY(ll.getY()+toMove);

//        ll.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                LinearLayout ll = (LinearLayout) findViewById(R.id.placeCard);
//                ll.setY(ll.getY()+100);
//            }
//        }, 500);
    }

    public void handelButtonClick(View v) {
//        EditText from = (EditText) findViewById(R.id.from);
//        EditText to = (EditText) findViewById(R.id.to);

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
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }

        LinearLayout placeCard = (LinearLayout) findViewById(R.id.placeCard);
        if (placeCard.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            ScalableMap map = (ScalableMap) findViewById(R.id.map);
            map.dontShowRoute();
            placeCard.setVisibility(View.GONE);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
