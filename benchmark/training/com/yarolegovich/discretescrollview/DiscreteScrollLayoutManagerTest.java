package com.yarolegovich.discretescrollview;


import DSVOrientation.Helper;
import DiscreteScrollLayoutManager.ScrollStateListener;
import RecyclerView.Recycler;
import RecyclerView.SCROLL_STATE_DRAGGING;
import RecyclerView.SCROLL_STATE_IDLE;
import RecyclerView.SCROLL_STATE_SETTLING;
import RecyclerView.SmoothScroller;
import RecyclerView.State;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.yarolegovich.discretescrollview.stub.StubRecyclerViewProxy;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;


/**
 * Created by yarolegovich on 10/25/17.
 */
public abstract class DiscreteScrollLayoutManagerTest {
    private static final int RECYCLER_WIDTH = 400;

    private static final int RECYCLER_HEIGHT = 600;

    private static final int CHILD_WIDTH = 130;

    private static final int CHILD_HEIGHT = 600;

    private static final int ADAPTER_ITEM_COUNT = 10;

    private DiscreteScrollLayoutManager layoutManager;

    private ScrollStateListener mockScrollStateListener;

    private StubRecyclerViewProxy stubRecyclerViewProxy;

    private Helper stubOrientationHelper;

    private State stubState;

    @Test
    public void onLayoutChildren_noItems_removesViewsAndResetsState() {
        layoutManager.pendingScroll = 200;
        layoutManager.scrolled = 1000;
        layoutManager.pendingPosition = 1;
        layoutManager.currentPosition = 2;
        Mockito.when(stubState.getItemCount()).thenReturn(0);
        layoutManager.onLayoutChildren(null, stubState);
        Mockito.verify(stubRecyclerViewProxy).removeAndRecycleAllViews(ArgumentMatchers.nullable(Recycler.class));
        Assert.assertThat(layoutManager.pendingScroll, is(0));
        Assert.assertThat(layoutManager.scrolled, is(0));
        Assert.assertThat(layoutManager.pendingPosition, is(DiscreteScrollLayoutManager.NO_POSITION));
        Assert.assertThat(layoutManager.currentPosition, is(DiscreteScrollLayoutManager.NO_POSITION));
    }

    @Test
    public void onLayoutChildren_whenFirstOrEmptyLayout_childDimensionsAreInitialized() {
        layoutManager.childHalfWidth = 0;
        layoutManager.childHalfHeight = 0;
        Mockito.when(stubState.getItemCount()).thenReturn(DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT);
        layoutManager.onLayoutChildren(null, stubState);
        Assert.assertThat(layoutManager.childHalfWidth, is(((DiscreteScrollLayoutManagerTest.CHILD_WIDTH) / 2)));
        Assert.assertThat(layoutManager.childHalfHeight, is(((DiscreteScrollLayoutManagerTest.CHILD_HEIGHT) / 2)));
    }

    @Test
    public void onLayoutChildren_notFirstOrEmptyLayout_childDimensionsAreNotInitialized() {
        stubRecyclerViewProxy.addChildren(5, 0);
        layoutManager.onLayoutChildren(null, stubState);
        Assert.assertThat(layoutManager.childHalfWidth, is(0));
        Assert.assertThat(layoutManager.childHalfHeight, is(0));
    }

    @Test
    public void onLayoutChildren_multipleCallsInLayoutPhase_isFirstOrEmptyLayoutFlagNotCleared() {
        Mockito.when(stubState.getItemCount()).thenReturn(DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT);
        layoutManager.onLayoutChildren(null, stubState);
        stubRecyclerViewProxy.addChildren(5, 0);
        layoutManager.onLayoutChildren(null, stubState);
        Assert.assertTrue(layoutManager.isFirstOrEmptyLayout);
    }

    @Test
    public void onLayoutCompleted_isFirstOrEmptyLayoutFlagSet_scrollStateListenerIsNotified() {
        layoutManager.isFirstOrEmptyLayout = true;
        layoutManager.onLayoutCompleted(stubState);
        Mockito.verify(mockScrollStateListener).onCurrentViewFirstLayout();
    }

