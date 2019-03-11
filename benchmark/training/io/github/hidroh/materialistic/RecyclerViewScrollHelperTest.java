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
package io.github.hidroh.materialistic;


import KeyDelegate.RecyclerViewHelper;
import RecyclerView.Adapter;
import RecyclerView.NO_POSITION;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import io.github.hidroh.materialistic.test.TestRunner;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@RunWith(TestRunner.class)
public class RecyclerViewScrollHelperTest {
    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;

    private RecyclerViewHelper helper;

    private Adapter adapter;

    @Test
    public void testScrollToTop() {
        helper = new KeyDelegate.RecyclerViewHelper(recyclerView, RecyclerViewHelper.SCROLL_ITEM);
        helper.scrollToTop();
        Mockito.verify(recyclerView).scrollToPosition(ArgumentMatchers.eq(0));
    }

    @Test
    public void testScrollToNextItem() {
        Mockito.when(adapter.getItemCount()).thenReturn(10);
        Mockito.when(layoutManager.findFirstVisibleItemPosition()).thenReturn(0);
        helper = new KeyDelegate.RecyclerViewHelper(recyclerView, RecyclerViewHelper.SCROLL_ITEM);
        Assert.assertTrue(helper.scrollToNext());
        Mockito.verify(recyclerView).smoothScrollToPosition(ArgumentMatchers.eq(1));
        Mockito.when(layoutManager.findFirstVisibleItemPosition()).thenReturn(NO_POSITION);
        Assert.assertFalse(helper.scrollToNext());
    }

    @Test
    public void testScrollToNextPage() {
        Mockito.when(adapter.getItemCount()).thenReturn(10);
        Mockito.when(layoutManager.findLastCompletelyVisibleItemPosition()).thenReturn(8);
        helper = new KeyDelegate.RecyclerViewHelper(recyclerView, RecyclerViewHelper.SCROLL_PAGE);
        Assert.assertTrue(helper.scrollToNext());
        Mockito.verify(recyclerView).smoothScrollToPosition(ArgumentMatchers.eq(9));
        Mockito.when(layoutManager.findLastCompletelyVisibleItemPosition()).thenReturn(9);
        Assert.assertFalse(helper.scrollToNext());
    }

    @Test
    public void testScrollToPreviousItem() {
        Mockito.when(adapter.getItemCount()).thenReturn(10);
        Mockito.when(layoutManager.findFirstVisibleItemPosition()).thenReturn(5);
        helper = new KeyDelegate.RecyclerViewHelper(recyclerView, RecyclerViewHelper.SCROLL_ITEM);
        Assert.assertTrue(helper.scrollToPrevious());
        Mockito.verify(recyclerView).smoothScrollToPosition(ArgumentMatchers.eq(4));
        Mockito.when(layoutManager.findFirstVisibleItemPosition()).thenReturn(0);
        Assert.assertFalse(helper.scrollToPrevious());
        Mockito.when(layoutManager.findFirstVisibleItemPosition()).thenReturn(NO_POSITION);
        Assert.assertFalse(helper.scrollToPrevious());
    }

    @Test
    public void testScrollToPreviousPage() {
        Mockito.when(layoutManager.findFirstVisibleItemPosition()).thenReturn(5);
        helper = new KeyDelegate.RecyclerViewHelper(recyclerView, RecyclerViewHelper.SCROLL_PAGE);
        Assert.assertTrue(helper.scrollToPrevious());
        Mockito.verify(recyclerView).smoothScrollBy(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.when(layoutManager.findFirstVisibleItemPosition()).thenReturn(0);
        Assert.assertFalse(helper.scrollToPrevious());
    }
}

