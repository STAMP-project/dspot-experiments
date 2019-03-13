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


import MllpAcknowledgementTimeoutException.EXCEPTION_MESSAGE;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the  class.
 */
public class MllpAcknowledgementTimeoutExceptionTest extends MllpExceptionTestSupport {
    static final String ALTERNATE_EXCEPTION_MESSAGE = "Test Message";

    MllpAcknowledgementTimeoutException instance;

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testConstructorOne() throws Exception {
        instance = new MllpAcknowledgementTimeoutException(MllpExceptionTestSupport.HL7_MESSAGE_BYTES);
        Assert.assertTrue(instance.getMessage().startsWith(EXCEPTION_MESSAGE));
        Assert.assertNull(instance.getCause());
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, instance.hl7MessageBytes);
        Assert.assertNull(instance.hl7AcknowledgementBytes);
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testConstructorTwo() throws Exception {
        instance = new MllpAcknowledgementTimeoutException(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES);
        Assert.assertTrue(instance.getMessage().startsWith(EXCEPTION_MESSAGE));
        Assert.assertNull(instance.getCause());
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, instance.hl7MessageBytes);
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES, instance.hl7AcknowledgementBytes);
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testConstructorThree() throws Exception {
        instance = new MllpAcknowledgementTimeoutException(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTestSupport.CAUSE);
        Assert.assertTrue(instance.getMessage().startsWith(EXCEPTION_MESSAGE));
        Assert.assertSame(MllpExceptionTestSupport.CAUSE, instance.getCause());
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, instance.hl7MessageBytes);
        Assert.assertNull(instance.hl7AcknowledgementBytes);
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testConstructorFour() throws Exception {
        instance = new MllpAcknowledgementTimeoutException(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES, MllpExceptionTestSupport.CAUSE);
        Assert.assertTrue(instance.getMessage().startsWith(EXCEPTION_MESSAGE));
        Assert.assertSame(MllpExceptionTestSupport.CAUSE, instance.getCause());
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, instance.hl7MessageBytes);
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES, instance.hl7AcknowledgementBytes);
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testConstructorFive() throws Exception {
        instance = new MllpAcknowledgementTimeoutException(MllpAcknowledgementTimeoutExceptionTest.ALTERNATE_EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES);
        Assert.assertTrue(instance.getMessage().startsWith(MllpAcknowledgementTimeoutExceptionTest.ALTERNATE_EXCEPTION_MESSAGE));
        Assert.assertNull(instance.getCause());
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, instance.hl7MessageBytes);
        Assert.assertNull(instance.hl7AcknowledgementBytes);
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testConstructorSix() throws Exception {
        instance = new MllpAcknowledgementTimeoutException(MllpAcknowledgementTimeoutExceptionTest.ALTERNATE_EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES);
        Assert.assertTrue(instance.getMessage().startsWith(MllpAcknowledgementTimeoutExceptionTest.ALTERNATE_EXCEPTION_MESSAGE));
        Assert.assertNull(instance.getCause());
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, instance.hl7MessageBytes);
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES, instance.hl7AcknowledgementBytes);
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testConstructorSeven() throws Exception {
        instance = new MllpAcknowledgementTimeoutException(MllpAcknowledgementTimeoutExceptionTest.ALTERNATE_EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTestSupport.CAUSE);
        Assert.assertTrue(instance.getMessage().startsWith(MllpAcknowledgementTimeoutExceptionTest.ALTERNATE_EXCEPTION_MESSAGE));
        Assert.assertSame(MllpExceptionTestSupport.CAUSE, instance.getCause());
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, instance.hl7MessageBytes);
        Assert.assertNull(instance.hl7AcknowledgementBytes);
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testConstructorEight() throws Exception {
        instance = new MllpAcknowledgementTimeoutException(MllpAcknowledgementTimeoutExceptionTest.ALTERNATE_EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES, MllpExceptionTestSupport.CAUSE);
        Assert.assertTrue(instance.getMessage().startsWith(MllpAcknowledgementTimeoutExceptionTest.ALTERNATE_EXCEPTION_MESSAGE));
        Assert.assertSame(MllpExceptionTestSupport.CAUSE, instance.getCause());
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_MESSAGE_BYTES, instance.hl7MessageBytes);
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES, instance.hl7AcknowledgementBytes);
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testGetHl7Acknowledgement() throws Exception {
        instance = new MllpAcknowledgementTimeoutException(MllpAcknowledgementTimeoutExceptionTest.ALTERNATE_EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES, MllpExceptionTestSupport.CAUSE);
        Assert.assertArrayEquals(MllpExceptionTestSupport.HL7_ACKNOWLEDGEMENT_BYTES, instance.getHl7Acknowledgement());
        instance = new MllpAcknowledgementTimeoutException(MllpAcknowledgementTimeoutExceptionTest.ALTERNATE_EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, new byte[0], MllpExceptionTestSupport.CAUSE);
        Assert.assertNull(instance.getHl7Acknowledgement());
        instance = new MllpAcknowledgementTimeoutException(MllpAcknowledgementTimeoutExceptionTest.ALTERNATE_EXCEPTION_MESSAGE, MllpExceptionTestSupport.HL7_MESSAGE_BYTES, MllpExceptionTestSupport.CAUSE);
        Assert.assertNull(instance.getHl7Acknowledgement());
    }
}

