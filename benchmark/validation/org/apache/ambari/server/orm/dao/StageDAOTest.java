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


import SortRequest.Order;
import StageResourceProvider.STAGE_CLUSTER_NAME;
import StageResourceProvider.STAGE_CONTEXT;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.apache.ambari.server.controller.internal.StageResourceProvider;
import org.apache.ambari.server.controller.spi.Predicate;
import org.apache.ambari.server.controller.spi.Request;
import org.apache.ambari.server.controller.spi.SortRequest;
import org.apache.ambari.server.controller.spi.SortRequestProperty;
import org.apache.ambari.server.controller.utilities.PredicateBuilder;
import org.apache.ambari.server.controller.utilities.PropertyHelper;
import org.apache.ambari.server.orm.entities.StageEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * StageDAO tests.
 */
public class StageDAOTest {
    private Injector injector;

    private StageDAO stageDao;

    /**
     * Tests that the Ambari {@link org.apache.ambari.server.controller.spi.Predicate}
     * can be converted and submitted to JPA correctly to return a restricted result set.
     */
    @Test
    public void testStagePredicate() throws Exception {
        Predicate predicate = new PredicateBuilder().property(STAGE_CLUSTER_NAME).equals("c1").toPredicate();
        List<StageEntity> entities = stageDao.findAll(PropertyHelper.getReadRequest(), predicate);
        Assert.assertEquals(5, entities.size());
        predicate = new PredicateBuilder().property(STAGE_CONTEXT).equals("request context for 3").or().property(STAGE_CONTEXT).equals("request context for 4").toPredicate();
        entities = stageDao.findAll(PropertyHelper.getReadRequest(), predicate);
        Assert.assertEquals(2, entities.size());
    }

    /**
     * Tests that JPA does the sorting work for us.
     */
    @Test
    public void testStageSorting() throws Exception {
        List<SortRequestProperty> sortProperties = new ArrayList<>();
        SortRequest sortRequest = new org.apache.ambari.server.controller.internal.SortRequestImpl(sortProperties);
        Predicate predicate = new PredicateBuilder().property(STAGE_CLUSTER_NAME).equals("c1").toPredicate();
        sortProperties.add(new SortRequestProperty(StageResourceProvider.STAGE_LOG_INFO, Order.ASC));
        Request request = PropertyHelper.getReadRequest(new HashSet(Arrays.asList()), null, null, null, sortRequest);
        // get back all 5
        List<StageEntity> entities = stageDao.findAll(request, predicate);
        Assert.assertEquals(5, entities.size());
        // assert sorting ASC
        String lastInfo = null;
        for (StageEntity entity : entities) {
            if (lastInfo == null) {
                lastInfo = entity.getLogInfo();
                continue;
            }
            String currentInfo = entity.getLogInfo();
            Assert.assertTrue(((lastInfo.compareTo(currentInfo)) <= 0));
            lastInfo = currentInfo;
        }
        // clear and do DESC
        sortProperties.clear();
        sortProperties.add(new SortRequestProperty(StageResourceProvider.STAGE_LOG_INFO, Order.DESC));
        // get back all 5
        entities = stageDao.findAll(request, predicate);
        Assert.assertEquals(5, entities.size());
        // assert sorting DESC
        lastInfo = null;
        for (StageEntity entity : entities) {
            if (null == lastInfo) {
                lastInfo = entity.getLogInfo();
                continue;
            }
            String currentInfo = entity.getLogInfo();
            Assert.assertTrue(((lastInfo.compareTo(currentInfo)) >= 0));
            lastInfo = currentInfo;
        }
    }
}

