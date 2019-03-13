/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.weibo.api.motan.protocol.v2motan;


import URLParamType.serialize;
import com.weibo.api.motan.mock.MockChannel;
import com.weibo.api.motan.transport.Channel;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.Test;


/**
 * Created by zhanglei28 on 2017/5/10.
 */
public class MotanV2CodecTest {
    static MotanV2Codec codec = new MotanV2Codec();

    @Test
    public void testEncode() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        URL url = new URL("motan2", "localhost", 0, "testService", params);
        url.getParameters().put(serialize.getName(), serialize.getValue());
        Channel channel = new MockChannel(url);
        // encode request
        DefaultRequest request = new DefaultRequest();
        request.setInterfaceName("com.weibo.test.TestService");
        request.setRequestId(new Random().nextLong());
        request.setMethodName("testHello");
        request.setArguments(new Object[]{ "hi!" });
        Map<String, String> attachs = new HashMap<String, String>();
        attachs.put("group", "testgroup");
        request.setAttachments(attachs);
        byte[] bytes = MotanV2CodecTest.codec.encode(channel, request);
        Request newReq = ((Request) (MotanV2CodecTest.codec.decode(channel, "localhost", bytes)));
        checkRequest(request, newReq);
        request.setAttachments(null);
        bytes = MotanV2CodecTest.codec.encode(channel, request);
        newReq = ((Request) (MotanV2CodecTest.codec.decode(channel, "localhost", bytes)));
        checkRequest(request, newReq);
        request.setArguments(new Object[]{ "123", 456, true });
        bytes = MotanV2CodecTest.codec.encode(channel, request);
        newReq = ((Request) (MotanV2CodecTest.codec.decode(channel, "localhost", bytes)));
        checkRequest(request, newReq);
        // encode response
        DefaultResponse response = new DefaultResponse();
        response.setRequestId(8908790);
        response.setProcessTime(5);
        response.setValue("xxede");
        Map<String, String> resAttachs = new HashMap<String, String>();
        resAttachs.put("res", "testres");
        resAttachs.put("xxx", "eee");
        response.setAttachments(resAttachs);
        bytes = MotanV2CodecTest.codec.encode(channel, response);
        Response newRes = ((Response) (MotanV2CodecTest.codec.decode(channel, "localhost", bytes)));
        checkResponse(response, newRes);
    }
}

