package com.mongodb.hadoop.io;


import com.mongodb.hadoop.input.MongoInputSplit;
import com.mongodb.hadoop.util.MongoConfigUtil;
import junit.framework.TestCase;
import org.apache.hadoop.conf.Configuration;
import org.junit.Test;


public class MongoInputSplitTest {
    @Test
    public void testConstructor() {
        Configuration conf = new Configuration();
        MongoConfigUtil.setFields(conf, "{\"field\": 1}");
        MongoConfigUtil.setAuthURI(conf, "mongodb://auth");
        MongoConfigUtil.setInputURI(conf, "mongodb://input");
        MongoConfigUtil.setInputKey(conf, "field");
        MongoConfigUtil.setMaxSplitKey(conf, "{\"field\": 1e9}");
        MongoConfigUtil.setMinSplitKey(conf, "{\"field\": -1e9}");
        MongoConfigUtil.setNoTimeout(conf, true);
        MongoConfigUtil.setQuery(conf, "{\"foo\": 42}");
        MongoConfigUtil.setSort(conf, "{\"foo\": -1}");
        MongoConfigUtil.setSkip(conf, 10);
        MongoInputSplit mis = new MongoInputSplit(conf);
        TestCase.assertEquals(MongoConfigUtil.getFields(conf), mis.getFields());
        TestCase.assertEquals(MongoConfigUtil.getAuthURI(conf), mis.getAuthURI());
        TestCase.assertEquals(MongoConfigUtil.getInputURI(conf), mis.getInputURI());
        TestCase.assertEquals(MongoConfigUtil.getInputKey(conf), mis.getKeyField());
        TestCase.assertEquals(MongoConfigUtil.getMaxSplitKey(conf), mis.getMax());
        TestCase.assertEquals(MongoConfigUtil.getMinSplitKey(conf), mis.getMin());
        TestCase.assertEquals(MongoConfigUtil.isNoTimeout(conf), mis.getNoTimeout());
        TestCase.assertEquals(MongoConfigUtil.getQuery(conf), mis.getQuery());
        TestCase.assertEquals(MongoConfigUtil.getSort(conf), mis.getSort());
        TestCase.assertEquals(MongoConfigUtil.getLimit(conf), ((int) (mis.getLimit())));
        TestCase.assertEquals(MongoConfigUtil.getSkip(conf), ((int) (mis.getSkip())));
        MongoInputSplit mis2 = new MongoInputSplit(mis);
        TestCase.assertEquals(mis, mis2);
    }
}

