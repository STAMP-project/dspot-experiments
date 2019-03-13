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


import OpenstackConstants.CREATE;
import OpenstackConstants.NAME;
import OpenstackConstants.OPERATION;
import org.apache.camel.component.openstack.AbstractProducerTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.openstack4j.api.compute.KeypairService;
import org.openstack4j.model.compute.Keypair;


@RunWith(MockitoJUnitRunner.class)
public class KeypairProducerTest extends NovaProducerTestSupport {
    private static final String KEYPAIR_NAME = "keypairName";

    @Mock
    private Keypair osTestKeypair;

    private Keypair dummyKeypair;

    @Mock
    private KeypairService keypairService;

    @Captor
    private ArgumentCaptor<String> nameCaptor;

    @Captor
    private ArgumentCaptor<String> keypairCaptor;

    @Test
    public void createKeypair() throws Exception {
        final String fingerPrint = "fp";
        final String privatecKey = "prk";
        Mockito.when(osTestKeypair.getName()).thenReturn(KeypairProducerTest.KEYPAIR_NAME);
        Mockito.when(osTestKeypair.getPublicKey()).thenReturn(dummyKeypair.getPublicKey());
        Mockito.when(osTestKeypair.getFingerprint()).thenReturn(fingerPrint);
        Mockito.when(osTestKeypair.getPrivateKey()).thenReturn(privatecKey);
        msg.setHeader(OPERATION, CREATE);
        msg.setHeader(NAME, KeypairProducerTest.KEYPAIR_NAME);
        producer.process(exchange);
        Mockito.verify(keypairService).create(nameCaptor.capture(), keypairCaptor.capture());
        Assert.assertEquals(KeypairProducerTest.KEYPAIR_NAME, nameCaptor.getValue());
        Assert.assertNull(keypairCaptor.getValue());
        Keypair result = msg.getBody(Keypair.class);
        Assert.assertEquals(fingerPrint, result.getFingerprint());
        Assert.assertEquals(privatecKey, result.getPrivateKey());
        Assert.assertEquals(dummyKeypair.getName(), result.getName());
        Assert.assertEquals(dummyKeypair.getPublicKey(), result.getPublicKey());
    }

    @Test
    public void createKeypairFromExisting() throws Exception {
        msg.setHeader(OPERATION, CREATE);
        msg.setHeader(NAME, KeypairProducerTest.KEYPAIR_NAME);
        String key = "existing public key string";
        Mockito.when(osTestKeypair.getPublicKey()).thenReturn(key);
        msg.setBody(key);
        producer.process(exchange);
        Mockito.verify(keypairService).create(nameCaptor.capture(), keypairCaptor.capture());
        Assert.assertEquals(KeypairProducerTest.KEYPAIR_NAME, nameCaptor.getValue());
        Assert.assertEquals(key, keypairCaptor.getValue());
        Keypair result = msg.getBody(Keypair.class);
        Assert.assertEquals(dummyKeypair.getName(), result.getName());
        Assert.assertEquals(dummyKeypair.getFingerprint(), result.getFingerprint());
        Assert.assertEquals(dummyKeypair.getPrivateKey(), result.getPrivateKey());
        Assert.assertEquals(key, result.getPublicKey());
    }
}

