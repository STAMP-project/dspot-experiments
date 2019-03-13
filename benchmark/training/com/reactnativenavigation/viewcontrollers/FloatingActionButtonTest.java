package com.reactnativenavigation.viewcontrollers;


import Options.EMPTY;
import android.app.Activity;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.mocks.SimpleViewController;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.viewcontrollers.stack.StackController;
import org.junit.Test;


public class FloatingActionButtonTest extends BaseTest {
    private static final int CHILD_FAB_COUNT = 3;

    private StackController stackController;

    private SimpleViewController childFab;

    private SimpleViewController childNoFab;

    private Activity activity;

    private ChildControllersRegistry childRegistry;

    @Test
    public void showOnPush() {
        stackController.push(childFab, new CommandListenerAdapter());
        onViewAppeared();
        assertThat(hasFab()).isTrue();
    }

    @Test
    public void hideOnPush() {
        stackController.push(childFab, new CommandListenerAdapter());
        onViewAppeared();
        assertThat(hasFab()).isTrue();
        stackController.push(childNoFab, new CommandListenerAdapter());
        onViewAppeared();
        assertThat(hasFab()).isFalse();
    }

    @Test
    public void hideOnPop() {
        disablePushAnimation(childNoFab, childFab);
        stackController.push(childNoFab, new CommandListenerAdapter());
        stackController.push(childFab, new CommandListenerAdapter());
        onViewAppeared();
        assertThat(hasFab()).isTrue();
        stackController.pop(EMPTY, new CommandListenerAdapter());
        onViewAppeared();
        assertThat(hasFab()).isFalse();
    }

    @Test
    public void showOnPop() {
        disablePushAnimation(childFab, childNoFab);
        stackController.push(childFab, new CommandListenerAdapter());
        stackController.push(childNoFab, new CommandListenerAdapter());
        onViewAppeared();
        assertThat(hasFab()).isFalse();
        stackController.pop(EMPTY, new CommandListenerAdapter());
        onViewAppeared();
        assertThat(hasFab()).isTrue();
    }

    @Test
    public void hasChildren() {
        childFab = new SimpleViewController(activity, childRegistry, "child1", getOptionsWithFabActions());
        stackController.push(childFab, new CommandListenerAdapter());
        onViewAppeared();
        assertThat(hasFab()).isTrue();
        assertThat(containsActions()).isTrue();
    }
}

