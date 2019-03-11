/**
 * Copyright (c) 2015 Ha Duy Trung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.hidroh.materialistic;


import ItemActivity.EXTRA_ITEM;
import ItemManager.MODE_DEFAULT;
import KeyEvent.KEYCODE_VOLUME_UP;
import R.id.comment;
import R.id.recycler_view;
import RecyclerView.ViewHolder;
import android.R.id.home;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import io.github.hidroh.materialistic.data.Item;
import io.github.hidroh.materialistic.data.ItemManager;
import io.github.hidroh.materialistic.data.ResponseListener;
import io.github.hidroh.materialistic.data.TestHnItem;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.test.shadow.CustomShadows;
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

import static ActivityModule.HN;


@Config(shadows = { ShadowRecyclerViewAdapter.class })
@RunWith(TestRunner.class)
public class ThreadPreviewActivityTest {
    private ActivityController<ThreadPreviewActivity> controller;

    private ThreadPreviewActivity activity;

    @Inject
    @Named(HN)
    ItemManager itemManager;

    @Inject
    KeyDelegate keyDelegate;

    @Captor
    ArgumentCaptor<ResponseListener<Item>> itemCaptor;

    @Test
    public void testNoItem() {
        controller = Robolectric.buildActivity(ThreadPreviewActivity.class);
        activity = controller.create().get();
        assertThat(activity).isFinishing();
    }

    @Test
    public void testHomePressed() {
        Shadows.shadowOf(activity).clickMenuItem(home);
        assertThat(activity).isFinishing();
    }

    @Test
    public void testBinding() {
        RecyclerView recyclerView = activity.findViewById(recycler_view);
        Mockito.verify(itemManager).getItem(ArgumentMatchers.eq("2"), ArgumentMatchers.eq(MODE_DEFAULT), itemCaptor.capture());
        itemCaptor.getValue().onResponse(new TestHnItem(2L) {
            @NonNull
            @Override
            public String getRawType() {
                return Item.COMMENT_TYPE;
            }

            @Override
            public String getText() {
                return "comment";
            }

            @Override
            public String getParent() {
                return "1";
            }

            @Override
            public String getBy() {
                return "username";
            }
        });
        Mockito.verify(itemManager).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(MODE_DEFAULT), itemCaptor.capture());
        itemCaptor.getValue().onResponse(new TestHnItem(1L) {
            @NonNull
            @Override
            public String getRawType() {
                return Item.STORY_TYPE;
            }

            @Override
            public String getTitle() {
                return "story";
            }

            @Override
            public String getBy() {
                return "author";
            }
        });
        RecyclerView.ViewHolder viewHolder1 = CustomShadows.customShadowOf(recyclerView.getAdapter()).getViewHolder(0);
        recyclerView.getAdapter().bindViewHolder(viewHolder1, 0);// TODO should not need this

        assertThat(((android.view.View) (viewHolder1.itemView.findViewById(comment)))).isVisible();
        Assert.assertEquals(0, recyclerView.getAdapter().getItemViewType(0));
        RecyclerView.ViewHolder viewHolder2 = CustomShadows.customShadowOf(recyclerView.getAdapter()).getViewHolder(1);
        assertThat(((android.view.View) (viewHolder2.itemView.findViewById(comment)))).isNotVisible();
        Assert.assertEquals(1, recyclerView.getAdapter().getItemViewType(1));
        viewHolder1.itemView.findViewById(comment).performClick();
        assertThat(Shadows.shadowOf(activity).getNextStartedActivity()).hasComponent(activity, ItemActivity.class).hasExtra(EXTRA_ITEM);
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

