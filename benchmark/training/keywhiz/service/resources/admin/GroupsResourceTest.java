/**
 * Copyright (C) 2015 Square, Inc.
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
package keywhiz.service.resources.admin;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.jersey.params.LongParam;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import keywhiz.api.ApiDate;
import keywhiz.api.CreateGroupRequest;
import keywhiz.api.GroupDetailResponse;
import keywhiz.api.model.Client;
import keywhiz.api.model.Group;
import keywhiz.api.model.SanitizedSecret;
import keywhiz.auth.User;
import keywhiz.log.AuditLog;
import keywhiz.log.SimpleLogger;
import keywhiz.service.daos.AclDAO;
import keywhiz.service.daos.GroupDAO;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class GroupsResourceTest {
    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    AclDAO aclDAO;

    @Mock
    GroupDAO groupDAO;

    User user = User.named("user");

    ApiDate now = ApiDate.now();

    Group group = new Group(1, "group", "desc", now, "creator", now, "creator", ImmutableMap.of("app", "app"));

    AuditLog auditLog = new SimpleLogger();

    GroupsResource resource;

    @Test
    public void listingOfGroups() {
        Group group1 = new Group(1, "group1", "desc", now, "creator", now, "updater", ImmutableMap.of("app", "app"));
        Group group2 = new Group(2, "group2", "desc", now, "creator", now, "updater", ImmutableMap.of("app", "app"));
        Mockito.when(groupDAO.getGroups()).thenReturn(ImmutableSet.of(group1, group2));
        List<Group> response = resource.listGroups(user);
        assertThat(response).containsOnly(group1, group2);
    }

    @Test
    public void createsGroup() {
        CreateGroupRequest request = new CreateGroupRequest("newGroup", "description", ImmutableMap.of("app", "app"));
        Mockito.when(groupDAO.getGroup("newGroup")).thenReturn(Optional.empty());
        Mockito.when(groupDAO.createGroup("newGroup", "user", "description", ImmutableMap.of("app", "app"))).thenReturn(55L);
        Mockito.when(groupDAO.getGroupById(55L)).thenReturn(Optional.of(group));
        Mockito.when(aclDAO.getSanitizedSecretsFor(group)).thenReturn(ImmutableSet.of());
        Response response = resource.createGroup(user, request);
        assertThat(response.getStatus()).isEqualTo(201);
    }

    @Test(expected = BadRequestException.class)
    public void rejectsWhenGroupExists() {
        CreateGroupRequest request = new CreateGroupRequest("newGroup", "description", ImmutableMap.of("app", "app"));
        Group group = new Group(3, "newGroup", "desc", now, "creator", now, "updater", ImmutableMap.of("app", "app"));
        Mockito.when(groupDAO.getGroup("newGroup")).thenReturn(Optional.of(group));
        resource.createGroup(user, request);
    }

    @Test
    public void getSpecificIncludesAllTheThings() {
        Mockito.when(groupDAO.getGroupById(4444)).thenReturn(Optional.of(group));
        SanitizedSecret secret = SanitizedSecret.of(1, "name", null, "checksum", now, "creator", now, "creator", null, null, null, 1136214245, 125L, now, "creator");
        Mockito.when(aclDAO.getSanitizedSecretsFor(group)).thenReturn(ImmutableSet.of(secret));
        Client client = new Client(1, "client", "desc", now, "creator", now, "creator", null, null, true, false);
        Mockito.when(aclDAO.getClientsFor(group)).thenReturn(ImmutableSet.of(client));
        GroupDetailResponse response = resource.getGroup(user, new LongParam("4444"));
        assertThat(response.getId()).isEqualTo(group.getId());
        assertThat(response.getName()).isEqualTo(group.getName());
        assertThat(response.getDescription()).isEqualTo(group.getDescription());
        assertThat(response.getCreationDate()).isEqualTo(group.getCreatedAt());
        assertThat(response.getCreatedBy()).isEqualTo(group.getCreatedBy());
        assertThat(response.getUpdateDate()).isEqualTo(group.getUpdatedAt());
        assertThat(response.getUpdatedBy()).isEqualTo(group.getUpdatedBy());
        assertThat(response.getSecrets()).containsExactly(secret);
        assertThat(response.getClients()).containsExactly(client);
    }

    @Test(expected = NotFoundException.class)
    public void notFoundWhenRetrievingBadId() {
        Mockito.when(groupDAO.getGroupById(464330218)).thenReturn(Optional.empty());
        resource.getGroup(user, new LongParam(Long.toString(464330218)));
    }

    @Test
    public void findGroupByName() {
        Mockito.when(groupDAO.getGroup(group.getName())).thenReturn(Optional.of(group));
        assertThat(resource.getGroupByName(user, "group")).isEqualTo(group);
    }

    @Test(expected = NotFoundException.class)
    public void notFoundWhenRetrievingBadName() {
        Mockito.when(groupDAO.getGroup("non-existent-group")).thenReturn(Optional.empty());
        resource.getGroupByName(user, "non-existent-group");
    }

    @Test
    public void canDelete() {
        Mockito.when(groupDAO.getGroupById(-559038737)).thenReturn(Optional.of(group));
        Response response = resource.deleteGroup(user, new LongParam(Long.toString(-559038737)));
        Mockito.verify(groupDAO).deleteGroup(group);
        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test(expected = NotFoundException.class)
    public void notFoundWhenDeletingBadId() {
        Mockito.when(groupDAO.getGroupById(464330218)).thenReturn(Optional.empty());
        resource.deleteGroup(user, new LongParam(Long.toString(464330218)));
    }
}

