/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.integrationtests.session;


import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class RuleRuntimeEventTest extends CommonTestMethodBase {
    @Test
    public void testEventModel() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_EventModel.drl"));
        final KieSession wm = createKnowledgeSession(kbase);
        final RuleRuntimeEventListener wmel = Mockito.mock(RuleRuntimeEventListener.class);
        wm.addEventListener(wmel);
        final Cheese stilton = new Cheese("stilton", 15);
        final FactHandle stiltonHandle = wm.insert(stilton);
        final ArgumentCaptor<ObjectInsertedEvent> oic = ArgumentCaptor.forClass(ObjectInsertedEvent.class);
        Mockito.verify(wmel).objectInserted(oic.capture());
        Assert.assertSame(stiltonHandle, oic.getValue().getFactHandle());
        wm.update(stiltonHandle, stilton);
        final ArgumentCaptor<ObjectUpdatedEvent> ouc = ArgumentCaptor.forClass(ObjectUpdatedEvent.class);
        Mockito.verify(wmel).objectUpdated(ouc.capture());
        Assert.assertSame(stiltonHandle, ouc.getValue().getFactHandle());
        wm.delete(stiltonHandle);
        final ArgumentCaptor<ObjectDeletedEvent> orc = ArgumentCaptor.forClass(ObjectDeletedEvent.class);
        Mockito.verify(wmel).objectDeleted(orc.capture());
        Assert.assertSame(stiltonHandle, orc.getValue().getFactHandle());
    }
}

