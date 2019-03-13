/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.test.aggregate;


import org.junit.Assert;
import org.junit.Test;


public class StubAggregateLifecycleTest {
    private StubAggregateLifecycle testSubject;

    @Test(expected = IllegalStateException.class)
    public void testLifecycleIsNotRegisteredAutomatically() {
        apply("test");
    }

    @Test(expected = IllegalStateException.class)
    public void testApplyingEventsAfterDeactivationFails() {
        testSubject.activate();
        testSubject.close();
        apply("test");
    }

    @Test
    public void testAppliedEventsArePassedToActiveLifecycle() {
        testSubject.activate();
        apply("test");
        Assert.assertEquals(1, testSubject.getAppliedEvents().size());
        Assert.assertEquals("test", testSubject.getAppliedEventPayloads().get(0));
        Assert.assertEquals("test", testSubject.getAppliedEvents().get(0).getPayload());
    }

    @Test
    public void testMarkDeletedIsRegisteredWithActiveLifecycle() {
        testSubject.activate();
        markDeleted();
        Assert.assertEquals(0, testSubject.getAppliedEvents().size());
        Assert.assertTrue(testSubject.isMarkedDeleted());
    }
}

