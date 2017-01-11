package hu.matech.findinmobile;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

/**
 * Created by matech on 2017.01.01..
 */
@RunWith(AndroidJUnit4.class)
public class ScalableMapTest {
    @Rule
    public ActivityTestRule<MainActivity> ma = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void getToShow() throws Exception {
//        assertNull(sm.getToShow());
    }

}