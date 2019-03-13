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


import TopicTypeDto.MANDATORY;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.kaaproject.kaa.common.dto.ApplicationDto;
import org.kaaproject.kaa.common.dto.EndpointGroupDto;
import org.kaaproject.kaa.common.dto.EndpointProfileBodyDto;
import org.kaaproject.kaa.common.dto.EndpointProfileDto;
import org.kaaproject.kaa.common.dto.EndpointProfilesBodyDto;
import org.kaaproject.kaa.common.dto.EndpointProfilesPageDto;
import org.kaaproject.kaa.common.dto.EndpointUserDto;
import org.kaaproject.kaa.common.dto.PageLinkDto;
import org.kaaproject.kaa.common.dto.TenantDto;
import org.kaaproject.kaa.common.dto.TopicDto;
import org.kaaproject.kaa.common.dto.TopicListEntryDto;
import org.kaaproject.kaa.server.common.dao.AbstractTest;
import org.kaaproject.kaa.server.common.dao.exception.IncorrectParameterException;
import org.kaaproject.kaa.server.common.dao.exception.KaaOptimisticLockingFailureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;


@Ignore("This test should be extended and initialized with proper context in each NoSQL submodule")
public class EndpointServiceImplTest extends AbstractTest {
    private static final Logger LOG = LoggerFactory.getLogger(EndpointServiceImplTest.class);

    private static final String INCORRECT_ID = "incorrect id";

    private static final String DEFAULT_LIMIT = "1";

    private static final String DEFAULT_OFFSET = "0";

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    public void findEndpointGroupsByAppIdTest() {
        EndpointGroupDto group = generateEndpointGroupDto(null);
        List<EndpointGroupDto> groups = endpointService.findEndpointGroupsByAppId(group.getApplicationId());
        Assert.assertNotNull(groups);
        Assert.assertFalse(groups.isEmpty());
        Assert.assertEquals(2, groups.size());
    }

    @Test(expected = IncorrectParameterException.class)
    public void findEndpointGroupByIdTest() {
        EndpointGroupDto group = generateEndpointGroupDto(null);
        EndpointGroupDto foundGroup = endpointService.findEndpointGroupById(group.getId());
        Assert.assertNotNull(foundGroup);
        group = endpointService.findEndpointGroupById(EndpointServiceImplTest.INCORRECT_ID);
    }

    @Test
    public void findEndpointProfileByEndpointGroupIdTest() {
        EndpointGroupDto group = generateEndpointGroupDto(null);
        String endpointGroupId = group.getId();
        PageLinkDto pageLinkDto = new PageLinkDto(endpointGroupId, EndpointServiceImplTest.DEFAULT_LIMIT, EndpointServiceImplTest.DEFAULT_OFFSET);
        EndpointProfileDto savedEndpointProfileDto = generateEndpointProfileWithGroupIdDto(endpointGroupId);
        EndpointProfilesPageDto endpointProfilesPage = endpointService.findEndpointProfileByEndpointGroupId(pageLinkDto);
        EndpointProfileDto endpointProfileDto = endpointProfilesPage.getEndpointProfiles().get(0);
        Assert.assertEquals(savedEndpointProfileDto, endpointProfileDto);
    }

    @Test
    public void findEndpointProfileBodyByEndpointGroupIdTest() {
        EndpointGroupDto group = generateEndpointGroupDto(null);
        String endpointGroupId = group.getId();
        PageLinkDto pageLinkDto = new PageLinkDto(endpointGroupId, EndpointServiceImplTest.DEFAULT_LIMIT, EndpointServiceImplTest.DEFAULT_OFFSET);
        EndpointProfileDto savedEndpointProfileDto = generateEndpointProfileWithGroupIdDto(endpointGroupId);
        EndpointProfilesBodyDto endpointProfilesPage = endpointService.findEndpointProfileBodyByEndpointGroupId(pageLinkDto);
        EndpointProfileBodyDto endpointProfileBodyDto = endpointProfilesPage.getEndpointProfilesBody().get(0);
        Assert.assertEquals(savedEndpointProfileDto.getClientProfileBody(), endpointProfileBodyDto.getClientSideProfile());
    }

