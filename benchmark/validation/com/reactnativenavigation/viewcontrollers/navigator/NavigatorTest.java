package com.reactnativenavigation.viewcontrollers.navigator;


import Options.EMPTY;
import ViewController.ViewVisibilityListener;
import android.R.id.content;
import android.widget.FrameLayout;
import com.facebook.react.ReactInstanceManager;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.TestActivity;
import com.reactnativenavigation.mocks.SimpleComponentViewController;
import com.reactnativenavigation.mocks.SimpleViewController;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.parse.params.Text;
import com.reactnativenavigation.presentation.OverlayManager;
import com.reactnativenavigation.react.EventEmitter;
import com.reactnativenavigation.utils.CommandListener;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.utils.ImageLoader;
import com.reactnativenavigation.utils.OptionHelper;
import com.reactnativenavigation.utils.ViewUtils;
import com.reactnativenavigation.viewcontrollers.ChildControllersRegistry;
import com.reactnativenavigation.viewcontrollers.ComponentViewController;
import com.reactnativenavigation.viewcontrollers.ViewController;
import com.reactnativenavigation.viewcontrollers.bottomtabs.BottomTabsController;
import com.reactnativenavigation.viewcontrollers.modal.ModalStack;
import com.reactnativenavigation.viewcontrollers.stack.StackController;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;


@Config(qualifiers = "xxhdpi")
public class NavigatorTest extends BaseTest {
    private TestActivity activity;

    private ChildControllersRegistry childRegistry;

    private Navigator uut;

    private RootPresenter rootPresenter;

    private StackController parentController;

    private SimpleViewController child1;

    private ViewController child2;

    private ViewController child3;

    private ViewController child4;

    private ViewController child5;

    private Options tabOptions = OptionHelper.createBottomTabOptions();

    private ImageLoader imageLoaderMock;

    private ActivityController<TestActivity> activityController;

    private OverlayManager overlayManager;

    private EventEmitter eventEmitter;

    private ViewVisibilityListener parentVisibilityListener;

    private ModalStack modalStack;

    private ReactInstanceManager reactInstanceManager;

    @Test
    public void bindViews() {
        Mockito.verify(rootPresenter).setRootContainer(uut.getRootLayout());
        Mockito.verify(modalStack).setModalsLayout(uut.getModalsLayout());
    }

    @Test
    public void setDefaultOptions() {
        uut.setDefaultOptions(new Options());
        SimpleViewController spy = Mockito.spy(child1);
        uut.setRoot(spy, new CommandListenerAdapter(), reactInstanceManager);
        Options defaultOptions = new Options();
        uut.setDefaultOptions(defaultOptions);
        Mockito.verify(spy).setDefaultOptions(defaultOptions);
        Mockito.verify(modalStack).setDefaultOptions(defaultOptions);
    }

    @Test
    public void setRoot_delegatesToRootPresenter() {
        CommandListenerAdapter listener = new CommandListenerAdapter();
        uut.setRoot(child1, listener, reactInstanceManager);
        ArgumentCaptor<CommandListenerAdapter> captor = ArgumentCaptor.forClass(CommandListenerAdapter.class);
        Mockito.verify(rootPresenter).setRoot(ArgumentMatchers.eq(child1), ArgumentMatchers.eq(uut.getDefaultOptions()), captor.capture(), ArgumentMatchers.eq(reactInstanceManager));
        assertThat(captor.getValue().getListener()).isEqualTo(listener);
    }

    @Test
    public void setRoot_clearsSplashLayout() {
        FrameLayout content = activity.findViewById(content);
        assertThat(content.getChildCount()).isEqualTo(4);// 3 frame layouts and the default splash layout

        uut.setRoot(child2, new CommandListenerAdapter(), reactInstanceManager);
        assertThat(content.getChildCount()).isEqualTo(3);
    }

    @Test
    public void setRoot_AddsChildControllerView() {
        uut.setRoot(child1, new CommandListenerAdapter(), reactInstanceManager);
        assertIsChild(uut.getRootLayout(), getView());
    }

    @Test
    public void setRoot_ReplacesExistingChildControllerViews() {
        uut.setRoot(child1, new CommandListenerAdapter(), reactInstanceManager);
        uut.setRoot(child2, new CommandListenerAdapter(), reactInstanceManager);
        assertIsChild(uut.getRootLayout(), child2.getView());
    }

