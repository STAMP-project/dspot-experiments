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


import RecyclerBinder.Builder;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.Size;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.viewcompat.SimpleViewBinder;
import com.facebook.litho.viewcompat.ViewCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


/**
 * Tests for {@link RecyclerBinder.ComponentAsyncInitRangeIterator}
 */
@RunWith(ComponentsTestRunner.class)
public class RecyclerBinderAsyncInitRangeIteratorTest {
    private static final float RANGE_RATIO = 2.0F;

    private static final ViewCreator VIEW_CREATOR = new ViewCreator() {
        @Override
        public View createView(Context c, ViewGroup parent) {
            return Mockito.mock(View.class);
        }
    };

    private RecyclerBinder mRecyclerBinder;

    private Builder mRecyclerBinderBuilder;

    private ComponentContext mComponentContext;

    private LayoutInfo mLayoutInfo;

    private final List<ComponentTreeHolder> mAllHoldersList = new ArrayList<>();

    @Test
    public void testComponentAsyncInitRangeIteratorForward() {
        int totalCount = 10;
        final RenderInfo[] components = new RenderInfo[totalCount];
        for (int i = 0; i < totalCount; i++) {
            components[i] = ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build();
        }
        mRecyclerBinder.insertRangeAt(0, new ArrayList(Arrays.asList(components)));
        final int initialComponentPosition = RecyclerBinder.findInitialComponentPosition(mAllHoldersList, mRecyclerBinder.mTraverseLayoutBackwards);
        assertThat(initialComponentPosition).isEqualTo(0);
        final Iterator<ComponentTreeHolder> asyncRangeIterator = new RecyclerBinder.ComponentAsyncInitRangeIterator(mAllHoldersList, initialComponentPosition, 4, mRecyclerBinder.mTraverseLayoutBackwards);
        assertIterator(asyncRangeIterator, new java.util.LinkedList(Arrays.asList(components[1], components[2], components[3], components[4])));
    }

    @Test
    public void testComponentAsyncInitRangeIteratorForwardSkipViews() {
        int totalCount = 10;
        final RenderInfo[] components = new RenderInfo[totalCount];
        for (int i = 0; i < totalCount; i++) {
            if ((i == 0) || (i == 2)) {
                components[i] = ViewRenderInfo.create().viewCreator(RecyclerBinderAsyncInitRangeIteratorTest.VIEW_CREATOR).viewBinder(new SimpleViewBinder()).build();
            } else {
                components[i] = ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build();
            }
        }
        mRecyclerBinder.insertRangeAt(0, new ArrayList(Arrays.asList(components)));
        final int initialComponentPosition = RecyclerBinder.findInitialComponentPosition(mAllHoldersList, mRecyclerBinder.mTraverseLayoutBackwards);
        assertThat(initialComponentPosition).isEqualTo(1);
        final Iterator<ComponentTreeHolder> asyncRangeIterator = new RecyclerBinder.ComponentAsyncInitRangeIterator(mAllHoldersList, initialComponentPosition, 4, mRecyclerBinder.mTraverseLayoutBackwards);
        assertIterator(asyncRangeIterator, new java.util.LinkedList(Arrays.asList(components[3], components[4], components[5], components[6])));
    }

    @Test
    public void testComponentAsyncInitRangeIteratorBackward() {
        final RecyclerBinder binder = getStackedEndRecyclerBinder();
        int totalCount = 10;
        final RenderInfo[] components = new RenderInfo[totalCount];
        for (int i = 0; i < totalCount; i++) {
            components[i] = ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build();
        }
        binder.insertRangeAt(0, new ArrayList(Arrays.asList(components)));
        final int initialComponentPosition = RecyclerBinder.findInitialComponentPosition(mAllHoldersList, binder.mTraverseLayoutBackwards);
        assertThat(initialComponentPosition).isEqualTo(9);
        final Iterator<ComponentTreeHolder> asyncRangeIterator = new RecyclerBinder.ComponentAsyncInitRangeIterator(mAllHoldersList, initialComponentPosition, 4, binder.mTraverseLayoutBackwards);
        assertIterator(asyncRangeIterator, new java.util.LinkedList(Arrays.asList(components[8], components[7], components[6], components[5])));
    }

    @Test
    public void testComponentAsyncInitRangeIteratorBackwardSkipValidTrees() {
        final RecyclerBinder binder = getStackedEndRecyclerBinder();
        int totalCount = 10;
        final RenderInfo[] components = new RenderInfo[totalCount];
        for (int i = 0; i < totalCount; i++) {
            components[i] = ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build();
        }
        binder.insertRangeAt(0, new ArrayList(Arrays.asList(components)));
        mAllHoldersList.get(9).computeLayoutSync(mComponentContext, 0, 0, new Size());
        mAllHoldersList.get(8).computeLayoutSync(mComponentContext, 0, 0, new Size());
        final int initialComponentPosition = RecyclerBinder.findInitialComponentPosition(mAllHoldersList, binder.mTraverseLayoutBackwards);
        assertThat(initialComponentPosition).isEqualTo(9);
        final Iterator<ComponentTreeHolder> asyncRangeIterator = new RecyclerBinder.ComponentAsyncInitRangeIterator(mAllHoldersList, initialComponentPosition, 4, binder.mTraverseLayoutBackwards);
        assertIterator(asyncRangeIterator, new java.util.LinkedList(Arrays.asList(components[7], components[6], components[5], components[4])));
    }

    @Test
    public void testComponentAsyncInitRangeIteratorForwardFewerItems() {
        int totalCount = 10;
        final RenderInfo[] components = new RenderInfo[totalCount];
        for (int i = 0; i < totalCount; i++) {
            if (((i == 4) || (i == 6)) || (i == 9)) {
                components[i] = ComponentRenderInfo.create().component(Mockito.mock(Component.class)).build();
            } else {
                components[i] = ViewRenderInfo.create().viewCreator(RecyclerBinderAsyncInitRangeIteratorTest.VIEW_CREATOR).viewBinder(new SimpleViewBinder()).build();
            }
        }
        mRecyclerBinder.insertRangeAt(0, new ArrayList(Arrays.asList(components)));
        mAllHoldersList.get(6).computeLayoutSync(mComponentContext, 0, 0, new Size());
        final int initialComponentPosition = RecyclerBinder.findInitialComponentPosition(mAllHoldersList, mRecyclerBinder.mTraverseLayoutBackwards);
        assertThat(initialComponentPosition).isEqualTo(4);
        final Iterator<ComponentTreeHolder> asyncRangeIterator = new RecyclerBinder.ComponentAsyncInitRangeIterator(mAllHoldersList, initialComponentPosition, 4, mRecyclerBinder.mTraverseLayoutBackwards);
        assertIterator(asyncRangeIterator, new java.util.LinkedList(Arrays.asList(components[9])));
    }
}

