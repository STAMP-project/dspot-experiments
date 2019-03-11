package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.sql.Timestamp;
import junit.framework.TestCase;


public class Bug_for_issue_414 extends TestCase {
    public void test_for_issue() throws Exception {
        String jsonStr = "{publishedDate:\"2015-09-07\"}";
        Bug_for_issue_414.TestEntity news = JSON.parseObject(jsonStr, Bug_for_issue_414.TestEntity.class);
        System.out.println(news.getPublishedDate());
    }

    public static class TestEntity {
        private Timestamp publishedDate;

        public Timestamp getPublishedDate() {
            return publishedDate;
        }

        public void setPublishedDate(Timestamp publishedDate) {
            this.publishedDate = publishedDate;
        }
    }
}

