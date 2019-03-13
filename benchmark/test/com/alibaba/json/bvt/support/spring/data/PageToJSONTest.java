package com.alibaba.json.bvt.support.spring.data;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;


public class PageToJSONTest extends TestCase {
    public void test_page() throws Exception {
        List<PageToJSONTest.Post> postList = new ArrayList<PageToJSONTest.Post>();
        {
            postList.add(new PageToJSONTest.Post(1001));
        }
        Page<PageToJSONTest.Post> page = new PageImpl(postList);
        JSONObject obj = ((JSONObject) (JSON.toJSON(page)));
        Assert.assertNotNull(obj);
        Assert.assertEquals(1, obj.getJSONArray("content").size());
    }

    public static class Post {
        public int id;

        public Post() {
        }

        public Post(int id) {
            this.id = id;
        }
    }
}

