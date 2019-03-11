package com.reactnativenavigation.viewcontrollers.bottomtabs;


import View.INVISIBLE;
import View.VISIBLE;
import ViewGroup.LayoutParams.MATCH_PARENT;
import ViewGroup.MarginLayoutParams;
import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.TestUtils;
import com.reactnativenavigation.mocks.ImageLoaderMock;
import com.reactnativenavigation.mocks.SimpleViewController;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.parse.params.Number;
import com.reactnativenavigation.parse.params.Text;
import com.reactnativenavigation.presentation.BottomTabsPresenter;
import com.reactnativenavigation.react.EventEmitter;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.utils.ImageLoader;
import com.reactnativenavigation.utils.OptionHelper;
import com.reactnativenavigation.utils.ViewUtils;
import com.reactnativenavigation.viewcontrollers.ChildControllersRegistry;
import com.reactnativenavigation.viewcontrollers.ViewController;
import com.reactnativenavigation.viewcontrollers.stack.StackController;
import com.reactnativenavigation.views.BottomTabs;
import com.reactnativenavigation.views.ReactComponent;
import edu.emory.mathcs.backport.java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class BottomTabsControllerTest extends BaseTest {
    private Activity activity;

    private BottomTabs bottomTabs;

    private BottomTabsController uut;

    private Options initialOptions = new Options();

    private ViewController child1;

    private ViewController child2;

    private ViewController child3;

    private StackController child4;

    private ViewController child5;

    private ViewController child6;

    private Options tabOptions = OptionHelper.createBottomTabOptions();

    private ImageLoader imageLoaderMock = ImageLoaderMock.mock();

    private EventEmitter eventEmitter;

    private ChildControllersRegistry childRegistry;

    private List<ViewController> tabs;

    private BottomTabsPresenter presenter;

    private BottomTabsAttacher tabsAttacher;

    @Test
    public void containsRelativeLayoutView() {
        assertThat(uut.getView()).isInstanceOf(RelativeLayout.class);
        assertThat(uut.getView().getChildAt(0)).isInstanceOf(BottomTabs.class);
    }

    @Test(expected = RuntimeException.class)
    public void setTabs_ThrowWhenMoreThan5() {
        tabs.add(new SimpleViewController(activity, childRegistry, "6", tabOptions));
        createBottomTabs();
    }

    @Test
    public void parentControllerIsSet() {
        uut = createBottomTabs();
        for (ViewController tab : tabs) {
            assertThat(tab.getParentController()).isEqualTo(uut);
        }
    }

    @Test
    public void setTabs_allChildViewsAreAttachedToHierarchy() {
        uut.onViewAppeared();
        assertThat(uut.getView().getChildCount()).isEqualTo(6);
        for (ViewController child : uut.getChildControllers()) {
            assertThat(child.getView().getParent()).isNotNull();
        }
    }

    @Test
    public void setTabs_firstChildIsVisibleOtherAreGone() {
        uut.onViewAppeared();
        for (int i = 0; i < (uut.getChildControllers().size()); i++) {
            assertThat(uut.getView().getChildAt((i + 1))).isEqualTo(getView());
            assertThat(uut.getView().getChildAt((i + 1)).getVisibility()).isEqualTo((i == 0 ? View.VISIBLE : View.INVISIBLE));
        }
    }

    @Test
    public void createView_layoutOptionsAreAppliedToTabs() {
        uut.ensureViewIsCreated();
        for (int i = 0; i < (tabs.size()); i++) {
            Mockito.verify(presenter, Mockito.times(1)).applyLayoutParamsOptions(ArgumentMatchers.any(), ArgumentMatchers.eq(i));
            assertThat(childLayoutParams(i).width).isEqualTo(MATCH_PARENT);
            assertThat(childLayoutParams(i).height).isEqualTo(MATCH_PARENT);
        }
    }

    @Test
    public void onTabSelected() {
        uut.ensureViewIsCreated();
        assertThat(uut.getSelectedIndex()).isZero();
        assertThat(getView().getVisibility()).isEqualTo(VISIBLE);
        uut.onTabSelected(3, false);
        assertThat(uut.getSelectedIndex()).isEqualTo(3);
        assertThat(getView().getVisibility()).isEqualTo(INVISIBLE);
        assertThat(getView().getVisibility()).isEqualTo(VISIBLE);
        Mockito.verify(eventEmitter, Mockito.times(1)).emitBottomTabSelected(0, 3);
    }

    @Test
    public void onTabReSelected() {
        uut.ensureViewIsCreated();
        assertThat(uut.getSelectedIndex()).isZero();
        uut.onTabSelected(0, true);
        assertThat(uut.getSelectedIndex()).isEqualTo(0);
        assertThat(getView().getParent()).isNotNull();
        Mockito.verify(eventEmitter, Mockito.times(1)).emitBottomTabSelected(0, 0);
    }

    @Test
    public void handleBack_DelegatesToSelectedChild() {
        uut.ensureViewIsCreated();
        assertThat(uut.handleBack(new CommandListenerAdapter())).isFalse();
        uut.selectTab(4);
        assertThat(uut.handleBack(new CommandListenerAdapter())).isTrue();
        Mockito.verify(child5, Mockito.times(1)).handleBack(ArgumentMatchers.any());
    }

    @Test
    public void applyOptions_bottomTabsOptionsAreClearedAfterApply() {
        ViewUtils.removeFromParent(uut.getView());
        Options options = new Options();
        options.bottomTabsOptions.backgroundColor = new com.reactnativenavigation.parse.params.Colour(Color.RED);
        child1.mergeOptions(options);
        uut.ensureViewIsCreated();
        StackController stack = Mockito.spy(createStack("stack"));
        stack.ensureViewIsCreated();
        stack.push(uut, new CommandListenerAdapter());
        child1.onViewAppeared();
        ArgumentCaptor<Options> optionsCaptor = ArgumentCaptor.forClass(Options.class);
        ArgumentCaptor<ReactComponent> viewCaptor = ArgumentCaptor.forClass(ReactComponent.class);
        Mockito.verify(stack, Mockito.times(1)).applyChildOptions(optionsCaptor.capture(), viewCaptor.capture());
        assertThat(viewCaptor.getValue()).isEqualTo(child1.getView());
        assertThat(optionsCaptor.getValue().bottomTabsOptions.backgroundColor.hasValue()).isFalse();
    }

    @Test
    public void applyOptions_bottomTabsCreateViewOnlyOnce() {
        Mockito.verify(presenter).applyOptions(ArgumentMatchers.any());
        Mockito.verify(bottomTabs, Mockito.times(2)).superCreateItems();// first time when view is created, second time when options are applied

    }

    @Test
    public void mergeOptions_currentTabIndex() {
        uut.ensureViewIsCreated();
        assertThat(uut.getSelectedIndex()).isZero();
        Options options = new Options();
        options.bottomTabsOptions.currentTabIndex = new Number(1);
        uut.mergeOptions(options);
        assertThat(uut.getSelectedIndex()).isOne();
        Mockito.verify(eventEmitter, Mockito.times(0)).emitBottomTabSelected(ArgumentMatchers.any(Integer.class), ArgumentMatchers.any(Integer.class));
    }

    @Test
    public void mergeOptions_drawBehind() {
        uut.ensureViewIsCreated();
        child1.onViewAppeared();
        uut.selectTab(0);
        assertThat(childLayoutParams(0).bottomMargin).isEqualTo(uut.getBottomTabs().getHeight());
        Options o1 = new Options();
        o1.bottomTabsOptions.drawBehind = new Bool(true);
        child1.mergeOptions(o1);
        assertThat(childLayoutParams(0).bottomMargin).isEqualTo(0);
        Options o2 = new Options();
        o2.topBar.title.text = new Text("Some text");
        child1.mergeOptions(o1);
        assertThat(childLayoutParams(0).bottomMargin).isEqualTo(0);
    }

    @Test
    public void applyChildOptions_resolvedOptionsAreUsed() {
        Options childOptions = new Options();
        SimpleViewController pushedScreen = new SimpleViewController(activity, childRegistry, "child4.1", childOptions);
        disablePushAnimation(pushedScreen);
        child4 = createStack(pushedScreen);
        tabs = new java.util.ArrayList(Collections.singletonList(child4));
        tabsAttacher = new BottomTabsAttacher(tabs, presenter);
        initialOptions.bottomTabsOptions.currentTabIndex = new Number(0);
        Options resolvedOptions = new Options();
        uut = new BottomTabsController(activity, tabs, childRegistry, eventEmitter, imageLoaderMock, "uut", initialOptions, new com.reactnativenavigation.presentation.Presenter(activity, new Options()), tabsAttacher, presenter, new com.reactnativenavigation.presentation.BottomTabPresenter(activity, tabs, ImageLoaderMock.mock(), new Options())) {
            @Override
            public Options resolveCurrentOptions() {
                return resolvedOptions;
            }

            @NonNull
            @Override
            protected BottomTabs createBottomTabs() {
                return new BottomTabs(activity) {
                    @Override
                    protected void createItems() {
                    }
                };
            }
        };
        activity.setContentView(uut.getView());
        Mockito.verify(presenter, Mockito.times(2)).applyChildOptions(ArgumentMatchers.eq(resolvedOptions), ArgumentMatchers.any());
    }

    @Test
    public void child_mergeOptions_currentTabIndex() {
        uut.ensureViewIsCreated();
        assertThat(uut.getSelectedIndex()).isZero();
        Options options = new Options();
        options.bottomTabsOptions.currentTabIndex = new Number(1);
        child1.mergeOptions(options);
        assertThat(uut.getSelectedIndex()).isOne();
    }

    @Test
    public void resolveCurrentOptions_returnsFirstTabIfInvokedBeforeViewIsCreated() {
        uut = createBottomTabs();
        assertThat(uut.getCurrentChild()).isEqualTo(tabs.get(0));
    }

    @Test
    public void buttonPressInvokedOnCurrentTab() {
        uut.ensureViewIsCreated();
        uut.selectTab(4);
        uut.sendOnNavigationButtonPressed("btn1");
        Mockito.verify(child5, Mockito.times(1)).sendOnNavigationButtonPressed("btn1");
    }

    @Test
    public void push() {
        uut.ensureViewIsCreated();
        uut.selectTab(3);
        SimpleViewController stackChild = new SimpleViewController(activity, childRegistry, "stackChild", new Options());
        SimpleViewController stackChild2 = new SimpleViewController(activity, childRegistry, "stackChild", new Options());
        disablePushAnimation(stackChild, stackChild2);
        TestUtils.hideBackButton(stackChild2);
        child4.push(stackChild, new CommandListenerAdapter());
        assertThat(child4.size()).isOne();
        child4.push(stackChild2, new CommandListenerAdapter());
        assertThat(child4.size()).isEqualTo(2);
    }

    @Test
    public void deepChildOptionsAreApplied() {
        child6.options.topBar.drawBehind = new Bool(false);
        disablePushAnimation(child6);
        child4.push(child6, new CommandListenerAdapter());
        assertThat(child4.size()).isOne();
        assertThat(uut.getSelectedIndex()).isZero();
        Mockito.verify(child6, Mockito.times(0)).onViewAppeared();
        assertThat(child4.getTopBar().getHeight()).isNotZero().isEqualTo(((ViewGroup.MarginLayoutParams) (child6.getView().getLayoutParams())).topMargin);
    }

    @Test
    public void oneTimeOptionsAreAppliedOnce() {
        Options options = new Options();
        options.bottomTabsOptions.currentTabIndex = new Number(1);
        assertThat(uut.getSelectedIndex()).isZero();
        uut.mergeOptions(options);
        assertThat(uut.getSelectedIndex()).isOne();
        assertThat(uut.options.bottomTabsOptions.currentTabIndex.hasValue()).isFalse();
        assertThat(uut.initialOptions.bottomTabsOptions.currentTabIndex.hasValue()).isFalse();
    }

    @Test
    public void selectTab() {
        uut.selectTab(1);
        Mockito.verify(tabsAttacher).onTabSelected(tabs.get(1));
    }

    @Test
    public void destroy() {
        uut.destroy();
        Mockito.verify(tabsAttacher).destroy();
    }
}

