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
package org.jeasy.rules.api;


import java.util.HashSet;
import java.util.Set;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.core.BasicRule;
import org.junit.Test;


public class RulesTest {
    private Rules rules = new Rules();

    @Test
    public void register() throws Exception {
        rules.register(new RulesTest.DummyRule());
        assertThat(rules).hasSize(1);
    }

    @Test
    public void rulesMustHaveUniqueName() throws Exception {
        Rule r1 = new BasicRule("rule");
        Rule r2 = new BasicRule("rule");
        Set<Rule> ruleSet = new HashSet<>();
        ruleSet.add(r1);
        ruleSet.add(r2);
        rules = new Rules(ruleSet);
        assertThat(rules).hasSize(1);
    }

    @Test
    public void unregister() throws Exception {
        RulesTest.DummyRule rule = new RulesTest.DummyRule();
        rules.register(rule);
        rules.unregister(rule);
        assertThat(rules).isEmpty();
    }

    @Test
    public void unregisterByName() throws Exception {
        Rule r1 = new BasicRule("rule1");
        Rule r2 = new BasicRule("rule2");
        Set<Rule> ruleSet = new HashSet<>();
        ruleSet.add(r1);
        ruleSet.add(r2);
        rules = new Rules(ruleSet);
        rules.unregister("rule2");
        assertThat(rules).hasSize(1).containsExactly(r1);
    }

    @Test
    public void unregisterByNameNonExistingRule() throws Exception {
        Rule r1 = new BasicRule("rule1");
        Set<Rule> ruleSet = new HashSet<>();
        ruleSet.add(r1);
        rules = new Rules(ruleSet);
        rules.unregister("rule2");
        assertThat(rules).hasSize(1).containsExactly(r1);
    }

    @Test
    public void isEmpty() throws Exception {
        assertThat(rules.isEmpty()).isTrue();
    }

    @Test
    public void clear() throws Exception {
        rules.register(new RulesTest.DummyRule());
        rules.clear();
        assertThat(rules).isEmpty();
    }

    @Test
    public void sort() throws Exception {
        Rule r1 = new BasicRule("rule", "", 1);
        Rule r2 = new BasicRule("rule", "", Integer.MAX_VALUE);
        RulesTest.DummyRule r3 = new RulesTest.DummyRule();
        rules.register(r3);
        rules.register(r1);
        rules.register(r2);
        assertThat(rules).startsWith(r1).endsWith(r2);
    }

    @Test(expected = NullPointerException.class)
    public void whenRegisterNullRule_thenShouldThrowNullPointerException() throws Exception {
        rules.register(null);
    }

    @Test(expected = NullPointerException.class)
    public void whenUnregisterNullRule_thenShouldThrowNullPointerException() throws Exception {
        rules.unregister(null);
    }

    @org.jeasy.rules.annotation.Rule
    class DummyRule {
        @Condition
        public boolean when() {
            return true;
        }

        @Action
        public void then() {
        }
    }
}

