package com.twitter.elephantbird.pig.load;


import com.google.common.primitives.Bytes;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.hadoop.fs.FileUtil;
import org.apache.pig.PigServer;
import org.apache.pig.data.Tuple;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Tests lzo loader with many splits (about 8K) to trigger corner cases.
 * Creates an indexed lzo file with 1M 1-byte records.
 *
 * The lzo buffer size is set to 512 bytes (default 256KB) so that we end up
 * with lots of lzo blocks.
 *
 * While loading in pig, set max split size to 1K. This verifies a bug fix
 * where the binary block reader was reading some records twice (pull #429).
 */
public class TestBinaryLoaderWithManySplits {
    private PigServer pigServer;

    private final String testDir = (System.getProperty("test.build.data")) + "/TestBinvaryLoaderWithManySplits";

    private final File inputDir = new File(testDir, "in");

    private final int NUM_RECORDS = 1000 * 1000;

    private final byte[] expectedRecords = new byte[NUM_RECORDS];

    @Test
    public void testLoaderWithMultiplePartitions() throws Exception {
        // setUp might not have run because of missing lzo native libraries
        Assume.assumeTrue(((pigServer) != null));
        pigServer.getPigContext().getProperties().setProperty("mapred.max.split.size", "1024");
        pigServer.registerQuery(String.format("A = load \'%s\' using %s as (bytes);\n", inputDir.toURI().toString(), LzoRawBytesLoader.class.getName()));
        Iterator<Tuple> rows = pigServer.openIterator("A");
        // verify:
        // read all the records and sort them since the splits are not processed in order
        ArrayList<Byte> actual = new ArrayList(expectedRecords.length);
        while (rows.hasNext()) {
            actual.add(get()[0]);
        } 
        byte[] actualRecords = Bytes.toArray(actual);
        Assert.assertEquals(expectedRecords.length, actual.size());
        Arrays.sort(expectedRecords);
        Arrays.sort(actualRecords);
        Assert.assertArrayEquals(expectedRecords, actualRecords);
        FileUtil.fullyDelete(inputDir);
    }
}

