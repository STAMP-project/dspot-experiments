/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.common.nosql.cassandra.dao;


import DirtiesContext.ClassMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaaproject.kaa.common.dto.EndpointSpecificConfigurationDto;
import org.kaaproject.kaa.server.common.dao.exception.KaaOptimisticLockingFailureException;
import org.kaaproject.kaa.server.common.dao.model.EndpointSpecificConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/cassandra-client-test-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class EndpointSpecificConfigurationCassandraDaoTest extends AbstractCassandraTest {
    private static final byte[] KEY = "key".getBytes();

    private static final byte[] KEY_2 = "key2".getBytes();

    private static final String BODY = "body";

    private EndpointSpecificConfigurationDto saved1;

    private EndpointSpecificConfigurationDto saved2;

    private EndpointSpecificConfigurationDto saved3;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    public void testRemoveByEndpointKeyHashAndConfigurationVersion() throws Exception {
        Assert.assertTrue(((endpointSpecificConfigurationDao.find().size()) == 3));
        endpointSpecificConfigurationDao.removeByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationCassandraDaoTest.KEY, 0);
        Assert.assertTrue(((endpointSpecificConfigurationDao.find().size()) == 2));
        Assert.assertTrue(((endpointSpecificConfigurationDao.findByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationCassandraDaoTest.KEY, 0)) == null));
    }

    @Test
    public void testFindByEndpointKeyHashAndConfigurationVersion() throws Exception {
        Assert.assertTrue(((endpointSpecificConfigurationDao.find().size()) == 3));
        EndpointSpecificConfigurationDto found1 = endpointSpecificConfigurationDao.findByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationCassandraDaoTest.KEY, 0).toDto();
        EndpointSpecificConfigurationDto found2 = endpointSpecificConfigurationDao.findByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationCassandraDaoTest.KEY, 1).toDto();
        EndpointSpecificConfigurationDto found3 = endpointSpecificConfigurationDao.findByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationCassandraDaoTest.KEY_2, 0).toDto();
        EndpointSpecificConfiguration found4 = endpointSpecificConfigurationDao.findByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationCassandraDaoTest.KEY_2, 4);
        Assert.assertEquals(saved1, found1);
        Assert.assertEquals(saved2, found2);
        Assert.assertEquals(saved3, found3);
        Assert.assertNull(found4);
    }

    @Test(expected = KaaOptimisticLockingFailureException.class)
    public void testLocking() throws Throwable {
        List<Future<?>> tasks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            tasks.add(executorService.submit(((Runnable) (() -> {
                endpointSpecificConfigurationDao.save(saved1);
            }))));
        }
        for (Future future : tasks) {
            try {
                future.get();
            } catch (ExecutionException ex) {
                throw ex.getCause();
            }
        }
    }
}

