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
package org.kaaproject.kaa.server.operations.service.history;


import ChangeType.ADD_CONF;
import ChangeType.ADD_PROF;
import ChangeType.ADD_TOPIC;
import ChangeType.REMOVE_CONF;
import ChangeType.REMOVE_GROUP;
import ChangeType.REMOVE_PROF;
import ChangeType.REMOVE_TOPIC;
import ChangeType.UPDATE;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.EndpointGroupDto;
import org.kaaproject.kaa.common.dto.EndpointGroupStateDto;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;
import org.kaaproject.kaa.common.dto.ProfileFilterDto;
import org.kaaproject.kaa.server.operations.service.cache.AppProfileVersionsKey;
import org.kaaproject.kaa.server.operations.service.cache.CacheService;
import org.kaaproject.kaa.server.operations.service.cache.ConfigurationIdKey;
import org.kaaproject.kaa.server.operations.service.cache.HistoryKey;
import org.kaaproject.kaa.server.operations.service.delta.HistoryDelta;
import org.kaaproject.kaa.server.operations.service.filter.FilterService;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DefaultHistoryDeltaServiceTest {
    private static final String CLIENT_PROFILE_BODY = "client_dummy";

    private static final String SERVER_PROFILE_BODY = "server_dummy";

    private static final int PROFILE_VERSION = 3;

    private static final int CONF_VERSION = 2;

    private static final String APP1_ID = "APP1_ID";

    private static final String APP1_TOKEN = "APP1_TOKEN";

    private static final String EG_DEFAULT = "EG_DEFAULT";

    private static final String EG1_ID = "EG1_ID";

    private static final String EG2_ID = "EG2_ID";

    private static final String PF1_ID = "PF1_ID";

    private static final String PF2_ID = "PF2_ID";

    private static final String CF1_ID = "CF1_ID";

    private static final String CF2_ID = "CF2_ID";

    private static final String CACHE_SERVICE = "cacheService";

    private static final String FILTER_SERVICE = "filterService";

    private HistoryDeltaService historyDeltaService;

    private CacheService cacheService;

    private FilterService filterService;

    private EndpointProfileDto profile;

    @Test
    public void testInitial() {
        List<ProfileFilterDto> allFilters = new ArrayList<>();
        ProfileFilterDto filter = new ProfileFilterDto();
        filter.setApplicationId(DefaultHistoryDeltaServiceTest.APP1_ID);
        filter.setEndpointGroupId(DefaultHistoryDeltaServiceTest.EG1_ID);
        filter.setId(DefaultHistoryDeltaServiceTest.PF1_ID);
        allFilters.add(filter);
        Mockito.when(filterService.getAllMatchingFilters(Mockito.any(AppProfileVersionsKey.class), Mockito.any(EndpointProfileDto.class))).thenReturn(allFilters);
        Mockito.when(cacheService.getConfIdByKey(Mockito.any(ConfigurationIdKey.class))).thenReturn(DefaultHistoryDeltaServiceTest.CF1_ID);
        EndpointGroupDto egDto = new EndpointGroupDto();
        egDto.setId(DefaultHistoryDeltaServiceTest.EG_DEFAULT);
        Mockito.when(cacheService.getDefaultGroup(Mockito.any(String.class))).thenReturn(egDto);
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 0);
        Assert.assertTrue(historyDelta.isConfigurationChanged());
        Assert.assertTrue(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(2, historyDelta.getEndpointGroupStates().size());
        EndpointGroupStateDto egs = historyDelta.getEndpointGroupStates().get(1);
        Assert.assertNotNull(egs);
        Assert.assertEquals(DefaultHistoryDeltaServiceTest.CF1_ID, egs.getConfigurationId());
        Assert.assertEquals(DefaultHistoryDeltaServiceTest.PF1_ID, egs.getProfileFilterId());
        Assert.assertEquals(DefaultHistoryDeltaServiceTest.EG1_ID, egs.getEndpointGroupId());
    }

    @Test
    public void testDeltaNoChanges() {
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 101);
        Assert.assertNotNull(historyDelta);
        Assert.assertFalse(historyDelta.isConfigurationChanged());
        Assert.assertFalse(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(0, historyDelta.getEndpointGroupStates().size());
    }

    @Test
    public void testDeltaOldGroupRemove() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(REMOVE_GROUP, DefaultHistoryDeltaServiceTest.EG1_ID)));
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertTrue(historyDelta.isConfigurationChanged());
        Assert.assertTrue(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(0, historyDelta.getEndpointGroupStates().size());
    }

    @Test
    public void testDeltaNewGroupRemove() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(REMOVE_GROUP, DefaultHistoryDeltaServiceTest.EG2_ID)));
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertFalse(historyDelta.isConfigurationChanged());
        Assert.assertFalse(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(1, historyDelta.getEndpointGroupStates().size());
    }

    @Test
    public void testDeltaOldGroupAddTopic() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(ADD_TOPIC, DefaultHistoryDeltaServiceTest.EG1_ID)));
        Mockito.when(filterService.matches(Mockito.anyString(), Mockito.anyString(), Mockito.any(EndpointProfileDto.class))).thenReturn(true);
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertFalse(historyDelta.isConfigurationChanged());
        Assert.assertTrue(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(1, historyDelta.getEndpointGroupStates().size());
    }

    @Test
    public void testDeltaOldGroupRemoveTopic() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(REMOVE_TOPIC, DefaultHistoryDeltaServiceTest.EG1_ID)));
        Mockito.when(filterService.matches(Mockito.anyString(), Mockito.anyString(), Mockito.any(EndpointProfileDto.class))).thenReturn(true);
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertFalse(historyDelta.isConfigurationChanged());
        Assert.assertTrue(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(1, historyDelta.getEndpointGroupStates().size());
    }

    @Test
    public void testDeltaOldGroupAddConf() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(ADD_CONF, DefaultHistoryDeltaServiceTest.EG1_ID)));
        Mockito.when(filterService.matches(Mockito.anyString(), Mockito.anyString(), Mockito.any(EndpointProfileDto.class))).thenReturn(true);
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertTrue(historyDelta.isConfigurationChanged());
        Assert.assertFalse(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(1, historyDelta.getEndpointGroupStates().size());
    }

    @Test
    public void testDeltaOldGroupRemoveConf() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(REMOVE_CONF, DefaultHistoryDeltaServiceTest.EG1_ID)));
        Mockito.when(filterService.matches(Mockito.anyString(), Mockito.anyString(), Mockito.any(EndpointProfileDto.class))).thenReturn(true);
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertTrue(historyDelta.isConfigurationChanged());
        Assert.assertFalse(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(1, historyDelta.getEndpointGroupStates().size());
    }

    @Test
    public void testDeltaOldGroupRemoveProf() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(REMOVE_PROF, DefaultHistoryDeltaServiceTest.EG1_ID)));
        Mockito.when(filterService.matches(Mockito.anyString(), Mockito.anyString(), Mockito.any(EndpointProfileDto.class))).thenReturn(true);
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertTrue(historyDelta.isConfigurationChanged());
        Assert.assertTrue(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(0, historyDelta.getEndpointGroupStates().size());
    }

    @Test
    public void testDeltaOldGroupAddMatchingProf() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(ADD_PROF, DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF2_ID)));
        Mockito.when(filterService.matches(Mockito.anyString(), Mockito.anyString(), Mockito.any(EndpointProfileDto.class))).thenReturn(true);
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertFalse(historyDelta.isConfigurationChanged());
        Assert.assertFalse(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(1, historyDelta.getEndpointGroupStates().size());
        Assert.assertEquals(DefaultHistoryDeltaServiceTest.PF2_ID, historyDelta.getEndpointGroupStates().get(0).getProfileFilterId());
    }

    @Test
    public void testDeltaOldGroupAddNotMatchingProf() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(ADD_PROF, DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF2_ID)));
        Mockito.when(filterService.matches(Mockito.anyString(), Mockito.anyString(), Mockito.any(EndpointProfileDto.class))).thenReturn(false);
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertTrue(historyDelta.isConfigurationChanged());
        Assert.assertTrue(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(0, historyDelta.getEndpointGroupStates().size());
    }

    @Test
    public void testDeltaOldGroupWrongChange() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(UPDATE, DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF2_ID)));
        Mockito.when(filterService.matches(Mockito.anyString(), Mockito.anyString(), Mockito.any(EndpointProfileDto.class))).thenReturn(false);
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertFalse(historyDelta.isConfigurationChanged());
        Assert.assertFalse(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(1, historyDelta.getEndpointGroupStates().size());
    }

    @Test
    public void testDeltaNewGroupAddTopic() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(ADD_TOPIC, DefaultHistoryDeltaServiceTest.EG2_ID)));
        Mockito.when(filterService.matches(Mockito.anyString(), Mockito.anyString(), Mockito.any(EndpointProfileDto.class))).thenReturn(true);
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertFalse(historyDelta.isConfigurationChanged());
        Assert.assertFalse(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(1, historyDelta.getEndpointGroupStates().size());
    }

    @Test
    public void testDeltaNewGroupRemoveTopic() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(REMOVE_TOPIC, DefaultHistoryDeltaServiceTest.EG2_ID)));
        Mockito.when(filterService.matches(Mockito.anyString(), Mockito.anyString(), Mockito.any(EndpointProfileDto.class))).thenReturn(true);
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertFalse(historyDelta.isConfigurationChanged());
        Assert.assertFalse(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(1, historyDelta.getEndpointGroupStates().size());
    }

    @Test
    public void testDeltaNewGroupMatchingPFWithoutCF() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(ADD_PROF, DefaultHistoryDeltaServiceTest.EG2_ID, DefaultHistoryDeltaServiceTest.PF2_ID)));
        Mockito.when(filterService.matches(Mockito.anyString(), Mockito.anyString(), Mockito.any(EndpointProfileDto.class))).thenReturn(true);
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertTrue(historyDelta.isConfigurationChanged());
        Assert.assertTrue(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(2, historyDelta.getEndpointGroupStates().size());
    }

    @Test
    public void testDeltaNewGroupMatchingPFWithCf() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(ADD_PROF, DefaultHistoryDeltaServiceTest.EG2_ID, DefaultHistoryDeltaServiceTest.PF2_ID)));
        Mockito.when(filterService.matches(Mockito.anyString(), Mockito.anyString(), Mockito.any(EndpointProfileDto.class))).thenReturn(true);
        Mockito.when(cacheService.getConfIdByKey(Mockito.any(ConfigurationIdKey.class))).thenReturn(DefaultHistoryDeltaServiceTest.CF2_ID);
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertTrue(historyDelta.isConfigurationChanged());
        Assert.assertTrue(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(2, historyDelta.getEndpointGroupStates().size());
        Assert.assertEquals(DefaultHistoryDeltaServiceTest.CF1_ID, historyDelta.getEndpointGroupStates().get(0).getConfigurationId());
        Assert.assertEquals(DefaultHistoryDeltaServiceTest.CF2_ID, historyDelta.getEndpointGroupStates().get(1).getConfigurationId());
    }

    @Test
    public void testDeltaNewGroupNotMatchingPF() {
        profile.setGroupState(toList(new EndpointGroupStateDto(DefaultHistoryDeltaServiceTest.EG1_ID, DefaultHistoryDeltaServiceTest.PF1_ID, DefaultHistoryDeltaServiceTest.CF1_ID)));
        Mockito.when(cacheService.getHistory(Mockito.any(HistoryKey.class))).thenReturn(toList(toDto(ADD_PROF, DefaultHistoryDeltaServiceTest.EG2_ID, DefaultHistoryDeltaServiceTest.PF2_ID)));
        Mockito.when(filterService.matches(Mockito.anyString(), Mockito.anyString(), Mockito.any(EndpointProfileDto.class))).thenReturn(false);
        HistoryDelta historyDelta = historyDeltaService.getDelta(profile, DefaultHistoryDeltaServiceTest.APP1_TOKEN, 101, 102);
        Assert.assertNotNull(historyDelta);
        Assert.assertFalse(historyDelta.isConfigurationChanged());
        Assert.assertFalse(historyDelta.isTopicListChanged());
        Assert.assertNotNull(historyDelta.getEndpointGroupStates());
        Assert.assertEquals(1, historyDelta.getEndpointGroupStates().size());
    }
}

