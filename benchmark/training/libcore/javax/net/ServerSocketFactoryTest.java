/**
 * Copyright (C) 2009 The Android Open Source Project
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
package libcore.javax.net;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import javax.net.ServerSocketFactory;
import junit.framework.TestCase;


public class ServerSocketFactoryTest extends TestCase {
    public void testCreateServerSocket() throws IOException {
        ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket();
        serverSocket.bind(new InetSocketAddress(0));
        testSocket(serverSocket, 50);
    }

    public void testCreateServerSocketWithPort() throws IOException {
        ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(0);
        testSocket(serverSocket, 50);
    }

    public void testCreateServerSocketWithPortNoBacklog() throws IOException {
        ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(0, 1);
        testSocket(serverSocket, 1);
    }

    public void testCreateServerSocketWithPortZeroBacklog() throws IOException {
        ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(0, 0);
        testSocket(serverSocket, 50);
    }

    public void testCreateServerSocketWithPortAndBacklog() throws IOException {
        ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(0, 50);
        testSocket(serverSocket, 50);
    }
}