    @Test
    public void onLayoutCompleted_isFirstOrEmptyLayoutFlagSet_theFlagIsCleared() {
        layoutManager.isFirstOrEmptyLayout = true;
        layoutManager.onLayoutCompleted(stubState);
        Assert.assertFalse(layoutManager.isFirstOrEmptyLayout);
    }

    @Test
    public void initChildDimensions_offscreenItemsNotSet_noExtraLayoutSpace() {
        layoutManager.extraLayoutSpace = 1000;
        layoutManager.initChildDimensions(null);
        Assert.assertThat(layoutManager.extraLayoutSpace, is(0));
    }

    @Test
    public void initChildDimensions_offscreenItemsSet_extraLayoutSpaceIsCalculated() {
        layoutManager.extraLayoutSpace = 0;
        layoutManager.setOffscreenItems(5);
        layoutManager.initChildDimensions(null);
        Assert.assertThat(layoutManager.extraLayoutSpace, is(not(0)));
    }

    @Test
    public void updateRecyclerDimensions_recyclerCenterIsInitialized() {
        layoutManager.recyclerCenter.set(0, 0);
        layoutManager.updateRecyclerDimensions(stubState);
        Assert.assertThat(layoutManager.recyclerCenter.x, is(((DiscreteScrollLayoutManagerTest.RECYCLER_WIDTH) / 2)));
        Assert.assertThat(layoutManager.recyclerCenter.y, is(((DiscreteScrollLayoutManagerTest.RECYCLER_HEIGHT) / 2)));
    }

    @Test
    public void cacheAndDetachAttachedViews_allRecyclerChildrenArePutToCache() {
        final int childCount = 6;
        stubRecyclerViewProxy.addChildren(6, 0);
        layoutManager.cacheAndDetachAttachedViews();
        Assert.assertThat(layoutManager.detachedCache.size(), is(childCount));
        for (int i = 0; i < childCount; i++) {
            int position = stubRecyclerViewProxy.getPosition(stubRecyclerViewProxy.getChildAt(i));
            Assert.assertNotNull(layoutManager.detachedCache.get(position));
        }
    }

    @Test
    public void cacheAndDetachAttachedViews_allRecyclerChildrenAreDetached() {
        final int childCount = 5;
        stubRecyclerViewProxy.addChildren(childCount, 0);
        layoutManager.cacheAndDetachAttachedViews();
        for (int i = 0; i < childCount; i++) {
            Mockito.verify(stubRecyclerViewProxy).detachView(stubRecyclerViewProxy.getChildAt(i));
        }
    }

    @Test
    public void recycleDetachedViewsAndClearCache_cacheIsClearedAndViewsAreRecycled() {
        List<View> views = Arrays.asList(Mockito.mock(View.class), Mockito.mock(View.class), Mockito.mock(View.class));
        for (int i = 0; i < (views.size()); i++)
            layoutManager.detachedCache.put(i, views.get(i));

        layoutManager.recycleDetachedViewsAndClearCache(null);
        Assert.assertThat(layoutManager.detachedCache.size(), is(0));
        Mockito.verify(stubRecyclerViewProxy, Mockito.times(views.size())).recycleView(MockitoHamcrest.argThat(isIn(views)), ArgumentMatchers.nullable(Recycler.class));
    }

    @Test
    public void onItemsAdded_afterCurrentPosition_currentPositionIsUnchanged() {
        final int addedItems = 3;
        final int initialCurrent = (DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) / 2;
        layoutManager.currentPosition = initialCurrent;
        stubRecyclerViewProxy.setAdapterItemCount(((DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) + addedItems));
        layoutManager.onItemsAdded(null, (initialCurrent + 1), addedItems);
        Assert.assertThat(layoutManager.currentPosition, is(initialCurrent));
    }

