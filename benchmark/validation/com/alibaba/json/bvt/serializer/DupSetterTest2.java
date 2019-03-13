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
import junit.framework.TestCase;


public class DupSetterTest2 extends TestCase {
    public void testEnum() {
        DupSetterTest2.VO enumTest = new DupSetterTest2.VO();
        enumTest.setStatus(3);
        String json = JSONObject.toJSONString(enumTest);
        JSONObject.parseObject(json, DupSetterTest2.VO.class);
    }

    public static class VO {
        private Integer status;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public void setStatus(String status) {
            throw new IllegalStateException();
        }
    }
}

