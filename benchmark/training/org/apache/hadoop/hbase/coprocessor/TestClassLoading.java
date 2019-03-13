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
package org.apache.hadoop.hbase.coprocessor;


import Coprocessor.PRIORITY_USER;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Coprocessor;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.Region;
import org.apache.hadoop.hbase.regionserver.TestServerCustomProtocol;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.CoprocessorClassLoader;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test coprocessors class loading.
 */
@Category({ CoprocessorTests.class, MediumTests.class })
public class TestClassLoading {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestClassLoading.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestClassLoading.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    public static class TestMasterCoprocessor implements MasterCoprocessor , MasterObserver {
        @Override
        public Optional<MasterObserver> getMasterObserver() {
            return Optional.of(this);
        }
    }

    private static MiniDFSCluster cluster;

    static final TableName tableName = TableName.valueOf("TestClassLoading");

    static final String cpName1 = "TestCP1";

    static final String cpName2 = "TestCP2";

    static final String cpName3 = "TestCP3";

    static final String cpName4 = "TestCP4";

    static final String cpName5 = "TestCP5";

    static final String cpName6 = "TestCP6";

    private static Class<?> regionCoprocessor1 = ColumnAggregationEndpoint.class;

    // TOOD: Fix the import of this handler.  It is coming in from a package that is far away.
    private static Class<?> regionCoprocessor2 = TestServerCustomProtocol.PingHandler.class;

    private static Class<?> regionServerCoprocessor = SampleRegionWALCoprocessor.class;

    private static Class<?> masterCoprocessor = TestClassLoading.TestMasterCoprocessor.class;

    private static final String[] regionServerSystemCoprocessors = new String[]{ TestClassLoading.regionServerCoprocessor.getSimpleName() };

    private static final String[] masterRegionServerSystemCoprocessors = new String[]{ TestClassLoading.regionCoprocessor1.getSimpleName(), MultiRowMutationEndpoint.class.getSimpleName(), TestClassLoading.regionServerCoprocessor.getSimpleName() };

