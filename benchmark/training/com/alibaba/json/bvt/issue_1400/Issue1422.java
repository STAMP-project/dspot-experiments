package com.alibaba.json.bvt.issue_1400;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import junit.framework.TestCase;


public class Issue1422 extends TestCase {
    public void test_for_issue() throws Exception {
        String strOk = "{\"v\": 111}";
        Issue1422.Foo ok = JSON.parseObject(strOk, Issue1422.Foo.class);
        TestCase.assertFalse(ok.v);
    }

    public void test_for_issue_reader() throws Exception {
        String strBad = "{\"v\": 111}";
        Issue1422.Foo bad = new JSONReader(new StringReader(strBad)).readObject(Issue1422.Foo.class);
        TestCase.assertFalse(bad.v);
    }

    public void test_for_issue_1() throws Exception {
        String strBad = "{\"v\":111}";
        Issue1422.Foo bad = JSON.parseObject(strBad, Issue1422.Foo.class);
        TestCase.assertFalse(bad.v);
    }

    public void test_for_issue_1_reader() throws Exception {
        String strBad = "{\"v\":111}";
        Issue1422.Foo bad = new JSONReader(new StringReader(strBad)).readObject(Issue1422.Foo.class);
        TestCase.assertFalse(bad.v);
    }

    public static class Foo {
        public boolean v;
    }
}

