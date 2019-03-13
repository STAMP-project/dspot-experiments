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
package org.apache.camel.component.mllp;


import MllpComponent.MLLP_LOG_PHI_PROPERTY;
import org.junit.Assert;
import org.junit.Test;


public class MllpExceptionTest extends MllpExceptionTestSupport {
    static final String EXCEPTION_MESSAGE = "Test MllpException";

    static final byte[] NULL_BYTE_ARRAY = null;

    static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    MllpException instance;

    @Test
    public void testGetHl7MessageBytes() throws Exception {
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE);
        Assert.assertNull(instance.getHl7MessageBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.NULL_BYTE_ARRAY);
        Assert.assertNull(instance.getHl7MessageBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.NULL_BYTE_ARRAY, MllpExceptionTest.NULL_BYTE_ARRAY);
        Assert.assertNull(instance.getHl7MessageBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.NULL_BYTE_ARRAY, MllpExceptionTest.EMPTY_BYTE_ARRAY);
        Assert.assertNull(instance.getHl7MessageBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.EMPTY_BYTE_ARRAY);
        Assert.assertNull(instance.getHl7MessageBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.EMPTY_BYTE_ARRAY, MllpExceptionTest.NULL_BYTE_ARRAY);
        Assert.assertNull(instance.getHl7MessageBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.EMPTY_BYTE_ARRAY, MllpExceptionTest.EMPTY_BYTE_ARRAY);
        Assert.assertNull(instance.getHl7MessageBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES);
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, instance.getHl7MessageBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTest.NULL_BYTE_ARRAY);
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, instance.getHl7MessageBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTest.EMPTY_BYTE_ARRAY);
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, instance.getHl7MessageBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES);
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, instance.getHl7MessageBytes());
    }

    @Test
    public void testGetHl7AcknowledgementBytes() throws Exception {
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE);
        Assert.assertNull(instance.getHl7AcknowledgementBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.NULL_BYTE_ARRAY);
        Assert.assertNull(instance.getHl7AcknowledgementBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.NULL_BYTE_ARRAY, MllpExceptionTest.NULL_BYTE_ARRAY);
        Assert.assertNull(instance.getHl7AcknowledgementBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.NULL_BYTE_ARRAY, MllpExceptionTest.EMPTY_BYTE_ARRAY);
        Assert.assertNull(instance.getHl7AcknowledgementBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.EMPTY_BYTE_ARRAY);
        Assert.assertNull(instance.getHl7AcknowledgementBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.EMPTY_BYTE_ARRAY, MllpExceptionTest.NULL_BYTE_ARRAY);
        Assert.assertNull(instance.getHl7AcknowledgementBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.EMPTY_BYTE_ARRAY, MllpExceptionTest.EMPTY_BYTE_ARRAY);
        Assert.assertNull(instance.getHl7AcknowledgementBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES);
        Assert.assertNull(instance.getHl7AcknowledgementBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTest.NULL_BYTE_ARRAY);
        Assert.assertNull(instance.getHl7AcknowledgementBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTest.EMPTY_BYTE_ARRAY);
        Assert.assertNull(instance.getHl7AcknowledgementBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES);
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES, instance.getHl7AcknowledgementBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, null, MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES);
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES, instance.getHl7AcknowledgementBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.EMPTY_BYTE_ARRAY, MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES);
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES, instance.getHl7AcknowledgementBytes());
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES);
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES, instance.getHl7AcknowledgementBytes());
    }

    @Test
    public void testNullHl7Message() throws Exception {
        System.setProperty(MLLP_LOG_PHI_PROPERTY, "true");
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, null, MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES);
        Assert.assertEquals(expectedMessage(null, MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT), instance.getMessage());
    }

    @Test
    public void testEmptyHl7Message() throws Exception {
        System.setProperty(MLLP_LOG_PHI_PROPERTY, "true");
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.EMPTY_BYTE_ARRAY, MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES);
        Assert.assertEquals(expectedMessage(null, MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT), instance.getMessage());
    }

    @Test
    public void testNullHl7Acknowledgement() throws Exception {
        System.setProperty(MLLP_LOG_PHI_PROPERTY, "true");
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTest.NULL_BYTE_ARRAY);
        Assert.assertEquals(expectedMessage(MllpExceptionTestSupport.HL7_MESSAGE, null), instance.getMessage());
    }

    @Test
    public void testEmptyAcknowledgement() throws Exception {
        System.setProperty(MLLP_LOG_PHI_PROPERTY, "true");
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTest.EMPTY_BYTE_ARRAY);
        Assert.assertEquals(expectedMessage(MllpExceptionTestSupport.HL7_MESSAGE, null), instance.getMessage());
    }

    @Test
    public void testNullHl7Payloads() throws Exception {
        System.setProperty(MLLP_LOG_PHI_PROPERTY, "true");
        instance = new MllpException(MllpExceptionTest.EXCEPTION_MESSAGE, MllpExceptionTest.NULL_BYTE_ARRAY, MllpExceptionTest.NULL_BYTE_ARRAY);
        Assert.assertEquals(expectedMessage(null, null), instance.getMessage());
    }
}

