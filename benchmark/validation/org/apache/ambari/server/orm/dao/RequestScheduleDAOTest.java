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
package org.apache.ambari.server.orm.dao;


import BatchRequest.Type.POST;
import com.google.inject.Injector;
import java.util.List;
import junit.framework.Assert;
import org.apache.ambari.server.orm.entities.RequestScheduleBatchRequestEntity;
import org.apache.ambari.server.orm.entities.RequestScheduleEntity;
import org.junit.Test;


public class RequestScheduleDAOTest {
    private Injector injector;

    private HostDAO hostDAO;

    private ClusterDAO clusterDAO;

    private RequestScheduleDAO requestScheduleDAO;

    private RequestScheduleBatchRequestDAO batchRequestDAO;

    private ResourceTypeDAO resourceTypeDAO;

    private String testUri = "http://localhost/blah";

    private String testBody = "ValidJson";

    private String testType = POST.name();

    @Test
    public void testCreateRequestSchedule() throws Exception {
        RequestScheduleEntity scheduleEntity = createScheduleEntity();
        Assert.assertTrue(((scheduleEntity.getScheduleId()) > 0));
        Assert.assertEquals("SCHEDULED", scheduleEntity.getStatus());
        Assert.assertEquals("12", scheduleEntity.getHours());
        RequestScheduleBatchRequestEntity batchRequestEntity = scheduleEntity.getRequestScheduleBatchRequestEntities().iterator().next();
        Assert.assertNotNull(batchRequestEntity);
        Assert.assertEquals(testUri, batchRequestEntity.getRequestUri());
        Assert.assertEquals(testType, batchRequestEntity.getRequestType());
        Assert.assertEquals(testBody, batchRequestEntity.getRequestBodyAsString());
    }

    @Test
    public void testFindById() throws Exception {
        RequestScheduleEntity scheduleEntity = createScheduleEntity();
        RequestScheduleEntity testScheduleEntity = requestScheduleDAO.findById(scheduleEntity.getScheduleId());
        Assert.assertEquals(scheduleEntity, testScheduleEntity);
    }

    @Test
    public void testFindByStatus() throws Exception {
        RequestScheduleEntity scheduleEntity = createScheduleEntity();
        List<RequestScheduleEntity> scheduleEntities = requestScheduleDAO.findByStatus("SCHEDULED");
        Assert.assertNotNull(scheduleEntities);
        Assert.assertEquals(scheduleEntity, scheduleEntities.get(0));
    }
}

