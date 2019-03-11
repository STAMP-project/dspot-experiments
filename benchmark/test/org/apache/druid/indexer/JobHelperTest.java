/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.indexer;


import RetryPolicies.TRY_ONCE_THEN_FAIL;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.apache.druid.java.util.common.CompressionUtils;
import org.apache.druid.java.util.common.ISE;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.partition.NoneShardSpec;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Progressable;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 */
public class JobHelperTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private HadoopDruidIndexerConfig config;

    private File tmpDir;

    private File dataFile;

    private Interval interval = Intervals.of("2014-10-22T00:00:00Z/P1D");

    @Test
    public void testEnsurePathsAddsProperties() {
        JobHelperTest.HadoopDruidIndexerConfigSpy hadoopDruidIndexerConfigSpy = new JobHelperTest.HadoopDruidIndexerConfigSpy(config);
        JobHelper.ensurePaths(hadoopDruidIndexerConfigSpy);
        Map<String, String> jobProperties = hadoopDruidIndexerConfigSpy.getJobProperties();
        Assert.assertEquals("fs.s3.impl property set correctly", "org.apache.hadoop.fs.s3native.NativeS3FileSystem", jobProperties.get("fs.s3.impl"));
        Assert.assertEquals("fs.s3.accessKeyId property set correctly", "THISISMYACCESSKEY", jobProperties.get("fs.s3.awsAccessKeyId"));
    }

    @Test
    public void testGoogleGetURIFromSegment() throws URISyntaxException {
        DataSegment segment = new DataSegment("test1", Intervals.of("2000/3000"), "ver", ImmutableMap.of("type", "google", "bucket", "test-test", "path", "tmp/foo:bar/index1.zip"), ImmutableList.of(), ImmutableList.of(), NoneShardSpec.instance(), 9, 1024);
        Assert.assertEquals(new URI("gs://test-test/tmp/foo%3Abar/index1.zip"), JobHelper.getURIFromSegment(segment));
    }

    @Test
    public void testEvilZip() throws IOException {
        final File tmpDir = temporaryFolder.newFolder("testEvilZip");
        final File evilResult = new File("/tmp/evil.txt");
        Files.deleteIfExists(evilResult.toPath());
        File evilZip = new File(tmpDir, "evil.zip");
        Files.deleteIfExists(evilZip.toPath());
        CompressionUtils.makeEvilZip(evilZip);
        try {
            JobHelper.unzipNoGuava(new Path(evilZip.getCanonicalPath()), new Configuration(), tmpDir, new Progressable() {
                @Override
                public void progress() {
                }
            }, TRY_ONCE_THEN_FAIL);
        } catch (ISE ise) {
            Assert.assertTrue(ise.getMessage().contains("does not start with outDir"));
            Assert.assertFalse("Zip exploit triggered, /tmp/evil.txt was written.", evilResult.exists());
            return;
        }
        Assert.fail("Exception was not thrown for malicious zip file");
    }

    private static class HadoopDruidIndexerConfigSpy extends HadoopDruidIndexerConfig {
        private Map<String, String> jobProperties = new HashMap<String, String>();

        public HadoopDruidIndexerConfigSpy(HadoopDruidIndexerConfig delegate) {
            super(delegate.getSchema());
        }

        @Override
        public Job addInputPaths(Job job) {
            Configuration configuration = job.getConfiguration();
            for (Map.Entry<String, String> en : configuration) {
                jobProperties.put(en.getKey(), en.getValue());
            }
            return job;
        }

        public Map<String, String> getJobProperties() {
            return jobProperties;
        }
    }
}

