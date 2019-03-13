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


import java.io.IOException;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.AlgorithmParametersSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.DSAParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import junit.framework.TestCase;


/**
 * Tests for <code>AlgorithmParameters</code> class constructors and
 * methods.
 */
public class AlgorithmParametersTest extends TestCase {
    /**
     * Provider
     */
    Provider p;

    /**
     * java.security.AlgorithmParameters#getAlgorithm()
     */
    public void test_getAlgorithm() throws Exception {
        // test: null value
        AlgorithmParameters ap = new AlgorithmParametersTest.DummyAlgorithmParameters(null, p, null);
        TestCase.assertNull(ap.getAlgorithm());
        // test: not null value
        ap = new AlgorithmParametersTest.DummyAlgorithmParameters(null, p, "AAA");
        TestCase.assertEquals("AAA", ap.getAlgorithm());
    }

    /**
     * java.security.AlgorithmParameters#getEncoded()
     */
    public void test_getEncoded() throws Exception {
        final byte[] enc = new byte[]{ 2, 1, 3 };
        AlgorithmParametersTest.MyAlgorithmParameters paramSpi = new AlgorithmParametersTest.MyAlgorithmParameters() {
            protected byte[] engineGetEncoded() throws IOException {
                return enc;
            }
        };
        AlgorithmParameters params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        // 
        // test: IOException if not initialized
        // 
        try {
            params.getEncoded();
            TestCase.fail("should not get encoded from un-initialized instance");
        } catch (IOException e) {
            // expected
        }
        // 
        // test: corresponding spi method is invoked
        // 
        params.init(new AlgorithmParametersTest.MyAlgorithmParameterSpec());
        TestCase.assertSame(enc, params.getEncoded());
    }

    /**
     * java.security.AlgorithmParameters#getEncoded(String)
     */
    public void test_getEncodedLjava_lang_String() throws Exception {
        final byte[] enc = new byte[]{ 2, 1, 3 };
        final String strFormatParam = "format";
        AlgorithmParametersTest.MyAlgorithmParameters paramSpi = new AlgorithmParametersTest.MyAlgorithmParameters() {
            protected byte[] engineGetEncoded(String format) throws IOException {
                TestCase.assertEquals(strFormatParam, format);
                return enc;
            }
        };
        AlgorithmParameters params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        // 
        // test: IOException if not initialized
        // 
        try {
            params.getEncoded(strFormatParam);
            TestCase.fail("should not get encoded from un-initialized instance");
        } catch (IOException e) {
            // expected
        }
        // 
        // test: corresponding spi method is invoked
        // 
        params.init(new AlgorithmParametersTest.MyAlgorithmParameterSpec());
        TestCase.assertSame(enc, params.getEncoded(strFormatParam));
        // 
        // test: if format param is null
        // Regression test for HARMONY-2680
        // 
        paramSpi = new AlgorithmParametersTest.MyAlgorithmParameters() {
            protected byte[] engineGetEncoded(String format) throws IOException {
                TestCase.assertNull(format);// null is passed to spi-provider

                return enc;
            }
        };
        params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        params.init(new AlgorithmParametersTest.MyAlgorithmParameterSpec());
        TestCase.assertSame(enc, params.getEncoded(null));
    }

    /**
     * java.security.AlgorithmParameters#getInstance(String)
     */
    public void test_getInstanceLjava_lang_String() {
        String[] str = new String[]{ "", "qwertyu", "!@#$%^&*()" };
        try {
            AlgorithmParameters ap = AlgorithmParameters.getInstance("ABC");
            checkUnititialized(ap);
            ap.init(new AlgorithmParametersTest.MyAlgorithmParameterSpec());
            checkAP(ap, p);
        } catch (Exception e) {
            TestCase.fail("Unexpected exception");
        }
        for (int i = 0; i < (str.length); i++) {
            try {
                AlgorithmParameters ap = AlgorithmParameters.getInstance(str[i]);
                TestCase.fail(("NoSuchAlgorithmException was not thrown for parameter " + (str[i])));
            } catch (NoSuchAlgorithmException nsae) {
                // expected
            }
        }
    }

