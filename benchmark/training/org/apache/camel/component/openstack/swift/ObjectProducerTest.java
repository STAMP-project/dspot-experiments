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
import SwiftConstants.GET_METADATA;
import SwiftConstants.OBJECT_NAME;
import java.util.ArrayList;
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
import org.openstack4j.api.storage.ObjectStorageObjectService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.common.Payload;
import org.openstack4j.model.storage.object.SwiftObject;
import org.openstack4j.model.storage.object.options.ObjectLocation;


@RunWith(MockitoJUnitRunner.class)
public class ObjectProducerTest extends SwiftProducerTestSupport {
    private static final String CONTAINER_NAME = "containerName";

    private static final String OBJECT_NAME = "objectName";

    private static final String ETAG = UUID.randomUUID().toString();

    @Mock
    private SwiftObject mockOsObject;

    @Mock
    private ObjectStorageObjectService objectService;

    @Captor
    private ArgumentCaptor<String> containerNameCaptor;

    @Captor
    private ArgumentCaptor<String> objectNameCaptor;

    @Captor
    private ArgumentCaptor<Payload<?>> payloadArgumentCaptor;

    @Captor
    private ArgumentCaptor<ObjectLocation> locationCaptor;

    @Captor
    private ArgumentCaptor<Map<String, String>> dataCaptor;

    @Test
    public void createTest() throws Exception {
        Mockito.when(objectService.put(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(ObjectProducerTest.ETAG);
        msg.setHeader(OPERATION, CREATE);
        msg.setHeader(SwiftConstants.CONTAINER_NAME, ObjectProducerTest.CONTAINER_NAME);
        msg.setHeader(SwiftConstants.OBJECT_NAME, ObjectProducerTest.OBJECT_NAME);
        final Payload<?> payload = getTmpPayload();
        msg.setBody(payload);
        producer.process(exchange);
        Mockito.verify(objectService).put(containerNameCaptor.capture(), objectNameCaptor.capture(), payloadArgumentCaptor.capture());
        Assert.assertEquals(ObjectProducerTest.CONTAINER_NAME, containerNameCaptor.getValue());
        Assert.assertEquals(ObjectProducerTest.OBJECT_NAME, objectNameCaptor.getValue());
        Assert.assertEquals(payload, payloadArgumentCaptor.getValue());
        Assert.assertEquals(ObjectProducerTest.ETAG, msg.getBody(String.class));
    }

    @Test
    public void getTest() throws Exception {
        Mockito.when(objectService.get(ObjectProducerTest.CONTAINER_NAME, ObjectProducerTest.OBJECT_NAME)).thenReturn(mockOsObject);
        Mockito.when(endpoint.getOperation()).thenReturn(GET);
        msg.setHeader(SwiftConstants.CONTAINER_NAME, ObjectProducerTest.CONTAINER_NAME);
        msg.setHeader(SwiftConstants.OBJECT_NAME, ObjectProducerTest.OBJECT_NAME);
        producer.process(exchange);
        Assert.assertEquals(ObjectProducerTest.ETAG, msg.getBody(SwiftObject.class).getETag());
    }

    @Test
    public void getAllFromContainerTest() throws Exception {
        List<SwiftObject> objectsList = new ArrayList<>();
        objectsList.add(mockOsObject);
        Mockito.doReturn(objectsList).when(objectService).list(ObjectProducerTest.CONTAINER_NAME);
        Mockito.when(endpoint.getOperation()).thenReturn(GET_ALL);
        msg.setHeader(SwiftConstants.CONTAINER_NAME, ObjectProducerTest.CONTAINER_NAME);
        producer.process(exchange);
        Assert.assertEquals(mockOsObject, msg.getBody(List.class).get(0));
    }

    @Test
    public void deleteObjectTest() throws Exception {
        Mockito.when(objectService.delete(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionSuccess());
        msg.setHeader(OPERATION, DELETE);
        msg.setHeader(SwiftConstants.CONTAINER_NAME, ObjectProducerTest.CONTAINER_NAME);
        msg.setHeader(SwiftConstants.OBJECT_NAME, ObjectProducerTest.OBJECT_NAME);
        producer.process(exchange);
        Mockito.verify(objectService).delete(containerNameCaptor.capture(), objectNameCaptor.capture());
        Assert.assertEquals(ObjectProducerTest.CONTAINER_NAME, containerNameCaptor.getValue());
        Assert.assertEquals(ObjectProducerTest.OBJECT_NAME, objectNameCaptor.getValue());
        Assert.assertFalse(msg.isFault());
    }

    @Test
    public void deleteObjectFailTest() throws Exception {
        final String failMessage = "fail";
        Mockito.when(objectService.delete(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionFailed(failMessage, 401));
        msg.setHeader(OPERATION, DELETE);
        msg.setHeader(SwiftConstants.CONTAINER_NAME, ObjectProducerTest.CONTAINER_NAME);
        msg.setHeader(SwiftConstants.OBJECT_NAME, ObjectProducerTest.OBJECT_NAME);
        producer.process(exchange);
        Assert.assertTrue(msg.isFault());
        Assert.assertTrue(msg.getBody(String.class).contains(failMessage));
    }

    @Test
    public void updateMetadataTest() throws Exception {
        final Map<String, String> md = new HashMap<>();
        md.put("key", "val");
        msg.setHeader(OPERATION, CREATE_UPDATE_METADATA);
        msg.setHeader(SwiftConstants.CONTAINER_NAME, ObjectProducerTest.CONTAINER_NAME);
        msg.setHeader(SwiftConstants.OBJECT_NAME, ObjectProducerTest.OBJECT_NAME);
        msg.setBody(md);
        producer.process(exchange);
        Mockito.verify(objectService).updateMetadata(locationCaptor.capture(), dataCaptor.capture());
        ObjectLocation location = locationCaptor.getValue();
        Assert.assertEquals(ObjectProducerTest.CONTAINER_NAME, location.getContainerName());
        Assert.assertEquals(ObjectProducerTest.OBJECT_NAME, location.getObjectName());
        Assert.assertEquals(md, dataCaptor.getValue());
    }

    @Test
    public void getMetadataTest() throws Exception {
        final Map<String, String> md = new HashMap<>();
        md.put("key", "val");
        Mockito.when(objectService.getMetadata(ObjectProducerTest.CONTAINER_NAME, ObjectProducerTest.OBJECT_NAME)).thenReturn(md);
        msg.setHeader(OPERATION, GET_METADATA);
        msg.setHeader(SwiftConstants.CONTAINER_NAME, ObjectProducerTest.CONTAINER_NAME);
        msg.setHeader(SwiftConstants.OBJECT_NAME, ObjectProducerTest.OBJECT_NAME);
        producer.process(exchange);
        Assert.assertEquals(md, msg.getBody(Map.class));
    }
}

