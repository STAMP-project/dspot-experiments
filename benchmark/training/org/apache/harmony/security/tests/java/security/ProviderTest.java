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
package org.apache.harmony.security.tests.java.security;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;
import java.security.Provider;
import java.security.Security;
import java.security.SecurityPermission;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;


/**
 * Tests for <code>Provider</code> constructor and methods
 */
// END android-added
public class ProviderTest extends TestCase {
    /* Implementation note: The algorithm name ASH-1 might seem a bit strange,
    but since the algorithms cannot be uninstalled anymore we need to make
    sure there are not side-effects on other tests. Simply inserting SHA-1
    destroys the existing provider infrastructure.
     */
    Provider[] storedProviders;

    Provider p;

    /* Class under test for void Provider() */
    public final void testProvider() {
        if (!(p.getProperty("Provider.id name").equals(String.valueOf(p.getName())))) {
            TestCase.fail("Incorrect \"Provider.id name\" value");
        }
        if (!(p.getProperty("Provider.id version").equals(String.valueOf(p.getVersion())))) {
            TestCase.fail("Incorrect \"Provider.id version\" value");
        }
        if (!(p.getProperty("Provider.id info").equals(String.valueOf(p.getInfo())))) {
            TestCase.fail("Incorrect \"Provider.id info\" value");
        }
        if (!(p.getProperty("Provider.id className").equals(p.getClass().getName()))) {
            TestCase.fail("Incorrect \"Provider.id className\" value");
        }
    }

    public final void testClear() {
        p.clear();
        TestCase.assertNull(p.getProperty("MessageDigest.SHA-1"));
    }

    /* Class under test for void Provider(String, double, String) */
    public final void testProviderStringdoubleString() {
        Provider p = new ProviderTest.MyProvider("Provider name", 123.456, "Provider info");
        TestCase.assertEquals("Provider name", p.getName());
        TestCase.assertEquals(123.456, p.getVersion(), 0L);
        TestCase.assertEquals("Provider info", p.getInfo());
    }

    public final void testGetName() {
        TestCase.assertEquals("MyProvider", p.getName());
    }

    public final void testGetVersion() {
        TestCase.assertEquals(1.0, p.getVersion(), 0L);
    }

    public final void testGetInfo() {
        TestCase.assertEquals("Provider for testing", p.getInfo());
    }

    /* Class under test for void putAll(Map) */
    public final void testPutAllMap() {
        HashMap hm = new HashMap();
        hm.put("MessageDigest.SHA-1", "aaa.bbb.ccc.ddd");
        hm.put("Property 1", "value 1");
        hm.put("serviceName.algName attrName", "attrValue");
        hm.put("Alg.Alias.engineClassName.aliasName", "standardName");
        p.putAll(hm);
        if ((((!("value 1".equals(p.getProperty("Property 1").trim()))) || (!("attrValue".equals(p.getProperty("serviceName.algName attrName").trim())))) || (!("standardName".equals(p.getProperty("Alg.Alias.engineClassName.aliasName").trim())))) || (!("aaa.bbb.ccc.ddd".equals(p.getProperty("MessageDigest.SHA-1").trim())))) {
            TestCase.fail("Incorrect property value");
        }
    }

    /* Class under test for Set entrySet() */
    public final void testEntrySet() {
        p.put("MessageDigest.SHA-256", "aaa.bbb.ccc.ddd");
        Set s = p.entrySet();
        try {
            s.clear();
            TestCase.fail("Must return unmodifiable set");
        } catch (UnsupportedOperationException e) {
        }
        TestCase.assertEquals("Incorrect set size", 8, s.size());
        for (Iterator it = s.iterator(); it.hasNext();) {
            Map.Entry e = ((Map.Entry) (it.next()));
            String key = ((String) (e.getKey()));
            String val = ((String) (e.getValue()));
            if ((key.equals("MessageDigest.SHA-1")) && (val.equals("SomeClassName"))) {
                continue;
            }
            if ((key.equals("Alg.Alias.MessageDigest.SHA1")) && (val.equals("SHA-1"))) {
                continue;
            }
            if ((key.equals("MessageDigest.abc")) && (val.equals("SomeClassName"))) {
                continue;
            }
            if ((key.equals("Provider.id className")) && (val.equals(p.getClass().getName()))) {
                continue;
            }
            if ((key.equals("Provider.id name")) && (val.equals("MyProvider"))) {
                continue;
            }
            if ((key.equals("MessageDigest.SHA-256")) && (val.equals("aaa.bbb.ccc.ddd"))) {
                continue;
            }
            if ((key.equals("Provider.id version")) && (val.equals("1.0"))) {
                continue;
            }
            if ((key.equals("Provider.id info")) && (val.equals("Provider for testing"))) {
                continue;
            }
            TestCase.fail("Incorrect set");
        }
    }

