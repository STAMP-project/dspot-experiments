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
import KeystoneConstants.EMAIL;
import KeystoneConstants.PASSWORD;
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
import org.openstack4j.api.identity.v3.UserService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.identity.v3.User;
import org.openstack4j.model.network.Network;


@RunWith(MockitoJUnitRunner.class)
public class UserProducerTest extends KeystoneProducerTestSupport {
    private User dummyUser;

    @Mock
    private User testOSuser;

    @Mock
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<String> userIdCaptor;

    @Test
    public void createTest() throws Exception {
        msg.setHeader(OPERATION, CREATE);
        msg.setHeader(NAME, dummyUser.getName());
        msg.setHeader(DESCRIPTION, dummyUser.getDescription());
        msg.setHeader(DOMAIN_ID, dummyUser.getDomainId());
        msg.setHeader(PASSWORD, dummyUser.getPassword());
        msg.setHeader(EMAIL, dummyUser.getEmail());
        producer.process(exchange);
        Mockito.verify(userService).create(userCaptor.capture());
        assertEqualsUser(dummyUser, userCaptor.getValue());
    }

    @Test
    public void getTest() throws Exception {
        final String id = "id";
        msg.setHeader(OPERATION, GET);
        msg.setHeader(ID, id);
        producer.process(exchange);
        Mockito.verify(userService).get(userIdCaptor.capture());
        Assert.assertEquals(id, userIdCaptor.getValue());
        assertEqualsUser(testOSuser, msg.getBody(User.class));
    }

    @Test
    public void getAllTest() throws Exception {
        msg.setHeader(OPERATION, GET_ALL);
        producer.process(exchange);
        final List<Network> result = msg.getBody(List.class);
        Assert.assertTrue(((result.size()) == 2));
        Assert.assertEquals(testOSuser, result.get(0));
    }

    @Test
    public void updateTest() throws Exception {
        final String id = "myID";
        msg.setHeader(OPERATION, UPDATE);
        Mockito.when(testOSuser.getId()).thenReturn(id);
        final String newDescription = "ndesc";
        Mockito.when(testOSuser.getDescription()).thenReturn(newDescription);
        Mockito.when(userService.update(ArgumentMatchers.any())).thenReturn(testOSuser);
        msg.setBody(testOSuser);
        producer.process(exchange);
        Mockito.verify(userService).update(userCaptor.capture());
        assertEqualsUser(testOSuser, userCaptor.getValue());
        Assert.assertNotNull(userCaptor.getValue().getId());
        Assert.assertEquals(newDescription, msg.getBody(User.class).getDescription());
    }

    @Test
    public void deleteTest() throws Exception {
        Mockito.when(userService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionSuccess());
        final String networkID = "myID";
        msg.setHeader(OPERATION, DELETE);
        msg.setHeader(ID, networkID);
        producer.process(exchange);
        Mockito.verify(userService).delete(userIdCaptor.capture());
        Assert.assertEquals(networkID, userIdCaptor.getValue());
        Assert.assertFalse(msg.isFault());
        // in case of failure
        final String failureMessage = "fail";
        Mockito.when(userService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionFailed(failureMessage, 404));
        producer.process(exchange);
        Assert.assertTrue(msg.isFault());
        Assert.assertTrue(msg.getBody(String.class).contains(failureMessage));
    }
}

