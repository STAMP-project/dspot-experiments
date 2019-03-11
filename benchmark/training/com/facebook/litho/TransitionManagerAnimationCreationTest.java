/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho;


import AnimatedProperties.HEIGHT;
import AnimatedProperties.WIDTH;
import AnimatedProperties.X;
import AnimatedProperties.Y;
import Transition.TransitionAnimator;
import Transition.TransitionKeyType.GLOBAL;
import com.facebook.litho.animation.PropertyAnimation;
import com.facebook.litho.animation.TransitionAnimationBinding;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for the creation of animations using the targeting API in {@link Transition}.
 */
@RunWith(ComponentsTestRunner.class)
public class TransitionManagerAnimationCreationTest {
    private TransitionManager mTransitionManager;

    private TransitionAnimator mTestVerificationAnimator;

    private ArrayList<PropertyAnimation> mCreatedAnimations = new ArrayList<>();

    @Test
    public void testCreateSingleAnimation() {
        final LayoutState current = createMockLayoutState(Transition.parallel(), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test", 0, 0));
        final LayoutState next = createMockLayoutState(Transition.parallel(Transition.create(GLOBAL, "test").animate(X).animator(mTestVerificationAnimator)), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test", 10, 10));
        mTransitionManager.setupTransitions(current, next, TransitionManager.getRootTransition(next.getTransitions()));
        assertThat(mCreatedAnimations).containsExactlyInAnyOrder(createPropertyAnimation("test", X, 10));
    }

    @Test
    public void testCreateMultiPropAnimation() {
        final LayoutState current = createMockLayoutState(Transition.parallel(), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test", 0, 0));
        final LayoutState next = createMockLayoutState(Transition.parallel(Transition.create(GLOBAL, "test").animate(X, Y).animator(mTestVerificationAnimator)), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test", 10, 10));
        mTransitionManager.setupTransitions(current, next, TransitionManager.getRootTransition(next.getTransitions()));
        assertThat(mCreatedAnimations).containsExactlyInAnyOrder(createPropertyAnimation("test", X, 10), createPropertyAnimation("test", Y, 10));
    }

    @Test
    public void testCreateMultiPropAnimationWithNonChangingProp() {
        final LayoutState current = createMockLayoutState(Transition.parallel(), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test", 0, 0));
        final LayoutState next = createMockLayoutState(Transition.parallel(Transition.create(GLOBAL, "test").animate(X, Y).animator(mTestVerificationAnimator)), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test", 10, 0));
        mTransitionManager.setupTransitions(current, next, TransitionManager.getRootTransition(next.getTransitions()));
        assertThat(mCreatedAnimations).containsExactlyInAnyOrder(createPropertyAnimation("test", X, 10));
    }

    @Test
    public void testCreateMultiComponentAnimation() {
        final LayoutState current = createMockLayoutState(Transition.parallel(), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test1", 0, 0), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test2", 0, 0));
        final LayoutState next = createMockLayoutState(Transition.parallel(Transition.create(GLOBAL, "test1", "test2").animate(X).animator(mTestVerificationAnimator)), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test1", 10, 10), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test2", (-10), (-10)));
        mTransitionManager.setupTransitions(current, next, TransitionManager.getRootTransition(next.getTransitions()));
        assertThat(mCreatedAnimations).containsExactlyInAnyOrder(createPropertyAnimation("test1", X, 10), createPropertyAnimation("test2", X, (-10)));
    }

    @Test
    public void testSetsOfComponentsAndPropertiesAnimation() {
        final LayoutState current = createMockLayoutState(Transition.parallel(), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test1", 0, 0), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test2", 0, 0));
        final LayoutState next = createMockLayoutState(Transition.parallel(Transition.create(GLOBAL, "test1", "test2").animate(X, Y).animator(mTestVerificationAnimator)), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test1", 10, 10), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test2", (-10), (-10)));
        mTransitionManager.setupTransitions(current, next, TransitionManager.getRootTransition(next.getTransitions()));
        assertThat(mCreatedAnimations).containsExactlyInAnyOrder(createPropertyAnimation("test1", X, 10), createPropertyAnimation("test1", Y, 10), createPropertyAnimation("test2", X, (-10)), createPropertyAnimation("test2", Y, (-10)));
    }

    @Test
    public void testAutoLayoutAnimation() {
        final LayoutState current = createMockLayoutState(Transition.parallel(), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test1", 0, 0), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test2", 0, 0));
        final LayoutState next = createMockLayoutState(Transition.parallel(Transition.allLayout().animator(mTestVerificationAnimator)), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test1", 10, 20, 200, 150), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test2", (-10), (-20), 50, 80));
        mTransitionManager.setupTransitions(current, next, TransitionManager.getRootTransition(next.getTransitions()));
        assertThat(mCreatedAnimations).containsExactlyInAnyOrder(createPropertyAnimation("test1", X, 10), createPropertyAnimation("test1", Y, 20), createPropertyAnimation("test1", WIDTH, 200), createPropertyAnimation("test1", HEIGHT, 150), createPropertyAnimation("test2", X, (-10)), createPropertyAnimation("test2", Y, (-20)), createPropertyAnimation("test2", WIDTH, 50), createPropertyAnimation("test2", HEIGHT, 80));
    }

