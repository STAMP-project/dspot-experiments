package io.github.hidroh.materialistic;


import ItemManager.MODE_DEFAULT;
import ItemManager.MODE_NETWORK;
import ItemManager.TOP_FETCH_MODE;
import ListFragment.EXTRA_FILTER;
import ListFragment.EXTRA_ITEM_MANAGER;
import R.id;
import R.id.empty;
import R.id.snackbar_action;
import R.id.snackbar_text;
import R.id.swipe_layout;
import R.plurals.new_stories_count;
import R.plurals.showing_new_stories;
import R.string.pref_list_item_view;
import android.R.id.list;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;
import io.github.hidroh.materialistic.data.HackerNewsClient;
import io.github.hidroh.materialistic.data.Item;
import io.github.hidroh.materialistic.data.ItemManager;
import io.github.hidroh.materialistic.data.TestHnItem;
import io.github.hidroh.materialistic.test.ListActivity;
import io.github.hidroh.materialistic.test.TestItem;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.ShadowPreferenceFragmentCompat;
import io.github.hidroh.materialistic.test.shadow.ShadowSnackbar;
import io.github.hidroh.materialistic.test.shadow.ShadowSwipeRefreshLayout;
import javax.inject.Inject;
import javax.inject.Named;
import junit.framework.Assert;
import org.assertj.android.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;

import static ActivityModule.HN;


@Config(shadows = { ShadowSwipeRefreshLayout.class, ShadowSnackbar.class, ShadowPreferenceFragmentCompat.class })
@RunWith(TestRunner.class)
public class ListFragmentTest {
    private ActivityController<ListActivity> controller;

    private ListActivity activity;

    @Inject
    @Named(HN)
    ItemManager itemManager;

    @Test
    public void testOnCreate() {
        Bundle args = new Bundle();
        args.putString(EXTRA_ITEM_MANAGER, HackerNewsClient.class.getName());
        args.putString(EXTRA_FILTER, TOP_FETCH_MODE);
        getSupportFragmentManager().beginTransaction().add(list, Fragment.instantiate(activity, ListFragment.class.getName(), args)).commit();
        assertThat(((android.support.v4.widget.SwipeRefreshLayout) (activity.findViewById(swipe_layout)))).isRefreshing();
        controller.pause().stop().destroy();
    }

    @Test
    public void testRefresh() {
        Bundle args = new Bundle();
        args.putString(EXTRA_ITEM_MANAGER, HackerNewsClient.class.getName());
        args.putString(EXTRA_FILTER, TOP_FETCH_MODE);
        getSupportFragmentManager().beginTransaction().add(list, Fragment.instantiate(activity, ListFragment.class.getName(), args)).commit();
        ShadowSwipeRefreshLayout shadowSwipeRefreshLayout = Shadow.extract(activity.findViewById(swipe_layout));
        shadowSwipeRefreshLayout.getOnRefreshListener().onRefresh();
        // should trigger another data request
        Mockito.verify(itemManager).getStories(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(MODE_DEFAULT));
        Mockito.verify(itemManager).getStories(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(MODE_NETWORK));
        controller.pause().stop().destroy();
    }

