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


import CoprocessorHost.COPROCESSORS_ENABLED_CONF_KEY;
import CoprocessorHost.DEFAULT_COPROCESSORS_ENABLED;
import CoprocessorHost.MASTER_COPROCESSOR_CONF_KEY;
import CoprocessorHost.REGIONSERVER_COPROCESSOR_CONF_KEY;
import CoprocessorHost.REGION_COPROCESSOR_CONF_KEY;
import CoprocessorHost.USER_COPROCESSORS_ENABLED_CONF_KEY;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.master.MasterServices;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.RegionCoprocessorHost;
import org.apache.hadoop.hbase.regionserver.RegionServerServices;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static CoprocessorHost.DEFAULT_COPROCESSORS_ENABLED;
import static CoprocessorHost.DEFAULT_USER_COPROCESSORS_ENABLED;


/**
 * Tests for global coprocessor loading configuration
 */
@Category({ CoprocessorTests.class, SmallTests.class })
public class TestCoprocessorConfiguration {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCoprocessorConfiguration.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final Configuration CONF = HBaseConfiguration.create();

    static {
        TestCoprocessorConfiguration.CONF.setStrings(MASTER_COPROCESSOR_CONF_KEY, TestCoprocessorConfiguration.SystemCoprocessor.class.getName());
        TestCoprocessorConfiguration.CONF.setStrings(REGIONSERVER_COPROCESSOR_CONF_KEY, TestCoprocessorConfiguration.SystemCoprocessor.class.getName());
        TestCoprocessorConfiguration.CONF.setStrings(REGION_COPROCESSOR_CONF_KEY, TestCoprocessorConfiguration.SystemCoprocessor.class.getName());
    }

    private static final TableName TABLENAME = TableName.valueOf("TestCoprocessorConfiguration");

    private static final HRegionInfo REGIONINFO = new HRegionInfo(TestCoprocessorConfiguration.TABLENAME);

    private static final HTableDescriptor TABLEDESC = new HTableDescriptor(TestCoprocessorConfiguration.TABLENAME);

