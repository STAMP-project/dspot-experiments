package com.alibaba.json.bvt.issue_1700;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import java.util.Date;
import junit.framework.TestCase;


public class Issue1769 extends TestCase {
    public void test_for_issue() throws Exception {
        byte[] newby = "{\"beginTime\":\"420180319160440\"}".getBytes();
        Issue1769.QueryTaskResultReq rsp3 = JSON.parseObject(newby, Issue1769.QueryTaskResultReq.class);
        TestCase.assertEquals("{\"beginTime\":\"152841225111920\"}", new String(JSON.toJSONBytes(rsp3)));
    }

    @JSONType(orders = { "beginTime" })
    public static class QueryTaskResultReq {
        private Date beginTime;

        @JSONField(format = "yyyyMMddHHmmss")
        public Date getBeginTime() {
            return beginTime;
        }

        public void setBeginTime(Date beginTime) {
            this.beginTime = beginTime;
        }
    }
}

