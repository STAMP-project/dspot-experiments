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


import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.Tool;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Distributed i/o benchmark.
 * <p>
 * This test writes into or reads from a specified number of files.
 * Number of bytes to write or read is specified as a parameter to the test.
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
 * <li>standard deviation of i/o rate </li>
 * </ul>
 */
public class TestDFSIO implements Tool {
    // Constants
    private static final Logger LOG = LoggerFactory.getLogger(TestDFSIO.class);

    private static final int DEFAULT_BUFFER_SIZE = 1000000;

    private static final String BASE_FILE_NAME = "test_io_";

    private static final String DEFAULT_RES_FILE_NAME = "TestDFSIO_results.log";

    private static final long MEGA = TestDFSIO.ByteMultiple.MB.value();

    private static final int DEFAULT_NR_BYTES = 128;

    private static final int DEFAULT_NR_FILES = 4;

    private static final String USAGE = ((((((((("Usage: " + (TestDFSIO.class.getSimpleName())) + " [genericOptions]") + " -read [-random | -backward | -skip [-skipSize Size]] |") + " -write | -append | -truncate | -clean") + " [-compression codecClassName]") + " [-nrFiles N]") + " [-size Size[B|KB|MB|GB|TB]]") + " [-resFile resultFileName] [-bufferSize Bytes]") + " [-storagePolicy storagePolicyName]") + " [-erasureCodePolicy erasureCodePolicyName]";

    private Configuration config;

    private static final String STORAGE_POLICY_NAME_KEY = "test.io.block.storage.policy";

    private static final String ERASURE_CODE_POLICY_NAME_KEY = "test.io.erasure.code.policy";

    static {
        Configuration.addDefaultResource("hdfs-default.xml");
        Configuration.addDefaultResource("hdfs-site.xml");
        Configuration.addDefaultResource("mapred-default.xml");
        Configuration.addDefaultResource("mapred-site.xml");
    }

    private enum TestType {

        TEST_TYPE_READ("read"),
        TEST_TYPE_WRITE("write"),
        TEST_TYPE_CLEANUP("cleanup"),
        TEST_TYPE_APPEND("append"),
        TEST_TYPE_READ_RANDOM("random read"),
        TEST_TYPE_READ_BACKWARD("backward read"),
        TEST_TYPE_READ_SKIP("skip read"),
        TEST_TYPE_TRUNCATE("truncate");
        private String type;

        private TestType(String t) {
            type = t;
        }

        // String
        @Override
        public String toString() {
            return type;
        }
    }

    enum ByteMultiple {

        B(1L),
        KB(1024L),
        MB(1048576L),
        GB(1073741824L),
        TB(1099511627776L);
        private long multiplier;

        private ByteMultiple(long mult) {
            multiplier = mult;
        }

        long value() {
            return multiplier;
        }

        static TestDFSIO.ByteMultiple parseString(String sMultiple) {
            // MB by default
            if ((sMultiple == null) || (sMultiple.isEmpty()))
                return TestDFSIO.ByteMultiple.MB;

            String sMU = StringUtils.toUpperCase(sMultiple);
            if (StringUtils.toUpperCase(TestDFSIO.ByteMultiple.B.name()).endsWith(sMU))
                return TestDFSIO.ByteMultiple.B;

            if (StringUtils.toUpperCase(TestDFSIO.ByteMultiple.KB.name()).endsWith(sMU))
                return TestDFSIO.ByteMultiple.KB;

            if (StringUtils.toUpperCase(TestDFSIO.ByteMultiple.MB.name()).endsWith(sMU))
                return TestDFSIO.ByteMultiple.MB;

            if (StringUtils.toUpperCase(TestDFSIO.ByteMultiple.GB.name()).endsWith(sMU))
                return TestDFSIO.ByteMultiple.GB;

            if (StringUtils.toUpperCase(TestDFSIO.ByteMultiple.TB.name()).endsWith(sMU))
                return TestDFSIO.ByteMultiple.TB;

            throw new IllegalArgumentException(("Unsupported ByteMultiple " + sMultiple));
        }
    }

    public TestDFSIO() {
        this.config = new Configuration();
    }

