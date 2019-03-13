package org.robolectric.shadows;


import android.app.TabActivity;
import android.widget.TabHost;
import android.widget.TabWidget;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.Robolectric;

import static org.robolectric.R.layout.tab_activity;


@RunWith(AndroidJUnit4.class)
public class ShadowTabActivityTest {
    @Test
    public void tabActivityShouldNotMakeNewTabHostEveryGet() throws Exception {
        TabActivity activity = Robolectric.buildActivity(TabActivity.class).create().get();
        TabHost tabHost1 = activity.getTabHost();
        TabHost tabHost2 = activity.getTabHost();
        assertThat(tabHost1).isEqualTo(tabHost2);
    }

    @Test
    public void shouldGetTabWidget() throws Exception {
        TabActivity activity = Robolectric.buildActivity(TabActivity.class).create().get();
        activity.setContentView(tab_activity);
        assertThat(activity.getTabWidget()).isInstanceOf(TabWidget.class);
    }
}

