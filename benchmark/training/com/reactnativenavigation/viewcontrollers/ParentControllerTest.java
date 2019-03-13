package com.reactnativenavigation.viewcontrollers;


import android.app.Activity;
import android.view.ViewGroup;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.TestUtils;
import com.reactnativenavigation.mocks.SimpleViewController;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.params.Text;
import com.reactnativenavigation.presentation.Presenter;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.viewcontrollers.stack.StackController;
import com.reactnativenavigation.views.ReactComponent;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class ParentControllerTest extends BaseTest {
    private static final String INITIAL_TITLE = "initial title";

    private Activity activity;

    private ChildControllersRegistry childRegistry;

    private List<ViewController> children;

    private ParentController uut;

    private Presenter presenter;

    @Test
    public void holdsViewGroup() {
        assertThat(uut.getView()).isInstanceOf(ViewGroup.class);
    }

    @Test
    public void mustHaveChildControllers() {
        assertThat(uut.getChildControllers()).isNotNull();
    }

    @Test
    public void findControllerById_ChildById() {
        SimpleViewController child1 = new SimpleViewController(activity, childRegistry, "child1", new Options());
        SimpleViewController child2 = new SimpleViewController(activity, childRegistry, "child2", new Options());
        children.add(child1);
        children.add(child2);
        assertThat(uut.findController("uut")).isEqualTo(uut);
        assertThat(uut.findController("child1")).isEqualTo(child1);
    }

    @Test
    public void findControllerById_Recursive() {
        StackController stackController = TestUtils.newStackController(activity).build();
        stackController.ensureViewIsCreated();
        SimpleViewController child1 = new SimpleViewController(activity, childRegistry, "child1", new Options());
        SimpleViewController child2 = new SimpleViewController(activity, childRegistry, "child2", new Options());
        stackController.push(child1, new CommandListenerAdapter());
        stackController.push(child2, new CommandListenerAdapter());
        children.add(stackController);
        assertThat(uut.findController("child2")).isEqualTo(child2);
    }

    @Test
    public void destroy_DestroysChildren() {
        ViewController child1 = Mockito.spy(new SimpleViewController(activity, childRegistry, "child1", new Options()));
        children.add(child1);
        Mockito.verify(child1, Mockito.times(0)).destroy();
        uut.destroy();
        Mockito.verify(child1, Mockito.times(1)).destroy();
    }

    @Test
    public void optionsAreClearedWhenChildIsAppeared() {
        StackController stackController = Mockito.spy(TestUtils.newStackController(activity).build());
        stackController.ensureViewIsCreated();
        SimpleViewController child1 = new SimpleViewController(activity, childRegistry, "child1", new Options());
        stackController.push(child1, new CommandListenerAdapter());
        onViewAppeared();
        Mockito.verify(stackController, Mockito.times(1)).clearOptions();
    }

    @Test
    public void mergeOptions_optionsAreMergedWhenChildAppears() {
        Options options = new Options();
        options.topBar.title.text = new Text("new title");
        ViewController child1 = Mockito.spy(new SimpleViewController(activity, childRegistry, "child1", options));
        children.add(child1);
        uut.ensureViewIsCreated();
        child1.ensureViewIsCreated();
        child1.onViewAppeared();
        ArgumentCaptor<Options> optionsCaptor = ArgumentCaptor.forClass(Options.class);
        ArgumentCaptor<ReactComponent> viewCaptor = ArgumentCaptor.forClass(ReactComponent.class);
        Mockito.verify(uut, Mockito.times(1)).clearOptions();
        Mockito.verify(uut, Mockito.times(1)).applyChildOptions(optionsCaptor.capture(), viewCaptor.capture());
        assertThat(optionsCaptor.getValue().topBar.title.text.get()).isEqualTo("new title");
        assertThat(viewCaptor.getValue()).isEqualTo(child1.getView());
    }

    @Test
    public void mergeOptions_initialParentOptionsAreNotMutatedWhenChildAppears() {
        Options options = new Options();
        options.topBar.title.text = new Text("new title");
        ViewController child1 = Mockito.spy(new SimpleViewController(activity, childRegistry, "child1", options));
        children.add(child1);
        uut.ensureViewIsCreated();
        child1.ensureViewIsCreated();
        child1.onViewAppeared();
        assertThat(uut.initialOptions.topBar.title.text.get()).isEqualTo(ParentControllerTest.INITIAL_TITLE);
    }

    @Test
    public void applyChildOptions_appliesRootOptionsIfRoot() {
        addToParent(activity, uut);
        Options options = new Options();
        SimpleViewController child1 = Mockito.spy(new SimpleViewController(activity, childRegistry, "child1", options));
        uut.applyChildOptions(options, getView());
        Mockito.verify(presenter, Mockito.times(1)).applyRootOptions(uut.getView(), options);
    }

    @Test
    public void applyChildOptions_doesNotApplyRootOptionsIfHasParent() {
        Options options = new Options();
        uut.setParentController(Mockito.mock(ParentController.class));
        SimpleViewController child1 = Mockito.spy(new SimpleViewController(activity, childRegistry, "child1", options));
        uut.applyChildOptions(options, getView());
        Mockito.verify(presenter, Mockito.times(0)).applyRootOptions(uut.getView(), options);
    }

    @Test
    public void resolveCurrentOptions_returnOptionsIfNoChildren() {
        assertThat(uut.getChildControllers().size()).isZero();
        assertThat(uut.resolveCurrentOptions()).isEqualTo(uut.initialOptions);
    }

    @Test
    public void resolveCurrentOptions_mergesWithCurrentChild() {
        ViewController child1 = Mockito.mock(ViewController.class);
        Mockito.when(child1.getView()).thenReturn(new android.widget.FrameLayout(activity));
        Options copiedChildOptions = Mockito.spy(new Options());
        Options childOptions = Mockito.spy(new Options() {
            @Override
            public Options copy() {
                return copiedChildOptions;
            }
        });
        Mockito.when(child1.resolveCurrentOptions()).thenReturn(childOptions);
        children.add(child1);
        uut.ensureViewIsCreated();
        assertThat(uut.getCurrentChild()).isEqualTo(child1);
        uut.resolveCurrentOptions();
        Mockito.verify(child1).resolveCurrentOptions();
        Mockito.verify(copiedChildOptions).withDefaultOptions(uut.initialOptions);
    }

    @Test
    public void resolveCurrentOptions_withDefaultOptions() {
        SimpleViewController child1 = new SimpleViewController(activity, childRegistry, "child1", new Options());
        children.add(child1);
        uut.ensureViewIsCreated();
        Options defaultOptions = new Options();
        Options currentOptions = Mockito.spy(new Options());
        ParentController spy = Mockito.spy(uut);
        Mockito.when(spy.resolveCurrentOptions()).thenReturn(currentOptions);
        spy.resolveCurrentOptions(defaultOptions);
        Mockito.verify(currentOptions).withDefaultOptions(defaultOptions);
    }
}

