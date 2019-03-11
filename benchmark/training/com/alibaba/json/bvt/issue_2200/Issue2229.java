package com.alibaba.json.bvt.issue_2200;


import com.alibaba.fastjson.JSON;
import java.util.Date;
import junit.framework.TestCase;


public class Issue2229 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue2229.Jon jon = JSON.parseObject("{\"dStr\":\"         hahahaha        \",\"user\":{\"createtime\":null,\"id\":0,\"username\":\"  asdfsadf  asdf  asdf  \"}}", Issue2229.Jon.class);
        TestCase.assertEquals("  asdfsadf  asdf  asdf  ", jon.user.username);
    }

    public void test_for_issue1() throws Exception {
        Issue2229.Jon jon1 = JSON.parseObject("{'dStr':'         hahahaha        ','user':{'createtime':null,'id':0,'username':'  asdfsadf  asdf  asdf  '}}", Issue2229.Jon.class);
        TestCase.assertEquals("  asdfsadf  asdf  asdf  ", jon1.user.username);
    }

    public static class Jon {
        public String dStr;

        public Issue2229.User user;
    }

    public static class User {
        public int id;

        public Date createtime;

        public String username;
    }
}