    @Test
    public void onItemsAdded_beforeCurrentPosition_currentIsShiftedByAmountOfAddedItems() {
        final int addedItems = 3;
        final int initialCurrent = (DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) / 2;
        layoutManager.currentPosition = initialCurrent;
        stubRecyclerViewProxy.setAdapterItemCount(((DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) + addedItems));
        layoutManager.onItemsAdded(null, (initialCurrent - 1), addedItems);
        Assert.assertThat(layoutManager.currentPosition, is((initialCurrent + addedItems)));
    }

    @Test
    public void onItemsRemoved_afterCurrentPosition_currentIsUnchanged() {
        final int initialCurrent = (DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) / 2;
        final int removedItems = initialCurrent / 2;
        layoutManager.currentPosition = initialCurrent;
        stubRecyclerViewProxy.setAdapterItemCount(((DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) - removedItems));
        layoutManager.onItemsRemoved(null, (initialCurrent + 1), removedItems);
        Assert.assertThat(layoutManager.currentPosition, is(initialCurrent));
    }

    @Test
    public void onItemsRemoved_beforeCurrentPosition_currentIsShiftedByAmountRemoved() {
        final int initialCurrent = (DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) / 2;
        final int removedItems = initialCurrent / 2;
        layoutManager.currentPosition = initialCurrent;
        stubRecyclerViewProxy.setAdapterItemCount(((DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) - removedItems));
        layoutManager.onItemsRemoved(null, 0, removedItems);
        Assert.assertThat(layoutManager.currentPosition, is((initialCurrent - removedItems)));
    }

    @Test
    public void onItemsRemoved_rangeWhichContainsCurrent_currentIsReset() {
        final int initialCurrent = (DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) / 2;
        layoutManager.currentPosition = initialCurrent;
        layoutManager.onItemsRemoved(null, (initialCurrent - 1), 3);
        Assert.assertThat(layoutManager.currentPosition, is(0));
    }

    @Test
    public void onItemsChanged_removedItemWhichWasCurrent_currentRemainsInValidRange() {
        layoutManager.currentPosition = (DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) - 1;
        stubRecyclerViewProxy.setAdapterItemCount(((DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) - 3));
        layoutManager.onItemsChanged(null);
        Assert.assertThat(layoutManager.currentPosition, is(((stubRecyclerViewProxy.getItemCount()) - 1)));
    }

    @Test
    public void scrollBy_noChildren_noScrollPerformed() {
        Mockito.doReturn(0).when(layoutManager).getChildCount();
        int scrolled = layoutManager.scrollBy(1000, null);
        Assert.assertThat(scrolled, is(0));
    }

    @Test
    public void scrollBy_moreScrollRequestedThanCanPerform_scrollsByAllAvailableAmount() {
        final int requested = 1000;
        final int maxAvailable = 333;
        prepareStubsForScrollBy(maxAvailable, 3, false);
        int scrolled = layoutManager.scrollBy(requested, null);
        Assert.assertThat(scrolled, both(not(equalTo(requested))).and(is(equalTo(maxAvailable))));
    }

    @Test
    public void scrollBy_offsetsChildrenByNegativeScrollDelta() {
        final int requested = 1000;
        prepareStubsForScrollBy(requested, 3, false);
        int scrolled = layoutManager.scrollBy(requested, null);
        Mockito.verify(stubOrientationHelper).offsetChildren((-scrolled), stubRecyclerViewProxy);
    }

    @Test
    public void scrollBy_noNewViewBecameVisible_fillIsNotCalled() {
        prepareStubsForScrollBy(1000, 10, false);
        layoutManager.scrollBy(200, null);
        Mockito.verify(layoutManager, Mockito.never()).fill(ArgumentMatchers.any(Recycler.class));
    }

    @Test
    public void scrollBy_newViewBecomesVisible_fillIsCalled() {
        final int childCount = 5;
        stubRecyclerViewProxy.addChildren(childCount, 0);
        prepareStubsForScrollBy(1000, childCount, true);
        layoutManager.scrollBy(200, null);
        Mockito.verify(layoutManager).fill(ArgumentMatchers.nullable(Recycler.class));
    }

