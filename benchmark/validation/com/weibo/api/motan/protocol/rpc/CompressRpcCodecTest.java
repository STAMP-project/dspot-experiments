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


import CompressRpcCodec.CODEC_VERSION_SWITCHER;
import URLParamType.group;
import URLParamType.mingzSize;
import URLParamType.usegz;
import com.weibo.api.motan.codec.Codec;
import com.weibo.api.motan.mock.MockChannel;
import com.weibo.api.motan.rpc.DefaultRequest;
import com.weibo.api.motan.rpc.Request;
import com.weibo.api.motan.rpc.URL;
import com.weibo.api.motan.transport.Channel;
import com.weibo.api.motan.util.ByteUtil;
import com.weibo.api.motan.util.MotanSwitcherUtil;
import java.io.IOException;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import static CompressRpcCodec.GROUP_CODEC_VERSION_SWITCHER;


/**
 * ??????????????????????????gz?????
 *
 * @author zhanglei
 */
public class CompressRpcCodecTest extends DefaultRpcCodecTest {
    // ???????
    @Test
    public void testSwitcher() throws IOException {
        DefaultRequest request = getRequest("void", null);
        byte[] bytes = rpcCodec.encode(channel, request);
        TestCase.assertTrue(isCompressVersion(bytes));
        // ??????
        MotanSwitcherUtil.setSwitcherValue(CODEC_VERSION_SWITCHER, true);
        bytes = rpcCodec.encode(channel, request);
        TestCase.assertTrue(isV1Version(bytes));
        // ??????
        MotanSwitcherUtil.setSwitcherValue(CODEC_VERSION_SWITCHER, false);
        MotanSwitcherUtil.setSwitcherValue(((GROUP_CODEC_VERSION_SWITCHER) + (group.getValue())), true);
        bytes = rpcCodec.encode(channel, request);
        TestCase.assertTrue(isV1Version(bytes));
    }

    // ??server?????????
    @Test
    public void testCompatibility() throws IOException {
        DefaultRequest request = getRequest("int[]", new Object[]{ new int[]{ 1, 2 } });
        Codec v1Codec = new DefaultRpcCodec();
        byte[] bytes = v1Codec.encode(channel, request);
        TestCase.assertTrue(isV1Version(bytes));
        Request result = ((Request) (rpcCodec.decode(channel, "", bytes)));
        Assert.assertTrue(equals(request, result));
    }

    // ??gz??
    @Test
    public void testGzip() throws IOException {
        DefaultRequest request = getRequest("int[]", new Object[]{ new int[]{ 1, 2 } });
        byte[] bytes = rpcCodec.encode(channel, request);
        TestCase.assertFalse(isGzip(bytes));
        // ????
        int bodyLength = ByteUtil.bytes2int(bytes, 12);
        URL url = new URL("motan", "localhost", 18080, "com.weibo.api.motan.procotol.example.IHello");
        Map<String, String> params = url.getParameters();
        params.put(usegz.name(), "true");
        params.put(mingzSize.name(), String.valueOf((bodyLength - 1)));
        Channel tempChannel = new MockChannel(url);
        bytes = rpcCodec.encode(tempChannel, request);
        TestCase.assertTrue(isGzip(bytes));
        // ???????.url???????????????????????????url?channel
        url = new URL("motan", "localhost", 18080, "com.weibo.api.motan.procotol.example.IHello");
        params = url.getParameters();
        params.put(usegz.name(), "true");
        params.put(mingzSize.name(), String.valueOf(bodyLength));
        tempChannel = new MockChannel(url);
        bytes = rpcCodec.encode(tempChannel, request);
        TestCase.assertFalse(isGzip(bytes));
    }
}

