/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 *
 *
 * @author Boris V. Kuznetsov
 * @version $Revision$
 */
package tests.api.javax.net;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.net.SocketFactory;
import junit.framework.TestCase;


public class SocketFactoryTest extends TestCase {
    public void test_Constructor() throws Exception {
        new MySocketFactory();
    }

    public final void test_createSocket() throws Exception {
        SocketFactory sf = SocketFactory.getDefault();
        Socket s = sf.createSocket();
        TestCase.assertNotNull(s);
        TestCase.assertEquals((-1), s.getLocalPort());
        TestCase.assertEquals(0, s.getPort());
        MySocketFactory msf = new MySocketFactory();
        try {
            msf.createSocket();
            TestCase.fail("No expected SocketException");
        } catch (SocketException expected) {
        }
    }

    public final void test_createSocket_StringI() throws Exception {
        SocketFactory sf = SocketFactory.getDefault();
        int sport = new ServerSocket(0).getLocalPort();
        int[] invalidPorts = new int[]{ Integer.MIN_VALUE, -1, 65536, Integer.MAX_VALUE };
        Socket s = sf.createSocket(InetAddress.getLocalHost().getHostName(), sport);
        TestCase.assertNotNull(s);
        TestCase.assertTrue("Failed to create socket", ((s.getPort()) == sport));
        try {
            sf.createSocket("bla-bla", sport);
            TestCase.fail("UnknownHostException wasn't thrown");
        } catch (UnknownHostException expected) {
        }
        for (int i = 0; i < (invalidPorts.length); i++) {
            try {
                sf.createSocket(InetAddress.getLocalHost().getHostName(), invalidPorts[i]);
                TestCase.fail(("IllegalArgumentException wasn't thrown for " + (invalidPorts[i])));
            } catch (IllegalArgumentException expected) {
            }
        }
        try {
            sf.createSocket(InetAddress.getLocalHost().getHostName(), s.getLocalPort());
            TestCase.fail("IOException wasn't thrown");
        } catch (IOException expected) {
        }
        SocketFactory f = SocketFactory.getDefault();
        try {
            f.createSocket(InetAddress.getLocalHost().getHostName(), 8082);
            TestCase.fail("IOException wasn't thrown ...");
        } catch (IOException expected) {
        }
    }

    public final void test_createSocket_InetAddressI() throws Exception {
        SocketFactory sf = SocketFactory.getDefault();
        int sport = new ServerSocket(0).getLocalPort();
        int[] invalidPorts = new int[]{ Integer.MIN_VALUE, -1, 65536, Integer.MAX_VALUE };
        Socket s = sf.createSocket(InetAddress.getLocalHost(), sport);
        TestCase.assertNotNull(s);
        TestCase.assertTrue("Failed to create socket", ((s.getPort()) == sport));
        for (int i = 0; i < (invalidPorts.length); i++) {
            try {
                sf.createSocket(InetAddress.getLocalHost(), invalidPorts[i]);
                TestCase.fail(("IllegalArgumentException wasn't thrown for " + (invalidPorts[i])));
            } catch (IllegalArgumentException expected) {
            }
        }
        try {
            sf.createSocket(InetAddress.getLocalHost(), s.getLocalPort());
            TestCase.fail("IOException wasn't thrown");
        } catch (IOException expected) {
        }
        SocketFactory f = SocketFactory.getDefault();
        try {
            f.createSocket(InetAddress.getLocalHost(), 8081);
            TestCase.fail("IOException wasn't thrown ...");
        } catch (IOException expected) {
        }
    }

