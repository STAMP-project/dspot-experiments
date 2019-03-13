package com.reactnativenavigation.viewcontrollers.stack;


import Options.EMPTY;
import View.GONE;
import View.INVISIBLE;
import View.VISIBLE;
import ViewGroup.LayoutParams.MATCH_PARENT;
import ViewGroup.MarginLayoutParams;
import android.animation.Animator;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.anim.NavigationAnimator;
import com.reactnativenavigation.mocks.ImageLoaderMock;
import com.reactnativenavigation.mocks.SimpleViewController;
import com.reactnativenavigation.mocks.TitleBarReactViewCreatorMock;
import com.reactnativenavigation.mocks.TopBarBackgroundViewCreatorMock;
import com.reactnativenavigation.mocks.TopBarButtonCreatorMock;
import com.reactnativenavigation.parse.AnimationOptions;
import com.reactnativenavigation.parse.NestedAnimationsOptions;
import com.reactnativenavigation.parse.Options;
import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.parse.params.Text;
import com.reactnativenavigation.presentation.RenderChecker;
import com.reactnativenavigation.presentation.StackPresenter;
import com.reactnativenavigation.utils.CommandListenerAdapter;
import com.reactnativenavigation.utils.ImageLoader;
import com.reactnativenavigation.utils.TitleBarHelper;
import com.reactnativenavigation.utils.ViewHelper;
import com.reactnativenavigation.utils.ViewUtils;
import com.reactnativenavigation.viewcontrollers.ChildControllersRegistry;
import com.reactnativenavigation.viewcontrollers.ParentController;
import com.reactnativenavigation.viewcontrollers.ViewController;
import com.reactnativenavigation.viewcontrollers.topbar.TopBarController;
import com.reactnativenavigation.views.Component;
import com.reactnativenavigation.views.ReactComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;


public class StackControllerTest extends BaseTest {
    private Activity activity;

    private ChildControllersRegistry childRegistry;

    private StackController uut;

    private ViewController child1;

    private ViewController child2;

    private ViewController child3;

    private ViewController child4;

    private NavigationAnimator animator;

    private TopBarController topBarController;

    private StackPresenter presenter;

    private BackButtonHelper backButtonHelper;

    private RenderChecker renderChecker;

    @Test
    public void isAViewController() {
        assertThat(uut).isInstanceOf(ViewController.class);
    }

    @Test
    public void childrenAreAssignedParent() {
        StackController uut = createStack(Arrays.asList(child1, child2));
        for (ViewController child : uut.getChildControllers()) {
            assertThat(child.getParentController().equals(uut)).isTrue();
        }
    }

    @Test
    public void constructor_backButtonIsAddedToChild() {
        createStack(Arrays.asList(child1, child2, child3));
        assertThat(child2.options.topBar.buttons.back.visible.get(false)).isTrue();
        assertThat(child3.options.topBar.buttons.back.visible.get(false)).isTrue();
    }

    @Test
    public void createView_currentChildIsAdded() {
        StackController uut = createStack(Arrays.asList(child1, child2, child3, child4));
        assertThat(uut.getChildControllers().size()).isEqualTo(4);
        assertThat(uut.getView().getChildCount()).isEqualTo(2);
        assertThat(uut.getView().getChildAt(0)).isEqualTo(child4.getView());
    }

