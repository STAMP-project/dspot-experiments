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
package org.apache.camel.component.openstack.glance;


import GlanceConstants.CHECKSUM;
import GlanceConstants.CONTAINER_FORMAT;
import GlanceConstants.DISK_FORMAT;
import GlanceConstants.MIN_DISK;
import GlanceConstants.MIN_RAM;
import GlanceConstants.OWNER;
import GlanceConstants.RESERVE;
import GlanceConstants.UPLOAD;
import OpenstackConstants.CREATE;
import OpenstackConstants.ID;
import OpenstackConstants.NAME;
import OpenstackConstants.OPERATION;
import OpenstackConstants.UPDATE;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.camel.component.openstack.AbstractProducerTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.openstack4j.api.Builders;
import org.openstack4j.api.image.ImageService;
import org.openstack4j.model.common.Payload;
import org.openstack4j.model.image.Image;


@RunWith(MockitoJUnitRunner.class)
public class GlanceProducerTest extends AbstractProducerTestSupport {
    @Mock
    private GlanceEndpoint endpoint;

    @Mock
    private ImageService imageService;

    @Captor
    private ArgumentCaptor<Image> captor;

    @Captor
    private ArgumentCaptor<Payload<?>> payloadCaptor;

    @Captor
    private ArgumentCaptor<String> imageIdCaptor;

    @Captor
    private ArgumentCaptor<Image> imageCaptor;

    private Image dummyImage;

    @Spy
    private Image osImage = Builders.image().build();

    @Test
    public void reserveTest() throws Exception {
        Mockito.when(endpoint.getOperation()).thenReturn(RESERVE);
        msg.setBody(dummyImage);
        producer.process(exchange);
        Mockito.verify(imageService).reserve(captor.capture());
        Assert.assertEquals(dummyImage, captor.getValue());
        Image result = msg.getBody(Image.class);
        Assert.assertNotNull(result.getId());
        assertEqualsImages(dummyImage, result);
    }

    @Test
    public void reserveWithHeadersTest() throws Exception {
        Mockito.when(endpoint.getOperation()).thenReturn(RESERVE);
        msg.setHeader(NAME, dummyImage.getName());
        msg.setHeader(CONTAINER_FORMAT, dummyImage.getContainerFormat());
        msg.setHeader(DISK_FORMAT, dummyImage.getDiskFormat());
        msg.setHeader(CHECKSUM, dummyImage.getChecksum());
        msg.setHeader(MIN_DISK, dummyImage.getMinDisk());
        msg.setHeader(MIN_RAM, dummyImage.getMinRam());
        msg.setHeader(OWNER, dummyImage.getOwner());
        producer.process(exchange);
        Mockito.verify(imageService).reserve(captor.capture());
        assertEqualsImages(dummyImage, captor.getValue());
        final Image result = msg.getBody(Image.class);
        Assert.assertNotNull(result.getId());
        assertEqualsImages(dummyImage, result);
    }

    @Test
    public void createTest() throws Exception {
        msg.setHeader(OPERATION, CREATE);
        msg.setHeader(NAME, dummyImage.getName());
        msg.setHeader(OWNER, dummyImage.getOwner());
        msg.setHeader(MIN_DISK, dummyImage.getMinDisk());
        msg.setHeader(MIN_RAM, dummyImage.getMinRam());
        msg.setHeader(CHECKSUM, dummyImage.getChecksum());
        msg.setHeader(DISK_FORMAT, dummyImage.getDiskFormat());
        msg.setHeader(CONTAINER_FORMAT, dummyImage.getContainerFormat());
        final InputStream is = new FileInputStream(File.createTempFile("image", ".iso"));
        msg.setBody(is);
        producer.process(exchange);
        Mockito.verify(imageService).create(imageCaptor.capture(), payloadCaptor.capture());
        Assert.assertEquals(is, payloadCaptor.getValue().open());
        final Image result = msg.getBody(Image.class);
        Assert.assertNotNull(result.getId());
        assertEqualsImages(dummyImage, result);
    }

    @Test
    public void uploadWithoutUpdatingTest() throws Exception {
        msg.setHeader(OPERATION, UPLOAD);
        final String id = "id";
        msg.setHeader(ID, id);
        final File file = File.createTempFile("image", ".iso");
        msg.setBody(file);
        producer.process(exchange);
        Mockito.verify(imageService).upload(imageIdCaptor.capture(), payloadCaptor.capture(), imageCaptor.capture());
        Assert.assertEquals(file, payloadCaptor.getValue().getRaw());
        Assert.assertEquals(id, imageIdCaptor.getValue());
        Assert.assertNull(imageCaptor.getValue());
        final Image result = msg.getBody(Image.class);
        Assert.assertNotNull(result.getId());
        assertEqualsImages(dummyImage, result);
    }

    @Test
    public void uploadWithUpdatingTest() throws Exception {
        final String newName = "newName";
        dummyImage.setName(newName);
        Mockito.when(osImage.getName()).thenReturn(newName);
        msg.setHeader(OPERATION, UPLOAD);
        final String id = "id";
        msg.setHeader(ID, id);
        msg.setHeader(NAME, dummyImage.getName());
        msg.setHeader(OWNER, dummyImage.getOwner());
        msg.setHeader(MIN_DISK, dummyImage.getMinDisk());
        msg.setHeader(MIN_RAM, dummyImage.getMinRam());
        msg.setHeader(CHECKSUM, dummyImage.getChecksum());
        msg.setHeader(DISK_FORMAT, dummyImage.getDiskFormat());
        msg.setHeader(CONTAINER_FORMAT, dummyImage.getContainerFormat());
        final File file = File.createTempFile("image", ".iso");
        msg.setBody(file);
        producer.process(exchange);
        Mockito.verify(imageService).upload(imageIdCaptor.capture(), payloadCaptor.capture(), imageCaptor.capture());
        Assert.assertEquals(id, imageIdCaptor.getValue());
        Assert.assertEquals(file, payloadCaptor.getValue().getRaw());
        Assert.assertEquals(newName, imageCaptor.getValue().getName());
        final Image result = msg.getBody(Image.class);
        Assert.assertNotNull(result.getId());
        assertEqualsImages(dummyImage, result);
    }

    @Test
    public void updateTest() throws Exception {
        msg.setHeader(OPERATION, UPDATE);
        Mockito.when(imageService.update(ArgumentMatchers.any())).thenReturn(osImage);
        final String newName = "newName";
        Mockito.when(osImage.getName()).thenReturn(newName);
        dummyImage.setName(newName);
        msg.setBody(dummyImage);
        producer.process(exchange);
        Mockito.verify(imageService).update(imageCaptor.capture());
        Assert.assertEquals(dummyImage, imageCaptor.getValue());
        assertEqualsImages(dummyImage, msg.getBody(Image.class));
    }
}

