package com.mongodb.hadoop.splitter;


import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.hadoop.input.MongoInputSplit;
import com.mongodb.hadoop.util.MongoConfigUtil;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputSplit;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;


public class MongoPaginatingSplitterTest {
    private static MongoCollection<Document> collection;

    private static MongoClientURI uri;

    @Test
    public void testQuery() throws SplitFailedException {
        Configuration conf = new Configuration();
        MongoConfigUtil.setInputURI(conf, MongoPaginatingSplitterTest.uri);
        MongoConfigUtil.setRangeQueryEnabled(conf, true);
        MongoConfigUtil.setInputSplitMinDocs(conf, 5000);
        DBObject query = new BasicDBObject("$or", new BasicDBObject[]{ new BasicDBObject("value", new BasicDBObject("$lt", 25000)), new BasicDBObject("value", new BasicDBObject("$gte", 31000)) });
        MongoConfigUtil.setQuery(conf, query);
        MongoPaginatingSplitter splitter = new MongoPaginatingSplitter(conf);
        List<InputSplit> splits = splitter.calculateSplits();
        Assert.assertEquals(7, splits.size());
        MongoSplitterTestUtils.assertSplitRange(((MongoInputSplit) (splits.get(0))), null, 5000);
        MongoSplitterTestUtils.assertSplitRange(((MongoInputSplit) (splits.get(1))), 5000, 10000);
        MongoSplitterTestUtils.assertSplitRange(((MongoInputSplit) (splits.get(2))), 10000, 15000);
        MongoSplitterTestUtils.assertSplitRange(((MongoInputSplit) (splits.get(3))), 15000, 20000);
        MongoSplitterTestUtils.assertSplitRange(((MongoInputSplit) (splits.get(4))), 20000, 31000);
        MongoSplitterTestUtils.assertSplitRange(((MongoInputSplit) (splits.get(5))), 31000, 36000);
        MongoSplitterTestUtils.assertSplitRange(((MongoInputSplit) (splits.get(6))), 36000, null);
        // 6000 documents excluded by query.
        MongoSplitterTestUtils.assertSplitsCount(((MongoPaginatingSplitterTest.collection.count()) - 6000), splits);
    }

    @Test
    public void testNoQuery() throws SplitFailedException {
        Configuration conf = new Configuration();
        MongoConfigUtil.setInputURI(conf, MongoPaginatingSplitterTest.uri);
        MongoConfigUtil.setRangeQueryEnabled(conf, true);
        MongoConfigUtil.setInputSplitMinDocs(conf, 5000);
        MongoPaginatingSplitter splitter = new MongoPaginatingSplitter(conf);
        List<InputSplit> splits = splitter.calculateSplits();
        Assert.assertEquals(8, splits.size());
        for (int i = 0; i < (splits.size()); ++i) {
            Integer min = (i == 0) ? null : i * 5000;
            Integer max = (i == ((splits.size()) - 1)) ? null : (i + 1) * 5000;
            MongoSplitterTestUtils.assertSplitRange(((MongoInputSplit) (splits.get(i))), min, max);
        }
        MongoSplitterTestUtils.assertSplitsCount(MongoPaginatingSplitterTest.collection.count(), splits);
    }
}

