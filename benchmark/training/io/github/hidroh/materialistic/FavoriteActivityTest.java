package io.github.hidroh.materialistic;


import Build.VERSION_CODES;
import DialogInterface.BUTTON_NEGATIVE;
import DialogInterface.BUTTON_POSITIVE;
import FavoriteActivity.EMPTY_QUERY;
import Intent.ACTION_SEND;
import ItemTouchHelper.LEFT;
import ItemTouchHelper.RIGHT;
import KeyEvent.KEYCODE_VOLUME_UP;
import LocalItemManager.Observer;
import R.id;
import R.id.button_more;
import R.id.menu_clear;
import R.id.menu_export;
import R.id.menu_search;
import R.id.snackbar_action;
import R.id.snackbar_text;
import R.string.toast_removed;
import R.string.vote_failed;
import R.string.voted;
import RecyclerView.Adapter;
import RecyclerView.ViewHolder;
import RuntimeEnvironment.application;
import SearchManager.QUERY;
import SearchView.OnCloseListener;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import io.github.hidroh.materialistic.accounts.UserServices;
import io.github.hidroh.materialistic.data.FavoriteManager;
import io.github.hidroh.materialistic.data.LocalItemManager;
import io.github.hidroh.materialistic.data.MaterialisticDatabase;
import io.github.hidroh.materialistic.data.SyncScheduler;
import io.github.hidroh.materialistic.data.TestFavorite;
import io.github.hidroh.materialistic.data.TestHnItem;
import io.github.hidroh.materialistic.data.WebItem;
import io.github.hidroh.materialistic.test.TestFavoriteActivity;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.CustomShadows;
import io.github.hidroh.materialistic.test.shadow.ShadowItemTouchHelper;
import io.github.hidroh.materialistic.test.shadow.ShadowRecyclerView;
import io.github.hidroh.materialistic.test.shadow.ShadowRecyclerViewAdapter;
import java.io.IOException;
import java.util.Set;
import javax.inject.Inject;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowPopupMenu;
import org.robolectric.shadows.ShadowToast;

import static org.junit.Assert.assertNotNull;


@Config(shadows = { ShadowRecyclerViewAdapter.class })
@RunWith(TestRunner.class)
public class FavoriteActivityTest {
    private ActivityController<TestFavoriteActivity> controller;

    private TestFavoriteActivity activity;

    private Adapter adapter;

    private RecyclerView recyclerView;

    private Fragment fragment;

    @Inject
    FavoriteManager favoriteManager;

    @Inject
    ActionViewResolver actionViewResolver;

    @Inject
    UserServices userServices;

    @Inject
    KeyDelegate keyDelegate;

    @Inject
    SyncScheduler syncScheduler;

    @Captor
    ArgumentCaptor<Set<String>> selection;

    @Captor
    ArgumentCaptor<View.OnClickListener> searchViewClickListener;

    @Captor
    ArgumentCaptor<SearchView.OnCloseListener> searchViewCloseListener;

    @Captor
    ArgumentCaptor<UserServices.Callback> userServicesCallback;

    @Captor
    ArgumentCaptor<LocalItemManager.Observer> observerCaptor;

    private ShadowRecyclerViewAdapter shadowAdapter;

