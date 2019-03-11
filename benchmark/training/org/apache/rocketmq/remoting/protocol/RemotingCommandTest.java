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
package org.apache.rocketmq.remoting.protocol;


import RemotingCommand.REMOTING_VERSION_KEY;
import RemotingSysResponseCode.SYSTEM_ERROR;
import SerializeType.JSON;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import org.apache.rocketmq.remoting.CommandCustomHeader;
import org.apache.rocketmq.remoting.exception.RemotingCommandException;
import org.junit.Test;

import static RemotingSysResponseCode.SUCCESS;
import static SerializeType.JSON;
import static SerializeType.ROCKETMQ;


public class RemotingCommandTest {
    @Test
    public void testMarkProtocolType_JSONProtocolType() {
        int source = 261;
        SerializeType type = JSON;
        byte[] result = RemotingCommand.markProtocolType(source, type);
        assertThat(result).isEqualTo(new byte[]{ 0, 0, 1, 5 });
    }

    @Test
    public void testMarkProtocolType_ROCKETMQProtocolType() {
        int source = 16777215;
        SerializeType type = ROCKETMQ;
        byte[] result = RemotingCommand.markProtocolType(source, type);
        assertThat(result).isEqualTo(new byte[]{ 1, -1, -1, -1 });
    }

    @Test
    public void testCreateRequestCommand_RegisterBroker() {
        System.setProperty(REMOTING_VERSION_KEY, "2333");
        int code = 103;// org.apache.rocketmq.common.protocol.RequestCode.REGISTER_BROKER

        CommandCustomHeader header = new SampleCommandCustomHeader();
        RemotingCommand cmd = RemotingCommand.createRequestCommand(code, header);
        assertThat(cmd.getCode()).isEqualTo(code);
        assertThat(cmd.getVersion()).isEqualTo(2333);
        assertThat(((cmd.getFlag()) & 1)).isEqualTo(0);// flag bit 0: 0 presents request

    }

    @Test
    public void testCreateResponseCommand_SuccessWithHeader() {
        System.setProperty(REMOTING_VERSION_KEY, "2333");
        int code = SUCCESS;
        String remark = "Sample remark";
        RemotingCommand cmd = RemotingCommand.createResponseCommand(code, remark, SampleCommandCustomHeader.class);
        assertThat(cmd.getCode()).isEqualTo(code);
        assertThat(cmd.getVersion()).isEqualTo(2333);
        assertThat(cmd.getRemark()).isEqualTo(remark);
        assertThat(((cmd.getFlag()) & 1)).isEqualTo(1);// flag bit 0: 1 presents response

    }

    @Test
    public void testCreateResponseCommand_SuccessWithoutHeader() {
        System.setProperty(REMOTING_VERSION_KEY, "2333");
        int code = SUCCESS;
        String remark = "Sample remark";
        RemotingCommand cmd = RemotingCommand.createResponseCommand(code, remark);
        assertThat(cmd.getCode()).isEqualTo(code);
        assertThat(cmd.getVersion()).isEqualTo(2333);
        assertThat(cmd.getRemark()).isEqualTo(remark);
        assertThat(((cmd.getFlag()) & 1)).isEqualTo(1);// flag bit 0: 1 presents response

    }

    @Test
    public void testCreateResponseCommand_FailToCreateCommand() {
        System.setProperty(REMOTING_VERSION_KEY, "2333");
        int code = SUCCESS;
        String remark = "Sample remark";
        RemotingCommand cmd = RemotingCommand.createResponseCommand(code, remark, CommandCustomHeader.class);
        assertThat(cmd).isNull();
    }

    @Test
    public void testCreateResponseCommand_SystemError() {
        System.setProperty(REMOTING_VERSION_KEY, "2333");
        RemotingCommand cmd = RemotingCommand.createResponseCommand(SampleCommandCustomHeader.class);
        assertThat(cmd.getCode()).isEqualTo(SYSTEM_ERROR);
        assertThat(cmd.getVersion()).isEqualTo(2333);
        assertThat(cmd.getRemark()).contains("not set any response code");
        assertThat(((cmd.getFlag()) & 1)).isEqualTo(1);// flag bit 0: 1 presents response

    }

