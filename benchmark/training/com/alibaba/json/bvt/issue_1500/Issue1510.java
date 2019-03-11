package com.alibaba.json.bvt.issue_1500;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import junit.framework.TestCase;


public class Issue1510 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1510.Model model = JSON.parseObject("{\"startTime\":\"2017-11-04\",\"endTime\":\"2017-11-14\"}", Issue1510.Model.class);
        String text = JSON.toJSONString(model);
        TestCase.assertEquals("{\"endTime\":\"2017-11-14\",\"startTime\":\"2017-11-04\"}", text);
    }

    public static class Model {
        @JSONField(format = "yyyy-MM-dd")
        private Date startTime;

        @JSONField(format = "yyyy-MM-dd")
        private Date endTime;

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }
    }
}

