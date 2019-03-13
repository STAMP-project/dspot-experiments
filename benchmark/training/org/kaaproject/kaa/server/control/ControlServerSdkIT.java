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
package org.kaaproject.kaa.server.control;


import SdkPlatform.ANDROID;
import SdkPlatform.C;
import SdkPlatform.CPP;
import SdkPlatform.JAVA;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.common.dto.ConfigurationSchemaDto;
import org.kaaproject.kaa.common.dto.EndpointProfileSchemaDto;
import org.kaaproject.kaa.common.dto.NotificationSchemaDto;
import org.kaaproject.kaa.common.dto.event.ApplicationEventFamilyMapDto;
import org.kaaproject.kaa.common.dto.file.FileData;
import org.kaaproject.kaa.common.dto.logs.LogSchemaDto;


/**
 * The Class ControlServerSdkIT.
 */
public class ControlServerSdkIT extends AbstractTestControlServer {
    /**
     * Test generate java sdk.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testGenerateJavaSdk() throws Exception {
        ApplicationDto application = createApplication(tenantAdminDto);
        EndpointProfileSchemaDto profileSchema = createEndpointProfileSchema(application.getId(), null);
        ConfigurationSchemaDto configSchema = createConfigurationSchema(application.getId(), null);
        NotificationSchemaDto notificationSchema = createUserNotificationSchema(application.getId());
        LogSchemaDto logSchema = createLogSchema(application.getId());
        FileData sdk = client.downloadSdk(getSdkProfile(application.getId(), profileSchema.getVersion(), configSchema.getVersion(), notificationSchema.getVersion(), null, logSchema.getVersion(), null).getId(), JAVA);
        Assert.assertNotNull(sdk);
        Assert.assertFalse(AbstractTestControlServer.strIsEmpty(sdk.getFileName()));
        Assert.assertNotNull(sdk.getFileData());
        Assert.assertEquals(JAVA.getContentType(), sdk.getContentType());
    }

    /**
     * Test generate java sdk with event support.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testGenerateJavaSdkWithEventSupport() throws Exception {
        ApplicationDto application = createApplication(tenantAdminDto);
        EndpointProfileSchemaDto profileSchema = createEndpointProfileSchema(application.getId(), null);
        ConfigurationSchemaDto configSchema = createConfigurationSchema(application.getId(), null);
        NotificationSchemaDto notificationSchema = createUserNotificationSchema(application.getId());
        LogSchemaDto logSchema = createLogSchema(application.getId());
        ApplicationEventFamilyMapDto aefMap = createApplicationEventFamilyMap(application.getApplicationToken(), null, 1);
        List<String> aefMapIds = Collections.singletonList(aefMap.getId());
        FileData sdk = client.downloadSdk(getSdkProfile(application.getId(), profileSchema.getVersion(), configSchema.getVersion(), notificationSchema.getVersion(), aefMapIds, logSchema.getVersion(), null).getId(), JAVA);
        Assert.assertNotNull(sdk);
        Assert.assertFalse(AbstractTestControlServer.strIsEmpty(sdk.getFileName()));
        Assert.assertNotNull(sdk.getFileData());
        Assert.assertEquals(JAVA.getContentType(), sdk.getContentType());
    }

    /**
     * Test generate android sdk with event support.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testGenerateAndroidSdkWithEventSupport() throws Exception {
        ApplicationDto application = createApplication(tenantAdminDto);
        EndpointProfileSchemaDto profileSchema = createEndpointProfileSchema(application.getId(), null);
        ConfigurationSchemaDto configSchema = createConfigurationSchema(application.getId(), null);
        NotificationSchemaDto notificationSchema = createUserNotificationSchema(application.getId());
        LogSchemaDto logSchema = createLogSchema(application.getId());
        ApplicationEventFamilyMapDto aefMap = createApplicationEventFamilyMap(application.getApplicationToken(), null, 1);
        List<String> aefMapIds = Collections.singletonList(aefMap.getId());
        FileData sdk = client.downloadSdk(getSdkProfile(application.getId(), profileSchema.getVersion(), configSchema.getVersion(), notificationSchema.getVersion(), aefMapIds, logSchema.getVersion(), null).getId(), ANDROID);
        Assert.assertNotNull(sdk);
        Assert.assertFalse(AbstractTestControlServer.strIsEmpty(sdk.getFileName()));
        Assert.assertNotNull(sdk.getFileData());
        Assert.assertEquals(ANDROID.getContentType(), sdk.getContentType());
    }

    /**
     * Test generate cpp sdk.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testGenerateCppSdk() throws Exception {
        ApplicationDto application = createApplication(tenantAdminDto);
        EndpointProfileSchemaDto profileSchema = createEndpointProfileSchema(application.getId(), null);
        ConfigurationSchemaDto configSchema = createConfigurationSchema(application.getId(), null);
        NotificationSchemaDto notificationSchema = createUserNotificationSchema(application.getId());
        LogSchemaDto logSchema = createLogSchema(application.getId());
        FileData sdk = client.downloadSdk(getSdkProfile(application.getId(), profileSchema.getVersion(), configSchema.getVersion(), notificationSchema.getVersion(), null, logSchema.getVersion(), null).getId(), CPP);
        Assert.assertNotNull(sdk);
        Assert.assertFalse(AbstractTestControlServer.strIsEmpty(sdk.getFileName()));
        Assert.assertNotNull(sdk.getFileData());
        Assert.assertEquals(CPP.getContentType(), sdk.getContentType());
    }

    /**
     * Test generate cpp sdk with event support.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testGenerateCppSdkWithEventSupport() throws Exception {
        ApplicationDto application = createApplication(tenantAdminDto);
        EndpointProfileSchemaDto profileSchema = createEndpointProfileSchema(application.getId(), null);
        ConfigurationSchemaDto configSchema = createConfigurationSchema(application.getId(), null);
        NotificationSchemaDto notificationSchema = createUserNotificationSchema(application.getId());
        LogSchemaDto logSchema = createLogSchema(application.getId());
        ApplicationEventFamilyMapDto aefMap = createApplicationEventFamilyMap(application.getApplicationToken(), null, 1);
        List<String> aefMapIds = Collections.singletonList(aefMap.getId());
        FileData sdk = client.downloadSdk(getSdkProfile(application.getId(), profileSchema.getVersion(), configSchema.getVersion(), notificationSchema.getVersion(), aefMapIds, logSchema.getVersion(), null).getId(), CPP);
        Assert.assertNotNull(sdk);
        Assert.assertFalse(AbstractTestControlServer.strIsEmpty(sdk.getFileName()));
        Assert.assertNotNull(sdk.getFileData());
        Assert.assertEquals(CPP.getContentType(), sdk.getContentType());
    }

    /**
     * Test generate c sdk.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testGenerateCSdk() throws Exception {
        ApplicationDto application = createApplication(tenantAdminDto);
        EndpointProfileSchemaDto profileSchema = createEndpointProfileSchema(application.getId(), null);
        ConfigurationSchemaDto configSchema = createConfigurationSchema(application.getId(), null);
        NotificationSchemaDto notificationSchema = createUserNotificationSchema(application.getId());
        LogSchemaDto logSchema = createLogSchema(application.getId());
        FileData sdk = client.downloadSdk(getSdkProfile(application.getId(), profileSchema.getVersion(), configSchema.getVersion(), notificationSchema.getVersion(), null, logSchema.getVersion(), null).getId(), C);
        Assert.assertNotNull(sdk);
        Assert.assertFalse(AbstractTestControlServer.strIsEmpty(sdk.getFileName()));
        Assert.assertNotNull(sdk.getFileData());
        Assert.assertEquals(C.getContentType(), sdk.getContentType());
    }

    /**
     * Test generate c sdk with event support.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testGenerateCSdkWithEventSupport() throws Exception {
        ApplicationDto application = createApplication(tenantAdminDto);
        EndpointProfileSchemaDto profileSchema = createEndpointProfileSchema(application.getId(), null);
        ConfigurationSchemaDto configSchema = createConfigurationSchema(application.getId(), null);
        NotificationSchemaDto notificationSchema = createUserNotificationSchema(application.getId());
        LogSchemaDto logSchema = createLogSchema(application.getId());
        ApplicationEventFamilyMapDto aefMap = createApplicationEventFamilyMap(application.getApplicationToken(), null, 1);
        List<String> aefMapIds = Collections.singletonList(aefMap.getId());
        FileData sdk = client.downloadSdk(getSdkProfile(application.getId(), profileSchema.getVersion(), configSchema.getVersion(), notificationSchema.getVersion(), aefMapIds, logSchema.getVersion(), null).getId(), C);
        Assert.assertNotNull(sdk);
        Assert.assertFalse(AbstractTestControlServer.strIsEmpty(sdk.getFileName()));
        Assert.assertNotNull(sdk.getFileData());
        Assert.assertEquals(C.getContentType(), sdk.getContentType());
    }

    /**
     * Test generate java sdk with invalid application.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testGenerateJavaSdkWithInvalidApplication() throws Exception {
        checkNotFound(new AbstractTestControlServer.TestRestCall() {
            @Override
            public void executeRestCall() throws Exception {
                client.downloadSdk(getSdkProfile("123", 1, 1, 1, null, 0, null).getId(), JAVA);
            }
        });
    }

    /**
     * Test generate java sdk with invalid profile schema.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testGenerateJavaSdkWithInvalidProfileSchema() throws Exception {
        final ApplicationDto application = createApplication(tenantAdminDto);
        loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        checkNotFound(new AbstractTestControlServer.TestRestCall() {
            @Override
            public void executeRestCall() throws Exception {
                client.downloadSdk(getSdkProfile(application.getId(), 2, 2, 2, null, 0, null).getId(), JAVA);
            }
        });
    }

    /**
     * Test generate java sdk with invalid configuration schema.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testGenerateJavaSdkWithInvalidConfigurationSchema() throws Exception {
        final ApplicationDto application = createApplication(tenantAdminDto);
        loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        final EndpointProfileSchemaDto profileSchema = createEndpointProfileSchema(application.getId(), null);
        checkNotFound(new AbstractTestControlServer.TestRestCall() {
            @Override
            public void executeRestCall() throws Exception {
                client.downloadSdk(getSdkProfile(application.getId(), profileSchema.getVersion(), 2, 2, null, 0, null).getId(), JAVA);
            }
        });
    }

    /**
     * Test generate java sdk with invalid notification schema.
     *
     * @throws Exception
     * 		the exception
     */
    @Test
    public void testGenerateJavaSdkWithInvalidNotificationSchema() throws Exception {
        final ApplicationDto application = createApplication(tenantAdminDto);
        loginTenantDeveloper(AbstractTestControlServer.tenantDeveloperUser);
        final EndpointProfileSchemaDto profileSchema = createEndpointProfileSchema(application.getId(), null);
        final ConfigurationSchemaDto configSchema = createConfigurationSchema(application.getId(), null);
        checkNotFound(new AbstractTestControlServer.TestRestCall() {
            @Override
            public void executeRestCall() throws Exception {
                client.downloadSdk(getSdkProfile(application.getId(), profileSchema.getVersion(), configSchema.getVersion(), 2, null, 0, null).getId(), JAVA);
            }
        });
    }
}

