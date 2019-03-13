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
package tests.api.javax.security.auth;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.security.auth.x500.X500Principal;
import junit.framework.TestCase;
import org.apache.harmony.security.tests.support.cert.TestUtils;


/**
 * Tests for <code>X500Principal</code> class constructors and methods.
 */
public class X500PrincipalTest extends TestCase {
    /**
     * javax.security.auth.x500.X500Principal#X500Principal(String name)
     */
    public void test_X500Principal_01() {
        String name = "CN=Duke,OU=JavaSoft,O=Sun Microsystems,C=US";
        try {
            X500Principal xpr = new X500Principal(name);
            TestCase.assertNotNull("Null object returned", xpr);
            String resName = xpr.getName();
            TestCase.assertEquals(name, resName);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        try {
            X500Principal xpr = new X500Principal(((String) (null)));
            TestCase.fail("NullPointerException wasn't thrown");
        } catch (NullPointerException npe) {
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of NullPointerException"));
        }
        try {
            X500Principal xpr = new X500Principal("X500PrincipalName");
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException npe) {
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of IllegalArgumentException"));
        }
    }

    /**
     * javax.security.auth.x500.X500Principal#X500Principal(InputStream is)
     */
    public void test_X500Principal_02() {
        String name = "CN=Duke,OU=JavaSoft,O=Sun Microsystems,C=US";
        byte[] ba = getByteArray(TestUtils.getX509Certificate_v1());
        ByteArrayInputStream is = new ByteArrayInputStream(ba);
        InputStream isNull = null;
        try {
            X500Principal xpr = new X500Principal(is);
            TestCase.assertNotNull("Null object returned", xpr);
            byte[] resArray = xpr.getEncoded();
            TestCase.assertEquals(ba.length, resArray.length);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        try {
            X500Principal xpr = new X500Principal(isNull);
            TestCase.fail("NullPointerException wasn't thrown");
        } catch (NullPointerException npe) {
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of NullPointerException"));
        }
        is = new ByteArrayInputStream(name.getBytes());
        try {
            X500Principal xpr = new X500Principal(is);
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException npe) {
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of IllegalArgumentException"));
        }
    }

    /**
     * javax.security.auth.x500.X500Principal#X500Principal(byte[] name)
     */
    public void test_X500Principal_03() {
        String name = "CN=Duke,OU=JavaSoft,O=Sun Microsystems,C=US";
        byte[] ba = getByteArray(TestUtils.getX509Certificate_v1());
        byte[] baNull = null;
        try {
            X500Principal xpr = new X500Principal(ba);
            TestCase.assertNotNull("Null object returned", xpr);
            byte[] resArray = xpr.getEncoded();
            TestCase.assertEquals(ba.length, resArray.length);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        try {
            X500Principal xpr = new X500Principal(baNull);
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException npe) {
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of IllegalArgumentException"));
        }
        ba = name.getBytes();
        try {
            X500Principal xpr = new X500Principal(ba);
            TestCase.fail("IllegalArgumentException wasn't thrown");
        } catch (IllegalArgumentException npe) {
        } catch (Exception e) {
            TestCase.fail((e + " was thrown instead of IllegalArgumentException"));
        }
    }

    /**
     * javax.security.auth.x500.X500Principal#getName()
     */
    public void test_getName() {
        String name = "CN=Duke,OU=JavaSoft,O=Sun Microsystems,C=US";
        X500Principal xpr = new X500Principal(name);
        try {
            String resName = xpr.getName();
            TestCase.assertEquals(name, resName);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
    }

    /**
     * javax.security.auth.x500.X500Principal#getName(String format)
     */
    public void test_getName_Format() {
        String name = "CN=Duke,OU=JavaSoft,O=Sun Microsystems,C=US";
        String expectedName = "cn=duke,ou=javasoft,o=sun microsystems,c=us";
        X500Principal xpr = new X500Principal(name);
        try {
            String resName = xpr.getName(X500Principal.CANONICAL);
            TestCase.assertEquals(expectedName, resName);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        expectedName = "CN=Duke, OU=JavaSoft, O=Sun Microsystems, C=US";
        try {
            String resName = xpr.getName(X500Principal.RFC1779);
            TestCase.assertEquals(expectedName, resName);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        try {
            String resName = xpr.getName(X500Principal.RFC2253);
            TestCase.assertEquals(name, resName);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
        try {
            String resName = xpr.getName(null);
            TestCase.fail("IllegalArgumentException  wasn't thrown");
        } catch (IllegalArgumentException iae) {
        }
        try {
            String resName = xpr.getName("RFC2254");
            TestCase.fail("IllegalArgumentException  wasn't thrown");
        } catch (IllegalArgumentException iae) {
        }
    }

    /**
     * javax.security.auth.x500.X500Principal#hashCode()
     */
    public void test_hashCode() {
        String name = "CN=Duke,OU=JavaSoft,O=Sun Microsystems,C=US";
        X500Principal xpr = new X500Principal(name);
        try {
            int res = xpr.hashCode();
            TestCase.assertNotNull(res);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
    }

    /**
     * javax.security.auth.x500.X500Principal#toString()
     */
    public void test_toString() {
        String name = "CN=Duke, OU=JavaSoft, O=Sun Microsystems, C=US";
        X500Principal xpr = new X500Principal(name);
        try {
            String res = xpr.toString();
            TestCase.assertNotNull(res);
            TestCase.assertEquals(name, res);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
    }

    /**
     * javax.security.auth.x500.X500Principal#getEncoded()
     */
    public void test_getEncoded() {
        byte[] ba = getByteArray(TestUtils.getX509Certificate_v1());
        X500Principal xpr = new X500Principal(ba);
        try {
            byte[] res = xpr.getEncoded();
            TestCase.assertNotNull(res);
            TestCase.assertEquals(ba.length, res.length);
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
    }

    /**
     * javax.security.auth.x500.X500Principal#equals(Object o)
     */
    public void test_equals() {
        String name1 = "CN=Duke, OU=JavaSoft, O=Sun Microsystems, C=US";
        String name2 = "cn=duke,ou=javasoft,o=sun microsystems,c=us";
        String name3 = "CN=Alex Astapchuk, OU=SSG, O=Intel ZAO, C=RU";
        X500Principal xpr1 = new X500Principal(name1);
        X500Principal xpr2 = new X500Principal(name2);
        X500Principal xpr3 = new X500Principal(name3);
        try {
            TestCase.assertTrue("False returned", xpr1.equals(xpr2));
            TestCase.assertFalse("True returned", xpr1.equals(xpr3));
        } catch (Exception e) {
            TestCase.fail(("Unexpected exception: " + e));
        }
    }
}

