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
package org.apache.hadoop.mapred.nativetask.compresstest;


import MRJobConfig.MAP_OUTPUT_COMPRESS_CODEC;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.nativetask.testutil.ResultVerifier;
import org.apache.hadoop.mapred.nativetask.testutil.ScenarioConfiguration;
import org.apache.hadoop.mapred.nativetask.testutil.TestConstants;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Assert;
import org.junit.Test;


public class CompressTest {
    private static final Configuration nativeConf = ScenarioConfiguration.getNativeConfiguration();

    private static final Configuration hadoopConf = ScenarioConfiguration.getNormalConfiguration();

    static {
        CompressTest.nativeConf.addResource(TestConstants.COMPRESS_TEST_CONF_PATH);
        CompressTest.hadoopConf.addResource(TestConstants.COMPRESS_TEST_CONF_PATH);
    }

    @Test
    public void testSnappyCompress() throws Exception {
        final String snappyCodec = "org.apache.hadoop.io.compress.SnappyCodec";
        CompressTest.nativeConf.set(MAP_OUTPUT_COMPRESS_CODEC, snappyCodec);
        final String nativeOutputPath = (TestConstants.NATIVETASK_COMPRESS_TEST_NATIVE_OUTPUTDIR) + "/snappy";
        final Job job = CompressMapper.getCompressJob("nativesnappy", CompressTest.nativeConf, TestConstants.NATIVETASK_COMPRESS_TEST_INPUTDIR, nativeOutputPath);
        Assert.assertTrue(job.waitForCompletion(true));
        CompressTest.hadoopConf.set(MAP_OUTPUT_COMPRESS_CODEC, snappyCodec);
        final String hadoopOutputPath = (TestConstants.NATIVETASK_COMPRESS_TEST_NORMAL_OUTPUTDIR) + "/snappy";
        final Job hadoopjob = CompressMapper.getCompressJob("hadoopsnappy", CompressTest.hadoopConf, TestConstants.NATIVETASK_COMPRESS_TEST_INPUTDIR, hadoopOutputPath);
        Assert.assertTrue(hadoopjob.waitForCompletion(true));
        final boolean compareRet = ResultVerifier.verify(nativeOutputPath, hadoopOutputPath);
        Assert.assertEquals("file compare result: if they are the same ,then return true", true, compareRet);
        ResultVerifier.verifyCounters(hadoopjob, job);
    }

    @Test
    public void testGzipCompress() throws Exception {
        final String gzipCodec = "org.apache.hadoop.io.compress.GzipCodec";
        CompressTest.nativeConf.set(MAP_OUTPUT_COMPRESS_CODEC, gzipCodec);
        final String nativeOutputPath = (TestConstants.NATIVETASK_COMPRESS_TEST_NATIVE_OUTPUTDIR) + "/gzip";
        final Job job = CompressMapper.getCompressJob("nativegzip", CompressTest.nativeConf, TestConstants.NATIVETASK_COMPRESS_TEST_INPUTDIR, nativeOutputPath);
        Assert.assertTrue(job.waitForCompletion(true));
        CompressTest.hadoopConf.set(MAP_OUTPUT_COMPRESS_CODEC, gzipCodec);
        final String hadoopOutputPath = (TestConstants.NATIVETASK_COMPRESS_TEST_NORMAL_OUTPUTDIR) + "/gzip";
        final Job hadoopjob = CompressMapper.getCompressJob("hadoopgzip", CompressTest.hadoopConf, TestConstants.NATIVETASK_COMPRESS_TEST_INPUTDIR, hadoopOutputPath);
        Assert.assertTrue(hadoopjob.waitForCompletion(true));
        final boolean compareRet = ResultVerifier.verify(nativeOutputPath, hadoopOutputPath);
        Assert.assertEquals("file compare result: if they are the same ,then return true", true, compareRet);
        ResultVerifier.verifyCounters(hadoopjob, job);
    }

    @Test
    public void testLz4Compress() throws Exception {
        final String lz4Codec = "org.apache.hadoop.io.compress.Lz4Codec";
        CompressTest.nativeConf.set(MAP_OUTPUT_COMPRESS_CODEC, lz4Codec);
        final String nativeOutputPath = (TestConstants.NATIVETASK_COMPRESS_TEST_NATIVE_OUTPUTDIR) + "/lz4";
        final Job nativeJob = CompressMapper.getCompressJob("nativelz4", CompressTest.nativeConf, TestConstants.NATIVETASK_COMPRESS_TEST_INPUTDIR, nativeOutputPath);
        Assert.assertTrue(nativeJob.waitForCompletion(true));
        CompressTest.hadoopConf.set(MAP_OUTPUT_COMPRESS_CODEC, lz4Codec);
        final String hadoopOutputPath = (TestConstants.NATIVETASK_COMPRESS_TEST_NORMAL_OUTPUTDIR) + "/lz4";
        final Job hadoopJob = CompressMapper.getCompressJob("hadooplz4", CompressTest.hadoopConf, TestConstants.NATIVETASK_COMPRESS_TEST_INPUTDIR, hadoopOutputPath);
        Assert.assertTrue(hadoopJob.waitForCompletion(true));
        final boolean compareRet = ResultVerifier.verify(nativeOutputPath, hadoopOutputPath);
        Assert.assertEquals("file compare result: if they are the same ,then return true", true, compareRet);
        ResultVerifier.verifyCounters(hadoopJob, nativeJob);
    }
}