    /* Class under test for Set keySet() */
    public final void testKeySet() {
        p.put("MessageDigest.SHA-256", "aaa.bbb.ccc.ddd");
        Set<Object> s = p.keySet();
        try {
            s.clear();
        } catch (UnsupportedOperationException e) {
        }
        Set s1 = p.keySet();
        TestCase.assertNotSame(s, s1);
        TestCase.assertFalse(s1.isEmpty());
        TestCase.assertEquals(8, s1.size());
        TestCase.assertTrue(s1.contains("MessageDigest.SHA-256"));
        TestCase.assertTrue(s1.contains("MessageDigest.SHA-1"));
        TestCase.assertTrue(s1.contains("Alg.Alias.MessageDigest.SHA1"));
        TestCase.assertTrue(s1.contains("MessageDigest.abc"));
        TestCase.assertTrue(s1.contains("Provider.id info"));
        TestCase.assertTrue(s1.contains("Provider.id className"));
        TestCase.assertTrue(s1.contains("Provider.id version"));
        TestCase.assertTrue(s1.contains("Provider.id name"));
    }

    /* Class under test for Collection values() */
    public final void testValues() {
        p.put("MessageDigest.ASH-256", "aaa.bbb.ccc.ddd");
        Collection<Object> c = p.values();
        try {
            c.clear();
        } catch (UnsupportedOperationException e) {
        }
        Collection c1 = p.values();
        TestCase.assertNotSame(c, c1);
        TestCase.assertFalse(c1.isEmpty());
        TestCase.assertEquals(8, c1.size());
        TestCase.assertTrue(c1.contains("MyProvider"));
        TestCase.assertTrue(c1.contains("aaa.bbb.ccc.ddd"));
        TestCase.assertTrue(c1.contains("Provider for testing"));
        TestCase.assertTrue(c1.contains("1.0"));
        TestCase.assertTrue(c1.contains("SomeClassName"));
        TestCase.assertTrue(c1.contains("SHA-1"));
        TestCase.assertTrue(c1.contains(p.getClass().getName()));
    }

    /* Class under test for Object put(Object, Object) */
    public final void testPutObjectObject() {
        p.put("MessageDigest.SHA-1", "aaa.bbb.ccc.ddd");
        p.put("Type.Algorithm", "className");
        TestCase.assertEquals("aaa.bbb.ccc.ddd", p.getProperty("MessageDigest.SHA-1").trim());
        Set services = p.getServices();
        TestCase.assertEquals(3, services.size());
        for (Iterator it = services.iterator(); it.hasNext();) {
            Provider.Service s = ((Provider.Service) (it.next()));
            if ((("Type".equals(s.getType())) && ("Algorithm".equals(s.getAlgorithm()))) && ("className".equals(s.getClassName()))) {
                continue;
            }
            if ((("MessageDigest".equals(s.getType())) && ("SHA-1".equals(s.getAlgorithm()))) && ("aaa.bbb.ccc.ddd".equals(s.getClassName()))) {
                continue;
            }
            if ((("MessageDigest".equals(s.getType())) && ("abc".equals(s.getAlgorithm()))) && ("SomeClassName".equals(s.getClassName()))) {
                continue;
            }
            TestCase.fail("Incorrect service");
        }
    }

    /* Class under test for Object remove(Object) */
    public final void testRemoveObject() {
        Object o = p.remove("MessageDigest.SHA-1");
        TestCase.assertEquals("SomeClassName", o);
        TestCase.assertNull(p.getProperty("MessageDigest.SHA-1"));
        TestCase.assertEquals(1, p.getServices().size());
    }

    public final void testService1() {
        p.put("MessageDigest.SHA-1", "AnotherClassName");
        Provider.Service s = p.getService("MessageDigest", "SHA-1");
        TestCase.assertEquals("AnotherClassName", s.getClassName());
    }

