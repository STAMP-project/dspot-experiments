/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import KieServices.Factory;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;


/**
 * Tests stateful/stateless KieSession listeners registration - DROOLS-818.
 */
public class ListenersTest {
    private static final ReleaseId RELEASE_ID = Factory.get().newReleaseId("org.drools.compiler.test", "listeners-test", "1.0.0");

    private static final String PACKAGE = ListenersTest.class.getPackage().getName();

    private static final String PACKAGE_PATH = ListenersTest.PACKAGE.replaceAll("\\.", "/");

    private static final String DRL = "import java.util.Collection\n" + ((("rule R1 when\n" + " String()\n") + "then\n") + "end\n");

    private KieServices ks = Factory.get();

    private KieSession kieSession;

    private StatelessKieSession statelessKieSession;

    @Test
    public void testRegisterAgendaEventListenerStateful() throws Exception {
        kieSession.insert("test");
        kieSession.fireAllRules();
        checkThatListenerFired(kieSession.getAgendaEventListeners());
    }

    @Test
    public void testRegisterRuleRuntimeEventListenerStateful() throws Exception {
        kieSession.insert("test");
        kieSession.fireAllRules();
        checkThatListenerFired(kieSession.getRuleRuntimeEventListeners());
    }

    @Test
    public void testRegisterAgendaEventListenerStateless() throws Exception {
        statelessKieSession.execute(Factory.get().getCommands().newInsert("test"));
        checkThatListenerFired(statelessKieSession.getAgendaEventListeners());
    }

    @Test
    public void testRegisterRuleEventListenerStateless() throws Exception {
        statelessKieSession.execute(Factory.get().getCommands().newInsert("test"));
        checkThatListenerFired(statelessKieSession.getRuleRuntimeEventListeners());
    }

    /**
     * Listener which just marks that it had fired.
     */
    public interface MarkingListener {
        boolean hasFired();
    }

    /**
     * A listener marking that an AgendaEvent has fired.
     */
    public static class MarkingAgendaEventListener extends DefaultAgendaEventListener implements ListenersTest.MarkingListener {
        private final AtomicBoolean fired = new AtomicBoolean(false);

        @Override
        public void afterMatchFired(final AfterMatchFiredEvent event) {
            super.afterMatchFired(event);
            this.fired.compareAndSet(false, true);
        }

        public boolean hasFired() {
            return this.fired.get();
        }
    }

    /**
     * A listener marking that a RuleRuntimeEvent has fired.
     */
    public static class MarkingRuntimeEventListener extends DefaultRuleRuntimeEventListener implements ListenersTest.MarkingListener {
        private final AtomicBoolean fired = new AtomicBoolean(false);

        @Override
        public void objectInserted(final ObjectInsertedEvent event) {
            super.objectInserted(event);
            this.fired.compareAndSet(false, true);
        }

        public boolean hasFired() {
            return this.fired.get();
        }
    }
}

