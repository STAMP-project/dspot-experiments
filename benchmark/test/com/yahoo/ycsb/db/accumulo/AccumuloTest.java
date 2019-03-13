/**
 * Copyright (c) 2016 YCSB contributors.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package com.yahoo.ycsb.db.accumulo;


import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.Workload;
import java.util.Properties;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Use an Accumulo MiniCluster to test out basic workload operations with
 * the Accumulo binding.
 */
public class AccumuloTest {
    private static final Logger LOG = LoggerFactory.getLogger(AccumuloTest.class);

    private static final int INSERT_COUNT = 2000;

    private static final int TRANSACTION_COUNT = 2000;

    @ClassRule
    public static TemporaryFolder workingDir = new TemporaryFolder();

    @Rule
    public TestName test = new TestName();

    private static MiniAccumuloCluster cluster;

    private static Properties properties;

    private Workload workload;

    private DB client;

    private Properties workloadProps;

    @Test
    public void workloada() throws Exception {
        runWorkload();
    }

    @Test
    public void workloadb() throws Exception {
        runWorkload();
    }

    @Test
    public void workloadc() throws Exception {
        runWorkload();
    }

    @Test
    public void workloadd() throws Exception {
        runWorkload();
    }

    @Test
    public void workloade() throws Exception {
        runWorkload();
    }
}

