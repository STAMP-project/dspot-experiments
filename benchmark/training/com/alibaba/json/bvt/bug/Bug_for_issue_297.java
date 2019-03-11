package com.alibaba.json.bvt.bug;


import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_297 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_297.Response<Bug_for_issue_297.User> resp = parse("{\"id\":1001,\"values\":[{}]}", Bug_for_issue_297.User.class);
        Assert.assertEquals(1001, resp.id);
        Assert.assertEquals(1, resp.values.size());
        Assert.assertEquals(Bug_for_issue_297.User.class, resp.values.get(0).getClass());
    }

    public static class Response<T> {
        public long id;

        public List<T> values;
    }

    public static class User {}
}

