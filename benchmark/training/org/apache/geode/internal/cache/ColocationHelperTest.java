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


import PartitionedRegionHelper.PR_ROOT_REGION_NAME;
import org.apache.geode.CancelCriterion;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.PartitionAttributes;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ColocationHelperTest {
    private GemFireCacheImpl cache;

    private GemFireCacheImpl oldCacheInstance;

    private InternalDistributedSystem system;

    private PartitionedRegion pr;

    private DistributedRegion prRoot;

    private PartitionAttributes pa;

    private PartitionRegionConfig prc;

    private Logger logger;

    private Appender mockAppender;

    private ArgumentCaptor<LogEvent> loggingEventCaptor;

    /**
     * Test method for
     * {@link org.apache.geode.internal.cache.ColocationHelper#getColocatedRegion(org.apache.geode.internal.cache.PartitionedRegion)}.
     */
    @Test
    public void testGetColocatedRegionThrowsIllegalStateExceptionForMissingParentRegion() {
        Mockito.when(pr.getCache()).thenReturn(cache);
        Mockito.when(cache.getRegion(PR_ROOT_REGION_NAME, true)).thenReturn(Mockito.mock(DistributedRegion.class));
        Mockito.when(pr.getPartitionAttributes()).thenReturn(pa);
        Mockito.when(pr.getFullPath()).thenReturn("/region1");
        Mockito.when(pa.getColocatedWith()).thenReturn("region2");
        PartitionedRegion colocatedPR;
        boolean caughtIllegalStateException = false;
        try {
            colocatedPR = ColocationHelper.getColocatedRegion(pr);
        } catch (Exception e) {
            Assert.assertEquals("Expected IllegalStateException for missing colocated parent region", IllegalStateException.class, e.getClass());
            Assert.assertTrue(("Expected IllegalStateException to be thrown for missing colocated region: " + (e.getMessage())), e.getMessage().matches("Region specified in 'colocated-with' .* does not exist.*"));
            caughtIllegalStateException = true;
        }
        Assert.assertTrue(caughtIllegalStateException);
    }

    /**
     * Test method for
     * {@link org.apache.geode.internal.cache.ColocationHelper#getColocatedRegion(org.apache.geode.internal.cache.PartitionedRegion)}.
     */
    @Test
    public void testGetColocatedRegionLogsWarningForMissingRegionWhenPRConfigHasRegion() {
        Mockito.when(pr.getCache()).thenReturn(cache);
        Mockito.when(cache.getRegion(PR_ROOT_REGION_NAME, true)).thenReturn(prRoot);
        Mockito.when(pr.getPartitionAttributes()).thenReturn(pa);
        Mockito.when(pr.getFullPath()).thenReturn("/region1");
        Mockito.when(pa.getColocatedWith()).thenReturn("region2");
        Mockito.when(get("#region2")).thenReturn(prc);
        PartitionedRegion colocatedPR = null;
        boolean caughtIllegalStateException = false;
        try {
            colocatedPR = ColocationHelper.getColocatedRegion(pr);
        } catch (Exception e) {
            Assert.assertEquals("Expected IllegalStateException for missing colocated parent region", IllegalStateException.class, e.getClass());
            Assert.assertTrue("Expected IllegalStateException to be thrown for missing colocated region", e.getMessage().matches("Region specified in 'colocated-with' .* does not exist.*"));
            caughtIllegalStateException = true;
        }
        Assert.assertTrue(caughtIllegalStateException);
    }

    @Test
    public void testGetColocatedRegionThrowsCacheClosedExceptionWhenCacheIsClosed() {
        Mockito.when(pr.getCache()).thenReturn(cache);
        DistributedRegion prRoot = Mockito.mock(DistributedRegion.class);
        Mockito.when(cache.getRegion(PR_ROOT_REGION_NAME, true)).thenReturn(prRoot);
        Mockito.when(pr.getPartitionAttributes()).thenReturn(pa);
        Mockito.when(pa.getColocatedWith()).thenReturn("region2");
        PartitionRegionConfig partitionRegionConfig = Mockito.mock(PartitionRegionConfig.class);
        Mockito.when(prRoot.get(ArgumentMatchers.any())).thenReturn(partitionRegionConfig);
        CancelCriterion cancelCriterion = Mockito.mock(CancelCriterion.class);
        Mockito.when(cache.getCancelCriterion()).thenReturn(cancelCriterion);
        Mockito.doThrow(CacheClosedException.class).when(cancelCriterion).checkCancelInProgress(ArgumentMatchers.any());
        assertThatThrownBy(() -> ColocationHelper.getColocatedRegion(pr)).isInstanceOf(CacheClosedException.class);
    }
}

