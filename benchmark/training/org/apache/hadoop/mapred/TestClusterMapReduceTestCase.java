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
package org.apache.hadoop.mapred;


import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


public class TestClusterMapReduceTestCase extends ClusterMapReduceTestCase {
    @Test
    public void testMapReduce() throws Exception {
        _testMapReduce(false);
    }

    @Test
    public void testMapReduceRestarting() throws Exception {
        _testMapReduce(true);
    }

    @Test
    public void testDFSRestart() throws Exception {
        Path file = new Path(getInputDir(), "text.txt");
        OutputStream os = getFileSystem().create(file);
        Writer wr = new OutputStreamWriter(os);
        wr.close();
        stopCluster();
        startCluster(false, null);
        Assert.assertTrue(getFileSystem().exists(file));
        stopCluster();
        startCluster(true, null);
        Assert.assertFalse(getFileSystem().exists(file));
    }

    @Test
    public void testMRConfig() throws Exception {
        JobConf conf = createJobConf();
        Assert.assertNull(conf.get("xyz"));
        Properties config = new Properties();
        config.setProperty("xyz", "XYZ");
        stopCluster();
        startCluster(false, config);
        conf = createJobConf();
        Assert.assertEquals("XYZ", conf.get("xyz"));
    }
}

