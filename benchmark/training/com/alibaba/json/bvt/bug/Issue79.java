package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue79 extends TestCase {
    public void test_for_issue_79() throws Exception {
        Issue79.SearchResult result = JSON.parseObject("{\"present\":{\"records\":[{}]}}", Issue79.SearchResult.class);
        Assert.assertNotNull(result.present);
        Assert.assertNotNull(result.present.records);
        Assert.assertNotNull(result.present.records.get(0));
        Assert.assertNotNull(((result.present.records.get(0)) instanceof Issue79.PresentRecord));
    }

    public static class SearchResult {
        public Issue79.Present present;
    }

    public static class Present {
        public List<Issue79.PresentRecord> records;
    }

    public static class PresentRecord {}
}