    @Test
    public void findEndpointProfileByKeyHashTest() {
        String endpointGroupId = "124";
        EndpointProfileDto savedEndpointProfileDto = generateEndpointProfileWithGroupIdDto(endpointGroupId);
        EndpointProfileDto endpointProfileDto = endpointService.findEndpointProfileByKeyHash(savedEndpointProfileDto.getEndpointKeyHash());
        Assert.assertEquals(savedEndpointProfileDto, endpointProfileDto);
    }

    @Test
    public void findEndpointProfileBodyByKeyHashTest() {
        String endpointGroupId = "124";
        EndpointProfileDto savedEndpointProfileDto = generateEndpointProfileWithGroupIdDto(endpointGroupId);
        EndpointProfileBodyDto endpointProfileBodyDto = endpointService.findEndpointProfileBodyByKeyHash(savedEndpointProfileDto.getEndpointKeyHash());
        Assert.assertEquals(savedEndpointProfileDto.getClientProfileBody(), endpointProfileBodyDto.getClientSideProfile());
    }

    @Test(expected = IncorrectParameterException.class)
    public void saveEndpointGroupWithSameWeightTest() {
        EndpointGroupDto group = generateEndpointGroupDto(null);
        EndpointGroupDto found = endpointService.findEndpointGroupById(group.getId());
        found.setId(null);
        endpointService.saveEndpointGroup(found);
    }

    @Test(expected = IncorrectParameterException.class)
    public void saveEndpointGroupWithSameNameTest() {
        EndpointGroupDto group = generateEndpointGroupDto(null);
        EndpointGroupDto found = endpointService.findEndpointGroupById(group.getId());
        found.setId(null);
        found.setWeight(((found.getWeight()) + 1));
        endpointService.saveEndpointGroup(found);
    }

    @Test
    public void removeEndpointGroupByAppIdTest() {
        String appId = generateApplicationDto().getId();
        List<EndpointGroupDto> groupDtoList = endpointService.findEndpointGroupsByAppId(appId);
        Assert.assertNotNull(groupDtoList);
        Assert.assertFalse(groupDtoList.isEmpty());
        endpointService.removeEndpointGroupByAppId(appId);
        groupDtoList = endpointService.findEndpointGroupsByAppId(appId);
        Assert.assertNotNull(groupDtoList);
        Assert.assertTrue(groupDtoList.isEmpty());
    }

    @Test(expected = IncorrectParameterException.class)
    public void invalidUpdateEndpointGroupTest() {
        ApplicationDto app = generateApplicationDto();
        List<EndpointGroupDto> groups = endpointService.findEndpointGroupsByAppId(app.getId());
        Assert.assertFalse(groups.isEmpty());
        EndpointGroupDto group = groups.get(0);
        group.setName("Updated Group Name");
        endpointService.saveEndpointGroup(group);
    }

    @Test(expected = IncorrectParameterException.class)
    public void saveEndpointGroupWithExistingWeightTest() {
        ApplicationDto app = generateApplicationDto();
        List<EndpointGroupDto> groups = endpointService.findEndpointGroupsByAppId(app.getId());
        Assert.assertFalse(groups.isEmpty());
        EndpointGroupDto group = groups.get(0);
        group.setId(null);
        group.setName("Updated Group Name");
        endpointService.saveEndpointGroup(group);
    }

    @Test(expected = IncorrectParameterException.class)
    public void saveEndpointGroupWithExistingNameTest() {
        ApplicationDto app = generateApplicationDto();
        List<EndpointGroupDto> groups = endpointService.findEndpointGroupsByAppId(app.getId());
        Assert.assertFalse(groups.isEmpty());
        EndpointGroupDto group = groups.get(0);
        group.setId(null);
        group.setWeight(((group.getWeight()) + 1));
        endpointService.saveEndpointGroup(group);
    }