    @Test
    public void testNewIntent() {
        Mockito.when(favoriteManager.getSize()).thenReturn(1);
        controller.newIntent(new Intent().putExtra(QUERY, "title"));
        Assert.assertEquals(1, adapter.getItemCount());
        controller.newIntent(new Intent());// should not clear filter

        Assert.assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void testOptionsMenuClear() {
        Assert.assertTrue(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_clear).isVisible());
        Shadows.shadowOf(activity).clickMenuItem(menu_clear);
        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        dialog.getButton(BUTTON_NEGATIVE).performClick();
        Assert.assertEquals(2, adapter.getItemCount());
        Shadows.shadowOf(activity).clickMenuItem(menu_clear);
        dialog = ShadowAlertDialog.getLatestAlertDialog();
        dialog.getButton(BUTTON_POSITIVE).performClick();
        Mockito.verify(favoriteManager).clear(ArgumentMatchers.any(Context.class), ArgumentMatchers.any());
        Mockito.when(favoriteManager.getSize()).thenReturn(0);
        observerCaptor.getValue().onChanged();
        Assert.assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testSearchView() {
        SearchView searchView = ((SearchView) (actionViewResolver.getActionView(Mockito.mock(MenuItem.class))));
        Mockito.verify(searchView, Mockito.atLeastOnce()).setOnSearchClickListener(searchViewClickListener.capture());
        Mockito.verify(searchView, Mockito.atLeastOnce()).setOnCloseListener(searchViewCloseListener.capture());
        searchViewClickListener.getAllValues().get(((searchViewClickListener.getAllValues().size()) - 1)).onClick(searchView);
        Assert.assertFalse(startActionMode(null));
        SearchView.OnCloseListener closeListener = searchViewCloseListener.getAllValues().get(((searchViewCloseListener.getAllValues().size()) - 1));
        closeListener.onClose();
        Assert.assertEquals(2, adapter.getItemCount());
        filter("ask");
        Mockito.verify(favoriteManager, Mockito.times(2)).attach(observerCaptor.capture(), ArgumentMatchers.any());
        Mockito.when(favoriteManager.getSize()).thenReturn(1);
        Mockito.when(favoriteManager.getItem(ArgumentMatchers.eq(0))).thenReturn(new TestFavorite("2", "http://example.com", "ask HN", System.currentTimeMillis()));
        observerCaptor.getValue().onChanged();
        Assert.assertEquals(1, adapter.getItemCount());
        Mockito.reset(searchView);
        closeListener.onClose();
        Mockito.verify(searchView).setQuery(ArgumentMatchers.eq(EMPTY_QUERY), ArgumentMatchers.eq(true));
    }

    @Test
    public void testItemClick() {
        Assert.assertEquals(2, adapter.getItemCount());
        RecyclerView.ViewHolder holder = shadowAdapter.getViewHolder(0);
        holder.itemView.performClick();
        Assert.assertNotNull(Shadows.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void testActionMode() {
        RecyclerView.ViewHolder holder = shadowAdapter.getViewHolder(0);
        holder.itemView.performLongClick();
        Assert.assertNotNull(activity.actionModeCallback);
    }

    @Test
    public void testSelectionToggle() {
        RecyclerView.ViewHolder holder = shadowAdapter.getViewHolder(0);
        holder.itemView.performLongClick();
        holder.itemView.performClick();
        Assert.assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
        holder.itemView.performClick();
        Assert.assertNull(Shadows.shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void testDelete() {
        RecyclerView.ViewHolder holder = shadowAdapter.getViewHolder(0);
        holder.itemView.performLongClick();
        ActionMode actionMode = Mockito.mock(ActionMode.class);
        activity.actionModeCallback.onActionItemClicked(actionMode, new org.robolectric.fakes.RoboMenuItem(id.menu_clear));
        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        dialog.getButton(BUTTON_NEGATIVE).performClick();
        Assert.assertEquals(2, adapter.getItemCount());
        activity.actionModeCallback.onActionItemClicked(actionMode, new org.robolectric.fakes.RoboMenuItem(id.menu_clear));
        dialog = ShadowAlertDialog.getLatestAlertDialog();
        dialog.getButton(BUTTON_POSITIVE).performClick();
        Mockito.verify(favoriteManager).remove(ArgumentMatchers.any(Context.class), selection.capture());
        assertThat(selection.getValue()).contains("1");
        Mockito.verify(actionMode).finish();
        Mockito.when(favoriteManager.getSize()).thenReturn(1);
        observerCaptor.getValue().onChanged();
        Assert.assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void testRefresh() {
        RecyclerView.ViewHolder holder = shadowAdapter.getViewHolder(0);
        holder.itemView.performLongClick();
        ActionMode actionMode = Mockito.mock(ActionMode.class);
        activity.actionModeCallback.onActionItemClicked(actionMode, new org.robolectric.fakes.RoboMenuItem(id.menu_refresh));
        Mockito.verify(syncScheduler).scheduleSync(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(actionMode).finish();
    }

    @Config(shadows = { ShadowRecyclerView.class, ShadowItemTouchHelper.class })
    @Test
    public void testSwipeToDelete() {
        RecyclerView.ViewHolder holder = shadowAdapter.getViewHolder(0);
        CustomShadows.customShadowOf(recyclerView).getItemTouchHelperCallback().onSwiped(holder, LEFT);
        Mockito.verify(favoriteManager).remove(ArgumentMatchers.any(Context.class), ArgumentMatchers.anyCollection());
        Mockito.when(favoriteManager.getSize()).thenReturn(1);
        observerCaptor.getValue().onChanged();
        Assert.assertEquals(1, adapter.getItemCount());
        assertThat(((android.widget.TextView) (activity.findViewById(snackbar_text)))).isNotNull().containsText(toast_removed);
        activity.findViewById(snackbar_action).performClick();
        Mockito.verify(favoriteManager).add(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(WebItem.class));
        Mockito.when(favoriteManager.getSize()).thenReturn(2);
        observerCaptor.getValue().onChanged();
        Assert.assertEquals(2, adapter.getItemCount());
    }

    @Config(shadows = { ShadowRecyclerView.class, ShadowItemTouchHelper.class })
    @Test
    public void testSwipeToRefresh() {
        RecyclerView.ViewHolder holder = shadowAdapter.getViewHolder(0);
        CustomShadows.customShadowOf(recyclerView).getItemTouchHelperCallback().onSwiped(holder, RIGHT);
        Mockito.verify(syncScheduler).scheduleSync(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void testExport() {
        Shadows.shadowOf(activity).clickMenuItem(menu_export);
        Mockito.verify(favoriteManager).export(ArgumentMatchers.any(Context.class), ArgumentMatchers.any());
    }

    @Test
    public void testFilter() {
        Assert.assertTrue(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_search).isVisible());
        Intent intent = new Intent();
        intent.putExtra(QUERY, "blah");
        controller.newIntent(intent);
        Mockito.verify(favoriteManager).attach(ArgumentMatchers.any(Observer.class), ArgumentMatchers.eq("blah"));
        intent = new Intent();
        intent.putExtra(QUERY, "ask");
        controller.newIntent(intent);
        Mockito.verify(favoriteManager).attach(ArgumentMatchers.any(Observer.class), ArgumentMatchers.eq("ask"));
    }

    @Test
    public void testSaveState() {
        Bundle outState = new Bundle();
        controller.saveInstanceState(outState);
        ActivityController<TestFavoriteActivity> controller = Robolectric.buildActivity(TestFavoriteActivity.class).create(outState).postCreate(outState).start().resume().visible();
        Assert.assertEquals(2, getAdapter().getItemCount());
        controller.pause().stop().destroy();
        Mockito.reset(keyDelegate);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testVoteItem() {
        shadowAdapter.getViewHolder(0).itemView.findViewById(button_more).performClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_vote));
        Mockito.verify(userServices).voteUp(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(), userServicesCallback.capture());
        userServicesCallback.getValue().onDone(true);
        Assert.assertEquals(activity.getString(voted), ShadowToast.getTextOfLatestToast());
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testVoteItemPromptToLogin() {
        shadowAdapter.getViewHolder(0).itemView.findViewById(button_more).performClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_vote));
        Mockito.verify(userServices).voteUp(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(), userServicesCallback.capture());
        userServicesCallback.getValue().onDone(false);
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, LoginActivity.class);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testVoteItemFailed() {
        shadowAdapter.getViewHolder(0).itemView.findViewById(button_more).performClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_vote));
        Mockito.verify(userServices).voteUp(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(), userServicesCallback.capture());
        userServicesCallback.getValue().onError(new IOException());
        Assert.assertEquals(activity.getString(vote_failed), ShadowToast.getTextOfLatestToast());
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testReply() {
        shadowAdapter.getViewHolder(0).itemView.findViewById(button_more).performClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_comment));
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, ComposeActivity.class);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testShare() {
        TestApplication.addResolver(new Intent(Intent.ACTION_SEND));
        shadowAdapter.getViewHolder(0).itemView.findViewById(button_more).performClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_share));
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasAction(ACTION_SEND);
    }

    @Test
    public void testRemoveClearSelection() {
        MaterialisticDatabase.getInstance(application).setLiveValue(MaterialisticDatabase.getBaseSavedUri().buildUpon().appendPath("remove").appendPath("3").build());
        Assert.assertNull(getSelectedItem());
        onItemSelected(new TestHnItem(1L));
        MaterialisticDatabase.getInstance(application).setLiveValue(MaterialisticDatabase.getBaseSavedUri().buildUpon().appendPath("remove").appendPath("2").build());
        Assert.assertNotNull(getSelectedItem());
        MaterialisticDatabase.getInstance(application).setLiveValue(MaterialisticDatabase.getBaseSavedUri().buildUpon().appendPath("remove").appendPath("1").build());
        Assert.assertNull(getSelectedItem());
    }

    @Test
    public void testClearSelection() {
        onItemSelected(new TestHnItem(1L));
        MaterialisticDatabase.getInstance(application).setLiveValue(MaterialisticDatabase.getBaseSavedUri().buildUpon().appendPath("clear").build());
        Assert.assertNull(getSelectedItem());
    }

    @Test
    public void testVolumeNavigation() {
        activity.onKeyDown(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
        Mockito.verify(keyDelegate).setScrollable(ArgumentMatchers.any(Scrollable.class), ArgumentMatchers.any(AppBarLayout.class));
        Mockito.verify(keyDelegate).onKeyDown(ArgumentMatchers.anyInt(), ArgumentMatchers.any(KeyEvent.class));
        activity.onKeyUp(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_UP));
        Mockito.verify(keyDelegate).onKeyUp(ArgumentMatchers.anyInt(), ArgumentMatchers.any(KeyEvent.class));
        activity.onKeyLongPress(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
        Mockito.verify(keyDelegate).onKeyLongPress(ArgumentMatchers.anyInt(), ArgumentMatchers.any(KeyEvent.class));
    }
}

