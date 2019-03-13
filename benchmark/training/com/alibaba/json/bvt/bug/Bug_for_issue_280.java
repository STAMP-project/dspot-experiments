package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_280 extends TestCase {
    public void test_for_issue() throws Exception {
        TypeReference<Bug_for_issue_280.Respone<Bug_for_issue_280.User>> type = new TypeReference<Bug_for_issue_280.Respone<Bug_for_issue_280.User>>() {};
        Bug_for_issue_280.Respone<Bug_for_issue_280.User> resp = JSON.parseObject("{\"code\":\"\",\"data\":\"\",\"msg\":\"\"}", type);
        Assert.assertNull(resp.data);
    }

    public static class Respone<T> {
        public String code;

        public String msg;

        public T data;
    }

    public static class User {}
}

