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


import com.facebook.litho.testing.subcomponents.InspectableComponent;
import org.assertj.core.api.Condition;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.mockito.Mock;


public class BaseMatcherTest {
    @Mock
    InspectableComponent mInspectableComponent;

    @Mock
    Component mComponent;

    @Test
    public void testMatcherCreation() {
        final BaseMatcherTest.TestBaseMatcher matcher = new BaseMatcherTest.TestBaseMatcher().clickHandler(IsNull.<EventHandler<ClickEvent>>nullValue(null));
        final Condition<InspectableComponent> condition = BaseMatcherBuilder.buildCommonMatcher(matcher);
        assertThat(condition.matches(mInspectableComponent)).isTrue();
    }

    @Test
    public void testMatcherFailureMessage() {
        final BaseMatcherTest.TestBaseMatcher matcher = new BaseMatcherTest.TestBaseMatcher().clickHandler(IsNull.<EventHandler<ClickEvent>>notNullValue(null));
        final Condition<InspectableComponent> condition = BaseMatcherBuilder.buildCommonMatcher(matcher);
        condition.matches(mInspectableComponent);
        assertThat(condition.description().toString()).isEqualTo("Click handler <not null> (doesn't match <null>)");
    }

    static class TestBaseMatcher extends BaseMatcher<BaseMatcherTest.TestBaseMatcher> {
        @Override
        protected BaseMatcherTest.TestBaseMatcher getThis() {
            return this;
        }
    }
}

