package com.mongodb.hadoop.splitter;


import com.mongodb.hadoop.bookstore.BookstoreTest;
import com.mongodb.hadoop.input.BSONFileSplit;
import com.mongodb.hadoop.util.MongoConfigUtil;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.bson.BSONCallback;
import org.bson.BSONDecoder;
import org.bson.BSONObject;
import org.bson.BasicBSONCallback;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONObject;
import org.junit.Assert;
import org.junit.Test;


public class BSONSplitterTest {
    private static final BSONSplitter SPLITTER = new BSONSplitter();

    private static FileSystem fs;

    private static FileStatus file;

    private static Configuration conf;

    @Test
    public void testGetSplitsFilePath() {
        Configuration conf = new Configuration();
        Path bsonFilePath = new Path("data.bson");
        // No explicit configuration.
        Assert.assertEquals(new Path(".data.bson.splits"), BSONSplitter.getSplitsFilePath(bsonFilePath, conf));
        // Explicit configuration.
        MongoConfigUtil.setBSONSplitsPath(conf, "/foo/bar");
        Assert.assertEquals(new Path("/foo/bar/.data.bson.splits"), BSONSplitter.getSplitsFilePath(bsonFilePath, conf));
    }

    @Test
    public void testGetSplitSize() {
        Configuration ssConf = new Configuration();
        // Provide old-style split size options.
        ssConf.set("mapred.max.split.size", "1000");
        ssConf.set("dfs.blockSize", "12345");
        // No input file, so should use max split size from the configuration.
        Assert.assertEquals(1000L, BSONSplitter.getSplitSize(ssConf, null));
        // Prefer the smaller of the file block size and max configured size.
        ssConf.set("mapred.max.split.size", "10000000000000");
        Assert.assertEquals(12345L, BSONSplitter.getSplitSize(ssConf, null));
        Assert.assertEquals(BSONSplitterTest.file.getBlockSize(), BSONSplitter.getSplitSize(ssConf, BSONSplitterTest.file));
        // Prefer block size on the file itself over global block size.
        Assert.assertEquals(BSONSplitterTest.file.getBlockSize(), BSONSplitter.getSplitSize(ssConf, BSONSplitterTest.file));
        // Use larger of min size and max or block size.
        ssConf.set("mapred.min.split.size", "100000000000000000");
        Assert.assertEquals(100000000000000000L, BSONSplitter.getSplitSize(ssConf, null));
        Assert.assertEquals(100000000000000000L, BSONSplitter.getSplitSize(ssConf, BSONSplitterTest.file));
        // New-style configuration option shadows the old one.
        ssConf.set("mapreduce.input.fileinputformat.split.maxsize", "5000");
        ssConf.set("mapreduce.input.fileinputformat.split.minsize", "1");
        Assert.assertEquals(5000L, BSONSplitter.getSplitSize(ssConf, null));
        Assert.assertEquals(5000L, BSONSplitter.getSplitSize(ssConf, BSONSplitterTest.file));
    }

    @Test
    public void testGetStartingPositionForSplit() throws IOException {
        String inventoryPathString = BookstoreTest.INVENTORY_BSON.toString();
        Path inventoryPath = new Path(inventoryPathString);
        Configuration conf = new Configuration();
        BSONSplitter splitter = new BSONSplitter();
        splitter.setInputPath(inventoryPath);
        // This is a very small value for maxsize and will result in many
        // splits being created.
        conf.setLong("mapreduce.input.fileinputformat.split.maxsize", 2000L);
        splitter.setConf(conf);
        // Test without splits file.
        FileSplit fileSplit = new FileSplit(inventoryPath, 2000L, 100L, new String[]{  });
        // Writing the split file is enabled by default, so this will cause
        // the BSONSplitter to create the splits file for later tests.
        Assert.assertEquals(2130L, splitter.getStartingPositionForSplit(fileSplit));
        // Test with splits file, which was created by the previous call to
        // getStartingPositionForSplit.
        Assert.assertEquals(2130L, splitter.getStartingPositionForSplit(fileSplit));
        // Test with reading the splits file disabled.
        MongoConfigUtil.setBSONReadSplits(conf, false);
        Assert.assertEquals(2130L, splitter.getStartingPositionForSplit(fileSplit));
    }

    @Test
    public void testCreateFileSplit() throws IOException {
        BSONFileSplit splitResult = BSONSplitterTest.SPLITTER.createFileSplit(BSONSplitterTest.file, BSONSplitterTest.fs, 0, BSONSplitterTest.file.getLen());
        assertOneSplit(splitResult);
    }

