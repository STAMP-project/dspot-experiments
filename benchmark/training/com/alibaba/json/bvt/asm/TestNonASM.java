package com.alibaba.json.bvt.asm;


import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestNonASM extends TestCase {
    public void test_no_asm() throws Exception {
        ParserConfig mapping = new ParserConfig();
        mapping.setAsmEnable(false);
        Assert.assertEquals(false, mapping.isAsmEnable());
        mapping.setAsmEnable(true);
        Assert.assertEquals(true, mapping.isAsmEnable());
    }
}

