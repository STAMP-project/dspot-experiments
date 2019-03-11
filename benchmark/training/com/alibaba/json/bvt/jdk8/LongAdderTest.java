package com.alibaba.json.bvt.jdk8;


import com.alibaba.fastjson.JSON;
import java.util.concurrent.atomic.LongAdder;
import junit.framework.TestCase;


/**
 * Created by wenshao on 14/03/2017.
 */
public class LongAdderTest extends TestCase {
    public void test_long_add() throws Exception {
        LongAdder adder = new LongAdder();
        adder.add(3);
        String json = JSON.toJSONString(adder);
        TestCase.assertEquals("{\"value\":3}", json);
    }
}