    @Test
    public void findAllEndpointUsersTest() {
        removeAllEndpointUsers();
        TenantDto tenantDto = generateTenantDto();
        EndpointUserDto endpointUserDto = generateEndpointUserDto(tenantDto.getId());
        List<EndpointUserDto> saved = new ArrayList<>(1);
        saved.add(endpointUserDto);
        List<EndpointUserDto> endpointUsers = endpointService.findAllEndpointUsers();
        Assert.assertEquals(saved, endpointUsers);
    }

    @Test
    public void findEndpointUserByIdTest() {
        TenantDto tenantDto = generateTenantDto();
        EndpointUserDto savedEndpointUserDto = generateEndpointUserDto(tenantDto.getId());
        EndpointUserDto endpointUser = endpointService.findEndpointUserById(savedEndpointUserDto.getId());
        Assert.assertEquals(savedEndpointUserDto, endpointUser);
    }

    @Test
    public void saveEndpointUserTest() {
        TenantDto tenantDto = generateTenantDto();
        EndpointUserDto savedEndpointUserDto = generateEndpointUserDto(tenantDto.getId());
        EndpointUserDto endpointUser = endpointService.findEndpointUserById(savedEndpointUserDto.getId());
        Assert.assertEquals(savedEndpointUserDto, endpointUser);
    }

    @Test
    public void removeEndpointUserByIdTest() {
        TenantDto tenantDto = generateTenantDto();
        EndpointUserDto savedEndpointUserDto = generateEndpointUserDto(tenantDto.getId());
        endpointService.removeEndpointUserById(savedEndpointUserDto.getId());
        EndpointUserDto endpointUser = endpointService.findEndpointUserById(savedEndpointUserDto.getId());
        Assert.assertNull(endpointUser);
    }

    @Test
    public void generateEndpointUserAccessTokenTest() {
        TenantDto tenantDto = generateTenantDto();
        EndpointUserDto savedEndpointUserDto = generateEndpointUserDto(tenantDto.getId());
        Assert.assertNull(savedEndpointUserDto.getAccessToken());
        String generatedAccessToken = endpointService.generateEndpointUserAccessToken(savedEndpointUserDto.getExternalId(), savedEndpointUserDto.getTenantId());
        EndpointUserDto endpointUser = endpointService.findEndpointUserById(savedEndpointUserDto.getId());
        Assert.assertNotNull(generatedAccessToken);
        Assert.assertEquals(generatedAccessToken, endpointUser.getAccessToken());
    }

    @Test
    public void findTopicListEntryByHashTest() {
        ApplicationDto applicationDto = generateApplicationDto();
        TopicDto topicDto = generateTopicDto(applicationDto.getId(), MANDATORY);
        List<TopicDto> topics = Arrays.asList(topicDto);
        byte[] hash = "123".getBytes();
        TopicListEntryDto topicListEntryDto = new TopicListEntryDto(1, hash, topics);
        endpointService.saveTopicListEntry(topicListEntryDto);
        TopicListEntryDto foundTopicListEntry = endpointService.findTopicListEntryByHash(hash);
        Assert.assertEquals(topicListEntryDto, foundTopicListEntry);
    }

    @Test
    public void attachEndpointToUserTest() {
        TenantDto tenantDto = generateTenantDto();
        EndpointUserDto endpointUserDto = generateEndpointUserDto(tenantDto.getId());
        String endpointGroupId = "124";
        EndpointProfileDto endpointProfileDto = generateEndpointProfileWithGroupIdDto(endpointGroupId);
        String accessToken = "1111";
        endpointProfileDto.setAccessToken(accessToken);
        endpointService.saveEndpointProfile(endpointProfileDto);
        endpointService.attachEndpointToUser(endpointUserDto.getId(), accessToken);
        endpointUserDto = endpointService.findEndpointUserById(endpointUserDto.getId());
        List<String> endpointIds = endpointUserDto.getEndpointIds();
        Assert.assertNotNull(endpointIds);
        Assert.assertEquals(1, endpointIds.size());
        Assert.assertEquals(endpointProfileDto.getId(), endpointIds.get(0));
    }

