/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.toolkit.s2s;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransactionCompletion;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.remote.protocol.DataPacket;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SiteToSiteSenderTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    SiteToSiteClient siteToSiteClient;

    @Mock
    Transaction transaction;

    @Mock
    TransactionCompletion transactionCompletion;

    ByteArrayOutputStream data;

    private final Supplier<SiteToSiteSender> senderSupplier = () -> new SiteToSiteSender(siteToSiteClient, new ByteArrayInputStream(data.toByteArray()));

    @Test
    public void testEmptyList() throws IOException {
        objectMapper.writeValue(data, Collections.emptyList());
        Assert.assertEquals(transactionCompletion, senderSupplier.get().sendFiles());
        Mockito.verify(transaction, Mockito.never()).send(ArgumentMatchers.any(DataPacket.class));
        Mockito.verify(transaction).complete();
        Mockito.verifyNoMoreInteractions(siteToSiteClient, transaction, transactionCompletion);
    }

    @Test
    public void testSingleElement() throws IOException {
        DataPacketDto dataPacketDto = new DataPacketDto("test-data".getBytes(StandardCharsets.UTF_8)).putAttribute("key", "value");
        objectMapper.writeValue(data, Arrays.stream(new DataPacketDto[]{ dataPacketDto }).collect(Collectors.toList()));
        Assert.assertEquals(transactionCompletion, senderSupplier.get().sendFiles());
        Mockito.verify(transaction).send(dataPacketDto.toDataPacket());
        Mockito.verify(transaction).complete();
        Mockito.verifyNoMoreInteractions(siteToSiteClient, transaction, transactionCompletion);
    }

    @Test
    public void testMultipleElements() throws IOException {
        DataPacketDto dataPacketDto = new DataPacketDto("test-data".getBytes(StandardCharsets.UTF_8)).putAttribute("key", "value");
        DataPacketDto dataPacketDto2 = new DataPacketDto("test-data2".getBytes(StandardCharsets.UTF_8)).putAttribute("key2", "value2");
        objectMapper.writeValue(data, Arrays.stream(new DataPacketDto[]{ dataPacketDto, dataPacketDto2 }).collect(Collectors.toList()));
        Assert.assertEquals(transactionCompletion, senderSupplier.get().sendFiles());
        Mockito.verify(transaction).send(dataPacketDto.toDataPacket());
        Mockito.verify(transaction).send(dataPacketDto2.toDataPacket());
        Mockito.verify(transaction).complete();
        Mockito.verifyNoMoreInteractions(siteToSiteClient, transaction, transactionCompletion);
    }

    @Test(expected = IOException.class)
    public void testIOException() throws IOException {
        IOException test = new IOException("test");
        DataPacketDto dataPacketDto = new DataPacketDto("test-data".getBytes(StandardCharsets.UTF_8)).putAttribute("key", "value");
        objectMapper.writeValue(data, Arrays.stream(new DataPacketDto[]{ dataPacketDto }).collect(Collectors.toList()));
        Mockito.doThrow(test).when(transaction).send(ArgumentMatchers.any(DataPacket.class));
        try {
            senderSupplier.get().sendFiles();
        } catch (IOException e) {
            Assert.assertEquals(test, e);
            throw e;
        }
    }

    @Test(expected = IOException.class)
    public void testRuntimeException() throws IOException {
        RuntimeException test = new RuntimeException("test");
        DataPacketDto dataPacketDto = new DataPacketDto("test-data".getBytes(StandardCharsets.UTF_8)).putAttribute("key", "value");
        objectMapper.writeValue(data, Arrays.stream(new DataPacketDto[]{ dataPacketDto }).collect(Collectors.toList()));
        Mockito.doThrow(test).when(transaction).send(ArgumentMatchers.any(DataPacket.class));
        try {
            senderSupplier.get().sendFiles();
        } catch (IOException e) {
            Assert.assertEquals(test, e.getCause());
            throw e;
        }
    }
}

