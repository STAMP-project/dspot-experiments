package io.github.hidroh.materialistic;


import Build.VERSION_CODES;
import KeyDelegate.BackInterceptor;
import KeyEvent.KEYCODE_BACK;
import R.id.content;
import R.id.empty_selection;
import R.id.menu_article;
import R.id.menu_comments;
import R.id.menu_external;
import R.id.menu_share;
import R.id.recycler_view;
import R.id.reply_button;
import R.id.tab_layout;
import R.string.pref_story_display;
import R.string.pref_story_display_value_comments;
import R.string.pref_story_display_value_readability;
import R.string.title_activity_list;
import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.PopupMenu;
import io.github.hidroh.materialistic.data.TestHnItem;
import io.github.hidroh.materialistic.test.TestListActivity;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.CustomShadows;
import io.github.hidroh.materialistic.test.shadow.ShadowFloatingActionButton;
import io.github.hidroh.materialistic.test.shadow.ShadowRecyclerView;
import javax.inject.Inject;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;
import org.robolectric.shadows.ShadowPopupMenu;


@Config(qualifiers = "w820dp-land", shadows = { ShadowFloatingActionButton.class })
@RunWith(TestRunner.class)
public class BaseListActivityLandTest {
    private ActivityController<TestListActivity> controller;

    private TestListActivity activity;

    @Inject
    KeyDelegate keyDelegate;

    @Test
    public void testCreateLand() {
        Assert.assertNotNull(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_share));
        Assert.assertFalse(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_share).isVisible());
        Assert.assertNotNull(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_external));
        Assert.assertFalse(Shadows.shadowOf(activity).getOptionsMenu().findItem(menu_external).isVisible());
        Assert.assertFalse(((ShadowFloatingActionButton) (Shadow.extract(activity.findViewById(reply_button)))).isVisible());
    }

    @Test
    public void testRotate() {
        activity.onItemSelected(new TestHnItem(1L) {
            @Override
            public String getDisplayedTitle() {
                return "item title";
            }

            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }
        });
        assertThat(activity).hasTitle("item title");
        Bundle savedState = new Bundle();
        activity.onSaveInstanceState(savedState);
        RuntimeEnvironment.setQualifiers("");
        controller = Robolectric.buildActivity(TestListActivity.class);
        activity = controller.create(savedState).postCreate(null).start().resume().get();
        assertThat(activity).hasTitle(activity.getString(title_activity_list));
        savedState = new Bundle();
        activity.onSaveInstanceState(savedState);
        RuntimeEnvironment.setQualifiers("w820dp-land");
        controller = Robolectric.buildActivity(TestListActivity.class);
        activity = controller.create(savedState).postCreate(null).start().resume().get();
        assertThat(activity).hasTitle("item title");
        Assert.assertTrue(((ShadowFloatingActionButton) (Shadow.extract(activity.findViewById(reply_button)))).isVisible());
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testSelectItemOpenStory() {
        assertThat(((android.view.View) (activity.findViewById(empty_selection)))).isVisible();
        activity.onItemSelected(new TestHnItem(1L) {
            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }

            @Override
            public String getUrl() {
                return "http://example.com";
            }
        });
        assertThat(((android.view.View) (activity.findViewById(empty_selection)))).isNotVisible();
        assertStoryMode();
        Shadows.shadowOf(activity).clickMenuItem(menu_share);
        PopupMenu popupMenu = ShadowPopupMenu.getLatestPopupMenu();
        Assert.assertNotNull(popupMenu);
        assertThat(popupMenu.getMenu()).hasItem(menu_article).hasItem(menu_comments);
        Shadows.shadowOf(activity).clickMenuItem(menu_external);
        Assert.assertNotNull(ShadowPopupMenu.getLatestPopupMenu());
    }

    @Test
    public void testDefaultCommentView() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_story_display), activity.getString(pref_story_display_value_comments)).apply();
        controller.pause().resume();
        activity.onItemSelected(new TestHnItem(1L) {
            @Override
            public String getId() {
                return "1";
            }

            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }
        });
        assertCommentMode();
        activity.findViewById(reply_button).performClick();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, ComposeActivity.class);
    }

    @Test
    public void testDefaultReadabilityView() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(activity.getString(pref_story_display), activity.getString(pref_story_display_value_readability)).apply();
        controller.pause().resume();
        activity.onItemSelected(new TestHnItem(1L) {
            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }
        });
        ViewPager viewPager = activity.findViewById(content);
        viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
        assertStoryMode();
    }

    @Test
    public void testGetSelectedItem() {
        activity.onItemSelected(createWebItem());
        Assert.assertNotNull(getSelectedItem());
        Shadows.shadowOf(activity).recreate();
        Assert.assertNotNull(getSelectedItem());
    }

    @Test
    public void testClearSelection() {
        activity.onItemSelected(createWebItem());
        assertThat(((android.view.View) (activity.findViewById(empty_selection)))).isNotVisible();
        activity.onItemSelected(null);
        assertThat(((android.view.View) (activity.findViewById(empty_selection)))).isVisible();
        Assert.assertFalse(((ShadowFloatingActionButton) (Shadow.extract(activity.findViewById(reply_button)))).isVisible());
    }

    @Test
    public void testToggleItemView() {
        activity.onItemSelected(createWebItem());
        TabLayout tabLayout = activity.findViewById(tab_layout);
        Assert.assertEquals(2, tabLayout.getTabCount());
        assertStoryMode();
        tabLayout.getTabAt(0).select();
        assertCommentMode();
        tabLayout.getTabAt(1).select();
        assertStoryMode();
    }

    @Config(shadows = ShadowRecyclerView.class)
    @Test
    public void testScrollItemToTop() {
        activity.onItemSelected(new TestHnItem(1L) {
            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }
        });
        TabLayout tabLayout = activity.findViewById(tab_layout);
        assertThat(tabLayout.getTabCount()).isEqualTo(2);
        tabLayout.getTabAt(0).select();
        ViewPager viewPager = activity.findViewById(content);
        viewPager.getAdapter().instantiateItem(viewPager, 0);
        viewPager.getAdapter().finishUpdate(viewPager);
        RecyclerView itemRecyclerView = viewPager.findViewById(recycler_view);
        itemRecyclerView.smoothScrollToPosition(1);
        assertThat(CustomShadows.customShadowOf(itemRecyclerView).getScrollPosition()).isEqualTo(1);
        tabLayout.getTabAt(1).select();
        tabLayout.getTabAt(0).select();
        tabLayout.getTabAt(0).select();
        assertThat(CustomShadows.customShadowOf(itemRecyclerView).getScrollPosition()).isEqualTo(0);
    }

    @Test
    public void testBackPressed() {
        onItemSelected(new TestHnItem(1L) {
            @NonNull
            @Override
            public String getType() {
                return STORY_TYPE;
            }
        });
        activity.onKeyDown(KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        Mockito.verify(keyDelegate).setBackInterceptor(ArgumentMatchers.any(BackInterceptor.class));
        Mockito.verify(keyDelegate).onKeyDown(ArgumentMatchers.anyInt(), ArgumentMatchers.any(KeyEvent.class));
    }
}

