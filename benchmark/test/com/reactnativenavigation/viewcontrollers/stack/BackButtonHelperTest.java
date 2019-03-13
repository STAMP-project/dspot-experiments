package com.reactnativenavigation.viewcontrollers.stack;


import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.viewcontrollers.ChildController;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class BackButtonHelperTest extends BaseTest {
    private BackButtonHelper uut;

    private StackController stack;

    private ChildController child1;

    private ChildController child2;

    @Test
    public void addToChild() {
        uut.addToPushedChild(child1);
        Mockito.verify(child1).mergeOptions(ArgumentMatchers.any());
    }

    @Test
    public void addToChild_addsIfStackContainsMoreThenOneChild() {
        disablePushAnimation(child1, child2);
        stack.push(child1, new CommandListenerAdapter());
        stack.push(child2, new CommandListenerAdapter());
        ArgumentCaptor<Options> optionWithBackButton = ArgumentCaptor.forClass(Options.class);
        Mockito.verify(child2, Mockito.times(1)).mergeOptions(optionWithBackButton.capture());
        assertThat(optionWithBackButton.getValue().topBar.buttons.back.visible.get()).isTrue();
    }

    @Test
    public void addToChild_doesNotAddIfBackButtonHidden() {
        disablePushAnimation(child1, child2);
        stack.push(child1, new CommandListenerAdapter());
        child2.options.topBar.buttons.back.visible = new Bool(false);
        stack.push(child2, new CommandListenerAdapter());
        Mockito.verify(child2, Mockito.times(0)).mergeOptions(ArgumentMatchers.any());
    }

    @Test
    public void clear() {
        child1.options.topBar.buttons.back.visible = new Bool(true);
        uut.clear(child1);
        assertThat(child1.options.topBar.buttons.back.visible.get()).isFalse();
    }
}

