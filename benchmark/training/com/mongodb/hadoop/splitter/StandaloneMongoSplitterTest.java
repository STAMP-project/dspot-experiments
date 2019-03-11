package com.mongodb.hadoop.splitter;


import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.hadoop.input.MongoInputSplit;
import com.mongodb.hadoop.util.MongoConfigUtil;
import java.net.UnknownHostException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputSplit;
import org.junit.Assert;
import org.junit.Test;


public class StandaloneMongoSplitterTest {
    private static MongoClientURI uri;

    private static DBCollection collection;

    @Test
    public void unshardedCollection() throws SplitFailedException, UnknownHostException {
        Configuration config = new Configuration();
        StandaloneMongoSplitter splitter = new StandaloneMongoSplitter(config);
        MongoConfigUtil.setInputURI(config, StandaloneMongoSplitterTest.uri);
        List<InputSplit> inputSplits = splitter.calculateSplits();
        Assert.assertFalse("Should find at least one split", inputSplits.isEmpty());
    }

    @Test
    public void unshardedCollectionMinMax() throws SplitFailedException, UnknownHostException {
        Configuration config = new Configuration();
        StandaloneMongoSplitter splitter = new StandaloneMongoSplitter(config);
        MongoConfigUtil.setInputURI(config, StandaloneMongoSplitterTest.uri);
        DBObject inputSplitKey = BasicDBObjectBuilder.start("value", 1).get();
        MongoConfigUtil.setInputSplitKey(config, inputSplitKey);
        MongoConfigUtil.setSplitSize(config, 1);
        List<InputSplit> regularSplits = splitter.calculateSplits();
        MongoConfigUtil.setMinSplitKey(config, "{value:100}");
        MongoConfigUtil.setMaxSplitKey(config, "{value:39900}");
        List<InputSplit> inputSplits = splitter.calculateSplits();
        Assert.assertTrue("should be fewer splits with min/max set", ((regularSplits.size()) >= (inputSplits.size())));
    }

    @Test
    public void testNullBounds() throws Exception {
        Configuration config = new Configuration();
        StandaloneMongoSplitter splitter = new StandaloneMongoSplitter(config);
        MongoInputSplit split = splitter.createSplitFromBounds(null, null);
        Assert.assertEquals(new BasicDBObject(), split.getMin());
        Assert.assertEquals(new BasicDBObject(), split.getMax());
    }

    @Test
    public void testNullLowerBound() throws Exception {
        Configuration config = new Configuration();
        StandaloneMongoSplitter splitter = new StandaloneMongoSplitter(config);
        BasicDBObject upperBound = new BasicDBObject("a", 10);
        MongoInputSplit split = splitter.createSplitFromBounds(null, upperBound);
        Assert.assertEquals(new BasicDBObject(), split.getMin());
        Assert.assertEquals(10, split.getMax().get("a"));
    }

    @Test
    public void testNullUpperBound() throws Exception {
        Configuration config = new Configuration();
        StandaloneMongoSplitter splitter = new StandaloneMongoSplitter(config);
        BasicDBObject lowerBound = new BasicDBObject("a", 10);
        MongoInputSplit split = splitter.createSplitFromBounds(lowerBound, null);
        Assert.assertEquals(10, split.getMin().get("a"));
        Assert.assertEquals(new BasicDBObject(), split.getMax());
    }

    @Test
    public void testLowerUpperBounds() throws Exception {
        Configuration config = new Configuration();
        StandaloneMongoSplitter splitter = new StandaloneMongoSplitter(config);
        BasicDBObject lowerBound = new BasicDBObject("a", 0);
        BasicDBObject upperBound = new BasicDBObject("a", 10);
        MongoInputSplit split = splitter.createSplitFromBounds(lowerBound, upperBound);
        Assert.assertEquals(0, split.getMin().get("a"));
        Assert.assertEquals(10, split.getMax().get("a"));
    }

    @Test
    public void testFilterEmptySplitsNoQuery() throws SplitFailedException {
        Configuration config = new Configuration();
        MongoConfigUtil.setInputURI(config, StandaloneMongoSplitterTest.uri);
        MongoConfigUtil.setEnableFilterEmptySplits(config, true);
        MongoConfigUtil.setSplitSize(config, 1);
        StandaloneMongoSplitter splitter = new StandaloneMongoSplitter(config);
        List<InputSplit> splits = splitter.calculateSplits();
        // No splits should be elided, because there's no query.
        for (InputSplit split : splits) {
            Assert.assertNotEquals(0, getCursor().itcount());
        }
        MongoSplitterTestUtils.assertSplitsCount(StandaloneMongoSplitterTest.collection.count(), splits);
    }

    @Test
    public void testFilterEmptySplits() throws SplitFailedException {
        Configuration config = new Configuration();
        DBObject query = new BasicDBObject("$or", new BasicDBObject[]{ new BasicDBObject("value", new BasicDBObject("$lt", 20000)), new BasicDBObject("value", new BasicDBObject("$gt", 35000)) });
        MongoConfigUtil.setInputURI(config, StandaloneMongoSplitterTest.uri);
        MongoConfigUtil.setEnableFilterEmptySplits(config, true);
        MongoConfigUtil.setQuery(config, query);
        // 1 MB per document results in 4 splits; the 3rd one is empty per
        // the above query.
        MongoConfigUtil.setSplitSize(config, 1);
        StandaloneMongoSplitter splitter = new StandaloneMongoSplitter(config);
        List<InputSplit> splits = splitter.calculateSplits();
        // No splits are empty.
        for (InputSplit split : splits) {
            // Cursor is closed on the split, so copy it to create a new one.
            MongoInputSplit mis = new MongoInputSplit(((MongoInputSplit) (split)));
            Assert.assertNotEquals(0, mis.getCursor().itcount());
        }
        MongoSplitterTestUtils.assertSplitsCount(StandaloneMongoSplitterTest.collection.count(query), splits);
    }
}

