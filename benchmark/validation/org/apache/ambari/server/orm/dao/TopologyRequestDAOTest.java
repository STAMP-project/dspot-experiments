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


import com.google.inject.Injector;
import java.util.List;
import junit.framework.Assert;
import org.apache.ambari.server.orm.OrmTestHelper;
import org.apache.ambari.server.orm.entities.TopologyRequestEntity;
import org.junit.Test;


public class TopologyRequestDAOTest {
    private Injector injector;

    private TopologyRequestDAO requestDAO;

    OrmTestHelper helper;

    Long clusterId;

    @Test
    public void testAndFindAll() throws Exception {
        create();
        testRequestEntity(requestDAO.findAll());
    }

    @Test
    public void testFindByClusterId() throws Exception {
        create();
        testRequestEntity(requestDAO.findByClusterId(clusterId));
    }

    @Test
    public void testRemoveAll() throws Exception {
        // Given
        create();
        // When
        requestDAO.removeAll(clusterId);
        // Then
        List<TopologyRequestEntity> requestEntities = requestDAO.findByClusterId(clusterId);
        Assert.assertEquals("All topology request entities associated with cluster should be removed !", 0, requestEntities.size());
    }
}