    @Test
    public void testEncodeAndDecode_EmptyBody() {
        System.setProperty(REMOTING_VERSION_KEY, "2333");
        int code = 103;// org.apache.rocketmq.common.protocol.RequestCode.REGISTER_BROKER

        CommandCustomHeader header = new SampleCommandCustomHeader();
        RemotingCommand cmd = RemotingCommand.createRequestCommand(code, header);
        ByteBuffer buffer = cmd.encode();
        // Simulate buffer being read in NettyDecoder
        buffer.getInt();
        byte[] bytes = new byte[(buffer.limit()) - 4];
        buffer.get(bytes, 0, ((buffer.limit()) - 4));
        buffer = ByteBuffer.wrap(bytes);
        RemotingCommand decodedCommand = RemotingCommand.decode(buffer);
        assertThat(decodedCommand.getSerializeTypeCurrentRPC()).isEqualTo(JSON);
        assertThat(decodedCommand.getBody()).isNull();
    }

    @Test
    public void testEncodeAndDecode_FilledBody() {
        System.setProperty(REMOTING_VERSION_KEY, "2333");
        int code = 103;// org.apache.rocketmq.common.protocol.RequestCode.REGISTER_BROKER

        CommandCustomHeader header = new SampleCommandCustomHeader();
        RemotingCommand cmd = RemotingCommand.createRequestCommand(code, header);
        cmd.setBody(new byte[]{ 0, 1, 2, 3, 4 });
        ByteBuffer buffer = cmd.encode();
        // Simulate buffer being read in NettyDecoder
        buffer.getInt();
        byte[] bytes = new byte[(buffer.limit()) - 4];
        buffer.get(bytes, 0, ((buffer.limit()) - 4));
        buffer = ByteBuffer.wrap(bytes);
        RemotingCommand decodedCommand = RemotingCommand.decode(buffer);
        assertThat(decodedCommand.getSerializeTypeCurrentRPC()).isEqualTo(JSON);
        assertThat(decodedCommand.getBody()).isEqualTo(new byte[]{ 0, 1, 2, 3, 4 });
    }

    @Test
    public void testEncodeAndDecode_FilledBodyWithExtFields() throws RemotingCommandException {
        System.setProperty(REMOTING_VERSION_KEY, "2333");
        int code = 103;// org.apache.rocketmq.common.protocol.RequestCode.REGISTER_BROKER

        CommandCustomHeader header = new ExtFieldsHeader();
        RemotingCommand cmd = RemotingCommand.createRequestCommand(code, header);
        cmd.addExtField("key", "value");
        ByteBuffer buffer = cmd.encode();
        // Simulate buffer being read in NettyDecoder
        buffer.getInt();
        byte[] bytes = new byte[(buffer.limit()) - 4];
        buffer.get(bytes, 0, ((buffer.limit()) - 4));
        buffer = ByteBuffer.wrap(bytes);
        RemotingCommand decodedCommand = RemotingCommand.decode(buffer);
        assertThat(decodedCommand.getExtFields().get("stringValue")).isEqualTo("bilibili");
        assertThat(decodedCommand.getExtFields().get("intValue")).isEqualTo("2333");
        assertThat(decodedCommand.getExtFields().get("longValue")).isEqualTo("23333333");
        assertThat(decodedCommand.getExtFields().get("booleanValue")).isEqualTo("true");
        assertThat(decodedCommand.getExtFields().get("doubleValue")).isEqualTo("0.618");
        assertThat(decodedCommand.getExtFields().get("key")).isEqualTo("value");
        CommandCustomHeader decodedHeader = decodedCommand.decodeCommandCustomHeader(ExtFieldsHeader.class);
        assertThat(((ExtFieldsHeader) (decodedHeader)).getStringValue()).isEqualTo("bilibili");
        assertThat(((ExtFieldsHeader) (decodedHeader)).getIntValue()).isEqualTo(2333);
        assertThat(((ExtFieldsHeader) (decodedHeader)).getLongValue()).isEqualTo(23333333L);
        assertThat(((ExtFieldsHeader) (decodedHeader)).isBooleanValue()).isEqualTo(true);
        assertThat(((ExtFieldsHeader) (decodedHeader)).getDoubleValue()).isBetween(0.617, 0.619);
    }

    @Test
    public void testNotNullField() throws Exception {
        RemotingCommand remotingCommand = new RemotingCommand();
        Method method = RemotingCommand.class.getDeclaredMethod("isFieldNullable", Field.class);
        method.setAccessible(true);
        Field nullString = FieldTestClass.class.getDeclaredField("nullString");
        assertThat(method.invoke(remotingCommand, nullString)).isEqualTo(false);
        Field nullableString = FieldTestClass.class.getDeclaredField("nullable");
        assertThat(method.invoke(remotingCommand, nullableString)).isEqualTo(true);
        Field value = FieldTestClass.class.getDeclaredField("value");
        assertThat(method.invoke(remotingCommand, value)).isEqualTo(false);
    }
}

