package io.github.hidroh.materialistic;


import Build.VERSION_CODES;
import ComposeActivity.EXTRA_PARENT_ID;
import Intent.ACTION_SEND;
import ItemActivity.EXTRA_OPEN_COMMENTS;
import ItemManager.MODE_DEFAULT;
import ItemTouchHelper.ACTION_STATE_SWIPE;
import ItemTouchHelper.LEFT;
import ItemTouchHelper.RIGHT;
import ItemTouchHelper.SimpleCallback;
import Preferences.SwipeAction.None;
import Preferences.SwipeAction.Refresh;
import R.id;
import R.id.bookmarked;
import R.id.button_more;
import R.id.comment;
import R.id.menu_contextual_save;
import R.id.menu_contextual_vote;
import R.id.rank;
import R.id.snackbar_action;
import R.id.snackbar_text;
import R.id.source;
import R.id.title;
import R.string.pref_auto_viewed;
import R.string.pref_list_swipe_left;
import R.string.pref_list_swipe_right;
import R.string.refresh;
import R.string.save;
import R.string.toast_saved;
import R.string.vote_failed;
import R.string.vote_up;
import RecyclerView.ViewHolder;
import RuntimeEnvironment.application;
import UserActivity.EXTRA_USERNAME;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.PopupMenu;
import io.github.hidroh.materialistic.accounts.UserServices;
import io.github.hidroh.materialistic.data.FavoriteManager;
import io.github.hidroh.materialistic.data.Item;
import io.github.hidroh.materialistic.data.ItemManager;
import io.github.hidroh.materialistic.data.MaterialisticDatabase;
import io.github.hidroh.materialistic.data.ResponseListener;
import io.github.hidroh.materialistic.data.SessionManager;
import io.github.hidroh.materialistic.data.TestHnItem;
import io.github.hidroh.materialistic.data.WebItem;
import io.github.hidroh.materialistic.test.ListActivity;
import io.github.hidroh.materialistic.test.TestLayoutManager;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.CustomShadows;
import io.github.hidroh.materialistic.test.shadow.ShadowAnimation;
import io.github.hidroh.materialistic.test.shadow.ShadowItemTouchHelper;
import io.github.hidroh.materialistic.test.shadow.ShadowRecyclerView;
import io.github.hidroh.materialistic.test.shadow.ShadowRecyclerViewAdapter;
import io.github.hidroh.materialistic.test.shadow.ShadowSnackbar;
import io.github.hidroh.materialistic.test.shadow.ShadowSwipeRefreshLayout;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPopupMenu;
import org.robolectric.shadows.ShadowToast;

import static ActivityModule.HN;
import static org.junit.Assert.assertNotNull;


@SuppressWarnings("ConstantConditions")
@Config(shadows = { ShadowSwipeRefreshLayout.class, ShadowRecyclerViewAdapter.class, ShadowRecyclerView.class, ShadowItemTouchHelper.class, ShadowAnimation.class, ShadowSnackbar.class })
@RunWith(TestRunner.class)
public class ListFragmentViewHolderTest {
    private ActivityController<ListActivity> controller;

    private ShadowRecyclerViewAdapter adapter;

    private ListActivity activity;

    private TestHnItem item;

    @Inject
    SessionManager sessionManager;

    @Inject
    @Named(HN)
    ItemManager itemManager;

    @Inject
    FavoriteManager favoriteManager;

    @Inject
    UserServices userServices;

    @Captor
    ArgumentCaptor<ResponseListener<Item>> itemListener;

    @Captor
    ArgumentCaptor<UserServices.Callback> voteCallback;

    private RecyclerView recyclerView;

    private SimpleCallback swipeCallback;

    @Test
    public void testStory() {
        setIsViewed(true);
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        RecyclerView.ViewHolder holder = adapter.getViewHolder(0);
        assertThat(((View) (holder.itemView.findViewById(bookmarked)))).isNotVisible();
        assertThat(((android.widget.TextView) (holder.itemView.findViewById(rank)))).hasTextString("46");
        assertThat(((android.widget.TextView) (holder.itemView.findViewById(title)))).hasTextString("title");
        assertThat(((android.widget.TextView) (holder.itemView.findViewById(comment)))).isVisible().isEmpty();
        assertViewed();
    }

