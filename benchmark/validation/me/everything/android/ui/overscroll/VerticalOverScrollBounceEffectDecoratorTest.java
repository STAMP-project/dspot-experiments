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
public class VerticalOverScrollBounceEffectDecoratorTest {
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
        VerticalOverScrollBounceEffectDecorator uut = getUUT();
        uut.onTouch(mView, createShortDownwardsMoveEvent());
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
        MotionEvent event = createShortDownwardsMoveEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(false);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(false);
        VerticalOverScrollBounceEffectDecorator uut = getUUT();
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
    public void onTouchMoveAction_dragDownInUpperEnd_overscrollDownwards() throws Exception {
        // Arrange
        MotionEvent event = createShortDownwardsMoveEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(true);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(false);
        VerticalOverScrollBounceEffectDecorator uut = getUUT();
        // Act
        boolean ret = uut.onTouch(mView, event);
        // Assert
        float expectedTransY = ((event.getY()) - (event.getHistoricalY(0))) / (VerticalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD);
        Mockito.verify(mView).setTranslationY(expectedTransY);
        Mockito.verify(mView, Mockito.never()).setTranslationX(ArgumentMatchers.anyFloat());
        Assert.assertTrue(ret);
        Assert.assertEquals(STATE_DRAG_START_SIDE, uut.getCurrentState());
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_IDLE), ArgumentMatchers.eq(STATE_DRAG_START_SIDE));
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_START_SIDE), ArgumentMatchers.eq(expectedTransY));
    }

    @Test
    public void onTouchMoveAction_dragUpInBottomEnd_overscrollUpwards() throws Exception {
        // Arrange
        MotionEvent event = createShortUpwardsMoveEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(false);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(true);
        VerticalOverScrollBounceEffectDecorator uut = getUUT();
        // Act
        boolean ret = uut.onTouch(mView, event);
        // Assert
        float expectedTransY = ((event.getY()) - (event.getHistoricalY(0))) / (VerticalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD);
        Mockito.verify(mView).setTranslationY(expectedTransY);
        Mockito.verify(mView, Mockito.never()).setTranslationX(ArgumentMatchers.anyFloat());
        Assert.assertTrue(ret);
        Assert.assertEquals(STATE_DRAG_END_SIDE, uut.getCurrentState());
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_IDLE), ArgumentMatchers.eq(STATE_DRAG_END_SIDE));
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_END_SIDE), ArgumentMatchers.eq(expectedTransY));
    }

    @Test
    public void onTouchMoveAction_dragUpInUpperEnd_ignoreTouchEvent() throws Exception {
        // Arrange
        MotionEvent event = createShortUpwardsMoveEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(true);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(false);
        VerticalOverScrollBounceEffectDecorator uut = getUUT();
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
    public void onTouchMoveAction_dragDownInBottomEnd_ignoreTouchEvent() throws Exception {
        // Arrange
        MotionEvent event = createShortDownwardsMoveEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(false);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(true);
        VerticalOverScrollBounceEffectDecorator uut = getUUT();
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
    public void onTouchMoveAction_2ndDownDragInUpperEnd_overscrollDownwardsFurther() throws Exception {
        // Arrange
        // Bring UUT to a downwards-overscroll state
        MotionEvent event1 = createShortDownwardsMoveEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(true);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(false);
        VerticalOverScrollBounceEffectDecorator uut = getUUT();
        uut.onTouch(mView, event1);
        Mockito.reset(mView);
        // Create 2nd downwards-drag event
        MotionEvent event2 = createLongDownwardsMoveEvent();
        // Act
        final boolean ret = uut.onTouch(mView, event2);
        // Assert
        final float expectedTransY1 = ((event1.getY()) - (event1.getHistoricalY(0))) / (VerticalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD);
        final float expectedTransY2 = ((event2.getY()) - (event2.getHistoricalY(0))) / (VerticalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD);
        Mockito.verify(mView).setTranslationY(expectedTransY2);
        Mockito.verify(mView, Mockito.never()).setTranslationX(ArgumentMatchers.anyFloat());
        Assert.assertTrue(ret);
        Assert.assertEquals(STATE_DRAG_START_SIDE, uut.getCurrentState());
        // State-change listener called only once?
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_IDLE), ArgumentMatchers.eq(STATE_DRAG_START_SIDE));
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Update-listener called exactly twice?
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_START_SIDE), ArgumentMatchers.eq(expectedTransY1));
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_START_SIDE), ArgumentMatchers.eq(expectedTransY2));
        Mockito.verify(mUpdateListener, Mockito.times(2)).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyFloat());
    }

    @Test
    public void onTouchMoveAction_2ndUpDragInBottomEnd_overscrollUpwardsFurther() throws Exception {
        // Arrange
        // Bring UUT to an upwards-overscroll state
        MotionEvent event1 = createShortUpwardsMoveEvent();
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(false);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(true);
        VerticalOverScrollBounceEffectDecorator uut = getUUT();
        uut.onTouch(mView, event1);
        Mockito.reset(mView);
        // Create 2nd upward-drag event
        MotionEvent event2 = createLongUpwardsMoveEvent();
        // Act
        final boolean ret = uut.onTouch(mView, event2);
        // Assert
        final float expectedTransY1 = ((event1.getY()) - (event1.getHistoricalY(0))) / (VerticalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD);
        final float expectedTransY2 = ((event2.getY()) - (event2.getHistoricalY(0))) / (VerticalOverScrollBounceEffectDecorator.DEFAULT_TOUCH_DRAG_MOVE_RATIO_FWD);
        Mockito.verify(mView).setTranslationY(expectedTransY2);
        Mockito.verify(mView, Mockito.never()).setTranslationX(ArgumentMatchers.anyFloat());
        Assert.assertTrue(ret);
        Assert.assertEquals(STATE_DRAG_END_SIDE, uut.getCurrentState());
        // State-change listener called only once?
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_IDLE), ArgumentMatchers.eq(STATE_DRAG_END_SIDE));
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Update-listener called exactly twice?
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_END_SIDE), ArgumentMatchers.eq(expectedTransY1));
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_END_SIDE), ArgumentMatchers.eq(expectedTransY2));
        Mockito.verify(mUpdateListener, Mockito.times(2)).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyFloat());
    }

    /**
     * When over-scroll has already started (downwards in this case) and suddenly the user changes
     * their mind and scrolls a bit in the other direction:
     * <br/>We expect the <b>touch to still be intercepted</b> in that case, and the <b>overscroll to remain in effect</b>.
     */
    @Test
    public void onTouchMoveAction_dragUpWhenDownOverscolled_continueOverscrollingUpwards() throws Exception {
        // Arrange
        // In down & up drag tests we use equal ratios to avoid the effect's under-scroll handling
        final float touchDragRatioFwd = 3.0F;
        final float touchDragRatioBck = 3.0F;
        // Bring UUT to a downwrads-overscroll state
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(true);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(false);
        VerticalOverScrollBounceEffectDecorator uut = getUUT(touchDragRatioFwd, touchDragRatioBck);
        MotionEvent eventMoveRight = createLongDownwardsMoveEvent();
        uut.onTouch(mView, eventMoveRight);
        Mockito.reset(mView);
        float startTransY = ((eventMoveRight.getY()) - (eventMoveRight.getHistoricalY(0))) / touchDragRatioFwd;
        Mockito.when(mView.getTranslationY()).thenReturn(startTransY);
        // Create the up-drag event
        MotionEvent eventMoveUpwards = createShortUpwardsMoveEvent();
        // Act
        boolean ret = uut.onTouch(mView, eventMoveUpwards);
        // Assert
        float expectedTransY = startTransY + (((eventMoveUpwards.getY()) - (eventMoveUpwards.getHistoricalY(0))) / touchDragRatioBck);
        Mockito.verify(mView).setTranslationY(expectedTransY);
        Mockito.verify(mView, Mockito.never()).setTranslationX(ArgumentMatchers.anyFloat());
        Assert.assertTrue(ret);
        Assert.assertEquals(STATE_DRAG_START_SIDE, uut.getCurrentState());
        // State-change listener called only once?
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_IDLE), ArgumentMatchers.eq(STATE_DRAG_START_SIDE));
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Update-listener called exactly twice?
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_START_SIDE), ArgumentMatchers.eq(startTransY));
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_START_SIDE), ArgumentMatchers.eq(expectedTransY));
        Mockito.verify(mUpdateListener, Mockito.times(2)).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyFloat());
    }

    /**
     * When over-scroll has already started (upwards in this case) and suddenly the user changes
     * their mind and scrolls a bit in the other direction:
     * <br/>We expect the <b>touch to still be intercepted</b> in that case, and the <b>overscroll to remain in effect</b>.
     */
    @Test
    public void onTouchMoveAction_dragDownWhenUpOverscolled_continueOverscrollingDownwards() throws Exception {
        // Arrange
        // In up & down drag tests we use equal ratios to avoid the effect's under-scroll handling
        final float touchDragRatioFwd = 3.0F;
        final float touchDragRatioBck = 3.0F;
        // Bring UUT to an upwards-overscroll state
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(false);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(true);
        VerticalOverScrollBounceEffectDecorator uut = getUUT(touchDragRatioFwd, touchDragRatioBck);
        MotionEvent eventMoveUp = createLongUpwardsMoveEvent();
        uut.onTouch(mView, eventMoveUp);
        Mockito.reset(mView);
        float startTransY = ((eventMoveUp.getY()) - (eventMoveUp.getHistoricalY(0))) / touchDragRatioFwd;
        Mockito.when(mView.getTranslationY()).thenReturn(startTransY);
        // Create the down-drag event
        MotionEvent eventMoveDown = createShortDownwardsMoveEvent();
        // Act
        boolean ret = uut.onTouch(mView, eventMoveDown);
        // Assert
        float expectedTransY = startTransY + (((eventMoveDown.getY()) - (eventMoveDown.getHistoricalY(0))) / touchDragRatioBck);
        Mockito.verify(mView).setTranslationY(expectedTransY);
        Mockito.verify(mView, Mockito.never()).setTranslationX(ArgumentMatchers.anyFloat());
        Assert.assertTrue(ret);
        Assert.assertEquals(STATE_DRAG_END_SIDE, uut.getCurrentState());
        // State-change listener called only once?
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_IDLE), ArgumentMatchers.eq(STATE_DRAG_END_SIDE));
        Mockito.verify(mStateListener).onOverScrollStateChange(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Update-listener called exactly twice?
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_END_SIDE), ArgumentMatchers.eq(startTransY));
        Mockito.verify(mUpdateListener).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.eq(STATE_DRAG_END_SIDE), ArgumentMatchers.eq(expectedTransY));
        Mockito.verify(mUpdateListener, Mockito.times(2)).onOverScrollUpdate(ArgumentMatchers.eq(uut), ArgumentMatchers.anyInt(), ArgumentMatchers.anyFloat());
    }

    @Test
    public void onTouchMoveAction_undragWhenDownOverscrolled_endOverscrolling() throws Exception {
        // Arrange
        // In left & right tests we use equal ratios to avoid the effect's under-scroll handling
        final float touchDragRatioFwd = 3.0F;
        final float touchDragRatioBck = 3.0F;
        // Bring UUT to a downwards-overscroll state
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(true);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(false);
        VerticalOverScrollBounceEffectDecorator uut = getUUT(touchDragRatioFwd, touchDragRatioBck);
        MotionEvent eventMoveDown = createLongDownwardsMoveEvent();
        uut.onTouch(mView, eventMoveDown);
        Mockito.reset(mView);
        float startTransX = ((eventMoveDown.getX()) - (eventMoveDown.getHistoricalX(0))) / touchDragRatioFwd;
        Mockito.when(mView.getTranslationX()).thenReturn(startTransX);
        // Create the (negative) upwards-drag event
        MotionEvent eventMoveUp = createLongUpwardsMoveEvent();
        // Act
        boolean ret = uut.onTouch(mView, eventMoveUp);
        // Assert
        Mockito.verify(mView, Mockito.never()).setTranslationX(ArgumentMatchers.anyFloat());
        Mockito.verify(mView).setTranslationY(0);
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
    public void onTouchMoveAction_undragWhenUpOverscrolled_endOverscrolling() throws Exception {
        // Arrange
        // In left & right tests we use equal ratios to avoid the effect's under-scroll handling
        final float touchDragRatioFwd = 3.0F;
        final float touchDragRatioBck = 3.0F;
        // Bring UUT to a left-overscroll state
        Mockito.when(mViewAdapter.isInAbsoluteStart()).thenReturn(false);
        Mockito.when(mViewAdapter.isInAbsoluteEnd()).thenReturn(true);
        VerticalOverScrollBounceEffectDecorator uut = getUUT(touchDragRatioFwd, touchDragRatioBck);
        MotionEvent eventMoveUp = createLongUpwardsMoveEvent();
        uut.onTouch(mView, eventMoveUp);
        Mockito.reset(mView);
        float startTransX = ((eventMoveUp.getX()) - (eventMoveUp.getHistoricalX(0))) / touchDragRatioFwd;
        Mockito.when(mView.getTranslationX()).thenReturn(startTransX);
        // Create the (negative) downwards-drag event
        MotionEvent eventMoveDown = createLongDownwardsMoveEvent();
        // Act
        boolean ret = uut.onTouch(mView, eventMoveDown);
        // Assert
        Mockito.verify(mView, Mockito.never()).setTranslationX(ArgumentMatchers.anyFloat());
        Mockito.verify(mView).setTranslationY(0);
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
        VerticalOverScrollBounceEffectDecorator uut = getUUT();
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