    @Test
    public void attachEndpointToNewUserByExternalIdTest() {
        TenantDto tenant = generateTenantDto();
        String endpointGroupId = "124";
        EndpointProfileDto endpointProfile = generateEndpointProfileWithGroupIdDto(endpointGroupId);
        String userExternalId = UUID.randomUUID().toString();
        String tenantId = tenant.getId();
        EndpointProfileDto savedEndpointProfile = endpointService.attachEndpointToUser(userExternalId, tenantId, endpointProfile);
        Assert.assertNotNull(savedEndpointProfile);
        Assert.assertNotNull(savedEndpointProfile.getEndpointUserId());
        EndpointUserDto attachedEndpointUser = endpointService.findEndpointUserById(savedEndpointProfile.getEndpointUserId());
        Assert.assertNotNull(attachedEndpointUser);
        Assert.assertEquals(userExternalId, attachedEndpointUser.getExternalId());
        Assert.assertEquals(userExternalId, attachedEndpointUser.getUsername());
        Assert.assertEquals(tenantId, attachedEndpointUser.getTenantId());
        List<String> endpointIds = attachedEndpointUser.getEndpointIds();
        Assert.assertNotNull(endpointIds);
        Assert.assertEquals(1, endpointIds.size());
        Assert.assertEquals(endpointProfile.getId(), endpointIds.get(0));
    }

    @Test
    public void attachEndpointToExistingUserByExternalIdTest() {
        TenantDto tenant = generateTenantDto();
        String endpointGroupId = "124";
        EndpointProfileDto endpointProfile = generateEndpointProfileWithGroupIdDto(endpointGroupId);
        String tenantId = tenant.getId();
        EndpointUserDto endpointUserDto = generateEndpointUserDto(tenantId);
        String userExternalId = endpointUserDto.getExternalId();
        EndpointProfileDto savedEndpointProfile = endpointService.attachEndpointToUser(userExternalId, tenantId, endpointProfile);
        Assert.assertNotNull(savedEndpointProfile);
        Assert.assertNotNull(savedEndpointProfile.getEndpointUserId());
        EndpointUserDto attachedEndpointUser = endpointService.findEndpointUserById(savedEndpointProfile.getEndpointUserId());
        Assert.assertNotNull(attachedEndpointUser);
        Assert.assertEquals(userExternalId, attachedEndpointUser.getExternalId());
        Assert.assertEquals(tenantId, attachedEndpointUser.getTenantId());
        List<String> endpointIds = attachedEndpointUser.getEndpointIds();
        Assert.assertNotNull(endpointIds);
        Assert.assertEquals(1, endpointIds.size());
        Assert.assertEquals(endpointProfile.getId(), endpointIds.get(0));
    }

    @Test
    public void attachEndpointToAlreadyAttachedUserByExternalIdTest() {
        TenantDto tenant = generateTenantDto();
        String endpointGroupId = "124";
        EndpointProfileDto endpointProfile = generateEndpointProfileWithGroupIdDto(endpointGroupId);
        String tenantId = tenant.getId();
        EndpointUserDto endpointUserDto = generateEndpointUserDto(tenantId);
        String userExternalId = endpointUserDto.getExternalId();
        EndpointProfileDto savedEndpointProfile = endpointService.attachEndpointToUser(userExternalId, tenantId, endpointProfile);
        savedEndpointProfile = endpointService.attachEndpointToUser(userExternalId, tenantId, savedEndpointProfile);
        Assert.assertNotNull(savedEndpointProfile);
        Assert.assertNotNull(savedEndpointProfile.getEndpointUserId());
        EndpointUserDto attachedEndpointUser = endpointService.findEndpointUserById(savedEndpointProfile.getEndpointUserId());
        Assert.assertNotNull(attachedEndpointUser);
        Assert.assertEquals(userExternalId, attachedEndpointUser.getExternalId());
        Assert.assertEquals(tenantId, attachedEndpointUser.getTenantId());
        List<String> endpointIds = attachedEndpointUser.getEndpointIds();
        Assert.assertNotNull(endpointIds);
        Assert.assertEquals(1, endpointIds.size());
        Assert.assertEquals(endpointProfile.getId(), endpointIds.get(0));
    }

