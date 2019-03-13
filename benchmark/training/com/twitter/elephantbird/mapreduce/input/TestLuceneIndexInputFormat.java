package com.twitter.elephantbird.mapreduce.input;


import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.twitter.elephantbird.mapreduce.input.LuceneIndexInputFormat.LuceneIndexInputSplit;
import com.twitter.elephantbird.mapreduce.output.LuceneIndexOutputFormat;
import com.twitter.elephantbird.util.HadoopCompat;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Alex Levenson
 */
public class TestLuceneIndexInputFormat {
    @Test
    public void testFindSplitsRecursive() throws Exception {
        findSplitsHelper(ImmutableList.of(new Path(("src/test/resources/com/twitter/elephantbird" + "/mapreduce/input/sample_indexes/"))));
    }

    @Test
    public void testFindSplitsExplicit() throws Exception {
        findSplitsHelper(ImmutableList.of(new Path(("src/test/resources/com/twitter/elephantbird/mapreduce/input/" + "sample_indexes/index-1")), new Path(("src/test/resources/com/twitter/elephantbird/mapreduce/input/" + "sample_indexes/index-2")), new Path(("src/test/resources/com/twitter/elephantbird/mapreduce/input/" + "sample_indexes/more-indexes/index-3"))));
    }

    private static class DummyLuceneInputFormat extends LuceneIndexInputFormat<IntWritable> {
        @Override
        public PathFilter getIndexDirPathFilter(Configuration conf) throws IOException {
            return LuceneIndexOutputFormat.newIndexDirFilter(conf);
        }

