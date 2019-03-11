/**
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.haproxy;


import io.netty.buffer.Unpooled;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public class HAProxySSLTLVTest {
    @Test
    public void testClientBitmask() throws Exception {
        // 0b0000_0111
        final byte allClientsEnabled = 7;
        final HAProxySSLTLV allClientsEnabledTLV = new HAProxySSLTLV(0, allClientsEnabled, Collections.<HAProxyTLV>emptyList(), Unpooled.buffer());
        Assert.assertTrue(allClientsEnabledTLV.isPP2ClientCertConn());
        Assert.assertTrue(allClientsEnabledTLV.isPP2ClientSSL());
        Assert.assertTrue(allClientsEnabledTLV.isPP2ClientCertSess());
        Assert.assertTrue(allClientsEnabledTLV.release());
        // 0b0000_0101
        final byte clientSSLandClientCertSessEnabled = 5;
        final HAProxySSLTLV clientSSLandClientCertSessTLV = new HAProxySSLTLV(0, clientSSLandClientCertSessEnabled, Collections.<HAProxyTLV>emptyList(), Unpooled.buffer());
        Assert.assertFalse(clientSSLandClientCertSessTLV.isPP2ClientCertConn());
        Assert.assertTrue(clientSSLandClientCertSessTLV.isPP2ClientSSL());
        Assert.assertTrue(clientSSLandClientCertSessTLV.isPP2ClientCertSess());
        Assert.assertTrue(clientSSLandClientCertSessTLV.release());
        // 0b0000_0000
        final byte noClientEnabled = 0;
        final HAProxySSLTLV noClientTlv = new HAProxySSLTLV(0, noClientEnabled, Collections.<HAProxyTLV>emptyList(), Unpooled.buffer());
        Assert.assertFalse(noClientTlv.isPP2ClientCertConn());
        Assert.assertFalse(noClientTlv.isPP2ClientSSL());
        Assert.assertFalse(noClientTlv.isPP2ClientCertSess());
        Assert.assertTrue(noClientTlv.release());
    }
}

