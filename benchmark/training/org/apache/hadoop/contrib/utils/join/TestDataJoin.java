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
package org.apache.hadoop.contrib.utils.join;


import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.Text;
import org.junit.Test;


/**
 * Class to test JOIN between 2 data
 * sources.
 */
public class TestDataJoin {
    private static MiniDFSCluster cluster = null;

    @Test
    public void testDataJoin() throws Exception {
        final int srcs = 4;
        JobConf job = new JobConf();
        job.setBoolean("mapreduce.fileoutputcommitter.marksuccessfuljobs", false);
        Path base = TestDataJoin.cluster.getFileSystem().makeQualified(new Path("/inner"));
        Path[] src = TestDataJoin.writeSimpleSrc(base, job, srcs);
        job.setInputFormat(SequenceFileInputFormat.class);
        Path outdir = new Path(base, "out");
        FileOutputFormat.setOutputPath(job, outdir);
        job.setMapperClass(SampleDataJoinMapper.class);
        job.setReducerClass(SampleDataJoinReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(SampleTaggedMapOutput.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setOutputFormat(TextOutputFormat.class);
        job.setNumMapTasks(1);
        job.setNumReduceTasks(1);
        FileInputFormat.setInputPaths(job, src);
        try {
            JobClient.runJob(job);
            TestDataJoin.confirmOutput(outdir, job, srcs);
        } finally {
            base.getFileSystem(job).delete(base, true);
        }
    }
}

