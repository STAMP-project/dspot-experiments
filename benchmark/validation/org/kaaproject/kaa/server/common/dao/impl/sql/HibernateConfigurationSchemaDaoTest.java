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
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaaproject.kaa.server.common.dao.AbstractTest;
import org.kaaproject.kaa.server.common.dao.model.sql.Application;
import org.kaaproject.kaa.server.common.dao.model.sql.ConfigurationSchema;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/common-dao-test-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Transactional
public class HibernateConfigurationSchemaDaoTest extends HibernateAbstractTest {
    @Test
    public void findByApplicationIdTest() {
        List<ConfigurationSchema> schemas = generateConfSchema(null, 3);
        Assert.assertEquals(3, schemas.size());
        ConfigurationSchema schema = schemas.get(0);
        Application app = schema.getApplication();
        List<ConfigurationSchema> found = configurationSchemaDao.findByApplicationId(app.getId().toString());
        Assert.assertEquals(3, found.size());
        Assert.assertEquals(schemas, found);
    }

    @Test
    public void findLatestByApplicationIdTest() {
        List<ConfigurationSchema> schemas = generateConfSchema(null, 4);
        Assert.assertEquals(4, schemas.size());
        ConfigurationSchema schema = schemas.get(0);
        Application app = schema.getApplication();
        ConfigurationSchema found = configurationSchemaDao.findLatestByApplicationId(app.getId().toString());
        Assert.assertNotNull(found);
        Assert.assertEquals(4, found.getVersion());
    }

    @Test
    public void findByAppIdAndVersionTest() {
        List<ConfigurationSchema> schemas = generateConfSchema(null, 4);
        Assert.assertEquals(4, schemas.size());
        ConfigurationSchema schema = schemas.get(2);
        Application app = schema.getApplication();
        ConfigurationSchema found = configurationSchemaDao.findByAppIdAndVersion(app.getId().toString(), schema.getVersion());
        Assert.assertNotNull(found);
        Assert.assertEquals(3, found.getVersion());
    }

    @Test
    public void testFindVacantSchemasTest() {
        Application app1 = generateApplication(null);
        List<ConfigurationSchema> schemas1 = generateConfSchema(app1, 3);
        List<String> ids1 = new ArrayList<>(2);
        ids1.add(schemas1.get(0).getId().toString());
        List<ConfigurationSchema> found2 = configurationSchemaDao.findVacantSchemas(app1.getId().toString(), ids1);
        Assert.assertEquals(2, found2.size());
        List<ConfigurationSchema> found3 = configurationSchemaDao.findVacantSchemas(app1.getId().toString(), null);
        Assert.assertEquals(3, found3.size());
    }
}

