/**
 * Copyright (C) 2011 The Android Open Source Project
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


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import junit.framework.TestCase;


public class ServerSocketTest extends TestCase {
    public void testTimeoutAfterAccept() throws Exception {
        final ServerSocket ss = new ServerSocket(0);
        ss.setReuseAddress(true);
        // On Unix, the receive timeout is inherited by the result of accept(2).
        // Java specifies that it should always be 0 instead.
        ss.setSoTimeout(1234);
        final Socket[] result = new Socket[1];
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    result[0] = ss.accept();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    TestCase.fail();
                }
            }
        });
        t.start();
        new Socket(ss.getInetAddress(), ss.getLocalPort());
        t.join();
        TestCase.assertEquals(0, result[0].getSoTimeout());
    }
}

