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


import HostRoleStatus.COMPLETED;
import HostRoleStatus.IN_PROGRESS;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ambari.server.controller.internal.CalculatedStatus;
import org.apache.ambari.server.orm.entities.RequestEntity;
import org.apache.ambari.server.orm.entities.StageEntity;
import org.apache.ambari.server.orm.entities.StageEntityPK;
import org.junit.Assert;
import org.junit.Test;


/**
 * RequestDAO unit tests
 */
public class RequestDAOTest {
    private Injector injector;

    private ClusterDAO clusterDAO;

    private StageDAO stageDAO;

    private HostRoleCommandDAO hostRoleCommandDAO;

    private HostDAO hostDAO;

    private RequestDAO requestDAO;

    @Test
    public void testFindAll() throws Exception {
        RequestDAO dao = injector.getInstance(RequestDAO.class);
        Set<Long> set = Collections.emptySet();
        List<RequestEntity> list = dao.findByPks(set);
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testFindAllRequestIds() throws Exception {
        RequestDAO dao = injector.getInstance(RequestDAO.class);
        List<Long> list = dao.findAllRequestIds(0, true);
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testCalculatedStatus() throws Exception {
        createGraph();
        RequestEntity requestEntity = requestDAO.findByPK(100L);
        // !!! accepted value
        CalculatedStatus calc1 = CalculatedStatus.statusFromStageEntities(requestEntity.getStages());
        // !!! aggregated value
        Map<Long, HostRoleCommandStatusSummaryDTO> map = hostRoleCommandDAO.findAggregateCounts(100L);
        CalculatedStatus calc2 = CalculatedStatus.statusFromStageSummary(map, map.keySet());
        Assert.assertEquals(IN_PROGRESS, calc1.getStatus());
        Assert.assertEquals(calc1.getStatus(), calc2.getStatus());
        Assert.assertEquals(calc1.getPercent(), calc2.getPercent(), 0.01);
        // !!! simulate an upgrade group
        Set<Long> group = new HashSet<>();
        group.add(2L);
        group.add(3L);
        group.add(4L);
        // !!! accepted
        List<StageEntity> stages = new ArrayList<>();
        StageEntityPK primaryKey = new StageEntityPK();
        primaryKey.setRequestId(requestEntity.getRequestId());
        primaryKey.setStageId(2L);
        StageEntity stage = stageDAO.findByPK(primaryKey);
        Assert.assertNotNull(stage);
        stages.add(stage);
        primaryKey.setStageId(3L);
        stage = stageDAO.findByPK(primaryKey);
        Assert.assertNotNull(stage);
        stages.add(stage);
        primaryKey.setStageId(4L);
        stage = stageDAO.findByPK(primaryKey);
        Assert.assertNotNull(stage);
        stages.add(stage);
        CalculatedStatus calc3 = CalculatedStatus.statusFromStageEntities(stages);
        // !!! aggregated
        CalculatedStatus calc4 = CalculatedStatus.statusFromStageSummary(map, group);
        Assert.assertEquals(100.0, calc3.getPercent(), 0.01);
        Assert.assertEquals(COMPLETED, calc3.getStatus());
        Assert.assertEquals(calc3.getPercent(), calc4.getPercent(), 0.01);
        Assert.assertEquals(calc3.getStatus(), calc4.getStatus());
    }
}

