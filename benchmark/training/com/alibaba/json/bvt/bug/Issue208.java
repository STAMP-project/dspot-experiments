package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Issue208 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue208.VO vo = new Issue208.VO();
        vo.?? = 1001;
        vo.?? = "??";
        String text = JSON.toJSONString(vo);
        JSON.parseObject(text, Issue208.VO.class);
    }

    public void test_for_issue_1() throws Exception {
        Issue208.?? vo = new Issue208.??();
        vo.?? = 1001;
        vo.?? = "??";
        String text = JSON.toJSONString(vo);
        JSON.parseObject(text, Issue208.??.class);
    }

    public static class VO {
        public int ??;

        public String ??;
    }

    public static class ?? {
        public int ??;

        public String ??;
    }
}

