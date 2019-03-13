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
package org.kaaproject.kaa.server.operations.service.profile;


import java.io.IOException;
import java.util.Arrays;
import org.apache.avro.generic.GenericRecord;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaaproject.kaa.common.avro.GenericAvroConverter;
import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;
import org.kaaproject.kaa.common.dto.EndpointProfileSchemaDto;
import org.kaaproject.kaa.common.dto.TenantDto;
import org.kaaproject.kaa.common.endpoint.gen.BasicEndpointProfile;
import org.kaaproject.kaa.common.hash.EndpointObjectHash;
import org.kaaproject.kaa.schema.system.EmptyData;
import org.kaaproject.kaa.server.common.dao.AbstractTest;
import org.kaaproject.kaa.server.common.dao.ApplicationService;
import org.kaaproject.kaa.server.common.dao.CtlService;
import org.kaaproject.kaa.server.common.dao.SdkProfileService;
import org.kaaproject.kaa.server.common.dao.UserService;
import org.kaaproject.kaa.server.operations.pojo.RegisterProfileRequest;
import org.kaaproject.kaa.server.operations.pojo.UpdateProfileRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/operations/common-test-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Transactional
public class ProfileServiceIT extends AbstractTest {
    protected static final Logger LOG = LoggerFactory.getLogger(ProfileServiceIT.class);

    private static final int PROFILE_SCHEMA_VERSION = 1;

    private static final int NEW_PROFILE_SCHEMA_VERSION = 2;

    private static final byte[] ENDPOINT_KEY = ProfileServiceIT.getRandEndpointKey();

    private static final String CUSTOMER_NAME = "CUSTOMER_NAME";

    private static final String APP_NAME = "APP_NAME";

    private static final EmptyData ENDPOINT_PROFILE = new EmptyData();

    private static final BasicEndpointProfile NEW_ENDPOINT_PROFILE = new BasicEndpointProfile("newprofile");

    private final GenericAvroConverter<GenericRecord> baseAvroConverter = new GenericAvroConverter<GenericRecord>(EmptyData.SCHEMA$);

    private final GenericAvroConverter<GenericRecord> newAvroConverter = new GenericAvroConverter<GenericRecord>(BasicEndpointProfile.SCHEMA$);

    @Autowired
    protected ProfileService profileService;

    @Autowired
    protected SdkProfileService sdkProfileService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ApplicationService applicationService;

    @Autowired
    protected org.kaaproject.kaa.server.common.dao.ProfileService daoProfileService;

    @Autowired
    protected CtlService ctlService;

    private String sdkToken;

    private String newSdkToken;

    private TenantDto tenant;

    private ApplicationDto application;

    private EndpointProfileSchemaDto schema1Dto;

    private EndpointProfileSchemaDto schema2Dto;

    @Test
    public void registerProfileServiceTest() throws IOException {
        byte[] profile = baseAvroConverter.encode(ProfileServiceIT.ENDPOINT_PROFILE);
        RegisterProfileRequest request = new RegisterProfileRequest(application.getApplicationToken(), ProfileServiceIT.ENDPOINT_KEY, sdkToken, profile);
        EndpointProfileDto dto = profileService.registerProfile(request);
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getId());
        Assert.assertTrue(Arrays.equals(ProfileServiceIT.ENDPOINT_KEY, dto.getEndpointKey()));
        Assert.assertTrue(Arrays.equals(EndpointObjectHash.fromSha1(ProfileServiceIT.ENDPOINT_KEY).getData(), dto.getEndpointKeyHash()));
        Assert.assertEquals(baseAvroConverter.encodeToJson(ProfileServiceIT.ENDPOINT_PROFILE), dto.getClientProfileBody().replaceAll(" ", ""));
        Assert.assertTrue(Arrays.equals(EndpointObjectHash.fromSha1(profile).getData(), dto.getProfileHash()));
    }

    @Test
    public void updateProfileServiceTest() throws IOException {
        byte[] profile = baseAvroConverter.encode(ProfileServiceIT.ENDPOINT_PROFILE);
        RegisterProfileRequest request = new RegisterProfileRequest(application.getApplicationToken(), ProfileServiceIT.ENDPOINT_KEY, sdkToken, profile);
        EndpointProfileDto oldDto = profileService.registerProfile(request);
        Assert.assertEquals(baseAvroConverter.encodeToJson(ProfileServiceIT.ENDPOINT_PROFILE), oldDto.getClientProfileBody().replaceAll(" ", ""));
        byte[] newProfile = newAvroConverter.encode(ProfileServiceIT.NEW_ENDPOINT_PROFILE);
        UpdateProfileRequest updateRequest = new UpdateProfileRequest(application.getApplicationToken(), EndpointObjectHash.fromSha1(ProfileServiceIT.ENDPOINT_KEY), null, newProfile, newSdkToken);
        EndpointProfileDto newDto = profileService.updateProfile(updateRequest);
        Assert.assertNotNull(newDto);
        Assert.assertNotNull(newDto.getId());
        Assert.assertEquals(oldDto.getId(), newDto.getId());
        Assert.assertEquals(newAvroConverter.encodeToJson(ProfileServiceIT.NEW_ENDPOINT_PROFILE), newDto.getClientProfileBody().replaceAll(" ", ""));
        Assert.assertTrue(Arrays.equals(EndpointObjectHash.fromSha1(newProfile).getData(), newDto.getProfileHash()));
    }
}

