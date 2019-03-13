package com.alibaba.json.bvt.parser.deser.asm;


import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ASMDeserializerFactory;
import com.alibaba.fastjson.util.ASMClassLoader;
import com.alibaba.fastjson.util.JavaBeanInfo;
import junit.framework.TestCase;


public class TestASM_primitive extends TestCase {
    public void test_asm() throws Exception {
        ASMDeserializerFactory factory = new ASMDeserializerFactory(new ASMClassLoader());
        Exception error = null;
        try {
            JavaBeanInfo beanInfo = JavaBeanInfo.build(int.class, int.class, null);
            factory.createJavaBeanDeserializer(ParserConfig.getGlobalInstance(), beanInfo);
        } catch (Exception ex) {
            error = ex;
        }
        TestCase.assertNotNull(error);
    }
}

