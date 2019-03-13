/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.controller.internal;


import HostRoleStatus.ABORTED;
import HostRoleStatus.COMPLETED;
import HostRoleStatus.HOLDING;
import HostRoleStatus.SKIPPED_FAILED;
import StageResourceProvider.STAGE_DISPLAY_STATUS;
import StageResourceProvider.STAGE_END_TIME;
import StageResourceProvider.STAGE_PROGRESS_PERCENT;
import StageResourceProvider.STAGE_REQUEST_ID;
import StageResourceProvider.STAGE_STAGE_ID;
import StageResourceProvider.STAGE_START_TIME;
import StageResourceProvider.STAGE_STATUS;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.actionmanager.ActionManager;
import org.apache.ambari.server.controller.AmbariManagementController;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.metadata.ActionMetadata;
import org.apache.ambari.server.orm.dao.HostRoleCommandDAO;
import org.apache.ambari.server.orm.dao.HostRoleCommandStatusSummaryDTO;
import org.apache.ambari.server.orm.dao.StageDAO;
import org.apache.ambari.server.orm.entities.StageEntity;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.topology.TopologyManager;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class StageResourceProviderTest {
    private StageDAO dao = null;

    private Clusters clusters = null;

    private Cluster cluster = null;

    private AmbariManagementController managementController = null;

    private Injector injector;

    private HostRoleCommandDAO hrcDao = null;

    private TopologyManager topologyManager = null;

    @Test
    public void testCreateResources() throws Exception {
        StageResourceProvider provider = new StageResourceProvider(managementController);
        Request request = createNiceMock(Request.class);
        try {
            provider.createResources(request);
            Assert.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void testUpdateResources() throws Exception {
        StageResourceProvider provider = new StageResourceProvider(managementController);
        Request request = createNiceMock(Request.class);
        Predicate predicate = createNiceMock(Predicate.class);
        expect(clusters.getClusterById(anyLong())).andReturn(cluster).anyTimes();
        expect(request.getProperties()).andReturn(Collections.emptySet());
        replay(clusters, cluster, request, predicate);
        provider.updateResources(request, predicate);
        verify(clusters, cluster);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDeleteResources() throws Exception {
        StageResourceProvider provider = new StageResourceProvider(managementController);
        Predicate predicate = createNiceMock(Predicate.class);
        provider.deleteResources(new RequestImpl(null, null, null, null), predicate);
    }

    @Test
    public void testGetResources() throws Exception {
        StageResourceProvider provider = new StageResourceProvider(managementController);
        Request request = createNiceMock(Request.class);
        Predicate predicate = createNiceMock(Predicate.class);
        List<StageEntity> entities = getStageEntities(COMPLETED);
        expect(dao.findAll(request, predicate)).andReturn(entities);
        expect(clusters.getClusterById(anyLong())).andReturn(cluster).anyTimes();
        expect(cluster.getClusterName()).andReturn("c1").anyTimes();
        replay(dao, clusters, cluster, request, predicate);
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        Resource resource = resources.iterator().next();
        Assert.assertEquals(100.0, resource.getPropertyValue(STAGE_PROGRESS_PERCENT));
        Assert.assertEquals(COMPLETED, resource.getPropertyValue(STAGE_STATUS));
        Assert.assertEquals(COMPLETED, resource.getPropertyValue(STAGE_DISPLAY_STATUS));
        Assert.assertEquals(1000L, resource.getPropertyValue(STAGE_START_TIME));
        Assert.assertEquals(2500L, resource.getPropertyValue(STAGE_END_TIME));
        verify(dao, clusters, cluster);
    }

    @Test
    public void testGetResourcesWithRequest() throws Exception {
        StageResourceProvider provider = new StageResourceProvider(managementController);
        Request request = createNiceMock(Request.class);
        Predicate predicate = new PredicateBuilder().property(STAGE_REQUEST_ID).equals(1L).toPredicate();
        List<StageEntity> entities = getStageEntities(COMPLETED);
        expect(dao.findAll(request, predicate)).andReturn(entities);
        expect(clusters.getClusterById(anyLong())).andReturn(cluster).anyTimes();
        expect(cluster.getClusterName()).andReturn("c1").anyTimes();
        reset(topologyManager);
        expect(topologyManager.getRequest(EasyMock.anyLong())).andReturn(null).atLeastOnce();
        replay(topologyManager, dao, clusters, cluster, request);
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        Resource resource = resources.iterator().next();
        Assert.assertEquals(100.0, resource.getPropertyValue(STAGE_PROGRESS_PERCENT));
        Assert.assertEquals(COMPLETED, resource.getPropertyValue(STAGE_STATUS));
        Assert.assertEquals(COMPLETED, resource.getPropertyValue(STAGE_DISPLAY_STATUS));
        Assert.assertEquals(1000L, resource.getPropertyValue(STAGE_START_TIME));
        Assert.assertEquals(2500L, resource.getPropertyValue(STAGE_END_TIME));
        verify(topologyManager, dao, clusters, cluster);
    }

    /**
     * Tests getting the display status of a stage which can differ from the final
     * status.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetDisplayStatus() throws Exception {
        // clear the HRC call so that it has the correct summary fields to represent
        // 1 skipped and 1 completed task
        EasyMock.reset(hrcDao);
        expect(hrcDao.findAggregateCounts(EasyMock.anyObject(Long.class))).andReturn(new HashMap<Long, HostRoleCommandStatusSummaryDTO>() {
            {
                put(0L, new HostRoleCommandStatusSummaryDTO(0, 1000L, 2500L, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1));
            }
        }).anyTimes();
        replay(hrcDao);
        StageResourceProvider provider = new StageResourceProvider(managementController);
        Request request = createNiceMock(Request.class);
        Predicate predicate = createNiceMock(Predicate.class);
        // make the stage skippable so it resolves to COMPLETED even though it has a
        // skipped failure
        List<StageEntity> entities = getStageEntities(SKIPPED_FAILED);
        entities.get(0).setSkippable(true);
        expect(dao.findAll(request, predicate)).andReturn(entities);
        expect(clusters.getClusterById(anyLong())).andReturn(cluster).anyTimes();
        expect(cluster.getClusterName()).andReturn("c1").anyTimes();
        replay(dao, clusters, cluster, request, predicate);
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        Resource resource = resources.iterator().next();
        // verify the two statuses
        Assert.assertEquals(COMPLETED, resource.getPropertyValue(STAGE_STATUS));
        Assert.assertEquals(SKIPPED_FAILED, resource.getPropertyValue(STAGE_DISPLAY_STATUS));
        verify(dao, clusters, cluster);
    }

    @Test
    public void testUpdateStageStatus_aborted() throws Exception {
        StageResourceProvider provider = new StageResourceProvider(managementController);
        ActionManager actionManager = createNiceMock(ActionManager.class);
        Predicate predicate = new PredicateBuilder().property(STAGE_STAGE_ID).equals(2L).and().property(STAGE_REQUEST_ID).equals(1L).toPredicate();
        Map<String, Object> requestProps = new HashMap<>();
        requestProps.put(STAGE_STATUS, ABORTED.name());
        Request request = PropertyHelper.getUpdateRequest(requestProps, null);
        List<StageEntity> entities = getStageEntities(HOLDING);
        expect(dao.findAll(request, predicate)).andReturn(entities);
        expect(managementController.getActionManager()).andReturn(actionManager).anyTimes();
        dao.updateStageStatus(entities.get(0), ABORTED, actionManager);
        EasyMock.expectLastCall().atLeastOnce();
        replay(dao, clusters, cluster, actionManager, managementController);
        provider.updateResources(request, predicate);
        verify(dao, clusters, cluster, actionManager, managementController);
    }

    private class MockModule implements Module {
        @Override
        public void configure(Binder binder) {
            binder.bind(StageDAO.class).toInstance(dao);
            binder.bind(Clusters.class).toInstance(clusters);
            binder.bind(Cluster.class).toInstance(cluster);
            binder.bind(HostRoleCommandDAO.class).toInstance(hrcDao);
            binder.bind(AmbariManagementController.class).toInstance(managementController);
            binder.bind(ActionMetadata.class);
            binder.bind(TopologyManager.class).toInstance(topologyManager);
        }
    }
}

