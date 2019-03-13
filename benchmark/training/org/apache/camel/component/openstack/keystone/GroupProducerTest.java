/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.openstack.keystone;


import KeystoneConstants.DESCRIPTION;
import KeystoneConstants.DOMAIN_ID;
import OpenstackConstants.CREATE;
import OpenstackConstants.DELETE;
import OpenstackConstants.GET;
import OpenstackConstants.GET_ALL;
import OpenstackConstants.ID;
import OpenstackConstants.NAME;
import OpenstackConstants.OPERATION;
import OpenstackConstants.UPDATE;
import java.util.List;
import org.apache.camel.component.openstack.AbstractProducerTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openstack4j.api.identity.v3.GroupService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.identity.v3.Group;
import org.openstack4j.model.network.Network;


@RunWith(MockitoJUnitRunner.class)
public class GroupProducerTest extends KeystoneProducerTestSupport {
    private Group dummyGroup;

    @Mock
    private Group testOSgroup;

    @Mock
    private GroupService groupService;

    @Captor
    private ArgumentCaptor<Group> groupCaptor;

    @Captor
    private ArgumentCaptor<String> groupIdCaptor;

    @Test
    public void createTest() throws Exception {
        msg.setHeader(OPERATION, CREATE);
        msg.setHeader(NAME, dummyGroup.getName());
        msg.setHeader(DESCRIPTION, dummyGroup.getDescription());
        msg.setHeader(DOMAIN_ID, dummyGroup.getDomainId());
        producer.process(exchange);
        Mockito.verify(groupService).create(groupCaptor.capture());
        assertEqualsGroup(dummyGroup, groupCaptor.getValue());
    }

    @Test
    public void getTest() throws Exception {
        final String id = "id";
        msg.setHeader(OPERATION, GET);
        msg.setHeader(ID, id);
        producer.process(exchange);
        Mockito.verify(groupService).get(groupIdCaptor.capture());
        Assert.assertEquals(id, groupIdCaptor.getValue());
        assertEqualsGroup(testOSgroup, msg.getBody(Group.class));
    }

    @Test
    public void getAllTest() throws Exception {
        msg.setHeader(OPERATION, GET_ALL);
        producer.process(exchange);
        final List<Network> result = msg.getBody(List.class);
        Assert.assertTrue(((result.size()) == 2));
        Assert.assertEquals(testOSgroup, result.get(0));
    }

    @Test
    public void updateTest() throws Exception {
        final String id = "myID";
        msg.setHeader(OPERATION, UPDATE);
        final String newName = "newName";
        Mockito.when(testOSgroup.getId()).thenReturn(id);
        Mockito.when(testOSgroup.getName()).thenReturn(newName);
        Mockito.when(testOSgroup.getDescription()).thenReturn("desc");
        Mockito.when(groupService.update(ArgumentMatchers.any())).thenReturn(testOSgroup);
        msg.setBody(testOSgroup);
        producer.process(exchange);
        Mockito.verify(groupService).update(groupCaptor.capture());
        assertEqualsGroup(testOSgroup, groupCaptor.getValue());
        Assert.assertNotNull(groupCaptor.getValue().getId());
        Assert.assertEquals(newName, msg.getBody(Group.class).getName());
    }

    @Test
    public void deleteTest() throws Exception {
        Mockito.when(groupService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionSuccess());
        final String networkID = "myID";
        msg.setHeader(OPERATION, DELETE);
        msg.setHeader(ID, networkID);
        producer.process(exchange);
        Mockito.verify(groupService).delete(groupIdCaptor.capture());
        Assert.assertEquals(networkID, groupIdCaptor.getValue());
        Assert.assertFalse(msg.isFault());
        // in case of failure
        final String failureMessage = "fail";
        Mockito.when(groupService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionFailed(failureMessage, 404));
        producer.process(exchange);
        Assert.assertTrue(msg.isFault());
        Assert.assertTrue(msg.getBody(String.class).contains(failureMessage));
    }
}

