/**
 * Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.weibo.api.motan.protocol.rpc;


import com.weibo.api.motan.codec.Codec;
import com.weibo.api.motan.exception.MotanErrorMsgConstant;
import com.weibo.api.motan.mock.MockChannel;
import com.weibo.api.motan.protocol.example.Model;
import com.weibo.api.motan.rpc.DefaultRequest;
import com.weibo.api.motan.rpc.DefaultResponse;
import com.weibo.api.motan.rpc.Response;
import com.weibo.api.motan.rpc.URL;
import com.weibo.api.motan.transport.Channel;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author maijunsheng
 * @version ?????2013-5-26
 */
public class DefaultRpcCodecTest extends TestCase {
    protected Codec rpcCodec = new DefaultRpcCodec();

    protected URL url = new URL("motan", "localhost", 18080, "com.weibo.api.motan.protocol.example.IHello");

    protected Channel channel = new MockChannel(url);

    protected String basicInterface = "com.weibo.api.motan.protocol.example.IHello";

    protected String basicMethod = "hello";

    @Test
    public void testVoidTypeRequest() throws Exception {
        DefaultRequest request = getRequest("void", null);
        testCodecRequest(request);
    }

    @Test
    public void testOriginalTypeRequest() throws Exception {
        DefaultRequest request = getRequest("java.lang.Integer", new Object[]{ 1 });
        testCodecRequest(request);
    }

    @Test
    public void testStringTypeRequest() throws Exception {
        DefaultRequest request = getRequest("java.lang.String", new Object[]{ "hello" });
        testCodecRequest(request);
    }

    @Test
    public void testObjectTypeRequest() throws Exception {
        DefaultRequest request = getRequest("com.weibo.api.motan.protocol.example.Model", new Object[]{ new Model("world", 12, Model.class) });
        testCodecRequest(request);
    }

    @Test
    public void testNullRequest() throws Exception {
        DefaultRequest request = getRequest("com.weibo.api.motan.protocol.example.Model", new Object[]{ null });
        testCodecRequest(request);
    }

    @Test
    public void testHalfNullRequest() throws Exception {
        DefaultRequest request = getRequest("com.weibo.api.motan.protocol.example.Model,com.weibo.api.motan.protocol.example.Model", new Object[]{ null, new Model("world", 12, Model.class) });
        testCodecRequest(request);
    }

    @Test
    public void testHalfNullRequest1() throws Exception {
        DefaultRequest request = getRequest("com.weibo.api.motan.protocol.example.Model[]", new Object[]{ new Model[]{ null, new Model("world", 12, Model.class) } });
        testCodecRequest(request);
    }

    @Test
    public void testMultiTypeRequest() throws Exception {
        DefaultRequest request = getRequest("com.weibo.api.motan.protocol.example.Model,java.lang.Integer,java.lang.String", new Object[]{ new Model("world", 12, Model.class), 1, "hello" });
        testCodecRequest(request);
    }

    @Test
    public void testOriginalTypeArrayRequest() throws Exception {
        DefaultRequest request = getRequest("int[]", new Object[]{ new int[]{ 1, 2 } });
        testCodecRequest(request);
    }

    @Test
    public void testStringArrayRequest() throws Exception {
        DefaultRequest request = getRequest("java.lang.String[]", new Object[]{ new String[]{ "hello", "world" } });
        testCodecRequest(request);
    }

    @Test
    public void testObjectArrayRequest() throws Exception {
        DefaultRequest request = getRequest("com.weibo.api.motan.protocol.example.Model[]", new Object[]{ new Model[]{ new Model("hello", 11, Model.class), new Model("world", 12, Model.class) } });
        testCodecRequest(request);
    }

    @Test
    public void testOriginalTypeResponse() throws Exception {
        DefaultResponse response = new DefaultResponse();
        response.setValue(1);
        testCodecResponse(response);
    }

    @Test
    public void testStringResponse() throws Exception {
        DefaultResponse response = new DefaultResponse();
        response.setValue("hello");
        testCodecResponse(response);
    }

    @Test
    public void testObjectResponse() throws Exception {
        DefaultResponse response = new DefaultResponse();
        response.setValue(new Model("world", 12, Model.class));
        testCodecResponse(response);
    }

    @Test
    public void testException() throws Exception {
        DefaultResponse response = new DefaultResponse();
        response.setException(new com.weibo.api.motan.exception.MotanServiceException("process thread pool is full, reject", MotanErrorMsgConstant.SERVICE_REJECT));
        byte[] bytes = rpcCodec.encode(channel, response);
        Response result = ((Response) (rpcCodec.decode(channel, "", bytes)));
        Assert.assertTrue(result.getException().getMessage().equals(response.getException().getMessage()));
        Assert.assertTrue(result.getException().getClass().equals(response.getException().getClass()));
    }
}

