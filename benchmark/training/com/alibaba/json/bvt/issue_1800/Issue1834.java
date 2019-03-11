package com.alibaba.json.bvt.issue_1800;


import com.alibaba.fastjson.JSON;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;


public class Issue1834 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1834.IndexQuery_Number query_number = new Issue1834.IndexQuery_Number();
        Issue1834.IndexQuery_Comparable query_comparable = new Issue1834.IndexQuery_Comparable();
        List<Integer> keys = Arrays.asList(1234);
        query_number.setKeys(keys);
        query_comparable.setKeys(keys);
        String json1 = JSON.toJSONString(query_number);
        System.out.println(json1);
        Issue1834.IndexQuery_Number queryNumber = JSON.parseObject(json1, new com.alibaba.fastjson.TypeReference<Issue1834.IndexQuery_Number>() {});
        String json2 = JSON.toJSONString(query_comparable);
        System.out.println(json2);
        Issue1834.IndexQuery_Comparable queryComparable = JSON.parseObject(json2, new com.alibaba.fastjson.TypeReference<Issue1834.IndexQuery_Comparable>() {});
    }

    static class IndexQuery_Comparable {
        List<? extends Comparable> keys;

        public List<? extends Comparable> getKeys() {
            return keys;
        }

        public void setKeys(List<? extends Comparable> keys) {
            this.keys = keys;
        }

        @Override
        public String toString() {
            return (("IndexQuery{" + "keys=") + (keys)) + '}';
        }
    }

    static class IndexQuery_Number {
        List<? extends Number> keys;

        public List<? extends Number> getKeys() {
            return keys;
        }

        public void setKeys(List<? extends Number> keys) {
            this.keys = keys;
        }

        @Override
        public String toString() {
            return (("IndexQuery{" + "keys=") + (keys)) + '}';
        }
    }
}

