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
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaaproject.kaa.common.dto.TenantDto;
import org.kaaproject.kaa.common.dto.ctl.CTLSchemaDto;
import org.kaaproject.kaa.server.common.dao.AbstractTest;
import org.kaaproject.kaa.server.common.dao.CtlService;
import org.kaaproject.kaa.server.common.dao.impl.DaoUtil;
import org.kaaproject.kaa.server.common.dao.model.sql.CtlSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/common-dao-test-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Transactional
public class HibernateCtlSchemaDaoTest extends HibernateAbstractTest {
    private static final Logger LOG = LoggerFactory.getLogger(HibernateCtlSchemaDaoTest.class);

    private static final String SYSTEM_FQN = "org.kaaproject.kaa.ctl.SystemSchema";

    @Autowired
    private CtlService ctlService;

    private TenantDto tenant;

    private CTLSchemaDto firstSchema;

    private CTLSchemaDto secondSchema;

    private CTLSchemaDto thirdSchema;

    private CTLSchemaDto fourthSchema;

    private CTLSchemaDto mainSchema;

    private CTLSchemaDto systemSchema;

    @Test(expected = Exception.class)
    public void saveCTLSchemaWithSameFqnAndVersion() {
        ctlSchemaDao.save(generateCTLSchema(AbstractTest.DEFAULT_FQN, new org.kaaproject.kaa.server.common.dao.model.sql.Tenant(tenant), 11, null));
        ctlSchemaDao.save(generateCTLSchema(AbstractTest.DEFAULT_FQN, null, 11, null));
    }

    @Test
    public void saveCTLSchemaWithDependency() throws InterruptedException {
        List<CTLSchemaDto> dep = DaoUtil.convertDtoList(ctlSchemaDao.findDependentSchemas(mainSchema.getId()));
        Assert.assertTrue(dep.isEmpty());
        List<CTLSchemaDto> expected = Arrays.asList(mainSchema);
        dep = DaoUtil.convertDtoList(ctlSchemaDao.findDependentSchemas(firstSchema.getId()));
        Assert.assertEquals(expected.size(), dep.size());
        dep = DaoUtil.convertDtoList(ctlSchemaDao.findDependentSchemas(secondSchema.getId()));
        Assert.assertEquals(expected.size(), dep.size());
        dep = DaoUtil.convertDtoList(ctlSchemaDao.findDependentSchemas(thirdSchema.getId()));
        Assert.assertEquals(expected.size(), dep.size());
        dep = DaoUtil.convertDtoList(ctlSchemaDao.findDependentSchemas(fourthSchema.getId()));
        Assert.assertEquals(expected.size(), dep.size());
    }

    @Test
    public void testFindByFqnAndVerAndTenantIdAndApplicationId() {
        CtlSchema found = ctlSchemaDao.findByFqnAndVerAndTenantIdAndApplicationId(firstSchema.getMetaInfo().getFqn(), firstSchema.getVersion(), firstSchema.getMetaInfo().getTenantId(), firstSchema.getMetaInfo().getApplicationId());
        Assert.assertEquals(firstSchema, found.toDto());
    }

    @Test
    public void testFindSystemByFqnAndVerAndTenantIdAndApplicationId() {
        CtlSchema found = ctlSchemaDao.findByFqnAndVerAndTenantIdAndApplicationId(systemSchema.getMetaInfo().getFqn(), systemSchema.getVersion(), null, null);
        Assert.assertEquals(systemSchema, found.toDto());
    }

    @Test
    public void testFindSystemSchemas() {
        List<CtlSchema> found = ctlSchemaDao.findSystemSchemas();
        Assert.assertEquals(getIdsDto(Arrays.asList(systemSchema)), getIds(found));
    }

    @Test
    public void testFindLatestByFqn() {
        CtlSchema latest = ctlSchemaDao.findLatestByFqnAndTenantIdAndApplicationId(HibernateCtlSchemaDaoTest.SYSTEM_FQN, null, null);
        Assert.assertEquals(systemSchema, latest.toDto());
    }

    @Test
    public void testFindAvailableSchemasForTenant() {
        List<CtlSchema> found = ctlSchemaDao.findAvailableSchemasForTenant(tenant.getId());
        Assert.assertEquals(getIdsDto(Arrays.asList(firstSchema, secondSchema, thirdSchema, fourthSchema, mainSchema, systemSchema)), getIds(found));
    }
}

