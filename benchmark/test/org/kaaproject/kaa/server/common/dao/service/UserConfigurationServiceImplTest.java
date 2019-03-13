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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.common.dto.ConfigurationSchemaDto;
import org.kaaproject.kaa.common.dto.EndpointUserConfigurationDto;
import org.kaaproject.kaa.common.dto.EndpointUserDto;
import org.kaaproject.kaa.server.common.dao.AbstractTest;
import org.kaaproject.kaa.server.common.dao.exception.IncorrectParameterException;


@Ignore("This test should be extended and initialized with proper context in each NoSQL submodule")
public class UserConfigurationServiceImplTest extends AbstractTest {
    private static final String OVERRIDE_USER_DATA_JSON = "dao/user/overrideData.json";

    @Test
    public void findUserConfigurationByUserIdAndAppTokenAndSchemaVersionTest() throws IOException {
        EndpointUserDto userDto = generateEndpointUserDto(null);
        ApplicationDto appDto = generateApplicationDto();
        ConfigurationSchemaDto schema = generateConfSchemaDto(null, appDto.getId(), 1).get(0);
        EndpointUserConfigurationDto firstUserConfigurationDto = generateEndpointUserConfigurationDto(userDto, appDto, schema, readSchemaFileAsString(UserConfigurationServiceImplTest.OVERRIDE_USER_DATA_JSON));
        generateEndpointUserConfigurationDto(userDto, appDto, null, readSchemaFileAsString(UserConfigurationServiceImplTest.OVERRIDE_USER_DATA_JSON));
        generateEndpointUserConfigurationDto(null, null, null, readSchemaFileAsString(UserConfigurationServiceImplTest.OVERRIDE_USER_DATA_JSON));
        EndpointUserConfigurationDto found = userConfigurationService.findUserConfigurationByUserIdAndAppTokenAndSchemaVersion(userDto.getId(), appDto.getApplicationToken(), schema.getVersion());
        Assert.assertEquals(firstUserConfigurationDto, found);
    }

    @Test
    public void findUserConfigurationByUserIdTest() throws IOException {
        EndpointUserDto userDto = generateEndpointUserDto(null);
        ApplicationDto appDto = generateApplicationDto();
        EndpointUserConfigurationDto firstUserConfigurationDto = generateEndpointUserConfigurationDto(userDto, appDto, null, readSchemaFileAsString(UserConfigurationServiceImplTest.OVERRIDE_USER_DATA_JSON));
        EndpointUserConfigurationDto secondUserConfigurationDto = generateEndpointUserConfigurationDto(userDto, appDto, null, readSchemaFileAsString(UserConfigurationServiceImplTest.OVERRIDE_USER_DATA_JSON));
        List<EndpointUserConfigurationDto> expectedList = new ArrayList<>();
        expectedList.add(firstUserConfigurationDto);
        expectedList.add(secondUserConfigurationDto);
        generateEndpointUserConfigurationDto(null, null, null, readSchemaFileAsString(UserConfigurationServiceImplTest.OVERRIDE_USER_DATA_JSON));
        List<EndpointUserConfigurationDto> foundList = userConfigurationService.findUserConfigurationByUserId(userDto.getId());
        Assert.assertEquals(expectedList.size(), foundList.size());
    }

    @Test
    public void removeByUserIdAndAppTokenAndSchemaVersionTest() throws IOException {
        EndpointUserDto userDto = generateEndpointUserDto(null);
        ApplicationDto appDto = generateApplicationDto();
        ConfigurationSchemaDto schema = generateConfSchemaDto(null, appDto.getId(), 1).get(0);
        generateEndpointUserConfigurationDto(userDto, appDto, schema, readSchemaFileAsString(UserConfigurationServiceImplTest.OVERRIDE_USER_DATA_JSON));
        generateEndpointUserConfigurationDto(userDto, appDto, null, readSchemaFileAsString(UserConfigurationServiceImplTest.OVERRIDE_USER_DATA_JSON));
        generateEndpointUserConfigurationDto(userDto, appDto, null, readSchemaFileAsString(UserConfigurationServiceImplTest.OVERRIDE_USER_DATA_JSON));
        userConfigurationService.removeByUserIdAndAppTokenAndSchemaVersion(userDto.getId(), appDto.getApplicationToken(), schema.getVersion());
        EndpointUserConfigurationDto removed = userConfigurationService.findUserConfigurationByUserIdAndAppTokenAndSchemaVersion(userDto.getId(), appDto.getApplicationToken(), schema.getVersion());
        Assert.assertNull(removed);
        List<EndpointUserConfigurationDto> foundList = userConfigurationService.findUserConfigurationByUserId(userDto.getId());
        Assert.assertEquals(2, foundList.size());
    }

