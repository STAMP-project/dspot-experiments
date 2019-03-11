package com.alibaba.json.bvt.issue_1300;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


/**
 * Created by wenshao on 23/07/2017.
 */
public class Issue1320 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1320.SSOToken token = new Issue1320.SSOToken();
        JSON.toJSONString(token);
    }

    @SuppressWarnings("serial")
    public static class SSOToken extends Issue1320.Token {
        /* ???? */
        private Integer type;

        /* ?? */
        private String data;

        /**
         * <p>
         * ??????? fastjson ?????????????? cookie ? ?
         * </p>
         * <p>
         * ????????????????????????
         * </p>
         */
        @JSONField(serialize = false)
        private Object object;

        public SSOToken() {
            // this.setApp(SSOConfig.getInstance().getRole());
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        /**
         * ?????????????
         */
        @SuppressWarnings("unchecked")
        public <T> T getCacheObject() {
            return ((T) (this.getObject()));
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }
    }

    public static class Token {}
}

