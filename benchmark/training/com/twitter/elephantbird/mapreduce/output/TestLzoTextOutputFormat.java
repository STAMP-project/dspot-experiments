package com.twitter.elephantbird.mapreduce.output;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;


public class TestLzoTextOutputFormat {
    private static final long SMALL_MIN_SIZE = 1000L;

    private static final long BIG_MIN_SIZE = (TestLzoTextOutputFormat.SMALL_MIN_SIZE) * 1000;

    private Path outputDir_;

    private Configuration conf_;

    private FileSystem lfs_;

    @Test
    public void testLzoIndexViaBlockSize() throws Exception {
        testIndexFile(TestLzoTextOutputFormat.SMALL_MIN_SIZE, true);
    }

    @Test
    public void testNoLzoIndexViaBlockSize() throws Exception {
        testIndexFile(TestLzoTextOutputFormat.BIG_MIN_SIZE, true);
    }

    @Test
    public void testLzoIndexViaMinSize() throws Exception {
        testIndexFile(TestLzoTextOutputFormat.SMALL_MIN_SIZE, false);
    }

    @Test
    public void testNoLzoIndexViaMinSize() throws Exception {
        testIndexFile(TestLzoTextOutputFormat.BIG_MIN_SIZE, false);
    }
}

