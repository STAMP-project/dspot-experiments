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
package com.facebook.litho.testing.assertj;


import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.testing.ComponentsRule;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.widget.Text;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class SubComponentDeepExtractorTest {
    @Rule
    public ComponentsRule mComponentsRule = new ComponentsRule();

    private Component mComponent;

    @Test
    public void testDeep() {
        final ComponentContext c = mComponentsRule.getContext();
        // ... but we do have one deep down.
        // We don't have a shallow Text component ...
        LithoAssertions.assertThat(c, mComponent).doesNotHave(SubComponentExtractor.subComponentWith(mComponentsRule.getContext(), ComponentConditions.typeIs(Text.class))).has(SubComponentDeepExtractor.deepSubComponentWith(mComponentsRule.getContext(), ComponentConditions.typeIs(Text.class)));
    }
}

