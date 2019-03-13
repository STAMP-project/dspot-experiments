package com.alibaba.json.bvt.issue_1400;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;


public class Issue1496 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = JSON.toJSONString(Issue1496.SetupStatus.FINAL_TRAIL);
        TestCase.assertEquals("{\"canRefuse\":true,\"code\":3,\"declaringClass\":\"com.alibaba.json.bvt.issue_1400.Issue1496$SetupStatus\",\"first\":false,\"last\":false,\"name\":\"FINAL_TRAIL\",\"nameCn\":\"\u516c\u76ca\u59d4\u5458\u4f1a/\u7406\u4e8b\u4f1a/\u7406\u4e8b\u957f\u5ba1\u6838\"}", json);
    }

    public interface ISetupStatusInfo {
        List<Issue1496.SetupStatus> nextList();

        Boolean isFirst();

        Boolean isLast();
    }

    public interface ISetupStatusProcess {
        /**
         *
         *
         * @return 
         */
        Issue1496.SetupStatus refuse();

        /**
         * ????????null
         *
         * @param name
         * 		
         * @return 
         */
        Issue1496.SetupStatus next(String name);
    }

    @JSONType(serializeEnumAsJavaBean = true)
    public enum SetupStatus implements Issue1496.ISetupStatusInfo , Issue1496.ISetupStatusProcess {

        EDIT(0, "EDIT", "???") {
            public List<Issue1496.SetupStatus> nextList() {
                return Arrays.asList(Issue1496.SetupStatus.FIRST_TRAIL);
            }

            @Override
            public Boolean isFirst() {
                return true;
            }

            @Override
            public Issue1496.SetupStatus refuse() {
                return Issue1496.SetupStatus.EDIT;
            }
        },
        FIRST_TRAIL(1, "FIRST_TRAIL", "??") {
            public List<Issue1496.SetupStatus> nextList() {
                return Arrays.asList(Issue1496.SetupStatus.EXPERT, Issue1496.SetupStatus.FINAL_TRAIL);
            }

            @Override
            public Issue1496.SetupStatus refuse() {
                return Issue1496.SetupStatus.EDIT;
            }
        },
        EXPERT(2, "EXPERT", "??????", false) {
            public List<Issue1496.SetupStatus> nextList() {
                return Arrays.asList(Issue1496.SetupStatus.FINAL_TRAIL);
            }
        },
        FINAL_TRAIL(3, "FINAL_TRAIL", "?????/???/?????") {
            public List<Issue1496.SetupStatus> nextList() {
                return Arrays.asList(Issue1496.SetupStatus.PASS);
            }

            @Override
            public Issue1496.SetupStatus refuse() {
                return Issue1496.SetupStatus.EDIT;
            }
        },
        PASS(4, "PASS", "????", false) {
            public List<Issue1496.SetupStatus> nextList() {
                return Arrays.asList(Issue1496.SetupStatus.SIGN);
            }
        },
        SIGN(5, "SIGN", "????", false) {
            @Override
            public List<Issue1496.SetupStatus> nextList() {
                return Arrays.asList(Issue1496.SetupStatus.ACTIVE);
            }
        },
        ACTIVE(6, "ACTIVE", "??") {
            @Override
            public List<Issue1496.SetupStatus> nextList() {
                return null;
            }

            @Override
            public Boolean isLast() {
                return true;
            }
        };
        private int code;

        private String name;

        private String nameCn;

        private boolean canRefuse;

        SetupStatus(int code, String name, String nameCn) {
            this.code = code;
            this.name = name;
            this.nameCn = nameCn;
            this.canRefuse = true;
        }

        SetupStatus(int code, String name, String nameCn, boolean canRefuse) {
            this.code = code;
            this.name = name;
            this.nameCn = nameCn;
            this.canRefuse = canRefuse;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNameCn() {
            return nameCn;
        }

        public void setNameCn(String nameCn) {
            this.nameCn = nameCn;
        }

        public boolean isCanRefuse() {
            return canRefuse;
        }

        public void setCanRefuse(boolean canRefuse) {
            this.canRefuse = canRefuse;
        }

        public static Issue1496.SetupStatus getFromCode(Integer code) {
            if (code == null) {
                return null;
            }
            for (Issue1496.SetupStatus status : Issue1496.SetupStatus.values()) {
                if ((status.code) == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException(("unknown SetupStatus enumeration code:" + code));
        }

        public static Issue1496.SetupStatus getFromName(String name) {
            if (name == null) {
                return null;
            }
            for (Issue1496.SetupStatus status : Issue1496.SetupStatus.values()) {
                if (status.name.equals(name)) {
                    return status;
                }
            }
            return null;
        }

        public Boolean isFirst() {
            return false;
        }

        public Boolean isLast() {
            return false;
        }

        public Issue1496.SetupStatus refuse() {
            return null;
        }

        public Issue1496.SetupStatus next(String name) {
            Issue1496.SetupStatus status = Issue1496.SetupStatus.getFromName(name);
            return (name != null) && (this.nextList().contains(status)) ? status : null;
        }

        @Override
        public String toString() {
            return (((((((((("SetupStatus{" + "code=") + (code)) + ", name='") + (name)) + '\'') + ", nameCn='") + (nameCn)) + '\'') + ", canRefuse=") + (canRefuse)) + '}';
        }
    }
}

