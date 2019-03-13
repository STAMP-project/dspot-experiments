package com.reactnativenavigation.viewcontrollers;


import Options.EMPTY;
import android.app.Activity;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.TestUtils;
import com.reactnativenavigation.mocks.SimpleViewController;
import com.reactnativenavigation.mocks.TestReactView;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.utils.ViewHelper;
import com.reactnativenavigation.viewcontrollers.stack.StackController;
import com.reactnativenavigation.viewcontrollers.toptabs.TopTabsController;
import com.reactnativenavigation.views.ReactComponent;
import com.reactnativenavigation.views.toptabs.TopTabsViewPager;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TopTabsViewControllerTest extends BaseTest {
    private static final int SIZE = 2;

    private StackController stack;

    private TopTabsController uut;

    private List<ViewController> tabControllers = new ArrayList<>(TopTabsViewControllerTest.SIZE);

    private final Options options = new Options();

    private TopTabsViewPager topTabsLayout;

    private Activity activity;

    private ChildControllersRegistry childRegistry;

    @Test
    public void createsViewFromComponentViewCreator() {
        uut.ensureViewIsCreated();
        for (int i = 0; i < (TopTabsViewControllerTest.SIZE); i++) {
            Mockito.verify(tabControllers.get(i), Mockito.times(1)).createView();
        }
    }

    @Test
    public void componentViewDestroyedOnDestroy() {
        uut.ensureViewIsCreated();
        TopTabsViewPager topTabs = uut.getView();
        for (int i = 0; i < (TopTabsViewControllerTest.SIZE); i++) {
            Mockito.verify(tab(topTabs, i), Mockito.times(0)).destroy();
        }
        uut.destroy();
        for (ViewController tabController : tabControllers) {
            Mockito.verify(tabController, Mockito.times(1)).destroy();
        }
    }

    @Test
    public void lifecycleMethodsSentWhenSelectedTabChanges() {
        stack.ensureViewIsCreated();
        uut.ensureViewIsCreated();
        tabControllers.get(0).ensureViewIsCreated();
        tabControllers.get(1).ensureViewIsCreated();
        tabControllers.get(0).onViewAppeared();
        uut.onViewAppeared();
        TestReactView initialTab = getActualTabView(0);
        TestReactView selectedTab = getActualTabView(1);
        uut.switchToTab(1);
        Mockito.verify(initialTab, Mockito.times(1)).sendComponentStop();
        Mockito.verify(selectedTab, Mockito.times(1)).sendComponentStart();
        Mockito.verify(selectedTab, Mockito.times(0)).sendComponentStop();
    }

    @Test
    public void lifecycleMethodsSentWhenSelectedPreviouslySelectedTab() {
        stack.ensureViewIsCreated();
        uut.ensureViewIsCreated();
        uut.onViewAppeared();
        uut.switchToTab(1);
        uut.switchToTab(0);
        Mockito.verify(getActualTabView(0), Mockito.times(1)).sendComponentStop();
        Mockito.verify(getActualTabView(0), Mockito.times(2)).sendComponentStart();
        Mockito.verify(getActualTabView(1), Mockito.times(1)).sendComponentStart();
        Mockito.verify(getActualTabView(1), Mockito.times(1)).sendComponentStop();
    }

    @Test
    public void setOptionsOfInitialTab() {
        stack.ensureViewIsCreated();
        uut.ensureViewIsCreated();
        uut.onViewAppeared();
        Mockito.verify(tabControllers.get(0), Mockito.times(1)).onViewAppeared();
        Mockito.verify(tabControllers.get(1), Mockito.times(0)).onViewAppeared();
        ReactComponent comp = getComponent();
        Mockito.verify(uut, Mockito.times(1)).applyChildOptions(ArgumentMatchers.any(Options.class), ArgumentMatchers.eq(comp));
    }

    @Test
    public void setOptionsWhenTabChanges() {
        stack.ensureViewIsCreated();
        uut.ensureViewIsCreated();
        tabControllers.get(0).ensureViewIsCreated();
        tabControllers.get(1).ensureViewIsCreated();
        uut.onViewAppeared();
        ReactComponent currentTab = tabView(0);
        Mockito.verify(uut, Mockito.times(1)).applyChildOptions(ArgumentMatchers.any(Options.class), ArgumentMatchers.eq(currentTab));
        assertThat(uut.options.topBar.title.text.get()).isEqualTo(createTabTopBarTitle(0));
        uut.switchToTab(1);
        currentTab = tabView(1);
        Mockito.verify(uut, Mockito.times(1)).applyChildOptions(ArgumentMatchers.any(Options.class), ArgumentMatchers.eq(currentTab));
        assertThat(uut.options.topBar.title.text.get()).isEqualTo(createTabTopBarTitle(1));
        uut.switchToTab(0);
        currentTab = tabView(0);
        Mockito.verify(uut, Mockito.times(2)).applyChildOptions(ArgumentMatchers.any(Options.class), ArgumentMatchers.eq(currentTab));
        assertThat(uut.options.topBar.title.text.get()).isEqualTo(createTabTopBarTitle(0));
    }

    @Test
    public void appliesOptionsOnLayoutWhenVisible() {
        tabControllers.get(0).ensureViewIsCreated();
        stack.ensureViewIsCreated();
        uut.ensureViewIsCreated();
        uut.onViewAppeared();
        Mockito.verify(topTabsLayout, Mockito.times(1)).applyOptions(ArgumentMatchers.any(Options.class));
    }

    @Test
    public void applyOptions_tabsAreRemovedAfterViewDisappears() {
        StackController stackController = TestUtils.newStackController(activity).build();
        stackController.ensureViewIsCreated();
        ViewController first = new SimpleViewController(activity, childRegistry, "first", Options.EMPTY);
        disablePushAnimation(first, uut);
        stackController.push(first, new CommandListenerAdapter());
        stackController.push(uut, new CommandListenerAdapter());
        uut.onViewAppeared();
        assertThat(ViewHelper.isVisible(stackController.getTopBar().getTopTabs())).isTrue();
        disablePopAnimation(uut);
        stackController.pop(EMPTY, new CommandListenerAdapter());
        first.onViewAppeared();
        assertThat(ViewHelper.isVisible(stackController.getTopBar().getTopTabs())).isFalse();
    }

    @Test
    public void onNavigationButtonPressInvokedOnCurrentTab() {
        uut.ensureViewIsCreated();
        uut.onViewAppeared();
        uut.switchToTab(1);
        uut.sendOnNavigationButtonPressed("btn1");
        Mockito.verify(tabControllers.get(1), Mockito.times(1)).sendOnNavigationButtonPressed("btn1");
    }
}

