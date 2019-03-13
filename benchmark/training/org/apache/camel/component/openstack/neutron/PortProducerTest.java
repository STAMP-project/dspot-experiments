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


import NeutronConstants.DEVICE_ID;
import NeutronConstants.MAC_ADDRESS;
import NeutronConstants.NETWORK_ID;
import NeutronConstants.TENANT_ID;
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
import org.openstack4j.api.networking.PortService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.network.Port;


@RunWith(MockitoJUnitRunner.class)
public class PortProducerTest extends NeutronProducerTestSupport {
    private Port dummyPort;

    @Mock
    private Port testOSport;

    @Mock
    private PortService portService;

    @Captor
    private ArgumentCaptor<Port> portCaptor;

    @Captor
    private ArgumentCaptor<String> portIdCaptor;

    @Test
    public void createTest() throws Exception {
        msg.setHeader(OPERATION, CREATE);
        msg.setHeader(NAME, dummyPort.getName());
        msg.setHeader(TENANT_ID, dummyPort.getTenantId());
        msg.setHeader(NETWORK_ID, dummyPort.getNetworkId());
        msg.setHeader(MAC_ADDRESS, dummyPort.getMacAddress());
        msg.setHeader(DEVICE_ID, dummyPort.getDeviceId());
        producer.process(exchange);
        Mockito.verify(portService).create(portCaptor.capture());
        assertEqualsPort(dummyPort, portCaptor.getValue());
        Assert.assertNotNull(msg.getBody(Port.class).getId());
    }

    @Test
    public void getTest() throws Exception {
        final String portID = "myNetID";
        msg.setHeader(OPERATION, GET);
        msg.setHeader(ID, portID);
        producer.process(exchange);
        Mockito.verify(portService).get(portIdCaptor.capture());
        Assert.assertEquals(portID, portIdCaptor.getValue());
        assertEqualsPort(testOSport, msg.getBody(Port.class));
    }

    @Test
    public void getAllTest() throws Exception {
        msg.setHeader(OPERATION, GET_ALL);
        producer.process(exchange);
        final List<Port> result = msg.getBody(List.class);
        Assert.assertTrue(((result.size()) == 2));
        Assert.assertEquals(testOSport, result.get(0));
    }

    @Test
    public void updateTest() throws Exception {
        final String portID = "myID";
        msg.setHeader(OPERATION, UPDATE);
        final String newDevId = "dev";
        Mockito.when(testOSport.getDeviceId()).thenReturn(newDevId);
        Mockito.when(testOSport.getId()).thenReturn(portID);
        Mockito.when(portService.update(ArgumentMatchers.any())).thenReturn(testOSport);
        msg.setBody(testOSport);
        producer.process(exchange);
        Mockito.verify(portService).update(portCaptor.capture());
        assertEqualsPort(testOSport, portCaptor.getValue());
        Assert.assertNotNull(portCaptor.getValue().getId());
    }

    @Test
    public void deleteTest() throws Exception {
        Mockito.when(portService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionSuccess());
        final String portID = "myNetID";
        msg.setHeader(OPERATION, DELETE);
        msg.setHeader(ID, portID);
        producer.process(exchange);
        Mockito.verify(portService).delete(portIdCaptor.capture());
        Assert.assertEquals(portID, portIdCaptor.getValue());
        Assert.assertFalse(msg.isFault());
        // in case of failure
        final String failureMessage = "fail";
        Mockito.when(portService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionFailed(failureMessage, 404));
        producer.process(exchange);
        Assert.assertTrue(msg.isFault());
        Assert.assertTrue(msg.getBody(String.class).contains(failureMessage));
    }
}

