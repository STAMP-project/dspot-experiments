package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import java.util.Date;
import junit.framework.TestCase;


public class Issue184 extends TestCase {
    public void test_for_issue() throws Exception {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        Issue184.VO vo = new Issue184.VO();
        vo.setDate(new Date());
        String text = JSON.toJSONString(vo, filter);
        System.out.println(text);
    }

    private static class VO {
        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}