    @Test
    public void testComment() {
        item.populate(new ListFragmentViewHolderTest.PopulatedStory(1) {
            @Override
            public int getDescendants() {
                return 1;
            }

            @Override
            public long[] getKids() {
                return new long[]{ 2 };
            }
        });
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        RecyclerView.ViewHolder holder = adapter.getViewHolder(0);
        View commentButton = holder.itemView.findViewById(comment);
        assertThat(commentButton).isVisible();
        Mockito.reset(activity.multiPaneListener);
        commentButton.performClick();
        Mockito.verify(activity.multiPaneListener, Mockito.never()).onItemSelected(ArgumentMatchers.any(WebItem.class));
        Intent actual = Shadows.shadowOf(activity).getNextStartedActivity();
        Assert.assertEquals(ItemActivity.class.getName(), actual.getComponent().getClassName());
        assertThat(actual).hasExtra(EXTRA_OPEN_COMMENTS, true);
    }

    @Test
    public void testJob() {
        item.populate(new ListFragmentViewHolderTest.PopulatedStory(1) {
            @Override
            public String getRawType() {
                return JOB_TYPE;
            }
        });
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        RecyclerView.ViewHolder holder = adapter.getViewHolder(0);
        assertThat(((android.widget.TextView) (holder.itemView.findViewById(source)))).isEmpty();
    }

    @Test
    public void testPoll() {
        populate(new ListFragmentViewHolderTest.PopulatedStory(1) {
            @Override
            public String getRawType() {
                return POLL_TYPE;
            }
        });
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        RecyclerView.ViewHolder holder = adapter.getViewHolder(0);
        assertThat(((android.widget.TextView) (holder.itemView.findViewById(source)))).isEmpty();
    }

