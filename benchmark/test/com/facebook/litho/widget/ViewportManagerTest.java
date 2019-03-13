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


import ViewportInfo.ViewportChanged;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


/**
 * Tests for {@link ViewportManager}
 */
@RunWith(ComponentsTestRunner.class)
public class ViewportManagerTest {
    private LayoutInfo mLayoutInfo;

    private ViewportChanged mViewportChangedListener;

    @Test
    public void testOnViewportChangedWhileScrolling() {
        ViewportManager viewportManager = getViewportManager(0, 0);
        setVisibleItemPositionInMockedLayoutManager(0, 5);
        setFullyVisibleItemPositionInMockedLayoutManager(1, 4);
        setTotalItemInMockedLayoutManager(20);
        viewportManager.setShouldUpdate(false);
        viewportManager.onViewportChanged(State.SCROLLING);
        Mockito.verify(mViewportChangedListener).viewportChanged(0, 5, 1, 4, State.SCROLLING);
    }

    @Test
    public void testOnViewportChangedWhileScrollingWithNoItemsFullyVisible() {
        ViewportManager viewportManager = getViewportManager(0, 0);
        // The second and third items are visible partially but neither is fully visible
        setVisibleItemPositionInMockedLayoutManager(1, 2);
        setFullyVisibleItemPositionInMockedLayoutManager((-1), (-1));
        setTotalItemInMockedLayoutManager(20);
        viewportManager.setShouldUpdate(true);
        viewportManager.onViewportChanged(State.SCROLLING);
        Mockito.verify(mViewportChangedListener).viewportChanged(1, 2, (-1), (-1), State.SCROLLING);
    }

    @Test
    public void testOnViewportChangedWithoutScrolling() {
        ViewportManager viewportManager = getViewportManager(5, 20);
        setVisibleItemPositionInMockedLayoutManager(5, 20);
        setFullyVisibleItemPositionInMockedLayoutManager(7, 18);
        setTotalItemInMockedLayoutManager(20);
        viewportManager.setShouldUpdate(true);
        viewportManager.onViewportChanged(State.DATA_CHANGES);
        Mockito.verify(mViewportChangedListener).viewportChanged(5, 20, 7, 18, State.DATA_CHANGES);
    }

    @Test
    public void testNoViewportChangedWithScrolling() {
        setFullyVisibleItemPositionInMockedLayoutManager(7, 9);
        setTotalItemInMockedLayoutManager(13);
        ViewportManager viewportManager = getViewportManager(5, 10);
        setVisibleItemPositionInMockedLayoutManager(5, 10);
        setFullyVisibleItemPositionInMockedLayoutManager(7, 9);
        setTotalItemInMockedLayoutManager(13);
        viewportManager.setShouldUpdate(false);
        viewportManager.onViewportChanged(State.SCROLLING);
        Mockito.verifyZeroInteractions(mViewportChangedListener);
    }

    @Test
    public void testTotalItemChangedWhileVisiblePositionsRemainTheSame() {
        setTotalItemInMockedLayoutManager(13);
        ViewportManager viewportManager = getViewportManager(5, 10);
        setVisibleItemPositionInMockedLayoutManager(5, 10);
        setFullyVisibleItemPositionInMockedLayoutManager(7, 9);
        setTotalItemInMockedLayoutManager(12);
        viewportManager.setShouldUpdate(true);
        viewportManager.onViewportChanged(State.DATA_CHANGES);
        Mockito.verify(mViewportChangedListener).viewportChanged(5, 10, 7, 9, State.DATA_CHANGES);
    }

    @Test
    public void testTotalItemChangedWhileNoItemsFullyVisible() {
        setTotalItemInMockedLayoutManager(13);
        ViewportManager viewportManager = getViewportManager(5, 6);
        // The seventh and eighth items are visible partially but neither is fully visible
        setVisibleItemPositionInMockedLayoutManager(6, 7);
        setFullyVisibleItemPositionInMockedLayoutManager((-1), (-1));
        setTotalItemInMockedLayoutManager(12);
        viewportManager.setShouldUpdate(false);
        viewportManager.onViewportChanged(State.SCROLLING);
        Mockito.verify(mViewportChangedListener).viewportChanged(6, 7, (-1), (-1), State.SCROLLING);
    }

