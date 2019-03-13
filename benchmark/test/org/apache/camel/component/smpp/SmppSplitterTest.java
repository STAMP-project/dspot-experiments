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
package org.apache.camel.component.smpp;


import org.junit.Assert;
import org.junit.Test;

import static SmppSplitter.MAX_MSG_BYTE_LENGTH;
import static SmppSplitter.UDHIE_HEADER_LENGTH;
import static SmppSplitter.UDHIE_HEADER_REAL_LENGTH;
import static SmppSplitter.UDHIE_IDENTIFIER_SAR;
import static SmppSplitter.UDHIE_SAR_LENGTH;


public class SmppSplitterTest {
    @Test
    public void splitShortMessageWith160Character() {
        String message = "12345678901234567890123456789012345678901234567890123456789012345678901234567890" + "12345678901234567890123456789012345678901234567890123456789012345678901234567890";
        int messageLength = ((MAX_MSG_BYTE_LENGTH) * 8) / 7;// 160

        int segmentLength = (((MAX_MSG_BYTE_LENGTH) - (UDHIE_HEADER_REAL_LENGTH)) * 8) / 7;// 153

        int currentLength = message.length();
        SmppSplitter splitter = new SmppSplitter(messageLength, segmentLength, currentLength);
        SmppSplitter.resetCurrentReferenceNumber();
        byte[][] result = splitter.split(message.getBytes());
        Assert.assertEquals(1, result.length);
        Assert.assertArrayEquals(new byte[]{ 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48 }, result[0]);
        Assert.assertEquals(message, new String(result[0]));
    }

    @Test
    public void splitShortMessageWith161Character() {
        String message = "12345678901234567890123456789012345678901234567890123456789012345678901234567890" + "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901";
        int messageLength = ((MAX_MSG_BYTE_LENGTH) * 8) / 7;// 160

        int segmentLength = (((MAX_MSG_BYTE_LENGTH) - (UDHIE_HEADER_REAL_LENGTH)) * 8) / 7;// 153

        int currentLength = message.length();
        SmppSplitter splitter = new SmppSplitter(messageLength, segmentLength, currentLength);
        SmppSplitter.resetCurrentReferenceNumber();
        byte[][] result = splitter.split(message.getBytes());
        Assert.assertEquals(2, result.length);
        Assert.assertArrayEquals(new byte[]{ UDHIE_HEADER_LENGTH, UDHIE_IDENTIFIER_SAR, UDHIE_SAR_LENGTH, 1, 2, 1, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51 }, result[0]);
        Assert.assertArrayEquals(new byte[]{ UDHIE_HEADER_LENGTH, UDHIE_IDENTIFIER_SAR, UDHIE_SAR_LENGTH, 1, 2, 2, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49 }, result[1]);
        String firstShortMessage = new String(result[0], UDHIE_HEADER_REAL_LENGTH, ((result[0].length) - (UDHIE_HEADER_REAL_LENGTH)));
        String secondShortMessage = new String(result[1], UDHIE_HEADER_REAL_LENGTH, ((result[1].length) - (UDHIE_HEADER_REAL_LENGTH)));
        Assert.assertEquals(message, (firstShortMessage + secondShortMessage));
    }
}

