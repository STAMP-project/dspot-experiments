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


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;
import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransactionCompletion;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.JsonInclude.Value.construct;


@RunWith(MockitoJUnitRunner.class)
public class SiteToSiteReceiverTest {
    private final ObjectMapper objectMapper = new ObjectMapper().setDefaultPropertyInclusion(construct(NON_NULL, ALWAYS));

    @Mock
    SiteToSiteClient siteToSiteClient;

    @Mock
    Transaction transaction;

    @Mock
    TransactionCompletion transactionCompletion;

    ByteArrayOutputStream data;

    private final Supplier<SiteToSiteReceiver> receiverSupplier = () -> new SiteToSiteReceiver(siteToSiteClient, data);

    ByteArrayOutputStream expectedData;

    @Test
    public void testEmpty() throws IOException {
        Assert.assertEquals(transactionCompletion, receiverSupplier.get().receiveFiles());
        objectMapper.writeValue(expectedData, Collections.emptyList());
        Assert.assertEquals(expectedData.toString(), data.toString());
    }

    @Test
    public void testSingle() throws IOException {
        DataPacketDto dataPacketDto = new DataPacketDto("test-data".getBytes(StandardCharsets.UTF_8)).putAttribute("key", "value");
        Mockito.when(transaction.receive()).thenReturn(dataPacketDto.toDataPacket()).thenReturn(null);
        Assert.assertEquals(transactionCompletion, receiverSupplier.get().receiveFiles());
        objectMapper.writeValue(expectedData, Arrays.asList(dataPacketDto));
        Assert.assertEquals(expectedData.toString(), data.toString());
    }

    @Test
    public void testMulti() throws IOException {
        DataPacketDto dataPacketDto = new DataPacketDto("test-data".getBytes(StandardCharsets.UTF_8)).putAttribute("key", "value");
        DataPacketDto dataPacketDto2 = new DataPacketDto("test-data2".getBytes(StandardCharsets.UTF_8)).putAttribute("key2", "value2");
        Mockito.when(transaction.receive()).thenReturn(dataPacketDto.toDataPacket()).thenReturn(dataPacketDto2.toDataPacket()).thenReturn(null);
        Assert.assertEquals(transactionCompletion, receiverSupplier.get().receiveFiles());
        objectMapper.writeValue(expectedData, Arrays.asList(dataPacketDto, dataPacketDto2));
        Assert.assertEquals(expectedData.toString(), data.toString());
    }
}

