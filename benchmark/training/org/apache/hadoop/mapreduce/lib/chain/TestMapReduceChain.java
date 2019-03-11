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
package org.apache.hadoop.mapreduce.lib.chain;


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.HadoopTestCase;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MapReduceTestUtil;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.junit.Assert;
import org.junit.Test;


public class TestMapReduceChain extends HadoopTestCase {
    private static String localPathRoot = System.getProperty("test.build.data", "/tmp");

    private static Path flagDir = new Path(TestMapReduceChain.localPathRoot, "testing/chain/flags");

    public TestMapReduceChain() throws IOException {
        super(HadoopTestCase.LOCAL_MR, HadoopTestCase.LOCAL_FS, 1, 1);
    }

    @Test
    public void testChain() throws Exception {
        Path inDir = new Path(TestMapReduceChain.localPathRoot, "testing/chain/input");
        Path outDir = new Path(TestMapReduceChain.localPathRoot, "testing/chain/output");
        String input = "1\n2\n";
        String expectedOutput = "0\t1ABCRDEF\n2\t2ABCRDEF\n";
        Configuration conf = createJobConf();
        TestMapReduceChain.cleanFlags(conf);
        conf.set("a", "X");
        Job job = MapReduceTestUtil.createJob(conf, inDir, outDir, 1, 1, input);
        job.setJobName("chain");
        Configuration mapAConf = new Configuration(false);
        mapAConf.set("a", "A");
        ChainMapper.addMapper(job, TestMapReduceChain.AMap.class, LongWritable.class, Text.class, LongWritable.class, Text.class, mapAConf);
        ChainMapper.addMapper(job, TestMapReduceChain.BMap.class, LongWritable.class, Text.class, LongWritable.class, Text.class, null);
        ChainMapper.addMapper(job, TestMapReduceChain.CMap.class, LongWritable.class, Text.class, LongWritable.class, Text.class, null);
        Configuration reduceConf = new Configuration(false);
        reduceConf.set("a", "C");
        ChainReducer.setReducer(job, TestMapReduceChain.RReduce.class, LongWritable.class, Text.class, LongWritable.class, Text.class, reduceConf);
        ChainReducer.addMapper(job, TestMapReduceChain.DMap.class, LongWritable.class, Text.class, LongWritable.class, Text.class, null);
        Configuration mapEConf = new Configuration(false);
        mapEConf.set("a", "E");
        ChainReducer.addMapper(job, TestMapReduceChain.EMap.class, LongWritable.class, Text.class, LongWritable.class, Text.class, mapEConf);
        ChainReducer.addMapper(job, TestMapReduceChain.FMap.class, LongWritable.class, Text.class, LongWritable.class, Text.class, null);
        job.waitForCompletion(true);
        Assert.assertTrue("Job failed", job.isSuccessful());
        String str = "flag not set";
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.setup.A"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.setup.B"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.setup.C"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "reduce.setup.R"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.setup.D"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.setup.E"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.setup.F"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.A.value.1"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.A.value.2"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.B.value.1A"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.B.value.2A"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.C.value.1AB"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.C.value.2AB"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "reduce.R.value.1ABC"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "reduce.R.value.2ABC"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.D.value.1ABCR"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.D.value.2ABCR"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.E.value.1ABCRD"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.E.value.2ABCRD"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.F.value.1ABCRDE"));
        Assert.assertTrue(str, TestMapReduceChain.getFlag(conf, "map.F.value.2ABCRDE"));
        Assert.assertTrue(TestMapReduceChain.getFlag(conf, "map.cleanup.A"));
        Assert.assertTrue(TestMapReduceChain.getFlag(conf, "map.cleanup.B"));
        Assert.assertTrue(TestMapReduceChain.getFlag(conf, "map.cleanup.C"));
        Assert.assertTrue(TestMapReduceChain.getFlag(conf, "reduce.cleanup.R"));
        Assert.assertTrue(TestMapReduceChain.getFlag(conf, "map.cleanup.D"));
        Assert.assertTrue(TestMapReduceChain.getFlag(conf, "map.cleanup.E"));
        Assert.assertTrue(TestMapReduceChain.getFlag(conf, "map.cleanup.F"));
        Assert.assertEquals("Outputs doesn't match", expectedOutput, MapReduceTestUtil.readOutput(outDir, conf));
    }

    public static class AMap extends TestMapReduceChain.IDMap {
        public AMap() {
            super("A", "A");
        }
    }

    public static class BMap extends TestMapReduceChain.IDMap {
        public BMap() {
            super("B", "X");
        }
    }

    public static class CMap extends TestMapReduceChain.IDMap {
        public CMap() {
            super("C", "X");
        }
    }

    public static class RReduce extends TestMapReduceChain.IDReduce {
        public RReduce() {
            super("R", "C");
        }
    }

    public static class DMap extends TestMapReduceChain.IDMap {
        public DMap() {
            super("D", "X");
        }
    }

    public static class EMap extends TestMapReduceChain.IDMap {
        public EMap() {
            super("E", "E");
        }
    }

    public static class FMap extends TestMapReduceChain.IDMap {
        public FMap() {
            super("F", "X");
        }
    }

    public static class IDMap extends Mapper<LongWritable, Text, LongWritable, Text> {
        private String name;

        private String prop;

        public IDMap(String name, String prop) {
            this.name = name;
            this.prop = prop;
        }

        public void setup(Context context) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            Assert.assertEquals(prop, conf.get("a"));
            TestMapReduceChain.writeFlag(conf, ("map.setup." + (name)));
        }

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            TestMapReduceChain.writeFlag(context.getConfiguration(), ((("map." + (name)) + ".value.") + value));
            context.write(key, new Text((value + (name))));
        }

        public void cleanup(Context context) throws IOException, InterruptedException {
            TestMapReduceChain.writeFlag(context.getConfiguration(), ("map.cleanup." + (name)));
        }
    }

    public static class IDReduce extends Reducer<LongWritable, Text, LongWritable, Text> {
        private String name;

        private String prop;

        public IDReduce(String name, String prop) {
            this.name = name;
            this.prop = prop;
        }

        public void setup(Context context) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            Assert.assertEquals(prop, conf.get("a"));
            TestMapReduceChain.writeFlag(conf, ("reduce.setup." + (name)));
        }

        public void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text value : values) {
                TestMapReduceChain.writeFlag(context.getConfiguration(), ((("reduce." + (name)) + ".value.") + value));
                context.write(key, new Text((value + (name))));
            }
        }

        public void cleanup(Context context) throws IOException, InterruptedException {
            TestMapReduceChain.writeFlag(context.getConfiguration(), ("reduce.cleanup." + (name)));
        }
    }
}

