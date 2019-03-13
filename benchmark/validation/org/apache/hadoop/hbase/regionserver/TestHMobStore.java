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


import CellComparatorImpl.COMPARATOR;
import CompactionLifeCycleTracker.DUMMY;
import HConstants.CIPHER_AES;
import HConstants.CRYPTO_KEYPROVIDER_CONF_KEY;
import HConstants.CRYPTO_KEY_ALGORITHM_CONF_KEY;
import HConstants.CRYPTO_MASTERKEY_NAME_CONF_KEY;
import MobConstants.MOB_SCAN_RAW;
import NoLimitThroughputController.INSTANCE;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NavigableSet;
import java.util.Optional;
import javax.crypto.spec.SecretKeySpec;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.crypto.KeyProviderForTesting;
import org.apache.hadoop.hbase.io.crypto.aes.AES;
import org.apache.hadoop.hbase.mob.MobUtils;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionContext;
import org.apache.hadoop.hbase.security.EncryptionUtil;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(MediumTests.class)
public class TestHMobStore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHMobStore.class);

    public static final Logger LOG = LoggerFactory.getLogger(TestHMobStore.class);

    @Rule
    public TestName name = new TestName();

    private HMobStore store;

    private HRegion region;

    private FileSystem fs;

    private byte[] table = Bytes.toBytes("table");

    private byte[] family = Bytes.toBytes("family");

    private byte[] row = Bytes.toBytes("row");

    private byte[] row2 = Bytes.toBytes("row2");

    private byte[] qf1 = Bytes.toBytes("qf1");

    private byte[] qf2 = Bytes.toBytes("qf2");

    private byte[] qf3 = Bytes.toBytes("qf3");

    private byte[] qf4 = Bytes.toBytes("qf4");

    private byte[] qf5 = Bytes.toBytes("qf5");

    private byte[] qf6 = Bytes.toBytes("qf6");

    private byte[] value = Bytes.toBytes("value");

    private byte[] value2 = Bytes.toBytes("value2");

    private Path mobFilePath;

    private Date currentDate = new Date();

    private Cell seekKey1;

    private Cell seekKey2;

    private Cell seekKey3;

    private NavigableSet<byte[]> qualifiers = new java.util.concurrent.ConcurrentSkipListSet(Bytes.BYTES_COMPARATOR);

    private List<Cell> expected = new ArrayList<>();

    private long id = System.currentTimeMillis();

    private Get get = new Get(row);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private final String DIR = getDataTestDir("TestHMobStore").toString();

    /**
     * Getting data from memstore
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testGetFromMemStore() throws IOException {
        final Configuration conf = HBaseConfiguration.create();
        init(name.getMethodName(), conf, false);
        // Put data in memstore
        this.store.add(new KeyValue(row, family, qf1, 1, value), null);
        this.store.add(new KeyValue(row, family, qf2, 1, value), null);
        this.store.add(new KeyValue(row, family, qf3, 1, value), null);
        this.store.add(new KeyValue(row, family, qf4, 1, value), null);
        this.store.add(new KeyValue(row, family, qf5, 1, value), null);
        this.store.add(new KeyValue(row, family, qf6, 1, value), null);
        Scan scan = new Scan(get);
        InternalScanner scanner = ((InternalScanner) (store.getScanner(scan, scan.getFamilyMap().get(store.getColumnFamilyDescriptor().getName()), 0)));
        List<Cell> results = new ArrayList<>();
        scanner.next(results);
        Collections.sort(results, COMPARATOR);
        scanner.close();
        // Compare
        Assert.assertEquals(expected.size(), results.size());
        for (int i = 0; i < (results.size()); i++) {
            // Verify the values
            Assert.assertEquals(expected.get(i), results.get(i));
        }
    }

    /**
     * Getting MOB data from files
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testGetFromFiles() throws IOException {
        final Configuration conf = TestHMobStore.TEST_UTIL.getConfiguration();
        init(name.getMethodName(), conf, false);
        // Put data in memstore
        this.store.add(new KeyValue(row, family, qf1, 1, value), null);
        this.store.add(new KeyValue(row, family, qf2, 1, value), null);
        // flush
        flush(1);
        // Add more data
        this.store.add(new KeyValue(row, family, qf3, 1, value), null);
        this.store.add(new KeyValue(row, family, qf4, 1, value), null);
        // flush
        flush(2);
        // Add more data
        this.store.add(new KeyValue(row, family, qf5, 1, value), null);
        this.store.add(new KeyValue(row, family, qf6, 1, value), null);
        // flush
        flush(3);
        Scan scan = new Scan(get);
        InternalScanner scanner = ((InternalScanner) (store.getScanner(scan, scan.getFamilyMap().get(store.getColumnFamilyDescriptor().getName()), 0)));
        List<Cell> results = new ArrayList<>();
        scanner.next(results);
        Collections.sort(results, COMPARATOR);
        scanner.close();
        // Compare
        Assert.assertEquals(expected.size(), results.size());
        for (int i = 0; i < (results.size()); i++) {
            Assert.assertEquals(expected.get(i), results.get(i));
        }
    }

    /**
     * Getting the reference data from files
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testGetReferencesFromFiles() throws IOException {
        final Configuration conf = HBaseConfiguration.create();
        init(name.getMethodName(), conf, false);
        // Put data in memstore
        this.store.add(new KeyValue(row, family, qf1, 1, value), null);
        this.store.add(new KeyValue(row, family, qf2, 1, value), null);
        // flush
        flush(1);
        // Add more data
        this.store.add(new KeyValue(row, family, qf3, 1, value), null);
        this.store.add(new KeyValue(row, family, qf4, 1, value), null);
        // flush
        flush(2);
        // Add more data
        this.store.add(new KeyValue(row, family, qf5, 1, value), null);
        this.store.add(new KeyValue(row, family, qf6, 1, value), null);
        // flush
        flush(3);
        Scan scan = new Scan(get);
        scan.setAttribute(MOB_SCAN_RAW, Bytes.toBytes(Boolean.TRUE));
        InternalScanner scanner = ((InternalScanner) (store.getScanner(scan, scan.getFamilyMap().get(store.getColumnFamilyDescriptor().getName()), 0)));
        List<Cell> results = new ArrayList<>();
        scanner.next(results);
        Collections.sort(results, COMPARATOR);
        scanner.close();
        // Compare
        Assert.assertEquals(expected.size(), results.size());
        for (int i = 0; i < (results.size()); i++) {
            Cell cell = results.get(i);
            Assert.assertTrue(MobUtils.isMobReferenceCell(cell));
        }
    }

    /**
     * Getting data from memstore and files
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testGetFromMemStoreAndFiles() throws IOException {
        final Configuration conf = HBaseConfiguration.create();
        init(name.getMethodName(), conf, false);
        // Put data in memstore
        this.store.add(new KeyValue(row, family, qf1, 1, value), null);
        this.store.add(new KeyValue(row, family, qf2, 1, value), null);
        // flush
        flush(1);
        // Add more data
        this.store.add(new KeyValue(row, family, qf3, 1, value), null);
        this.store.add(new KeyValue(row, family, qf4, 1, value), null);
        // flush
        flush(2);
        // Add more data
        this.store.add(new KeyValue(row, family, qf5, 1, value), null);
        this.store.add(new KeyValue(row, family, qf6, 1, value), null);
        Scan scan = new Scan(get);
        InternalScanner scanner = ((InternalScanner) (store.getScanner(scan, scan.getFamilyMap().get(store.getColumnFamilyDescriptor().getName()), 0)));
        List<Cell> results = new ArrayList<>();
        scanner.next(results);
        Collections.sort(results, COMPARATOR);
        scanner.close();
        // Compare
        Assert.assertEquals(expected.size(), results.size());
        for (int i = 0; i < (results.size()); i++) {
            Assert.assertEquals(expected.get(i), results.get(i));
        }
    }

    /**
     * Getting data from memstore and files
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testMobCellSizeThreshold() throws IOException {
        final Configuration conf = HBaseConfiguration.create();
        ColumnFamilyDescriptor cfd = ColumnFamilyDescriptorBuilder.newBuilder(family).setMobEnabled(true).setMobThreshold(100).setMaxVersions(4).build();
        init(name.getMethodName(), conf, cfd, false);
        // Put data in memstore
        this.store.add(new KeyValue(row, family, qf1, 1, value), null);
        this.store.add(new KeyValue(row, family, qf2, 1, value), null);
        // flush
        flush(1);
        // Add more data
        this.store.add(new KeyValue(row, family, qf3, 1, value), null);
        this.store.add(new KeyValue(row, family, qf4, 1, value), null);
        // flush
        flush(2);
        // Add more data
        this.store.add(new KeyValue(row, family, qf5, 1, value), null);
        this.store.add(new KeyValue(row, family, qf6, 1, value), null);
        // flush
        flush(3);
        Scan scan = new Scan(get);
        scan.setAttribute(MOB_SCAN_RAW, Bytes.toBytes(Boolean.TRUE));
        InternalScanner scanner = ((InternalScanner) (store.getScanner(scan, scan.getFamilyMap().get(store.getColumnFamilyDescriptor().getName()), 0)));
        List<Cell> results = new ArrayList<>();
        scanner.next(results);
        Collections.sort(results, COMPARATOR);
        scanner.close();
        // Compare
        Assert.assertEquals(expected.size(), results.size());
        for (int i = 0; i < (results.size()); i++) {
            Cell cell = results.get(i);
            // this is not mob reference cell.
            Assert.assertFalse(MobUtils.isMobReferenceCell(cell));
            Assert.assertEquals(expected.get(i), results.get(i));
            Assert.assertEquals(100, store.getColumnFamilyDescriptor().getMobThreshold());
        }
    }

    @Test
    public void testCommitFile() throws Exception {
        final Configuration conf = HBaseConfiguration.create();
        init(name.getMethodName(), conf, true);
        String targetPathName = MobUtils.formatDate(new Date());
        Path targetPath = new Path(store.getPath(), ((targetPathName + (Path.SEPARATOR)) + (mobFilePath.getName())));
        fs.delete(targetPath, true);
        Assert.assertFalse(fs.exists(targetPath));
        // commit file
        store.commitFile(mobFilePath, targetPath);
        Assert.assertTrue(fs.exists(targetPath));
    }

    @Test
    public void testResolve() throws Exception {
        final Configuration conf = HBaseConfiguration.create();
        init(name.getMethodName(), conf, true);
        String targetPathName = MobUtils.formatDate(currentDate);
        Path targetPath = new Path(store.getPath(), targetPathName);
        store.commitFile(mobFilePath, targetPath);
        // resolve
        Cell resultCell1 = store.resolve(seekKey1, false);
        Cell resultCell2 = store.resolve(seekKey2, false);
        Cell resultCell3 = store.resolve(seekKey3, false);
        // compare
        Assert.assertEquals(Bytes.toString(value), Bytes.toString(CellUtil.cloneValue(resultCell1)));
        Assert.assertEquals(Bytes.toString(value), Bytes.toString(CellUtil.cloneValue(resultCell2)));
        Assert.assertEquals(Bytes.toString(value2), Bytes.toString(CellUtil.cloneValue(resultCell3)));
    }

    @Test
    public void testMOBStoreEncryption() throws Exception {
        final Configuration conf = TestHMobStore.TEST_UTIL.getConfiguration();
        conf.set(CRYPTO_KEYPROVIDER_CONF_KEY, KeyProviderForTesting.class.getName());
        conf.set(CRYPTO_MASTERKEY_NAME_CONF_KEY, "hbase");
        SecureRandom rng = new SecureRandom();
        byte[] keyBytes = new byte[AES.KEY_LENGTH];
        rng.nextBytes(keyBytes);
        String algorithm = conf.get(CRYPTO_KEY_ALGORITHM_CONF_KEY, CIPHER_AES);
        Key cfKey = new SecretKeySpec(keyBytes, algorithm);
        ColumnFamilyDescriptor cfd = ColumnFamilyDescriptorBuilder.newBuilder(family).setMobEnabled(true).setMobThreshold(100).setMaxVersions(4).setEncryptionType(algorithm).setEncryptionKey(EncryptionUtil.wrapKey(conf, conf.get(CRYPTO_MASTERKEY_NAME_CONF_KEY, User.getCurrent().getShortName()), cfKey)).build();
        init(name.getMethodName(), conf, cfd, false);
        this.store.add(new KeyValue(row, family, qf1, 1, value), null);
        this.store.add(new KeyValue(row, family, qf2, 1, value), null);
        this.store.add(new KeyValue(row, family, qf3, 1, value), null);
        flush(1);
        this.store.add(new KeyValue(row, family, qf4, 1, value), null);
        this.store.add(new KeyValue(row, family, qf5, 1, value), null);
        this.store.add(new KeyValue(row, family, qf6, 1, value), null);
        flush(2);
        Collection<HStoreFile> storefiles = this.store.getStorefiles();
        checkMobHFileEncrytption(storefiles);
        // Scan the values
        Scan scan = new Scan(get);
        InternalScanner scanner = ((InternalScanner) (store.getScanner(scan, scan.getFamilyMap().get(store.getColumnFamilyDescriptor().getName()), 0)));
        List<Cell> results = new ArrayList<>();
        scanner.next(results);
        Collections.sort(results, COMPARATOR);
        scanner.close();
        Assert.assertEquals(expected.size(), results.size());
        for (int i = 0; i < (results.size()); i++) {
            Assert.assertEquals(expected.get(i), results.get(i));
        }
        // Trigger major compaction
        this.store.triggerMajorCompaction();
        Optional<CompactionContext> requestCompaction = this.store.requestCompaction(Store.PRIORITY_USER, DUMMY, null);
        this.store.compact(requestCompaction.get(), INSTANCE, null);
        Assert.assertEquals(1, this.store.getStorefiles().size());
        // Check encryption after compaction
        checkMobHFileEncrytption(this.store.getStorefiles());
    }
}

