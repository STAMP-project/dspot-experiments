/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm;


import HBServerMessageType.CREATE_PATH;
import HBServerMessageType.CREATE_PATH_RESPONSE;
import HBServerMessageType.DELETE_PATH;
import HBServerMessageType.DELETE_PATH_RESPONSE;
import HBServerMessageType.DELETE_PULSE_ID;
import HBServerMessageType.DELETE_PULSE_ID_RESPONSE;
import HBServerMessageType.EXISTS;
import HBServerMessageType.EXISTS_RESPONSE;
import HBServerMessageType.GET_ALL_NODES_FOR_PATH;
import HBServerMessageType.GET_ALL_NODES_FOR_PATH_RESPONSE;
import HBServerMessageType.GET_ALL_PULSE_FOR_PATH;
import HBServerMessageType.GET_ALL_PULSE_FOR_PATH_RESPONSE;
import HBServerMessageType.GET_PULSE;
import HBServerMessageType.GET_PULSE_RESPONSE;
import HBServerMessageType.NOT_AUTHORIZED;
import HBServerMessageType.SEND_PULSE;
import HBServerMessageType.SEND_PULSE_RESPONSE;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;
import org.apache.storm.generated.HBMessage;
import org.apache.storm.generated.HBMessageData;
import org.apache.storm.generated.HBPulse;
import org.apache.storm.pacemaker.Pacemaker;
import org.apache.storm.utils.Utils;
import org.junit.Assert;
import org.junit.Test;


public class PacemakerTest {
    private HBMessage hbMessage;

    private int mid;

    private Random random;

    private Pacemaker handler;

    @Test
    public void testServerCreatePath() {
        messageWithRandId(CREATE_PATH, HBMessageData.path("/testpath"));
        HBMessage response = handler.handleMessage(hbMessage, true);
        Assert.assertEquals(mid, response.get_message_id());
        Assert.assertEquals(CREATE_PATH_RESPONSE, response.get_type());
        Assert.assertNull(response.get_data());
    }

    @Test
    public void testServerExistsFalse() {
        messageWithRandId(EXISTS, HBMessageData.path("/testpath"));
        HBMessage badResponse = handler.handleMessage(hbMessage, false);
        HBMessage goodResponse = handler.handleMessage(hbMessage, true);
        Assert.assertEquals(mid, badResponse.get_message_id());
        Assert.assertEquals(NOT_AUTHORIZED, badResponse.get_type());
        Assert.assertEquals(mid, goodResponse.get_message_id());
        Assert.assertEquals(EXISTS_RESPONSE, goodResponse.get_type());
        Assert.assertFalse(goodResponse.get_data().get_boolval());
    }

    @Test
    public void testServerExistsTrue() {
        String path = "/exists_path";
        String dataString = "pulse data";
        HBPulse hbPulse = new HBPulse();
        hbPulse.set_id(path);
        hbPulse.set_details(Utils.javaSerialize(dataString));
        messageWithRandId(SEND_PULSE, HBMessageData.pulse(hbPulse));
        handler.handleMessage(hbMessage, true);
        messageWithRandId(EXISTS, HBMessageData.path(path));
        HBMessage badResponse = handler.handleMessage(hbMessage, false);
        HBMessage goodResponse = handler.handleMessage(hbMessage, true);
        Assert.assertEquals(mid, badResponse.get_message_id());
        Assert.assertEquals(NOT_AUTHORIZED, badResponse.get_type());
        Assert.assertEquals(mid, goodResponse.get_message_id());
        Assert.assertEquals(EXISTS_RESPONSE, goodResponse.get_type());
        Assert.assertTrue(goodResponse.get_data().get_boolval());
    }

    @Test
    public void testServerSendPulseGetPulse() throws UnsupportedEncodingException {
        String path = "/pulsepath";
        String dataString = "pulse data";
        HBPulse hbPulse = new HBPulse();
        hbPulse.set_id(path);
        hbPulse.set_details(dataString.getBytes("UTF-8"));
        messageWithRandId(SEND_PULSE, HBMessageData.pulse(hbPulse));
        HBMessage sendResponse = handler.handleMessage(hbMessage, true);
        Assert.assertEquals(mid, sendResponse.get_message_id());
        Assert.assertEquals(SEND_PULSE_RESPONSE, sendResponse.get_type());
        Assert.assertNull(sendResponse.get_data());
        messageWithRandId(GET_PULSE, HBMessageData.path(path));
        HBMessage response = handler.handleMessage(hbMessage, true);
        Assert.assertEquals(mid, response.get_message_id());
        Assert.assertEquals(GET_PULSE_RESPONSE, response.get_type());
        Assert.assertEquals(dataString, new String(response.get_data().get_pulse().get_details(), "UTF-8"));
    }

    @Test
    public void testServerGetAllPulseForPath() {
        messageWithRandId(GET_ALL_PULSE_FOR_PATH, HBMessageData.path("/testpath"));
        HBMessage badResponse = handler.handleMessage(hbMessage, false);
        HBMessage goodResponse = handler.handleMessage(hbMessage, true);
        Assert.assertEquals(mid, badResponse.get_message_id());
        Assert.assertEquals(NOT_AUTHORIZED, badResponse.get_type());
        Assert.assertEquals(mid, goodResponse.get_message_id());
        Assert.assertEquals(GET_ALL_PULSE_FOR_PATH_RESPONSE, goodResponse.get_type());
        Assert.assertNull(goodResponse.get_data());
    }

