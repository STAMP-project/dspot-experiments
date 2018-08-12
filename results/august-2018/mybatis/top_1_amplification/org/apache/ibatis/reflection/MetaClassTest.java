package org.apache.ibatis.reflection;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.domain.misc.RichType;
import org.apache.ibatis.domain.misc.generics.GenericConcrete;
import org.junit.Assert;
import org.junit.Test;


public class MetaClassTest {
    private RichType rich = new RichType();

    Map<String, RichType> map = new HashMap<String, RichType>() {
        {
            put("richType", rich);
        }
    };

    public MetaClassTest() {
        rich.setRichType(new RichType());
    }

    @Test
    public void shouldTestDataTypeOfGenericMethod() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertEquals(Long.class, meta.getGetterType("id"));
        Assert.assertEquals(Long.class, meta.getSetterType("id"));
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString611046_failAssert655() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("richType.richProperty");
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString611046 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'richType\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString611052_failAssert661() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType(",");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString611052 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \',\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611069() throws Exception {
        String __DSPOT_name_105093 = "CAuwmQ3Q-.[]]rJUePSY";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_mg611069__8 = meta.hasSetter(__DSPOT_name_105093);
        Assert.assertFalse(o_shouldTestDataTypeOfGenericMethod_mg611069__8);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611068() throws Exception {
        String __DSPOT_name_105092 = "KZ5wr>b)Ns)o#,L./zM/";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_mg611068__8 = meta.hasGetter(__DSPOT_name_105092);
        Assert.assertFalse(o_shouldTestDataTypeOfGenericMethod_mg611068__8);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611061_failAssert665() throws Exception {
        try {
            String __DSPOT_name_105088 = "2P.=,{(LD(|h]D3|uN&k";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            meta.getGetInvoker(__DSPOT_name_105088);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611061 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'2P.=,{(LD(|h]D3|uN&k\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodnull611082() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getSetterType("id");
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString611047_failAssert656() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("/");
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString611047 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'/\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611060() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_105087 = true;
        String __DSPOT_name_105086 = "PFfy[f19rR3|C)Nfr]*:";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getSetterType("id");
        meta.findProperty(__DSPOT_name_105086, __DSPOT_useCamelCaseMapping_105087);
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString611051_failAssert660() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString611051 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'richType\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611064_failAssert667() throws Exception {
        try {
            String __DSPOT_name_105090 = "MkJ1L.%=;?iXZZ%gn@x(";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            meta.getSetInvoker(__DSPOT_name_105090);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611064 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'MkJ1L.%=;?iXZZ%gn@x(\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611068_rv616701() throws Exception {
        Object __DSPOT_arg0_106328 = new Object();
        String __DSPOT_name_105092 = "KZ5wr>b)Ns)o#,L./zM/";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        Class<?> __DSPOT_invoc_13 = meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_mg611068__8 = meta.hasGetter(__DSPOT_name_105092);
        boolean o_shouldTestDataTypeOfGenericMethod_mg611068_rv616701__15 = __DSPOT_invoc_13.isInstance(__DSPOT_arg0_106328);
        Assert.assertFalse(o_shouldTestDataTypeOfGenericMethod_mg611068_rv616701__15);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611069_mg612317_failAssert707() throws Exception {
        try {
            String __DSPOT_name_105359 = "IlkAWB{1d^W`pdcUSgO=";
            String __DSPOT_name_105093 = "CAuwmQ3Q-.[]]rJUePSY";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg611069__8 = meta.hasSetter(__DSPOT_name_105093);
            meta.getGetterType(__DSPOT_name_105359);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611069_mg612317 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'IlkAWB{1d^W`pdcUSgO=\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611068_rv616611_failAssert703() throws Exception {
        try {
            Object __DSPOT_arg0_106312 = new Object();
            String __DSPOT_name_105092 = "KZ5wr>b)Ns)o#,L./zM/";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            Class<?> __DSPOT_invoc_13 = meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg611068__8 = meta.hasGetter(__DSPOT_name_105092);
            __DSPOT_invoc_13.cast(__DSPOT_arg0_106312);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611068_rv616611 should have thrown ClassCastException");
        } catch (ClassCastException expected) {
            Assert.assertEquals("Cannot cast java.lang.Object to java.lang.Long", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611069_rv612353_failAssert697() throws Exception {
        try {
            String __DSPOT_arg0_105371 = "ht5QI2H5zbw-W5<z=MT]";
            String __DSPOT_name_105093 = "CAuwmQ3Q-.[]]rJUePSY";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Class<?> __DSPOT_invoc_12 = meta.getGetterType("id");
            meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg611069__8 = meta.hasSetter(__DSPOT_name_105093);
            __DSPOT_invoc_12.getField(__DSPOT_arg0_105371);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611069_rv612353 should have thrown NoSuchFieldException");
        } catch (NoSuchFieldException expected) {
            Assert.assertEquals("ht5QI2H5zbw-W5<z=MT]", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611068_mg616401() throws Exception {
        String __DSPOT_name_106267 = "a_dBF.)L&-JscRA*B(RL";
        String __DSPOT_name_105092 = "KZ5wr>b)Ns)o#,L./zM/";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_mg611068__8 = meta.hasGetter(__DSPOT_name_105092);
        meta.findProperty(__DSPOT_name_106267);
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodnull611072_failAssert671_rv611405() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = null;
            Class<?> __DSPOT_invoc_6 = meta.getGetterType("id");
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodnull611072 should have thrown NullPointerException");
            __DSPOT_invoc_6.getSuperclass();
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611069_mg612320_failAssert708() throws Exception {
        try {
            String __DSPOT_name_105361 = "wEbf!u1{!%(%ToU-1vz!";
            String __DSPOT_name_105093 = "CAuwmQ3Q-.[]]rJUePSY";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg611069__8 = meta.hasSetter(__DSPOT_name_105093);
            meta.getSetterType(__DSPOT_name_105361);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611069_mg612320 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'wEbf!u1{!%(%ToU-1vz!\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611069_mg612315_failAssert709() throws Exception {
        try {
            String __DSPOT_name_105358 = "Go&)Z? !V_U2#]wx 1}Y";
            String __DSPOT_name_105093 = "CAuwmQ3Q-.[]]rJUePSY";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg611069__8 = meta.hasSetter(__DSPOT_name_105093);
            meta.getGetInvoker(__DSPOT_name_105358);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611069_mg612315 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'Go&)Z? !V_U2#]wx 1}Y\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611068_mg616427() throws Exception {
        String __DSPOT_name_106277 = "EYD2/ Mm%Ef.h%cMT]D*";
        String __DSPOT_name_105092 = "KZ5wr>b)Ns)o#,L./zM/";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_mg611068__8 = meta.hasGetter(__DSPOT_name_105092);
        boolean o_shouldTestDataTypeOfGenericMethod_mg611068_mg616427__12 = meta.hasGetter(__DSPOT_name_106277);
        Assert.assertFalse(o_shouldTestDataTypeOfGenericMethod_mg611068_mg616427__12);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611069_mg612318_failAssert706() throws Exception {
        try {
            String __DSPOT_name_105360 = "7kE^Yja=ygqUe6CvAmVu";
            String __DSPOT_name_105093 = "CAuwmQ3Q-.[]]rJUePSY";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg611069__8 = meta.hasSetter(__DSPOT_name_105093);
            meta.getSetInvoker(__DSPOT_name_105360);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611069_mg612318 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'7kE^Yja=ygqUe6CvAmVu\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611068_rv616587_mg635019_failAssert796() throws Exception {
        try {
            String __DSPOT_name_110340 = "b*M;S-OyN0C ]QHPL._*";
            Object __DSPOT_arg0_106307 = new Object();
            String __DSPOT_name_105092 = "KZ5wr>b)Ns)o#,L./zM/";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Class<?> __DSPOT_invoc_12 = meta.getGetterType("id");
            meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg611068__8 = meta.hasGetter(__DSPOT_name_105092);
            boolean o_shouldTestDataTypeOfGenericMethod_mg611068_rv616587__15 = __DSPOT_invoc_12.isInstance(__DSPOT_arg0_106307);
            meta.getGetterType(__DSPOT_name_110340);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611068_rv616587_mg635019 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'b*M;S-OyN0C ]QHPL\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611069_rv612417_mg633295() throws Exception {
        String __DSPOT_name_109940 = "I`.ajrQ!x=&-b.J%!!$R";
        Object __DSPOT_arg0_105397 = new Object();
        String __DSPOT_name_105093 = "CAuwmQ3Q-.[]]rJUePSY";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Class<?> __DSPOT_invoc_12 = meta.getGetterType("id");
        meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_mg611069__8 = meta.hasSetter(__DSPOT_name_105093);
        boolean o_shouldTestDataTypeOfGenericMethod_mg611069_rv612417__15 = __DSPOT_invoc_12.isInstance(__DSPOT_arg0_105397);
        boolean o_shouldTestDataTypeOfGenericMethod_mg611069_rv612417_mg633295__19 = meta.hasSetter(__DSPOT_name_109940);
        Assert.assertFalse(o_shouldTestDataTypeOfGenericMethod_mg611069_rv612417_mg633295__19);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611069_mg612323_rv632258_failAssert762() throws Exception {
        try {
            Class<?>[] __DSPOT_arg0_109705 = new Class<?>[]{  };
            String __DSPOT_name_105363 = "EH|!wr!2#+E2P3;X f&}";
            String __DSPOT_name_105093 = "CAuwmQ3Q-.[]]rJUePSY";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            Class<?> __DSPOT_invoc_14 = meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg611069__8 = meta.hasSetter(__DSPOT_name_105093);
            boolean o_shouldTestDataTypeOfGenericMethod_mg611069_mg612323__12 = meta.hasSetter(__DSPOT_name_105363);
            __DSPOT_invoc_14.getDeclaredConstructor(__DSPOT_arg0_109705);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611069_mg612323_rv632258 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
            Assert.assertEquals("java.lang.Long.<init>()", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611069_rv612417_mg633294() throws Exception {
        String __DSPOT_name_109939 = "!?1I KQppPfR(H*$r_OI";
        Object __DSPOT_arg0_105397 = new Object();
        String __DSPOT_name_105093 = "CAuwmQ3Q-.[]]rJUePSY";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Class<?> __DSPOT_invoc_12 = meta.getGetterType("id");
        meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_mg611069__8 = meta.hasSetter(__DSPOT_name_105093);
        boolean o_shouldTestDataTypeOfGenericMethod_mg611069_rv612417__15 = __DSPOT_invoc_12.isInstance(__DSPOT_arg0_105397);
        boolean o_shouldTestDataTypeOfGenericMethod_mg611069_rv612417_mg633294__19 = meta.hasGetter(__DSPOT_name_109939);
        Assert.assertFalse(o_shouldTestDataTypeOfGenericMethod_mg611069_rv612417_mg633294__19);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611069_rv612417_mg633292_failAssert787() throws Exception {
        try {
            String __DSPOT_name_109938 = "-!e&CV[DUQ]>+JsF5Y&$";
            Object __DSPOT_arg0_105397 = new Object();
            String __DSPOT_name_105093 = "CAuwmQ3Q-.[]]rJUePSY";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Class<?> __DSPOT_invoc_12 = meta.getGetterType("id");
            meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg611069__8 = meta.hasSetter(__DSPOT_name_105093);
            boolean o_shouldTestDataTypeOfGenericMethod_mg611069_rv612417__15 = __DSPOT_invoc_12.isInstance(__DSPOT_arg0_105397);
            meta.getSetterType(__DSPOT_name_109938);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611069_rv612417_mg633292 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'-!e&CV\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611069_rv612417_mg633290_failAssert780() throws Exception {
        try {
            String __DSPOT_name_109937 = "b8A8U&sOsbge{#PfE)q2";
            Object __DSPOT_arg0_105397 = new Object();
            String __DSPOT_name_105093 = "CAuwmQ3Q-.[]]rJUePSY";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Class<?> __DSPOT_invoc_12 = meta.getGetterType("id");
            meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg611069__8 = meta.hasSetter(__DSPOT_name_105093);
            boolean o_shouldTestDataTypeOfGenericMethod_mg611069_rv612417__15 = __DSPOT_invoc_12.isInstance(__DSPOT_arg0_105397);
            meta.getSetInvoker(__DSPOT_name_109937);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611069_rv612417_mg633290 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'b8A8U&sOsbge{#PfE)q2\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611068_rv616701_mg635586_failAssert790() throws Exception {
        try {
            String __DSPOT_name_110453 = "QeYxzN#7z5u!8-i c6Mo";
            Object __DSPOT_arg0_106328 = new Object();
            String __DSPOT_name_105092 = "KZ5wr>b)Ns)o#,L./zM/";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            Class<?> __DSPOT_invoc_13 = meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg611068__8 = meta.hasGetter(__DSPOT_name_105092);
            boolean o_shouldTestDataTypeOfGenericMethod_mg611068_rv616701__15 = __DSPOT_invoc_13.isInstance(__DSPOT_arg0_106328);
            meta.getGetterType(__DSPOT_name_110453);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611068_rv616701_mg635586 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'QeYxzN#7z5u!8-i c6Mo\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611068_rv616587_rv635144_failAssert718() throws Exception {
        try {
            Class<?>[] __DSPOT_arg1_110364 = new Class<?>[]{  };
            String __DSPOT_arg0_110363 = "N9$ZY Ip|[(JBn!i#,#:";
            Object __DSPOT_arg0_106307 = new Object();
            String __DSPOT_name_105092 = "KZ5wr>b)Ns)o#,L./zM/";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Class<?> __DSPOT_invoc_12 = meta.getGetterType("id");
            Class<?> __DSPOT_invoc_17 = meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg611068__8 = meta.hasGetter(__DSPOT_name_105092);
            boolean o_shouldTestDataTypeOfGenericMethod_mg611068_rv616587__15 = __DSPOT_invoc_12.isInstance(__DSPOT_arg0_106307);
            __DSPOT_invoc_17.getDeclaredMethod(__DSPOT_arg0_110363, __DSPOT_arg1_110364);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611068_rv616587_rv635144 should have thrown NoSuchMethodException");
        } catch (NoSuchMethodException expected) {
            Assert.assertEquals("java.lang.Long.N9$ZY Ip|[(JBn!i#,#:()", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611068_rv616701_mg635593_failAssert795() throws Exception {
        try {
            String __DSPOT_name_110455 = "%h8.JN(8LxKES!a#+J}y";
            Object __DSPOT_arg0_106328 = new Object();
            String __DSPOT_name_105092 = "KZ5wr>b)Ns)o#,L./zM/";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            Class<?> __DSPOT_invoc_13 = meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg611068__8 = meta.hasGetter(__DSPOT_name_105092);
            boolean o_shouldTestDataTypeOfGenericMethod_mg611068_rv616701__15 = __DSPOT_invoc_13.isInstance(__DSPOT_arg0_106328);
            meta.getSetterType(__DSPOT_name_110455);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611068_rv616701_mg635593 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'%h8\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611069_rv612417_mg633287_failAssert784() throws Exception {
        try {
            String __DSPOT_name_109935 = ">.=o+d#}Typ$770X{,}7";
            Object __DSPOT_arg0_105397 = new Object();
            String __DSPOT_name_105093 = "CAuwmQ3Q-.[]]rJUePSY";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            Class<?> __DSPOT_invoc_12 = meta.getGetterType("id");
            meta.getSetterType("id");
            boolean o_shouldTestDataTypeOfGenericMethod_mg611069__8 = meta.hasSetter(__DSPOT_name_105093);
            boolean o_shouldTestDataTypeOfGenericMethod_mg611069_rv612417__15 = __DSPOT_invoc_12.isInstance(__DSPOT_arg0_105397);
            meta.getGetInvoker(__DSPOT_name_109935);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg611069_rv612417_mg633287 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'>.=o+d#}Typ$770X{,}7\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611068_rv616701_mg635598() throws Exception {
        String __DSPOT_name_110456 = "(cF0NhSeA>J_!4;={./U";
        Object __DSPOT_arg0_106328 = new Object();
        String __DSPOT_name_105092 = "KZ5wr>b)Ns)o#,L./zM/";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        Class<?> __DSPOT_invoc_13 = meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_mg611068__8 = meta.hasGetter(__DSPOT_name_105092);
        boolean o_shouldTestDataTypeOfGenericMethod_mg611068_rv616701__15 = __DSPOT_invoc_13.isInstance(__DSPOT_arg0_106328);
        boolean o_shouldTestDataTypeOfGenericMethod_mg611068_rv616701_mg635598__19 = meta.hasGetter(__DSPOT_name_110456);
        Assert.assertFalse(o_shouldTestDataTypeOfGenericMethod_mg611068_rv616701_mg635598__19);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611069_rv612690_mg633829() throws Exception {
        String __DSPOT_name_110057 = "&g9+.L?igFkLl_+N!9sX";
        Object __DSPOT_arg0_105466 = new Object();
        String __DSPOT_name_105093 = "CAuwmQ3Q-.[]]rJUePSY";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        Class<?> __DSPOT_invoc_13 = meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_mg611069__8 = meta.hasSetter(__DSPOT_name_105093);
        boolean o_shouldTestDataTypeOfGenericMethod_mg611069_rv612690__15 = __DSPOT_invoc_13.isInstance(__DSPOT_arg0_105466);
        meta.findProperty(__DSPOT_name_110057);
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg611068_rv616587_mg635007() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_110336 = true;
        String __DSPOT_name_110333 = "=%SMX{KC]mx` b*$.!^u";
        Object __DSPOT_arg0_106307 = new Object();
        String __DSPOT_name_105092 = "KZ5wr>b)Ns)o#,L./zM/";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Class<?> __DSPOT_invoc_12 = meta.getGetterType("id");
        meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_mg611068__8 = meta.hasGetter(__DSPOT_name_105092);
        boolean o_shouldTestDataTypeOfGenericMethod_mg611068_rv616587__15 = __DSPOT_invoc_12.isInstance(__DSPOT_arg0_106307);
        meta.findProperty(__DSPOT_name_110333, __DSPOT_useCamelCaseMapping_110336);
    }

    @Test
    public void shouldCheckGetterExistance() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(meta.hasGetter("richField"));
        Assert.assertTrue(meta.hasGetter("richProperty"));
        Assert.assertTrue(meta.hasGetter("richList"));
        Assert.assertTrue(meta.hasGetter("richMap"));
        Assert.assertTrue(meta.hasGetter("richList[0]"));
        Assert.assertTrue(meta.hasGetter("richType"));
        Assert.assertTrue(meta.hasGetter("richType.richField"));
        Assert.assertTrue(meta.hasGetter("richType.richProperty"));
        Assert.assertTrue(meta.hasGetter("richType.richList"));
        Assert.assertTrue(meta.hasGetter("richType.richMap"));
        Assert.assertTrue(meta.hasGetter("richType.richList[0]"));
        Assert.assertEquals("richType.richProperty", meta.findProperty("richType.richProperty", false));
        Assert.assertFalse(meta.hasGetter("[0]"));
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistance_mg9504_failAssert161() throws Exception {
        try {
            String __DSPOT_name_2315 = "*)Xg*ZNR[YU#(PJ`Fx,+";
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
            meta.getGetterType(__DSPOT_name_2315);
            org.junit.Assert.fail("shouldCheckGetterExistance_mg9504 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'*)Xg*ZNR\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistance_mg9507_failAssert163() throws Exception {
        try {
            String __DSPOT_name_2317 = "FklM{+YYUl4Nf_V/,Kg5";
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
            meta.getSetterType(__DSPOT_name_2317);
            org.junit.Assert.fail("shouldCheckGetterExistance_mg9507 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'FklM{+YYUl4Nf_V/,Kg5\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitBool9485() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitBool9485__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__5);
        boolean o_shouldCheckGetterExistancelitBool9485__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__6);
        boolean o_shouldCheckGetterExistancelitBool9485__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__7);
        boolean o_shouldCheckGetterExistancelitBool9485__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__8);
        boolean o_shouldCheckGetterExistancelitBool9485__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__9);
        boolean o_shouldCheckGetterExistancelitBool9485__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__10);
        boolean o_shouldCheckGetterExistancelitBool9485__11 = meta.hasGetter("richType.richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__11);
        boolean o_shouldCheckGetterExistancelitBool9485__12 = meta.hasGetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__12);
        boolean o_shouldCheckGetterExistancelitBool9485__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__13);
        boolean o_shouldCheckGetterExistancelitBool9485__14 = meta.hasGetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__14);
        boolean o_shouldCheckGetterExistancelitBool9485__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__15);
        String o_shouldCheckGetterExistancelitBool9485__16 = meta.findProperty("richType.richProperty", true);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitBool9485__16);
        boolean o_shouldCheckGetterExistancelitBool9485__17 = meta.hasGetter("[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitBool9485__17);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__5);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__6);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__7);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__8);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__9);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__10);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__11);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__12);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__13);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__14);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool9485__15);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitBool9485__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9438() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9438__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__5);
        boolean o_shouldCheckGetterExistancelitString9438__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__6);
        boolean o_shouldCheckGetterExistancelitString9438__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__7);
        boolean o_shouldCheckGetterExistancelitString9438__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__8);
        boolean o_shouldCheckGetterExistancelitString9438__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__9);
        boolean o_shouldCheckGetterExistancelitString9438__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__10);
        boolean o_shouldCheckGetterExistancelitString9438__11 = meta.hasGetter("richType.richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__11);
        boolean o_shouldCheckGetterExistancelitString9438__12 = meta.hasGetter("richType.{ichProperty");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9438__12);
        boolean o_shouldCheckGetterExistancelitString9438__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__13);
        boolean o_shouldCheckGetterExistancelitString9438__14 = meta.hasGetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__14);
        boolean o_shouldCheckGetterExistancelitString9438__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__15);
        String o_shouldCheckGetterExistancelitString9438__16 = meta.findProperty("richType.richProperty", false);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9438__16);
        boolean o_shouldCheckGetterExistancelitString9438__17 = meta.hasGetter("[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9438__17);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__5);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__6);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__7);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__8);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__9);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__10);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__11);
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9438__12);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__13);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__14);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9438__15);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9438__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistance_mg9505_failAssert162() throws Exception {
        try {
            String __DSPOT_name_2316 = "N8!rM5UX7`( I^!rbf]z";
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
            meta.getSetInvoker(__DSPOT_name_2316);
            org.junit.Assert.fail("shouldCheckGetterExistance_mg9505 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'N8!rM5UX7`( I^!rbf]z\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistance_mg9502_failAssert160() throws Exception {
        try {
            String __DSPOT_name_2314 = "fVJ3B^KETq8HHB;|a}D,";
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
            meta.getGetInvoker(__DSPOT_name_2314);
            org.junit.Assert.fail("shouldCheckGetterExistance_mg9502 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'fVJ3B^KETq8HHB;|a}D,\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9430() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9430__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__5);
        boolean o_shouldCheckGetterExistancelitString9430__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__6);
        boolean o_shouldCheckGetterExistancelitString9430__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__7);
        boolean o_shouldCheckGetterExistancelitString9430__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__8);
        boolean o_shouldCheckGetterExistancelitString9430__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__9);
        boolean o_shouldCheckGetterExistancelitString9430__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__10);
        boolean o_shouldCheckGetterExistancelitString9430__11 = meta.hasGetter("richTyfe.richField");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9430__11);
        boolean o_shouldCheckGetterExistancelitString9430__12 = meta.hasGetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__12);
        boolean o_shouldCheckGetterExistancelitString9430__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__13);
        boolean o_shouldCheckGetterExistancelitString9430__14 = meta.hasGetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__14);
        boolean o_shouldCheckGetterExistancelitString9430__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__15);
        String o_shouldCheckGetterExistancelitString9430__16 = meta.findProperty("richType.richProperty", false);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9430__16);
        boolean o_shouldCheckGetterExistancelitString9430__17 = meta.hasGetter("[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9430__17);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__5);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__6);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__7);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__8);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__9);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__10);
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9430__11);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__12);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__13);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__14);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9430__15);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString9430__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9473() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9473__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9473__5);
        boolean o_shouldCheckGetterExistancelitString9473__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9473__6);
        boolean o_shouldCheckGetterExistancelitString9473__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9473__7);
        boolean o_shouldCheckGetterExistancelitString9473__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9473__8);
        boolean o_shouldCheckGetterExistancelitString9473__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9473__9);
        boolean o_shouldCheckGetterExistancelitString9473__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9473__10);
        boolean o_shouldCheckGetterExistancelitString9473__11 = meta.hasGetter("richType.richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9473__11);
        boolean o_shouldCheckGetterExistancelitString9473__12 = meta.hasGetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9473__12);
        boolean o_shouldCheckGetterExistancelitString9473__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9473__13);
        boolean o_shouldCheckGetterExistancelitString9473__14 = meta.hasGetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9473__14);
        boolean o_shouldCheckGetterExistancelitString9473__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9473__15);
        meta.findProperty("NQU/C%:3jNf]#XG@771Q ", false);
        meta.hasGetter("[0]");
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9472() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9472__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__5);
        boolean o_shouldCheckGetterExistancelitString9472__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__6);
        boolean o_shouldCheckGetterExistancelitString9472__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__7);
        boolean o_shouldCheckGetterExistancelitString9472__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__8);
        boolean o_shouldCheckGetterExistancelitString9472__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__9);
        boolean o_shouldCheckGetterExistancelitString9472__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__10);
        boolean o_shouldCheckGetterExistancelitString9472__11 = meta.hasGetter("richType.richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__11);
        boolean o_shouldCheckGetterExistancelitString9472__12 = meta.hasGetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__12);
        boolean o_shouldCheckGetterExistancelitString9472__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__13);
        boolean o_shouldCheckGetterExistancelitString9472__14 = meta.hasGetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__14);
        boolean o_shouldCheckGetterExistancelitString9472__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__15);
        String o_shouldCheckGetterExistancelitString9472__16 = meta.findProperty("richType.richProprty", false);
        Assert.assertEquals("richType.", o_shouldCheckGetterExistancelitString9472__16);
        boolean o_shouldCheckGetterExistancelitString9472__17 = meta.hasGetter("[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString9472__17);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__5);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__6);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__7);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__8);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__9);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__10);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__11);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__12);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__13);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__14);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9472__15);
        Assert.assertEquals("richType.", o_shouldCheckGetterExistancelitString9472__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString9471() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString9471__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9471__5);
        boolean o_shouldCheckGetterExistancelitString9471__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9471__6);
        boolean o_shouldCheckGetterExistancelitString9471__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9471__7);
        boolean o_shouldCheckGetterExistancelitString9471__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9471__8);
        boolean o_shouldCheckGetterExistancelitString9471__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9471__9);
        boolean o_shouldCheckGetterExistancelitString9471__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9471__10);
        boolean o_shouldCheckGetterExistancelitString9471__11 = meta.hasGetter("richType.richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9471__11);
        boolean o_shouldCheckGetterExistancelitString9471__12 = meta.hasGetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9471__12);
        boolean o_shouldCheckGetterExistancelitString9471__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9471__13);
        boolean o_shouldCheckGetterExistancelitString9471__14 = meta.hasGetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9471__14);
        boolean o_shouldCheckGetterExistancelitString9471__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString9471__15);
        meta.findProperty("riNchType.richProperty", false);
        meta.hasGetter("[0]");
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancenull9513_failAssert166litString14711() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = null;
            meta.hasGetter("richField");
            meta.hasGetter("richProperty");
            meta.hasGetter("richList");
            meta.hasGetter("richMap");
            meta.hasGetter("richList[0]");
            meta.hasGetter("richType");
            meta.hasGetter("richType.richField");
            meta.hasGetter(":");
            meta.hasGetter("richType.richList");
            meta.hasGetter("richType.richMap");
            meta.hasGetter("richType.richList[0]");
            meta.findProperty("richType.richProperty", false);
            meta.hasGetter("[0]");
            org.junit.Assert.fail("shouldCheckGetterExistancenull9513 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancenull9565_failAssert180null23875null53896() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = null;
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
            org.junit.Assert.fail("shouldCheckGetterExistancenull9565 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
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
    public void shouldCheckSetterExistance_mg57022_failAssert185() throws Exception {
        try {
            String __DSPOT_name_5620 = "l;lB}CE[/?0+<u4<Iyl#";
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
            meta.getSetterType(__DSPOT_name_5620);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg57022 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'l;lB}CE\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString56951() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistancelitString56951__5 = meta.hasSetter("richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__5);
        boolean o_shouldCheckSetterExistancelitString56951__6 = meta.hasSetter("richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__6);
        boolean o_shouldCheckSetterExistancelitString56951__7 = meta.hasSetter("richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__7);
        boolean o_shouldCheckSetterExistancelitString56951__8 = meta.hasSetter("richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__8);
        boolean o_shouldCheckSetterExistancelitString56951__9 = meta.hasSetter("richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__9);
        boolean o_shouldCheckSetterExistancelitString56951__10 = meta.hasSetter("");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString56951__10);
        boolean o_shouldCheckSetterExistancelitString56951__11 = meta.hasSetter("richType.richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__11);
        boolean o_shouldCheckSetterExistancelitString56951__12 = meta.hasSetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__12);
        boolean o_shouldCheckSetterExistancelitString56951__13 = meta.hasSetter("richType.richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__13);
        boolean o_shouldCheckSetterExistancelitString56951__14 = meta.hasSetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__14);
        boolean o_shouldCheckSetterExistancelitString56951__15 = meta.hasSetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__15);
        boolean o_shouldCheckSetterExistancelitString56951__16 = meta.hasSetter("[0]");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString56951__16);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__5);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__6);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__7);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__8);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__9);
        Assert.assertFalse(o_shouldCheckSetterExistancelitString56951__10);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__11);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__12);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__13);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__14);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56951__15);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg57016() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_5616 = true;
        String __DSPOT_name_5615 = "ZU(Hi 8SZ08/?58T:)nQ";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistance_mg57016__7 = meta.hasSetter("richField");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57016__7);
        boolean o_shouldCheckSetterExistance_mg57016__8 = meta.hasSetter("richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57016__8);
        boolean o_shouldCheckSetterExistance_mg57016__9 = meta.hasSetter("richList");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57016__9);
        boolean o_shouldCheckSetterExistance_mg57016__10 = meta.hasSetter("richMap");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57016__10);
        boolean o_shouldCheckSetterExistance_mg57016__11 = meta.hasSetter("richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57016__11);
        boolean o_shouldCheckSetterExistance_mg57016__12 = meta.hasSetter("richType");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57016__12);
        boolean o_shouldCheckSetterExistance_mg57016__13 = meta.hasSetter("richType.richField");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57016__13);
        boolean o_shouldCheckSetterExistance_mg57016__14 = meta.hasSetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57016__14);
        boolean o_shouldCheckSetterExistance_mg57016__15 = meta.hasSetter("richType.richList");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57016__15);
        boolean o_shouldCheckSetterExistance_mg57016__16 = meta.hasSetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57016__16);
        boolean o_shouldCheckSetterExistance_mg57016__17 = meta.hasSetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57016__17);
        boolean o_shouldCheckSetterExistance_mg57016__18 = meta.hasSetter("[0]");
        Assert.assertFalse(o_shouldCheckSetterExistance_mg57016__18);
        meta.findProperty(__DSPOT_name_5615, __DSPOT_useCamelCaseMapping_5616);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString56971() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistancelitString56971__5 = meta.hasSetter("richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__5);
        boolean o_shouldCheckSetterExistancelitString56971__6 = meta.hasSetter("richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__6);
        boolean o_shouldCheckSetterExistancelitString56971__7 = meta.hasSetter("richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__7);
        boolean o_shouldCheckSetterExistancelitString56971__8 = meta.hasSetter("richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__8);
        boolean o_shouldCheckSetterExistancelitString56971__9 = meta.hasSetter("richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__9);
        boolean o_shouldCheckSetterExistancelitString56971__10 = meta.hasSetter("richType");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__10);
        boolean o_shouldCheckSetterExistancelitString56971__11 = meta.hasSetter("richType.richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__11);
        boolean o_shouldCheckSetterExistancelitString56971__12 = meta.hasSetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__12);
        boolean o_shouldCheckSetterExistancelitString56971__13 = meta.hasSetter("rich;ype.richList");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString56971__13);
        boolean o_shouldCheckSetterExistancelitString56971__14 = meta.hasSetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__14);
        boolean o_shouldCheckSetterExistancelitString56971__15 = meta.hasSetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__15);
        boolean o_shouldCheckSetterExistancelitString56971__16 = meta.hasSetter("[0]");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString56971__16);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__5);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__6);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__7);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__8);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__9);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__10);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__11);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__12);
        Assert.assertFalse(o_shouldCheckSetterExistancelitString56971__13);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__14);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56971__15);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg57019_failAssert183() throws Exception {
        try {
            String __DSPOT_name_5618 = "p]78q7]PGW?K{`<XYj <";
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
            meta.getGetterType(__DSPOT_name_5618);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg57019 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'p]78q7]PGW?K{`<XYj <\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg57017_failAssert182() throws Exception {
        try {
            String __DSPOT_name_5617 = "G8gIwU#mt/MM)7pHcUF%";
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
            meta.getGetInvoker(__DSPOT_name_5617);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg57017 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'G8gIwU#mt/MM)7pHcUF%\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg57020_failAssert184() throws Exception {
        try {
            String __DSPOT_name_5619 = "EtLfYqEL6*#%-/r9)aPK";
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
            meta.getSetInvoker(__DSPOT_name_5619);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg57020 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'EtLfYqEL6*#%-/r9)aPK\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString56956() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistancelitString56956__5 = meta.hasSetter("richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__5);
        boolean o_shouldCheckSetterExistancelitString56956__6 = meta.hasSetter("richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__6);
        boolean o_shouldCheckSetterExistancelitString56956__7 = meta.hasSetter("richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__7);
        boolean o_shouldCheckSetterExistancelitString56956__8 = meta.hasSetter("richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__8);
        boolean o_shouldCheckSetterExistancelitString56956__9 = meta.hasSetter("richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__9);
        boolean o_shouldCheckSetterExistancelitString56956__10 = meta.hasSetter("richType");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__10);
        boolean o_shouldCheckSetterExistancelitString56956__11 = meta.hasSetter("richType.richFi[eld");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString56956__11);
        boolean o_shouldCheckSetterExistancelitString56956__12 = meta.hasSetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__12);
        boolean o_shouldCheckSetterExistancelitString56956__13 = meta.hasSetter("richType.richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__13);
        boolean o_shouldCheckSetterExistancelitString56956__14 = meta.hasSetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__14);
        boolean o_shouldCheckSetterExistancelitString56956__15 = meta.hasSetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__15);
        boolean o_shouldCheckSetterExistancelitString56956__16 = meta.hasSetter("[0]");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString56956__16);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__5);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__6);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__7);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__8);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__9);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__10);
        Assert.assertFalse(o_shouldCheckSetterExistancelitString56956__11);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__12);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__13);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__14);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString56956__15);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancenull57028_failAssert188litString61169() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = null;
            meta.hasSetter("richField");
            meta.hasSetter("richProperty");
            meta.hasSetter("richList");
            meta.hasSetter("richMap");
            meta.hasSetter("ricahList[0]");
            meta.hasSetter("richType");
            meta.hasSetter("richType.richField");
            meta.hasSetter("richType.richProperty");
            meta.hasSetter("richType.richList");
            meta.hasSetter("richType.richMap");
            meta.hasSetter("richType.richList[0]");
            meta.hasSetter("[0]");
            org.junit.Assert.fail("shouldCheckSetterExistancenull57028 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancenull57063_failAssert198null66494_mg95096() throws Exception {
        try {
            boolean __DSPOT_useCamelCaseMapping_8447 = true;
            String __DSPOT_name_8446 = "6%}d@kJ$cr*R`j-FRZP`";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = null;
            meta.hasSetter("richField");
            meta.hasSetter("richProperty");
            meta.hasSetter("richList");
            meta.hasSetter("richMap");
            meta.hasSetter("richList[0]");
            meta.hasSetter("richType");
            meta.hasSetter("richType.richField");
            meta.hasSetter(null);
            meta.hasSetter("richType.richList");
            meta.hasSetter("richType.richMap");
            meta.hasSetter("richType.richList[0]");
            meta.hasSetter("[0]");
            org.junit.Assert.fail("shouldCheckSetterExistancenull57063 should have thrown NullPointerException");
            meta.findProperty(__DSPOT_name_8446, __DSPOT_useCamelCaseMapping_8447);
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void shouldCheckTypeForEachGetter() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertEquals(String.class, meta.getGetterType("richField"));
        Assert.assertEquals(String.class, meta.getGetterType("richProperty"));
        Assert.assertEquals(List.class, meta.getGetterType("richList"));
        Assert.assertEquals(Map.class, meta.getGetterType("richMap"));
        Assert.assertEquals(List.class, meta.getGetterType("richList[0]"));
        Assert.assertEquals(RichType.class, meta.getGetterType("richType"));
        Assert.assertEquals(String.class, meta.getGetterType("richType.richField"));
        Assert.assertEquals(String.class, meta.getGetterType("richType.richProperty"));
        Assert.assertEquals(List.class, meta.getGetterType("richType.richList"));
        Assert.assertEquals(Map.class, meta.getGetterType("richType.richMap"));
        Assert.assertEquals(List.class, meta.getGetterType("richType.richList[0]"));
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetternull97878() throws Exception {
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
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString97800_failAssert257() throws Exception {
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
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("AichType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString97800 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'AichType\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString97736_failAssert203() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("riihField");
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
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString97736 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'riihField\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg97842_failAssert281() throws Exception {
        try {
            String __DSPOT_name_8681 = "NIFL;LNdw%cXc1Mf* 9^";
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
            meta.getSetterType(__DSPOT_name_8681);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg97842 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'NIFL;LNdw%cXc1Mf* 9^\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg97835() throws Exception {
        String __DSPOT_name_8675 = "iCcX`hqG/@l8Omo`qWd.";
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
        meta.findProperty(__DSPOT_name_8675);
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg97836() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_8677 = true;
        String __DSPOT_name_8676 = "eu@Wn,])[dt6]:JRg7>|";
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
        meta.findProperty(__DSPOT_name_8676, __DSPOT_useCamelCaseMapping_8677);
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg97840_failAssert280() throws Exception {
        try {
            String __DSPOT_name_8680 = "&0/]0pa0i{!BH>!5O[jM";
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
            meta.getSetInvoker(__DSPOT_name_8680);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg97840 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'&0/]0pa0i{!BH>!5O[jM\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg97837_failAssert278() throws Exception {
        try {
            String __DSPOT_name_8678 = "gqhgY0d5.W[|5BUrf^AW";
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
            meta.getGetInvoker(__DSPOT_name_8678);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg97837 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'gqhgY0d5.W[|5BUrf^AW\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString97792_failAssert250() throws Exception {
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
            meta.getGetterType("richTyp[.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString97792 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg97845_rv189923() throws Exception {
        Object __DSPOT_arg0_26662 = new Object();
        String __DSPOT_name_8683 = "+!+BMg_?|&<IJ/u$40h ";
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
        Class<?> __DSPOT_invoc_21 = meta.getGetterType("richType.richMap");
        meta.getGetterType("richType.richList[0]");
        boolean o_shouldCheckTypeForEachGetter_mg97845__17 = meta.hasSetter(__DSPOT_name_8683);
        boolean o_shouldCheckTypeForEachGetter_mg97845_rv189923__24 = __DSPOT_invoc_21.isInstance(__DSPOT_arg0_26662);
        Assert.assertFalse(o_shouldCheckTypeForEachGetter_mg97845_rv189923__24);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetternull97848_failAssert284litString116025() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = null;
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetternull97848 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetternull97891_failAssert294null146596litString329799() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = null;
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType(null);
            meta.getGetterType("");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetternull97891 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void shouldCheckTypeForEachSetter() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertEquals(String.class, meta.getSetterType("richField"));
        Assert.assertEquals(String.class, meta.getSetterType("richProperty"));
        Assert.assertEquals(List.class, meta.getSetterType("richList"));
        Assert.assertEquals(Map.class, meta.getSetterType("richMap"));
        Assert.assertEquals(List.class, meta.getSetterType("richList[0]"));
        Assert.assertEquals(RichType.class, meta.getSetterType("richType"));
        Assert.assertEquals(String.class, meta.getSetterType("richType.richField"));
        Assert.assertEquals(String.class, meta.getSetterType("richType.richProperty"));
        Assert.assertEquals(List.class, meta.getSetterType("richType.richList"));
        Assert.assertEquals(Map.class, meta.getSetterType("richType.richMap"));
        Assert.assertEquals(List.class, meta.getSetterType("richType.richList[0]"));
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString349197_failAssert438() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richTGpe.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString349197 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'richTGpe\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetternull349266() throws Exception {
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
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString349174_failAssert418() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("ri<chMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString349174 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'ri<chMap\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString349189_failAssert431() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("[ichType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString349189 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_mg349250_failAssert473() throws Exception {
        try {
            String __DSPOT_name_55861 = "q]vtyH>sO;X<Px&7r<G:";
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
            meta.getGetInvoker(__DSPOT_name_55861);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_mg349250 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'q]vtyH>sO;X<Px&7r<G:\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_mg349253_failAssert475() throws Exception {
        try {
            String __DSPOT_name_55863 = "QtBF,K E=kzcn4:w:5&i";
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
            meta.getSetInvoker(__DSPOT_name_55863);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_mg349253 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'QtBF,K E=kzcn4:w:5&i\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_mg349248() throws Exception {
        String __DSPOT_name_55858 = "tSya1.FhMz@15]^9Fb;=";
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
        meta.findProperty(__DSPOT_name_55858);
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString349160_failAssert406() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("xW;#@gF`/V-[");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString349160 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_mg349258_mg378684() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_61310 = true;
        String __DSPOT_name_61309 = "`xTegz9NHIY{nxrc_P[Z";
        String __DSPOT_name_55866 = "Iqv?xd1kKw`/=TMd6-[w";
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
        boolean o_shouldCheckTypeForEachSetter_mg349258__17 = meta.hasSetter(__DSPOT_name_55866);
        meta.findProperty(__DSPOT_name_61309, __DSPOT_useCamelCaseMapping_61310);
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetternull349261_failAssert479litString368457() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = null;
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetternull349261 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_mg349258_rv378947() throws Exception {
        String __DSPOT_arg0_61374 = "!>$C!7!jow/!?:|C{U3 ";
        String __DSPOT_name_55866 = "Iqv?xd1kKw`/=TMd6-[w";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getSetterType("richField");
        Class<?> __DSPOT_invoc_13 = meta.getSetterType("richProperty");
        meta.getSetterType("richList");
        meta.getSetterType("richMap");
        meta.getSetterType("richList[0]");
        meta.getSetterType("richType");
        meta.getSetterType("richType.richField");
        meta.getSetterType("richType.richProperty");
        meta.getSetterType("richType.richList");
        meta.getSetterType("richType.richMap");
        meta.getSetterType("richType.richList[0]");
        boolean o_shouldCheckTypeForEachSetter_mg349258__17 = meta.hasSetter(__DSPOT_name_55866);
        __DSPOT_invoc_13.getResource(__DSPOT_arg0_61374);
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetternull349261_failAssert479null370958null530006() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = null;
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("richList[0]");
            meta.getSetterType("richType");
            meta.getSetterType(null);
            meta.getSetterType(null);
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetternull349261 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void shouldCheckGetterAndSetterNames() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertEquals(5, meta.getGetterNames().length);
        Assert.assertEquals(5, meta.getSetterNames().length);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNamesnull17() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = null;
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg9_failAssert2() throws Exception {
        try {
            String __DSPOT_name_5 = ";0L`A=SO/woO!OKS@Rl&";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetInvoker(__DSPOT_name_5);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg9 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \';0L`A=SO/woO!OKS@Rl&\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg11_failAssert3() throws Exception {
        try {
            String __DSPOT_name_6 = "{ha!&Bcvg[?i!rb0/|]6";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType(__DSPOT_name_6);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg11 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'{ha!&Bcvg\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg15_failAssert4() throws Exception {
        try {
            String __DSPOT_name_9 = "}8wu]&8(Dgh`l V!3a(!";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.metaClassForProperty(__DSPOT_name_9);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg15 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'}8wu]&8(Dgh`l V!3a(!\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg6_failAssert0() throws Exception {
        try {
            String __DSPOT_name_3 = "2[|+mr6#-VtX(r!Fs2l>";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetInvoker(__DSPOT_name_3);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg6 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'2[|+mr6#-VtX(r!Fs2l>\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg5() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_2 = true;
        String __DSPOT_name_1 = ",y(q2 5[gpbL[{$QV5:W";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.findProperty(__DSPOT_name_1, __DSPOT_useCamelCaseMapping_2);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNamesnull23() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg14_mg880_failAssert23() throws Exception {
        try {
            String __DSPOT_name_260 = "gQD^/r %<0v|2J j[AsO";
            String __DSPOT_name_8 = "wpauR%h1,xavU[1Rvnj|";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterAndSetterNames_mg14__6 = meta.hasSetter(__DSPOT_name_8);
            meta.getSetterType(__DSPOT_name_260);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg14_mg880 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'gQD^/r %<0v|2J j\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg13_mg337_failAssert28() throws Exception {
        try {
            String __DSPOT_name_92 = "99)  +jNx$d:PtF]or`w";
            String __DSPOT_name_7 = "^FT)-ef&bk*201yCi*Od";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterAndSetterNames_mg13__6 = meta.hasGetter(__DSPOT_name_7);
            meta.getGetterType(__DSPOT_name_92);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg13_mg337 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'99)  +jNx$d:PtF]or`w\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg13_mg355() throws Exception {
        String __DSPOT_name_96 = "m#.W9*#qYoA($d%nuEff";
        String __DSPOT_name_7 = "^FT)-ef&bk*201yCi*Od";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterAndSetterNames_mg13__6 = meta.hasGetter(__DSPOT_name_7);
        boolean o_shouldCheckGetterAndSetterNames_mg13_mg355__10 = meta.hasGetter(__DSPOT_name_96);
        Assert.assertFalse(o_shouldCheckGetterAndSetterNames_mg13_mg355__10);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNamesnull17null964() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = null;
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNamesnull21null741() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg14_mg844() throws Exception {
        String __DSPOT_name_244 = ".(9M9YkYLwDF9}]4,Kk]";
        String __DSPOT_name_8 = "wpauR%h1,xavU[1Rvnj|";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterAndSetterNames_mg14__6 = meta.hasSetter(__DSPOT_name_8);
        meta.findProperty(__DSPOT_name_244);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg13_mg330_failAssert26() throws Exception {
        try {
            String __DSPOT_name_90 = "1FoAgu-u1_)f(dMmVFwF";
            String __DSPOT_name_7 = "^FT)-ef&bk*201yCi*Od";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterAndSetterNames_mg13__6 = meta.hasGetter(__DSPOT_name_7);
            meta.getGetInvoker(__DSPOT_name_90);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg13_mg330 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'1FoAgu-u1_)f(dMmVFwF\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg15_failAssert4_rv260() throws Exception {
        try {
            String __DSPOT_name_62 = "9AC*$S oY.>c^U!$Cz2l";
            String __DSPOT_name_9 = "}8wu]&8(Dgh`l V!3a(!";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            MetaClass __DSPOT_invoc_8 = meta.metaClassForProperty(__DSPOT_name_9);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg15 should have thrown ReflectionException");
            __DSPOT_invoc_8.getGetInvoker(__DSPOT_name_62);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg9_failAssert2_mg816() throws Exception {
        try {
            boolean __DSPOT_useCamelCaseMapping_233 = false;
            String __DSPOT_name_232 = "[DU@79_,7q>6EK|ECO>A";
            String __DSPOT_name_5 = ";0L`A=SO/woO!OKS@Rl&";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            meta.getSetInvoker(__DSPOT_name_5);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg9 should have thrown ReflectionException");
            meta.findProperty(__DSPOT_name_232, __DSPOT_useCamelCaseMapping_233);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg13_mg341_failAssert25() throws Exception {
        try {
            String __DSPOT_name_94 = "k<;Do^DZks#P][B@BafG";
            String __DSPOT_name_7 = "^FT)-ef&bk*201yCi*Od";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterAndSetterNames_mg13__6 = meta.hasGetter(__DSPOT_name_7);
            meta.getSetInvoker(__DSPOT_name_94);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg13_mg341 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'k<;Do^DZks#P][B@BafG\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg11_failAssert3_rv1043_rv5938() throws Exception {
        try {
            Object[] __DSPOT_arg1_1469 = new Object[]{ new Object(), new Object() };
            Object __DSPOT_arg0_1468 = new Object();
            Class<?>[] __DSPOT_arg1_308 = new Class<?>[]{  };
            String __DSPOT_arg0_307 = "3LHg@h[Y&QUR[N_Y#I)<";
            String __DSPOT_name_6 = "{ha!&Bcvg[?i!rb0/|]6";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            Class<?> __DSPOT_invoc_8 = meta.getSetterType(__DSPOT_name_6);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg11 should have thrown ReflectionException");
            Method __DSPOT_invoc_20 = __DSPOT_invoc_8.getMethod(__DSPOT_arg0_307, __DSPOT_arg1_308);
            __DSPOT_invoc_20.invoke(__DSPOT_arg0_1468, __DSPOT_arg1_1469);
        } catch (ReflectionException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg13_mg355_mg4777() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_1161 = true;
        String __DSPOT_name_1160 = "Y9{yV-@!Lh&K^AOp9u*s";
        String __DSPOT_name_96 = "m#.W9*#qYoA($d%nuEff";
        String __DSPOT_name_7 = "^FT)-ef&bk*201yCi*Od";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterAndSetterNames_mg13__6 = meta.hasGetter(__DSPOT_name_7);
        boolean o_shouldCheckGetterAndSetterNames_mg13_mg355__10 = meta.hasGetter(__DSPOT_name_96);
        meta.findProperty(__DSPOT_name_1160, __DSPOT_useCamelCaseMapping_1161);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNamesnull22null849null4227() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = null;
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg8_failAssert1_rv1015_rv7681() throws Exception {
        try {
            Object[] __DSPOT_arg1_1989 = new Object[]{ new Object(), new Object(), new Object(), new Object() };
            Object __DSPOT_arg0_1988 = new Object();
            Class<?>[] __DSPOT_arg1_302 = new Class<?>[]{  };
            String __DSPOT_arg0_301 = ")J$[[g4kX4j14:$ DHZ6";
            String __DSPOT_name_4 = "UgIvC=TU&zgYc TM1`_8";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
            Class<?> __DSPOT_invoc_8 = meta.getGetterType(__DSPOT_name_4);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg8 should have thrown ReflectionException");
            Method __DSPOT_invoc_20 = __DSPOT_invoc_8.getMethod(__DSPOT_arg0_301, __DSPOT_arg1_302);
            __DSPOT_invoc_20.invoke(__DSPOT_arg0_1988, __DSPOT_arg1_1989);
        } catch (ReflectionException expected) {
        }
    }

    @Test
    public void shouldFindPropertyName() {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertEquals("richField", meta.findProperty("RICHfield"));
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601001_failAssert593() throws Exception {
        try {
            String __DSPOT_name_103077 = "En>-.!VCv.|8.V-yf-[b";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            meta.getGetInvoker(__DSPOT_name_103077);
            org.junit.Assert.fail("shouldFindPropertyName_mg601001 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'En>-.!VCv.|8.V-yf-[b\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601006_failAssert596() throws Exception {
        try {
            String __DSPOT_name_103080 = "CI&{+mrda7S57`}Unu)F";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            meta.getSetterType(__DSPOT_name_103080);
            org.junit.Assert.fail("shouldFindPropertyName_mg601006 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'CI&{+mrda7S57`}Unu)F\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601008() throws Exception {
        String __DSPOT_name_103081 = "`z|xYPPw2&:N?,<.!`X9";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg601008__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601008__6);
        boolean o_shouldFindPropertyName_mg601008__7 = meta.hasGetter(__DSPOT_name_103081);
        Assert.assertFalse(o_shouldFindPropertyName_mg601008__7);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601008__6);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString600989() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyNamelitString600989__5 = meta.findProperty("richType.richList[0]");
        Assert.assertEquals("richType.", o_shouldFindPropertyNamelitString600989__5);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamenull601018() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601009() throws Exception {
        String __DSPOT_name_103082 = ">XNcL+.P8t[w|T$HDY8r";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg601009__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601009__6);
        boolean o_shouldFindPropertyName_mg601009__7 = meta.hasSetter(__DSPOT_name_103082);
        Assert.assertFalse(o_shouldFindPropertyName_mg601009__7);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601009__6);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601010_failAssert597() throws Exception {
        try {
            String __DSPOT_name_103083 = "%ZW[S_[Z_wr*d]uzX|p(";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            meta.metaClassForProperty(__DSPOT_name_103083);
            org.junit.Assert.fail("shouldFindPropertyName_mg601010 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'%ZW[S_[Z_wr*d]uzX|p(\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601000() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_103076 = true;
        String __DSPOT_name_103075 = "1Xex/X+No;FSy b152/;";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg601000__7 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601000__7);
        meta.findProperty(__DSPOT_name_103075, __DSPOT_useCamelCaseMapping_103076);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601004_failAssert595() throws Exception {
        try {
            String __DSPOT_name_103079 = "_Z5dE^:o`hp+YZ5.lvVN";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            meta.getSetInvoker(__DSPOT_name_103079);
            org.junit.Assert.fail("shouldFindPropertyName_mg601004 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'_Z5dE^:o`hp+YZ5.lvVN\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601008_mg602218_failAssert606() throws Exception {
        try {
            String __DSPOT_name_103382 = "7|}$X`R<FU^y{_.x,UQL";
            String __DSPOT_name_103081 = "`z|xYPPw2&:N?,<.!`X9";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg601008__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg601008__7 = meta.hasGetter(__DSPOT_name_103081);
            meta.getSetterType(__DSPOT_name_103382);
            org.junit.Assert.fail("shouldFindPropertyName_mg601008_mg602218 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'7|}$X`R<FU^y{_\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601009_mg601892_failAssert611() throws Exception {
        try {
            String __DSPOT_name_103319 = "K[;p|H(&NkQ13-P2GKc_";
            String __DSPOT_name_103082 = ">XNcL+.P8t[w|T$HDY8r";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg601009__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg601009__7 = meta.hasSetter(__DSPOT_name_103082);
            meta.getGetInvoker(__DSPOT_name_103319);
            org.junit.Assert.fail("shouldFindPropertyName_mg601009_mg601892 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'K[;p|H(&NkQ13-P2GKc_\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamelitString600990_remove601660() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601009_mg601906_failAssert610() throws Exception {
        try {
            String __DSPOT_name_103322 = ":(Dlw<O*#-gM>J<[Ao}^";
            String __DSPOT_name_103082 = ">XNcL+.P8t[w|T$HDY8r";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg601009__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg601009__7 = meta.hasSetter(__DSPOT_name_103082);
            meta.getSetterType(__DSPOT_name_103322);
            org.junit.Assert.fail("shouldFindPropertyName_mg601009_mg601906 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \':(Dlw<O*#-gM>J<\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601009_mg601899_failAssert605() throws Exception {
        try {
            String __DSPOT_name_103321 = "iiqLj?]7r7_Y&[_(J`kJ";
            String __DSPOT_name_103082 = ">XNcL+.P8t[w|T$HDY8r";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg601009__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg601009__7 = meta.hasSetter(__DSPOT_name_103082);
            meta.getSetInvoker(__DSPOT_name_103321);
            org.junit.Assert.fail("shouldFindPropertyName_mg601009_mg601899 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'iiqLj?]7r7_Y&[_(J`kJ\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601009_mg601887() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_103318 = true;
        String __DSPOT_name_103317 = "[s:jS%A_l^]YdaaYPt?7";
        String __DSPOT_name_103082 = ">XNcL+.P8t[w|T$HDY8r";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg601009__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601009__6);
        boolean o_shouldFindPropertyName_mg601009__7 = meta.hasSetter(__DSPOT_name_103082);
        meta.findProperty(__DSPOT_name_103317, __DSPOT_useCamelCaseMapping_103318);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601008null602267() throws Exception {
        String __DSPOT_name_103081 = "`z|xYPPw2&:N?,<.!`X9";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg601008__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601008__6);
        boolean o_shouldFindPropertyName_mg601008__7 = meta.hasGetter(__DSPOT_name_103081);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601008__6);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601009null601984() throws Exception {
        String __DSPOT_name_103082 = ">XNcL+.P8t[w|T$HDY8r";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg601009__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601009__6);
        boolean o_shouldFindPropertyName_mg601009__7 = meta.hasSetter(__DSPOT_name_103082);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601009__6);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601009_mg601915_failAssert603() throws Exception {
        try {
            String __DSPOT_name_103329 = "LSLGl|MIFN1+Z&Dus$Iw";
            String __DSPOT_name_103082 = ">XNcL+.P8t[w|T$HDY8r";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg601009__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg601009__7 = meta.hasSetter(__DSPOT_name_103082);
            meta.metaClassForProperty(__DSPOT_name_103329);
            org.junit.Assert.fail("shouldFindPropertyName_mg601009_mg601915 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'LSLGl|MIFN1+Z&Dus$Iw\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamenull601012_failAssert599_mg601152() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = null;
            meta.findProperty("RICHfield");
            org.junit.Assert.fail("shouldFindPropertyNamenull601012 should have thrown NullPointerException");
            meta.getGetterNames();
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601007_mg601966() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_103338 = false;
        String __DSPOT_name_103337 = "/fpG$K7?n(_OzI.CVdd<";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg601007__5 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601007__5);
        meta.hasDefaultConstructor();
        meta.findProperty(__DSPOT_name_103337, __DSPOT_useCamelCaseMapping_103338);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601008_mg602221() throws Exception {
        String __DSPOT_name_103384 = "MP8f6`eZ1w^J2K.y_}AP";
        String __DSPOT_name_103081 = "`z|xYPPw2&:N?,<.!`X9";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg601008__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601008__6);
        boolean o_shouldFindPropertyName_mg601008__7 = meta.hasGetter(__DSPOT_name_103081);
        boolean o_shouldFindPropertyName_mg601008_mg602221__13 = meta.hasSetter(__DSPOT_name_103384);
        Assert.assertFalse(o_shouldFindPropertyName_mg601008_mg602221__13);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601008__6);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601009_mg601913_mg609033() throws Exception {
        String __DSPOT_name_104783 = "Xq6tZNwE&Ebkn0wv8eF7";
        String __DSPOT_name_103328 = "1tr^o9TmWi(X`u]9?;Ba";
        String __DSPOT_name_103082 = ">XNcL+.P8t[w|T$HDY8r";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg601009__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601009__6);
        boolean o_shouldFindPropertyName_mg601009__7 = meta.hasSetter(__DSPOT_name_103082);
        boolean o_shouldFindPropertyName_mg601009_mg601913__13 = meta.hasSetter(__DSPOT_name_103328);
        boolean o_shouldFindPropertyName_mg601009_mg601913_mg609033__17 = meta.hasSetter(__DSPOT_name_104783);
        Assert.assertFalse(o_shouldFindPropertyName_mg601009_mg601913_mg609033__17);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601009__6);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601009_mg601910_mg609877_failAssert653() throws Exception {
        try {
            String __DSPOT_name_104934 = "46q]TK:>AD%Wq#=32tE>";
            String __DSPOT_name_103324 = "Ro#t>*?!Vy]B0TV tS4(";
            String __DSPOT_name_103082 = ">XNcL+.P8t[w|T$HDY8r";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg601009__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg601009__7 = meta.hasSetter(__DSPOT_name_103082);
            boolean o_shouldFindPropertyName_mg601009_mg601910__13 = meta.hasGetter(__DSPOT_name_103324);
            meta.getSetInvoker(__DSPOT_name_104934);
            org.junit.Assert.fail("shouldFindPropertyName_mg601009_mg601910_mg609877 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'46q]TK:>AD%Wq#=32tE>\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601009_mg601910_mg609862() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_104927 = true;
        String __DSPOT_name_104926 = ")Lw{er[f<&yh$/j/la(w";
        String __DSPOT_name_103324 = "Ro#t>*?!Vy]B0TV tS4(";
        String __DSPOT_name_103082 = ">XNcL+.P8t[w|T$HDY8r";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg601009__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601009__6);
        boolean o_shouldFindPropertyName_mg601009__7 = meta.hasSetter(__DSPOT_name_103082);
        boolean o_shouldFindPropertyName_mg601009_mg601910__13 = meta.hasGetter(__DSPOT_name_103324);
        meta.findProperty(__DSPOT_name_104926, __DSPOT_useCamelCaseMapping_104927);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601008_mg602220_mg609563() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_104867 = true;
        String __DSPOT_name_104866 = "U(]3RZ#}:xN1Fx6Vo#E.";
        String __DSPOT_name_103383 = "n`+b;jUl,n!id<Rg!E&@";
        String __DSPOT_name_103081 = "`z|xYPPw2&:N?,<.!`X9";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg601008__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601008__6);
        boolean o_shouldFindPropertyName_mg601008__7 = meta.hasGetter(__DSPOT_name_103081);
        boolean o_shouldFindPropertyName_mg601008_mg602220__13 = meta.hasGetter(__DSPOT_name_103383);
        meta.findProperty(__DSPOT_name_104866, __DSPOT_useCamelCaseMapping_104867);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601008_mg602220_mg609578_failAssert649() throws Exception {
        try {
            String __DSPOT_name_104876 = "D;PwKEn18$GL>x!`B1yK";
            String __DSPOT_name_103383 = "n`+b;jUl,n!id<Rg!E&@";
            String __DSPOT_name_103081 = "`z|xYPPw2&:N?,<.!`X9";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg601008__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg601008__7 = meta.hasGetter(__DSPOT_name_103081);
            boolean o_shouldFindPropertyName_mg601008_mg602220__13 = meta.hasGetter(__DSPOT_name_103383);
            meta.getSetterType(__DSPOT_name_104876);
            org.junit.Assert.fail("shouldFindPropertyName_mg601008_mg602220_mg609578 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'D;PwKEn18$GL>x!`B1yK\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601009_mg601913_mg608996_failAssert640() throws Exception {
        try {
            String __DSPOT_name_104779 = "[4rbZ!tca%:0kENKtM *";
            String __DSPOT_name_103328 = "1tr^o9TmWi(X`u]9?;Ba";
            String __DSPOT_name_103082 = ">XNcL+.P8t[w|T$HDY8r";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg601009__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg601009__7 = meta.hasSetter(__DSPOT_name_103082);
            boolean o_shouldFindPropertyName_mg601009_mg601913__13 = meta.hasSetter(__DSPOT_name_103328);
            meta.getGetterType(__DSPOT_name_104779);
            org.junit.Assert.fail("shouldFindPropertyName_mg601009_mg601913_mg608996 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamenull601018null601292null609245() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = null;
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601009_mg601910_mg609886_failAssert648() throws Exception {
        try {
            String __DSPOT_name_104937 = "c-me{%:Yw.$,3<:<vUK^";
            String __DSPOT_name_103324 = "Ro#t>*?!Vy]B0TV tS4(";
            String __DSPOT_name_103082 = ">XNcL+.P8t[w|T$HDY8r";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg601009__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg601009__7 = meta.hasSetter(__DSPOT_name_103082);
            boolean o_shouldFindPropertyName_mg601009_mg601910__13 = meta.hasGetter(__DSPOT_name_103324);
            meta.getSetterType(__DSPOT_name_104937);
            org.junit.Assert.fail("shouldFindPropertyName_mg601009_mg601910_mg609886 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'c-me{%:Yw\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601009_mg601913_mg608986_failAssert644() throws Exception {
        try {
            String __DSPOT_name_104778 = "b$h-9:Ph#c!{geV5S;<[";
            String __DSPOT_name_103328 = "1tr^o9TmWi(X`u]9?;Ba";
            String __DSPOT_name_103082 = ">XNcL+.P8t[w|T$HDY8r";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg601009__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg601009__7 = meta.hasSetter(__DSPOT_name_103082);
            boolean o_shouldFindPropertyName_mg601009_mg601913__13 = meta.hasSetter(__DSPOT_name_103328);
            meta.getGetInvoker(__DSPOT_name_104778);
            org.junit.Assert.fail("shouldFindPropertyName_mg601009_mg601913_mg608986 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'b$h-9:Ph#c!{geV5S;<[\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601008_mg602221_mg609303_failAssert645() throws Exception {
        try {
            String __DSPOT_name_104831 = "!^^4LnYPKVkx&DP<8US[";
            String __DSPOT_name_103384 = "MP8f6`eZ1w^J2K.y_}AP";
            String __DSPOT_name_103081 = "`z|xYPPw2&:N?,<.!`X9";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg601008__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg601008__7 = meta.hasGetter(__DSPOT_name_103081);
            boolean o_shouldFindPropertyName_mg601008_mg602221__13 = meta.hasSetter(__DSPOT_name_103384);
            meta.getSetterType(__DSPOT_name_104831);
            org.junit.Assert.fail("shouldFindPropertyName_mg601008_mg602221_mg609303 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601009_mg601910_mg609895_failAssert638() throws Exception {
        try {
            String __DSPOT_name_104942 = "P[(W4BWsZKwG1 3k,H!m";
            String __DSPOT_name_103324 = "Ro#t>*?!Vy]B0TV tS4(";
            String __DSPOT_name_103082 = ">XNcL+.P8t[w|T$HDY8r";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg601009__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg601009__7 = meta.hasSetter(__DSPOT_name_103082);
            boolean o_shouldFindPropertyName_mg601009_mg601910__13 = meta.hasGetter(__DSPOT_name_103324);
            meta.metaClassForProperty(__DSPOT_name_104942);
            org.junit.Assert.fail("shouldFindPropertyName_mg601009_mg601910_mg609895 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'P[(W4BWsZKwG1 3k,H!m\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601008_mg602220_mg609571_failAssert650() throws Exception {
        try {
            String __DSPOT_name_104872 = "5)a[uJuZd`(BR=}.#%W*";
            String __DSPOT_name_103383 = "n`+b;jUl,n!id<Rg!E&@";
            String __DSPOT_name_103081 = "`z|xYPPw2&:N?,<.!`X9";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg601008__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg601008__7 = meta.hasGetter(__DSPOT_name_103081);
            boolean o_shouldFindPropertyName_mg601008_mg602220__13 = meta.hasGetter(__DSPOT_name_103383);
            meta.getGetterType(__DSPOT_name_104872);
            org.junit.Assert.fail("shouldFindPropertyName_mg601008_mg602220_mg609571 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'5)a\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg601008_mg602221_mg609306() throws Exception {
        String __DSPOT_name_104833 = "MJvfN9$>6$Q^?1v9#x!%";
        String __DSPOT_name_103384 = "MP8f6`eZ1w^J2K.y_}AP";
        String __DSPOT_name_103081 = "`z|xYPPw2&:N?,<.!`X9";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg601008__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601008__6);
        boolean o_shouldFindPropertyName_mg601008__7 = meta.hasGetter(__DSPOT_name_103081);
        boolean o_shouldFindPropertyName_mg601008_mg602221__13 = meta.hasSetter(__DSPOT_name_103384);
        boolean o_shouldFindPropertyName_mg601008_mg602221_mg609306__17 = meta.hasSetter(__DSPOT_name_104833);
        Assert.assertFalse(o_shouldFindPropertyName_mg601008_mg602221_mg609306__17);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg601008__6);
    }
}

