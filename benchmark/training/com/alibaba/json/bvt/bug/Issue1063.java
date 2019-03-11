package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.sql.Timestamp;
import junit.framework.TestCase;


/**
 * Created by wenshao on 11/03/2017.
 */
public class Issue1063 extends TestCase {
    public void test_for_issue() throws Exception {
        long currentMillis = System.currentTimeMillis();
        Issue1063.TimestampBean bean = new Issue1063.TimestampBean();
        bean.setTimestamp(new Timestamp(currentMillis));
        String timestampJson = JSON.toJSONString(bean);
        // ???????
        Issue1063.TimestampBean beanOfJSON = JSON.parseObject(timestampJson, Issue1063.TimestampBean.class);
        // ????? java.lang.NumberFormatException
        JSONObject jsonObject = JSON.parseObject(timestampJson);
        Timestamp timestamp2 = jsonObject.getObject("timestamp", Timestamp.class);
        TestCase.assertEquals((currentMillis / 1000), ((timestamp2.getTime()) / 1000));
    }

    public static class TimestampBean {
        private Timestamp timestamp = null;

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }
    }
}

