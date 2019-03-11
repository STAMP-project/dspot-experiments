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


import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.RuleListener;
import org.jeasy.rules.api.RulesEngineListener;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DefaultRulesEngineTest extends AbstractTest {
    @Mock
    private RuleListener ruleListener;

    @Mock
    private RulesEngineListener rulesEngineListener;

    private DefaultRulesEngineTest.AnnotatedRule annotatedRule;

    @Test
    public void whenConditionIsTrue_thenActionShouldBeExecuted() throws Exception {
        // Given
        Mockito.when(rule1.evaluate(facts)).thenReturn(true);
        rules.register(rule1);
        // When
        rulesEngine.fire(rules, facts);
        // Then
        Mockito.verify(rule1).execute(facts);
    }

    @Test
    public void whenConditionIsFalse_thenActionShouldNotBeExecuted() throws Exception {
        // Given
        Mockito.when(rule1.evaluate(facts)).thenReturn(false);
        rules.register(rule1);
        // When
        rulesEngine.fire(rules, facts);
        // Then
        Mockito.verify(rule1, Mockito.never()).execute(facts);
    }

    @Test
    public void rulesMustBeTriggeredInTheirNaturalOrder() throws Exception {
        // Given
        Mockito.when(rule1.evaluate(facts)).thenReturn(true);
        Mockito.when(rule2.evaluate(facts)).thenReturn(true);
        Mockito.when(rule2.compareTo(rule1)).thenReturn(1);
        rules.register(rule1);
        rules.register(rule2);
        // When
        rulesEngine.fire(rules, facts);
        // Then
        InOrder inOrder = Mockito.inOrder(rule1, rule2);
        inOrder.verify(rule1).execute(facts);
        inOrder.verify(rule2).execute(facts);
    }

    @Test
    public void rulesMustBeCheckedInTheirNaturalOrder() throws Exception {
        // Given
        Mockito.when(rule1.evaluate(facts)).thenReturn(true);
        Mockito.when(rule2.evaluate(facts)).thenReturn(true);
        Mockito.when(rule2.compareTo(rule1)).thenReturn(1);
        rules.register(rule1);
        rules.register(rule2);
        // When
        rulesEngine.check(rules, facts);
        // Then
        InOrder inOrder = Mockito.inOrder(rule1, rule2);
        inOrder.verify(rule1).evaluate(facts);
        inOrder.verify(rule2).evaluate(facts);
    }

    @Test
    public void actionsMustBeExecutedInTheDefinedOrder() {
        // Given
        rules.register(annotatedRule);
        // When
        rulesEngine.fire(rules, facts);
        // Then
        Assert.assertEquals("012", annotatedRule.getActionSequence());
    }

    @Test
    public void annotatedRulesAndNonAnnotatedRulesShouldBeUsableTogether() throws Exception {
        // Given
        Mockito.when(rule1.evaluate(facts)).thenReturn(true);
        rules.register(rule1);
        rules.register(annotatedRule);
        // When
        rulesEngine.fire(rules, facts);
        // Then
        Mockito.verify(rule1).execute(facts);
        assertThat(annotatedRule.isExecuted()).isTrue();
    }

    @Test
    public void whenRuleNameIsNotSpecified_thenItShouldBeEqualToClassNameByDefault() throws Exception {
        org.jeasy.rules.api.Rule rule = RuleProxy.asRule(new DefaultRulesEngineTest.DummyRule());
        assertThat(rule.getName()).isEqualTo("DummyRule");
    }

    @Test
    public void whenRuleDescriptionIsNotSpecified_thenItShouldBeEqualToConditionNameFollowedByActionsNames() throws Exception {
        org.jeasy.rules.api.Rule rule = RuleProxy.asRule(new DefaultRulesEngineTest.DummyRule());
        assertThat(rule.getDescription()).isEqualTo("when condition then action1,action2");
    }

    @Test
    public void testCheckRules() throws Exception {
        // Given
        Mockito.when(rule1.evaluate(facts)).thenReturn(true);
        rules.register(rule1);
        rules.register(annotatedRule);
        // When
        Map<org.jeasy.rules.api.Rule, Boolean> result = rulesEngine.check(rules, facts);
        // Then
        assertThat(result).hasSize(2);
        for (org.jeasy.rules.api.Rule r : rules) {
            assertThat(result.get(r)).isTrue();
        }
    }

    @Test
    public void listenerShouldBeInvokedBeforeCheckingRules() throws Exception {
        // Given
        Mockito.when(rule1.evaluate(facts)).thenReturn(true);
        Mockito.when(ruleListener.beforeEvaluate(rule1, facts)).thenReturn(true);
        DefaultRulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.registerRuleListener(ruleListener);
        rules.register(rule1);
        // When
        rulesEngine.check(rules, facts);
        // Then
        Mockito.verify(ruleListener).beforeEvaluate(rule1, facts);
    }

    @Test
    public void nullFactsShouldNotCrashTheEngine() {
        // Given
        facts.put("foo", null);
        // When
        try {
            rulesEngine.fire(rules, facts);
        } catch (Exception e) {
            Assertions.fail("Unable to fire rules on known facts", e);
        }
        // Then
        // Should not throw exception
    }

    @Test
    public void testGetRuleListeners() throws Exception {
        // Given
        DefaultRulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.registerRuleListener(ruleListener);
        // When
        List<RuleListener> ruleListeners = rulesEngine.getRuleListeners();
        // Then
        assertThat(ruleListeners).contains(ruleListener);
    }

    @Test
    public void testGetRulesEngineListeners() throws Exception {
        // Given
        DefaultRulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.registerRulesEngineListener(rulesEngineListener);
        // When
        List<RulesEngineListener> rulesEngineListeners = rulesEngine.getRulesEngineListeners();
        // Then
        assertThat(rulesEngineListeners).contains(rulesEngineListener);
    }

    @org.jeasy.rules.annotation.Rule(name = "myRule", description = "my rule description")
    public class AnnotatedRule {
        private boolean executed;

        private String actionSequence = "";

        @Condition
        public boolean when() {
            return true;
        }

        @Action
        public void then0() throws Exception {
            actionSequence += "0";
        }

        @Action(order = 1)
        public void then1() throws Exception {
            actionSequence += "1";
        }

        @Action(order = 2)
        public void then2() throws Exception {
            actionSequence += "2";
            executed = true;
        }

        @Priority
        public int getPriority() {
            return 0;
        }

        public boolean isExecuted() {
            return executed;
        }

        public String getActionSequence() {
            return actionSequence;
        }
    }

    @org.jeasy.rules.annotation.Rule
    public class DummyRule {
        @Condition
        public boolean condition() {
            return true;
        }

        @Action(order = 1)
        public void action1() throws Exception {
            // no op
        }

        @Action(order = 2)
        public void action2() throws Exception {
            // no op
        }
    }
}