    public final void test_createSocket_InetAddressIInetAddressI() throws Exception {
        SocketFactory sf = SocketFactory.getDefault();
        int sport = new ServerSocket(0).getLocalPort();
        int[] invalidPorts = new int[]{ Integer.MIN_VALUE, -1, 65536, Integer.MAX_VALUE };
        Socket s = sf.createSocket(InetAddress.getLocalHost(), sport, InetAddress.getLocalHost(), 0);
        TestCase.assertNotNull(s);
        TestCase.assertTrue("1: Failed to create socket", ((s.getPort()) == sport));
        int portNumber = s.getLocalPort();
        for (int i = 0; i < (invalidPorts.length); i++) {
            try {
                sf.createSocket(InetAddress.getLocalHost(), invalidPorts[i], InetAddress.getLocalHost(), portNumber);
                TestCase.fail(("IllegalArgumentException wasn't thrown for " + (invalidPorts[i])));
            } catch (IllegalArgumentException expected) {
            }
            try {
                sf.createSocket(InetAddress.getLocalHost(), sport, InetAddress.getLocalHost(), invalidPorts[i]);
                TestCase.fail(("IllegalArgumentException wasn't thrown for " + (invalidPorts[i])));
            } catch (IllegalArgumentException expected) {
            }
        }
        try {
            sf.createSocket(InetAddress.getLocalHost(), sport, InetAddress.getLocalHost(), portNumber);
            TestCase.fail("IOException wasn't thrown");
        } catch (IOException expected) {
        }
        SocketFactory f = SocketFactory.getDefault();
        try {
            f.createSocket(InetAddress.getLocalHost(), 8081, InetAddress.getLocalHost(), 8082);
            TestCase.fail("IOException wasn't thrown ...");
        } catch (IOException expected) {
        }
    }

    /**
     * javax.net.SocketFactory#createSocket(String host, int port,
     *                                             InetAddress localHost, int localPort)
     */
    public final void test_createSocket_05() throws Exception {
        SocketFactory sf = SocketFactory.getDefault();
        int sport = new ServerSocket(0).getLocalPort();
        int[] invalidPorts = new int[]{ Integer.MIN_VALUE, -1, 65536, Integer.MAX_VALUE };
        Socket s = sf.createSocket(InetAddress.getLocalHost().getHostName(), sport, InetAddress.getLocalHost(), 0);
        TestCase.assertNotNull(s);
        TestCase.assertTrue("1: Failed to create socket", ((s.getPort()) == sport));
        try {
            sf.createSocket("bla-bla", sport, InetAddress.getLocalHost(), 0);
            TestCase.fail("UnknownHostException wasn't thrown");
        } catch (UnknownHostException expected) {
        }
        for (int i = 0; i < (invalidPorts.length); i++) {
            try {
                sf.createSocket(InetAddress.getLocalHost().getHostName(), invalidPorts[i], InetAddress.getLocalHost(), 0);
                TestCase.fail(("IllegalArgumentException wasn't thrown for " + (invalidPorts[i])));
            } catch (IllegalArgumentException expected) {
            }
            try {
                sf.createSocket(InetAddress.getLocalHost().getHostName(), sport, InetAddress.getLocalHost(), invalidPorts[i]);
                TestCase.fail(("IllegalArgumentException wasn't thrown for " + (invalidPorts[i])));
            } catch (IllegalArgumentException expected) {
            }
        }
        try {
            sf.createSocket(InetAddress.getLocalHost().getHostName(), 8081, InetAddress.getLocalHost(), 8082);
            TestCase.fail("IOException wasn't thrown ...");
        } catch (IOException expected) {
        }
    }

    /**
     * javax.net.SocketFactory#getDefault()
     */
    public final void test_getDefault() {
        SocketFactory sf = SocketFactory.getDefault();
        Socket s;
        try {
            s = sf.createSocket(InetAddress.getLocalHost().getHostName(), 8082);
            s.close();
        } catch (IOException e) {
        }
        try {
            s = sf.createSocket(InetAddress.getLocalHost().getHostName(), 8081, InetAddress.getLocalHost(), 8082);
            s.close();
        } catch (IOException e) {
        }
        try {
            s = sf.createSocket(InetAddress.getLocalHost(), 8081);
            s.close();
        } catch (IOException e) {
        }
        try {
            s = sf.createSocket(InetAddress.getLocalHost(), 8081, InetAddress.getLocalHost(), 8082);
            s.close();
        } catch (IOException e) {
        }
    }
}

