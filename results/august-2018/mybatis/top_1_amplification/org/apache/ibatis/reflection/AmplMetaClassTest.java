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
    public void shouldTestDataTypeOfGenericMethodlitString638684_failAssert810() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("v");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString638684 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'v\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString638683_failAssert809() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("richType.richField");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString638683 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'richType\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodnull638708() throws Exception {
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
    public void shouldTestDataTypeOfGenericMethod_mg638693_failAssert814() throws Exception {
        try {
            String __DSPOT_name_113288 = "=o)w[1HCd!&)<BkLy[Ie";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            meta.getGetInvoker(__DSPOT_name_113288);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg638693 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'=o)w[1HCd!&)<BkLy[Ie\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg638695_failAssert815() throws Exception {
        try {
            String __DSPOT_name_113289 = "-.e? mAVYBCBM-a>$[xm";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            meta.getGetterType(__DSPOT_name_113289);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg638695 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'-\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_mg638696_failAssert816() throws Exception {
        try {
            String __DSPOT_name_113290 = "V+PHx9<o1Ua.PH<]r%-o";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            meta.getSetInvoker(__DSPOT_name_113290);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_mg638696 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'V+PHx9<o1Ua.PH<]r%-o\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString638680_failAssert806() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("");
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString638680 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodlitString638678_failAssert804() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodlitString638678 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'richField\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodnull638704_failAssert820null639275() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = null;
            meta.getGetterType("id");
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodnull638704 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688_mg643362() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_114337 = true;
        String __DSPOT_name_114336 = "xEWD$gh^ndF|QcUFzMoA";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getSetterType("id");
        meta.findProperty(__DSPOT_name_114336, __DSPOT_useCamelCaseMapping_114337);
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688litString643151_failAssert854() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("V");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_add638688litString643151 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'V\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688litString643143_failAssert857() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("-");
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_add638688litString643143 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'-\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688_mg643374_failAssert846() throws Exception {
        try {
            String __DSPOT_name_114344 = "S^[C:vLQbXq$iuYuKqO-";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            meta.getSetInvoker(__DSPOT_name_114344);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_add638688_mg643374 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'S^[C:vLQbXq$iuYuKqO-\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688_rv643657() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        Class<?> __DSPOT_invoc_18 = meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_add638688_rv643657__12 = __DSPOT_invoc_18.desiredAssertionStatus();
        Assert.assertFalse(o_shouldTestDataTypeOfGenericMethod_add638688_rv643657__12);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688_mg643365_failAssert845() throws Exception {
        try {
            String __DSPOT_name_114338 = "K1-kxl_^{_JkMmo:BKkC";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            meta.getGetInvoker(__DSPOT_name_114338);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_add638688_mg643365 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'K1-kxl_^{_JkMmo:BKkC\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688litString643142_failAssert851() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("richType.richField");
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_add638688litString643142 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'richType\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688_add643282_mg673928_failAssert908() throws Exception {
        try {
            String __DSPOT_name_121502 = "kc8#SU^:+>*lf`IzW_^J";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688_add643282__6 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            o_shouldTestDataTypeOfGenericMethod_add638688__3.getSetInvoker(__DSPOT_name_121502);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_add638688_add643282_mg673928 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'kc8#SU^:+>*lf`IzW_^J\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688_add643279_mg672989_failAssert910() throws Exception {
        try {
            String __DSPOT_name_121284 = "mln)b11)P$(@vOZ6XmNY";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688_add643279__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            o_shouldTestDataTypeOfGenericMethod_add638688__3.getSetterType(__DSPOT_name_121284);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_add638688_add643279_mg672989 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'mln)b11)P$(@vOZ6XmNY\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688_add643282_mg673920_failAssert913() throws Exception {
        try {
            String __DSPOT_name_121499 = "]4Z(XZyo%(#PLq&S4il(";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688_add643282__6 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            o_shouldTestDataTypeOfGenericMethod_add638688__3.getGetInvoker(__DSPOT_name_121499);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_add638688_add643282_mg673920 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \']4Z(XZyo%(#PLq&S4il(\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688_add643279_mg673016() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_121305 = true;
        String __DSPOT_name_121304 = "Aw/EiNgk*|x?KrcLt3LS";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldTestDataTypeOfGenericMethod_add638688_add643279__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688_add643279__3)).hasDefaultConstructor());
        MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getSetterType("id");
        meta.findProperty(__DSPOT_name_121304, __DSPOT_useCamelCaseMapping_121305);
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688_add643279_mg673004() throws Exception {
        String __DSPOT_name_121296 = "2i*0m.6#u9+p?SB= :hV";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldTestDataTypeOfGenericMethod_add638688_add643279__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688_add643279__3)).hasDefaultConstructor());
        MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_add638688_add643279_mg673004__14 = o_shouldTestDataTypeOfGenericMethod_add638688__3.hasSetter(__DSPOT_name_121296);
        Assert.assertFalse(o_shouldTestDataTypeOfGenericMethod_add638688_add643279_mg673004__14);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688_add643279__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethodnull638704_failAssert820null639275_add654815() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = null;
            meta.getGetterType("id");
            meta.getGetterType("id");
            meta.getSetterType("id");
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethodnull638704 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688_add643279_mg672998() throws Exception {
        String __DSPOT_name_121292 = "DrRj.{(ZZB42R{)3Q|u:";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldTestDataTypeOfGenericMethod_add638688_add643279__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688_add643279__3)).hasDefaultConstructor());
        MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_add638688_add643279_mg672998__14 = o_shouldTestDataTypeOfGenericMethod_add638688__3.hasGetter(__DSPOT_name_121292);
        Assert.assertFalse(o_shouldTestDataTypeOfGenericMethod_add638688_add643279_mg672998__14);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688_add643279__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688_add643282_mg673940_failAssert911() throws Exception {
        try {
            String __DSPOT_name_121505 = "[>?)oc:L[wLPA]] 8[e;";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688_add643282__6 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            o_shouldTestDataTypeOfGenericMethod_add638688__3.getSetterType(__DSPOT_name_121505);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_add638688_add643282_mg673940 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688_add643282_mg674011() throws Exception {
        String __DSPOT_name_121520 = "%Vz_bId!qkj:kH3I@S`a";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688__3)).hasDefaultConstructor());
        MetaClass o_shouldTestDataTypeOfGenericMethod_add638688_add643282__6 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688_add643282__6)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("id");
        meta.getSetterType("id");
        boolean o_shouldTestDataTypeOfGenericMethod_add638688_add643282_mg674011__14 = o_shouldTestDataTypeOfGenericMethod_add638688_add643282__6.hasSetter(__DSPOT_name_121520);
        Assert.assertFalse(o_shouldTestDataTypeOfGenericMethod_add638688_add643282_mg674011__14);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (o_shouldTestDataTypeOfGenericMethod_add638688_add643282__6)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldTestDataTypeOfGenericMethod_add638688_add643282_mg673926_failAssert912() throws Exception {
        try {
            String __DSPOT_name_121501 = "7e5&$WY[/<pXtz4;Aq(S";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688__3 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass o_shouldTestDataTypeOfGenericMethod_add638688_add643282__6 = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(GenericConcrete.class, reflectorFactory);
            meta.getGetterType("id");
            meta.getSetterType("id");
            o_shouldTestDataTypeOfGenericMethod_add638688__3.getGetterType(__DSPOT_name_121501);
            org.junit.Assert.fail("shouldTestDataTypeOfGenericMethod_add638688_add643282_mg673926 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'7e5&$WY\' in \'class org.apache.ibatis.domain.misc.generics.GenericConcrete\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString10089() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString10089__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10089__5);
        boolean o_shouldCheckGetterExistancelitString10089__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10089__6);
        boolean o_shouldCheckGetterExistancelitString10089__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10089__7);
        boolean o_shouldCheckGetterExistancelitString10089__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10089__8);
        boolean o_shouldCheckGetterExistancelitString10089__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10089__9);
        boolean o_shouldCheckGetterExistancelitString10089__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10089__10);
        boolean o_shouldCheckGetterExistancelitString10089__11 = meta.hasGetter("richType.richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10089__11);
        boolean o_shouldCheckGetterExistancelitString10089__12 = meta.hasGetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10089__12);
        boolean o_shouldCheckGetterExistancelitString10089__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10089__13);
        boolean o_shouldCheckGetterExistancelitString10089__14 = meta.hasGetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10089__14);
        boolean o_shouldCheckGetterExistancelitString10089__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10089__15);
        meta.findProperty("richT`ype.richProperty", false);
        meta.hasGetter("[0]");
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistance_mg10120_failAssert105() throws Exception {
        try {
            String __DSPOT_name_2794 = "6>/v<W5:s%ezvrGEreU<";
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
            meta.getGetInvoker(__DSPOT_name_2794);
            org.junit.Assert.fail("shouldCheckGetterExistance_mg10120 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'6>/v<W5:s%ezvrGEreU<\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancenull10183_failAssert125() throws Exception {
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
            org.junit.Assert.fail("shouldCheckGetterExistancenull10183 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistance_mg10125_failAssert108() throws Exception {
        try {
            String __DSPOT_name_2797 = "S.N9[p6kQ}-q3#WIefj|";
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
            meta.getSetterType(__DSPOT_name_2797);
            org.junit.Assert.fail("shouldCheckGetterExistance_mg10125 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'S\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistance_mg10123_failAssert107() throws Exception {
        try {
            String __DSPOT_name_2796 = "NcQG/f UxnV[bZiI%a#8";
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
            meta.getSetInvoker(__DSPOT_name_2796);
            org.junit.Assert.fail("shouldCheckGetterExistance_mg10123 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'NcQG/f UxnV[bZiI%a#8\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString10090() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString10090__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__5);
        boolean o_shouldCheckGetterExistancelitString10090__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__6);
        boolean o_shouldCheckGetterExistancelitString10090__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__7);
        boolean o_shouldCheckGetterExistancelitString10090__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__8);
        boolean o_shouldCheckGetterExistancelitString10090__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__9);
        boolean o_shouldCheckGetterExistancelitString10090__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__10);
        boolean o_shouldCheckGetterExistancelitString10090__11 = meta.hasGetter("richType.richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__11);
        boolean o_shouldCheckGetterExistancelitString10090__12 = meta.hasGetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__12);
        boolean o_shouldCheckGetterExistancelitString10090__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__13);
        boolean o_shouldCheckGetterExistancelitString10090__14 = meta.hasGetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__14);
        boolean o_shouldCheckGetterExistancelitString10090__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__15);
        String o_shouldCheckGetterExistancelitString10090__16 = meta.findProperty("richType.ricProperty", false);
        Assert.assertEquals("richType.", o_shouldCheckGetterExistancelitString10090__16);
        boolean o_shouldCheckGetterExistancelitString10090__17 = meta.hasGetter("[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString10090__17);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__5);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__6);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__7);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__8);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__9);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__10);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__11);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__12);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__13);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__14);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10090__15);
        Assert.assertEquals("richType.", o_shouldCheckGetterExistancelitString10090__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString10067_failAssert104() throws Exception {
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
            meta.hasGetter("p(9c@=iyKZI(dB?u[");
            meta.hasGetter("richType.richMap");
            meta.hasGetter("richType.richList[0]");
            meta.findProperty("richType.richProperty", false);
            meta.hasGetter("[0]");
            org.junit.Assert.fail("shouldCheckGetterExistancelitString10067 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistance_mg10122_failAssert106() throws Exception {
        try {
            String __DSPOT_name_2795 = "<1A*((38s&}$H,n<U[]d";
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
            meta.getGetterType(__DSPOT_name_2795);
            org.junit.Assert.fail("shouldCheckGetterExistance_mg10122 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'<1A*((38s&}$H,n<U\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitBool10103() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitBool10103__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__5);
        boolean o_shouldCheckGetterExistancelitBool10103__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__6);
        boolean o_shouldCheckGetterExistancelitBool10103__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__7);
        boolean o_shouldCheckGetterExistancelitBool10103__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__8);
        boolean o_shouldCheckGetterExistancelitBool10103__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__9);
        boolean o_shouldCheckGetterExistancelitBool10103__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__10);
        boolean o_shouldCheckGetterExistancelitBool10103__11 = meta.hasGetter("richType.richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__11);
        boolean o_shouldCheckGetterExistancelitBool10103__12 = meta.hasGetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__12);
        boolean o_shouldCheckGetterExistancelitBool10103__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__13);
        boolean o_shouldCheckGetterExistancelitBool10103__14 = meta.hasGetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__14);
        boolean o_shouldCheckGetterExistancelitBool10103__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__15);
        String o_shouldCheckGetterExistancelitBool10103__16 = meta.findProperty("richType.richProperty", true);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitBool10103__16);
        boolean o_shouldCheckGetterExistancelitBool10103__17 = meta.hasGetter("[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitBool10103__17);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__5);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__6);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__7);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__8);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__9);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__10);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__11);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__12);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__13);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__14);
        Assert.assertTrue(o_shouldCheckGetterExistancelitBool10103__15);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitBool10103__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString10074() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString10074__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__5);
        boolean o_shouldCheckGetterExistancelitString10074__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__6);
        boolean o_shouldCheckGetterExistancelitString10074__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__7);
        boolean o_shouldCheckGetterExistancelitString10074__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__8);
        boolean o_shouldCheckGetterExistancelitString10074__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__9);
        boolean o_shouldCheckGetterExistancelitString10074__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__10);
        boolean o_shouldCheckGetterExistancelitString10074__11 = meta.hasGetter("richType.richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__11);
        boolean o_shouldCheckGetterExistancelitString10074__12 = meta.hasGetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__12);
        boolean o_shouldCheckGetterExistancelitString10074__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__13);
        boolean o_shouldCheckGetterExistancelitString10074__14 = meta.hasGetter("richType.ichMap");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString10074__14);
        boolean o_shouldCheckGetterExistancelitString10074__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__15);
        String o_shouldCheckGetterExistancelitString10074__16 = meta.findProperty("richType.richProperty", false);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString10074__16);
        boolean o_shouldCheckGetterExistancelitString10074__17 = meta.hasGetter("[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString10074__17);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__5);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__6);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__7);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__8);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__9);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__10);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__11);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__12);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__13);
        Assert.assertFalse(o_shouldCheckGetterExistancelitString10074__14);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10074__15);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString10074__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancelitString10073() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterExistancelitString10073__5 = meta.hasGetter("richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__5);
        boolean o_shouldCheckGetterExistancelitString10073__6 = meta.hasGetter("richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__6);
        boolean o_shouldCheckGetterExistancelitString10073__7 = meta.hasGetter("richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__7);
        boolean o_shouldCheckGetterExistancelitString10073__8 = meta.hasGetter("richMap");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__8);
        boolean o_shouldCheckGetterExistancelitString10073__9 = meta.hasGetter("richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__9);
        boolean o_shouldCheckGetterExistancelitString10073__10 = meta.hasGetter("richType");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__10);
        boolean o_shouldCheckGetterExistancelitString10073__11 = meta.hasGetter("richType.richField");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__11);
        boolean o_shouldCheckGetterExistancelitString10073__12 = meta.hasGetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__12);
        boolean o_shouldCheckGetterExistancelitString10073__13 = meta.hasGetter("richType.richList");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__13);
        boolean o_shouldCheckGetterExistancelitString10073__14 = meta.hasGetter("richTypVe.richMap");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString10073__14);
        boolean o_shouldCheckGetterExistancelitString10073__15 = meta.hasGetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__15);
        String o_shouldCheckGetterExistancelitString10073__16 = meta.findProperty("richType.richProperty", false);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString10073__16);
        boolean o_shouldCheckGetterExistancelitString10073__17 = meta.hasGetter("[0]");
        Assert.assertFalse(o_shouldCheckGetterExistancelitString10073__17);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__5);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__6);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__7);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__8);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__9);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__10);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__11);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__12);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__13);
        Assert.assertFalse(o_shouldCheckGetterExistancelitString10073__14);
        Assert.assertTrue(o_shouldCheckGetterExistancelitString10073__15);
        Assert.assertEquals("richType.richProperty", o_shouldCheckGetterExistancelitString10073__16);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancenull10131_failAssert111litString15151() throws Exception {
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
            meta.hasGetter("richTpe.richProperty");
            meta.hasGetter("richType.richList");
            meta.hasGetter("richType.richMap");
            meta.hasGetter("richType.richList[0]");
            meta.findProperty("richType.richProperty", false);
            meta.hasGetter("[0]");
            org.junit.Assert.fail("shouldCheckGetterExistancenull10131 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterExistancenull10131_failAssert111litString15074_mg56717() throws Exception {
        try {
            boolean __DSPOT_useCamelCaseMapping_6065 = true;
            String __DSPOT_name_6064 = "M5CE!?@&]D6?yf<h7(q6";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = null;
            meta.hasGetter("rich>Field");
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
            org.junit.Assert.fail("shouldCheckGetterExistancenull10131 should have thrown NullPointerException");
            meta.findProperty(__DSPOT_name_6064, __DSPOT_useCamelCaseMapping_6065);
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
    public void shouldCheckSetterExistance_mg57684() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_6106 = true;
        String __DSPOT_name_6105 = "bT?u=[S0eG#vi3LZo_*T";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistance_mg57684__7 = meta.hasSetter("richField");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57684__7);
        boolean o_shouldCheckSetterExistance_mg57684__8 = meta.hasSetter("richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57684__8);
        boolean o_shouldCheckSetterExistance_mg57684__9 = meta.hasSetter("richList");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57684__9);
        boolean o_shouldCheckSetterExistance_mg57684__10 = meta.hasSetter("richMap");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57684__10);
        boolean o_shouldCheckSetterExistance_mg57684__11 = meta.hasSetter("richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57684__11);
        boolean o_shouldCheckSetterExistance_mg57684__12 = meta.hasSetter("richType");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57684__12);
        boolean o_shouldCheckSetterExistance_mg57684__13 = meta.hasSetter("richType.richField");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57684__13);
        boolean o_shouldCheckSetterExistance_mg57684__14 = meta.hasSetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57684__14);
        boolean o_shouldCheckSetterExistance_mg57684__15 = meta.hasSetter("richType.richList");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57684__15);
        boolean o_shouldCheckSetterExistance_mg57684__16 = meta.hasSetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57684__16);
        boolean o_shouldCheckSetterExistance_mg57684__17 = meta.hasSetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistance_mg57684__17);
        boolean o_shouldCheckSetterExistance_mg57684__18 = meta.hasSetter("[0]");
        Assert.assertFalse(o_shouldCheckSetterExistance_mg57684__18);
        meta.findProperty(__DSPOT_name_6105, __DSPOT_useCamelCaseMapping_6106);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg57690_failAssert130() throws Exception {
        try {
            String __DSPOT_name_6110 = "6mI[k(;[)nJV&kCOMMu[";
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
            meta.getSetterType(__DSPOT_name_6110);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg57690 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'6mI\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString57660() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistancelitString57660__5 = meta.hasSetter("richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__5);
        boolean o_shouldCheckSetterExistancelitString57660__6 = meta.hasSetter("richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__6);
        boolean o_shouldCheckSetterExistancelitString57660__7 = meta.hasSetter("richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__7);
        boolean o_shouldCheckSetterExistancelitString57660__8 = meta.hasSetter("richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__8);
        boolean o_shouldCheckSetterExistancelitString57660__9 = meta.hasSetter("richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__9);
        boolean o_shouldCheckSetterExistancelitString57660__10 = meta.hasSetter("richType");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__10);
        boolean o_shouldCheckSetterExistancelitString57660__11 = meta.hasSetter("richType.richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__11);
        boolean o_shouldCheckSetterExistancelitString57660__12 = meta.hasSetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__12);
        boolean o_shouldCheckSetterExistancelitString57660__13 = meta.hasSetter("richType.richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__13);
        boolean o_shouldCheckSetterExistancelitString57660__14 = meta.hasSetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__14);
        boolean o_shouldCheckSetterExistancelitString57660__15 = meta.hasSetter("\n");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString57660__15);
        boolean o_shouldCheckSetterExistancelitString57660__16 = meta.hasSetter("[0]");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString57660__16);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__5);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__6);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__7);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__8);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__9);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__10);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__11);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__12);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__13);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57660__14);
        Assert.assertFalse(o_shouldCheckSetterExistancelitString57660__15);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg57685_failAssert127() throws Exception {
        try {
            String __DSPOT_name_6107 = "i>$v{}4%*x-LC]a35s)h";
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
            meta.getGetInvoker(__DSPOT_name_6107);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg57685 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'i>$v{}4%*x-LC]a35s)h\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg57694_failAssert131() throws Exception {
        try {
            String __DSPOT_name_6113 = "W.S,fj0iCZ:ZUUd?P#f]";
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
            meta.metaClassForProperty(__DSPOT_name_6113);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg57694 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'W.S,fj0iCZ:ZUUd?P#f]\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString57639() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistancelitString57639__5 = meta.hasSetter("richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__5);
        boolean o_shouldCheckSetterExistancelitString57639__6 = meta.hasSetter("richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__6);
        boolean o_shouldCheckSetterExistancelitString57639__7 = meta.hasSetter("richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__7);
        boolean o_shouldCheckSetterExistancelitString57639__8 = meta.hasSetter("richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__8);
        boolean o_shouldCheckSetterExistancelitString57639__9 = meta.hasSetter("richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__9);
        boolean o_shouldCheckSetterExistancelitString57639__10 = meta.hasSetter("richType");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__10);
        boolean o_shouldCheckSetterExistancelitString57639__11 = meta.hasSetter("richType.richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__11);
        boolean o_shouldCheckSetterExistancelitString57639__12 = meta.hasSetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__12);
        boolean o_shouldCheckSetterExistancelitString57639__13 = meta.hasSetter("richType.richL7st");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString57639__13);
        boolean o_shouldCheckSetterExistancelitString57639__14 = meta.hasSetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__14);
        boolean o_shouldCheckSetterExistancelitString57639__15 = meta.hasSetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__15);
        boolean o_shouldCheckSetterExistancelitString57639__16 = meta.hasSetter("[0]");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString57639__16);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__5);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__6);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__7);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__8);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__9);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__10);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__11);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__12);
        Assert.assertFalse(o_shouldCheckSetterExistancelitString57639__13);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__14);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57639__15);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancelitString57623() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckSetterExistancelitString57623__5 = meta.hasSetter("richField");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__5);
        boolean o_shouldCheckSetterExistancelitString57623__6 = meta.hasSetter("richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__6);
        boolean o_shouldCheckSetterExistancelitString57623__7 = meta.hasSetter("richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__7);
        boolean o_shouldCheckSetterExistancelitString57623__8 = meta.hasSetter("richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__8);
        boolean o_shouldCheckSetterExistancelitString57623__9 = meta.hasSetter("richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__9);
        boolean o_shouldCheckSetterExistancelitString57623__10 = meta.hasSetter("richType");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__10);
        boolean o_shouldCheckSetterExistancelitString57623__11 = meta.hasSetter("rich8ype.richField");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString57623__11);
        boolean o_shouldCheckSetterExistancelitString57623__12 = meta.hasSetter("richType.richProperty");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__12);
        boolean o_shouldCheckSetterExistancelitString57623__13 = meta.hasSetter("richType.richList");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__13);
        boolean o_shouldCheckSetterExistancelitString57623__14 = meta.hasSetter("richType.richMap");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__14);
        boolean o_shouldCheckSetterExistancelitString57623__15 = meta.hasSetter("richType.richList[0]");
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__15);
        boolean o_shouldCheckSetterExistancelitString57623__16 = meta.hasSetter("[0]");
        Assert.assertFalse(o_shouldCheckSetterExistancelitString57623__16);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__5);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__6);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__7);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__8);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__9);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__10);
        Assert.assertFalse(o_shouldCheckSetterExistancelitString57623__11);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__12);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__13);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__14);
        Assert.assertTrue(o_shouldCheckSetterExistancelitString57623__15);
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistance_mg57688_failAssert129() throws Exception {
        try {
            String __DSPOT_name_6109 = "[!aEJ`pL!!)=<[I<O7u{";
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
            meta.getSetInvoker(__DSPOT_name_6109);
            org.junit.Assert.fail("shouldCheckSetterExistance_mg57688 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'[!aEJ`pL!!)=<[I<O7u{\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancenull57696_failAssert133litString62349() throws Exception {
        try {
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
            meta.hasSetter("");
            meta.hasSetter("richType.richList");
            meta.hasSetter("richType.richMap");
            meta.hasSetter("richType.richList[0]");
            meta.hasSetter("[0]");
            org.junit.Assert.fail("shouldCheckSetterExistancenull57696 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckSetterExistancenull57719_failAssert140null66386null90820() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
            MetaClass meta = null;
            meta.hasSetter("richField");
            meta.hasSetter("richProperty");
            meta.hasSetter("richList");
            meta.hasSetter("richMap");
            meta.hasSetter(null);
            meta.hasSetter("richType");
            meta.hasSetter("richType.richField");
            meta.hasSetter("richType.richProperty");
            meta.hasSetter("richType.richList");
            meta.hasSetter("richType.richMap");
            meta.hasSetter("richType.richList[0]");
            meta.hasSetter("[0]");
            org.junit.Assert.fail("shouldCheckSetterExistancenull57719 should have thrown NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetternull98449() throws Exception {
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
    public void shouldCheckTypeForEachGetterlitString98339_failAssert173() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("[itTEN8");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("richType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString98339 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString98313_failAssert149() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("ricNhField");
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
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString98313 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'ricNhField\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg98416_failAssert227() throws Exception {
        try {
            String __DSPOT_name_9179 = " xL89JOU.YCW?LNB(*+*";
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
            meta.getSetInvoker(__DSPOT_name_9179);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg98416 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \' xL89JOU.YCW?LNB(*+*\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetterlitString98360_failAssert190() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType("richField");
            meta.getGetterType("richProperty");
            meta.getGetterType("richList");
            meta.getGetterType("richMap");
            meta.getGetterType("richList[0]");
            meta.getGetterType("richType");
            meta.getGetterType("riShType.richField");
            meta.getGetterType("richType.richProperty");
            meta.getGetterType("richType.richList");
            meta.getGetterType("richType.richMap");
            meta.getGetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachGetterlitString98360 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'riShType\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg98418_failAssert228() throws Exception {
        try {
            String __DSPOT_name_9180 = ")=i_)zxDe:&<K=LiE1-t";
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
            meta.getSetterType(__DSPOT_name_9180);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg98418 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \')=i_)zxDe:&<K=LiE1-t\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg98413_failAssert225() throws Exception {
        try {
            String __DSPOT_name_9177 = "37+()2Q9+##>(w}/),&v";
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
            meta.getGetInvoker(__DSPOT_name_9177);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_mg98413 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'37+()2Q9+##>(w}/),&v\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_mg98411() throws Exception {
        String __DSPOT_name_9174 = "!d*H0P(x `Z?4=:W.7]*";
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
        meta.findProperty(__DSPOT_name_9174);
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_mg164905_failAssert313() throws Exception {
        try {
            String __DSPOT_name_21807 = "[V$&W8u?cdO%aI0w]D)*";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachGetter_add98399__3.getGetInvoker(__DSPOT_name_21807);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_add98399_mg164905 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'[V$&W8u?cdO%aI0w]D)*\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_mg164953() throws Exception {
        String __DSPOT_name_21826 = "sb.$3GpXIzoP2U+#%e@#";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399__3)).hasDefaultConstructor());
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
        boolean o_shouldCheckTypeForEachGetter_add98399_mg164953__20 = meta.hasGetter(__DSPOT_name_21826);
        Assert.assertFalse(o_shouldCheckTypeForEachGetter_add98399_mg164953__20);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_mg164936_failAssert321() throws Exception {
        try {
            String __DSPOT_name_21820 = "C-fH6KD^1$L YT<@@aZ ";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            meta.getGetterType(__DSPOT_name_21820);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_add98399_mg164936 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'C-fH6KD^1$L YT<@@aZ \' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_mg164957() throws Exception {
        String __DSPOT_name_21827 = "MN>V5O)7G5pV_^CY.BAh";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399__3)).hasDefaultConstructor());
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
        boolean o_shouldCheckTypeForEachGetter_add98399_mg164957__20 = meta.hasSetter(__DSPOT_name_21827);
        Assert.assertFalse(o_shouldCheckTypeForEachGetter_add98399_mg164957__20);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_mg164915_failAssert305() throws Exception {
        try {
            String __DSPOT_name_21810 = "!2]!Odyz<P=QAD<!hNvG";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachGetter_add98399__3.getSetterType(__DSPOT_name_21810);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_add98399_mg164915 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'!2]!Odyz<P=QAD<!hNvG\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_mg164911_failAssert294() throws Exception {
        try {
            String __DSPOT_name_21809 = "Zo*ob[2Z4xNdLU&Q{R=?";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachGetter_add98399__3.getSetInvoker(__DSPOT_name_21809);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_add98399_mg164911 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'Zo*ob[2Z4xNdLU&Q{R=?\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_rv166320() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getGetterType("richField");
        meta.getGetterType("richProperty");
        meta.getGetterType("richList");
        meta.getGetterType("richMap");
        meta.getGetterType("richList[0]");
        Class<?> __DSPOT_invoc_22 = meta.getGetterType("richType");
        meta.getGetterType("richType.richField");
        meta.getGetterType("richType.richProperty");
        meta.getGetterType("richType.richList");
        meta.getGetterType("richType.richMap");
        meta.getGetterType("richType.richList[0]");
        __DSPOT_invoc_22.getEnclosingConstructor();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_mg164927() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_21817 = true;
        String __DSPOT_name_21816 = "Y.%y@J8tFhG:|Vl7y}6&";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399__3)).hasDefaultConstructor());
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
        meta.findProperty(__DSPOT_name_21816, __DSPOT_useCamelCaseMapping_21817);
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_add164794_mg335093_failAssert413() throws Exception {
        try {
            String __DSPOT_name_54524 = "w?.[3xYZR(rC-Mt$c]B!";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckTypeForEachGetter_add98399_add164794__6 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachGetter_add98399_add164794__6.getSetterType(__DSPOT_name_54524);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_add98399_add164794_mg335093 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'w?\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_add164794_mg335054() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_54508 = false;
        String __DSPOT_name_54507 = "d4&g&zCyW]7#7z;z.FV{";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399__3)).hasDefaultConstructor());
        MetaClass o_shouldCheckTypeForEachGetter_add98399_add164794__6 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399_add164794__6)).hasDefaultConstructor());
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
        o_shouldCheckTypeForEachGetter_add98399__3.findProperty(__DSPOT_name_54507, __DSPOT_useCamelCaseMapping_54508);
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_add164791_mg338465_failAssert395() throws Exception {
        try {
            String __DSPOT_name_55168 = "5gU`S`hGCR[hb;d^w{-v";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachGetter_add98399_add164791__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachGetter_add98399_add164791__3.getSetterType(__DSPOT_name_55168);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_add98399_add164791_mg338465 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'5gU`S`hGCR\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_add164794_rv337215() throws Exception {
        Object __DSPOT_arg0_54963 = new Object();
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399__3)).hasDefaultConstructor());
        MetaClass o_shouldCheckTypeForEachGetter_add98399_add164794__6 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399_add164794__6)).hasDefaultConstructor());
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
        Class<?> __DSPOT_invoc_33 = meta.getGetterType("richType.richList[0]");
        boolean o_shouldCheckTypeForEachGetter_add98399_add164794_rv337215__26 = __DSPOT_invoc_33.isInstance(__DSPOT_arg0_54963);
        Assert.assertFalse(o_shouldCheckTypeForEachGetter_add98399_add164794_rv337215__26);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399_add164794__6)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_add164794_mg335089_failAssert418() throws Exception {
        try {
            String __DSPOT_name_54522 = "{tE3_e$R%##Yi:?SN:;l";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckTypeForEachGetter_add98399_add164794__6 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachGetter_add98399_add164794__6.getSetInvoker(__DSPOT_name_54522);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_add98399_add164794_mg335089 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'{tE3_e$R%##Yi:?SN:;l\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_add164794_mg335084_failAssert410() throws Exception {
        try {
            String __DSPOT_name_54520 = "K;fOm7,eqe9Cq?dNg=*]";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckTypeForEachGetter_add98399_add164794__6 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachGetter_add98399_add164794__6.getGetInvoker(__DSPOT_name_54520);
            org.junit.Assert.fail("shouldCheckTypeForEachGetter_add98399_add164794_mg335084 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'K;fOm7,eqe9Cq?dNg=*]\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachGetter_add98399_add164794_mg335105() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_54530 = true;
        String __DSPOT_name_54529 = "Fiuob`1|$C$4n<#t#6py";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckTypeForEachGetter_add98399__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399__3)).hasDefaultConstructor());
        MetaClass o_shouldCheckTypeForEachGetter_add98399_add164794__6 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachGetter_add98399_add164794__6)).hasDefaultConstructor());
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
        meta.findProperty(__DSPOT_name_54529, __DSPOT_useCamelCaseMapping_54530);
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetternull362423() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getSetterType("richField");
        meta.getSetterType("richProperty");
        meta.getSetterType("richList");
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
    public void shouldCheckTypeForEachSetter_mg362394_failAssert506() throws Exception {
        try {
            String __DSPOT_name_59743 = "|=_!:bf[cAyO{.SY )9l";
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
            meta.getSetInvoker(__DSPOT_name_59743);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_mg362394 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'|=_!:bf[cAyO{.SY )9l\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_mg362390() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_59740 = true;
        String __DSPOT_name_59739 = "QMnz?stqt`?U.}go+5H2";
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
        meta.findProperty(__DSPOT_name_59739, __DSPOT_useCamelCaseMapping_59740);
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString362327_failAssert460() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("richField");
            meta.getSetterType("richProperty");
            meta.getSetterType("richList");
            meta.getSetterType("richMap");
            meta.getSetterType("\n");
            meta.getSetterType("richType");
            meta.getSetterType("richType.richField");
            meta.getSetterType("richType.richProperty");
            meta.getSetterType("richType.richList");
            meta.getSetterType("richType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString362327 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'\n\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString362289_failAssert426() throws Exception {
        try {
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetterType("[0]");
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
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString362289 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_mg362391_failAssert504() throws Exception {
        try {
            String __DSPOT_name_59741 = "!v!{f*oAMbkd01MXr[&A";
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
            meta.getGetInvoker(__DSPOT_name_59741);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_mg362391 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'!v!{f*oAMbkd01MXr[&A\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetterlitString362362_failAssert490() throws Exception {
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
            meta.getSetterType("richType.richList");
            meta.getSetterType("r.chType.richMap");
            meta.getSetterType("richType.richList[0]");
            org.junit.Assert.fail("shouldCheckTypeForEachSetterlitString362362 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'r\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add362377_rv423239() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckTypeForEachSetter_add362377__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachSetter_add362377__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.getSetterType("richField");
        meta.getSetterType("richProperty");
        meta.getSetterType("richList");
        meta.getSetterType("richMap");
        meta.getSetterType("richList[0]");
        meta.getSetterType("richType");
        meta.getSetterType("richType.richField");
        Class<?> __DSPOT_invoc_24 = meta.getSetterType("richType.richProperty");
        meta.getSetterType("richType.richList");
        meta.getSetterType("richType.richMap");
        meta.getSetterType("richType.richList[0]");
        __DSPOT_invoc_24.getAnnotatedInterfaces();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachSetter_add362377__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add362377_mg421738_failAssert597() throws Exception {
        try {
            String __DSPOT_name_70929 = "2_1m|wSLu[`sR.^7Ix}z";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachSetter_add362377__3 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            meta.getSetterType(__DSPOT_name_70929);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_add362377_mg421738 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'2_1m|wSLu\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add362377_mg421698_failAssert569() throws Exception {
        try {
            String __DSPOT_name_70920 = "+]>_N1&LD:H+mW9-F#Ut";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachSetter_add362377__3 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachSetter_add362377__3.metaClassForProperty(__DSPOT_name_70920);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_add362377_mg421698 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'+]>_N1&LD:H+mW9-F#Ut\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add362377_mg421658_failAssert580() throws Exception {
        try {
            String __DSPOT_name_70913 = "yB$6h,h5Yv-Dna/NrE-=";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachSetter_add362377__3 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachSetter_add362377__3.getGetInvoker(__DSPOT_name_70913);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_add362377_mg421658 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'yB$6h,h5Yv-Dna/NrE-=\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add362377_mg421723_failAssert599() throws Exception {
        try {
            String __DSPOT_name_70927 = "A(&.flPCT F}|p|I7ZaD";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachSetter_add362377__3 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            meta.getGetterType(__DSPOT_name_70927);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_add362377_mg421723 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'A(&\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add362377_mg421670_failAssert594() throws Exception {
        try {
            String __DSPOT_name_70916 = "wf!=w9$F,U<?65!r*5dA";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachSetter_add362377__3 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachSetter_add362377__3.getSetInvoker(__DSPOT_name_70916);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_add362377_mg421670 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'wf!=w9$F,U<?65!r*5dA\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add362377_mg421694() throws Exception {
        String __DSPOT_name_70919 = "/d_bF#&05,0 .5&Sh_/o";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckTypeForEachSetter_add362377__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachSetter_add362377__3)).hasDefaultConstructor());
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
        boolean o_shouldCheckTypeForEachSetter_add362377_mg421694__20 = o_shouldCheckTypeForEachSetter_add362377__3.hasSetter(__DSPOT_name_70919);
        Assert.assertFalse(o_shouldCheckTypeForEachSetter_add362377_mg421694__20);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachSetter_add362377__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add362377_add421554_mg596904_failAssert669() throws Exception {
        try {
            String __DSPOT_name_104840 = "*o/zkc#8 %d)`SY%L8X;";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachSetter_add362377__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckTypeForEachSetter_add362377_add421554__6 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachSetter_add362377_add421554__6.metaClassForProperty(__DSPOT_name_104840);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_add362377_add421554_mg596904 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'*o/zkc#8 %d)`SY%L8X;\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add362377_add421554_mg596903() throws Exception {
        String __DSPOT_name_104839 = "O^-X}-(9Wb@n7U< SQ{*";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckTypeForEachSetter_add362377__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachSetter_add362377__3)).hasDefaultConstructor());
        MetaClass o_shouldCheckTypeForEachSetter_add362377_add421554__6 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachSetter_add362377_add421554__6)).hasDefaultConstructor());
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
        boolean o_shouldCheckTypeForEachSetter_add362377_add421554_mg596903__23 = o_shouldCheckTypeForEachSetter_add362377_add421554__6.hasSetter(__DSPOT_name_104839);
        Assert.assertFalse(o_shouldCheckTypeForEachSetter_add362377_add421554_mg596903__23);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachSetter_add362377__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachSetter_add362377_add421554__6)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add362377_add421554_mg596866_failAssert704() throws Exception {
        try {
            String __DSPOT_name_104825 = "gqUe6CvAmVuwEbf!u1{!";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachSetter_add362377__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckTypeForEachSetter_add362377_add421554__6 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachSetter_add362377__3.getSetterType(__DSPOT_name_104825);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_add362377_add421554_mg596866 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'gqUe6CvAmVuwEbf!u1{!\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add362377_add421554_mg596861_failAssert705() throws Exception {
        try {
            String __DSPOT_name_104823 = "d^W`pdcUSgO=7kE^Yja=";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachSetter_add362377__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckTypeForEachSetter_add362377_add421554__6 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachSetter_add362377__3.getSetInvoker(__DSPOT_name_104823);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_add362377_add421554_mg596861 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'d^W`pdcUSgO=7kE^Yja=\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add362377_add421554_mg596880() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_104832 = true;
        String __DSPOT_name_104831 = "o@8!,&J;%][-#?q$q`sK";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckTypeForEachSetter_add362377__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachSetter_add362377__3)).hasDefaultConstructor());
        MetaClass o_shouldCheckTypeForEachSetter_add362377_add421554__6 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckTypeForEachSetter_add362377_add421554__6)).hasDefaultConstructor());
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
        o_shouldCheckTypeForEachSetter_add362377_add421554__6.findProperty(__DSPOT_name_104831, __DSPOT_useCamelCaseMapping_104832);
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add362377_add421554_mg596883_failAssert697() throws Exception {
        try {
            String __DSPOT_name_104833 = "#`F_6V];7Epnht5QI2H5";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachSetter_add362377__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckTypeForEachSetter_add362377_add421554__6 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachSetter_add362377_add421554__6.getGetInvoker(__DSPOT_name_104833);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_add362377_add421554_mg596883 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'#`F_6V];7Epnht5QI2H5\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckTypeForEachSetter_add362377_add421554_mg596887_failAssert679() throws Exception {
        try {
            String __DSPOT_name_104834 = "zbw-W5<z=MT]kk:c.@`U";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckTypeForEachSetter_add362377__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckTypeForEachSetter_add362377_add421554__6 = MetaClass.forClass(RichType.class, reflectorFactory);
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
            o_shouldCheckTypeForEachSetter_add362377_add421554__6.getGetterType(__DSPOT_name_104834);
            org.junit.Assert.fail("shouldCheckTypeForEachSetter_add362377_add421554_mg596887 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'zbw-W5<z=MT]kk:c\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNamesnull17() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = null;
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg8_failAssert1() throws Exception {
        try {
            String __DSPOT_name_4 = "UgIvC=TU&zgYc TM1`_8";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType(__DSPOT_name_4);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg8 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'UgIvC=TU&zgYc TM1`_8\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
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
    public void shouldCheckGetterAndSetterNames_mg14_mg949_failAssert40() throws Exception {
        try {
            String __DSPOT_name_275 = "j57v[dc=WO=QzF5*<#D<";
            String __DSPOT_name_8 = "wpauR%h1,xavU[1Rvnj|";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterAndSetterNames_mg14__6 = meta.hasSetter(__DSPOT_name_8);
            meta.getGetInvoker(__DSPOT_name_275);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg14_mg949 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'j57v[dc=WO=QzF5*<#D<\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg13_mg360_failAssert43() throws Exception {
        try {
            String __DSPOT_name_94 = "jN$:t]rwVX^,i9Clv-<D";
            String __DSPOT_name_7 = "^FT)-ef&bk*201yCi*Od";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterAndSetterNames_mg13__6 = meta.hasGetter(__DSPOT_name_7);
            meta.getSetterType(__DSPOT_name_94);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_mg13_mg360 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'jN$:t]rwVX^,i9Clv-<D\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_mg1096_failAssert14() throws Exception {
        try {
            String __DSPOT_name_317 = "@OskRx;om6_QO0c5a.U(";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getGetterType(__DSPOT_name_317);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add1_mg1096 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'@OskRx;om6_QO0c5a\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_mg13_mg321() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_73 = true;
        String __DSPOT_name_72 = "(!P:(01(Vo/][%sGMuXX";
        String __DSPOT_name_7 = "^FT)-ef&bk*201yCi*Od";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterAndSetterNames_mg13__6 = meta.hasGetter(__DSPOT_name_7);
        meta.findProperty(__DSPOT_name_72, __DSPOT_useCamelCaseMapping_73);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_mg1111_failAssert13() throws Exception {
        try {
            String __DSPOT_name_322 = "[ `8U^L|U2^w&]RipC8T";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.metaClassForProperty(__DSPOT_name_322);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add1_mg1111 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'[ `8U^L|U2^w&]RipC8T\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNamesnull17_add1063() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        ((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled();
        MetaClass meta = null;
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNamesnull28null302() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_mg1040_failAssert12() throws Exception {
        try {
            String __DSPOT_name_296 = "!Lb^R/Cg|3RC!0fPw#b@";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            o_shouldCheckGetterAndSetterNames_add1__3.getSetInvoker(__DSPOT_name_296);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add1_mg1040 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'!Lb^R/Cg|3RC!0fPw#b@\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_mg1059_mg8052() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_2323 = false;
        String __DSPOT_name_2322 = "iB37nt^W7C$ke.`p(S-7";
        String __DSPOT_name_311 = "R@z<e}*1gCLA [,68H-;";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckGetterAndSetterNames_add1__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterAndSetterNames_add1_mg1059__9 = o_shouldCheckGetterAndSetterNames_add1__3.hasSetter(__DSPOT_name_311);
        meta.findProperty(__DSPOT_name_2322, __DSPOT_useCamelCaseMapping_2323);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_add993_mg8535_failAssert54() throws Exception {
        try {
            String __DSPOT_name_2485 = "P{(}2((`;Ot`.U2{`rpI";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckGetterAndSetterNames_add1_add993__6 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            o_shouldCheckGetterAndSetterNames_add1_add993__6.getGetterType(__DSPOT_name_2485);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add1_add993_mg8535 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'P{(}2((`;Ot`\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_add990_mg8900_failAssert61() throws Exception {
        try {
            String __DSPOT_name_2610 = "[{tx#QNA47V6kyJppngg";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckGetterAndSetterNames_add1_add990__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            o_shouldCheckGetterAndSetterNames_add1__3.getGetterType(__DSPOT_name_2610);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add1_add990_mg8900 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_add993null8715() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckGetterAndSetterNames_add1__3)).hasDefaultConstructor());
        MetaClass o_shouldCheckGetterAndSetterNames_add1_add993__6 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckGetterAndSetterNames_add1_add993__6)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldCheckGetterAndSetterNames_add1__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (o_shouldCheckGetterAndSetterNames_add1_add993__6)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_add993_mg8565() throws Exception {
        String __DSPOT_name_2493 = "T.qhVC;5OOmgcF,+yW!m";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckGetterAndSetterNames_add1__3)).hasDefaultConstructor());
        MetaClass o_shouldCheckGetterAndSetterNames_add1_add993__6 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckGetterAndSetterNames_add1_add993__6)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        boolean o_shouldCheckGetterAndSetterNames_add1_add993_mg8565__12 = o_shouldCheckGetterAndSetterNames_add1_add993__6.hasSetter(__DSPOT_name_2493);
        Assert.assertFalse(o_shouldCheckGetterAndSetterNames_add1_add993_mg8565__12);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldCheckGetterAndSetterNames_add1__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (o_shouldCheckGetterAndSetterNames_add1_add993__6)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_add993_mg8390_failAssert67() throws Exception {
        try {
            String __DSPOT_name_2434 = "V_^)G<E=/}?2G^}2<85)";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckGetterAndSetterNames_add1_add993__6 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            o_shouldCheckGetterAndSetterNames_add1__3.getGetInvoker(__DSPOT_name_2434);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add1_add993_mg8390 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'V_^)G<E=/}?2G^}2<85)\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_add990_mg8929_failAssert73() throws Exception {
        try {
            String __DSPOT_name_2627 = "D:]|a>pkfp&yfH)B>I5k";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckGetterAndSetterNames_add1_add990__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.getSetInvoker(__DSPOT_name_2627);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add1_add990_mg8929 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'D:]|a>pkfp&yfH)B>I5k\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNamesnull23null671null4952() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = null;
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_add990_mg8906_failAssert63() throws Exception {
        try {
            String __DSPOT_name_2613 = ".A%r{8[eyCgeumu4>mG0";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckGetterAndSetterNames_add1_add990__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            o_shouldCheckGetterAndSetterNames_add1__3.getSetterType(__DSPOT_name_2613);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add1_add990_mg8906 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_add993_mg8571_failAssert50() throws Exception {
        try {
            String __DSPOT_name_2497 = "m(5Ht;27qSf0X7%YEO9.";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckGetterAndSetterNames_add1_add993__6 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            o_shouldCheckGetterAndSetterNames_add1_add993__6.metaClassForProperty(__DSPOT_name_2497);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add1_add993_mg8571 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'m(5Ht;27qSf0X7%YEO9.\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_mg1059_mg8016_failAssert83() throws Exception {
        try {
            String __DSPOT_name_2306 = "}J+jYAtV,@OFp:}EGHm}";
            String __DSPOT_name_311 = "R@z<e}*1gCLA [,68H-;";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            boolean o_shouldCheckGetterAndSetterNames_add1_mg1059__9 = o_shouldCheckGetterAndSetterNames_add1__3.hasSetter(__DSPOT_name_311);
            o_shouldCheckGetterAndSetterNames_add1__3.getSetterType(__DSPOT_name_2306);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add1_mg1059_mg8016 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'}J+jYAtV,@OFp:}EGHm}\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_add990_mg8882_failAssert53() throws Exception {
        try {
            String __DSPOT_name_2601 = "y5;n*jni{0XQN|PGQ&.o";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckGetterAndSetterNames_add1_add990__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            o_shouldCheckGetterAndSetterNames_add1_add990__3.getSetterType(__DSPOT_name_2601);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add1_add990_mg8882 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'y5;n*jni{0XQN|PGQ&\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_add993_mg8580() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_2503 = true;
        String __DSPOT_name_2502 = "IgOML>%M?QCq3_YV^]f`";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckGetterAndSetterNames_add1__3)).hasDefaultConstructor());
        MetaClass o_shouldCheckGetterAndSetterNames_add1_add993__6 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldCheckGetterAndSetterNames_add1_add993__6)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        meta.findProperty(__DSPOT_name_2502, __DSPOT_useCamelCaseMapping_2503);
    }

    @Test(timeout = 10000)
    public void shouldCheckGetterAndSetterNames_add1_add993_mg8458_failAssert66() throws Exception {
        try {
            String __DSPOT_name_2452 = "8mGW8Fi#CuB_A3$u]u|[";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldCheckGetterAndSetterNames_add1__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldCheckGetterAndSetterNames_add1_add993__6 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            o_shouldCheckGetterAndSetterNames_add1__3.getSetterType(__DSPOT_name_2452);
            org.junit.Assert.fail("shouldCheckGetterAndSetterNames_add1_add993_mg8458 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg626553_failAssert709() throws Exception {
        try {
            String __DSPOT_name_110344 = "]G,:!<*U%u&;Q%cQ:uf5";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            meta.getGetInvoker(__DSPOT_name_110344);
            org.junit.Assert.fail("shouldFindPropertyName_mg626553 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \']G,:!<*U%u&;Q%cQ:uf5\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg626562_failAssert713() throws Exception {
        try {
            String __DSPOT_name_110350 = "@R_)o4[vH!()6x+(`)8C";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            meta.metaClassForProperty(__DSPOT_name_110350);
            org.junit.Assert.fail("shouldFindPropertyName_mg626562 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'@R_)o4[vH!()6x+(`)8C\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamenull626570() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg626556_failAssert711() throws Exception {
        try {
            String __DSPOT_name_110346 = "s)]Fn[%5gR12cC}9-XmB";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            meta.getSetInvoker(__DSPOT_name_110346);
            org.junit.Assert.fail("shouldFindPropertyName_mg626556 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'s)]Fn[%5gR12cC}9-XmB\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg626552_failAssert708() throws Exception {
        try {
            boolean __DSPOT_useCamelCaseMapping_110343 = true;
            String __DSPOT_name_110342 = "Qp5?NM(e@?| 5LS37t/[";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            meta.findProperty(__DSPOT_name_110342, __DSPOT_useCamelCaseMapping_110343);
            org.junit.Assert.fail("shouldFindPropertyName_mg626552 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg626558_failAssert712() throws Exception {
        try {
            String __DSPOT_name_110347 = "1.SGpe$r_:;Aw3O@I>tZ";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            meta.findProperty("RICHfield");
            meta.getSetterType(__DSPOT_name_110347);
            org.junit.Assert.fail("shouldFindPropertyName_mg626558 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'1\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626550_mg626858_failAssert749() throws Exception {
        try {
            String __DSPOT_name_110407 = "}4lyC):W-*GN9FyJgKS6";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_add626550__5 = meta.findProperty("RICHfield");
            String o_shouldFindPropertyName_add626550__6 = meta.findProperty("RICHfield");
            meta.getSetterType(__DSPOT_name_110407);
            org.junit.Assert.fail("shouldFindPropertyName_add626550_mg626858 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'}4lyC):W-*GN9FyJgKS6\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626549null627470() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldFindPropertyName_add626549__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_add626549__6 = null;
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626550_mg626845_failAssert748() throws Exception {
        try {
            String __DSPOT_name_110405 = "?.mt0H6%nr^(}[G0eIr!";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_add626550__5 = meta.findProperty("RICHfield");
            String o_shouldFindPropertyName_add626550__6 = meta.findProperty("RICHfield");
            meta.getGetterType(__DSPOT_name_110405);
            org.junit.Assert.fail("shouldFindPropertyName_add626550_mg626845 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'?\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg626561_mg627862_failAssert740() throws Exception {
        try {
            String __DSPOT_name_110670 = "(l5tqVh3;A(9A)s<}*Ul";
            String __DSPOT_name_110349 = "YCFt}zF Tj;YzA[p9|sO";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_mg626561__6 = meta.findProperty("RICHfield");
            boolean o_shouldFindPropertyName_mg626561__7 = meta.hasSetter(__DSPOT_name_110349);
            meta.getSetInvoker(__DSPOT_name_110670);
            org.junit.Assert.fail("shouldFindPropertyName_mg626561_mg627862 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'(l5tqVh3;A(9A)s<}*Ul\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626550_mg626836() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_110403 = false;
        String __DSPOT_name_110402 = "jC9N#6=ooD}9! .@bT`H";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_add626550__5 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_add626550__5);
        String o_shouldFindPropertyName_add626550__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_add626550__6);
        meta.findProperty(__DSPOT_name_110402, __DSPOT_useCamelCaseMapping_110403);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626550_mg626871_failAssert751() throws Exception {
        try {
            String __DSPOT_name_110409 = "v5F_GFa3&+YAke#gTam[";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_add626550__5 = meta.findProperty("RICHfield");
            String o_shouldFindPropertyName_add626550__6 = meta.findProperty("RICHfield");
            meta.hasGetter(__DSPOT_name_110409);
            org.junit.Assert.fail("shouldFindPropertyName_add626550_mg626871 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626550_mg626877_failAssert746() throws Exception {
        try {
            String __DSPOT_name_110414 = "Bropw]fg#_Hi_@^jlh;!";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_add626550__5 = meta.findProperty("RICHfield");
            String o_shouldFindPropertyName_add626550__6 = meta.findProperty("RICHfield");
            meta.metaClassForProperty(__DSPOT_name_110414);
            org.junit.Assert.fail("shouldFindPropertyName_add626550_mg626877 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'Bropw]fg#_Hi_@^jlh;!\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamenull626570null626686() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = null;
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg626561_mg627843() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_110666 = true;
        String __DSPOT_name_110665 = ",ANN^K|-T}.1F,K#nHWy";
        String __DSPOT_name_110349 = "YCFt}zF Tj;YzA[p9|sO";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg626561__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg626561__6);
        boolean o_shouldFindPropertyName_mg626561__7 = meta.hasSetter(__DSPOT_name_110349);
        meta.findProperty(__DSPOT_name_110665, __DSPOT_useCamelCaseMapping_110666);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_mg626561_mg627876() throws Exception {
        String __DSPOT_name_110672 = "A,U<N8ycZz#B_^.iq.hB";
        String __DSPOT_name_110349 = "YCFt}zF Tj;YzA[p9|sO";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_mg626561__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg626561__6);
        boolean o_shouldFindPropertyName_mg626561__7 = meta.hasSetter(__DSPOT_name_110349);
        boolean o_shouldFindPropertyName_mg626561_mg627876__13 = meta.hasGetter(__DSPOT_name_110672);
        Assert.assertFalse(o_shouldFindPropertyName_mg626561_mg627876__13);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richField", o_shouldFindPropertyName_mg626561__6);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626549_mg627379() throws Exception {
        String __DSPOT_name_110548 = "0)OJ^B3b,J.]Ckhy N(G";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldFindPropertyName_add626549__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_add626549__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_add626549__6);
        boolean o_shouldFindPropertyName_add626549_mg627379__12 = o_shouldFindPropertyName_add626549__3.hasSetter(__DSPOT_name_110548);
        Assert.assertFalse(o_shouldFindPropertyName_add626549_mg627379__12);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richField", o_shouldFindPropertyName_add626549__6);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626549_mg627372_failAssert721() throws Exception {
        try {
            String __DSPOT_name_110546 = "PuWN6^Eo ].[(G:)(;^|";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldFindPropertyName_add626549__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_add626549__6 = meta.findProperty("RICHfield");
            o_shouldFindPropertyName_add626549__3.getSetterType(__DSPOT_name_110546);
            org.junit.Assert.fail("shouldFindPropertyName_add626549_mg627372 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'PuWN6^Eo ]\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626550_mg626838_failAssert747() throws Exception {
        try {
            String __DSPOT_name_110404 = "awBid;xM!DTQXd:[lo5|";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_add626550__5 = meta.findProperty("RICHfield");
            String o_shouldFindPropertyName_add626550__6 = meta.findProperty("RICHfield");
            meta.getGetInvoker(__DSPOT_name_110404);
            org.junit.Assert.fail("shouldFindPropertyName_add626550_mg626838 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'awBid;xM!DTQXd:[lo5|\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626549_add627329_mg633385_failAssert758() throws Exception {
        try {
            String __DSPOT_name_112117 = "V]-%{vyc#rKE&}_WK$t:";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldFindPropertyName_add626549__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldFindPropertyName_add626549_add627329__6 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_add626549__6 = meta.findProperty("RICHfield");
            o_shouldFindPropertyName_add626549_add627329__6.getGetterType(__DSPOT_name_112117);
            org.junit.Assert.fail("shouldFindPropertyName_add626549_add627329_mg633385 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'V]-%{vyc#rKE&}_WK$t:\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626549_add627329_mg633425_failAssert752() throws Exception {
        try {
            boolean __DSPOT_useCamelCaseMapping_112133 = true;
            String __DSPOT_name_112132 = "4=l#5JwXK(%:uJ=*6(,[";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldFindPropertyName_add626549__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldFindPropertyName_add626549_add627329__6 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_add626549__6 = meta.findProperty("RICHfield");
            meta.findProperty(__DSPOT_name_112132, __DSPOT_useCamelCaseMapping_112133);
            org.junit.Assert.fail("shouldFindPropertyName_add626549_add627329_mg633425 should have thrown StringIndexOutOfBoundsException");
        } catch (StringIndexOutOfBoundsException expected) {
            Assert.assertEquals("String index out of range: -1", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626549_mg627379_mg633016() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_112011 = true;
        String __DSPOT_name_112010 = "j8#y:>p|`{w87LLw3VJ0";
        String __DSPOT_name_110548 = "0)OJ^B3b,J.]Ckhy N(G";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldFindPropertyName_add626549__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_add626549__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_add626549__6);
        boolean o_shouldFindPropertyName_add626549_mg627379__12 = o_shouldFindPropertyName_add626549__3.hasSetter(__DSPOT_name_110548);
        o_shouldFindPropertyName_add626549__3.findProperty(__DSPOT_name_112010, __DSPOT_useCamelCaseMapping_112011);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626549_add627329_mg633363_failAssert775() throws Exception {
        try {
            String __DSPOT_name_112101 = "NxP8(7,:J[;*=<6uR|/W";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldFindPropertyName_add626549__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldFindPropertyName_add626549_add627329__6 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_add626549__6 = meta.findProperty("RICHfield");
            o_shouldFindPropertyName_add626549__3.getSetterType(__DSPOT_name_112101);
            org.junit.Assert.fail("shouldFindPropertyName_add626549_add627329_mg633363 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'NxP8(7,:J\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626549_add627325_mg633819() throws Exception {
        boolean __DSPOT_useCamelCaseMapping_112247 = true;
        String __DSPOT_name_112246 = "Xkft1am${stDa8/7$NcL";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldFindPropertyName_add626549_add627325__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549_add627325__3)).hasDefaultConstructor());
        MetaClass o_shouldFindPropertyName_add626549__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_add626549__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_add626549__6);
        meta.findProperty(__DSPOT_name_112246, __DSPOT_useCamelCaseMapping_112247);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626549_add627325litString633720() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldFindPropertyName_add626549_add627325__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549_add627325__3)).hasDefaultConstructor());
        MetaClass o_shouldFindPropertyName_add626549__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_add626549__6 = meta.findProperty("9.,}wdPl0");
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626549_add627329_mg633431_failAssert785() throws Exception {
        try {
            String __DSPOT_name_112138 = "(<o#u$Yc5&OG?2BZ)LK&";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldFindPropertyName_add626549__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldFindPropertyName_add626549_add627329__6 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_add626549__6 = meta.findProperty("RICHfield");
            meta.getGetInvoker(__DSPOT_name_112138);
            org.junit.Assert.fail("shouldFindPropertyName_add626549_add627329_mg633431 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'(<o#u$Yc5&OG?2BZ)LK&\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626549_add627329_mg633406() throws Exception {
        String __DSPOT_name_112122 = "$Rw.fG].adAIH/+f(a?c";
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldFindPropertyName_add626549__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549__3)).hasDefaultConstructor());
        MetaClass o_shouldFindPropertyName_add626549_add627329__6 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549_add627329__6)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_add626549__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_add626549__6);
        boolean o_shouldFindPropertyName_add626549_add627329_mg633406__15 = o_shouldFindPropertyName_add626549_add627329__6.hasGetter(__DSPOT_name_112122);
        Assert.assertFalse(o_shouldFindPropertyName_add626549_add627329_mg633406__15);
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549_add627329__6)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richField", o_shouldFindPropertyName_add626549__6);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626549_add627325_mg633821() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass o_shouldFindPropertyName_add626549_add627325__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549_add627325__3)).hasDefaultConstructor());
        MetaClass o_shouldFindPropertyName_add626549__3 = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549__3)).hasDefaultConstructor());
        MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        String o_shouldFindPropertyName_add626549__6 = meta.findProperty("RICHfield");
        Assert.assertEquals("richField", o_shouldFindPropertyName_add626549__6);
        meta.getGetterNames();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549_add627325__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (o_shouldFindPropertyName_add626549__3)).hasDefaultConstructor());
        Assert.assertTrue(((MetaClass) (meta)).hasDefaultConstructor());
        Assert.assertEquals("richField", o_shouldFindPropertyName_add626549__6);
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626549_add627325_mg633822_failAssert778() throws Exception {
        try {
            String __DSPOT_name_112249 = "[}q;WNCokAK=_LU=pqV;";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldFindPropertyName_add626549_add627325__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldFindPropertyName_add626549__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_add626549__6 = meta.findProperty("RICHfield");
            meta.getGetterType(__DSPOT_name_112249);
            org.junit.Assert.fail("shouldFindPropertyName_add626549_add627325_mg633822 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no getter for property named \'\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyName_add626549_add627329_mg633355_failAssert772() throws Exception {
        try {
            String __DSPOT_name_112100 = "G8Y{g0[zMWk!8w*67/}T";
            ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
            MetaClass o_shouldFindPropertyName_add626549__3 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass o_shouldFindPropertyName_add626549_add627329__6 = MetaClass.forClass(RichType.class, reflectorFactory);
            MetaClass meta = MetaClass.forClass(RichType.class, reflectorFactory);
            String o_shouldFindPropertyName_add626549__6 = meta.findProperty("RICHfield");
            o_shouldFindPropertyName_add626549__3.getSetInvoker(__DSPOT_name_112100);
            org.junit.Assert.fail("shouldFindPropertyName_add626549_add627329_mg633355 should have thrown ReflectionException");
        } catch (ReflectionException expected) {
            Assert.assertEquals("There is no setter for property named \'G8Y{g0[zMWk!8w*67/}T\' in \'class org.apache.ibatis.domain.misc.RichType\'", expected.getMessage());
        }
    }

    @Test(timeout = 10000)
    public void shouldFindPropertyNamenull626570null626686null634385() throws Exception {
        ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
        Assert.assertTrue(((DefaultReflectorFactory) (reflectorFactory)).isClassCacheEnabled());
        MetaClass meta = null;
    }
}

