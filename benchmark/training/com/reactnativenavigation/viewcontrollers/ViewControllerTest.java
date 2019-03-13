package com.reactnativenavigation.viewcontrollers;


import View.GONE;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.TestUtils;
import com.reactnativenavigation.mocks.SimpleViewController;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.parse.params.NullBool;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.viewcontrollers.stack.StackController;
import com.reactnativenavigation.views.Component;
import java.lang.reflect.Field;
import org.assertj.android.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Shadows;


public class ViewControllerTest extends BaseTest {
    private ViewController uut;

    private Activity activity;

    private ChildControllersRegistry childRegistry;

    private YellowBoxDelegate yellowBoxDelegate;

    @Test
    public void holdsAView() {
        assertThat(uut.getView()).isNotNull().isInstanceOf(View.class);
    }

    @Test
    public void holdsARefToActivity() {
        assertThat(uut.getActivity()).isNotNull().isEqualTo(activity);
    }

    @Test
    public void canOverrideViewCreation() {
        final FrameLayout otherView = new FrameLayout(activity);
        yellowBoxDelegate = Mockito.spy(new YellowBoxDelegate());
        ViewController myController = new ViewController(activity, "vc", yellowBoxDelegate, new Options()) {
            @Override
            protected FrameLayout createView() {
                return otherView;
            }

            @Override
            public void sendOnNavigationButtonPressed(String buttonId) {
            }
        };
        assertThat(myController.getView()).isEqualTo(otherView);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void holdsAReferenceToStackControllerOrNull() {
        uut.setParentController(null);
        assertThat(uut.getParentController()).isNull();
        StackController nav = TestUtils.newStackController(activity).build();
        nav.ensureViewIsCreated();
        nav.push(uut, new CommandListenerAdapter());
        assertThat(uut.getParentController()).isEqualTo(nav);
    }

    @Test
    public void handleBackDefaultFalse() {
        assertThat(uut.handleBack(new CommandListenerAdapter())).isFalse();
    }

    @Test
    public void holdsId() {
        assertThat(uut.getId()).isEqualTo("uut");
    }

    @Test
    public void isSameId() {
        assertThat(uut.isSameId("")).isFalse();
        assertThat(uut.isSameId(null)).isFalse();
        assertThat(uut.isSameId("uut")).isTrue();
    }

    @Test
    public void findControllerById_SelfOrNull() {
        assertThat(uut.findController("456")).isNull();
        assertThat(uut.findController("uut")).isEqualTo(uut);
    }

    @Test
    public void onChildViewAdded_delegatesToYellowBoxDelegate() {
        View child = new View(activity);
        ViewGroup view = new FrameLayout(activity);
        ViewController vc = new ViewController(activity, "", yellowBoxDelegate, new Options()) {
            @Override
            protected ViewGroup createView() {
                return view;
            }

            @Override
            public void sendOnNavigationButtonPressed(String buttonId) {
            }
        };
        vc.onChildViewAdded(view, child);
        Mockito.verify(yellowBoxDelegate).onChildViewAdded(view, child);
    }

    @Test
    public void onAppear_WhenShown() {
        ViewController spy = Mockito.spy(uut);
        spy.getView().getViewTreeObserver().dispatchOnGlobalLayout();
        Assertions.assertThat(spy.getView()).isNotShown();
        Mockito.verify(spy, Mockito.times(0)).onViewAppeared();
        Shadows.shadowOf(spy.getView()).setMyParent(Mockito.mock(ViewParent.class));
        spy.getView().getViewTreeObserver().dispatchOnGlobalLayout();
        Assertions.assertThat(spy.getView()).isShown();
        Mockito.verify(spy, Mockito.times(1)).onViewAppeared();
    }

    @Test
    public void onAppear_CalledAtMostOnce() {
        ViewController spy = Mockito.spy(uut);
        Shadows.shadowOf(spy.getView()).setMyParent(Mockito.mock(ViewParent.class));
        Assertions.assertThat(spy.getView()).isShown();
        spy.getView().getViewTreeObserver().dispatchOnGlobalLayout();
        spy.getView().getViewTreeObserver().dispatchOnGlobalLayout();
        spy.getView().getViewTreeObserver().dispatchOnGlobalLayout();
        Mockito.verify(spy, Mockito.times(1)).onViewAppeared();
    }

    @Test
    public void onDisappear_WhenNotShown_AfterOnAppearWasCalled() {
        ViewController spy = Mockito.spy(uut);
        Shadows.shadowOf(spy.getView()).setMyParent(Mockito.mock(ViewParent.class));
        Assertions.assertThat(spy.getView()).isShown();
        spy.getView().getViewTreeObserver().dispatchOnGlobalLayout();
        Mockito.verify(spy, Mockito.times(1)).onViewAppeared();
        Mockito.verify(spy, Mockito.times(0)).onViewDisappear();
        spy.getView().setVisibility(GONE);
        spy.getView().getViewTreeObserver().dispatchOnGlobalLayout();
        Assertions.assertThat(spy.getView()).isNotShown();
        Mockito.verify(spy, Mockito.times(1)).onViewDisappear();
    }

    @Test
    public void onDisappear_CalledAtMostOnce() {
        ViewController spy = Mockito.spy(uut);
        Shadows.shadowOf(spy.getView()).setMyParent(Mockito.mock(ViewParent.class));
        Assertions.assertThat(spy.getView()).isShown();
        spy.getView().getViewTreeObserver().dispatchOnGlobalLayout();
        spy.getView().setVisibility(GONE);
        spy.getView().getViewTreeObserver().dispatchOnGlobalLayout();
        spy.getView().getViewTreeObserver().dispatchOnGlobalLayout();
        spy.getView().getViewTreeObserver().dispatchOnGlobalLayout();
        Mockito.verify(spy, Mockito.times(1)).onViewDisappear();
    }

    @Test
    public void onDestroy_RemovesGlobalLayoutListener() throws Exception {
        new SimpleViewController(activity, childRegistry, "ensureNotNull", new Options()).destroy();
        ViewController spy = Mockito.spy(uut);
        View view = spy.getView();
        Shadows.shadowOf(view).setMyParent(Mockito.mock(ViewParent.class));
        spy.destroy();
        Assertions.assertThat(view).isShown();
        view.getViewTreeObserver().dispatchOnGlobalLayout();
        Mockito.verify(spy, Mockito.times(0)).onViewAppeared();
        Mockito.verify(spy, Mockito.times(0)).onViewDisappear();
        Field field = ViewController.class.getDeclaredField("view");
        field.setAccessible(true);
        assertThat(field.get(spy)).isNull();
    }

    @Test
    public void onDestroy_CallsOnDisappearIfNeeded() {
        ViewController spy = Mockito.spy(uut);
        Shadows.shadowOf(spy.getView()).setMyParent(Mockito.mock(ViewParent.class));
        Assertions.assertThat(spy.getView()).isShown();
        spy.getView().getViewTreeObserver().dispatchOnGlobalLayout();
        Mockito.verify(spy, Mockito.times(1)).onViewAppeared();
        spy.destroy();
        Mockito.verify(spy, Mockito.times(1)).onViewDisappear();
    }

    @Test
    public void onDestroy_destroysViewEvenIfHidden() {
        final SimpleViewController.SimpleView[] spy = new SimpleViewController.SimpleView[1];
        ViewController uut = new SimpleViewController(activity, childRegistry, "uut", new Options()) {
            @Override
            protected SimpleViewController.SimpleView createView() {
                SimpleViewController.SimpleView view = Mockito.spy(super.createView());
                spy[0] = view;
                return view;
            }
        };
        assertThat(uut.isViewShown()).isFalse();
        uut.destroy();
        Mockito.verify(spy[0], Mockito.times(1)).destroy();
    }

    @Test
    public void onDestroy_RemovesSelfFromParentIfExists() {
        LinearLayout parent = new LinearLayout(activity);
        parent.addView(uut.getView());
        uut.destroy();
        assertThat(parent.getChildCount()).withFailMessage("expected not to have children").isZero();
    }

    @Test
    public void ensureViewIsCreated() {
        ViewController spy = Mockito.spy(uut);
        Mockito.verify(spy, Mockito.times(0)).getView();
        spy.ensureViewIsCreated();
        Mockito.verify(spy, Mockito.times(1)).getView();
    }

    @Test
    public void isRendered_falseIfViewIsNotCreated() {
        uut.setWaitForRender(new Bool(true));
        assertThat(uut.isRendered()).isFalse();
    }

    @Test
    public void isRendered_delegatesToView() {
        uut.setWaitForRender(new Bool(true));
        uut.view = Mockito.mock(ViewGroup.class, Mockito.withSettings().extraInterfaces(Component.class));
        uut.isRendered();
        Mockito.verify(((Component) (uut.view))).isRendered();
    }

    @Test
    public void isRendered_returnsTrueForEveryViewByDefault() {
        uut.setWaitForRender(new NullBool());
        uut.view = Mockito.mock(ViewGroup.class);
        assertThat(uut.isRendered()).isTrue();
    }
}

