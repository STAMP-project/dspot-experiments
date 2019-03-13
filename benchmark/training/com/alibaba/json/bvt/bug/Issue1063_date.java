package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.sql.Date;
import java.sql.Timestamp;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/03/2017.
 */
public class Issue1063_date extends TestCase {
    public void test_for_issue() throws Exception {
        long currentMillis = System.currentTimeMillis();
        Issue1063_date.TimestampBean bean = new Issue1063_date.TimestampBean();
        bean.setTimestamp(new Date(currentMillis));
        String timestampJson = JSON.toJSONString(bean);
        // ???????
        Issue1063_date.TimestampBean beanOfJSON = JSON.parseObject(timestampJson, Issue1063_date.TimestampBean.class);
        // ????? java.lang.NumberFormatException
        JSONObject jsonObject = JSON.parseObject(timestampJson);
        Timestamp timestamp2 = jsonObject.getObject("timestamp", Timestamp.class);
        TestCase.assertEquals((currentMillis / 1000), ((timestamp2.getTime()) / 1000));
    }

    public static class TimestampBean {
        private Date timestamp = null;

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }
    }
}