    @Test
    public void findUserConfigurationByExternalUIdAndAppTokenAndSchemaVersionTest() throws IOException {
        ApplicationDto appDto = generateApplicationDto();
        EndpointUserDto userDto = generateEndpointUserDto(appDto.getTenantId());
        EndpointUserConfigurationDto userConfigurationDto = generateEndpointUserConfigurationDto(userDto, appDto, null, readSchemaFileAsString(UserConfigurationServiceImplTest.OVERRIDE_USER_DATA_JSON));
        EndpointUserConfigurationDto endpointUserConfigurationDto = userConfigurationService.findUserConfigurationByExternalUIdAndAppTokenAndSchemaVersion(userDto.getExternalId(), appDto.getApplicationToken(), userConfigurationDto.getSchemaVersion(), appDto.getTenantId());
        Assert.assertNotNull(endpointUserConfigurationDto);
    }

    @Test
    public void findUserConfigurationByExternalUIdAndAppTokenAndSchemaVersionNullTest() throws IOException {
        ApplicationDto appDto = generateApplicationDto();
        EndpointUserDto userDto = generateEndpointUserDto(appDto.getTenantId());
        EndpointUserConfigurationDto userConfigurationDto = generateEndpointUserConfigurationDto(userDto, appDto, null, readSchemaFileAsString(UserConfigurationServiceImplTest.OVERRIDE_USER_DATA_JSON));
        userConfigurationService.removeByUserIdAndAppTokenAndSchemaVersion(userDto.getId(), appDto.getApplicationToken(), userConfigurationDto.getSchemaVersion());
        EndpointUserConfigurationDto endpointUserConfigurationDto = userConfigurationService.findUserConfigurationByExternalUIdAndAppTokenAndSchemaVersion(userDto.getExternalId(), appDto.getApplicationToken(), userConfigurationDto.getSchemaVersion(), appDto.getTenantId());
        Assert.assertNull(endpointUserConfigurationDto);
    }

    @Test
    public void saveUserConfigurationTest() throws IOException {
        EndpointUserConfigurationDto configurationDto = generateEndpointUserConfigurationDto(null, null, null, readSchemaFileAsString(UserConfigurationServiceImplTest.OVERRIDE_USER_DATA_JSON));
        Assert.assertNotNull(configurationDto);
    }

    @Test
    public void userConfigNullTest() {
        EndpointUserConfigurationDto dto = null;
        EndpointUserConfigurationDto result = userConfigurationService.saveUserConfiguration(dto);
        Assert.assertNull(result);
    }

    @Test
    public void userConfigNotNullTest() throws IOException {
        EndpointUserConfigurationDto configurationDto = generateEndpointUserConfigurationDto(null, null, null, readSchemaFileAsString(UserConfigurationServiceImplTest.OVERRIDE_USER_DATA_JSON));
        EndpointUserConfigurationDto result = userConfigurationService.saveUserConfiguration(configurationDto);
        Assert.assertEquals(configurationDto, result);
    }

    @Test(expected = IncorrectParameterException.class)
    public void saveUserConfigBlankTest() {
        EndpointUserConfigurationDto configurationDto = generateEndpointUserConfigurationDto(null, null, null, "");
        userConfigurationService.saveUserConfiguration(configurationDto);
    }

    @Test(expected = IncorrectParameterException.class)
    public void appDtoNullTest() throws IOException {
        EndpointUserConfigurationDto configurationDto = generateEndpointUserConfigurationDto(null, null, null, readSchemaFileAsString(UserConfigurationServiceImplTest.OVERRIDE_USER_DATA_JSON), true);
        EndpointUserConfigurationDto result = userConfigurationService.saveUserConfiguration(configurationDto);
        Assert.assertEquals(configurationDto, result);
    }
}

