package com.reactnativenavigation.viewcontrollers.sidemenu;


import Gravity.LEFT;
import Gravity.RIGHT;
import TypedValue.COMPLEX_UNIT_DIP;
import android.app.Activity;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.mocks.SimpleComponentViewController;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.parse.params.Number;
import com.reactnativenavigation.parse.params.Text;
import com.reactnativenavigation.presentation.SideMenuPresenter;
import com.reactnativenavigation.viewcontrollers.ChildControllersRegistry;
import com.reactnativenavigation.viewcontrollers.ParentController;
import com.reactnativenavigation.viewcontrollers.ViewController;
import com.reactnativenavigation.views.Component;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.eq;


@SuppressWarnings("MagicNumber")
public class SideMenuControllerTest extends BaseTest {
    private SideMenuController uut;

    private Activity activity;

    private ChildControllersRegistry childRegistry;

    private SideMenuPresenter presenter;

    private ViewController left;

    private ViewController right;

    private ViewController center;

    private ViewController child;

    private ParentController parent;

    private Options resolvedOptions;

    @Test
    public void createView_bindView() {
        uut.ensureViewIsCreated();
        Mockito.verify(presenter).bindView(uut.getView());
    }

    @Test
    public void applyChildOptions() {
        uut.applyChildOptions(new Options(), ((Component) (child.getView())));
        Mockito.verify(presenter).applyChildOptions(eq(resolvedOptions));
        Mockito.verify(parent).applyChildOptions(uut.options, ((Component) (child.getView())));
    }

    @Test
    public void mergeOptions_openLeftSideMenu() {
        uut.setLeftController(new SimpleComponentViewController(activity, childRegistry, "left", new Options()));
        Options options = new Options();
        options.sideMenuRootOptions.left.visible = new Bool(true);
        assertThat(uut.getView().isDrawerOpen(LEFT)).isFalse();
        uut.mergeOptions(options);
        assertThat(uut.getView().isDrawerOpen(LEFT)).isTrue();
    }

    @Test
    public void mergeOptions_openRightSideMenu() {
        uut.setRightController(new SimpleComponentViewController(activity, childRegistry, "right", new Options()));
        Options options = new Options();
        options.sideMenuRootOptions.right.visible = new Bool(true);
        assertThat(uut.getView().isDrawerOpen(RIGHT)).isFalse();
        uut.mergeOptions(options);
        assertThat(uut.getView().isDrawerOpen(RIGHT)).isTrue();
    }

    @Test
    public void mergeOptions_optionsAreClearedAfterMerge() {
        Options initialOptions = uut.options;
        Options options = new Options();
        uut.mergeOptions(options);
        assertThat(uut.options.sideMenuRootOptions).isNotEqualTo(initialOptions.sideMenuRootOptions);
    }

    @Test
    public void mergeChildOptions() {
        Options options = new Options();
        uut.mergeChildOptions(options, child, ((Component) (child.getView())));
        Mockito.verify(presenter).mergeChildOptions(options.sideMenuRootOptions);
    }

    @Test
    public void resolveCurrentOptions_centerOptionsAreMergedEvenIfDrawerIsOpen() {
        uut.setLeftController(left);
        center.options.topBar.title.text = new Text("Center");
        assertThat(uut.resolveCurrentOptions().topBar.title.text.hasValue()).isTrue();
        uut.getView().openDrawer(LEFT);
        assertThat(uut.resolveCurrentOptions().topBar.title.text.hasValue()).isTrue();
    }

    @Test
    public void setLeftController_matchesParentByDefault() {
        SideMenuOptions options = new SideMenuOptions();
        assertThat(options.width.hasValue()).isFalse();
        assertThat(options.height.hasValue()).isFalse();
        uut.options.sideMenuRootOptions.left = options;
        SimpleComponentViewController componentViewController = new SimpleComponentViewController(activity, childRegistry, "left", new Options());
        uut.setLeftController(componentViewController);
        LayoutParams params = getView().getLayoutParams();
        assertThat(params.width).isEqualTo(LayoutParams.MATCH_PARENT);
        assertThat(params.height).isEqualTo(LayoutParams.MATCH_PARENT);
    }

    @Test
    public void setLeftController_setHeightAndWidthWithOptions() {
        SideMenuOptions options = new SideMenuOptions();
        options.height = new Number(100);
        options.width = new Number(200);
        uut.options.sideMenuRootOptions.left = options;
        SimpleComponentViewController componentViewController = new SimpleComponentViewController(activity, childRegistry, "left", new Options());
        uut.setLeftController(componentViewController);
        int heightInDp = ((int) (TypedValue.applyDimension(COMPLEX_UNIT_DIP, 100, Resources.getSystem().getDisplayMetrics())));
        int widthInDp = ((int) (TypedValue.applyDimension(COMPLEX_UNIT_DIP, 200, Resources.getSystem().getDisplayMetrics())));
        LayoutParams params = getView().getLayoutParams();
        assertThat(params.width).isEqualTo(widthInDp);
        assertThat(params.height).isEqualTo(heightInDp);
    }

