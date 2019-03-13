package com.alibaba.json.bvt.asm;


import Type.BOOLEAN_TYPE;
import Type.BYTE_TYPE;
import Type.CHAR_TYPE;
import Type.DOUBLE_TYPE;
import Type.FLOAT_TYPE;
import Type.INT_TYPE;
import Type.LONG_TYPE;
import Type.SHORT_TYPE;
import Type.VOID_TYPE;
import com.alibaba.fastjson.asm.Type;
import com.alibaba.fastjson.util.ASMUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestType extends TestCase {
    public void test_getType() throws Exception {
        Assert.assertEquals(VOID_TYPE, Type.getType(ASMUtils.desc(void.class)));
        Assert.assertEquals(BOOLEAN_TYPE, Type.getType(ASMUtils.desc(boolean.class)));
        Assert.assertEquals(CHAR_TYPE, Type.getType(ASMUtils.desc(char.class)));
        Assert.assertEquals(BYTE_TYPE, Type.getType(ASMUtils.desc(byte.class)));
        Assert.assertEquals(SHORT_TYPE, Type.getType(ASMUtils.desc(short.class)));
        Assert.assertEquals(INT_TYPE, Type.getType(ASMUtils.desc(int.class)));
        Assert.assertEquals(LONG_TYPE, Type.getType(ASMUtils.desc(long.class)));
        Assert.assertEquals(FLOAT_TYPE, Type.getType(ASMUtils.desc(float.class)));
        Assert.assertEquals(DOUBLE_TYPE, Type.getType(ASMUtils.desc(double.class)));
        Assert.assertEquals("[D", Type.getType(ASMUtils.desc(double[].class)).getInternalName());
        Assert.assertEquals("[[D", Type.getType(ASMUtils.desc(double[][].class)).getInternalName());
        Assert.assertEquals("[Ljava/lang/Double;", Type.getType(ASMUtils.desc(Double[].class)).getInternalName());
    }
}

