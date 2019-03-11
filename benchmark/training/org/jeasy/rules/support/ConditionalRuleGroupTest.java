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


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ConditionalRuleGroupTest {
    @Mock
    private Rule rule1;

    @Mock
    private Rule rule2;

    @Mock
    private Rule conditionalRule;

    private Facts facts = new Facts();

    private Rules rules = new Rules();

    private DefaultRulesEngine rulesEngine = new DefaultRulesEngine();

    private ConditionalRuleGroup conditionalRuleGroup;

    @Test
    public void rulesMustNotBeExecutedIfConditionalRuleEvaluatesToFalse() throws Exception {
        // Given
        Mockito.when(conditionalRule.evaluate(facts)).thenReturn(false);
        conditionalRuleGroup.addRule(rule1);
        conditionalRuleGroup.addRule(rule2);
        conditionalRuleGroup.addRule(conditionalRule);
        rules.register(conditionalRuleGroup);
        // When
        rulesEngine.fire(rules, facts);
        // Then
        /* The composing rules should not be executed
        since the conditional rule evaluate to FALSE
         */
        // primaryRule should not be executed
        Mockito.verify(conditionalRule, Mockito.never()).execute(facts);
        // Rule 1 should not be executed
        Mockito.verify(rule1, Mockito.never()).execute(facts);
        // Rule 2 should not be executed
        Mockito.verify(rule2, Mockito.never()).execute(facts);
    }

    @Test
    public void rulesMustBeExecutedForThoseThatEvaluateToTrueIfConditionalRuleEvaluatesToTrue() throws Exception {
        // Given
        Mockito.when(conditionalRule.evaluate(facts)).thenReturn(true);
        conditionalRuleGroup.addRule(rule1);
        conditionalRuleGroup.addRule(rule2);
        conditionalRuleGroup.addRule(conditionalRule);
        rules.register(conditionalRuleGroup);
        // When
        rulesEngine.fire(rules, facts);
        // Then
        /* Some of he composing rules should be executed
        since the conditional rule evaluate to TRUE
         */
        // primaryRule should be executed
        Mockito.verify(conditionalRule, Mockito.times(1)).execute(facts);
        // Rule 1 should not be executed
        Mockito.verify(rule1, Mockito.never()).execute(facts);
        // Rule 2 should be executed
        Mockito.verify(rule2, Mockito.times(1)).execute(facts);
    }

    @Test
    public void whenARuleIsRemoved_thenItShouldNotBeEvaluated() throws Exception {
        // Given
        Mockito.when(conditionalRule.evaluate(facts)).thenReturn(true);
        conditionalRuleGroup.addRule(rule1);
        conditionalRuleGroup.addRule(rule2);
        conditionalRuleGroup.addRule(conditionalRule);
        conditionalRuleGroup.removeRule(rule2);
        rules.register(conditionalRuleGroup);
        // When
        rulesEngine.fire(rules, facts);
        // Then
        // primaryRule should be executed
        Mockito.verify(conditionalRule, Mockito.times(1)).execute(facts);
        // Rule 1 should not be executed
        Mockito.verify(rule1, Mockito.times(1)).evaluate(facts);
        Mockito.verify(rule1, Mockito.never()).execute(facts);
        // Rule 2 should not be evaluated nor executed
        Mockito.verify(rule2, Mockito.never()).evaluate(facts);
        Mockito.verify(rule2, Mockito.never()).execute(facts);
    }

    @Test
    public void testCompositeRuleWithAnnotatedComposingRules() throws Exception {
        // Given
        Mockito.when(conditionalRule.evaluate(facts)).thenReturn(true);
        ConditionalRuleGroupTest.MyRule rule = new ConditionalRuleGroupTest.MyRule();
        conditionalRuleGroup = new ConditionalRuleGroup("myConditinalRule");
        conditionalRuleGroup.addRule(rule);
        Mockito.when(conditionalRule.compareTo(ArgumentMatchers.any(Rule.class))).thenReturn(1);
        conditionalRuleGroup.addRule(conditionalRule);
        rules.register(conditionalRuleGroup);
        // When
        rulesEngine.fire(rules, facts);
        // Then
        Mockito.verify(conditionalRule, Mockito.times(1)).execute(facts);
        assertThat(rule.isExecuted()).isTrue();
    }

    @Test
    public void whenAnnotatedRuleIsRemoved_thenItsProxyShouldBeRetrieved() throws Exception {
        // Given
        Mockito.when(conditionalRule.evaluate(facts)).thenReturn(true);
        ConditionalRuleGroupTest.MyRule rule = new ConditionalRuleGroupTest.MyRule();
        ConditionalRuleGroupTest.MyAnnotatedRule annotatedRule = new ConditionalRuleGroupTest.MyAnnotatedRule();
        conditionalRuleGroup = new ConditionalRuleGroup("myCompositeRule", "composite rule with mixed types of rules");
        conditionalRuleGroup.addRule(rule);
        conditionalRuleGroup.addRule(annotatedRule);
        conditionalRuleGroup.removeRule(annotatedRule);
        Mockito.when(conditionalRule.compareTo(ArgumentMatchers.any(Rule.class))).thenReturn(1);
        conditionalRuleGroup.addRule(conditionalRule);
        rules.register(conditionalRuleGroup);
        // When
        rulesEngine.fire(rules, facts);
        // Then
        Mockito.verify(conditionalRule, Mockito.times(1)).execute(facts);
        assertThat(rule.isExecuted()).isTrue();
        assertThat(annotatedRule.isExecuted()).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void twoRulesWithSameHighestPriorityIsNotAllowed() {
        conditionalRuleGroup.addRule(new ConditionalRuleGroupTest.MyOtherRule(1));
        conditionalRuleGroup.addRule(new ConditionalRuleGroupTest.MyOtherRule(2));
        conditionalRuleGroup.addRule(new ConditionalRuleGroupTest.MyRule());
        rules.register(conditionalRuleGroup);
        rulesEngine.fire(rules, facts);
    }

    @Test
    public void twoRulesWithSamePriorityIsAllowedIfAnotherRuleHasHigherPriority() {
        ConditionalRuleGroupTest.MyOtherRule rule1 = new ConditionalRuleGroupTest.MyOtherRule(3);
        conditionalRuleGroup.addRule(rule1);
        conditionalRuleGroup.addRule(new ConditionalRuleGroupTest.MyOtherRule(2));
        conditionalRuleGroup.addRule(new ConditionalRuleGroupTest.MyRule());
        rules.register(conditionalRuleGroup);
        rulesEngine.fire(rules, facts);
        assertThat(rule1.isExecuted()).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void aRuleWithoutPriorityHasAHighPriororty() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ConditionalRuleGroupTest.MyOtherRule rule1 = new ConditionalRuleGroupTest.MyOtherRule(3);
        conditionalRuleGroup.addRule(rule1);
        conditionalRuleGroup.addRule(new ConditionalRuleGroupTest.UnprioritizedRule());
        Method m = conditionalRuleGroup.getClass().getDeclaredMethod("sortRules");
        m.setAccessible(true);
        List<Rule> sorted = ((List<Rule>) (m.invoke(conditionalRuleGroup)));
        assertThat(sorted.get(0).getPriority()).isEqualTo(((Integer.MAX_VALUE) - 1));
        assertThat(sorted.get(1).getPriority()).isEqualTo(3);
    }

    @org.jeasy.rules.annotation.Rule
    public class MyRule {
        boolean executed;

        @Condition
        public boolean when() {
            return true;
        }

        @Action
        public void then() {
            executed = true;
        }

        @Priority
        public int priority() {
            return 2;
        }

        public boolean isExecuted() {
            return executed;
        }
    }

    @org.jeasy.rules.annotation.Rule
    public static class MyAnnotatedRule {
        private boolean executed;

        @Condition
        public boolean evaluate() {
            return true;
        }

        @Action
        public void execute() {
            executed = true;
        }

        @Priority
        public int priority() {
            return 3;
        }

        public boolean isExecuted() {
            return executed;
        }
    }

    @org.jeasy.rules.annotation.Rule
    public class MyOtherRule {
        boolean executed;

        private int priority;

        public MyOtherRule(int priority) {
            this.priority = priority;
        }

        @Condition
        public boolean when() {
            return true;
        }

        @Action
        public void then() {
            executed = true;
        }

        @Priority
        public int priority() {
            return priority;
        }

        public boolean isExecuted() {
            return executed;
        }
    }

    @org.jeasy.rules.annotation.Rule
    public class UnprioritizedRule {
        boolean executed;

        @Condition
        public boolean when() {
            return false;
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

