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


import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Path;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Distributed i/o benchmark.
 * <p>
 * This test writes into or reads from a specified number of files.
 * File size is specified as a parameter to the test.
 * Each file is accessed in a separate map task.
 * <p>
 * The reducer collects the following statistics:
 * <ul>
 * <li>number of tasks completed</li>
 * <li>number of bytes written/read</li>
 * <li>execution time</li>
 * <li>io rate</li>
 * <li>io rate squared</li>
 * </ul>
 *
 * Finally, the following information is appended to a local file
 * <ul>
 * <li>read or write test</li>
 * <li>date and time the test finished</li>
 * <li>number of files</li>
 * <li>total number of bytes processed</li>
 * <li>throughput in mb/sec (total number of bytes / sum of processing times)</li>
 * <li>average i/o rate in mb/sec per file</li>
 * <li>standard i/o rate deviation</li>
 * </ul>
 */
@Ignore
public class DFSCIOTest {
    // Constants
    private static final Logger LOG = LoggerFactory.getLogger(DFSCIOTest.class);

    private static final int TEST_TYPE_READ = 0;

    private static final int TEST_TYPE_WRITE = 1;

    private static final int TEST_TYPE_CLEANUP = 2;

    private static final int DEFAULT_BUFFER_SIZE = 1000000;

    private static final String BASE_FILE_NAME = "test_io_";

    private static final String DEFAULT_RES_FILE_NAME = "DFSCIOTest_results.log";

    private static Configuration fsConfig = new Configuration();

    private static final long MEGA = 1048576;

    private static String TEST_ROOT_DIR = System.getProperty("test.build.data", "/benchmarks/DFSCIOTest");

    private static Path CONTROL_DIR = new Path(DFSCIOTest.TEST_ROOT_DIR, "io_control");

    private static Path WRITE_DIR = new Path(DFSCIOTest.TEST_ROOT_DIR, "io_write");

    private static Path READ_DIR = new Path(DFSCIOTest.TEST_ROOT_DIR, "io_read");

    private static Path DATA_DIR = new Path(DFSCIOTest.TEST_ROOT_DIR, "io_data");

    private static Path HDFS_TEST_DIR = new Path("/tmp/DFSCIOTest");

    private static String HDFS_LIB_VERSION = System.getProperty("libhdfs.version", "1");

    private static String CHMOD = new String("chmod");

    private static Path HDFS_SHLIB = new Path((((DFSCIOTest.HDFS_TEST_DIR) + "/libhdfs.so.") + (DFSCIOTest.HDFS_LIB_VERSION)));

    private static Path HDFS_READ = new Path(((DFSCIOTest.HDFS_TEST_DIR) + "/hdfs_read"));

    private static Path HDFS_WRITE = new Path(((DFSCIOTest.HDFS_TEST_DIR) + "/hdfs_write"));

    /**
     * Run the test with default parameters.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testIOs() throws Exception {
        DFSCIOTest.testIOs(10, 10);
    }

    /**
     * Write/Read mapper base class.
     * <p>
     * Collects the following statistics per task:
     * <ul>
     * <li>number of tasks completed</li>
     * <li>number of bytes written/read</li>
     * <li>execution time</li>
     * <li>i/o rate</li>
     * <li>i/o rate squared</li>
     * </ul>
     */
    private abstract static class IOStatMapper extends IOMapperBase<Long> {
        IOStatMapper() {
        }

        void collectStats(OutputCollector<Text, Text> output, String name, long execTime, Long objSize) throws IOException {
            long totalSize = objSize.longValue();
            float ioRateMbSec = (((float) (totalSize)) * 1000) / (execTime * (DFSCIOTest.MEGA));
            DFSCIOTest.LOG.info(("Number of bytes processed = " + totalSize));
            DFSCIOTest.LOG.info(("Exec time = " + execTime));
            DFSCIOTest.LOG.info(("IO rate = " + ioRateMbSec));
            output.collect(new Text(((AccumulatingReducer.VALUE_TYPE_LONG) + "tasks")), new Text(String.valueOf(1)));
            output.collect(new Text(((AccumulatingReducer.VALUE_TYPE_LONG) + "size")), new Text(String.valueOf(totalSize)));
            output.collect(new Text(((AccumulatingReducer.VALUE_TYPE_LONG) + "time")), new Text(String.valueOf(execTime)));
            output.collect(new Text(((AccumulatingReducer.VALUE_TYPE_FLOAT) + "rate")), new Text(String.valueOf((ioRateMbSec * 1000))));
            output.collect(new Text(((AccumulatingReducer.VALUE_TYPE_FLOAT) + "sqrate")), new Text(String.valueOf(((ioRateMbSec * ioRateMbSec) * 1000))));
        }
    }

    /**
     * Write mapper class.
     */
    public static class WriteMapper extends DFSCIOTest.IOStatMapper {
        public WriteMapper() {
            super();
            for (int i = 0; i < (bufferSize); i++)
                buffer[i] = ((byte) ('0' + (i % 50)));

        }

