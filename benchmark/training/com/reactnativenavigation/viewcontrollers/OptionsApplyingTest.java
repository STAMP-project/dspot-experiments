package com.reactnativenavigation.viewcontrollers;


import Color.RED;
import RelativeLayout.LayoutParams;
import View.GONE;
import android.app.Activity;
import android.graphics.Color;
import android.widget.RelativeLayout;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.mocks.TitleBarReactViewCreatorMock;
import com.reactnativenavigation.mocks.TopBarBackgroundViewCreatorMock;
import com.reactnativenavigation.mocks.TopBarButtonCreatorMock;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.parse.params.Fraction;
import com.reactnativenavigation.parse.params.Text;
import com.reactnativenavigation.presentation.RenderChecker;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.utils.ImageLoader;
import com.reactnativenavigation.viewcontrollers.stack.StackController;
import com.reactnativenavigation.viewcontrollers.topbar.TopBarController;
import com.reactnativenavigation.views.topbar.TopBar;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class OptionsApplyingTest extends BaseTest {
    private Activity activity;

    private StackController stack;

    private ComponentViewController uut;

    private IReactView view;

    private Options initialNavigationOptions;

    private TopBar topBar;

    @SuppressWarnings("ConstantConditions")
    @Test
    public void applyNavigationOptionsHandlesNoParentStack() {
        uut.setParentController(null);
        assertThat(uut.getParentController()).isNull();
        uut.ensureViewIsCreated();
        uut.onViewAppeared();
        assertThat(uut.getParentController()).isNull();
    }

    @Test
    public void initialOptionsAppliedOnAppear() {
        uut.options.topBar.title.text = new Text("the title");
        StackController stackController = new com.reactnativenavigation.viewcontrollers.stack.StackControllerBuilder(activity).setTopBarController(new TopBarController()).setId("stackId").setInitialOptions(new Options()).setStackPresenter(new com.reactnativenavigation.presentation.StackPresenter(activity, new TitleBarReactViewCreatorMock(), new TopBarBackgroundViewCreatorMock(), new TopBarButtonCreatorMock(), new ImageLoader(), new RenderChecker(), new Options())).build();
        stackController.ensureViewIsCreated();
        stackController.push(uut, new CommandListenerAdapter());
        assertThat(stackController.getTopBar().getTitle()).isEmpty();
        uut.onViewAppeared();
        assertThat(stackController.getTopBar().getTitle()).isEqualTo("the title");
    }

    @Test
    public void mergeNavigationOptionsUpdatesCurrentOptions() {
        assertThat(uut.options.topBar.title.text.get("")).isEmpty();
        Options options = new Options();
        options.topBar.title.text = new Text("new title");
        uut.mergeOptions(options);
        assertThat(uut.options.topBar.title.text.get()).isEqualTo("new title");
    }

    @Test
    public void reappliesOptionsOnMerge() {
        assertThat(stack.getTopBar().getTitle()).isEmpty();
        stack.push(uut, new CommandListenerAdapter());
        Options opts = new Options();
        opts.topBar.title.text = new Text("the new title");
        uut.mergeOptions(opts);
        assertThat(stack.getTopBar().getTitle()).isEqualTo("the new title");
    }

    @Test
    public void appliesTopBackBackgroundColor() {
        uut.options.topBar.background.color = new com.reactnativenavigation.parse.params.Colour(Color.RED);
        stack.push(uut, new CommandListenerAdapter());
        assertThat(getColor()).isEqualTo(RED);
    }

    @Test
    public void appliesTopBarTextColor() {
        assertThat(uut.initialOptions).isSameAs(initialNavigationOptions);
        uut.options.topBar.title.text = new Text("the title");
        uut.options.topBar.title.color = new com.reactnativenavigation.parse.params.Colour(Color.RED);
        stack.push(uut, new CommandListenerAdapter());
        assertThat(stack.getTopBar().getTitleTextView()).isNotEqualTo(null);
        assertThat(stack.getTopBar().getTitleTextView().getCurrentTextColor()).isEqualTo(RED);
    }

    @SuppressWarnings("MagicNumber")
    @Test
    public void appliesTopBarTextSize() {
        assertThat(uut.initialOptions).isSameAs(initialNavigationOptions);
        initialNavigationOptions.topBar.title.text = new Text("the title");
        uut.options.topBar.title.text = new Text("the title");
        uut.options.topBar.title.fontSize = new Fraction(18);
        stack.push(uut, new CommandListenerAdapter());
        assertThat(stack.getTopBar().getTitleTextView()).isNotEqualTo(null);
        assertThat(stack.getTopBar().getTitleTextView().getTextSize()).isEqualTo(18);
    }

    @Test
    public void appliesTopBarVisible() {
        stack.push(uut, new CommandListenerAdapter());
        assertThat(uut.initialOptions).isSameAs(initialNavigationOptions);
        initialNavigationOptions.topBar.title.text = new Text("the title");
        assertThat(stack.getTopBar().getVisibility()).isNotEqualTo(GONE);
        Options opts = new Options();
        opts.topBar.visible = new Bool(false);
        opts.topBar.animate = new Bool(false);
        uut.mergeOptions(opts);
        assertThat(stack.getTopBar().getVisibility()).isEqualTo(GONE);
    }

    @Test
    public void appliesDrawUnder() {
        uut.options.topBar.title.text = new Text("the title");
        uut.options.topBar.drawBehind = new Bool(false);
        uut.ensureViewIsCreated();
        stack.ensureViewIsCreated();
        stack.push(uut, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.onViewAppeared();
                RelativeLayout.LayoutParams uutLayoutParams = ((RelativeLayout.LayoutParams) (uut.getComponent().asView().getLayoutParams()));
                assertThat(uutLayoutParams.topMargin).isNotEqualTo(0);
                Options opts = new Options();
                opts.topBar.drawBehind = new Bool(true);
                uut.mergeOptions(opts);
                uutLayoutParams = ((RelativeLayout.LayoutParams) (uut.getComponent().asView().getLayoutParams()));
                assertThat(uutLayoutParams.getRule(BELOW)).isNotEqualTo(stack.getTopBar().getId());
            }
        });
    }

    @Test
    public void appliesTopBarComponent() {
        uut.options.topBar.background.component.name = new Text("someComponent");
        uut.options.topBar.background.component.componentId = new Text("id");
        stack.push(uut, new CommandListenerAdapter());
        Mockito.verify(topBar, Mockito.times(1)).setBackgroundComponent(ArgumentMatchers.any());
    }

    @Test
    public void appliesSubtitle() {
        uut.options.topBar.subtitle.text = new Text("sub");
        stack.push(uut, new CommandListenerAdapter());
        assertThat(stack.getTopBar().getTitleBar().getSubtitle()).isEqualTo("sub");
    }
}

