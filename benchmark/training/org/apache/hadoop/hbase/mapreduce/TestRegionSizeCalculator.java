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
package org.apache.hadoop.hbase.mapreduce;


import RegionSizeCalculator.ENABLE_REGIONSIZECALCULATOR;
import ServerName.NON_STARTCODE;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, SmallTests.class })
public class TestRegionSizeCalculator {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionSizeCalculator.class);

    private Configuration configuration = new Configuration();

    private final long megabyte = 1024L * 1024L;

    private final ServerName sn = ServerName.valueOf("local-rs", HConstants.DEFAULT_REGIONSERVER_PORT, NON_STARTCODE);

    @Test
    public void testSimpleTestCase() throws Exception {
        RegionLocator regionLocator = mockRegionLocator("region1", "region2", "region3");
        Admin admin = mockAdmin(mockRegion("region1", 123), mockRegion("region3", 1232), mockRegion("region2", 54321));
        RegionSizeCalculator calculator = new RegionSizeCalculator(regionLocator, admin);
        Assert.assertEquals((123 * (megabyte)), calculator.getRegionSize(Bytes.toBytes("region1")));
        Assert.assertEquals((54321 * (megabyte)), calculator.getRegionSize(Bytes.toBytes("region2")));
        Assert.assertEquals((1232 * (megabyte)), calculator.getRegionSize(Bytes.toBytes("region3")));
        // if regionCalculator does not know about a region, it should return 0
        Assert.assertEquals((0 * (megabyte)), calculator.getRegionSize(Bytes.toBytes("otherTableRegion")));
        Assert.assertEquals(3, calculator.getRegionSizeMap().size());
    }

    /**
     * When size of region in megabytes is larger than largest possible integer there could be
     * error caused by lost of precision.
     */
    @Test
    public void testLargeRegion() throws Exception {
        RegionLocator regionLocator = mockRegionLocator("largeRegion");
        Admin admin = mockAdmin(mockRegion("largeRegion", Integer.MAX_VALUE));
        RegionSizeCalculator calculator = new RegionSizeCalculator(regionLocator, admin);
        Assert.assertEquals((((long) (Integer.MAX_VALUE)) * (megabyte)), calculator.getRegionSize(Bytes.toBytes("largeRegion")));
    }

    /**
     * When calculator is disabled, it should return 0 for each request.
     */
    @Test
    public void testDisabled() throws Exception {
        String regionName = "cz.goout:/index.html";
        RegionLocator table = mockRegionLocator(regionName);
        Admin admin = mockAdmin(mockRegion(regionName, 999));
        // first request on enabled calculator
        RegionSizeCalculator calculator = new RegionSizeCalculator(table, admin);
        Assert.assertEquals((999 * (megabyte)), calculator.getRegionSize(Bytes.toBytes(regionName)));
        // then disabled calculator.
        configuration.setBoolean(ENABLE_REGIONSIZECALCULATOR, false);
        RegionSizeCalculator disabledCalculator = new RegionSizeCalculator(table, admin);
        Assert.assertEquals((0 * (megabyte)), disabledCalculator.getRegionSize(Bytes.toBytes(regionName)));
        Assert.assertEquals(0, disabledCalculator.getRegionSizeMap().size());
    }
}

