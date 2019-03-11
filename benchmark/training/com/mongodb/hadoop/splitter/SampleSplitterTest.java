package com.mongodb.hadoop.splitter;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.hadoop.input.MongoInputSplit;
import com.mongodb.hadoop.testutils.BaseHadoopTest;
import com.mongodb.hadoop.util.MongoConfigUtil;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputSplit;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import static junit.framework.Assert.fail;


public class SampleSplitterTest extends BaseHadoopTest {
    private SampleSplitter splitter = new SampleSplitter();

    private static MongoClient client = new MongoClient("localhost:27017");

    private static MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017/mongo_hadop.sample_splitter");

    @Test
    public void testCalculateSplits() throws SplitFailedException {
        Assume.assumeTrue(isSampleOperatorSupported(SampleSplitterTest.uri));
        Configuration conf = new Configuration();
        MongoConfigUtil.setInputURI(conf, SampleSplitterTest.uri.getURI());
        MongoConfigUtil.setSplitSize(conf, 1);
        splitter.setConfiguration(conf);
        List<InputSplit> splits = splitter.calculateSplits();
        Assert.assertEquals(12, splits.size());
        MongoInputSplit firstSplit = ((MongoInputSplit) (splits.get(0)));
        Assert.assertTrue(firstSplit.getMin().toMap().isEmpty());
        MongoInputSplit lastSplit = ((MongoInputSplit) (splits.get(11)));
        Assert.assertTrue(lastSplit.getMax().toMap().isEmpty());
        // Ranges for splits are ascending.
        int lastKey = ((Integer) (firstSplit.getMax().get("_id")));
        for (int i = 1; i < ((splits.size()) - 1); i++) {
            MongoInputSplit split = ((MongoInputSplit) (splits.get(i)));
            int currentKey = ((Integer) (split.getMax().get("_id")));
            Assert.assertTrue((currentKey > lastKey));
            lastKey = currentKey;
        }
    }

    @Test
    public void testAllOnOneSplit() throws SplitFailedException {
        Assume.assumeTrue(isSampleOperatorSupported(SampleSplitterTest.uri));
        Configuration conf = new Configuration();
        MongoConfigUtil.setInputURI(conf, SampleSplitterTest.uri.getURI());
        // Split size is enough to encapsulate all documents.
        MongoConfigUtil.setSplitSize(conf, 12);
        splitter.setConfiguration(conf);
        List<InputSplit> splits = splitter.calculateSplits();
        Assert.assertEquals(1, splits.size());
        MongoInputSplit firstSplit = ((MongoInputSplit) (splits.get(0)));
        Assert.assertTrue(firstSplit.getMin().toMap().isEmpty());
        Assert.assertTrue(firstSplit.getMax().toMap().isEmpty());
    }

    @Test
    public void testAlternateSplitKey() throws SplitFailedException {
        Assume.assumeTrue(isSampleOperatorSupported(SampleSplitterTest.uri));
        Configuration conf = new Configuration();
        MongoConfigUtil.setInputURI(conf, SampleSplitterTest.uri.getURI());
        MongoConfigUtil.setSplitSize(conf, 1);
        MongoConfigUtil.setInputSplitKeyPattern(conf, "{\"i\": 1}");
        splitter.setConfiguration(conf);
        List<InputSplit> splits = splitter.calculateSplits();
        Assert.assertEquals(12, splits.size());
        MongoInputSplit firstSplit = ((MongoInputSplit) (splits.get(0)));
        Assert.assertTrue(firstSplit.getMin().toMap().isEmpty());
        MongoInputSplit lastSplit = ((MongoInputSplit) (splits.get(11)));
        Assert.assertTrue(lastSplit.getMax().toMap().isEmpty());
        // Ranges for splits are ascending.
        int lastKey = ((Integer) (firstSplit.getMax().get("i")));
        for (int i = 1; i < ((splits.size()) - 1); i++) {
            MongoInputSplit split = ((MongoInputSplit) (splits.get(i)));
            int currentKey = ((Integer) (split.getMax().get("i")));
            Assert.assertTrue((currentKey > lastKey));
            lastKey = currentKey;
        }
    }

    @Test
    public void testSampleSplitterOldMongoDB() {
        Assume.assumeFalse(isSampleOperatorSupported(SampleSplitterTest.uri));
        Configuration conf = new Configuration();
        MongoConfigUtil.setInputURI(conf, SampleSplitterTest.uri.getURI());
        MongoConfigUtil.setSplitSize(conf, 1);
        splitter.setConfiguration(conf);
        try {
            splitter.calculateSplits();
            fail(("MongoDB < 3.2 should throw SplitFailedException should fail to" + " use SampleSplitter."));
        } catch (SplitFailedException e) {
            // Good.
        }
    }
}

