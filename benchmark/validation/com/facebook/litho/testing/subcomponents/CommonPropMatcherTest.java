/**
 * Copyright 2018-present Facebook, Inc.
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


import com.facebook.litho.Column;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.Row;
import com.facebook.litho.testing.ComponentsRule;
import com.facebook.litho.testing.assertj.LithoAssertions;
import com.facebook.litho.testing.assertj.SubComponentExtractor;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.widget.Card;
import com.facebook.litho.widget.TestCard;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(ComponentsTestRunner.class)
public class CommonPropMatcherTest {
    @Rule
    public ComponentsRule mComponentsRule = new ComponentsRule();

    @Test
    public void testTransitionKeyMatcher() {
        final ComponentContext c = mComponentsRule.getContext();
        final String key = "nocolusion";
        final Component component = Row.create(c).child(Card.create(c).transitionKey(key).content(Column.create(c))).build();
        LithoAssertions.assertThat(c, component).has(SubComponentExtractor.subComponentWith(c, TestCard.matcher(c).transitionKey(key).build()));
    }
}

