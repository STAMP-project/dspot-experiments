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


import java.util.Set;
import java.util.TreeSet;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.CellComparator;
import org.apache.hadoop.hbase.IntegrationTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValue.Type;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.util.Tool;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Validate ImportTsv + LoadIncrementalHFiles on a distributed cluster.
 */
@Category(IntegrationTests.class)
public class IntegrationTestImportTsv extends Configured implements Tool {
    private static final String NAME = IntegrationTestImportTsv.class.getSimpleName();

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestImportTsv.class);

    protected static final String simple_tsv = "row1\t1\tc1\tc2\n" + (((((((("row2\t1\tc1\tc2\n" + "row3\t1\tc1\tc2\n") + "row4\t1\tc1\tc2\n") + "row5\t1\tc1\tc2\n") + "row6\t1\tc1\tc2\n") + "row7\t1\tc1\tc2\n") + "row8\t1\tc1\tc2\n") + "row9\t1\tc1\tc2\n") + "row10\t1\tc1\tc2\n");

    @Rule
    public TestName name = new TestName();

    protected static final Set<KeyValue> simple_expected = new TreeSet<KeyValue>(CellComparator.getInstance()) {
        private static final long serialVersionUID = 1L;

        {
            byte[] family = Bytes.toBytes("d");
            for (String line : IntegrationTestImportTsv.simple_tsv.split("\n")) {
                String[] row = line.split("\t");
                byte[] key = Bytes.toBytes(row[0]);
                long ts = Long.parseLong(row[1]);
                byte[][] fields = new byte[][]{ Bytes.toBytes(row[2]), Bytes.toBytes(row[3]) };
                add(new KeyValue(key, family, fields[0], ts, Type.Put, fields[0]));
                add(new KeyValue(key, family, fields[1], ts, Type.Put, fields[1]));
            }
        }
    };

    // this instance is initialized on first access when the test is run from
    // JUnit/Maven or by main when run from the CLI.
    protected static IntegrationTestingUtility util = null;

    @Test
    public void testGenerateAndLoad() throws Exception {
        generateAndLoad(TableName.valueOf(name.getMethodName()));
    }
}

