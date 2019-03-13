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


import com.google.inject.Provider;
import javax.persistence.EntityManager;
import org.apache.ambari.server.orm.entities.HostComponentStateEntity;
import org.apache.ambari.server.orm.entities.HostEntity;
import org.junit.Test;


/**
 * HostComponentStateDAO tests.
 */
public class HostComponentStateDAOTest {
    @Test
    public void testRemove() throws Exception {
        Provider<EntityManager> entityManagerProvider = createNiceMock(Provider.class);
        EntityManager entityManager = createNiceMock(EntityManager.class);
        HostDAO hostDAO = createNiceMock(HostDAO.class);
        HostEntity hostEntity = createNiceMock(HostEntity.class);
        HostComponentStateEntity hostComponentStateEntity = createNiceMock(HostComponentStateEntity.class);
        expect(entityManagerProvider.get()).andReturn(entityManager).anyTimes();
        entityManager.remove(hostComponentStateEntity);
        replay(entityManagerProvider, entityManager, hostDAO, hostEntity, hostComponentStateEntity);
        HostComponentStateDAO dao = new HostComponentStateDAO();
        dao.entityManagerProvider = entityManagerProvider;
        dao.hostDAO = hostDAO;
        dao.remove(hostComponentStateEntity);
        verify(entityManagerProvider, entityManager, hostDAO, hostEntity, hostComponentStateEntity);
    }
}

