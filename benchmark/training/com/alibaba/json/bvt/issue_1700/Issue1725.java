package com.alibaba.json.bvt.issue_1700;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Issue1725 extends TestCase {
    public void test_for_issue() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("enumField", 0);
        Issue1725.AbstractBean bean = JSON.parseObject(JSON.toJSONString(map), Issue1725.ConcreteBean.class);
        TestCase.assertEquals(Issue1725.FieldEnum.A, bean.enumField);
    }

    public static class AbstractBean {
        public Issue1725.FieldEnum enumField;
    }

    public static class ConcreteBean extends Issue1725.AbstractBean {}

    public static enum FieldEnum {

        A,
        B;}
}

