package org.robolectric.shadows;


import android.widget.ExpandableListView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ShadowExpandableListViewTest {
    private ExpandableListView expandableListView;

    @Test
    public void shouldTolerateNullChildClickListener() throws Exception {
        expandableListView.performItemClick(null, 6, (-1));
    }
}

