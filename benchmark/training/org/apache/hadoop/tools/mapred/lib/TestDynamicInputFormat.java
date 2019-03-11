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
package org.apache.hadoop.tools.mapred.lib;


import DistCpConstants.CONF_LABEL_LISTING_FILE_PATH;
import DistCpConstants.CONF_LABEL_MAX_CHUNKS_IDEAL;
import DistCpConstants.CONF_LABEL_MAX_CHUNKS_TOLERABLE;
import DistCpConstants.CONF_LABEL_MIN_RECORDS_PER_CHUNK;
import DistCpConstants.CONF_LABEL_SPLIT_RATIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.tools.CopyListing;
import org.apache.hadoop.tools.CopyListingFileStatus;
import org.apache.hadoop.tools.DistCpContext;
import org.apache.hadoop.tools.StubContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDynamicInputFormat {
    private static final Logger LOG = LoggerFactory.getLogger(TestDynamicInputFormat.class);

    private static MiniDFSCluster cluster;

    private static final int N_FILES = 1000;

    private static final int NUM_SPLITS = 7;

    private static final Credentials CREDENTIALS = new Credentials();

    private static List<String> expectedFilePaths = new ArrayList<String>(TestDynamicInputFormat.N_FILES);

    @Test
    public void testGetSplits() throws Exception {
        final DistCpContext context = new DistCpContext(TestDynamicInputFormat.getOptions());
        Configuration configuration = new Configuration();
        configuration.set("mapred.map.tasks", String.valueOf(context.getMaxMaps()));
        CopyListing.getCopyListing(configuration, TestDynamicInputFormat.CREDENTIALS, context).buildListing(new org.apache.hadoop.fs.Path(((TestDynamicInputFormat.cluster.getFileSystem().getUri().toString()) + "/tmp/testDynInputFormat/fileList.seq")), context);
        JobContext jobContext = new org.apache.hadoop.mapreduce.task.JobContextImpl(configuration, new JobID());
        DynamicInputFormat<Text, CopyListingFileStatus> inputFormat = new DynamicInputFormat<Text, CopyListingFileStatus>();
        List<InputSplit> splits = inputFormat.getSplits(jobContext);
        int nFiles = 0;
        int taskId = 0;
        for (InputSplit split : splits) {
            StubContext stubContext = new StubContext(jobContext.getConfiguration(), null, taskId);
            final TaskAttemptContext taskAttemptContext = getContext();
            RecordReader<Text, CopyListingFileStatus> recordReader = inputFormat.createRecordReader(split, taskAttemptContext);
            stubContext.setReader(recordReader);
            recordReader.initialize(splits.get(0), taskAttemptContext);
            float previousProgressValue = 0.0F;
            while (recordReader.nextKeyValue()) {
                CopyListingFileStatus fileStatus = recordReader.getCurrentValue();
                String source = fileStatus.getPath().toString();
                System.out.println(source);
                Assert.assertTrue(TestDynamicInputFormat.expectedFilePaths.contains(source));
                final float progress = recordReader.getProgress();
                Assert.assertTrue((progress >= previousProgressValue));
                Assert.assertTrue((progress >= 0.0F));
                Assert.assertTrue((progress <= 1.0F));
                previousProgressValue = progress;
                ++nFiles;
            } 
            Assert.assertTrue(((recordReader.getProgress()) == 1.0F));
            ++taskId;
        }
        Assert.assertEquals(TestDynamicInputFormat.expectedFilePaths.size(), nFiles);
    }

    @Test
    public void testGetSplitRatio() throws Exception {
        Assert.assertEquals(1, DynamicInputFormat.getSplitRatio(1, 1000000000));
        Assert.assertEquals(2, DynamicInputFormat.getSplitRatio(11000000, 10));
        Assert.assertEquals(4, DynamicInputFormat.getSplitRatio(30, 700));
        Assert.assertEquals(2, DynamicInputFormat.getSplitRatio(30, 200));
        // Tests with negative value configuration
        Configuration conf = new Configuration();
        conf.setInt(CONF_LABEL_MAX_CHUNKS_TOLERABLE, (-1));
        conf.setInt(CONF_LABEL_MAX_CHUNKS_IDEAL, (-1));
        conf.setInt(CONF_LABEL_MIN_RECORDS_PER_CHUNK, (-1));
        conf.setInt(CONF_LABEL_SPLIT_RATIO, (-1));
        Assert.assertEquals(1, DynamicInputFormat.getSplitRatio(1, 1000000000, conf));
        Assert.assertEquals(2, DynamicInputFormat.getSplitRatio(11000000, 10, conf));
        Assert.assertEquals(4, DynamicInputFormat.getSplitRatio(30, 700, conf));
        Assert.assertEquals(2, DynamicInputFormat.getSplitRatio(30, 200, conf));
        // Tests with valid configuration
        conf.setInt(CONF_LABEL_MAX_CHUNKS_TOLERABLE, 100);
        conf.setInt(CONF_LABEL_MAX_CHUNKS_IDEAL, 30);
        conf.setInt(CONF_LABEL_MIN_RECORDS_PER_CHUNK, 10);
        conf.setInt(CONF_LABEL_SPLIT_RATIO, 53);
        Assert.assertEquals(53, DynamicInputFormat.getSplitRatio(3, 200, conf));
    }

    @Test
    public void testDynamicInputChunkContext() throws IOException {
        Configuration configuration = new Configuration();
        configuration.set(CONF_LABEL_LISTING_FILE_PATH, "/tmp/test/file1.seq");
        DynamicInputFormat firstInputFormat = new DynamicInputFormat();
        DynamicInputFormat secondInputFormat = new DynamicInputFormat();
        DynamicInputChunkContext firstContext = firstInputFormat.getChunkContext(configuration);
        DynamicInputChunkContext secondContext = firstInputFormat.getChunkContext(configuration);
        DynamicInputChunkContext thirdContext = secondInputFormat.getChunkContext(configuration);
        DynamicInputChunkContext fourthContext = secondInputFormat.getChunkContext(configuration);
        Assert.assertTrue(("Chunk contexts from the same DynamicInputFormat " + "object should be the same."), firstContext.equals(secondContext));
        Assert.assertTrue(("Chunk contexts from the same DynamicInputFormat " + "object should be the same."), thirdContext.equals(fourthContext));
        Assert.assertTrue(("Contexts from different DynamicInputFormat " + "objects should be different."), (!(firstContext.equals(thirdContext))));
    }
}