    @Test
    public void testKeyDoesntExist() {
        final LayoutState current = createMockLayoutState(Transition.parallel(), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test", 0, 0));
        final LayoutState next = createMockLayoutState(Transition.parallel(Transition.create(GLOBAL, "test", "keydoesntexist").animate(X, Y).animator(mTestVerificationAnimator)), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test", 10, 0));
        mTransitionManager.setupTransitions(current, next, TransitionManager.getRootTransition(next.getTransitions()));
        assertThat(mCreatedAnimations).containsExactlyInAnyOrder(createPropertyAnimation("test", X, 10));
    }

    @Test
    public void testNoPreviousLayoutState() {
        final LayoutState next = createMockLayoutState(Transition.parallel(Transition.create(GLOBAL, "test", "keydoesntexist").animate(X, Y).animator(mTestVerificationAnimator), Transition.create(GLOBAL, "appearing").animate(X).appearFrom(0).animator(mTestVerificationAnimator)), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test", 10, 0), TransitionManagerAnimationCreationTest.createMockLayoutOutput("appearing", 20, 0));
        mTransitionManager.setupTransitions(null, next, TransitionManager.getRootTransition(next.getTransitions()));
        assertThat(mCreatedAnimations).containsExactlyInAnyOrder(createPropertyAnimation("appearing", X, 20));
    }

    @Test
    public void testWithMountTimeAnimations() {
        final LayoutState current = createMockLayoutState(Transition.parallel(), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test", 0, 0), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test2", 0, 0));
        final LayoutState next = createMockLayoutState(Transition.parallel(Transition.create(GLOBAL, "test", "keydoesntexist").animate(X, Y).animator(mTestVerificationAnimator)), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test", 10, 0), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test2", 20, 0));
        final ArrayList<Transition> mountTimeTransitions = new ArrayList<>();
        mountTimeTransitions.add(Transition.create(GLOBAL, "test2").animate(X).appearFrom(0).animator(mTestVerificationAnimator));
        final List<Transition> allTransitions = new ArrayList<>();
        allTransitions.addAll(next.getTransitions());
        allTransitions.addAll(mountTimeTransitions);
        mTransitionManager.setupTransitions(current, next, TransitionManager.getRootTransition(allTransitions));
        assertThat(mCreatedAnimations).containsExactlyInAnyOrder(createPropertyAnimation("test", X, 10), createPropertyAnimation("test2", X, 20));
    }

    @Test
    public void testWithOnlyMountTimeAnimations() {
        final LayoutState current = createMockLayoutState(Transition.parallel(), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test", 0, 0), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test2", 0, 0));
        final LayoutState next = createMockLayoutState(null, TransitionManagerAnimationCreationTest.createMockLayoutOutput("test", 10, 0), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test2", 20, 0));
        final ArrayList<Transition> mountTimeTransitions = new ArrayList<>();
        mountTimeTransitions.add(Transition.create(GLOBAL, "test2").animate(X).appearFrom(0).animator(mTestVerificationAnimator));
        mountTimeTransitions.add(Transition.create(GLOBAL, "test", "keydoesntexist").animate(X, Y).animator(mTestVerificationAnimator));
        mTransitionManager.setupTransitions(current, next, TransitionManager.getRootTransition(mountTimeTransitions));
        assertThat(mCreatedAnimations).containsExactlyInAnyOrder(createPropertyAnimation("test", X, 10), createPropertyAnimation("test2", X, 20));
    }

    @Test
    public void testAnimationFromStateUpdate() {
        final LayoutState current = createMockLayoutState(Transition.parallel(), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test1", 0, 0), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test2", 0, 0));
        final ArrayList<PropertyAnimation> animationsCreatedFromStateUpdate = new ArrayList<>();
        Transition.TransitionAnimator animatorForStateUpdate = new Transition.TransitionAnimator() {
            @Override
            public TransitionAnimationBinding createAnimation(PropertyAnimation propertyAnimation) {
                animationsCreatedFromStateUpdate.add(propertyAnimation);
                return new com.facebook.litho.animation.SpringTransition(propertyAnimation);
            }
        };
        final ArrayList<Transition> transitionsFromStateUpdate = new ArrayList<>();
        transitionsFromStateUpdate.add(Transition.create(GLOBAL, "test1", "test2").animate(Y).animator(animatorForStateUpdate));
        final LayoutState next = createMockLayoutState(Transition.parallel(Transition.create(GLOBAL, "test1", "test2").animate(X).animator(mTestVerificationAnimator)), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test1", 10, 20), TransitionManagerAnimationCreationTest.createMockLayoutOutput("test2", (-10), (-20)));
        final List<Transition> allTransitions = new ArrayList<>();
        allTransitions.addAll(next.getTransitions());
        allTransitions.addAll(transitionsFromStateUpdate);
        mTransitionManager.setupTransitions(current, next, TransitionManager.getRootTransition(allTransitions));
        assertThat(mCreatedAnimations.size()).isEqualTo(2);
        assertThat(animationsCreatedFromStateUpdate).containsExactlyInAnyOrder(createPropertyAnimation("test1", Y, 20), createPropertyAnimation("test2", Y, (-20)));
    }
}

