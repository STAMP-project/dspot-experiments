package com.alibaba.json.bvt.bug;


import SerializerFeature.PrettyFormat;
import SerializerFeature.QuoteFieldNames;
import SerializerFeature.SkipTransientField;
import SerializerFeature.SortField;
import SerializerFeature.WriteDateUseDateFormat;
import SerializerFeature.WriteEnumUsingToString;
import SerializerFeature.WriteNonStringKeyAsString;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue166 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue166.VO vo = new Issue166.VO();
        vo.setbId("xxxx");
        String text = JSON.toJSONString(vo, WriteDateUseDateFormat, WriteEnumUsingToString, WriteNonStringKeyAsString, QuoteFieldNames, SkipTransientField, SortField, PrettyFormat);
        System.out.println(text);
        Issue166.VO vo2 = JSON.parseObject(text, Issue166.VO.class);
        Assert.assertEquals(vo.getbId(), vo2.getbId());
    }

    public static class VO {
        private String bId;

        public String getbId() {
            return bId;
        }

        public void setbId(String bId) {
            this.bId = bId;
        }
    }
}