    @Test
    public void attachEndpointAlreadyAttachedToAnotherUserByExternalIdTest() {
        TenantDto tenant = generateTenantDto();
        String endpointGroupId = "124";
        EndpointProfileDto endpointProfile = generateEndpointProfileWithGroupIdDto(endpointGroupId);
        String tenantId = tenant.getId();
        EndpointUserDto endpointUser1 = generateEndpointUserDto(tenantId);
        String userExternalId1 = endpointUser1.getExternalId();
        EndpointUserDto endpointUser2 = generateEndpointUserDto(tenantId);
        String userExternalId2 = endpointUser2.getExternalId();
        EndpointProfileDto savedEndpointProfile = endpointService.attachEndpointToUser(userExternalId1, tenantId, endpointProfile);
        savedEndpointProfile = endpointService.attachEndpointToUser(userExternalId2, tenantId, savedEndpointProfile);
        EndpointUserDto attachedUser = endpointService.findEndpointUserById(savedEndpointProfile.getEndpointUserId());
        endpointUser1 = endpointService.findEndpointUserByExternalIdAndTenantId(userExternalId1, tenantId);
        endpointUser2 = endpointService.findEndpointUserByExternalIdAndTenantId(userExternalId2, tenantId);
        Assert.assertNotNull(attachedUser);
        Assert.assertEquals(endpointUser2, attachedUser);
        List<String> user1EndpointIds = endpointUser1.getEndpointIds();
        List<String> user2EndpointIds = endpointUser2.getEndpointIds();
        Assert.assertTrue(CollectionUtils.isEmpty(user1EndpointIds));
        Assert.assertFalse(CollectionUtils.isEmpty(user2EndpointIds));
        Assert.assertEquals(1, user2EndpointIds.size());
        Assert.assertEquals(endpointProfile.getId(), user2EndpointIds.get(0));
        Assert.assertEquals(endpointUser2.getId(), savedEndpointProfile.getEndpointUserId());
    }

    @Test
    public void findEndpointProfilesByExternalUserIdTest() {
        TenantDto tenantDto = generateTenantDto();
        EndpointUserDto endpointUserDto = generateEndpointUserDto(tenantDto.getId());
        String endpointGroupId = "124";
        String accessToken1 = "1111";
        String accessToken2 = "2222";
        EndpointProfileDto endpointProfileDto1 = generateEndpointProfileWithGroupIdDto(endpointGroupId);
        endpointProfileDto1.setAccessToken(accessToken1);
        EndpointProfileDto endpointProfileDto2 = generateEndpointProfileWithGroupIdDto(endpointGroupId);
        endpointProfileDto2.setAccessToken(accessToken2);
        endpointProfileDto1 = endpointService.saveEndpointProfile(endpointProfileDto1);
        endpointProfileDto2 = endpointService.saveEndpointProfile(endpointProfileDto2);
        endpointService.attachEndpointToUser(endpointUserDto.getId(), accessToken1);
        endpointService.attachEndpointToUser(endpointUserDto.getId(), accessToken2);
        List<EndpointProfileDto> endpointProfiles = endpointService.findEndpointProfilesByExternalIdAndTenantId(endpointUserDto.getExternalId(), tenantDto.getId());
        Assert.assertEquals(2, endpointProfiles.size());
        Comparator<EndpointProfileDto> endpointProfilesComparator = new Comparator<EndpointProfileDto>() {
            @Override
            public int compare(EndpointProfileDto o1, EndpointProfileDto o2) {
                return o1.getId().compareTo(o2.getId());
            }
        };
        List<EndpointProfileDto> expected = Arrays.asList(endpointProfileDto1, endpointProfileDto2);
        Collections.sort(expected, endpointProfilesComparator);
        Collections.sort(endpointProfiles, endpointProfilesComparator);
        Assert.assertEquals(expected, endpointProfiles);
    }