    @Test
    public void testServerGetAllNodesForPath() throws UnsupportedEncodingException {
        makeNode(handler, "/some-root-path/foo");
        makeNode(handler, "/some-root-path/bar");
        makeNode(handler, "/some-root-path/baz");
        makeNode(handler, "/some-root-path/boo");
        messageWithRandId(GET_ALL_NODES_FOR_PATH, HBMessageData.path("/some-root-path"));
        HBMessage badResponse = handler.handleMessage(hbMessage, false);
        HBMessage goodResponse = handler.handleMessage(hbMessage, true);
        List<String> pulseIds = goodResponse.get_data().get_nodes().get_pulseIds();
        Assert.assertEquals(mid, badResponse.get_message_id());
        Assert.assertEquals(NOT_AUTHORIZED, badResponse.get_type());
        Assert.assertEquals(mid, goodResponse.get_message_id());
        Assert.assertEquals(GET_ALL_NODES_FOR_PATH_RESPONSE, goodResponse.get_type());
        Assert.assertTrue(pulseIds.contains("foo"));
        Assert.assertTrue(pulseIds.contains("bar"));
        Assert.assertTrue(pulseIds.contains("baz"));
        Assert.assertTrue(pulseIds.contains("boo"));
        makeNode(handler, "/some/deeper/path/foo");
        makeNode(handler, "/some/deeper/path/bar");
        makeNode(handler, "/some/deeper/path/baz");
        messageWithRandId(GET_ALL_NODES_FOR_PATH, HBMessageData.path("/some/deeper/path"));
        badResponse = handler.handleMessage(hbMessage, false);
        goodResponse = handler.handleMessage(hbMessage, true);
        pulseIds = goodResponse.get_data().get_nodes().get_pulseIds();
        Assert.assertEquals(mid, badResponse.get_message_id());
        Assert.assertEquals(NOT_AUTHORIZED, badResponse.get_type());
        Assert.assertEquals(mid, goodResponse.get_message_id());
        Assert.assertEquals(GET_ALL_NODES_FOR_PATH_RESPONSE, goodResponse.get_type());
        Assert.assertTrue(pulseIds.contains("foo"));
        Assert.assertTrue(pulseIds.contains("bar"));
        Assert.assertTrue(pulseIds.contains("baz"));
    }

    @Test
    public void testServerGetPulse() throws UnsupportedEncodingException {
        makeNode(handler, "/some-root/GET_PULSE");
        messageWithRandId(GET_PULSE, HBMessageData.path("/some-root/GET_PULSE"));
        HBMessage badResponse = handler.handleMessage(hbMessage, false);
        HBMessage goodResponse = handler.handleMessage(hbMessage, true);
        HBPulse goodPulse = goodResponse.get_data().get_pulse();
        Assert.assertEquals(mid, badResponse.get_message_id());
        Assert.assertEquals(NOT_AUTHORIZED, badResponse.get_type());
        Assert.assertNull(badResponse.get_data());
        Assert.assertEquals(mid, goodResponse.get_message_id());
        Assert.assertEquals(GET_PULSE_RESPONSE, goodResponse.get_type());
        Assert.assertEquals("/some-root/GET_PULSE", goodPulse.get_id());
        Assert.assertEquals("nothing", new String(goodPulse.get_details(), "UTF-8"));
    }

    @Test
    public void testServerDeletePath() throws UnsupportedEncodingException {
        makeNode(handler, "/some-root/DELETE_PATH/foo");
        makeNode(handler, "/some-root/DELETE_PATH/bar");
        makeNode(handler, "/some-root/DELETE_PATH/baz");
        makeNode(handler, "/some-root/DELETE_PATH/boo");
        messageWithRandId(DELETE_PATH, HBMessageData.path("/some-root/DELETE_PATH"));
        HBMessage response = handler.handleMessage(hbMessage, true);
        Assert.assertEquals(mid, response.get_message_id());
        Assert.assertEquals(DELETE_PATH_RESPONSE, response.get_type());
        Assert.assertNull(response.get_data());
        messageWithRandId(GET_ALL_NODES_FOR_PATH, HBMessageData.path("/some-root/DELETE_PATH"));
        response = handler.handleMessage(hbMessage, true);
        List<String> pulseIds = response.get_data().get_nodes().get_pulseIds();
        Assert.assertEquals(mid, response.get_message_id());
        Assert.assertEquals(GET_ALL_NODES_FOR_PATH_RESPONSE, response.get_type());
        Assert.assertTrue(pulseIds.isEmpty());
    }

    @Test
    public void testServerDeletePulseId() throws UnsupportedEncodingException {
        makeNode(handler, "/some-root/DELETE_PULSE_ID/foo");
        makeNode(handler, "/some-root/DELETE_PULSE_ID/bar");
        makeNode(handler, "/some-root/DELETE_PULSE_ID/baz");
        makeNode(handler, "/some-root/DELETE_PULSE_ID/boo");
        messageWithRandId(DELETE_PULSE_ID, HBMessageData.path("/some-root/DELETE_PULSE_ID/foo"));
        HBMessage response = handler.handleMessage(hbMessage, true);
        Assert.assertEquals(mid, response.get_message_id());
        Assert.assertEquals(DELETE_PULSE_ID_RESPONSE, response.get_type());
        Assert.assertNull(response.get_data());
        messageWithRandId(GET_ALL_NODES_FOR_PATH, HBMessageData.path("/some-root/DELETE_PULSE_ID"));
        response = handler.handleMessage(hbMessage, true);
        List<String> pulseIds = response.get_data().get_nodes().get_pulseIds();
        Assert.assertEquals(mid, response.get_message_id());
        Assert.assertEquals(GET_ALL_NODES_FOR_PATH_RESPONSE, response.get_type());
        Assert.assertFalse(pulseIds.contains("foo"));
    }
}

