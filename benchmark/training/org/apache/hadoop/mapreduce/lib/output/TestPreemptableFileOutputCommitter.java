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
package org.apache.hadoop.mapreduce.lib.output;


import MRJobConfig.APPLICATION_ATTEMPT_ID;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.task.annotation.Checkpointable;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestPreemptableFileOutputCommitter {
    @Test
    public void testPartialOutputCleanup() throws FileNotFoundException, IOException, IllegalArgumentException {
        Configuration conf = new Configuration(false);
        conf.setInt(APPLICATION_ATTEMPT_ID, 1);
        TaskAttemptID tid0 = new TaskAttemptID("1363718006656", 1, TaskType.REDUCE, 14, 3);
        Path p = Mockito.spy(new Path("/user/hadoop/out"));
        Path a = new Path("hdfs://user/hadoop/out");
        Path p0 = new Path(a, "_temporary/1/attempt_1363718006656_0001_r_000014_0");
        Path p1 = new Path(a, "_temporary/1/attempt_1363718006656_0001_r_000014_1");
        Path p2 = new Path(a, "_temporary/1/attempt_1363718006656_0001_r_000013_0");
        // (p3 does not exist)
        Path p3 = new Path(a, "_temporary/1/attempt_1363718006656_0001_r_000014_2");
        FileStatus[] fsa = new FileStatus[3];
        fsa[0] = new FileStatus();
        fsa[0].setPath(p0);
        fsa[1] = new FileStatus();
        fsa[1].setPath(p1);
        fsa[2] = new FileStatus();
        fsa[2].setPath(p2);
        final FileSystem fs = Mockito.mock(FileSystem.class);
        Mockito.when(fs.exists(ArgumentMatchers.eq(p0))).thenReturn(true);
        Mockito.when(fs.exists(ArgumentMatchers.eq(p1))).thenReturn(true);
        Mockito.when(fs.exists(ArgumentMatchers.eq(p2))).thenReturn(true);
        Mockito.when(fs.exists(ArgumentMatchers.eq(p3))).thenReturn(false);
        Mockito.when(fs.delete(ArgumentMatchers.eq(p0), ArgumentMatchers.eq(true))).thenReturn(true);
        Mockito.when(fs.delete(ArgumentMatchers.eq(p1), ArgumentMatchers.eq(true))).thenReturn(true);
        Mockito.doReturn(fs).when(p).getFileSystem(ArgumentMatchers.any(Configuration.class));
        Mockito.when(fs.makeQualified(ArgumentMatchers.eq(p))).thenReturn(a);
        TaskAttemptContext context = Mockito.mock(TaskAttemptContext.class);
        Mockito.when(context.getTaskAttemptID()).thenReturn(tid0);
        Mockito.when(context.getConfiguration()).thenReturn(conf);
        PartialFileOutputCommitter foc = new TestPreemptableFileOutputCommitter.TestPFOC(p, context, fs);
        foc.cleanUpPartialOutputForTask(context);
        Mockito.verify(fs).delete(ArgumentMatchers.eq(p0), ArgumentMatchers.eq(true));
        Mockito.verify(fs).delete(ArgumentMatchers.eq(p1), ArgumentMatchers.eq(true));
        Mockito.verify(fs, Mockito.times(1)).delete(ArgumentMatchers.eq(p3), ArgumentMatchers.eq(true));
        Mockito.verify(fs, Mockito.never()).delete(ArgumentMatchers.eq(p2), ArgumentMatchers.eq(true));
    }

    @Checkpointable
    static class TestPFOC extends PartialFileOutputCommitter {
        final FileSystem fs;

        TestPFOC(Path outputPath, TaskAttemptContext ctxt, FileSystem fs) throws IOException {
            super(outputPath, ctxt);
            this.fs = fs;
        }

        @Override
        FileSystem fsFor(Path p, Configuration conf) {
            return fs;
        }
    }
}

