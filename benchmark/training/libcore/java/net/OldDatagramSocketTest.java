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
package libcore.java.net;


import Support_Configuration.IPv6GlobalAddressJcl4;
import Support_Configuration.InetTestAddress2;
import Support_Configuration.InetTestIP;
import Support_Configuration.nonLocalAddressBytes;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.DatagramSocketImplFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.IllegalBlockingModeException;
import java.util.Date;
import java.util.Vector;
import junit.framework.TestCase;
import tests.support.Support_Configuration;
import tests.support.Support_PortManager;


public class OldDatagramSocketTest extends TestCase {
    DatagramSocket ds;

    DatagramPacket dp;

    DatagramSocket sds = null;

    String retval;

    String testString = "Test String";

    boolean interrupted;

    class DatagramServer extends Thread {
        public DatagramSocket ms;

        boolean running = true;

        public volatile byte[] rbuf = new byte[512];

        volatile DatagramPacket rdp = null;

        public void run() {
            try {
                while (running) {
                    try {
                        ms.receive(rdp);
                        // echo the packet back
                        ms.send(rdp);
                    } catch (InterruptedIOException e) {
                        Thread.yield();
                    }
                } 
            } catch (IOException e) {
                System.out.println(("DatagramServer server failed: " + e));
            } finally {
                ms.close();
            }
        }

        public void stopServer() {
            running = false;
        }

        public DatagramServer(int aPort, InetAddress address) throws IOException {
            rbuf = new byte[512];
            rbuf[0] = -1;
            rdp = new DatagramPacket(rbuf, rbuf.length);
            ms = new DatagramSocket(aPort, address);
            ms.setSoTimeout(2000);
        }
    }

    public void test_Constructor() {
        // Test for method java.net.DatagramSocket()
        try {
            ds = new DatagramSocket();
        } catch (Exception e) {
            TestCase.fail(("Could not create DatagramSocket : " + (e.getMessage())));
        }
        /* SecurityManager sm = new SecurityManager() {

        public void checkPermission(Permission perm) {
        }

        public void checkListen(int port) {
        throw new SecurityException();
        }
        };

        SecurityManager oldSm = System.getSecurityManager();
        System.setSecurityManager(sm);
        try {
        new DatagramSocket();
        fail("SecurityException should be thrown.");
        } catch (SecurityException e) {
        // expected
        } catch (SocketException e) {
        fail("SocketException was thrown.");
        } finally {
        System.setSecurityManager(oldSm);
        }
         */
    }

    public void test_ConstructorI() {
        // Test for method java.net.DatagramSocket(int)
        try {
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds = new DatagramSocket(portNumber);
            TestCase.assertTrue("Created socket with incorrect port", ((ds.getLocalPort()) == portNumber));
        } catch (Exception e) {
            TestCase.fail(("Could not create DatagramSocket : " + (e.getMessage())));
        }
        /* SecurityManager sm = new SecurityManager() {

        public void checkPermission(Permission perm) {
        }

        public void checkListen(int port) {
        throw new SecurityException();
        }
        };

        SecurityManager oldSm = System.getSecurityManager();
        System.setSecurityManager(sm);
        try {
        new DatagramSocket(8080);
        fail("SecurityException should be thrown.");
        } catch (SecurityException e) {
        // expected
        } catch (SocketException e) {
        fail("SocketException was thrown.");
        } finally {
        System.setSecurityManager(oldSm);
        }
         */
        try {
            DatagramSocket ds = new DatagramSocket(1);
            if (!("root".equals(System.getProperty("user.name")))) {
                TestCase.fail("SocketException was not thrown.");
            }
        } catch (SocketException e) {
            // expected
        }
    }

    public void test_ConstructorILjava_net_InetAddress() {
        // Test for method java.net.DatagramSocket(int, java.net.InetAddress)
        try {
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds = new DatagramSocket(portNumber, InetAddress.getLocalHost());
            TestCase.assertTrue("Created socket with incorrect port", ((ds.getLocalPort()) == portNumber));
            TestCase.assertTrue("Created socket with incorrect address", ds.getLocalAddress().equals(InetAddress.getLocalHost()));
        } catch (Exception e) {
            TestCase.fail(("Could not create DatagramSocket : " + (e.getMessage())));
        }
        /* SecurityManager sm = new SecurityManager() {

        public void checkPermission(Permission perm) {
        }

        public void checkListen(int port) {
        throw new SecurityException();
        }
        };

        SecurityManager oldSm = System.getSecurityManager();
        System.setSecurityManager(sm);
        try {
        new java.net.DatagramSocket(8080, InetAddress
        .getLocalHost());
        fail("SecurityException should be thrown.");
        } catch (SecurityException e) {
        // expected
        } catch (SocketException e) {
        fail("SocketException was thrown.");
        } catch (UnknownHostException e) {
        fail("UnknownHostException was thrown.");
        } finally {
        System.setSecurityManager(oldSm);
        }
         */
        try {
            new DatagramSocket(1, InetAddress.getLocalHost());
            TestCase.fail("SocketException was not thrown.");
        } catch (SocketException se) {
            // expected
        } catch (UnknownHostException e) {
            TestCase.fail("UnknownHostException was thrown.");
        }
    }

