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
package org.apache.hadoop.mapred;


import JobConf.MAPRED_REDUCE_TASK_JAVA_OPTS;
import JobContext.IO_SORT_FACTOR;
import JobContext.REDUCE_INPUT_BUFFER_PERCENT;
import JobContext.REDUCE_MEMORY_TOTAL_BYTES;
import JobContext.REDUCE_MERGE_INMEM_THRESHOLD;
import JobContext.SHUFFLE_INPUT_BUFFER_PERCENT;
import TaskCounter.MAP_OUTPUT_RECORDS;
import TaskCounter.SPILLED_RECORDS;
import org.junit.Assert;
import org.junit.Test;


public class TestReduceFetch extends TestReduceFetchFromPartialMem {
    /**
     * Verify that all segments are read from disk
     *
     * @throws Exception
     * 		might be thrown
     */
    @Test
    public void testReduceFromDisk() throws Exception {
        final int MAP_TASKS = 8;
        JobConf job = TestReduceFetchFromPartialMem.mrCluster.createJobConf();
        job.set(REDUCE_INPUT_BUFFER_PERCENT, "0.0");
        job.setNumMapTasks(MAP_TASKS);
        job.set(MAPRED_REDUCE_TASK_JAVA_OPTS, "-Xmx128m");
        job.setLong(REDUCE_MEMORY_TOTAL_BYTES, (128 << 20));
        job.set(SHUFFLE_INPUT_BUFFER_PERCENT, "0.05");
        job.setInt(IO_SORT_FACTOR, 2);
        job.setInt(REDUCE_MERGE_INMEM_THRESHOLD, 4);
        Counters c = TestReduceFetchFromPartialMem.runJob(job);
        final long spill = c.findCounter(SPILLED_RECORDS).getCounter();
        final long out = c.findCounter(MAP_OUTPUT_RECORDS).getCounter();
        Assert.assertTrue((("Expected all records spilled during reduce (" + spill) + ")"), (spill >= (2 * out)));// all records spill at map, reduce

        Assert.assertTrue((("Expected intermediate merges (" + spill) + ")"), (spill >= ((2 * out) + (out / MAP_TASKS))));// some records hit twice

    }

    /**
     * Verify that no segment hits disk.
     *
     * @throws Exception
     * 		might be thrown
     */
    @Test
    public void testReduceFromMem() throws Exception {
        final int MAP_TASKS = 3;
        JobConf job = TestReduceFetchFromPartialMem.mrCluster.createJobConf();
        job.set(REDUCE_INPUT_BUFFER_PERCENT, "1.0");
        job.set(SHUFFLE_INPUT_BUFFER_PERCENT, "1.0");
        job.setLong(REDUCE_MEMORY_TOTAL_BYTES, (128 << 20));
        job.setNumMapTasks(MAP_TASKS);
        Counters c = TestReduceFetchFromPartialMem.runJob(job);
        final long spill = c.findCounter(SPILLED_RECORDS).getCounter();
        final long out = c.findCounter(MAP_OUTPUT_RECORDS).getCounter();
        Assert.assertEquals(("Spilled records: " + spill), out, spill);// no reduce spill

    }
}