    @Test(expected = KaaOptimisticLockingFailureException.class)
    public void createDuplicateProfileTest() {
        EndpointProfileDto endpointProfileDto = generateEndpointProfileWithGroupIdDto("124");
        endpointProfileDto.setVersion(null);
        endpointService.saveEndpointProfile(endpointProfileDto);
    }

    @Test
    public void multiThreadAttachDetachEndpointToUserTest() throws InterruptedException, ExecutionException {
        TenantDto tenantDto = generateTenantDto();
        EndpointUserDto endpointUserDto = generateEndpointUserDto(tenantDto.getId());
        String endpointGroupId = "124";
        List<String> accessTokens = new ArrayList<>();
        List<String> endpointIds = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            EndpointProfileDto endpointProfileDto = generateEndpointProfileWithGroupIdDto(endpointGroupId);
            String accessToken = "" + i;
            endpointProfileDto.setAccessToken(accessToken);
            endpointService.saveEndpointProfile(endpointProfileDto);
            accessTokens.add(accessToken);
            endpointIds.add(endpointProfileDto.getId());
        }
        List<Future<EndpointProfileDto>> list = new ArrayList<>();
        final String endpointUserId = endpointUserDto.getId();
        for (int i = 0; i < (accessTokens.size()); i++) {
            final String accessToken = accessTokens.get(i);
            list.add(executorService.submit(new Callable<EndpointProfileDto>() {
                @Override
                public EndpointProfileDto call() {
                    EndpointProfileDto ep = null;
                    try {
                        ep = endpointService.attachEndpointToUser(endpointUserId, accessToken);
                    } catch (Throwable t) {
                        EndpointServiceImplTest.LOG.error(((((("Error: " + (t.getClass())) + ": ") + (t.getMessage())) + ". accessToken = ") + accessToken));
                        throw t;
                    }
                    return ep;
                }
            }));
        }
        Iterator<Future<EndpointProfileDto>> iterator = list.iterator();
        List<EndpointProfileDto> attachedProfiles = new ArrayList<>();
        while (iterator.hasNext()) {
            Future<EndpointProfileDto> f = iterator.next();
            attachedProfiles.add(f.get());
            iterator.remove();
        } 
        endpointUserDto = endpointService.findEndpointUserById(endpointUserId);
        List<String> attachedEndpointIds = endpointUserDto.getEndpointIds();
        Assert.assertNotNull(attachedEndpointIds);
        Collections.sort(endpointIds);
        Collections.sort(attachedEndpointIds);
        Assert.assertEquals(endpointIds, attachedEndpointIds);
        List<Future<Void>> detachFutureList = new ArrayList<>();
        for (int i = 0; i < (attachedProfiles.size()); i++) {
            final EndpointProfileDto attachedProfile = attachedProfiles.get(i);
            detachFutureList.add(executorService.submit(new Callable<Void>() {
                @Override
                public Void call() {
                    try {
                        endpointService.detachEndpointFromUser(attachedProfile);
                    } catch (Throwable t) {
                        EndpointServiceImplTest.LOG.error(("Error: " + (t.getMessage())), t);
                        throw t;
                    }
                    return null;
                }
            }));
        }
        Iterator<Future<Void>> detachIterator = detachFutureList.iterator();
        while (detachIterator.hasNext()) {
            Future<Void> f = detachIterator.next();
            while (!(f.isDone())) {
            } 
            detachIterator.remove();
        } 
        endpointUserDto = endpointService.findEndpointUserById(endpointUserId);
        attachedEndpointIds = endpointUserDto.getEndpointIds();
        Assert.assertTrue(((attachedEndpointIds == null) || (attachedEndpointIds.isEmpty())));
    }
}

