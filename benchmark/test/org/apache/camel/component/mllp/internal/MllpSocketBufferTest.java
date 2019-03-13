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
package org.apache.camel.component.mllp.internal;


import MllpProtocolConstants.END_OF_BLOCK;
import MllpProtocolConstants.END_OF_DATA;
import MllpProtocolConstants.START_OF_BLOCK;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the MllpSocketBuffer class.
 */
public class MllpSocketBufferTest extends SocketBufferTestSupport {
    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testConstructorWithNullEndpoing() throws Exception {
        try {
            new MllpSocketBuffer(null);
            Assert.fail("Constructor should have thrown an exception with a null Endpoint argument");
        } catch (IllegalArgumentException expectedEx) {
            Assert.assertEquals("MllpEndpoint cannot be null", expectedEx.getMessage());
        }
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testToHl7StringWithRequiredEndOfData() throws Exception {
        Assert.assertNull(instance.toHl7String());
        instance.write(buildTestBytes(true, true, true));
        Assert.assertEquals(SocketBufferTestSupport.TEST_HL7_MESSAGE, instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(true, true, false));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(true, false, false));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(false, true, true));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(false, true, false));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(false, false, false));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(null, true, true, true));
        Assert.assertEquals("", instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(null, true, true, false));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(null, true, false, false));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(null, false, true, true));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(null, false, true, false));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(null, false, false, false));
        Assert.assertNull(instance.toHl7String());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testToHl7StringWithOptionalEndOfData() throws Exception {
        endpoint.setRequireEndOfData(false);
        Assert.assertNull(instance.toHl7String());
        instance.write(buildTestBytes(true, true, true));
        Assert.assertEquals(SocketBufferTestSupport.TEST_HL7_MESSAGE, instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(true, true, false));
        Assert.assertEquals(SocketBufferTestSupport.TEST_HL7_MESSAGE, instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(true, false, false));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(false, true, true));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(false, true, false));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(false, false, false));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(null, true, true, true));
        Assert.assertEquals("", instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(null, true, true, false));
        Assert.assertEquals("", instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(null, true, false, false));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(null, false, true, true));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(null, false, true, false));
        Assert.assertNull(instance.toHl7String());
        instance.reset();
        instance.write(buildTestBytes(null, false, false, false));
        Assert.assertNull(instance.toHl7String());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testToHl7StringWithInvalidCharset() throws Exception {
        instance.write(buildTestBytes(true, true, true));
        Assert.assertEquals(SocketBufferTestSupport.TEST_HL7_MESSAGE, instance.toHl7String("FOO"));
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testToMllpPayloadWithRequiredEndOfData() throws Exception {
        Assert.assertNull(instance.toMllpPayload());
        instance.write(buildTestBytes(true, true, true));
        Assert.assertArrayEquals(SocketBufferTestSupport.TEST_HL7_MESSAGE.getBytes(), instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(true, true, false));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(true, false, false));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(false, true, true));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(false, true, false));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(false, false, false));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(null, true, true, true));
        Assert.assertArrayEquals(new byte[0], instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(null, true, true, false));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(null, true, false, false));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(null, false, true, true));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(null, false, true, false));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(null, false, false, false));
        Assert.assertNull(instance.toMllpPayload());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testToMllpPayloadWithOptionalEndOfData() throws Exception {
        endpoint.setRequireEndOfData(false);
        Assert.assertNull(instance.toMllpPayload());
        instance.write(buildTestBytes(true, true, true));
        Assert.assertArrayEquals(SocketBufferTestSupport.TEST_HL7_MESSAGE.getBytes(), instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(true, true, false));
        Assert.assertArrayEquals(SocketBufferTestSupport.TEST_HL7_MESSAGE.getBytes(), instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(true, false, false));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(false, true, true));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(false, true, false));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(false, false, false));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(null, true, true, true));
        Assert.assertArrayEquals(new byte[0], instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(null, true, true, false));
        Assert.assertArrayEquals(new byte[0], instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(null, true, false, false));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(null, false, true, true));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(null, false, true, false));
        Assert.assertNull(instance.toMllpPayload());
        instance.reset();
        instance.write(buildTestBytes(null, false, false, false));
        Assert.assertNull(instance.toMllpPayload());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testGetStartOfBlockIndex() throws Exception {
        int expected = -1;
        Assert.assertEquals("Unexpected initial value", expected, instance.getStartOfBlockIndex());
        expected = 0;
        instance.startOfBlockIndex = expected;
        Assert.assertEquals(expected, instance.getStartOfBlockIndex());
        expected = 5;
        instance.startOfBlockIndex = expected;
        Assert.assertEquals(expected, instance.getStartOfBlockIndex());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void tesGgetEndOfBlockIndex() throws Exception {
        int expected = -1;
        Assert.assertEquals("Unexpected initial value", expected, instance.getEndOfBlockIndex());
        expected = 0;
        instance.endOfBlockIndex = expected;
        Assert.assertEquals(expected, instance.getEndOfBlockIndex());
        expected = 5;
        instance.endOfBlockIndex = expected;
        Assert.assertEquals(expected, instance.getEndOfBlockIndex());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testHasCompleteEnvelopeWithRequiredEndOfData() throws Exception {
        endpoint.setRequireEndOfData(true);
        Assert.assertFalse("Unexpected initial value", instance.hasCompleteEnvelope());
        instance.write(START_OF_BLOCK);
        Assert.assertFalse(instance.hasCompleteEnvelope());
        instance.write(SocketBufferTestSupport.TEST_HL7_MESSAGE.getBytes());
        Assert.assertFalse(instance.hasCompleteEnvelope());
        instance.write(END_OF_BLOCK);
        Assert.assertFalse(instance.hasCompleteEnvelope());
        instance.write(END_OF_DATA);
        Assert.assertTrue(instance.hasCompleteEnvelope());
        instance.reset();
        Assert.assertFalse(instance.hasCompleteEnvelope());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testHasCompleteEnvelopeWithOptionalEndOfData() throws Exception {
        endpoint.setRequireEndOfData(false);
        Assert.assertFalse("Unexpected initial value", instance.hasCompleteEnvelope());
        instance.write(START_OF_BLOCK);
        Assert.assertFalse(instance.hasCompleteEnvelope());
        instance.write(SocketBufferTestSupport.TEST_HL7_MESSAGE.getBytes());
        Assert.assertFalse(instance.hasCompleteEnvelope());
        instance.write(END_OF_BLOCK);
        Assert.assertTrue(instance.hasCompleteEnvelope());
        instance.write(END_OF_DATA);
        Assert.assertTrue(instance.hasCompleteEnvelope());
        instance.reset();
        Assert.assertFalse(instance.hasCompleteEnvelope());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testHasStartOfBlock() throws Exception {
        Assert.assertFalse("Unexpected initial value", instance.hasStartOfBlock());
        instance.write(START_OF_BLOCK);
        Assert.assertTrue(instance.hasStartOfBlock());
        instance.reset();
        Assert.assertFalse(instance.hasStartOfBlock());
        instance.write(SocketBufferTestSupport.TEST_HL7_MESSAGE.getBytes());
        Assert.assertFalse(instance.hasStartOfBlock());
        instance.write(START_OF_BLOCK);
        Assert.assertTrue(instance.hasStartOfBlock());
        instance.write(SocketBufferTestSupport.TEST_HL7_MESSAGE.getBytes());
        Assert.assertTrue(instance.hasStartOfBlock());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testHasEndOfBlock() throws Exception {
        Assert.assertFalse("Unexpected initial value", instance.hasEndOfBlock());
        instance.write(END_OF_BLOCK);
        Assert.assertFalse("START_OF_BLOCK before an END_OF_BLOCK", instance.hasEndOfBlock());
        instance.reset();
        Assert.assertFalse(instance.hasEndOfBlock());
        instance.write(START_OF_BLOCK);
        Assert.assertFalse(instance.hasEndOfBlock());
        instance.write(SocketBufferTestSupport.TEST_HL7_MESSAGE.getBytes());
        Assert.assertFalse(instance.hasEndOfBlock());
        instance.write(END_OF_BLOCK);
        Assert.assertTrue(instance.hasEndOfBlock());
        instance.write(END_OF_DATA);
        Assert.assertTrue(instance.hasEndOfBlock());
        instance.reset();
        Assert.assertFalse(instance.hasEndOfBlock());
        instance.write(START_OF_BLOCK);
        Assert.assertFalse(instance.hasEndOfBlock());
        instance.write(SocketBufferTestSupport.TEST_HL7_MESSAGE.getBytes());
        Assert.assertFalse(instance.hasEndOfBlock());
        instance.write(END_OF_BLOCK);
        Assert.assertTrue(instance.hasEndOfBlock());
        instance.write("BLAH".getBytes());
        Assert.assertTrue(instance.hasEndOfBlock());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testHasEndOfData() throws Exception {
        Assert.assertFalse("Unexpected initial value", instance.hasEndOfData());
        // Test just the END_OF_DATA
        instance.write(END_OF_DATA);
        Assert.assertFalse(instance.hasEndOfData());
        instance.reset();
        Assert.assertFalse(instance.hasEndOfData());
        // Test just the terminators
        instance.write(END_OF_BLOCK);
        Assert.assertFalse(instance.hasEndOfData());
        instance.write(END_OF_DATA);
        Assert.assertFalse("Need a START_OF_BLOCK before the END_OF_DATA", instance.hasEndOfData());
        instance.reset();
        Assert.assertFalse(instance.hasEndOfData());
        instance.write(START_OF_BLOCK);
        Assert.assertFalse(instance.hasEndOfData());
        instance.write(SocketBufferTestSupport.TEST_HL7_MESSAGE.getBytes());
        Assert.assertFalse(instance.hasEndOfData());
        instance.write(END_OF_BLOCK);
        Assert.assertFalse(instance.hasEndOfData());
        instance.write(END_OF_DATA);
        Assert.assertTrue(instance.hasEndOfData());
        instance.reset();
        Assert.assertFalse(instance.hasEndOfData());
        instance.write(START_OF_BLOCK);
        Assert.assertFalse(instance.hasEndOfData());
        instance.write(SocketBufferTestSupport.TEST_HL7_MESSAGE.getBytes());
        Assert.assertFalse(instance.hasEndOfData());
        instance.write(END_OF_BLOCK);
        Assert.assertFalse(instance.hasEndOfData());
        instance.write("BLAH".getBytes());
        Assert.assertFalse(instance.hasEndOfData());
        instance.write(END_OF_DATA);
        Assert.assertFalse(instance.hasEndOfData());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testhasOutOfBandData() throws Exception {
        Assert.assertFalse("Unexpected initial value", instance.hasOutOfBandData());
        instance.write(buildTestBytes(true, true, true));
        Assert.assertFalse(instance.hasOutOfBandData());
        instance.write("BLAH".getBytes());
        Assert.assertTrue(instance.hasOutOfBandData());
        instance.reset();
        Assert.assertFalse(instance.hasOutOfBandData());
        instance.write("BLAH".getBytes());
        instance.write(buildTestBytes(true, true, true));
        Assert.assertTrue(instance.hasOutOfBandData());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testHasLeadingOutOfBandData() throws Exception {
        Assert.assertFalse("Unexpected initial value", instance.hasLeadingOutOfBandData());
        instance.write(buildTestBytes(true, true, true));
        Assert.assertFalse(instance.hasLeadingOutOfBandData());
        instance.write("BLAH".getBytes());
        Assert.assertFalse(instance.hasLeadingOutOfBandData());
        instance.reset();
        Assert.assertFalse(instance.hasLeadingOutOfBandData());
        instance.write("BLAH".getBytes());
        instance.write(buildTestBytes(true, true, true));
        Assert.assertTrue(instance.hasLeadingOutOfBandData());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testHasTrailingOutOfBandDataWithRequiredEndOfData() throws Exception {
        endpoint.setRequireEndOfData(true);
        Assert.assertFalse("Unexpected initial value", instance.hasTrailingOutOfBandData());
        instance.write(buildTestBytes(true, true, true));
        Assert.assertFalse(instance.hasTrailingOutOfBandData());
        instance.write("BLAH".getBytes());
        Assert.assertTrue(instance.hasTrailingOutOfBandData());
        instance.reset();
        Assert.assertFalse(instance.hasTrailingOutOfBandData());
        instance.write(buildTestBytes(true, true, false));
        Assert.assertFalse(instance.hasTrailingOutOfBandData());
        instance.write("BLAH".getBytes());
        Assert.assertFalse(instance.hasTrailingOutOfBandData());
        // Test with leading out-of-band data
        instance.reset();
        instance.write("BLAH".getBytes());
        instance.write(buildTestBytes(true, true, true));
        Assert.assertFalse(instance.hasTrailingOutOfBandData());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testHasTrailingOutOfBandDataWithOptionalEndOfData() throws Exception {
        endpoint.setRequireEndOfData(false);
        Assert.assertFalse("Unexpected initial value", instance.hasTrailingOutOfBandData());
        instance.write(buildTestBytes(true, true, true));
        Assert.assertFalse(instance.hasTrailingOutOfBandData());
        instance.write("BLAH".getBytes());
        Assert.assertTrue(instance.hasTrailingOutOfBandData());
        instance.reset();
        Assert.assertFalse(instance.hasTrailingOutOfBandData());
        instance.write(buildTestBytes(true, true, false));
        Assert.assertFalse(instance.hasTrailingOutOfBandData());
        instance.write("BLAH".getBytes());
        Assert.assertTrue(instance.hasTrailingOutOfBandData());
        // Test with leading out-of-band data
        instance.reset();
        instance.write("BLAH".getBytes());
        instance.write(buildTestBytes(true, true, true));
        Assert.assertFalse(instance.hasTrailingOutOfBandData());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testGetLeadingOutOfBandData() throws Exception {
        Assert.assertNull("Unexpected initial value", instance.getLeadingOutOfBandData());
        instance.write(buildTestBytes(true, true, true));
        Assert.assertNull(instance.getLeadingOutOfBandData());
        instance.write("BLAH".getBytes());
        Assert.assertNull(instance.getLeadingOutOfBandData());
        instance.reset();
        Assert.assertNull(instance.getLeadingOutOfBandData());
        byte[] expected = "BLAH".getBytes();
        instance.write(expected);
        instance.write(buildTestBytes(true, true, true));
        Assert.assertArrayEquals(expected, instance.getLeadingOutOfBandData());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testGetTrailingOutOfBandDataWithRequiredEndOfData() throws Exception {
        endpoint.setRequireEndOfData(true);
        Assert.assertNull("Unexpected initial value", instance.getTrailingOutOfBandData());
        // Test with END_OF_DATA
        instance.write(buildTestBytes(true, true, true));
        Assert.assertNull(instance.getTrailingOutOfBandData());
        byte[] expected = "BLAH".getBytes();
        instance.write(expected);
        Assert.assertArrayEquals(expected, instance.getTrailingOutOfBandData());
        instance.reset();
        Assert.assertNull(instance.getTrailingOutOfBandData());
        // Test without END_OF_DATA
        instance.write(buildTestBytes(true, true, false));
        instance.write(expected);
        Assert.assertNull(instance.getTrailingOutOfBandData());
        // Test without END_OF_BLOCK
        instance.reset();
        instance.write(expected);
        Assert.assertNull(instance.getTrailingOutOfBandData());
        // Test without envelope termination
        instance.reset();
        instance.write(buildTestBytes(true, false, false));
        Assert.assertNull(instance.getTrailingOutOfBandData());
        instance.reset();
        // Test with END_OF_DATA
        instance.write(expected);
        instance.write(buildTestBytes(true, true, true));
        Assert.assertNull(instance.getTrailingOutOfBandData());
        // Test without END_OF_DATA
        instance.reset();
        instance.write(buildTestBytes(true, true, true));
        Assert.assertNull(instance.getTrailingOutOfBandData());
    }

    /**
     * Description of test.
     *
     * @throws Exception
     * 		in the event of a test error.
     */
    @Test
    public void testGetTrailingOutOfBandDataWithOptionalEndOfData() throws Exception {
        endpoint.setRequireEndOfData(false);
        Assert.assertNull("Unexpected initial value", instance.getTrailingOutOfBandData());
        // Test with END_OF_DATA
        instance.write(buildTestBytes(true, true, true));
        Assert.assertNull(instance.getTrailingOutOfBandData());
        byte[] expected = "BLAH".getBytes();
        instance.write(expected);
        Assert.assertArrayEquals(expected, instance.getTrailingOutOfBandData());
        instance.reset();
        Assert.assertNull(instance.getTrailingOutOfBandData());
        // Test without END_OF_DATA
        instance.write(buildTestBytes(true, true, false));
        instance.write(expected);
        Assert.assertArrayEquals(expected, instance.getTrailingOutOfBandData());
        // Test without END_OF_BLOCK
        instance.reset();
        instance.write(buildTestBytes(true, false, true));
        Assert.assertNull(instance.getTrailingOutOfBandData());
        // Test without envelope termination
        instance.reset();
        instance.write(buildTestBytes(true, false, false));
        Assert.assertNull(instance.getTrailingOutOfBandData());
        instance.reset();
        // Test with END_OF_DATA
        instance.write(expected);
        instance.write(buildTestBytes(true, true, true));
        Assert.assertNull(instance.getTrailingOutOfBandData());
        // Test without END_OF_DATA
        instance.reset();
        instance.write(buildTestBytes(true, true, true));
        Assert.assertNull(instance.getTrailingOutOfBandData());
    }
}