    @Test
    public void testItemClick() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        adapter.getViewHolder(0).itemView.performClick();
        Mockito.verify(activity.multiPaneListener).onItemSelected(ArgumentMatchers.any(WebItem.class));
    }

    @Test
    public void testViewedObserver() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        assertNotViewed();
        controller.pause();
        MaterialisticDatabase.getInstance(application).setLiveValue(MaterialisticDatabase.getBaseReadUri().buildUpon().appendPath("2").build());// not in view

        MaterialisticDatabase.getInstance(application).setLiveValue(MaterialisticDatabase.getBaseReadUri().buildUpon().appendPath("1").build());// in view

        controller.resume();
        assertViewed();
    }

    @Test
    public void testFavoriteObserver() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        setFavorite(true);
        itemListener.getValue().onResponse(item);
        Assert.assertTrue(isFavorite());
        controller.pause();
        // observed clear
        MaterialisticDatabase.getInstance(application).setLiveValue(MaterialisticDatabase.getBaseSavedUri().buildUpon().appendPath("clear").build());
        RecyclerView.ViewHolder viewHolder = adapter.getViewHolder(0);
        Assert.assertFalse(isFavorite());
        assertThat(((View) (viewHolder.itemView.findViewById(bookmarked)))).isNotVisible();
        // observed add
        MaterialisticDatabase.getInstance(application).setLiveValue(MaterialisticDatabase.getBaseSavedUri().buildUpon().appendPath("add").appendPath("1").build());
        Assert.assertTrue(isFavorite());
        // observed remove
        MaterialisticDatabase.getInstance(application).setLiveValue(MaterialisticDatabase.getBaseSavedUri().buildUpon().appendPath("remove").appendPath("1").build());
        Assert.assertFalse(isFavorite());
        controller.resume();
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testSaveItem() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        adapter.getViewHolder(0).itemView.performLongClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        assertThat(popupMenu.getMenu().findItem(menu_contextual_save).isVisible()).isFalse();
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_save));
        Mockito.verify(favoriteManager).add(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq(item));
        MaterialisticDatabase.getInstance(application).setLiveValue(MaterialisticDatabase.getBaseSavedUri().buildUpon().appendPath("add").appendPath("1").build());
        Assert.assertTrue(isFavorite());
        View snackbarView = ShadowSnackbar.getLatestView();
        assertThat(((android.widget.TextView) (snackbarView.findViewById(snackbar_text)))).isNotNull().containsText(toast_saved);
        snackbarView.findViewById(snackbar_action).performClick();
        Mockito.verify(favoriteManager).remove(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq("1"));
        MaterialisticDatabase.getInstance(application).setLiveValue(MaterialisticDatabase.getBaseSavedUri().buildUpon().appendPath("remove").appendPath("1").build());
        Assert.assertFalse(isFavorite());
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testSwipeToSaveItem() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        RecyclerView.ViewHolder holder = adapter.getViewHolder(0);
        assertThat(swipeCallback.onMove(recyclerView, holder, holder)).isFalse();
        assertThat(swipeCallback.getSwipeThreshold(holder)).isGreaterThan(0.0F);
        assertThat(swipeCallback.getSwipeDirs(recyclerView, holder)).isEqualTo(((ItemTouchHelper.LEFT) | (ItemTouchHelper.RIGHT)));
        Canvas canvas = Mockito.mock(Canvas.class);
        swipeCallback.onChildDraw(canvas, recyclerView, holder, (-1.0F), 0.0F, ACTION_STATE_SWIPE, true);
        Mockito.verify(canvas).drawText(ArgumentMatchers.eq(activity.getString(save).toUpperCase()), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.any(Paint.class));
        swipeCallback.onSwiped(holder, LEFT);
        Mockito.verify(favoriteManager).add(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq(item));
        setFavorite(true);
        assertThat(swipeCallback.getSwipeDirs(recyclerView, holder)).isEqualTo(((ItemTouchHelper.LEFT) | (ItemTouchHelper.RIGHT)));
    }

    @Test
    public void testDisableSwipe() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_list_swipe_left), None.name()).putString(activity.getString(pref_list_swipe_right), None.name()).apply();
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        RecyclerView.ViewHolder holder = adapter.getViewHolder(0);
        assertThat(swipeCallback.getSwipeDirs(recyclerView, holder)).isEqualTo(0);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testViewUser() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        adapter.getViewHolder(0).itemView.performLongClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_profile));
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, UserActivity.class).hasExtra(EXTRA_USERNAME, "author");
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testVoteItem() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        adapter.getViewHolder(0).itemView.performLongClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        assertThat(popupMenu.getMenu().findItem(menu_contextual_vote).isVisible()).isFalse();
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_vote));
        Mockito.verify(userServices).voteUp(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq(getId()), voteCallback.capture());
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testSwipeToVoteItem() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        RecyclerView.ViewHolder holder = adapter.getViewHolder(0);
        assertThat(swipeCallback.getSwipeDirs(recyclerView, holder)).isEqualTo(((ItemTouchHelper.LEFT) | (ItemTouchHelper.RIGHT)));
        Canvas canvas = Mockito.mock(Canvas.class);
        swipeCallback.onChildDraw(canvas, recyclerView, holder, 1.0F, 0.0F, ACTION_STATE_SWIPE, true);
        Mockito.verify(canvas).drawText(ArgumentMatchers.eq(activity.getString(vote_up).toUpperCase()), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.any(Paint.class));
        swipeCallback.onSwiped(holder, RIGHT);
        Mockito.verify(userServices).voteUp(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq(getId()), voteCallback.capture());
        incrementScore();
        assertThat(swipeCallback.getSwipeDirs(recyclerView, holder)).isEqualTo(LEFT);
        clearPendingVoted();
        assertThat(swipeCallback.getSwipeDirs(recyclerView, holder)).isEqualTo(LEFT);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testVoteItemPromptToLogin() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        adapter.getViewHolder(0).itemView.findViewById(button_more).performClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_vote));
        Mockito.verify(userServices).voteUp(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq(getId()), voteCallback.capture());
        voteCallback.getValue().onDone(false);
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, LoginActivity.class);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testVoteItemFailed() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        adapter.getViewHolder(0).itemView.performLongClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_vote));
        Mockito.verify(userServices).voteUp(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq(getId()), voteCallback.capture());
        voteCallback.getValue().onError(new IOException());
        Assert.assertEquals(activity.getString(vote_failed), ShadowToast.getTextOfLatestToast());
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testReply() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        adapter.getViewHolder(0).itemView.performLongClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_comment));
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, ComposeActivity.class).hasExtra(EXTRA_PARENT_ID, "1");
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testRefresh() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        Mockito.reset(itemManager);
        adapter.getViewHolder(0).itemView.performLongClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_refresh));
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), ArgumentMatchers.any());
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testSwipeToRefresh() {
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        Mockito.reset(itemManager);
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_list_swipe_left), None.name()).putString(activity.getString(pref_list_swipe_right), Refresh.name()).apply();
        RecyclerView.ViewHolder holder = adapter.getViewHolder(0);
        assertThat(swipeCallback.getSwipeDirs(recyclerView, holder)).isEqualTo(RIGHT);
        Canvas canvas = Mockito.mock(Canvas.class);
        swipeCallback.onChildDraw(canvas, recyclerView, holder, 1.0F, 0.0F, ACTION_STATE_SWIPE, true);
        Mockito.verify(canvas).drawText(ArgumentMatchers.eq(activity.getString(refresh).toUpperCase()), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.any(Paint.class));
        swipeCallback.onSwiped(holder, RIGHT);
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), ArgumentMatchers.any());
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testShare() {
        TestApplication.addResolver(new Intent(Intent.ACTION_SEND));
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        adapter.getViewHolder(0).itemView.performLongClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_share));
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasAction(ACTION_SEND);
    }

    @Test
    public void testAutoMarkAsViewed() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_auto_viewed), true).apply();
        ShadowRecyclerView shadowRecyclerView = CustomShadows.customShadowOf(recyclerView);
        TestLayoutManager testLayout = new TestLayoutManager(activity);
        recyclerView.setLayoutManager(testLayout);
        testLayout.firstVisiblePosition = 0;
        shadowRecyclerView.getScrollListener().onScrolled(recyclerView, 0, 1);
        Mockito.verify(sessionManager, Mockito.never()).view(ArgumentMatchers.any());
        Mockito.verify(itemManager).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), itemListener.capture());
        itemListener.getValue().onResponse(item);
        testLayout.firstVisiblePosition = 0;
        shadowRecyclerView.getScrollListener().onScrolled(recyclerView, 0, 1);
        Mockito.verify(sessionManager, Mockito.never()).view(ArgumentMatchers.any());
        testLayout.firstVisiblePosition = 1;
        shadowRecyclerView.getScrollListener().onScrolled(recyclerView, 0, 1);
        Mockito.verify(sessionManager).view(ArgumentMatchers.any());
        setIsViewed(true);
        testLayout.firstVisiblePosition = 1;
        shadowRecyclerView.getScrollListener().onScrolled(recyclerView, 0, 1);
        Mockito.verify(sessionManager).view(ArgumentMatchers.any());// should not trigger again

        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_auto_viewed), false).apply();
        Assert.assertNull(shadowRecyclerView.getScrollListener());
    }

    @SuppressLint("ParcelCreator")
    private static class PopulatedStory extends TestHnItem {
        public PopulatedStory(long id) {
            super(id);
        }

        @Override
        public String getTitle() {
            return "title";
        }

        @Override
        public String getRawType() {
            return STORY_TYPE;
        }

        @Override
        public long[] getKids() {
            return new long[0];
        }

        @Override
        public int getDescendants() {
            return 0;
        }
    }
}