    @Test
    public void hasUniqueId() {
        assertThat(uut.getId()).startsWith("navigator");
        assertThat(getId()).isNotEqualTo(uut.getId());
    }

    @Test
    public void push() {
        StackController stackController = newStack();
        stackController.push(child1, new CommandListenerAdapter());
        uut.setRoot(stackController, new CommandListenerAdapter(), reactInstanceManager);
        assertIsChild(uut.getView(), stackController.getView());
        assertIsChild(stackController.getView(), getView());
        uut.push(getId(), child2, new CommandListenerAdapter());
        assertIsChild(uut.getView(), stackController.getView());
        assertIsChild(stackController.getView(), child2.getView());
    }

    @Test
    public void push_InvalidPushWithoutAStack_DoesNothing() {
        uut.setRoot(child1, new CommandListenerAdapter(), reactInstanceManager);
        uut.push(getId(), child2, new CommandListenerAdapter());
        assertIsChild(uut.getView(), getView());
    }

    @Test
    public void push_OnCorrectStackByFindingChildId() {
        StackController stack1 = newStack();
        stack1.ensureViewIsCreated();
        StackController stack2 = newStack();
        stack2.ensureViewIsCreated();
        stack1.push(child1, new CommandListenerAdapter());
        stack2.push(child2, new CommandListenerAdapter());
        BottomTabsController bottomTabsController = newTabs(Arrays.asList(stack1, stack2));
        uut.setRoot(bottomTabsController, new CommandListenerAdapter(), reactInstanceManager);
        SimpleViewController newChild = new SimpleViewController(activity, childRegistry, "new child", tabOptions);
        uut.push(child2.getId(), newChild, new CommandListenerAdapter());
        assertThat(stack1.getChildControllers()).doesNotContain(newChild);
        assertThat(stack2.getChildControllers()).contains(newChild);
    }

    @Test
    public void push_rejectIfNotContainedInStack() {
        CommandListener listener = Mockito.mock(CommandListener.class);
        uut.push("someId", child1, listener);
        Mockito.verify(listener).onError(ArgumentMatchers.any());
    }

    @Test
    public void pop_InvalidDoesNothing() {
        uut.pop("123", EMPTY, new CommandListenerAdapter());
        uut.setRoot(child1, new CommandListenerAdapter(), reactInstanceManager);
        uut.pop(getId(), EMPTY, new CommandListenerAdapter());
        assertThat(uut.getChildControllers()).hasSize(1);
    }

