/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho.widget;


import RecyclerView.LayoutManager;
import StickyHeaderControllerImpl.LAYOUTMANAGER_NOT_INITIALIZED;
import StickyHeaderControllerImpl.RECYCLER_ALREADY_INITIALIZED;
import StickyHeaderControllerImpl.RECYCLER_NOT_INITIALIZED;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.litho.ComponentTree;
import com.facebook.litho.LithoView;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link StickyHeaderController}
 */
@RunWith(ComponentsTestRunner.class)
public class StickyHeaderControllerTest {
    private HasStickyHeader mHasStickyHeader;

    private StickyHeaderController mStickyHeaderController;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testInitNoLayoutManager() {
        SectionsRecyclerView recycler = Mockito.mock(SectionsRecyclerView.class);
        RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        Mockito.when(recycler.getRecyclerView()).thenReturn(recyclerView);
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(LAYOUTMANAGER_NOT_INITIALIZED);
        mStickyHeaderController.init(recycler);
    }

    @Test
    public void testInitTwiceWithoutReset() {
        SectionsRecyclerView recycler1 = Mockito.mock(SectionsRecyclerView.class);
        RecyclerView recyclerView1 = Mockito.mock(RecyclerView.class);
        Mockito.when(recycler1.getRecyclerView()).thenReturn(recyclerView1);
        Mockito.when(recyclerView1.getLayoutManager()).thenReturn(Mockito.mock(LayoutManager.class));
        mStickyHeaderController.init(recycler1);
        SectionsRecyclerView recycler2 = Mockito.mock(SectionsRecyclerView.class);
        RecyclerView recyclerView2 = Mockito.mock(RecyclerView.class);
        Mockito.when(recyclerView2.getLayoutManager()).thenReturn(Mockito.mock(LayoutManager.class));
        Mockito.when(recycler2.getRecyclerView()).thenReturn(recyclerView2);
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(RECYCLER_ALREADY_INITIALIZED);
        mStickyHeaderController.init(recycler2);
    }

    @Test
    public void testResetBeforeInit() {
        thrown.expect(RuntimeException.class);
        thrown.expectMessage(RECYCLER_NOT_INITIALIZED);
        mStickyHeaderController.reset();
    }

    @Test
    public void testTranslateRecyclerViewChild() {
        SectionsRecyclerView recycler = Mockito.mock(SectionsRecyclerView.class);
        RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        Mockito.when(recycler.getRecyclerView()).thenReturn(recyclerView);
        Mockito.when(recyclerView.getLayoutManager()).thenReturn(Mockito.mock(LayoutManager.class));
        mStickyHeaderController.init(recycler);
        Mockito.when(mHasStickyHeader.findFirstVisibleItemPosition()).thenReturn(2);
        Mockito.when(mHasStickyHeader.isSticky(2)).thenReturn(true);
        ComponentTree componentTree = Mockito.mock(ComponentTree.class);
        Mockito.when(mHasStickyHeader.getComponentForStickyHeaderAt(2)).thenReturn(componentTree);
        LithoView lithoView = Mockito.mock(LithoView.class);
        Mockito.when(componentTree.getLithoView()).thenReturn(lithoView);
        mStickyHeaderController.onScrolled(null, 0, 0);
        Mockito.verify(lithoView).setTranslationY(ArgumentMatchers.anyFloat());
        Mockito.verify(recycler, Mockito.times(2)).hideStickyHeader();
    }

    @Test
    public void testTranslaterecyclerChild() {
        SectionsRecyclerView recycler = Mockito.mock(SectionsRecyclerView.class);
        RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        Mockito.when(recycler.getRecyclerView()).thenReturn(recyclerView);
        Mockito.when(recyclerView.getLayoutManager()).thenReturn(Mockito.mock(LayoutManager.class));
        mStickyHeaderController.init(recycler);
        Mockito.when(mHasStickyHeader.findFirstVisibleItemPosition()).thenReturn(6);
        Mockito.when(mHasStickyHeader.isSticky(2)).thenReturn(true);
        Mockito.when(mHasStickyHeader.getComponentForStickyHeaderAt(2)).thenReturn(Mockito.mock(ComponentTree.class));
        Mockito.when(mHasStickyHeader.getComponentForStickyHeaderAt(6)).thenReturn(Mockito.mock(ComponentTree.class));
        mStickyHeaderController.onScrolled(null, 0, 0);
        Mockito.verify(recycler).setStickyHeaderVerticalOffset(ArgumentMatchers.any(Integer.class));
    }

    @Test
    public void testTranslateStackedStickyHeaders() {
        SectionsRecyclerView recycler = Mockito.mock(SectionsRecyclerView.class);
        RecyclerView recyclerView = Mockito.mock(RecyclerView.class);
        Mockito.when(recycler.getRecyclerView()).thenReturn(recyclerView);
        Mockito.when(recyclerView.getLayoutManager()).thenReturn(Mockito.mock(LayoutManager.class));
        mStickyHeaderController.init(recycler);
        Mockito.when(mHasStickyHeader.findFirstVisibleItemPosition()).thenReturn(2);
        Mockito.when(mHasStickyHeader.isSticky(2)).thenReturn(true);
        Mockito.when(mHasStickyHeader.isSticky(3)).thenReturn(true);
        Mockito.when(mHasStickyHeader.isValidPosition(3)).thenReturn(true);
        ComponentTree componentTree = Mockito.mock(ComponentTree.class);
        Mockito.when(mHasStickyHeader.getComponentForStickyHeaderAt(2)).thenReturn(componentTree);
        LithoView lithoView = Mockito.mock(LithoView.class);
        Mockito.when(componentTree.getLithoView()).thenReturn(lithoView);
        mStickyHeaderController.onScrolled(null, 0, 0);
        Mockito.verify(lithoView, Mockito.never()).setTranslationY(ArgumentMatchers.any(Integer.class));
        Mockito.verify(recycler, Mockito.times(2)).hideStickyHeader();
    }
}

