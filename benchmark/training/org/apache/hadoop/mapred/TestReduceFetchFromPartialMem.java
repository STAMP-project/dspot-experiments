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
import JobContext.IO_SORT_MB;
import JobContext.REDUCE_INPUT_BUFFER_PERCENT;
import JobContext.REDUCE_MEMORY_TOTAL_BYTES;
import JobContext.REDUCE_MERGE_INMEM_THRESHOLD;
import JobContext.SHUFFLE_INPUT_BUFFER_PERCENT;
import JobContext.SHUFFLE_MERGE_PERCENT;
import JobContext.SHUFFLE_PARALLEL_COPIES;
import JobContext.TASK_PARTITION;
import TaskCounter.MAP_OUTPUT_RECORDS;
import TaskCounter.SPILLED_RECORDS;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Iterator;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparator;
import org.junit.Assert;
import org.junit.Test;


public class TestReduceFetchFromPartialMem {
    protected static MiniMRCluster mrCluster = null;

    protected static MiniDFSCluster dfsCluster = null;

    private static final String tagfmt = "%04d";

    private static final String keyfmt = "KEYKEYKEYKEYKEYKEYKE";

    private static final int keylen = TestReduceFetchFromPartialMem.keyfmt.length();

    /**
     * Verify that at least one segment does not hit disk
     */
    @Test
    public void testReduceFromPartialMem() throws Exception {
        final int MAP_TASKS = 7;
        JobConf job = TestReduceFetchFromPartialMem.mrCluster.createJobConf();
        job.setNumMapTasks(MAP_TASKS);
        job.setInt(REDUCE_MERGE_INMEM_THRESHOLD, 0);
        job.set(REDUCE_INPUT_BUFFER_PERCENT, "1.0");
        job.setInt(SHUFFLE_PARALLEL_COPIES, 1);
        job.setInt(IO_SORT_MB, 10);
        job.set(MAPRED_REDUCE_TASK_JAVA_OPTS, "-Xmx128m");
        job.setLong(REDUCE_MEMORY_TOTAL_BYTES, (128 << 20));
        job.set(SHUFFLE_INPUT_BUFFER_PERCENT, "0.14");
        job.set(SHUFFLE_MERGE_PERCENT, "1.0");
        Counters c = TestReduceFetchFromPartialMem.runJob(job);
        final long out = c.findCounter(MAP_OUTPUT_RECORDS).getCounter();
        final long spill = c.findCounter(SPILLED_RECORDS).getCounter();
        Assert.assertTrue((("Expected some records not spilled during reduce" + spill) + ")"), (spill < (2 * out)));// spilled map records, some records at the reduce

    }

    /**
     * Emit 4096 small keys, 2 &quot;tagged&quot; keys. Emits a fixed amount of
     * data so the in-memory fetch semantics can be tested.
     */
    public static class MapMB implements Mapper<NullWritable, NullWritable, Text, Text> {
        private int id;

        private int nMaps;

        private final Text key = new Text();

        private final Text val = new Text();

        private final byte[] b = new byte[4096];

        private final Formatter fmt = new Formatter(new StringBuilder(25));

        @Override
        public void configure(JobConf conf) {
            nMaps = conf.getNumMapTasks();
            id = ((nMaps) - (conf.getInt(TASK_PARTITION, (-1)))) - 1;
            Arrays.fill(b, 0, 4096, ((byte) ('V')));
            ((StringBuilder) (fmt.out())).append(TestReduceFetchFromPartialMem.keyfmt);
        }

        @Override
        public void map(NullWritable nk, NullWritable nv, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
            // Emit 4096 fixed-size records
            val.set(b, 0, 1000);
            val.getBytes()[0] = ((byte) (id));
            for (int i = 0; i < 4096; ++i) {
                key.set(fmt.format(TestReduceFetchFromPartialMem.tagfmt, i).toString());
                output.collect(key, val);
                ((StringBuilder) (fmt.out())).setLength(TestReduceFetchFromPartialMem.keylen);
            }
            // Emit two "tagged" records from the map. To validate the merge, segments
            // should have both a small and large record such that reading a large
            // record from an on-disk segment into an in-memory segment will write
            // over the beginning of a record in the in-memory segment, causing the
            // merge and/or validation to fail.
            // Add small, tagged record
            val.set(b, 0, ((TestReduceFetchFromPartialMem.getValLen(id, nMaps)) - 128));
            val.getBytes()[0] = ((byte) (id));
            ((StringBuilder) (fmt.out())).setLength(TestReduceFetchFromPartialMem.keylen);
            key.set(("A" + (fmt.format(TestReduceFetchFromPartialMem.tagfmt, id).toString())));
            output.collect(key, val);
            // Add large, tagged record
            val.set(b, 0, TestReduceFetchFromPartialMem.getValLen(id, nMaps));
            val.getBytes()[0] = ((byte) (id));
            ((StringBuilder) (fmt.out())).setLength(TestReduceFetchFromPartialMem.keylen);
            key.set(("B" + (fmt.format(TestReduceFetchFromPartialMem.tagfmt, id).toString())));
            output.collect(key, val);
        }

        @Override
        public void close() throws IOException {
        }
    }

