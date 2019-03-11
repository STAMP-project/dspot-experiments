package io.github.hidroh.materialistic;


import ItemActivity.EXTRA_OPEN_COMMENTS;
import ItemFragment.EXTRA_ITEM;
import ItemFragment.ItemChangedListener;
import ItemManager.MODE_DEFAULT;
import ItemManager.MODE_NETWORK;
import R.id;
import R.id.comment;
import R.id.empty;
import R.id.recycler_view;
import R.id.swipe_layout;
import R.id.text;
import RecyclerView.ViewHolder;
import RuntimeEnvironment.application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import io.github.hidroh.materialistic.data.Item;
import io.github.hidroh.materialistic.data.ItemManager;
import io.github.hidroh.materialistic.data.ResponseListener;
import io.github.hidroh.materialistic.data.TestHnItem;
import io.github.hidroh.materialistic.data.WebItem;
import io.github.hidroh.materialistic.test.TestItem;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.CustomShadows;
import io.github.hidroh.materialistic.test.shadow.ShadowPreferenceFragmentCompat;
import io.github.hidroh.materialistic.test.shadow.ShadowRecyclerViewAdapter;
import io.github.hidroh.materialistic.test.shadow.ShadowSwipeRefreshLayout;
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
import org.robolectric.shadow.api.Shadow;

import static ActivityModule.HN;


@SuppressWarnings("ConstantConditions")
@Config(shadows = { ShadowSwipeRefreshLayout.class, ShadowRecyclerViewAdapter.class, ShadowPreferenceFragmentCompat.class })
@RunWith(TestRunner.class)
public class ItemFragmentMultiPageTest {
    @Inject
    @Named(HN)
    ItemManager hackerNewsClient;

    @Captor
    ArgumentCaptor<ResponseListener<Item>> listener;

    private ActivityController<ItemFragmentMultiPageTest.TestItemActivity> controller;

    private ItemFragmentMultiPageTest.TestItemActivity activity;

