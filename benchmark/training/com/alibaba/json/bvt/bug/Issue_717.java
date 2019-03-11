package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue_717 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue_717.Group group = new Issue_717.Group();
        group.setId(0L);
        group.setNAME("admin");
        group.setAUTHORITY("administrors");
        String json = JSON.toJSONString(group);
        Assert.assertEquals("{\"ID\":0,\"nAME\":\"admin\"}", json);
    }

    public static class Group {
        @JSONField(name = "ID")
        private Long id;

        private String NAME;

        @JSONField(serialize = false, deserialize = false)
        private String AUTHORITY;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNAME() {
            return NAME;
        }

        public void setNAME(String NAME) {
            this.NAME = NAME;
        }

        public String getAUTHORITY() {
            return AUTHORITY;
        }

        public void setAUTHORITY(String AUTHORITY) {
            this.AUTHORITY = AUTHORITY;
        }
    }
}