    /**
     * java.security.AlgorithmParameters#getInstance(String, String)
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String() {
        String[] alg = new String[]{ "", "qwertyu", "!@#$%^&*()" };
        String[] prv = new String[]{ "", null };
        String[] prv1 = new String[]{ "1234567890", "qwertyu", "!@#$%^&*()" };
        try {
            AlgorithmParameters ap = AlgorithmParameters.getInstance("ABC", "MyProvider");
            checkUnititialized(ap);
            ap.init(new byte[6]);
            checkAP(ap, p);
        } catch (Exception e) {
            TestCase.fail("Unexpected exception");
        }
        for (int i = 0; i < (alg.length); i++) {
            try {
                AlgorithmParameters ap = AlgorithmParameters.getInstance(alg[i], "MyProvider");
                TestCase.fail(("NoSuchAlgorithmException was not thrown for parameter " + (alg[i])));
            } catch (NoSuchAlgorithmException nsae) {
                // expected
            } catch (Exception e) {
                TestCase.fail(((("Incorrect exception " + e) + " was thrown for ") + (alg[i])));
            }
        }
        for (int i = 0; i < (prv.length); i++) {
            try {
                AlgorithmParameters ap = AlgorithmParameters.getInstance("ABC", prv[i]);
                TestCase.fail(("IllegalArgumentException was not thrown for parameter " + (prv[i])));
            } catch (IllegalArgumentException iae) {
                // expected
            } catch (Exception e) {
                TestCase.fail(((("Incorrect exception " + e) + " was thrown for ") + (prv[i])));
            }
        }
        for (int i = 0; i < (prv1.length); i++) {
            try {
                AlgorithmParameters ap = AlgorithmParameters.getInstance("ABC", prv1[i]);
                TestCase.fail(("NoSuchProviderException was not thrown for parameter " + (prv1[i])));
            } catch (NoSuchProviderException nspe) {
                // expected
            } catch (Exception e) {
                TestCase.fail(((("Incorrect exception " + e) + " was thrown for ") + (prv1[i])));
            }
        }
    }

    /**
     * java.security.AlgorithmParameters#getParameterSpec(Class)
     */
    public void test_getParameterSpecLjava_lang_Class() throws Exception {
        final AlgorithmParametersTest.MyAlgorithmParameterSpec myParamSpec = new AlgorithmParametersTest.MyAlgorithmParameterSpec();
        AlgorithmParametersTest.MyAlgorithmParameters paramSpi = new AlgorithmParametersTest.MyAlgorithmParameters() {
            protected AlgorithmParameterSpec engineGetParameterSpec(Class paramSpec) {
                return myParamSpec;
            }
        };
        AlgorithmParameters params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        // 
        // test: InvalidParameterSpecException if not initialized
        // 
        try {
            params.getParameterSpec(null);
            TestCase.fail("No expected InvalidParameterSpecException");
        } catch (InvalidParameterSpecException e) {
            // expected
        }
        try {
            params.getParameterSpec(AlgorithmParametersTest.MyAlgorithmParameterSpec.class);
            TestCase.fail("No expected InvalidParameterSpecException");
        } catch (InvalidParameterSpecException e) {
            // expected
        }
        // 
        // test: corresponding spi method is invoked
        // 
        params.init(new AlgorithmParametersTest.MyAlgorithmParameterSpec());
        TestCase.assertSame(myParamSpec, params.getParameterSpec(AlgorithmParametersTest.MyAlgorithmParameterSpec.class));
        // 
        // test: if paramSpec is null
        // Regression test for HARMONY-2733
        // 
        paramSpi = new AlgorithmParametersTest.MyAlgorithmParameters() {
            protected AlgorithmParameterSpec engineGetParameterSpec(Class paramSpec) {
                TestCase.assertNull(paramSpec);// null is passed to spi-provider

                return null;
            }
        };
        params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        params.init(new AlgorithmParametersTest.MyAlgorithmParameterSpec());
        TestCase.assertNull(params.getParameterSpec(null));
    }

