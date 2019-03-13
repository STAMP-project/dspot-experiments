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


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionPseudoClock;


public class QueryCepTest {
    private KieSession ksession;

    private SessionPseudoClock clock;

    private EntryPoint firstEntryPoint;

    private EntryPoint secondEntryPoint;

    @Test
    public void noResultTest() {
        QueryResults results = ksession.getQueryResults("EventsFromStream");
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void withResultTest() {
        secondEntryPoint.insert(new QueryCepTest.TestEvent("minusOne"));
        clock.advanceTime(5, TimeUnit.SECONDS);
        firstEntryPoint.insert(new QueryCepTest.TestEvent("zero"));
        secondEntryPoint.insert(new QueryCepTest.TestEvent("one"));
        clock.advanceTime(10, TimeUnit.SECONDS);
        secondEntryPoint.insert(new QueryCepTest.TestEvent("two"));
        clock.advanceTime(5, TimeUnit.SECONDS);
        secondEntryPoint.insert(new QueryCepTest.TestEvent("three"));
        QueryResults results = ksession.getQueryResults("ZeroToNineteenSeconds");
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void withNoResultTest() {
        secondEntryPoint.insert(new QueryCepTest.TestEvent("minusOne"));
        clock.advanceTime(5, TimeUnit.SECONDS);
        firstEntryPoint.insert(new QueryCepTest.TestEvent("zero"));
        secondEntryPoint.insert(new QueryCepTest.TestEvent("one"));
        clock.advanceTime(10, TimeUnit.SECONDS);
        secondEntryPoint.insert(new QueryCepTest.TestEvent("two"));
        // the following expires event "zero" and "one", causing the query to no longer match
        clock.advanceTime(10, TimeUnit.SECONDS);
        secondEntryPoint.insert(new QueryCepTest.TestEvent("three"));
        QueryResults results = ksession.getQueryResults("ZeroToNineteenSeconds");
        Assert.assertEquals(0, results.size());
    }

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

