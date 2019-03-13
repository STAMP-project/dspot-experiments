package org.robolectric.shadows;


import Animation.AnimationListener;
import DeviceConfig.DEFAULT_SCREEN_SIZE;
import HapticFeedbackConstants.LONG_PRESS;
import MeasureSpec.AT_MOST;
import MotionEvent.ACTION_DOWN;
import View.GONE;
import View.INVISIBLE;
import View.LAYER_TYPE_NONE;
import View.LAYER_TYPE_SOFTWARE;
import View.NO_ID;
import View.OnCreateContextMenuListener;
import View.OnSystemUiVisibilityChangeListener;
import View.VISIBLE;
import ViewGroup.LayoutParams;
import android.R.attr.onClick;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowId;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.R;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.AccessibilityChecks;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.robolectric.util.TestRunnable;

import static org.robolectric.R.color.black;
import static org.robolectric.R.color.clear;
import static org.robolectric.R.color.white;
import static org.robolectric.R.drawable.an_image;
import static org.robolectric.R.id.icon;
import static org.robolectric.R.layout.inner_merge;


@RunWith(AndroidJUnit4.class)
public class ShadowViewTest {
    private View view;

    private List<String> transcript;

    private Application context;

    @Test
    public void testHasNullLayoutParamsUntilAddedToParent() throws Exception {
        assertThat(view.getLayoutParams()).isNull();
        new LinearLayout(context).addView(view);
        assertThat(view.getLayoutParams()).isNotNull();
    }

    @Test
    public void layout_shouldAffectWidthAndHeight() throws Exception {
        assertThat(view.getWidth()).isEqualTo(0);
        assertThat(view.getHeight()).isEqualTo(0);
        view.layout(100, 200, 303, 404);
        assertThat(view.getWidth()).isEqualTo((303 - 100));
        assertThat(view.getHeight()).isEqualTo((404 - 200));
    }

    @Test
    public void measuredDimensions() throws Exception {
        View view1 = new View(context) {
            {
                setMeasuredDimension(123, 456);
            }
        };
        assertThat(view1.getMeasuredWidth()).isEqualTo(123);
        assertThat(view1.getMeasuredHeight()).isEqualTo(456);
    }

