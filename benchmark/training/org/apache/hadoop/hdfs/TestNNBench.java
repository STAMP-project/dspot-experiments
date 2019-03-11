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
package org.apache.hadoop.hdfs;


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.HadoopTestCase;
import org.junit.Assert;
import org.junit.Test;


public class TestNNBench extends HadoopTestCase {
    private static final String BASE_DIR = new File(System.getProperty("test.build.data", "build/test/data"), "NNBench").getAbsolutePath();

    public TestNNBench() throws IOException {
        super(HadoopTestCase.LOCAL_MR, HadoopTestCase.LOCAL_FS, 1, 1);
    }

    @Test(timeout = 30000)
    public void testNNBenchCreateReadAndDelete() throws Exception {
        runNNBench(createJobConf(), "create_write");
        Path path = new Path(((TestNNBench.BASE_DIR) + "/data/file_0_0"));
        Assert.assertTrue("create_write should create the file", getFileSystem().exists(path));
        runNNBench(createJobConf(), "open_read");
        runNNBench(createJobConf(), "delete");
        Assert.assertFalse("Delete operation should delete the file", getFileSystem().exists(path));
    }

    @Test(timeout = 30000)
    public void testNNBenchCreateAndRename() throws Exception {
        runNNBench(createJobConf(), "create_write");
        Path path = new Path(((TestNNBench.BASE_DIR) + "/data/file_0_0"));
        Assert.assertTrue("create_write should create the file", getFileSystem().exists(path));
        runNNBench(createJobConf(), "rename");
        Path renamedPath = new Path(((TestNNBench.BASE_DIR) + "/data/file_0_r_0"));
        Assert.assertFalse("Rename should rename the file", getFileSystem().exists(path));
        Assert.assertTrue("Rename should rename the file", getFileSystem().exists(renamedPath));
    }
}

