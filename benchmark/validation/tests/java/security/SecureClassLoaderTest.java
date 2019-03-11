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
 * @author Alexander V. Astapchuk
 * @version $Revision$
 */
package tests.java.security;


import java.net.URL;
import java.nio.ByteBuffer;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import java.security.SecureClassLoader;
import java.security.cert.Certificate;
import junit.framework.TestCase;


/**
 * Unit test for SecureClassLoader.
 */
public class SecureClassLoaderTest extends TestCase {
    /**
     * A class name for the class presented as {@link #klassData bytecode below}
     */
    private static final String klassName = "HiWorld";

    /**
     * Some class presented as bytecode<br>
     * Class src:<br>
     * <p>
     * <code>public class HiWorld {
     *     public static void main(String[] args)
     *         {System.out.println("Hi, world!"); }
     *    }
     * </code>
     */
    private static final byte[] klassData = new byte[]{ ((byte) (202)), ((byte) (254)), ((byte) (186)), ((byte) (190)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (46)), ((byte) (0)), ((byte) (34)), ((byte) (1)), ((byte) (0)), ((byte) (7)), ((byte) (72)), ((byte) (105)), ((byte) (87)), ((byte) (111)), ((byte) (114)), ((byte) (108)), ((byte) (100)), ((byte) (7)), ((byte) (0)), ((byte) (1)), ((byte) (1)), ((byte) (0)), ((byte) (16)), ((byte) (106)), ((byte) (97)), ((byte) (118)), ((byte) (97)), ((byte) (47)), ((byte) (108)), ((byte) (97)), ((byte) (110)), ((byte) (103)), ((byte) (47)), ((byte) (79)), ((byte) (98)), ((byte) (106)), ((byte) (101)), ((byte) (99)), ((byte) (116)), ((byte) (7)), ((byte) (0)), ((byte) (3)), ((byte) (1)), ((byte) (0)), ((byte) (6)), ((byte) (60)), ((byte) (105)), ((byte) (110)), ((byte) (105)), ((byte) (116)), ((byte) (62)), ((byte) (1)), ((byte) (0)), ((byte) (3)), ((byte) (40)), ((byte) (41)), ((byte) (86)), ((byte) (1)), ((byte) (0)), ((byte) (4)), ((byte) (67)), ((byte) (111)), ((byte) (100)), ((byte) (101)), ((byte) (12)), ((byte) (0)), ((byte) (5)), ((byte) (0)), ((byte) (6)), ((byte) (10)), ((byte) (0)), ((byte) (4)), ((byte) (0)), ((byte) (8)), ((byte) (1)), ((byte) (0)), ((byte) (15)), ((byte) (76)), ((byte) (105)), ((byte) (110)), ((byte) (101)), ((byte) (78)), ((byte) (117)), ((byte) (109)), ((byte) (98)), ((byte) (101)), ((byte) (114)), ((byte) (84)), ((byte) (97)), ((byte) (98)), ((byte) (108)), ((byte) (101)), ((byte) (1)), ((byte) (0)), ((byte) (18)), ((byte) (76)), ((byte) (111)), ((byte) (99)), ((byte) (97)), ((byte) (108)), ((byte) (86)), ((byte) (97)), ((byte) (114)), ((byte) (105)), ((byte) (97)), ((byte) (98)), ((byte) (108)), ((byte) (101)), ((byte) (84)), ((byte) (97)), ((byte) (98)), ((byte) (108)), ((byte) (101)), ((byte) (1)), ((byte) (0)), ((byte) (4)), ((byte) (116)), ((byte) (104)), ((byte) (105)), ((byte) (115)), ((byte) (1)), ((byte) (0)), ((byte) (9)), ((byte) (76)), ((byte) (72)), ((byte) (105)), ((byte) (87)), ((byte) (111)), ((byte) (114)), ((byte) (108)), ((byte) (100)), ((byte) (59)), ((byte) (1)), ((byte) (0)), ((byte) (4)), ((byte) (109)), ((byte) (97)), ((byte) (105)), ((byte) (110)), ((byte) (1)), ((byte) (0)), ((byte) (22)), ((byte) (40)), ((byte) (91)), ((byte) (76)), ((byte) (106)), ((byte) (97)), ((byte) (118)), ((byte) (97)), ((byte) (47)), ((byte) (108)), ((byte) (97)), ((byte) (110)), ((byte) (103)), ((byte) (47)), ((byte) (83)), ((byte) (116)), ((byte) (114)), ((byte) (105)), ((byte) (110)), ((byte) (103)), ((byte) (59)), ((byte) (41)), ((byte) (86)), ((byte) (1)), ((byte) (0)), ((byte) (16)), ((byte) (106)), ((byte) (97)), ((byte) (118)), ((byte) (97)), ((byte) (47)), ((byte) (108)), ((byte) (97)), ((byte) (110)), ((byte) (103)), ((byte) (47)), ((byte) (83)), ((byte) (121)), ((byte) (115)), ((byte) (116)), ((byte) (101)), ((byte) (109)), ((byte) (7)), ((byte) (0)), ((byte) (16)), ((byte) (1)), ((byte) (0)), ((byte) (3)), ((byte) (111)), ((byte) (117)), ((byte) (116)), ((byte) (1)), ((byte) (0)), ((byte) (21)), ((byte) (76)), ((byte) (106)), ((byte) (97)), ((byte) (118)), ((byte) (97)), ((byte) (47)), ((byte) (105)), ((byte) (111)), ((byte) (47)), ((byte) (80)), ((byte) (114)), ((byte) (105)), ((byte) (110)), ((byte) (116)), ((byte) (83)), ((byte) (116)), ((byte) (114)), ((byte) (101)), ((byte) (97)), ((byte) (109)), ((byte) (59)), ((byte) (12)), ((byte) (0)), ((byte) (18)), ((byte) (0)), ((byte) (19)), ((byte) (9)), ((byte) (0)), ((byte) (17)), ((byte) (0)), ((byte) (20)), ((byte) (1)), ((byte) (0)), ((byte) (10)), ((byte) (72)), ((byte) (105)), ((byte) (44)), ((byte) (32)), ((byte) (119)), ((byte) (111)), ((byte) (114)), ((byte) (108)), ((byte) (100)), ((byte) (33)), ((byte) (8)), ((byte) (0)), ((byte) (22)), ((byte) (1)), ((byte) (0)), ((byte) (19)), ((byte) (106)), ((byte) (97)), ((byte) (118)), ((byte) (97)), ((byte) (47)), ((byte) (105)), ((byte) (111)), ((byte) (47)), ((byte) (80)), ((byte) (114)), ((byte) (105)), ((byte) (110)), ((byte) (116)), ((byte) (83)), ((byte) (116)), ((byte) (114)), ((byte) (101)), ((byte) (97)), ((byte) (109)), ((byte) (7)), ((byte) (0)), ((byte) (24)), ((byte) (1)), ((byte) (0)), ((byte) (7)), ((byte) (112)), ((byte) (114)), ((byte) (105)), ((byte) (110)), ((byte) (116)), ((byte) (108)), ((byte) (110)), ((byte) (1)), ((byte) (0)), ((byte) (21)), ((byte) (40)), ((byte) (76)), ((byte) (106)), ((byte) (97)), ((byte) (118)), ((byte) (97)), ((byte) (47)), ((byte) (108)), ((byte) (97)), ((byte) (110)), ((byte) (103)), ((byte) (47)), ((byte) (83)), ((byte) (116)), ((byte) (114)), ((byte) (105)), ((byte) (110)), ((byte) (103)), ((byte) (59)), ((byte) (41)), ((byte) (86)), ((byte) (12)), ((byte) (0)), ((byte) (26)), ((byte) (0)), ((byte) (27)), ((byte) (10)), ((byte) (0)), ((byte) (25)), ((byte) (0)), ((byte) (28)), ((byte) (1)), ((byte) (0)), ((byte) (4)), ((byte) (97)), ((byte) (114)), ((byte) (103)), ((byte) (115)), ((byte) (1)), ((byte) (0)), ((byte) (19)), ((byte) (91)), ((byte) (76)), ((byte) (106)), ((byte) (97)), ((byte) (118)), ((byte) (97)), ((byte) (47)), ((byte) (108)), ((byte) (97)), ((byte) (110)), ((byte) (103)), ((byte) (47)), ((byte) (83)), ((byte) (116)), ((byte) (114)), ((byte) (105)), ((byte) (110)), ((byte) (103)), ((byte) (59)), ((byte) (1)), ((byte) (0)), ((byte) (10)), ((byte) (83)), ((byte) (111)), ((byte) (117)), ((byte) (114)), ((byte) (99)), ((byte) (101)), ((byte) (70)), ((byte) (105)), ((byte) (108)), ((byte) (101)), ((byte) (1)), ((byte) (0)), ((byte) (12)), ((byte) (72)), ((byte) (105)), ((byte) (87)), ((byte) (111)), ((byte) (114)), ((byte) (108)), ((byte) (100)), ((byte) (46)), ((byte) (106)), ((byte) (97)), ((byte) (118)), ((byte) (97)), ((byte) (0)), ((byte) (33)), ((byte) (0)), ((byte) (2)), ((byte) (0)), ((byte) (4)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (2)), ((byte) (0)), ((byte) (1)), ((byte) (0)), ((byte) (5)), ((byte) (0)), ((byte) (6)), ((byte) (0)), ((byte) (1)), ((byte) (0)), ((byte) (7)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (47)), ((byte) (0)), ((byte) (1)), ((byte) (0)), ((byte) (1)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (5)), ((byte) (42)), ((byte) (183)), ((byte) (0)), ((byte) (9)), ((byte) (177)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (2)), ((byte) (0)), ((byte) (10)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (6)), ((byte) (0)), ((byte) (1)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (20)), ((byte) (0)), ((byte) (11)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (12)), ((byte) (0)), ((byte) (1)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (5)), ((byte) (0)), ((byte) (12)), ((byte) (0)), ((byte) (13)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (9)), ((byte) (0)), ((byte) (14)), ((byte) (0)), ((byte) (15)), ((byte) (0)), ((byte) (1)), ((byte) (0)), ((byte) (7)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (55)), ((byte) (0)), ((byte) (2)), ((byte) (0)), ((byte) (1)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (9)), ((byte) (178)), ((byte) (0)), ((byte) (21)), ((byte) (18)), ((byte) (23)), ((byte) (182)), ((byte) (0)), ((byte) (29)), ((byte) (177)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (2)), ((byte) (0)), ((byte) (10)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (10)), ((byte) (0)), ((byte) (2)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (23)), ((byte) (0)), ((byte) (8)), ((byte) (0)), ((byte) (24)), ((byte) (0)), ((byte) (11)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (12)), ((byte) (0)), ((byte) (1)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (9)), ((byte) (0)), ((byte) (30)), ((byte) (0)), ((byte) (31)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (1)), ((byte) (0)), ((byte) (32)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (2)), ((byte) (0)), ((byte) (33)) };

