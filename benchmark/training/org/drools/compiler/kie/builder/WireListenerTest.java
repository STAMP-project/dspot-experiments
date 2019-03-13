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
package org.drools.compiler.kie.builder;


import KieServices.Factory;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;


public class WireListenerTest {
    private static final List<ObjectInsertedEvent> insertEvents = new ArrayList<ObjectInsertedEvent>();

    private static final List<ObjectUpdatedEvent> updateEvents = new ArrayList<ObjectUpdatedEvent>();

    private static final List<ObjectDeletedEvent> retractEvents = new ArrayList<ObjectDeletedEvent>();

    @Test
    public void testWireListener() throws Exception {
        KieServices ks = Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "listener-test", "1.0");
        build(ks, releaseId);
        KieContainer kieContainer = ks.newKieContainer(releaseId);
        KieSession ksession = kieContainer.newKieSession();
        ksession.fireAllRules();
        Assert.assertEquals(1, WireListenerTest.insertEvents.size());
        Assert.assertEquals(1, WireListenerTest.updateEvents.size());
        Assert.assertEquals(1, WireListenerTest.retractEvents.size());
    }

    public static class RecordingWorkingMemoryEventListener implements RuleRuntimeEventListener {
        @Override
        public void objectInserted(ObjectInsertedEvent event) {
            WireListenerTest.insertEvents.add(event);
        }

        @Override
        public void objectUpdated(ObjectUpdatedEvent event) {
            WireListenerTest.updateEvents.add(event);
        }

        @Override
        public void objectDeleted(ObjectDeletedEvent event) {
            WireListenerTest.retractEvents.add(event);
        }
    }
}

