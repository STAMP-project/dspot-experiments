package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug2 extends TestCase {
    public void test_0() throws Exception {
        Bug2.Entity entity = new Bug2.Entity();
        entity.setArticles(Collections.singletonList(new Bug2.Article()));
        String jsonString = JSON.toJSONString(entity);
        System.out.println(jsonString);
        Bug2.Entity entity2 = JSON.parseObject(jsonString, Bug2.Entity.class);
        Assert.assertEquals(entity.getArticles().size(), entity2.getArticles().size());
    }

    public static class Entity {
        private List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        private List<Bug2.Article> articles = null;

        public List<HashMap<String, String>> getList() {
            return list;
        }

        public void setList(List<HashMap<String, String>> list) {
            this.list = list;
        }

        public List<Bug2.Article> getArticles() {
            return articles;
        }

        public void setArticles(List<Bug2.Article> articles) {
            this.articles = articles;
        }
    }

    public static class Article {
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}