    private static MiniDFSCluster cluster;

    private static TestDFSIO bench;

    @Test(timeout = 10000)
    public void testRead() throws Exception {
        FileSystem fs = TestDFSIO.cluster.getFileSystem();
        long execTime = TestDFSIO.bench.readTest(fs);
        TestDFSIO.bench.analyzeResult(fs, TestDFSIO.TestType.TEST_TYPE_READ, execTime);
    }

    @Test(timeout = 10000)
    public void testReadRandom() throws Exception {
        FileSystem fs = TestDFSIO.cluster.getFileSystem();
        TestDFSIO.bench.getConf().setLong("test.io.skip.size", 0);
        long execTime = TestDFSIO.bench.randomReadTest(fs);
        TestDFSIO.bench.analyzeResult(fs, TestDFSIO.TestType.TEST_TYPE_READ_RANDOM, execTime);
    }

    @Test(timeout = 10000)
    public void testReadBackward() throws Exception {
        FileSystem fs = TestDFSIO.cluster.getFileSystem();
        TestDFSIO.bench.getConf().setLong("test.io.skip.size", (-(TestDFSIO.DEFAULT_BUFFER_SIZE)));
        long execTime = TestDFSIO.bench.randomReadTest(fs);
        TestDFSIO.bench.analyzeResult(fs, TestDFSIO.TestType.TEST_TYPE_READ_BACKWARD, execTime);
    }

    @Test(timeout = 10000)
    public void testReadSkip() throws Exception {
        FileSystem fs = TestDFSIO.cluster.getFileSystem();
        TestDFSIO.bench.getConf().setLong("test.io.skip.size", 1);
        long execTime = TestDFSIO.bench.randomReadTest(fs);
        TestDFSIO.bench.analyzeResult(fs, TestDFSIO.TestType.TEST_TYPE_READ_SKIP, execTime);
    }

    @Test(timeout = 10000)
    public void testAppend() throws Exception {
        FileSystem fs = TestDFSIO.cluster.getFileSystem();
        long execTime = TestDFSIO.bench.appendTest(fs);
        TestDFSIO.bench.analyzeResult(fs, TestDFSIO.TestType.TEST_TYPE_APPEND, execTime);
    }

