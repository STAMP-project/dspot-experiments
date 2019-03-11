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
package org.apache.geode.management.internal.cli.functions;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.PartitionAttributes;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.internal.cache.DistributedRegion;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.PartitionRegionConfig;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.execute.FunctionContextImpl;
import org.apache.geode.internal.cache.partitioned.ColocatedRegionDetails;
import org.apache.geode.internal.cache.persistence.DiskStoreID;
import org.apache.geode.internal.cache.persistence.PersistentMemberID;
import org.apache.geode.internal.cache.persistence.PersistentMemberManager;
import org.apache.geode.internal.cache.persistence.PersistentMemberPattern;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class ShowMissingDiskStoresFunctionJUnitTest {
    private GemFireCacheImpl cache;

    private InternalDistributedSystem system;

    private PartitionedRegion pr1;

    private PartitionedRegion pr2;

    private DistributedRegion prRoot;

    private PartitionAttributes pa;

    private PartitionRegionConfig prc;

    private Logger logger;

    private Appender mockAppender;

    private ArgumentCaptor<LogEvent> loggingEventCaptor;

    private FunctionContext context;

    private ShowMissingDiskStoresFunctionJUnitTest.TestResultSender resultSender;

    private PersistentMemberManager memberManager;

    private ShowMissingDiskStoresFunction smdsFunc;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testExecute() throws Exception {
        List<?> results = null;
        Mockito.when(cache.getPersistentMemberManager()).thenReturn(memberManager);
        smdsFunc.execute(context);
        results = resultSender.getResults();
        Assert.assertNotNull(results);
    }

    @Test
    public void testExecuteWithNullContextThrowsRuntimeException() {
        expectedException.expect(RuntimeException.class);
        smdsFunc.execute(null);
    }

    /**
     * Test method for
     * {@link org.apache.geode.management.internal.cli.functions.ShowMissingDiskStoresFunction#execute(org.apache.geode.cache.execute.FunctionContext)}.
     */
    @Test
    public void testExecuteWithNullCacheInstanceThrowsCacheClosedException() throws Throwable {
        expectedException.expect(CacheClosedException.class);
        context = new FunctionContextImpl(null, "testFunction", null, resultSender);
        List<?> results = null;
        smdsFunc.execute(context);
        results = resultSender.getResults();
    }

    @Test
    public void testExecuteWithNullGFCIResultValueIsNull() throws Throwable {
        List<?> results = null;
        Mockito.when(cache.getPersistentMemberManager()).thenReturn(memberManager);
        smdsFunc.execute(context);
        results = resultSender.getResults();
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertNull(results.get(0));
    }

    @Test
    public void testExecuteWhenGFCIClosedResultValueIsNull() throws Throwable {
        List<?> results = null;
        Mockito.when(cache.getPersistentMemberManager()).thenReturn(memberManager);
        Mockito.when(isClosed()).thenReturn(true);
        smdsFunc.execute(context);
        results = resultSender.getResults();
        Assert.assertNotNull(results);
    }

    @Test
    public void testExecuteReturnsMissingDiskStores() throws Throwable {
        List<?> results = null;
        Mockito.when(cache.getPersistentMemberManager()).thenReturn(memberManager);
        // Fake missing disk-stores
        Set<PersistentMemberID> regions1 = new HashSet<PersistentMemberID>();
        regions1.add(new PersistentMemberID(new DiskStoreID(), InetAddress.getLocalHost(), "/diskStore1", 1L, ((short) (1))));
        regions1.add(new PersistentMemberID(new DiskStoreID(), InetAddress.getLocalHost(), "/diskStore2", 2L, ((short) (2))));
        Map<String, Set<PersistentMemberID>> mapMember1 = new HashMap<String, Set<PersistentMemberID>>();
        mapMember1.put("member1", regions1);
        Mockito.when(memberManager.getWaitingRegions()).thenReturn(mapMember1);
        smdsFunc.execute(context);
        results = resultSender.getResults();
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Set<?> detailSet = ((Set<?>) (results.get(0)));
        Assert.assertEquals(2, detailSet.toArray().length);
        Assert.assertTrue(((detailSet.toArray()[0]) instanceof PersistentMemberPattern));
        Assert.assertTrue(((detailSet.toArray()[1]) instanceof PersistentMemberPattern));
        // Results are not sorted so verify results in either order
        if (getDirectory().equals("/diskStore1")) {
            Assert.assertEquals("/diskStore2", getDirectory());
        } else
            if (getDirectory().equals("/diskStore2")) {
                Assert.assertEquals("/diskStore1", getDirectory());
            }

    }

    @Test
    public void testExecuteReturnsMissingColocatedRegions() throws Throwable {
        List<?> results = null;
        Mockito.when(cache.getPersistentMemberManager()).thenReturn(memberManager);
        // Fake missing colocated regions
        Set<PartitionedRegion> prs = new HashSet<PartitionedRegion>();
        prs.add(pr1);
        prs.add(pr2);
        List<String> missing1 = new ArrayList<String>(Arrays.asList("child1", "child2"));
        Mockito.when(cache.getPartitionedRegions()).thenReturn(prs);
        Mockito.when(pr1.getMissingColocatedChildren()).thenReturn(missing1);
        Mockito.when(pr1.getFullPath()).thenReturn("/pr1");
        smdsFunc.execute(context);
        results = resultSender.getResults();
        Assert.assertEquals(1, results.size());
        Set<?> detailSet = ((Set<?>) (results.get(0)));
        Assert.assertEquals(2, detailSet.toArray().length);
        Assert.assertTrue(((detailSet.toArray()[0]) instanceof ColocatedRegionDetails));
        Assert.assertTrue(((detailSet.toArray()[1]) instanceof ColocatedRegionDetails));
        Assert.assertEquals("/pr1", getParent());
        Assert.assertEquals("/pr1", getParent());
        // Results are not sorted so verify results in either order
        if (getChild().equals("child1")) {
            Assert.assertEquals("child2", getChild());
        } else
            if (getChild().equals("child2")) {
                Assert.assertEquals("child1", getChild());
            }

    }

    @Test
    public void testExecuteReturnsMissingStoresAndRegions() throws Throwable {
        List<?> results = null;
        Mockito.when(cache.getPersistentMemberManager()).thenReturn(memberManager);
        // Fake missing disk-stores
        Set<PersistentMemberID> regions1 = new HashSet<PersistentMemberID>();
        regions1.add(new PersistentMemberID(new DiskStoreID(), InetAddress.getLocalHost(), "/diskStore1", 1L, ((short) (1))));
        regions1.add(new PersistentMemberID(new DiskStoreID(), InetAddress.getLocalHost(), "/diskStore2", 2L, ((short) (2))));
        Map<String, Set<PersistentMemberID>> mapMember1 = new HashMap<String, Set<PersistentMemberID>>();
        mapMember1.put("member1", regions1);
        Mockito.when(memberManager.getWaitingRegions()).thenReturn(mapMember1);
        // Fake missing colocated regions
        Set<PartitionedRegion> prs = new HashSet<PartitionedRegion>();
        prs.add(pr1);
        prs.add(pr2);
        List<String> missing1 = new ArrayList<String>(Arrays.asList("child1", "child2"));
        Mockito.when(cache.getPartitionedRegions()).thenReturn(prs);
        Mockito.when(pr1.getMissingColocatedChildren()).thenReturn(missing1);
        Mockito.when(pr1.getFullPath()).thenReturn("/pr1");
        smdsFunc.execute(context);
        results = resultSender.getResults();
        Assert.assertEquals(2, results.size());
        for (Object result : results) {
            Set<?> detailSet = ((Set<?>) (result));
            if ((detailSet.toArray()[0]) instanceof PersistentMemberPattern) {
                Assert.assertEquals(2, detailSet.toArray().length);
                Assert.assertTrue(((detailSet.toArray()[1]) instanceof PersistentMemberPattern));
                // Results are not sorted so verify results in either order
                if (getDirectory().equals("/diskStore1")) {
                    Assert.assertEquals("/diskStore2", getDirectory());
                } else
                    if (getDirectory().equals("/diskStore2")) {
                        Assert.assertEquals("/diskStore1", getDirectory());
                    }

            } else
                if ((detailSet.toArray()[0]) instanceof ColocatedRegionDetails) {
                    Assert.assertEquals(2, detailSet.toArray().length);
                    Assert.assertTrue(((detailSet.toArray()[1]) instanceof ColocatedRegionDetails));
                    Assert.assertEquals("/pr1", getParent());
                    Assert.assertEquals("/pr1", getParent());
                    // Results are not sorted so verify results in either order
                    if (getChild().equals("child1")) {
                        Assert.assertEquals("child2", getChild());
                    } else
                        if (getChild().equals("child2")) {
                            Assert.assertEquals("child1", getChild());
                        } else {
                            Assert.fail("Incorrect missing colocated region results");
                        }

                }

        }
    }

    @Test
    public void testExecuteCatchesExceptions() throws Exception {
        expectedException.expect(RuntimeException.class);
        Mockito.when(cache.getPersistentMemberManager()).thenThrow(new RuntimeException());
        smdsFunc.execute(context);
        List<?> results = resultSender.getResults();
    }

    @Test
    public void testGetId() {
        Assert.assertEquals(ShowMissingDiskStoresFunction.class.getName(), smdsFunc.getId());
    }

    private static class TestResultSender implements ResultSender {
        private final List<Object> results = new LinkedList<Object>();

        private Exception t;

        protected List<Object> getResults() throws Exception {
            if ((t) != null) {
                throw t;
            }
            return Collections.unmodifiableList(results);
        }

        @Override
        public void lastResult(final Object lastResult) {
            results.add(lastResult);
        }

        @Override
        public void sendResult(final Object oneResult) {
            results.add(oneResult);
        }

        @Override
        public void sendException(final Throwable t) {
            this.t = ((Exception) (t));
        }
    }
}

