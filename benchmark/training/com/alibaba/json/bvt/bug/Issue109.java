package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.util.ASMUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class Issue109 extends TestCase {
    public void test_for_issue() throws Exception {
        Assert.assertFalse(ASMUtils.isAndroid(null));
    }
}

