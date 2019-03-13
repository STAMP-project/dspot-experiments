/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;


/**
 * Tests negative patterns with or without additional constraints and events are
 * inserted through one or more entry points.
 * BZ-978979
 */
@RunWith(Parameterized.class)
public class NegativePatternsTest {
    private static final int LOOPS = 300;

    private static final int SHORT_SLEEP_TIME = 20;

    private static final int LONG_SLEEP_TIME = 30;

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    private KieSession ksession;

    private NegativePatternsTest.TrackingAgendaEventListener firedRulesListener;

    public NegativePatternsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Test
    public void testSingleEvent() {
        final EntryPoint entryPoint = ksession.getEntryPoint("EventStream");
        int count = 0;
        // no rules should be fired in the beginning
        advanceTime(NegativePatternsTest.LONG_SLEEP_TIME);
        Assert.assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));
        // after firing the rule will wait for 18ms
        ksession.fireAllRules();
        Assert.assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));
        count++;
        advanceTime(NegativePatternsTest.LONG_SLEEP_TIME);
        ksession.fireAllRules();
        Assert.assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));
        final FactHandle event = entryPoint.insert(new NegativePatternsTest.TestEvent(0, "EventA"));
        ksession.fireAllRules();
        advanceTime(NegativePatternsTest.LONG_SLEEP_TIME);
        ksession.fireAllRules();
        Assert.assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));
        entryPoint.delete(event);
        ksession.fireAllRules();
        count++;
        advanceTime(NegativePatternsTest.LONG_SLEEP_TIME);
        ksession.fireAllRules();
        Assert.assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));
        // rule was already fired and no changes were made to working memory
        ksession.fireAllRules();
        advanceTime(NegativePatternsTest.LONG_SLEEP_TIME);
        ksession.fireAllRules();
        Assert.assertEquals(count, firedRulesListener.ruleFiredCount("SingleAbsence"));
    }

    @Test
    public void testConstrainedAbsence() {
        final EntryPoint entryPoint = ksession.getEntryPoint("EventStream");
        int count = 0;
        count++;
        for (int i = 0; i < (NegativePatternsTest.LOOPS); i++) {
            entryPoint.insert(new NegativePatternsTest.TestEvent(count, "EventB"));
            ksession.fireAllRules();
            advanceTime(NegativePatternsTest.LONG_SLEEP_TIME);
        }
        FactHandle handle;
        for (int i = 0; i < (NegativePatternsTest.LOOPS); i++) {
            handle = entryPoint.insert(new NegativePatternsTest.TestEvent(i, "EventA"));
            advanceTime(NegativePatternsTest.LONG_SLEEP_TIME);
            ksession.fireAllRules();
            entryPoint.delete(handle);
            count++;
            advanceTime(NegativePatternsTest.LONG_SLEEP_TIME);
            ksession.fireAllRules();
        }
        ksession.fireAllRules();
        Assert.assertEquals(count, firedRulesListener.ruleFiredCount("SingleConstrained"));
    }

    @Test
    public void testMultipleEvents() {
        final EntryPoint entryPoint = ksession.getEntryPoint("EventStream");
        int count = 0;
        for (; count < ((NegativePatternsTest.LOOPS) / 2);) {
            entryPoint.insert(new NegativePatternsTest.TestEvent(count, "EventA"));
            ksession.fireAllRules();
            count++;
            advanceTime(NegativePatternsTest.SHORT_SLEEP_TIME);
            ksession.fireAllRules();
        }
        Assert.assertEquals(count, firedRulesListener.ruleFiredCount("MultipleEvents"));
        entryPoint.insert(new NegativePatternsTest.TestEvent(count, "EventA"));
        final FactHandle handle = entryPoint.insert(new NegativePatternsTest.TestEvent((-1), "EventB"));
        advanceTime(NegativePatternsTest.SHORT_SLEEP_TIME);
        ksession.fireAllRules();
        entryPoint.delete(handle);
        ksession.fireAllRules();
        // it shouldn't fire because of the duration
        advanceTime(NegativePatternsTest.SHORT_SLEEP_TIME);
        ksession.fireAllRules();
        // it shouldn't fire because event A is gone out of window
        for (; count < (NegativePatternsTest.LOOPS);) {
            entryPoint.insert(new NegativePatternsTest.TestEvent(count, "EventA"));
            ksession.fireAllRules();
            count++;
            advanceTime(NegativePatternsTest.SHORT_SLEEP_TIME);
            ksession.fireAllRules();
        }
        ksession.fireAllRules();
        Assert.assertEquals(count, firedRulesListener.ruleFiredCount("MultipleEvents"));
    }

    @Test
    public void testMultipleEntryPoints() {
        final EntryPoint entryPoint = ksession.getEntryPoint("EventStream");
        final EntryPoint otherStream = ksession.getEntryPoint("OtherStream");
        int count = 0;
        count++;
        ksession.fireAllRules();
        advanceTime(NegativePatternsTest.LONG_SLEEP_TIME);
        ksession.fireAllRules();
        Assert.assertEquals(count, firedRulesListener.ruleFiredCount("MultipleEntryPoints"));
        FactHandle handle;
        for (int i = 0; i < (NegativePatternsTest.LOOPS); i++) {
            handle = entryPoint.insert(new NegativePatternsTest.TestEvent(count, "EventA"));
            ksession.fireAllRules();
            advanceTime(NegativePatternsTest.LONG_SLEEP_TIME);
            entryPoint.delete(handle);
            ksession.fireAllRules();
            count++;
            advanceTime(NegativePatternsTest.LONG_SLEEP_TIME);
            ksession.fireAllRules();
        }
        for (int i = 0; i < (NegativePatternsTest.LOOPS); i++) {
            handle = otherStream.insert(new NegativePatternsTest.TestEvent(count, "EventB"));
            advanceTime(NegativePatternsTest.LONG_SLEEP_TIME);
            ksession.fireAllRules();
            otherStream.delete(handle);
            count++;
            advanceTime(NegativePatternsTest.SHORT_SLEEP_TIME);
            ksession.fireAllRules();
        }
        ksession.fireAllRules();
        Assert.assertEquals(count, firedRulesListener.ruleFiredCount("MultipleEntryPoints"));
    }

    /**
     * Simple event used for tests.
     */
    public static class TestEvent implements Serializable {
        private static final long serialVersionUID = -6985691286327371275L;

        private final Integer id;

        private final String name;

        public TestEvent(final Integer id, final String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return String.format("TestEvent[id=%s, name=%s]", id, name);
        }
    }

    /**
     * Listener tracking number of rules fired.
     */
    public static class TrackingAgendaEventListener extends DefaultAgendaEventListener {
        private final Map<String, Integer> rulesFired = new HashMap<String, Integer>();

        @Override
        public void afterMatchFired(final AfterMatchFiredEvent event) {
            final String rule = event.getMatch().getRule().getName();
            if (isRuleFired(rule)) {
                rulesFired.put(rule, ((rulesFired.get(rule)) + 1));
            } else {
                rulesFired.put(rule, 1);
            }
        }

        /**
         * Return true if the rule was fired at least once
         *
         * @param rule
         * 		- name of the rule
         * @return true if the rule was fired
         */
        public boolean isRuleFired(final String rule) {
            return rulesFired.containsKey(rule);
        }

        /**
         * Returns number saying how many times the rule was fired
         *
         * @param rule
         * 		- name of the rule
         * @return number how many times rule was fired, 0 if rule wasn't fired
         */
        public int ruleFiredCount(final String rule) {
            if (isRuleFired(rule)) {
                return rulesFired.get(rule);
            } else {
                return 0;
            }
        }

        public void clear() {
            rulesFired.clear();
        }
    }
}

