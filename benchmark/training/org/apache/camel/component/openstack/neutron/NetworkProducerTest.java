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
package org.apache.camel.component.openstack.neutron;


import NeutronConstants.NETWORK_ID;
import NeutronConstants.NETWORK_TYPE;
import NeutronConstants.TENANT_ID;
import OpenstackConstants.CREATE;
import OpenstackConstants.DELETE;
import OpenstackConstants.GET;
import OpenstackConstants.GET_ALL;
import OpenstackConstants.ID;
import OpenstackConstants.NAME;
import OpenstackConstants.OPERATION;
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
import org.openstack4j.api.networking.NetworkService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.network.Network;


@RunWith(MockitoJUnitRunner.class)
public class NetworkProducerTest extends NeutronProducerTestSupport {
    private Network dummyNetwork;

    @Mock
    private Network testOSnetwork;

    @Mock
    private NetworkService networkService;

    @Captor
    private ArgumentCaptor<Network> networkCaptor;

    @Captor
    private ArgumentCaptor<String> networkIdCaptor;

    @Test
    public void createTest() throws Exception {
        msg.setHeader(OPERATION, CREATE);
        msg.setHeader(NAME, dummyNetwork.getName());
        msg.setHeader(NETWORK_TYPE, dummyNetwork.getNetworkType());
        msg.setHeader(TENANT_ID, dummyNetwork.getTenantId());
        producer.process(exchange);
        Mockito.verify(networkService).create(networkCaptor.capture());
        assertEqualsNetwork(dummyNetwork, networkCaptor.getValue());
        Assert.assertNotNull(msg.getBody(Network.class).getId());
    }

    @Test
    public void getTest() throws Exception {
        final String networkID = "myNetID";
        msg.setHeader(OPERATION, GET);
        msg.setHeader(NETWORK_ID, networkID);
        producer.process(exchange);
        Mockito.verify(networkService).get(networkIdCaptor.capture());
        Assert.assertEquals(networkID, networkIdCaptor.getValue());
        assertEqualsNetwork(testOSnetwork, msg.getBody(Network.class));
    }

    @Test
    public void getAllTest() throws Exception {
        msg.setHeader(OPERATION, GET_ALL);
        producer.process(exchange);
        final List<Network> result = msg.getBody(List.class);
        Assert.assertTrue(((result.size()) == 2));
        Assert.assertEquals(testOSnetwork, result.get(0));
    }

    @Test
    public void deleteTest() throws Exception {
        Mockito.when(networkService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionSuccess());
        final String networkID = "myNetID";
        msg.setHeader(OPERATION, DELETE);
        msg.setHeader(ID, networkID);
        producer.process(exchange);
        Mockito.verify(networkService).delete(networkIdCaptor.capture());
        Assert.assertEquals(networkID, networkIdCaptor.getValue());
        Assert.assertFalse(msg.isFault());
        // in case of failure
        final String failureMessage = "fail";
        Mockito.when(networkService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionFailed(failureMessage, 404));
        producer.process(exchange);
        Assert.assertTrue(msg.isFault());
        Assert.assertTrue(msg.getBody(String.class).contains(failureMessage));
    }
}

