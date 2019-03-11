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
package com.facebook.litho.testing.subcomponents;


import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.EventDispatcher;
import com.facebook.litho.HasEventDispatcher;
import com.facebook.litho.testing.ComponentsRule;
import com.facebook.litho.testing.assertj.LithoAssertions;
import com.facebook.litho.testing.assertj.SubComponentDeepExtractor;
import com.facebook.litho.testing.specmodels.TestMyGeneric;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import org.assertj.core.api.Condition;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class GenericMatcherGenerationTest {
    @Rule
    public ComponentsRule mComponentsRule = new ComponentsRule();

    private final GenericMatcherGenerationTest.GenericProp mGenericProp = new GenericMatcherGenerationTest.GenericProp();

    @Test
    public void testGenericPropMatching() {
        final ComponentContext c = mComponentsRule.getContext();
        final Component component = new com.facebook.litho.testing.specmodels.MyGeneric(c.getAndroidContext()).create(c).genericProp(mGenericProp).build();
        final Condition<InspectableComponent> matcher = TestMyGeneric.matcher(c).genericProp(mGenericProp).build();
        LithoAssertions.assertThat(c, component).has(SubComponentDeepExtractor.deepSubComponentWith(c, matcher));
    }

    // This is just to fulfill the prop requirements, reusing an existing interface we've got lying
    // around.
    public static class GenericProp implements HasEventDispatcher {
        @Override
        public EventDispatcher getEventDispatcher() {
            return null;
        }
    }
}

