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
package org.kaaproject.kaa.server.operations.service.thrift;


import OperationsThriftService.Iface;
import org.apache.thrift.TException;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.common.dto.ProfileFilterDto;
import org.kaaproject.kaa.server.common.dao.ApplicationService;
import org.kaaproject.kaa.server.common.thrift.gen.operations.Notification;
import org.kaaproject.kaa.server.common.thrift.gen.operations.RedirectionRule;
import org.kaaproject.kaa.server.operations.service.akka.AkkaService;
import org.kaaproject.kaa.server.operations.service.cache.AppProfileVersionsKey;
import org.kaaproject.kaa.server.operations.service.cache.AppSeqNumber;
import org.kaaproject.kaa.server.operations.service.cache.CacheService;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class OperationsThriftServiceTest {
    private static final int PF_VERSION = 3;

    private static final String TEST_TENANT_ID = "testTenantId";

    private static final String TEST_APP_ID = "testAppId";

    private static final String TEST_APP_TOKEN = "testApp";

    private static final String TEST_PF_ID = "pfID";

    private static final String TEST_PF_ENDPOINT_SCHEMA_ID = "epPfSchemaId";

    private static final String TEST_PF_SERVER_SCHEMA_ID = "serverPfSchemaId";

    private static final Integer TEST_PF_ENDPOINT_SCHEMA_VERSION = 42;

    private static final Integer TEST_PF_SERVER_SCHEMA_VERSION = 73;

    private static final int TEST_APP_SEQ_NUMBER = 42;

    Iface operationsThriftService;

    // mocks
    private AkkaService akkaService;

    private CacheService cacheService;

    private ApplicationService applicationService;

    @Test
    public void testSimpleFlowWithZeroAppSeqNumber() throws TException {
        Notification notification = new Notification();
        notification.setAppId(OperationsThriftServiceTest.TEST_APP_ID);
        notification.setProfileFilterId(OperationsThriftServiceTest.TEST_PF_ID);
        notification.setAppSeqNumber(0);
        ApplicationDto appDto = new ApplicationDto();
        appDto.setId(OperationsThriftServiceTest.TEST_APP_ID);
        appDto.setApplicationToken(OperationsThriftServiceTest.TEST_APP_TOKEN);
        ProfileFilterDto pfDto = new ProfileFilterDto();
        pfDto.setEndpointProfileSchemaId(OperationsThriftServiceTest.TEST_PF_ENDPOINT_SCHEMA_ID);
        pfDto.setEndpointProfileSchemaVersion(OperationsThriftServiceTest.TEST_PF_ENDPOINT_SCHEMA_VERSION);
        pfDto.setServerProfileSchemaId(OperationsThriftServiceTest.TEST_PF_SERVER_SCHEMA_ID);
        pfDto.setServerProfileSchemaVersion(OperationsThriftServiceTest.TEST_PF_SERVER_SCHEMA_VERSION);
        Mockito.when(applicationService.findAppById(OperationsThriftServiceTest.TEST_APP_ID)).thenReturn(appDto);
        Mockito.when(cacheService.getFilter(OperationsThriftServiceTest.TEST_PF_ID)).thenReturn(pfDto);
        operationsThriftService.onNotification(notification);
        Mockito.verify(applicationService).findAppById(OperationsThriftServiceTest.TEST_APP_ID);
        Mockito.verify(cacheService).getFilter(OperationsThriftServiceTest.TEST_PF_ID);
        Mockito.verify(cacheService).resetFilters(new AppProfileVersionsKey(OperationsThriftServiceTest.TEST_APP_TOKEN, OperationsThriftServiceTest.TEST_PF_ENDPOINT_SCHEMA_VERSION, OperationsThriftServiceTest.TEST_PF_SERVER_SCHEMA_VERSION));
        // Due to notification.setAppSeqNumber(0);
        Mockito.verify(cacheService, Mockito.times(0)).putAppSeqNumber(Mockito.anyString(), Mockito.any(AppSeqNumber.class));
        Mockito.verify(akkaService).onNotification(notification);
    }

    @Test
    public void testSimpleFlowWithNotZeroAppSeqNumber() throws TException {
        Notification notification = new Notification();
        notification.setAppId(OperationsThriftServiceTest.TEST_APP_ID);
        notification.setProfileFilterId(OperationsThriftServiceTest.TEST_PF_ID);
        notification.setAppSeqNumber(OperationsThriftServiceTest.TEST_APP_SEQ_NUMBER);
        ApplicationDto appDto = new ApplicationDto();
        appDto.setId(OperationsThriftServiceTest.TEST_APP_ID);
        appDto.setApplicationToken(OperationsThriftServiceTest.TEST_APP_TOKEN);
        ProfileFilterDto pfDto = new ProfileFilterDto();
        pfDto.setEndpointProfileSchemaId(OperationsThriftServiceTest.TEST_PF_ENDPOINT_SCHEMA_ID);
        pfDto.setEndpointProfileSchemaVersion(OperationsThriftServiceTest.TEST_PF_ENDPOINT_SCHEMA_VERSION);
        pfDto.setServerProfileSchemaId(OperationsThriftServiceTest.TEST_PF_SERVER_SCHEMA_ID);
        pfDto.setServerProfileSchemaVersion(OperationsThriftServiceTest.TEST_PF_SERVER_SCHEMA_VERSION);
        Mockito.when(applicationService.findAppById(OperationsThriftServiceTest.TEST_APP_ID)).thenReturn(appDto);
        Mockito.when(cacheService.getAppSeqNumber(OperationsThriftServiceTest.TEST_APP_TOKEN)).thenReturn(new AppSeqNumber(OperationsThriftServiceTest.TEST_TENANT_ID, OperationsThriftServiceTest.TEST_APP_ID, OperationsThriftServiceTest.TEST_APP_TOKEN, 0));
        Mockito.when(cacheService.getFilter(OperationsThriftServiceTest.TEST_PF_ID)).thenReturn(pfDto);
        operationsThriftService.onNotification(notification);
        Mockito.verify(applicationService).findAppById(OperationsThriftServiceTest.TEST_APP_ID);
        Mockito.verify(cacheService).getFilter(OperationsThriftServiceTest.TEST_PF_ID);
        Mockito.verify(cacheService).resetFilters(new AppProfileVersionsKey(OperationsThriftServiceTest.TEST_APP_TOKEN, OperationsThriftServiceTest.TEST_PF_ENDPOINT_SCHEMA_VERSION, OperationsThriftServiceTest.TEST_PF_SERVER_SCHEMA_VERSION));
        // Due to notification.setAppSeqNumber(TEST_APP_SEQ_NUMBER);
        Mockito.verify(cacheService, Mockito.times(1)).putAppSeqNumber(OperationsThriftServiceTest.TEST_APP_TOKEN, new AppSeqNumber(OperationsThriftServiceTest.TEST_TENANT_ID, OperationsThriftServiceTest.TEST_APP_ID, OperationsThriftServiceTest.TEST_APP_TOKEN, OperationsThriftServiceTest.TEST_APP_SEQ_NUMBER));
        Mockito.verify(akkaService).onNotification(notification);
    }

    @Test
    public void testAppNotFound() throws TException {
        Notification notification = new Notification();
        notification.setAppId(OperationsThriftServiceTest.TEST_APP_ID);
        notification.setProfileFilterId(OperationsThriftServiceTest.TEST_PF_ID);
        notification.setAppSeqNumber(OperationsThriftServiceTest.TEST_APP_SEQ_NUMBER);
        ProfileFilterDto pfDto = new ProfileFilterDto();
        pfDto.setEndpointProfileSchemaId(OperationsThriftServiceTest.TEST_PF_ENDPOINT_SCHEMA_ID);
        pfDto.setEndpointProfileSchemaVersion(OperationsThriftServiceTest.TEST_PF_ENDPOINT_SCHEMA_VERSION);
        pfDto.setServerProfileSchemaId(OperationsThriftServiceTest.TEST_PF_SERVER_SCHEMA_ID);
        pfDto.setServerProfileSchemaVersion(OperationsThriftServiceTest.TEST_PF_SERVER_SCHEMA_VERSION);
        Mockito.when(applicationService.findAppById(OperationsThriftServiceTest.TEST_APP_ID)).thenReturn(null);
        operationsThriftService.onNotification(notification);
        Mockito.verify(applicationService).findAppById(OperationsThriftServiceTest.TEST_APP_ID);
        Mockito.verify(cacheService, Mockito.times(0)).getFilter(Mockito.anyString());
        Mockito.verify(cacheService, Mockito.times(0)).resetFilters(Mockito.any(AppProfileVersionsKey.class));
        Mockito.verify(cacheService, Mockito.times(0)).putAppSeqNumber(Mockito.anyString(), Mockito.any(AppSeqNumber.class));
        Mockito.verify(akkaService).onNotification(notification);
    }

    @Test
    public void testSetRedirectionRule() throws TException {
        RedirectionRule redirectionRule = new RedirectionRule();
        operationsThriftService.setRedirectionRule(redirectionRule);
        Mockito.verify(akkaService, Mockito.atLeastOnce()).onRedirectionRule(redirectionRule);
    }
}

