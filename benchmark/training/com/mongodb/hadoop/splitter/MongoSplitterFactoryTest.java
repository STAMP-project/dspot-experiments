package com.mongodb.hadoop.splitter;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.hadoop.testutils.BaseHadoopTest;
import com.mongodb.hadoop.util.MongoConfigUtil;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;


public class MongoSplitterFactoryTest extends BaseHadoopTest {
    private static MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017/mongo_hadoop.splitter_factory_test");

    private static MongoClient client;

    @Test
    public void testDefaultSplitter() {
        Configuration conf = new Configuration();
        MongoConfigUtil.setInputURI(conf, MongoSplitterFactoryTest.uri);
        MongoSplitter defaultSplitter = MongoSplitterFactory.getSplitter(conf);
        if (isSharded(MongoSplitterFactoryTest.uri)) {
            Assert.assertTrue((defaultSplitter instanceof ShardChunkMongoSplitter));
            MongoConfigUtil.setShardChunkSplittingEnabled(conf, false);
            MongoConfigUtil.setReadSplitsFromShards(conf, true);
            Assert.assertTrue(((MongoSplitterFactory.getSplitter(conf)) instanceof ShardMongoSplitter));
        } else {
            if (isSampleOperatorSupported(MongoSplitterFactoryTest.uri)) {
                Assert.assertTrue((defaultSplitter instanceof SampleSplitter));
            } else {
                Assert.assertTrue((defaultSplitter instanceof StandaloneMongoSplitter));
            }
        }
    }
}

