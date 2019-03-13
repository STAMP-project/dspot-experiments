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


import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.GenericTestUtils.LogCapturer;
import org.junit.Test;
import org.slf4j.LoggerFactory;


public class TestDataStream {
    static MiniDFSCluster cluster;

    static int PACKET_SIZE = 1024;

    @Test(timeout = 60000)
    public void testDfsClient() throws IOException, InterruptedException {
        LogCapturer logs = GenericTestUtils.LogCapturer.captureLogs(LoggerFactory.getLogger(DataStreamer.class));
        byte[] toWrite = new byte[TestDataStream.PACKET_SIZE];
        new Random(1).nextBytes(toWrite);
        final Path path = new Path("/file1");
        final DistributedFileSystem dfs = TestDataStream.cluster.getFileSystem();
        FSDataOutputStream out = null;
        out = dfs.create(path, false);
        out.write(toWrite);
        out.write(toWrite);
        out.hflush();
        // Wait to cross slow IO warning threshold
        Thread.sleep((15 * 1000));
        out.write(toWrite);
        out.write(toWrite);
        out.hflush();
        // Wait for capturing logs in busy cluster
        Thread.sleep((5 * 1000));
        out.close();
        logs.stopCapturing();
        GenericTestUtils.assertDoesNotMatch(logs.getOutput(), "Slow ReadProcessor read fields for block");
    }
}