    public void test_close() {
        // Test for method void java.net.DatagramSocket.close()
        try {
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds = new DatagramSocket(portNumber);
            dp = new DatagramPacket("Test String".getBytes(), 11, InetAddress.getLocalHost(), 0);
            ds.close();
            try {
                ds.send(dp);
                TestCase.fail("IOException was not thrown.");
            } catch (IOException ioe) {
                // expected
            }
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + (e.getMessage())));
        }
    }

    public void test_connectLjava_net_InetAddressI() throws SocketException, UnknownHostException {
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getLocalHost();
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds.connect(inetAddress, portNumber);
            TestCase.assertTrue("Incorrect InetAddress", ds.getInetAddress().equals(inetAddress));
            TestCase.assertTrue("Incorrect Port", ((ds.getPort()) == portNumber));
            ds.disconnect();
        } catch (Exception e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
        System.out.println((("Running test_connectLjava_net_InetAddressI" + "(DatagramSocketTest) with IPv6GlobalAddressJcl4: ") + (Support_Configuration.IPv6GlobalAddressJcl4)));
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getByName(IPv6GlobalAddressJcl4);
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds.connect(inetAddress, portNumber);
            TestCase.assertTrue("Incorrect InetAddress", ds.getInetAddress().equals(inetAddress));
            TestCase.assertTrue("Incorrect Port", ((ds.getPort()) == portNumber));
            ds.disconnect();
        } catch (Exception e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
        try {
            // Create a connected datagram socket to test
            // PlainDatagramSocketImpl.peek()
            InetAddress localHost = InetAddress.getLocalHost();
            DatagramSocket ds = new DatagramSocket();
            int port = ds.getLocalPort();
            ds.connect(localHost, port);
            DatagramPacket send = new DatagramPacket(new byte[10], 10, localHost, port);
            ds.send(send);
            DatagramPacket receive = new DatagramPacket(new byte[20], 20);
            ds.setSoTimeout(2000);
            ds.receive(receive);
            ds.close();
            TestCase.assertTrue(("Wrong size: " + (receive.getLength())), ((receive.getLength()) == 10));
            TestCase.assertTrue("Wrong receiver", receive.getAddress().equals(localHost));
        } catch (IOException e) {
            TestCase.fail(("Unexpected IOException : " + (e.getMessage())));
        }
        class DatagramServer extends Thread {
            public DatagramSocket ms;

            boolean running = true;

            public byte[] rbuf = new byte[512];

            DatagramPacket rdp = null;

            public void run() {
                try {
                    while (running) {
                        try {
                            ms.receive(rdp);
                            // echo the packet back
                            ms.send(rdp);
                        } catch (InterruptedIOException e) {
                            Thread.yield();
                        }
                    } 
                } catch (IOException e) {
                    System.out.println(("Multicast server failed: " + e));
                } finally {
                    ms.close();
                }
            }

            public void stopServer() {
                running = false;
            }

            public DatagramServer(int aPort, InetAddress address) throws IOException {
                rbuf = new byte[512];
                rbuf[0] = -1;
                rdp = new DatagramPacket(rbuf, rbuf.length);
                ms = new DatagramSocket(aPort, address);
                ms.setSoTimeout(2000);
            }
        }
        // send a dgram to a server that is not running and then do a recv
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getLocalHost();
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds.connect(inetAddress, portNumber);
            DatagramPacket send = new DatagramPacket(new byte[10], 10);
            ds.send(send);
            DatagramPacket receive = new DatagramPacket(new byte[20], 20);
            ds.setSoTimeout(10000);
            ds.receive(receive);
            ds.close();
            TestCase.fail("No PortUnreachableException when connected at native level on recv ");
        } catch (Exception e) {
            TestCase.assertTrue(("Wrong exception when trying to connect at native level on recv: " + (e.toString())), (e instanceof PortUnreachableException));
        }
        // validate that we can send/receive with datagram sockets connected at
        // the native level
        DatagramServer server = null;
        int[] ports = Support_PortManager.getNextPortsForUDP(3);
        int serverPortNumber = ports[0];
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            DatagramSocket ds = new DatagramSocket(ports[1]);
            DatagramSocket ds2 = new DatagramSocket(ports[2]);
            try {
                server = new DatagramServer(serverPortNumber, localHost);
                server.start();
                Thread.sleep(1000);
            } catch (Exception e) {
                TestCase.fail("Failed to set up datagram server for native connected Dgram socket test ");
            }
            int port = ds.getLocalPort();
            ds.connect(localHost, serverPortNumber);
            byte[] sendBytes = new byte[]{ 'T', 'e', 's', 't', 0 };
            DatagramPacket send = new DatagramPacket(sendBytes, sendBytes.length);
            ds.send(send);
            DatagramPacket receive = new DatagramPacket(new byte[20], 20);
            ds.setSoTimeout(2000);
            ds.receive(receive);
            ds.close();
            TestCase.assertTrue(("Wrong size data received: " + (receive.getLength())), ((receive.getLength()) == (sendBytes.length)));
            TestCase.assertTrue(((("Wrong data received" + (new String(receive.getData(), 0, receive.getLength()))) + ":") + (new String(sendBytes))), new String(receive.getData(), 0, receive.getLength()).equals(new String(sendBytes)));
            TestCase.assertTrue(((("Wrong receiver:" + (receive.getAddress())) + ":") + localHost), receive.getAddress().equals(localHost));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception when sending data on dgram connected at native level:" + (e.toString())));
        }
        if (server != null) {
            server.stopServer();
        }
        // validate that we can disconnect
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getLocalHost();
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds.connect(inetAddress, portNumber);
            ds.disconnect();
            ds.close();
        } catch (Exception e) {
            TestCase.assertTrue(("Unexpected exception when trying to connect at native" + (e.toString())), (e instanceof PortUnreachableException));
        }
        // validate that once connected we cannot send to another address
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getLocalHost();
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds.connect(inetAddress, portNumber);
            DatagramPacket send = new DatagramPacket(new byte[10], 10, inetAddress, (portNumber + 1));
            ds.send(send);
            ds.close();
            TestCase.fail("No Exception when trying to send to a different address on a connected socket ");
        } catch (Exception e) {
            TestCase.assertTrue(("Wrong exception when trying to send to a different address on a connected socket: " + (e.toString())), (e instanceof IllegalArgumentException));
        }
        // validate that we can connect, then disconnect, then connect then
        // send/recv
        server = null;
        ports = Support_PortManager.getNextPortsForUDP(3);
        serverPortNumber = ports[0];
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            DatagramSocket ds = new DatagramSocket(ports[1]);
            DatagramSocket ds2 = new DatagramSocket(ports[2]);
            try {
                server = new DatagramServer(serverPortNumber, localHost);
                server.start();
                Thread.sleep(1000);
            } catch (Exception e) {
                TestCase.fail("Failed to set up datagram server for native connected Dgram socket test ");
            }
            int port = ds.getLocalPort();
            ds.connect(localHost, (serverPortNumber + 1));
            ds.disconnect();
            ds.connect(localHost, serverPortNumber);
            byte[] sendBytes = new byte[]{ 'T', 'e', 's', 't', 0 };
            DatagramPacket send = new DatagramPacket(sendBytes, sendBytes.length);
            ds.send(send);
            DatagramPacket receive = new DatagramPacket(new byte[20], 20);
            ds.setSoTimeout(2000);
            ds.receive(receive);
            ds.close();
            TestCase.assertTrue(("connect/disconnect/connect - Wrong size data received: " + (receive.getLength())), ((receive.getLength()) == (sendBytes.length)));
            TestCase.assertTrue(((("connect/disconnect/connect - Wrong data received" + (new String(receive.getData(), 0, receive.getLength()))) + ":") + (new String(sendBytes))), new String(receive.getData(), 0, receive.getLength()).equals(new String(sendBytes)));
            TestCase.assertTrue(((("connect/disconnect/connect - Wrong receiver:" + (receive.getAddress())) + ":") + localHost), receive.getAddress().equals(localHost));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception when sending data on dgram connected at native level after connect/disconnect/connect:" + (e.toString())));
        }
        if (server != null) {
            server.stopServer();
        }
        // validate that we can connect/disconnect then send/recv to any address
        server = null;
        ports = Support_PortManager.getNextPortsForUDP(3);
        serverPortNumber = ports[0];
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            DatagramSocket ds = new DatagramSocket(ports[1]);
            DatagramSocket ds2 = new DatagramSocket(ports[2]);
            try {
                server = new DatagramServer(serverPortNumber, localHost);
                server.start();
                Thread.sleep(1000);
            } catch (Exception e) {
                TestCase.fail("Failed to set up datagram server for native connected Dgram socket test ");
            }
            int port = ds.getLocalPort();
            ds.connect(localHost, (serverPortNumber + 1));
            ds.disconnect();
            byte[] sendBytes = new byte[]{ 'T', 'e', 's', 't', 0 };
            DatagramPacket send = new DatagramPacket(sendBytes, sendBytes.length, localHost, serverPortNumber);
            ds.send(send);
            DatagramPacket receive = new DatagramPacket(new byte[20], 20);
            ds.setSoTimeout(2000);
            ds.receive(receive);
            ds.close();
            TestCase.assertTrue(("connect/disconnect - Wrong size data received: " + (receive.getLength())), ((receive.getLength()) == (sendBytes.length)));
            TestCase.assertTrue(((("connect/disconnect - Wrong data received" + (new String(receive.getData(), 0, receive.getLength()))) + ":") + (new String(sendBytes))), new String(receive.getData(), 0, receive.getLength()).equals(new String(sendBytes)));
            TestCase.assertTrue(((("connect/disconnect - Wrong receiver:" + (receive.getAddress())) + ":") + localHost), receive.getAddress().equals(localHost));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception when sending data on dgram connected at native level after connect/disconnect:" + (e.toString())));
        }
        if (server != null) {
            server.stopServer();
        }
        // validate that we can connect on an allready connected socket and then
        // send/recv
        server = null;
        ports = Support_PortManager.getNextPortsForUDP(3);
        serverPortNumber = ports[0];
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            DatagramSocket ds = new DatagramSocket(ports[1]);
            DatagramSocket ds2 = new DatagramSocket(ports[2]);
            try {
                server = new DatagramServer(serverPortNumber, localHost);
                server.start();
                Thread.sleep(1000);
            } catch (Exception e) {
                TestCase.fail("Failed to set up datagram server for native connected Dgram socket test ");
            }
            int port = ds.getLocalPort();
            ds.connect(localHost, (serverPortNumber + 1));
            ds.connect(localHost, serverPortNumber);
            byte[] sendBytes = new byte[]{ 'T', 'e', 's', 't', 0 };
            DatagramPacket send = new DatagramPacket(sendBytes, sendBytes.length);
            ds.send(send);
            DatagramPacket receive = new DatagramPacket(new byte[20], 20);
            ds.setSoTimeout(2000);
            ds.receive(receive);
            ds.close();
            TestCase.assertTrue(("connect/connect - Wrong size data received: " + (receive.getLength())), ((receive.getLength()) == (sendBytes.length)));
            TestCase.assertTrue(((("connect/connect - Wrong data received" + (new String(receive.getData(), 0, receive.getLength()))) + ":") + (new String(sendBytes))), new String(receive.getData(), 0, receive.getLength()).equals(new String(sendBytes)));
            TestCase.assertTrue(((("connect/connect - Wrong receiver:" + (receive.getAddress())) + ":") + localHost), receive.getAddress().equals(localHost));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception when sending data on dgram connected at native level after connect/connect: " + (e.toString())));
        }
        if (server != null) {
            server.stopServer();
        }
        // test for when we fail to connect at the native level. Even though we
        // fail at the native level there is no way to return an exception so
        // there should be no exception
        try {
            ds = new DatagramSocket();
            byte[] addressBytes = new byte[]{ 0, 0, 0, 0 };
            InetAddress inetAddress = InetAddress.getByAddress(addressBytes);
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds.connect(inetAddress, portNumber);
        } catch (Exception e) {
            TestCase.fail(("Unexcpected exception when trying to connect at native level with bad address for signature with no exception to be returned: " + (e.toString())));
        }
        System.out.println("Running test_connectLjava_net_InetAddressI(DatagramSocketTest) with IPv6 address");
        try {
            ds = new DatagramSocket();
            byte[] addressBytes = new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            InetAddress inetAddress = InetAddress.getByAddress(addressBytes);
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds.connect(inetAddress, portNumber);
        } catch (Exception e) {
            TestCase.fail(("Unexcpected exception when trying to connect at native level with bad IPv6 address for signature with no exception to be returned: " + (e.toString())));
        }
    }

    public void test_disconnect() {
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getLocalHost();
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds.connect(inetAddress, portNumber);
            ds.disconnect();
            TestCase.assertNull("Incorrect InetAddress", ds.getInetAddress());
            TestCase.assertEquals("Incorrect Port", (-1), ds.getPort());
        } catch (Exception e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
        System.out.println(("Running test_disconnect(DatagramSocketTest) with IPv6GlobalAddressJcl4: " + (Support_Configuration.IPv6GlobalAddressJcl4)));
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getByName(IPv6GlobalAddressJcl4);
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds.connect(inetAddress, portNumber);
            ds.disconnect();
            TestCase.assertNull("Incorrect InetAddress", ds.getInetAddress());
            TestCase.assertEquals("Incorrect Port", (-1), ds.getPort());
        } catch (Exception e) {
            TestCase.fail(("Exception during test : " + (e.getMessage())));
        }
    }

    public void test_getInetAddress() {
        Vector<InetAddress> ias = new Vector<InetAddress>();
        try {
            ias.add(InetAddress.getLocalHost());
            ias.add(InetAddress.getByName(IPv6GlobalAddressJcl4));
            ias.add(InetAddress.getByName(InetTestAddress2));
            ias.add(InetAddress.getByName(InetTestAddress2));
            ias.add(InetAddress.getByName(InetTestIP));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception was thrown: " + (e.toString())));
        }
        for (InetAddress ia : ias) {
            int portNumber = Support_PortManager.getNextPortForUDP();
            DatagramSocket ds = null;
            try {
                ds = new DatagramSocket();
                ds.connect(ia, portNumber);
                TestCase.assertEquals(ia, ds.getInetAddress());
                TestCase.assertEquals(("" + ia), ia, ds.getInetAddress());
            } catch (SocketException e) {
                TestCase.fail("SocketException was thrown.");
            } finally {
                ds.disconnect();
                ds.close();
            }
        }
        try {
            TestCase.assertNull(new DatagramSocket().getInetAddress());
        } catch (SocketException e) {
            TestCase.fail("SocketException was thrown.");
        }
    }

    public void test_getLocalPort() {
        // Test for method int java.net.DatagramSocket.getLocalPort()
        try {
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds = new DatagramSocket(portNumber);
            TestCase.assertTrue("Returned incorrect port", ((ds.getLocalPort()) == portNumber));
        } catch (Exception e) {
            TestCase.fail(("Exception during getLocalAddress : " + (e.getMessage())));
        }
    }

    public void test_getPort() {
        try {
            int portNumber = Support_PortManager.getNextPortForUDP();
            DatagramSocket theSocket = new DatagramSocket(portNumber);
            TestCase.assertEquals("Expected -1 for remote port as not connected", (-1), theSocket.getPort());
            // now connect the socket and validate that we get the right port
            theSocket.connect(InetAddress.getLocalHost(), portNumber);
            TestCase.assertTrue(((("getPort returned wrong value:" + (theSocket.getPort())) + ":Expected:") + portNumber), ((theSocket.getPort()) == portNumber));
        } catch (Exception e) {
            TestCase.fail(("unexpected exception during getPort test : " + (e.getMessage())));
        }
    }

    public void test_getReceiveBufferSize() throws Exception {
        int portNumber = Support_PortManager.getNextPortForUDP();
        ds = new DatagramSocket(portNumber);
        ds.setReceiveBufferSize(130);
        TestCase.assertTrue("Incorrect buffer size", ((ds.getReceiveBufferSize()) >= 130));
        ds.close();
        try {
            ds.getReceiveBufferSize();
            TestCase.fail("SocketException was not thrown.");
        } catch (SocketException se) {
            // expected
        }
    }

    public void test_getSendBufferSize() throws Exception {
        int portNumber = Support_PortManager.getNextPortForUDP();
        ds = new DatagramSocket(portNumber);
        ds.setSendBufferSize(134);
        TestCase.assertTrue("Incorrect buffer size", ((ds.getSendBufferSize()) >= 134));
        ds.close();
        try {
            ds.getSendBufferSize();
            TestCase.fail("SocketException was not thrown.");
        } catch (SocketException se) {
            // expected
        }
    }

    public void test_getSoTimeout_setSoTimeout() throws Exception {
        // TODO: a useful test would check that setSoTimeout actually causes timeouts!
        DatagramSocket s = new DatagramSocket();
        s.setSoTimeout(1500);
        int ms = s.getSoTimeout();
        if ((ms < (1500 - 10)) || (ms > (1500 + 10))) {
            TestCase.fail(("suspicious timeout: " + ms));
        }
        s.close();
        try {
            s.getSoTimeout();
            TestCase.fail("SocketException was not thrown.");
        } catch (SocketException expected) {
        }
        try {
            s.setSoTimeout(1000);
            TestCase.fail("SocketException was not thrown.");
        } catch (SocketException expected) {
        }
    }

    public void test_receiveLjava_net_DatagramPacket() throws Exception {
        // Test for method void
        // java.net.DatagramSocket.receive(java.net.DatagramPacket)
        receive_oversize_java_net_DatagramPacket();
        final int[] ports = Support_PortManager.getNextPortsForUDP(2);
        final int portNumber = ports[0];
        class TestDGRcv implements Runnable {
            public void run() {
                try {
                    InetAddress localHost = InetAddress.getLocalHost();
                    Thread.sleep(1000);
                    DatagramSocket sds = new DatagramSocket(ports[1]);
                    sds.send(new DatagramPacket("Test".getBytes("UTF-8"), "Test".length(), localHost, portNumber));
                    sds.send(new DatagramPacket("Longer test".getBytes("UTF-8"), "Longer test".length(), localHost, portNumber));
                    sds.send(new DatagramPacket("3 Test".getBytes("UTF-8"), "3 Test".length(), localHost, portNumber));
                    sds.send(new DatagramPacket("4 Test".getBytes("UTF-8"), "4 Test".length(), localHost, portNumber));
                    sds.send(new DatagramPacket("5".getBytes("UTF-8"), "5".length(), localHost, portNumber));
                    sds.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        try {
            new Thread(new TestDGRcv(), "datagram receiver").start();
            ds = new DatagramSocket(portNumber);
            ds.setSoTimeout(6000);
            byte[] rbuf = new byte[1000];
            DatagramPacket rdp = new DatagramPacket(rbuf, rbuf.length);
            // Receive the first packet.
            ds.receive(rdp);
            TestCase.assertEquals("Test", new String(rbuf, 0, rdp.getLength()));
            // Check that we can still receive a longer packet (http://code.google.com/p/android/issues/detail?id=24748).
            ds.receive(rdp);
            TestCase.assertEquals("Longer test", new String(rbuf, 0, rdp.getLength()));
            // See what happens if we manually call DatagramPacket.setLength.
            rdp.setLength(4);
            ds.receive(rdp);
            TestCase.assertEquals("3 Te", new String(rbuf, 0, rdp.getLength()));
            // And then another.
            ds.receive(rdp);
            TestCase.assertEquals("4 Te", new String(rbuf, 0, rdp.getLength()));
            // And then a packet shorter than the user-supplied length.
            ds.receive(rdp);
            TestCase.assertEquals("5", new String(rbuf, 0, rdp.getLength()));
            ds.close();
        } finally {
            ds.close();
        }
        DatagramSocket socket = null;
        try {
            byte[] rbuf = new byte[1000];
            DatagramPacket rdp = new DatagramPacket(rbuf, rbuf.length);
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            socket = channel.socket();
            socket.receive(rdp);
            TestCase.fail("IllegalBlockingModeException was not thrown.");
        } catch (IllegalBlockingModeException expected) {
        } finally {
            socket.close();
        }
        try {
            ds = new DatagramSocket(portNumber);
            ds.setSoTimeout(1000);
            byte[] rbuf = new byte[1000];
            DatagramPacket rdp = new DatagramPacket(rbuf, rbuf.length);
            ds.receive(rdp);
            TestCase.fail("SocketTimeoutException was not thrown.");
        } catch (SocketTimeoutException expected) {
        } finally {
            ds.close();
        }
        interrupted = false;
        final DatagramSocket ds = new DatagramSocket();
        ds.setSoTimeout(12000);
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    ds.receive(new DatagramPacket(new byte[1], 1));
                } catch (InterruptedIOException e) {
                    interrupted = true;
                } catch (IOException ignored) {
                }
            }
        };
        Thread thread = new Thread(runnable, "DatagramSocket.receive1");
        thread.start();
        do {
            Thread.sleep(500);
        } while (!(thread.isAlive()) );
        ds.close();
        int c = 0;
        do {
            Thread.sleep(500);
            if (interrupted) {
                TestCase.fail("received interrupt");
            }
            if ((++c) > 4) {
                TestCase.fail("read call did not exit");
            }
        } while (thread.isAlive() );
        interrupted = false;
        final int portNum = ports[0];
        final DatagramSocket ds2 = new DatagramSocket(ports[1]);
        ds2.setSoTimeout(12000);
        Runnable runnable2 = new Runnable() {
            public void run() {
                try {
                    ds2.receive(new DatagramPacket(new byte[1], 1, InetAddress.getLocalHost(), portNum));
                } catch (InterruptedIOException e) {
                    interrupted = true;
                } catch (IOException ignored) {
                }
            }
        };
        Thread thread2 = new Thread(runnable2, "DatagramSocket.receive2");
        thread2.start();
        try {
            do {
                Thread.sleep(500);
            } while (!(thread2.isAlive()) );
        } catch (InterruptedException ignored) {
        }
        ds2.close();
        int c2 = 0;
        do {
            Thread.sleep(500);
            if (interrupted) {
                TestCase.fail("receive2 was interrupted");
            }
            if ((++c2) > 4) {
                TestCase.fail("read2 call did not exit");
            }
        } while (thread2.isAlive() );
        interrupted = false;
        DatagramSocket ds3 = new DatagramSocket();
        ds3.setSoTimeout(500);
        Date start = new Date();
        try {
            ds3.receive(new DatagramPacket(new byte[1], 1));
        } catch (InterruptedIOException e) {
            interrupted = true;
        }
        ds3.close();
        TestCase.assertTrue("receive not interrupted", interrupted);
        int delay = ((int) ((new Date().getTime()) - (start.getTime())));
        TestCase.assertTrue(("timeout too soon: " + delay), (delay >= 490));
    }

    public void test_sendLjava_net_DatagramPacket() throws Exception {
        // Test for method void
        // java.net.DatagramSocket.send(java.net.DatagramPacket)
        int[] ports = Support_PortManager.getNextPortsForUDP(2);
        final int portNumber = ports[0];
        class TestDGSend implements Runnable {
            Thread pThread;

            public TestDGSend(Thread t) {
                pThread = t;
            }

            public void run() {
                try {
                    byte[] rbuf = new byte[1000];
                    sds = new DatagramSocket(portNumber);
                    DatagramPacket sdp = new DatagramPacket(rbuf, rbuf.length);
                    sds.setSoTimeout(6000);
                    sds.receive(sdp);
                    retval = new String(rbuf, 0, testString.length());
                    pThread.interrupt();
                } catch (InterruptedIOException e) {
                    System.out.println("Recv operation timed out");
                    pThread.interrupt();
                    ds.close();
                } catch (Exception e) {
                    System.out.println(("Failed to establish Dgram server: " + e));
                }
            }
        }
        try {
            new Thread(new TestDGSend(Thread.currentThread()), "DGServer").start();
            ds = new DatagramSocket(ports[1]);
            dp = new DatagramPacket(testString.getBytes(), testString.length(), InetAddress.getLocalHost(), portNumber);
            // Wait to allow send to occur
            try {
                Thread.sleep(500);
                ds.send(dp);
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                ds.close();
                TestCase.assertTrue(("Incorrect data sent: " + (retval)), retval.equals(testString));
            }
        } catch (Exception e) {
            TestCase.fail(("Exception during send test : " + (e.getMessage())));
        } finally {
            ds.close();
        }
        /* SecurityManager sm = new SecurityManager() {

        public void checkPermission(Permission perm) {
        }

        public void checkMulticast(InetAddress maddr) {
        throw new SecurityException();
        }

        public void checkConnect(String host,
        int port) {
        throw new SecurityException();
        }
        };
        try {

        ds = new java.net.DatagramSocket(ports[1]);
        dp = new DatagramPacket(testString.getBytes(), testString.length(),
        InetAddress.getLocalHost(), portNumber);

        SecurityManager oldSm = System.getSecurityManager();
        System.setSecurityManager(sm);
        try {
        ds.send(dp);
        fail("SecurityException should be thrown.");
        } catch (SecurityException e) {
        // expected
        } catch (SocketException e) {
        fail("SocketException was thrown.");
        } finally {
        System.setSecurityManager(oldSm);
        }
        } catch(Exception e) {
        fail("Unexpected exception was thrown: " + e.getMessage());
        }
         */
        DatagramSocket socket = null;
        try {
            byte[] rbuf = new byte[1000];
            DatagramPacket rdp = new DatagramPacket(rbuf, rbuf.length);
            SocketAddress address = new InetSocketAddress(portNumber);
            DatagramChannel channel = DatagramChannel.open();
            channel.configureBlocking(false);
            socket = channel.socket();
            socket.send(rdp);
            TestCase.fail("IllegalBlockingModeException was not thrown.");
        } catch (IllegalBlockingModeException ibme) {
            // expected
        } catch (IOException ioe) {
            TestCase.fail(("IOException was thrown: " + (ioe.getMessage())));
        } finally {
            socket.close();
        }
        // Regression for HARMONY-1118
        class testDatagramSocket extends DatagramSocket {
            public testDatagramSocket(DatagramSocketImpl impl) {
                super(impl);
            }
        }
        class testDatagramSocketImpl extends DatagramSocketImpl {
            protected void create() throws SocketException {
            }

            protected void bind(int arg0, InetAddress arg1) throws SocketException {
            }

            protected void send(DatagramPacket arg0) throws IOException {
            }

            protected int peek(InetAddress arg0) throws IOException {
                return 0;
            }

            protected int peekData(DatagramPacket arg0) throws IOException {
                return 0;
            }

            protected void receive(DatagramPacket arg0) throws IOException {
            }

            protected void setTTL(byte arg0) throws IOException {
            }

            protected byte getTTL() throws IOException {
                return 0;
            }

            protected void setTimeToLive(int arg0) throws IOException {
            }

            protected int getTimeToLive() throws IOException {
                return 0;
            }

            protected void join(InetAddress arg0) throws IOException {
            }

            protected void leave(InetAddress arg0) throws IOException {
            }

            protected void joinGroup(SocketAddress arg0, NetworkInterface arg1) throws IOException {
            }

            protected void leaveGroup(SocketAddress arg0, NetworkInterface arg1) throws IOException {
            }

            protected void close() {
            }

            public void setOption(int arg0, Object arg1) throws SocketException {
            }

            public Object getOption(int arg0) throws SocketException {
                return null;
            }
        }
        InetSocketAddress sa = new InetSocketAddress(InetAddress.getLocalHost(), 0);
        // no exception expected for next line
        new testDatagramSocket(new testDatagramSocketImpl()).send(new DatagramPacket(new byte[272], 3, sa));
        // Regression test for Harmony-2938
        InetAddress i = InetAddress.getByName("127.0.0.1");
        DatagramSocket d = new DatagramSocket(0, i);
        try {
            d.send(new DatagramPacket(new byte[]{ 1 }, 1));
            TestCase.fail("should throw NPE.");
        } catch (NullPointerException e) {
            // expected;
        } finally {
            d.close();
        }
    }

    public void test_setSendBufferSizeI() throws Exception {
        int portNumber = Support_PortManager.getNextPortForUDP();
        ds = new DatagramSocket(portNumber);
        ds.setSendBufferSize(134);
        TestCase.assertTrue("Incorrect buffer size", ((ds.getSendBufferSize()) >= 134));
        ds.close();
        try {
            ds.setSendBufferSize(1);
            TestCase.fail("SocketException was not thrown.");
        } catch (SocketException se) {
            // expected
        }
    }

    public void test_setReceiveBufferSizeI() throws Exception {
        int portNumber = Support_PortManager.getNextPortForUDP();
        ds = new DatagramSocket(portNumber);
        ds.setReceiveBufferSize(130);
        TestCase.assertTrue("Incorrect buffer size", ((ds.getReceiveBufferSize()) >= 130));
        try {
            ds.setReceiveBufferSize(0);
            TestCase.fail("IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        try {
            ds.setReceiveBufferSize((-1));
            TestCase.fail("IllegalArgumentException was not thrown.");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        ds.close();
        try {
            ds.setReceiveBufferSize(1);
            TestCase.fail("SocketException was not thrown.");
        } catch (SocketException e) {
            // expected
        }
    }

    public void test_ConstructorLjava_net_DatagramSocketImpl() {
        class testDatagramSocket extends DatagramSocket {
            public testDatagramSocket(DatagramSocketImpl impl) {
                super(impl);
            }
        }
        try {
            new testDatagramSocket(((DatagramSocketImpl) (null)));
            TestCase.fail("exception expected");
        } catch (NullPointerException ex) {
            // expected
        }
    }

    public void test_ConstructorLjava_net_SocketAddress() {
        class mySocketAddress extends SocketAddress {
            public mySocketAddress() {
            }
        }
        try {
            try {
                int portNumber = Support_PortManager.getNextPortForUDP();
                ds = new DatagramSocket(new InetSocketAddress(InetAddress.getLocalHost(), portNumber));
                TestCase.assertTrue(ds.getBroadcast());
                TestCase.assertTrue("Created socket with incorrect port", ((ds.getLocalPort()) == portNumber));
                TestCase.assertTrue("Created socket with incorrect address", ds.getLocalAddress().equals(InetAddress.getLocalHost()));
            } catch (Exception e) {
                TestCase.fail(("Could not create DatagramSocket : " + (e.getMessage())));
            }
            try {
                int portNumber = Support_PortManager.getNextPortForUDP();
                ds = new DatagramSocket(new mySocketAddress());
                TestCase.fail("No exception when constucting datagramSocket with unsupported SocketAddress type");
            } catch (IllegalArgumentException e) {
            }
            // regression for Harmony-894
            ds = new DatagramSocket(((SocketAddress) (null)));
            TestCase.assertTrue(ds.getBroadcast());
        } catch (Exception ex) {
            TestCase.fail("unexpected exception when datagramSocket SocketAddress constructor test");
        }
        /* SecurityManager sm = new SecurityManager() {

        public void checkPermission(Permission perm) {
        }

        public void checkListen(int port) {
        throw new SecurityException();
        }
        };

        SecurityManager oldSm = System.getSecurityManager();
        System.setSecurityManager(sm);
        try {
        new DatagramSocket(new InetSocketAddress(
        InetAddress.getLocalHost(), 1));
        fail("SecurityException should be thrown.");
        } catch (SecurityException e) {
        // expected
        } catch (SocketException e) {
        fail("SocketException was thrown.");
        } catch (UnknownHostException e) {
        fail("UnknownHostException was thrown.");
        } finally {
        System.setSecurityManager(oldSm);
        }
         */
        InetSocketAddress isa = null;
        try {
            isa = new InetSocketAddress(InetAddress.getLocalHost(), 1);
        } catch (UnknownHostException e) {
            TestCase.fail("UnknownHostException was thrown.");
        }
    }

    public void test_bindLjava_net_SocketAddress() throws Exception {
        int[] ports = Support_PortManager.getNextPortsForUDP(3);
        int serverPortNumber = ports[1];
        // now create a socket that is not bound and then bind it
        InetAddress localHost = InetAddress.getLocalHost();
        InetSocketAddress localAddress1 = new InetSocketAddress(localHost, ports[0]);
        DatagramSocket theSocket = new DatagramSocket(localAddress1);
        // validate that the localSocketAddress reflects the address we bound to
        TestCase.assertEquals(localAddress1, theSocket.getLocalSocketAddress());
        // now make sure that datagrams sent from this socket appear to come
        // from the address we bound to
        InetSocketAddress localAddress2 = new InetSocketAddress(localHost, ports[2]);
        DatagramSocket ds = new DatagramSocket(((SocketAddress) (null)));
        ds.bind(localAddress2);
        OldDatagramSocketTest.DatagramServer server = new OldDatagramSocketTest.DatagramServer(serverPortNumber, localHost);
        server.start();
        Thread.sleep(1000);
        ds.connect(new InetSocketAddress(localHost, serverPortNumber));
        byte[] sendBytes = new byte[]{ 'T', 'e', 's', 't', 0 };
        DatagramPacket send = new DatagramPacket(sendBytes, sendBytes.length);
        ds.send(send);
        Thread.sleep(1000);
        ds.close();
        // Check that the address in the packet matches the bound address.
        TestCase.assertEquals(localAddress2, server.rdp.getSocketAddress());
        if (server != null) {
            server.stopServer();
        }
    }

    public void test_bindLjava_net_SocketAddress_null() throws Exception {
        // validate if we pass in null that it picks an address for us.
        DatagramSocket theSocket = new DatagramSocket(((SocketAddress) (null)));
        theSocket.bind(null);
        TestCase.assertNotNull(theSocket.getLocalSocketAddress());
        theSocket.close();
    }

    public void test_bindLjava_net_SocketAddress_bad_address() throws Exception {
        // Address we cannot bind to
        DatagramSocket theSocket = new DatagramSocket(((SocketAddress) (null)));
        try {
            InetAddress badAddress = InetAddress.getByAddress(nonLocalAddressBytes);
            theSocket.bind(new InetSocketAddress(badAddress, Support_PortManager.getNextPortForUDP()));
            TestCase.fail("No exception when binding to bad address");
        } catch (SocketException expected) {
        }
        theSocket.close();
    }

    public void test_bindLjava_net_SocketAddress_address_in_use() throws Exception {
        // Address that we have already bound to
        int[] ports = Support_PortManager.getNextPortsForUDP(2);
        DatagramSocket theSocket1 = new DatagramSocket(((SocketAddress) (null)));
        DatagramSocket theSocket2 = new DatagramSocket(ports[0]);
        try {
            InetSocketAddress theAddress = new InetSocketAddress(InetAddress.getLocalHost(), ports[1]);
            theSocket1.bind(theAddress);
            theSocket2.bind(theAddress);
            TestCase.fail("No exception binding to address that is not available");
        } catch (SocketException expected) {
        }
        theSocket1.close();
        theSocket2.close();
    }

    public void test_bindLjava_net_SocketAddress_unsupported_address_type() throws Exception {
        class mySocketAddress extends SocketAddress {
            public mySocketAddress() {
            }
        }
        // unsupported SocketAddress subclass
        DatagramSocket theSocket = new DatagramSocket(((SocketAddress) (null)));
        try {
            theSocket.bind(new mySocketAddress());
            TestCase.fail("No exception when binding using unsupported SocketAddress subclass");
        } catch (IllegalArgumentException expected) {
        }
        theSocket.close();
    }

    public void test_connectLjava_net_SocketAddress() {
        // validate that we get the PortUnreachable exception if we try to
        // send a dgram to a server that is not running and then do a recv
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getLocalHost();
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds.connect(new InetSocketAddress(inetAddress, portNumber));
            DatagramPacket send = new DatagramPacket(new byte[10], 10);
            ds.send(send);
            DatagramPacket receive = new DatagramPacket(new byte[20], 20);
            ds.setSoTimeout(10000);
            ds.receive(receive);
            ds.close();
            TestCase.fail("No PortUnreachableException when connected at native level on recv ");
        } catch (Exception e) {
            TestCase.assertTrue(("Wrong exception when trying to connect at native level on recv: " + (e.toString())), (e instanceof PortUnreachableException));
        }
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getLocalHost();
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds.connect(new InetSocketAddress("asdfasdf", 1));
            ds.close();
            TestCase.fail("SocketException was not thrown.");
        } catch (Exception e) {
            TestCase.assertTrue(("Wrong exception when trying to connect to unknown host: " + (e.toString())), (e instanceof SocketException));
        }
        // validate that we can send/receive with datagram sockets connected at
        // the native level
        OldDatagramSocketTest.DatagramServer server = null;
        int[] ports = Support_PortManager.getNextPortsForUDP(3);
        int serverPortNumber = ports[0];
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            DatagramSocket ds = new DatagramSocket(ports[1]);
            DatagramSocket ds2 = new DatagramSocket(ports[2]);
            try {
                server = new OldDatagramSocketTest.DatagramServer(serverPortNumber, localHost);
                server.start();
                Thread.sleep(1000);
            } catch (Exception e) {
                TestCase.fail("Failed to set up datagram server for native connected Dgram socket test ");
            }
            int port = ds.getLocalPort();
            ds.connect(new InetSocketAddress(localHost, serverPortNumber));
            byte[] sendBytes = new byte[]{ 'T', 'e', 's', 't', 0 };
            DatagramPacket send = new DatagramPacket(sendBytes, sendBytes.length);
            ds.send(send);
            DatagramPacket receive = new DatagramPacket(new byte[20], 20);
            ds.setSoTimeout(2000);
            ds.receive(receive);
            ds.close();
            TestCase.assertTrue(("Wrong size data received: " + (receive.getLength())), ((receive.getLength()) == (sendBytes.length)));
            TestCase.assertTrue(((("Wrong data received" + (new String(receive.getData(), 0, receive.getLength()))) + ":") + (new String(sendBytes))), new String(receive.getData(), 0, receive.getLength()).equals(new String(sendBytes)));
            TestCase.assertTrue(((("Wrong receiver:" + (receive.getAddress())) + ":") + localHost), receive.getAddress().equals(localHost));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception when sending data on dgram connected at native level:" + (e.toString())));
        }
        if (server != null) {
            server.stopServer();
        }
        // validate that we can disconnect
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getLocalHost();
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds.connect(new InetSocketAddress(inetAddress, portNumber));
            ds.disconnect();
            ds.close();
        } catch (Exception e) {
            TestCase.assertTrue(("Unexpected exception when trying to connect at native" + (e.toString())), (e instanceof PortUnreachableException));
        }
        // validate that once connected we cannot send to another address
        try {
            ds = new DatagramSocket();
            InetAddress inetAddress = InetAddress.getLocalHost();
            int portNumber = Support_PortManager.getNextPortForUDP();
            ds.connect(new InetSocketAddress(inetAddress, portNumber));
            DatagramPacket send = new DatagramPacket(new byte[10], 10, inetAddress, (portNumber + 1));
            ds.send(send);
            ds.close();
            TestCase.fail("No Exception when trying to send to a different address on a connected socket ");
        } catch (Exception e) {
            TestCase.assertTrue(("Wrong exception when trying to send to a different address on a connected socket: " + (e.toString())), (e instanceof IllegalArgumentException));
        }
        // validate that we can connect, then disconnect, then connect then
        // send/recv
        server = null;
        ports = Support_PortManager.getNextPortsForUDP(3);
        serverPortNumber = ports[0];
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            DatagramSocket ds = new DatagramSocket(ports[1]);
            DatagramSocket ds2 = new DatagramSocket(ports[2]);
            try {
                server = new OldDatagramSocketTest.DatagramServer(serverPortNumber, localHost);
                server.start();
                Thread.sleep(1000);
            } catch (Exception e) {
                TestCase.fail("Failed to set up datagram server for native connected Dgram socket test ");
            }
            int port = ds.getLocalPort();
            ds.connect(new InetSocketAddress(localHost, (serverPortNumber + 1)));
            ds.disconnect();
            ds.connect(new InetSocketAddress(localHost, serverPortNumber));
            byte[] sendBytes = new byte[]{ 'T', 'e', 's', 't', 0 };
            DatagramPacket send = new DatagramPacket(sendBytes, sendBytes.length);
            ds.send(send);
            DatagramPacket receive = new DatagramPacket(new byte[20], 20);
            ds.setSoTimeout(2000);
            ds.receive(receive);
            ds.close();
            TestCase.assertTrue(("connect/disconnect/connect - Wrong size data received: " + (receive.getLength())), ((receive.getLength()) == (sendBytes.length)));
            TestCase.assertTrue(((("connect/disconnect/connect - Wrong data received" + (new String(receive.getData(), 0, receive.getLength()))) + ":") + (new String(sendBytes))), new String(receive.getData(), 0, receive.getLength()).equals(new String(sendBytes)));
            TestCase.assertTrue(((("connect/disconnect/connect - Wrong receiver:" + (receive.getAddress())) + ":") + localHost), receive.getAddress().equals(localHost));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception when sending data on dgram connected at native level after connect/disconnect/connect:" + (e.toString())));
        }
        if (server != null) {
            server.stopServer();
        }
        // validate that we can connect/disconnect then send/recv to any address
        server = null;
        ports = Support_PortManager.getNextPortsForUDP(3);
        serverPortNumber = ports[0];
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            DatagramSocket ds = new DatagramSocket(ports[1]);
            DatagramSocket ds2 = new DatagramSocket(ports[2]);
            try {
                server = new OldDatagramSocketTest.DatagramServer(serverPortNumber, localHost);
                server.start();
                Thread.sleep(1000);
            } catch (Exception e) {
                TestCase.fail("Failed to set up datagram server for native connected Dgram socket test ");
            }
            int port = ds.getLocalPort();
            ds.connect(new InetSocketAddress(localHost, (serverPortNumber + 1)));
            ds.disconnect();
            byte[] sendBytes = new byte[]{ 'T', 'e', 's', 't', 0 };
            DatagramPacket send = new DatagramPacket(sendBytes, sendBytes.length, localHost, serverPortNumber);
            ds.send(send);
            DatagramPacket receive = new DatagramPacket(new byte[20], 20);
            ds.setSoTimeout(2000);
            ds.receive(receive);
            ds.close();
            TestCase.assertTrue(("connect/disconnect - Wrong size data received: " + (receive.getLength())), ((receive.getLength()) == (sendBytes.length)));
            TestCase.assertTrue(((("connect/disconnect - Wrong data received" + (new String(receive.getData(), 0, receive.getLength()))) + ":") + (new String(sendBytes))), new String(receive.getData(), 0, receive.getLength()).equals(new String(sendBytes)));
            TestCase.assertTrue(((("connect/disconnect - Wrong receiver:" + (receive.getAddress())) + ":") + localHost), receive.getAddress().equals(localHost));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception when sending data on dgram connected at native level after connect/disconnect:" + (e.toString())));
        }
        if (server != null) {
            server.stopServer();
        }
        // validate that we can connect on an allready connected socket and then
        // send/recv
        server = null;
        ports = Support_PortManager.getNextPortsForUDP(3);
        serverPortNumber = ports[0];
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            DatagramSocket ds = new DatagramSocket(ports[1]);
            DatagramSocket ds2 = new DatagramSocket(ports[2]);
            try {
                server = new OldDatagramSocketTest.DatagramServer(serverPortNumber, localHost);
                server.start();
                Thread.sleep(1000);
            } catch (Exception e) {
                TestCase.fail("Failed to set up datagram server for native connected Dgram socket test ");
            }
            int port = ds.getLocalPort();
            ds.connect(new InetSocketAddress(localHost, (serverPortNumber + 1)));
            ds.connect(new InetSocketAddress(localHost, serverPortNumber));
            byte[] sendBytes = new byte[]{ 'T', 'e', 's', 't', 0 };
            DatagramPacket send = new DatagramPacket(sendBytes, sendBytes.length);
            ds.send(send);
            DatagramPacket receive = new DatagramPacket(new byte[20], 20);
            ds.setSoTimeout(2000);
            ds.receive(receive);
            ds.close();
            TestCase.assertTrue(("connect/connect - Wrong size data received: " + (receive.getLength())), ((receive.getLength()) == (sendBytes.length)));
            TestCase.assertTrue(((("connect/connect - Wrong data received" + (new String(receive.getData(), 0, receive.getLength()))) + ":") + (new String(sendBytes))), new String(receive.getData(), 0, receive.getLength()).equals(new String(sendBytes)));
            TestCase.assertTrue(((("connect/connect - Wrong receiver:" + (receive.getAddress())) + ":") + localHost), receive.getAddress().equals(localHost));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception when sending data on dgram connected at native level after connect/connect: " + (e.toString())));
        }
        if (server != null) {
            server.stopServer();
        }
        // test for when we fail to connect at the native level. It seems to
        // fail for the any address so we use this. Now to be compatible we
        // don't throw the exception but eat it and then act as if we were
        // connected at the Java level.
        try {
            ds = new DatagramSocket();
            byte[] addressBytes = new byte[]{ 0, 0, 0, 0 };
            InetAddress inetAddress = InetAddress.getByAddress(addressBytes);
            int portNumber = Support_PortManager.getNextPortForUDP();
            InetAddress localHost = InetAddress.getLocalHost();
            ds.connect(new InetSocketAddress(inetAddress, portNumber));
            TestCase.assertTrue("Is not connected after connect to inaddr any", ds.isConnected());
            byte[] sendBytes = new byte[]{ 'T', 'e', 's', 't', 0 };
            DatagramPacket send = new DatagramPacket(sendBytes, sendBytes.length, localHost, portNumber);
            ds.send(send);
            TestCase.fail("No exception when trying to connect at native level with bad address (exception from send)  ");
        } catch (Exception e) {
            TestCase.assertTrue(("Wrong exception when trying to connect at native level with bad address (exception from send): " + (e.toString())), (e instanceof IllegalArgumentException));
        }
    }

    public void test_isBound() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            int[] ports = Support_PortManager.getNextPortsForUDP(3);
            int port = ports[0];
            DatagramSocket theSocket = new DatagramSocket(ports[1]);
            TestCase.assertTrue("Socket indicated  not bound when it should be (1)", theSocket.isBound());
            theSocket.close();
            theSocket = new DatagramSocket(new InetSocketAddress(addr, port));
            TestCase.assertTrue("Socket indicated  not bound when it should be (2)", theSocket.isBound());
            theSocket.close();
            theSocket = new DatagramSocket(((SocketAddress) (null)));
            TestCase.assertFalse("Socket indicated  bound when it should not be (1)", theSocket.isBound());
            theSocket.close();
            // connect causes implicit bind
            theSocket = new DatagramSocket(((SocketAddress) (null)));
            theSocket.connect(new InetSocketAddress(addr, port));
            TestCase.assertTrue("Socket indicated not bound when it should be (3)", theSocket.isBound());
            theSocket.close();
            // now test when we bind explicitely
            InetSocketAddress theLocalAddress = new InetSocketAddress(InetAddress.getLocalHost(), ports[2]);
            theSocket = new DatagramSocket(((SocketAddress) (null)));
            TestCase.assertFalse("Socket indicated bound when it should not be (2)", theSocket.isBound());
            theSocket.bind(theLocalAddress);
            TestCase.assertTrue("Socket indicated not bound when it should be (4)", theSocket.isBound());
            theSocket.close();
            TestCase.assertTrue("Socket indicated not bound when it should be (5)", theSocket.isBound());
        } catch (Exception e) {
            TestCase.fail(("Got exception during isBound tests" + (e.toString())));
        }
    }

    public void test_isConnected() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            int[] ports = Support_PortManager.getNextPortsForUDP(4);
            int port = ports[0];
            // base test
            DatagramSocket theSocket = new DatagramSocket(ports[1]);
            TestCase.assertFalse("Socket indicated connected when it should not be", theSocket.isConnected());
            theSocket.connect(new InetSocketAddress(addr, port));
            TestCase.assertTrue("Socket indicated  not connected when it should be", theSocket.isConnected());
            // reconnect the socket and make sure we get the right answer
            theSocket.connect(new InetSocketAddress(addr, ports[2]));
            TestCase.assertTrue("Socket indicated  not connected when it should be", theSocket.isConnected());
            // now disconnect the socket and make sure we get the right answer
            theSocket.disconnect();
            TestCase.assertFalse("Socket indicated connected when it should not be", theSocket.isConnected());
            theSocket.close();
            // now check behavior when socket is closed when connected
            theSocket = new DatagramSocket(ports[3]);
            theSocket.connect(new InetSocketAddress(addr, port));
            theSocket.close();
            TestCase.assertTrue("Socket indicated  not connected when it should be", theSocket.isConnected());
        } catch (Exception e) {
            TestCase.fail(("Got exception during isConnected tests" + (e.toString())));
        }
    }

    public void test_getRemoteSocketAddress() {
        try {
            int[] ports = Support_PortManager.getNextPortsForUDP(3);
            int sport = ports[0];
            int portNumber = ports[1];
            DatagramSocket s = new DatagramSocket(new InetSocketAddress(InetAddress.getLocalHost(), portNumber));
            s.connect(new InetSocketAddress(InetAddress.getLocalHost(), sport));
            TestCase.assertTrue(("Returned incorrect InetSocketAddress(1):" + (s.getLocalSocketAddress().toString())), s.getRemoteSocketAddress().equals(new InetSocketAddress(InetAddress.getLocalHost(), sport)));
            s.close();
            // now create one that is not connected and validate that we get the
            // right answer
            DatagramSocket theSocket = new DatagramSocket(((SocketAddress) (null)));
            portNumber = ports[2];
            theSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), portNumber));
            TestCase.assertNull(("Returned incorrect InetSocketAddress -unconnected socket:" + "Expected: NULL"), theSocket.getRemoteSocketAddress());
            // now connect and validate we get the right answer
            theSocket.connect(new InetSocketAddress(InetAddress.getLocalHost(), sport));
            TestCase.assertTrue(("Returned incorrect InetSocketAddress(2):" + (theSocket.getRemoteSocketAddress().toString())), theSocket.getRemoteSocketAddress().equals(new InetSocketAddress(InetAddress.getLocalHost(), sport)));
            theSocket.close();
        } catch (Exception e) {
            TestCase.fail(("Exception during getRemoteSocketAddress test: " + e));
        }
    }

    public void test_setReuseAddressZ() throws Exception {
        // test case were we set it to false
        DatagramSocket theSocket1 = null;
        DatagramSocket theSocket2 = null;
        try {
            InetSocketAddress theAddress = new InetSocketAddress(InetAddress.getLocalHost(), Support_PortManager.getNextPortForUDP());
            theSocket1 = new DatagramSocket(((SocketAddress) (null)));
            theSocket2 = new DatagramSocket(((SocketAddress) (null)));
            theSocket1.setReuseAddress(false);
            theSocket2.setReuseAddress(false);
            theSocket1.bind(theAddress);
            theSocket2.bind(theAddress);
            TestCase.fail("No exception when trying to connect to do duplicate socket bind with re-useaddr set to false");
        } catch (BindException e) {
        }
        if (theSocket1 != null)
            theSocket1.close();

        if (theSocket2 != null)
            theSocket2.close();

        // test case were we set it to true
        try {
            InetSocketAddress theAddress = new InetSocketAddress(InetAddress.getLocalHost(), Support_PortManager.getNextPortForUDP());
            theSocket1 = new DatagramSocket(((SocketAddress) (null)));
            theSocket2 = new DatagramSocket(((SocketAddress) (null)));
            theSocket1.setReuseAddress(true);
            theSocket2.setReuseAddress(true);
            theSocket1.bind(theAddress);
            theSocket2.bind(theAddress);
        } catch (Exception e) {
            TestCase.fail("unexpected exception when trying to connect to do duplicate socket bind with re-useaddr set to true");
        }
        if (theSocket1 != null)
            theSocket1.close();

        if (theSocket2 != null)
            theSocket2.close();

        // test the default case which we expect to be the same on all
        // platforms
        try {
            InetSocketAddress theAddress = new InetSocketAddress(InetAddress.getLocalHost(), Support_PortManager.getNextPortForUDP());
            theSocket1 = new DatagramSocket(((SocketAddress) (null)));
            theSocket2 = new DatagramSocket(((SocketAddress) (null)));
            theSocket1.bind(theAddress);
            theSocket2.bind(theAddress);
            TestCase.fail("No exception when trying to connect to do duplicate socket bind with re-useaddr left as default");
        } catch (BindException e) {
        }
        if (theSocket1 != null)
            theSocket1.close();

        if (theSocket2 != null)
            theSocket2.close();

        try {
            theSocket1.setReuseAddress(true);
            TestCase.fail("SocketException was not thrown.");
        } catch (SocketException se) {
            // expected
        }
    }

    public void test_getReuseAddress() throws Exception {
        DatagramSocket theSocket = new DatagramSocket();
        theSocket.setReuseAddress(true);
        TestCase.assertTrue("getReuseAddress false when it should be true", theSocket.getReuseAddress());
        theSocket.setReuseAddress(false);
        TestCase.assertFalse("getReuseAddress true when it should be false", theSocket.getReuseAddress());
        theSocket.close();
        try {
            theSocket.getReuseAddress();
            TestCase.fail("SocketException was not thrown.");
        } catch (SocketException se) {
            // expected
        }
    }

    public void test_setBroadcastZ() throws Exception {
        int[] ports = Support_PortManager.getNextPortsForUDP(3);
        DatagramSocket theSocket = new DatagramSocket(ports[0]);
        theSocket.setBroadcast(false);
        byte[] theBytes = new byte[]{ -1, -1, -1, -1 };
        // validate we cannot connect to the broadcast address when
        // setBroadcast is false
        try {
            theSocket.connect(new InetSocketAddress(InetAddress.getByAddress(theBytes), ports[1]));
            TestCase.assertFalse("No exception when connecting to broadcast address with setBroadcast(false)", theSocket.getBroadcast());
        } catch (SocketException ex) {
            // expected
        }
        // now validate that we can connect to the broadcast address when
        // setBroadcast is true
        theSocket.setBroadcast(true);
        theSocket.connect(new InetSocketAddress(InetAddress.getByAddress(theBytes), ports[2]));
        theSocket.close();
        try {
            theSocket.setBroadcast(false);
            TestCase.fail("SocketException was not thrown.");
        } catch (SocketException se) {
            // expected
        }
    }

    public void test_getBroadcast() throws Exception {
        DatagramSocket theSocket = new DatagramSocket();
        theSocket.setBroadcast(true);
        TestCase.assertTrue("getBroadcast false when it should be true", theSocket.getBroadcast());
        theSocket.setBroadcast(false);
        TestCase.assertFalse("getBroadcast true when it should be False", theSocket.getBroadcast());
        theSocket.close();
        try {
            theSocket.getBroadcast();
            TestCase.fail("SocketException was not thrown.");
        } catch (SocketException se) {
            // expected
        }
    }

    public void test_setTrafficClassI() throws Exception {
        int IPTOS_LOWCOST = 2;
        int IPTOS_RELIABILTY = 4;
        int IPTOS_THROUGHPUT = 8;
        int IPTOS_LOWDELAY = 16;
        int[] ports = Support_PortManager.getNextPortsForUDP(2);
        new InetSocketAddress(InetAddress.getLocalHost(), ports[0]);
        DatagramSocket theSocket = new DatagramSocket(ports[1]);
        // validate that value set must be between 0 and 255
        try {
            theSocket.setTrafficClass(256);
            TestCase.fail("No exception when traffic class set to 256");
        } catch (IllegalArgumentException e) {
        }
        try {
            theSocket.setTrafficClass((-1));
            TestCase.fail("No exception when traffic class set to -1");
        } catch (IllegalArgumentException e) {
        }
        // now validate that we can set it to some good values
        theSocket.setTrafficClass(IPTOS_LOWCOST);
        theSocket.setTrafficClass(IPTOS_THROUGHPUT);
        theSocket.close();
        try {
            theSocket.setTrafficClass(1);
            TestCase.fail("SocketException was not thrown.");
        } catch (SocketException se) {
            // expected
        }
    }

    public void test_getTrafficClass() throws Exception {
        int IPTOS_LOWCOST = 2;
        int IPTOS_RELIABILTY = 4;
        int IPTOS_THROUGHPUT = 8;
        int IPTOS_LOWDELAY = 16;
        int[] ports = Support_PortManager.getNextPortsForUDP(2);
        new InetSocketAddress(InetAddress.getLocalHost(), ports[0]);
        DatagramSocket theSocket = new DatagramSocket(ports[1]);
        /* we cannot actually check that the values are set as if a platform
        does not support the option then it may come back unset even
        though we set it so just get the value to make sure we can get it
         */
        int trafficClass = theSocket.getTrafficClass();
        theSocket.close();
        try {
            theSocket.getTrafficClass();
            TestCase.fail("SocketException was not thrown.");
        } catch (SocketException se) {
            // expected
        }
    }

    public void test_isClosed() {
        try {
            DatagramSocket theSocket = new DatagramSocket();
            // validate isClosed returns expected values
            TestCase.assertFalse("Socket should indicate it is not closed(1):", theSocket.isClosed());
            theSocket.close();
            TestCase.assertTrue("Socket should indicate it is not closed(1):", theSocket.isClosed());
            InetSocketAddress theAddress = new InetSocketAddress(InetAddress.getLocalHost(), Support_PortManager.getNextPortForUDP());
            theSocket = new DatagramSocket(theAddress);
            TestCase.assertFalse("Socket should indicate it is not closed(2):", theSocket.isClosed());
            theSocket.close();
            TestCase.assertTrue("Socket should indicate it is not closed(2):", theSocket.isClosed());
        } catch (Exception e) {
            TestCase.fail(("Got exception during isClosed tests" + (e.toString())));
        }
    }

    public void test_getChannel() throws Exception {
        TestCase.assertNull(new DatagramSocket().getChannel());
        int portNumber = Support_PortManager.getNextPortForUDP();
        DatagramSocket ds = null;
        try {
            InetAddress ia = InetAddress.getByName(IPv6GlobalAddressJcl4);
            ds = new DatagramSocket();
            TestCase.assertNull(ds.getChannel());
            ds.connect(ia, portNumber);
            TestCase.assertNull(ds.getChannel());
        } catch (SocketException e) {
            TestCase.fail("SocketException was thrown.");
        } finally {
            ds.disconnect();
            ds.close();
        }
        portNumber = Support_PortManager.getNextPortForUDP();
        SocketAddress address = new InetSocketAddress(portNumber);
        DatagramChannel channel = DatagramChannel.open();
        DatagramSocket socket = channel.socket();
        TestCase.assertEquals(channel, socket.getChannel());
        socket.close();
    }

    class TestDatagramSocketImplFactory implements DatagramSocketImplFactory {
        public DatagramSocketImpl createDatagramSocketImpl() {
            return new OldDatagramSocketTest.TestDatagramSocketImpl();
        }
    }

    class TestDatagramSocketImpl extends DatagramSocketImpl {
        @Override
        protected void bind(int arg0, InetAddress arg1) throws SocketException {
            // TODO Auto-generated method stub
        }

        @Override
        protected void close() {
            // TODO Auto-generated method stub
        }

        @Override
        protected void create() throws SocketException {
            // TODO Auto-generated method stub
        }

        @Override
        protected byte getTTL() throws IOException {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        protected int getTimeToLive() throws IOException {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        protected void join(InetAddress arg0) throws IOException {
            // TODO Auto-generated method stub
        }

        @Override
        protected void joinGroup(SocketAddress arg0, NetworkInterface arg1) throws IOException {
            // TODO Auto-generated method stub
        }

        @Override
        protected void leave(InetAddress arg0) throws IOException {
            // TODO Auto-generated method stub
        }

        @Override
        protected void leaveGroup(SocketAddress arg0, NetworkInterface arg1) throws IOException {
            // TODO Auto-generated method stub
        }

        @Override
        protected int peek(InetAddress arg0) throws IOException {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        protected int peekData(DatagramPacket arg0) throws IOException {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        protected void receive(DatagramPacket arg0) throws IOException {
            // TODO Auto-generated method stub
        }

        @Override
        protected void send(DatagramPacket arg0) throws IOException {
            // TODO Auto-generated method stub
        }

        @Override
        protected void setTTL(byte arg0) throws IOException {
            // TODO Auto-generated method stub
        }

        @Override
        protected void setTimeToLive(int arg0) throws IOException {
            // TODO Auto-generated method stub
        }

        public Object getOption(int arg0) throws SocketException {
            // TODO Auto-generated method stub
            return null;
        }

        public void setOption(int arg0, Object arg1) throws SocketException {
            // TODO Auto-generated method stub
        }
    }
}

