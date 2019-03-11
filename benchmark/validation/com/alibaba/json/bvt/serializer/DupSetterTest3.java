/**
 * www.yiji.com Inc.
 * Copyright (c) 2014 All Rights Reserved
 */
/**
 * ????:
 * qzhanbo@yiji.com 2015-03-01 00:55 ??
 */
package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


public class DupSetterTest3 extends TestCase {
    public void testEnum() {
        DupSetterTest3.VO enumTest = new DupSetterTest3.VO();
        enumTest.status = 3;
        String json = JSONObject.toJSONString(enumTest);
        JSONObject.parseObject(json, DupSetterTest3.VO.class);
    }

    public static class VO {
        public Integer status;

        @JSONField(name = "status")
        public Integer status2;
    }
}

