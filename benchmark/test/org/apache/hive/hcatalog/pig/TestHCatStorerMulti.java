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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.pig;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hive.hcatalog.common.HCatUtil;
import org.apache.hive.hcatalog.data.Pair;
import org.apache.hive.hcatalog.mapreduce.HCatBaseTest;
import org.apache.pig.PigServer;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestHCatStorerMulti {
    public static final String TEST_DATA_DIR = HCatUtil.makePathASafeFileName((((((System.getProperty("user.dir")) + "/build/test/data/") + (TestHCatStorerMulti.class.getCanonicalName())) + "-") + (System.currentTimeMillis())));

    private static final String TEST_WAREHOUSE_DIR = (TestHCatStorerMulti.TEST_DATA_DIR) + "/warehouse";

    private static final String INPUT_FILE_NAME = (TestHCatStorerMulti.TEST_DATA_DIR) + "/input.data";

    private static final String BASIC_TABLE = "junit_unparted_basic";

    private static final String PARTITIONED_TABLE = "junit_parted_basic";

    private static IDriver driver;

    private static Map<Integer, Pair<Integer, String>> basicInputData;

    private static final Map<String, Set<String>> DISABLED_STORAGE_FORMATS = new HashMap<String, Set<String>>();

    private final String storageFormat;

    public TestHCatStorerMulti(String storageFormat) {
        this.storageFormat = storageFormat;
    }

    @Test
    public void testStoreBasicTable() throws Exception {
        Assume.assumeTrue((!(TestUtil.shouldSkip(storageFormat, TestHCatStorerMulti.DISABLED_STORAGE_FORMATS))));
        createTable(TestHCatStorerMulti.BASIC_TABLE, "a int, b string");
        populateBasicFile();
        PigServer server = HCatBaseTest.createPigServer(false);
        server.setBatchOn();
        server.registerQuery((("A = load '" + (TestHCatStorerMulti.INPUT_FILE_NAME)) + "' as (a:int, b:chararray);"));
        server.registerQuery((("store A into '" + (TestHCatStorerMulti.BASIC_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatStorer();"));
        server.executeBatch();
        TestHCatStorerMulti.driver.run(("select * from " + (TestHCatStorerMulti.BASIC_TABLE)));
        ArrayList<String> unpartitionedTableValuesReadFromHiveDriver = new ArrayList<String>();
        TestHCatStorerMulti.driver.getResults(unpartitionedTableValuesReadFromHiveDriver);
        Assert.assertEquals(TestHCatStorerMulti.basicInputData.size(), unpartitionedTableValuesReadFromHiveDriver.size());
    }

    @Test
    public void testStorePartitionedTable() throws Exception {
        Assume.assumeTrue((!(TestUtil.shouldSkip(storageFormat, TestHCatStorerMulti.DISABLED_STORAGE_FORMATS))));
        createTable(TestHCatStorerMulti.PARTITIONED_TABLE, "a int, b string", "bkt string");
        populateBasicFile();
        PigServer server = HCatBaseTest.createPigServer(false);
        server.setBatchOn();
        server.registerQuery((("A = load '" + (TestHCatStorerMulti.INPUT_FILE_NAME)) + "' as (a:int, b:chararray);"));
        server.registerQuery("B2 = filter A by a < 2;");
        server.registerQuery((("store B2 into '" + (TestHCatStorerMulti.PARTITIONED_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatStorer('bkt=0');"));
        server.registerQuery("C2 = filter A by a >= 2;");
        server.registerQuery((("store C2 into '" + (TestHCatStorerMulti.PARTITIONED_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatStorer('bkt=1');"));
        server.executeBatch();
        TestHCatStorerMulti.driver.run(("select * from " + (TestHCatStorerMulti.PARTITIONED_TABLE)));
        ArrayList<String> partitionedTableValuesReadFromHiveDriver = new ArrayList<String>();
        TestHCatStorerMulti.driver.getResults(partitionedTableValuesReadFromHiveDriver);
        Assert.assertEquals(TestHCatStorerMulti.basicInputData.size(), partitionedTableValuesReadFromHiveDriver.size());
    }

    @Test
    public void testStoreTableMulti() throws Exception {
        Assume.assumeTrue((!(TestUtil.shouldSkip(storageFormat, TestHCatStorerMulti.DISABLED_STORAGE_FORMATS))));
        createTable(TestHCatStorerMulti.BASIC_TABLE, "a int, b string");
        createTable(TestHCatStorerMulti.PARTITIONED_TABLE, "a int, b string", "bkt string");
        populateBasicFile();
        PigServer server = HCatBaseTest.createPigServer(false);
        server.setBatchOn();
        server.registerQuery((("A = load '" + (TestHCatStorerMulti.INPUT_FILE_NAME)) + "' as (a:int, b:chararray);"));
        server.registerQuery((("store A into '" + (TestHCatStorerMulti.BASIC_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatStorer();"));
        server.registerQuery("B2 = filter A by a < 2;");
        server.registerQuery((("store B2 into '" + (TestHCatStorerMulti.PARTITIONED_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatStorer('bkt=0');"));
        server.registerQuery("C2 = filter A by a >= 2;");
        server.registerQuery((("store C2 into '" + (TestHCatStorerMulti.PARTITIONED_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatStorer('bkt=1');"));
        server.executeBatch();
        TestHCatStorerMulti.driver.run(("select * from " + (TestHCatStorerMulti.BASIC_TABLE)));
        ArrayList<String> unpartitionedTableValuesReadFromHiveDriver = new ArrayList<String>();
        TestHCatStorerMulti.driver.getResults(unpartitionedTableValuesReadFromHiveDriver);
        TestHCatStorerMulti.driver.run(("select * from " + (TestHCatStorerMulti.PARTITIONED_TABLE)));
        ArrayList<String> partitionedTableValuesReadFromHiveDriver = new ArrayList<String>();
        TestHCatStorerMulti.driver.getResults(partitionedTableValuesReadFromHiveDriver);
        Assert.assertEquals(TestHCatStorerMulti.basicInputData.size(), unpartitionedTableValuesReadFromHiveDriver.size());
        Assert.assertEquals(TestHCatStorerMulti.basicInputData.size(), partitionedTableValuesReadFromHiveDriver.size());
    }
}

