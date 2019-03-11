package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wuwen on 2017/2/16.
 */
public class Issue1013 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1013.TestDomain domain = new Issue1013.TestDomain();
        String json = JSON.toJSONString(domain);
        Issue1013.TestDomain domain1 = JSON.parseObject(json, Issue1013.TestDomain.class);
        TestCase.assertEquals(domain.getList(), domain1.getList());
    }

    public void test_for_issue_1() throws Exception {
        Issue1013.TestDomain domain1 = JSON.parseObject("{\"list\":[]}", Issue1013.TestDomain.class);
        Issue1013.TestDomain domain2 = JSON.parseObject("{\"list\":[1, 2]}", Issue1013.TestDomain.class);
        TestCase.assertEquals(0, domain1.getList().size());
        TestCase.assertEquals(Arrays.asList(1, 2), domain2.getList());
    }

    public void test_for_issue_2() throws Exception {
        Issue1013.TestDomain domain1 = JSON.parseObject("{\"list\":null}", Issue1013.TestDomain.class);
        TestCase.assertEquals(1, domain1.getList().size());
    }

    public void test_for_issue3() throws Exception {
        Issue1013.TestDomain2 domain = new Issue1013.TestDomain2();
        String json = JSON.toJSONString(domain);
        Issue1013.TestDomain2 domain1 = JSON.parseObject(json, Issue1013.TestDomain2.class);
        TestCase.assertEquals(domain.list, domain1.list);
    }

    public void test_for_issue_4() throws Exception {
        Issue1013.TestDomain2 domain1 = JSON.parseObject("{\"list\":[1, 2]}", Issue1013.TestDomain2.class);
        TestCase.assertEquals(Arrays.asList(1, 2), domain1.list);
    }

    public void test_for_issue_5() throws Exception {
        Issue1013.TestDomain2 domain1 = JSON.parseObject("{\"list\":null}", Issue1013.TestDomain2.class);
        TestCase.assertNull(domain1.list);
    }

    public void test_for_issue_6() throws Exception {
        Issue1013.TestDomain3 domain3 = JSON.parseObject("{\"list\":null}", Issue1013.TestDomain3.class);
        TestCase.assertNull(domain3.list);
    }

    static class TestDomain {
        private List<Integer> list;

        public TestDomain() {
            list = new ArrayList<Integer>();
            list.add(1);
        }

        public List<Integer> getList() {
            return list;
        }
    }

    static class TestDomain2 {
        public List<Integer> list;

        public TestDomain2() {
            list = new ArrayList<Integer>();
            list.add(1);
        }
    }

    static class TestDomain3 {
        private List<Integer> list;

        public TestDomain3() {
            list = new ArrayList<Integer>();
            list.add(1);
        }

        public List<Integer> getList() {
            return list;
        }

        public void setList(List<Integer> list) {
            this.list = list;
        }
    }
}

