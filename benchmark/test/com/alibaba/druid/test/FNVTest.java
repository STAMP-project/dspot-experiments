package com.alibaba.druid.test;


import junit.framework.TestCase;


/**
 * Created by wenshao on 28/07/2017.
 */
public class FNVTest extends TestCase {
    public void test_fnv_32() throws Exception {
        System.out.println((" 0x811c9dc5 : " + -2128831035));
        long x = -3750763034362895579L;
        System.out.println(x);
        System.out.println((x << 2));
        // 14695981039346656037
    }
}