    /**
     * java.security.AlgorithmParameters#getInstance(String, Provider)
     */
    public void test_getInstanceLjava_lang_StringLjava_security_Provider() {
        String[] alg = new String[]{ "", "qwertyu", "!@#$%^&*()" };
        Provider pp = null;
        try {
            AlgorithmParameters ap = AlgorithmParameters.getInstance("ABC", p);
            checkUnititialized(ap);
            ap.init(new byte[6], "aaa");
            checkAP(ap, p);
        } catch (Exception e) {
            TestCase.fail("Unexpected exception");
        }
        for (int i = 0; i < (alg.length); i++) {
            try {
                AlgorithmParameters ap = AlgorithmParameters.getInstance(alg[i], p);
                TestCase.fail(("NoSuchAlgorithmException was not thrown for parameter " + (alg[i])));
            } catch (NoSuchAlgorithmException nsae) {
                // expected
            } catch (Exception e) {
                TestCase.fail(((("Incorrect exception " + e) + " was thrown for ") + (alg[i])));
            }
        }
        try {
            AlgorithmParameters ap = AlgorithmParameters.getInstance("ABC", pp);
            TestCase.fail("IllegalArgumentException was not thrown for NULL provider");
        } catch (IllegalArgumentException iae) {
            // expected
        } catch (Exception e) {
            TestCase.fail((("Incorrect exception " + e) + " was thrown"));
        }
    }

    /**
     * java.security.AlgorithmParameters#getProvider()
     */
    public void test_getProvider() throws Exception {
        // test: null value
        AlgorithmParameters ap = new AlgorithmParametersTest.DummyAlgorithmParameters(null, null, "AAA");
        TestCase.assertNull(ap.getProvider());
        // test: not null value
        ap = new AlgorithmParametersTest.DummyAlgorithmParameters(null, p, "AAA");
        TestCase.assertSame(p, ap.getProvider());
    }