    @Test
    public void pop_FromCorrectStackByFindingChildId() {
        StackController stack1 = newStack();
        StackController stack2 = newStack();
        BottomTabsController bottomTabsController = newTabs(Arrays.asList(stack1, stack2));
        uut.setRoot(bottomTabsController, new CommandListenerAdapter(), reactInstanceManager);
        stack1.push(child1, new CommandListenerAdapter());
        stack2.push(child2, new CommandListenerAdapter());
        stack2.push(child3, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                stack2.push(child4, new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        uut.pop("child4", EMPTY, new CommandListenerAdapter());
                        assertThat(stack2.getChildControllers()).containsOnly(child2, child3);
                    }
                });
            }
        });
    }

    @Test
    public void pop_byStackId() {
        disablePushAnimation(child1, child2);
        disablePopAnimation(child2, child1);
        StackController stack = newStack();
        stack.ensureViewIsCreated();
        uut.setRoot(stack, new CommandListenerAdapter(), reactInstanceManager);
        stack.push(child1, new CommandListenerAdapter());
        stack.push(child2, new CommandListenerAdapter());
        uut.pop(stack.getId(), EMPTY, new CommandListenerAdapter());
        assertThat(stack.getChildControllers()).containsOnly(child1);
    }

    @Test
    public void popTo_FromCorrectStackUpToChild() {
        StackController stack1 = newStack();
        StackController stack2 = newStack();
        BottomTabsController bottomTabsController = newTabs(Arrays.asList(stack1, stack2));
        uut.setRoot(bottomTabsController, new CommandListenerAdapter(), reactInstanceManager);
        stack1.push(child1, new CommandListenerAdapter());
        stack2.push(child2, new CommandListenerAdapter());
        stack2.push(child3, new CommandListenerAdapter());
        stack2.push(child4, new CommandListenerAdapter());
        stack2.push(child5, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.popTo(child2.getId(), EMPTY, new CommandListenerAdapter());
                assertThat(stack2.getChildControllers()).containsOnly(child2);
            }
        });
    }

    @Test
    public void popToRoot() {
        StackController stack1 = newStack();
        StackController stack2 = newStack();
        BottomTabsController bottomTabsController = newTabs(Arrays.asList(stack1, stack2));
        uut.setRoot(bottomTabsController, new CommandListenerAdapter(), reactInstanceManager);
        stack1.push(child1, new CommandListenerAdapter());
        stack2.push(child2, new CommandListenerAdapter());
        stack2.push(child3, new CommandListenerAdapter());
        stack2.push(child4, new CommandListenerAdapter());
        stack2.push(child5, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.popToRoot(child3.getId(), EMPTY, new CommandListenerAdapter());
                assertThat(stack2.getChildControllers()).containsOnly(child2);
            }
        });
    }

    @Test
    public void setStackRoot() {
        disablePushAnimation(child1, child2, child3);
        StackController stack = newStack();
        uut.setRoot(stack, new CommandListenerAdapter(), reactInstanceManager);
        stack.push(child1, new CommandListenerAdapter());
        stack.push(child2, new CommandListenerAdapter());
        stack.setRoot(Collections.singletonList(child3), new CommandListenerAdapter());
        assertThat(stack.getChildControllers()).containsOnly(child3);
    }

    @Test
    public void handleBack_DelegatesToRoot() {
        assertThat(uut.handleBack(new CommandListenerAdapter())).isFalse();
        ViewController root = Mockito.spy(child1);
        uut.setRoot(root, new CommandListenerAdapter(), reactInstanceManager);
        Mockito.when(root.handleBack(ArgumentMatchers.any(CommandListener.class))).thenReturn(true);
        assertThat(uut.handleBack(new CommandListenerAdapter())).isTrue();
        Mockito.verify(root, Mockito.times(1)).handleBack(ArgumentMatchers.any());
    }

    @Test
    public void handleBack_modalTakePrecedenceOverRoot() {
        ViewController root = Mockito.spy(child1);
        uut.setRoot(root, new CommandListenerAdapter(), reactInstanceManager);
        uut.showModal(child2, new CommandListenerAdapter());
        Mockito.verify(root, Mockito.times(0)).handleBack(new CommandListenerAdapter());
    }

    @Test
    public void mergeOptions_CallsApplyNavigationOptions() {
        ComponentViewController componentVc = new SimpleComponentViewController(activity, childRegistry, "theId", new Options());
        componentVc.setParentController(parentController);
        assertThat(componentVc.options.topBar.title.text.get("")).isEmpty();
        uut.setRoot(componentVc, new CommandListenerAdapter(), reactInstanceManager);
        Options options = new Options();
        options.topBar.title.text = new Text("new title");
        uut.mergeOptions("theId", options);
        assertThat(componentVc.options.topBar.title.text.get()).isEqualTo("new title");
    }

    @Test
    public void mergeOptions_AffectsOnlyComponentViewControllers() {
        uut.mergeOptions("some unknown child id", new Options());
    }

    @Test
    public void findController_root() {
        uut.setRoot(child1, new CommandListenerAdapter(), reactInstanceManager);
        assertThat(uut.findController(getId())).isEqualTo(child1);
    }

    @Test
    public void findController_overlay() {
        uut.showOverlay(child1, new CommandListenerAdapter());
        assertThat(uut.findController(getId())).isEqualTo(child1);
    }

    @Test
    public void findController_modal() {
        uut.showModal(child1, new CommandListenerAdapter());
        assertThat(uut.findController(getId())).isEqualTo(child1);
    }

    @Test
    public void push_promise() {
        final StackController stackController = newStack();
        stackController.push(child1, new CommandListenerAdapter());
        uut.setRoot(stackController, new CommandListenerAdapter(), reactInstanceManager);
        assertIsChild(uut.getView(), stackController.getView());
        assertIsChild(stackController.getView(), getView());
        uut.push(getId(), child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertIsChild(uut.getView(), stackController.getView());
                assertIsChild(stackController.getView(), child2.getView());
            }
        });
    }

    @Test
    public void push_InvalidPushWithoutAStack_DoesNothing_Promise() {
        uut.setRoot(child1, new CommandListenerAdapter(), reactInstanceManager);
        uut.push(getId(), child2, new CommandListenerAdapter() {
            @Override
            public void onError(String message) {
                assertIsChild(uut.getView(), getView());
            }
        });
    }

    @Test
    public void pop_InvalidDoesNothing_Promise() {
        uut.pop("123", EMPTY, new CommandListenerAdapter());
        uut.setRoot(child1, new CommandListenerAdapter(), reactInstanceManager);
        uut.pop(getId(), EMPTY, new CommandListenerAdapter() {
            @Override
            public void onError(String reason) {
                assertThat(uut.getChildControllers()).hasSize(1);
            }
        });
    }

    @Test
    public void pop_FromCorrectStackByFindingChildId_Promise() {
        StackController stack1 = newStack();
        final StackController stack2 = newStack();
        BottomTabsController bottomTabsController = newTabs(Arrays.asList(stack1, stack2));
        uut.setRoot(bottomTabsController, new CommandListenerAdapter(), reactInstanceManager);
        stack1.push(child1, new CommandListenerAdapter());
        stack2.push(child2, new CommandListenerAdapter());
        stack2.push(child3, new CommandListenerAdapter());
        stack2.push(child4, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.pop("child4", EMPTY, new CommandListenerAdapter());
                assertThat(stack2.getChildControllers()).containsOnly(child2, child3);
            }
        });
    }

    @Test
    public void pushIntoModal() {
        uut.setRoot(parentController, new CommandListenerAdapter(), reactInstanceManager);
        StackController stackController = newStack();
        stackController.push(child1, new CommandListenerAdapter());
        uut.showModal(stackController, new CommandListenerAdapter());
        uut.push(stackController.getId(), child2, new CommandListenerAdapter());
        assertIsChild(stackController.getView(), child2.getView());
    }

    @Test
    public void pushedStackCanBePopped() {
        child1.options.animations.push.enabled = new Bool(false);
        child2.options.animations.push.enabled = new Bool(false);
        StackController spy = Mockito.spy(parentController);
        StackController parent = newStack();
        parent.ensureViewIsCreated();
        uut.setRoot(parent, new CommandListenerAdapter(), reactInstanceManager);
        parent.push(spy, new CommandListenerAdapter());
        spy.push(child1, new CommandListenerAdapter());
        spy.push(child2, new CommandListenerAdapter());
        assertThat(spy.getChildControllers().size()).isEqualTo(2);
        ensureViewIsCreated();
        child2.ensureViewIsCreated();
        CommandListenerAdapter listener = new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertThat(spy.getChildControllers().size()).isEqualTo(1);
            }
        };
        uut.pop("child2", EMPTY, listener);
        Mockito.verify(spy, Mockito.times(1)).pop(EMPTY, listener);
    }

    @Test
    public void showModal_onViewDisappearIsInvokedOnRoot() {
        uut.setRoot(parentController, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.showModal(child1, new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        assertThat(parentController.getView().getParent()).isNull();
                        Mockito.verify(parentController, Mockito.times(1)).onViewDisappear();
                    }
                });
            }
        }, reactInstanceManager);
    }

    @Test
    public void dismissModal_onViewAppearedInvokedOnRoot() {
        disableShowModalAnimation(child1, child2, child3);
        disableDismissModalAnimation(child1, child2);
        uut.setRoot(parentController, new CommandListenerAdapter(), reactInstanceManager);
        parentController.push(child3, new CommandListenerAdapter());
        uut.showModal(child1, new CommandListenerAdapter());
        uut.showModal(child2, new CommandListenerAdapter());
        uut.dismissModal(child2.getId(), new CommandListenerAdapter());
        assertThat(parentController.getView().getParent()).isNull();
        Mockito.verify(parentVisibilityListener, Mockito.times(1)).onViewAppeared(parentController.getView());
        uut.dismissModal(getId(), new CommandListenerAdapter());
        assertThat(parentController.getView().getParent()).isNotNull();
        Mockito.verify(parentVisibilityListener, Mockito.times(2)).onViewAppeared(parentController.getView());
    }

    @Test
    public void dismissModal_reattachedToRoot() {
        disableModalAnimations(child1);
        uut.setRoot(parentController, new CommandListenerAdapter(), reactInstanceManager);
        assertThat(ViewUtils.isChildOf(uut.getRootLayout(), parentController.getView()));
        uut.showModal(child1, new CommandListenerAdapter());
        uut.dismissModal(getId(), new CommandListenerAdapter());
        assertThat(ViewUtils.isChildOf(uut.getRootLayout(), parentController.getView()));
    }

    @Test
    public void dismissModal_rejectIfRootIsNotSetAndSingleModalIsDisplayed() {
        disableModalAnimations(child1, child2);
        uut.showModal(child1, new CommandListenerAdapter());
        uut.showModal(child2, new CommandListenerAdapter());
        CommandListenerAdapter listener1 = Mockito.spy(new CommandListenerAdapter());
        uut.dismissModal(child2.getId(), listener1);
        Mockito.verify(listener1).onSuccess(ArgumentMatchers.any());
        assertThat(child2.isDestroyed()).isTrue();
        CommandListenerAdapter listener2 = Mockito.spy(new CommandListenerAdapter());
        uut.dismissModal(getId(), listener2);
        Mockito.verify(listener2).onError(ArgumentMatchers.any());
        assertThat(isDestroyed()).isFalse();
    }

    @Test
    public void dismissAllModals_onViewAppearedInvokedOnRoot() {
        disablePushAnimation(child2);
        disableShowModalAnimation(child1);
        uut.dismissAllModals(EMPTY, new CommandListenerAdapter());
        Mockito.verify(parentVisibilityListener, Mockito.times(0)).onViewAppeared(parentController.getView());
        uut.setRoot(parentController, new CommandListenerAdapter(), reactInstanceManager);
        parentController.push(child2, new CommandListenerAdapter());
        Mockito.verify(parentVisibilityListener, Mockito.times(1)).onViewAppeared(parentController.getView());
        uut.showModal(child1, new CommandListenerAdapter());
        uut.dismissAllModals(EMPTY, new CommandListenerAdapter());
        Mockito.verify(parentVisibilityListener, Mockito.times(2)).onViewAppeared(parentController.getView());
    }

    @Test
    public void handleBack_onViewAppearedInvokedOnRoot() {
        disableShowModalAnimation(child1, child2, child3);
        parentController.push(child3, new CommandListenerAdapter());
        StackController spy = Mockito.spy(parentController);
        uut.setRoot(spy, new CommandListenerAdapter(), reactInstanceManager);
        uut.showModal(child1, new CommandListenerAdapter());
        uut.showModal(child2, new CommandListenerAdapter());
        uut.handleBack(new CommandListenerAdapter());
        Mockito.verify(parentVisibilityListener, Mockito.times(1)).onViewAppeared(spy.getView());
        uut.handleBack(new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertThat(spy.getView().getParent()).isNotNull();
                Mockito.verify(spy, Mockito.times(2)).onViewAppeared();
            }
        });
    }

    @Test
    public void handleBack_falseIfRootIsNotSetAndSingleModalIsDisplayed() {
        disableShowModalAnimation(child1, child2, child3);
        uut.showModal(child1, new CommandListenerAdapter());
        uut.showModal(child2, new CommandListenerAdapter());
        assertThat(uut.handleBack(new CommandListenerAdapter())).isTrue();
        assertThat(uut.handleBack(new CommandListenerAdapter())).isFalse();
    }

    @Test
    public void destroy_destroyedRoot() {
        disablePushAnimation(child1);
        StackController spy = Mockito.spy(parentController);
        spy.options.animations.setRoot.enabled = new Bool(false);
        uut.setRoot(spy, new CommandListenerAdapter(), reactInstanceManager);
        spy.push(child1, new CommandListenerAdapter());
        activityController.destroy();
        Mockito.verify(spy, Mockito.times(1)).destroy();
    }

    @Test
    public void destroy_destroyOverlayManager() {
        uut.setRoot(parentController, new CommandListenerAdapter(), reactInstanceManager);
        activityController.destroy();
        Mockito.verify(overlayManager).destroy();
    }

    @Test
    public void destroyViews() {
        uut.setRoot(parentController, new CommandListenerAdapter(), reactInstanceManager);
        uut.showModal(child1, new CommandListenerAdapter());
        uut.showOverlay(child2, new CommandListenerAdapter());
        uut.destroy();
        assertThat(childRegistry.size()).isZero();
    }
}

