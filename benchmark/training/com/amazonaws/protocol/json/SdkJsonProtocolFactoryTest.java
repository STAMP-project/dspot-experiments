/**
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not
 * use this file except in compliance with the License. A copy of the License is
 * located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.protocol.json;


import org.junit.Assert;
import org.junit.Test;


public class SdkJsonProtocolFactoryTest {
    @Test
    public void ionBinaryEnabledGeneratorWritesIonBinary() {
        StructuredJsonGenerator generator = protocolFactory(SdkJsonProtocolFactoryTest.IonEnabled.YES, SdkJsonProtocolFactoryTest.IonBinaryEnabled.YES).createGenerator();
        generator.writeValue(true);
        byte[] actual = generator.getBytes();
        byte[] expected = SdkJsonProtocolFactoryTest.bytes(224, 1, 0, 234, 17);
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void ionBinaryDisabledGeneratorWritesIonText() throws Exception {
        StructuredJsonGenerator generator = protocolFactory(SdkJsonProtocolFactoryTest.IonEnabled.YES, SdkJsonProtocolFactoryTest.IonBinaryEnabled.NO).createGenerator();
        generator.writeValue(true);
        byte[] actual = generator.getBytes();
        byte[] expected = "true".getBytes("UTF-8");
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void ionBinaryEnabledUsesIonBinaryContentType() {
        SdkJsonProtocolFactory protocolFactory = protocolFactory(SdkJsonProtocolFactoryTest.IonEnabled.YES, SdkJsonProtocolFactoryTest.IonBinaryEnabled.YES);
        Assert.assertEquals("application/x-amz-ion-1.0", protocolFactory.getContentType());
    }

    @Test
    public void ionBinaryDisabledUsesIonTextContentType() {
        SdkJsonProtocolFactory protocolFactory = protocolFactory(SdkJsonProtocolFactoryTest.IonEnabled.YES, SdkJsonProtocolFactoryTest.IonBinaryEnabled.NO);
        Assert.assertEquals("text/x-amz-ion-1.0", protocolFactory.getContentType());
    }

    private enum IonEnabled {

        YES,
        NO;}

    private enum IonBinaryEnabled {

        YES,
        NO;}
}

