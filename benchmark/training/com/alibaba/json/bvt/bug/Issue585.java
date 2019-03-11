package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue585 extends TestCase {
    private String original = JSON.DEFAULT_TYPE_KEY;

    private ParserConfig originalConfig = ParserConfig.global;

    public void test_for_issue() throws Exception {
        String cc = "{\"mySpace\":\"com.alibaba.json.bvt.bug.Issue585$Result\",\"attachments\":{\"mySpace\":\"java.util.HashMap\",\"timeout\":5000,\"consumeApp\":\"multiGroupTestServer\"},\"status\":0}";
        byte[] bytes = cc.getBytes("utf-8");
        Issue585.Result res = ((Issue585.Result) (this.deserialize(bytes)));
        Assert.assertEquals(0, res.getStatus());
    }

    public static class Result {
        private int status;

        private Object value;

        private Map<String, Object> attachments = new HashMap<String, Object>(2);

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Map<String, Object> getAttachments() {
            return attachments;
        }

        public void setAttachments(Map<String, Object> attachments) {
            this.attachments = attachments;
        }
    }
}

