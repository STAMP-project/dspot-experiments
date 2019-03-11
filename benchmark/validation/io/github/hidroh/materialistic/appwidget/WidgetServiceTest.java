/**
 * Copyright (c) 2016 Ha Duy Trung
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
package io.github.hidroh.materialistic.appwidget;


import ItemManager.BEST_FETCH_MODE;
import ItemManager.NEW_FETCH_MODE;
import RemoteViewsService.RemoteViewsFactory;
import WidgetService.EXTRA_SECTION;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import io.github.hidroh.materialistic.data.Item;
import io.github.hidroh.materialistic.data.ItemManager;
import io.github.hidroh.materialistic.data.TestHnItem;
import io.github.hidroh.materialistic.test.TestRunner;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;


@SuppressLint("NewApi")
@RunWith(TestRunner.class)
public class WidgetServiceTest {
    private RemoteViewsFactory viewFactory;

    private ItemManager itemManager = Mockito.mock(ItemManager.class);

    @Test
    public void testGetViewFactory() {
        WidgetService service = new WidgetService() {
            @Override
            public Context getApplicationContext() {
                return RuntimeEnvironment.application;
            }
        };
        Assert.assertNotNull(service.onGetViewFactory(new Intent().putExtra(EXTRA_SECTION, BEST_FETCH_MODE)));
        Assert.assertNotNull(service.onGetViewFactory(new Intent().putExtra(EXTRA_SECTION, NEW_FETCH_MODE)));
    }

    @Test
    public void testAdapter() {
        Mockito.when(itemManager.getStories(ArgumentMatchers.any(), ArgumentMatchers.anyInt())).thenReturn(new Item[]{ new TestHnItem(1L) });
        Mockito.when(itemManager.getItem(ArgumentMatchers.any(), ArgumentMatchers.anyInt())).thenReturn(new TestHnItem(1L) {
            @Override
            public String getDisplayedTitle() {
                return "title";
            }

            @Override
            public int getScore() {
                return 100;
            }
        });
        viewFactory.onDataSetChanged();
        Mockito.verify(itemManager).getStories(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        assertThat(viewFactory.hasStableIds()).isTrue();
        assertThat(viewFactory.getCount()).isEqualTo(1);
        assertThat(viewFactory.getLoadingView()).isNotNull();
        assertThat(viewFactory.getViewTypeCount()).isEqualTo(1);
        assertThat(viewFactory.getItemId(0)).isEqualTo(1L);
        assertThat(viewFactory.getViewAt(0)).isNotNull();
        Mockito.verify(itemManager).getItem(ArgumentMatchers.eq("1"), ArgumentMatchers.anyInt());
    }

    @Test
    public void testEmpty() {
        viewFactory.onDataSetChanged();
        Mockito.verify(itemManager).getStories(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        assertThat(viewFactory.getCount()).isEqualTo(0);
        assertThat(viewFactory.getItemId(0)).isEqualTo(0L);
        assertThat(viewFactory.getViewAt(0)).isNotNull();
        Mockito.verify(itemManager, Mockito.never()).getItem(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
    }
}

