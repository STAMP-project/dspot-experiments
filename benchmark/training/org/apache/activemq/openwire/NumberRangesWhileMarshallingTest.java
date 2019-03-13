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
package org.apache.activemq.openwire;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import junit.framework.TestCase;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class NumberRangesWhileMarshallingTest extends TestCase {
    private static final Logger LOG = LoggerFactory.getLogger(NumberRangesWhileMarshallingTest.class);

    protected String connectionId = "Cheese";

    protected ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    protected DataOutputStream ds = new DataOutputStream(buffer);

    protected OpenWireFormat openWireformat;

    protected int endOfStreamMarker = 305419896;

    public void testLongNumberRanges() throws Exception {
        long[] numberValues = new long[]{ // bytes
        0, 1, 126, 127, 128, 129, 240, 255, // shorts
        32511, 32767L, 32769L, 32768L, 57344L, 917505L, 65280L, 65535L, // ints
        65536L, 7340032L, 305419896L, 1916032632L, 2147483647L, 2147483648L, 2147483649L, 3758096385L, 4294967295L, // 3 byte longs
        4886718337L, 78187493394L, 1250999894307L, 20015998308916L, 320255972942661L, 5124095567082582L, 35523393051833430L, 36028797018963967L, 36028797018963968L, 36028797018963969L, 63050394783186945L, 72057594037927935L, // 4 byte longs
        1311768465173141112L, 9223372036854775807L, -9223372036854775808L, -9223372036854775807L, -2305843009213693951L, -1L, 1 };
        for (int i = 0; i < (numberValues.length); i++) {
            long value = numberValues[i];
            SessionId object = new SessionId();
            object.setConnectionId(connectionId);
            object.setValue(value);
            writeObject(object);
        }
        ds.writeInt(endOfStreamMarker);
        // now lets read from the stream
        ds.close();
        ByteArrayInputStream in = new ByteArrayInputStream(buffer.toByteArray());
        DataInputStream dis = new DataInputStream(in);
        for (int i = 0; i < (numberValues.length); i++) {
            long value = numberValues[i];
            String expected = Long.toHexString(value);
            NumberRangesWhileMarshallingTest.LOG.info(((("Unmarshaling value: " + i) + " = ") + expected));
            SessionId command = ((SessionId) (openWireformat.unmarshal(dis)));
            TestCase.assertEquals(("connection ID in object: " + i), connectionId, command.getConnectionId());
            String actual = Long.toHexString(command.getValue());
            TestCase.assertEquals(((("value of object: " + i) + " was: ") + actual), expected, actual);
        }
        int marker = dis.readInt();
        TestCase.assertEquals("Marker int", Integer.toHexString(endOfStreamMarker), Integer.toHexString(marker));
        // lets try read and we should get an exception
        try {
            byte value = dis.readByte();
            TestCase.fail(("Should have reached the end of the stream: " + value));
        } catch (IOException e) {
            // worked!
        }
    }

    public void testMaxFrameSize() throws Exception {
        OpenWireFormat wf = new OpenWireFormat();
        wf.setMaxFrameSize(10);
        ActiveMQTextMessage msg = new ActiveMQTextMessage();
        msg.setText("This is a test");
        writeObject(msg);
        ds.writeInt(endOfStreamMarker);
        // now lets read from the stream
        ds.close();
        ByteArrayInputStream in = new ByteArrayInputStream(buffer.toByteArray());
        DataInputStream dis = new DataInputStream(in);
        try {
            wf.unmarshal(dis);
        } catch (IOException ioe) {
            return;
        }
        TestCase.fail("Should fail because of the large frame size");
    }

    public void testDefaultMaxFrameSizeUnlimited() {
        OpenWireFormat wf = new OpenWireFormat();
        TestCase.assertEquals(Long.MAX_VALUE, wf.getMaxFrameSize());
    }
}

