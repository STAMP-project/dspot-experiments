package com.alibaba.json.bvt.issue_1100;


import com.alibaba.fastjson.JSON;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 10/05/2017.
 */
public class Issue1189 extends TestCase {
    public void test_for_issue() throws Exception {
        String str = new String("{\"headernotificationType\": \"PUSH\",\"headertemplateNo\": \"99\",\"headerdestination\": [{\"target\": \"all\",\"targetvalue\": \"all\"}],\"body\": [{\"title\": \"\u9884\u7ea6\u8d85\u65f6\",\"body\": \"\u60a8\u7684\u9884\u7ea6\u5df2\u7ecf\u8d85\u65f6\"}]}");
        Issue1189.JsonBean objeclt = JSON.parseObject(str, Issue1189.JsonBean.class);
        Map<String, String> list = objeclt.getBody();
        System.out.println(list.get("body"));
    }

    public static class JsonBean {
        private Map<String, String> body;

        private int headertemplateno;

        private Map<String, String> headerdestination;

        private String headernotificationtype;

        private String notificationType;

        public Map<String, String> getBody() {
            return body;
        }

        public void setBody(Map<String, String> body) {
            this.body = body;
        }

        public int getHeadertemplateno() {
            return headertemplateno;
        }

        public void setHeadertemplateno(int headertemplateno) {
            this.headertemplateno = headertemplateno;
        }

        public Map<String, String> getHeaderdestination() {
            return headerdestination;
        }

        public void setHeaderdestination(Map<String, String> headerdestination) {
            this.headerdestination = headerdestination;
        }

        public String getHeadernotificationtype() {
            return headernotificationtype;
        }

        public void setHeadernotificationtype(String headernotificationtype) {
            this.headernotificationtype = headernotificationtype;
        }

        public String getNotificationType() {
            return notificationType;
        }

        public void setNotificationType(String notificationType) {
            this.notificationType = notificationType;
        }

        public JsonBean(Map<String, String> body, int headertemplateno, Map<String, String> headerdestination, String headernotificationtype, String notificationType) {
            super();
            this.body = body;
            this.headertemplateno = headertemplateno;
            this.headerdestination = headerdestination;
            this.headernotificationtype = headernotificationtype;
            this.notificationType = notificationType;
        }

        public JsonBean() {
            super();
            // TODO Auto-generated constructor stub
        }
    }
}