    @Test
    public void scrollBy_hasPendingScroll_pendingScrollDecreasedByScrolledAmount() {
        final int initialPendingScroll = 1000;
        layoutManager.pendingScroll = initialPendingScroll;
        prepareStubsForScrollBy(300, 3, false);
        int scrolled = layoutManager.scrollBy(200, null);
        Assert.assertThat(layoutManager.pendingScroll, is((initialPendingScroll - scrolled)));
    }

    @Test
    public void scrollBy_scrollIsAccumulated() {
        int initialScrolled = layoutManager.scrolled;
        prepareStubsForScrollBy(300, 3, false);
        int scrolled = layoutManager.scrollBy(100, null);
        Assert.assertThat(layoutManager.scrolled, is((initialScrolled + scrolled)));
    }

    @Test
    public void scrollBy_scrolledMoreThanZero_listenerIsNotifiedAboutScroll() {
        prepareStubsForScrollBy(300, 3, false);
        int scrolled = layoutManager.scrollBy(200, null);
        Assert.assertThat(scrolled, is(greaterThan(0)));
        Mockito.verify(mockScrollStateListener).onScroll(ArgumentMatchers.anyFloat());
    }

    @Test
    public void scrollBy_scrolledByZero_listenerIsNotNotifiedAboutScroll() {
        prepareStubsForScrollBy(0, 2, false);
        int scrolled = layoutManager.scrollBy(300, null);
        Assert.assertThat(scrolled, is(0));
        Mockito.verify(mockScrollStateListener, Mockito.never()).onScroll(ArgumentMatchers.anyFloat());
    }

    @Test
    public void scrollBy_triesToScrollToTheItemBeforeFirst_onBoundReachedIsTrue() {
        layoutManager.currentPosition = 0;
        stubRecyclerViewProxy.addChildren(5, 0);
        layoutManager.scrollBy((-100), null);
        Mockito.verify(mockScrollStateListener).onIsBoundReachedFlagChange(true);
    }

    @Test
    public void scrollBy_triesToScrollToTheItemAfterLast_onBoundReachedIsTrue() {
        layoutManager.currentPosition = (DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) - 1;
        stubRecyclerViewProxy.addChildren(5, ((DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) - 5));
        layoutManager.scrollBy(100, null);
        Mockito.verify(mockScrollStateListener).onIsBoundReachedFlagChange(true);
    }

    @Test
    public void scrollBy_scrollsToAllowedElement_onBoundReachedIsFalse() {
        layoutManager.currentPosition = (DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) / 2;
        stubRecyclerViewProxy.addChildren(5, 0);
        layoutManager.scrollBy(100, null);
        Mockito.verify(mockScrollStateListener).onIsBoundReachedFlagChange(false);
    }

    @Test
    public void onScrollStateChanged_dragStartedWhenWasIdle_listenerNotifiedAboutScrollStart() {
        layoutManager.currentScrollState = RecyclerView.SCROLL_STATE_IDLE;
        layoutManager.onScrollStateChanged(SCROLL_STATE_DRAGGING);
        Mockito.verify(mockScrollStateListener).onScrollStart();
    }

    @Test
    public void onScrollStateChanged_settlingStartedWhenWasIdle_listenerNotifiedAboutScrollStart() {
        layoutManager.currentScrollState = RecyclerView.SCROLL_STATE_IDLE;
        layoutManager.onScrollStateChanged(SCROLL_STATE_SETTLING);
        Mockito.verify(mockScrollStateListener).onScrollStart();
    }

    @Test
    public void onScrollStateChanged_newState_layoutManagerUpdatesItsState() {
        final int newState = RecyclerView.SCROLL_STATE_DRAGGING;
        layoutManager.currentScrollState = RecyclerView.SCROLL_STATE_IDLE;
        Assert.assertThat(layoutManager.currentScrollState, is(not(newState)));
        layoutManager.onScrollStateChanged(newState);
        Assert.assertThat(layoutManager.currentScrollState, is(newState));
    }

