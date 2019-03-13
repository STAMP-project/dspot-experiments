package com.alibaba.json.bvt.issue_1600;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


public class Issue1636 extends TestCase {
    public void test_for_issue_1() throws Exception {
        Issue1636.Item1 item = JSON.parseObject("{\"modelId\":1001}", Issue1636.Item1.class);
        TestCase.assertEquals(1001, item.modelId);
    }

    public void test_for_issue_2() throws Exception {
        Issue1636.Item2 item = JSON.parseObject("{\"modelId\":1001}", Issue1636.Item2.class);
        TestCase.assertEquals(1001, item.modelId);
    }

    public static class Item1 {
        @JSONField
        private int modelId;

        @JSONCreator
        public Item1(@JSONField
        int modelId) {
            // ????
            this.modelId = modelId;
        }
    }

    public static class Item2 {
        private int modelId;

        @JSONCreator
        public Item2(int modelId) {
            // ????
            this.modelId = modelId;
        }
    }
}

