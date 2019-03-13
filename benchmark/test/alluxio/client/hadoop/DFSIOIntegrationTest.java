/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.hadoop;


import PropertyKey.USER_FILE_WRITE_TYPE_DEFAULT;
import alluxio.conf.ServerConfiguration;
import alluxio.hadoop.HadoopConfigurationUtils;
import alluxio.hadoop.org.apache.hadoop.fs.FileSystem;
import alluxio.testutils.BaseIntegrationTest;
import alluxio.testutils.LocalAlluxioClusterResource;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PositionedReadable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.util.Tool;
import org.junit.AssumptionViolatedException;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Distributed i/o benchmark.
 * <p>
 * This test writes into or reads from a specified number of files. Number of bytes to write or read
 * is specified as a parameter to the test. Each file is accessed in a separate map task.
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
 * <li>standard deviation of i/o rate</li>
 * </ul>
 */
public class DFSIOIntegrationTest extends BaseIntegrationTest implements Tool {
    /**
     * A rule that is used to enforce supported hadoop client versions before running this test.
     * Value for system property, "alluxio.hadoop.version", is injected by surefire plugin.
     */
    private static class HadoopVersionRule implements TestRule {
        @Override
        public Statement apply(Statement base, Description description) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    String hadoopVersion = System.getProperty("alluxio.hadoop.version");
                    if ((hadoopVersion != null) && (((hadoopVersion.startsWith("2.4")) || (hadoopVersion.startsWith("2.5"))) || (hadoopVersion.startsWith("2.6")))) {
                        throw new AssumptionViolatedException("Hadoop version not supported. Skipping test!");
                    } else {
                        base.evaluate();
                    }
                }
            };
        }
    }

    // Constants for DFSIOIntegrationTest
    private static final Logger LOG = LoggerFactory.getLogger(DFSIOIntegrationTest.class);

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private static final String BASE_FILE_NAME = "test_io_";

    private static final String DEFAULT_RES_FILE_NAME = "DFSIOIntegrationTest_results.log";

    private static final long MEGA = DFSIOIntegrationTest.ByteMultiple.MB.value();

    private static final int DEFAULT_NR_BYTES = 16384;

    private static final int DEFAULT_NR_FILES = 4;

    private static boolean sGenerateReportFile = false;

    private static final String USAGE = (((((((("Usage: " + (DFSIOIntegrationTest.class.getSimpleName())) + " [genericOptions]") + " -read [-random | -backward | -skip [-skipSize Size]] |") + " -write | -append | -clean") + " [-compression codecClassName]") + " [-nrFiles N]") + " [-size Size[B|KB|MB|GB|TB]]") + " [-resFile resultFileName] [-bufferSize Bytes]") + " [-rootDir]";

    private Configuration mConfig;

    @ClassRule
    public static LocalAlluxioClusterResource sLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().setProperty(USER_FILE_WRITE_TYPE_DEFAULT, "CACHE_THROUGH").build();

    private static URI sLocalAlluxioClusterUri = null;

    static {
        org.apache.hadoop.conf.Configuration.addDefaultResource("hdfs-default.xml");
        org.apache.hadoop.conf.Configuration.addDefaultResource("hdfs-site.xml");
        org.apache.hadoop.conf.Configuration.addDefaultResource("mapred-default.xml");
        org.apache.hadoop.conf.Configuration.addDefaultResource("mapred-site.xml");
    }

    /**
     * Represents different types of tests.
     */
    private enum TestType {

        TEST_TYPE_READ("read"),
        TEST_TYPE_WRITE("write"),
        TEST_TYPE_CLEANUP("cleanup"),
        TEST_TYPE_APPEND("append"),
        TEST_TYPE_READ_RANDOM("random read"),
        TEST_TYPE_READ_BACKWARD("backward read"),
        TEST_TYPE_READ_SKIP("skip read");
        private String mType;

        TestType(String t) {
            mType = t;
        }

        // String
        @Override
        public String toString() {
            return mType;
        }
    }

    /**
     * Represents for 5 multiple bytes unit.
     */
    enum ByteMultiple {

        B(1L),
        KB(1024L),
        MB(1048576L),
        GB(1073741824L),
        TB(1099511627776L);
        private long mMultiplier;

        ByteMultiple(long mult) {
            mMultiplier = mult;
        }

        long value() {
            return mMultiplier;
        }

        static DFSIOIntegrationTest.ByteMultiple parseString(String sMultiple) {
            if ((sMultiple == null) || (sMultiple.isEmpty())) {
                // MB by default
                return DFSIOIntegrationTest.ByteMultiple.MB;
            }
            String sMU = sMultiple.toUpperCase();
            if (DFSIOIntegrationTest.ByteMultiple.B.name().toUpperCase().endsWith(sMU)) {
                return DFSIOIntegrationTest.ByteMultiple.B;
            }
            if (DFSIOIntegrationTest.ByteMultiple.KB.name().toUpperCase().endsWith(sMU)) {
                return DFSIOIntegrationTest.ByteMultiple.KB;
            }
            if (DFSIOIntegrationTest.ByteMultiple.MB.name().toUpperCase().endsWith(sMU)) {
                return DFSIOIntegrationTest.ByteMultiple.MB;
            }
            if (DFSIOIntegrationTest.ByteMultiple.GB.name().toUpperCase().endsWith(sMU)) {
                return DFSIOIntegrationTest.ByteMultiple.GB;
            }
            if (DFSIOIntegrationTest.ByteMultiple.TB.name().toUpperCase().endsWith(sMU)) {
                return DFSIOIntegrationTest.ByteMultiple.TB;
            }
            throw new IllegalArgumentException(("Unsupported ByteMultiple " + sMultiple));
        }
    }

    public DFSIOIntegrationTest() {
        mConfig = new Configuration();
    }

    private static DFSIOIntegrationTest sBench;

    @ClassRule
    public static DFSIOIntegrationTest.HadoopVersionRule sHadoopVersionRule = new DFSIOIntegrationTest.HadoopVersionRule();

    @Test(timeout = 50000)
    public void read() throws Exception {
        FileSystem fs = org.apache.hadoop.fs.FileSystem.get(DFSIOIntegrationTest.sLocalAlluxioClusterUri, HadoopConfigurationUtils.mergeAlluxioConfiguration(DFSIOIntegrationTest.sBench.getConf(), ServerConfiguration.global()));
        long tStart = System.currentTimeMillis();
        DFSIOIntegrationTest.sBench.mapperReadTest(fs);
        long execTime = (System.currentTimeMillis()) - tStart;
        DFSIOIntegrationTest.sBench.analyzeResult(fs, DFSIOIntegrationTest.TestType.TEST_TYPE_READ, execTime);
    }

    @Test(timeout = 50000)
    public void readRandom() throws Exception {
        FileSystem fs = org.apache.hadoop.fs.FileSystem.get(DFSIOIntegrationTest.sLocalAlluxioClusterUri, HadoopConfigurationUtils.mergeAlluxioConfiguration(DFSIOIntegrationTest.sBench.getConf(), ServerConfiguration.global()));
        long tStart = System.currentTimeMillis();
        DFSIOIntegrationTest.sBench.getConf().setLong("test.io.skip.size", 0);
        DFSIOIntegrationTest.sBench.randomReadTest(fs);
        long execTime = (System.currentTimeMillis()) - tStart;
        DFSIOIntegrationTest.sBench.analyzeResult(fs, DFSIOIntegrationTest.TestType.TEST_TYPE_READ_RANDOM, execTime);
    }

    @Test(timeout = 50000)
    public void readBackward() throws Exception {
        FileSystem fs = org.apache.hadoop.fs.FileSystem.get(DFSIOIntegrationTest.sLocalAlluxioClusterUri, HadoopConfigurationUtils.mergeAlluxioConfiguration(DFSIOIntegrationTest.sBench.getConf(), ServerConfiguration.global()));
        long tStart = System.currentTimeMillis();
        DFSIOIntegrationTest.sBench.getConf().setLong("test.io.skip.size", (-(DFSIOIntegrationTest.DEFAULT_BUFFER_SIZE)));
        DFSIOIntegrationTest.sBench.randomReadTest(fs);
        long execTime = (System.currentTimeMillis()) - tStart;
        DFSIOIntegrationTest.sBench.analyzeResult(fs, DFSIOIntegrationTest.TestType.TEST_TYPE_READ_BACKWARD, execTime);
    }

    @Test(timeout = 50000)
    public void readSkip() throws Exception {
        FileSystem fs = org.apache.hadoop.fs.FileSystem.get(DFSIOIntegrationTest.sLocalAlluxioClusterUri, HadoopConfigurationUtils.mergeAlluxioConfiguration(DFSIOIntegrationTest.sBench.getConf(), ServerConfiguration.global()));
        long tStart = System.currentTimeMillis();
        DFSIOIntegrationTest.sBench.getConf().setLong("test.io.skip.size", 1);
        DFSIOIntegrationTest.sBench.randomReadTest(fs);
        long execTime = (System.currentTimeMillis()) - tStart;
        DFSIOIntegrationTest.sBench.analyzeResult(fs, DFSIOIntegrationTest.TestType.TEST_TYPE_READ_SKIP, execTime);
    }

    @Test(timeout = 50000)
    public void readLargeSkip() throws Exception {
        FileSystem fs = org.apache.hadoop.fs.FileSystem.get(DFSIOIntegrationTest.sLocalAlluxioClusterUri, HadoopConfigurationUtils.mergeAlluxioConfiguration(DFSIOIntegrationTest.sBench.getConf(), ServerConfiguration.global()));
        long tStart = System.currentTimeMillis();
        DFSIOIntegrationTest.sBench.getConf().setLong("test.io.skip.size", 5000);
        DFSIOIntegrationTest.sBench.randomReadTest(fs);
        long execTime = (System.currentTimeMillis()) - tStart;
        DFSIOIntegrationTest.sBench.analyzeResult(fs, DFSIOIntegrationTest.TestType.TEST_TYPE_READ_SKIP, execTime);
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
    private abstract static class IOStatMapper extends AbstractIOMapper<Long> {
        protected CompressionCodec mCompressionCodec;

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
                mCompressionCodec = ReflectionUtils.newInstance(codec, getConf());
            }
        }

        // AbstractIOMapper
        @Override
        void collectStats(OutputCollector<Text, Text> output, String name, long execTime, Long objSize) throws IOException {
            long totalSize = objSize;
            float ioRateMbSec = (((float) (totalSize)) * 1000) / (execTime * (DFSIOIntegrationTest.MEGA));
            DFSIOIntegrationTest.LOG.info(("Number of bytes processed = " + totalSize));
            DFSIOIntegrationTest.LOG.info(("Exec time = " + execTime));
            DFSIOIntegrationTest.LOG.info(("IO rate = " + ioRateMbSec));
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
    public static class WriteMapper extends DFSIOIntegrationTest.IOStatMapper {
        public WriteMapper() {
            for (int i = 0; i < (mBufferSize); i++) {
                mBuffer[i] = ((byte) ('0' + (i % 50)));
            }
        }

        // AbstractIOMapper
        @Override
        public Closeable getIOStream(String name) throws IOException {
            // create file
            OutputStream out = mFS.create(new Path(DFSIOIntegrationTest.getDataDir(getConf()), name), true, mBufferSize);
            if ((mCompressionCodec) != null) {
                out = mCompressionCodec.createOutputStream(out);
            }
            DFSIOIntegrationTest.LOG.info(("out = " + (out.getClass().getName())));
            return out;
        }

        // AbstractIOMapper, totalSize is in bytes
        @Override
        public Long doIO(Reporter reporter, String name, long totalSize) throws IOException {
            OutputStream out = ((OutputStream) (this.mStream));
            // write to the file
            long nrRemaining;
            for (nrRemaining = totalSize; nrRemaining > 0; nrRemaining -= mBufferSize) {
                int curSize = ((mBufferSize) < nrRemaining) ? mBufferSize : ((int) (nrRemaining));
                out.write(mBuffer, 0, curSize);
                reporter.setStatus(((((((("writing " + name) + "@") + (totalSize - nrRemaining)) + "/") + totalSize) + " ::host = ") + (mHostname)));
            }
            return totalSize;
        }
    }

    /**
     * Append mapper class.
     */
    public static class AppendMapper extends DFSIOIntegrationTest.IOStatMapper {
        public AppendMapper() {
            for (int i = 0; i < (mBufferSize); i++) {
                mBuffer[i] = ((byte) ('0' + (i % 50)));
            }
        }

        // AbstractIOMapper
        @Override
        public Closeable getIOStream(String name) throws IOException {
            // open file for append
            OutputStream out = mFS.append(new Path(DFSIOIntegrationTest.getDataDir(getConf()), name), mBufferSize);
            if ((mCompressionCodec) != null) {
                out = mCompressionCodec.createOutputStream(out);
            }
            DFSIOIntegrationTest.LOG.info(("out = " + (out.getClass().getName())));
            return out;
        }

        // AbstractIOMapper, totalSize is in Bytes
        @Override
        public Long doIO(Reporter reporter, String name, long totalSize) throws IOException {
            OutputStream out = ((OutputStream) (this.mStream));
            // write to the file
            long nrRemaining;
            for (nrRemaining = totalSize; nrRemaining > 0; nrRemaining -= mBufferSize) {
                int curSize = ((mBufferSize) < nrRemaining) ? mBufferSize : ((int) (nrRemaining));
                out.write(mBuffer, 0, curSize);
                reporter.setStatus(((((((("writing " + name) + "@") + (totalSize - nrRemaining)) + "/") + totalSize) + " ::host = ") + (mHostname)));
            }
            return totalSize;
        }
    }

    /**
     * Read mapper class.
     */
    public static class ReadMapper extends DFSIOIntegrationTest.IOStatMapper {
        public ReadMapper() {
        }

        // AbstractIOMapper
        @Override
        public Closeable getIOStream(String name) throws IOException {
            // open file
            InputStream in = mFS.open(new Path(DFSIOIntegrationTest.getDataDir(getConf()), name));
            if ((mCompressionCodec) != null) {
                in = mCompressionCodec.createInputStream(in);
            }
            DFSIOIntegrationTest.LOG.info(("in = " + (in.getClass().getName())));
            return in;
        }

        // AbstractIOMapper, totalSize in Bytes
        @Override
        public Long doIO(Reporter reporter, String name, long totalSize) throws IOException {
            InputStream in = ((InputStream) (this.mStream));
            long actualSize = 0;
            while (actualSize < totalSize) {
                int curSize = in.read(mBuffer, 0, mBufferSize);
                if (curSize < 0) {
                    break;
                }
                actualSize += curSize;
                reporter.setStatus(((((((("reading " + name) + "@") + actualSize) + "/") + totalSize) + " ::host = ") + (mHostname)));
            } 
            return actualSize;
        }
    }

    /**
     * Mapper class for random reads. The mapper chooses a position in the file and reads bufferSize
     * bytes starting at the chosen position. It stops after reading the totalSize bytes, specified
     * by size.
     *
     * There are three type of reads. 1) Random read always chooses a random position to read from:
     * skipSize = 0 2) Backward read reads file in reverse order : skipSize &lt; 0 3) Skip-read skips
     * skipSize bytes after every read : skipSize &gt; 0
     */
    public static class RandomReadMapper extends DFSIOIntegrationTest.IOStatMapper {
        private Random mRnd;

        private long mFileSize;

        private long mSkipSize;

        // Mapper
        @Override
        public void configure(JobConf conf) {
            super.configure(conf);
            mSkipSize = conf.getLong("test.io.skip.size", 0);
        }

        public RandomReadMapper() {
            mRnd = new Random();
        }

        // AbstractIOMapper
        @Override
        public Closeable getIOStream(String name) throws IOException {
            Path filePath = new Path(DFSIOIntegrationTest.getDataDir(getConf()), name);
            mFileSize = mFS.getFileStatus(filePath).getLen();
            InputStream in = mFS.open(filePath);
            if ((mCompressionCodec) != null) {
                in = new org.apache.hadoop.fs.FSDataInputStream(mCompressionCodec.createInputStream(in));
            }
            DFSIOIntegrationTest.LOG.info(("in = " + (in.getClass().getName())));
            DFSIOIntegrationTest.LOG.info(("skipSize = " + (mSkipSize)));
            return in;
        }

        // AbstractIOMapper, totalSize in Bytes
        @Override
        public Long doIO(Reporter reporter, String name, long totalSize) throws IOException {
            PositionedReadable in = ((PositionedReadable) (this.mStream));
            long actualSize = 0;
            for (long pos = nextOffset((-1)); actualSize < totalSize; pos = nextOffset(pos)) {
                int curSize = in.read(pos, mBuffer, 0, mBufferSize);
                if (curSize < 0) {
                    break;
                }
                actualSize += curSize;
                reporter.setStatus(((((((("reading " + name) + "@") + actualSize) + "/") + totalSize) + " ::host = ") + (mHostname)));
            }
            return actualSize;
        }

        /**
         * Get next offset for reading. If current < 0 then choose initial offset according to the read
         * type.
         *
         * @param current
         * 		offset
         * @return the next offset for reading
         */
        private long nextOffset(long current) {
            if ((mSkipSize) == 0) {
                return mRnd.nextInt(((int) (mFileSize)));
            }
            if ((mSkipSize) > 0) {
                return current < 0 ? 0 : (current + (mBufferSize)) + (mSkipSize);
            }
            // skipSize < 0
            return current < 0 ? Math.max(0, ((mFileSize) - (mBufferSize))) : Math.max(0, (current + (mSkipSize)));
        }
    }
}

