package io.github.hidroh.materialistic;


import HackerNewsClient.WEB_ITEM_PATH;
import Intent.ACTION_SEND;
import Intent.ACTION_VIEW;
import ItemActivity.EXTRA_ITEM;
import ItemManager.MODE_DEFAULT;
import KeyEvent.KEYCODE_BACK;
import KeyEvent.KEYCODE_VOLUME_UP;
import MotionEvent.ACTION_MOVE;
import MotionEvent.ACTION_UP;
import R.drawable.ic_poll_white_18dp;
import R.drawable.ic_work_white_18dp;
import R.id;
import R.id.bookmarked;
import R.id.button_article;
import R.id.menu_external;
import R.id.menu_share;
import R.id.navigation_button;
import R.id.recycler_view;
import R.id.reply_button;
import R.id.snackbar_action;
import R.id.snackbar_text;
import R.id.source;
import R.id.tab_layout;
import R.id.vote_button;
import R.plurals.comments_count;
import R.string.hint_drag;
import R.string.hint_nav_short;
import R.string.pref_custom_tab;
import R.string.pref_external;
import R.string.pref_navigation;
import R.string.pref_story_display;
import R.string.pref_story_display_value_comments;
import R.string.pref_story_display_value_readability;
import R.string.toast_removed;
import R.string.vote_failed;
import R.string.voted;
import RuntimeEnvironment.application;
import WebFragment.EXTRA_FULLSCREEN;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import io.github.hidroh.materialistic.accounts.UserServices;
import io.github.hidroh.materialistic.data.FavoriteManager;
import io.github.hidroh.materialistic.data.Item;
import io.github.hidroh.materialistic.data.ItemManager;
import io.github.hidroh.materialistic.data.MaterialisticDatabase;
import io.github.hidroh.materialistic.data.ResponseListener;
import io.github.hidroh.materialistic.data.TestHnItem;
import io.github.hidroh.materialistic.data.WebItem;
import io.github.hidroh.materialistic.test.TestItem;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.TestWebItem;
import io.github.hidroh.materialistic.test.shadow.CustomShadows;
import io.github.hidroh.materialistic.test.shadow.ShadowFloatingActionButton;
import io.github.hidroh.materialistic.test.shadow.ShadowRecyclerView;
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
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.shadows.ShadowPackageManager;
import org.robolectric.shadows.ShadowPopupMenu;
import org.robolectric.shadows.ShadowResolveInfo;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.support.v4.ShadowLocalBroadcastManager;

import static ActivityModule.HN;
import static BuildConfig.APPLICATION_ID;
import static WebFragment.ACTION_FULLSCREEN;


@SuppressWarnings("ConstantConditions")
@RunWith(TestRunner.class)
public class ItemActivityTest {
    private ActivityController<ItemActivity> controller;

    private ItemActivity activity;

    @Inject
    @Named(HN)
    ItemManager hackerNewsClient;

    @Inject
    FavoriteManager favoriteManager;

    @Inject
    UserServices userServices;

    @Inject
    KeyDelegate keyDelegate;

    @Captor
    ArgumentCaptor<ResponseListener<Item>> listener;

    @Captor
    ArgumentCaptor<UserServices.Callback> userServicesCallback;

