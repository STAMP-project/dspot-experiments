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
package org.kaaproject.kaa.server.operations.service.cache;


import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kaaproject.kaa.common.dto.ChangeType;
import org.kaaproject.kaa.common.dto.ConfigurationSchemaDto;
import org.kaaproject.kaa.common.dto.EndpointConfigurationDto;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;
import org.kaaproject.kaa.common.dto.EndpointProfileSchemaDto;
import org.kaaproject.kaa.common.dto.HistoryDto;
import org.kaaproject.kaa.common.dto.ProfileFilterDto;
import org.kaaproject.kaa.common.dto.TopicListEntryDto;
import org.kaaproject.kaa.common.dto.admin.SdkProfileDto;
import org.kaaproject.kaa.common.dto.event.ApplicationEventFamilyMapDto;
import org.kaaproject.kaa.common.hash.EndpointObjectHash;
import org.kaaproject.kaa.server.common.dao.AbstractTest;
import org.kaaproject.kaa.server.common.dao.ApplicationEventMapService;
import org.kaaproject.kaa.server.common.dao.ApplicationService;
import org.kaaproject.kaa.server.common.dao.ConfigurationService;
import org.kaaproject.kaa.server.common.dao.EndpointService;
import org.kaaproject.kaa.server.common.dao.EventClassService;
import org.kaaproject.kaa.server.common.dao.HistoryService;
import org.kaaproject.kaa.server.common.dao.ProfileService;
import org.kaaproject.kaa.server.common.dao.SdkProfileService;
import org.kaaproject.kaa.server.operations.pojo.exceptions.GetDeltaException;
import org.kaaproject.kaa.server.operations.service.cache.concurrent.ConcurrentCacheService;
import org.kaaproject.kaa.server.operations.service.event.EventClassFamilyVersion;
import org.kaaproject.kaa.server.operations.service.event.EventClassFqnVersion;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/operations/cache-test-context.xml")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ConcurrentCacheServiceTest extends AbstractTest {
    private static final int EVENT_CLASS_FAMILY_VERSION = 42;

    private static final String EVENT_CLASS_ID = "EVENT_CLASS_ID";

    private static final String EVENT_CLASS_FAMILY_VERSION_ID = "EVENT_CLASS_FAMILY_VERSION_ID";

    private static final String EVENT_CLASS_FAMILY_ID = "EVENT_CLASS_FAMILY_ID";

    private static final String ECF_NAME = "ECF_NAME";

    private static final String EC_FQN = "EC_FQN";

    private static final String TENANT_ID = "TENANT_ID";

    private static final int CONF1_SCHEMA_VERSION = 1;

    private static final int PROFILE1_SCHEMA_VERSION = 1;

    private static final int PROFILE1_SERVER_SCHEMA_VERSION = 1;

    private static final int PROFILE2_SCHEMA_VERSION = 2;

    private static final int PROFILE2_SERVER_SCHEMA_VERSION = 2;

    private static final String CF1_ID = "cf1";

    private static final String CF2_ID = "cf2";

    private static final String CF3_ID = "cf3";

    private static final String PF1_ID = "pf1";

    private static final String PF2_ID = "pf2";

    private static final String PF3_ID = "pf3";

    private static final String ENDPOINT_GROUP1_ID = "eg1";

    private static final int STRESS_TEST_N_THREADS = 10;

    private static final int STRESS_TEST_INVOCATIONS = 50;

    private static final int ESTIMATED_METHOD_EXECUTION_TIME = 5;

    private static final String TEST_APP_ID = "testAppId";

    private static final String TEST_APP_TOKEN = "testApp";

    private static final String TEST_SDK_TOKEN = "testSdkToken";

    private static final String APP_ID = "testAppId";

    private static final String DEFAULT_VERIFIER_TOKEN = "defaultVerifierToken";

    private static final int TEST_APP_SEQ_NUMBER = 42;

    private static final int TEST_APP_SEQ_NUMBER_NEW = 46;

    private static final EndpointConfigurationDto CF1 = new EndpointConfigurationDto();

    private static final ConfigurationSchemaDto CF1_SCHEMA = new ConfigurationSchemaDto();

    private static final EndpointProfileSchemaDto PF1_SCHEMA = new EndpointProfileSchemaDto();

    private static final SdkProfileDto SDK_PROFILE = new SdkProfileDto();

    private static final ProfileFilterDto TEST_PROFILE_FILTER = new ProfileFilterDto();

    private static final List<ProfileFilterDto> TEST_PROFILE_FILTER_LIST = Collections.singletonList(ConcurrentCacheServiceTest.TEST_PROFILE_FILTER);

    private static final EndpointObjectHash CF1_HASH = EndpointObjectHash.fromSha1(ConcurrentCacheServiceTest.CF1_ID);

    private static final EndpointObjectHash TLCE1_HASH = ConcurrentCacheServiceTest.CF1_HASH;

    private static final TopicListEntryDto TLCE1 = new TopicListEntryDto(100, ConcurrentCacheServiceTest.TLCE1_HASH.getData(), null);

    private static final ConfigurationIdKey TEST_CONF_ID_KEY = new ConfigurationIdKey(ConcurrentCacheServiceTest.APP_ID, ConcurrentCacheServiceTest.TEST_APP_SEQ_NUMBER, ConcurrentCacheServiceTest.CONF1_SCHEMA_VERSION, ConcurrentCacheServiceTest.ENDPOINT_GROUP1_ID);

    private static final HistoryKey TEST_HISTORY_KEY = new HistoryKey(ConcurrentCacheServiceTest.TEST_APP_TOKEN, ConcurrentCacheServiceTest.TEST_APP_SEQ_NUMBER, ConcurrentCacheServiceTest.TEST_APP_SEQ_NUMBER_NEW, ConcurrentCacheServiceTest.CONF1_SCHEMA_VERSION, ConcurrentCacheServiceTest.PROFILE1_SCHEMA_VERSION, ConcurrentCacheServiceTest.PROFILE1_SERVER_SCHEMA_VERSION);

    private static final AppProfileVersionsKey TEST_GET_PROFILES_KEY = new AppProfileVersionsKey(ConcurrentCacheServiceTest.TEST_APP_TOKEN, ConcurrentCacheServiceTest.PROFILE1_SCHEMA_VERSION, ConcurrentCacheServiceTest.PROFILE1_SERVER_SCHEMA_VERSION);

    private static final AppVersionKey CF_SCHEMA_KEY = new AppVersionKey(ConcurrentCacheServiceTest.TEST_APP_TOKEN, ConcurrentCacheServiceTest.CONF1_SCHEMA_VERSION);

    private static final AppVersionKey PF_SCHEMA_KEY = new AppVersionKey(ConcurrentCacheServiceTest.TEST_APP_TOKEN, ConcurrentCacheServiceTest.PROFILE1_SCHEMA_VERSION);

    private static final List<String> AEFMAP_IDS = Arrays.asList("id1");

    private static final ApplicationEventFamilyMapDto APPLICATION_EVENT_FAMILY_MAP_DTO = new ApplicationEventFamilyMapDto();

    private static final List<ApplicationEventFamilyMapDto> AEFM_LIST = Arrays.asList(ConcurrentCacheServiceTest.APPLICATION_EVENT_FAMILY_MAP_DTO);

    private PublicKey publicKey;

    private PublicKey publicKey2;

    private EndpointObjectHash publicKeyHash;

    private EndpointObjectHash publicKeyHash2;

    @Autowired
    private CacheService cacheService;

    private ApplicationService appService;

    private ConfigurationService configurationService;

    private HistoryService historyService;

    private ProfileService profileService;

    private EndpointService endpointService;

    private EventClassService eventClassService;

    private ApplicationEventMapService applicationEventMapService;

    private SdkProfileService sdkProfileService;

    @Test
    public void testGetAppSeqNumber() throws GetDeltaException {
        Assert.assertEquals(new AppSeqNumber(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.TEST_APP_ID, ConcurrentCacheServiceTest.TEST_APP_TOKEN, ConcurrentCacheServiceTest.TEST_APP_SEQ_NUMBER), cacheService.getAppSeqNumber(ConcurrentCacheServiceTest.TEST_APP_TOKEN));
        Mockito.verify(appService, Mockito.times(1)).findAppByApplicationToken(ConcurrentCacheServiceTest.TEST_APP_TOKEN);
        Mockito.reset(appService);
        Assert.assertEquals(new AppSeqNumber(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.TEST_APP_ID, ConcurrentCacheServiceTest.TEST_APP_TOKEN, ConcurrentCacheServiceTest.TEST_APP_SEQ_NUMBER), cacheService.getAppSeqNumber(ConcurrentCacheServiceTest.TEST_APP_TOKEN));
        Mockito.verify(appService, Mockito.times(0)).findAppByApplicationToken(ConcurrentCacheServiceTest.TEST_APP_TOKEN);
        Mockito.reset(appService);
    }

    @Test
    public void testConcurrentGetAppSeqNumberMultipleTimes() throws GetDeltaException {
        for (int i = 0; i < (ConcurrentCacheServiceTest.STRESS_TEST_INVOCATIONS); i++) {
            ConcurrentCacheServiceTest.launchCodeInParallelThreads(ConcurrentCacheServiceTest.STRESS_TEST_N_THREADS, new Runnable() {
                @Override
                public void run() {
                    Assert.assertEquals(new AppSeqNumber(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.TEST_APP_ID, ConcurrentCacheServiceTest.TEST_APP_TOKEN, ConcurrentCacheServiceTest.TEST_APP_SEQ_NUMBER), cacheService.getAppSeqNumber(ConcurrentCacheServiceTest.TEST_APP_TOKEN));
                }
            });
            Mockito.verify(appService, Mockito.atMost(2)).findAppByApplicationToken(ConcurrentCacheServiceTest.TEST_APP_TOKEN);
            Mockito.reset(appService);
        }
    }

    @Test
    public void testGetConfIdNumber() throws GetDeltaException {
        Assert.assertEquals(ConcurrentCacheServiceTest.CF1_ID, cacheService.getConfIdByKey(ConcurrentCacheServiceTest.TEST_CONF_ID_KEY));
        Mockito.verify(configurationService, Mockito.times(1)).findConfigurationsByEndpointGroupId(ConcurrentCacheServiceTest.ENDPOINT_GROUP1_ID);
        Mockito.reset(configurationService);
        Assert.assertEquals(ConcurrentCacheServiceTest.CF1_ID, cacheService.getConfIdByKey(ConcurrentCacheServiceTest.TEST_CONF_ID_KEY));
        Mockito.verify(configurationService, Mockito.times(0)).findConfigurationsByEndpointGroupId(ConcurrentCacheServiceTest.ENDPOINT_GROUP1_ID);
        Mockito.reset(configurationService);
    }

    @Test
    public void testGetAppTokenBySdkToken() {
        Assert.assertEquals(ConcurrentCacheServiceTest.TEST_APP_TOKEN, cacheService.getAppTokenBySdkToken(ConcurrentCacheServiceTest.TEST_SDK_TOKEN));
        Mockito.verify(sdkProfileService, Mockito.times(1)).findSdkProfileByToken(ConcurrentCacheServiceTest.TEST_SDK_TOKEN);
        Mockito.reset(sdkProfileService);
        Assert.assertEquals(ConcurrentCacheServiceTest.TEST_APP_TOKEN, cacheService.getAppTokenBySdkToken(ConcurrentCacheServiceTest.TEST_SDK_TOKEN));
        Mockito.verify(sdkProfileService, Mockito.never()).findSdkProfileByToken(ConcurrentCacheServiceTest.TEST_SDK_TOKEN);
        Mockito.reset(sdkProfileService);
    }

    @Test
    public void testConcurrentGetConfIdMultipleTimes() throws GetDeltaException {
        for (int i = 0; i < (ConcurrentCacheServiceTest.STRESS_TEST_INVOCATIONS); i++) {
            ConcurrentCacheServiceTest.launchCodeInParallelThreads(ConcurrentCacheServiceTest.STRESS_TEST_N_THREADS, new Runnable() {
                @Override
                public void run() {
                    Assert.assertEquals(ConcurrentCacheServiceTest.CF1_ID, cacheService.getConfIdByKey(ConcurrentCacheServiceTest.TEST_CONF_ID_KEY));
                }
            });
            Mockito.verify(configurationService, Mockito.atMost(2)).findConfigurationsByEndpointGroupId(ConcurrentCacheServiceTest.ENDPOINT_GROUP1_ID);
            Mockito.reset(configurationService);
        }
    }

    @Test
    public void testGetHistory() throws GetDeltaException {
        List<HistoryDto> expectedList = getResultHistoryList();
        Assert.assertEquals(expectedList, cacheService.getHistory(ConcurrentCacheServiceTest.TEST_HISTORY_KEY));
        Mockito.verify(historyService, Mockito.times(1)).findHistoriesBySeqNumberRange(ConcurrentCacheServiceTest.APP_ID, ConcurrentCacheServiceTest.TEST_APP_SEQ_NUMBER, ConcurrentCacheServiceTest.TEST_APP_SEQ_NUMBER_NEW);
        Mockito.reset(historyService);
        Assert.assertEquals(expectedList, cacheService.getHistory(ConcurrentCacheServiceTest.TEST_HISTORY_KEY));
        Mockito.verify(historyService, Mockito.times(0)).findHistoriesBySeqNumberRange(ConcurrentCacheServiceTest.APP_ID, ConcurrentCacheServiceTest.TEST_APP_SEQ_NUMBER, ConcurrentCacheServiceTest.TEST_APP_SEQ_NUMBER_NEW);
        Mockito.reset(historyService);
    }

    @Test
    public void testConcurrentGetHistoryMultipleTimes() throws GetDeltaException {
        final List<HistoryDto> expectedList = getResultHistoryList();
        for (int i = 0; i < (ConcurrentCacheServiceTest.STRESS_TEST_INVOCATIONS); i++) {
            ConcurrentCacheServiceTest.launchCodeInParallelThreads(ConcurrentCacheServiceTest.STRESS_TEST_N_THREADS, new Runnable() {
                @Override
                public void run() {
                    Assert.assertEquals(expectedList, cacheService.getHistory(ConcurrentCacheServiceTest.TEST_HISTORY_KEY));
                }
            });
            Mockito.verify(historyService, Mockito.atMost(2)).findHistoriesBySeqNumberRange(ConcurrentCacheServiceTest.APP_ID, ConcurrentCacheServiceTest.TEST_APP_SEQ_NUMBER, ConcurrentCacheServiceTest.TEST_APP_SEQ_NUMBER_NEW);
            Mockito.reset(historyService);
        }
    }

    @Test
    public void testGetFilters() throws GetDeltaException {
        Assert.assertEquals(ConcurrentCacheServiceTest.TEST_PROFILE_FILTER_LIST, cacheService.getFilters(ConcurrentCacheServiceTest.TEST_GET_PROFILES_KEY));
        Mockito.verify(profileService, Mockito.times(1)).findProfileFiltersByAppIdAndVersionsCombination(ConcurrentCacheServiceTest.APP_ID, ConcurrentCacheServiceTest.TEST_GET_PROFILES_KEY.getEndpointProfileSchemaVersion(), ConcurrentCacheServiceTest.TEST_GET_PROFILES_KEY.getServerProfileSchemaVersion());
        Mockito.reset(profileService);
        Assert.assertEquals(ConcurrentCacheServiceTest.TEST_PROFILE_FILTER_LIST, cacheService.getFilters(ConcurrentCacheServiceTest.TEST_GET_PROFILES_KEY));
        Mockito.verify(profileService, Mockito.times(0)).findProfileFiltersByAppIdAndVersionsCombination(ConcurrentCacheServiceTest.APP_ID, ConcurrentCacheServiceTest.TEST_GET_PROFILES_KEY.getEndpointProfileSchemaVersion(), ConcurrentCacheServiceTest.TEST_GET_PROFILES_KEY.getServerProfileSchemaVersion());
        Mockito.reset(profileService);
    }

    @Test
    public void testConcurrentGetFiltersMultipleTimes() throws GetDeltaException {
        for (int i = 0; i < (ConcurrentCacheServiceTest.STRESS_TEST_INVOCATIONS); i++) {
            ConcurrentCacheServiceTest.launchCodeInParallelThreads(ConcurrentCacheServiceTest.STRESS_TEST_N_THREADS, new Runnable() {
                @Override
                public void run() {
                    Assert.assertEquals(ConcurrentCacheServiceTest.TEST_PROFILE_FILTER_LIST, cacheService.getFilters(ConcurrentCacheServiceTest.TEST_GET_PROFILES_KEY));
                }
            });
            Mockito.verify(profileService, Mockito.atMost(2)).findProfileFiltersByAppIdAndVersionsCombination(ConcurrentCacheServiceTest.APP_ID, ConcurrentCacheServiceTest.TEST_GET_PROFILES_KEY.getEndpointProfileSchemaVersion(), ConcurrentCacheServiceTest.TEST_GET_PROFILES_KEY.getServerProfileSchemaVersion());
            Mockito.reset(profileService);
        }
    }

    @Test
    public void testGetFilter() throws GetDeltaException {
        Assert.assertEquals(ConcurrentCacheServiceTest.TEST_PROFILE_FILTER, cacheService.getFilter(ConcurrentCacheServiceTest.PF1_ID));
        Mockito.verify(profileService, Mockito.times(1)).findProfileFilterById(ConcurrentCacheServiceTest.PF1_ID);
        Mockito.reset(profileService);
        Assert.assertEquals(ConcurrentCacheServiceTest.TEST_PROFILE_FILTER, cacheService.getFilter(ConcurrentCacheServiceTest.PF1_ID));
        Mockito.verify(profileService, Mockito.times(0)).findProfileFilterById(ConcurrentCacheServiceTest.PF1_ID);
        Mockito.reset(profileService);
    }

    @Test
    public void testConcurrentGetFilterMultipleTimes() throws GetDeltaException {
        for (int i = 0; i < (ConcurrentCacheServiceTest.STRESS_TEST_INVOCATIONS); i++) {
            ConcurrentCacheServiceTest.launchCodeInParallelThreads(ConcurrentCacheServiceTest.STRESS_TEST_N_THREADS, new Runnable() {
                @Override
                public void run() {
                    Assert.assertEquals(ConcurrentCacheServiceTest.TEST_PROFILE_FILTER, cacheService.getFilter(ConcurrentCacheServiceTest.PF1_ID));
                }
            });
            Mockito.verify(profileService, Mockito.atMost(2)).findProfileFilterById(ConcurrentCacheServiceTest.PF1_ID);
            Mockito.reset(profileService);
        }
    }

    @Test
    public void testGetConf() throws GetDeltaException {
        Assert.assertEquals(ConcurrentCacheServiceTest.CF1, cacheService.getConfByHash(ConcurrentCacheServiceTest.CF1_HASH));
        Mockito.verify(endpointService, Mockito.times(1)).findEndpointConfigurationByHash(ConcurrentCacheServiceTest.CF1_HASH.getData());
        Mockito.reset(endpointService);
        Assert.assertEquals(ConcurrentCacheServiceTest.CF1, cacheService.getConfByHash(ConcurrentCacheServiceTest.CF1_HASH));
        Mockito.verify(endpointService, Mockito.times(0)).findEndpointConfigurationByHash(ConcurrentCacheServiceTest.CF1_HASH.getData());
        Mockito.reset(endpointService);
    }

    @Test
    public void testConcurrentGetConfMultipleTimes() throws GetDeltaException {
        for (int i = 0; i < (ConcurrentCacheServiceTest.STRESS_TEST_INVOCATIONS); i++) {
            ConcurrentCacheServiceTest.launchCodeInParallelThreads(ConcurrentCacheServiceTest.STRESS_TEST_N_THREADS, new Runnable() {
                @Override
                public void run() {
                    Assert.assertEquals(ConcurrentCacheServiceTest.CF1, cacheService.getConfByHash(ConcurrentCacheServiceTest.CF1_HASH));
                }
            });
            Mockito.verify(endpointService, Mockito.atMost(2)).findEndpointConfigurationByHash(ConcurrentCacheServiceTest.CF1_HASH.getData());
            Mockito.reset(endpointService);
        }
    }

    @Test
    public void testGetConfSchema() throws GetDeltaException {
        Assert.assertEquals(ConcurrentCacheServiceTest.CF1_SCHEMA, cacheService.getConfSchemaByAppAndVersion(ConcurrentCacheServiceTest.CF_SCHEMA_KEY));
        Mockito.verify(configurationService, Mockito.times(1)).findConfSchemaByAppIdAndVersion(ConcurrentCacheServiceTest.APP_ID, ConcurrentCacheServiceTest.CF_SCHEMA_KEY.getVersion());
        Mockito.reset(configurationService);
        Assert.assertEquals(ConcurrentCacheServiceTest.CF1_SCHEMA, cacheService.getConfSchemaByAppAndVersion(ConcurrentCacheServiceTest.CF_SCHEMA_KEY));
        Mockito.verify(configurationService, Mockito.times(0)).findConfSchemaByAppIdAndVersion(ConcurrentCacheServiceTest.APP_ID, ConcurrentCacheServiceTest.CF_SCHEMA_KEY.getVersion());
        Mockito.reset(configurationService);
    }

    @Test
    public void testConcurrentGetConfSchemaMultipleTimes() throws GetDeltaException {
        for (int i = 0; i < (ConcurrentCacheServiceTest.STRESS_TEST_INVOCATIONS); i++) {
            ConcurrentCacheServiceTest.launchCodeInParallelThreads(ConcurrentCacheServiceTest.STRESS_TEST_N_THREADS, new Runnable() {
                @Override
                public void run() {
                    Assert.assertEquals(ConcurrentCacheServiceTest.CF1_SCHEMA, cacheService.getConfSchemaByAppAndVersion(ConcurrentCacheServiceTest.CF_SCHEMA_KEY));
                }
            });
            Mockito.verify(configurationService, Mockito.atMost(2)).findConfigurationByAppIdAndVersion(ConcurrentCacheServiceTest.APP_ID, ConcurrentCacheServiceTest.CF_SCHEMA_KEY.getVersion());
            Mockito.reset(configurationService);
        }
    }

    @Test
    public void testGetProfileSchema() throws GetDeltaException {
        Assert.assertEquals(ConcurrentCacheServiceTest.PF1_SCHEMA, cacheService.getProfileSchemaByAppAndVersion(ConcurrentCacheServiceTest.PF_SCHEMA_KEY));
        Mockito.verify(profileService, Mockito.times(1)).findProfileSchemaByAppIdAndVersion(ConcurrentCacheServiceTest.APP_ID, ConcurrentCacheServiceTest.PF_SCHEMA_KEY.getVersion());
        Mockito.reset(profileService);
        Assert.assertEquals(ConcurrentCacheServiceTest.PF1_SCHEMA, cacheService.getProfileSchemaByAppAndVersion(ConcurrentCacheServiceTest.PF_SCHEMA_KEY));
        Mockito.verify(profileService, Mockito.times(0)).findProfileSchemaByAppIdAndVersion(ConcurrentCacheServiceTest.APP_ID, ConcurrentCacheServiceTest.PF_SCHEMA_KEY.getVersion());
        Mockito.reset(profileService);
    }

    @Test
    public void testGetSdkProperties() {
        Assert.assertEquals(ConcurrentCacheServiceTest.SDK_PROFILE, cacheService.getSdkProfileBySdkToken(ConcurrentCacheServiceTest.TEST_SDK_TOKEN));
        Mockito.verify(sdkProfileService, Mockito.times(1)).findSdkProfileByToken(ConcurrentCacheServiceTest.TEST_SDK_TOKEN);
        Mockito.reset(sdkProfileService);
        Assert.assertEquals(ConcurrentCacheServiceTest.SDK_PROFILE, cacheService.getSdkProfileBySdkToken(ConcurrentCacheServiceTest.TEST_SDK_TOKEN));
        Mockito.verify(sdkProfileService, Mockito.times(0)).findSdkProfileByToken(ConcurrentCacheServiceTest.TEST_SDK_TOKEN);
        Mockito.reset(sdkProfileService);
    }

    @Test
    public void testGetApplicationEventFamilyMapsByIds() {
        Assert.assertEquals(ConcurrentCacheServiceTest.AEFM_LIST, cacheService.getApplicationEventFamilyMapsByIds(ConcurrentCacheServiceTest.AEFMAP_IDS));
        Mockito.verify(applicationEventMapService, Mockito.times(1)).findApplicationEventFamilyMapsByIds(ConcurrentCacheServiceTest.AEFMAP_IDS);
        Mockito.reset(applicationEventMapService);
        Assert.assertEquals(ConcurrentCacheServiceTest.AEFM_LIST, cacheService.getApplicationEventFamilyMapsByIds(ConcurrentCacheServiceTest.AEFMAP_IDS));
        Mockito.verify(applicationEventMapService, Mockito.times(0)).findApplicationEventFamilyMapsByIds(ConcurrentCacheServiceTest.AEFMAP_IDS);
        Mockito.reset(applicationEventMapService);
    }

    @Test
    public void testConcurrentGetProfileSchemaMultipleTimes() throws GetDeltaException {
        for (int i = 0; i < (ConcurrentCacheServiceTest.STRESS_TEST_INVOCATIONS); i++) {
            ConcurrentCacheServiceTest.launchCodeInParallelThreads(ConcurrentCacheServiceTest.STRESS_TEST_N_THREADS, new Runnable() {
                @Override
                public void run() {
                    Assert.assertEquals(ConcurrentCacheServiceTest.PF1_SCHEMA, cacheService.getProfileSchemaByAppAndVersion(ConcurrentCacheServiceTest.PF_SCHEMA_KEY));
                }
            });
            Mockito.verify(profileService, Mockito.atMost(2)).findProfileSchemaByAppIdAndVersion(ConcurrentCacheServiceTest.APP_ID, ConcurrentCacheServiceTest.PF_SCHEMA_KEY.getVersion());
            Mockito.reset(profileService);
        }
    }

    @Test
    public void testGetEndpointKey() throws GetDeltaException {
        Assert.assertEquals(publicKey, cacheService.getEndpointKey(publicKeyHash));
        Mockito.verify(endpointService, Mockito.times(1)).findEndpointProfileByKeyHash(publicKeyHash.getData());
        Mockito.reset(endpointService);
        Assert.assertEquals(publicKey, cacheService.getEndpointKey(publicKeyHash));
        Mockito.verify(endpointService, Mockito.times(0)).findEndpointProfileByKeyHash(publicKeyHash.getData());
        Mockito.reset(endpointService);
        Assert.assertNull(cacheService.getEndpointKey(publicKeyHash2));
        Mockito.verify(endpointService, Mockito.times(1)).findEndpointProfileByKeyHash(publicKeyHash2.getData());
        Mockito.reset(endpointService);
        final EndpointProfileDto ep2 = new EndpointProfileDto();
        ep2.setEndpointKey(publicKey2.getEncoded());
        Mockito.when(endpointService.findEndpointProfileByKeyHash(publicKeyHash2.getData())).then(new Answer<EndpointProfileDto>() {
            @Override
            public EndpointProfileDto answer(InvocationOnMock invocation) throws Throwable {
                sleepABit();
                return ep2;
            }
        });
        cacheService.putEndpointKey(publicKeyHash2, publicKey2);
        Assert.assertEquals(publicKey2, cacheService.getEndpointKey(publicKeyHash2));
        Mockito.verify(endpointService, Mockito.times(0)).findEndpointProfileByKeyHash(publicKeyHash2.getData());
        Mockito.reset(endpointService);
    }

    @Test
    public void testConcurrentGetEndpointMultipleTimes() throws GetDeltaException {
        for (int i = 0; i < (ConcurrentCacheServiceTest.STRESS_TEST_INVOCATIONS); i++) {
            ConcurrentCacheServiceTest.launchCodeInParallelThreads(ConcurrentCacheServiceTest.STRESS_TEST_N_THREADS, new Runnable() {
                @Override
                public void run() {
                    Assert.assertEquals(publicKey, cacheService.getEndpointKey(publicKeyHash));
                }
            });
            Mockito.verify(endpointService, Mockito.atMost(2)).findEndpointProfileByKeyHash(publicKeyHash.getData());
            Mockito.reset(endpointService);
        }
    }

    @Test
    public void testGetEcfIdByName() throws GetDeltaException {
        Assert.assertEquals(ConcurrentCacheServiceTest.EVENT_CLASS_FAMILY_ID, cacheService.getEventClassFamilyIdByName(new EventClassFamilyIdKey(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.ECF_NAME)));
        Mockito.verify(eventClassService, Mockito.times(1)).findEventClassFamilyByTenantIdAndName(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.ECF_NAME);
        Mockito.reset(eventClassService);
        Assert.assertEquals(ConcurrentCacheServiceTest.EVENT_CLASS_FAMILY_ID, cacheService.getEventClassFamilyIdByName(new EventClassFamilyIdKey(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.ECF_NAME)));
        Mockito.verify(eventClassService, Mockito.times(0)).findEventClassFamilyByTenantIdAndName(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.ECF_NAME);
        Mockito.reset(eventClassService);
    }

    @Test
    public void testGetEcfIdByFqn() throws GetDeltaException {
        Assert.assertEquals(ConcurrentCacheServiceTest.EVENT_CLASS_FAMILY_ID, cacheService.getEventClassFamilyIdByEventClassFqn(new EventClassFqnKey(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.EC_FQN)));
        Mockito.verify(eventClassService, Mockito.times(1)).findEventClassByTenantIdAndFqn(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.EC_FQN);
        Mockito.reset(eventClassService);
        Assert.assertEquals(ConcurrentCacheServiceTest.EVENT_CLASS_FAMILY_ID, cacheService.getEventClassFamilyIdByEventClassFqn(new EventClassFqnKey(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.EC_FQN)));
        Mockito.verify(eventClassService, Mockito.times(0)).findEventClassByTenantIdAndFqn(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.EC_FQN);
        Mockito.reset(eventClassService);
    }

    @Test
    public void testGetRouteKeys() throws GetDeltaException {
        Assert.assertEquals(Collections.singleton(new org.kaaproject.kaa.server.operations.service.event.RouteTableKey(ConcurrentCacheServiceTest.TEST_APP_TOKEN, new EventClassFamilyVersion(ConcurrentCacheServiceTest.EVENT_CLASS_FAMILY_ID, ConcurrentCacheServiceTest.EVENT_CLASS_FAMILY_VERSION))), cacheService.getRouteKeys(new EventClassFqnVersion(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.EC_FQN, ConcurrentCacheServiceTest.EVENT_CLASS_FAMILY_VERSION)));
        Mockito.verify(eventClassService, Mockito.times(1)).findEventClassByTenantIdAndFqnAndVersion(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.EC_FQN, ConcurrentCacheServiceTest.EVENT_CLASS_FAMILY_VERSION);
        Mockito.reset(eventClassService);
        Assert.assertEquals(Collections.singleton(new org.kaaproject.kaa.server.operations.service.event.RouteTableKey(ConcurrentCacheServiceTest.TEST_APP_TOKEN, new EventClassFamilyVersion(ConcurrentCacheServiceTest.EVENT_CLASS_FAMILY_ID, ConcurrentCacheServiceTest.EVENT_CLASS_FAMILY_VERSION))), cacheService.getRouteKeys(new EventClassFqnVersion(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.EC_FQN, ConcurrentCacheServiceTest.EVENT_CLASS_FAMILY_VERSION)));
        Mockito.verify(eventClassService, Mockito.times(0)).findEventClassByTenantIdAndFqnAndVersion(ConcurrentCacheServiceTest.TENANT_ID, ConcurrentCacheServiceTest.EC_FQN, ConcurrentCacheServiceTest.EVENT_CLASS_FAMILY_VERSION);
        Mockito.reset(eventClassService);
    }

    @Test
    public void testIsSupported() {
        for (ChangeType change : ChangeType.values()) {
            switch (change) {
                case ADD_CONF :
                case ADD_PROF :
                case ADD_TOPIC :
                case REMOVE_CONF :
                case REMOVE_PROF :
                case REMOVE_TOPIC :
                case REMOVE_GROUP :
                    Assert.assertTrue(ConcurrentCacheService.isSupported(change));
                    break;
                default :
                    Assert.assertFalse(ConcurrentCacheService.isSupported(change));
                    break;
            }
        }
    }

    @Test
    public void testGetTopicListByHash() throws GetDeltaException {
        TopicListCacheEntry entry = cacheService.getTopicListByHash(ConcurrentCacheServiceTest.CF1_HASH);
        Assert.assertNotNull(entry);
        Mockito.verify(endpointService, Mockito.times(1)).findTopicListEntryByHash(ConcurrentCacheServiceTest.CF1_HASH.getData());
        Mockito.reset(endpointService);
        entry = cacheService.getTopicListByHash(ConcurrentCacheServiceTest.CF1_HASH);
        Assert.assertNotNull(entry);
        Mockito.verify(endpointService, Mockito.times(0)).findTopicListEntryByHash(ConcurrentCacheServiceTest.CF1_HASH.getData());
        Mockito.reset(endpointService);
    }
}

