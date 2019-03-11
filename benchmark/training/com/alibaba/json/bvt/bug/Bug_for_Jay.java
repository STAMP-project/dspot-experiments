package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_Jay extends TestCase {
    public void test_for_jay() throws Exception {
        JSON.toJSONString(new Bug_for_Jay.B(), true);
    }

    public class A {
        String nameA;
    }

    public class B extends Bug_for_Jay.A {
        String nameB;
    }
}

