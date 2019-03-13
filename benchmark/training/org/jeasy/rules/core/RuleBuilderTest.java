/**
 * The MIT License
 *
 *  Copyright (c) 2019, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.jeasy.rules.core;


import Rule.DEFAULT_DESCRIPTION;
import Rule.DEFAULT_NAME;
import Rule.DEFAULT_PRIORITY;
import java.util.Arrays;
import org.jeasy.rules.api.Action;
import org.jeasy.rules.api.Condition;
import org.jeasy.rules.api.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RuleBuilderTest {
    @Mock
    private Condition condition;

    @Mock
    private Action action1;

    @Mock
    private Action action2;

    @Test
    public void testDefaultRuleCreationWithDefaultValues() throws Exception {
        // when
        Rule rule = new RuleBuilder().build();
        // then
        assertThat(rule.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(rule.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(rule.getPriority()).isEqualTo(DEFAULT_PRIORITY);
        assertThat(rule).isInstanceOf(DefaultRule.class);
    }

    @Test
    public void testDefaultRuleCreationWithCustomValues() throws Exception {
        // when
        Rule rule = new RuleBuilder().name("myRule").description("myRuleDescription").priority(3).when(condition).then(action1).then(action2).build();
        // then
        assertThat(rule.getName()).isEqualTo("myRule");
        assertThat(rule.getDescription()).isEqualTo("myRuleDescription");
        assertThat(rule.getPriority()).isEqualTo(3);
        assertThat(rule).isInstanceOf(DefaultRule.class);
        assertThat(rule).extracting("condition").containsExactly(condition);
        assertThat(rule).extracting("actions").containsExactly(Arrays.asList(action1, action2));
    }
}

