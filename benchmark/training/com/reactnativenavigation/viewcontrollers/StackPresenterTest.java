package com.reactnativenavigation.viewcontrollers;


import Alignment.Center;
import Alignment.Default;
import Gravity.CENTER;
import Toolbar.LayoutParams;
import Typeface.DEFAULT_BOLD;
import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.mocks.TestComponentLayout;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.OrientationOptions;
import com.reactnativenavigation.parse.SubtitleOptions;
import com.reactnativenavigation.parse.TitleOptions;
import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.parse.params.Button;
import com.reactnativenavigation.parse.params.Colour;
import com.reactnativenavigation.parse.params.Fraction;
import com.reactnativenavigation.parse.params.Number;
import com.reactnativenavigation.parse.params.Text;
import com.reactnativenavigation.presentation.RenderChecker;
import com.reactnativenavigation.presentation.StackPresenter;
import com.reactnativenavigation.utils.CollectionUtils;
import com.reactnativenavigation.utils.TitleBarHelper;
import com.reactnativenavigation.views.titlebar.TitleBarReactView;
import com.reactnativenavigation.views.topbar.TopBar;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class StackPresenterTest extends BaseTest {
    private static final Options EMPTY_OPTIONS = new Options();

    private StackPresenter uut;

    private TestComponentLayout child;

    private TestComponentLayout otherChild;

    private Activity activity;

    private TopBar topBar;

    private RenderChecker renderChecker;

    private Button textBtn1 = TitleBarHelper.textualButton("btn1");

    private Button textBtn2 = TitleBarHelper.textualButton("btn2");

    private Button componentBtn1 = TitleBarHelper.reactViewButton("btn1_");

    private Button componentBtn2 = TitleBarHelper.reactViewButton("btn2_");

    @Test
    public void isRendered() {
        Options o1 = new Options();
        o1.topBar.title.component = component(Default);
        o1.topBar.background.component = component(Default);
        o1.topBar.buttons.right = new ArrayList(Collections.singletonList(componentBtn1));
        uut.applyChildOptions(o1, child);
        uut.isRendered(child);
        ArgumentCaptor<Collection<ViewController>> controllers = ArgumentCaptor.forClass(Collection.class);
        Mockito.verify(renderChecker).areRendered(controllers.capture());
        ArrayList<ViewController> items = new ArrayList(controllers.getValue());
        assertThat(items.contains(uut.getComponentButtons(child).get(0))).isTrue();
        assertThat(items.contains(uut.getTitleComponents().get(child))).isTrue();
        assertThat(items.contains(uut.getBackgroundComponents().get(child))).isTrue();
        assertThat(items.size()).isEqualTo(3);
    }

    @Test
    public void applyChildOptions_setTitleComponent() {
        Options options = new Options();
        options.topBar.title.component = component(Default);
        uut.applyChildOptions(options, child);
        Mockito.verify(topBar).setTitleComponent(uut.getTitleComponents().get(child).getView());
    }

    @Test
    public void applyChildOptions_setTitleComponentCreatesOnce() {
        Options options = new Options();
        options.topBar.title.component = component(Default);
        uut.applyChildOptions(options, child);
        uut.applyChildOptions(new Options(), otherChild);
        TitleBarReactViewController titleController = uut.getTitleComponents().get(child);
        uut.applyChildOptions(options, child);
        assertThat(uut.getTitleComponents().size()).isOne();
        assertThat(uut.getTitleComponents().get(child)).isEqualTo(titleController);
    }

    @Test
    public void applyChildOptions_setTitleComponentAlignment() {
        Options options = new Options();
        options.topBar.title.component = component(Center);
        uut.applyChildOptions(options, child);
        ArgumentCaptor<View> captor = ArgumentCaptor.forClass(View.class);
        Mockito.verify(topBar).setTitleComponent(captor.capture());
        Toolbar.LayoutParams lp = ((Toolbar.LayoutParams) (captor.getValue().getLayoutParams()));
        assertThat(lp.gravity).isEqualTo(CENTER);
    }

    @Test
    public void onChildDestroyed_destroyTitleComponent() {
        Options options = new Options();
        options.topBar.title.component = component(Default);
        uut.applyChildOptions(options, child);
        TitleBarReactView titleView = uut.getTitleComponents().get(child).getView();
        uut.onChildDestroyed(child);
        Mockito.verify(titleView).destroy();
    }

    @Test
    public void mergeOrientation() throws Exception {
        Options options = new Options();
        uut.mergeChildOptions(options, StackPresenterTest.EMPTY_OPTIONS, child);
        Mockito.verify(uut, Mockito.times(0)).applyOrientation(ArgumentMatchers.any());
        JSONObject orientation = new JSONObject().put("orientation", "landscape");
        options.layout.orientation = OrientationOptions.parse(orientation);
        uut.mergeChildOptions(options, StackPresenterTest.EMPTY_OPTIONS, child);
        Mockito.verify(uut, Mockito.times(1)).applyOrientation(options.layout.orientation);
    }

    @Test
    public void mergeButtons() {
        uut.mergeChildOptions(StackPresenterTest.EMPTY_OPTIONS, StackPresenterTest.EMPTY_OPTIONS, child);
        Mockito.verify(topBar, Mockito.times(0)).setRightButtons(ArgumentMatchers.any());
        Mockito.verify(topBar, Mockito.times(0)).setLeftButtons(ArgumentMatchers.any());
        Options options = new Options();
        options.topBar.buttons.right = new ArrayList();
        uut.mergeChildOptions(options, StackPresenterTest.EMPTY_OPTIONS, child);
        Mockito.verify(topBar, Mockito.times(1)).setRightButtons(ArgumentMatchers.any());
        options.topBar.buttons.left = new ArrayList();
        uut.mergeChildOptions(options, StackPresenterTest.EMPTY_OPTIONS, child);
        Mockito.verify(topBar, Mockito.times(1)).setLeftButtons(ArgumentMatchers.any());
    }

    @Test
    public void mergeButtons_previousRightButtonsAreDestroyed() {
        Options options = new Options();
        options.topBar.buttons.right = new ArrayList(Collections.singletonList(componentBtn1));
        uut.applyChildOptions(options, child);
        List<TitleBarButtonController> initialButtons = uut.getComponentButtons(child);
        CollectionUtils.forEach(initialButtons, ViewController::ensureViewIsCreated);
        options.topBar.buttons.right = new ArrayList(Collections.singletonList(componentBtn2));
        uut.mergeChildOptions(options, new Options(), child);
        for (TitleBarButtonController button : initialButtons) {
            assertThat(button.isDestroyed()).isTrue();
        }
    }

    @Test
    public void mergeButtons_mergingRightButtonsOnlyDestroysRightButtons() {
        Options a = new Options();
        a.topBar.buttons.right = new ArrayList(Collections.singletonList(componentBtn1));
        a.topBar.buttons.left = new ArrayList(Collections.singletonList(componentBtn2));
        uut.applyChildOptions(a, child);
        List<TitleBarButtonController> initialButtons = uut.getComponentButtons(child);
        CollectionUtils.forEach(initialButtons, ViewController::ensureViewIsCreated);
        Options b = new Options();
        b.topBar.buttons.right = new ArrayList(Collections.singletonList(componentBtn2));
        uut.mergeChildOptions(b, new Options(), child);
        assertThat(initialButtons.get(0).isDestroyed()).isTrue();
        assertThat(initialButtons.get(1).isDestroyed()).isFalse();
    }

    @Test
    public void mergeButtons_mergingLeftButtonsOnlyDestroysLeftButtons() {
        Options a = new Options();
        a.topBar.buttons.right = new ArrayList(Collections.singletonList(componentBtn1));
        a.topBar.buttons.left = new ArrayList(Collections.singletonList(componentBtn2));
        uut.applyChildOptions(a, child);
        List<TitleBarButtonController> initialButtons = uut.getComponentButtons(child);
        CollectionUtils.forEach(initialButtons, ViewController::ensureViewIsCreated);
        Options b = new Options();
        b.topBar.buttons.left = new ArrayList(Collections.singletonList(componentBtn2));
        uut.mergeChildOptions(b, new Options(), child);
        assertThat(initialButtons.get(0).isDestroyed()).isFalse();
        assertThat(initialButtons.get(1).isDestroyed()).isTrue();
    }

    @Test
    public void mergeTopBarOptions() {
        Options options = new Options();
        uut.mergeChildOptions(options, StackPresenterTest.EMPTY_OPTIONS, child);
        assertTopBarOptions(options, 0);
        TitleOptions title = new TitleOptions();
        title.text = new Text("abc");
        title.component.name = new Text("someComponent");
        title.component.componentId = new Text("compId");
        title.color = new Colour(0);
        title.fontSize = new Fraction(1.0F);
        title.fontFamily = Typeface.DEFAULT_BOLD;
        options.topBar.title = title;
        SubtitleOptions subtitleOptions = new SubtitleOptions();
        subtitleOptions.text = new Text("Sub");
        subtitleOptions.color = new Colour(1);
        options.topBar.subtitle = subtitleOptions;
        options.topBar.background.color = new Colour(0);
        options.topBar.testId = new Text("test123");
        options.topBar.animate = new Bool(false);
        options.topBar.visible = new Bool(false);
        options.topBar.drawBehind = new Bool(false);
        options.topBar.hideOnScroll = new Bool(false);
        options.topBar.validate();
        uut.mergeChildOptions(options, StackPresenterTest.EMPTY_OPTIONS, child);
        assertTopBarOptions(options, 1);
        options.topBar.drawBehind = new Bool(true);
        uut.mergeChildOptions(options, StackPresenterTest.EMPTY_OPTIONS, child);
        drawBehindTopBar();
    }

    @Test
    public void mergeTopTabsOptions() {
        Options options = new Options();
        uut.mergeChildOptions(options, StackPresenterTest.EMPTY_OPTIONS, child);
        Mockito.verify(topBar, Mockito.times(0)).applyTopTabsColors(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(topBar, Mockito.times(0)).applyTopTabsFontSize(ArgumentMatchers.any());
        Mockito.verify(topBar, Mockito.times(0)).setTopTabsVisible(ArgumentMatchers.anyBoolean());
        options.topTabs.selectedTabColor = new Colour(1);
        options.topTabs.unselectedTabColor = new Colour(1);
        options.topTabs.fontSize = new Number(1);
        options.topTabs.visible = new Bool(true);
        uut.mergeChildOptions(options, StackPresenterTest.EMPTY_OPTIONS, child);
        Mockito.verify(topBar, Mockito.times(1)).applyTopTabsColors(options.topTabs.selectedTabColor, options.topTabs.unselectedTabColor);
        Mockito.verify(topBar, Mockito.times(1)).applyTopTabsFontSize(options.topTabs.fontSize);
        Mockito.verify(topBar, Mockito.times(1)).setTopTabsVisible(ArgumentMatchers.anyBoolean());
    }

    @Test
    public void mergeTopTabOptions() {
        Options options = new Options();
        uut.mergeChildOptions(options, StackPresenterTest.EMPTY_OPTIONS, child);
        Mockito.verify(topBar, Mockito.times(0)).setTopTabFontFamily(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        options.topTabOptions.tabIndex = 1;
        options.topTabOptions.fontFamily = Typeface.DEFAULT_BOLD;
        uut.mergeChildOptions(options, StackPresenterTest.EMPTY_OPTIONS, child);
        Mockito.verify(topBar, Mockito.times(1)).setTopTabFontFamily(1, DEFAULT_BOLD);
    }

    @Test
    public void applyInitialChildLayoutOptions() {
        Options options = new Options();
        options.topBar.visible = new Bool(false);
        options.topBar.animate = new Bool(true);
        uut.applyInitialChildLayoutOptions(options);
        Mockito.verify(topBar).hide();
    }

    @Test
    public void mergeOptions_defaultOptionsAreNotApplied() {
        Options defaultOptions = new Options();
        defaultOptions.topBar.background.color = new Colour(10);
        uut.setDefaultOptions(defaultOptions);
        Options childOptions = new Options();
        childOptions.topBar.title.text = new Text("someText");
        uut.mergeChildOptions(childOptions, StackPresenterTest.EMPTY_OPTIONS, child);
        Mockito.verify(topBar, Mockito.times(0)).setBackgroundColor(ArgumentMatchers.anyInt());
    }

    @Test
    public void applyButtons_buttonColorIsMergedToButtons() {
        Options options = new Options();
        Button rightButton1 = new Button();
        Button rightButton2 = new Button();
        Button leftButton = new Button();
        options.topBar.rightButtonColor = new Colour(10);
        options.topBar.leftButtonColor = new Colour(100);
        options.topBar.buttons.right = new ArrayList();
        options.topBar.buttons.right.add(rightButton1);
        options.topBar.buttons.right.add(rightButton2);
        options.topBar.buttons.left = new ArrayList();
        options.topBar.buttons.left.add(leftButton);
        uut.applyChildOptions(options, child);
        ArgumentCaptor<List<TitleBarButtonController>> rightCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(topBar).setRightButtons(rightCaptor.capture());
        assertThat(rightCaptor.getValue().get(0).getButton().color.get()).isEqualTo(options.topBar.rightButtonColor.get());
        assertThat(rightCaptor.getValue().get(1).getButton().color.get()).isEqualTo(options.topBar.rightButtonColor.get());
        assertThat(rightCaptor.getValue().get(0)).isNotEqualTo(rightButton1);
        assertThat(rightCaptor.getValue().get(1)).isNotEqualTo(rightButton2);
        ArgumentCaptor<List<TitleBarButtonController>> leftCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(topBar).setLeftButtons(leftCaptor.capture());
        assertThat(leftCaptor.getValue().get(0).getButton().color).isEqualTo(options.topBar.leftButtonColor);
        assertThat(leftCaptor.getValue().get(0)).isNotEqualTo(leftButton);
    }

    @Test
    public void mergeChildOptions_buttonColorIsResolvedFromAppliedOptions() {
        Options appliedOptions = new Options();
        appliedOptions.topBar.rightButtonColor = new Colour(10);
        appliedOptions.topBar.leftButtonColor = new Colour(100);
        Options options2 = new Options();
        Button rightButton1 = new Button();
        Button rightButton2 = new Button();
        Button leftButton = new Button();
        options2.topBar.buttons.right = new ArrayList();
        options2.topBar.buttons.right.add(rightButton1);
        options2.topBar.buttons.right.add(rightButton2);
        options2.topBar.buttons.left = new ArrayList();
        options2.topBar.buttons.left.add(leftButton);
        uut.mergeChildOptions(options2, appliedOptions, child);
        ArgumentCaptor<List<TitleBarButtonController>> rightCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(topBar, Mockito.times(1)).setRightButtons(rightCaptor.capture());
        assertThat(rightCaptor.getValue().get(0).getButton().color.get()).isEqualTo(appliedOptions.topBar.rightButtonColor.get());
        assertThat(rightCaptor.getValue().get(1).getButton().color.get()).isEqualTo(appliedOptions.topBar.rightButtonColor.get());
        assertThat(rightCaptor.getValue().get(0)).isNotEqualTo(rightButton1);
        assertThat(rightCaptor.getValue().get(1)).isNotEqualTo(rightButton2);
        ArgumentCaptor<List<TitleBarButtonController>> leftCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(topBar, Mockito.times(1)).setLeftButtons(leftCaptor.capture());
        assertThat(leftCaptor.getValue().get(0).getButton().color.get()).isEqualTo(appliedOptions.topBar.leftButtonColor.get());
        assertThat(leftCaptor.getValue().get(0)).isNotEqualTo(leftButton);
    }

    @Test
    public void mergeChildOptions_buttonColorIsResolvedFromMergedOptions() {
        Options resolvedOptions = new Options();
        resolvedOptions.topBar.rightButtonColor = new Colour(10);
        resolvedOptions.topBar.leftButtonColor = new Colour(100);
        Options options2 = new Options();
        Button rightButton1 = new Button();
        Button rightButton2 = new Button();
        Button leftButton = new Button();
        options2.topBar.buttons.right = new ArrayList();
        options2.topBar.buttons.right.add(rightButton1);
        options2.topBar.buttons.right.add(rightButton2);
        options2.topBar.buttons.left = new ArrayList();
        options2.topBar.buttons.left.add(leftButton);
        uut.mergeChildOptions(options2, resolvedOptions, child);
        ArgumentCaptor<List<TitleBarButtonController>> rightCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(topBar).setRightButtons(rightCaptor.capture());
        assertThat(rightCaptor.getValue().get(0).getButton().color.get()).isEqualTo(resolvedOptions.topBar.rightButtonColor.get());
        assertThat(rightCaptor.getValue().get(1).getButton().color.get()).isEqualTo(resolvedOptions.topBar.rightButtonColor.get());
        assertThat(rightCaptor.getValue().get(0)).isNotEqualTo(rightButton1);
        assertThat(rightCaptor.getValue().get(1)).isNotEqualTo(rightButton2);
        ArgumentCaptor<List<TitleBarButtonController>> leftCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(topBar).setLeftButtons(leftCaptor.capture());
        assertThat(leftCaptor.getValue().get(0).getButton().color.get()).isEqualTo(resolvedOptions.topBar.leftButtonColor.get());
        assertThat(leftCaptor.getValue().get(0)).isNotEqualTo(leftButton);
    }

    @Test
    public void getButtonControllers_buttonControllersArePassedToTopBar() {
        Options options = new Options();
        options.topBar.buttons.right = new ArrayList(Collections.singletonList(textBtn1));
        options.topBar.buttons.left = new ArrayList(Collections.singletonList(textBtn1));
        uut.applyChildOptions(options, child);
        ArgumentCaptor<List<TitleBarButtonController>> rightCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<TitleBarButtonController>> leftCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(topBar).setRightButtons(rightCaptor.capture());
        Mockito.verify(topBar).setLeftButtons(leftCaptor.capture());
        assertThat(rightCaptor.getValue().size()).isOne();
        assertThat(leftCaptor.getValue().size()).isOne();
    }

    @Test
    public void getButtonControllers_storesButtonsByComponent() {
        Options options = new Options();
        options.topBar.buttons.right = new ArrayList(Collections.singletonList(textBtn1));
        options.topBar.buttons.left = new ArrayList(Collections.singletonList(textBtn2));
        uut.applyChildOptions(options, child);
        List<TitleBarButtonController> componentButtons = uut.getComponentButtons(child);
        assertThat(componentButtons.size()).isEqualTo(2);
        assertThat(componentButtons.get(0).getButton().text.get()).isEqualTo(textBtn1.text.get());
        assertThat(componentButtons.get(1).getButton().text.get()).isEqualTo(textBtn2.text.get());
    }

    @Test
    public void getButtonControllers_createdOnce() {
        Options options = new Options();
        options.topBar.buttons.right = new ArrayList(Collections.singletonList(textBtn1));
        options.topBar.buttons.left = new ArrayList(Collections.singletonList(textBtn2));
        uut.applyChildOptions(options, child);
        List<TitleBarButtonController> buttons1 = uut.getComponentButtons(child);
        uut.applyChildOptions(options, child);
        List<TitleBarButtonController> buttons2 = uut.getComponentButtons(child);
        for (int i = 0; i < 2; i++) {
            assertThat(buttons1.get(i)).isEqualTo(buttons2.get(i));
        }
    }

    @Test
    public void applyButtons_doesNotDestroyOtherComponentButtons() {
        Options options = new Options();
        options.topBar.buttons.right = new ArrayList(Collections.singletonList(componentBtn1));
        options.topBar.buttons.left = new ArrayList(Collections.singletonList(componentBtn2));
        uut.applyChildOptions(options, child);
        List<TitleBarButtonController> buttons = uut.getComponentButtons(child);
        CollectionUtils.forEach(buttons, ViewController::ensureViewIsCreated);
        uut.applyChildOptions(options, otherChild);
        for (TitleBarButtonController button : buttons) {
            assertThat(button.isDestroyed()).isFalse();
        }
    }

    @Test
    public void onChildDestroyed_destroyedButtons() {
        Options options = new Options();
        options.topBar.buttons.right = new ArrayList(Collections.singletonList(componentBtn1));
        options.topBar.buttons.left = new ArrayList(Collections.singletonList(componentBtn2));
        uut.applyChildOptions(options, child);
        List<TitleBarButtonController> buttons = uut.getComponentButtons(child);
        CollectionUtils.forEach(buttons, ViewController::ensureViewIsCreated);
        uut.onChildDestroyed(child);
        for (TitleBarButtonController button : buttons) {
            assertThat(button.isDestroyed()).isTrue();
        }
        assertThat(uut.getComponentButtons(child, null)).isNull();
    }
}

