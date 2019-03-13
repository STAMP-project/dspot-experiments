package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_285 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_285.VO vo = new Bug_for_issue_285.VO();
        vo.v1 = new Bug_for_issue_285.V1();
        vo.v1.v2 = new Bug_for_issue_285.V2();
        vo.v1.v2.v3 = new Bug_for_issue_285.V3();
        vo.v1.v2.v3.v4 = new Bug_for_issue_285.V4();
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        filter.setMaxLevel(2);
        String text = JSON.toJSONString(vo, filter);
        Assert.assertEquals("{\"v1\":{\"v2\":{}}}", text);
    }

    public static class VO {
        public Bug_for_issue_285.V1 v1;
    }

    public static class V1 {
        public Bug_for_issue_285.V2 v2;
    }

    public static class V2 {
        public Bug_for_issue_285.V3 v3;
    }

    public static class V3 {
        public Bug_for_issue_285.V4 v4;
    }

    public static class V4 {}
}