    @Test
    public void testEmptyView() {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ITEM, new TestItem() {});
        Fragment fragment = Fragment.instantiate(application, ItemFragment.class.getName(), args);
        makeVisible(fragment);
        assertThat(((android.view.View) (fragment.getView().findViewById(empty)))).isVisible();
    }

    @Test
    public void testWebItem() {
        WebItem webItem = Mockito.mock(WebItem.class);
        Mockito.when(webItem.getId()).thenReturn("1");
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ITEM, webItem);
        Fragment fragment = Fragment.instantiate(application, ItemFragment.class.getName(), args);
        makeVisible(fragment);
        Mockito.verify(hackerNewsClient).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(MODE_DEFAULT), listener.capture());
        listener.getValue().onResponse(new TestItem() {
            @Override
            public Item[] getKidItems() {
                return new Item[]{ new TestItem() {} };
            }

            @Override
            public int getKidCount() {
                return 1;
            }
        });
        assertThat(((android.view.View) (fragment.getView().findViewById(empty)))).isNotVisible();
    }

    @Test
    public void testBindLocalKidData() {
        Item story = new TestHnItem(0L);
        story.populate(new TestItem() {
            @Override
            public int getDescendants() {
                return 1;
            }

            @Override
            public long[] getKids() {
                return new long[]{ 1L };
            }
        });
        story.getKidItems()[0].populate(new TestItem() {
            @Override
            public String getText() {
                return "text";
            }

            @Override
            public long[] getKids() {
                return new long[]{ 2L };
            }

            @Override
            public int getDescendants() {
                return 1;
            }
        });
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ITEM, story);
        Fragment fragment = Fragment.instantiate(application, ItemFragment.class.getName(), args);
        makeVisible(fragment);
        assertThat(((android.view.View) (fragment.getView().findViewById(empty)))).isNotVisible();
        RecyclerView recyclerView = fragment.getView().findViewById(recycler_view);
        RecyclerView.ViewHolder viewHolder = CustomShadows.customShadowOf(recyclerView.getAdapter()).getViewHolder(0);
        assertThat(((android.widget.TextView) (viewHolder.itemView.findViewById(text)))).hasTextString("text");
        assertThat(((android.view.View) (viewHolder.itemView.findViewById(comment)))).isVisible();
        viewHolder.itemView.findViewById(comment).performClick();
        Intent actual = Shadows.shadowOf(fragment.getActivity()).getNextStartedActivity();
        Assert.assertEquals(ItemActivity.class.getName(), actual.getComponent().getClassName());
        assertThat(actual).hasExtra(EXTRA_OPEN_COMMENTS, true);
    }

    @Test
    public void testBindRemoteKidData() {
        Bundle args = new Bundle();
        Item item = new TestHnItem(2L);
        item.populate(new TestHnItem(2L) {
            @Override
            public long[] getKids() {
                return new long[]{ 1L };
            }
        });
        args.putParcelable(EXTRA_ITEM, item);
        Fragment fragment = Fragment.instantiate(application, ItemFragment.class.getName(), args);
        makeVisible(fragment);
        Mockito.verify(hackerNewsClient).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(MODE_DEFAULT), listener.capture());
        listener.getValue().onResponse(new TestHnItem(1L) {
            @Override
            public String getTitle() {
                return "title";
            }
        });
        Assert.assertEquals(1, item.getKidItems()[0].getLocalRevision());
        Assert.assertEquals("title", item.getKidItems()[0].getTitle());
    }

    @Test
    public void testRefresh() {
        WebItem webItem = Mockito.mock(WebItem.class);
        Mockito.when(webItem.getId()).thenReturn("1");
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ITEM, webItem);
        Fragment fragment = Fragment.instantiate(application, ItemFragment.class.getName(), args);
        makeVisible(fragment);
        ShadowSwipeRefreshLayout shadowSwipeRefreshLayout = Shadow.extract(fragment.getView().findViewById(swipe_layout));
        shadowSwipeRefreshLayout.getOnRefreshListener().onRefresh();
        Mockito.verify(hackerNewsClient).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(MODE_DEFAULT), listener.capture());
        Mockito.verify(hackerNewsClient).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(MODE_NETWORK), listener.capture());
        listener.getAllValues().get(1).onResponse(new TestHnItem(1L));
        assertThat(((android.support.v4.widget.SwipeRefreshLayout) (fragment.getView().findViewById(swipe_layout)))).isNotRefreshing();
        Mockito.verify(((ItemFragmentMultiPageTest.TestItemActivity) (fragment.getActivity())).itemChangedListener).onItemChanged(ArgumentMatchers.any(Item.class));
    }

    @Test
    public void testRefreshFailed() {
        WebItem webItem = Mockito.mock(WebItem.class);
        Mockito.when(webItem.getId()).thenReturn("1");
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ITEM, webItem);
        Fragment fragment = Fragment.instantiate(application, ItemFragment.class.getName(), args);
        makeVisible(fragment);
        ShadowSwipeRefreshLayout shadowSwipeRefreshLayout = Shadow.extract(fragment.getView().findViewById(swipe_layout));
        shadowSwipeRefreshLayout.getOnRefreshListener().onRefresh();
        Mockito.verify(hackerNewsClient).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(MODE_DEFAULT), listener.capture());
        Mockito.verify(hackerNewsClient).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(MODE_NETWORK), listener.capture());
        listener.getAllValues().get(1).onError(null);
        assertThat(((android.support.v4.widget.SwipeRefreshLayout) (fragment.getView().findViewById(swipe_layout)))).isNotRefreshing();
    }

    @Test
    public void testDisplayMenu() {
        WebItem webItem = Mockito.mock(WebItem.class);
        Mockito.when(webItem.getId()).thenReturn("1");
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ITEM, webItem);
        Fragment fragment = Fragment.instantiate(application, ItemFragment.class.getName(), args);
        makeVisible(fragment);
        fragment.onOptionsItemSelected(new org.robolectric.fakes.RoboMenuItem(id.menu_comments));
        assertThat(fragment.getFragmentManager()).hasFragmentWithTag(PopupSettingsFragment.class.getName());
    }

    public static class TestItemActivity extends InjectableActivity implements ItemFragment.ItemChangedListener {
        ItemChangedListener itemChangedListener = Mockito.mock(ItemChangedListener.class);

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_item);
            setSupportActionBar(findViewById(R.id.toolbar));
        }

        @Override
        public void onItemChanged(@NonNull
        Item item) {
            itemChangedListener.onItemChanged(item);
        }
    }
}

