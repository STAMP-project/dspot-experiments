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


import AnimatedProperties.ALPHA;
import AnimatedProperties.HEIGHT;
import AnimatedProperties.WIDTH;
import Transition.RootBoundsTransition;
import Transition.TransitionKeyType.GLOBAL;
import TransitionId.Type;
import com.facebook.litho.animation.DimensionValue;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(ComponentsTestRunner.class)
public class TransitionTest {
    @Test
    public void testCollectRootBoundsTransitions() {
        Transition transition = Transition.create(GLOBAL, "rootKey").animate(WIDTH);
        TransitionId rootTransitionId = new TransitionId(Type.GLOBAL, "rootKey", null);
        Transition.RootBoundsTransition rootWidthTransition = new Transition.RootBoundsTransition();
        TransitionUtils.collectRootBoundsTransitions(rootTransitionId, transition, WIDTH, rootWidthTransition);
        assertThat(rootWidthTransition.hasTransition).isTrue();
        assertThat(rootWidthTransition.appearTransition).isNull();
        Transition.RootBoundsTransition rootHeightTransition = new Transition.RootBoundsTransition();
        TransitionUtils.collectRootBoundsTransitions(rootTransitionId, transition, HEIGHT, rootHeightTransition);
        assertThat(rootHeightTransition.hasTransition).isFalse();
    }

    @Test
    public void testCollectRootBoundsTransitionsAppearComesAfterAllLayout() {
        Transition transition = Transition.parallel(Transition.allLayout(), Transition.create(GLOBAL, "rootKey").animate(HEIGHT).appearFrom(10), Transition.create(GLOBAL, "otherkey").animate(ALPHA));
        TransitionId rootTransitionId = new TransitionId(Type.GLOBAL, "rootKey", null);
        Transition.RootBoundsTransition rootHeightTransition = new Transition.RootBoundsTransition();
        TransitionUtils.collectRootBoundsTransitions(rootTransitionId, transition, HEIGHT, rootHeightTransition);
        assertThat(rootHeightTransition.hasTransition).isTrue();
        assertThat(rootHeightTransition.appearTransition).isNotNull();
    }

    @Test
    public void testCollectRootBoundsTransitionsAppearComesBeforeAllLayout() {
        Transition transition = Transition.parallel(Transition.create(GLOBAL, "rootKey").animate(HEIGHT).appearFrom(10), Transition.allLayout(), Transition.create(GLOBAL, "otherkey").animate(ALPHA));
        TransitionId rootTransitionId = new TransitionId(Type.GLOBAL, "rootKey", null);
        Transition.RootBoundsTransition rootHeightTransition = new Transition.RootBoundsTransition();
        TransitionUtils.collectRootBoundsTransitions(rootTransitionId, transition, HEIGHT, rootHeightTransition);
        assertThat(rootHeightTransition.hasTransition).isTrue();
        assertThat(rootHeightTransition.appearTransition).isNotNull();
    }

    @Test
    public void testCollectRootBoundsTransitionsExtractAppearFrom() {
        Transition transition = Transition.create(GLOBAL, "rootKey").animate(HEIGHT).appearFrom(10);
        TransitionId rootTransitionId = new TransitionId(Type.GLOBAL, "rootKey", null);
        Transition.RootBoundsTransition rootHeightTransition = new Transition.RootBoundsTransition();
        TransitionUtils.collectRootBoundsTransitions(rootTransitionId, transition, HEIGHT, rootHeightTransition);
        assertThat(rootHeightTransition.hasTransition).isTrue();
        assertThat(rootHeightTransition.appearTransition).isNotNull();
        LayoutState layoutState = Mockito.mock(LayoutState.class);
        LayoutOutput rootLayout = new LayoutOutput();
        rootLayout.setBounds(0, 0, 300, 100);
        Mockito.when(layoutState.getMountableOutputAt(0)).thenReturn(rootLayout);
        int animateFrom = ((int) (Transition.getRootAppearFromValue(rootHeightTransition.appearTransition, layoutState, HEIGHT)));
        assertThat(animateFrom).isEqualTo(10);
    }

    @Test
    public void testCollectRootBoundsTransitionsExtractAppearFromDimensionValue() {
        Transition transition = Transition.create(GLOBAL, "rootKey").animate(HEIGHT).appearFrom(DimensionValue.heightPercentageOffset(50));
        TransitionId rootTransitionId = new TransitionId(Type.GLOBAL, "rootKey", null);
        Transition.RootBoundsTransition rootHeightTransition = new Transition.RootBoundsTransition();
        TransitionUtils.collectRootBoundsTransitions(rootTransitionId, transition, HEIGHT, rootHeightTransition);
        assertThat(rootHeightTransition.hasTransition).isTrue();
        assertThat(rootHeightTransition.appearTransition).isNotNull();
        LayoutState layoutState = Mockito.mock(LayoutState.class);
        LayoutOutput rootLayout = new LayoutOutput();
        rootLayout.setBounds(0, 0, 300, 100);
        Mockito.when(layoutState.getMountableOutputAt(0)).thenReturn(rootLayout);
        int animateFrom = ((int) (Transition.getRootAppearFromValue(rootHeightTransition.appearTransition, layoutState, HEIGHT)));
        float expectedAppearFrom = (rootLayout.getBounds().height()) * 1.5F;
        assertThat(animateFrom).isEqualTo(((int) (expectedAppearFrom)));
    }
}

