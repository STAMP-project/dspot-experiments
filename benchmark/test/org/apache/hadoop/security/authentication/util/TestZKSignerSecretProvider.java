/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See accompanying LICENSE file.
 */
package org.apache.hadoop.security.authentication.util;


import RolloverSignerSecretProvider.LOG;
import ZKSignerSecretProvider.ZOOKEEPER_CONNECTION_STRING;
import ZKSignerSecretProvider.ZOOKEEPER_PATH;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.Random;
import org.apache.curator.test.TestingServer;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestZKSignerSecretProvider {
    private TestingServer zkServer;

    // rollover every 50 msec
    private final int timeout = 100;

    private final long rolloverFrequency = (timeout) / 2;

    {
        LogManager.getLogger(LOG.getName()).setLevel(Level.DEBUG);
    }

    // Test just one ZKSignerSecretProvider to verify that it works in the
    // simplest case
    @Test
    public void testOne() throws Exception {
        // Use the same seed and a "plain" Random so we can predict the RNG
        long seed = System.currentTimeMillis();
        Random rand = new Random(seed);
        byte[] secret2 = generateNewSecret(rand);
        byte[] secret1 = generateNewSecret(rand);
        byte[] secret3 = generateNewSecret(rand);
        TestZKSignerSecretProvider.MockZKSignerSecretProvider secretProvider = Mockito.spy(new TestZKSignerSecretProvider.MockZKSignerSecretProvider(seed));
        Properties config = new Properties();
        config.setProperty(ZOOKEEPER_CONNECTION_STRING, zkServer.getConnectString());
        config.setProperty(ZOOKEEPER_PATH, "/secret");
        try {
            secretProvider.init(config, getDummyServletContext(), rolloverFrequency);
            byte[] currentSecret = getCurrentSecret();
            byte[][] allSecrets = getAllSecrets();
            Assert.assertArrayEquals(secret1, currentSecret);
            Assert.assertEquals(2, allSecrets.length);
            Assert.assertArrayEquals(secret1, allSecrets[0]);
            Assert.assertNull(allSecrets[1]);
            Mockito.verify(secretProvider, Mockito.timeout(timeout).atLeastOnce()).rollSecret();
            secretProvider.realRollSecret();
            currentSecret = getCurrentSecret();
            allSecrets = getAllSecrets();
            Assert.assertArrayEquals(secret2, currentSecret);
            Assert.assertEquals(2, allSecrets.length);
            Assert.assertArrayEquals(secret2, allSecrets[0]);
            Assert.assertArrayEquals(secret1, allSecrets[1]);
            Mockito.verify(secretProvider, Mockito.timeout(timeout).atLeast(2)).rollSecret();
            secretProvider.realRollSecret();
            currentSecret = getCurrentSecret();
            allSecrets = getAllSecrets();
            Assert.assertArrayEquals(secret3, currentSecret);
            Assert.assertEquals(2, allSecrets.length);
            Assert.assertArrayEquals(secret3, allSecrets[0]);
            Assert.assertArrayEquals(secret2, allSecrets[1]);
            Mockito.verify(secretProvider, Mockito.timeout(timeout).atLeast(3)).rollSecret();
            secretProvider.realRollSecret();
        } finally {
            destroy();
        }
    }

    /**
     * A hack to test ZKSignerSecretProvider.
     * We want to test that ZKSignerSecretProvider.rollSecret() is periodically
     * called at the expected frequency, but we want to exclude the
     * race-condition and not take a long time to run the test.
     */
    private class MockZKSignerSecretProvider extends ZKSignerSecretProvider {
        MockZKSignerSecretProvider(long seed) {
            super(seed);
        }

        @Override
        protected synchronized void rollSecret() {
            // this is a no-op: simply used for Mockito to verify that rollSecret()
            // is periodically called at the expected frequency
        }

        public void realRollSecret() {
            // the test code manually calls ZKSignerSecretProvider.rollSecret()
            // to update the state
            super.rollSecret();
        }
    }

    // HADOOP-14246 increased the length of the secret from 160 bits to 256 bits.
    // This test verifies that the upgrade goes smoothly.
    @Test
    public void testUpgradeChangeSecretLength() throws Exception {
        // Use the same seed and a "plain" Random so we can predict the RNG
        long seed = System.currentTimeMillis();
        Random rand = new Random(seed);
        byte[] secret2 = Long.toString(rand.nextLong()).getBytes(Charset.forName("UTF-8"));
        byte[] secret1 = Long.toString(rand.nextLong()).getBytes(Charset.forName("UTF-8"));
        byte[] secret3 = Long.toString(rand.nextLong()).getBytes(Charset.forName("UTF-8"));
        rand = new Random(seed);
        // Secrets 4 and 5 get thrown away by ZK when the new secret provider tries
        // to init
        byte[] secret4 = generateNewSecret(rand);
        byte[] secret5 = generateNewSecret(rand);
        byte[] secret6 = generateNewSecret(rand);
        byte[] secret7 = generateNewSecret(rand);
        // Initialize the znode data with the old secret length
        TestZKSignerSecretProvider.MockZKSignerSecretProvider oldSecretProvider = Mockito.spy(new TestZKSignerSecretProvider.OldMockZKSignerSecretProvider(seed));
        Properties config = new Properties();
        config.setProperty(ZOOKEEPER_CONNECTION_STRING, zkServer.getConnectString());
        config.setProperty(ZOOKEEPER_PATH, "/secret");
        try {
            oldSecretProvider.init(config, getDummyServletContext(), rolloverFrequency);
            byte[] currentSecret = getCurrentSecret();
            byte[][] allSecrets = getAllSecrets();
            Assert.assertArrayEquals(secret1, currentSecret);
            Assert.assertEquals(2, allSecrets.length);
            Assert.assertArrayEquals(secret1, allSecrets[0]);
            Assert.assertNull(allSecrets[1]);
            oldSecretProvider.realRollSecret();
            currentSecret = getCurrentSecret();
            allSecrets = getAllSecrets();
            Assert.assertArrayEquals(secret2, currentSecret);
            Assert.assertEquals(2, allSecrets.length);
            Assert.assertArrayEquals(secret2, allSecrets[0]);
            Assert.assertArrayEquals(secret1, allSecrets[1]);
        } finally {
            destroy();
        }
        // Now use a ZKSignerSecretProvider with the newer length
        TestZKSignerSecretProvider.MockZKSignerSecretProvider newSecretProvider = Mockito.spy(new TestZKSignerSecretProvider.MockZKSignerSecretProvider(seed));
        try {
            newSecretProvider.init(config, getDummyServletContext(), rolloverFrequency);
            byte[] currentSecret = getCurrentSecret();
            byte[][] allSecrets = getAllSecrets();
            Assert.assertArrayEquals(secret2, currentSecret);
            Assert.assertEquals(2, allSecrets.length);
            Assert.assertArrayEquals(secret2, allSecrets[0]);
            Assert.assertArrayEquals(secret1, allSecrets[1]);
            newSecretProvider.realRollSecret();
            currentSecret = getCurrentSecret();
            allSecrets = getAllSecrets();
            Assert.assertArrayEquals(secret3, currentSecret);
            Assert.assertEquals(2, allSecrets.length);
            Assert.assertArrayEquals(secret3, allSecrets[0]);
            Assert.assertArrayEquals(secret2, allSecrets[1]);
            newSecretProvider.realRollSecret();
            currentSecret = getCurrentSecret();
            allSecrets = getAllSecrets();
            Assert.assertArrayEquals(secret6, currentSecret);
            Assert.assertEquals(2, allSecrets.length);
            Assert.assertArrayEquals(secret6, allSecrets[0]);
            Assert.assertArrayEquals(secret3, allSecrets[1]);
            newSecretProvider.realRollSecret();
            currentSecret = getCurrentSecret();
            allSecrets = getAllSecrets();
            Assert.assertArrayEquals(secret7, currentSecret);
            Assert.assertEquals(2, allSecrets.length);
            Assert.assertArrayEquals(secret7, allSecrets[0]);
            Assert.assertArrayEquals(secret6, allSecrets[1]);
        } finally {
            destroy();
        }
    }

    /**
     * A version of {@link MockZKSignerSecretProvider} that uses the old way of
     * generating secrets (160 bit long).
     */
    private class OldMockZKSignerSecretProvider extends TestZKSignerSecretProvider.MockZKSignerSecretProvider {
        private Random rand;

        OldMockZKSignerSecretProvider(long seed) {
            super(seed);
            rand = new Random(seed);
        }

        @Override
        protected byte[] generateRandomSecret() {
            return Long.toString(rand.nextLong()).getBytes(Charset.forName("UTF-8"));
        }
    }

    @Test
    public void testMultiple1() throws Exception {
        testMultiple(1);
    }

    @Test
    public void testMultiple2() throws Exception {
        testMultiple(2);
    }
}