    // HBASE-3516: Test CP Class loading from HDFS
    @Test
    public void testClassLoadingFromHDFS() throws Exception {
        FileSystem fs = TestClassLoading.cluster.getFileSystem();
        File jarFile1 = TestClassLoading.buildCoprocessorJar(TestClassLoading.cpName1);
        File jarFile2 = TestClassLoading.buildCoprocessorJar(TestClassLoading.cpName2);
        // copy the jars into dfs
        fs.copyFromLocalFile(new Path(jarFile1.getPath()), new Path(((fs.getUri().toString()) + (Path.SEPARATOR))));
        String jarFileOnHDFS1 = ((fs.getUri().toString()) + (Path.SEPARATOR)) + (jarFile1.getName());
        Path pathOnHDFS1 = new Path(jarFileOnHDFS1);
        Assert.assertTrue("Copy jar file to HDFS failed.", fs.exists(pathOnHDFS1));
        TestClassLoading.LOG.info(("Copied jar file to HDFS: " + jarFileOnHDFS1));
        fs.copyFromLocalFile(new Path(jarFile2.getPath()), new Path(((fs.getUri().toString()) + (Path.SEPARATOR))));
        String jarFileOnHDFS2 = ((fs.getUri().toString()) + (Path.SEPARATOR)) + (jarFile2.getName());
        Path pathOnHDFS2 = new Path(jarFileOnHDFS2);
        Assert.assertTrue("Copy jar file to HDFS failed.", fs.exists(pathOnHDFS2));
        TestClassLoading.LOG.info(("Copied jar file to HDFS: " + jarFileOnHDFS2));
        // create a table that references the coprocessors
        HTableDescriptor htd = new HTableDescriptor(TestClassLoading.tableName);
        htd.addFamily(new HColumnDescriptor("test"));
        // without configuration values
        htd.setValue("COPROCESSOR$1", (((((jarFileOnHDFS1.toString()) + "|") + (TestClassLoading.cpName1)) + "|") + (Coprocessor.PRIORITY_USER)));
        // with configuration values
        htd.setValue("COPROCESSOR$2", ((((((jarFileOnHDFS2.toString()) + "|") + (TestClassLoading.cpName2)) + "|") + (Coprocessor.PRIORITY_USER)) + "|k1=v1,k2=v2,k3=v3"));
        Admin admin = TestClassLoading.TEST_UTIL.getAdmin();
        if (admin.tableExists(TestClassLoading.tableName)) {
            if (admin.isTableEnabled(TestClassLoading.tableName)) {
                admin.disableTable(TestClassLoading.tableName);
            }
            admin.deleteTable(TestClassLoading.tableName);
        }
        CoprocessorClassLoader.clearCache();
        byte[] startKey = new byte[]{ 10, 63 };
        byte[] endKey = new byte[]{ 12, 43 };
        admin.createTable(htd, startKey, endKey, 4);
        waitForTable(htd.getTableName());
        // verify that the coprocessors were loaded
        boolean foundTableRegion = false;
        boolean found1 = true;
        boolean found2 = true;
        boolean found2_k1 = true;
        boolean found2_k2 = true;
        boolean found2_k3 = true;
        Map<Region, Set<ClassLoader>> regionsActiveClassLoaders = new HashMap<>();
        MiniHBaseCluster hbase = TestClassLoading.TEST_UTIL.getHBaseCluster();
        for (HRegion region : hbase.getRegionServer(0).getOnlineRegionsLocalContext()) {
            if (region.getRegionInfo().getRegionNameAsString().startsWith(TestClassLoading.tableName.getNameAsString())) {
                foundTableRegion = true;
                CoprocessorEnvironment env;
                env = region.getCoprocessorHost().findCoprocessorEnvironment(TestClassLoading.cpName1);
                found1 = found1 && (env != null);
                env = region.getCoprocessorHost().findCoprocessorEnvironment(TestClassLoading.cpName2);
                found2 = found2 && (env != null);
                if (env != null) {
                    Configuration conf = env.getConfiguration();
                    found2_k1 = found2_k1 && ((conf.get("k1")) != null);
                    found2_k2 = found2_k2 && ((conf.get("k2")) != null);
                    found2_k3 = found2_k3 && ((conf.get("k3")) != null);
                } else {
                    found2_k1 = false;
                    found2_k2 = false;
                    found2_k3 = false;
                }
                regionsActiveClassLoaders.put(region, getExternalClassLoaders());
            }
        }
        Assert.assertTrue(("No region was found for table " + (TestClassLoading.tableName)), foundTableRegion);
        Assert.assertTrue((("Class " + (TestClassLoading.cpName1)) + " was missing on a region"), found1);
        Assert.assertTrue((("Class " + (TestClassLoading.cpName2)) + " was missing on a region"), found2);
        Assert.assertTrue("Configuration key 'k1' was missing on a region", found2_k1);
        Assert.assertTrue("Configuration key 'k2' was missing on a region", found2_k2);
        Assert.assertTrue("Configuration key 'k3' was missing on a region", found2_k3);
        // check if CP classloaders are cached
        Assert.assertNotNull((jarFileOnHDFS1 + " was not cached"), CoprocessorClassLoader.getIfCached(pathOnHDFS1));
        Assert.assertNotNull((jarFileOnHDFS2 + " was not cached"), CoprocessorClassLoader.getIfCached(pathOnHDFS2));
        // two external jar used, should be one classloader per jar
        Assert.assertEquals(("The number of cached classloaders should be equal to the number" + " of external jar files"), 2, CoprocessorClassLoader.getAllCached().size());
        // check if region active classloaders are shared across all RS regions
        Set<ClassLoader> externalClassLoaders = new java.util.HashSet(CoprocessorClassLoader.getAllCached());
        for (Map.Entry<Region, Set<ClassLoader>> regionCP : regionsActiveClassLoaders.entrySet()) {
            Assert.assertTrue((((((("Some CP classloaders for region " + (regionCP.getKey())) + " are not cached.") + " ClassLoader Cache:") + externalClassLoaders) + " Region ClassLoaders:") + (regionCP.getValue())), externalClassLoaders.containsAll(regionCP.getValue()));
        }
    }