    @Test
    public void onScrollStateChanged_scrolledEnoughToChangeCurrent_listenerNotifiedAboutScrollEnd() {
        layoutManager.currentScrollState = RecyclerView.SCROLL_STATE_DRAGGING;
        layoutManager.scrolled = layoutManager.scrollToChangeCurrent;
        layoutManager.onScrollStateChanged(SCROLL_STATE_IDLE);
        Mockito.verify(mockScrollStateListener).onScrollEnd();
    }

    @Test
    public void onScrollStateChanged_scrolledNotEnoughToChangeCurrent_listenerNotNotifiedAboutScrollEnd() {
        layoutManager.currentScrollState = RecyclerView.SCROLL_STATE_DRAGGING;
        layoutManager.scrollToChangeCurrent = stubOrientationHelper.getDistanceToChangeCurrent(DiscreteScrollLayoutManagerTest.CHILD_WIDTH, DiscreteScrollLayoutManagerTest.CHILD_HEIGHT);
        layoutManager.scrolled = (layoutManager.scrollToChangeCurrent) / 2;
        layoutManager.onScrollStateChanged(SCROLL_STATE_IDLE);
        Mockito.verify(mockScrollStateListener, Mockito.never()).onScrollEnd();
    }

    @Test
    public void onScrollStateChanged_draggedLessThanScrollToSnapToAnotherItem_settlesToCurrentPosition() {
        layoutManager.currentScrollState = RecyclerView.SCROLL_STATE_DRAGGING;
        layoutManager.currentPosition = (DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) / 2;
        layoutManager.scrollToChangeCurrent = stubOrientationHelper.getDistanceToChangeCurrent(DiscreteScrollLayoutManagerTest.CHILD_WIDTH, DiscreteScrollLayoutManagerTest.CHILD_HEIGHT);
        layoutManager.pendingScroll = 0;
        layoutManager.scrolled = ((int) ((layoutManager.scrollToChangeCurrent) * ((DiscreteScrollLayoutManager.SCROLL_TO_SNAP_TO_ANOTHER_ITEM) - 0.01F)));
        layoutManager.onScrollStateChanged(SCROLL_STATE_IDLE);
        Assert.assertThat(layoutManager.pendingScroll, is((-(layoutManager.scrolled))));
        Mockito.verify(stubRecyclerViewProxy).startSmoothScroll(ArgumentMatchers.any(SmoothScroller.class));
    }

    @Test
    public void onScrollStateChanged_draggedMoreThanOrScrollToSnapToAnotherItem_settlesToClosestItem() {
        layoutManager.currentScrollState = RecyclerView.SCROLL_STATE_DRAGGING;
        layoutManager.currentPosition = (DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) / 2;
        layoutManager.scrollToChangeCurrent = stubOrientationHelper.getDistanceToChangeCurrent(DiscreteScrollLayoutManagerTest.CHILD_WIDTH, DiscreteScrollLayoutManagerTest.CHILD_HEIGHT);
        layoutManager.pendingScroll = 0;
        layoutManager.scrolled = ((int) ((layoutManager.scrollToChangeCurrent) * (DiscreteScrollLayoutManager.SCROLL_TO_SNAP_TO_ANOTHER_ITEM)));
        int scrollLeftToAnotherItem = (layoutManager.scrollToChangeCurrent) - (layoutManager.scrolled);
        layoutManager.onScrollStateChanged(SCROLL_STATE_IDLE);
        Assert.assertThat(layoutManager.pendingScroll, is(scrollLeftToAnotherItem));
        Mockito.verify(stubRecyclerViewProxy).startSmoothScroll(ArgumentMatchers.any(SmoothScroller.class));
    }

    @Test
    public void onScrollStateChanged_whenSettlingDragIsStarted_settlingStops() {
        layoutManager.currentScrollState = RecyclerView.SCROLL_STATE_SETTLING;
        layoutManager.pendingScroll = 1000;
        layoutManager.onScrollStateChanged(SCROLL_STATE_DRAGGING);
        Assert.assertThat(layoutManager.pendingScroll, is(0));
    }