    /**
     * java.security.AlgorithmParameters#init(java.security.spec.AlgorithmParameterSpec)
     */
    public void test_initLjava_security_spec_AlgorithmParameterSpec() throws Exception {
        // 
        // test: corresponding spi method is invoked
        // 
        final AlgorithmParametersTest.MyAlgorithmParameterSpec spec = new AlgorithmParametersTest.MyAlgorithmParameterSpec();
        AlgorithmParametersTest.MyAlgorithmParameters paramSpi = new AlgorithmParametersTest.MyAlgorithmParameters() {
            protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
                TestCase.assertSame(spec, paramSpec);
                runEngineInit_AlgParamSpec = true;
            }
        };
        AlgorithmParameters params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        params.init(spec);
        TestCase.assertTrue(paramSpi.runEngineInit_AlgParamSpec);
        // 
        // test: InvalidParameterSpecException if already initialized
        // 
        try {
            params.init(spec);
            TestCase.fail("No expected InvalidParameterSpecException");
        } catch (InvalidParameterSpecException e) {
            // expected
        }
        params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        params.init(new byte[0]);
        try {
            params.init(spec);
            TestCase.fail("No expected InvalidParameterSpecException");
        } catch (InvalidParameterSpecException e) {
            // expected
        }
        params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        params.init(new byte[0], "format");
        try {
            params.init(spec);
            TestCase.fail("No expected InvalidParameterSpecException");
        } catch (InvalidParameterSpecException e) {
            // expected
        }
        // 
        // test: if paramSpec is null
        // 
        paramSpi = new AlgorithmParametersTest.MyAlgorithmParameters() {
            protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
                TestCase.assertNull(paramSpec);// null is passed to spi-provider

                runEngineInit_AlgParamSpec = true;
            }
        };
        params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        params.init(((AlgorithmParameterSpec) (null)));
        TestCase.assertTrue(paramSpi.runEngineInit_AlgParamSpec);
    }

    /**
     * java.security.AlgorithmParameters#init(byte[])
     */
    public void test_init$B() throws Exception {
        // 
        // test: corresponding spi method is invoked
        // 
        final byte[] enc = new byte[]{ 2, 1, 3 };
        AlgorithmParametersTest.MyAlgorithmParameters paramSpi = new AlgorithmParametersTest.MyAlgorithmParameters() {
            protected void engineInit(byte[] params) throws IOException {
                runEngineInitB$ = true;
                TestCase.assertSame(enc, params);
            }
        };
        AlgorithmParameters params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        params.init(enc);
        TestCase.assertTrue(paramSpi.runEngineInitB$);
        // 
        // test: IOException if already initialized
        // 
        try {
            params.init(enc);
            TestCase.fail("No expected IOException");
        } catch (IOException e) {
            // expected
        }
        params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        params.init(new AlgorithmParametersTest.MyAlgorithmParameterSpec());
        try {
            params.init(enc);
            TestCase.fail("No expected IOException");
        } catch (IOException e) {
            // expected
        }
        params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        params.init(enc, "format");
        try {
            params.init(enc);
            TestCase.fail("No expected IOException");
        } catch (IOException e) {
            // expected
        }
        // 
        // test: if params is null
        // 
        paramSpi = new AlgorithmParametersTest.MyAlgorithmParameters() {
            protected void engineInit(byte[] params) throws IOException {
                runEngineInitB$ = true;
                TestCase.assertNull(params);// null is passed to spi-provider

            }
        };
        params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        params.init(((byte[]) (null)));
        TestCase.assertTrue(paramSpi.runEngineInitB$);
    }

    /**
     * java.security.AlgorithmParameters#init(byte[],String)
     */
    public void test_init$BLjava_lang_String() throws Exception {
        // 
        // test: corresponding spi method is invoked
        // 
        final byte[] enc = new byte[]{ 2, 1, 3 };
        final String strFormatParam = "format";
        AlgorithmParametersTest.MyAlgorithmParameters paramSpi = new AlgorithmParametersTest.MyAlgorithmParameters() {
            protected void engineInit(byte[] params, String format) throws IOException {
                runEngineInitB$String = true;
                TestCase.assertSame(enc, params);
                TestCase.assertSame(strFormatParam, format);
            }
        };
        AlgorithmParameters params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        params.init(enc, strFormatParam);
        TestCase.assertTrue(paramSpi.runEngineInitB$String);
        // 
        // test: IOException if already initialized
        // 
        try {
            params.init(enc, strFormatParam);
            TestCase.fail("No expected IOException");
        } catch (IOException e) {
            // expected
        }
        params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        params.init(new AlgorithmParametersTest.MyAlgorithmParameterSpec());
        try {
            params.init(enc, strFormatParam);
            TestCase.fail("No expected IOException");
        } catch (IOException e) {
            // expected
        }
        params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        params.init(enc);
        try {
            params.init(enc, strFormatParam);
            TestCase.fail("No expected IOException");
        } catch (IOException e) {
            // expected
        }
        // 
        // test: if params and format are null
        // Regression test for HARMONY-2724
        // 
        paramSpi = new AlgorithmParametersTest.MyAlgorithmParameters() {
            protected void engineInit(byte[] params, String format) throws IOException {
                runEngineInitB$String = true;
                // null is passed to spi-provider
                TestCase.assertNull(params);
                TestCase.assertNull(format);
            }
        };
        params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        params.init(null, null);
        TestCase.assertTrue(paramSpi.runEngineInitB$String);
    }

    /**
     * java.security.AlgorithmParameters#toString()
     */
    public void test_toString() throws Exception {
        final String str = "AlgorithmParameters";
        AlgorithmParametersTest.MyAlgorithmParameters paramSpi = new AlgorithmParametersTest.MyAlgorithmParameters() {
            protected String engineToString() {
                return str;
            }
        };
        AlgorithmParameters params = new AlgorithmParametersTest.DummyAlgorithmParameters(paramSpi, p, "algorithm");
        TestCase.assertNull("unititialized", params.toString());
        params.init(new byte[0]);
        TestCase.assertSame(str, params.toString());
    }

    /**
     * Tests DSA AlgorithmParameters provider
     */
    public void testDSAProvider() throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance("DSA");
        TestCase.assertEquals("Algorithm", "DSA", params.getAlgorithm());
        // init(AlgorithmParameterSpec)
        BigInteger p = BigInteger.ONE;
        BigInteger q = BigInteger.TEN;
        BigInteger g = BigInteger.ZERO;
        params.init(new DSAParameterSpec(p, q, g));
        // getEncoded() and getEncoded(String) (TODO verify returned encoding)
        byte[] enc = params.getEncoded();
        TestCase.assertNotNull(enc);
        TestCase.assertNotNull(params.getEncoded("ASN.1"));
        // TODO assertNotNull(params.getEncoded(null)); // HARMONY-2680
        // getParameterSpec(Class)
        DSAParameterSpec spec = params.getParameterSpec(DSAParameterSpec.class);
        TestCase.assertEquals("p is wrong ", p, spec.getP());
        TestCase.assertEquals("q is wrong ", q, spec.getQ());
        TestCase.assertEquals("g is wrong ", g, spec.getG());
        // init(byte[])
        params = AlgorithmParameters.getInstance("DSA");
        params.init(enc);
        TestCase.assertTrue("param encoded is different", Arrays.equals(enc, params.getEncoded()));
        // init(byte[], String)
        params = AlgorithmParameters.getInstance("DSA");
        params.init(enc, "ASN.1");
        TestCase.assertTrue("param encoded is different", Arrays.equals(enc, params.getEncoded()));
        params = AlgorithmParameters.getInstance("DSA");
        try {
            params.init(enc, "DOUGLASMAWSON");
            TestCase.fail("unsupported format should have raised IOException");
        } catch (IOException e) {
            // expected
        }
    }

    /**
     * Tests OAEP AlgorithmParameters provider
     */
    public void testOAEPProvider() throws Exception {
        AlgorithmParameters params = AlgorithmParameters.getInstance("OAEP");
        TestCase.assertEquals("Algorithm", "OAEP", params.getAlgorithm());
    }

    /**
     * Test for <code>AlgorithmParameters</code> constructor
     * Assertion: returns AlgorithmParameters object
     */
    public void testAlgorithmParametersConst() throws Exception {
        AlgorithmParametersSpi spi = new AlgorithmParametersTest.MyAlgorithmParameters();
        AlgorithmParameters ap = new AlgorithmParametersTest.myAlgP(spi, p, "ABC");
        checkUnititialized(ap);
        ap.init(new byte[6], "aaa");
        checkAP(ap, p);
        // NULL parameters
        try {
            ap = new AlgorithmParametersTest.myAlgP(null, null, null);
        } catch (Exception e) {
            TestCase.fail("Exception should be not thrown");
        }
    }

    @SuppressWarnings("serial")
    private class MyProvider extends Provider {
        MyProvider() {
            super("MyProvider", 1.0, "Provider for testing");
            put("AlgorithmParameters.ABC", AlgorithmParametersTest.MyAlgorithmParameters.class.getName());
        }

        MyProvider(String name, double version, String info) {
            super(name, version, info);
        }
    }

    private class MyAlgorithmParameterSpec implements AlgorithmParameterSpec {}

    private class DummyAlgorithmParameters extends AlgorithmParameters {
        public DummyAlgorithmParameters(AlgorithmParametersSpi paramSpi, Provider provider, String algorithm) {
            super(paramSpi, provider, algorithm);
        }
    }

    public static class MyAlgorithmParameters extends AlgorithmParametersSpi {
        public boolean runEngineInit_AlgParamSpec = false;

        public boolean runEngineInitB$ = false;

        public boolean runEngineInitB$String = false;

        public static boolean runEngineToString = false;

        protected void engineInit(AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
        }

        protected void engineInit(byte[] params) throws IOException {
        }

        protected void engineInit(byte[] params, String format) throws IOException {
        }

        protected AlgorithmParameterSpec engineGetParameterSpec(Class paramSpec) throws InvalidParameterSpecException {
            return null;
        }

        protected byte[] engineGetEncoded() throws IOException {
            return null;
        }

        protected byte[] engineGetEncoded(String format) throws IOException {
            return null;
        }

        protected String engineToString() {
            AlgorithmParametersTest.MyAlgorithmParameters.runEngineToString = true;
            return "AlgorithmParameters";
        }
    }

    /**
     * Additional class to verify AlgorithmParameters constructor
     */
    class myAlgP extends AlgorithmParameters {
        public myAlgP(AlgorithmParametersSpi spi, Provider prov, String alg) {
            super(spi, prov, alg);
        }
    }
}