        public Long doIO(Reporter reporter, String name, long totalSize) throws IOException {
            // create file
            totalSize *= DFSCIOTest.MEGA;
            // create instance of local filesystem
            FileSystem localFS = FileSystem.getLocal(DFSCIOTest.fsConfig);
            try {
                // native runtime
                Runtime runTime = Runtime.getRuntime();
                // copy the dso and executable from dfs and chmod them
                synchronized(this) {
                    localFS.delete(DFSCIOTest.HDFS_TEST_DIR, true);
                    if (!(localFS.mkdirs(DFSCIOTest.HDFS_TEST_DIR))) {
                        throw new IOException((("Failed to create " + (DFSCIOTest.HDFS_TEST_DIR)) + " on local filesystem"));
                    }
                }
                synchronized(this) {
                    if (!(localFS.exists(DFSCIOTest.HDFS_SHLIB))) {
                        FileUtil.copy(fs, DFSCIOTest.HDFS_SHLIB, localFS, DFSCIOTest.HDFS_SHLIB, false, DFSCIOTest.fsConfig);
                        String chmodCmd = new String((((DFSCIOTest.CHMOD) + " a+x ") + (DFSCIOTest.HDFS_SHLIB)));
                        Process process = runTime.exec(chmodCmd);
                        int exitStatus = process.waitFor();
                        if (exitStatus != 0) {
                            throw new IOException(((chmodCmd + ": Failed with exitStatus: ") + exitStatus));
                        }
                    }
                }
                synchronized(this) {
                    if (!(localFS.exists(DFSCIOTest.HDFS_WRITE))) {
                        FileUtil.copy(fs, DFSCIOTest.HDFS_WRITE, localFS, DFSCIOTest.HDFS_WRITE, false, DFSCIOTest.fsConfig);
                        String chmodCmd = new String((((DFSCIOTest.CHMOD) + " a+x ") + (DFSCIOTest.HDFS_WRITE)));
                        Process process = runTime.exec(chmodCmd);
                        int exitStatus = process.waitFor();
                        if (exitStatus != 0) {
                            throw new IOException(((chmodCmd + ": Failed with exitStatus: ") + exitStatus));
                        }
                    }
                }
                // exec the C program
                Path outFile = new Path(DFSCIOTest.DATA_DIR, name);
                String writeCmd = new String((((((((DFSCIOTest.HDFS_WRITE) + " ") + outFile) + " ") + totalSize) + " ") + (bufferSize)));
                Process process = runTime.exec(writeCmd, null, new File(DFSCIOTest.HDFS_TEST_DIR.toString()));
                int exitStatus = process.waitFor();
                if (exitStatus != 0) {
                    throw new IOException(((writeCmd + ": Failed with exitStatus: ") + exitStatus));
                }
            } catch (InterruptedException interruptedException) {
                reporter.setStatus(interruptedException.toString());
            } finally {
                localFS.close();
            }
            return new Long(totalSize);
        }
    }

    /**
     * Read mapper class.
     */
    public static class ReadMapper extends DFSCIOTest.IOStatMapper {
        public ReadMapper() {
            super();
        }

        public Long doIO(Reporter reporter, String name, long totalSize) throws IOException {
            totalSize *= DFSCIOTest.MEGA;
            // create instance of local filesystem
            FileSystem localFS = FileSystem.getLocal(DFSCIOTest.fsConfig);
            try {
                // native runtime
                Runtime runTime = Runtime.getRuntime();
                // copy the dso and executable from dfs
                synchronized(this) {
                    localFS.delete(DFSCIOTest.HDFS_TEST_DIR, true);
                    if (!(localFS.mkdirs(DFSCIOTest.HDFS_TEST_DIR))) {
                        throw new IOException((("Failed to create " + (DFSCIOTest.HDFS_TEST_DIR)) + " on local filesystem"));
                    }
                }
                synchronized(this) {
                    if (!(localFS.exists(DFSCIOTest.HDFS_SHLIB))) {
                        if (!(FileUtil.copy(fs, DFSCIOTest.HDFS_SHLIB, localFS, DFSCIOTest.HDFS_SHLIB, false, DFSCIOTest.fsConfig))) {
                            throw new IOException((("Failed to copy " + (DFSCIOTest.HDFS_SHLIB)) + " to local filesystem"));
                        }
                        String chmodCmd = new String((((DFSCIOTest.CHMOD) + " a+x ") + (DFSCIOTest.HDFS_SHLIB)));
                        Process process = runTime.exec(chmodCmd);
                        int exitStatus = process.waitFor();
                        if (exitStatus != 0) {
                            throw new IOException(((chmodCmd + ": Failed with exitStatus: ") + exitStatus));
                        }
                    }
                }
                synchronized(this) {
                    if (!(localFS.exists(DFSCIOTest.HDFS_READ))) {
                        if (!(FileUtil.copy(fs, DFSCIOTest.HDFS_READ, localFS, DFSCIOTest.HDFS_READ, false, DFSCIOTest.fsConfig))) {
                            throw new IOException((("Failed to copy " + (DFSCIOTest.HDFS_READ)) + " to local filesystem"));
                        }
                        String chmodCmd = new String((((DFSCIOTest.CHMOD) + " a+x ") + (DFSCIOTest.HDFS_READ)));
                        Process process = runTime.exec(chmodCmd);
                        int exitStatus = process.waitFor();
                        if (exitStatus != 0) {
                            throw new IOException(((chmodCmd + ": Failed with exitStatus: ") + exitStatus));
                        }
                    }
                }
                // exec the C program
                Path inFile = new Path(DFSCIOTest.DATA_DIR, name);
                String readCmd = new String((((((((DFSCIOTest.HDFS_READ) + " ") + inFile) + " ") + totalSize) + " ") + (bufferSize)));
                Process process = runTime.exec(readCmd, null, new File(DFSCIOTest.HDFS_TEST_DIR.toString()));
                int exitStatus = process.waitFor();
                if (exitStatus != 0) {
                    throw new IOException((((DFSCIOTest.HDFS_READ) + ": Failed with exitStatus: ") + exitStatus));
                }
            } catch (InterruptedException interruptedException) {
                reporter.setStatus(interruptedException.toString());
            } finally {
                localFS.close();
            }
            return new Long(totalSize);
        }
    }
}