    @Test(timeout = 60000)
    public void testTruncate() throws Exception {
        FileSystem fs = TestDFSIO.cluster.getFileSystem();
        TestDFSIO.bench.createControlFile(fs, ((TestDFSIO.DEFAULT_NR_BYTES) / 2), TestDFSIO.DEFAULT_NR_FILES);
        long execTime = TestDFSIO.bench.truncateTest(fs);
        TestDFSIO.bench.analyzeResult(fs, TestDFSIO.TestType.TEST_TYPE_TRUNCATE, execTime);
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
        protected CompressionCodec compressionCodec;

        protected String blockStoragePolicy;

        IOStatMapper() {
        }

        // Mapper
        @Override
        public void configure(JobConf conf) {
            super.configure(conf);
            // grab compression
            String compression = getConf().get("test.io.compression.class", null);
            Class<? extends CompressionCodec> codec;
            // try to initialize codec
            try {
                codec = (compression == null) ? null : Class.forName(compression).asSubclass(CompressionCodec.class);
            } catch (Exception e) {
                throw new RuntimeException("Compression codec not found: ", e);
            }
            if (codec != null) {
                compressionCodec = ((CompressionCodec) (ReflectionUtils.newInstance(codec, getConf())));
            }
            blockStoragePolicy = getConf().get(TestDFSIO.STORAGE_POLICY_NAME_KEY, null);
        }

        // IOMapperBase
        @Override
        void collectStats(OutputCollector<Text, Text> output, String name, long execTime, Long objSize) throws IOException {
            long totalSize = objSize.longValue();
            float ioRateMbSec = (((float) (totalSize)) * 1000) / (execTime * (TestDFSIO.MEGA));
            TestDFSIO.LOG.info(("Number of bytes processed = " + totalSize));
            TestDFSIO.LOG.info(("Exec time = " + execTime));
            TestDFSIO.LOG.info(("IO rate = " + ioRateMbSec));
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
    public static class WriteMapper extends TestDFSIO.IOStatMapper {
        public WriteMapper() {
            for (int i = 0; i < (bufferSize); i++) {
                buffer[i] = ((byte) ('0' + (i % 50)));
            }
        }

        // IOMapperBase
        @Override
        public Closeable getIOStream(String name) throws IOException {
            // create file
            Path filePath = new Path(TestDFSIO.getDataDir(getConf()), name);
            OutputStream out = fs.create(filePath, true, bufferSize);
            if ((blockStoragePolicy) != null) {
                fs.setStoragePolicy(filePath, blockStoragePolicy);
            }
            if ((compressionCodec) != null)
                out = compressionCodec.createOutputStream(out);

            TestDFSIO.LOG.info(("out = " + (out.getClass().getName())));
            return out;
        }

        // IOMapperBase
        // in bytes
        @Override
        public Long doIO(Reporter reporter, String name, long totalSize) throws IOException {
            OutputStream out = ((OutputStream) (this.stream));
            // write to the file
            long nrRemaining;
            for (nrRemaining = totalSize; nrRemaining > 0; nrRemaining -= bufferSize) {
                int curSize = ((bufferSize) < nrRemaining) ? bufferSize : ((int) (nrRemaining));
                out.write(buffer, 0, curSize);
                reporter.setStatus(((((((("writing " + name) + "@") + (totalSize - nrRemaining)) + "/") + totalSize) + " ::host = ") + (hostName)));
            }
            return Long.valueOf(totalSize);
        }
    }

    /**
     * Append mapper class.
     */
    public static class AppendMapper extends TestDFSIO.IOStatMapper {
        public AppendMapper() {
            for (int i = 0; i < (bufferSize); i++)
                buffer[i] = ((byte) ('0' + (i % 50)));

        }

        // IOMapperBase
        @Override
        public Closeable getIOStream(String name) throws IOException {
            // open file for append
            OutputStream out = fs.append(new Path(TestDFSIO.getDataDir(getConf()), name), bufferSize);
            if ((compressionCodec) != null)
                out = compressionCodec.createOutputStream(out);

            TestDFSIO.LOG.info(("out = " + (out.getClass().getName())));
            return out;
        }

        // IOMapperBase
        // in bytes
        @Override
        public Long doIO(Reporter reporter, String name, long totalSize) throws IOException {
            OutputStream out = ((OutputStream) (this.stream));
            // write to the file
            long nrRemaining;
            for (nrRemaining = totalSize; nrRemaining > 0; nrRemaining -= bufferSize) {
                int curSize = ((bufferSize) < nrRemaining) ? bufferSize : ((int) (nrRemaining));
                out.write(buffer, 0, curSize);
                reporter.setStatus(((((((("writing " + name) + "@") + (totalSize - nrRemaining)) + "/") + totalSize) + " ::host = ") + (hostName)));
            }
            return Long.valueOf(totalSize);
        }
    }

    /**
     * Read mapper class.
     */
    public static class ReadMapper extends TestDFSIO.IOStatMapper {
        public ReadMapper() {
        }

        // IOMapperBase
        @Override
        public Closeable getIOStream(String name) throws IOException {
            // open file
            InputStream in = fs.open(new Path(TestDFSIO.getDataDir(getConf()), name));
            if ((compressionCodec) != null)
                in = compressionCodec.createInputStream(in);

            TestDFSIO.LOG.info(("in = " + (in.getClass().getName())));
            return in;
        }

        // IOMapperBase
        // in bytes
        @Override
        public Long doIO(Reporter reporter, String name, long totalSize) throws IOException {
            InputStream in = ((InputStream) (this.stream));
            long actualSize = 0;
            while (actualSize < totalSize) {
                int curSize = in.read(buffer, 0, bufferSize);
                if (curSize < 0)
                    break;

                actualSize += curSize;
                reporter.setStatus(((((((("reading " + name) + "@") + actualSize) + "/") + totalSize) + " ::host = ") + (hostName)));
            } 
            return Long.valueOf(actualSize);
        }
    }

    /**
     * Mapper class for random reads.
     * The mapper chooses a position in the file and reads bufferSize
     * bytes starting at the chosen position.
     * It stops after reading the totalSize bytes, specified by -size.
     *
     * There are three type of reads.
     * 1) Random read always chooses a random position to read from: skipSize = 0
     * 2) Backward read reads file in reverse order                : skipSize < 0
     * 3) Skip-read skips skipSize bytes after every read          : skipSize > 0
     */
    public static class RandomReadMapper extends TestDFSIO.IOStatMapper {
        private ThreadLocalRandom rnd;

        private long fileSize;

        private long skipSize;

        // Mapper
        @Override
        public void configure(JobConf conf) {
            super.configure(conf);
            skipSize = conf.getLong("test.io.skip.size", 0);
        }

        public RandomReadMapper() {
            rnd = ThreadLocalRandom.current();
        }

        // IOMapperBase
        @Override
        public Closeable getIOStream(String name) throws IOException {
            Path filePath = new Path(TestDFSIO.getDataDir(getConf()), name);
            this.fileSize = fs.getFileStatus(filePath).getLen();
            InputStream in = fs.open(filePath);
            if ((compressionCodec) != null)
                in = new FSDataInputStream(compressionCodec.createInputStream(in));

            TestDFSIO.LOG.info(("in = " + (in.getClass().getName())));
            TestDFSIO.LOG.info(("skipSize = " + (skipSize)));
            return in;
        }

        // IOMapperBase
        // in bytes
        @Override
        public Long doIO(Reporter reporter, String name, long totalSize) throws IOException {
            PositionedReadable in = ((PositionedReadable) (this.stream));
            long actualSize = 0;
            for (long pos = nextOffset((-1)); actualSize < totalSize; pos = nextOffset(pos)) {
                int curSize = in.read(pos, buffer, 0, bufferSize);
                if (curSize < 0)
                    break;

                actualSize += curSize;
                reporter.setStatus(((((((("reading " + name) + "@") + actualSize) + "/") + totalSize) + " ::host = ") + (hostName)));
            }
            return Long.valueOf(actualSize);
        }

        /**
         * Get next offset for reading.
         * If current < 0 then choose initial offset according to the read type.
         *
         * @param current
         * 		offset
         * @return 
         */
        private long nextOffset(long current) {
            if ((skipSize) == 0)
                return rnd.nextLong(fileSize);

            if ((skipSize) > 0)
                return current < 0 ? 0 : (current + (bufferSize)) + (skipSize);

            // skipSize < 0
            return current < 0 ? Math.max(0, ((fileSize) - (bufferSize))) : Math.max(0, (current + (skipSize)));
        }
    }

    /**
     * Truncate mapper class.
     * The mapper truncates given file to the newLength, specified by -size.
     */
    public static class TruncateMapper extends TestDFSIO.IOStatMapper {
        private static final long DELAY = 100L;

        private Path filePath;

        private long fileSize;

        // IOMapperBase
        @Override
        public Closeable getIOStream(String name) throws IOException {
            filePath = new Path(TestDFSIO.getDataDir(getConf()), name);
            fileSize = fs.getFileStatus(filePath).getLen();
            return null;
        }

        // IOMapperBase
        // in bytes
        @Override
        public Long doIO(Reporter reporter, String name, long newLength) throws IOException {
            boolean isClosed = fs.truncate(filePath, newLength);
            reporter.setStatus(((((("truncating " + name) + " to newLength ") + newLength) + " ::host = ") + (hostName)));
            for (int i = 0; !isClosed; i++) {
                try {
                    Thread.sleep(TestDFSIO.TruncateMapper.DELAY);
                } catch (InterruptedException ignored) {
                }
                FileStatus status = fs.getFileStatus(filePath);
                assert status != null : "status is null";
                isClosed = (status.getLen()) == newLength;
                reporter.setStatus(((((((("truncate recover for " + name) + " to newLength ") + newLength) + " attempt ") + i) + " ::host = ") + (hostName)));
            }
            return Long.valueOf(((fileSize) - newLength));
        }
    }
}

