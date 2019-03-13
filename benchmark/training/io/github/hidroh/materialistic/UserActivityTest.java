package io.github.hidroh.materialistic;


import ItemManager.MODE_DEFAULT;
import KeyEvent.KEYCODE_VOLUME_UP;
import R.id.about;
import R.id.comment;
import R.id.empty;
import R.id.posted;
import R.id.recycler_view;
import R.id.text;
import R.id.title;
import R.plurals.score;
import R.plurals.submissions_count;
import R.string.user_failed;
import RecyclerView.ViewHolder;
import TabLayout.Tab;
import ThreadPreviewActivity.EXTRA_ITEM;
import UserManager.User;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import io.github.hidroh.materialistic.data.Item;
import io.github.hidroh.materialistic.data.ItemManager;
import io.github.hidroh.materialistic.data.ResponseListener;
import io.github.hidroh.materialistic.data.TestHnItem;
import io.github.hidroh.materialistic.data.UserManager;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.CustomShadows;
import io.github.hidroh.materialistic.test.shadow.ShadowRecyclerView;
import io.github.hidroh.materialistic.test.shadow.ShadowRecyclerViewAdapter;
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
import org.robolectric.shadows.ShadowToast;

import static ActivityModule.HN;
import static BuildConfig.APPLICATION_ID;


@Config(shadows = ShadowRecyclerViewAdapter.class)
@RunWith(TestRunner.class)
public class UserActivityTest {
    private ActivityController<UserActivity> controller;

    private UserActivity activity;

    @Inject
    UserManager userManager;

    @Inject
    @Named(HN)
    ItemManager itemManager;

    @Inject
    KeyDelegate keyDelegate;

    @Captor
    ArgumentCaptor<ResponseListener<UserManager.User>> userCaptor;

    @Captor
    ArgumentCaptor<ResponseListener<Item>> itemCaptor;

    private User user;

    @Test
    public void testBinding() {
        Mockito.verify(userManager).getUser(ArgumentMatchers.eq("username"), userCaptor.capture());
        userCaptor.getValue().onResponse(user);
        assertThat(((android.widget.TextView) (activity.findViewById(title)))).hasTextString("username (2,016)");
        assertThat(((android.widget.TextView) (activity.findViewById(about)))).hasTextString("about");
        Assert.assertEquals(activity.getResources().getQuantityString(submissions_count, 2, 2), getTabAt(0).getText());
        Assert.assertEquals(2, getAdapter().getItemCount());
        Shadows.shadowOf(activity).recreate();
        assertThat(((android.widget.TextView) (activity.findViewById(title)))).hasTextString("username (2,016)");
    }

    @Test
    public void testBindingNoAbout() {
        Mockito.when(user.getAbout()).thenReturn(null);
        Mockito.verify(userManager).getUser(ArgumentMatchers.eq("username"), userCaptor.capture());
        userCaptor.getValue().onResponse(user);
        assertThat(((android.widget.TextView) (activity.findViewById(about)))).isNotVisible();
    }

    @Test
    public void testEmpty() {
        Mockito.verify(userManager).getUser(ArgumentMatchers.eq("username"), userCaptor.capture());
        userCaptor.getValue().onResponse(null);
        assertThat(((android.view.View) (activity.findViewById(empty)))).isVisible();
    }

    @Test
    public void testFailed() {
        Mockito.verify(userManager).getUser(ArgumentMatchers.eq("username"), userCaptor.capture());
        userCaptor.getValue().onError(null);
        Assert.assertEquals(activity.getString(user_failed), ShadowToast.getTextOfLatestToast());
    }

    @Config(shadows = ShadowRecyclerView.class)
    @Test
    public void testScrollToTop() {
        Mockito.verify(userManager).getUser(ArgumentMatchers.eq("username"), userCaptor.capture());
        userCaptor.getValue().onResponse(user);
        RecyclerView recyclerView = ((RecyclerView) (activity.findViewById(recycler_view)));
        recyclerView.smoothScrollToPosition(1);
        assertThat(CustomShadows.customShadowOf(recyclerView).getScrollPosition()).isEqualTo(1);
        TabLayout.Tab tab = ((TabLayout) (activity.findViewById(R.id.tab_layout))).getTabAt(0);
        tab.select();
        tab.select();
        assertThat(CustomShadows.customShadowOf(recyclerView).getScrollPosition()).isEqualTo(0);
    }

    @Test
    public void testNoId() {
        controller = Robolectric.buildActivity(UserActivity.class);
        activity = controller.create().get();
        assertThat(activity).isFinishing();
    }

    @Test
    public void testNoDataId() {
        Intent intent = new Intent();
        intent.setData(Uri.parse(((APPLICATION_ID) + "://user/")));
        controller = Robolectric.buildActivity(UserActivity.class, intent);
        activity = controller.create().get();
        assertThat(activity).isFinishing();
    }