    @Test
    public void setRightController_matchesParentByDefault() {
        SideMenuOptions options = new SideMenuOptions();
        assertThat(options.width.hasValue()).isFalse();
        assertThat(options.height.hasValue()).isFalse();
        uut.options.sideMenuRootOptions.left = options;
        SimpleComponentViewController componentViewController = new SimpleComponentViewController(activity, childRegistry, "right", new Options());
        uut.setRightController(componentViewController);
        LayoutParams params = getView().getLayoutParams();
        assertThat(params.width).isEqualTo(LayoutParams.MATCH_PARENT);
        assertThat(params.height).isEqualTo(LayoutParams.MATCH_PARENT);
    }

    @Test
    public void setRightController_setHeightAndWidthWithOptions() {
        SideMenuOptions options = new SideMenuOptions();
        options.height = new Number(100);
        options.width = new Number(200);
        uut.options.sideMenuRootOptions.left = options;
        SimpleComponentViewController componentViewController = new SimpleComponentViewController(activity, childRegistry, "left", new Options());
        uut.setLeftController(componentViewController);
        int heightInDp = ((int) (TypedValue.applyDimension(COMPLEX_UNIT_DIP, 100, Resources.getSystem().getDisplayMetrics())));
        int widthInDp = ((int) (TypedValue.applyDimension(COMPLEX_UNIT_DIP, 200, Resources.getSystem().getDisplayMetrics())));
        LayoutParams params = getView().getLayoutParams();
        assertThat(params.width).isEqualTo(widthInDp);
        assertThat(params.height).isEqualTo(heightInDp);
    }

    @Test
    public void handleBack_closesLeftMenu() {
        uut.setLeftController(left);
        assertThat(uut.handleBack(new CommandListenerAdapter())).isFalse();
        Mockito.verify(center, Mockito.times(1)).handleBack(ArgumentMatchers.any());
        openLeftMenu();
        assertThat(uut.handleBack(new CommandListenerAdapter())).isTrue();
        Mockito.verify(center, Mockito.times(1)).handleBack(ArgumentMatchers.any());
    }

    @Test
    public void handleBack_closesRightMenu() {
        uut.setRightController(right);
        assertThat(uut.handleBack(new CommandListenerAdapter())).isFalse();
        Mockito.verify(center, Mockito.times(1)).handleBack(ArgumentMatchers.any());
        openRightMenu();
        assertThat(uut.handleBack(new CommandListenerAdapter())).isTrue();
        Mockito.verify(center, Mockito.times(1)).handleBack(ArgumentMatchers.any());
    }

    @Test
    public void leftMenuOpen_visibilityEventsAreEmitted() {
        ViewController spy = Mockito.spy(left);
        uut.setLeftController(spy);
        activity.setContentView(uut.getView());
        assertThat(uut.getView().isDrawerOpen(LEFT)).isFalse();
        Mockito.verify(spy, Mockito.times(0)).onViewAppeared();
        openLeftMenu();
        assertThat(uut.getView().isDrawerOpen(LEFT)).isTrue();
        Mockito.verify(spy).onViewAppeared();
        closeLeft();
        assertThat(uut.getView().isDrawerOpen(LEFT)).isFalse();
        Mockito.verify(spy).onViewDisappear();
    }

    @Test
    public void rightMenuOpen_visibilityEventsAreEmitted() {
        ViewController spy = Mockito.spy(right);
        uut.setRightController(spy);
        activity.setContentView(uut.getView());
        assertThat(uut.getView().isDrawerOpen(RIGHT)).isFalse();
        Mockito.verify(spy, Mockito.times(0)).onViewAppeared();
        openRightMenu();
        assertThat(uut.getView().isDrawerOpen(RIGHT)).isTrue();
        Mockito.verify(spy).onViewAppeared();
        closeRightMenu();
        assertThat(uut.getView().isDrawerOpen(RIGHT)).isFalse();
        Mockito.verify(spy).onViewDisappear();
    }

    @Test
    public void onDrawerOpened_drawerOpenedWIthSwipe_visibilityIsUpdated() {
        uut.setLeftController(left);
        uut.setRightController(right);
        uut.ensureViewIsCreated();
        openDrawerAndAssertVisibility(right, ( side) -> side.resolveCurrentOptions().sideMenuRootOptions.right);
        closeDrawerAndAssertVisibility(right, ( side) -> side.resolveCurrentOptions().sideMenuRootOptions.right);
        openDrawerAndAssertVisibility(left, ( side) -> side.resolveCurrentOptions().sideMenuRootOptions.left);
        closeDrawerAndAssertVisibility(left, ( side) -> side.resolveCurrentOptions().sideMenuRootOptions.left);
    }
}

