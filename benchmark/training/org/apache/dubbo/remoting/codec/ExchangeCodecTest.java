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
package org.apache.dubbo.remoting.codec;


import Constants.DEFAULT_REMOTING_SERIALIZATION;
import Constants.PAYLOAD_KEY;
import Response.BAD_RESPONSE;
import Response.CLIENT_ERROR;
import TelnetCodec.DecodeResult.NEED_MORE_INPUT;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.dubbo.common.Version;
import org.apache.dubbo.common.io.Bytes;
import org.apache.dubbo.common.serialize.Serialization;
import org.apache.dubbo.remoting.Channel;
import org.apache.dubbo.remoting.buffer.ChannelBuffer;
import org.apache.dubbo.remoting.buffer.ChannelBuffers;
import org.apache.dubbo.remoting.exchange.Request;
import org.apache.dubbo.remoting.exchange.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * byte 16
 *         0-1 magic code
 *         2 flag
 *         8 - 1-request/0-response
 *         7 - two way
 *         6 - heartbeat
 *         1-5 serialization id
 *         3 status
 *         20 ok
 *         90 error?
 *         4-11 id (long)
 *         12 -15 datalength
 */
public class ExchangeCodecTest extends TelnetCodecTest {
    // magic header.
    private static final short MAGIC = ((short) (55995));

    private static final byte MAGIC_HIGH = ((byte) (Bytes.short2bytes(ExchangeCodecTest.MAGIC)[0]));

    private static final byte MAGIC_LOW = ((byte) (Bytes.short2bytes(ExchangeCodecTest.MAGIC)[1]));

    Serialization serialization = ExchangeCodecTest.getSerialization(DEFAULT_REMOTING_SERIALIZATION);

    @Test
    public void test_Decode_Error_MagicNum() throws IOException {
        HashMap<byte[], Object> inputBytes = new HashMap<byte[], Object>();
        inputBytes.put(new byte[]{ 0 }, NEED_MORE_INPUT);
        inputBytes.put(new byte[]{ ExchangeCodecTest.MAGIC_HIGH, 0 }, NEED_MORE_INPUT);
        inputBytes.put(new byte[]{ 0, ExchangeCodecTest.MAGIC_LOW }, NEED_MORE_INPUT);
        for (Map.Entry<byte[], Object> entry : inputBytes.entrySet()) {
            testDecode_assertEquals(assemblyDataProtocol(entry.getKey()), entry.getValue());
        }
    }

    @Test
    public void test_Decode_Error_Length() throws IOException {
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, 2, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        TelnetCodecTest.Person person = new TelnetCodecTest.Person();
        byte[] request = getRequestBytes(person, header);
        Channel channel = getServerSideChannel(url);
        byte[] baddata = new byte[]{ 1, 2 };
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(join(request, baddata));
        Response obj = ((Response) (codec.decode(channel, buffer)));
        Assertions.assertEquals(person, obj.getResult());
        // only decode necessary bytes
        Assertions.assertEquals(request.length, buffer.readerIndex());
    }

    @Test
    public void test_Decode_Error_Response_Object() throws IOException {
        // 00000010-response/oneway/hearbeat=true |20-stats=ok|id=0|length=0
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, 2, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        TelnetCodecTest.Person person = new TelnetCodecTest.Person();
        byte[] request = getRequestBytes(person, header);
        // bad object
        byte[] badbytes = new byte[]{ -1, -2, -3, -4, -3, -4, -3, -4, -3, -4, -3, -4 };
        System.arraycopy(badbytes, 0, request, 21, badbytes.length);
        Response obj = ((Response) (decode(request)));
        Assertions.assertEquals(90, obj.getStatus());
    }

    @Test
    public void testInvalidSerializaitonId() throws Exception {
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, ((byte) (143)), 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        Object obj = decode(header);
        Assertions.assertTrue((obj instanceof Request));
        Request request = ((Request) (obj));
        Assertions.assertTrue(request.isBroken());
        Assertions.assertTrue(((request.getData()) instanceof IOException));
        header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, ((byte) (31)), 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        obj = decode(header);
        Assertions.assertTrue((obj instanceof Response));
        Response response = ((Response) (obj));
        Assertions.assertEquals(response.getStatus(), CLIENT_ERROR);
        Assertions.assertTrue(response.getErrorMessage().contains("IOException"));
    }

