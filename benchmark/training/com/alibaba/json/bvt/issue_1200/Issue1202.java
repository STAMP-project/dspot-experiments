package com.alibaba.json.bvt.issue_1200;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import junit.framework.TestCase;


/**
 * Created by wenshao on 16/05/2017.
 */
public class Issue1202 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "{\"date\":\"Apr 27, 2017 5:02:17 PM\"}";
        Issue1202.Model model = JSON.parseObject(text, Issue1202.Model.class);
        TestCase.assertNotNull(model.date);
        // assertEquals("{\"date\":\"Apr 27, 2017 5:02:17 PM\"}", JSON.toJSONString(model));
    }

    public static class Model {
        @JSONField(format = "MMM dd, yyyy h:mm:ss aa")
        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}

