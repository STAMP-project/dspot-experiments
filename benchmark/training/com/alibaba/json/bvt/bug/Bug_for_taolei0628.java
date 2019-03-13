package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.Random;
import junit.framework.TestCase;


public class Bug_for_taolei0628 extends TestCase {
    static final Random rand = new Random(1);

    public void test_bug() throws Exception {
        Object object = Bug_for_taolei0628.createObject();
        JSON.toJSONString(object);
    }
}

