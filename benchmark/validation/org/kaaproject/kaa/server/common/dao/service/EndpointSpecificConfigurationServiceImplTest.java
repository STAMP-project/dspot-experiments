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


import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.ConfigurationSchemaDto;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;
import org.kaaproject.kaa.common.dto.EndpointSpecificConfigurationDto;
import org.kaaproject.kaa.server.common.dao.ConfigurationService;
import org.kaaproject.kaa.server.common.dao.EndpointService;
import org.kaaproject.kaa.server.common.dao.impl.EndpointSpecificConfigurationDao;
import org.kaaproject.kaa.server.common.dao.model.EndpointSpecificConfiguration;
import org.mockito.Mockito;
import org.springframework.util.Base64Utils;


public class EndpointSpecificConfigurationServiceImplTest {
    private static final byte[] KEY = Base64Utils.decodeFromString("keyHasg=");

    private static final String APP_ID = "app";

    private static final String CONFIG_BODY = "body";

    private static final int CONF_VERSION = 7;

    private static final EndpointSpecificConfigurationServiceImpl SERVICE = new EndpointSpecificConfigurationServiceImpl();

    private EndpointSpecificConfigurationDao daoMock = Mockito.mock(EndpointSpecificConfigurationDao.class);

    private EndpointService endpointServiceMock = Mockito.mock(EndpointService.class);

    private ConfigurationService configurationServiceMock = Mockito.mock(ConfigurationService.class);

    private EndpointSpecificConfiguration configuration = Mockito.mock(EndpointSpecificConfiguration.class);

    private EndpointSpecificConfigurationDto configurationDto = Mockito.mock(EndpointSpecificConfigurationDto.class);

