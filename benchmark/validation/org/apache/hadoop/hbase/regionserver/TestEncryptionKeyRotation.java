/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.regionserver;


import HConstants.CIPHER_AES;
import HConstants.CRYPTO_KEY_ALGORITHM_CONF_KEY;
import HConstants.CRYPTO_MASTERKEY_ALTERNATE_NAME_CONF_KEY;
import HConstants.CRYPTO_MASTERKEY_NAME_CONF_KEY;
import java.security.Key;
import java.security.SecureRandom;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.io.crypto.aes.AES;
import org.apache.hadoop.hbase.security.EncryptionUtil;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestEncryptionKeyRotation {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestEncryptionKeyRotation.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestEncryptionKeyRotation.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Configuration conf = TestEncryptionKeyRotation.TEST_UTIL.getConfiguration();

    private static final Key initialCFKey;

    private static final Key secondCFKey;

    @Rule
    public TestName name = new TestName();

    static {
        // Create the test encryption keys
        SecureRandom rng = new SecureRandom();
        byte[] keyBytes = new byte[AES.KEY_LENGTH];
        rng.nextBytes(keyBytes);
        String algorithm = TestEncryptionKeyRotation.conf.get(CRYPTO_KEY_ALGORITHM_CONF_KEY, CIPHER_AES);
        initialCFKey = new SecretKeySpec(keyBytes, algorithm);
        rng.nextBytes(keyBytes);
        secondCFKey = new SecretKeySpec(keyBytes, algorithm);
    }

    @Test
    public void testCFKeyRotation() throws Exception {
        // Create the table schema
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf("default", name.getMethodName()));
        HColumnDescriptor hcd = new HColumnDescriptor("cf");
        String algorithm = TestEncryptionKeyRotation.conf.get(CRYPTO_KEY_ALGORITHM_CONF_KEY, CIPHER_AES);
        hcd.setEncryptionType(algorithm);
        hcd.setEncryptionKey(EncryptionUtil.wrapKey(TestEncryptionKeyRotation.conf, "hbase", TestEncryptionKeyRotation.initialCFKey));
        htd.addFamily(hcd);
        // Create the table and some on disk files
        createTableAndFlush(htd);
        // Verify we have store file(s) with the initial key
        final List<Path> initialPaths = TestEncryptionKeyRotation.findStorefilePaths(htd.getTableName());
        Assert.assertTrue(((initialPaths.size()) > 0));
        for (Path path : initialPaths) {
            Assert.assertTrue((("Store file " + path) + " has incorrect key"), Bytes.equals(TestEncryptionKeyRotation.initialCFKey.getEncoded(), TestEncryptionKeyRotation.extractHFileKey(path)));
        }
        // Update the schema with a new encryption key
        hcd = htd.getFamily(Bytes.toBytes("cf"));
        hcd.setEncryptionKey(EncryptionUtil.wrapKey(TestEncryptionKeyRotation.conf, TestEncryptionKeyRotation.conf.get(CRYPTO_MASTERKEY_NAME_CONF_KEY, User.getCurrent().getShortName()), TestEncryptionKeyRotation.secondCFKey));
        TestEncryptionKeyRotation.TEST_UTIL.getAdmin().modifyColumnFamily(htd.getTableName(), hcd);
        Thread.sleep(5000);// Need a predicate for online schema change

        // And major compact
        TestEncryptionKeyRotation.TEST_UTIL.getAdmin().majorCompact(htd.getTableName());
        final List<Path> updatePaths = TestEncryptionKeyRotation.findCompactedStorefilePaths(htd.getTableName());
        waitFor(30000, 1000, true, new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                // When compaction has finished, all of the original files will be
                // gone
                boolean found = false;
                for (Path path : updatePaths) {
                    found = TestEncryptionKeyRotation.TEST_UTIL.getTestFileSystem().exists(path);
                    if (found) {
                        TestEncryptionKeyRotation.LOG.info(("Found " + path));
                        break;
                    }
                }
                return !found;
            }
        });
        // Verify we have store file(s) with only the new key
        Thread.sleep(1000);
        TestEncryptionKeyRotation.waitForCompaction(htd.getTableName());
        List<Path> pathsAfterCompaction = TestEncryptionKeyRotation.findStorefilePaths(htd.getTableName());
        Assert.assertTrue(((pathsAfterCompaction.size()) > 0));
        for (Path path : pathsAfterCompaction) {
            Assert.assertTrue((("Store file " + path) + " has incorrect key"), Bytes.equals(TestEncryptionKeyRotation.secondCFKey.getEncoded(), TestEncryptionKeyRotation.extractHFileKey(path)));
        }
        List<Path> compactedPaths = TestEncryptionKeyRotation.findCompactedStorefilePaths(htd.getTableName());
        Assert.assertTrue(((compactedPaths.size()) > 0));
        for (Path path : compactedPaths) {
            Assert.assertTrue((("Store file " + path) + " retains initial key"), Bytes.equals(TestEncryptionKeyRotation.initialCFKey.getEncoded(), TestEncryptionKeyRotation.extractHFileKey(path)));
        }
    }

    @Test
    public void testMasterKeyRotation() throws Exception {
        // Create the table schema
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf("default", name.getMethodName()));
        HColumnDescriptor hcd = new HColumnDescriptor("cf");
        String algorithm = TestEncryptionKeyRotation.conf.get(CRYPTO_KEY_ALGORITHM_CONF_KEY, CIPHER_AES);
        hcd.setEncryptionType(algorithm);
        hcd.setEncryptionKey(EncryptionUtil.wrapKey(TestEncryptionKeyRotation.conf, "hbase", TestEncryptionKeyRotation.initialCFKey));
        htd.addFamily(hcd);
        // Create the table and some on disk files
        createTableAndFlush(htd);
        // Verify we have store file(s) with the initial key
        List<Path> storeFilePaths = TestEncryptionKeyRotation.findStorefilePaths(htd.getTableName());
        Assert.assertTrue(((storeFilePaths.size()) > 0));
        for (Path path : storeFilePaths) {
            Assert.assertTrue((("Store file " + path) + " has incorrect key"), Bytes.equals(TestEncryptionKeyRotation.initialCFKey.getEncoded(), TestEncryptionKeyRotation.extractHFileKey(path)));
        }
        // Now shut down the HBase cluster
        TestEncryptionKeyRotation.TEST_UTIL.shutdownMiniHBaseCluster();
        // "Rotate" the master key
        TestEncryptionKeyRotation.conf.set(CRYPTO_MASTERKEY_NAME_CONF_KEY, "other");
        TestEncryptionKeyRotation.conf.set(CRYPTO_MASTERKEY_ALTERNATE_NAME_CONF_KEY, "hbase");
        // Start the cluster back up
        TestEncryptionKeyRotation.TEST_UTIL.startMiniHBaseCluster();
        // Verify the table can still be loaded
        TestEncryptionKeyRotation.TEST_UTIL.waitTableAvailable(htd.getTableName(), 5000);
        // Double check that the store file keys can be unwrapped
        storeFilePaths = TestEncryptionKeyRotation.findStorefilePaths(htd.getTableName());
        Assert.assertTrue(((storeFilePaths.size()) > 0));
        for (Path path : storeFilePaths) {
            Assert.assertTrue((("Store file " + path) + " has incorrect key"), Bytes.equals(TestEncryptionKeyRotation.initialCFKey.getEncoded(), TestEncryptionKeyRotation.extractHFileKey(path)));
        }
    }
}

