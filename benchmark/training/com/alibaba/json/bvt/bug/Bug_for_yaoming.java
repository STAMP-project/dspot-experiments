package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class Bug_for_yaoming extends TestCase {
    public void test_bug() throws Exception {
        Bug_for_yaoming.SimpleHttpReuslt v = new Bug_for_yaoming.SimpleHttpReuslt();
        v.setErrorMessage(new ArrayList<Bug_for_yaoming.SimpleHttpReuslt.ErrorMessage>());
        v.getErrorMessage().add(new Bug_for_yaoming.SimpleHttpReuslt.ErrorMessage());
        String text = JSON.toJSONString(v);
        text = "{\"content\":{\"versionModelList\":[{\"version\":\"260\",\"currentVersion\":true,\"versionComment\":\"testVersion\",\"warSize\":\"43130185\",\"appIdentifier\":\"parent\",\"uploadTime\":1375850777000},{\"version\":\"247\",\"currentVersion\":false,\"versionComment\":\"testVersion\",\"warSize\":\"43130186\",\"appIdentifier\":\"parent\",\"uploadTime\":1375634817000},{\"version\":\"246\",\"currentVersion\":false,\"versionComment\":\"testVersion\",\"warSize\":\"43130186\",\"appIdentifier\":\"parent\",\"uploadTime\":1375613193000},{\"version\":\"245\",\"currentVersion\":false,\"versionComment\":\"testVersion\",\"warSize\":\"43130185\",\"appIdentifier\":\"parent\",\"uploadTime\":1375591593000},{\"version\":\"244\",\"currentVersion\":false,\"versionComment\":\"testVersion\",\"warSize\":\"43130186\",\"appIdentifier\":\"parent\",\"uploadTime\":1375569999000},{\"version\":\"243\",\"currentVersion\":false,\"versionComment\":\"testVersion\",\"warSize\":\"43130185\",\"appIdentifier\":\"parent\",\"uploadTime\":1375548418000}],\"exceptionCode\":0},\"hasError\":false}";
        JSON.parseObject(text, Bug_for_yaoming.SimpleHttpReuslt.class);
    }

    public static class SimpleHttpReuslt {
        private String content;

        private Boolean hasError;

        private List<Bug_for_yaoming.SimpleHttpReuslt.ErrorMessage> errorMessage;

        public String getContent() {
            return content;
        }

        public Boolean isHasError() {
            return hasError;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setHasError(Boolean hasError) {
            this.hasError = hasError;
        }

        public List<Bug_for_yaoming.SimpleHttpReuslt.ErrorMessage> getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(List<Bug_for_yaoming.SimpleHttpReuslt.ErrorMessage> errorMessage) {
            this.errorMessage = errorMessage;
        }

        public static class ErrorMessage {
            private String field;

            private String code;

            private String msg;

            public String getField() {
                return field;
            }

            public String getCode() {
                return code;
            }

            public String getMsg() {
                return msg;
            }

            public void setField(String field) {
                this.field = field;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }
        }
    }
}

