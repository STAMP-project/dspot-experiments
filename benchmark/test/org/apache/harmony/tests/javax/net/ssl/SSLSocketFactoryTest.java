/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.tests.javax.net.ssl;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import junit.framework.TestCase;


public class SSLSocketFactoryTest extends TestCase {
    private ServerSocket ss;

    /**
     * javax.net.ssl.SSLSocketFactory#SSLSocketFactory()
     */
    public void test_Constructor() {
        try {
            SocketFactory sf = SSLSocketFactory.getDefault();
            TestCase.assertTrue((sf instanceof SSLSocketFactory));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception " + (e.toString())));
        }
    }

    /**
     * javax.net.ssl.SSLSocketFactory#getDefault()
     */
    public void test_getDefault() {
        TestCase.assertNotNull("Incorrect default socket factory", SSLSocketFactory.getDefault());
    }

    /**
     * javax.net.ssl.SSLSocketFactory#createSocket(Socket s, String host, int port, boolean autoClose)
     */
    public void test_createSocket() throws Exception {
        SSLSocketFactory sf = ((SSLSocketFactory) (SSLSocketFactory.getDefault()));
        int sport = startServer("test_createSocket()");
        int[] invalid = new int[]{ Integer.MIN_VALUE, -1, 65536, Integer.MAX_VALUE };
        Socket st = new Socket("localhost", sport);
        Socket s = sf.createSocket(st, "localhost", sport, false);
        TestCase.assertFalse(s.isClosed());
        st = new Socket("localhost", sport);
        s = sf.createSocket(st, "localhost", sport, true);
        s.close();
        TestCase.assertTrue(st.isClosed());
        try {
            sf.createSocket(null, "localhost", sport, true);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        for (int i = 0; i < (invalid.length); i++) {
            try {
                s = sf.createSocket(new Socket(), "localhost", 1080, false);
                TestCase.fail();
            } catch (IOException expected) {
            }
        }
        try {
            st = new Socket("1.2.3.4hello", sport);
            s = sf.createSocket(st, "1.2.3.4hello", sport, false);
            TestCase.fail();
        } catch (UnknownHostException expected) {
        }
    }
}

