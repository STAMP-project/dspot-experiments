package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_hifor_issue_511 extends TestCase {
    public void test_for_issue() throws Exception {
        String resultString = "{" + ((((((((((((((((((((((((((((((((((((((("    \"errCode\": 0, " + "    \"errMsg\": \"ok\", ") + "    \"model\": {") + "        \"doctor\": {") + "            \"duty\": \"\u526f\u4e3b\u4efb\u533b\u5e08\", ") + "            \"glide\": \"20051010082558\", ") + "            \"mark\": \"0703010000\", ") + "            \"name\": \"\u67d0\u67d0\u67d0\", ") + "            \"office\": \"\u5c0f\u513f\u9aa8\u79d1\"") + "        }, ") + "        \"patientInfoList\": [") + "            {") + "                \"patient_Master_Card\": 1, ") + "                \"patient_addDate\": 1451097938410, ") + "                \"patient_age\": 30, ") + "                \"patient_id\": 347, ") + "                \"patient_idCard\": \"123321\", ") + "                \"patient_name\": \"\u5f20\u4e09\", ") + "                \"patient_s_ic_no\": \"123321\", ") + "                \"patient_sex\": \"1\", ") + "                \"patient_tel\": \"123\", ") + "                \"patient_userId\": 2, ") + "                \"s_ic_no\": \"123321\"") + "            }, ") + "            {") + "                \"patient_Master_Card\": 0, ") + "                \"patient_addDate\": 1454296296847, ") + "                \"patient_age\": 23, ") + "                \"patient_id\": 598, ") + "                \"patient_idCard\": \"123123\", ") + "                \"patient_name\": \"\u674e\u56db\", ") + "                \"patient_s_ic_no\": \"F10020000615011\", ") + "                \"patient_sex\": \"1\", ") + "                \"patient_tel\": \"18065212123\", ") + "                \"patient_userId\": 2, ") + "                \"s_ic_no\": \"F10020000615011\"") + "            }") + "        ]") + "    }") + "}");
        Bug_for_hifor_issue_511.TResult<Bug_for_hifor_issue_511.BookConfirmVo> result = JSON.parseObject(resultString, new com.alibaba.fastjson.TypeReference<Bug_for_hifor_issue_511.TResult<Bug_for_hifor_issue_511.BookConfirmVo>>() {});
        Assert.assertSame(Bug_for_hifor_issue_511.BookConfirmVo.class, result.model.getClass());
    }

    public static class TResult<T> {
        int errCode = 0;

        String errMsg = "ok";

        List<T> data = null;

        String stringData;

        Integer intData;

        T model;

        String url;

        public int getErrCode() {
            return errCode;
        }

        public void setErrCode(int errCode) {
            this.errCode = errCode;
        }

        public String getErrMsg() {
            return errMsg;
        }

        public void setErrMsg(String errMsg) {
            this.errMsg = errMsg;
        }

        public List<T> getData() {
            return data;
        }

        public void setData(List<T> data) {
            this.data = data;
        }

        public String getStringData() {
            return stringData;
        }

        public void setStringData(String stringData) {
            this.stringData = stringData;
        }

        public Integer getIntData() {
            return intData;
        }

        public void setIntData(Integer intData) {
            this.intData = intData;
        }

        public T getModel() {
            return model;
        }

        public void setModel(T model) {
            this.model = model;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class BookConfirmVo {
        String selectDay;

        String selectTime;

        Bug_for_hifor_issue_511.VW_NRE_Doctor doctor;

        List<Bug_for_hifor_issue_511.PatientInfoVo> patientInfoList;
    }

    public static class VW_NRE_Doctor {
        String glide;

        String name;

        String office;

        String mark;

        String duty;

        byte[] pic;
    }

    public static class PatientInfoVo extends Bug_for_hifor_issue_511.PatientInfo {
        String cols;

        String glide;

        String s_ic_no;

        // ???????
        @JSONField(serialize = false)
        public String getCols() {
            return cols;
        }

        // ????????
        @JSONField(deserialize = false)
        public void setCols(String cols) {
            this.cols = cols;
        }

        public String getGlide() {
            return glide;
        }

        public void setGlide(String glide) {
            this.glide = glide;
        }

        public String getS_ic_no() {
            return s_ic_no;
        }

        public void setS_ic_no(String s_ic_no) {
            this.s_ic_no = s_ic_no;
        }
    }

    public static class PatientInfo {
        Integer patient_id;

        Integer patient_userId;

        String patient_name;

        String patient_sex;

        Integer patient_age;

        String patient_tel;

        String patient_idCard;

        Date patient_addDate;

        Date patient_Date;

        String patient_s_ic_no;

        Integer patient_Master_Card;
    }
}

