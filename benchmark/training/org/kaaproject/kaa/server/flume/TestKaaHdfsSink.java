/**
 * Copyright 2014 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaaproject.kaa.server.flume;


import java.io.File;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.SinkRunner;
import org.apache.flume.source.AvroSource;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Test;
import org.kaaproject.kaa.server.flume.sink.hdfs.KaaHdfsSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestKaaHdfsSink {
    private static final Logger LOG = LoggerFactory.getLogger(TestKaaHdfsSink.class);

    private static AvroSource source;

    private static KaaHdfsSink sink;

    private static Channel channel;

    private static MiniDFSCluster dfsCluster = null;

    private static FileSystem fileSystem = null;

    private static String applicationToken = "42342342";

    private static byte[] endpointKeyHash = new byte[]{ 6, 3, 8, 4, 7, 5, 3, 6 };

    private static int logSchemaVersion = 1;

    private static File logSchemasRootDir;

    private SinkRunner sinkRunner;

    private long flushRecordsCountSmall = 1000;

    private long blockSizeSmall = 2097152;

    private long flushRecordsCount = flushRecordsCountSmall;

    private long blockSize = blockSizeSmall;

    @Test
    public void testLogDataEvents() throws Exception {
        TestKaaHdfsSink.source.setName("testLogDataSource");
        TestKaaHdfsSink.sink.setName("testLogDataSink");
        Context context = prepareContext();
        runTestAndCheckResult(context);
    }
}

