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
package org.jeasy.rules.support;


import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ActivationRuleGroupTest {
    private Facts facts = new Facts();

    private Rules rules = new Rules();

    private DefaultRulesEngine rulesEngine = new DefaultRulesEngine();

    @Test
    public void onlySelectedRuleShouldBeExecuted_whenComposingRulesHaveDifferentPriorities() {
        // given
        ActivationRuleGroupTest.Rule1 rule1 = new ActivationRuleGroupTest.Rule1();
        ActivationRuleGroupTest.Rule2 rule2 = new ActivationRuleGroupTest.Rule2();
        ActivationRuleGroup activationRuleGroup = new ActivationRuleGroup("my activation rule", "rule1 xor rule2");
        activationRuleGroup.addRule(rule1);
        activationRuleGroup.addRule(rule2);
        rules.register(activationRuleGroup);
        // when
        rulesEngine.fire(rules, facts);
        // then
        assertThat(rule1.isExecuted()).isTrue();
        assertThat(rule2.isExecuted()).isFalse();
    }

    @Test
    public void onlySelectedRuleShouldBeExecuted_whenComposingRulesHaveSamePriority() {
        // given
        ActivationRuleGroupTest.Rule2 rule2 = new ActivationRuleGroupTest.Rule2();
        ActivationRuleGroupTest.Rule3 rule3 = new ActivationRuleGroupTest.Rule3();
        ActivationRuleGroup activationRuleGroup = new ActivationRuleGroup("my activation rule", "rule2 xor rule3");
        activationRuleGroup.addRule(rule2);
        activationRuleGroup.addRule(rule3);
        rules.register(activationRuleGroup);
        // when
        rulesEngine.fire(rules, facts);
        // then
        // we don't know upfront which rule will be selected, but only one of them should be executed
        if (rule2.isExecuted()) {
            assertThat(rule3.isExecuted()).isFalse();
        } else {
            assertThat(rule3.isExecuted()).isTrue();
        }
    }

    @Rule(priority = 1)
    public class Rule1 {
        private boolean executed;

        @Condition
        public boolean when() {
            return true;
        }

        @Action
        public void then() {
            executed = true;
        }

        public boolean isExecuted() {
            return executed;
        }
    }

    @Rule(priority = 2)
    public class Rule2 {
        private boolean executed;

        @Condition
        public boolean when() {
            return true;
        }

        @Action
        public void then() {
            executed = true;
        }

        public boolean isExecuted() {
            return executed;
        }
    }

    @Rule(priority = 2)
    public class Rule3 {
        private boolean executed;

        @Condition
        public boolean when() {
            return true;
        }

        @Action
        public void then() {
            executed = true;
        }

        public boolean isExecuted() {
            return executed;
        }
    }
}