    @Test
    public void test_Decode_Check_Payload() throws IOException {
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
        byte[] request = assemblyDataProtocol(header);
        try {
            testDecode_assertEquals(request, NEED_MORE_INPUT);
            Assertions.fail();
        } catch (IOException expected) {
            Assertions.assertTrue(expected.getMessage().startsWith(("Data length too large: " + (Bytes.bytes2int(new byte[]{ 1, 1, 1, 1 })))));
        }
    }

    @Test
    public void test_Decode_Header_Need_Readmore() throws IOException {
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        testDecode_assertEquals(header, NEED_MORE_INPUT);
    }

    @Test
    public void test_Decode_Body_Need_Readmore() throws IOException {
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 'a', 'a' };
        testDecode_assertEquals(header, NEED_MORE_INPUT);
    }

    @Test
    public void test_Decode_MigicCodec_Contain_ExchangeHeader() throws IOException {
        byte[] header = new byte[]{ 0, 0, ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        Channel channel = getServerSideChannel(url);
        ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(header);
        Object obj = codec.decode(channel, buffer);
        Assertions.assertEquals(NEED_MORE_INPUT, obj);
        // If the telnet data and request data are in the same data packet, we should guarantee that the receipt of request data won't be affected by the factor that telnet does not have an end characters.
        Assertions.assertEquals(2, buffer.readerIndex());
    }

    @Test
    public void test_Decode_Return_Response_Person() throws IOException {
        // 00000010-response/oneway/hearbeat=false/hessian |20-stats=ok|id=0|length=0
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, 2, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        TelnetCodecTest.Person person = new TelnetCodecTest.Person();
        byte[] request = getRequestBytes(person, header);
        Response obj = ((Response) (decode(request)));
        Assertions.assertEquals(20, obj.getStatus());
        Assertions.assertEquals(person, obj.getResult());
        System.out.println(obj);
    }

    // The status input has a problem, and the read information is wrong when the serialization is serialized.
    @Test
    public void test_Decode_Return_Response_Error() throws IOException {
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, 2, 90, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        String errorString = "encode request data error ";
        byte[] request = getRequestBytes(errorString, header);
        Response obj = ((Response) (decode(request)));
        Assertions.assertEquals(90, obj.getStatus());
        Assertions.assertEquals(errorString, obj.getErrorMessage());
    }

    @Test
    public void test_Decode_Return_Request_Event_Object() throws IOException {
        // |10011111|20-stats=ok|id=0|length=0
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, ((byte) (226)), 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        TelnetCodecTest.Person person = new TelnetCodecTest.Person();
        byte[] request = getRequestBytes(person, header);
        Request obj = ((Request) (decode(request)));
        Assertions.assertEquals(person, obj.getData());
        Assertions.assertEquals(true, obj.isTwoWay());
        Assertions.assertEquals(true, obj.isEvent());
        Assertions.assertEquals(Version.getProtocolVersion(), obj.getVersion());
        System.out.println(obj);
    }

    @Test
    public void test_Decode_Return_Request_Event_String() throws IOException {
        // |10011111|20-stats=ok|id=0|length=0
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, ((byte) (226)), 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        String event = Request.READONLY_EVENT;
        byte[] request = getRequestBytes(event, header);
        Request obj = ((Request) (decode(request)));
        Assertions.assertEquals(event, obj.getData());
        Assertions.assertEquals(true, obj.isTwoWay());
        Assertions.assertEquals(true, obj.isEvent());
        Assertions.assertEquals(Version.getProtocolVersion(), obj.getVersion());
        System.out.println(obj);
    }

    @Test
    public void test_Decode_Return_Request_Heartbeat_Object() throws IOException {
        // |10011111|20-stats=ok|id=0|length=0
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, ((byte) (226)), 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        byte[] request = getRequestBytes(null, header);
        Request obj = ((Request) (decode(request)));
        Assertions.assertEquals(null, obj.getData());
        Assertions.assertEquals(true, obj.isTwoWay());
        Assertions.assertEquals(true, obj.isHeartbeat());
        Assertions.assertEquals(Version.getProtocolVersion(), obj.getVersion());
        System.out.println(obj);
    }

    @Test
    public void test_Decode_Return_Request_Object() throws IOException {
        // |10011111|20-stats=ok|id=0|length=0
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, ((byte) (226)), 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        TelnetCodecTest.Person person = new TelnetCodecTest.Person();
        byte[] request = getRequestBytes(person, header);
        Request obj = ((Request) (decode(request)));
        Assertions.assertEquals(person, obj.getData());
        Assertions.assertEquals(true, obj.isTwoWay());
        Assertions.assertEquals(false, obj.isHeartbeat());
        Assertions.assertEquals(Version.getProtocolVersion(), obj.getVersion());
        System.out.println(obj);
    }

    @Test
    public void test_Decode_Error_Request_Object() throws IOException {
        // 00000010-response/oneway/hearbeat=true |20-stats=ok|id=0|length=0
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, ((byte) (226)), 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        TelnetCodecTest.Person person = new TelnetCodecTest.Person();
        byte[] request = getRequestBytes(person, header);
        // bad object
        byte[] badbytes = new byte[]{ -1, -2, -3, -4, -3, -4, -3, -4, -3, -4, -3, -4 };
        System.arraycopy(badbytes, 0, request, 21, badbytes.length);
        Request obj = ((Request) (decode(request)));
        Assertions.assertEquals(true, obj.isBroken());
        Assertions.assertEquals(true, ((obj.getData()) instanceof Throwable));
    }

    @Test
    public void test_Header_Response_NoSerializationFlag() throws IOException {
        // 00000010-response/oneway/hearbeat=false/noset |20-stats=ok|id=0|length=0
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, ((byte) (2)), 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        TelnetCodecTest.Person person = new TelnetCodecTest.Person();
        byte[] request = getRequestBytes(person, header);
        Response obj = ((Response) (decode(request)));
        Assertions.assertEquals(20, obj.getStatus());
        Assertions.assertEquals(person, obj.getResult());
        System.out.println(obj);
    }

    @Test
    public void test_Header_Response_Heartbeat() throws IOException {
        // 00000010-response/oneway/hearbeat=true |20-stats=ok|id=0|length=0
        byte[] header = new byte[]{ ExchangeCodecTest.MAGIC_HIGH, ExchangeCodecTest.MAGIC_LOW, 2, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        TelnetCodecTest.Person person = new TelnetCodecTest.Person();
        byte[] request = getRequestBytes(person, header);
        Response obj = ((Response) (decode(request)));
        Assertions.assertEquals(20, obj.getStatus());
        Assertions.assertEquals(person, obj.getResult());
        System.out.println(obj);
    }

    @Test
    public void test_Encode_Request() throws IOException {
        ChannelBuffer encodeBuffer = ChannelBuffers.dynamicBuffer(2014);
        Channel channel = getCliendSideChannel(url);
        Request request = new Request();
        TelnetCodecTest.Person person = new TelnetCodecTest.Person();
        request.setData(person);
        codec.encode(channel, encodeBuffer, request);
        // encode resault check need decode
        byte[] data = new byte[encodeBuffer.writerIndex()];
        encodeBuffer.readBytes(data);
        ChannelBuffer decodeBuffer = ChannelBuffers.wrappedBuffer(data);
        Request obj = ((Request) (codec.decode(channel, decodeBuffer)));
        Assertions.assertEquals(request.isBroken(), obj.isBroken());
        Assertions.assertEquals(request.isHeartbeat(), obj.isHeartbeat());
        Assertions.assertEquals(request.isTwoWay(), obj.isTwoWay());
        Assertions.assertEquals(person, obj.getData());
    }

    @Test
    public void test_Encode_Response() throws IOException {
        ChannelBuffer encodeBuffer = ChannelBuffers.dynamicBuffer(1024);
        Channel channel = getCliendSideChannel(url);
        Response response = new Response();
        response.setHeartbeat(true);
        response.setId(1001L);
        response.setStatus(((byte) (20)));
        response.setVersion("11");
        TelnetCodecTest.Person person = new TelnetCodecTest.Person();
        response.setResult(person);
        codec.encode(channel, encodeBuffer, response);
        byte[] data = new byte[encodeBuffer.writerIndex()];
        encodeBuffer.readBytes(data);
        // encode resault check need decode
        ChannelBuffer decodeBuffer = ChannelBuffers.wrappedBuffer(data);
        Response obj = ((Response) (codec.decode(channel, decodeBuffer)));
        Assertions.assertEquals(response.getId(), obj.getId());
        Assertions.assertEquals(response.getStatus(), obj.getStatus());
        Assertions.assertEquals(response.isHeartbeat(), obj.isHeartbeat());
        Assertions.assertEquals(person, obj.getResult());
        // encode response verson ??
        // Assertions.assertEquals(response.getProtocolVersion(), obj.getVersion());
    }

    @Test
    public void test_Encode_Error_Response() throws IOException {
        ChannelBuffer encodeBuffer = ChannelBuffers.dynamicBuffer(1024);
        Channel channel = getCliendSideChannel(url);
        Response response = new Response();
        response.setHeartbeat(true);
        response.setId(1001L);
        response.setStatus(((byte) (10)));
        response.setVersion("11");
        String badString = "bad";
        response.setErrorMessage(badString);
        TelnetCodecTest.Person person = new TelnetCodecTest.Person();
        response.setResult(person);
        codec.encode(channel, encodeBuffer, response);
        byte[] data = new byte[encodeBuffer.writerIndex()];
        encodeBuffer.readBytes(data);
        // encode resault check need decode
        ChannelBuffer decodeBuffer = ChannelBuffers.wrappedBuffer(data);
        Response obj = ((Response) (codec.decode(channel, decodeBuffer)));
        Assertions.assertEquals(response.getId(), obj.getId());
        Assertions.assertEquals(response.getStatus(), obj.getStatus());
        Assertions.assertEquals(response.isHeartbeat(), obj.isHeartbeat());
        Assertions.assertEquals(badString, obj.getErrorMessage());
        Assertions.assertEquals(null, obj.getResult());
        // Assertions.assertEquals(response.getProtocolVersion(), obj.getVersion());
    }

    // http://code.alibabatech.com/jira/browse/DUBBO-392
    @Test
    public void testMessageLengthGreaterThanMessageActualLength() throws Exception {
        Channel channel = getCliendSideChannel(url);
        Request request = new Request(1L);
        request.setVersion(Version.getProtocolVersion());
        Date date = new Date();
        request.setData(date);
        ChannelBuffer encodeBuffer = ChannelBuffers.dynamicBuffer(1024);
        codec.encode(channel, encodeBuffer, request);
        byte[] bytes = new byte[encodeBuffer.writerIndex()];
        encodeBuffer.readBytes(bytes);
        int len = Bytes.bytes2int(bytes, 12);
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        out.write(bytes, 0, 12);
        /* The fill length can not be less than 256, because by default, hessian reads 256 bytes from the stream each time.
        Refer Hessian2Input.readBuffer for more details
         */
        int padding = 512;
        out.write(Bytes.int2bytes((len + padding)));
        out.write(bytes, 16, ((bytes.length) - 16));
        for (int i = 0; i < padding; i++) {
            out.write(1);
        }
        out.write(bytes);
        /* request|1111...|request */
        ChannelBuffer decodeBuffer = ChannelBuffers.wrappedBuffer(out.toByteArray());
        Request decodedRequest = ((Request) (codec.decode(channel, decodeBuffer)));
        Assertions.assertTrue(date.equals(decodedRequest.getData()));
        Assertions.assertEquals(((bytes.length) + padding), decodeBuffer.readerIndex());
        decodedRequest = ((Request) (codec.decode(channel, decodeBuffer)));
        Assertions.assertTrue(date.equals(decodedRequest.getData()));
    }

    @Test
    public void testMessageLengthExceedPayloadLimitWhenEncode() throws Exception {
        Request request = new Request(1L);
        request.setData("hello");
        ChannelBuffer encodeBuffer = ChannelBuffers.dynamicBuffer(512);
        AbstractMockChannel channel = getCliendSideChannel(url.addParameter(PAYLOAD_KEY, 4));
        try {
            codec.encode(channel, encodeBuffer, request);
            Assertions.fail();
        } catch (IOException e) {
            Assertions.assertTrue(e.getMessage().startsWith(("Data length too large: " + 6)));
        }
        Response response = new Response(1L);
        response.setResult("hello");
        encodeBuffer = ChannelBuffers.dynamicBuffer(512);
        channel = getServerSideChannel(url.addParameter(PAYLOAD_KEY, 4));
        codec.encode(channel, encodeBuffer, response);
        Assertions.assertTrue(((channel.getReceivedMessage()) instanceof Response));
        Response receiveMessage = ((Response) (channel.getReceivedMessage()));
        Assertions.assertEquals(BAD_RESPONSE, receiveMessage.getStatus());
        Assertions.assertTrue(receiveMessage.getErrorMessage().contains("Data length too large: "));
    }
}

