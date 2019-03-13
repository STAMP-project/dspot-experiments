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
package org.kaaproject.kaa.server.operations.service.delta;


import GetDeltaResponse.GetDeltaResponseType.CONF_RESYNC;
import GetDeltaResponse.GetDeltaResponseType.NO_DELTA;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaaproject.kaa.common.avro.GenericAvroConverter;
import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.common.dto.ConfigurationDto;
import org.kaaproject.kaa.common.dto.ConfigurationSchemaDto;
import org.kaaproject.kaa.common.dto.EndpointConfigurationDto;
import org.kaaproject.kaa.common.dto.EndpointGroupStateDto;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;
import org.kaaproject.kaa.common.dto.EndpointProfileSchemaDto;
import org.kaaproject.kaa.common.dto.ProfileFilterDto;
import org.kaaproject.kaa.common.dto.TenantDto;
import org.kaaproject.kaa.common.endpoint.gen.BasicEndpointProfile;
import org.kaaproject.kaa.common.hash.EndpointObjectHash;
import org.kaaproject.kaa.server.common.dao.AbstractTest;
import org.kaaproject.kaa.server.operations.pojo.GetDeltaRequest;
import org.kaaproject.kaa.server.operations.pojo.GetDeltaResponse;
import org.kaaproject.kaa.server.operations.service.OperationsServiceIT;
import org.kaaproject.kaa.server.operations.service.cache.CacheService;
import org.kaaproject.kaa.server.operations.service.cache.ConfigurationCacheEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/operations/common-test-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class DeltaServiceIT extends AbstractTest {
    protected static final Logger LOG = LoggerFactory.getLogger(DeltaServiceIT.class);

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static final int PROFILE_VERSION = 1;

    private static final int PROFILE_SCHEMA_VERSION = 1;

    private static final int OLD_ENDPOINT_SEQ_NUMBER = 0;

    private static final int NEW_APPLICATION_SEQ_NUMBER = 6;

    private static final int MAJOR_VERSION = 1;

    private static final int CONF_SCHEMA_VERSION = 2;

    private static final String CUSTOMER_ID = "CustomerId";

    private static final String APPLICATION_ID = "ApplicationId";

    private static final String APPLICATION_NAME = "ApplicationName";

    private static final GenericAvroConverter<GenericRecord> avroConverter = new GenericAvroConverter(BasicEndpointProfile.SCHEMA$);

    private static final BasicEndpointProfile ENDPOINT_PROFILE = new BasicEndpointProfile("dummy profile 1");

    private static final byte[] ENDPOINT_KEY = "EndpointKey".getBytes(DeltaServiceIT.UTF_8);

    private static byte[] PROFILE_BYTES;

    private static String PROFILE_JSON;

    private static String APP_TOKEN;

    @Autowired
    protected DeltaService deltaService;

    @Autowired
    protected CacheService cacheService;

    private TenantDto tenant;

    private ApplicationDto application;

    private EndpointProfileSchemaDto profileSchema;

    private ProfileFilterDto profileFilter;

    private EndpointProfileDto endpointProfile;

    private EndpointConfigurationDto endpointConfiguration;

    private byte[] endpointConfigurationBytes;

    private ConfigurationSchemaDto confSchema;

    private String egAllId;

    private String pfAllId;

    private String cfAllId;

    @Test
    public void testDeltaServiceNoHistoryDelta() throws Exception {
        GetDeltaRequest request = new GetDeltaRequest(application.getApplicationToken(), EndpointObjectHash.fromSha1(endpointConfiguration.getConfiguration()), true);
        request.setEndpointProfile(endpointProfile);
        GetDeltaResponse response = deltaService.getDelta(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(NO_DELTA, response.getResponseType());
        Assert.assertNull(response.getDelta());
        Assert.assertNull(response.getConfSchema());
    }

    @Test
    public void testDeltaServiceFirstRequest() throws Exception {
        GetDeltaRequest request = new GetDeltaRequest(application.getApplicationToken());
        request.setEndpointProfile(endpointProfile);
        GetDeltaResponse response = deltaService.getDelta(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(CONF_RESYNC, response.getResponseType());
        Assert.assertNotNull(response.getDelta());
        endpointConfigurationBytes = response.getDelta().getData();
        Assert.assertNotNull(endpointConfigurationBytes);
    }

    @Test
    public void testDeltaServiceHashMismatch() throws Exception {
        GetDeltaRequest request = new GetDeltaRequest(application.getApplicationToken(), EndpointObjectHash.fromBytes(new byte[]{ 1, 2, 3 }));
        request.setEndpointProfile(endpointProfile);
        GetDeltaResponse response = deltaService.getDelta(request);
        Assert.assertNotNull(response);
        Assert.assertEquals(CONF_RESYNC, response.getResponseType());
        Assert.assertNotNull(response.getDelta());
        endpointConfigurationBytes = response.getDelta().getData();
        Assert.assertNotNull(endpointConfigurationBytes);
    }

    @Test
    public void testDeltaServiceSecondRequest() throws Exception {
        ConfigurationCacheEntry cacheEntry = deltaService.getConfiguration(DeltaServiceIT.APP_TOKEN, "EndpointId", endpointProfile);
        Assert.assertNotNull(cacheEntry);
        Assert.assertNotNull(cacheEntry.getConfiguration());
        Assert.assertNotNull(cacheEntry.getDelta());
        Assert.assertNotNull(cacheEntry.getHash());
        Assert.assertNull(cacheEntry.getUserConfigurationHash());
        GenericAvroConverter<GenericContainer> newConfConverter = new GenericAvroConverter(new Schema.Parser().parse(confSchema.getBaseSchema()));
        GenericContainer container = newConfConverter.decodeJson(OperationsServiceIT.getResourceAsString(OperationsServiceIT.BASE_DATA_UPDATED_LOCATION));
        byte[] newConfData = newConfConverter.encodeToJsonBytes(container);
        ConfigurationDto newConfDto = new ConfigurationDto();
        newConfDto.setEndpointGroupId(egAllId);
        newConfDto.setSchemaId(confSchema.getId());
        newConfDto.setBody(new String(newConfData, DeltaServiceIT.UTF_8));
        newConfDto = configurationService.saveConfiguration(newConfDto);
        configurationService.activateConfiguration(newConfDto.getId(), "test");
        List<EndpointGroupStateDto> changes = new ArrayList<>();
        changes.add(new EndpointGroupStateDto(egAllId, pfAllId, newConfDto.getId()));
        endpointProfile.setGroupState(changes);
        ConfigurationCacheEntry newCacheEntry = deltaService.getConfiguration(DeltaServiceIT.APP_TOKEN, "EndpointId", endpointProfile);
        Assert.assertNotNull(newCacheEntry);
        Assert.assertNotNull(newCacheEntry.getConfiguration());
        Assert.assertNotNull(newCacheEntry.getDelta());
        Assert.assertNotNull(newCacheEntry.getHash());
        Assert.assertNull(newCacheEntry.getUserConfigurationHash());
        Assert.assertNotEquals(cacheEntry.getHash(), newCacheEntry.getHash());
    }
}

