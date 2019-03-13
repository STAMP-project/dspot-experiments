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
package org.apache.camel.component.openstack.nova;


import NovaConstants.DISK;
import NovaConstants.RAM;
import NovaConstants.SWAP;
import NovaConstants.VCPU;
import OpenstackConstants.CREATE;
import OpenstackConstants.DELETE;
import OpenstackConstants.GET;
import OpenstackConstants.GET_ALL;
import OpenstackConstants.ID;
import OpenstackConstants.NAME;
import OpenstackConstants.OPERATION;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import org.openstack4j.api.compute.FlavorService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Flavor;


@RunWith(MockitoJUnitRunner.class)
public class FlavorProducerTest extends NovaProducerTestSupport {
    @Mock
    private Flavor testOSFlavor;

    @Mock
    private FlavorService flavorService;

    @Captor
    private ArgumentCaptor<Flavor> flavorCaptor;

    @Captor
    private ArgumentCaptor<String> flavorIdCaptor;

    private Flavor dummyFlavor;

    @Test
    public void createFlavor() throws Exception {
        Mockito.when(endpoint.getOperation()).thenReturn(CREATE);
        final String expectedFlavorID = UUID.randomUUID().toString();
        Mockito.when(testOSFlavor.getId()).thenReturn(expectedFlavorID);
        // send dummyFlavor to create
        msg.setBody(dummyFlavor);
        producer.process(exchange);
        Mockito.verify(flavorService).create(flavorCaptor.capture());
        Assert.assertEquals(dummyFlavor, flavorCaptor.getValue());
        final Flavor createdFlavor = msg.getBody(Flavor.class);
        assertEqualsFlavors(dummyFlavor, createdFlavor);
        Assert.assertNotNull(createdFlavor.getId());
    }

    @Test
    public void createFlavorWithHeaders() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        headers.put(OPERATION, CREATE);
        headers.put(NAME, dummyFlavor.getName());
        headers.put(VCPU, dummyFlavor.getVcpus());
        headers.put(DISK, dummyFlavor.getDisk());
        headers.put(SWAP, dummyFlavor.getSwap());
        headers.put(RAM, dummyFlavor.getRam());
        msg.setHeaders(headers);
        producer.process(exchange);
        Mockito.verify(flavorService).create(flavorCaptor.capture());
        assertEqualsFlavors(dummyFlavor, flavorCaptor.getValue());
        final Flavor created = msg.getBody(Flavor.class);
        Assert.assertNotNull(created.getId());
        assertEqualsFlavors(dummyFlavor, created);
    }

    @Test
    public void getTest() throws Exception {
        msg.setHeader(OPERATION, GET);
        msg.setHeader(ID, "anything - client is mocked");
        // should return dummyFlavor
        producer.process(exchange);
        final Flavor result = msg.getBody(Flavor.class);
        assertEqualsFlavors(dummyFlavor, result);
        Assert.assertNotNull(result.getId());
    }

    @Test
    public void getAllTest() throws Exception {
        Mockito.when(endpoint.getOperation()).thenReturn(GET_ALL);
        producer.process(exchange);
        List<Flavor> result = msg.getBody(List.class);
        Assert.assertTrue(((result.size()) == 2));
        for (Flavor f : result) {
            assertEqualsFlavors(dummyFlavor, f);
            Assert.assertNotNull(f.getId());
        }
    }

    @Test
    public void deleteSuccess() throws Exception {
        Mockito.when(flavorService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionSuccess());
        Mockito.when(endpoint.getOperation()).thenReturn(DELETE);
        String id = "myID";
        msg.setHeader(ID, id);
        producer.process(exchange);
        Mockito.verify(flavorService).delete(flavorIdCaptor.capture());
        Assert.assertEquals(id, flavorIdCaptor.getValue());
        Assert.assertFalse(msg.isFault());
        Assert.assertNull(msg.getBody());
    }

    @Test
    public void deleteFailure() throws Exception {
        final String failReason = "unknown";
        Mockito.when(flavorService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionFailed(failReason, 401));
        Mockito.when(endpoint.getOperation()).thenReturn(DELETE);
        String id = "myID";
        msg.setHeader(ID, id);
        producer.process(exchange);
        Mockito.verify(flavorService).delete(flavorIdCaptor.capture());
        Assert.assertEquals(id, flavorIdCaptor.getValue());
        Assert.assertTrue(msg.isFault());
        Assert.assertTrue(msg.getBody(String.class).contains(failReason));
    }
}

