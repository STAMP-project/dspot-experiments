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
package org.kaaproject.kaa.server.common.dao.service;


import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.common.dto.TenantDto;
import org.kaaproject.kaa.common.dto.VersionDto;
import org.kaaproject.kaa.common.dto.logs.LogSchemaDto;
import org.kaaproject.kaa.server.common.dao.AbstractTest;


@Ignore("This test should be extended and initialized with proper context in each NoSQL submodule")
public class LogSchemaServiceImplTest extends AbstractTest {
    private static final String CUSTOMER_ID = "customer id";

    private static final String APPLICATION_NAME = "application name";

    private ApplicationDto applicationDto;

    private TenantDto customer;

    @Test
    public void removeLogSchemaByIdTest() {
        List<LogSchemaDto> schemas = logSchemaService.findLogSchemasByAppId(applicationDto.getId());
        Assert.assertEquals(1, schemas.size());
        logSchemaService.removeLogSchemaById(schemas.get(0).getId());
        schemas = logSchemaService.findLogSchemasByAppId(applicationDto.getId());
        Assert.assertEquals(0, schemas.size());
    }

    @Test
    public void removeLogSchemasByAppIdTest() {
        List<LogSchemaDto> schemas = logSchemaService.findLogSchemasByAppId(applicationDto.getId());
        Assert.assertEquals(1, schemas.size());
        logSchemaService.removeLogSchemasByAppId(applicationDto.getId());
        schemas = logSchemaService.findLogSchemasByAppId(applicationDto.getId());
        Assert.assertEquals(0, schemas.size());
    }

    @Test
    public void findLogSchemaByIdTest() {
        List<LogSchemaDto> schemas = logSchemaService.findLogSchemasByAppId(applicationDto.getId());
        Assert.assertEquals(1, schemas.size());
        LogSchemaDto dto = null;
        dto = logSchemaService.findLogSchemaById(schemas.get(0).getId());
        Assert.assertNotNull(dto);
    }

    @Test
    public void findLogSchemaVersionsByApplicationIdTest() {
        List<VersionDto> schemas = logSchemaService.findLogSchemaVersionsByApplicationId(applicationDto.getId());
        Assert.assertEquals(1, schemas.size());
    }
}

