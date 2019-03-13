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


import com.google.inject.Inject;
import com.google.inject.Provider;
import javax.persistence.EntityManager;
import org.junit.Assert;
import org.junit.Test;


/**
 * UserDAO unit tests.
 */
public class UserDAOTest {
    private static String SERVICEOP_USER_NAME = "serviceopuser";

    private UserDAO userDAO;

    @Test
    public void testUserByName() {
        init(UserDAOTest.user());
        Assert.assertEquals(UserDAOTest.SERVICEOP_USER_NAME, userDAO.findUserByName(UserDAOTest.SERVICEOP_USER_NAME).getUserName());
    }

    static class EntityManagerProvider implements Provider<EntityManager> {
        private final EntityManager entityManager;

        @Inject
        public EntityManagerProvider(EntityManager entityManager) {
            this.entityManager = entityManager;
        }

        @Override
        public EntityManager get() {
            return entityManager;
        }
    }
}

