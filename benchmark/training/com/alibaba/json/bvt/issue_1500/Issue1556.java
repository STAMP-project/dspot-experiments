package com.alibaba.json.bvt.issue_1500;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import java.io.Serializable;
import java.util.Date;
import junit.framework.TestCase;


public class Issue1556 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1556.ClassForData classForData = new Issue1556.ClassForData();
        classForData.setDataName("dataname");
        Issue1556.SubCommonClass commonClass = new Issue1556.SubCommonClass(new Date());
        Issue1556.FirstSubClass firstSubClass = new Issue1556.FirstSubClass();
        firstSubClass.setAddr("It is addr");
        firstSubClass.setCommonInfo(commonClass);
        Issue1556.SecondSubClass secondSubClass = new Issue1556.SecondSubClass();
        secondSubClass.setName("It is name");
        secondSubClass.setCommonInfo(firstSubClass.getCommonInfo());
        classForData.setFirst(firstSubClass);
        classForData.setSecond(secondSubClass);
        Issue1556.ApiResult<Issue1556.ClassForData> apiResult = Issue1556.ApiResult.valueOfSuccess(classForData);
        ParserConfig config = new ParserConfig();
        config.setAutoTypeSupport(true);
        String jsonString = JSON.toJSONString(apiResult, WriteClassName);// ????SerializerFeature.DisableCircularReferenceDetect

        System.out.println(jsonString);
        Object obj = JSON.parse(jsonString, config);// ????Feature.DisableCircularReferenceDetect  ???? ????????  ???$ref ??????

        System.out.println(JSON.toJSONString(obj));
    }

    public static class ApiResult<T> implements Serializable {
        private String msg;

        private int code;

        private T data;

        public ApiResult() {
        }

        public ApiResult(int code, String msg, T data) {
            this.code = code;
            this.msg = msg;
            this.data = data;
        }

        public String getMsg() {
            return msg;
        }

        public int getCode() {
            return code;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public static <T> Issue1556.ApiResult<T> valueOfSuccess(T data) {
            return new Issue1556.ApiResult<T>(0, "Success", data);
        }
    }

    public static class ClassForData implements Serializable {
        private String dataName;

        private Issue1556.FirstSubClass first;

        private Issue1556.SecondSubClass second;

        public String getDataName() {
            return dataName;
        }

        public void setDataName(String dataName) {
            this.dataName = dataName;
        }

        public Issue1556.FirstSubClass getFirst() {
            return first;
        }

        public void setFirst(Issue1556.FirstSubClass first) {
            this.first = first;
        }

        public Issue1556.SecondSubClass getSecond() {
            return second;
        }

        public void setSecond(Issue1556.SecondSubClass second) {
            this.second = second;
        }
    }

    public static class FirstSubClass implements Serializable {
        private String addr;// ?????second???


        private Issue1556.SubCommonClass commonInfo;

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public Issue1556.SubCommonClass getCommonInfo() {
            return commonInfo;
        }

        public void setCommonInfo(Issue1556.SubCommonClass commonInfo) {
            this.commonInfo = commonInfo;
        }
    }

    public static class SecondSubClass implements Serializable {
        private String name;

        private Issue1556.SubCommonClass commonInfo;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Issue1556.SubCommonClass getCommonInfo() {
            return commonInfo;
        }

        public void setCommonInfo(Issue1556.SubCommonClass commonInfo) {
            this.commonInfo = commonInfo;
        }
    }

    public static class SubCommonClass implements Serializable {
        private Date demoDate;

        public SubCommonClass() {
        }

        public SubCommonClass(Date demoDate) {
            this.demoDate = demoDate;
        }

        public Date getDemoDate() {
            return demoDate;
        }

        public void setDemoDate(Date demoDate) {
            this.demoDate = demoDate;
        }
    }
}

