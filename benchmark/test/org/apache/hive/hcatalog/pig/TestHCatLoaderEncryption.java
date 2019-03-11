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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.pig;


import HadoopShims.HdfsEncryptionShim;
import HadoopShims.MiniDFSShim;
import Warehouse.DEFAULT_DATABASE_NAME;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hive.hcatalog.common.HCatUtil;
import org.apache.hive.hcatalog.data.HCatRecord;
import org.apache.hive.hcatalog.data.Pair;
import org.apache.hive.hcatalog.mapreduce.HCatBaseTest;
import org.apache.hive.hcatalog.mapreduce.HCatInputFormat;
import org.apache.pig.PigServer;
import org.apache.pig.data.Tuple;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class TestHCatLoaderEncryption {
    private static final AtomicInteger salt = new AtomicInteger(new Random().nextInt());

    private static final Logger LOG = LoggerFactory.getLogger(TestHCatLoaderEncryption.class);

    private final String TEST_DATA_DIR = HCatUtil.makePathASafeFileName((((((((System.getProperty("java.io.tmpdir")) + (File.separator)) + (TestHCatLoaderEncryption.class.getCanonicalName())) + "-") + (System.currentTimeMillis())) + "_") + (TestHCatLoaderEncryption.salt.getAndIncrement())));

    private final String TEST_WAREHOUSE_DIR = (TEST_DATA_DIR) + "/warehouse";

    private final String BASIC_FILE_NAME = (TEST_DATA_DIR) + "/basic.input.data";

    private static final String BASIC_TABLE = "junit_unparted_basic";

    private static final String ENCRYPTED_TABLE = "encrypted_table";

    private static final String SECURITY_KEY_PROVIDER_URI_NAME = "dfs.encryption.key.provider.uri";

    private MiniDFSShim dfs = null;

    private HdfsEncryptionShim hes = null;

    private final String[] testOnlyCommands = new String[]{ "crypto" };

    private IDriver driver;

    private Map<Integer, Pair<Integer, String>> basicInputData;

    private static List<HCatRecord> readRecords = new ArrayList<HCatRecord>();

    private static final Map<String, Set<String>> DISABLED_STORAGE_FORMATS = new HashMap<String, Set<String>>();

    private String storageFormat;

    public TestHCatLoaderEncryption(String storageFormat) {
        this.storageFormat = storageFormat;
    }

    @Test
    public void testReadDataFromEncryptedHiveTableByPig() throws IOException {
        Assume.assumeTrue((!(TestUtil.shouldSkip(storageFormat, TestHCatLoaderEncryption.DISABLED_STORAGE_FORMATS))));
        PigServer server = HCatBaseTest.createPigServer(false);
        server.registerQuery((("X = load '" + (TestHCatLoaderEncryption.ENCRYPTED_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
        Iterator<Tuple> XIter = server.openIterator("X");
        int numTuplesRead = 0;
        while (XIter.hasNext()) {
            Tuple t = XIter.next();
            Assert.assertEquals(2, t.size());
            Assert.assertNotNull(t.get(0));
            Assert.assertNotNull(t.get(1));
            Assert.assertTrue(((t.get(0).getClass()) == (Integer.class)));
            Assert.assertTrue(((t.get(1).getClass()) == (String.class)));
            Assert.assertEquals(t.get(0), basicInputData.get(numTuplesRead).first);
            Assert.assertEquals(t.get(1), basicInputData.get(numTuplesRead).second);
            numTuplesRead++;
        } 
        Assert.assertEquals(("failed with storage format: " + (this.storageFormat)), basicInputData.size(), numTuplesRead);
    }

    @Test
    public void testReadDataFromEncryptedHiveTableByHCatMR() throws Exception {
        Assume.assumeTrue((!(TestUtil.shouldSkip(storageFormat, TestHCatLoaderEncryption.DISABLED_STORAGE_FORMATS))));
        TestHCatLoaderEncryption.readRecords.clear();
        Configuration conf = new Configuration();
        Job job = new Job(conf, "hcat mapreduce read encryption test");
        job.setJarByClass(this.getClass());
        job.setMapperClass(TestHCatLoaderEncryption.MapRead.class);
        // input/output settings
        job.setInputFormatClass(HCatInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        HCatInputFormat.setInput(job, DEFAULT_DATABASE_NAME, TestHCatLoaderEncryption.ENCRYPTED_TABLE, null);
        job.setMapOutputKeyClass(BytesWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setNumReduceTasks(0);
        FileSystem fs = new LocalFileSystem();
        String pathLoc = (TEST_DATA_DIR) + "/testHCatMREncryptionOutput";
        Path path = new Path(pathLoc);
        if (fs.exists(path)) {
            fs.delete(path, true);
        }
        TextOutputFormat.setOutputPath(job, new Path(pathLoc));
        job.waitForCompletion(true);
        int numTuplesRead = 0;
        for (HCatRecord hCatRecord : TestHCatLoaderEncryption.readRecords) {
            Assert.assertEquals(2, hCatRecord.size());
            Assert.assertNotNull(hCatRecord.get(0));
            Assert.assertNotNull(hCatRecord.get(1));
            Assert.assertTrue(((hCatRecord.get(0).getClass()) == (Integer.class)));
            Assert.assertTrue(((hCatRecord.get(1).getClass()) == (String.class)));
            Assert.assertEquals(hCatRecord.get(0), basicInputData.get(numTuplesRead).first);
            Assert.assertEquals(hCatRecord.get(1), basicInputData.get(numTuplesRead).second);
            numTuplesRead++;
        }
        Assert.assertEquals(("failed HCat MR read with storage format: " + (this.storageFormat)), basicInputData.size(), numTuplesRead);
    }

    public static class MapRead extends Mapper<WritableComparable, HCatRecord, BytesWritable, Text> {
        @Override
        public void map(WritableComparable key, HCatRecord value, Context context) throws IOException, InterruptedException {
            try {
                TestHCatLoaderEncryption.readRecords.add(value);
            } catch (Exception e) {
                TestHCatLoaderEncryption.LOG.error("error when read record.", e);
                throw new IOException(e);
            }
        }
    }
}