        @Override
        public RecordReader<IntWritable, IntWritable> createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
            return null;
        }
    }

    @Test
    public void testGetSplits() throws Exception {
        TestLuceneIndexInputFormat.DummyLuceneInputFormat lif = new TestLuceneIndexInputFormat.DummyLuceneInputFormat();
        Configuration conf = new Configuration();
        LuceneIndexInputFormat.setInputPaths(ImmutableList.of(new Path(("src/test/resources/com/twitter/elephantbird" + "/mapreduce/input/sample_indexes/"))), conf);
        LuceneIndexInputFormat.setMaxCombinedIndexSizePerSplitBytes(15L, conf);
        JobContext jobContext = createStrictMock(JobContext.class);
        expect(HadoopCompat.getConfiguration(jobContext)).andStubReturn(conf);
        replay(jobContext);
        List<InputSplit> splits = lif.getSplits(jobContext);
        LuceneIndexInputSplit split = ((LuceneIndexInputSplit) (splits.get(0)));
        Assert.assertEquals(2, split.getIndexDirs().size());
        Assert.assertTrue(split.getIndexDirs().get(0).toString().endsWith("sample_indexes/index-1"));
        Assert.assertTrue(split.getIndexDirs().get(1).toString().endsWith("sample_indexes/more-indexes/index-3"));
        split = ((LuceneIndexInputSplit) (splits.get(1)));
        Assert.assertEquals(1, split.getIndexDirs().size());
        Assert.assertTrue(split.getIndexDirs().get(0).toString().endsWith("sample_indexes/index-2"));
    }

    @Test
    public void testLuceneIndexInputSplit() throws Exception {
        LuceneIndexInputSplit orig = new LuceneIndexInputSplit(Lists.newArrayList(new Path("/index/test"), new Path("/index/test2"), new Path("/index/test3")), 500L);
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(bytesOut);
        orig.write(dataOut);
        LuceneIndexInputSplit deSerialized = new LuceneIndexInputSplit();
        deSerialized.readFields(new DataInputStream(new ByteArrayInputStream(bytesOut.toByteArray())));
        Assert.assertEquals(orig.getIndexDirs(), deSerialized.getIndexDirs());
        Assert.assertEquals(orig.getLength(), deSerialized.getLength());
        Assert.assertEquals(0, orig.compareTo(deSerialized));
        LuceneIndexInputSplit smaller = new LuceneIndexInputSplit(Lists.newArrayList(new Path("/index/small")), 100L);
        Assert.assertTrue(((orig.compareTo(smaller)) > 0));
        Assert.assertTrue(((smaller.compareTo(orig)) < 0));
    }

    @Test
    public void testCombineSplits() throws Exception {
        TestLuceneIndexInputFormat.DummyLuceneInputFormat lif = new TestLuceneIndexInputFormat.DummyLuceneInputFormat();
        PriorityQueue<LuceneIndexInputSplit> splits = new PriorityQueue<LuceneIndexInputSplit>();
        String[] paths = new String[]{ "/index/1", "/index/2", "/index/3", "/index/4", "/index/5", "/index/6" };
        Long[] sizes = new Long[]{ 500L, 300L, 100L, 150L, 1200L, 500L };
        for (int i = 0; i < (paths.length); i++) {
            splits.add(new LuceneIndexInputSplit(Lists.newArrayList(new Path(paths[i])), sizes[i]));
        }
        List<InputSplit> combined = lif.combineSplits(splits, 1000L, 10000L);
        Assert.assertEquals(3, combined.size());
        List<Path> dirs = getIndexDirs();
        Set<String> dirsStrings = Sets.newHashSet(Iterables.transform(dirs, Functions.toStringFunction()));
        Assert.assertEquals(3, dirsStrings.size());
        Assert.assertTrue(dirsStrings.contains("/index/2"));
        Assert.assertTrue(dirsStrings.contains("/index/3"));
        Assert.assertTrue(dirsStrings.contains("/index/4"));
        dirs = ((LuceneIndexInputSplit) (combined.get(1))).getIndexDirs();
        dirsStrings = Sets.newHashSet(Iterables.transform(dirs, Functions.toStringFunction()));
        Assert.assertEquals(2, dirsStrings.size());
        Assert.assertTrue(dirsStrings.contains("/index/1"));
        Assert.assertTrue(dirsStrings.contains("/index/6"));
        dirs = ((LuceneIndexInputSplit) (combined.get(2))).getIndexDirs();
        dirsStrings = Sets.newHashSet(Iterables.transform(dirs, Functions.toStringFunction()));
        Assert.assertEquals(1, dirsStrings.size());
        Assert.assertTrue(dirsStrings.contains("/index/5"));
    }

    @Test
    public void testCombineSplitsOneSplit() throws Exception {
        TestLuceneIndexInputFormat.DummyLuceneInputFormat lif = new TestLuceneIndexInputFormat.DummyLuceneInputFormat();
        PriorityQueue<LuceneIndexInputSplit> splits = new PriorityQueue<LuceneIndexInputSplit>();
        splits.add(new LuceneIndexInputSplit(Lists.newArrayList(new Path("/index/1")), 1500L));
        List<InputSplit> combined = lif.combineSplits(splits, 1000L, 10000L);
        Assert.assertEquals(1, combined.size());
        List<Path> dirs = getIndexDirs();
        Set<String> dirsStrings = Sets.newHashSet(Iterables.transform(dirs, Functions.toStringFunction()));
        Assert.assertEquals(1, dirsStrings.size());
        Assert.assertTrue(dirsStrings.contains("/index/1"));
    }

    @Test
    public void testCombineSplitsAllTooBig() throws Exception {
        TestLuceneIndexInputFormat.DummyLuceneInputFormat lif = new TestLuceneIndexInputFormat.DummyLuceneInputFormat();
        PriorityQueue<LuceneIndexInputSplit> splits = new PriorityQueue<LuceneIndexInputSplit>();
        String[] paths = new String[]{ "/index/1", "/index/2", "/index/3" };
        Long[] sizes = new Long[]{ 1500L, 1501L, 1502L };
        for (int i = 0; i < (paths.length); i++) {
            splits.add(new LuceneIndexInputSplit(Lists.newArrayList(new Path(paths[i])), sizes[i]));
        }
        List<InputSplit> combined = lif.combineSplits(splits, 1000L, 10000L);
        Assert.assertEquals(3, combined.size());
        for (int i = 0; i < (paths.length); i++) {
            List<Path> dirs = getIndexDirs();
            List<String> dirsStrings = Lists.newLinkedList(Iterables.transform(dirs, Functions.toStringFunction()));
            Assert.assertEquals(1, dirsStrings.size());
            Assert.assertEquals(("/index/" + (String.valueOf((i + 1)))), dirsStrings.get(0));
        }
    }

    @Test
    public void testCombineSplitsWithMaxNumberIndexesPerMapper() throws Exception {
        TestLuceneIndexInputFormat.DummyLuceneInputFormat lif = new TestLuceneIndexInputFormat.DummyLuceneInputFormat();
        PriorityQueue<LuceneIndexInputSplit> splits = new PriorityQueue<LuceneIndexInputSplit>();
        String[] paths = new String[1000];
        long[] sizes = new long[1000];
        for (int i = 0; i < 100; i++) {
            switch (i) {
                case 0 :
                    sizes[i] = 500L;
                    paths[i] = "/index/500";
                    break;
                case 1 :
                    sizes[i] = 300L;
                    paths[i] = "/index/300";
                    break;
                case 2 :
                    sizes[i] = 100L;
                    paths[i] = "/index/100";
                    break;
                default :
                    sizes[i] = 1L;
                    paths[i] = "/index/small-" + i;
            }
            splits.add(new LuceneIndexInputSplit(Lists.newArrayList(new Path(paths[i])), sizes[i]));
        }
        List<InputSplit> combined = lif.combineSplits(splits, 150L, 10L);
        Assert.assertEquals(12, combined.size());
        for (int i = 0; i < 9; i++) {
            LuceneIndexInputSplit split = ((LuceneIndexInputSplit) (combined.get(i)));
            Assert.assertEquals(10L, split.getIndexDirs().size());
            Assert.assertEquals(10L, split.getLength());
            for (Path p : split.getIndexDirs()) {
                Assert.assertTrue(p.toString().startsWith("/index/small-"));
            }
        }
        LuceneIndexInputSplit split = ((LuceneIndexInputSplit) (combined.get(9)));
        Assert.assertEquals(8, split.getIndexDirs().size());
        Assert.assertEquals(107, split.getLength());
        for (int i = 0; i < 7; i++) {
            Assert.assertTrue(split.getIndexDirs().get(i).toString().startsWith("/index/small-"));
        }
        Assert.assertEquals("/index/100", split.getIndexDirs().get(7).toString());
        split = ((LuceneIndexInputSplit) (combined.get(10)));
        Assert.assertEquals(1, split.getIndexDirs().size());
        Assert.assertEquals(300, split.getLength());
        Assert.assertEquals("/index/300", split.getIndexDirs().get(0).toString());
        split = ((LuceneIndexInputSplit) (combined.get(11)));
        Assert.assertEquals(1, split.getIndexDirs().size());
        Assert.assertEquals(500, split.getLength());
        Assert.assertEquals("/index/500", split.getIndexDirs().get(0).toString());
    }

    @Test
    public void testQuerySerialization() throws Exception {
        Configuration conf = new Configuration();
        List<String> queries = ImmutableList.of("+one -two", "something, with, commas", "something ?? with unicode", "\"something with quotes\"");
        LuceneIndexInputFormat.setQueries(queries, conf);
        Assert.assertEquals(queries, LuceneIndexInputFormat.getQueries(conf));
    }
}

