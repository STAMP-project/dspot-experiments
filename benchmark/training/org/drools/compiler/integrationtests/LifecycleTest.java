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
import org.drools.compiler.StockTick;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;


/**
 * Tests updating events using API.
 */
public class LifecycleTest {
    private KieSession kieSession;

    @Test
    public void testExpires() throws Exception {
        EntryPoint entryPoint = kieSession.getEntryPoint("EventStream");
        StockTick event = new StockTick();
        FactHandle handle = entryPoint.insert(event);
        Assert.assertTrue(entryPoint.getFactHandles().contains(handle));
        kieSession.fireAllRules();
        Assert.assertTrue(entryPoint.getFactHandles().contains(handle));
        advanceTime(5, TimeUnit.SECONDS);
        kieSession.fireAllRules();
        Assert.assertFalse(entryPoint.getFactHandles().contains(handle));
    }
}

