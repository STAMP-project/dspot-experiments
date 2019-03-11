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
package org.apache.hadoop.mapreduce.lib.join;


import CompositeInputFormat.JOIN_EXPR;
import SequenceFile.Reader;
import SequenceFile.Writer;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.Utils;
import org.apache.hadoop.mapreduce.MapReduceTestUtil;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.hadoop.mapreduce.MapReduceTestUtil.Fake_IF.setKeyClass;
import static org.apache.hadoop.mapreduce.MapReduceTestUtil.Fake_IF.setValClass;
import static org.apache.hadoop.mapreduce.Reducer.<init>;


public class TestJoinDatamerge {
    private static MiniDFSCluster cluster = null;

    private abstract static class SimpleCheckerMapBase<V extends Writable> extends Mapper<IntWritable, V, IntWritable, IntWritable> {
        protected static final IntWritable one = new IntWritable(1);

        int srcs;

        public void setup(Context context) {
            srcs = context.getConfiguration().getInt("testdatamerge.sources", 0);
            Assert.assertTrue(("Invalid src count: " + (srcs)), ((srcs) > 0));
        }
    }

    private abstract static class SimpleCheckerReduceBase extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        protected static final IntWritable one = new IntWritable(1);

        int srcs;

        public void setup(Context context) {
            srcs = context.getConfiguration().getInt("testdatamerge.sources", 0);
            Assert.assertTrue(("Invalid src count: " + (srcs)), ((srcs) > 0));
        }

        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int seen = 0;
            for (IntWritable value : values) {
                seen += value.get();
            }
            Assert.assertTrue(("Bad count for " + (key.get())), verify(key.get(), seen));
            context.write(key, new IntWritable(seen));
        }

        public abstract boolean verify(int key, int occ);
    }

    private static class InnerJoinMapChecker extends TestJoinDatamerge.SimpleCheckerMapBase<TupleWritable> {
        public void map(IntWritable key, TupleWritable val, Context context) throws IOException, InterruptedException {
            int k = key.get();
            final String kvstr = "Unexpected tuple: " + (TestJoinDatamerge.stringify(key, val));
            Assert.assertTrue(kvstr, (0 == (k % ((srcs) * (srcs)))));
            for (int i = 0; i < (val.size()); ++i) {
                final int vali = get();
                Assert.assertTrue(kvstr, (((vali - i) * (srcs)) == (10 * k)));
            }
            context.write(key, TestJoinDatamerge.SimpleCheckerMapBase.one);
            // If the user modifies the key or any of the values in the tuple, it
            // should not affect the rest of the join.
            key.set((-1));
            if (val.has(0)) {
                set(0);
            }
        }
    }

    private static class InnerJoinReduceChecker extends TestJoinDatamerge.SimpleCheckerReduceBase {
        public boolean verify(int key, int occ) {
            return ((key == 0) && (occ == 2)) || (((key != 0) && ((key % ((srcs) * (srcs))) == 0)) && (occ == 1));
        }
    }

    private static class OuterJoinMapChecker extends TestJoinDatamerge.SimpleCheckerMapBase<TupleWritable> {
        public void map(IntWritable key, TupleWritable val, Context context) throws IOException, InterruptedException {
            int k = key.get();
            final String kvstr = "Unexpected tuple: " + (TestJoinDatamerge.stringify(key, val));
            if (0 == (k % ((srcs) * (srcs)))) {
                for (int i = 0; i < (val.size()); ++i) {
                    Assert.assertTrue(kvstr, ((val.get(i)) instanceof IntWritable));
                    final int vali = get();
                    Assert.assertTrue(kvstr, (((vali - i) * (srcs)) == (10 * k)));
                }
            } else {
                for (int i = 0; i < (val.size()); ++i) {
                    if (i == (k % (srcs))) {
                        Assert.assertTrue(kvstr, ((val.get(i)) instanceof IntWritable));
                        final int vali = get();
                        Assert.assertTrue(kvstr, (((srcs) * (vali - i)) == (10 * (k - i))));
                    } else {
                        Assert.assertTrue(kvstr, (!(val.has(i))));
                    }
                }
            }
            context.write(key, TestJoinDatamerge.SimpleCheckerMapBase.one);
            // If the user modifies the key or any of the values in the tuple, it
            // should not affect the rest of the join.
            key.set((-1));
            if (val.has(0)) {
                set(0);
            }
        }
    }

    private static class OuterJoinReduceChecker extends TestJoinDatamerge.SimpleCheckerReduceBase {
        public boolean verify(int key, int occ) {
            if ((key < ((srcs) * (srcs))) && ((key % ((srcs) + 1)) == 0)) {
                return 2 == occ;
            }
            return 1 == occ;
        }
    }

    private static class OverrideMapChecker extends TestJoinDatamerge.SimpleCheckerMapBase<IntWritable> {
        public void map(IntWritable key, IntWritable val, Context context) throws IOException, InterruptedException {
            int k = key.get();
            final int vali = val.get();
            final String kvstr = "Unexpected tuple: " + (TestJoinDatamerge.stringify(key, val));
            if (0 == (k % ((srcs) * (srcs)))) {
                Assert.assertTrue(kvstr, (vali == ((((k * 10) / (srcs)) + (srcs)) - 1)));
            } else {
                final int i = k % (srcs);
                Assert.assertTrue(kvstr, (((srcs) * (vali - i)) == (10 * (k - i))));
            }
            context.write(key, TestJoinDatamerge.SimpleCheckerMapBase.one);
            // If the user modifies the key or any of the values in the tuple, it
            // should not affect the rest of the join.
            key.set((-1));
            val.set(0);
        }
    }

    private static class OverrideReduceChecker extends TestJoinDatamerge.SimpleCheckerReduceBase {
        public boolean verify(int key, int occ) {
            if (((key < ((srcs) * (srcs))) && ((key % ((srcs) + 1)) == 0)) && (key != 0)) {
                return 2 == occ;
            }
            return 1 == occ;
        }
    }

    @Test
    public void testSimpleInnerJoin() throws Exception {
        TestJoinDatamerge.joinAs("inner", TestJoinDatamerge.InnerJoinMapChecker.class, TestJoinDatamerge.InnerJoinReduceChecker.class);
    }

    @Test
    public void testSimpleOuterJoin() throws Exception {
        TestJoinDatamerge.joinAs("outer", TestJoinDatamerge.OuterJoinMapChecker.class, TestJoinDatamerge.OuterJoinReduceChecker.class);
    }

    @Test
    public void testSimpleOverride() throws Exception {
        TestJoinDatamerge.joinAs("override", TestJoinDatamerge.OverrideMapChecker.class, TestJoinDatamerge.OverrideReduceChecker.class);
    }

    @Test
    public void testNestedJoin() throws Exception {
        // outer(inner(S1,...,Sn),outer(S1,...Sn))
        final int SOURCES = 3;
        final int ITEMS = (SOURCES + 1) * (SOURCES + 1);
        Configuration conf = new Configuration();
        Path base = TestJoinDatamerge.cluster.getFileSystem().makeQualified(new Path("/nested"));
        int[][] source = new int[SOURCES][];
        for (int i = 0; i < SOURCES; ++i) {
            source[i] = new int[ITEMS];
            for (int j = 0; j < ITEMS; ++j) {
                source[i][j] = (i + 2) * (j + 1);
            }
        }
        Path[] src = new Path[SOURCES];
        SequenceFile[] out = TestJoinDatamerge.createWriters(base, conf, SOURCES, src);
        IntWritable k = new IntWritable();
        for (int i = 0; i < SOURCES; ++i) {
            IntWritable v = new IntWritable();
            v.set(i);
            for (int j = 0; j < ITEMS; ++j) {
                k.set(source[i][j]);
                out[i].append(k, v);
            }
            out[i].close();
        }
        out = null;
        StringBuilder sb = new StringBuilder();
        sb.append("outer(inner(");
        for (int i = 0; i < SOURCES; ++i) {
            sb.append(CompositeInputFormat.compose(SequenceFileInputFormat.class, src[i].toString()));
            if ((i + 1) != SOURCES)
                sb.append(",");

        }
        sb.append("),outer(");
        sb.append(CompositeInputFormat.compose(MapReduceTestUtil.Fake_IF.class, "foobar"));
        sb.append(",");
        for (int i = 0; i < SOURCES; ++i) {
            sb.append(CompositeInputFormat.compose(SequenceFileInputFormat.class, src[i].toString()));
            sb.append(",");
        }
        sb.append(((CompositeInputFormat.compose(MapReduceTestUtil.Fake_IF.class, "raboof")) + "))"));
        conf.set(JOIN_EXPR, sb.toString());
        setKeyClass(conf, IntWritable.class);
        setValClass(conf, IntWritable.class);
        Job job = Job.getInstance(conf);
        Path outf = new Path(base, "out");
        FileOutputFormat.setOutputPath(job, outf);
        job.setInputFormatClass(CompositeInputFormat.class);
        job.setMapperClass(org.apache.hadoop.mapreduce.Mapper.class);
        job.setReducerClass(org.apache.hadoop.mapreduce.Reducer.class);
        job.setNumReduceTasks(0);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(TupleWritable.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);
        job.waitForCompletion(true);
        Assert.assertTrue("Job failed", job.isSuccessful());
        FileStatus[] outlist = TestJoinDatamerge.cluster.getFileSystem().listStatus(outf, new Utils.OutputFileUtils.OutputFilesFilter());
        Assert.assertEquals(1, outlist.length);
        Assert.assertTrue((0 < (outlist[0].getLen())));
        SequenceFile.Reader r = new SequenceFile.Reader(TestJoinDatamerge.cluster.getFileSystem(), outlist[0].getPath(), conf);
        TupleWritable v = new TupleWritable();
        while (r.next(k, v)) {
            Assert.assertFalse(has(0));
            Assert.assertFalse(has((SOURCES + 1)));
            boolean chk = true;
            int ki = k.get();
            for (int i = 2; i < (SOURCES + 2); ++i) {
                if (((ki % i) == 0) && (ki <= (i * ITEMS))) {
                    Assert.assertEquals((i - 2), ((IntWritable) (((TupleWritable) (v.get(1))).get((i - 1)))).get());
                } else
                    chk = false;

            }
            if (chk) {
                // present in all sources; chk inner
                Assert.assertTrue(v.has(0));
                for (int i = 0; i < SOURCES; ++i)
                    Assert.assertTrue(has(i));

            } else {
                // should not be present in inner join
                Assert.assertFalse(v.has(0));
            }
        } 
        r.close();
        base.getFileSystem(conf).delete(base, true);
    }

    @Test
    public void testEmptyJoin() throws Exception {
        Configuration conf = new Configuration();
        Path base = TestJoinDatamerge.cluster.getFileSystem().makeQualified(new Path("/empty"));
        Path[] src = new Path[]{ new Path(base, "i0"), new Path("i1"), new Path("i2") };
        conf.set(JOIN_EXPR, CompositeInputFormat.compose("outer", MapReduceTestUtil.Fake_IF.class, src));
        setKeyClass(conf, MapReduceTestUtil.IncomparableKey.class);
        Job job = Job.getInstance(conf);
        job.setInputFormatClass(CompositeInputFormat.class);
        FileOutputFormat.setOutputPath(job, new Path(base, "out"));
        job.setMapperClass(org.apache.hadoop.mapreduce.Mapper.class);
        job.setReducerClass(org.apache.hadoop.mapreduce.Reducer.class);
        job.setOutputKeyClass(MapReduceTestUtil.IncomparableKey.class);
        job.setOutputValueClass(NullWritable.class);
        job.waitForCompletion(true);
        Assert.assertTrue(job.isSuccessful());
        base.getFileSystem(conf).delete(base, true);
    }
}