    static {
        try {
            TestCoprocessorConfiguration.TABLEDESC.addCoprocessor(TestCoprocessorConfiguration.TableCoprocessor.class.getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // use atomic types in case coprocessor loading is ever multithreaded, also
    // so we can mutate them even though they are declared final here
    private static final AtomicBoolean systemCoprocessorLoaded = new AtomicBoolean();

    private static final AtomicBoolean tableCoprocessorLoaded = new AtomicBoolean();

    public static class SystemCoprocessor implements MasterCoprocessor , RegionCoprocessor , RegionServerCoprocessor {
        @Override
        public void start(CoprocessorEnvironment env) throws IOException {
            TestCoprocessorConfiguration.systemCoprocessorLoaded.set(true);
        }

        @Override
        public void stop(CoprocessorEnvironment env) throws IOException {
        }
    }

    public static class TableCoprocessor implements RegionCoprocessor {
        @Override
        public void start(CoprocessorEnvironment env) throws IOException {
            TestCoprocessorConfiguration.tableCoprocessorLoaded.set(true);
        }

        @Override
        public void stop(CoprocessorEnvironment env) throws IOException {
        }
    }

    @Test
    public void testRegionCoprocessorHostDefaults() throws Exception {
        Configuration conf = new Configuration(TestCoprocessorConfiguration.CONF);
        HRegion region = Mockito.mock(HRegion.class);
        Mockito.when(region.getRegionInfo()).thenReturn(TestCoprocessorConfiguration.REGIONINFO);
        Mockito.when(region.getTableDescriptor()).thenReturn(TestCoprocessorConfiguration.TABLEDESC);
        RegionServerServices rsServices = Mockito.mock(RegionServerServices.class);
        TestCoprocessorConfiguration.systemCoprocessorLoaded.set(false);
        TestCoprocessorConfiguration.tableCoprocessorLoaded.set(false);
        new RegionCoprocessorHost(region, rsServices, conf);
        Assert.assertEquals("System coprocessors loading default was not honored", DEFAULT_COPROCESSORS_ENABLED, TestCoprocessorConfiguration.systemCoprocessorLoaded.get());
        Assert.assertEquals("Table coprocessors loading default was not honored", ((DEFAULT_COPROCESSORS_ENABLED) && (DEFAULT_USER_COPROCESSORS_ENABLED)), TestCoprocessorConfiguration.tableCoprocessorLoaded.get());
    }

    @Test
    public void testRegionServerCoprocessorHostDefaults() throws Exception {
        Configuration conf = new Configuration(TestCoprocessorConfiguration.CONF);
        RegionServerServices rsServices = Mockito.mock(RegionServerServices.class);
        TestCoprocessorConfiguration.systemCoprocessorLoaded.set(false);
        new org.apache.hadoop.hbase.regionserver.RegionServerCoprocessorHost(rsServices, conf);
        Assert.assertEquals("System coprocessors loading default was not honored", DEFAULT_COPROCESSORS_ENABLED, TestCoprocessorConfiguration.systemCoprocessorLoaded.get());
    }

    @Test
    public void testMasterCoprocessorHostDefaults() throws Exception {
        Configuration conf = new Configuration(TestCoprocessorConfiguration.CONF);
        MasterServices masterServices = Mockito.mock(MasterServices.class);
        TestCoprocessorConfiguration.systemCoprocessorLoaded.set(false);
        new org.apache.hadoop.hbase.master.MasterCoprocessorHost(masterServices, conf);
        Assert.assertEquals("System coprocessors loading default was not honored", DEFAULT_COPROCESSORS_ENABLED, TestCoprocessorConfiguration.systemCoprocessorLoaded.get());
    }

    @Test
    public void testRegionCoprocessorHostAllDisabled() throws Exception {
        Configuration conf = new Configuration(TestCoprocessorConfiguration.CONF);
        conf.setBoolean(COPROCESSORS_ENABLED_CONF_KEY, false);
        HRegion region = Mockito.mock(HRegion.class);
        Mockito.when(region.getRegionInfo()).thenReturn(TestCoprocessorConfiguration.REGIONINFO);
        Mockito.when(region.getTableDescriptor()).thenReturn(TestCoprocessorConfiguration.TABLEDESC);
        RegionServerServices rsServices = Mockito.mock(RegionServerServices.class);
        TestCoprocessorConfiguration.systemCoprocessorLoaded.set(false);
        TestCoprocessorConfiguration.tableCoprocessorLoaded.set(false);
        new RegionCoprocessorHost(region, rsServices, conf);
        Assert.assertFalse("System coprocessors should not have been loaded", TestCoprocessorConfiguration.systemCoprocessorLoaded.get());
        Assert.assertFalse("Table coprocessors should not have been loaded", TestCoprocessorConfiguration.tableCoprocessorLoaded.get());
    }

    @Test
    public void testRegionCoprocessorHostTableLoadingDisabled() throws Exception {
        Configuration conf = new Configuration(TestCoprocessorConfiguration.CONF);
        conf.setBoolean(COPROCESSORS_ENABLED_CONF_KEY, true);// if defaults change

        conf.setBoolean(USER_COPROCESSORS_ENABLED_CONF_KEY, false);
        HRegion region = Mockito.mock(HRegion.class);
        Mockito.when(region.getRegionInfo()).thenReturn(TestCoprocessorConfiguration.REGIONINFO);
        Mockito.when(region.getTableDescriptor()).thenReturn(TestCoprocessorConfiguration.TABLEDESC);
        RegionServerServices rsServices = Mockito.mock(RegionServerServices.class);
        TestCoprocessorConfiguration.systemCoprocessorLoaded.set(false);
        TestCoprocessorConfiguration.tableCoprocessorLoaded.set(false);
        new RegionCoprocessorHost(region, rsServices, conf);
        Assert.assertTrue("System coprocessors should have been loaded", TestCoprocessorConfiguration.systemCoprocessorLoaded.get());
        Assert.assertFalse("Table coprocessors should not have been loaded", TestCoprocessorConfiguration.tableCoprocessorLoaded.get());
    }

    /**
     * Rough test that Coprocessor Environment is Read-Only.
     * Just check a random CP and see that it returns a read-only config.
     */
    @Test
    public void testReadOnlyConfiguration() throws Exception {
        Configuration conf = new Configuration(TestCoprocessorConfiguration.CONF);
        HRegion region = Mockito.mock(HRegion.class);
        Mockito.when(region.getRegionInfo()).thenReturn(TestCoprocessorConfiguration.REGIONINFO);
        Mockito.when(region.getTableDescriptor()).thenReturn(TestCoprocessorConfiguration.TABLEDESC);
        RegionServerServices rsServices = Mockito.mock(RegionServerServices.class);
        RegionCoprocessorHost rcp = new RegionCoprocessorHost(region, rsServices, conf);
        boolean found = false;
        for (String cpStr : rcp.getCoprocessors()) {
            CoprocessorEnvironment cpenv = rcp.findCoprocessorEnvironment(cpStr);
            if (cpenv != null) {
                found = true;
            }
            Configuration c = cpenv.getConfiguration();
            thrown.expect(UnsupportedOperationException.class);
            c.set("one.two.three", "four.five.six");
        }
        Assert.assertTrue("Should be at least one CP found", found);
    }
}

