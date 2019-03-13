package com.alibaba.json.bvt.issue_1200;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


/**
 * Created by kimmking on 27/06/2017.
 */
public class Issue1293 extends TestCase {
    public void test_for_issue() {
        String data = "{\"idType\":\"123123\",\"userType\":\"134\",\"count\":\"123123\"}";
        {
            Issue1293.Test test = JSON.parseObject(data, Issue1293.Test.class);
            TestCase.assertNull(test.idType);
            TestCase.assertNull(test.userType);
        }
        Issue1293.Test test = JSON.parseObject(data, Issue1293.Test.class);
        TestCase.assertNull(test.idType);
        TestCase.assertNull(test.userType);
    }

    static class Test {
        private long count;

        private Issue1293.IdType idType;

        private Issue1293.UserType userType;

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }

        public Issue1293.IdType getIdType() {
            return idType;
        }

        public void setIdType(Issue1293.IdType idType) {
            this.idType = idType;
        }

        public Issue1293.UserType getUserType() {
            return userType;
        }

        public void setUserType(Issue1293.UserType userType) {
            this.userType = userType;
        }
    }

    static enum IdType {

        A,
        B;}

    static enum UserType {

        C,
        D;}
}

