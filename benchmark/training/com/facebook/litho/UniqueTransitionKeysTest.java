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


import LayoutState.CalculateLayoutSource.TEST;
import Transition.TransitionKeyType.GLOBAL;
import View.MeasureSpec;
import View.MeasureSpec.EXACTLY;
import com.facebook.litho.testing.assertj.LithoAssertions;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.testing.util.InlineLayoutSpec;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class UniqueTransitionKeysTest {
    private final InlineLayoutSpec mHasNonUniqueTransitionKeys = new InlineLayoutSpec() {
        @Override
        protected Component onCreateLayout(ComponentContext c) {
            return Row.create(c).child(Row.create(c).transitionKey("test").transitionKeyType(GLOBAL)).child(Row.create(c).transitionKey("test").transitionKeyType(GLOBAL)).build();
        }
    };

    private final InlineLayoutSpec mHasUniqueTransitionKeys = new InlineLayoutSpec() {
        @Override
        protected Component onCreateLayout(ComponentContext c) {
            return Row.create(c).child(Row.create(c).transitionKey("test").transitionKeyType(GLOBAL)).child(Row.create(c).transitionKey("test2").transitionKeyType(GLOBAL)).build();
        }
    };

    @Rule
    public ExpectedException mExpectedException = ExpectedException.none();

    @Test
    public void testGetTransitionKeyMapping() {
        ComponentContext c = new ComponentContext(application);
        LayoutState layoutState = LayoutState.calculate(c, mHasUniqueTransitionKeys, ComponentTree.generateComponentTreeId(), MeasureSpec.makeMeasureSpec(100, EXACTLY), MeasureSpec.makeMeasureSpec(100, EXACTLY), TEST);
        layoutState.getTransitionIdMapping();
    }

    @Test
    public void testThrowIfSameTransitionKeyAppearsMultipleTimes() {
        mExpectedException.expect(RuntimeException.class);
        mExpectedException.expectMessage("The transitionId \'TransitionId{\"test\", GLOBAL}\' is defined multiple times in the same layout.");
        ComponentContext c = new ComponentContext(application);
        LayoutState layoutState = LayoutState.calculate(c, mHasNonUniqueTransitionKeys, ComponentTree.generateComponentTreeId(), MeasureSpec.makeMeasureSpec(100, EXACTLY), MeasureSpec.makeMeasureSpec(100, EXACTLY), TEST);
        LithoAssertions.assertThat(layoutState.getTransitionIdMapping()).isNotNull();
    }
}

