/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import java.util.HashMap;
import java.util.Map;
import org.apache.geode.cache.CacheEvent;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.DistributedCacheOperation.CacheOperationMessage;
import org.apache.geode.internal.cache.persistence.PersistentMemberID;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DistributedCacheOperationTest {
    @Test
    public void shouldBeMockable() throws Exception {
        DistributedCacheOperation mockDistributedCacheOperation = Mockito.mock(DistributedCacheOperation.class);
        CacheOperationMessage mockCacheOperationMessage = Mockito.mock(CacheOperationMessage.class);
        Map<InternalDistributedMember, PersistentMemberID> persistentIds = new HashMap<>();
        Mockito.when(mockDistributedCacheOperation.supportsDirectAck()).thenReturn(false);
        mockDistributedCacheOperation.waitForAckIfNeeded(mockCacheOperationMessage, persistentIds);
        Mockito.verify(mockDistributedCacheOperation, Mockito.times(1)).waitForAckIfNeeded(mockCacheOperationMessage, persistentIds);
        assertThat(mockDistributedCacheOperation.supportsDirectAck()).isFalse();
    }

    /**
     * The startOperation and endOperation methods of DistributedCacheOperation record the
     * beginning and end of distribution of an operation. If startOperation is invoked it
     * is essential that endOperation be invoked or the state-flush operation will hang.<br>
     * This test ensures that if distribution of the operation throws an exception then
     * endOperation is correctly invoked before allowing the exception to escape the startOperation
     * method.
     */
    @Test
    public void endOperationIsInvokedOnDistributionError() {
        DistributedRegion region = Mockito.mock(DistributedRegion.class);
        CacheDistributionAdvisor advisor = Mockito.mock(CacheDistributionAdvisor.class);
        Mockito.when(region.getDistributionAdvisor()).thenReturn(advisor);
        DistributedCacheOperationTest.TestOperation operation = new DistributedCacheOperationTest.TestOperation(null);
        operation.region = region;
        try {
            startOperation();
        } catch (RuntimeException e) {
            Assert.assertEquals("boom", e.getMessage());
        }
        Assert.assertTrue(operation.endOperationInvoked);
    }

    static class TestOperation extends DistributedCacheOperation {
        boolean endOperationInvoked;

        DistributedRegion region;

        public TestOperation(CacheEvent event) {
            super(event);
        }

        @Override
        public DistributedRegion getRegion() {
            return region;
        }

        @Override
        public boolean containsRegionContentChange() {
            return true;
        }

        @Override
        public void endOperation(long viewVersion) {
            endOperationInvoked = true;
            super.endOperation(viewVersion);
        }

        @Override
        protected CacheOperationMessage createMessage() {
            return null;
        }

        @Override
        protected void _distribute() {
            throw new RuntimeException("boom");
        }
    }
}