    public final void testGetServiceCaseSensitivity() {
        p.put("i.I", "foo");
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(new Locale("tr", "TR"));
        try {
            TestCase.assertEquals("foo", p.getService("i", "i").getClassName());
            TestCase.assertEquals("foo", p.getService("i", "I").getClassName());
            TestCase.assertNull(p.getService("\u0130", "\u0130"));// Turkish dotless i and dotted I

            TestCase.assertNull(p.getService("\u0131", "\u0131"));
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    // Regression for HARMONY-2760.
    public void testConstructor() {
        ProviderTest.MyProvider myProvider = new ProviderTest.MyProvider(null, 1, null);
        TestCase.assertNull(myProvider.getName());
        TestCase.assertNull(myProvider.getInfo());
        TestCase.assertEquals("null", myProvider.getProperty("Provider.id name"));
        TestCase.assertEquals("null", myProvider.getProperty("Provider.id info"));
    }

    // END android-added
    class MyProvider extends Provider {
        MyProvider() {
            super("MyProvider", 1.0, "Provider for testing");
            put("MessageDigest.SHA-1", "SomeClassName");
            put("MessageDigest.abc", "SomeClassName");
            put("Alg.Alias.MessageDigest.SHA1", "SHA-1");
        }

        MyProvider(String name, double version, String info) {
            super(name, version, info);
        }

        // BEGIN android-added
        public void putService(Provider.Service s) {
            super.putService(s);
        }

        // END android-added
        // BEGIN android-added
        public void removeService(Provider.Service s) {
            super.removeService(s);
        }

        // END android-added
        // BEGIN android-added
        public int getNumServices() {
            return getServices().size();
        }
    }

    // BEGIN android-added
    public final void testService2() {
        Provider[] pp = Security.getProviders("MessageDigest.ASH-1");
        if (pp == null) {
            return;
        }
        Provider p2 = pp[0];
        String old = p2.getProperty("MessageDigest.ASH-1");
        p2.put("MessageDigest.ASH-1", "AnotherClassName");
        Provider.Service s = p2.getService("MessageDigest", "ASH-1");
        if (!("AnotherClassName".equals(s.getClassName()))) {
            TestCase.fail(("Incorrect class name " + (s.getClassName())));
        }
        try {
            s.newInstance(null);
            TestCase.fail("No expected NoSuchAlgorithmException");
        } catch (NoSuchAlgorithmException e) {
        }
    }

    // END android-added
    // BEGIN android-added
    public final void testGetServices() {
        ProviderTest.MyProvider myProvider = new ProviderTest.MyProvider(null, 1, null);
        Set<Provider.Service> services = myProvider.getServices();
        TestCase.assertEquals(0, services.size());
        Provider.Service[] s = new Provider.Service[3];
        s[0] = new Provider.Service(p, "type1", "algorithm1", "className1", null, null);
        s[1] = new Provider.Service(p, "type2", "algorithm2", "className2", null, null);
        s[2] = new Provider.Service(p, "type3", "algorithm3", "className3", null, null);
        myProvider.putService(s[0]);
        myProvider.putService(s[1]);
        TestCase.assertEquals(2, myProvider.getNumServices());
        Set<Provider.Service> actual = myProvider.getServices();
        TestCase.assertTrue(actual.contains(s[0]));
        TestCase.assertTrue(actual.contains(s[1]));
        TestCase.assertTrue((!(actual.contains(s[2]))));
        myProvider.removeService(s[1]);
        actual = myProvider.getServices();
        TestCase.assertEquals(1, myProvider.getNumServices());
        TestCase.assertTrue(actual.contains(s[0]));
        TestCase.assertTrue((!(actual.contains(s[1]))));
        TestCase.assertTrue((!(actual.contains(s[2]))));
        myProvider.putService(s[2]);
        actual = myProvider.getServices();
        TestCase.assertEquals(2, myProvider.getNumServices());
        TestCase.assertTrue(actual.contains(s[0]));
        TestCase.assertTrue((!(actual.contains(s[1]))));
        TestCase.assertTrue(actual.contains(s[2]));
    }

    // END android-added
    // BEGIN android-added
    public final void testPutService() {
        ProviderTest.MyProvider myProvider = new ProviderTest.MyProvider(null, 1, null);
        Provider.Service[] s = new Provider.Service[3];
        s[0] = new Provider.Service(p, "type1", "algorithm1", "className1", null, null);
        s[1] = new Provider.Service(p, "type2", "algorithm2", "className2", null, null);
        s[2] = new Provider.Service(p, "type3", "algorithm3", "className3", null, null);
        myProvider.putService(s[0]);
        myProvider.putService(s[1]);
        TestCase.assertEquals(2, myProvider.getNumServices());
        Set<Provider.Service> actual = myProvider.getServices();
        TestCase.assertTrue(actual.contains(s[0]));
        TestCase.assertTrue(actual.contains(s[1]));
        TestCase.assertTrue((!(actual.contains(s[2]))));
        myProvider.removeService(s[1]);
        TestCase.assertEquals(1, myProvider.getNumServices());
        actual = myProvider.getServices();
        TestCase.assertTrue(actual.contains(s[0]));
        TestCase.assertTrue((!(actual.contains(s[1]))));
        TestCase.assertTrue((!(actual.contains(s[2]))));
        myProvider.putService(s[2]);
        actual = myProvider.getServices();
        TestCase.assertEquals(2, myProvider.getNumServices());
        TestCase.assertTrue(actual.contains(s[0]));
        TestCase.assertTrue((!(actual.contains(s[1]))));
        TestCase.assertTrue(actual.contains(s[2]));
        myProvider.putService(s[2]);
        actual = myProvider.getServices();
        TestCase.assertEquals(2, myProvider.getNumServices());
        TestCase.assertTrue(actual.contains(s[0]));
        TestCase.assertTrue((!(actual.contains(s[1]))));
        TestCase.assertTrue(actual.contains(s[2]));
        try {
            myProvider.putService(null);
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    // END android-added
    // BEGIN android-added
    public final void testRemoveService() {
        ProviderTest.MyProvider myProvider = new ProviderTest.MyProvider(null, 1, null);
        try {
            myProvider.removeService(null);
            TestCase.fail("NullPoiterException expected");
        } catch (NullPointerException e) {
            // expected
        }
        Provider.Service[] s = new Provider.Service[3];
        s[0] = new Provider.Service(p, "type0", "algorithm0", "className0", null, null);
        s[1] = new Provider.Service(p, "type1", "algorithm1", "className1", null, null);
        s[2] = new Provider.Service(p, "type2", "algorithm2", "className2", null, null);
        try {
            myProvider.removeService(s[0]);
        } catch (NullPointerException e) {
            TestCase.fail("Unexpected exception");
        }
        myProvider.putService(s[0]);
        myProvider.putService(s[1]);
        myProvider.putService(s[2]);
        TestCase.assertEquals(3, myProvider.getNumServices());
        Set<Provider.Service> actual = myProvider.getServices();
        TestCase.assertTrue(actual.contains(s[0]));
        TestCase.assertTrue(actual.contains(s[1]));
        TestCase.assertTrue(actual.contains(s[2]));
        myProvider.removeService(s[1]);
        TestCase.assertEquals(2, myProvider.getNumServices());
        actual = myProvider.getServices();
        TestCase.assertTrue(actual.contains(s[0]));
        TestCase.assertTrue((!(actual.contains(s[1]))));
        TestCase.assertTrue(actual.contains(s[2]));
        myProvider.removeService(s[0]);
        TestCase.assertEquals(1, myProvider.getNumServices());
        actual = myProvider.getServices();
        TestCase.assertTrue((!(actual.contains(s[0]))));
        TestCase.assertTrue((!(actual.contains(s[1]))));
        TestCase.assertTrue(actual.contains(s[2]));
        myProvider.removeService(s[2]);
        TestCase.assertEquals(0, myProvider.getNumServices());
        actual = myProvider.getServices();
        TestCase.assertTrue((!(actual.contains(s[0]))));
        TestCase.assertTrue((!(actual.contains(s[1]))));
        TestCase.assertTrue((!(actual.contains(s[2]))));
        try {
            myProvider.removeService(null);
            TestCase.fail("NullPoiterException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    // END android-added
    // BEGIN android-added
    public final void testLoad() throws IOException {
        InputStream is = new ByteArrayInputStream(writeProperties());
        ProviderTest.MyProvider myProvider = new ProviderTest.MyProvider("name", 1, "info");
        myProvider.load(is);
        TestCase.assertEquals("tests.security", myProvider.get("test.pkg"));
        TestCase.assertEquals("Unit Tests", myProvider.get("test.proj"));
        TestCase.assertNull(myProvider.get("#commented.entry"));
        TestCase.assertEquals("info", myProvider.get("Provider.id info"));
        String className = myProvider.getClass().toString();
        TestCase.assertEquals(className.substring("class ".length(), className.length()), myProvider.get("Provider.id className"));
        TestCase.assertEquals("1.0", myProvider.get("Provider.id version"));
        try {
            myProvider.load(((InputStream) (null)));
            TestCase.fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }
    }

    // END android-added
    // BEGIN android-added
    public final void testLoad2() {
        class TestInputStream extends InputStream {
            @Override
            public int read() throws IOException {
                throw new IOException();
            }
        }
        ProviderTest.MyProvider p = new ProviderTest.MyProvider();
        try {
            p.load(new TestInputStream());
            TestCase.fail("expected IOException");
        } catch (IOException e) {
            // expected
        }
    }

    // END android-added
    // BEGIN android-added
    static class TestSecurityManager extends SecurityManager {
        boolean called = false;

        private final String permissionName;

        public TestSecurityManager(String permissionName) {
            this.permissionName = permissionName;
        }

        @Override
        public void checkPermission(Permission permission) {
            if (permission instanceof SecurityPermission) {
                if (permissionName.equals(permission.getName())) {
                    called = true;
                }
            }
        }
    }
}

