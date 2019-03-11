/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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


import Resource.Type;
import WorkflowResourceProvider.WORKFLOW_CLUSTER_NAME_PROPERTY_ID;
import WorkflowResourceProvider.WORKFLOW_CONTEXT_PROPERTY_ID;
import WorkflowResourceProvider.WORKFLOW_ELAPSED_TIME_PROPERTY_ID;
import WorkflowResourceProvider.WORKFLOW_ID_PROPERTY_ID;
import WorkflowResourceProvider.WORKFLOW_INPUT_BYTES_PROPERTY_ID;
import WorkflowResourceProvider.WORKFLOW_LAST_UPDATE_TIME_PROPERTY_ID;
import WorkflowResourceProvider.WORKFLOW_NAME_PROPERTY_ID;
import WorkflowResourceProvider.WORKFLOW_NUM_JOBS_COMPLETED_PROPERTY_ID;
import WorkflowResourceProvider.WORKFLOW_NUM_JOBS_TOTAL_PROPERTY_ID;
import WorkflowResourceProvider.WORKFLOW_OUTPUT_BYTES_PROPERTY_ID;
import WorkflowResourceProvider.WORKFLOW_PARENT_ID_PROPERTY_ID;
import WorkflowResourceProvider.WORKFLOW_START_TIME_PROPERTY_ID;
import WorkflowResourceProvider.WORKFLOW_USER_NAME_PROPERTY_ID;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.internal.WorkflowResourceProvider.WorkflowFetcher;
import org.apache.ambari.server.controller.jdbc.ConnectionFactory;
import org.apache.ambari.server.controller.spi.NoSuchParentResourceException;
import org.apache.ambari.server.controller.spi.NoSuchResourceException;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.Resource;
import org.apache.ambari.server.controller.spi.ResourceProvider;
import org.apache.ambari.server.controller.spi.SystemException;
import org.apache.ambari.server.controller.spi.UnsupportedPropertyException;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.junit.Assert;
import org.junit.Test;


/**
 * WorkflowResourceProvider tests.
 */
public class WorkflowResourceProviderTest {
    @Test
    public void testGetResources() throws NoSuchParentResourceException, NoSuchResourceException, SystemException, UnsupportedPropertyException {
        Set<Resource> expected = new HashSet<>();
        expected.add(WorkflowResourceProviderTest.createWorkflowResponse("Cluster100", "workflow1"));
        expected.add(WorkflowResourceProviderTest.createWorkflowResponse("Cluster100", "workflow2"));
        expected.add(WorkflowResourceProviderTest.createWorkflowResponse("Cluster100", "workflow3"));
        Resource.Type type = Type.Workflow;
        Set<String> propertyIds = new HashSet<>();
        propertyIds.add(WORKFLOW_CLUSTER_NAME_PROPERTY_ID);
        propertyIds.add(WORKFLOW_ID_PROPERTY_ID);
        propertyIds.add(WORKFLOW_NAME_PROPERTY_ID);
        propertyIds.add(WORKFLOW_USER_NAME_PROPERTY_ID);
        propertyIds.add(WORKFLOW_START_TIME_PROPERTY_ID);
        propertyIds.add(WORKFLOW_LAST_UPDATE_TIME_PROPERTY_ID);
        propertyIds.add(WORKFLOW_ELAPSED_TIME_PROPERTY_ID);
        propertyIds.add(WORKFLOW_INPUT_BYTES_PROPERTY_ID);
        propertyIds.add(WORKFLOW_OUTPUT_BYTES_PROPERTY_ID);
        propertyIds.add(WORKFLOW_NUM_JOBS_TOTAL_PROPERTY_ID);
        propertyIds.add(WORKFLOW_NUM_JOBS_COMPLETED_PROPERTY_ID);
        propertyIds.add(WORKFLOW_PARENT_ID_PROPERTY_ID);
        propertyIds.add(WORKFLOW_CONTEXT_PROPERTY_ID);
        WorkflowFetcher workflowFetcher = createMock(WorkflowFetcher.class);
        expect(workflowFetcher.fetchWorkflows(propertyIds, "Cluster100", null)).andReturn(expected).once();
        replay(workflowFetcher);
        Map<Resource.Type, String> keyPropertyIds = PropertyHelper.getKeyPropertyIds(type);
        ResourceProvider provider = new WorkflowResourceProvider(workflowFetcher);
        Request request = PropertyHelper.getReadRequest(propertyIds);
        Predicate predicate = new PredicateBuilder().property(WORKFLOW_CLUSTER_NAME_PROPERTY_ID).equals("Cluster100").toPredicate();
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(3, resources.size());
        Set<String> names = new HashSet<>();
        for (Resource resource : resources) {
            String clusterName = ((String) (resource.getPropertyValue(WORKFLOW_CLUSTER_NAME_PROPERTY_ID)));
            Assert.assertEquals("Cluster100", clusterName);
            names.add(((String) (resource.getPropertyValue(WORKFLOW_ID_PROPERTY_ID))));
        }
        // Make sure that all of the response objects got moved into resources
        for (Resource resource : expected) {
            Assert.assertTrue(names.contains(resource.getPropertyValue(WORKFLOW_ID_PROPERTY_ID)));
        }
        verify(workflowFetcher);
    }

    @Test
    public void testWorkflowFetcher() throws NoSuchParentResourceException, NoSuchResourceException, SystemException, UnsupportedPropertyException {
        Set<String> requestedIds = new HashSet<>();
        requestedIds.add(WORKFLOW_ID_PROPERTY_ID);
        ResourceProvider provider = new WorkflowResourceProviderTest.TestWorkflowResourceProvider();
        Request request = PropertyHelper.getReadRequest(requestedIds);
        Predicate predicate = new PredicateBuilder().property(WORKFLOW_ID_PROPERTY_ID).equals("workflow1").toPredicate();
        Set<Resource> resources = provider.getResources(request, predicate);
        Assert.assertEquals(1, resources.size());
        for (Resource resource : resources) {
            String workflowId = ((String) (resource.getPropertyValue(WORKFLOW_ID_PROPERTY_ID)));
            Assert.assertEquals("workflow1", workflowId);
        }
    }

    private static class TestWorkflowResourceProvider extends WorkflowResourceProvider {
        protected TestWorkflowResourceProvider() {
            super(null);
            this.workflowFetcher = new WorkflowResourceProviderTest.TestWorkflowResourceProvider.TestWorkflowFetcher();
        }

        private class TestWorkflowFetcher extends PostgresWorkflowFetcher {
            ResultSet rs = null;

            public TestWorkflowFetcher() {
                super(((ConnectionFactory) (null)));
            }

            @Override
            protected ResultSet getResultSet(Set<String> requestedIds, String workflowId) throws SQLException {
                rs = createMock(ResultSet.class);
                expect(rs.next()).andReturn(true).once();
                expect(rs.getString(getDBField(WORKFLOW_ID_PROPERTY_ID).toString())).andReturn("workflow1").once();
                expect(rs.next()).andReturn(false).once();
                rs.close();
                expectLastCall().once();
                replay(rs);
                return rs;
            }

            @Override
            protected void close() {
                verify(rs);
            }
        }
    }
}

