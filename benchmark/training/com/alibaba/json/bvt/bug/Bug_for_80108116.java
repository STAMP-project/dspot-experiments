package com.alibaba.json.bvt.bug;


import JSON.defaultTimeZone;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_80108116 extends TestCase {
    public void test_for_dateFormat() throws Exception {
        Bug_for_80108116.VO vo = new Bug_for_80108116.VO();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", JSON.defaultLocale);
        dateFormat.setTimeZone(defaultTimeZone);
        vo.setDate(dateFormat.parse("2012-07-12"));
        List<Bug_for_80108116.VO> voList = new ArrayList<Bug_for_80108116.VO>();
        voList.add(vo);
        String text = JSON.toJSONString(voList);
        Assert.assertEquals("[{\"date\":\"2012-07-12\"}]", text);
    }

    public static class VO {
        private Date date;

        @JSONField(format = "yyyy-MM-dd")
        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}

