package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.util.Optional;
import junit.framework.TestCase;


/**
 * Created by wenshao on 02/04/2017.
 */
public class OptionalTest4 extends TestCase {
    public void test_for_issue() throws Exception {
        OptionalTest4.JsonResult result = new OptionalTest4.JsonResult();
        result.a = Optional.empty();
        result.b = Optional.empty();
        String json = JSON.toJSONString(result);
        System.out.println(json);
    }

    public static class JsonResult {
        public Object a;

        public Optional<Object> b;
    }
}

