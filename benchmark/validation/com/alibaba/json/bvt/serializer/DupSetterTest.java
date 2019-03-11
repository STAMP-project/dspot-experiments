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


public class DupSetterTest extends TestCase {
    public void testEnum() {
        DupSetterTest.VO enumTest = new DupSetterTest.VO();
        enumTest.setStatus(3);
        String json = JSONObject.toJSONString(enumTest);
        JSONObject.parseObject(json, DupSetterTest.VO.class);
    }

    public static class VO {
        private Integer status;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public void setStatus(DupSetterTest.Status status) {
            throw new IllegalStateException();
        }
    }

    public static enum Status {

        ENABLE(1);
        private Integer code;

        Status(Integer code) {
            this.code = code;
        }
    }
}

