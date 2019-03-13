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
package org.apache.camel.component.openstack.swift;


import OpenstackConstants.CREATE;
import OpenstackConstants.DELETE;
import OpenstackConstants.GET;
import OpenstackConstants.GET_ALL;
import OpenstackConstants.OPERATION;
import SwiftConstants.CONTAINER_NAME;
import SwiftConstants.CREATE_UPDATE_METADATA;
import SwiftConstants.DELIMITER;
import SwiftConstants.GET_METADATA;
import SwiftConstants.LIMIT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.openstack4j.api.storage.ObjectStorageContainerService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.storage.object.SwiftContainer;
import org.openstack4j.model.storage.object.options.ContainerListOptions;
import org.openstack4j.model.storage.object.options.CreateUpdateContainerOptions;


@RunWith(MockitoJUnitRunner.class)
public class ContainerProducerTest extends SwiftProducerTestSupport {
    private static final String CONTAINER_NAME = "containerName";

    @Mock
    private SwiftContainer mockOsContainer;

    @Mock
    private ObjectStorageContainerService containerService;

    @Captor
    private ArgumentCaptor<String> nameCaptor;

    @Captor
    private ArgumentCaptor<Map<String, String>> dataCaptor;

    @Captor
    private ArgumentCaptor<String> containerNameCaptor;

    @Captor
    private ArgumentCaptor<CreateUpdateContainerOptions> optionsCaptor;

    @Captor
    private ArgumentCaptor<ContainerListOptions> containerListOptionsCaptor;

    @Test
    public void createTestWithoutOptions() throws Exception {
        Mockito.when(containerService.create(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn(ActionResponse.actionSuccess());
        msg.setHeader(OPERATION, CREATE);
        msg.setHeader(SwiftConstants.CONTAINER_NAME, ContainerProducerTest.CONTAINER_NAME);
        producer.process(exchange);
        Mockito.verify(containerService).create(containerNameCaptor.capture(), optionsCaptor.capture());
        Assert.assertEquals(ContainerProducerTest.CONTAINER_NAME, containerNameCaptor.getValue());
        Assert.assertNull(optionsCaptor.getValue());
        Assert.assertFalse(msg.isFault());
    }

    @Test
    public void createTestWithOptions() throws Exception {
        Mockito.when(containerService.create(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(ActionResponse.actionSuccess());
        msg.setHeader(OPERATION, CREATE);
        msg.setHeader(SwiftConstants.CONTAINER_NAME, ContainerProducerTest.CONTAINER_NAME);
        final CreateUpdateContainerOptions options = CreateUpdateContainerOptions.create().accessAnybodyRead();
        msg.setBody(options);
        producer.process(exchange);
        Mockito.verify(containerService).create(containerNameCaptor.capture(), optionsCaptor.capture());
        Assert.assertEquals(ContainerProducerTest.CONTAINER_NAME, containerNameCaptor.getValue());
        Assert.assertEquals(options, optionsCaptor.getValue());
        Assert.assertFalse(msg.isFault());
    }

    @Test
    public void getTest() throws Exception {
        List<SwiftContainer> list = new ArrayList<>();
        list.add(mockOsContainer);
        Mockito.doReturn(list).when(containerService).list(ArgumentMatchers.any());
        Mockito.when(endpoint.getOperation()).thenReturn(GET);
        msg.setHeader(LIMIT, 10);
        msg.setHeader(DELIMITER, 'x');
        producer.process(exchange);
        Mockito.verify(containerService).list(containerListOptionsCaptor.capture());
        Map<String, String> options = containerListOptionsCaptor.getValue().getOptions();
        Assert.assertEquals(String.valueOf(10), options.get(LIMIT));
        Assert.assertEquals("x", options.get(DELIMITER));
        Assert.assertEquals(list, msg.getBody(List.class));
    }

    @Test
    public void getAllFromContainerTest() throws Exception {
        List<SwiftContainer> list = new ArrayList<>();
        list.add(mockOsContainer);
        Mockito.doReturn(list).when(containerService).list();
        Mockito.when(endpoint.getOperation()).thenReturn(GET_ALL);
        msg.setHeader(SwiftConstants.CONTAINER_NAME, ContainerProducerTest.CONTAINER_NAME);
        producer.process(exchange);
        Assert.assertEquals(mockOsContainer, msg.getBody(List.class).get(0));
    }

    @Test
    public void deleteObjectTest() throws Exception {
        Mockito.when(containerService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionSuccess());
        msg.setHeader(OPERATION, DELETE);
        msg.setHeader(SwiftConstants.CONTAINER_NAME, ContainerProducerTest.CONTAINER_NAME);
        producer.process(exchange);
        Mockito.verify(containerService).delete(containerNameCaptor.capture());
        Assert.assertEquals(ContainerProducerTest.CONTAINER_NAME, containerNameCaptor.getValue());
        Assert.assertFalse(msg.isFault());
    }

    @Test
    public void deleteObjectFailTest() throws Exception {
        final String failMessage = "fail";
        Mockito.when(containerService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionFailed(failMessage, 401));
        msg.setHeader(OPERATION, DELETE);
        msg.setHeader(SwiftConstants.CONTAINER_NAME, ContainerProducerTest.CONTAINER_NAME);
        producer.process(exchange);
        Assert.assertTrue(msg.isFault());
        Assert.assertTrue(msg.getBody(String.class).contains(failMessage));
    }

    @Test
    public void createUpdateMetadataTest() throws Exception {
        final Map<String, String> md = new HashMap<>();
        md.put("key", "val");
        msg.setHeader(OPERATION, CREATE_UPDATE_METADATA);
        msg.setHeader(SwiftConstants.CONTAINER_NAME, ContainerProducerTest.CONTAINER_NAME);
        msg.setBody(md);
        producer.process(exchange);
        Mockito.verify(containerService).updateMetadata(nameCaptor.capture(), dataCaptor.capture());
        Assert.assertEquals(ContainerProducerTest.CONTAINER_NAME, nameCaptor.getValue());
        Assert.assertEquals(md, dataCaptor.getValue());
    }

    @Test
    public void getMetadataTest() throws Exception {
        final Map<String, String> md = new HashMap<>();
        md.put("key", "val");
        Mockito.when(containerService.getMetadata(ContainerProducerTest.CONTAINER_NAME)).thenReturn(md);
        msg.setHeader(OPERATION, GET_METADATA);
        msg.setHeader(SwiftConstants.CONTAINER_NAME, ContainerProducerTest.CONTAINER_NAME);
        producer.process(exchange);
        Assert.assertEquals(md, msg.getBody(Map.class));
    }
}