    @Test
    public void onScrollStateChanged_whenSettlingDragIsStarted_closestPositionBecomesCurrent() {
        final int initialPosition = 5;
        layoutManager.currentScrollState = RecyclerView.SCROLL_STATE_SETTLING;
        layoutManager.scrollToChangeCurrent = stubOrientationHelper.getDistanceToChangeCurrent(DiscreteScrollLayoutManagerTest.CHILD_WIDTH, DiscreteScrollLayoutManagerTest.CHILD_HEIGHT);
        final int scrolled = ((int) ((layoutManager.scrollToChangeCurrent) * (DiscreteScrollLayoutManager.SCROLL_TO_SNAP_TO_ANOTHER_ITEM)));
        layoutManager.scrolled = scrolled;
        layoutManager.currentPosition = initialPosition;
        layoutManager.onScrollStateChanged(SCROLL_STATE_DRAGGING);
        Assert.assertThat(layoutManager.currentPosition, is((initialPosition + 1)));
        Assert.assertThat(layoutManager.scrolled, is((scrolled - (layoutManager.scrollToChangeCurrent))));
    }

    @Test
    public void onFling_velocitiesWithDifferentSignsOnDifferentAxis_correctFlingDirection() {
        final int velocityX = 100;
        final int velocityY = -100;
        final int velocityToUse = stubOrientationHelper.getFlingVelocity(velocityX, velocityY);
        Direction direction = Direction.fromDelta(velocityToUse);
        layoutManager.currentPosition = (DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) / 2;
        layoutManager.scrollToChangeCurrent = stubOrientationHelper.getDistanceToChangeCurrent(DiscreteScrollLayoutManagerTest.CHILD_WIDTH, DiscreteScrollLayoutManagerTest.CHILD_HEIGHT);
        layoutManager.pendingScroll = 0;
        layoutManager.onFling(velocityX, velocityY);
        Assert.assertThat(layoutManager.pendingScroll, is(direction.applyTo(layoutManager.scrollToChangeCurrent)));
    }

    @Test
    public void onFling_toTheOppositeToScrollDirection_returnsToPosition() {
        layoutManager.scrollToChangeCurrent = stubOrientationHelper.getDistanceToChangeCurrent(DiscreteScrollLayoutManagerTest.CHILD_WIDTH, DiscreteScrollLayoutManagerTest.CHILD_HEIGHT);
        int scrolled = (layoutManager.scrollToChangeCurrent) / 2;
        layoutManager.pendingScroll = 0;
        layoutManager.scrolled = scrolled;
        layoutManager.onFling((-scrolled), (-scrolled));
        Assert.assertThat(layoutManager.pendingScroll, is((-scrolled)));
    }

    @Test
    public void onFling_toTheSameDirectionAsScrolled_changesPosition() {
        layoutManager.scrollToChangeCurrent = stubOrientationHelper.getDistanceToChangeCurrent(DiscreteScrollLayoutManagerTest.CHILD_WIDTH, DiscreteScrollLayoutManagerTest.CHILD_HEIGHT);
        int scrolled = (layoutManager.scrollToChangeCurrent) / 3;
        int leftToScroll = (layoutManager.scrollToChangeCurrent) - scrolled;
        layoutManager.pendingScroll = 0;
        layoutManager.scrolled = scrolled;
        layoutManager.onFling(scrolled, scrolled);
        Assert.assertThat(layoutManager.pendingScroll, is(leftToScroll));
    }

    @Test
    public void onFling_toItemBeforeTheFirst_isImpossible() {
        layoutManager.currentPosition = 0;
        layoutManager.onFling((-100), (-100));
        Assert.assertThat(layoutManager.pendingScroll, is(0));
    }

    @Test
    public void onFling_toItemAfterTheLast_isImpossible() {
        layoutManager.currentPosition = (DiscreteScrollLayoutManagerTest.ADAPTER_ITEM_COUNT) - 1;
        layoutManager.onFling(100, 100);
        Assert.assertThat(layoutManager.pendingScroll, is(0));
    }
}

