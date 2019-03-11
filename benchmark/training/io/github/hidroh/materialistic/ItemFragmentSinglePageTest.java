package io.github.hidroh.materialistic;


import Build.VERSION_CODES;
import ComposeActivity.EXTRA_PARENT_ID;
import Intent.ACTION_SEND;
import ItemFragment.EXTRA_ITEM;
import R.color.blackT87;
import R.id;
import R.id.button_more;
import R.id.content_frame;
import R.id.more;
import R.id.posted;
import R.id.recycler_view;
import R.id.text;
import R.string.pref_comment_display;
import R.string.pref_comment_display_value_collapsed;
import R.string.pref_comment_display_value_multiple;
import R.string.pref_comment_display_value_single;
import R.string.vote_failed;
import R.string.voted;
import RuntimeEnvironment.application;
import android.R.attr.textColorSecondary;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.PopupMenu;
import io.github.hidroh.materialistic.accounts.UserServices;
import io.github.hidroh.materialistic.data.Item;
import io.github.hidroh.materialistic.data.ItemManager;
import io.github.hidroh.materialistic.data.ResponseListener;
import io.github.hidroh.materialistic.test.TestItem;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.ShadowRecyclerViewAdapter;
import io.github.hidroh.materialistic.test.shadow.ShadowTextView;
import io.github.hidroh.materialistic.widget.SinglePageItemRecyclerViewAdapter;
import io.github.hidroh.materialistic.widget.ToggleItemViewHolder;
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
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static ActivityModule.HN;
import static org.junit.Assert.assertNotNull;


@SuppressWarnings("ConstantConditions")
@Config(shadows = { ShadowRecyclerViewAdapter.class, ShadowTextView.class })
@RunWith(TestRunner.class)
public class ItemFragmentSinglePageTest {
    @Inject
    @Named(HN)
    ItemManager hackerNewsClient;

    @Inject
    UserServices userServices;

    @Captor
    ArgumentCaptor<ResponseListener<Item>> listener;

    @Captor
    ArgumentCaptor<UserServices.Callback> voteCallback;

    private RecyclerView recyclerView;

    private SinglePageItemRecyclerViewAdapter adapter;

    private ToggleItemViewHolder viewHolder;

    private ToggleItemViewHolder viewHolder1;

    private ToggleItemViewHolder viewHolder2;

    private ItemFragmentMultiPageTest.TestItemActivity activity;

    private ItemFragment fragment;

    private ActivityController<ItemFragmentMultiPageTest.TestItemActivity> controller;

    @Test
    public void testExpand() {
        Assert.assertEquals(5, adapter.getItemCount());// 4 items + footer

    }

    @Test
    public void testGetItemType() {
        Assert.assertEquals(0, adapter.getItemViewType(0));
    }

    @Test
    public void testPendingItem() {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ITEM, new TestItem() {
            @Override
            public Item[] getKidItems() {
                return new Item[]{ new TestItem() {
                    @Override
                    public int getLocalRevision() {
                        return -1;
                    }
                } };
            }

            @Override
            public int getKidCount() {
                return 1;
            }
        });
        Fragment fragment = Fragment.instantiate(application, ItemFragment.class.getName(), args);
        SupportFragmentTestUtil.startVisibleFragment(fragment, ItemFragmentMultiPageTest.TestItemActivity.class, content_frame);
        recyclerView = ((RecyclerView) (fragment.getView().findViewById(recycler_view)));
        adapter = ((SinglePageItemRecyclerViewAdapter) (recyclerView.getAdapter()));
    }

    @Test
    public void testDeleted() {
        Assert.assertNull(Shadows.shadowOf(((View) (viewHolder1.itemView.findViewById(posted)))).getOnClickListener());
    }

    @Test
    public void testDead() {
        assertThat(((android.widget.TextView) (viewHolder.itemView.findViewById(text)))).hasCurrentTextColor(ContextCompat.getColor(activity, blackT87));
        assertThat(((android.widget.TextView) (viewHolder2.itemView.findViewById(text)))).hasCurrentTextColor(ContextCompat.getColor(activity, AppUtils.getThemedResId(activity, textColorSecondary)));
    }

    @Test
    public void testDefaultCollapsed() {
        PreferenceManager.getDefaultSharedPreferences(application).edit().putString(application.getString(pref_comment_display), application.getString(pref_comment_display_value_collapsed)).apply();
        final TestItem item0 = new TestItem() {
            // level 0
            @Override
            public String getId() {
                return "1";
            }

            @Override
            public int getKidCount() {
                return 1;
            }

            @Override
            public Item[] getKidItems() {
                return new Item[]{ new TestItem() {
                    @Override
                    public String getId() {
                        return "2";
                    }
                } };
            }
        };
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ITEM, new TestItem() {
            @Override
            public Item[] getKidItems() {
                return new Item[]{ item0 };
            }

            @Override
            public int getKidCount() {
                return 1;
            }
        });
        Fragment fragment = Fragment.instantiate(application, ItemFragment.class.getName(), args);
        SupportFragmentTestUtil.startVisibleFragment(fragment, ItemFragmentMultiPageTest.TestItemActivity.class, content_frame);
        recyclerView = ((RecyclerView) (fragment.getView().findViewById(recycler_view)));
        adapter = ((SinglePageItemRecyclerViewAdapter) (recyclerView.getAdapter()));
        Assert.assertEquals(2, adapter.getItemCount());// item + footer

        Assert.assertEquals(2, adapter.getItemCount());// should not add kid to adapter

    }

    @Test
    public void testSavedState() {
        Shadows.shadowOf(activity).recreate();
        Assert.assertEquals(5, adapter.getItemCount());
    }

    @Test
    public void testDefaultDisplayAllLines() {
        assertThat(((View) (viewHolder.itemView.findViewById(more)))).isNotVisible();
    }

    @Test
    public void testChangeThreadDisplay() {
        assertSinglePage();
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_comment_display), activity.getString(pref_comment_display_value_single)).apply();// still single

        assertSinglePage();
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_comment_display), activity.getString(pref_comment_display_value_multiple)).apply();// multiple

        assertMultiplePage();
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testVote() {
        viewHolder.itemView.findViewById(button_more).performClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_vote));
        Mockito.verify(userServices).voteUp(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(), voteCallback.capture());
        voteCallback.getValue().onDone(true);
        Assert.assertEquals(activity.getString(voted), ShadowToast.getTextOfLatestToast());
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testVoteItemPromptToLogin() {
        viewHolder.itemView.findViewById(button_more).performClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_vote));
        Mockito.verify(userServices).voteUp(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(), voteCallback.capture());
        voteCallback.getValue().onDone(false);
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, LoginActivity.class);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testVoteItemFailed() {
        viewHolder.itemView.findViewById(button_more).performClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_vote));
        Mockito.verify(userServices).voteUp(ArgumentMatchers.any(Context.class), ArgumentMatchers.any(), voteCallback.capture());
        voteCallback.getValue().onError(new IOException());
        Assert.assertEquals(activity.getString(vote_failed), ShadowToast.getTextOfLatestToast());
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testReply() {
        viewHolder.itemView.findViewById(button_more).performClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_comment));
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, ComposeActivity.class).hasExtra(EXTRA_PARENT_ID, "1");
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testShare() {
        TestApplication.addResolver(new Intent(Intent.ACTION_SEND));
        viewHolder.itemView.findViewById(button_more).performClick();
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        assertNotNull(popupMenu);
        Shadows.shadowOf(popupMenu).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_contextual_share));
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasAction(ACTION_SEND);
    }
}

