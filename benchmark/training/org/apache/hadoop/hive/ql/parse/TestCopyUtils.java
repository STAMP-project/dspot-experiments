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
package org.apache.hadoop.hive.ql.parse;


import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hive.shims.HadoopShims;
import org.apache.hadoop.hive.shims.HadoopShims.MiniMrShim;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCopyUtils {
    @Rule
    public final TestName testName = new TestName();

    @Rule
    public TestRule replV1BackwardCompat;

    protected static final Logger LOG = LoggerFactory.getLogger(TestReplicationScenarios.class);

    static class WarehouseInstanceWithMR extends WarehouseInstance {
        MiniMrShim mrCluster;

        WarehouseInstanceWithMR(Logger logger, MiniDFSCluster cluster, Map<String, String> overridesForHiveConf) throws Exception {
            super(logger, cluster, overridesForHiveConf);
            HadoopShims shims = ShimLoader.getHadoopShims();
            mrCluster = shims.getLocalMiniTezCluster(hiveConf, false);
            // mrCluster = shims.getMiniMrCluster(hiveConf, 2,
            // miniDFSCluster.getFileSystem().getUri().toString(), 1);
            mrCluster.setupConfiguration(hiveConf);
        }

        @Override
        public void close() throws IOException {
            mrCluster.shutdown();
            super.close();
        }
    }

    private static TestCopyUtils.WarehouseInstanceWithMR primary;

    private static TestCopyUtils.WarehouseInstanceWithMR replica;

    private String primaryDbName;

    private String replicatedDbName;

    /**
     * We need to have to separate insert statements as we want the table to have two different data files.
     * This is required as one of the conditions for distcp to get invoked is to have more than 1 file.
     */
    @Test
    public void testPrivilegedDistCpWithSameUserAsCurrentDoesNotTryToImpersonate() throws Throwable {
        WarehouseInstance.Tuple tuple = TestCopyUtils.primary.run(("use " + (primaryDbName))).run("create table t1 (id int)").run("insert into t1 values (1),(2),(3)").run("insert into t1 values (11),(12),(13)").dump(primaryDbName, null);
        /* We have to do a comparision on the data of table t1 in replicated database because even though the file
        copy will fail due to impersonation failure the driver will return a success code 0. May be something to look at later
         */
        TestCopyUtils.replica.load(replicatedDbName, tuple.dumpLocation).run((("select * from " + (replicatedDbName)) + ".t1")).verifyResults(Arrays.asList("1", "2", "3", "12", "11", "13"));
    }
}

