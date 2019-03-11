package com.alibaba.json.bvt.issue_1300;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 05/08/2017.
 */
public class Issue1363 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1363.DataSimpleVO a = new Issue1363.DataSimpleVO("a", 1);
        Issue1363.DataSimpleVO b = new Issue1363.DataSimpleVO("b", 2);
        b.value = a;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(a.name, a);
        b.value1 = map;
        String jsonStr = JSON.toJSONString(b);
        System.out.println(jsonStr);
        Issue1363.DataSimpleVO obj = JSON.parseObject(jsonStr, Issue1363.DataSimpleVO.class);
        TestCase.assertEquals(jsonStr, JSON.toJSONString(obj));
    }

    public void test_for_issue_1() throws Exception {
        Issue1363.DataSimpleVO a = new Issue1363.DataSimpleVO("a", 1);
        Issue1363.DataSimpleVO b = new Issue1363.DataSimpleVO("b", 2);
        b.value1 = a;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(a.name, a);
        b.value = map;
        String jsonStr = JSON.toJSONString(b);
        System.out.println(jsonStr);
        Issue1363.DataSimpleVO obj = JSON.parseObject(jsonStr, Issue1363.DataSimpleVO.class);
        System.out.println(obj.toString());
        TestCase.assertNotNull(obj.value1);
        TestCase.assertEquals(jsonStr, JSON.toJSONString(obj));
    }

    public static class DataSimpleVO {
        public String name;

        public Object value;

        public Object value1;

        public DataSimpleVO() {
        }

        public DataSimpleVO(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return ((((("DataSimpleVO [name=" + (name)) + ", value=") + (value)) + ", value1=") + (value1)) + "]";
        }
    }
}

