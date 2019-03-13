package com.alibaba.json.bvt.ref;


import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import junit.framework.TestCase;


/**
 * Created by wenshao on 08/02/2017.
 */
public class RefTest_for_huanxige extends TestCase {
    public void test_for_ref() throws Exception {
        // ????????????????????????????$ref
        String jsonStr = "{\"displayName\":\"\u7070\u5ea6\u53d1\u5e03\",\"id\":221," + (("\"name\":\"\u7070\u5ea6\",\"processInsId\":48,\"processInstance\":{\"$ref\":\"$" + ".lastSubProcessInstence.parentProcess\"},\"status\":1,\"success\":true,") + "\"tail\":true,\"type\":\"gray\"}");
        RefTest_for_huanxige.ProcessNodeInstanceDto a = JSON.parseObject(jsonStr, RefTest_for_huanxige.ProcessNodeInstanceDto.class);// status?????

        TestCase.assertNotNull(a.status);
        TestCase.assertEquals(1, a.status.intValue());
    }

    public static class ProcessNodeInstanceDto implements Serializable {
        private Long id;

        private Long processInsId;

        private String name;

        private String displayName;

        private Integer status;

        private String type;

        private Boolean success;

        private Boolean tail;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getProcessInsId() {
            return processInsId;
        }

        public void setProcessInsId(Long processInsId) {
            this.processInsId = processInsId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public Boolean getTail() {
            return tail;
        }

        public void setTail(Boolean tail) {
            this.tail = tail;
        }
    }
}

