package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Bug_for_lenolix_6 extends TestCase {
    public void test_for_objectKey() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", 1);
        map.put("name", "leno.lix");
        map.put("birthday", new Date());
        map.put("gmtCreate", new java.sql.Date(new Date().getTime()));
        map.put("gmtModified", new Timestamp(new Date().getTime()));
        String userJSON = JSON.toJSONString(map, WriteClassName, WriteMapNullValue);
        System.out.println(userJSON);
        Object object = JSON.parse(userJSON);
    }
}

