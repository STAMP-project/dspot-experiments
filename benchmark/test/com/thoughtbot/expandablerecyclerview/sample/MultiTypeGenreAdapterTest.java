package com.thoughtbot.expandablerecyclerview.sample;


import android.content.Context;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;
import com.thoughtbot.expandablerecyclerview.sample.multitype.MultiTypeGenreAdapter;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Unit test for MultiTypeGenreAdapter
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class MultiTypeGenreAdapterTest {
    private Context context;

    private List<Genre> groups;

    @Test
    public void test_getItemViewType() {
        MultiTypeGenreAdapter adapter = new MultiTypeGenreAdapter(groups);
        // initial state
        int initialExpected = ExpandableListPosition.GROUP;
        int initialActual = adapter.getItemViewType(3);
        Assert.assertEquals(initialExpected, initialActual);
        // expand first group
        adapter.toggleGroup(0);
        int newExpected = MultiTypeGenreAdapter.ARTIST_VIEW_TYPE;
        int newActual = adapter.getItemViewType(3);
        Assert.assertEquals(newExpected, newActual);
    }

    @Test
    public void test_isChild() {
        MultiTypeGenreAdapter adapter = new MultiTypeGenreAdapter(groups);
        int validChildViewType = MultiTypeGenreAdapter.ARTIST_VIEW_TYPE;
        int inValidChildViewType = ExpandableListPosition.GROUP;
        Assert.assertTrue(adapter.isChild(validChildViewType));
        Assert.assertFalse(adapter.isChild(inValidChildViewType));
    }
}

