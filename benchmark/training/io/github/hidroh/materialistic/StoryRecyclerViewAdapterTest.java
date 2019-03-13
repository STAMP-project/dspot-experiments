/**
 * Copyright (c) 2018 Ha Duy Trung
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


import ListRecyclerViewAdapter.ItemViewHolder;
import io.github.hidroh.materialistic.data.Item;
import io.github.hidroh.materialistic.data.TestHnItem;
import io.github.hidroh.materialistic.test.ListActivity;
import io.github.hidroh.materialistic.test.TestRunner;
import io.github.hidroh.materialistic.widget.StoryRecyclerViewAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.android.controller.ActivityController;


@RunWith(TestRunner.class)
public class StoryRecyclerViewAdapterTest {
    private ActivityController<ListActivity> controller;

    private StoryRecyclerViewAdapter adapter;

    private ListActivity activity;

    @Mock
    private ItemViewHolder holder;

    @Test
    public void testNewStory() {
        TestHnItem newItem = new TestHnItem(3) {
            @Override
            public int getRank() {
                return 3;
            }

            @Override
            public int getLocalRevision() {
                return 1;
            }
        };
        adapter.setItems(new Item[]{ new TestHnItem(1) {
            @Override
            public int getRank() {
                return 1;
            }
        }, new TestHnItem(2) {
            @Override
            public int getRank() {
                return 2;
            }
        }, newItem });
        adapter.onBindViewHolder(holder, 2);
        Mockito.verify(holder).setUpdated(ArgumentMatchers.eq(newItem), ArgumentMatchers.eq(true), ArgumentMatchers.eq(0));
    }

    @Test
    public void testPromoted() {
        TestHnItem promotedItem = new TestHnItem(2) {
            @Override
            public int getRank() {
                return 1;
            }

            @Override
            public int getLocalRevision() {
                return 1;
            }
        };
        adapter.setItems(new Item[]{ promotedItem, new TestHnItem(1) {
            @Override
            public int getRank() {
                return 2;
            }
        } });
        adapter.onBindViewHolder(holder, 0);
        Mockito.verify(holder).setUpdated(ArgumentMatchers.eq(promotedItem), ArgumentMatchers.eq(false), ArgumentMatchers.eq(1));
    }
}

