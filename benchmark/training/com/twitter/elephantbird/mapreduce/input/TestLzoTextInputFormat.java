package com.twitter.elephantbird.mapreduce.input;


import LzoIndex.NOT_FOUND;
import com.hadoop.compression.lzo.LzoIndex;
import com.hadoop.compression.lzo.LzopCodec;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the LzoTextInputFormat, make sure it splits the file properly and
 * returns the right data.
 */
public class TestLzoTextInputFormat {
    private static final Log LOG = LogFactory.getLog(TestLzoTextInputFormat.class);

    private MessageDigest md5_;

    private final String lzoFileName_ = "part-r-00001" + (new LzopCodec().getDefaultExtension());

    private Path outputDir_;

    // Test both bigger outputs and small one chunk ones.
    private static final int OUTPUT_BIG = 10485760;

    private static final int OUTPUT_SMALL = 50000;

    /**
     * Make sure the lzo index class works as described.
     */
    @Test
    public void testLzoIndex() {
        LzoIndex index = new LzoIndex();
        Assert.assertTrue(index.isEmpty());
        index = new LzoIndex(4);
        index.set(0, 0);
        index.set(1, 5);
        index.set(2, 10);
        index.set(3, 15);
        Assert.assertFalse(index.isEmpty());
        Assert.assertEquals(0, index.findNextPosition((-1)));
        Assert.assertEquals(5, index.findNextPosition(1));
        Assert.assertEquals(5, index.findNextPosition(5));
        Assert.assertEquals(15, index.findNextPosition(11));
        Assert.assertEquals(15, index.findNextPosition(15));
        Assert.assertEquals((-1), index.findNextPosition(16));
        Assert.assertEquals(5, index.alignSliceStartToIndex(3, 20));
        Assert.assertEquals(15, index.alignSliceStartToIndex(15, 20));
        Assert.assertEquals(10, index.alignSliceEndToIndex(8, 30));
        Assert.assertEquals(10, index.alignSliceEndToIndex(10, 30));
        Assert.assertEquals(30, index.alignSliceEndToIndex(17, 30));
        Assert.assertEquals(NOT_FOUND, index.alignSliceStartToIndex(16, 20));
    }

    /**
     * Index the file and make sure it splits properly.
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testWithIndex() throws IOException, InterruptedException, NoSuchAlgorithmException {
        runTest(true, TestLzoTextInputFormat.OUTPUT_BIG);
        runTest(true, TestLzoTextInputFormat.OUTPUT_SMALL);
    }

    /**
     * Don't index the file and make sure it can be processed anyway.
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testWithoutIndex() throws IOException, InterruptedException, NoSuchAlgorithmException {
        runTest(false, TestLzoTextInputFormat.OUTPUT_BIG);
        runTest(false, TestLzoTextInputFormat.OUTPUT_SMALL);
    }

    @Test
    public void testCombineWithIndex() throws IOException, InterruptedException, NoSuchAlgorithmException {
        runTest(true, TestLzoTextInputFormat.OUTPUT_BIG, true);
        runTest(true, TestLzoTextInputFormat.OUTPUT_SMALL, true);
    }

    @Test
    public void testCombineWithoutIndex() throws IOException, InterruptedException, NoSuchAlgorithmException {
        runTest(false, TestLzoTextInputFormat.OUTPUT_BIG, true);
        runTest(false, TestLzoTextInputFormat.OUTPUT_SMALL, true);
    }
}