    @Test
    public void testCreateFileSplitFromBSON() throws IOException {
        BSONObject splitSpec = new BasicBSONObject();
        splitSpec.put("s", 0L);
        splitSpec.put("l", BSONSplitterTest.file.getLen());
        BSONFileSplit splitResult = BSONSplitterTest.SPLITTER.createFileSplitFromBSON(splitSpec, BSONSplitterTest.fs, BSONSplitterTest.file);
        assertOneSplit(splitResult);
    }

    @Test
    public void testReadSplitsForFile() throws IOException {
        Configuration readSplitsConfig = new Configuration(BSONSplitterTest.conf);
        BSONSplitterTest.SPLITTER.setConf(readSplitsConfig);
        // Only one split if reading splits is disabled.
        MongoConfigUtil.setBSONReadSplits(readSplitsConfig, false);
        BSONSplitterTest.SPLITTER.readSplitsForFile(BSONSplitterTest.file);
        List<BSONFileSplit> splitsList = BSONSplitterTest.SPLITTER.getAllSplits();
        Assert.assertEquals(1, splitsList.size());
        BSONFileSplit theSplit = splitsList.get(0);
        assertOneSplit(theSplit);
        // Actually compute splits.
        MongoConfigUtil.setBSONReadSplits(readSplitsConfig, true);
        // Set split size to be really small so we get a lot of them.
        readSplitsConfig.set("mapreduce.input.fileinputformat.split.maxsize", "5000");
        BSONSplitterTest.SPLITTER.readSplitsForFile(BSONSplitterTest.file);
        splitsList = BSONSplitterTest.SPLITTER.getAllSplits();
        // Value found through manual inspection.
        Assert.assertEquals(40, splitsList.size());
        // Make sure that all splits start on document boundaries.
        FSDataInputStream stream = BSONSplitterTest.fs.open(BSONSplitterTest.file.getPath());
        BSONDecoder decoder = new BasicBSONDecoder();
        BSONCallback callback = new BasicBSONCallback();
        for (BSONFileSplit split : splitsList) {
            stream.seek(split.getStart());
            decoder.decode(stream, callback);
            BSONObject doc = ((BSONObject) (callback.get()));
            Assert.assertTrue(doc.containsField("_id"));
        }
    }

    @Test
    public void testReadSplits() throws IOException {
        BSONSplitterTest.SPLITTER.setInputPath(null);
        try {
            BSONSplitterTest.SPLITTER.readSplits();
            Assert.fail("Need to set inputPath to call readSplits.");
        } catch (IllegalStateException e) {
            // ok.
        }
        BSONSplitterTest.SPLITTER.readSplitsForFile(BSONSplitterTest.file);
        List<BSONFileSplit> expectedSplits = BSONSplitterTest.SPLITTER.getAllSplits();
        BSONSplitterTest.SPLITTER.setInputPath(BSONSplitterTest.file.getPath());
        BSONSplitterTest.SPLITTER.readSplits();
        List<BSONFileSplit> actualSplits = BSONSplitterTest.SPLITTER.getAllSplits();
        assertSplitsEqual(expectedSplits, actualSplits);
    }

    @Test
    public void testWriteAndLoadSplits() throws IOException {
        Configuration writeConfig = new Configuration(BSONSplitterTest.conf);
        MongoConfigUtil.setBSONWriteSplits(writeConfig, true);
        BSONSplitterTest.SPLITTER.setConf(writeConfig);
        BSONSplitterTest.SPLITTER.setInputPath(BSONSplitterTest.file.getPath());
        Path splitsFilePath = BSONSplitter.getSplitsFilePath(BSONSplitterTest.file.getPath(), writeConfig);
        try {
            // readSplitsForFile has the side-effect of writing the splits file.
            BSONSplitterTest.SPLITTER.readSplitsForFile(BSONSplitterTest.file);
            List<BSONFileSplit> expectedSplits = BSONSplitterTest.SPLITTER.getAllSplits();
            try {
                BSONSplitterTest.SPLITTER.loadSplitsFromSplitFile(BSONSplitterTest.file, splitsFilePath);
            } catch (BSONSplitter nsfe) {
                Assert.fail("Splits file not created.");
            }
            assertSplitsEqual(expectedSplits, BSONSplitterTest.SPLITTER.getAllSplits());
        } finally {
            BSONSplitterTest.SPLITTER.setConf(BSONSplitterTest.conf);
            // Remove the splits file.
            BSONSplitterTest.fs.delete(splitsFilePath, false);
        }
    }
}

