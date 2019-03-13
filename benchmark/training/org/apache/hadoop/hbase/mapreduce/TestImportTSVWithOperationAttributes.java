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


import java.io.IOException;
import java.util.Optional;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.regionserver.Region;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ImportTsv.COLUMNS_CONF_KEY;
import static ImportTsv.MAPPER_CONF_KEY;
import static ImportTsv.SEPARATOR_CONF_KEY;


@Category({ MapReduceTests.class, LargeTests.class })
public class TestImportTSVWithOperationAttributes implements Configurable {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestImportTSVWithOperationAttributes.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestImportTSVWithOperationAttributes.class);

    protected static final String NAME = TestImportTsv.class.getSimpleName();

    protected static HBaseTestingUtility util = new HBaseTestingUtility();

    /**
     * Delete the tmp directory after running doMROnTableTest. Boolean. Default is
     * false.
     */
    protected static final String DELETE_AFTER_LOAD_CONF = (TestImportTSVWithOperationAttributes.NAME) + ".deleteAfterLoad";

    /**
     * Force use of combiner in doMROnTableTest. Boolean. Default is true.
     */
    protected static final String FORCE_COMBINER_CONF = (TestImportTSVWithOperationAttributes.NAME) + ".forceCombiner";

    private static Configuration conf;

    private static final String TEST_ATR_KEY = "test";

    private final String FAMILY = "FAM";

    @Rule
    public TestName name = new TestName();

    @Test
    public void testMROnTable() throws Exception {
        final TableName tableName = TableName.valueOf(((name.getMethodName()) + (TestImportTSVWithOperationAttributes.util.getRandomUUID())));
        // Prepare the arguments required for the test.
        String[] args = new String[]{ ("-D" + (MAPPER_CONF_KEY)) + "=org.apache.hadoop.hbase.mapreduce.TsvImporterCustomTestMapperForOprAttr", ("-D" + (COLUMNS_CONF_KEY)) + "=HBASE_ROW_KEY,FAM:A,FAM:B,HBASE_ATTRIBUTES_KEY", ("-D" + (SEPARATOR_CONF_KEY)) + "=\u001b", tableName.getNameAsString() };
        String data = "KEY\u001bVALUE1\u001bVALUE2\u001btest=>myvalue\n";
        TestImportTSVWithOperationAttributes.util.createTable(tableName, FAMILY);
        doMROnTableTest(TestImportTSVWithOperationAttributes.util, FAMILY, data, args, 1, true);
        TestImportTSVWithOperationAttributes.util.deleteTable(tableName);
    }

    @Test
    public void testMROnTableWithInvalidOperationAttr() throws Exception {
        final TableName tableName = TableName.valueOf(((name.getMethodName()) + (TestImportTSVWithOperationAttributes.util.getRandomUUID())));
        // Prepare the arguments required for the test.
        String[] args = new String[]{ ("-D" + (MAPPER_CONF_KEY)) + "=org.apache.hadoop.hbase.mapreduce.TsvImporterCustomTestMapperForOprAttr", ("-D" + (COLUMNS_CONF_KEY)) + "=HBASE_ROW_KEY,FAM:A,FAM:B,HBASE_ATTRIBUTES_KEY", ("-D" + (SEPARATOR_CONF_KEY)) + "=\u001b", tableName.getNameAsString() };
        String data = "KEY\u001bVALUE1\u001bVALUE2\u001btest1=>myvalue\n";
        TestImportTSVWithOperationAttributes.util.createTable(tableName, FAMILY);
        doMROnTableTest(TestImportTSVWithOperationAttributes.util, FAMILY, data, args, 1, false);
        TestImportTSVWithOperationAttributes.util.deleteTable(tableName);
    }

    public static class OperationAttributesTestController implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void prePut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
            Region region = e.getEnvironment().getRegion();
            if ((!(region.getRegionInfo().isMetaRegion())) && (!(region.getRegionInfo().getTable().isSystemTable()))) {
                if ((put.getAttribute(TestImportTSVWithOperationAttributes.TEST_ATR_KEY)) != null) {
                    TestImportTSVWithOperationAttributes.LOG.debug(("allow any put to happen " + (region.getRegionInfo().getRegionNameAsString())));
                } else {
                    e.bypass();
                }
            }
        }
    }
}

