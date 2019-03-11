/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.security.tests.java.security;


import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.security.Security;
import junit.framework.TestCase;


public class SecureRandom2Test extends TestCase {
    private static final byte[] SEED_BYTES = new byte[]{ ((byte) (33)), ((byte) (15)), ((byte) (-3)), ((byte) (22)), ((byte) (77)), ((byte) (-16)), ((byte) (-33)), ((byte) (56)) };

    private static final int SEED_SIZE = 539;

    private static final long SEED_VALUE = 5335486759L;

    public void testGetProvider() {
        SecureRandom sr1 = new SecureRandom();
        TestCase.assertNotNull(sr1.getProvider());
        SecureRandom sr2 = new SecureRandom(SecureRandom2Test.SEED_BYTES);
        TestCase.assertNotNull(sr2.getProvider());
        SecureRandom2Test.MyProvider p = new SecureRandom2Test.MyProvider();
        SecureRandom2Test.MySecureRandom sr3 = new SecureRandom2Test.MySecureRandom(new SecureRandom2Test.MySecureRandomSpi(), p);
        TestCase.assertEquals(p, sr3.getProvider());
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            TestCase.assertNotNull(random.getProvider());
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail("Unexpected NoSuchAlgorithmException");
        }
    }

    /**
     * java.security.SecureRandom#SecureRandom()
     */
    public void test_Constructor() {
        // Test for method java.security.SecureRandom()
        try {
            new SecureRandom();
        } catch (Exception e) {
            TestCase.fail(("Constructor threw exception : " + e));
        }
    }

    /**
     * java.security.SecureRandom#SecureRandom(byte[])
     */
    public void test_Constructor$B() {
        // Test for method java.security.SecureRandom(byte [])
        try {
            new SecureRandom(SecureRandom2Test.SEED_BYTES);
        } catch (Exception e) {
            TestCase.fail(("Constructor threw exception : " + e));
        }
        try {
            new SecureRandom(null);
            TestCase.fail("NullPointerException was not thrown for NULL parameter");
        } catch (NullPointerException e) {
            // expected
        }
    }

    /**
     * java.security.SecureRandom#SecureRandom(java.security.SecureRandomSpi, java.security.Provider)
     */
    public void test_ConstructorLjava_security_SecureRandomSpi_java_security_Provider() {
        try {
            new SecureRandom2Test.MySecureRandom(null, null);
        } catch (Exception e) {
            TestCase.fail(("Constructor threw exception : " + e));
        }
        try {
            SecureRandom2Test.MyProvider p = new SecureRandom2Test.MyProvider();
            SecureRandom2Test.MySecureRandom sr = new SecureRandom2Test.MySecureRandom(new SecureRandom2Test.MySecureRandomSpi(), p);
            TestCase.assertEquals("unknown", sr.getAlgorithm());
            TestCase.assertEquals(p, sr.getProvider());
            sr = new SecureRandom2Test.MySecureRandom(new SecureRandom2Test.MySecureRandomSpi(), null);
            sr = new SecureRandom2Test.MySecureRandom(null, p);
        } catch (Exception e) {
            TestCase.fail(("Constructor threw exception : " + e));
        }
    }

    /**
     * java.security.SecureRandom#generateSeed(int)
     */
    public void test_generateSeedI() {
        // Test for method byte [] java.security.SecureRandom.generateSeed(int)
        byte[] seed = new SecureRandom().generateSeed(SecureRandom2Test.SEED_SIZE);
        TestCase.assertEquals("seed has incorrect size", SecureRandom2Test.SEED_SIZE, seed.length);
        try {
            new SecureRandom().generateSeed((-42));
            TestCase.fail("expected an exception");
        } catch (Exception e) {
            // ok
        }
    }

    /**
     * java.security.SecureRandom#getInstance(java.lang.String)
     */
    public void test_getInstanceLjava_lang_String() {
        // Test for method java.security.SecureRandom
        // java.security.SecureRandom.getInstance(java.lang.String)
        try {
            SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail("getInstance did not find a SHA1PRNG algorithm");
        }
        try {
            SecureRandom.getInstance("MD2");
            TestCase.fail("NoSuchAlgorithmException should be thrown for MD2 algorithm");
        } catch (NoSuchAlgorithmException e) {
            // expected
        }
    }

    /**
     * java.security.SecureRandom#getInstance(java.lang.String, java.lang.String)
     */
    public void test_getInstanceLjava_lang_StringLjava_lang_String() {
        // Test for method java.security.SecureRandom
        // java.security.SecureRandom.getInstance(java.lang.String,
        // java.lang.String)
        try {
            Provider[] providers = Security.getProviders("SecureRandom.SHA1PRNG");
            if (providers != null) {
                for (int i = 0; i < (providers.length); i++) {
                    SecureRandom.getInstance("SHA1PRNG", providers[i].getName());
                }// end for

            } else {
                TestCase.fail("No providers support SHA1PRNG");
            }
        } catch (NoSuchAlgorithmException e) {
            TestCase.fail("getInstance did not find a SHA1PRNG algorithm");
        } catch (NoSuchProviderException e) {
            TestCase.fail("getInstance did not find the provider for SHA1PRNG");
        }
    }

    /**
     * java.security.SecureRandom#getSeed(int)
     */
    public void test_getSeedI() {
        // Test for method byte [] java.security.SecureRandom.getSeed(int)
        byte[] seed = SecureRandom.getSeed(SecureRandom2Test.SEED_SIZE);
        TestCase.assertEquals("seed has incorrect size", SecureRandom2Test.SEED_SIZE, seed.length);
        try {
            new SecureRandom().getSeed((-42));
            TestCase.fail("expected an exception");
        } catch (Exception e) {
            // ok
        }
    }

    /**
     * java.security.SecureRandom#nextBytes(byte[])
     */
    public void test_nextBytes$B() {
        // Test for method void java.security.SecureRandom.nextBytes(byte [])
        byte[] bytes = new byte[313];
        try {
            new SecureRandom().nextBytes(bytes);
        } catch (Exception e) {
            TestCase.fail(("next bytes not ok : " + e));
        }
        try {
            new SecureRandom().nextBytes(null);
            TestCase.fail("expected exception");
        } catch (Exception e) {
            // ok
        }
    }

    /**
     * java.security.SecureRandom#setSeed(byte[])
     */
    public void test_setSeed$B() {
        // Test for method void java.security.SecureRandom.setSeed(byte [])
        try {
            new SecureRandom().setSeed(SecureRandom2Test.SEED_BYTES);
        } catch (Exception e) {
            TestCase.fail(("seed generation with bytes failed : " + e));
        }
        try {
            new SecureRandom().setSeed(null);
            TestCase.fail("expected exception");
        } catch (Exception e) {
            // ok
        }
    }

    /**
     * java.security.SecureRandom#setSeed(long)
     */
    public void test_setSeedJ() {
        // Test for method void java.security.SecureRandom.setSeed(long)
        try {
            new SecureRandom().setSeed(SecureRandom2Test.SEED_VALUE);
        } catch (Exception e) {
            TestCase.fail(("seed generation with long failed : " + e));
        }
        try {
            new SecureRandom().setSeed((-1));
        } catch (Exception e) {
            TestCase.fail(("unexpected exception: " + e));
        }
    }

    /**
     * java.security.SecureRandom#getAlgorithm()
     */
    public void test_getAlgorithm() {
        // Regression for HARMONY-750
        SecureRandomSpi spi = new SecureRandomSpi() {
            protected void engineSetSeed(byte[] arg) {
            }

            protected void engineNextBytes(byte[] arg) {
            }

            protected byte[] engineGenerateSeed(int arg) {
                return null;
            }
        };
        SecureRandom sr = new SecureRandom(spi, null) {};
        TestCase.assertEquals("unknown", sr.getAlgorithm());
    }

    // Regression Test for HARMONY-3552.
    public void test_nextJ() throws Exception {
        SecureRandom2Test.MySecureRandom mySecureRandom = new SecureRandom2Test.MySecureRandom(new SecureRandom2Test.MySecureRandomSpi(), null);
        int numBits = 29;
        int random = mySecureRandom.getNext(numBits);
        TestCase.assertEquals(numBits, Integer.bitCount(random));
        numBits = 0;
        random = mySecureRandom.getNext(numBits);
        TestCase.assertEquals(numBits, Integer.bitCount(random));
        numBits = 40;
        random = mySecureRandom.getNext(numBits);
        TestCase.assertEquals(32, Integer.bitCount(random));
        numBits = -1;
        random = mySecureRandom.getNext(numBits);
        TestCase.assertEquals(0, Integer.bitCount(random));
    }

    /**
     * Two {@link SecureRandom} objects, created with
     * {@link SecureRandom#getInstance(String)} and initialized before use
     * with the same seed, should return the same results.<p>
     *
     * In the future, it may sense to disallow seeding {@code SecureRandom},
     * as it tends to be error prone and open up security holes.
     * See {@link SecureRandom} for more details about insecure seeding.
     *
     * Note that this only works with the Harmony "Crypto" provider.
     */
    public void testSameSeedGeneratesSameResults() throws Exception {
        byte[] seed1 = new byte[]{ 'a', 'b', 'c' };
        SecureRandom sr1 = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr1.setSeed(seed1);
        byte[] seed2 = new byte[]{ 'a', 'b', 'c' };
        SecureRandom sr2 = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr2.setSeed(seed2);
        TestCase.assertTrue(((sr1.nextLong()) == (sr2.nextLong())));
    }

    /**
     * Assert that a {@link SecureRandom} object seeded from a constant
     * seed always returns the same value, even across VM restarts.
     *
     * Future versions of Android may change the implementation of
     * SHA1PRNG, so users of {@code SecureRandom} should not assume
     * the same seed will always produce the same value.  This test
     * is not a guarantee of future compatibility.
     *
     * In fact, this test only works with the Harmony "Crypto" provider.
     */
    public void testAlwaysSameValueWithSameSeed() throws Exception {
        byte[] seed1 = new byte[]{ 'a', 'b', 'c' };
        SecureRandom sr1 = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr1.setSeed(seed1);
        // This long value has no special meaning and may change in the future.
        TestCase.assertEquals(6180693691264871500L, sr1.nextLong());
    }

    /**
     * Validate that calling {@link SecureRandom#setSeed} <b>after</b> generating
     * a random number compliments, but doesn't replace, the existing seed.
     *
     * Compare this test to {@link #testAlwaysSameValueWithSameSeed()}.
     */
    public void testSetSeedComplimentsAfterFirstRandomNumber() throws Exception {
        byte[] seed1 = new byte[]{ 'a', 'b', 'c' };
        SecureRandom sr1 = SecureRandom.getInstance("SHA1PRNG");
        sr1.nextInt();
        sr1.setSeed(seed1);
        // This long value has no special meaning
        TestCase.assertTrue((6180693691264871500L != (sr1.nextLong())));
    }

    class MySecureRandom extends SecureRandom {
        private static final long serialVersionUID = 1L;

        public MySecureRandom(SecureRandomSpi secureRandomSpi, Provider provider) {
            super(secureRandomSpi, provider);
        }

        public int getNext(int numBits) {
            return super.next(numBits);
        }
    }

    class MySecureRandomSpi extends SecureRandomSpi {
        private static final long serialVersionUID = 1L;

        @Override
        protected byte[] engineGenerateSeed(int arg0) {
            return null;
        }

        @Override
        protected void engineNextBytes(byte[] bytes) {
            for (int i = 0; i < (bytes.length); i++) {
                bytes[i] = ((byte) (255));
            }
        }

        @Override
        protected void engineSetSeed(byte[] arg0) {
            return;
        }
    }

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

        public void putService(Provider.Service s) {
            super.putService(s);
        }

        public void removeService(Provider.Service s) {
            super.removeService(s);
        }
    }
}