    // HBASE-3516: Test CP Class loading from local file system
    @Test
    public void testClassLoadingFromLocalFS() throws Exception {
        File jarFile = TestClassLoading.buildCoprocessorJar(TestClassLoading.cpName3);
        // create a table that references the jar
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(TestClassLoading.cpName3));
        htd.addFamily(new HColumnDescriptor("test"));
        htd.setValue("COPROCESSOR$1", (((((getLocalPath(jarFile)) + "|") + (TestClassLoading.cpName3)) + "|") + (Coprocessor.PRIORITY_USER)));
        Admin admin = TestClassLoading.TEST_UTIL.getAdmin();
        admin.createTable(htd);
        waitForTable(htd.getTableName());
        // verify that the coprocessor was loaded
        boolean found = false;
        MiniHBaseCluster hbase = TestClassLoading.TEST_UTIL.getHBaseCluster();
        for (HRegion region : hbase.getRegionServer(0).getOnlineRegionsLocalContext()) {
            if (region.getRegionInfo().getRegionNameAsString().startsWith(TestClassLoading.cpName3)) {
                found = (region.getCoprocessorHost().findCoprocessor(TestClassLoading.cpName3)) != null;
            }
        }
        Assert.assertTrue((("Class " + (TestClassLoading.cpName3)) + " was missing on a region"), found);
    }

    // HBASE-6308: Test CP classloader is the CoprocessorClassLoader
    @Test
    public void testPrivateClassLoader() throws Exception {
        File jarFile = TestClassLoading.buildCoprocessorJar(TestClassLoading.cpName4);
        // create a table that references the jar
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(TestClassLoading.cpName4));
        htd.addFamily(new HColumnDescriptor("test"));
        htd.setValue("COPROCESSOR$1", (((((getLocalPath(jarFile)) + "|") + (TestClassLoading.cpName4)) + "|") + (Coprocessor.PRIORITY_USER)));
        Admin admin = TestClassLoading.TEST_UTIL.getAdmin();
        admin.createTable(htd);
        waitForTable(htd.getTableName());
        // verify that the coprocessor was loaded correctly
        boolean found = false;
        MiniHBaseCluster hbase = TestClassLoading.TEST_UTIL.getHBaseCluster();
        for (HRegion region : hbase.getRegionServer(0).getOnlineRegionsLocalContext()) {
            if (region.getRegionInfo().getRegionNameAsString().startsWith(TestClassLoading.cpName4)) {
                Coprocessor cp = region.getCoprocessorHost().findCoprocessor(TestClassLoading.cpName4);
                if (cp != null) {
                    found = true;
                    Assert.assertEquals((("Class " + (TestClassLoading.cpName4)) + " was not loaded by CoprocessorClassLoader"), cp.getClass().getClassLoader().getClass(), CoprocessorClassLoader.class);
                }
            }
        }
        Assert.assertTrue((("Class " + (TestClassLoading.cpName4)) + " was missing on a region"), found);
    }

    // HBase-3810: Registering a Coprocessor at HTableDescriptor should be
    // less strict
    @Test
    public void testHBase3810() throws Exception {
        // allowed value pattern: [path] | class name | [priority] | [key values]
        File jarFile1 = TestClassLoading.buildCoprocessorJar(TestClassLoading.cpName1);
        File jarFile2 = TestClassLoading.buildCoprocessorJar(TestClassLoading.cpName2);
        File jarFile5 = TestClassLoading.buildCoprocessorJar(TestClassLoading.cpName5);
        File jarFile6 = TestClassLoading.buildCoprocessorJar(TestClassLoading.cpName6);
        String cpKey1 = "COPROCESSOR$1";
        String cpKey2 = " Coprocessor$2 ";
        String cpKey3 = " coprocessor$03 ";
        String cpValue1 = ((((getLocalPath(jarFile1)) + "|") + (TestClassLoading.cpName1)) + "|") + (Coprocessor.PRIORITY_USER);
        String cpValue2 = (((getLocalPath(jarFile2)) + " | ") + (TestClassLoading.cpName2)) + " | ";
        // load from default class loader
        String cpValue3 = " | org.apache.hadoop.hbase.coprocessor.SimpleRegionObserver | | k=v ";
        // create a table that references the jar
        HTableDescriptor htd = new HTableDescriptor(TestClassLoading.tableName);
        htd.addFamily(new HColumnDescriptor("test"));
        // add 3 coprocessors by setting htd attributes directly.
        htd.setValue(cpKey1, cpValue1);
        htd.setValue(cpKey2, cpValue2);
        htd.setValue(cpKey3, cpValue3);
        // add 2 coprocessor by using new htd.setCoprocessor() api
        htd.addCoprocessor(TestClassLoading.cpName5, new Path(getLocalPath(jarFile5)), PRIORITY_USER, null);
        Map<String, String> kvs = new HashMap<>();
        kvs.put("k1", "v1");
        kvs.put("k2", "v2");
        kvs.put("k3", "v3");
        htd.addCoprocessor(TestClassLoading.cpName6, new Path(getLocalPath(jarFile6)), PRIORITY_USER, kvs);
        Admin admin = TestClassLoading.TEST_UTIL.getAdmin();
        if (admin.tableExists(TestClassLoading.tableName)) {
            if (admin.isTableEnabled(TestClassLoading.tableName)) {
                admin.disableTable(TestClassLoading.tableName);
            }
            admin.deleteTable(TestClassLoading.tableName);
        }
        admin.createTable(htd);
        waitForTable(htd.getTableName());
        // verify that the coprocessor was loaded
        boolean found_2 = false;
        boolean found_1 = false;
        boolean found_3 = false;
        boolean found_5 = false;
        boolean found_6 = false;
        boolean found6_k1 = false;
        boolean found6_k2 = false;
        boolean found6_k3 = false;
        boolean found6_k4 = false;
        MiniHBaseCluster hbase = TestClassLoading.TEST_UTIL.getHBaseCluster();
        for (HRegion region : hbase.getRegionServer(0).getOnlineRegionsLocalContext()) {
            if (region.getRegionInfo().getRegionNameAsString().startsWith(TestClassLoading.tableName.getNameAsString())) {
                found_1 = found_1 || ((region.getCoprocessorHost().findCoprocessor(TestClassLoading.cpName1)) != null);
                found_2 = found_2 || ((region.getCoprocessorHost().findCoprocessor(TestClassLoading.cpName2)) != null);
                found_3 = found_3 || ((region.getCoprocessorHost().findCoprocessor("SimpleRegionObserver")) != null);
                found_5 = found_5 || ((region.getCoprocessorHost().findCoprocessor(TestClassLoading.cpName5)) != null);
                CoprocessorEnvironment env = region.getCoprocessorHost().findCoprocessorEnvironment(TestClassLoading.cpName6);
                if (env != null) {
                    found_6 = true;
                    Configuration conf = env.getConfiguration();
                    found6_k1 = (conf.get("k1")) != null;
                    found6_k2 = (conf.get("k2")) != null;
                    found6_k3 = (conf.get("k3")) != null;
                }
            }
        }
        Assert.assertTrue((("Class " + (TestClassLoading.cpName1)) + " was missing on a region"), found_1);
        Assert.assertTrue((("Class " + (TestClassLoading.cpName2)) + " was missing on a region"), found_2);
        Assert.assertTrue("Class SimpleRegionObserver was missing on a region", found_3);
        Assert.assertTrue((("Class " + (TestClassLoading.cpName5)) + " was missing on a region"), found_5);
        Assert.assertTrue((("Class " + (TestClassLoading.cpName6)) + " was missing on a region"), found_6);
        Assert.assertTrue("Configuration key 'k1' was missing on a region", found6_k1);
        Assert.assertTrue("Configuration key 'k2' was missing on a region", found6_k2);
        Assert.assertTrue("Configuration key 'k3' was missing on a region", found6_k3);
        Assert.assertFalse("Configuration key 'k4' wasn't configured", found6_k4);
    }

    @Test
    public void testClassLoadingFromLibDirInJar() throws Exception {
        loadingClassFromLibDirInJar("/lib/");
    }

    @Test
    public void testClassLoadingFromRelativeLibDirInJar() throws Exception {
        loadingClassFromLibDirInJar("lib/");
    }

    @Test
    public void testRegionServerCoprocessorsReported() throws Exception {
        // This was a test for HBASE-4070.
        // We are removing coprocessors from region load in HBASE-5258.
        // Therefore, this test now only checks system coprocessors.
        assertAllRegionServers(null);
    }

    @Test
    public void testMasterCoprocessorsReported() {
        // HBASE 4070: Improve region server metrics to report loaded coprocessors
        // to master: verify that the master is reporting the correct set of
        // loaded coprocessors.
        final String loadedMasterCoprocessorsVerify = ("[" + (TestClassLoading.masterCoprocessor.getSimpleName())) + "]";
        String loadedMasterCoprocessors = Arrays.toString(TestClassLoading.TEST_UTIL.getHBaseCluster().getMaster().getMasterCoprocessors());
        Assert.assertEquals(loadedMasterCoprocessorsVerify, loadedMasterCoprocessors);
    }
}

