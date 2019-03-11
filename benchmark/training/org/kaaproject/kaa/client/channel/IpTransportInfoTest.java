/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.client.channel;


import ServerType.OPERATIONS;
import TransportProtocolIdConstants.TCP_TRANSPORT_ID;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.common.endpoint.security.KeyUtil;


/**
 *
 *
 * @author Andrew Shvayka
 */
public class IpTransportInfoTest {
    protected static final int SIZE_OF_INT = 4;

    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Test
    public void testInit() throws NoSuchAlgorithmException {
        IpTransportInfo info = new IpTransportInfo(IpTransportInfoTest.createTestServerInfo(OPERATIONS, TCP_TRANSPORT_ID, "localhost", 80, KeyUtil.generateKeyPair().getPublic()));
        Assert.assertEquals(OPERATIONS, info.getServerType());
        Assert.assertEquals(TCP_TRANSPORT_ID, info.getTransportId());
        Assert.assertEquals("localhost", info.getHost());
        Assert.assertEquals(80, info.getPort());
    }
}

