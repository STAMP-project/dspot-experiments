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


import Action.PAUSE;
import NovaConstants.ACTION;
import NovaConstants.CREATE_SNAPSHOT;
import NovaConstants.FLAVOR_ID;
import NovaConstants.IMAGE_ID;
import OpenstackConstants.CREATE;
import OpenstackConstants.ID;
import OpenstackConstants.NAME;
import OpenstackConstants.OPERATION;
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
import org.openstack4j.api.compute.ServerService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;


@RunWith(MockitoJUnitRunner.class)
public class ServerProducerTest extends NovaProducerTestSupport {
    @Mock
    private Server testOSServer;

    @Mock
    private ServerService serverService;

    @Captor
    private ArgumentCaptor<Action> actionArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> idArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> snapshot;

    @Captor
    private ArgumentCaptor<String> idCaptor;

    private ServerCreate dummyServer;

    @Test
    public void createServer() throws Exception {
        Mockito.when(endpoint.getOperation()).thenReturn(CREATE);
        final String expectedFlavorID = UUID.randomUUID().toString();
        Mockito.when(testOSServer.getId()).thenReturn(expectedFlavorID);
        msg.setBody(dummyServer);
        producer.process(exchange);
        final Server created = msg.getBody(Server.class);
        checkCreatedServer(dummyServer, created);
    }

    @Test
    public void createServerWithHeaders() throws Exception {
        final String expectedFlavorID = UUID.randomUUID().toString();
        Mockito.when(testOSServer.getId()).thenReturn(expectedFlavorID);
        msg.setHeader(OPERATION, CREATE);
        msg.setHeader(NAME, dummyServer.getName());
        msg.setHeader(FLAVOR_ID, dummyServer.getFlavorRef());
        msg.setHeader(IMAGE_ID, dummyServer.getImageRef());
        producer.process(exchange);
        final Server created = msg.getBody(Server.class);
        checkCreatedServer(dummyServer, created);
    }

    @Test
    public void serverAction() throws Exception {
        Mockito.when(serverService.action(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(ActionResponse.actionSuccess());
        Mockito.when(endpoint.getOperation()).thenReturn(ACTION);
        String id = "myID";
        msg.setHeader(ACTION, PAUSE);
        msg.setHeader(ID, id);
        producer.process(exchange);
        Mockito.verify(serverService).action(idArgumentCaptor.capture(), actionArgumentCaptor.capture());
        Assert.assertEquals(id, idArgumentCaptor.getValue());
        Assert.assertTrue(((actionArgumentCaptor.getValue()) == (Action.PAUSE)));
        Assert.assertFalse(msg.isFault());
        Assert.assertNull(msg.getBody());
        // test fail
        final String failReason = "fr";
        Mockito.when(serverService.action(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenReturn(ActionResponse.actionFailed(failReason, 401));
        producer.process(exchange);
        Assert.assertTrue(msg.isFault());
        Assert.assertTrue(msg.getBody(String.class).contains(failReason));
    }

    @Test
    public void createSnapshot() throws Exception {
        String id = "myID";
        String snapshotName = "mySnapshot";
        msg.setHeader(OPERATION, CREATE_SNAPSHOT);
        msg.setHeader(NAME, snapshotName);
        msg.setHeader(ID, id);
        producer.process(exchange);
        Mockito.verify(serverService).createSnapshot(idCaptor.capture(), snapshot.capture());
        Assert.assertEquals(id, idCaptor.getValue());
        Assert.assertEquals(snapshotName, snapshot.getValue());
    }
}