    @Test
    public void testShouldSaveWithActiveConfigSchemaVersion() {
        EndpointSpecificConfigurationDto dto = new EndpointSpecificConfigurationDto();
        dto.setConfigurationSchemaVersion(null);
        dto.setEndpointKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY);
        dto.setConfiguration(EndpointSpecificConfigurationServiceImplTest.CONFIG_BODY);
        Mockito.when(endpointServiceMock.findEndpointProfileByKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY)).thenReturn(generateProfile());
        Mockito.when(configurationServiceMock.normalizeAccordingToOverrideConfigurationSchema(EndpointSpecificConfigurationServiceImplTest.APP_ID, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION, EndpointSpecificConfigurationServiceImplTest.CONFIG_BODY)).thenReturn("valid body");
        Mockito.when(daoMock.save(dto)).thenReturn(configuration);
        Mockito.when(configuration.toDto()).thenReturn(new EndpointSpecificConfigurationDto());
        Assert.assertTrue(((EndpointSpecificConfigurationServiceImplTest.SERVICE.save(dto)) != null));
        Mockito.verify(configurationServiceMock).normalizeAccordingToOverrideConfigurationSchema(EndpointSpecificConfigurationServiceImplTest.APP_ID, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION, EndpointSpecificConfigurationServiceImplTest.CONFIG_BODY);
        Mockito.verify(endpointServiceMock).findEndpointProfileByKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY);
        Mockito.verify(daoMock).save(dto);
    }

    @Test
    public void testShouldSaveWithProvidedConfigSchemaVersion() {
        EndpointSpecificConfigurationDto dto = new EndpointSpecificConfigurationDto();
        dto.setConfigurationSchemaVersion(7);
        dto.setEndpointKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY);
        dto.setConfiguration(EndpointSpecificConfigurationServiceImplTest.CONFIG_BODY);
        Mockito.when(endpointServiceMock.findEndpointProfileByKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY)).thenReturn(generateProfile());
        Mockito.when(configurationServiceMock.normalizeAccordingToOverrideConfigurationSchema(EndpointSpecificConfigurationServiceImplTest.APP_ID, 7, EndpointSpecificConfigurationServiceImplTest.CONFIG_BODY)).thenReturn("valid body");
        Mockito.when(daoMock.save(dto)).thenReturn(configuration);
        Mockito.when(configuration.toDto()).thenReturn(new EndpointSpecificConfigurationDto());
        Assert.assertTrue(((EndpointSpecificConfigurationServiceImplTest.SERVICE.save(dto)) != null));
        Mockito.verify(configurationServiceMock).normalizeAccordingToOverrideConfigurationSchema(EndpointSpecificConfigurationServiceImplTest.APP_ID, 7, EndpointSpecificConfigurationServiceImplTest.CONFIG_BODY);
        Mockito.verify(endpointServiceMock).findEndpointProfileByKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY);
        Mockito.verify(daoMock).save(dto);
    }

    @Test
    public void testShouldActiveConfigurationByEndpointProfile() {
        Mockito.when(daoMock.findByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationServiceImplTest.KEY, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION)).thenReturn(configuration);
        Mockito.when(configuration.toDto()).thenReturn(new EndpointSpecificConfigurationDto());
        Assert.assertTrue(EndpointSpecificConfigurationServiceImplTest.SERVICE.findActiveConfigurationByEndpointProfile(generateProfile()).isPresent());
        Mockito.verify(daoMock).findByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationServiceImplTest.KEY, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION);
    }

    @Test
    public void testShouldFindActiveConfigurationByEndpointKeyHash() {
        EndpointSpecificConfigurationDto dto = new EndpointSpecificConfigurationDto();
        dto.setConfigurationSchemaVersion(EndpointSpecificConfigurationServiceImplTest.CONF_VERSION);
        Mockito.when(endpointServiceMock.findEndpointProfileByKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY)).thenReturn(generateProfile());
        Mockito.when(daoMock.findByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationServiceImplTest.KEY, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION)).thenReturn(configuration);
        Mockito.when(configuration.toDto()).thenReturn(dto);
        Assert.assertTrue(EndpointSpecificConfigurationServiceImplTest.SERVICE.findActiveConfigurationByEndpointKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY).isPresent());
        Mockito.verify(endpointServiceMock).findEndpointProfileByKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY);
        Mockito.verify(daoMock).findByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationServiceImplTest.KEY, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION);
    }

    @Test
    public void testShouldNotFindActiveConfigurationByEndpointKeyHashWhenProfileNotFound() {
        Mockito.when(endpointServiceMock.findEndpointProfileByKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY)).thenReturn(null);
        Assert.assertFalse(EndpointSpecificConfigurationServiceImplTest.SERVICE.findActiveConfigurationByEndpointKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY).isPresent());
        Mockito.verify(endpointServiceMock).findEndpointProfileByKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY);
    }

    @Test
    public void testShouldDeleteActiveConfigurationByEndpointKeyHash() {
        Mockito.when(endpointServiceMock.findEndpointProfileByKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY)).thenReturn(generateProfile());
        Mockito.when(daoMock.findByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationServiceImplTest.KEY, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION)).thenReturn(configuration);
        Mockito.when(configuration.toDto()).thenReturn(configurationDto);
        Mockito.when(configurationDto.getConfigurationSchemaVersion()).thenReturn(EndpointSpecificConfigurationServiceImplTest.CONF_VERSION);
        Assert.assertTrue(EndpointSpecificConfigurationServiceImplTest.SERVICE.deleteActiveConfigurationByEndpointKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY).isPresent());
        Mockito.verify(endpointServiceMock).findEndpointProfileByKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY);
        Mockito.verify(daoMock).findByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationServiceImplTest.KEY, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION);
        Mockito.verify(daoMock).removeByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationServiceImplTest.KEY, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION);
    }

    @Test
    public void testShouldDeleteByEndpointKeyHashAndConfSchemaVersion() {
        EndpointProfileDto profile = generateProfile();
        profile.setConfigurationVersion(0);
        Mockito.when(endpointServiceMock.findEndpointProfileByKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY)).thenReturn(profile);
        Mockito.when(daoMock.findByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationServiceImplTest.KEY, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION)).thenReturn(configuration);
        Mockito.when(configuration.toDto()).thenReturn(configurationDto);
        Mockito.when(configurationServiceMock.findConfSchemaByAppIdAndVersion(EndpointSpecificConfigurationServiceImplTest.APP_ID, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION)).thenReturn(new ConfigurationSchemaDto());
        Mockito.when(configurationDto.getConfigurationSchemaVersion()).thenReturn(EndpointSpecificConfigurationServiceImplTest.CONF_VERSION);
        Assert.assertTrue(EndpointSpecificConfigurationServiceImplTest.SERVICE.deleteByEndpointKeyHashAndConfSchemaVersion(EndpointSpecificConfigurationServiceImplTest.KEY, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION).isPresent());
        Mockito.verify(endpointServiceMock).findEndpointProfileByKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY);
        Mockito.verify(daoMock).findByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationServiceImplTest.KEY, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION);
        Mockito.verify(daoMock).removeByEndpointKeyHashAndConfigurationVersion(EndpointSpecificConfigurationServiceImplTest.KEY, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION);
        Mockito.verify(configurationServiceMock).findConfSchemaByAppIdAndVersion(EndpointSpecificConfigurationServiceImplTest.APP_ID, EndpointSpecificConfigurationServiceImplTest.CONF_VERSION);
    }

    @Test
    public void testShouldNotDeleteActiveConfigurationByEndpointKeyHashWhenProfileNotFound() {
        Mockito.when(endpointServiceMock.findEndpointProfileByKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY)).thenReturn(null);
        Assert.assertFalse(EndpointSpecificConfigurationServiceImplTest.SERVICE.deleteActiveConfigurationByEndpointKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY).isPresent());
        Mockito.verify(endpointServiceMock).findEndpointProfileByKeyHash(EndpointSpecificConfigurationServiceImplTest.KEY);
    }
}