    @Test
    public void holdsAStackOfViewControllers() {
        assertThat(uut.isEmpty()).isTrue();
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter());
        assertThat(uut.peek()).isEqualTo(child3);
        assertContainsOnlyId(child1.getId(), child2.getId(), child3.getId());
    }

    @Test
    public void isRendered_falseIfStackIsEmpty() {
        assertThat(uut.size()).isZero();
        assertThat(uut.isRendered()).isFalse();
    }

    @Test
    public void isRendered() {
        disablePushAnimation(child1);
        uut.push(child1, new CommandListenerAdapter());
        verify(presenter).isRendered(((Component) (child1.getView())));
        verify(renderChecker).areRendered(ArgumentMatchers.any());
        assertThat(uut.isRendered()).isTrue();
        child1.setWaitForRender(new Bool(true));
        assertThat(uut.isRendered()).isFalse();
        child1.getView().addView(new View(activity));
        assertThat(uut.isRendered()).isTrue();
        Mockito.when(presenter.isRendered(((Component) (child1.getView())))).then(( ignored) -> false);
        assertThat(uut.isRendered()).isFalse();
    }

    @Test
    public void push() {
        assertThat(uut.isEmpty()).isTrue();
        CommandListenerAdapter listener = Mockito.spy(new CommandListenerAdapter());
        uut.push(child1, listener);
        assertContainsOnlyId(child1.getId());
        Mockito.verify(listener, Mockito.times(1)).onSuccess(child1.getId());
    }

    @Test
    public void push_backButtonIsNotAddedIfScreenContainsLeftButton() {
        disablePushAnimation(child1, child2);
        uut.push(child1, new CommandListenerAdapter());
        child2.options.topBar.buttons.left = new ArrayList(Collections.singleton(TitleBarHelper.iconButton("someButton", "icon.png")));
        uut.push(child2, new CommandListenerAdapter());
        assertThat(topBarController.getView().getTitleBar().getNavigationIcon()).isNotNull();
        Mockito.verify(topBarController.getView(), Mockito.times(0)).setBackButton(ArgumentMatchers.any());
    }

    @Test
    public void push_backButtonIsNotAddedIfScreenClearsLeftButton() {
        child1.options.topBar.buttons.left = new ArrayList();
        uut.push(child1, new CommandListenerAdapter());
        Mockito.verify(child1, Mockito.times(0)).mergeOptions(ArgumentMatchers.any());
    }

    @Test
    public void push_backButtonAddedBeforeChildViewIsCreated() {
        disablePopAnimation(child1, child2);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        InOrder inOrder = Mockito.inOrder(backButtonHelper, child2);
        inOrder.verify(backButtonHelper).addToPushedChild(child2);
        inOrder.verify(child2).setParentController(uut);
        inOrder.verify(child2).getView();// creates view

    }

    @Test
    public void push_waitForRender() {
        disablePushAnimation(child1);
        uut.push(child1, new CommandListenerAdapter());
        child2.options.animations.push.waitForRender = new Bool(true);
        uut.push(child2, new CommandListenerAdapter());
        verify(child2).addOnAppearedListener(ArgumentMatchers.any());
        Mockito.verify(animator, Mockito.times(0)).push(ArgumentMatchers.eq(child1.getView()), ArgumentMatchers.eq(child1.options.animations.push), ArgumentMatchers.any());
    }

    @Test
    public void push_backPressedDuringPushAnimationDestroysPushedScreenImmediately() {
        disablePushAnimation(child1);
        uut.push(child1, new CommandListenerAdapter());
        CommandListenerAdapter pushListener = Mockito.spy(new CommandListenerAdapter());
        uut.push(child2, pushListener);
        CommandListenerAdapter backListener = Mockito.spy(new CommandListenerAdapter());
        uut.handleBack(backListener);
        assertThat(uut.size()).isOne();
        assertThat(child1.getView().getParent()).isEqualTo(uut.getView());
        assertThat(child2.isDestroyed()).isTrue();
        InOrder inOrder = Mockito.inOrder(pushListener, backListener);
        inOrder.verify(pushListener).onSuccess(ArgumentMatchers.any());
        inOrder.verify(backListener).onSuccess(ArgumentMatchers.any());
    }

    @Test
    public void animateSetRoot() {
        disablePushAnimation(child1, child2, child3);
        assertThat(uut.isEmpty()).isTrue();
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.setRoot(Collections.singletonList(child3), new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertContainsOnlyId(child3.getId());
            }
        });
    }

    @Test
    public void setRoot_singleChild() {
        activity.setContentView(uut.getView());
        disablePushAnimation(child1, child2, child3);
        assertThat(uut.isEmpty()).isTrue();
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        assertThat(uut.getTopBar().getTitleBar().getNavigationIcon()).isNotNull();
        uut.setRoot(Collections.singletonList(child3), new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertContainsOnlyId(child3.getId());
                assertThat(uut.getTopBar().getTitleBar().getNavigationIcon()).isNull();
            }
        });
    }

    @Test
    public void setRoot_multipleChildren() {
        activity.setContentView(uut.getView());
        disablePushAnimation(child1, child2, child3, child4);
        disablePopAnimation(child4);
        assertThat(uut.isEmpty()).isTrue();
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        assertThat(uut.getTopBar().getTitleBar().getNavigationIcon()).isNotNull();
        uut.setRoot(Arrays.asList(child3, child4), new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertContainsOnlyId(child3.getId(), child4.getId());
                assertThat(uut.getTopBar().getTitleBar().getNavigationIcon()).isNotNull();
                assertThat(child4.isViewShown()).isTrue();
                assertThat(child3.isViewShown()).isFalse();
                assertThat(uut.getCurrentChild()).isEqualTo(child4);
                uut.pop(EMPTY, new CommandListenerAdapter());
                assertThat(uut.getTopBar().getTitleBar().getNavigationIcon()).isNull();
                assertThat(uut.getCurrentChild()).isEqualTo(child3);
            }
        });
    }

    @Test
    public void setRoot_doesNotCrashWhenCalledInQuickSuccession() {
        disablePushAnimation(child1);
        uut.setRoot(Collections.singletonList(child1), new CommandListenerAdapter());
        uut.setRoot(Collections.singletonList(child2), new CommandListenerAdapter());
        uut.setRoot(Collections.singletonList(child3), new CommandListenerAdapter());
        animator.endPushAnimation(child2.getView());
        animator.endPushAnimation(child3.getView());
        assertContainsOnlyId(child3.getId());
    }

    @Test
    public synchronized void pop() {
        disablePushAnimation(child1, child2);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertContainsOnlyId(child2.getId(), child1.getId());
                uut.pop(EMPTY, new CommandListenerAdapter());
                assertContainsOnlyId(child1.getId());
            }
        });
    }

    @Test
    public void pop_screenCurrentlyBeingPushedIsPopped() {
        disablePushAnimation(child1, child2);
        uut.push(child1, Mockito.mock(CommandListenerAdapter.class));
        uut.push(child2, Mockito.mock(CommandListenerAdapter.class));
        uut.push(child3, Mockito.mock(CommandListenerAdapter.class));
        uut.pop(EMPTY, Mockito.mock(CommandListenerAdapter.class));
        assertThat(uut.size()).isEqualTo(2);
        assertContainsOnlyId(child1.getId(), child2.getId());
    }

    @Test
    public void pop_appliesOptionsAfterPop() {
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.pop(EMPTY, new CommandListenerAdapter());
                Mockito.verify(uut, Mockito.times(1)).applyChildOptions(uut.options, ArgumentMatchers.eq(((ReactComponent) (child1.getView()))));
            }
        });
    }

    @Test
    public void pop_layoutHandlesChildWillDisappear() {
        uut = new StackControllerBuilder(activity).setTopBarController(new TopBarController()).setId("uut").setInitialOptions(new Options()).setStackPresenter(new StackPresenter(activity, new TitleBarReactViewCreatorMock(), new TopBarBackgroundViewCreatorMock(), new TopBarButtonCreatorMock(), new ImageLoader(), new RenderChecker(), new Options())).build();
        uut.ensureViewIsCreated();
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.pop(EMPTY, new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        Mockito.verify(presenter, Mockito.times(1)).onChildWillAppear(child1.options, child2.options);
                    }
                });
            }
        });
    }

    @Test
    public void stackOperations() {
        assertThat(uut.peek()).isNull();
        assertThat(uut.size()).isZero();
        assertThat(uut.isEmpty()).isTrue();
        uut.push(child1, new CommandListenerAdapter());
        assertThat(uut.peek()).isEqualTo(child1);
        assertThat(uut.size()).isEqualTo(1);
        assertThat(uut.isEmpty()).isFalse();
    }

    @Test
    public void onChildDestroyed() {
        Component childView = ((Component) (child2.getView()));
        uut.onChildDestroyed(childView);
        verify(presenter).onChildDestroyed(childView);
    }

    @Test
    public void handleBack_PopsUnlessSingleChild() {
        assertThat(uut.isEmpty()).isTrue();
        assertThat(uut.handleBack(new CommandListenerAdapter())).isFalse();
        uut.push(child1, new CommandListenerAdapter());
        assertThat(uut.size()).isEqualTo(1);
        assertThat(uut.handleBack(new CommandListenerAdapter())).isFalse();
        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertThat(uut.size()).isEqualTo(2);
                assertThat(uut.handleBack(new CommandListenerAdapter())).isTrue();
                assertThat(uut.size()).isEqualTo(1);
                assertThat(uut.handleBack(new CommandListenerAdapter())).isFalse();
            }
        });
    }

    @Test
    public void pop_doesNothingWhenZeroOrOneChild() {
        assertThat(uut.isEmpty()).isTrue();
        uut.pop(EMPTY, new CommandListenerAdapter());
        assertThat(uut.isEmpty()).isTrue();
        uut.push(child1, new CommandListenerAdapter());
        uut.pop(EMPTY, new CommandListenerAdapter());
        assertContainsOnlyId(child1.getId());
    }

    @SuppressWarnings("MagicNumber")
    @Test
    public void pop_animationOptionsAreMergedCorrectlyToDisappearingChild() throws JSONException {
        disablePushAnimation(child1, child2);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        Options mergeOptions = new Options();
        JSONObject content = new JSONObject();
        JSONObject x = new JSONObject();
        x.put("duration", 300);
        x.put("from", 0);
        x.put("to", 1000);
        content.put("x", x);
        mergeOptions.animations.pop.content = AnimationOptions.parse(content);
        uut.pop(mergeOptions, new CommandListenerAdapter());
        ArgumentCaptor<NestedAnimationsOptions> captor = ArgumentCaptor.forClass(NestedAnimationsOptions.class);
        Mockito.verify(animator, Mockito.times(1)).pop(ArgumentMatchers.any(), captor.capture(), ArgumentMatchers.any());
        Animator animator = captor.getValue().content.getAnimation(Mockito.mock(View.class)).getChildAnimations().get(0);
        assertThat(animator.getDuration()).isEqualTo(300);
    }

    @SuppressWarnings("MagicNumber")
    @Test
    public void pop_animationOptionsAreMergedCorrectlyToDisappearingChildWithDefaultOptions() throws JSONException {
        disablePushAnimation(child1, child2);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        Options defaultOptions = new Options();
        JSONObject content = new JSONObject();
        JSONObject x = new JSONObject();
        x.put("duration", 300);
        x.put("from", 0);
        x.put("to", 1000);
        content.put("x", x);
        defaultOptions.animations.pop.content = AnimationOptions.parse(content);
        uut.setDefaultOptions(defaultOptions);
        uut.pop(EMPTY, new CommandListenerAdapter());
        ArgumentCaptor<NestedAnimationsOptions> captor = ArgumentCaptor.forClass(NestedAnimationsOptions.class);
        Mockito.verify(animator, Mockito.times(1)).pop(ArgumentMatchers.any(), captor.capture(), ArgumentMatchers.any());
        Animator animator = captor.getValue().content.getAnimation(Mockito.mock(View.class)).getChildAnimations().get(0);
        assertThat(animator.getDuration()).isEqualTo(300);
    }

    @Test
    public void canPopWhenSizeIsMoreThanOne() {
        assertThat(uut.isEmpty()).isTrue();
        assertThat(uut.canPop()).isFalse();
        uut.push(child1, new CommandListenerAdapter());
        assertContainsOnlyId(child1.getId());
        assertThat(uut.canPop()).isFalse();
        uut.push(child2, new CommandListenerAdapter());
        assertContainsOnlyId(child1.getId(), child2.getId());
        assertThat(uut.canPop()).isTrue();
    }

    @Test
    public void push_addsToViewTree() {
        assertNotChildOf(uut.getView(), child1.getView());
        uut.push(child1, new CommandListenerAdapter());
        assertIsChild(uut.getView(), child1.getView());
    }

    @Test
    public void push_removesPreviousFromTree() {
        assertNotChildOf(uut.getView(), child1.getView());
        uut.push(child1, new CommandListenerAdapter());
        assertIsChild(uut.getView(), child1.getView());
        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertThat(((View) (uut.getView().findViewById(child1.getView().getId())))).isNull();
                assertThat(((View) (uut.getView().findViewById(child2.getView().getId())))).isNotNull();
            }
        });
    }

    @Test
    public void push_assignsRefToSelfOnPushedController() {
        assertThat(child1.getParentController()).isNull();
        uut.push(child1, new CommandListenerAdapter());
        assertThat(child1.getParentController()).isEqualTo(uut);
        StackController anotherNavController = createStack("another");
        anotherNavController.ensureViewIsCreated();
        anotherNavController.push(child2, new CommandListenerAdapter());
        assertThat(child2.getParentController()).isEqualTo(anotherNavController);
    }

    @Test
    public void push_doesNotAnimateTopBarIfScreenIsPushedWithoutAnimation() {
        uut.ensureViewIsCreated();
        child1.ensureViewIsCreated();
        child1.options.topBar.visible = new Bool(false);
        child1.options.topBar.animate = new Bool(false);
        disablePushAnimation(child1, child2);
        uut.push(child1, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                child1.onViewAppeared();
                assertThat(uut.getTopBar().getVisibility()).isEqualTo(GONE);
                uut.push(child2, new CommandListenerAdapter());
                child2.onViewAppeared();
                Mockito.verify(uut.getTopBar(), Mockito.times(0)).showAnimate(child2.options.animations.push.topBar);
                assertThat(uut.getTopBar().getVisibility()).isEqualTo(VISIBLE);
                Mockito.verify(uut.getTopBar(), Mockito.times(1)).resetAnimationOptions();
            }
        });
    }

    @Test
    public void push_animatesAndClearsPreviousAnimationValues() {
        uut.ensureViewIsCreated();
        child1.options.topBar.visible = new Bool(false);
        child1.options.topBar.animate = new Bool(false);
        child1.options.animations.push.enabled = new Bool(false);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                Mockito.verify(uut.getTopBar(), Mockito.times(1)).resetAnimationOptions();
            }
        });
    }

    @Test
    public void pop_replacesViewWithPrevious() {
        final View child2View = child2.getView();
        final View child1View = child1.getView();
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertIsChild(uut.getView(), child2View);
                assertNotChildOf(uut.getView(), child1View);
                uut.pop(EMPTY, new CommandListenerAdapter());
                assertNotChildOf(uut.getView(), child2View);
                assertIsChild(uut.getView(), child1View);
            }
        });
    }

    @Test
    public void pop_appearingChildHasCorrectLayoutParams() {
        child2.options.animations.pop.enabled = new Bool(false);
        child1.options.topBar.drawBehind = new Bool(false);
        StackController uut = createStack(Arrays.asList(child1, child2));
        uut.ensureViewIsCreated();
        assertThat(child2.getView().getParent()).isEqualTo(uut.getView());
        uut.pop(EMPTY, new CommandListenerAdapter());
        assertThat(child1.getView().getParent()).isEqualTo(uut.getView());
        assertThat(child1.getView().getLayoutParams().width).isEqualTo(MATCH_PARENT);
        assertThat(child1.getView().getLayoutParams().height).isEqualTo(MATCH_PARENT);
        assertThat(((ViewGroup.MarginLayoutParams) (child1.getView().getLayoutParams())).topMargin).isEqualTo(uut.getTopBar().getHeight());
    }

    @Test
    public void popTo_PopsTopUntilControllerIsNewTop() {
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertThat(uut.size()).isEqualTo(3);
                assertThat(uut.peek()).isEqualTo(child3);
                uut.popTo(child1, EMPTY, new CommandListenerAdapter());
                assertThat(uut.size()).isEqualTo(1);
                assertThat(uut.peek()).isEqualTo(child1);
            }
        });
    }

    @Test
    public void popTo_optionsAreMergedOnTopChild() {
        disablePushAnimation(child1, child2);
        uut.push(child1, new CommandListenerAdapter());
        Options mergeOptions = new Options();
        uut.popTo(child2, mergeOptions, new CommandListenerAdapter());
        uut.popTo(child1, mergeOptions, new CommandListenerAdapter());
        Mockito.verify(child1, Mockito.times(0)).mergeOptions(mergeOptions);
        uut.push(child2, new CommandListenerAdapter());
        uut.popTo(child1, mergeOptions, new CommandListenerAdapter());
        verify(child2).mergeOptions(mergeOptions);
    }

    @Test
    public void popTo_NotAChildOfThisStack_DoesNothing() {
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter());
        assertThat(uut.size()).isEqualTo(2);
        uut.popTo(child2, EMPTY, new CommandListenerAdapter());
        assertThat(uut.size()).isEqualTo(2);
    }

    @Test
    public void popTo_animatesTopController() {
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter());
        uut.push(child4, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.popTo(child2, EMPTY, new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        Mockito.verify(animator, Mockito.times(0)).pop(ArgumentMatchers.eq(child1.getView()), ArgumentMatchers.any(), ArgumentMatchers.any());
                        Mockito.verify(animator, Mockito.times(0)).pop(ArgumentMatchers.eq(child2.getView()), ArgumentMatchers.any(), ArgumentMatchers.any());
                        Mockito.verify(animator, Mockito.times(1)).pop(ArgumentMatchers.eq(child4.getView()), ArgumentMatchers.eq(child4.options.animations.push), ArgumentMatchers.any());
                    }
                });
            }
        });
    }

    @Test
    public void popTo_pushAnimationIsCancelled() {
        disablePushAnimation(child1, child2);
        uut.push(child1, Mockito.mock(CommandListenerAdapter.class));
        uut.push(child2, Mockito.mock(CommandListenerAdapter.class));
        ViewGroup pushed = child3.getView();
        uut.push(child3, Mockito.mock(CommandListenerAdapter.class));
        uut.popTo(child1, EMPTY, Mockito.mock(CommandListenerAdapter.class));
        animator.endPushAnimation(pushed);
        assertContainsOnlyId(child1.getId());
    }

    @Test
    public void popToRoot_PopsEverythingAboveFirstController() {
        child1.options.animations.push.enabled = new Bool(false);
        child2.options.animations.push.enabled = new Bool(false);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                assertThat(uut.size()).isEqualTo(3);
                assertThat(uut.peek()).isEqualTo(child3);
                uut.popToRoot(EMPTY, new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        assertThat(uut.size()).isEqualTo(1);
                        assertThat(uut.peek()).isEqualTo(child1);
                    }
                });
            }
        });
    }

    @Test
    public void popToRoot_onlyTopChildIsAnimated() {
        child1.options.animations.push.enabled = new Bool(false);
        child2.options.animations.push.enabled = new Bool(false);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.popToRoot(EMPTY, new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        Mockito.verify(animator, Mockito.times(1)).pop(ArgumentMatchers.eq(child3.getView()), ArgumentMatchers.eq(child3.options.animations.pop), ArgumentMatchers.any());
                    }
                });
            }
        });
    }

    @Test
    public void popToRoot_topChildrenAreDestroyed() {
        child1.options.animations.push.enabled = new Bool(false);
        child2.options.animations.push.enabled = new Bool(false);
        child3.options.animations.push.enabled = new Bool(false);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter());
        uut.popToRoot(EMPTY, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                Mockito.verify(child1, Mockito.times(0)).destroy();
                Mockito.verify(child2, Mockito.times(1)).destroy();
                Mockito.verify(child3, Mockito.times(1)).destroy();
            }
        });
    }

    @Test
    public void popToRoot_EmptyStackDoesNothing() {
        assertThat(uut.isEmpty()).isTrue();
        CommandListenerAdapter listener = Mockito.spy(new CommandListenerAdapter());
        uut.popToRoot(EMPTY, listener);
        assertThat(uut.isEmpty()).isTrue();
        Mockito.verify(listener, Mockito.times(1)).onError(ArgumentMatchers.any());
    }

    @Test
    public void popToRoot_optionsAreMergedOnTopChild() {
        disablePushAnimation(child1, child2);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        Options mergeOptions = new Options();
        uut.popToRoot(mergeOptions, new CommandListenerAdapter());
        verify(child2).mergeOptions(mergeOptions);
        Mockito.verify(child1, Mockito.times(0)).mergeOptions(mergeOptions);
    }

    @Test
    public void popToRoot_screenPushedBeforePopAnimationCompletesIsPopped() {
        disablePushAnimation(child1, child2);
        uut.push(child1, Mockito.mock(CommandListenerAdapter.class));
        uut.push(child2, Mockito.mock(CommandListenerAdapter.class));
        ViewGroup pushed = child3.getView();
        uut.push(child3, Mockito.mock(CommandListenerAdapter.class));
        uut.popToRoot(EMPTY, Mockito.mock(CommandListenerAdapter.class));
        animator.endPushAnimation(pushed);
        assertContainsOnlyId(child1.getId());
    }

    @Test
    public void findControllerById_ReturnsSelfOrChildrenById() {
        assertThat(uut.findController("123")).isNull();
        assertThat(uut.findController(uut.getId())).isEqualTo(uut);
        uut.push(child1, new CommandListenerAdapter());
        assertThat(uut.findController(child1.getId())).isEqualTo(child1);
    }

    @Test
    public void findControllerById_Deeply() {
        StackController stack = createStack("another");
        stack.ensureViewIsCreated();
        stack.push(child2, new CommandListenerAdapter());
        uut.push(stack, new CommandListenerAdapter());
        assertThat(uut.findController(child2.getId())).isEqualTo(child2);
    }

    @Test
    public void pop_CallsDestroyOnPoppedChild() {
        child1 = Mockito.spy(child1);
        child2 = Mockito.spy(child2);
        child3 = Mockito.spy(child3);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                Mockito.verify(child3, Mockito.times(0)).destroy();
                uut.pop(EMPTY, new CommandListenerAdapter());
                Mockito.verify(child3, Mockito.times(1)).destroy();
            }
        });
    }

    @Test
    public void pop_callWillAppearWillDisappear() {
        child1.options.animations.push.enabled = new Bool(false);
        child2.options.animations.push.enabled = new Bool(false);
        child1 = Mockito.spy(child1);
        child2 = Mockito.spy(child2);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.pop(EMPTY, new CommandListenerAdapter());
        Mockito.verify(child1, Mockito.times(1)).onViewWillAppear();
        Mockito.verify(child2, Mockito.times(1)).onViewWillDisappear();
    }

    @Test
    public void pop_animatesTopBar() {
        uut.ensureViewIsCreated();
        child1.options.topBar.visible = new Bool(false);
        child1.options.animations.push.enabled = new Bool(false);
        child2.options.animations.push.enabled = new Bool(true);
        uut.push(child1, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                child1.onViewAppeared();
                assertThat(uut.getTopBar().getVisibility()).isEqualTo(GONE);
                uut.push(child2, new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        uut.pop(EMPTY, new CommandListenerAdapter() {
                            @Override
                            public void onSuccess(String childId) {
                                Mockito.verify(uut.getTopBar(), Mockito.times(1)).hideAnimate(child2.options.animations.pop.topBar);
                            }
                        });
                    }
                });
            }
        });
    }

    @Test
    public void pop_doesNotAnimateTopBarIfScreenIsPushedWithoutAnimation() {
        uut.ensureViewIsCreated();
        disablePushAnimation(child1, child2);
        child1.options.topBar.visible = new Bool(false);
        child1.options.topBar.animate = new Bool(false);
        child2.options.animations.push.enabled = new Bool(false);
        child2.options.topBar.animate = new Bool(false);
        child1.ensureViewIsCreated();
        uut.push(child1, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                uut.push(child2, new CommandListenerAdapter());
                assertThat(uut.getTopBar().getVisibility()).isEqualTo(VISIBLE);
                uut.pop(EMPTY, new CommandListenerAdapter());
                Mockito.verify(uut.getTopBar(), Mockito.times(0)).hideAnimate(child2.options.animations.pop.topBar);
                assertThat(uut.getTopBar().getVisibility()).isEqualTo(GONE);
            }
        });
    }

    @Test
    public void popTo_CallsDestroyOnPoppedChild() {
        child1 = Mockito.spy(child1);
        child2 = Mockito.spy(child2);
        child3 = Mockito.spy(child3);
        uut.push(child1, new CommandListenerAdapter());
        uut.push(child2, new CommandListenerAdapter());
        uut.push(child3, new CommandListenerAdapter() {
            @Override
            public void onSuccess(String childId) {
                Mockito.verify(child2, Mockito.times(0)).destroy();
                Mockito.verify(child3, Mockito.times(0)).destroy();
                uut.popTo(child1, EMPTY, new CommandListenerAdapter() {
                    @Override
                    public void onSuccess(String childId) {
                        Mockito.verify(child2, Mockito.times(1)).destroy();
                        Mockito.verify(child3, Mockito.times(1)).destroy();
                    }
                });
            }
        });
    }

    @Test
    public void stackCanBePushed() {
        ViewUtils.removeFromParent(uut.getView());
        StackController parent = createStack("someStack");
        parent.ensureViewIsCreated();
        parent.push(uut, new CommandListenerAdapter());
        uut.onViewAppeared();
        assertThat(parent.getView().getChildAt(0)).isEqualTo(uut.getView());
    }

    @Test
    public void applyOptions_applyOnlyOnFirstStack() {
        ViewUtils.removeFromParent(uut.getView());
        StackController parent = Mockito.spy(createStack("someStack"));
        parent.ensureViewIsCreated();
        parent.push(uut, new CommandListenerAdapter());
        Options childOptions = new Options();
        childOptions.topBar.title.text = new Text("Something");
        child1.options = childOptions;
        uut.push(child1, new CommandListenerAdapter());
        child1.ensureViewIsCreated();
        child1.onViewAppeared();
        ArgumentCaptor<Options> optionsCaptor = ArgumentCaptor.forClass(Options.class);
        ArgumentCaptor<ReactComponent> viewCaptor = ArgumentCaptor.forClass(ReactComponent.class);
        Mockito.verify(parent, Mockito.times(1)).applyChildOptions(optionsCaptor.capture(), viewCaptor.capture());
        assertThat(optionsCaptor.getValue().topBar.title.text.hasValue()).isFalse();
    }

    @Test
    public void applyOptions_topTabsAreNotVisibleIfNoTabsAreDefined() {
        uut.ensureViewIsCreated();
        uut.push(child1, new CommandListenerAdapter());
        child1.ensureViewIsCreated();
        child1.onViewAppeared();
        assertThat(ViewHelper.isVisible(uut.getTopBar().getTopTabs())).isFalse();
    }

    @Test
    public void buttonPressInvokedOnCurrentStack() {
        uut.ensureViewIsCreated();
        uut.push(child1, new CommandListenerAdapter());
        uut.sendOnNavigationButtonPressed("btn1");
        Mockito.verify(child1, Mockito.times(1)).sendOnNavigationButtonPressed("btn1");
    }

    @Test
    public void mergeChildOptions_updatesViewWithNewOptions() {
        StackController uut = Mockito.spy(new StackControllerBuilder(activity).setTopBarController(new TopBarController()).setId("stack").setInitialOptions(new Options()).setStackPresenter(new StackPresenter(activity, new TitleBarReactViewCreatorMock(), new TopBarBackgroundViewCreatorMock(), new TitleBarReactViewCreatorMock(), ImageLoaderMock.mock(), new RenderChecker(), Options.EMPTY)).build());
        Options optionsToMerge = new Options();
        Component component = Mockito.mock(Component.class);
        ViewController vc = Mockito.mock(ViewController.class);
        uut.mergeChildOptions(optionsToMerge, vc, component);
        Mockito.verify(uut, Mockito.times(1)).mergeChildOptions(optionsToMerge, vc, component);
    }

    @Test
    public void mergeChildOptions_updatesParentControllerWithNewOptions() {
        StackController uut = new StackControllerBuilder(activity).setTopBarController(new TopBarController()).setId("stack").setInitialOptions(new Options()).setStackPresenter(new StackPresenter(activity, new TitleBarReactViewCreatorMock(), new TopBarBackgroundViewCreatorMock(), new TitleBarReactViewCreatorMock(), ImageLoaderMock.mock(), new RenderChecker(), Options.EMPTY)).build();
        ParentController parentController = Mockito.mock(ParentController.class);
        uut.setParentController(parentController);
        uut.ensureViewIsCreated();
        Options optionsToMerge = new Options();
        optionsToMerge.topBar.testId = new Text("topBarID");
        optionsToMerge.bottomTabsOptions.testId = new Text("bottomTabsID");
        Component component = Mockito.mock(Component.class);
        ViewController vc = Mockito.mock(ViewController.class);
        uut.mergeChildOptions(optionsToMerge, vc, component);
        ArgumentCaptor<Options> captor = ArgumentCaptor.forClass(Options.class);
        Mockito.verify(parentController, Mockito.times(1)).mergeChildOptions(captor.capture(), ArgumentMatchers.eq(vc), ArgumentMatchers.eq(component));
        assertThat(captor.getValue().topBar.testId.hasValue()).isFalse();
        assertThat(captor.getValue().bottomTabsOptions.testId.get()).isEqualTo(optionsToMerge.bottomTabsOptions.testId.get());
    }

    @Test
    public void mergeChildOptions_StackRelatedOptionsAreCleared() {
        uut.ensureViewIsCreated();
        ParentController parentController = Mockito.mock(ParentController.class);
        uut.setParentController(parentController);
        Options options = new Options();
        options.animations.push = NestedAnimationsOptions.parse(new JSONObject());
        options.topBar.testId = new Text("id");
        options.fabOptions.id = new Text("fabId");
        Component component = Mockito.mock(Component.class);
        ViewController vc = Mockito.mock(ViewController.class);
        assertThat(options.fabOptions.hasValue()).isTrue();
        uut.mergeChildOptions(options, vc, component);
        ArgumentCaptor<Options> captor = ArgumentCaptor.forClass(Options.class);
        Mockito.verify(parentController, Mockito.times(1)).mergeChildOptions(captor.capture(), ArgumentMatchers.eq(vc), ArgumentMatchers.eq(component));
        assertThat(captor.getValue().animations.push.hasValue()).isFalse();
        assertThat(captor.getValue().topBar.testId.hasValue()).isFalse();
        assertThat(captor.getValue().fabOptions.hasValue()).isFalse();
    }

    @Test
    public void applyChildOptions_appliesResolvedOptions() {
        disablePushAnimation(child1, child2);
        uut.push(child1, new CommandListenerAdapter());
        assertThat(uut.getTopBar().getTitle()).isNullOrEmpty();
        Options uutOptions = new Options();
        uutOptions.topBar.title.text = new Text("UUT");
        uut.mergeOptions(uutOptions);
        assertThat(uut.getTopBar().getTitle()).isEqualTo("UUT");
        uut.push(child2, new CommandListenerAdapter());
        assertThat(uut.getTopBar().getTitle()).isEqualTo("UUT");
    }

    @Test
    public void mergeChildOptions_presenterDoesNotApplyOptionsIfViewIsNotShown() {
        ViewController vc = Mockito.mock(ViewController.class);
        Mockito.when(vc.isViewShown()).thenReturn(false);
        Component child = Mockito.mock(Component.class);
        uut.mergeChildOptions(new Options(), vc, child);
        Mockito.verify(presenter, Mockito.times(0)).mergeChildOptions(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void mergeChildOptions_presenterMergesOptionsOnlyForCurrentChild() {
        ViewController vc = Mockito.mock(ViewController.class);
        Mockito.when(vc.isViewShown()).thenReturn(true);
        Component child = Mockito.mock(Component.class);
        uut.mergeChildOptions(new Options(), vc, child);
        Mockito.verify(presenter, Mockito.times(0)).mergeChildOptions(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void resolvedOptionsAreAppliedWhenStackIsAttachedToParentAndNotVisible() {
        FrameLayout parent = new FrameLayout(activity);
        activity.setContentView(parent);
        ViewController child = new SimpleViewController(activity, childRegistry, "child1", new Options());
        StackController stack = createStack(Collections.singletonList(child));
        stack.getView().setVisibility(INVISIBLE);
        parent.addView(stack.getView());
        Component component = ((Component) (child.getView()));
        verify(presenter).applyChildOptions(ArgumentMatchers.any(), ArgumentMatchers.eq(component));
    }

    @Test
    public void destroy() {
        uut.destroy();
        Mockito.verify(topBarController, Mockito.times(1)).clear();
    }
}

