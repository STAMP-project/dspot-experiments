/**
 * Copyright (C) 2014 The Android Open Source Project
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
package libcore.java.net;


import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import junit.framework.TestCase;


public class DatagramSocketTest extends TestCase {
    public void testInitialState() throws Exception {
        DatagramSocket ds = new DatagramSocket();
        try {
            TestCase.assertTrue(ds.isBound());
            TestCase.assertTrue(ds.getBroadcast());// The RI starts DatagramSocket in broadcast mode.

            TestCase.assertFalse(ds.isClosed());
            TestCase.assertFalse(ds.isConnected());
            TestCase.assertTrue(((ds.getLocalPort()) > 0));
            TestCase.assertTrue(ds.getLocalAddress().isAnyLocalAddress());
            InetSocketAddress socketAddress = ((InetSocketAddress) (ds.getLocalSocketAddress()));
            TestCase.assertEquals(ds.getLocalPort(), socketAddress.getPort());
            TestCase.assertEquals(ds.getLocalAddress(), socketAddress.getAddress());
            TestCase.assertNull(ds.getInetAddress());
            TestCase.assertEquals((-1), ds.getPort());
            TestCase.assertNull(ds.getRemoteSocketAddress());
            TestCase.assertFalse(ds.getReuseAddress());
            TestCase.assertNull(ds.getChannel());
        } finally {
            ds.close();
        }
    }

    public void testStateAfterClose() throws Exception {
        DatagramSocket ds = new DatagramSocket();
        ds.close();
        TestCase.assertTrue(ds.isBound());
        TestCase.assertTrue(ds.isClosed());
        TestCase.assertFalse(ds.isConnected());
        TestCase.assertNull(ds.getLocalAddress());
        TestCase.assertEquals((-1), ds.getLocalPort());
        TestCase.assertNull(ds.getLocalSocketAddress());
    }
}

