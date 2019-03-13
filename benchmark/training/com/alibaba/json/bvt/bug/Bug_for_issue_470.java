package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_470 extends TestCase {
    public void test_for_issue() throws Exception {
        List<Bug_for_issue_470.VO> list = JSON.parseArray("[{\"value\":null}]", Bug_for_issue_470.VO.class);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(false, list.get(0).value);
    }

    public void test_for_issue_private() throws Exception {
        List<Bug_for_issue_470.V1> list = JSON.parseArray("[{\"value\":null}]", Bug_for_issue_470.V1.class);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(false, list.get(0).value);
    }

    public void test_for_issue_method() throws Exception {
        List<Bug_for_issue_470.V2> list = JSON.parseArray("[{\"value\":null}]", Bug_for_issue_470.V2.class);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(false, list.get(0).value);
    }

    public static class VO {
        public boolean value;
    }

    private static class V1 {
        public boolean value;
    }

    public static class V2 {
        private boolean value;

        public boolean isValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }
}

