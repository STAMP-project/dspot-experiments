package org.apache.ibatis.reflection;


import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.domain.misc.RichType;
import org.apache.ibatis.domain.misc.generics.GenericConcrete;
import org.junit.Assert;
import org.junit.Test;


public class AmplMetaClassTest {
    private RichType rich = new RichType();

    Map<String, RichType> map = new HashMap<String, RichType>() {
        {
            put("richType", rich);
        }
    };

    public AmplMetaClassTest() {
        rich.setRichType(new RichType());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534547_failAssert658() throws Exception {
        try {
            String __DSPOT_name_105524 = "hC#NRuGXz4VD=pNkBo<6";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            meta.getGetInvoker(__DSPOT_name_105524);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534547 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'hC#NRuGXz4VD=pNkBo<6\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534549_failAssert659() throws Exception {
        try {
            String __DSPOT_name_105526 = "GcG0*&i7BLl#DLP1x+mn";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            meta.getSetInvoker(__DSPOT_name_105526);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534549 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'GcG0*&i7BLl#DLP1x+mn\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534545() throws Exception {
        String __DSPOT_name_105521 = "l}!U@`(!e[TW_*-Y2bB.";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getSetterType("id");
        meta.findProperty(__DSPOT_name_105521);
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534546() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_105523 = true;
        String __DSPOT_name_105522 = "8/lF)rTfH7Bp0!|x%.Rt";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getSetterType("id");
        meta.findProperty(__DSPOT_name_105522, __DSPOT_useCamelCaseMapping_105523);
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString534539_failAssert669() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString534539 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString534532_failAssert671() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("richType.richMap");
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString534532 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'richType\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString534536_failAssert670() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType(":");
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString534536 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \':\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString534537_failAssert667() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("richProperty");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString534537 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'richProperty\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534551() throws Exception {
        String __DSPOT_name_105528 = "B /6(b[3^.ra,Nzzh&HF";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_mg534551__8 = meta.hasGetter(__DSPOT_name_105528);
        Assert.assertFalse(o_shouldTestDataTypeOfGenericMethod_mg534551__8);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add534543() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getGetterType("id");
        meta.getSetterType("id");
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString534532_failAssert671_mg535214() throws Exception {
        try {
            String __DSPOT_name_105764 = "^CQ[Veo+1t^$`w5h]4P}";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("richType.richMap");
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString534532 should have thrown ReflectionException");
            meta.getGetInvoker(__DSPOT_name_105764);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534545litString534646_failAssert685() throws Exception {
        try {
            String __DSPOT_name_105521 = "l}!U@`(!e[TW_*-Y2bB.";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("u");
            meta.findProperty(__DSPOT_name_105521);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534545litString534646 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'u\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534545litString534643_failAssert699() throws Exception {
        try {
            String __DSPOT_name_105521 = "l}!U@`(!e[TW_*-Y2bB.";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("\n");
            meta.getSetterType("id");
            meta.findProperty(__DSPOT_name_105521);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534545litString534643 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'\n\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534549_failAssert659litString534783() throws Exception {
        try {
            String __DSPOT_name_105526 = "GcG0*&7BLl#DLP1x+mn";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("id");
            meta.getSetterType("id");
            meta.getSetInvoker(__DSPOT_name_105526);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534549 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534551_add534859() throws Exception {
        String __DSPOT_name_105528 = "B /6(b[3^.ra,Nzzh&HF";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getGetterType("id");
        meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_mg534551__8 = meta.hasGetter(__DSPOT_name_105528);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString534540_failAssert665_add534951() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass o_shouldTestDataTypeOfGenericMethodlitString534540_failAssert665_add534951__5 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethodlitString534540_failAssert665_add534951__5)).hasDefaultConstructor());
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("id");
            meta.getSetterType("\n");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString534540 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534551_mg535007_failAssert688() throws Exception {
        try {
            String __DSPOT_name_105534 = "&`74G#h)hOp|iA2bpF16";
            String __DSPOT_name_105528 = "B /6(b[3^.ra,Nzzh&HF";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg534551__8 = meta.hasGetter(__DSPOT_name_105528);
            meta.getGetInvoker(__DSPOT_name_105534);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534551_mg535007 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'&`74G#h)hOp|iA2bpF16\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534546_remove534993() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_105523 = true;
        String __DSPOT_name_105522 = "8/lF)rTfH7Bp0!|x%.Rt";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.findProperty(__DSPOT_name_105522, __DSPOT_useCamelCaseMapping_105523);
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodnull534555_failAssert663_add534944() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType(null);
            meta.getGetterType(null);
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodnull534555 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodnull534554_failAssert661_mg535125() throws Exception {
        try {
            String __DSPOT_name_105665 = "%_H/[/$xR]<w;JweL{|[";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, null);
            meta.getGetterType("id");
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodnull534554 should have thrown NullPointerException");
            meta.getGetterType(__DSPOT_name_105665);
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534552_mg535036_failAssert674() throws Exception {
        try {
            String __DSPOT_name_105566 = "G^JX6-{rH+tp]Xp(@js.";
            String __DSPOT_name_105529 = "0w T*N2f+8%Lmk]<tI{m";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg534552__8 = meta.hasSetter(__DSPOT_name_105529);
            meta.getSetInvoker(__DSPOT_name_105566);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534552_mg535036 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'G^JX6-{rH+tp]Xp(@js.\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534551_rv535267litString539334_failAssert710() throws Exception {
        try {
            String __DSPOT_name_105528 = "B /6(b[3^.ra,Nzzh&HF";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Class<?> __DSPOT_invoc_12 = meta.getGetterType("id");
            meta.getSetterType("J");
            boolean o_shouldTestDataTypeOfGenericMethod_mg534551__8 = meta.hasGetter(__DSPOT_name_105528);
            __DSPOT_invoc_12.getGenericSuperclass();
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534551_rv535267litString539334 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'J\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534553_failAssert656_mg535077litString539742() throws Exception {
        try {
            String __DSPOT_name_105611 = "N9{HXxd_n:19ZwJ S$%B";
            String __DSPOT_name_105530 = "LT<);o0{!YO1%>xFt+4%";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("id");
            meta.getSetterType("");
            meta.metaClassForProperty(__DSPOT_name_105530);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534553 should have thrown ReflectionException");
            meta.findProperty(__DSPOT_name_105611);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534550_failAssert657null538317_failAssert680null566847() throws Exception {
        try {
            try {
                String __DSPOT_name_105527 = "XB0;%>f!PgSax@g^0g%N";
                ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
                Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
                MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
                Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
                meta.getGetterType(null);
                meta.getSetterType("id");
                meta.getSetterType(null);
                org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534550 should have thrown ReflectionException");
            } catch (ReflectionException expected) {
            }
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534550_failAssert657null538317 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534552_rv535674litString539310_failAssert711() throws Exception {
        try {
            String __DSPOT_name_105529 = "0w T*N2f+8%Lmk]<tI{m";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("richType.richProperty");
            Class<?> __DSPOT_invoc_13 = meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg534552__8 = meta.hasSetter(__DSPOT_name_105529);
            __DSPOT_invoc_13.isAnnotation();
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534552_rv535674litString539310 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'richType\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534546_remove534992litBool540640() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_105523 = false;
        String __DSPOT_name_105522 = "8/lF)rTfH7Bp0!|x%.Rt";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getSetterType("id");
        meta.findProperty(__DSPOT_name_105522, __DSPOT_useCamelCaseMapping_105523);
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534551_rv535241_remove541922() throws Exception {
        String __DSPOT_name_105528 = "B /6(b[3^.ra,Nzzh&HF";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Class<?> __DSPOT_invoc_12 = meta.getGetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_mg534551__8 = meta.hasGetter(__DSPOT_name_105528);
        boolean o_shouldTestDataTypeOfGenericMethod_mg534551_rv535241__13 = __DSPOT_invoc_12.desiredAssertionStatus();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString534533_failAssert664_rv537241null566814() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("`");
            Class<?> __DSPOT_invoc_8 = meta.getSetterType(null);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString534533 should have thrown ReflectionException");
            __DSPOT_invoc_8.getInterfaces();
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534546_mg535031_failAssert675litString540448() throws Exception {
        try {
            String __DSPOT_name_105560 = "$&YjJwpaoe(,?bO725^b";
            boolean __DSPOT_useCamelCaseMapping_105523 = true;
            String __DSPOT_name_105522 = "richType.richMap";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("id");
            meta.getSetterType("id");
            meta.findProperty(__DSPOT_name_105522, __DSPOT_useCamelCaseMapping_105523);
            meta.metaClassForProperty(__DSPOT_name_105560);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534546_mg535031 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodnull534554_failAssert661_rv536865null566711() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, null);
            Class<?> __DSPOT_invoc_7 = meta.getGetterType("id");
            meta.getSetterType(null);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodnull534554 should have thrown NullPointerException");
            __DSPOT_invoc_7.isArray();
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534549_failAssert659litString534782_rv561649() throws Exception {
        try {
            String __DSPOT_name_105526 = "GcG0*&i7BLl#DLPg1x+mn";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("id");
            Class<?> __DSPOT_invoc_15 = meta.getSetterType("id");
            meta.getSetInvoker(__DSPOT_name_105526);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534549 should have thrown ReflectionException");
            __DSPOT_invoc_15.getEnclosingConstructor();
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString534538_failAssert668_add534966_add541104() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("id");
            meta.getSetterType("S");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString534538 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            expected.getMessage();
            expected.getMessage();
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg534551_rv535267_mg542337_failAssert712() throws Exception {
        try {
            String __DSPOT_name_106892 = "0mph[C1TY/KRmM)Gsy}V";
            String __DSPOT_name_105528 = "B /6(b[3^.ra,Nzzh&HF";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Class<?> __DSPOT_invoc_12 = meta.getGetterType("id");
            meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg534551__8 = meta.hasGetter(__DSPOT_name_105528);
            __DSPOT_invoc_12.getGenericSuperclass();
            meta.getGetInvoker(__DSPOT_name_106892);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg534551_rv535267_mg542337 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'0mph[C1TY/KRmM)Gsy}V\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistance_mg9448_failAssert86() throws Exception {
        try {
            String __DSPOT_name_3252 = "phY7bv]]u7H,t>jQ@5]U";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.hasGetter("richField");
            meta.hasGetter("richProperty");
            meta.hasGetter("richList");
            meta.hasGetter("richMap");
            meta.hasGetter("richList[0]");
            meta.hasGetter("richType");
            meta.hasGetter("richType.richField");
            meta.hasGetter("richType.richProperty");
            meta.hasGetter("richType.richList");
            meta.hasGetter("richType.richMap");
            meta.hasGetter("richType.richList[0]");
            meta.findProperty("richType.richProperty", false);
            meta.hasGetter("[0]");
            meta.getGetInvoker(__DSPOT_name_3252);
            org.junit.Assert.fail("shouldCheckGetterExistance_mg9448 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'phY7bv]]u7H,t>jQ@5]U\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9418() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9418__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9418__5);
        boolean o_shouldCheckGetterExistancelitString9418__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9418__6);
        boolean o_shouldCheckGetterExistancelitString9418__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9418__7);
        boolean o_shouldCheckGetterExistancelitString9418__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9418__8);
        boolean o_shouldCheckGetterExistancelitString9418__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9418__9);
        boolean o_shouldCheckGetterExistancelitString9418__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9418__10);
        boolean o_shouldCheckGetterExistancelitString9418__11 = meta.hasGetter("richType.richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9418__11);
        boolean o_shouldCheckGetterExistancelitString9418__12 = meta.hasGetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9418__12);
        boolean o_shouldCheckGetterExistancelitString9418__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9418__13);
        boolean o_shouldCheckGetterExistancelitString9418__14 = meta.hasGetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9418__14);
        boolean o_shouldCheckGetterExistancelitString9418__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9418__15);
        meta.findProperty("rihType.richProperty", false);
        meta.hasGetter("[0]");
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistance_mg9451_failAssert87() throws Exception {
        try {
            String __DSPOT_name_3255 = "UBRpi8,#.!&@ZLnEmUEa";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.hasGetter("richField");
            meta.hasGetter("richProperty");
            meta.hasGetter("richList");
            meta.hasGetter("richMap");
            meta.hasGetter("richList[0]");
            meta.hasGetter("richType");
            meta.hasGetter("richType.richField");
            meta.hasGetter("richType.richProperty");
            meta.hasGetter("richType.richList");
            meta.hasGetter("richType.richMap");
            meta.hasGetter("richType.richList[0]");
            meta.findProperty("richType.richProperty", false);
            meta.hasGetter("[0]");
            meta.getSetterType(__DSPOT_name_3255);
            org.junit.Assert.fail("shouldCheckGetterExistance_mg9451 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'UBRpi8,#\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitBool9431() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitBool9431__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__5);
        boolean o_shouldCheckGetterExistancelitBool9431__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__6);
        boolean o_shouldCheckGetterExistancelitBool9431__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__7);
        boolean o_shouldCheckGetterExistancelitBool9431__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__8);
        boolean o_shouldCheckGetterExistancelitBool9431__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__9);
        boolean o_shouldCheckGetterExistancelitBool9431__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__10);
        boolean o_shouldCheckGetterExistancelitBool9431__11 = meta.hasGetter("richType.richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__11);
        boolean o_shouldCheckGetterExistancelitBool9431__12 = meta.hasGetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__12);
        boolean o_shouldCheckGetterExistancelitBool9431__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__13);
        boolean o_shouldCheckGetterExistancelitBool9431__14 = meta.hasGetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__14);
        boolean o_shouldCheckGetterExistancelitBool9431__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__15);
        String o_shouldCheckGetterExistancelitBool9431__16 = meta.findProperty("richType.richProperty", true);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitBool9431__16);
        boolean o_shouldCheckGetterExistancelitBool9431__17 = meta.hasGetter("[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitBool9431__17);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__5);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__6);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__7);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__8);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__9);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__10);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__11);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__12);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__13);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__14);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9431__15);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitBool9431__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9414() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9414__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__5);
        boolean o_shouldCheckGetterExistancelitString9414__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__6);
        boolean o_shouldCheckGetterExistancelitString9414__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__7);
        boolean o_shouldCheckGetterExistancelitString9414__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__8);
        boolean o_shouldCheckGetterExistancelitString9414__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__9);
        boolean o_shouldCheckGetterExistancelitString9414__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__10);
        boolean o_shouldCheckGetterExistancelitString9414__11 = meta.hasGetter("richType.richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__11);
        boolean o_shouldCheckGetterExistancelitString9414__12 = meta.hasGetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__12);
        boolean o_shouldCheckGetterExistancelitString9414__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__13);
        boolean o_shouldCheckGetterExistancelitString9414__14 = meta.hasGetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__14);
        boolean o_shouldCheckGetterExistancelitString9414__15 = meta.hasGetter(":");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9414__15);
        String o_shouldCheckGetterExistancelitString9414__16 = meta.findProperty("richType.richProperty", false);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9414__16);
        boolean o_shouldCheckGetterExistancelitString9414__17 = meta.hasGetter("[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9414__17);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__5);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__6);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__7);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__8);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__9);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__10);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__11);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__12);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__13);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9414__14);
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9414__15);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9414__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9378() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9378__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__5);
        boolean o_shouldCheckGetterExistancelitString9378__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__6);
        boolean o_shouldCheckGetterExistancelitString9378__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__7);
        boolean o_shouldCheckGetterExistancelitString9378__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__8);
        boolean o_shouldCheckGetterExistancelitString9378__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__9);
        boolean o_shouldCheckGetterExistancelitString9378__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__10);
        boolean o_shouldCheckGetterExistancelitString9378__11 = meta.hasGetter("rchType.richField");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9378__11);
        boolean o_shouldCheckGetterExistancelitString9378__12 = meta.hasGetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__12);
        boolean o_shouldCheckGetterExistancelitString9378__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__13);
        boolean o_shouldCheckGetterExistancelitString9378__14 = meta.hasGetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__14);
        boolean o_shouldCheckGetterExistancelitString9378__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__15);
        String o_shouldCheckGetterExistancelitString9378__16 = meta.findProperty("richType.richProperty", false);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9378__16);
        boolean o_shouldCheckGetterExistancelitString9378__17 = meta.hasGetter("[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9378__17);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__5);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__6);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__7);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__8);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__9);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__10);
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9378__11);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__12);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__13);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__14);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9378__15);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9378__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancenull9467_failAssert100() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.hasGetter("richField");
            meta.hasGetter("richProperty");
            meta.hasGetter("richList");
            meta.hasGetter("richMap");
            meta.hasGetter("richList[0]");
            meta.hasGetter("richType");
            meta.hasGetter("richType.richField");
            meta.hasGetter("richType.richProperty");
            meta.hasGetter("richType.richList");
            meta.hasGetter("richType.richMap");
            meta.hasGetter("richType.richList[0]");
            meta.findProperty(null, false);
            meta.hasGetter("[0]");
            org.junit.Assert.fail("shouldCheckGetterExistancenull9467 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistance_mg9454_failAssert89() throws Exception {
        try {
            String __DSPOT_name_3258 = "|Z9RN!8#@G.VzVZq!K-x";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.hasGetter("richField");
            meta.hasGetter("richProperty");
            meta.hasGetter("richList");
            meta.hasGetter("richMap");
            meta.hasGetter("richList[0]");
            meta.hasGetter("richType");
            meta.hasGetter("richType.richField");
            meta.hasGetter("richType.richProperty");
            meta.hasGetter("richType.richList");
            meta.hasGetter("richType.richMap");
            meta.hasGetter("richType.richList[0]");
            meta.findProperty("richType.richProperty", false);
            meta.hasGetter("[0]");
            meta.metaClassForProperty(__DSPOT_name_3258);
            org.junit.Assert.fail("shouldCheckGetterExistance_mg9454 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'|Z9RN!8#@G.VzVZq!K-x\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9384() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9384__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__5);
        boolean o_shouldCheckGetterExistancelitString9384__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__6);
        boolean o_shouldCheckGetterExistancelitString9384__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__7);
        boolean o_shouldCheckGetterExistancelitString9384__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__8);
        boolean o_shouldCheckGetterExistancelitString9384__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__9);
        boolean o_shouldCheckGetterExistancelitString9384__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__10);
        boolean o_shouldCheckGetterExistancelitString9384__11 = meta.hasGetter("richType.richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__11);
        boolean o_shouldCheckGetterExistancelitString9384__12 = meta.hasGetter("richType.michProperty");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9384__12);
        boolean o_shouldCheckGetterExistancelitString9384__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__13);
        boolean o_shouldCheckGetterExistancelitString9384__14 = meta.hasGetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__14);
        boolean o_shouldCheckGetterExistancelitString9384__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__15);
        String o_shouldCheckGetterExistancelitString9384__16 = meta.findProperty("richType.richProperty", false);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9384__16);
        boolean o_shouldCheckGetterExistancelitString9384__17 = meta.hasGetter("[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9384__17);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__5);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__6);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__7);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__8);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__9);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__10);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__11);
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9384__12);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__13);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__14);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9384__15);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9384__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistance_mg9450_failAssert90() throws Exception {
        try {
            String __DSPOT_name_3254 = "7`Vx<ANu}A:Ma9O),ED?";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.hasGetter("richField");
            meta.hasGetter("richProperty");
            meta.hasGetter("richList");
            meta.hasGetter("richMap");
            meta.hasGetter("richList[0]");
            meta.hasGetter("richType");
            meta.hasGetter("richType.richField");
            meta.hasGetter("richType.richProperty");
            meta.hasGetter("richType.richList");
            meta.hasGetter("richType.richMap");
            meta.hasGetter("richType.richList[0]");
            meta.findProperty("richType.richProperty", false);
            meta.hasGetter("[0]");
            meta.getSetInvoker(__DSPOT_name_3254);
            org.junit.Assert.fail("shouldCheckGetterExistance_mg9450 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'7`Vx<ANu}A:Ma9O),ED?\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9407_mg22100_failAssert137() throws Exception {
        try {
            String __DSPOT_name_4264 = "_u#9Ly^dTbVUHO&=}w$F";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9407__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9407__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9407__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9407__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9407__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9407__10 = meta.hasGetter("richType");
            boolean o_shouldCheckGetterExistancelitString9407__11 = meta.hasGetter("richType.richField");
            boolean o_shouldCheckGetterExistancelitString9407__12 = meta.hasGetter("richType.richProperty");
            boolean o_shouldCheckGetterExistancelitString9407__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9407__14 = meta.hasGetter("richType.richMap");
            boolean o_shouldCheckGetterExistancelitString9407__15 = meta.hasGetter("richType.richMap");
            boolean o_shouldCheckGetterExistancelitString9407__16 = meta.hasGetter("richType.richMap");
            String o_shouldCheckGetterExistancelitString9407__17 = meta.findProperty("richType.richProperty", false);
            boolean o_shouldCheckGetterExistancelitString9407__18 = meta.hasGetter("[0]");
            meta.getSetInvoker(__DSPOT_name_4264);
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9407_mg22100 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'_u#9Ly^dTbVUHO&=}w$F\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9419litBool18574() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9419__5 = meta.hasGetter("richField");
        boolean o_shouldCheckGetterExistancelitString9419__6 = meta.hasGetter("richProperty");
        boolean o_shouldCheckGetterExistancelitString9419__7 = meta.hasGetter("richList");
        boolean o_shouldCheckGetterExistancelitString9419__8 = meta.hasGetter("richMap");
        boolean o_shouldCheckGetterExistancelitString9419__9 = meta.hasGetter("richList[0]");
        boolean o_shouldCheckGetterExistancelitString9419__10 = meta.hasGetter("richType");
        boolean o_shouldCheckGetterExistancelitString9419__11 = meta.hasGetter("richType.richField");
        boolean o_shouldCheckGetterExistancelitString9419__12 = meta.hasGetter("richType.richProperty");
        boolean o_shouldCheckGetterExistancelitString9419__13 = meta.hasGetter("richType.richList");
        boolean o_shouldCheckGetterExistancelitString9419__14 = meta.hasGetter("richType.richMap");
        boolean o_shouldCheckGetterExistancelitString9419__15 = meta.hasGetter("richType.richList[0]");
        meta.findProperty("DIjcP3}Y/Fz3^-=67+n$*", true);
        meta.hasGetter("[0]");
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9377_mg22013() throws Exception {
        String __DSPOT_name_4167 = "<C!}X})Xn?/a&}&.iG{:";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9377__5 = meta.hasGetter("richField");
        boolean o_shouldCheckGetterExistancelitString9377__6 = meta.hasGetter("richProperty");
        boolean o_shouldCheckGetterExistancelitString9377__7 = meta.hasGetter("richList");
        boolean o_shouldCheckGetterExistancelitString9377__8 = meta.hasGetter("richMap");
        boolean o_shouldCheckGetterExistancelitString9377__9 = meta.hasGetter("richList[0]");
        boolean o_shouldCheckGetterExistancelitString9377__10 = meta.hasGetter("richType");
        boolean o_shouldCheckGetterExistancelitString9377__11 = meta.hasGetter("richT!ype.richField");
        boolean o_shouldCheckGetterExistancelitString9377__12 = meta.hasGetter("richType.richProperty");
        boolean o_shouldCheckGetterExistancelitString9377__13 = meta.hasGetter("richType.richList");
        boolean o_shouldCheckGetterExistancelitString9377__14 = meta.hasGetter("richType.richMap");
        boolean o_shouldCheckGetterExistancelitString9377__15 = meta.hasGetter("richType.richList[0]");
        String o_shouldCheckGetterExistancelitString9377__16 = meta.findProperty("richType.richProperty", false);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9377__16);
        boolean o_shouldCheckGetterExistancelitString9377__17 = meta.hasGetter("[0]");
        boolean o_shouldCheckGetterExistancelitString9377_mg22013__45 = meta.hasSetter(__DSPOT_name_4167);
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9377_mg22013__45);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9377__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9409litString15046() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9409__5 = meta.hasGetter("richField");
        boolean o_shouldCheckGetterExistancelitString9409__6 = meta.hasGetter("richProperty");
        boolean o_shouldCheckGetterExistancelitString9409__7 = meta.hasGetter("richList");
        boolean o_shouldCheckGetterExistancelitString9409__8 = meta.hasGetter("richMap");
        boolean o_shouldCheckGetterExistancelitString9409__9 = meta.hasGetter("richList[0]");
        boolean o_shouldCheckGetterExistancelitString9409__10 = meta.hasGetter("richType");
        boolean o_shouldCheckGetterExistancelitString9409__11 = meta.hasGetter("richType.richField");
        boolean o_shouldCheckGetterExistancelitString9409__12 = meta.hasGetter("richType.richProperty");
        boolean o_shouldCheckGetterExistancelitString9409__13 = meta.hasGetter("richType.richList");
        boolean o_shouldCheckGetterExistancelitString9409__14 = meta.hasGetter("richType.richMap");
        boolean o_shouldCheckGetterExistancelitString9409__15 = meta.hasGetter("richTy}pe.richList[0]");
        String o_shouldCheckGetterExistancelitString9409__16 = meta.findProperty("richType.richProp(erty", false);
        Assert.assertEquals("richType.", o_shouldCheckGetterExistancelitString9409__16);
        boolean o_shouldCheckGetterExistancelitString9409__17 = meta.hasGetter("[0]");
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richType.", o_shouldCheckGetterExistancelitString9409__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9403_mg21792_failAssert140() throws Exception {
        try {
            String __DSPOT_name_3922 = "3E4#nR!g,kw.X>}m^>ms";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9403__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9403__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9403__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9403__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9403__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9403__10 = meta.hasGetter("richType");
            boolean o_shouldCheckGetterExistancelitString9403__11 = meta.hasGetter("richType.richField");
            boolean o_shouldCheckGetterExistancelitString9403__12 = meta.hasGetter("richType.richProperty");
            boolean o_shouldCheckGetterExistancelitString9403__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9403__14 = meta.hasGetter("to$GYsR$+9l$dt[U");
            boolean o_shouldCheckGetterExistancelitString9403__15 = meta.hasGetter("richType.richList[0]");
            String o_shouldCheckGetterExistancelitString9403__16 = meta.findProperty("richType.richProperty", false);
            boolean o_shouldCheckGetterExistancelitString9403__17 = meta.hasGetter("[0]");
            meta.getGetInvoker(__DSPOT_name_3922);
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9403_mg21792 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'3E4#nR!g,kw.X>}m^>ms\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9408_add20202() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9408__5 = meta.hasGetter("richField");
        boolean o_shouldCheckGetterExistancelitString9408__6 = meta.hasGetter("richProperty");
        boolean o_shouldCheckGetterExistancelitString9408__7 = meta.hasGetter("richList");
        boolean o_shouldCheckGetterExistancelitString9408__8 = meta.hasGetter("richMap");
        boolean o_shouldCheckGetterExistancelitString9408__9 = meta.hasGetter("richList[0]");
        boolean o_shouldCheckGetterExistancelitString9408__10 = meta.hasGetter("richType");
        boolean o_shouldCheckGetterExistancelitString9408__11 = meta.hasGetter("richType.richField");
        boolean o_shouldCheckGetterExistancelitString9408__12 = meta.hasGetter("richType.richProperty");
        boolean o_shouldCheckGetterExistancelitString9408__13 = meta.hasGetter("richType.richList");
        boolean o_shouldCheckGetterExistancelitString9408__14 = meta.hasGetter("richType.richMap");
        boolean o_shouldCheckGetterExistancelitString9408_add20202__35 = meta.hasGetter("richType.richLrst[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9408_add20202__35);
        boolean o_shouldCheckGetterExistancelitString9408__15 = meta.hasGetter("richType.richLrst[0]");
        String o_shouldCheckGetterExistancelitString9408__16 = meta.findProperty("richType.richProperty", false);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9408__16);
        boolean o_shouldCheckGetterExistancelitString9408__17 = meta.hasGetter("[0]");
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9408_add20202__35);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9408__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9369_mg21822_failAssert141() throws Exception {
        try {
            String __DSPOT_name_3955 = "lkGQQ,.;chKYX02&z!_V";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9369__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9369__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9369__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9369__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9369__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9369__10 = meta.hasGetter("ric,hType");
            boolean o_shouldCheckGetterExistancelitString9369__11 = meta.hasGetter("richType.richField");
            boolean o_shouldCheckGetterExistancelitString9369__12 = meta.hasGetter("richType.richProperty");
            boolean o_shouldCheckGetterExistancelitString9369__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9369__14 = meta.hasGetter("richType.richMap");
            boolean o_shouldCheckGetterExistancelitString9369__15 = meta.hasGetter("richType.richList[0]");
            String o_shouldCheckGetterExistancelitString9369__16 = meta.findProperty("richType.richProperty", false);
            boolean o_shouldCheckGetterExistancelitString9369__17 = meta.hasGetter("[0]");
            meta.getSetterType(__DSPOT_name_3955);
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9369_mg21822 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'lkGQQ,\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9404_mg21584() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_3691 = false;
        String __DSPOT_name_3690 = "oF^qsmt.MJEzB<r,L)k+";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9404__5 = meta.hasGetter("richField");
        boolean o_shouldCheckGetterExistancelitString9404__6 = meta.hasGetter("richProperty");
        boolean o_shouldCheckGetterExistancelitString9404__7 = meta.hasGetter("richList");
        boolean o_shouldCheckGetterExistancelitString9404__8 = meta.hasGetter("richMap");
        boolean o_shouldCheckGetterExistancelitString9404__9 = meta.hasGetter("richList[0]");
        boolean o_shouldCheckGetterExistancelitString9404__10 = meta.hasGetter("richType");
        boolean o_shouldCheckGetterExistancelitString9404__11 = meta.hasGetter("richType.richField");
        boolean o_shouldCheckGetterExistancelitString9404__12 = meta.hasGetter("richType.richProperty");
        boolean o_shouldCheckGetterExistancelitString9404__13 = meta.hasGetter("richType.richList");
        boolean o_shouldCheckGetterExistancelitString9404__14 = meta.hasGetter("");
        boolean o_shouldCheckGetterExistancelitString9404__15 = meta.hasGetter("richType.richList[0]");
        String o_shouldCheckGetterExistancelitString9404__16 = meta.findProperty("richType.richProperty", false);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9404__16);
        boolean o_shouldCheckGetterExistancelitString9404__17 = meta.hasGetter("[0]");
        meta.findProperty(__DSPOT_name_3690, __DSPOT_useCamelCaseMapping_3691);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9402null23466_failAssert125() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9402__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9402__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9402__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9402__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9402__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9402__10 = meta.hasGetter("richType");
            boolean o_shouldCheckGetterExistancelitString9402__11 = meta.hasGetter("richType.richField");
            boolean o_shouldCheckGetterExistancelitString9402__12 = meta.hasGetter("richType.richProperty");
            boolean o_shouldCheckGetterExistancelitString9402__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9402__14 = meta.hasGetter("richType.ricMap");
            boolean o_shouldCheckGetterExistancelitString9402__15 = meta.hasGetter("richType.richList[0]");
            String o_shouldCheckGetterExistancelitString9402__16 = meta.findProperty(null, false);
            boolean o_shouldCheckGetterExistancelitString9402__17 = meta.hasGetter("[0]");
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9402null23466 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9409_add20039() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9409__5 = meta.hasGetter("richField");
        boolean o_shouldCheckGetterExistancelitString9409__6 = meta.hasGetter("richProperty");
        boolean o_shouldCheckGetterExistancelitString9409__7 = meta.hasGetter("richList");
        boolean o_shouldCheckGetterExistancelitString9409__8 = meta.hasGetter("richMap");
        boolean o_shouldCheckGetterExistancelitString9409__9 = meta.hasGetter("richList[0]");
        boolean o_shouldCheckGetterExistancelitString9409__10 = meta.hasGetter("richType");
        boolean o_shouldCheckGetterExistancelitString9409__11 = meta.hasGetter("richType.richField");
        boolean o_shouldCheckGetterExistancelitString9409__12 = meta.hasGetter("richType.richProperty");
        boolean o_shouldCheckGetterExistancelitString9409__13 = meta.hasGetter("richType.richList");
        boolean o_shouldCheckGetterExistancelitString9409__14 = meta.hasGetter("richType.richMap");
        boolean o_shouldCheckGetterExistancelitString9409_add20039__35 = meta.hasGetter("richTy}pe.richList[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9409_add20039__35);
        boolean o_shouldCheckGetterExistancelitString9409__15 = meta.hasGetter("richTy}pe.richList[0]");
        String o_shouldCheckGetterExistancelitString9409__16 = meta.findProperty("richType.richProperty", false);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9409__16);
        boolean o_shouldCheckGetterExistancelitString9409__17 = meta.hasGetter("[0]");
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9409_add20039__35);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9409__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9408_mg21966_failAssert105() throws Exception {
        try {
            String __DSPOT_name_4115 = "/A+Z77y1Ksj$6!Sc3b#=";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9408__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9408__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9408__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9408__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9408__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9408__10 = meta.hasGetter("richType");
            boolean o_shouldCheckGetterExistancelitString9408__11 = meta.hasGetter("richType.richField");
            boolean o_shouldCheckGetterExistancelitString9408__12 = meta.hasGetter("richType.richProperty");
            boolean o_shouldCheckGetterExistancelitString9408__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9408__14 = meta.hasGetter("richType.richMap");
            boolean o_shouldCheckGetterExistancelitString9408__15 = meta.hasGetter("richType.richLrst[0]");
            String o_shouldCheckGetterExistancelitString9408__16 = meta.findProperty("richType.richProperty", false);
            boolean o_shouldCheckGetterExistancelitString9408__17 = meta.hasGetter("[0]");
            meta.getSetterType(__DSPOT_name_4115);
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9408_mg21966 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'/A+Z77y1Ksj$6!Sc3b#=\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9405_mg22018_failAssert107() throws Exception {
        try {
            String __DSPOT_name_4173 = ",54O!fxD_!8gJR?L;(p)";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9405__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9405__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9405__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9405__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9405__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9405__10 = meta.hasGetter("richType");
            boolean o_shouldCheckGetterExistancelitString9405__11 = meta.hasGetter("richType.richField");
            boolean o_shouldCheckGetterExistancelitString9405__12 = meta.hasGetter("richType.richProperty");
            boolean o_shouldCheckGetterExistancelitString9405__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9405__14 = meta.hasGetter("\n");
            boolean o_shouldCheckGetterExistancelitString9405__15 = meta.hasGetter("richType.richList[0]");
            String o_shouldCheckGetterExistancelitString9405__16 = meta.findProperty("richType.richProperty", false);
            boolean o_shouldCheckGetterExistancelitString9405__17 = meta.hasGetter("[0]");
            meta.getGetterType(__DSPOT_name_4173);
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9405_mg22018 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \',54O!fxD_!8gJR?L;(p)\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancenull9459_failAssert98null23731() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            boolean o_shouldCheckGetterExistancenull9459_failAssert98null23731__7 = meta.hasGetter("richField");
            Assert.assertTrue(o_shouldCheckGetterExistancenull9459_failAssert98null23731__7);
            boolean o_shouldCheckGetterExistancenull9459_failAssert98null23731__8 = meta.hasGetter("richProperty");
            Assert.assertTrue(o_shouldCheckGetterExistancenull9459_failAssert98null23731__8);
            boolean o_shouldCheckGetterExistancenull9459_failAssert98null23731__9 = meta.hasGetter("richList");
            Assert.assertTrue(o_shouldCheckGetterExistancenull9459_failAssert98null23731__9);
            meta.hasGetter(null);
            meta.hasGetter("richList[0]");
            meta.hasGetter("richType");
            meta.hasGetter("richType.richField");
            meta.hasGetter("richType.richProperty");
            meta.hasGetter(null);
            meta.hasGetter("richType.richMap");
            meta.hasGetter("richType.richList[0]");
            meta.findProperty("richType.richProperty", false);
            meta.hasGetter("[0]");
            org.junit.Assert.fail("shouldCheckGetterExistancenull9459 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9404_mg21590_failAssert139() throws Exception {
        try {
            String __DSPOT_name_3697 = " t8Bg!u%5]KyQeHS7aN[";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9404__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9404__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9404__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9404__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9404__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9404__10 = meta.hasGetter("richType");
            boolean o_shouldCheckGetterExistancelitString9404__11 = meta.hasGetter("richType.richField");
            boolean o_shouldCheckGetterExistancelitString9404__12 = meta.hasGetter("richType.richProperty");
            boolean o_shouldCheckGetterExistancelitString9404__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9404__14 = meta.hasGetter("");
            boolean o_shouldCheckGetterExistancelitString9404__15 = meta.hasGetter("richType.richList[0]");
            String o_shouldCheckGetterExistancelitString9404__16 = meta.findProperty("richType.richProperty", false);
            boolean o_shouldCheckGetterExistancelitString9404__17 = meta.hasGetter("[0]");
            meta.hasSetter(__DSPOT_name_3697);
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9404_mg21590 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9376null23352_failAssert134() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9376__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9376__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9376__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9376__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9376__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9376__10 = meta.hasGetter("richType");
            boolean o_shouldCheckGetterExistancelitString9376__11 = meta.hasGetter("MichType.richField");
            boolean o_shouldCheckGetterExistancelitString9376__12 = meta.hasGetter(null);
            boolean o_shouldCheckGetterExistancelitString9376__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9376__14 = meta.hasGetter("richType.richMap");
            boolean o_shouldCheckGetterExistancelitString9376__15 = meta.hasGetter("richType.richList[0]");
            String o_shouldCheckGetterExistancelitString9376__16 = meta.findProperty("richType.richProperty", false);
            boolean o_shouldCheckGetterExistancelitString9376__17 = meta.hasGetter("[0]");
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9376null23352 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancenull9455_failAssert102litString18341() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, null);
            meta.hasGetter("richField");
            meta.hasGetter("ricOProperty");
            meta.hasGetter("richList");
            meta.hasGetter("richMap");
            meta.hasGetter("richList[0]");
            meta.hasGetter("richType");
            meta.hasGetter("richType.richField");
            meta.hasGetter("richType.richProperty");
            meta.hasGetter("richType.richList");
            meta.hasGetter("richType.richMap");
            meta.hasGetter("richType.richList[0]");
            meta.findProperty("richType.richProperty", false);
            meta.hasGetter("[0]");
            org.junit.Assert.fail("shouldCheckGetterExistancenull9455 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9409litBool18600_mg35477_failAssert154() throws Exception {
        try {
            String __DSPOT_name_5098 = "h RzejXQbCd``Z+#vyh#";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9409__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9409__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9409__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9409__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9409__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9409__10 = meta.hasGetter("richType");
            boolean o_shouldCheckGetterExistancelitString9409__11 = meta.hasGetter("richType.richField");
            boolean o_shouldCheckGetterExistancelitString9409__12 = meta.hasGetter("richType.richProperty");
            boolean o_shouldCheckGetterExistancelitString9409__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9409__14 = meta.hasGetter("richType.richMap");
            boolean o_shouldCheckGetterExistancelitString9409__15 = meta.hasGetter("richTy}pe.richList[0]");
            String o_shouldCheckGetterExistancelitString9409__16 = meta.findProperty("richType.richProperty", true);
            boolean o_shouldCheckGetterExistancelitString9409__17 = meta.hasGetter("[0]");
            meta.getSetInvoker(__DSPOT_name_5098);
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9409litBool18600_mg35477 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'h RzejXQbCd``Z+#vyh#\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9409litBool18600litString26026() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9409__5 = meta.hasGetter("richField");
        boolean o_shouldCheckGetterExistancelitString9409__6 = meta.hasGetter("richProperty");
        boolean o_shouldCheckGetterExistancelitString9409__7 = meta.hasGetter("richList");
        boolean o_shouldCheckGetterExistancelitString9409__8 = meta.hasGetter("richMap");
        boolean o_shouldCheckGetterExistancelitString9409__9 = meta.hasGetter("richList[0]");
        boolean o_shouldCheckGetterExistancelitString9409__10 = meta.hasGetter("richType");
        boolean o_shouldCheckGetterExistancelitString9409__11 = meta.hasGetter("richType.richField");
        boolean o_shouldCheckGetterExistancelitString9409__12 = meta.hasGetter("richType.richProperty");
        boolean o_shouldCheckGetterExistancelitString9409__13 = meta.hasGetter("richType.richList");
        boolean o_shouldCheckGetterExistancelitString9409__14 = meta.hasGetter("richType.richMap");
        boolean o_shouldCheckGetterExistancelitString9409__15 = meta.hasGetter("richTy}pe.richList[0]");
        String o_shouldCheckGetterExistancelitString9409__16 = meta.findProperty("richType.richProerty", true);
        Assert.assertEquals("richType.", o_shouldCheckGetterExistancelitString9409__16);
        boolean o_shouldCheckGetterExistancelitString9409__17 = meta.hasGetter("[0]");
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richType.", o_shouldCheckGetterExistancelitString9409__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9407_add20478_mg36171_failAssert153() throws Exception {
        try {
            String __DSPOT_name_5869 = "y+.rJbjOh/HC+&|5; !@";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9407__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9407__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9407__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9407__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9407__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9407__10 = meta.hasGetter("richType");
            boolean o_shouldCheckGetterExistancelitString9407__11 = meta.hasGetter("richType.richField");
            boolean o_shouldCheckGetterExistancelitString9407__12 = meta.hasGetter("richType.richProperty");
            boolean o_shouldCheckGetterExistancelitString9407__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9407__14 = meta.hasGetter("richType.richMap");
            boolean o_shouldCheckGetterExistancelitString9407__15 = meta.hasGetter("richType.richMap");
            boolean o_shouldCheckGetterExistancelitString9407__16 = meta.hasGetter("richType.richMap");
            String o_shouldCheckGetterExistancelitString9407__17 = meta.findProperty("richType.richProperty", false);
            boolean o_shouldCheckGetterExistancelitString9407__18 = meta.hasGetter("[0]");
            ((MetaClass) (meta)).hasDefaultConstructor();
            meta.getSetterType(__DSPOT_name_5869);
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9407_add20478_mg36171 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'y+\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9370_add20260_mg35394_failAssert160() throws Exception {
        try {
            String __DSPOT_name_5006 = "@%V:97_dqr;zcguoCk[L";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9370__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9370__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9370__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9370__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9370__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9370__10 = meta.hasGetter("richTpe");
            boolean o_shouldCheckGetterExistancelitString9370__11 = meta.hasGetter("richType.richField");
            boolean o_shouldCheckGetterExistancelitString9370__12 = meta.hasGetter("richType.richProperty");
            boolean o_shouldCheckGetterExistancelitString9370__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9370__14 = meta.hasGetter("richType.richMap");
            boolean o_shouldCheckGetterExistancelitString9370__15 = meta.hasGetter("richType.richList[0]");
            String o_shouldCheckGetterExistancelitString9370__16 = meta.findProperty("richType.richProperty", false);
            boolean o_shouldCheckGetterExistancelitString9370__17 = meta.hasGetter("[0]");
            ((MetaClass) (meta)).hasDefaultConstructor();
            meta.getGetInvoker(__DSPOT_name_5006);
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9370_add20260_mg35394 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'@%V:97_dqr;zcguoCk[L\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancenull9459_failAssert98null23725litBool31541() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            boolean o_shouldCheckGetterExistancenull9459_failAssert98null23725__7 = meta.hasGetter("richField");
            meta.hasGetter(null);
            meta.hasGetter("richList");
            meta.hasGetter(null);
            meta.hasGetter("richList[0]");
            meta.hasGetter("richType");
            meta.hasGetter("richType.richField");
            meta.hasGetter("richType.richProperty");
            meta.hasGetter("richType.richList");
            meta.hasGetter("richType.richMap");
            meta.hasGetter("richType.richList[0]");
            meta.findProperty("richType.richProperty", true);
            meta.hasGetter("[0]");
            org.junit.Assert.fail("shouldCheckGetterExistancenull9459 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9409_add20029null38890_failAssert199() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9409_add20029__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9409__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9409__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9409__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9409__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9409__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9409__10 = meta.hasGetter("richType");
            boolean o_shouldCheckGetterExistancelitString9409__11 = meta.hasGetter("richType.richField");
            boolean o_shouldCheckGetterExistancelitString9409__12 = meta.hasGetter("richType.richProperty");
            boolean o_shouldCheckGetterExistancelitString9409__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9409__14 = meta.hasGetter("richType.richMap");
            boolean o_shouldCheckGetterExistancelitString9409__15 = meta.hasGetter("richTy}pe.richList[0]");
            String o_shouldCheckGetterExistancelitString9409__16 = meta.findProperty(null, false);
            boolean o_shouldCheckGetterExistancelitString9409__17 = meta.hasGetter("[0]");
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9409_add20029null38890 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9408litBool18609litString26054() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9408__5 = meta.hasGetter("richField");
        boolean o_shouldCheckGetterExistancelitString9408__6 = meta.hasGetter("richProperty");
        boolean o_shouldCheckGetterExistancelitString9408__7 = meta.hasGetter("richList");
        boolean o_shouldCheckGetterExistancelitString9408__8 = meta.hasGetter("richMap");
        boolean o_shouldCheckGetterExistancelitString9408__9 = meta.hasGetter("richList[0]");
        boolean o_shouldCheckGetterExistancelitString9408__10 = meta.hasGetter("richType");
        boolean o_shouldCheckGetterExistancelitString9408__11 = meta.hasGetter("richType.richField");
        boolean o_shouldCheckGetterExistancelitString9408__12 = meta.hasGetter("richType.richProperty");
        boolean o_shouldCheckGetterExistancelitString9408__13 = meta.hasGetter("richType.richList");
        boolean o_shouldCheckGetterExistancelitString9408__14 = meta.hasGetter("richType.richMap");
        boolean o_shouldCheckGetterExistancelitString9408__15 = meta.hasGetter("richType.richLrst[0]");
        String o_shouldCheckGetterExistancelitString9408__16 = meta.findProperty("richType.richProperty", true);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9408__16);
        boolean o_shouldCheckGetterExistancelitString9408__17 = meta.hasGetter(":");
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9408__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9409_add20029_mg36142_failAssert158() throws Exception {
        try {
            String __DSPOT_name_5837 = "15NsC(KN5573vft_<Pr ";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9409_add20029__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9409__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9409__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9409__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9409__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9409__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9409__10 = meta.hasGetter("richType");
            boolean o_shouldCheckGetterExistancelitString9409__11 = meta.hasGetter("richType.richField");
            boolean o_shouldCheckGetterExistancelitString9409__12 = meta.hasGetter("richType.richProperty");
            boolean o_shouldCheckGetterExistancelitString9409__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9409__14 = meta.hasGetter("richType.richMap");
            boolean o_shouldCheckGetterExistancelitString9409__15 = meta.hasGetter("richTy}pe.richList[0]");
            String o_shouldCheckGetterExistancelitString9409__16 = meta.findProperty("richType.richProperty", false);
            boolean o_shouldCheckGetterExistancelitString9409__17 = meta.hasGetter("[0]");
            meta.getGetterType(__DSPOT_name_5837);
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9409_add20029_mg36142 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'15NsC(KN5573vft_<Pr \' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9409_add20039_add31968() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9409__5 = meta.hasGetter("richField");
        boolean o_shouldCheckGetterExistancelitString9409__6 = meta.hasGetter("richProperty");
        boolean o_shouldCheckGetterExistancelitString9409__7 = meta.hasGetter("richList");
        boolean o_shouldCheckGetterExistancelitString9409__8 = meta.hasGetter("richMap");
        boolean o_shouldCheckGetterExistancelitString9409__9 = meta.hasGetter("richList[0]");
        boolean o_shouldCheckGetterExistancelitString9409__10 = meta.hasGetter("richType");
        boolean o_shouldCheckGetterExistancelitString9409__11 = meta.hasGetter("richType.richField");
        boolean o_shouldCheckGetterExistancelitString9409__12 = meta.hasGetter("richType.richProperty");
        boolean o_shouldCheckGetterExistancelitString9409__13 = meta.hasGetter("richType.richList");
        boolean o_shouldCheckGetterExistancelitString9409__14 = meta.hasGetter("richType.richMap");
        boolean o_shouldCheckGetterExistancelitString9409_add20039_add31968__35 = meta.hasGetter("richTy}pe.richList[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9409_add20039_add31968__35);
        boolean o_shouldCheckGetterExistancelitString9409_add20039__35 = meta.hasGetter("richTy}pe.richList[0]");
        boolean o_shouldCheckGetterExistancelitString9409__15 = meta.hasGetter("richTy}pe.richList[0]");
        String o_shouldCheckGetterExistancelitString9409__16 = meta.findProperty("richType.richProperty", false);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9409__16);
        boolean o_shouldCheckGetterExistancelitString9409__17 = meta.hasGetter("[0]");
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9409_add20039_add31968__35);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9409__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9409_add20039_mg35281_failAssert164() throws Exception {
        try {
            String __DSPOT_name_4880 = ",jJ`4/9V*nE)-[. )t`H";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9409__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9409__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9409__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9409__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9409__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9409__10 = meta.hasGetter("richType");
            boolean o_shouldCheckGetterExistancelitString9409__11 = meta.hasGetter("richType.richField");
            boolean o_shouldCheckGetterExistancelitString9409__12 = meta.hasGetter("richType.richProperty");
            boolean o_shouldCheckGetterExistancelitString9409__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9409__14 = meta.hasGetter("richType.richMap");
            boolean o_shouldCheckGetterExistancelitString9409_add20039__35 = meta.hasGetter("richTy}pe.richList[0]");
            boolean o_shouldCheckGetterExistancelitString9409__15 = meta.hasGetter("richTy}pe.richList[0]");
            String o_shouldCheckGetterExistancelitString9409__16 = meta.findProperty("richType.richProperty", false);
            boolean o_shouldCheckGetterExistancelitString9409__17 = meta.hasGetter("[0]");
            meta.hasGetter(__DSPOT_name_4880);
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9409_add20039_mg35281 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9409_add20029litString29185() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9409_add20029__5 = meta.hasGetter("richField");
        boolean o_shouldCheckGetterExistancelitString9409__5 = meta.hasGetter("richField");
        boolean o_shouldCheckGetterExistancelitString9409__6 = meta.hasGetter("richProperty");
        boolean o_shouldCheckGetterExistancelitString9409__7 = meta.hasGetter("richList");
        boolean o_shouldCheckGetterExistancelitString9409__8 = meta.hasGetter("richMap");
        boolean o_shouldCheckGetterExistancelitString9409__9 = meta.hasGetter("richList[0]");
        boolean o_shouldCheckGetterExistancelitString9409__10 = meta.hasGetter("richType");
        boolean o_shouldCheckGetterExistancelitString9409__11 = meta.hasGetter("richType.richField");
        boolean o_shouldCheckGetterExistancelitString9409__12 = meta.hasGetter("richType.richProperty");
        boolean o_shouldCheckGetterExistancelitString9409__13 = meta.hasGetter("richType.richList");
        boolean o_shouldCheckGetterExistancelitString9409__14 = meta.hasGetter("richType.richMap");
        boolean o_shouldCheckGetterExistancelitString9409__15 = meta.hasGetter("richTy}pe.richList[0]");
        String o_shouldCheckGetterExistancelitString9409__16 = meta.findProperty("ri@chType.richProperty", false);
        boolean o_shouldCheckGetterExistancelitString9409__17 = meta.hasGetter("[0]");
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9409_add20029litString29188() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9409_add20029__5 = meta.hasGetter("richField");
        boolean o_shouldCheckGetterExistancelitString9409__5 = meta.hasGetter("richField");
        boolean o_shouldCheckGetterExistancelitString9409__6 = meta.hasGetter("richProperty");
        boolean o_shouldCheckGetterExistancelitString9409__7 = meta.hasGetter("richList");
        boolean o_shouldCheckGetterExistancelitString9409__8 = meta.hasGetter("richMap");
        boolean o_shouldCheckGetterExistancelitString9409__9 = meta.hasGetter("richList[0]");
        boolean o_shouldCheckGetterExistancelitString9409__10 = meta.hasGetter("richType");
        boolean o_shouldCheckGetterExistancelitString9409__11 = meta.hasGetter("richType.richField");
        boolean o_shouldCheckGetterExistancelitString9409__12 = meta.hasGetter("richType.richProperty");
        boolean o_shouldCheckGetterExistancelitString9409__13 = meta.hasGetter("richType.richList");
        boolean o_shouldCheckGetterExistancelitString9409__14 = meta.hasGetter("richType.richMap");
        boolean o_shouldCheckGetterExistancelitString9409__15 = meta.hasGetter("richTy}pe.richList[0]");
        String o_shouldCheckGetterExistancelitString9409__16 = meta.findProperty("", false);
        boolean o_shouldCheckGetterExistancelitString9409__17 = meta.hasGetter("[0]");
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9415litBool18621_mg36342_failAssert155() throws Exception {
        try {
            String __DSPOT_name_6059 = "P5g f&#Rk8v=X%[Gv[h{";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterExistancelitString9415__5 = meta.hasGetter("richField");
            boolean o_shouldCheckGetterExistancelitString9415__6 = meta.hasGetter("richProperty");
            boolean o_shouldCheckGetterExistancelitString9415__7 = meta.hasGetter("richList");
            boolean o_shouldCheckGetterExistancelitString9415__8 = meta.hasGetter("richMap");
            boolean o_shouldCheckGetterExistancelitString9415__9 = meta.hasGetter("richList[0]");
            boolean o_shouldCheckGetterExistancelitString9415__10 = meta.hasGetter("richType");
            boolean o_shouldCheckGetterExistancelitString9415__11 = meta.hasGetter("richType.richField");
            boolean o_shouldCheckGetterExistancelitString9415__12 = meta.hasGetter("richType.richProperty");
            boolean o_shouldCheckGetterExistancelitString9415__13 = meta.hasGetter("richType.richList");
            boolean o_shouldCheckGetterExistancelitString9415__14 = meta.hasGetter("richType.richMap");
            boolean o_shouldCheckGetterExistancelitString9415__15 = meta.hasGetter("richType.richList[0]");
            String o_shouldCheckGetterExistancelitString9415__16 = meta.findProperty("richType", true);
            boolean o_shouldCheckGetterExistancelitString9415__17 = meta.hasGetter("[0]");
            meta.getSetterType(__DSPOT_name_6059);
            org.junit.Assert.fail("shouldCheckGetterExistancelitString9415litBool18621_mg36342 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'P5g f&#Rk8v=X%\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistance__5 = meta.hasSetter("richField");
        Assert.assertTrue(o_shouldCheckSetterExistance__5);
        boolean o_shouldCheckSetterExistance__6 = meta.hasSetter("richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistance__6);
        boolean o_shouldCheckSetterExistance__7 = meta.hasSetter("richList");
        Assert.assertTrue(o_shouldCheckSetterExistance__7);
        boolean o_shouldCheckSetterExistance__8 = meta.hasSetter("richMap");
        Assert.assertTrue(o_shouldCheckSetterExistance__8);
        boolean o_shouldCheckSetterExistance__9 = meta.hasSetter("richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistance__9);
        boolean o_shouldCheckSetterExistance__10 = meta.hasSetter("richType");
        Assert.assertTrue(o_shouldCheckSetterExistance__10);
        boolean o_shouldCheckSetterExistance__11 = meta.hasSetter("richType.richField");
        Assert.assertTrue(o_shouldCheckSetterExistance__11);
        boolean o_shouldCheckSetterExistance__12 = meta.hasSetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistance__12);
        boolean o_shouldCheckSetterExistance__13 = meta.hasSetter("richType.richList");
        Assert.assertTrue(o_shouldCheckSetterExistance__13);
        boolean o_shouldCheckSetterExistance__14 = meta.hasSetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckSetterExistance__14);
        boolean o_shouldCheckSetterExistance__15 = meta.hasSetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistance__15);
        boolean o_shouldCheckSetterExistance__16 = meta.hasSetter("[0]");
        Assert.assertFalse(o_shouldCheckSetterExistance__16);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckSetterExistance__5);
        Assert.assertTrue(o_shouldCheckSetterExistance__6);
        Assert.assertTrue(o_shouldCheckSetterExistance__7);
        Assert.assertTrue(o_shouldCheckSetterExistance__8);
        Assert.assertTrue(o_shouldCheckSetterExistance__9);
        Assert.assertTrue(o_shouldCheckSetterExistance__10);
        Assert.assertTrue(o_shouldCheckSetterExistance__11);
        Assert.assertTrue(o_shouldCheckSetterExistance__12);
        Assert.assertTrue(o_shouldCheckSetterExistance__13);
        Assert.assertTrue(o_shouldCheckSetterExistance__14);
        Assert.assertTrue(o_shouldCheckSetterExistance__15);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg39973_failAssert204() throws Exception {
        try {
            String __DSPOT_name_7030 = "WhY,Y&02.w0lzR+[KFs!";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.hasSetter("richField");
            meta.hasSetter("richProperty");
            meta.hasSetter("richList");
            meta.hasSetter("richMap");
            meta.hasSetter("richList[0]");
            meta.hasSetter("richType");
            meta.hasSetter("richType.richField");
            meta.hasSetter("richType.richProperty");
            meta.hasSetter("richType.richList");
            meta.hasSetter("richType.richMap");
            meta.hasSetter("richType.richList[0]");
            meta.hasSetter("[0]");
            meta.getGetterType(__DSPOT_name_7030);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg39973 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'WhY,Y&02\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg39974_failAssert206() throws Exception {
        try {
            String __DSPOT_name_7031 = "_f@dIk1TIb9j[A52 {CA";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.hasSetter("richField");
            meta.hasSetter("richProperty");
            meta.hasSetter("richList");
            meta.hasSetter("richMap");
            meta.hasSetter("richList[0]");
            meta.hasSetter("richType");
            meta.hasSetter("richType.richField");
            meta.hasSetter("richType.richProperty");
            meta.hasSetter("richType.richList");
            meta.hasSetter("richType.richMap");
            meta.hasSetter("richType.richList[0]");
            meta.hasSetter("[0]");
            meta.getSetInvoker(__DSPOT_name_7031);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg39974 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'_f@dIk1TIb9j[A52 {CA\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39918() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistancelitString39918__5 = meta.hasSetter("richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__5);
        boolean o_shouldCheckSetterExistancelitString39918__6 = meta.hasSetter("richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__6);
        boolean o_shouldCheckSetterExistancelitString39918__7 = meta.hasSetter("richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__7);
        boolean o_shouldCheckSetterExistancelitString39918__8 = meta.hasSetter("richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__8);
        boolean o_shouldCheckSetterExistancelitString39918__9 = meta.hasSetter("richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__9);
        boolean o_shouldCheckSetterExistancelitString39918__10 = meta.hasSetter("richType");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__10);
        boolean o_shouldCheckSetterExistancelitString39918__11 = meta.hasSetter("richType.richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__11);
        boolean o_shouldCheckSetterExistancelitString39918__12 = meta.hasSetter("ri[hType.richProperty");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString39918__12);
        boolean o_shouldCheckSetterExistancelitString39918__13 = meta.hasSetter("richType.richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__13);
        boolean o_shouldCheckSetterExistancelitString39918__14 = meta.hasSetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__14);
        boolean o_shouldCheckSetterExistancelitString39918__15 = meta.hasSetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__15);
        boolean o_shouldCheckSetterExistancelitString39918__16 = meta.hasSetter("[0]");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString39918__16);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__5);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__6);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__7);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__8);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__9);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__10);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__11);
        Assert.assertFalse(o_shouldCheckSetterExistancelitString39918__12);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__13);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__14);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39918__15);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39892() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistancelitString39892__5 = meta.hasSetter("richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__5);
        boolean o_shouldCheckSetterExistancelitString39892__6 = meta.hasSetter("richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__6);
        boolean o_shouldCheckSetterExistancelitString39892__7 = meta.hasSetter("richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__7);
        boolean o_shouldCheckSetterExistancelitString39892__8 = meta.hasSetter(":");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString39892__8);
        boolean o_shouldCheckSetterExistancelitString39892__9 = meta.hasSetter("richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__9);
        boolean o_shouldCheckSetterExistancelitString39892__10 = meta.hasSetter("richType");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__10);
        boolean o_shouldCheckSetterExistancelitString39892__11 = meta.hasSetter("richType.richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__11);
        boolean o_shouldCheckSetterExistancelitString39892__12 = meta.hasSetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__12);
        boolean o_shouldCheckSetterExistancelitString39892__13 = meta.hasSetter("richType.richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__13);
        boolean o_shouldCheckSetterExistancelitString39892__14 = meta.hasSetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__14);
        boolean o_shouldCheckSetterExistancelitString39892__15 = meta.hasSetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__15);
        boolean o_shouldCheckSetterExistancelitString39892__16 = meta.hasSetter("[0]");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString39892__16);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__5);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__6);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__7);
        Assert.assertFalse(o_shouldCheckSetterExistancelitString39892__8);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__9);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__10);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__11);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__12);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__13);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__14);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39892__15);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39919() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistancelitString39919__5 = meta.hasSetter("richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__5);
        boolean o_shouldCheckSetterExistancelitString39919__6 = meta.hasSetter("richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__6);
        boolean o_shouldCheckSetterExistancelitString39919__7 = meta.hasSetter("richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__7);
        boolean o_shouldCheckSetterExistancelitString39919__8 = meta.hasSetter("richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__8);
        boolean o_shouldCheckSetterExistancelitString39919__9 = meta.hasSetter("richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__9);
        boolean o_shouldCheckSetterExistancelitString39919__10 = meta.hasSetter("richType");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__10);
        boolean o_shouldCheckSetterExistancelitString39919__11 = meta.hasSetter("richType.richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__11);
        boolean o_shouldCheckSetterExistancelitString39919__12 = meta.hasSetter("richType.richPmroperty");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString39919__12);
        boolean o_shouldCheckSetterExistancelitString39919__13 = meta.hasSetter("richType.richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__13);
        boolean o_shouldCheckSetterExistancelitString39919__14 = meta.hasSetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__14);
        boolean o_shouldCheckSetterExistancelitString39919__15 = meta.hasSetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__15);
        boolean o_shouldCheckSetterExistancelitString39919__16 = meta.hasSetter("[0]");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString39919__16);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__5);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__6);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__7);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__8);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__9);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__10);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__11);
        Assert.assertFalse(o_shouldCheckSetterExistancelitString39919__12);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__13);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__14);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString39919__15);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg39975_failAssert207() throws Exception {
        try {
            String __DSPOT_name_7032 = "]uu)t5l{u,Z%nYEk0?M>";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.hasSetter("richField");
            meta.hasSetter("richProperty");
            meta.hasSetter("richList");
            meta.hasSetter("richMap");
            meta.hasSetter("richList[0]");
            meta.hasSetter("richType");
            meta.hasSetter("richType.richField");
            meta.hasSetter("richType.richProperty");
            meta.hasSetter("richType.richList");
            meta.hasSetter("richType.richMap");
            meta.hasSetter("richType.richList[0]");
            meta.hasSetter("[0]");
            meta.getSetterType(__DSPOT_name_7032);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg39975 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \']uu)t5l{u,Z%nYEk0?M>\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg39978_failAssert203() throws Exception {
        try {
            String __DSPOT_name_7035 = "H>gxhcl2yd+8+cL0WB>!";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.hasSetter("richField");
            meta.hasSetter("richProperty");
            meta.hasSetter("richList");
            meta.hasSetter("richMap");
            meta.hasSetter("richList[0]");
            meta.hasSetter("richType");
            meta.hasSetter("richType.richField");
            meta.hasSetter("richType.richProperty");
            meta.hasSetter("richType.richList");
            meta.hasSetter("richType.richMap");
            meta.hasSetter("richType.richList[0]");
            meta.hasSetter("[0]");
            meta.metaClassForProperty(__DSPOT_name_7035);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg39978 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'H>gxhcl2yd+8+cL0WB>!\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg39972_failAssert205() throws Exception {
        try {
            String __DSPOT_name_7029 = "`8yM]|!1qIC8f=_bw_&)";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.hasSetter("richField");
            meta.hasSetter("richProperty");
            meta.hasSetter("richList");
            meta.hasSetter("richMap");
            meta.hasSetter("richList[0]");
            meta.hasSetter("richType");
            meta.hasSetter("richType.richField");
            meta.hasSetter("richType.richProperty");
            meta.hasSetter("richType.richList");
            meta.hasSetter("richType.richMap");
            meta.hasSetter("richType.richList[0]");
            meta.hasSetter("[0]");
            meta.getGetInvoker(__DSPOT_name_7029);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg39972 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'`8yM]|!1qIC8f=_bw_&)\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg39971() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_7028 = true;
        String __DSPOT_name_7027 = "1SySTS+]lp0-uxx13oWD";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistance_mg39971__7 = meta.hasSetter("richField");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg39971__7);
        boolean o_shouldCheckSetterExistance_mg39971__8 = meta.hasSetter("richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg39971__8);
        boolean o_shouldCheckSetterExistance_mg39971__9 = meta.hasSetter("richList");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg39971__9);
        boolean o_shouldCheckSetterExistance_mg39971__10 = meta.hasSetter("richMap");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg39971__10);
        boolean o_shouldCheckSetterExistance_mg39971__11 = meta.hasSetter("richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg39971__11);
        boolean o_shouldCheckSetterExistance_mg39971__12 = meta.hasSetter("richType");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg39971__12);
        boolean o_shouldCheckSetterExistance_mg39971__13 = meta.hasSetter("richType.richField");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg39971__13);
        boolean o_shouldCheckSetterExistance_mg39971__14 = meta.hasSetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg39971__14);
        boolean o_shouldCheckSetterExistance_mg39971__15 = meta.hasSetter("richType.richList");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg39971__15);
        boolean o_shouldCheckSetterExistance_mg39971__16 = meta.hasSetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg39971__16);
        boolean o_shouldCheckSetterExistance_mg39971__17 = meta.hasSetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg39971__17);
        boolean o_shouldCheckSetterExistance_mg39971__18 = meta.hasSetter("[0]");
        Assert.assertFalse(o_shouldCheckSetterExistance_mg39971__18);
        meta.findProperty(__DSPOT_name_7027, __DSPOT_useCamelCaseMapping_7028);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg39978_failAssert203_rv51034() throws Exception {
        try {
            String __DSPOT_name_8359 = "l{+Mw{DSUR1d)O)#Att#";
            String __DSPOT_name_7035 = "H>gxhcl2yd+8+cL0WB>!";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            boolean o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__9 = meta.hasSetter("richField");
            Assert.assertTrue(o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__9);
            boolean o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__10 = meta.hasSetter("richProperty");
            Assert.assertTrue(o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__10);
            boolean o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__11 = meta.hasSetter("richList");
            Assert.assertTrue(o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__11);
            boolean o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__12 = meta.hasSetter("richMap");
            Assert.assertTrue(o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__12);
            boolean o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__13 = meta.hasSetter("richList[0]");
            Assert.assertTrue(o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__13);
            boolean o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__14 = meta.hasSetter("richType");
            Assert.assertTrue(o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__14);
            boolean o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__15 = meta.hasSetter("richType.richField");
            Assert.assertTrue(o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__15);
            boolean o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__16 = meta.hasSetter("richType.richProperty");
            Assert.assertTrue(o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__16);
            boolean o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__17 = meta.hasSetter("richType.richList");
            Assert.assertTrue(o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__17);
            boolean o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__18 = meta.hasSetter("richType.richMap");
            Assert.assertTrue(o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__18);
            boolean o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__19 = meta.hasSetter("richType.richList[0]");
            Assert.assertTrue(o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__19);
            boolean o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__20 = meta.hasSetter("[0]");
            Assert.assertFalse(o_shouldCheckSetterExistance_mg39978_failAssert203_rv51034__20);
            MetaClass __DSPOT_invoc_20 = meta.metaClassForProperty(__DSPOT_name_7035);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg39978 should have thrown ReflectionException");
            __DSPOT_invoc_20.getGetInvoker(__DSPOT_name_8359);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg39976litString40635() throws Exception {
        String __DSPOT_name_7033 = "b##]WkD|]TUv)|s},qp>";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistance_mg39976__6 = meta.hasSetter("richField");
        boolean o_shouldCheckSetterExistance_mg39976__7 = meta.hasSetter("richProperty");
        boolean o_shouldCheckSetterExistance_mg39976__8 = meta.hasSetter("richList");
        boolean o_shouldCheckSetterExistance_mg39976__9 = meta.hasSetter("richMap");
        boolean o_shouldCheckSetterExistance_mg39976__10 = meta.hasSetter("richList[0]");
        boolean o_shouldCheckSetterExistance_mg39976__11 = meta.hasSetter("richType");
        boolean o_shouldCheckSetterExistance_mg39976__12 = meta.hasSetter("oU/}g&a.#+n%)u}(r2");
        boolean o_shouldCheckSetterExistance_mg39976__13 = meta.hasSetter("richType.richProperty");
        boolean o_shouldCheckSetterExistance_mg39976__14 = meta.hasSetter("richType.richList");
        boolean o_shouldCheckSetterExistance_mg39976__15 = meta.hasSetter("richType.richMap");
        boolean o_shouldCheckSetterExistance_mg39976__16 = meta.hasSetter("richType.richList[0]");
        boolean o_shouldCheckSetterExistance_mg39976__17 = meta.hasSetter("[0]");
        boolean o_shouldCheckSetterExistance_mg39976__18 = meta.hasGetter(__DSPOT_name_7033);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39864_mg50688_failAssert244() throws Exception {
        try {
            String __DSPOT_name_7975 = "(w&pl-9DzdYi6Z(}A-+,";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckSetterExistancelitString39864__5 = meta.hasSetter("richFied");
            boolean o_shouldCheckSetterExistancelitString39864__6 = meta.hasSetter("richProperty");
            boolean o_shouldCheckSetterExistancelitString39864__7 = meta.hasSetter("richList");
            boolean o_shouldCheckSetterExistancelitString39864__8 = meta.hasSetter("richMap");
            boolean o_shouldCheckSetterExistancelitString39864__9 = meta.hasSetter("richList[0]");
            boolean o_shouldCheckSetterExistancelitString39864__10 = meta.hasSetter("richType");
            boolean o_shouldCheckSetterExistancelitString39864__11 = meta.hasSetter("richType.richField");
            boolean o_shouldCheckSetterExistancelitString39864__12 = meta.hasSetter("richType.richProperty");
            boolean o_shouldCheckSetterExistancelitString39864__13 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistancelitString39864__14 = meta.hasSetter("richType.richMap");
            boolean o_shouldCheckSetterExistancelitString39864__15 = meta.hasSetter("richType.richList[0]");
            boolean o_shouldCheckSetterExistancelitString39864__16 = meta.hasSetter("[0]");
            meta.metaClassForProperty(__DSPOT_name_7975);
            org.junit.Assert.fail("shouldCheckSetterExistancelitString39864_mg50688 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'(w&pl-9DzdYi6Z(}A-+,\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39865_mg50060() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_7278 = true;
        String __DSPOT_name_7277 = "t.1Q[=nm@hU}RO)/Kkb3";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistancelitString39865__5 = meta.hasSetter("y[BFPhp]H");
        boolean o_shouldCheckSetterExistancelitString39865__6 = meta.hasSetter("richProperty");
        boolean o_shouldCheckSetterExistancelitString39865__7 = meta.hasSetter("richList");
        boolean o_shouldCheckSetterExistancelitString39865__8 = meta.hasSetter("richMap");
        boolean o_shouldCheckSetterExistancelitString39865__9 = meta.hasSetter("richList[0]");
        boolean o_shouldCheckSetterExistancelitString39865__10 = meta.hasSetter("richType");
        boolean o_shouldCheckSetterExistancelitString39865__11 = meta.hasSetter("richType.richField");
        boolean o_shouldCheckSetterExistancelitString39865__12 = meta.hasSetter("richType.richProperty");
        boolean o_shouldCheckSetterExistancelitString39865__13 = meta.hasSetter("richType.richList");
        boolean o_shouldCheckSetterExistancelitString39865__14 = meta.hasSetter("richType.richMap");
        boolean o_shouldCheckSetterExistancelitString39865__15 = meta.hasSetter("richType.richList[0]");
        boolean o_shouldCheckSetterExistancelitString39865__16 = meta.hasSetter("[0]");
        meta.findProperty(__DSPOT_name_7277, __DSPOT_useCamelCaseMapping_7278);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39867_mg50209() throws Exception {
        String __DSPOT_name_7443 = "7zPnsD(g5!w-t>W[CMeV";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistancelitString39867__5 = meta.hasSetter("\n");
        boolean o_shouldCheckSetterExistancelitString39867__6 = meta.hasSetter("richProperty");
        boolean o_shouldCheckSetterExistancelitString39867__7 = meta.hasSetter("richList");
        boolean o_shouldCheckSetterExistancelitString39867__8 = meta.hasSetter("richMap");
        boolean o_shouldCheckSetterExistancelitString39867__9 = meta.hasSetter("richList[0]");
        boolean o_shouldCheckSetterExistancelitString39867__10 = meta.hasSetter("richType");
        boolean o_shouldCheckSetterExistancelitString39867__11 = meta.hasSetter("richType.richField");
        boolean o_shouldCheckSetterExistancelitString39867__12 = meta.hasSetter("richType.richProperty");
        boolean o_shouldCheckSetterExistancelitString39867__13 = meta.hasSetter("richType.richList");
        boolean o_shouldCheckSetterExistancelitString39867__14 = meta.hasSetter("richType.richMap");
        boolean o_shouldCheckSetterExistancelitString39867__15 = meta.hasSetter("richType.richList[0]");
        boolean o_shouldCheckSetterExistancelitString39867__16 = meta.hasSetter("[0]");
        boolean o_shouldCheckSetterExistancelitString39867_mg50209__42 = meta.hasGetter(__DSPOT_name_7443);
        Assert.assertFalse(o_shouldCheckSetterExistancelitString39867_mg50209__42);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39950_mg50403_failAssert249() throws Exception {
        try {
            String __DSPOT_name_7659 = "8l9lq9)B?<O^8j`J&[M7";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckSetterExistancelitString39950__5 = meta.hasSetter("richField");
            boolean o_shouldCheckSetterExistancelitString39950__6 = meta.hasSetter("richProperty");
            boolean o_shouldCheckSetterExistancelitString39950__7 = meta.hasSetter("richList");
            boolean o_shouldCheckSetterExistancelitString39950__8 = meta.hasSetter("richMap");
            boolean o_shouldCheckSetterExistancelitString39950__9 = meta.hasSetter("richList[0]");
            boolean o_shouldCheckSetterExistancelitString39950__10 = meta.hasSetter("richType");
            boolean o_shouldCheckSetterExistancelitString39950__11 = meta.hasSetter("richType.richField");
            boolean o_shouldCheckSetterExistancelitString39950__12 = meta.hasSetter("richType.richProperty");
            boolean o_shouldCheckSetterExistancelitString39950__13 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistancelitString39950__14 = meta.hasSetter("richType.richMap");
            boolean o_shouldCheckSetterExistancelitString39950__15 = meta.hasSetter("richType.richList[0]");
            boolean o_shouldCheckSetterExistancelitString39950__16 = meta.hasSetter("A0]");
            meta.getGetInvoker(__DSPOT_name_7659);
            org.junit.Assert.fail("shouldCheckSetterExistancelitString39950_mg50403 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'8l9lq9)B?<O^8j`J&[M7\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39863_mg50757_failAssert221() throws Exception {
        try {
            String __DSPOT_name_8052 = "lG]QlsL*J:nPPK9YO&xY";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckSetterExistancelitString39863__5 = meta.hasSetter("richF`ield");
            boolean o_shouldCheckSetterExistancelitString39863__6 = meta.hasSetter("richProperty");
            boolean o_shouldCheckSetterExistancelitString39863__7 = meta.hasSetter("richList");
            boolean o_shouldCheckSetterExistancelitString39863__8 = meta.hasSetter("richMap");
            boolean o_shouldCheckSetterExistancelitString39863__9 = meta.hasSetter("richList[0]");
            boolean o_shouldCheckSetterExistancelitString39863__10 = meta.hasSetter("richType");
            boolean o_shouldCheckSetterExistancelitString39863__11 = meta.hasSetter("richType.richField");
            boolean o_shouldCheckSetterExistancelitString39863__12 = meta.hasSetter("richType.richProperty");
            boolean o_shouldCheckSetterExistancelitString39863__13 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistancelitString39863__14 = meta.hasSetter("richType.richMap");
            boolean o_shouldCheckSetterExistancelitString39863__15 = meta.hasSetter("richType.richList[0]");
            boolean o_shouldCheckSetterExistancelitString39863__16 = meta.hasSetter("[0]");
            meta.getSetterType(__DSPOT_name_8052);
            org.junit.Assert.fail("shouldCheckSetterExistancelitString39863_mg50757 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'lG]QlsL*J:nPPK9YO&xY\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39861_mg50704() throws Exception {
        String __DSPOT_name_7993 = "^@|0?.|uGCS-}|RHYDB-";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistancelitString39861__5 = meta.hasSetter("richProperty");
        boolean o_shouldCheckSetterExistancelitString39861__6 = meta.hasSetter("richProperty");
        boolean o_shouldCheckSetterExistancelitString39861__7 = meta.hasSetter("richProperty");
        boolean o_shouldCheckSetterExistancelitString39861__8 = meta.hasSetter("richList");
        boolean o_shouldCheckSetterExistancelitString39861__9 = meta.hasSetter("richMap");
        boolean o_shouldCheckSetterExistancelitString39861__10 = meta.hasSetter("richList[0]");
        boolean o_shouldCheckSetterExistancelitString39861__11 = meta.hasSetter("richType");
        boolean o_shouldCheckSetterExistancelitString39861__12 = meta.hasSetter("richType.richField");
        boolean o_shouldCheckSetterExistancelitString39861__13 = meta.hasSetter("richType.richProperty");
        boolean o_shouldCheckSetterExistancelitString39861__14 = meta.hasSetter("richType.richList");
        boolean o_shouldCheckSetterExistancelitString39861__15 = meta.hasSetter("richType.richMap");
        boolean o_shouldCheckSetterExistancelitString39861__16 = meta.hasSetter("richType.richList[0]");
        boolean o_shouldCheckSetterExistancelitString39861__17 = meta.hasSetter("[0]");
        boolean o_shouldCheckSetterExistancelitString39861_mg50704__45 = meta.hasGetter(__DSPOT_name_7993);
        Assert.assertFalse(o_shouldCheckSetterExistancelitString39861_mg50704__45);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39901_mg50782_failAssert222() throws Exception {
        try {
            String __DSPOT_name_8080 = "#@bt#/.;Lv=8k.SE1Ib[";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckSetterExistancelitString39901__5 = meta.hasSetter("richField");
            boolean o_shouldCheckSetterExistancelitString39901__6 = meta.hasSetter("richProperty");
            boolean o_shouldCheckSetterExistancelitString39901__7 = meta.hasSetter("richList");
            boolean o_shouldCheckSetterExistancelitString39901__8 = meta.hasSetter("richMap");
            boolean o_shouldCheckSetterExistancelitString39901__9 = meta.hasSetter("richList[0]");
            boolean o_shouldCheckSetterExistancelitString39901__10 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistancelitString39901__11 = meta.hasSetter("richType.richField");
            boolean o_shouldCheckSetterExistancelitString39901__12 = meta.hasSetter("richType.richProperty");
            boolean o_shouldCheckSetterExistancelitString39901__13 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistancelitString39901__14 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistancelitString39901__15 = meta.hasSetter("richType.richMap");
            boolean o_shouldCheckSetterExistancelitString39901__16 = meta.hasSetter("richType.richList[0]");
            boolean o_shouldCheckSetterExistancelitString39901__17 = meta.hasSetter("[0]");
            meta.getGetterType(__DSPOT_name_8080);
            org.junit.Assert.fail("shouldCheckSetterExistancelitString39901_mg50782 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'#@bt#/\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancenull39979_failAssert214litString47381() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, null);
            meta.hasSetter("richField");
            meta.hasSetter("richProperty");
            meta.hasSetter("richList");
            meta.hasSetter("richMap");
            meta.hasSetter("richList[0]");
            meta.hasSetter("richType");
            meta.hasSetter("richType.richField");
            meta.hasSetter("richType.richProperty");
            meta.hasSetter("richType.richList");
            meta.hasSetter("richType.richMap");
            meta.hasSetter(":");
            meta.hasSetter("[0]");
            org.junit.Assert.fail("shouldCheckSetterExistancenull39979 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39868_mg50774_failAssert246() throws Exception {
        try {
            String __DSPOT_name_8071 = "h|09($ t*KQaS}^c7u9;";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckSetterExistancelitString39868__5 = meta.hasSetter(":");
            boolean o_shouldCheckSetterExistancelitString39868__6 = meta.hasSetter("richProperty");
            boolean o_shouldCheckSetterExistancelitString39868__7 = meta.hasSetter("richList");
            boolean o_shouldCheckSetterExistancelitString39868__8 = meta.hasSetter("richMap");
            boolean o_shouldCheckSetterExistancelitString39868__9 = meta.hasSetter("richList[0]");
            boolean o_shouldCheckSetterExistancelitString39868__10 = meta.hasSetter("richType");
            boolean o_shouldCheckSetterExistancelitString39868__11 = meta.hasSetter("richType.richField");
            boolean o_shouldCheckSetterExistancelitString39868__12 = meta.hasSetter("richType.richProperty");
            boolean o_shouldCheckSetterExistancelitString39868__13 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistancelitString39868__14 = meta.hasSetter("richType.richMap");
            boolean o_shouldCheckSetterExistancelitString39868__15 = meta.hasSetter("richType.richList[0]");
            boolean o_shouldCheckSetterExistancelitString39868__16 = meta.hasSetter("[0]");
            meta.getSetInvoker(__DSPOT_name_8071);
            org.junit.Assert.fail("shouldCheckSetterExistancelitString39868_mg50774 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'h|09($ t*KQaS}^c7u9;\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39867_add48326_mg70108_failAssert272() throws Exception {
        try {
            String __DSPOT_name_8703 = "Cnb.&uW+h]>x& =&{;tA";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckSetterExistancelitString39867__5 = meta.hasSetter("\n");
            boolean o_shouldCheckSetterExistancelitString39867__6 = meta.hasSetter("richProperty");
            boolean o_shouldCheckSetterExistancelitString39867__7 = meta.hasSetter("richList");
            boolean o_shouldCheckSetterExistancelitString39867__8 = meta.hasSetter("richMap");
            boolean o_shouldCheckSetterExistancelitString39867__9 = meta.hasSetter("richList[0]");
            boolean o_shouldCheckSetterExistancelitString39867__10 = meta.hasSetter("richType");
            boolean o_shouldCheckSetterExistancelitString39867_add48326__23 = meta.hasSetter("richType.richField");
            boolean o_shouldCheckSetterExistancelitString39867__11 = meta.hasSetter("richType.richField");
            boolean o_shouldCheckSetterExistancelitString39867__12 = meta.hasSetter("richType.richProperty");
            boolean o_shouldCheckSetterExistancelitString39867__13 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistancelitString39867__14 = meta.hasSetter("richType.richMap");
            boolean o_shouldCheckSetterExistancelitString39867__15 = meta.hasSetter("richType.richList[0]");
            boolean o_shouldCheckSetterExistancelitString39867__16 = meta.hasSetter("[0]");
            meta.getGetInvoker(__DSPOT_name_8703);
            org.junit.Assert.fail("shouldCheckSetterExistancelitString39867_add48326_mg70108 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'Cnb.&uW+h]>x& =&{;tA\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg39976_mg49865_failAssert247_add69459() throws Exception {
        try {
            String __DSPOT_name_7061 = "gSPH{7K};jM;>Hlt28]o";
            String __DSPOT_name_7033 = "b##]WkD|]TUv)|s},qp>";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            boolean o_shouldCheckSetterExistance_mg39976__6 = meta.hasSetter("richField");
            boolean o_shouldCheckSetterExistance_mg39976__7 = meta.hasSetter("richProperty");
            boolean o_shouldCheckSetterExistance_mg39976__8 = meta.hasSetter("richList");
            boolean o_shouldCheckSetterExistance_mg39976__9 = meta.hasSetter("richMap");
            boolean o_shouldCheckSetterExistance_mg39976__10 = meta.hasSetter("richList[0]");
            boolean o_shouldCheckSetterExistance_mg39976__11 = meta.hasSetter("richType");
            boolean o_shouldCheckSetterExistance_mg39976__12 = meta.hasSetter("richType.richField");
            boolean o_shouldCheckSetterExistance_mg39976_mg49865_failAssert247_add69459__30 = meta.hasSetter("richType.richProperty");
            Assert.assertTrue(o_shouldCheckSetterExistance_mg39976_mg49865_failAssert247_add69459__30);
            boolean o_shouldCheckSetterExistance_mg39976__13 = meta.hasSetter("richType.richProperty");
            boolean o_shouldCheckSetterExistance_mg39976__14 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistance_mg39976__15 = meta.hasSetter("richType.richMap");
            boolean o_shouldCheckSetterExistance_mg39976__16 = meta.hasSetter("richType.richList[0]");
            boolean o_shouldCheckSetterExistance_mg39976__17 = meta.hasSetter("[0]");
            boolean o_shouldCheckSetterExistance_mg39976__18 = meta.hasGetter(__DSPOT_name_7033);
            meta.getSetInvoker(__DSPOT_name_7061);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg39976_mg49865 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39866_mg50050litString53487() throws Exception {
        String __DSPOT_name_7266 = "eV*LJBllSUb#F?kVkCM}";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistancelitString39866__5 = meta.hasSetter("");
        boolean o_shouldCheckSetterExistancelitString39866__6 = meta.hasSetter("richProperty");
        boolean o_shouldCheckSetterExistancelitString39866__7 = meta.hasSetter("richList");
        boolean o_shouldCheckSetterExistancelitString39866__8 = meta.hasSetter("richMap");
        boolean o_shouldCheckSetterExistancelitString39866__9 = meta.hasSetter("richList[0]");
        boolean o_shouldCheckSetterExistancelitString39866__10 = meta.hasSetter("richType");
        boolean o_shouldCheckSetterExistancelitString39866__11 = meta.hasSetter("richType.richField");
        boolean o_shouldCheckSetterExistancelitString39866__12 = meta.hasSetter("richType.richProperty");
        boolean o_shouldCheckSetterExistancelitString39866__13 = meta.hasSetter("`ichType.richList");
        boolean o_shouldCheckSetterExistancelitString39866__14 = meta.hasSetter("richType.richMap");
        boolean o_shouldCheckSetterExistancelitString39866__15 = meta.hasSetter("richType.richList[0]");
        boolean o_shouldCheckSetterExistancelitString39866__16 = meta.hasSetter("[0]");
        meta.findProperty(__DSPOT_name_7266);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg39971_add47706_remove69807() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_7028 = true;
        String __DSPOT_name_7027 = "1SySTS+]lp0-uxx13oWD";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistance_mg39971__7 = meta.hasSetter("richField");
        boolean o_shouldCheckSetterExistance_mg39971__8 = meta.hasSetter("richProperty");
        boolean o_shouldCheckSetterExistance_mg39971__9 = meta.hasSetter("richList");
        boolean o_shouldCheckSetterExistance_mg39971__10 = meta.hasSetter("richMap");
        boolean o_shouldCheckSetterExistance_mg39971__11 = meta.hasSetter("richList[0]");
        boolean o_shouldCheckSetterExistance_mg39971__12 = meta.hasSetter("richType");
        boolean o_shouldCheckSetterExistance_mg39971__13 = meta.hasSetter("richType.richField");
        boolean o_shouldCheckSetterExistance_mg39971__14 = meta.hasSetter("richType.richProperty");
        boolean o_shouldCheckSetterExistance_mg39971__15 = meta.hasSetter("richType.richList");
        boolean o_shouldCheckSetterExistance_mg39971__16 = meta.hasSetter("richType.richMap");
        boolean o_shouldCheckSetterExistance_mg39971__17 = meta.hasSetter("richType.richList[0]");
        boolean o_shouldCheckSetterExistance_mg39971__18 = meta.hasSetter("[0]");
        meta.findProperty(__DSPOT_name_7027, __DSPOT_useCamelCaseMapping_7028);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39865_mg50060litBool66467() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_7278 = false;
        String __DSPOT_name_7277 = "t.1Q[=nm@hU}RO)/Kkb3";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistancelitString39865__5 = meta.hasSetter("y[BFPhp]H");
        boolean o_shouldCheckSetterExistancelitString39865__6 = meta.hasSetter("richProperty");
        boolean o_shouldCheckSetterExistancelitString39865__7 = meta.hasSetter("richList");
        boolean o_shouldCheckSetterExistancelitString39865__8 = meta.hasSetter("richMap");
        boolean o_shouldCheckSetterExistancelitString39865__9 = meta.hasSetter("richList[0]");
        boolean o_shouldCheckSetterExistancelitString39865__10 = meta.hasSetter("richType");
        boolean o_shouldCheckSetterExistancelitString39865__11 = meta.hasSetter("richType.richField");
        boolean o_shouldCheckSetterExistancelitString39865__12 = meta.hasSetter("richType.richProperty");
        boolean o_shouldCheckSetterExistancelitString39865__13 = meta.hasSetter("richType.richList");
        boolean o_shouldCheckSetterExistancelitString39865__14 = meta.hasSetter("richType.richMap");
        boolean o_shouldCheckSetterExistancelitString39865__15 = meta.hasSetter("richType.richList[0]");
        boolean o_shouldCheckSetterExistancelitString39865__16 = meta.hasSetter("[0]");
        meta.findProperty(__DSPOT_name_7277, __DSPOT_useCamelCaseMapping_7278);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg39971null51203_failAssert266litBool66476() throws Exception {
        try {
            boolean __DSPOT_useCamelCaseMapping_7028 = false;
            String __DSPOT_name_7027 = "1SySTS+]lp0-uxx13oWD";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            boolean o_shouldCheckSetterExistance_mg39971__7 = meta.hasSetter(null);
            boolean o_shouldCheckSetterExistance_mg39971__8 = meta.hasSetter("richProperty");
            boolean o_shouldCheckSetterExistance_mg39971__9 = meta.hasSetter("richList");
            boolean o_shouldCheckSetterExistance_mg39971__10 = meta.hasSetter("richMap");
            boolean o_shouldCheckSetterExistance_mg39971__11 = meta.hasSetter("richList[0]");
            boolean o_shouldCheckSetterExistance_mg39971__12 = meta.hasSetter("richType");
            boolean o_shouldCheckSetterExistance_mg39971__13 = meta.hasSetter("richType.richField");
            boolean o_shouldCheckSetterExistance_mg39971__14 = meta.hasSetter("richType.richProperty");
            boolean o_shouldCheckSetterExistance_mg39971__15 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistance_mg39971__16 = meta.hasSetter("richType.richMap");
            boolean o_shouldCheckSetterExistance_mg39971__17 = meta.hasSetter("richType.richList[0]");
            boolean o_shouldCheckSetterExistance_mg39971__18 = meta.hasSetter("[0]");
            meta.findProperty(__DSPOT_name_7027, __DSPOT_useCamelCaseMapping_7028);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg39971null51203 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39862_mg50498_mg70462_failAssert270() throws Exception {
        try {
            String __DSPOT_name_9096 = "qT8_}Fd[%VWRN1s1mfG3";
            String __DSPOT_name_7764 = "[%TA1qH/L2/6,vPT,i[6";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckSetterExistancelitString39862__5 = meta.hasSetter("r6chField");
            boolean o_shouldCheckSetterExistancelitString39862__6 = meta.hasSetter("richProperty");
            boolean o_shouldCheckSetterExistancelitString39862__7 = meta.hasSetter("richList");
            boolean o_shouldCheckSetterExistancelitString39862__8 = meta.hasSetter("richMap");
            boolean o_shouldCheckSetterExistancelitString39862__9 = meta.hasSetter("richList[0]");
            boolean o_shouldCheckSetterExistancelitString39862__10 = meta.hasSetter("richType");
            boolean o_shouldCheckSetterExistancelitString39862__11 = meta.hasSetter("richType.richField");
            boolean o_shouldCheckSetterExistancelitString39862__12 = meta.hasSetter("richType.richProperty");
            boolean o_shouldCheckSetterExistancelitString39862__13 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistancelitString39862__14 = meta.hasSetter("richType.richMap");
            boolean o_shouldCheckSetterExistancelitString39862__15 = meta.hasSetter("richType.richList[0]");
            boolean o_shouldCheckSetterExistancelitString39862__16 = meta.hasSetter("[0]");
            boolean o_shouldCheckSetterExistancelitString39862_mg50498__42 = meta.hasSetter(__DSPOT_name_7764);
            meta.getSetterType(__DSPOT_name_9096);
            org.junit.Assert.fail("shouldCheckSetterExistancelitString39862_mg50498_mg70462 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'qT8_}Fd\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString39901_mg50782_failAssert222_rv72079() throws Exception {
        try {
            String __DSPOT_arg0_10626 = "o0vT7Gbl+O|+,aEN,mV+";
            String __DSPOT_name_8080 = "#@bt#/.;Lv=8k.SE1Ib[";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            boolean o_shouldCheckSetterExistancelitString39901__5 = meta.hasSetter("richField");
            boolean o_shouldCheckSetterExistancelitString39901__6 = meta.hasSetter("richProperty");
            boolean o_shouldCheckSetterExistancelitString39901__7 = meta.hasSetter("richList");
            boolean o_shouldCheckSetterExistancelitString39901__8 = meta.hasSetter("richMap");
            boolean o_shouldCheckSetterExistancelitString39901__9 = meta.hasSetter("richList[0]");
            boolean o_shouldCheckSetterExistancelitString39901__10 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistancelitString39901__11 = meta.hasSetter("richType.richField");
            boolean o_shouldCheckSetterExistancelitString39901__12 = meta.hasSetter("richType.richProperty");
            boolean o_shouldCheckSetterExistancelitString39901__13 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistancelitString39901__14 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistancelitString39901__15 = meta.hasSetter("richType.richMap");
            boolean o_shouldCheckSetterExistancelitString39901__16 = meta.hasSetter("richType.richList[0]");
            boolean o_shouldCheckSetterExistancelitString39901__17 = meta.hasSetter("[0]");
            Class<?> __DSPOT_invoc_47 = meta.getGetterType(__DSPOT_name_8080);
            org.junit.Assert.fail("shouldCheckSetterExistancelitString39901_mg50782 should have thrown ReflectionException");
            __DSPOT_invoc_47.getResourceAsStream(__DSPOT_arg0_10626);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg39970_add47646_mg70218_failAssert293() throws Exception {
        try {
            String __DSPOT_name_8825 = ":/V9^*l!ZS>*in!(;hRG";
            String __DSPOT_name_7026 = "{/-?3v<jw>iRvBSnIN{P";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckSetterExistance_mg39970__6 = meta.hasSetter("richField");
            boolean o_shouldCheckSetterExistance_mg39970__7 = meta.hasSetter("richProperty");
            boolean o_shouldCheckSetterExistance_mg39970__8 = meta.hasSetter("richList");
            boolean o_shouldCheckSetterExistance_mg39970__9 = meta.hasSetter("richMap");
            boolean o_shouldCheckSetterExistance_mg39970_add47646__18 = meta.hasSetter("richList[0]");
            boolean o_shouldCheckSetterExistance_mg39970__10 = meta.hasSetter("richList[0]");
            boolean o_shouldCheckSetterExistance_mg39970__11 = meta.hasSetter("richType");
            boolean o_shouldCheckSetterExistance_mg39970__12 = meta.hasSetter("richType.richField");
            boolean o_shouldCheckSetterExistance_mg39970__13 = meta.hasSetter("richType.richProperty");
            boolean o_shouldCheckSetterExistance_mg39970__14 = meta.hasSetter("richType.richList");
            boolean o_shouldCheckSetterExistance_mg39970__15 = meta.hasSetter("richType.richMap");
            boolean o_shouldCheckSetterExistance_mg39970__16 = meta.hasSetter("richType.richList[0]");
            boolean o_shouldCheckSetterExistance_mg39970__17 = meta.hasSetter("[0]");
            meta.findProperty(__DSPOT_name_7026);
            meta.getSetInvoker(__DSPOT_name_8825);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg39970_add47646_mg70218 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \':/V9^*l!ZS>*in!(;hRG\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_add39964litString41983_add68985() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistance_add39964__5 = meta.hasSetter("richField");
        boolean o_shouldCheckSetterExistance_add39964__6 = meta.hasSetter("richProperty");
        boolean o_shouldCheckSetterExistance_add39964__7 = meta.hasSetter("richList");
        boolean o_shouldCheckSetterExistance_add39964__8 = meta.hasSetter("richMap");
        boolean o_shouldCheckSetterExistance_add39964__9 = meta.hasSetter("richList[0]");
        boolean o_shouldCheckSetterExistance_add39964__10 = meta.hasSetter("richType");
        boolean o_shouldCheckSetterExistance_add39964__11 = meta.hasSetter("richType.richField");
        boolean o_shouldCheckSetterExistance_add39964litString41983_add68985__26 = meta.hasSetter("richType.9ichField");
        Assert.assertFalse(o_shouldCheckSetterExistance_add39964litString41983_add68985__26);
        boolean o_shouldCheckSetterExistance_add39964__12 = meta.hasSetter("richType.9ichField");
        boolean o_shouldCheckSetterExistance_add39964__13 = meta.hasSetter("richType.richProperty");
        boolean o_shouldCheckSetterExistance_add39964__14 = meta.hasSetter("richType.richList");
        boolean o_shouldCheckSetterExistance_add39964__15 = meta.hasSetter("richType.richMap");
        boolean o_shouldCheckSetterExistance_add39964__16 = meta.hasSetter("richType.richList[0]");
        boolean o_shouldCheckSetterExistance_add39964__17 = meta.hasSetter("[0]");
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertFalse(o_shouldCheckSetterExistance_add39964litString41983_add68985__26);
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg75135_failAssert308() throws Exception {
        try {
            String __DSPOT_name_10691 = "eY;13xI5#]@fG5l]Syx>";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            meta.getSetInvoker(__DSPOT_name_10691);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg75135 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'eY;13xI5#]@fG5l]Syx>\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg75133_failAssert310() throws Exception {
        try {
            String __DSPOT_name_10689 = "YtA{Ig+1iM$S?Y]DhA8[";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            meta.getGetInvoker(__DSPOT_name_10689);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg75133 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'YtA{Ig+1iM$S?Y]DhA8[\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString75090_failAssert343() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richTpe.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString75090 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'richTpe\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg75132_failAssert311() throws Exception {
        try {
            boolean __DSPOT_useCamelCaseMapping_10688 = false;
            String __DSPOT_name_10687 = "2IqnLBR7}4[.AH5uZ#}W";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            meta.findProperty(__DSPOT_name_10687, __DSPOT_useCamelCaseMapping_10688);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg75132 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString75036_failAssert379() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString75036 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString75037_failAssert380() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("\n");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString75037 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'\n\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString75039() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("richField");
        meta.getGetterType("richType");
        meta.getGetterType("richType");
        meta.getGetterType("richList");
        meta.getGetterType("richMap");
        meta.getGetterType("richList[0]");
        meta.getGetterType("richType.richField");
        meta.getGetterType("richType.richProperty");
        meta.getGetterType("richType.richList");
        meta.getGetterType("richType.richMap");
        meta.getGetterType("richType.richList[0]");
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg75136_failAssert307() throws Exception {
        try {
            String __DSPOT_name_10692 = "&,o-I :y<9&ISL_pfIpo";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            meta.getSetterType(__DSPOT_name_10692);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg75136 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'&,o-I :y<9&ISL_pfIpo\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg75136_failAssert307_add82109() throws Exception {
        try {
            String __DSPOT_name_10692 = "&,o-I :y<9&ISL_pfIpo";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            meta.getSetterType(__DSPOT_name_10692);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg75136 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString75111_remove83572() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("richField");
        meta.getGetterType("richProperty");
        meta.getGetterType("richList");
        meta.getGetterType("richMap");
        meta.getGetterType("richType");
        meta.getGetterType("richType.richField");
        meta.getGetterType("richType.richField");
        meta.getGetterType("richType.richProperty");
        meta.getGetterType("richType.richList");
        meta.getGetterType("richType.richMap");
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString75054_failAssert371litString80398() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType(":");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("rich8Type.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString75054 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString75111_mg83829_failAssert402() throws Exception {
        try {
            String __DSPOT_name_10899 = "<kwtD)&w`hJf,^j!%0|3";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetInvoker(__DSPOT_name_10899);
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString75111_mg83829 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'<kwtD)&w`hJf,^j!%0|3\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString75080_failAssert341_add82554() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("rtchType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString75080 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg75132_failAssert311litBool81668() throws Exception {
        try {
            boolean __DSPOT_useCamelCaseMapping_10688 = true;
            String __DSPOT_name_10687 = "2IqnLBR7}4[.AH5uZ#}W";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            meta.findProperty(__DSPOT_name_10687, __DSPOT_useCamelCaseMapping_10688);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg75132 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString75079litString76979_failAssert410() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richProperty");
            meta.getGetterType("richProperty");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType(":");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString75079litString76979 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \':\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg75132_failAssert311litBool81668_rv268477() throws Exception {
        try {
            boolean __DSPOT_useCamelCaseMapping_10688 = true;
            String __DSPOT_name_10687 = "2IqnLBR7}4[.AH5uZ#}W";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            Class<?> __DSPOT_invoc_15 = meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            meta.findProperty(__DSPOT_name_10687, __DSPOT_useCamelCaseMapping_10688);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg75132 should have thrown StringIndexOutOfBoundsException");
            __DSPOT_invoc_15.getMethods();
        } catch (StringIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg75136_failAssert307_add82109_rv224737() throws Exception {
        try {
            String __DSPOT_name_10692 = "&,o-I :y<9&ISL_pfIpo";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            Class<?> __DSPOT_invoc_26 = meta.getSetterType(__DSPOT_name_10692);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg75136 should have thrown ReflectionException");
            __DSPOT_invoc_26.isSynthetic();
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString75069_failAssert350litString79496litString167745() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("\n");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("R5!C>-uXgm[A2D1O5");
            meta.getGetterType("riAhType.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString75069 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString75071_remove83588litString165890_failAssert429() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richMap");
            meta.getGetterType("ricRhList[0]");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString75071_remove83588litString165890 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'ricRhList\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg75133_failAssert310_add82150_add169674() throws Exception {
        try {
            String __DSPOT_name_10689 = "YtA{Ig+1iM$S?Y]DhA8[";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            meta.getGetInvoker(__DSPOT_name_10689);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg75133 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString75071_remove83588_mg172919_failAssert432() throws Exception {
        try {
            String __DSPOT_name_29308 = "C2Ze_UU}{W?.zxH282;Y";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richList[0]");
            meta.getSetterType(__DSPOT_name_29308);
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString75071_remove83588_mg172919 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'C2Ze_UU}{W?\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString75080_failAssert341_mg84202_add168685() throws Exception {
        try {
            String __DSPOT_name_11313 = "mFsn!K&%Zdw9gR.=h_jW";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("rtchType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString75080 should have thrown ReflectionException");
            meta.hasGetter(__DSPOT_name_11313);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString296590_failAssert514() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("rchType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString296590 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'rchType\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString296527_failAssert482() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType(":Mbps!2_[");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString296527 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_mg296627_failAssert439() throws Exception {
        try {
            String __DSPOT_name_56103 = "{p>8kc!.fXF/?`VnL6J/";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            meta.getSetInvoker(__DSPOT_name_56103);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_mg296627 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'{p>8kc!.fXF/?`VnL6J/\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_mg296625_failAssert443() throws Exception {
        try {
            String __DSPOT_name_56101 = "}4W2]T(#:^}>$5g%&dDv";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            meta.getGetInvoker(__DSPOT_name_56101);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_mg296625 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'}4W2]T(#:^}>$5g%&dDv\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString296539() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getSetterType("richField");
        meta.getSetterType("richProperty");
        meta.getSetterType("richType.richList");
        meta.getSetterType("richType.richList");
        meta.getSetterType("richType.richList");
        meta.getSetterType("richMap");
        meta.getSetterType("richList[0]");
        meta.getSetterType("richType");
        meta.getSetterType("richType.richField");
        meta.getSetterType("richType.richProperty");
        meta.getSetterType("richType.richMap");
        meta.getSetterType("richType.richList[0]");
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString296533_failAssert522() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richPropqerty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString296533 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'richPropqerty\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString296531_mg305280_failAssert532() throws Exception {
        try {
            String __DSPOT_name_56284 = "zhVuw8An(z(>}VlY}+7.";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richField");
            meta.getSetterType("richField");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            meta.getSetterType(__DSPOT_name_56284);
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString296531_mg305280 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'zhVuw8An(z(>}VlY}+7\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_mg296624litBool303114() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_56100 = true;
        String __DSPOT_name_56099 = "wkL]g*3pR3H49>vKzt}b";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getSetterType("richField");
        meta.getSetterType("richProperty");
        meta.getSetterType("richList");
        meta.getSetterType("richMap");
        meta.getSetterType("richList[0]");
        meta.getSetterType("richType");
        meta.getSetterType("richType.richField");
        meta.getSetterType("richType.richProperty");
        meta.getSetterType("richType.richList");
        meta.getSetterType("richType.richMap");
        meta.getSetterType("richType.richList[0]");
        meta.findProperty(__DSPOT_name_56099, __DSPOT_useCamelCaseMapping_56100);
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString296540litString298611_failAssert546() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("rich1ap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString296540litString298611 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'rich1ap\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString296527_failAssert482litString301140() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getSetterType(":Mbps!2_[");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richM1ap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString296527 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString296539_mg305322_failAssert548() throws Exception {
        try {
            String __DSPOT_name_56331 = "FF&sld|na[CBCQumydjv";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            meta.getGetInvoker(__DSPOT_name_56331);
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString296539_mg305322 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'FF&sld|na[CBCQumydjv\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString296539_add303477_mg396436_failAssert557() throws Exception {
        try {
            String __DSPOT_name_74171 = "F./Gz#y}xGi9#qhf.d%<";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            meta.getSetInvoker(__DSPOT_name_74171);
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString296539_add303477_mg396436 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'F./Gz#y}xGi9#qhf.d%<\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString296539_add303472_mg397155_failAssert567() throws Exception {
        try {
            String __DSPOT_name_74970 = "1Va+ cv5A7ZT!FNRjb`+";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            meta.getGetterType(__DSPOT_name_74970);
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString296539_add303472_mg397155 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'1Va+ cv5A7ZT!FNRjb`+\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_mg296624_remove304837litBool392406() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_56100 = true;
        String __DSPOT_name_56099 = "wkL]g*3pR3H49>vKzt}b";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getSetterType("richField");
        meta.getSetterType("richProperty");
        meta.getSetterType("richList");
        meta.getSetterType("richMap");
        meta.getSetterType("richList[0]");
        meta.getSetterType("richType");
        meta.getSetterType("richType.richField");
        meta.getSetterType("richType.richList");
        meta.getSetterType("richType.richMap");
        meta.getSetterType("richType.richList[0]");
        meta.findProperty(__DSPOT_name_56099, __DSPOT_useCamelCaseMapping_56100);
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetternull296638_failAssert455_add303796_add394546() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getSetterType("richField");
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType(null);
            meta.getSetterType(null);
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetternull296638 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetternull296638_failAssert455_add303796null523574() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, null);
            meta.getSetterType("richField");
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType(null);
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetternull296638 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add296620_mg305182_mg395989() throws Exception {
        String __DSPOT_name_73674 = "rSHbCl.Yx<r)n`Dmb;6r";
        String __DSPOT_name_56175 = "mIDbV(JJ5:DsH$LN3+i ";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getSetterType("richField");
        meta.getSetterType("richProperty");
        meta.getSetterType("richList");
        meta.getSetterType("richMap");
        meta.getSetterType("richList[0]");
        meta.getSetterType("richType");
        meta.getSetterType("richType.richField");
        meta.getSetterType("richType.richProperty");
        meta.getSetterType("richType.richList");
        meta.getSetterType("richType.richList");
        meta.getSetterType("richType.richMap");
        meta.getSetterType("richType.richList[0]");
        boolean o_shouldCheckTypeForEachSetter_add296620_mg305182__18 = meta.hasGetter(__DSPOT_name_56175);
        boolean o_shouldCheckTypeForEachSetter_add296620_mg305182_mg395989__22 = meta.hasSetter(__DSPOT_name_73674);
        Assert.assertFalse(o_shouldCheckTypeForEachSetter_add296620_mg305182_mg395989__22);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString296539_add303472litString389894_failAssert565() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.rBchProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString296539_add303472litString389894 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'rBchProperty\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString296527_failAssert482_add304153litString390862() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getSetterType(":Mbps!2_[");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richFsield");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString296527 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg12_failAssert0() throws Exception {
        try {
            String __DSPOT_name_9 = "wu]&8(Dgh`l V!3a(!.#";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.metaClassForProperty(__DSPOT_name_9);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg12 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'wu]&8(Dgh`l V!3a(!.#\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add3() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getSetterNames();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg8_failAssert2() throws Exception {
        try {
            String __DSPOT_name_5 = "L`A=SO/woO!OKS@Rl&{h";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetInvoker(__DSPOT_name_5);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg8 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'L`A=SO/woO!OKS@Rl&{h\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg9_failAssert3() throws Exception {
        try {
            String __DSPOT_name_6 = "a!&Bcvg[?i!rb0/|]6^F";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType(__DSPOT_name_6);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg9 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'a!&Bcvg\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg6_failAssert1() throws Exception {
        try {
            String __DSPOT_name_3 = "|+mr6#-VtX(r!Fs2l>Ug";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetInvoker(__DSPOT_name_3);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg6 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'|+mr6#-VtX(r!Fs2l>Ug\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg5_remove177() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_2 = false;
        String __DSPOT_name_1 = "(q2 5[gpbL[{$QV5:Wz2";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg12_failAssert0_rv307() throws Exception {
        try {
            String __DSPOT_name_150 = "O0)nD+/OS38cC[Ga?A(>";
            String __DSPOT_name_9 = "wu]&8(Dgh`l V!3a(!.#";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            MetaClass __DSPOT_invoc_8 = meta.metaClassForProperty(__DSPOT_name_9);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg12 should have thrown ReflectionException");
            __DSPOT_invoc_8.findProperty(__DSPOT_name_150);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add3_mg221_failAssert22() throws Exception {
        try {
            String __DSPOT_name_56 = "ZQzekGw)WtZA0i9NP1<z";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterNames();
            meta.getSetterType(__DSPOT_name_56);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add3_mg221 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'ZQzekGw)WtZA0i9NP1<z\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg10_mg195() throws Exception {
        String __DSPOT_name_27 = "?56TtKz.F5M(E@of6;bU";
        String __DSPOT_name_7 = "T)-ef&bk*201yCi*Odwp";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterAndSetterNames_mg10__6 = meta.hasGetter(__DSPOT_name_7);
        boolean o_shouldCheckGetterAndSetterNames_mg10_mg195__10 = meta.hasGetter(__DSPOT_name_27);
        Assert.assertFalse(o_shouldCheckGetterAndSetterNames_mg10_mg195__10);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg6_failAssert1_add163() throws Exception {
        try {
            String __DSPOT_name_3 = "|+mr6#-VtX(r!Fs2l>Ug";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetInvoker(__DSPOT_name_3);
            meta.getGetInvoker(__DSPOT_name_3);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg6 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add2_mg243() throws Exception {
        String __DSPOT_name_80 = "H,Hzr;m#.W9*#qYoA($d";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterNames();
        meta.findProperty(__DSPOT_name_80);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add3_mg224_failAssert7() throws Exception {
        try {
            String __DSPOT_name_59 = "{_@e,R]r3_{}VLc{;bMa";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterNames();
            meta.metaClassForProperty(__DSPOT_name_59);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add3_mg224 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'{_@e,R]r3_{}VLc{;bMa\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg5_mg199() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_32 = true;
        String __DSPOT_name_31 = "&5+;N4Sb)kE+#PmjF|_k";
        boolean __DSPOT_useCamelCaseMapping_2 = false;
        String __DSPOT_name_1 = "(q2 5[gpbL[{$QV5:Wz2";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.findProperty(__DSPOT_name_1, __DSPOT_useCamelCaseMapping_2);
        meta.findProperty(__DSPOT_name_31, __DSPOT_useCamelCaseMapping_32);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg8_failAssert2litString99() throws Exception {
        try {
            String __DSPOT_name_5 = ",y4JV)d4}^w[&oDAIOw?";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getSetInvoker(__DSPOT_name_5);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg8 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg9_failAssert3litString107() throws Exception {
        try {
            String __DSPOT_name_6 = "%uE_&Ml%;sG#Ahw*&z*$";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getSetterType(__DSPOT_name_6);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg9 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNamesnull13_failAssert5_add175() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, null);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNamesnull13 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            expected.getMessage();
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg5_mg202_failAssert27() throws Exception {
        try {
            String __DSPOT_name_35 = "nJW,ftS}%g/mS6TE0=.J";
            boolean __DSPOT_useCamelCaseMapping_2 = false;
            String __DSPOT_name_1 = "(q2 5[gpbL[{$QV5:Wz2";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty(__DSPOT_name_1, __DSPOT_useCamelCaseMapping_2);
            meta.getSetInvoker(__DSPOT_name_35);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg5_mg202 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'nJW,ftS}%g/mS6TE0=.J\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add2_mg245_failAssert25() throws Exception {
        try {
            String __DSPOT_name_83 = ")=3&}%hbH%0kY^9)l,IK";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterNames();
            meta.getGetInvoker(__DSPOT_name_83);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add2_mg245 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \')=3&}%hbH%0kY^9)l,IK\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg5null437_failAssert18() throws Exception {
        try {
            boolean __DSPOT_useCamelCaseMapping_2 = false;
            String __DSPOT_name_1 = null;
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty(__DSPOT_name_1, __DSPOT_useCamelCaseMapping_2);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg5null437 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg10litString60_mg4696_failAssert51() throws Exception {
        try {
            String __DSPOT_name_1703 = "APxrfV&tp^&$#+>(G5:+";
            String __DSPOT_name_7 = "";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterAndSetterNames_mg10__6 = meta.hasGetter(__DSPOT_name_7);
            meta.metaClassForProperty(__DSPOT_name_1703);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg10litString60_mg4696 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'APxrfV&tp^&$#+>(G5:+\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add2_mg243_remove3283() throws Exception {
        String __DSPOT_name_80 = "H,Hzr;m#.W9*#qYoA($d";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.findProperty(__DSPOT_name_80);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg12_failAssert0_rv308litBool2309() throws Exception {
        try {
            boolean __DSPOT_useCamelCaseMapping_152 = false;
            String __DSPOT_name_151 = "iJy:v}SV5HR!Y({UBa;x";
            String __DSPOT_name_9 = "wu]&8(Dgh`l V!3a(!.#";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            MetaClass __DSPOT_invoc_8 = meta.metaClassForProperty(__DSPOT_name_9);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg12 should have thrown ReflectionException");
            __DSPOT_invoc_8.findProperty(__DSPOT_name_151, __DSPOT_useCamelCaseMapping_152);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg5_remove177litBool2303() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_2 = true;
        String __DSPOT_name_1 = "(q2 5[gpbL[{$QV5:Wz2";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg5null437_failAssert18_mg5146() throws Exception {
        try {
            String __DSPOT_name_2203 = "sdOHJ8RZ#j4br1mmMsq<";
            boolean __DSPOT_useCamelCaseMapping_2 = false;
            String __DSPOT_name_1 = null;
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.findProperty(__DSPOT_name_1, __DSPOT_useCamelCaseMapping_2);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg5null437 should have thrown NullPointerException");
            meta.metaClassForProperty(__DSPOT_name_2203);
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg8_failAssert2null454_failAssert33_add3248() throws Exception {
        try {
            try {
                String __DSPOT_name_5 = "L`A=SO/woO!OKS@Rl&{h";
                ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
                Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
                MetaClass meta = MetaClass.forClass(RichType.class, null);
                meta.getSetInvoker(__DSPOT_name_5);
                meta.getSetInvoker(__DSPOT_name_5);
                org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg8 should have thrown ReflectionException");
            } catch (ReflectionException expected) {
            }
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg8_failAssert2null454 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg11litString78_mg3694_failAssert45() throws Exception {
        try {
            String __DSPOT_name_590 = " <?$.>[U!g6(Ir]hkK]R";
            String __DSPOT_name_8 = ":";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterAndSetterNames_mg11__6 = meta.hasSetter(__DSPOT_name_8);
            meta.getSetterType(__DSPOT_name_590);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg11litString78_mg3694 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \' <?$\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg6_failAssert1litString93_mg4770() throws Exception {
        try {
            boolean __DSPOT_useCamelCaseMapping_1786 = true;
            String __DSPOT_name_1785 = "-@^SC wj[laX)Rfs=(Z ";
            String __DSPOT_name_3 = "\n";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getGetInvoker(__DSPOT_name_3);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg6 should have thrown ReflectionException");
            meta.findProperty(__DSPOT_name_1785, __DSPOT_useCamelCaseMapping_1786);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg10litString59_mg3718_failAssert47() throws Exception {
        try {
            String __DSPOT_name_617 = "272,Cl6JEg{EH$YvU}*C";
            String __DSPOT_name_7 = "p[$XdYQ7-#sa<}t>?]?7";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterAndSetterNames_mg10__6 = meta.hasGetter(__DSPOT_name_7);
            meta.getGetInvoker(__DSPOT_name_617);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg10litString59_mg3718 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'272,Cl6JEg{EH$YvU}*C\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg5litBool119litString1258() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_2 = true;
        String __DSPOT_name_1 = "(q2 5gpbL[{$QV5:Wz2";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.findProperty(__DSPOT_name_1, __DSPOT_useCamelCaseMapping_2);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg10litString61_mg4980_failAssert49() throws Exception {
        try {
            String __DSPOT_name_2019 = "kx)]$|iC`/{}}(4bz; 8";
            String __DSPOT_name_7 = "\n";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterAndSetterNames_mg10__6 = meta.hasGetter(__DSPOT_name_7);
            meta.getSetInvoker(__DSPOT_name_2019);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg10litString61_mg4980 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'kx)]$|iC`/{}}(4bz; 8\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg9_failAssert3_mg279_add2559() throws Exception {
        try {
            String __DSPOT_name_120 = "I4De0drHp,* B!1!0nSv";
            String __DSPOT_name_6 = "a!&Bcvg[?i!rb0/|]6^F";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getSetterType(__DSPOT_name_6);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg9 should have thrown ReflectionException");
            meta.findProperty(__DSPOT_name_120);
            meta.findProperty(__DSPOT_name_120);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg8_failAssert2litString101_mg4633() throws Exception {
        try {
            String __DSPOT_name_1633 = "+T,&]oL_*u[C[K8+}gnq";
            String __DSPOT_name_5 = "\n";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getSetInvoker(__DSPOT_name_5);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg8 should have thrown ReflectionException");
            meta.metaClassForProperty(__DSPOT_name_1633);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524595_failAssert579() throws Exception {
        try {
            String __DSPOT_name_102326 = "R{b6GNz6_kX9%^&v:7J?";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            meta.getGetInvoker(__DSPOT_name_102326);
            org.junit.Assert.fail("shouldFindPropertyName_mg524595 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'R{b6GNz6_kX9%^&v:7J?\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524598_failAssert577() throws Exception {
        try {
            String __DSPOT_name_102329 = "=]HzO@O)IL]/O-MCZ%8l";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            meta.getSetterType(__DSPOT_name_102329);
            org.junit.Assert.fail("shouldFindPropertyName_mg524598 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'=]HzO@O)IL]/O-MCZ%8l\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524589() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.findProperty("\n");
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524594() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_102325 = false;
        String __DSPOT_name_102324 = "!Q9&.#qb;9`wq0pO`0LU";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg524594__7 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg524594__7);
        meta.findProperty(__DSPOT_name_102324, __DSPOT_useCamelCaseMapping_102325);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524597_failAssert580() throws Exception {
        try {
            String __DSPOT_name_102328 = "ad%Sl!L#Xn;,^O14bV3f";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            meta.getSetInvoker(__DSPOT_name_102328);
            org.junit.Assert.fail("shouldFindPropertyName_mg524597 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'ad%Sl!L#Xn;,^O14bV3f\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524601_failAssert578() throws Exception {
        try {
            String __DSPOT_name_102332 = "WuXl|o`nZ^MK[VX{SoLc";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            meta.metaClassForProperty(__DSPOT_name_102332);
            org.junit.Assert.fail("shouldFindPropertyName_mg524601 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'WuXl|o`nZ^MK[VX{SoLc\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524583() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyNamelitString524583__5 = meta.findProperty("richType.richList[0]");
        Assert.assertEquals("richType.", o_shouldFindPropertyNamelitString524583__5);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524596_failAssert581() throws Exception {
        try {
            String __DSPOT_name_102327 = "[^s^;x]N(&MG]EJ@xoZ=";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            meta.getGetterType(__DSPOT_name_102327);
            org.junit.Assert.fail("shouldFindPropertyName_mg524596 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524600() throws Exception {
        String __DSPOT_name_102331 = "{q9o:#<[xp$wNL7U[._H";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg524600__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg524600__6);
        boolean o_shouldFindPropertyName_mg524600__7 = meta.hasSetter(__DSPOT_name_102331);
        Assert.assertFalse(o_shouldFindPropertyName_mg524600__7);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg524600__6);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add524592_mg525003_failAssert584() throws Exception {
        try {
            String __DSPOT_name_102397 = ".FE6q6_lEgkaQ`cH5aGx";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_add524592__5 = meta.findProperty("RICHfield");
            String o_shouldFindPropertyName_add524592__6 = meta.findProperty("RICHfield");
            meta.getGetterType(__DSPOT_name_102397);
            org.junit.Assert.fail("shouldFindPropertyName_add524592_mg525003 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524588_remove524939() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524594_add524859() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_102325 = false;
        String __DSPOT_name_102324 = "!Q9&.#qb;9`wq0pO`0LU";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg524594_add524859__7 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg524594_add524859__7);
        String o_shouldFindPropertyName_mg524594__7 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg524594__7);
        meta.findProperty(__DSPOT_name_102324, __DSPOT_useCamelCaseMapping_102325);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524587_mg525055() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_102455 = true;
        String __DSPOT_name_102454 = "EB`!Uh6IT-p-zW2^{1D&";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.findProperty("X]A!TS7E9");
        meta.findProperty(__DSPOT_name_102454, __DSPOT_useCamelCaseMapping_102455);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524583_add524890() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldFindPropertyNamelitString524583_add524890__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyNamelitString524583_add524890__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyNamelitString524583__5 = meta.findProperty("richType.richList[0]");
        Assert.assertEquals("richType.", o_shouldFindPropertyNamelitString524583__5);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyNamelitString524583_add524890__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524593_mg524968_failAssert627() throws Exception {
        try {
            String __DSPOT_name_102358 = "=:#!z[2Hy]&vfw^{P4OH";
            String __DSPOT_name_102323 = "|![P:+NspJLSg(0>1L<U";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg524593__6 = meta.findProperty("RICHfield");
            meta.findProperty(__DSPOT_name_102323);
            meta.getSetInvoker(__DSPOT_name_102358);
            org.junit.Assert.fail("shouldFindPropertyName_mg524593_mg524968 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'=:#!z[2Hy]&vfw^{P4OH\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524597_failAssert580_add524924() throws Exception {
        try {
            String __DSPOT_name_102328 = "ad%Sl!L#Xn;,^O14bV3f";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            String o_shouldFindPropertyName_mg524597_failAssert580_add524924__8 = meta.findProperty("RICHfield");
            Assert.assertEquals("richField", o_shouldFindPropertyName_mg524597_failAssert580_add524924__8);
            String o_shouldFindPropertyName_mg524597_failAssert580_add524924__9 = meta.findProperty("RICHfield");
            Assert.assertEquals("richField", o_shouldFindPropertyName_mg524597_failAssert580_add524924__9);
            meta.getSetInvoker(__DSPOT_name_102328);
            org.junit.Assert.fail("shouldFindPropertyName_mg524597 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524588_mg525014_failAssert585() throws Exception {
        try {
            String __DSPOT_name_102409 = "D]K3R)U-Sc >#7_8F&Kp";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("");
            meta.getSetterType(__DSPOT_name_102409);
            org.junit.Assert.fail("shouldFindPropertyNamelitString524588_mg525014 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'D]K3R)U-Sc >#7_8F&Kp\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524587_mg525056_failAssert603() throws Exception {
        try {
            String __DSPOT_name_102456 = "MRh.mz_6s)D_GiQX6.YQ";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("X]A!TS7E9");
            meta.getGetInvoker(__DSPOT_name_102456);
            org.junit.Assert.fail("shouldFindPropertyNamelitString524587_mg525056 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'MRh.mz_6s)D_GiQX6.YQ\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524594null525281_failAssert620() throws Exception {
        try {
            boolean __DSPOT_useCamelCaseMapping_102325 = false;
            String __DSPOT_name_102324 = null;
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg524594__7 = meta.findProperty("RICHfield");
            meta.findProperty(__DSPOT_name_102324, __DSPOT_useCamelCaseMapping_102325);
            org.junit.Assert.fail("shouldFindPropertyName_mg524594null525281 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524600litString524690() throws Exception {
        String __DSPOT_name_102331 = "{q9o:#<[xp$wNL7U[._H";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg524600__6 = meta.findProperty("\n");
        boolean o_shouldFindPropertyName_mg524600__7 = meta.hasSetter(__DSPOT_name_102331);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524601_failAssert578_add524915() throws Exception {
        try {
            String __DSPOT_name_102332 = "WuXl|o`nZ^MK[VX{SoLc";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass o_shouldFindPropertyName_mg524601_failAssert578_add524915__6 = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_mg524601_failAssert578_add524915__6)).hasDefaultConstructor());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            String o_shouldFindPropertyName_mg524601_failAssert578_add524915__9 = meta.findProperty("RICHfield");
            Assert.assertEquals("richField", o_shouldFindPropertyName_mg524601_failAssert578_add524915__9);
            meta.metaClassForProperty(__DSPOT_name_102332);
            org.junit.Assert.fail("shouldFindPropertyName_mg524601 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamenull524602_failAssert583null525312() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, null);
            meta.findProperty(null);
            org.junit.Assert.fail("shouldFindPropertyNamenull524602 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524585_mg525066_failAssert604() throws Exception {
        try {
            String __DSPOT_name_102467 = "U`sY%!SNC:p}jOyUS!OL";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfie/ld");
            meta.getGetterType(__DSPOT_name_102467);
            org.junit.Assert.fail("shouldFindPropertyNamelitString524585_mg525066 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'U`sY%!SNC:p}jOyUS!OL\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524589_add524895_mg530155_failAssert629() throws Exception {
        try {
            String __DSPOT_name_103912 = "h^YM[&6>0[I!Z)>[ 1QS";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            ((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("\n");
            meta.getSetInvoker(__DSPOT_name_103912);
            org.junit.Assert.fail("shouldFindPropertyNamelitString524589_add524895_mg530155 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'h^YM[&6>0[I!Z)>[ 1QS\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524600_mg524961_mg529091_failAssert630() throws Exception {
        try {
            String __DSPOT_name_102730 = "r5#XTv9FihR?B>hkG1&|";
            String __DSPOT_name_102350 = "HPVv(^|@@Z}zhVxP{!l}";
            String __DSPOT_name_102331 = "{q9o:#<[xp$wNL7U[._H";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg524600__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg524600__7 = meta.hasSetter(__DSPOT_name_102331);
            boolean o_shouldFindPropertyName_mg524600_mg524961__13 = meta.hasGetter(__DSPOT_name_102350);
            meta.getGetInvoker(__DSPOT_name_102730);
            org.junit.Assert.fail("shouldFindPropertyName_mg524600_mg524961_mg529091 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'r5#XTv9FihR?B>hkG1&|\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524598_failAssert577litString524760_rv531559() throws Exception {
        try {
            String __DSPOT_arg0_105004 = "/+JA1w8RsD3By!|QpU/w";
            String __DSPOT_name_102329 = "=]HzO@O)IL]/O-MCZ%8l";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.findProperty("%ci*!a9>!");
            Class<?> __DSPOT_invoc_15 = meta.getSetterType(__DSPOT_name_102329);
            org.junit.Assert.fail("shouldFindPropertyName_mg524598 should have thrown ReflectionException");
            __DSPOT_invoc_15.getResource(__DSPOT_arg0_105004);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524583_mg525037_add527891() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_102435 = false;
        String __DSPOT_name_102434 = "?,=#iz[b,]dfW5j){JS.";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyNamelitString524583__5 = meta.findProperty("richType.richList[0]");
        Assert.assertEquals("richType.", o_shouldFindPropertyNamelitString524583__5);
        meta.findProperty(__DSPOT_name_102434, __DSPOT_useCamelCaseMapping_102435);
        meta.findProperty(__DSPOT_name_102434, __DSPOT_useCamelCaseMapping_102435);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524598_failAssert577null525292_failAssert615litString527702() throws Exception {
        try {
            try {
                String __DSPOT_name_102329 = "=]HzP@O)IL]/O-MCZ%8l";
                ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
                Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
                MetaClass meta = MetaClass.forClass(RichType.class, null);
                meta.findProperty("RICHfield");
                meta.getSetterType(__DSPOT_name_102329);
                org.junit.Assert.fail("shouldFindPropertyName_mg524598 should have thrown ReflectionException");
            } catch (ReflectionException expected) {
            }
            org.junit.Assert.fail("shouldFindPropertyName_mg524598_failAssert577null525292 should have thrown NullPointerException");
        } catch (NullPointerException expected_1) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524588_remove524939_mg529146_failAssert628() throws Exception {
        try {
            String __DSPOT_name_102791 = "M{U`j(Fc>,]xZBu,8SUJ";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType(__DSPOT_name_102791);
            org.junit.Assert.fail("shouldFindPropertyNamelitString524588_remove524939_mg529146 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'M{U`j(Fc>,]xZBu,8SUJ\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524600_mg524961_add527907() throws Exception {
        String __DSPOT_name_102350 = "HPVv(^|@@Z}zhVxP{!l}";
        String __DSPOT_name_102331 = "{q9o:#<[xp$wNL7U[._H";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg524600__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg524600__6);
        boolean o_shouldFindPropertyName_mg524600_mg524961_add527907__10 = meta.hasSetter(__DSPOT_name_102331);
        Assert.assertFalse(o_shouldFindPropertyName_mg524600_mg524961_add527907__10);
        boolean o_shouldFindPropertyName_mg524600__7 = meta.hasSetter(__DSPOT_name_102331);
        boolean o_shouldFindPropertyName_mg524600_mg524961__13 = meta.hasGetter(__DSPOT_name_102350);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg524600__6);
        Assert.assertFalse(o_shouldFindPropertyName_mg524600_mg524961_add527907__10);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524588_remove524939_mg529143() throws Exception {
        String __DSPOT_name_102787 = "pT}4=lWSZbh`S2.&ng(w";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.findProperty(__DSPOT_name_102787);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524588_add524877_mg529355_failAssert646() throws Exception {
        try {
            String __DSPOT_name_103023 = "?xK.$cOa9M&Yw}b);,&8";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            ((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("");
            meta.getSetterType(__DSPOT_name_103023);
            org.junit.Assert.fail("shouldFindPropertyNamelitString524588_add524877_mg529355 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'?xK\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524589_add524896_mg529329() throws Exception {
        String __DSPOT_name_102994 = "F8^PjM1h.7Zt_6)25Y&Y";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldFindPropertyNamelitString524589_add524896__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyNamelitString524589_add524896__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.findProperty("\n");
        o_shouldFindPropertyNamelitString524589_add524896__3.hasGetter(__DSPOT_name_102994);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524588_add524877_remove528915() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        ((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled();
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524586_mg525073litBool527821() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_102475 = true;
        String __DSPOT_name_102474 = "A>5ZS;yiX{<90U]h E>S";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.findProperty("RIHfield");
        meta.findProperty(__DSPOT_name_102474, __DSPOT_useCamelCaseMapping_102475);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524583_mg525037null533425_failAssert639() throws Exception {
        try {
            boolean __DSPOT_useCamelCaseMapping_102435 = false;
            String __DSPOT_name_102434 = "?,=#iz[b,]dfW5j){JS.";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyNamelitString524583__5 = meta.findProperty("richType.richList[0]");
            meta.findProperty(null, __DSPOT_useCamelCaseMapping_102435);
            org.junit.Assert.fail("shouldFindPropertyNamelitString524583_mg525037null533425 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString524590_mg525029_failAssert606_mg530803() throws Exception {
        try {
            String __DSPOT_name_104632 = "+5&,sjXZj0k a42%K&=@";
            String __DSPOT_name_102426 = "+ZEX2k*=tkw&+#L^d(S@";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.findProperty(":");
            meta.getGetInvoker(__DSPOT_name_102426);
            org.junit.Assert.fail("shouldFindPropertyNamelitString524590_mg525029 should have thrown ReflectionException");
            meta.getSetInvoker(__DSPOT_name_104632);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg524600_mg524961null533433_failAssert638() throws Exception {
        try {
            String __DSPOT_name_102350 = null;
            String __DSPOT_name_102331 = "{q9o:#<[xp$wNL7U[._H";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg524600__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg524600__7 = meta.hasSetter(__DSPOT_name_102331);
            boolean o_shouldFindPropertyName_mg524600_mg524961__13 = meta.hasGetter(__DSPOT_name_102350);
            org.junit.Assert.fail("shouldFindPropertyName_mg524600_mg524961null533433 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }
}

