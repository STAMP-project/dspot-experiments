package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_issue_676 extends TestCase {
    public void test_for_issue() throws Exception {
        JSON.parseObject("{\"modelType\":\"\"}", Bug_for_issue_676.MenuExpend.class);
    }

    public static class MenuExpend {
        public Bug_for_issue_676.ModelType modelType;
    }

    public static enum ModelType {

        A,
        B,
        C;}
}