    @Test
    public void testHighlightNewItems() {
        Mockito.when(itemManager.getStories(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT))).thenReturn(new Item[]{ new TestItem() {
            @Override
            public String getId() {
                return "1";
            }
        } });
        Bundle args = new Bundle();
        args.putString(EXTRA_ITEM_MANAGER, HackerNewsClient.class.getName());
        args.putString(EXTRA_FILTER, TOP_FETCH_MODE);
        getSupportFragmentManager().beginTransaction().add(list, Fragment.instantiate(activity, ListFragment.class.getName(), args)).commit();
        Mockito.verify(itemManager).getStories(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT));
        Mockito.reset(itemManager);
        Mockito.when(itemManager.getStories(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_NETWORK))).thenReturn(new Item[]{ new TestHnItem(1L), new TestHnItem(2L) });
        ShadowSwipeRefreshLayout shadowSwipeRefreshLayout = Shadow.extract(activity.findViewById(swipe_layout));
        shadowSwipeRefreshLayout.getOnRefreshListener().onRefresh();
        // should trigger another data request
        Mockito.verify(itemManager).getStories(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(MODE_NETWORK));
        Assert.assertEquals(2, getAdapter().getItemCount());
        View snackbarView = ShadowSnackbar.getLatestView();
        Assertions.assertThat(((TextView) (snackbarView.findViewById(snackbar_text)))).isNotNull().containsText(getResources().getQuantityString(new_stories_count, 1, 1));
        snackbarView.findViewById(snackbar_action).performClick();
        Assert.assertEquals(1, getAdapter().getItemCount());
        snackbarView = ShadowSnackbar.getLatestView();
        Assertions.assertThat(((TextView) (snackbarView.findViewById(snackbar_text)))).isNotNull().containsText(getResources().getQuantityString(showing_new_stories, 1, 1));
        snackbarView.findViewById(snackbar_action).performClick();
        Assert.assertEquals(2, getAdapter().getItemCount());
        controller.pause().stop().destroy();
    }

    @Test
    public void testConfigurationChanged() {
        Mockito.when(itemManager.getStories(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT))).thenReturn(new Item[]{ new TestItem() {} });
        Bundle args = new Bundle();
        args.putString(EXTRA_ITEM_MANAGER, HackerNewsClient.class.getName());
        args.putString(EXTRA_FILTER, TOP_FETCH_MODE);
        Fragment fragment = Fragment.instantiate(activity, ListFragment.class.getName(), args);
        getSupportFragmentManager().beginTransaction().add(list, fragment).commit();
        Mockito.verify(itemManager).getStories(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT));
        Mockito.reset(itemManager);
        Bundle state = new Bundle();
        fragment.onSaveInstanceState(state);
        fragment.onActivityCreated(state);
        // should not trigger another data request
        Mockito.verify(itemManager, Mockito.never()).getStories(ArgumentMatchers.any(String.class), ArgumentMatchers.eq(MODE_DEFAULT));
        controller.pause().stop().destroy();
    }

    @Test
    public void testEmptyResponse() {
        Mockito.when(itemManager.getStories(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT))).thenReturn(new Item[0]);
        Bundle args = new Bundle();
        args.putString(EXTRA_ITEM_MANAGER, HackerNewsClient.class.getName());
        args.putString(EXTRA_FILTER, TOP_FETCH_MODE);
        getSupportFragmentManager().beginTransaction().add(list, Fragment.instantiate(activity, ListFragment.class.getName(), args)).commit();
        Mockito.verify(itemManager).getStories(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT));
        assertThat(((android.support.v4.widget.SwipeRefreshLayout) (activity.findViewById(swipe_layout)))).isNotRefreshing();
        Assertions.assertThat(((View) (activity.findViewById(empty)))).isNotVisible();
        controller.pause().stop().destroy();
    }

    @Test
    public void testRefreshError() {
        Bundle args = new Bundle();
        args.putString(EXTRA_ITEM_MANAGER, HackerNewsClient.class.getName());
        args.putString(EXTRA_FILTER, TOP_FETCH_MODE);
        getSupportFragmentManager().beginTransaction().add(list, Fragment.instantiate(activity, ListFragment.class.getName(), args)).commit();
        Mockito.verify(itemManager).getStories(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT));
        Assertions.assertThat(((View) (activity.findViewById(empty)))).isNotVisible();
        Mockito.reset(itemManager);
        Mockito.when(itemManager.getStories(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT))).thenReturn(new Item[]{  });
        ShadowSwipeRefreshLayout shadowSwipeRefreshLayout = ((ShadowSwipeRefreshLayout) (Shadow.extract(activity.findViewById(swipe_layout))));
        shadowSwipeRefreshLayout.getOnRefreshListener().onRefresh();
        Mockito.verify(itemManager).getStories(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_NETWORK));
        Assertions.assertThat(((View) (activity.findViewById(empty)))).isNotVisible();
        controller.pause().stop().destroy();
    }

    @Test
    public void testLayoutToggle() {
        Bundle args = new Bundle();
        args.putString(EXTRA_ITEM_MANAGER, HackerNewsClient.class.getName());
        args.putString(EXTRA_FILTER, TOP_FETCH_MODE);
        getSupportFragmentManager().beginTransaction().add(list, Fragment.instantiate(activity, ListFragment.class.getName(), args)).commit();
        assertCompactView();
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_list_item_view), true).apply();
        assertCardView();
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_list_item_view), false).apply();
        assertCompactView();
        controller.pause().stop().destroy();
    }

    @Test
    public void testTogglePreferenceChange() {
        Bundle args = new Bundle();
        args.putString(EXTRA_ITEM_MANAGER, HackerNewsClient.class.getName());
        args.putString(EXTRA_FILTER, TOP_FETCH_MODE);
        getSupportFragmentManager().beginTransaction().add(list, Fragment.instantiate(activity, ListFragment.class.getName(), args), ListFragment.class.getName()).commit();
        assertCompactView();
        controller.pause();
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_list_item_view), true).apply();
        controller.resume().postResume();
        getSupportFragmentManager().findFragmentByTag(ListFragment.class.getName()).onPrepareOptionsMenu(Shadows.shadowOf(activity).getOptionsMenu());
        assertCardView();
        controller.pause().stop().destroy();
    }

    @Test
    public void testListMenu() {
        Bundle args = new Bundle();
        args.putString(EXTRA_ITEM_MANAGER, HackerNewsClient.class.getName());
        args.putString(EXTRA_FILTER, TOP_FETCH_MODE);
        getSupportFragmentManager().beginTransaction().add(list, Fragment.instantiate(activity, ListFragment.class.getName(), args), ListFragment.class.getName()).commit();
        getSupportFragmentManager().findFragmentByTag(ListFragment.class.getName()).onOptionsItemSelected(new org.robolectric.fakes.RoboMenuItem(id.menu_list));
        assertThat(getSupportFragmentManager()).hasFragmentWithTag(PopupSettingsFragment.class.getName());
    }
}