    @Test
    public void testInsertAffectsVisibleRange() {
        ViewportManager viewportManager = getViewportManager(5, 10);
        // fully above viewport
        assertThat(viewportManager.insertAffectsVisibleRange(1, 2, 5)).isTrue();
        // last insert within viewport
        assertThat(viewportManager.insertAffectsVisibleRange(1, 6, 5)).isTrue();
        // insert positions fully cover viewport
        assertThat(viewportManager.insertAffectsVisibleRange(1, 20, 5)).isTrue();
        // position within viewport
        assertThat(viewportManager.insertAffectsVisibleRange(5, 1, 5)).isTrue();
        assertThat(viewportManager.insertAffectsVisibleRange(7, 2, 5)).isTrue();
        // position within extended viewport
        assertThat(viewportManager.insertAffectsVisibleRange(11, 2, 7)).isTrue();
        // fully below viewport
        assertThat(viewportManager.insertAffectsVisibleRange(11, 2, 6)).isFalse();
        assertThat(viewportManager.insertAffectsVisibleRange(18, 2, 5)).isFalse();
    }

    @Test
    public void testUpdateAffectsVisibleRange() {
        ViewportManager viewportManager = getViewportManager(5, 10);
        // fully above viewport
        assertThat(viewportManager.updateAffectsVisibleRange(1, 2)).isFalse();
        // last update within viewport
        assertThat(viewportManager.updateAffectsVisibleRange(1, 6)).isTrue();
        // update positions fully cover viewport
        assertThat(viewportManager.updateAffectsVisibleRange(1, 20)).isTrue();
        // position within viewport
        assertThat(viewportManager.updateAffectsVisibleRange(5, 1)).isTrue();
        assertThat(viewportManager.updateAffectsVisibleRange(7, 2)).isTrue();
        // fully below viewport
        assertThat(viewportManager.updateAffectsVisibleRange(11, 2)).isFalse();
        assertThat(viewportManager.updateAffectsVisibleRange(18, 2)).isFalse();
    }

    @Test
    public void testMoveAffectsVisibleRange() {
        ViewportManager viewportManager = getViewportManager(5, 10);
        // either is within viewport
        assertThat(viewportManager.moveAffectsVisibleRange(12, 9, 6)).isTrue();
        assertThat(viewportManager.moveAffectsVisibleRange(9, 12, 6)).isTrue();
        // both is within viewport
        assertThat(viewportManager.moveAffectsVisibleRange(6, 7, 6)).isTrue();
        // neither is within viewport
        assertThat(viewportManager.moveAffectsVisibleRange(11, 13, 6)).isFalse();
        assertThat(viewportManager.moveAffectsVisibleRange(1, 3, 6)).isFalse();
    }

    @Test
    public void testRemoveAffectsVisibleRange() {
        ViewportManager viewportManager = getViewportManager(5, 10);
        // fully above viewport
        assertThat(viewportManager.removeAffectsVisibleRange(1, 2)).isTrue();
        // last remove within viewport
        assertThat(viewportManager.removeAffectsVisibleRange(1, 6)).isTrue();
        // remove positions fully cover viewport
        assertThat(viewportManager.removeAffectsVisibleRange(1, 20)).isTrue();
        // position within viewport
        assertThat(viewportManager.removeAffectsVisibleRange(5, 1)).isTrue();
        assertThat(viewportManager.removeAffectsVisibleRange(7, 2)).isTrue();
        // fully below viewport
        assertThat(viewportManager.removeAffectsVisibleRange(11, 2)).isFalse();
        assertThat(viewportManager.removeAffectsVisibleRange(18, 2)).isFalse();
    }

    @Test
    public void testChangeSetIsVisibleForInitialisation() {
        ViewportManager viewportManager = getViewportManager((-1), 2);
        assertThat(viewportManager.insertAffectsVisibleRange(6, 2, (-1))).isTrue();
        assertThat(viewportManager.updateAffectsVisibleRange(6, 2)).isTrue();
        assertThat(viewportManager.moveAffectsVisibleRange(6, 2, 10)).isTrue();
        assertThat(viewportManager.removeAffectsVisibleRange(6, 2)).isTrue();
        viewportManager = getViewportManager(1, (-1));
        assertThat(viewportManager.insertAffectsVisibleRange(6, 2, 10)).isTrue();
        assertThat(viewportManager.updateAffectsVisibleRange(6, 2)).isTrue();
        assertThat(viewportManager.moveAffectsVisibleRange(6, 2, (-1))).isTrue();
        assertThat(viewportManager.removeAffectsVisibleRange(6, 2)).isTrue();
    }
}

