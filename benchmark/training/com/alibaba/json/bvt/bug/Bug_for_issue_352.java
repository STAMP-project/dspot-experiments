package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_issue_352 extends TestCase {
    public void test_for_issue() throws Exception {
        Bug_for_issue_352.VO vo = new Bug_for_issue_352.VO();
        vo.name = "??";
        String text = JSON.toJSONString(vo);
        Assert.assertEquals("{\"index\":0,\"\u540d\":\"\u5f20\u4e09\"}", text);
        Bug_for_issue_352.VO v1 = JSON.parseObject(text, Bug_for_issue_352.VO.class);
        Assert.assertEquals(vo.name, v1.name);
    }

    public static class VO {
        public int index;

        @JSONField(name = "?")
        public String name;
    }
}