    @Test
    public void testWithDataId() {
        Intent intent = new Intent();
        intent.setData(Uri.parse(((APPLICATION_ID) + "://user/123")));
        controller = Robolectric.buildActivity(UserActivity.class, intent);
        activity = controller.create().get();
        assertThat(activity).isNotFinishing();
    }

    @Test
    public void testDeepLink() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("https://news.ycombinator.com/user?id=123"));
        controller = Robolectric.buildActivity(UserActivity.class, intent);
        activity = controller.create().get();
        assertThat(activity).isNotFinishing();
    }

    @Test
    public void testCommentBinding() {
        Mockito.verify(userManager).getUser(ArgumentMatchers.eq("username"), userCaptor.capture());
        userCaptor.getValue().onResponse(user);
        RecyclerView recyclerView = ((RecyclerView) (activity.findViewById(recycler_view)));
        Mockito.verify(itemManager).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(MODE_DEFAULT), itemCaptor.capture());
        itemCaptor.getValue().onResponse(new TestHnItem(1L) {
            @Override
            public String getText() {
                return "content";
            }

            @Override
            public String getParent() {
                return "2";
            }
        });
        RecyclerView.ViewHolder viewHolder = CustomShadows.customShadowOf(recyclerView.getAdapter()).getViewHolder(0);
        assertThat(((android.view.View) (viewHolder.itemView.findViewById(title)))).isNotVisible();
        assertThat(((android.widget.TextView) (viewHolder.itemView.findViewById(text)))).isVisible().hasTextString("content");
        viewHolder.itemView.findViewById(comment).performClick();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, ThreadPreviewActivity.class).hasExtra(EXTRA_ITEM);
    }

    @Test
    public void testStoryBinding() {
        Mockito.verify(userManager).getUser(ArgumentMatchers.eq("username"), userCaptor.capture());
        userCaptor.getValue().onResponse(user);
        RecyclerView recyclerView = ((RecyclerView) (activity.findViewById(recycler_view)));
        Mockito.verify(itemManager).getItem(ArgumentMatchers.eq("2"), ArgumentMatchers.eq(MODE_DEFAULT), itemCaptor.capture());
        itemCaptor.getValue().onResponse(new TestHnItem(2L) {
            @Override
            public String getTitle() {
                return "title";
            }

            @Override
            public String getText() {
                return "content";
            }

            @Override
            public int getScore() {
                return 46;
            }
        });
        RecyclerView.ViewHolder viewHolder = CustomShadows.customShadowOf(recyclerView.getAdapter()).getViewHolder(1);
        assertThat(((android.widget.TextView) (viewHolder.itemView.findViewById(posted)))).containsText(activity.getResources().getQuantityString(score, 46, 46));
        assertThat(((android.widget.TextView) (viewHolder.itemView.findViewById(title)))).isVisible().hasTextString("title");
        assertThat(((android.widget.TextView) (viewHolder.itemView.findViewById(text)))).isVisible().hasTextString("content");
        viewHolder.itemView.findViewById(comment).performClick();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, ItemActivity.class).hasExtra(ItemActivity.EXTRA_ITEM);
    }

    @Test
    public void testDeletedItemBinding() {
        Mockito.verify(userManager).getUser(ArgumentMatchers.eq("username"), userCaptor.capture());
        userCaptor.getValue().onResponse(user);
        RecyclerView recyclerView = ((RecyclerView) (activity.findViewById(recycler_view)));
        Mockito.verify(itemManager).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(MODE_DEFAULT), itemCaptor.capture());
        itemCaptor.getValue().onResponse(new TestHnItem(1L) {
            @Override
            public boolean isDeleted() {
                return true;
            }
        });
        RecyclerView.ViewHolder viewHolder = CustomShadows.customShadowOf(recyclerView.getAdapter()).getViewHolder(0);
        assertThat(((android.view.View) (viewHolder.itemView.findViewById(comment)))).isNotVisible();
    }

    @Test
    public void testVolumeNavigation() {
        activity.onKeyDown(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
        Mockito.verify(keyDelegate).setScrollable(ArgumentMatchers.any(Scrollable.class), ArgumentMatchers.any());
        Mockito.verify(keyDelegate).onKeyDown(ArgumentMatchers.anyInt(), ArgumentMatchers.any(KeyEvent.class));
        activity.onKeyUp(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_VOLUME_UP));
        Mockito.verify(keyDelegate).onKeyUp(ArgumentMatchers.anyInt(), ArgumentMatchers.any(KeyEvent.class));
        activity.onKeyLongPress(KEYCODE_VOLUME_UP, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_UP));
        Mockito.verify(keyDelegate).onKeyLongPress(ArgumentMatchers.anyInt(), ArgumentMatchers.any(KeyEvent.class));
    }
}

