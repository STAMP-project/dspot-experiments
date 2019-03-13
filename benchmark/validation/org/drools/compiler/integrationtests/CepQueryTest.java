/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionPseudoClock;


/**
 * Tests queries using temporal operators on events from two entry points.
 */
@RunWith(Parameterized.class)
public class CepQueryTest {
    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    private KieSession ksession;

    private SessionPseudoClock clock;

    private EntryPoint firstEntryPoint;

    private EntryPoint secondEntryPoint;

    public CepQueryTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    /**
     * Tests query using temporal operator 'after' on events from two entry points.
     */
    @Test
    public void testQueryWithAfter() {
        this.eventsInitialization();
        final QueryResults results = ksession.getQueryResults("EventsAfterZeroToNineSeconds");
        Assert.assertEquals("Unexpected query result length", 1, results.size());
        Assert.assertEquals("Unexpected query result content", "one", ((CepQueryTest.TestEvent) (results.iterator().next().get("$result"))).getName());
    }

    /**
     * Simple event used in the test.
     */
    public static class TestEvent {
        private final String name;

        public TestEvent(final String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            return ("TestEvent[" + (name)) + "]";
        }
    }
}

