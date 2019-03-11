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
package org.apache.hadoop.mapreduce.split;


import SystemErasureCodingPolicies.RS_10_4_POLICY_ID;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.SystemErasureCodingPolicies;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapreduce.org.apache.hadoop.mapred.InputSplit;
import org.junit.Test;


/**
 * Tests that maxBlockLocations default value is sufficient for RS-10-4.
 */
public class TestJobSplitWriterWithEC {
    // This will ensure 14 block locations
    private ErasureCodingPolicy ecPolicy = SystemErasureCodingPolicies.getByID(RS_10_4_POLICY_ID);

    private static final int BLOCKSIZE = (1024 * 1024) * 10;

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    private Configuration conf;

    private Path submitDir;

    private Path testFile;

    @Test
    public void testMaxBlockLocationsNewSplitsWithErasureCoding() throws Exception {
        Job job = Job.getInstance(conf);
        final FileInputFormat<?, ?> fileInputFormat = new TextInputFormat();
        final List<InputSplit> splits = fileInputFormat.getSplits(job);
        JobSplitWriter.createSplitFiles(submitDir, conf, fs, splits);
        validateSplitMetaInfo();
    }

    @Test
    public void testMaxBlockLocationsOldSplitsWithErasureCoding() throws Exception {
        JobConf jobConf = new JobConf(conf);
        org.apache.hadoop.mapred.TextInputFormat fileInputFormat = new org.apache.hadoop.mapred.TextInputFormat();
        fileInputFormat.configure(jobConf);
        final org.apache.hadoop.mapred.InputSplit[] splits = fileInputFormat.getSplits(jobConf, 1);
        JobSplitWriter.createSplitFiles(submitDir, conf, fs, splits);
        validateSplitMetaInfo();
    }
}

