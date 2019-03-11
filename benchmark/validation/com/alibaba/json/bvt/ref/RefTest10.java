package com.alibaba.json.bvt.ref;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Assert;


public class RefTest10 extends TestCase {
    public void test_bug_for_wanglin() throws Exception {
        String text = "{ \"schedulerCluster\": \"xyQuestionImport\", \"log\": { \"abilityServiceId\": \"-1\", \"abilityServiceVersionId\": \"-1\", \"createTime\": 1456832040060, \"ip\": \"192.168.1.71\", \"jobDataMap\": { \"com.fjhb.context.v1.Context\": { \"domain\": \"dev.medical.com\", \"gUID\": \"25c5e12ec19946e8a6850237cd8182de\", \"ip\": \"127.0.0.1\", \"organizationId\": \"-1\", \"platformId\": \"2c9180e5520a5e70015214fb2849000a\", \"platformVersionId\": \"2c9180e5520a6063015214fc062d0006\", \"projectId\": \"2c9180e5520a60630152150b0b4a000e\", \"recordChain\": true, \"requestUrl\": \"http://dev.medical.com:9009/gateway/web/admin/questionIE/questionImport\", \"subProjectId\": \"2c9180e5520a606301521596e7070018\", \"test\": false, \"unitId\": \"2c9180e54e7580cd014e801793720010\", \"userId\": \"4028823c4e850e60014e853115dc00sa\" }, \"questionImportDto\": { \"filePath\": \"/work/A4Mode2.xls\", \"organizationId\": \"-1\", \"platformId\": \"2c9180e5520a5e70015214fb2849000a\", \"platformVersionId\": \"2c9180e5520a6063015214fc062d0006\", \"projectId\": \"2c9180e5520a60630152150b0b4a000e\", \"subProjectId\": \"2c9180e5520a606301521596e7070018\", \"unitId\": \"-1\" }, \"questionExcelModeType\": 2, \"user.job.current.execute.key\": \"402881c75331cc62015331e732ce0002\" }, \"jobGroup\": \"xyQuestionImport\", \"jobName\": \"questionImport\", \"key\": \"402881c75331cc62015331e732ce0002\", \"organizationId\": \"-1\", \"platformId\": \"-1\", \"platformVersionId\": \"-1\", \"projectId\": \"-1\", \"remark\": \"\\\"xyQuestionImport\\\"\u96c6\u7fa4\u4e2d\u540d\u4e3a:\\\"402881c75331cc62015331ccecbc0000\\\"\u7684\u8c03\u5ea6\u5668\u5f00\u59cb\u8fd0\u884c\u6b64\u4efb\u52a1\", \"status\": \"toExecuted\", \"subProjectId\": \"-1\", \"unitId\": \"-1\", \"userId\": \"4028823c4e850e60014e853115dc00sa\" }, \"context\": { \"$ref\": \"$.log.jobDataMap.com.fjhb.context.v1.Context\" }, \"schedulerName\": \"402881c75331cc62015331ccecbc0000\" }";
        JSONObject jsonObj = JSON.parseObject(text);
        Assert.assertSame(jsonObj.getJSONObject("log").getJSONObject("jobDataMap").get("com.fjhb.context.v1.Context"), jsonObj.get("context"));
    }

    public static class VO {
        private RefTest10.A a;

        private Set<RefTest10.A> values = new HashSet<RefTest10.A>();

        public RefTest10.A getA() {
            return a;
        }

        public void setA(RefTest10.A a) {
            this.a = a;
        }

        public Set<RefTest10.A> getValues() {
            return values;
        }

        public void setValues(Set<RefTest10.A> values) {
            this.values = values;
        }
    }

    public static class A {}
}

