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


import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.hbase.IntegrationTestingUtility;
import org.apache.hadoop.hbase.testclassification.IntegrationTests;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test that we add tmpjars correctly including the named dependencies. Runs
 * as an integration test so that classpath is realistic.
 */
@Category(IntegrationTests.class)
public class IntegrationTestTableMapReduceUtil implements Configurable , Tool {
    private static IntegrationTestingUtility util;

    /**
     * Look for jars we expect to be on the classpath by name.
     */
    @Test
    public void testAddDependencyJars() throws Exception {
        Job job = new Job();
        TableMapReduceUtil.addDependencyJars(job);
        String tmpjars = job.getConfiguration().get("tmpjars");
        // verify presence of modules
        Assert.assertTrue(tmpjars.contains("hbase-common"));
        Assert.assertTrue(tmpjars.contains("hbase-protocol"));
        Assert.assertTrue(tmpjars.contains("hbase-client"));
        Assert.assertTrue(tmpjars.contains("hbase-hadoop-compat"));
        Assert.assertTrue(tmpjars.contains("hbase-server"));
        // verify presence of 3rd party dependencies.
        Assert.assertTrue(tmpjars.contains("zookeeper"));
        Assert.assertTrue(tmpjars.contains("netty"));
        Assert.assertTrue(tmpjars.contains("protobuf"));
        Assert.assertTrue(tmpjars.contains("guava"));
        Assert.assertTrue(tmpjars.contains("htrace"));
    }
}

