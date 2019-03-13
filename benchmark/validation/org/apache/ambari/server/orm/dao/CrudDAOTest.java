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
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.state.StackId;
import org.junit.Assert;
import org.junit.Test;


/**
 * CrudDAO unit tests.
 * Uses repo_version table for in-memory DB tests.
 */
public class CrudDAOTest {
    private static Injector injector;

    private CrudDAO<RepositoryVersionEntity, Long> repositoryVersionDAO;

    private int uniqueCounter = 0;

    private static final long FIRST_ID = 1L;

    private static final StackId HDP_206 = new StackId("HDP", "2.0.6");

    private StackDAO stackDAO;

    @Test
    public void testFindByPK() {
        Assert.assertNull(repositoryVersionDAO.findByPK(CrudDAOTest.FIRST_ID));
        createSingleRecord();
        Assert.assertNotNull(repositoryVersionDAO.findByPK(CrudDAOTest.FIRST_ID));
    }

    @Test
    public void testFindAll() {
        Assert.assertEquals(0, repositoryVersionDAO.findAll().size());
        createSingleRecord();
        createSingleRecord();
        Assert.assertEquals(2, repositoryVersionDAO.findAll().size());
        repositoryVersionDAO.remove(repositoryVersionDAO.findByPK(CrudDAOTest.FIRST_ID));
        Assert.assertEquals(1, repositoryVersionDAO.findAll().size());
    }

    @Test
    public void testCreate() {
        createSingleRecord();
        Assert.assertTrue(((repositoryVersionDAO.findAll().size()) == 1));
        createSingleRecord();
        Assert.assertTrue(((repositoryVersionDAO.findAll().size()) == 2));
    }

    @Test
    public void testMerge() {
        createSingleRecord();
        RepositoryVersionEntity entity = repositoryVersionDAO.findByPK(CrudDAOTest.FIRST_ID);
        entity.setDisplayName("newname");
        repositoryVersionDAO.merge(entity);
        entity = repositoryVersionDAO.findByPK(CrudDAOTest.FIRST_ID);
        Assert.assertEquals("newname", entity.getDisplayName());
    }

    @Test
    public void testRemove() {
        createSingleRecord();
        createSingleRecord();
        Assert.assertEquals(2, repositoryVersionDAO.findAll().size());
        repositoryVersionDAO.remove(repositoryVersionDAO.findByPK(CrudDAOTest.FIRST_ID));
        Assert.assertEquals(1, repositoryVersionDAO.findAll().size());
        Assert.assertNull(repositoryVersionDAO.findByPK(1L));
    }
}

