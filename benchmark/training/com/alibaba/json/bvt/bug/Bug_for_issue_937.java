package com.alibaba.json.bvt.bug;


import junit.framework.TestCase;
import org.junit.Assert;


/**
 * Created by wuwen on 2016/12/7.
 */
public class Bug_for_issue_937 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{outPara:{name:\"user\"}}";
        Bug_for_issue_937.Out<Bug_for_issue_937.Info> out = Bug_for_issue_937.returnOut(json, Bug_for_issue_937.Info.class);
        Assert.assertEquals("user", out.getOutPara().getName());
    }

    public static class Out<T> {
        private T outPara;

        public void setOutPara(T t) {
            outPara = t;
        }

        public T getOutPara() {
            return outPara;
        }

        public Out() {
        }

        public Out(T t) {
            setOutPara(t);
        }
    }

    public static class Info {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

