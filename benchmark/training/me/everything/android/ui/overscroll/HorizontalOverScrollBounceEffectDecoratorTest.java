package me.everything.android.ui.overscroll;


import View.OVER_SCROLL_ALWAYS;
import android.view.MotionEvent;
import android.view.View;
import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author amitd
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class HorizontalOverScrollBounceEffectDecoratorTest {
    View mView;

    IOverScrollDecoratorAdapter mViewAdapter;

    IOverScrollStateListener mStateListener;

    IOverScrollUpdateListener mUpdateListener;

    @Test
    public void detach_decoratorIsAttached_detachFromView() throws Exception {
        // Arrange
        HorizontalOverScrollBounceEffectDecorator uut = new HorizontalOverScrollBounceEffectDecorator(mViewAdapter);
        // Act
        uut.detach();
        // Assert
        Mockito.verify(mView).setOnTouchListener(ArgumentMatchers.eq(((View.OnTouchListener) (null))));
        Mockito.verify(mView).setOverScrollMode(OVER_SCROLL_ALWAYS);
    }

    @Test
    public void detach_overScrollInEffect_detachFromView() throws Exception {
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(true);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(false);
        HorizontalOverScrollBounceEffectDecorator uut = getUUT();
        uut.onTouch(mView, createShortRightMoveEvent());
        // Act
        uut.detach();
        // Assert
        Mockito.verify(mView).setOnTouchListener(ArgumentMatchers.eq(((View.OnTouchListener) (null))));
        Mockito.verify(mView).setOverScrollMode(OVER_SCROLL_ALWAYS);
    }

    /* Move-action event */
    @Test
    public void onTouchMoveAction_notInViewEnds_ignoreTouchEvent() throws Exception {
        // Arrange
        MotionEvent event = createShortRightMoveEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(false);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(false);
        HorizontalOverScrollBounceEffectDecorator uut = getUUT();
        // Act
        boolean ret = uut.onTouch(mView, event);
        // Assert
        Mockito.verify(mView, Mockito.never()).setTranslationX(ArgumentMatchers.anyFloat());
        Mockito.verify(mView, Mockito.never()).setTranslationY(ArgumentMatchers.anyFloat());
        Assert.assertFalse(ret);
        Assert.assertEquals(STATE_IDLE, uut.getCurrentState());
        Mockito.verify(mStateListener, Mockito.never()).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verify(mUpdateListener, Mockito.never()).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyFloat());
    }

    @Test
    public void onTouchMoveAction_dragRightInLeftEnd_overscrollRight() throws Exception {
        // Arrange
        MotionEvent event = createShortRightMoveEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(true);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(false);
        HorizontalOverScrollBounceEffectDecorator uut = getUUT();
        // Act
        final boolean ret = uut.onTouch(mView, event);
        // Assert
        final float expectedTransX = ((event.getX()) - (event.getHistoricalX(0))) / (HorizontalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD);
        Mockito.verify(mView).setTranslationX(expectedTransX);
        Mockito.verify(mView, Mockito.never()).setTranslationY(ArgumentMatchers.anyFloat());
        Assert.assertTrue(ret);
        Assert.assertEquals(STATE_DRAG_START_SIDE, uut.getCurrentState());
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_IDLE), ArgumentMatchers.eq(STATE_DRAG_START_SIDE));
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_START_SIDE), ArgumentMatchers.eq(expectedTransX));
    }

    @Test
    public void onTouchMoveAction_dragLeftInRightEnd_overscrollLeft() throws Exception {
        // Arrange
        MotionEvent event = createShortLeftMoveEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(false);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(true);
        HorizontalOverScrollBounceEffectDecorator uut = getUUT();
        // Act
        final boolean ret = uut.onTouch(mView, event);
        // Assert
        final float expectedTransX = ((event.getX()) - (event.getHistoricalX(0))) / (HorizontalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD);
        Mockito.verify(mView).setTranslationX(expectedTransX);
        Mockito.verify(mView, Mockito.never()).setTranslationY(ArgumentMatchers.anyFloat());
        Assert.assertTrue(ret);
        Assert.assertEquals(STATE_DRAG_END_SIDE, uut.getCurrentState());
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_IDLE), ArgumentMatchers.eq(STATE_DRAG_END_SIDE));
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_END_SIDE), ArgumentMatchers.eq(expectedTransX));
    }

    @Test
    public void onTouchMoveAction_dragLeftInLeftEnd_ignoreTouchEvent() throws Exception {
        // Arrange
        MotionEvent event = createShortLeftMoveEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(true);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(false);
        HorizontalOverScrollBounceEffectDecorator uut = getUUT();
        // Act
        final boolean ret = uut.onTouch(mView, event);
        // Assert
        Mockito.verify(mView, Mockito.never()).setTranslationX(ArgumentMatchers.anyFloat());
        Mockito.verify(mView, Mockito.never()).setTranslationY(ArgumentMatchers.anyFloat());
        Assert.assertFalse(ret);
        Assert.assertEquals(STATE_IDLE, uut.getCurrentState());
        Mockito.verify(mStateListener, Mockito.never()).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verify(mUpdateListener, Mockito.never()).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyFloat());
    }

    @Test
    public void onTouchMoveAction_dragRightInRightEnd_ignoreTouchEvent() throws Exception {
        // Arrange
        MotionEvent event = createShortRightMoveEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(false);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(true);
        HorizontalOverScrollBounceEffectDecorator uut = getUUT();
        // Act
        boolean ret = uut.onTouch(mView, event);
        // Assert
        Mockito.verify(mView, Mockito.never()).setTranslationX(ArgumentMatchers.anyFloat());
        Mockito.verify(mView, Mockito.never()).setTranslationY(ArgumentMatchers.anyFloat());
        Assert.assertFalse(ret);
        Assert.assertEquals(STATE_IDLE, uut.getCurrentState());
        Mockito.verify(mStateListener, Mockito.never()).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verify(mUpdateListener, Mockito.never()).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyFloat());
    }

    @Test
    public void onTouchMoveAction_2ndRightDragInLeftEnd_overscrollRightFurther() throws Exception {
        // Arrange
        // Bring UUT to a right-overscroll state
        MotionEvent event1 = createShortRightMoveEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(true);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(false);
        HorizontalOverScrollBounceEffectDecorator uut = getUUT();
        uut.onTouch(mView, event1);
        Mockito.reset(mView);
        // Create 2nd right-drag event
        MotionEvent event2 = createLongRightMoveEvent();
        // Act
        final boolean ret = uut.onTouch(mView, event2);
        // Assert
        final float expectedTransX1 = ((event1.getX()) - (event1.getHistoricalX(0))) / (HorizontalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD);
        final float expectedTransX2 = ((event2.getX()) - (event2.getHistoricalX(0))) / (HorizontalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD);
        Mockito.verify(mView).setTranslationX(expectedTransX2);
        Mockito.verify(mView, Mockito.never()).setTranslationY(ArgumentMatchers.anyFloat());
        Assert.assertTrue(ret);
        Assert.assertEquals(STATE_DRAG_START_SIDE, uut.getCurrentState());
        // State-change listener called only once?
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_IDLE), ArgumentMatchers.eq(STATE_DRAG_START_SIDE));
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Update-listener called exactly twice?
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_START_SIDE), ArgumentMatchers.eq(expectedTransX1));
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_START_SIDE), ArgumentMatchers.eq(expectedTransX2));
        Mockito.verify(mUpdateListener, Mockito.times(2)).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyFloat());
    }

    @Test
    public void onTouchMoveAction_2ndLeftDragInRightEnd_overscrollLeftFurther() throws Exception {
        // Arrange
        // Bring UUT to a left-overscroll state
        MotionEvent event1 = createShortLeftMoveEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(false);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(true);
        HorizontalOverScrollBounceEffectDecorator uut = getUUT();
        uut.onTouch(mView, event1);
        Mockito.reset(mView);
        // Create 2nd left-drag event
        MotionEvent event2 = createLongLeftMoveEvent();
        // Act
        final boolean ret = uut.onTouch(mView, event2);
        // Assert
        final float expectedTransX1 = ((event1.getX()) - (event1.getHistoricalX(0))) / (HorizontalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD);
        final float expectedTransX2 = ((event2.getX()) - (event2.getHistoricalX(0))) / (HorizontalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD);
        Mockito.verify(mView).setTranslationX(expectedTransX2);
        Mockito.verify(mView, Mockito.never()).setTranslationY(ArgumentMatchers.anyFloat());
        Assert.assertTrue(ret);
        Assert.assertEquals(STATE_DRAG_END_SIDE, uut.getCurrentState());
        // State-change listener called only once?
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_IDLE), ArgumentMatchers.eq(STATE_DRAG_END_SIDE));
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Update-listener called exactly twice?
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_END_SIDE), ArgumentMatchers.eq(expectedTransX1));
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_END_SIDE), ArgumentMatchers.eq(expectedTransX2));
        Mockito.verify(mUpdateListener, Mockito.times(2)).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyFloat());
    }

    /**
     * When over-scroll has already started (to the right in this case) and suddenly the user changes
     * their mind and scrolls a bit in the other direction:
     * <br/>We expect the <b>touch to still be intercepted</b> in that case, and the <b>overscroll to
     * remain in effect</b>.
     */
    @Test
    public void onTouchMoveAction_dragLeftWhenRightOverscolled_continueOverscrollingLeft() throws Exception {
        // Arrange
        // In left & right tests we use equal ratios to avoid the effect's under-scroll handling
        final float touchDragRatioFwd = 3.0F;
        final float touchDragRatioBck = 3.0F;
        // Bring UUT to a right-overscroll state
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(true);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(false);
        HorizontalOverScrollBounceEffectDecorator uut = getUUT(touchDragRatioFwd, touchDragRatioBck);
        MotionEvent eventMoveRight = createLongRightMoveEvent();
        uut.onTouch(mView, eventMoveRight);
        Mockito.reset(mView);
        float startTransX = ((eventMoveRight.getX()) - (eventMoveRight.getHistoricalX(0))) / touchDragRatioFwd;
        Mockito.when(mView.getTranslationX()).thenReturn(startTransX);
        // Create the left-drag event
        MotionEvent eventMoveLeft = createShortLeftMoveEvent();
        // Act
        boolean ret = uut.onTouch(mView, eventMoveLeft);
        // Assert
        float expectedTransX = startTransX + (((eventMoveLeft.getX()) - (eventMoveLeft.getHistoricalX(0))) / touchDragRatioBck);
        Mockito.verify(mView).setTranslationX(expectedTransX);
        Mockito.verify(mView, Mockito.never()).setTranslationY(ArgumentMatchers.anyFloat());
        Assert.assertTrue(ret);
        Assert.assertEquals(STATE_DRAG_START_SIDE, uut.getCurrentState());
        // State-change listener called only once?
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_IDLE), ArgumentMatchers.eq(STATE_DRAG_START_SIDE));
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Update-listener called exactly twice?
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_START_SIDE), ArgumentMatchers.eq(startTransX));
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_START_SIDE), ArgumentMatchers.eq(expectedTransX));
        Mockito.verify(mUpdateListener, Mockito.times(2)).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyFloat());
    }

    /**
     * When over-scroll has already started (to the left in this case) and suddenly the user changes
     * their mind and scrolls a bit in the other direction:
     * <br/>We expect the <b>touch to still be intercepted</b> in that case, and the <b>overscroll to remain in effect</b>.
     */
    @Test
    public void onTouchMoveAction_dragRightWhenLeftOverscolled_continueOverscrollingRight() throws Exception {
        // Arrange
        // In left & right tests we use equal ratios to avoid the effect's under-scroll handling
        final float touchDragRatioFwd = 3.0F;
        final float touchDragRatioBck = 3.0F;
        // Bring UUT to a left-overscroll state
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(false);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(true);
        HorizontalOverScrollBounceEffectDecorator uut = getUUT(touchDragRatioFwd, touchDragRatioBck);
        MotionEvent eventMoveLeft = createLongLeftMoveEvent();
        uut.onTouch(mView, eventMoveLeft);
        Mockito.reset(mView);
        float startTransX = ((eventMoveLeft.getX()) - (eventMoveLeft.getHistoricalX(0))) / touchDragRatioFwd;
        Mockito.when(mView.getTranslationX()).thenReturn(startTransX);
        // Create the right-drag event
        MotionEvent eventMoveRight = createShortRightMoveEvent();
        // Act
        boolean ret = uut.onTouch(mView, eventMoveRight);
        // Assert
        float expectedTransX = startTransX + (((eventMoveRight.getX()) - (eventMoveRight.getHistoricalX(0))) / touchDragRatioBck);
        Mockito.verify(mView).setTranslationX(expectedTransX);
        Mockito.verify(mView, Mockito.never()).setTranslationY(ArgumentMatchers.anyFloat());
        Assert.assertTrue(ret);
        Assert.assertEquals(STATE_DRAG_END_SIDE, uut.getCurrentState());
        // State-change listener called only once?
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_IDLE), ArgumentMatchers.eq(STATE_DRAG_END_SIDE));
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Update-listener called exactly twice?
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_END_SIDE), ArgumentMatchers.eq(startTransX));
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_END_SIDE), ArgumentMatchers.eq(expectedTransX));
        Mockito.verify(mUpdateListener, Mockito.times(2)).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyFloat());
    }

    @Test
    public void onTouchMoveAction_undragWhenRightOverscrolled_endOverscrolling() throws Exception {
        // Arrange
        // In left & right tests we use equal ratios to avoid the effect's under-scroll handling
        final float touchDragRatioFwd = 3.0F;
        final float touchDragRatioBck = 3.0F;
        // Bring UUT to a right-overscroll state
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(true);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(false);
        HorizontalOverScrollBounceEffectDecorator uut = getUUT(touchDragRatioFwd, touchDragRatioBck);
        MotionEvent eventMoveRight = createLongRightMoveEvent();
        uut.onTouch(mView, eventMoveRight);
        Mockito.reset(mView);
        float startTransX = ((eventMoveRight.getX()) - (eventMoveRight.getHistoricalX(0))) / touchDragRatioFwd;
        Mockito.when(mView.getTranslationX()).thenReturn(startTransX);
        // Create the left-drag event
        MotionEvent eventMoveLeft = createLongLeftMoveEvent();
        // Act
        boolean ret = uut.onTouch(mView, eventMoveLeft);
        // Assert
        Mockito.verify(mView).setTranslationX(0);
        Mockito.verify(mView, Mockito.never()).setTranslationY(ArgumentMatchers.anyFloat());
        Assert.assertTrue(ret);
        Assert.assertEquals(STATE_IDLE, uut.getCurrentState());
        // State-change listener invoked to say drag-on and drag-off (idle).
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_IDLE), ArgumentMatchers.eq(STATE_DRAG_START_SIDE));
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_START_SIDE), ArgumentMatchers.eq(STATE_IDLE));
        Mockito.verify(mStateListener, Mockito.times(2)).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Update-listener called exactly twice?
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_START_SIDE), ArgumentMatchers.eq(startTransX));
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_START_SIDE), ArgumentMatchers.eq(0.0F));
        Mockito.verify(mUpdateListener, Mockito.times(2)).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyFloat());
    }

    @Test
    public void onTouchMoveAction_undragWhenLeftOverscrolled_endOverscrolling() throws Exception {
        // Arrange
        // In left & right tests we use equal ratios to avoid the effect's under-scroll handling
        final float touchDragRatioFwd = 3.0F;
        final float touchDragRatioBck = 3.0F;
        // Bring UUT to a left-overscroll state
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(false);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(true);
        HorizontalOverScrollBounceEffectDecorator uut = getUUT(touchDragRatioFwd, touchDragRatioBck);
        MotionEvent eventMoveLeft = createLongLeftMoveEvent();
        uut.onTouch(mView, eventMoveLeft);
        Mockito.reset(mView);
        float startTransX = ((eventMoveLeft.getX()) - (eventMoveLeft.getHistoricalX(0))) / touchDragRatioFwd;
        Mockito.when(mView.getTranslationX()).thenReturn(startTransX);
        // Create the left-drag event
        MotionEvent eventMoveRight = createLongRightMoveEvent();
        // Act
        boolean ret = uut.onTouch(mView, eventMoveRight);
        // Assert
        Mockito.verify(mView).setTranslationX(0);
        Mockito.verify(mView, Mockito.never()).setTranslationY(ArgumentMatchers.anyFloat());
        Assert.assertTrue(ret);
        Assert.assertEquals(STATE_IDLE, uut.getCurrentState());
        // State-change listener invoked to say drag-on and drag-off (idle).
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_IDLE), ArgumentMatchers.eq(STATE_DRAG_END_SIDE));
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_END_SIDE), ArgumentMatchers.eq(STATE_IDLE));
        Mockito.verify(mStateListener, Mockito.times(2)).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Update-listener called exactly twice?
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_END_SIDE), ArgumentMatchers.eq(startTransX));
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_END_SIDE), ArgumentMatchers.eq(0.0F));
        Mockito.verify(mUpdateListener, Mockito.times(2)).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyFloat());
    }

    /* Up action event */
    @Test
    public void onTouchUpAction_eventWhenNotOverscrolled_ignoreTouchEvent() throws Exception {
        // Arrange
        MotionEvent event = createDefaultUpActionEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(true);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(true);
        HorizontalOverScrollBounceEffectDecorator uut = getUUT();
        // Act
        boolean ret = uut.onTouch(mView, event);
        // Assert
        Mockito.verify(mView, Mockito.never()).setTranslationX(ArgumentMatchers.anyFloat());
        Mockito.verify(mView, Mockito.never()).setTranslationY(ArgumentMatchers.anyFloat());
        Assert.assertFalse(ret);
        Assert.assertEquals(STATE_IDLE, uut.getCurrentState());
        Mockito.verify(mStateListener, Mockito.never()).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verify(mUpdateListener, Mockito.never()).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyFloat());
    }
}