    /**
     * Confirm that each small key is emitted once by all maps, each tagged key
     * is emitted by only one map, all IDs are consistent with record data, and
     * all non-ID record data is consistent.
     */
    public static class MBValidate implements Reducer<Text, Text, Text, Text> {
        private static int nMaps;

        private static final Text vb = new Text();

        static {
            byte[] v = new byte[4096];
            Arrays.fill(v, ((byte) ('V')));
            TestReduceFetchFromPartialMem.MBValidate.vb.set(v);
        }

        private int nRec = 0;

        private int nKey = -1;

        private int aKey = -1;

        private int bKey = -1;

        private final Text kb = new Text();

        private final Formatter fmt = new Formatter(new StringBuilder(25));

        @Override
        public void configure(JobConf conf) {
            TestReduceFetchFromPartialMem.MBValidate.nMaps = conf.getNumMapTasks();
            ((StringBuilder) (fmt.out())).append(TestReduceFetchFromPartialMem.keyfmt);
        }

        @Override
        public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> out, Reporter reporter) throws IOException {
            int vc = 0;
            final int vlen;
            final int preRec = nRec;
            final int vcCheck;
            final int recCheck;
            ((StringBuilder) (fmt.out())).setLength(TestReduceFetchFromPartialMem.keylen);
            if (25 == (key.getLength())) {
                // tagged record
                recCheck = 1;// expect only 1 record

                switch (((char) (key.getBytes()[0]))) {
                    case 'A' :
                        vlen = (TestReduceFetchFromPartialMem.getValLen((++(aKey)), TestReduceFetchFromPartialMem.MBValidate.nMaps)) - 128;
                        vcCheck = aKey;// expect eq id

                        break;
                    case 'B' :
                        vlen = TestReduceFetchFromPartialMem.getValLen((++(bKey)), TestReduceFetchFromPartialMem.MBValidate.nMaps);
                        vcCheck = bKey;// expect eq id

                        break;
                    default :
                        vlen = vcCheck = -1;
                        Assert.fail(("Unexpected tag on record: " + ((char) (key.getBytes()[24]))));
                }
                kb.set((((char) (key.getBytes()[0])) + (fmt.format(TestReduceFetchFromPartialMem.tagfmt, vcCheck).toString())));
            } else {
                kb.set(fmt.format(TestReduceFetchFromPartialMem.tagfmt, (++(nKey))).toString());
                vlen = 1000;
                recCheck = TestReduceFetchFromPartialMem.MBValidate.nMaps;
                // expect 1 rec per map
                vcCheck = ((TestReduceFetchFromPartialMem.MBValidate.nMaps) * ((TestReduceFetchFromPartialMem.MBValidate.nMaps) - 1)) >>> 1;// expect eq sum(id)

            }
            Assert.assertEquals(kb, key);
            while (values.hasNext()) {
                final Text val = values.next();
                // increment vc by map ID assoc w/ val
                vc += val.getBytes()[0];
                // verify that all the fixed characters 'V' match
                Assert.assertEquals(0, WritableComparator.compareBytes(TestReduceFetchFromPartialMem.MBValidate.vb.getBytes(), 1, (vlen - 1), val.getBytes(), 1, ((val.getLength()) - 1)));
                out.collect(key, val);
                ++(nRec);
            } 
            Assert.assertEquals(("Bad rec count for " + key), recCheck, ((nRec) - preRec));
            Assert.assertEquals(("Bad rec group for " + key), vcCheck, vc);
        }

        @Override
        public void close() throws IOException {
            Assert.assertEquals(4095, nKey);
            Assert.assertEquals(((TestReduceFetchFromPartialMem.MBValidate.nMaps) - 1), aKey);
            Assert.assertEquals(((TestReduceFetchFromPartialMem.MBValidate.nMaps) - 1), bKey);
            Assert.assertEquals("Bad record count", ((TestReduceFetchFromPartialMem.MBValidate.nMaps) * (4096 + 2)), nRec);
        }
    }

    public static class FakeSplit implements InputSplit {
        public void write(DataOutput out) throws IOException {
        }

        public void readFields(DataInput in) throws IOException {
        }

        public long getLength() {
            return 0L;
        }

        public String[] getLocations() {
            return new String[0];
        }
    }

    public static class FakeIF implements InputFormat<NullWritable, NullWritable> {
        public FakeIF() {
        }

        public InputSplit[] getSplits(JobConf conf, int numSplits) {
            InputSplit[] splits = new InputSplit[numSplits];
            for (int i = 0; i < (splits.length); ++i) {
                splits[i] = new TestReduceFetchFromPartialMem.FakeSplit();
            }
            return splits;
        }

        public RecordReader<NullWritable, NullWritable> getRecordReader(InputSplit ignored, JobConf conf, Reporter reporter) {
            return new RecordReader<NullWritable, NullWritable>() {
                private boolean done = false;

                public boolean next(NullWritable key, NullWritable value) throws IOException {
                    if (done)
                        return false;

                    done = true;
                    return true;
                }

                public NullWritable createKey() {
                    return NullWritable.get();
                }

                public NullWritable createValue() {
                    return NullWritable.get();
                }

                public long getPos() throws IOException {
                    return 0L;
                }

                public void close() throws IOException {
                }

                public float getProgress() throws IOException {
                    return 0.0F;
                }
            };
        }
    }
}

