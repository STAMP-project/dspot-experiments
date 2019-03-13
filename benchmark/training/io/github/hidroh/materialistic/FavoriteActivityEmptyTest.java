package io.github.hidroh.materialistic;


import R.id.bookmarked;
import R.id.empty;
import R.id.empty_search;
import R.id.header_card_view;
import R.id.menu_clear;
import R.id.menu_search;
import SearchManager.QUERY;
import android.content.Intent;
import io.github.hidroh.materialistic.data.FavoriteManager;
import io.github.hidroh.materialistic.data.LocalItemManager;
import io.github.hidroh.materialistic.test.TestRunner;
import javax.inject.Inject;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;


@RunWith(TestRunner.class)
public class FavoriteActivityEmptyTest {
    private ActivityController<FavoriteActivity> controller;

    private FavoriteActivity activity;

    @Inject
    FavoriteManager favoriteManager;

    @Captor
    ArgumentCaptor<LocalItemManager.Observer> observerCaptor;

    @Test
    public void testEmpty() {
        assertThat(((android.view.View) (activity.findViewById(empty)))).isVisible();
        activity.findViewById(header_card_view).performLongClick();
        assertThat(((android.view.View) (activity.findViewById(header_card_view).findViewById(bookmarked)))).isVisible();
        activity.findViewById(header_card_view).performLongClick();
        assertThat(((android.view.View) (activity.findViewById(header_card_view).findViewById(bookmarked)))).isNotVisible();
        Assert.assertNull(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_clear));
        assertThat(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_search)).isNotVisible();
    }

    @Test
    public void testDataChanged() {
        assertThat(((android.view.View) (activity.findViewById(empty_search)))).isNotVisible();
        Mockito.reset(favoriteManager);
        controller.newIntent(new Intent().putExtra(QUERY, "query"));
        Mockito.verify(favoriteManager, Mockito.atLeastOnce()).attach(observerCaptor.capture(), ArgumentMatchers.any());
        observerCaptor.getValue().onChanged();
        assertThat(((android.view.View) (activity.findViewById(empty_search)))).isVisible();
    }
}