    public void testGetPermissions() throws Exception {
        URL url = new URL("http://localhost");
        CodeSource cs = new CodeSource(url, ((Certificate[]) (null)));
        SecureClassLoaderTest.MyClassLoader ldr = new SecureClassLoaderTest.MyClassLoader();
        ldr.getPerms(null);
        ldr.getPerms(cs);
    }

    // /**
    // * Tests defineClass(String, byte[], int, int, CodeSource)
    // */
    // public void _testDefineClassStringbyteArrayintintCodeSource() {
    // MyClassLoader ldr = new MyClassLoader();
    // Class klass = ldr.define(null, klassData, 0, klassData.length, null);
    // assertEquals(klass.getName(), klassName);
    // }
    // 
    // /**
    // * Tests defineClass(String, ByteBuffer, CodeSource)
    // */
    // public void _testDefineClassStringByteBufferCodeSource() {
    // MyClassLoader ldr = new MyClassLoader();
    // ByteBuffer bbuf = ByteBuffer.wrap(klassData);
    // Class klass = ldr.define(null, bbuf, null);
    // assertEquals(klass.getName(), klassName);
    // }
    class MyClassLoader extends SecureClassLoader {
        public MyClassLoader() {
            super();
        }

        public MyClassLoader(ClassLoader parent) {
            super(parent);
        }

        public PermissionCollection getPerms(CodeSource codesource) {
            return super.getPermissions(codesource);
        }

        public Class define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length, ((ProtectionDomain) (null)));
        }

        public Class define(String name, ByteBuffer b, CodeSource cs) {
            return defineClass(name, b, cs);
        }

        public Class define(String name, byte[] b, int off, int len, CodeSource cs) {
            return defineClass(name, b, off, len, cs);
        }
    }
}

