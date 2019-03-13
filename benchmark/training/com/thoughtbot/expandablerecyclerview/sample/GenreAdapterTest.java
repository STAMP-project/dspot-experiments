package com.thoughtbot.expandablerecyclerview.sample;


import ExpandableListPosition.CHILD;
import ExpandableListPosition.GROUP;
import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.widget.LinearLayout;
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition;
import com.thoughtbot.expandablerecyclerview.sample.expand.ArtistViewHolder;
import com.thoughtbot.expandablerecyclerview.sample.expand.GenreAdapter;
import com.thoughtbot.expandablerecyclerview.sample.expand.GenreViewHolder;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Unit test for GenreAdapter
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class GenreAdapterTest {
    private Context context;

    private List<Genre> groups;

    @Test
    public void test_onCreateViewHolder() {
        GenreAdapter adapter = new GenreAdapter(groups);
        LinearLayout parent = new LinearLayout(context);
        // child view holder
        ViewHolder childViewHolder = adapter.onCreateViewHolder(parent, CHILD);
        Assert.assertTrue((childViewHolder instanceof ArtistViewHolder));
        // group view holder
        ViewHolder groupViewHolder = adapter.onCreateViewHolder(parent, GROUP);
        Assert.assertTrue((groupViewHolder instanceof GenreViewHolder));
    }

    @Test
    public void test_getItemCount() {
        GenreAdapter adapter = new GenreAdapter(groups);
        // initial state
        int initialExpected = 5;
        int initialActual = adapter.getItemCount();
        Assert.assertEquals(initialExpected, initialActual);
    }

    @Test
    public void test_getItemViewType() {
        GenreAdapter adapter = new GenreAdapter(groups);
        // initial state
        int initialExpected = ExpandableListPosition.GROUP;
        int initialActual = adapter.getItemViewType(3);
        Assert.assertEquals(initialExpected, initialActual);
    }
}

