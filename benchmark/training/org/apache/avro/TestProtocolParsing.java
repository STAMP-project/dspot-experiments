/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro;


import Schema.Field;
import java.io.IOException;
import org.apache.avro.Protocol.Message;
import org.junit.Assert;
import org.junit.Test;


public class TestProtocolParsing {
    @Test
    public void testParsing() throws IOException {
        Protocol protocol = TestProtocolParsing.getSimpleProtocol();
        Assert.assertEquals(protocol.getDoc(), "Protocol used for testing.");
        Assert.assertEquals(6, protocol.getMessages().size());
        Assert.assertEquals("Pretend you're in a cave!", protocol.getMessages().get("echo").getDoc());
    }

    @Test
    public void oneWay() throws Exception {
        Message m;
        // permit one-way messages w/ null response
        m = TestProtocolParsing.parseMessage(("\"ack\": {" + (("\"request\": []," + "\"response\": \"null\",") + "\"one-way\": true}")));
        Assert.assertTrue(m.isOneWay());
        // permit one-way messages w/o response
        m = TestProtocolParsing.parseMessage(("\"ack\": {" + ("\"request\": []," + "\"one-way\": true}")));
        Assert.assertTrue(m.isOneWay());
    }

    @Test(expected = SchemaParseException.class)
    public void oneWayResponse() throws Exception {
        // prohibit one-way messages with a non-null response type
        TestProtocolParsing.parseMessage(("\"ack\": {" + (("\"request\": [\"string\"]," + "\"response\": \"string\",") + "\"one-way\": true}")));
    }

    @Test(expected = SchemaParseException.class)
    public void oneWayError() throws Exception {
        // prohibit one-way messages with errors
        TestProtocolParsing.parseMessage(("\"ack\": {" + (("\"request\": [\"string\"]," + "\"errors\": [],") + "\"one-way\": true}")));
    }

    @Test
    public void testMessageFieldAliases() throws IOException {
        Protocol protocol = TestProtocolParsing.getSimpleProtocol();
        final Message msg = protocol.getMessages().get("hello");
        Assert.assertNotNull(msg);
        final Schema.Field field = msg.getRequest().getField("greeting");
        Assert.assertNotNull(field);
        Assert.assertTrue(field.aliases().contains("salute"));
    }

    @Test
    public void testMessageCustomProperties() throws IOException {
        Protocol protocol = TestProtocolParsing.getSimpleProtocol();
        final Message msg = protocol.getMessages().get("hello");
        Assert.assertNotNull(msg);
        final Schema.Field field = msg.getRequest().getField("greeting");
        Assert.assertNotNull(field);
        Assert.assertEquals("customValue", field.getProp("customProp"));
    }
}

