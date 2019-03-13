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
package org.apache.camel.component.openstack.cinder;


import OpenstackConstants.CREATE;
import OpenstackConstants.DELETE;
import OpenstackConstants.DESCRIPTION;
import OpenstackConstants.GET;
import OpenstackConstants.ID;
import OpenstackConstants.NAME;
import OpenstackConstants.OPERATION;
import OpenstackConstants.UPDATE;
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
import org.openstack4j.api.storage.BlockVolumeSnapshotService;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.storage.block.VolumeSnapshot;


@RunWith(MockitoJUnitRunner.class)
public class VolumeSnapshotProducerTest extends CinderProducerTestSupport {
    @Mock
    private BlockVolumeSnapshotService snapshotService;

    @Mock
    private VolumeSnapshot testOSVolumeSnapshot;

    @Captor
    private ArgumentCaptor<String> idCaptor;

    @Captor
    private ArgumentCaptor<String> nameCaptor;

    @Captor
    private ArgumentCaptor<String> descCaptor;

    @Captor
    private ArgumentCaptor<String> captor;

    private VolumeSnapshot dummyVolumeSnapshot;

    @Test
    public void createVolumeTest() throws Exception {
        Mockito.when(endpoint.getOperation()).thenReturn(CREATE);
        msg.setBody(dummyVolumeSnapshot);
        producer.process(exchange);
        final VolumeSnapshot result = msg.getBody(VolumeSnapshot.class);
        assertEqualsVolumeSnapshots(dummyVolumeSnapshot, result);
        Assert.assertNotNull(result.getId());
    }

    @Test
    public void updateVolumeSnapshotTest() throws Exception {
        Mockito.when(snapshotService.update(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionSuccess());
        msg.setHeader(OPERATION, UPDATE);
        final String id = "id";
        final String desc = "newDesc";
        final String name = "newName";
        msg.setHeader(ID, id);
        msg.setHeader(DESCRIPTION, desc);
        msg.setHeader(NAME, name);
        producer.process(exchange);
        Mockito.verify(snapshotService).update(idCaptor.capture(), nameCaptor.capture(), descCaptor.capture());
        Assert.assertEquals(id, idCaptor.getValue());
        Assert.assertEquals(name, nameCaptor.getValue());
        Assert.assertEquals(desc, descCaptor.getValue());
        Assert.assertFalse(msg.isFault());
        Assert.assertNull(msg.getBody());
    }

    @Test
    public void getVolumeSnapshotTest() throws Exception {
        Mockito.when(endpoint.getOperation()).thenReturn(GET);
        msg.setHeader(ID, "anyID");
        producer.process(exchange);
        assertEqualsVolumeSnapshots(dummyVolumeSnapshot, msg.getBody(VolumeSnapshot.class));
    }

    @Test
    public void deleteVolumeSnapshotTest() throws Exception {
        msg.setHeader(OPERATION, DELETE);
        Mockito.when(snapshotService.delete(ArgumentMatchers.anyString())).thenReturn(ActionResponse.actionSuccess());
        final String id = "id";
        msg.setHeader(ID, id);
        producer.process(exchange);
        Mockito.verify(snapshotService).delete(captor.capture());
        Assert.assertEquals(id, captor.getValue());
        Assert.assertFalse(msg.isFault());
    }
}

