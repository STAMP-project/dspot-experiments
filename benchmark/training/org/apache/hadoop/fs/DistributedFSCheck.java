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
package org.apache.hadoop.fs;


import java.io.IOException;
import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSystem;
import org.apache.hadoop.mapred.Path;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Distributed checkup of the file system consistency.
 * <p>
 * Test file system consistency by reading each block of each file
 * of the specified file tree.
 * Report corrupted blocks and general file statistics.
 * <p>
 * Optionally displays statistics on read performance.
 */
@Ignore
public class DistributedFSCheck extends TestCase {
    // Constants
    private static final Logger LOG = LoggerFactory.getLogger(DistributedFSCheck.class);

    private static final int TEST_TYPE_READ = 0;

    private static final int TEST_TYPE_CLEANUP = 2;

    private static final int DEFAULT_BUFFER_SIZE = 1000000;

    private static final String DEFAULT_RES_FILE_NAME = "DistributedFSCheck_results.log";

    private static final long MEGA = 1048576;

    private static Configuration fsConfig = new Configuration();

    private static Path TEST_ROOT_DIR = new Path(System.getProperty("test.build.data", "/benchmarks/DistributedFSCheck"));

    private static Path MAP_INPUT_DIR = new Path(DistributedFSCheck.TEST_ROOT_DIR, "map_input");

    private static Path READ_DIR = new Path(DistributedFSCheck.TEST_ROOT_DIR, "io_read");

    private FileSystem fs;

    private long nrFiles;

    DistributedFSCheck(Configuration conf) throws Exception {
        DistributedFSCheck.fsConfig = conf;
        this.fs = FileSystem.get(conf);
    }

    /**
     * Run distributed checkup for the entire files system.
     *
     * @throws Exception
     * 		
     */
    public void testFSBlocks() throws Exception {
        testFSBlocks("/");
    }

    /**
     * DistributedFSCheck mapper class.
     */
    public static class DistributedFSCheckMapper extends IOMapperBase<Object> {
        public DistributedFSCheckMapper() {
        }

        public Object doIO(Reporter reporter, String name, long offset) throws IOException {
            // open file
            FSDataInputStream in = null;
            Path p = new Path(name);
            try {
                in = fs.open(p);
            } catch (IOException e) {
                return name + "@(missing)";
            }
            in.seek(offset);
            long actualSize = 0;
            try {
                long blockSize = fs.getDefaultBlockSize(p);
                reporter.setStatus(((((("reading " + name) + "@") + offset) + "/") + blockSize));
                for (int curSize = bufferSize; (curSize == (bufferSize)) && (actualSize < blockSize); actualSize += curSize) {
                    curSize = in.read(buffer, 0, bufferSize);
                }
            } catch (IOException e) {
                DistributedFSCheck.LOG.info(((("Corrupted block detected in \"" + name) + "\" at ") + offset));
                return (name + "@") + offset;
            } finally {
                in.close();
            }
            return new Long(actualSize);
        }

        void collectStats(OutputCollector<Text, Text> output, String name, long execTime, Object corruptedBlock) throws IOException {
            output.collect(new Text(((AccumulatingReducer.VALUE_TYPE_LONG) + "blocks")), new Text(String.valueOf(1)));
            if (corruptedBlock.getClass().getName().endsWith("String")) {
                output.collect(new Text(((AccumulatingReducer.VALUE_TYPE_STRING) + "badBlocks")), new Text(((String) (corruptedBlock))));
                return;
            }
            long totalSize = ((Long) (corruptedBlock)).longValue();
            float ioRateMbSec = (((float) (totalSize)) * 1000) / (execTime * 1048576);
            DistributedFSCheck.LOG.info(("Number of bytes processed = " + totalSize));
            DistributedFSCheck.LOG.info(("Exec time = " + execTime));
            DistributedFSCheck.LOG.info(("IO rate = " + ioRateMbSec));
            output.collect(new Text(((AccumulatingReducer.VALUE_TYPE_LONG) + "size")), new Text(String.valueOf(totalSize)));
            output.collect(new Text(((AccumulatingReducer.VALUE_TYPE_LONG) + "time")), new Text(String.valueOf(execTime)));
            output.collect(new Text(((AccumulatingReducer.VALUE_TYPE_FLOAT) + "rate")), new Text(String.valueOf((ioRateMbSec * 1000))));
        }
    }
}