    @Test
    public void testCustomScheme() {
        Intent intent = new Intent();
        intent.setAction(ACTION_VIEW);
        intent.setData(Uri.parse(((APPLICATION_ID) + "://item/1")));
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        Mockito.verify(hackerNewsClient).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(MODE_DEFAULT), ArgumentMatchers.any(ResponseListener.class));
    }

    @Test
    public void testJobGivenDeepLink() {
        Intent intent = new Intent();
        intent.setAction(ACTION_VIEW);
        intent.setData(Uri.parse("https://news.ycombinator.com/item?id=1"));
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        Mockito.verify(hackerNewsClient).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(MODE_DEFAULT), listener.capture());
        listener.getValue().onResponse(new TestItem() {
            @NonNull
            @Override
            public String getType() {
                return JOB_TYPE;
            }

            @Override
            public String getId() {
                return "1";
            }

            @Override
            public String getUrl() {
                return String.format(WEB_ITEM_PATH, "1");
            }

            @Override
            public String getSource() {
                return "http://example.com";
            }

            @Override
            public boolean isStoryType() {
                return true;
            }
        });
        Assert.assertEquals(ic_work_white_18dp, Shadows.shadowOf(getCompoundDrawables()[0]).getCreatedFromResId());
        assertThat(((android.widget.TextView) (activity.findViewById(source)))).hasText("http://example.com");
        Mockito.reset(hackerNewsClient);
        Shadows.shadowOf(activity).recreate();
        Mockito.verify(hackerNewsClient, Mockito.never()).getItem(ArgumentMatchers.any(), ArgumentMatchers.eq(MODE_DEFAULT), ArgumentMatchers.any(ResponseListener.class));
    }

    @Test
    public void testPoll() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM, new TestItem() {
            @NonNull
            @Override
            public String getType() {
                return POLL_TYPE;
            }

            @Override
            public boolean isStoryType() {
                return true;
            }

            @Override
            public String getUrl() {
                return "http://example.com";
            }
        });
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        assertThat(((View) (activity.findViewById(source)))).isNotVisible();
        Assert.assertEquals(ic_poll_white_18dp, Shadows.shadowOf(getCompoundDrawables()[0]).getCreatedFromResId());
    }

    @SuppressLint("NewApi")
    @Test
    public void testOptionExternal() {
        ShadowPackageManager packageManager = Shadows.shadowOf(application.getPackageManager());
        packageManager.addResolveInfoForIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("http://example.com")), ShadowResolveInfo.newResolveInfo("label", "com.android.chrome", "DefaultActivity"));
        packageManager.addResolveInfoForIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(WEB_ITEM_PATH, "1"))), ShadowResolveInfo.newResolveInfo("label", "com.android.chrome", "DefaultActivity"));
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM, new TestItem() {
            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }

            @Override
            public String getUrl() {
                return "http://example.com";
            }

            @Override
            public boolean isStoryType() {
                return true;
            }

            @Override
            public String getId() {
                return "1";
            }
        });
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        // inflate menu, see https://github.com/robolectric/robolectric/issues/1326
        ShadowLooper.pauseMainLooper();
        controller.visible();
        ShadowApplication.getInstance().getForegroundThreadScheduler().advanceToLastPostedRunnable();
        // open article
        Shadows.shadowOf(activity).clickMenuItem(menu_external);
        Shadows.shadowOf(ShadowPopupMenu.getLatestPopupMenu()).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_article));
        ShadowApplication.getInstance().getForegroundThreadScheduler().advanceToLastPostedRunnable();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasAction(ACTION_VIEW);
        // open item
        Shadows.shadowOf(activity).clickMenuItem(menu_external);
        Shadows.shadowOf(ShadowPopupMenu.getLatestPopupMenu()).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_comments));
        ShadowApplication.getInstance().getForegroundThreadScheduler().advanceToLastPostedRunnable();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasAction(ACTION_VIEW);
    }

    @SuppressLint("NewApi")
    @Test
    public void testShare() {
        TestApplication.addResolver(new Intent(Intent.ACTION_SEND));
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM, new TestItem() {
            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }

            @Override
            public String getUrl() {
                return "http://example.com";
            }

            @Override
            public boolean isStoryType() {
                return true;
            }

            @Override
            public String getId() {
                return "1";
            }
        });
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        // inflate menu, see https://github.com/robolectric/robolectric/issues/1326
        ShadowLooper.pauseMainLooper();
        controller.visible();
        ShadowApplication.getInstance().getForegroundThreadScheduler().advanceToLastPostedRunnable();
        // share article
        Shadows.shadowOf(activity).clickMenuItem(menu_share);
        Shadows.shadowOf(ShadowPopupMenu.getLatestPopupMenu()).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_article));
        ShadowApplication.getInstance().getForegroundThreadScheduler().advanceToLastPostedRunnable();
        Intent actual = Shadows.shadowOf(activity).getNextStartedActivity();
        assertThat(actual).hasAction(ACTION_SEND);
        // share item
        Shadows.shadowOf(activity).clickMenuItem(menu_share);
        Shadows.shadowOf(ShadowPopupMenu.getLatestPopupMenu()).getOnMenuItemClickListener().onMenuItemClick(new org.robolectric.fakes.RoboMenuItem(id.menu_comments));
        ShadowApplication.getInstance().getForegroundThreadScheduler().advanceToLastPostedRunnable();
        actual = Shadows.shadowOf(activity).getNextStartedActivity();
        assertThat(actual).hasAction(ACTION_SEND);
    }

    @Test
    public void testHeaderOpenExternal() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_custom_tab), false).apply();
        TestApplication.addResolver(new Intent(Intent.ACTION_VIEW, Uri.parse("http://example.com")));
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM, new TestItem() {
            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }

            @Override
            public String getUrl() {
                return "http://example.com";
            }

            @Override
            public boolean isStoryType() {
                return true;
            }

            @Override
            public String getId() {
                return "1";
            }
        });
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(activity.getString(pref_external), true).apply();
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        activity.findViewById(button_article).performClick();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasAction(ACTION_VIEW);
    }

    @Test
    public void testFavoriteStory() {
        Intent intent = new Intent();
        TestHnItem item = new TestHnItem(1L) {
            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }
        };
        setFavorite(true);
        intent.putExtra(EXTRA_ITEM, item);
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        Assert.assertTrue(isFavorite());
        activity.findViewById(bookmarked).performClick();
        Mockito.verify(favoriteManager).remove(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq("1"));
        MaterialisticDatabase.getInstance(application).setLiveValue(MaterialisticDatabase.getBaseSavedUri().buildUpon().appendPath("remove").appendPath("1").build());
        Assert.assertFalse(isFavorite());
        assertThat(((android.widget.TextView) (activity.findViewById(snackbar_text)))).isNotNull().containsText(toast_removed);
        activity.findViewById(snackbar_action).performClick();
        MaterialisticDatabase.getInstance(application).setLiveValue(MaterialisticDatabase.getBaseSavedUri().buildUpon().appendPath("add").appendPath("1").build());
        Assert.assertTrue(isFavorite());
    }

    @Test
    public void testNonFavoriteStory() {
        TestHnItem item = new TestHnItem(1L) {
            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }
        };
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM, item);
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        Assert.assertFalse(isFavorite());
        activity.findViewById(bookmarked).performClick();
        MaterialisticDatabase.getInstance(application).setLiveValue(MaterialisticDatabase.getBaseSavedUri().buildUpon().appendPath("add").appendPath("1").build());
        Assert.assertTrue(isFavorite());
    }

    @Config(shadows = ShadowRecyclerView.class)
    @Test
    public void testScrollToTop() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM, new TestItem() {
            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }

            @Override
            public String getId() {
                return "1";
            }

            @Override
            public boolean isStoryType() {
                return true;
            }

            @Override
            public int getKidCount() {
                return 10;
            }

            @Override
            public String getUrl() {
                return "http://example.com";
            }
        });
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        // see https://github.com/robolectric/robolectric/issues/1326
        ShadowLooper.pauseMainLooper();
        controller.visible();
        ShadowApplication.getInstance().getForegroundThreadScheduler().advanceToLastPostedRunnable();
        RecyclerView recyclerView = activity.findViewById(recycler_view);
        recyclerView.smoothScrollToPosition(1);
        assertThat(CustomShadows.customShadowOf(recyclerView).getScrollPosition()).isEqualTo(1);
        TabLayout tabLayout = activity.findViewById(tab_layout);
        assertThat(tabLayout.getTabCount()).isEqualTo(2);
        tabLayout.getTabAt(1).select();
        tabLayout.getTabAt(0).select();
        tabLayout.getTabAt(0).select();
        assertThat(CustomShadows.customShadowOf(recyclerView).getScrollPosition()).isEqualTo(0);
    }

    @Test
    public void testDefaultReadabilityView() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_story_display), activity.getString(pref_story_display_value_readability)).apply();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM, new TestItem() {
            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }

            @Override
            public String getId() {
                return "1";
            }

            @Override
            public boolean isStoryType() {
                return true;
            }

            @Override
            public int getKidCount() {
                return 10;
            }

            @Override
            public String getUrl() {
                return "http://example.com";
            }
        });
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        TabLayout tabLayout = activity.findViewById(tab_layout);
        Assert.assertEquals(2, tabLayout.getTabCount());
        Assert.assertEquals(1, tabLayout.getSelectedTabPosition());
    }

    @Test
    public void testVotePromptToLogin() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM, new TestHnItem(1));
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        activity.findViewById(vote_button).performClick();
        Mockito.verify(userServices).voteUp(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq("1"), userServicesCallback.capture());
        userServicesCallback.getValue().onDone(false);
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, LoginActivity.class);
    }

    @Test
    public void testVote() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM, new TestHnItem(1));
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        activity.findViewById(vote_button).performClick();
        Mockito.verify(userServices).voteUp(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq("1"), userServicesCallback.capture());
        userServicesCallback.getValue().onDone(true);
        Assert.assertEquals(activity.getString(voted), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testVoteError() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM, new TestHnItem(1));
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        activity.findViewById(vote_button).performClick();
        Mockito.verify(userServices).voteUp(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq("1"), userServicesCallback.capture());
        userServicesCallback.getValue().onError(new IOException());
        Assert.assertEquals(activity.getString(vote_failed), ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testReply() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_story_display), activity.getString(pref_story_display_value_comments)).apply();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM, new TestHnItem(1L));
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        activity.findViewById(reply_button).performClick();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, ComposeActivity.class);
    }

    @Test
    public void testVolumeNavigation() {
        Intent intent = new Intent();
        WebItem webItem = new TestWebItem() {
            @Override
            public String getUrl() {
                return "http://example.com";
            }

            @Override
            public String getId() {
                return "1";
            }
        };
        intent.putExtra(EXTRA_ITEM, webItem);
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume().visible();
        activity = controller.get();
        activity.onKeyDown(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
        Mockito.verify(keyDelegate).setScrollable(ArgumentMatchers.any(Scrollable.class), ArgumentMatchers.any(AppBarLayout.class));
        Mockito.verify(keyDelegate).onKeyDown(ArgumentMatchers.anyInt(), ArgumentMatchers.any(KeyEvent.class));
        activity.onKeyUp(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_UP));
        Mockito.verify(keyDelegate).onKeyUp(ArgumentMatchers.anyInt(), ArgumentMatchers.any(KeyEvent.class));
        activity.onKeyLongPress(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
        Mockito.verify(keyDelegate).onKeyLongPress(ArgumentMatchers.anyInt(), ArgumentMatchers.any(KeyEvent.class));
    }

    @Test
    public void testBackPressed() {
        Intent intent = new Intent();
        WebItem webItem = new TestWebItem() {
            @Override
            public String getUrl() {
                return "http://example.com";
            }

            @Override
            public String getId() {
                return "1";
            }
        };
        intent.putExtra(EXTRA_ITEM, webItem);
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume().visible();
        activity = controller.get();
        activity.onKeyDown(KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        Mockito.verify(keyDelegate).setBackInterceptor(ArgumentMatchers.any());
        Mockito.verify(keyDelegate).onKeyDown(ArgumentMatchers.anyInt(), ArgumentMatchers.any(KeyEvent.class));
    }

    @Config(shadows = { ShadowFloatingActionButton.class })
    @Test
    public void testFullscreen() {
        Intent intent = new Intent();
        WebItem webItem = new TestWebItem() {
            @Override
            public String getUrl() {
                return "http://example.com";
            }

            @Override
            public String getId() {
                return "1";
            }
        };
        intent.putExtra(EXTRA_ITEM, webItem);
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume().visible();
        activity = controller.get();
        ShadowFloatingActionButton shadowFab = Shadow.extract(activity.findViewById(reply_button));
        Assert.assertTrue(shadowFab.isVisible());
        ShadowLocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(ACTION_FULLSCREEN).putExtra(EXTRA_FULLSCREEN, true));
        Assert.assertFalse(shadowFab.isVisible());
        ShadowLocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(ACTION_FULLSCREEN).putExtra(EXTRA_FULLSCREEN, false));
        Assert.assertTrue(shadowFab.isVisible());
    }

    @Test
    public void testFullscreenBackPressed() {
        Intent intent = new Intent();
        WebItem webItem = new TestWebItem() {
            @Override
            public String getUrl() {
                return "http://example.com";
            }

            @Override
            public String getId() {
                return "1";
            }
        };
        intent.putExtra(EXTRA_ITEM, webItem);
        controller = Robolectric.buildActivity(ItemActivity.class, intent);
        controller.create().start().resume().visible();
        activity = controller.get();
        ShadowLocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(ACTION_FULLSCREEN).putExtra(EXTRA_FULLSCREEN, true));
        activity.onBackPressed();
        assertThat(activity).isNotFinishing();
        activity.onBackPressed();
        assertThat(activity).isFinishing();
    }

    @Test
    public void testItemChanged() {
        startWithIntent();
        TabLayout tabLayout = activity.findViewById(tab_layout);
        Assert.assertEquals(activity.getResources().getQuantityString(comments_count, 0, 0), tabLayout.getTabAt(0).getText());
        activity.onItemChanged(new TestHnItem(1L) {
            @Override
            public int getKidCount() {
                return 10;
            }
        });
        Assert.assertEquals(activity.getResources().getQuantityString(comments_count, 10, 10), tabLayout.getTabAt(0).getText());
    }

    @Test
    public void testNavButtonHint() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_story_display), activity.getString(pref_story_display_value_comments)).putBoolean(activity.getString(pref_navigation), true).apply();
        startWithIntent();
        View navButton = activity.findViewById(navigation_button);
        assertThat(navButton).isVisible();
        onSingleTapConfirmed(Mockito.mock(MotionEvent.class));
        assertThat(ShadowToast.getTextOfLatestToast()).contains(activity.getString(hint_nav_short));
    }

    @Test
    public void testNavButtonDrag() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_story_display), activity.getString(pref_story_display_value_comments)).putBoolean(activity.getString(pref_navigation), true).apply();
        startWithIntent();
        View navButton = activity.findViewById(navigation_button);
        assertThat(navButton).isVisible();
        getDetector(navButton).getListener().onLongPress(Mockito.mock(MotionEvent.class));
        assertThat(ShadowToast.getTextOfLatestToast()).contains(activity.getString(hint_drag));
        MotionEvent motionEvent = Mockito.mock(MotionEvent.class);
        Mockito.when(motionEvent.getAction()).thenReturn(ACTION_MOVE);
        Mockito.when(motionEvent.getRawX()).thenReturn(1.0F);
        Mockito.when(motionEvent.getRawY()).thenReturn(1.0F);
        Shadows.shadowOf(navButton).getOnTouchListener().onTouch(navButton, motionEvent);
        motionEvent = Mockito.mock(MotionEvent.class);
        Mockito.when(motionEvent.getAction()).thenReturn(ACTION_UP);
        Shadows.shadowOf(navButton).getOnTouchListener().onTouch(navButton, motionEvent);
        assertThat(navButton).hasX(1.0F).hasY(1.0F);
    }
}

