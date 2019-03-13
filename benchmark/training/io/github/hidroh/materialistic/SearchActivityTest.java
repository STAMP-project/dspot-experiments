package io.github.hidroh.materialistic;


import AlgoliaClient.sSortByTime;
import ItemManager.MODE_DEFAULT;
import R.id.menu_sort_popular;
import R.id.menu_sort_recent;
import R.string.title_activity_search;
import SearchManager.QUERY;
import android.content.Intent;
import io.github.hidroh.materialistic.data.ItemManager;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.ShadowSearchRecentSuggestions;
import javax.inject.Inject;
import javax.inject.Named;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static ActivityModule.ALGOLIA;


@Config(shadows = ShadowSearchRecentSuggestions.class)
@RunWith(TestRunner.class)
public class SearchActivityTest {
    private ActivityController<SearchActivity> controller;

    private SearchActivity activity;

    @Inject
    @Named(ALGOLIA)
    ItemManager itemManager;

    @Test
    public void testCreateWithQuery() {
        Intent intent = new Intent();
        intent.putExtra(QUERY, "filter");
        controller = Robolectric.buildActivity(SearchActivity.class, intent);
        controller.create().start().resume();// skip menu inflation

        activity = controller.get();
        assertThat(ShadowSearchRecentSuggestions.recentQueries).contains("filter");
        Assert.assertEquals(activity.getString(title_activity_search), activity.getDefaultTitle());
        Assert.assertEquals("filter", activity.getSupportActionBar().getSubtitle());
    }

    @Test
    public void testCreateWithoutQuery() {
        controller.create().start().resume();// skip menu inflation

        assertThat(ShadowSearchRecentSuggestions.recentQueries).isEmpty();
        Assert.assertEquals(activity.getString(title_activity_search), activity.getDefaultTitle());
        Assert.assertNull(activity.getSupportActionBar().getSubtitle());
    }

    @Test
    public void testSort() {
        Intent intent = new Intent();
        intent.putExtra(QUERY, "filter");
        controller = Robolectric.buildActivity(SearchActivity.class, intent);
        controller.create().postCreate(null).start().resume().visible();
        activity = controller.get();
        Assert.assertTrue(sSortByTime);
        activity.onOptionsItemSelected(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_sort_recent));// should not trigger search

        activity.onOptionsItemSelected(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_sort_popular));// should trigger search

        Assert.assertFalse(sSortByTime);
        Mockito.verify(itemManager, Mockito.times(2)).getStories(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT));
    }
}