    @Test
    public void layout_shouldCallOnLayoutOnlyIfChanged() throws Exception {
        View view1 = new View(context) {
            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                transcript.add(((((((((("onLayout " + changed) + " ") + left) + " ") + top) + " ") + right) + " ") + bottom));
            }
        };
        view1.layout(0, 0, 0, 0);
        assertThat(transcript).isEmpty();
        view1.layout(1, 2, 3, 4);
        assertThat(transcript).containsExactly("onLayout true 1 2 3 4");
        transcript.clear();
        view1.layout(1, 2, 3, 4);
        assertThat(transcript).isEmpty();
    }

    @Test
    public void shouldFocus() throws Exception {
        final List<String> transcript = new ArrayList<>();
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                transcript.add((hasFocus ? "Gained focus" : "Lost focus"));
            }
        });
        Assert.assertFalse(view.isFocused());
        Assert.assertFalse(view.hasFocus());
        assertThat(transcript).isEmpty();
        view.requestFocus();
        Assert.assertFalse(view.isFocused());
        Assert.assertFalse(view.hasFocus());
        assertThat(transcript).isEmpty();
        view.setFocusable(true);
        view.requestFocus();
        Assert.assertTrue(view.isFocused());
        Assert.assertTrue(view.hasFocus());
        assertThat(transcript).containsExactly("Gained focus");
        transcript.clear();
        Shadows.shadowOf(view).setMyParent(new LinearLayout(context));// we can never lose focus unless a parent can

        // take it
        view.clearFocus();
        Assert.assertFalse(view.isFocused());
        Assert.assertFalse(view.hasFocus());
        assertThat(transcript).containsExactly("Lost focus");
    }

    @Test
    public void shouldNotBeFocusableByDefault() throws Exception {
        Assert.assertFalse(view.isFocusable());
        view.setFocusable(true);
        Assert.assertTrue(view.isFocusable());
    }

    @Test
    public void shouldKnowIfThisOrAncestorsAreVisible() throws Exception {
        assertThat(view.isShown()).named("view isn't considered shown unless it has a view root").isFalse();
        Shadows.shadowOf(view).setMyParent(ReflectionHelpers.createNullProxy(ViewParent.class));
        assertThat(view.isShown()).isTrue();
        Shadows.shadowOf(view).setMyParent(null);
        ViewGroup parent = new LinearLayout(context);
        parent.addView(view);
        ViewGroup grandParent = new LinearLayout(context);
        grandParent.addView(parent);
        grandParent.setVisibility(GONE);
        Assert.assertFalse(view.isShown());
    }

    @Test
    public void shouldInflateMergeRootedLayoutAndNotCreateReferentialLoops() throws Exception {
        LinearLayout root = new LinearLayout(context);
        LinearLayout.inflate(context, inner_merge, root);
        for (int i = 0; i < (root.getChildCount()); i++) {
            View child = root.getChildAt(i);
            Assert.assertNotSame(root, child);
        }
    }

    @Test
    public void performLongClick_shouldClickOnView() throws Exception {
        OnLongClickListener clickListener = Mockito.mock(OnLongClickListener.class);
        Shadows.shadowOf(view).setMyParent(ReflectionHelpers.createNullProxy(ViewParent.class));
        view.setOnLongClickListener(clickListener);
        view.performLongClick();
        Mockito.verify(clickListener).onLongClick(view);
    }

    @Test
    public void checkedClick_shouldClickOnView() throws Exception {
        OnClickListener clickListener = Mockito.mock(OnClickListener.class);
        Shadows.shadowOf(view).setMyParent(ReflectionHelpers.createNullProxy(ViewParent.class));
        view.setOnClickListener(clickListener);
        Shadows.shadowOf(view).checkedPerformClick();
        Mockito.verify(clickListener).onClick(view);
    }

    @Test(expected = RuntimeException.class)
    public void checkedClick_shouldThrowIfViewIsNotVisible() throws Exception {
        ViewGroup grandParent = new LinearLayout(context);
        ViewGroup parent = new LinearLayout(context);
        grandParent.addView(parent);
        parent.addView(view);
        grandParent.setVisibility(GONE);
        Shadows.shadowOf(view).checkedPerformClick();
    }

    @Test(expected = RuntimeException.class)
    public void checkedClick_shouldThrowIfViewIsDisabled() throws Exception {
        view.setEnabled(false);
        Shadows.shadowOf(view).checkedPerformClick();
    }

    /* This test will throw an exception because the accessibility checks depend on the  Android
    Support Library. If the support library is included at some point, a single test from
    AccessibilityUtilTest could be moved here to make sure the accessibility checking is run.
     */
    @Test(expected = RuntimeException.class)
    @AccessibilityChecks
    public void checkedClick_withA11yChecksAnnotation_shouldThrow() throws Exception {
        Shadows.shadowOf(view).checkedPerformClick();
    }

    @Test
    public void getBackground_shouldReturnNullIfNoBackgroundHasBeenSet() throws Exception {
        assertThat(view.getBackground()).isNull();
    }

    @Test
    public void shouldSetBackgroundColor() {
        int red = -65536;
        view.setBackgroundColor(red);
        ColorDrawable background = ((ColorDrawable) (view.getBackground()));
        assertThat(background.getColor()).isEqualTo(red);
    }

    @Test
    public void shouldSetBackgroundResource() throws Exception {
        view.setBackgroundResource(an_image);
        assertThat(Shadows.shadowOf(((BitmapDrawable) (view.getBackground()))).getCreatedFromResId()).isEqualTo(an_image);
    }

    @Test
    public void shouldClearBackgroundResource() throws Exception {
        view.setBackgroundResource(an_image);
        view.setBackgroundResource(0);
        assertThat(view.getBackground()).isEqualTo(null);
    }

    @Test
    public void shouldRecordBackgroundColor() {
        int[] colors = new int[]{ black, clear, white };
        for (int color : colors) {
            view.setBackgroundColor(color);
            ColorDrawable drawable = ((ColorDrawable) (view.getBackground()));
            assertThat(drawable.getColor()).isEqualTo(color);
        }
    }

    @Test
    public void shouldRecordBackgroundDrawable() {
        Drawable drawable = new BitmapDrawable(BitmapFactory.decodeFile("some/fake/file"));
        view.setBackgroundDrawable(drawable);
        assertThat(view.getBackground()).isSameAs(drawable);
        assertThat(ShadowView.visualize(view)).isEqualTo("background:\nBitmap for file:some/fake/file");
    }

    @Test
    public void shouldPostActionsToTheMessageQueue() throws Exception {
        ShadowLooper.pauseMainLooper();
        TestRunnable runnable = new TestRunnable();
        view.post(runnable);
        Assert.assertFalse(runnable.wasRun);
        ShadowLooper.unPauseMainLooper();
        Assert.assertTrue(runnable.wasRun);
    }

    @Test
    public void shouldPostInvalidateDelayed() throws Exception {
        ShadowLooper.pauseMainLooper();
        view.postInvalidateDelayed(100);
        ShadowView shadowView = Shadows.shadowOf(view);
        Assert.assertFalse(shadowView.wasInvalidated());
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks();
        Assert.assertTrue(shadowView.wasInvalidated());
    }

    @Test
    public void shouldPostActionsToTheMessageQueueWithDelay() throws Exception {
        ShadowLooper.pauseMainLooper();
        TestRunnable runnable = new TestRunnable();
        view.postDelayed(runnable, 1);
        Assert.assertFalse(runnable.wasRun);
        Robolectric.getForegroundThreadScheduler().advanceBy(1);
        Assert.assertTrue(runnable.wasRun);
    }

    @Test
    public void shouldRemovePostedCallbacksFromMessageQueue() throws Exception {
        TestRunnable runnable = new TestRunnable();
        view.postDelayed(runnable, 1);
        view.removeCallbacks(runnable);
        Robolectric.getForegroundThreadScheduler().advanceBy(1);
        assertThat(runnable.wasRun).isFalse();
    }

    @Test
    public void shouldSupportAllConstructors() throws Exception {
        new View(context);
        new View(context, null);
        new View(context, null, 0);
    }

    @Test
    public void shouldRememberIsPressed() {
        view.setPressed(true);
        Assert.assertTrue(view.isPressed());
        view.setPressed(false);
        Assert.assertFalse(view.isPressed());
    }

    @Test
    public void shouldAddOnClickListenerFromAttribute() throws Exception {
        AttributeSet attrs = Robolectric.buildAttributeSet().addAttribute(onClick, "clickMe").build();
        view = new View(context, attrs);
        Assert.assertNotNull(Shadows.shadowOf(view).getOnClickListener());
    }

    @Test
    public void shouldCallOnClickWithAttribute() throws Exception {
        ShadowViewTest.MyActivity myActivity = Robolectric.buildActivity(ShadowViewTest.MyActivity.class).create().get();
        AttributeSet attrs = Robolectric.buildAttributeSet().addAttribute(onClick, "clickMe").build();
        view = new View(myActivity, attrs);
        view.performClick();
        Assert.assertTrue("Should have been called", myActivity.called);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWithBadMethodName() throws Exception {
        ShadowViewTest.MyActivity myActivity = Robolectric.buildActivity(ShadowViewTest.MyActivity.class).create().get();
        AttributeSet attrs = Robolectric.buildAttributeSet().addAttribute(onClick, "clickYou").build();
        view = new View(myActivity, attrs);
        view.performClick();
    }

    @Test
    public void shouldSetAnimation() throws Exception {
        Animation anim = new ShadowViewTest.TestAnimation();
        view.setAnimation(anim);
        assertThat(view.getAnimation()).isSameAs(anim);
    }

    @Test
    public void shouldFindViewWithTag() {
        view.setTag("tagged");
        assertThat(((View) (view.findViewWithTag("tagged")))).isSameAs(view);
    }

    @Test
    public void scrollTo_shouldStoreTheScrolledCoordinates() throws Exception {
        view.scrollTo(1, 2);
        assertThat(Shadows.shadowOf(view).scrollToCoordinates).isEqualTo(new Point(1, 2));
    }

    @Test
    public void shouldScrollTo() throws Exception {
        view.scrollTo(7, 6);
        Assert.assertEquals(7, view.getScrollX());
        Assert.assertEquals(6, view.getScrollY());
    }

    @Test
    public void scrollBy_shouldStoreTheScrolledCoordinates() throws Exception {
        view.scrollTo(4, 5);
        view.scrollBy(10, 20);
        assertThat(Shadows.shadowOf(view).scrollToCoordinates).isEqualTo(new Point(14, 25));
        assertThat(view.getScrollX()).isEqualTo(14);
        assertThat(view.getScrollY()).isEqualTo(25);
    }

    @Test
    public void shouldGetScrollXAndY() {
        Assert.assertEquals(0, view.getScrollX());
        Assert.assertEquals(0, view.getScrollY());
    }

    @Test
    public void getViewTreeObserver_shouldReturnTheSameObserverFromMultipleCalls() throws Exception {
        ViewTreeObserver observer = view.getViewTreeObserver();
        assertThat(observer).isInstanceOf(ViewTreeObserver.class);
        assertThat(view.getViewTreeObserver()).isSameAs(observer);
    }

    @Test
    public void dispatchTouchEvent_sendsMotionEventToOnTouchEvent() throws Exception {
        ShadowViewTest.TouchableView touchableView = new ShadowViewTest.TouchableView(context);
        MotionEvent event = MotionEvent.obtain(0L, 0L, ACTION_DOWN, 12.0F, 34.0F, 0);
        touchableView.dispatchTouchEvent(event);
        assertThat(touchableView.event).isSameAs(event);
        view.dispatchTouchEvent(event);
        assertThat(Shadows.shadowOf(view).getLastTouchEvent()).isSameAs(event);
    }

    @Test
    public void dispatchTouchEvent_listensToFalseFromListener() throws Exception {
        final AtomicBoolean called = new AtomicBoolean(false);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                called.set(true);
                return false;
            }
        });
        MotionEvent event = MotionEvent.obtain(0L, 0L, ACTION_DOWN, 12.0F, 34.0F, 0);
        view.dispatchTouchEvent(event);
        assertThat(Shadows.shadowOf(view).getLastTouchEvent()).isSameAs(event);
        assertThat(called.get()).isTrue();
    }

    @Test
    public void test_nextFocusDownId() throws Exception {
        Assert.assertEquals(NO_ID, view.getNextFocusDownId());
        view.setNextFocusDownId(icon);
        Assert.assertEquals(icon, view.getNextFocusDownId());
    }

    @Test
    public void startAnimation() {
        ShadowViewTest.TestView view = new ShadowViewTest.TestView(Robolectric.buildActivity(Activity.class).create().get());
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        Animation.AnimationListener listener = Mockito.mock(AnimationListener.class);
        animation.setAnimationListener(listener);
        view.startAnimation(animation);
        Mockito.verify(listener).onAnimationStart(animation);
        Mockito.verify(listener).onAnimationEnd(animation);
    }

    @Test
    public void setAnimation() {
        ShadowViewTest.TestView view = new ShadowViewTest.TestView(Robolectric.buildActivity(Activity.class).create().get());
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        Animation.AnimationListener listener = Mockito.mock(AnimationListener.class);
        animation.setAnimationListener(listener);
        animation.setStartTime(1000);
        view.setAnimation(animation);
        Mockito.verifyZeroInteractions(listener);
        Robolectric.getForegroundThreadScheduler().advanceToNextPostedRunnable();
        Mockito.verify(listener).onAnimationStart(animation);
        Mockito.verify(listener).onAnimationEnd(animation);
    }

    @Test
    public void setNullAnimation() {
        ShadowViewTest.TestView view = new ShadowViewTest.TestView(Robolectric.buildActivity(Activity.class).create().get());
        view.setAnimation(null);
        assertThat(getAnimation()).isNull();
    }

    @Test
    public void test_measuredDimension() {
        // View does not provide its own onMeasure implementation
        ShadowViewTest.TestView view1 = new ShadowViewTest.TestView(Robolectric.buildActivity(Activity.class).create().get());
        assertThat(getHeight()).isEqualTo(0);
        assertThat(getWidth()).isEqualTo(0);
        assertThat(getMeasuredHeight()).isEqualTo(0);
        assertThat(getMeasuredWidth()).isEqualTo(0);
        view1.measure(MeasureSpec.makeMeasureSpec(150, AT_MOST), MeasureSpec.makeMeasureSpec(300, AT_MOST));
        assertThat(getHeight()).isEqualTo(0);
        assertThat(getWidth()).isEqualTo(0);
        assertThat(getMeasuredHeight()).isEqualTo(300);
        assertThat(getMeasuredWidth()).isEqualTo(150);
    }

    @Test
    public void test_measuredDimensionCustomView() {
        // View provides its own onMeasure implementation
        ShadowViewTest.TestView2 view2 = new ShadowViewTest.TestView2(Robolectric.buildActivity(Activity.class).create().get(), 300, 100);
        assertThat(getWidth()).isEqualTo(0);
        assertThat(getHeight()).isEqualTo(0);
        assertThat(getMeasuredWidth()).isEqualTo(0);
        assertThat(getMeasuredHeight()).isEqualTo(0);
        view2.measure(MeasureSpec.makeMeasureSpec(200, AT_MOST), MeasureSpec.makeMeasureSpec(50, AT_MOST));
        assertThat(getWidth()).isEqualTo(0);
        assertThat(getHeight()).isEqualTo(0);
        assertThat(getMeasuredWidth()).isEqualTo(300);
        assertThat(getMeasuredHeight()).isEqualTo(100);
    }

    @Test
    public void shouldGetAndSetTranslations() throws Exception {
        view = new ShadowViewTest.TestView(Robolectric.buildActivity(Activity.class).create().get());
        view.setTranslationX(8.9F);
        view.setTranslationY(4.6F);
        assertThat(view.getTranslationX()).isEqualTo(8.9F);
        assertThat(view.getTranslationY()).isEqualTo(4.6F);
    }

    @Test
    public void shouldGetAndSetAlpha() throws Exception {
        view = new ShadowViewTest.TestView(Robolectric.buildActivity(Activity.class).create().get());
        view.setAlpha(9.1F);
        assertThat(view.getAlpha()).isEqualTo(9.1F);
    }

    @Test
    public void itKnowsIfTheViewIsShown() {
        Shadows.shadowOf(view).setMyParent(ReflectionHelpers.createNullProxy(ViewParent.class));// a view is only considered visible if it is added to a view root

        view.setVisibility(VISIBLE);
        assertThat(view.isShown()).isTrue();
    }

    @Test
    public void itKnowsIfTheViewIsNotShown() {
        view.setVisibility(GONE);
        assertThat(view.isShown()).isFalse();
        view.setVisibility(INVISIBLE);
        assertThat(view.isShown()).isFalse();
    }

    @Test
    public void shouldTrackRequestLayoutCalls() throws Exception {
        assertThat(Shadows.shadowOf(view).didRequestLayout()).isFalse();
        view.requestLayout();
        assertThat(Shadows.shadowOf(view).didRequestLayout()).isTrue();
        Shadows.shadowOf(view).setDidRequestLayout(false);
        assertThat(Shadows.shadowOf(view).didRequestLayout()).isFalse();
    }

    @Test
    public void shouldClickAndNotClick() throws Exception {
        assertThat(view.isClickable()).isFalse();
        view.setClickable(true);
        assertThat(view.isClickable()).isTrue();
        view.setClickable(false);
        assertThat(view.isClickable()).isFalse();
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        assertThat(view.isClickable()).isTrue();
    }

    @Test
    public void shouldLongClickAndNotLongClick() throws Exception {
        assertThat(view.isLongClickable()).isFalse();
        view.setLongClickable(true);
        assertThat(view.isLongClickable()).isTrue();
        view.setLongClickable(false);
        assertThat(view.isLongClickable()).isFalse();
        view.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        assertThat(view.isLongClickable()).isTrue();
    }

    @Test
    public void rotationX() {
        view.setRotationX(10.0F);
        assertThat(view.getRotationX()).isEqualTo(10.0F);
    }

    @Test
    public void rotationY() {
        view.setRotationY(20.0F);
        assertThat(view.getRotationY()).isEqualTo(20.0F);
    }

    @Test
    public void rotation() {
        view.setRotation(30.0F);
        assertThat(view.getRotation()).isEqualTo(30.0F);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void cameraDistance() {
        view.setCameraDistance(100.0F);
        assertThat(view.getCameraDistance()).isEqualTo(100.0F);
    }

    @Test
    public void scaleX() {
        assertThat(view.getScaleX()).isEqualTo(1.0F);
        view.setScaleX(0.5F);
        assertThat(view.getScaleX()).isEqualTo(0.5F);
    }

    @Test
    public void scaleY() {
        assertThat(view.getScaleY()).isEqualTo(1.0F);
        view.setScaleY(0.5F);
        assertThat(view.getScaleY()).isEqualTo(0.5F);
    }

    @Test
    public void pivotX() {
        view.setPivotX(10.0F);
        assertThat(view.getPivotX()).isEqualTo(10.0F);
    }

    @Test
    public void pivotY() {
        view.setPivotY(10.0F);
        assertThat(view.getPivotY()).isEqualTo(10.0F);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void elevation() {
        view.setElevation(10.0F);
        assertThat(view.getElevation()).isEqualTo(10.0F);
    }

    @Test
    public void translationX() {
        view.setTranslationX(10.0F);
        assertThat(view.getTranslationX()).isEqualTo(10.0F);
    }

    @Test
    public void translationY() {
        view.setTranslationY(10.0F);
        assertThat(view.getTranslationY()).isEqualTo(10.0F);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void translationZ() {
        view.setTranslationZ(10.0F);
        assertThat(view.getTranslationZ()).isEqualTo(10.0F);
    }

    @Test
    @Config(minSdk = VERSION_CODES.LOLLIPOP)
    public void clipToOutline() {
        view.setClipToOutline(true);
        assertThat(view.getClipToOutline()).isTrue();
    }

    @Test
    public void performHapticFeedback_shouldSetLastPerformedHapticFeedback() throws Exception {
        assertThat(Shadows.shadowOf(view).lastHapticFeedbackPerformed()).isEqualTo((-1));
        view.performHapticFeedback(LONG_PRESS);
        assertThat(Shadows.shadowOf(view).lastHapticFeedbackPerformed()).isEqualTo(LONG_PRESS);
    }

    @Test
    public void canAssertThatSuperDotOnLayoutWasCalledFromViewSubclasses() throws Exception {
        ShadowViewTest.TestView2 view = new ShadowViewTest.TestView2(Robolectric.setupActivity(Activity.class), 1111, 1112);
        assertThat(Shadows.shadowOf(view).onLayoutWasCalled()).isFalse();
        view.onLayout(true, 1, 2, 3, 4);
        assertThat(Shadows.shadowOf(view).onLayoutWasCalled()).isTrue();
    }

    @Test
    public void setScrolls_canBeAskedFor() throws Exception {
        view.setScrollX(234);
        view.setScrollY(544);
        assertThat(view.getScrollX()).isEqualTo(234);
        assertThat(view.getScrollY()).isEqualTo(544);
    }

    @Test
    public void setScrolls_firesOnScrollChanged() throws Exception {
        ShadowViewTest.TestView testView = new ShadowViewTest.TestView(Robolectric.buildActivity(Activity.class).create().get());
        setScrollX(122);
        setScrollY(150);
        setScrollX(453);
        assertThat(testView.oldl).isEqualTo(122);
        setScrollY(54);
        assertThat(testView.l).isEqualTo(453);
        assertThat(testView.t).isEqualTo(54);
        assertThat(testView.oldt).isEqualTo(150);
    }

    @Test
    public void layerType() throws Exception {
        assertThat(view.getLayerType()).isEqualTo(LAYER_TYPE_NONE);
        view.setLayerType(LAYER_TYPE_SOFTWARE, null);
        assertThat(view.getLayerType()).isEqualTo(LAYER_TYPE_SOFTWARE);
    }

    private static class TestAnimation extends Animation {}

    private static class TouchableView extends View {
        MotionEvent event;

        public TouchableView(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            this.event = event;
            return false;
        }
    }

    public static class TestView extends View {
        boolean onAnimationEndWasCalled;

        private int l;

        private int t;

        private int oldl;

        private int oldt;

        public TestView(Context context) {
            super(context);
        }

        @Override
        protected void onAnimationEnd() {
            super.onAnimationEnd();
            onAnimationEndWasCalled = true;
        }

        @Override
        public void onScrollChanged(int l, int t, int oldl, int oldt) {
            this.l = l;
            this.t = t;
            this.oldl = oldl;
            this.oldt = oldt;
        }
    }

    private static class TestView2 extends View {
        private int minWidth;

        private int minHeight;

        public TestView2(Context context, int minWidth, int minHeight) {
            super(context);
            this.minWidth = minWidth;
            this.minHeight = minHeight;
        }

        @Override
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(minWidth, minHeight);
        }
    }

    @Test
    public void shouldCallOnAttachedToAndDetachedFromWindow() throws Exception {
        ShadowViewTest.MyView parent = new ShadowViewTest.MyView("parent", transcript);
        addView(new ShadowViewTest.MyView("child", transcript));
        assertThat(transcript).isEmpty();
        Activity activity = Robolectric.buildActivity(ShadowViewTest.ContentViewActivity.class).create().get();
        activity.getWindowManager().addView(parent, new WindowManager.LayoutParams(100, 100));
        assertThat(transcript).containsExactly("parent attached", "child attached");
        transcript.clear();
        addView(new ShadowViewTest.MyView("another child", transcript));
        assertThat(transcript).containsExactly("another child attached");
        transcript.clear();
        ShadowViewTest.MyView temporaryChild = new ShadowViewTest.MyView("temporary child", transcript);
        addView(temporaryChild);
        assertThat(transcript).containsExactly("temporary child attached");
        transcript.clear();
        Assert.assertTrue(Shadows.shadowOf(temporaryChild).isAttachedToWindow());
        removeView(temporaryChild);
        assertThat(transcript).containsExactly("temporary child detached");
        Assert.assertFalse(Shadows.shadowOf(temporaryChild).isAttachedToWindow());
    }

    @Test
    @Config(minSdk = VERSION_CODES.JELLY_BEAN_MR2)
    public void getWindowId_shouldReturnValidObjectWhenAttached() throws Exception {
        ShadowViewTest.MyView parent = new ShadowViewTest.MyView("parent", transcript);
        ShadowViewTest.MyView child = new ShadowViewTest.MyView("child", transcript);
        addView(child);
        assertThat(getWindowId()).isNull();
        assertThat(getWindowId()).isNull();
        Activity activity = Robolectric.buildActivity(ShadowViewTest.ContentViewActivity.class).create().get();
        activity.getWindowManager().addView(parent, new WindowManager.LayoutParams(100, 100));
        WindowId windowId = parent.getWindowId();
        assertThat(windowId).isNotNull();
        assertThat(getWindowId()).isSameAs(windowId);
        assertThat(getWindowId()).isEqualTo(windowId);// equals must work!

        ShadowViewTest.MyView anotherChild = new ShadowViewTest.MyView("another child", transcript);
        addView(anotherChild);
        assertThat(getWindowId()).isEqualTo(windowId);
        removeView(anotherChild);
        assertThat(getWindowId()).isNull();
    }

    // todo looks like this is flaky...
    @Test
    public void removeAllViews_shouldCallOnAttachedToAndDetachedFromWindow() throws Exception {
        ShadowViewTest.MyView parent = new ShadowViewTest.MyView("parent", transcript);
        Activity activity = Robolectric.buildActivity(ShadowViewTest.ContentViewActivity.class).create().get();
        activity.getWindowManager().addView(parent, new WindowManager.LayoutParams(100, 100));
        addView(new ShadowViewTest.MyView("child", transcript));
        addView(new ShadowViewTest.MyView("another child", transcript));
        ShadowLooper.runUiThreadTasks();
        transcript.clear();
        removeAllViews();
        ShadowLooper.runUiThreadTasks();
        assertThat(transcript).containsExactly("another child detached", "child detached");
    }

    @Test
    public void capturesOnSystemUiVisibilityChangeListener() throws Exception {
        ShadowViewTest.TestView testView = new ShadowViewTest.TestView(Robolectric.buildActivity(Activity.class).create().get());
        View.OnSystemUiVisibilityChangeListener changeListener = new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
            }
        };
        testView.setOnSystemUiVisibilityChangeListener(changeListener);
        assertThat(changeListener).isEqualTo(Shadows.shadowOf(testView).getOnSystemUiVisibilityChangeListener());
    }

    @Test
    public void capturesOnCreateContextMenuListener() throws Exception {
        ShadowViewTest.TestView testView = new ShadowViewTest.TestView(Robolectric.buildActivity(Activity.class).create().get());
        assertThat(Shadows.shadowOf(testView).getOnCreateContextMenuListener()).isNull();
        View.OnCreateContextMenuListener createListener = new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            }
        };
        testView.setOnCreateContextMenuListener(createListener);
        assertThat(Shadows.shadowOf(testView).getOnCreateContextMenuListener()).isEqualTo(createListener);
        setOnCreateContextMenuListener(null);
        assertThat(Shadows.shadowOf(testView).getOnCreateContextMenuListener()).isNull();
    }

    @Test
    public void setsGlobalVisibleRect() {
        Rect globalVisibleRect = new Rect();
        Shadows.shadowOf(view).setGlobalVisibleRect(new Rect());
        assertThat(view.getGlobalVisibleRect(globalVisibleRect)).isFalse();
        assertThat(globalVisibleRect.isEmpty()).isTrue();
        assertThat(view.getGlobalVisibleRect(globalVisibleRect, new Point(1, 1))).isFalse();
        assertThat(globalVisibleRect.isEmpty()).isTrue();
        Shadows.shadowOf(view).setGlobalVisibleRect(new Rect(1, 2, 3, 4));
        assertThat(view.getGlobalVisibleRect(globalVisibleRect)).isTrue();
        assertThat(globalVisibleRect).isEqualTo(new Rect(1, 2, 3, 4));
        assertThat(view.getGlobalVisibleRect(globalVisibleRect, new Point(1, 1))).isTrue();
        assertThat(globalVisibleRect).isEqualTo(new Rect(0, 1, 2, 3));
    }

    @Test
    public void usesDefaultGlobalVisibleRect() {
        final ActivityController<Activity> activityController = Robolectric.buildActivity(Activity.class);
        final Activity activity = activityController.get();
        activity.setContentView(view, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        activityController.setup();
        Rect globalVisibleRect = new Rect();
        assertThat(view.getGlobalVisibleRect(globalVisibleRect)).isTrue();
        assertThat(globalVisibleRect).isEqualTo(new Rect(0, 25, DEFAULT_SCREEN_SIZE.width, DEFAULT_SCREEN_SIZE.height));
    }

    public static class MyActivity extends Activity {
        public boolean called;

        @SuppressWarnings("UnusedDeclaration")
        public void clickMe(View view) {
            called = true;
        }
    }

    public static class MyView extends LinearLayout {
        private String name;

        private List<String> transcript;

        public MyView(String name, List<String> transcript) {
            super(ApplicationProvider.getApplicationContext());
            this.name = name;
            this.transcript = transcript;
        }

        @Override
        protected void onAttachedToWindow() {
            transcript.add(((name) + " attached"));
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            transcript.add(((name) + " detached"));
            super.onDetachedFromWindow();
        }
    }

    private static class ContentViewActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(new FrameLayout(this));
        }
    }
}

