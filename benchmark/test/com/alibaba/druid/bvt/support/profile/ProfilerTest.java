package com.alibaba.druid.bvt.support.profile;


import junit.framework.TestCase;


public class ProfilerTest extends TestCase {
    public void test_profile() throws Exception {
        for (int i = 0; i < 10; ++i) {
            req();
        }
    }
}

