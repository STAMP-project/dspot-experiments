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
package libcore.io;


import Libcore.os;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetUnixAddress;
import java.net.ServerSocket;
import junit.framework.TestCase;


public class OsTest extends TestCase {
    public void testIsSocket() throws Exception {
        File f = new File("/dev/null");
        FileInputStream fis = new FileInputStream(f);
        TestCase.assertFalse(S_ISSOCK(os.fstat(fis.getFD()).st_mode));
        fis.close();
        ServerSocket s = new ServerSocket();
        TestCase.assertTrue(S_ISSOCK(os.fstat(getImpl$().getFD$()).st_mode));
        s.close();
    }

    public void testUnixDomainSockets_in_file_system() throws Exception {
        String path = (System.getProperty("java.io.tmpdir")) + "/test_unix_socket";
        new File(path).delete();
        checkUnixDomainSocket(new InetUnixAddress(path), false);
    }

    public void testUnixDomainSocket_abstract_name() throws Exception {
        // Linux treats a sun_path starting with a NUL byte as an abstract name. See unix(7).
        byte[] path = "/abstract_name_unix_socket".getBytes("UTF-8");
        path[0] = 0;
        checkUnixDomainSocket(new InetUnixAddress(path), true);
    }

    public void test_strsignal() throws Exception {
        TestCase.assertEquals("Killed", os.strsignal(9));
        TestCase.assertEquals("Unknown signal -1", os.strsignal((-1)));
    }
}

