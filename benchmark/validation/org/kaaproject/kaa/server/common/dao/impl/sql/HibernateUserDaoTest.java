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
package org.kaaproject.kaa.server.common.dao.impl.sql;


import DirtiesContext.ClassMode;
import KaaAuthorityDto.KAA_ADMIN;
import KaaAuthorityDto.TENANT_ADMIN;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaaproject.kaa.server.common.dao.AbstractTest;
import org.kaaproject.kaa.server.common.dao.model.sql.Tenant;
import org.kaaproject.kaa.server.common.dao.model.sql.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/common-dao-test-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Transactional
public class HibernateUserDaoTest extends HibernateAbstractTest {
    private static final Logger LOG = LoggerFactory.getLogger(HibernateUserDaoTest.class);

    @Test
    public void saveUserTest() {
        Tenant tenant = generateTenant();
        User user = generateUser(tenant, null);
        user = userDao.save(user);
        List<User> users = userDao.findByTenantIdAndAuthority(tenant.getId().toString(), KAA_ADMIN.name());
        HibernateUserDaoTest.LOG.debug("---> List users {} ", Arrays.toString(users.toArray()));
        Assert.assertEquals(user, users.get(0));
    }

    @Test
    public void findUsersByTenantIdAndAuthoritiesTest() {
        Tenant tenant = generateTenant();
        generateUser(tenant, TENANT_ADMIN);
        generateUser(tenant, TENANT_ADMIN);
        generateUser(tenant, KAA_ADMIN);
        List<User> users = userDao.findByTenantIdAndAuthorities(tenant.getId().toString(), new String[]{ TENANT_ADMIN.name() });
        Assert.assertEquals(2, users.size());
    }

    @Test
    public void testRemoveByTenantId() {
        Tenant tenant = generateTenant();
        User user = generateUser(tenant, null);
        userDao.removeByTenantId(tenant.getId().toString());
        User found = userDao.findById(user.getId().toString());
        Assert.assertNull(found);
    }
}

