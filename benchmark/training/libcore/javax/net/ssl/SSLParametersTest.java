/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.javax.net.ssl;


import java.util.Arrays;
import javax.net.ssl.SSLParameters;
import junit.framework.TestCase;


public class SSLParametersTest extends TestCase {
    public void test_SSLParameters_emptyConstructor() {
        SSLParameters p = new SSLParameters();
        TestCase.assertNull(p.getCipherSuites());
        TestCase.assertNull(p.getProtocols());
        TestCase.assertFalse(p.getWantClientAuth());
        TestCase.assertFalse(p.getNeedClientAuth());
    }

    public void test_SSLParameters_cipherSuitesConstructor() {
        String[] cipherSuites = new String[]{ "foo", null, "bar" };
        SSLParameters p = new SSLParameters(cipherSuites);
        TestCase.assertNotNull(p.getCipherSuites());
        TestCase.assertNotSame(cipherSuites, p.getCipherSuites());
        TestCase.assertEquals(Arrays.asList(cipherSuites), Arrays.asList(p.getCipherSuites()));
        TestCase.assertNull(p.getProtocols());
        TestCase.assertFalse(p.getWantClientAuth());
        TestCase.assertFalse(p.getNeedClientAuth());
    }

    public void test_SSLParameters_cpherSuitesProtocolsConstructor() {
        String[] cipherSuites = new String[]{ "foo", null, "bar" };
        String[] protocols = new String[]{ "baz", null, "qux" };
        SSLParameters p = new SSLParameters(cipherSuites, protocols);
        TestCase.assertNotNull(p.getCipherSuites());
        TestCase.assertNotNull(p.getProtocols());
        TestCase.assertNotSame(cipherSuites, p.getCipherSuites());
        TestCase.assertNotSame(protocols, p.getProtocols());
        TestCase.assertEquals(Arrays.asList(cipherSuites), Arrays.asList(p.getCipherSuites()));
        TestCase.assertEquals(Arrays.asList(protocols), Arrays.asList(p.getProtocols()));
        TestCase.assertFalse(p.getWantClientAuth());
        TestCase.assertFalse(p.getNeedClientAuth());
    }

    public void test_SSLParameters_CipherSuites() {
        SSLParameters p = new SSLParameters();
        TestCase.assertNull(p.getCipherSuites());
        // confirm clone on input
        String[] cipherSuites = new String[]{ "fnord" };
        String[] copy = cipherSuites.clone();
        p.setCipherSuites(copy);
        copy[0] = null;
        TestCase.assertEquals(Arrays.asList(cipherSuites), Arrays.asList(p.getCipherSuites()));
        // confirm clone on output
        TestCase.assertNotSame(p.getCipherSuites(), p.getCipherSuites());
    }

    public void test_SSLParameters_Protocols() {
        SSLParameters p = new SSLParameters();
        TestCase.assertNull(p.getProtocols());
        // confirm clone on input
        String[] protocols = new String[]{ "fnord" };
        String[] copy = protocols.clone();
        p.setProtocols(copy);
        copy[0] = null;
        TestCase.assertEquals(Arrays.asList(protocols), Arrays.asList(p.getProtocols()));
        // confirm clone on output
        TestCase.assertNotSame(p.getProtocols(), p.getProtocols());
    }

    public void test_SSLParameters_ClientAuth() {
        SSLParameters p = new SSLParameters();
        TestCase.assertFalse(p.getWantClientAuth());
        TestCase.assertFalse(p.getNeedClientAuth());
        // confirm turning one on by itself
        p.setWantClientAuth(true);
        TestCase.assertTrue(p.getWantClientAuth());
        TestCase.assertFalse(p.getNeedClientAuth());
        // confirm turning setting on toggles the other
        p.setNeedClientAuth(true);
        TestCase.assertFalse(p.getWantClientAuth());
        TestCase.assertTrue(p.getNeedClientAuth());
        // confirm toggling back
        p.setWantClientAuth(true);
        TestCase.assertTrue(p.getWantClientAuth());
        TestCase.assertFalse(p.getNeedClientAuth());
    }
}

