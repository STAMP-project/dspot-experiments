package com.blankj.utilcode.util.reflect;


import com.blankj.utilcode.util.ReflectUtils;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.blankj.utilcode.util.reflect.Test2.ConstructorType.INTEGER;
import static com.blankj.utilcode.util.reflect.Test2.ConstructorType.NO_ARGS;
import static com.blankj.utilcode.util.reflect.Test2.ConstructorType.NUMBER;
import static com.blankj.utilcode.util.reflect.Test2.ConstructorType.OBJECT;


/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2017/12/15
 *     desc  : ReflectUtils ????
 * </pre>
 */
public class ReflectUtilsTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void reflect() {
        Assert.assertEquals(ReflectUtils.reflect(Object.class), ReflectUtils.reflect("java.lang.Object", ClassLoader.getSystemClassLoader()));
        Assert.assertEquals(ReflectUtils.reflect(Object.class), ReflectUtils.reflect("java.lang.Object"));
        Assert.assertEquals(ReflectUtils.reflect(String.class).get(), ReflectUtils.reflect("java.lang.String").get());
        Assert.assertEquals(Object.class, ReflectUtils.reflect(Object.class).get());
        Assert.assertEquals("abc", ReflectUtils.reflect(((Object) ("abc"))).get());
        Assert.assertEquals(1, ReflectUtils.reflect(1).get());
    }

    @Test
    public void newInstance() {
        Assert.assertEquals("", ReflectUtils.reflect(String.class).newInstance().get());
        Assert.assertEquals("abc", ReflectUtils.reflect(String.class).newInstance("abc").get());
        Assert.assertEquals("abc", ReflectUtils.reflect(String.class).newInstance("abc".getBytes()).get());
        Assert.assertEquals("abc", ReflectUtils.reflect(String.class).newInstance("abc".toCharArray()).get());
        Assert.assertEquals("b", ReflectUtils.reflect(String.class).newInstance("abc".toCharArray(), 1, 1).get());
    }

    @Test
    public void newInstancePrivate() {
        Assert.assertNull(ReflectUtils.reflect(PrivateConstructors.class).newInstance().field("string").get());
        Assert.assertEquals("abc", ReflectUtils.reflect(PrivateConstructors.class).newInstance("abc").field("string").get());
    }

    @Test
    public void newInstanceNull() {
        Test2 test2 = ReflectUtils.reflect(Test2.class).newInstance(((Object) (null))).get();
        Assert.assertNull(test2.n);
    }

    @Test
    public void newInstanceWithPrivate() {
        Test7 t1 = ReflectUtils.reflect(Test7.class).newInstance(1).get();
        Assert.assertEquals(1, ((int) (t1.i)));
        Assert.assertNull(t1.s);
        Test7 t2 = ReflectUtils.reflect(Test7.class).newInstance("a").get();
        Assert.assertNull(t2.i);
        Assert.assertEquals("a", t2.s);
        Test7 t3 = ReflectUtils.reflect(Test7.class).newInstance("a", 1).get();
        Assert.assertEquals(1, ((int) (t3.i)));
        Assert.assertEquals("a", t3.s);
    }

    @Test
    public void newInstanceAmbiguity() {
        Test2 test;
        test = ReflectUtils.reflect(Test2.class).newInstance().get();
        Assert.assertEquals(null, test.n);
        Assert.assertEquals(NO_ARGS, test.constructorType);
        test = ReflectUtils.reflect(Test2.class).newInstance("abc").get();
        Assert.assertEquals("abc", test.n);
        Assert.assertEquals(OBJECT, test.constructorType);
        test = ReflectUtils.reflect(Test2.class).newInstance(new Long("1")).get();
        Assert.assertEquals(1L, test.n);
        Assert.assertEquals(NUMBER, test.constructorType);
        test = ReflectUtils.reflect(Test2.class).newInstance(1).get();
        Assert.assertEquals(1, test.n);
        Assert.assertEquals(INTEGER, test.constructorType);
        test = ReflectUtils.reflect(Test2.class).newInstance('a').get();
        Assert.assertEquals('a', test.n);
        Assert.assertEquals(OBJECT, test.constructorType);
    }

    @Test
    public void method() {
        // instance methods
        Assert.assertEquals("", ReflectUtils.reflect(((Object) (" "))).method("trim").get());
        Assert.assertEquals("12", ReflectUtils.reflect(((Object) (" 12 "))).method("trim").get());
        Assert.assertEquals("34", ReflectUtils.reflect(((Object) ("1234"))).method("substring", 2).get());
        Assert.assertEquals("12", ReflectUtils.reflect(((Object) ("1234"))).method("substring", 0, 2).get());
        Assert.assertEquals("1234", ReflectUtils.reflect(((Object) ("12"))).method("concat", "34").get());
        Assert.assertEquals("123456", ReflectUtils.reflect(((Object) ("12"))).method("concat", "34").method("concat", "56").get());
        Assert.assertEquals(2, ReflectUtils.reflect(((Object) ("1234"))).method("indexOf", "3").get());
        Assert.assertEquals(2.0F, ((float) (ReflectUtils.reflect(((Object) ("1234"))).method("indexOf", "3").method("floatValue").get())), 0.0F);
        Assert.assertEquals("2", ReflectUtils.reflect(((Object) ("1234"))).method("indexOf", "3").method("toString").get());
        // static methods
        Assert.assertEquals("true", ReflectUtils.reflect(String.class).method("valueOf", true).get());
        Assert.assertEquals("1", ReflectUtils.reflect(String.class).method("valueOf", 1).get());
        Assert.assertEquals("abc", ReflectUtils.reflect(String.class).method("valueOf", "abc".toCharArray()).get());
        Assert.assertEquals("abc", ReflectUtils.reflect(String.class).method("copyValueOf", "abc".toCharArray()).get());
        Assert.assertEquals("b", ReflectUtils.reflect(String.class).method("copyValueOf", "abc".toCharArray(), 1, 1).get());
    }

    @Test
    public void methodVoid() {
        // instance methods
        Test4 test4 = new Test4();
        Assert.assertEquals(test4, ReflectUtils.reflect(test4).method("i_method").get());
        // static methods
        Assert.assertEquals(Test4.class, ReflectUtils.reflect(Test4.class).method("s_method").get());
    }

    @Test
    public void methodPrivate() {
        // instance methods
        Test5 test8 = new Test5();
        Assert.assertEquals(test8, ReflectUtils.reflect(test8).method("i_method").get());
        // static methods
        Assert.assertEquals(Test5.class, ReflectUtils.reflect(Test5.class).method("s_method").get());
    }

    @Test
    public void methodNullArguments() {
        Test6 test9 = new Test6();
        ReflectUtils.reflect(test9).method("put", "key", "value");
        Assert.assertTrue(test9.map.containsKey("key"));
        Assert.assertEquals("value", test9.map.get("key"));
        ReflectUtils.reflect(test9).method("put", "key", null);
        Assert.assertTrue(test9.map.containsKey("key"));
        Assert.assertNull(test9.map.get("key"));
    }

    @Test
    public void methodSuper() {
        TestHierarchicalMethodsSubclass subclass = new TestHierarchicalMethodsSubclass();
        Assert.assertEquals(TestHierarchicalMethodsBase.PUBLIC_RESULT, ReflectUtils.reflect(subclass).method("pub_base_method", 1).get());
        Assert.assertEquals(TestHierarchicalMethodsBase.PRIVATE_RESULT, ReflectUtils.reflect(subclass).method("very_priv_method").get());
    }

    @Test
    public void methodDeclaring() {
        TestHierarchicalMethodsSubclass subclass = new TestHierarchicalMethodsSubclass();
        Assert.assertEquals(TestHierarchicalMethodsSubclass.PRIVATE_RESULT, ReflectUtils.reflect(subclass).method("priv_method", 1).get());
        TestHierarchicalMethodsBase baseClass = new TestHierarchicalMethodsBase();
        Assert.assertEquals(TestHierarchicalMethodsBase.PRIVATE_RESULT, ReflectUtils.reflect(baseClass).method("priv_method", 1).get());
    }

    @Test
    public void methodAmbiguity() {
        Test3 test;
        test = ReflectUtils.reflect(Test3.class).newInstance().method("method").get();
        Assert.assertEquals(null, test.n);
        Assert.assertEquals(Test3.MethodType.NO_ARGS, test.methodType);
        test = ReflectUtils.reflect(Test3.class).newInstance().method("method", "abc").get();
        Assert.assertEquals("abc", test.n);
        Assert.assertEquals(Test3.MethodType.OBJECT, test.methodType);
        test = ReflectUtils.reflect(Test3.class).newInstance().method("method", new Long("1")).get();
        Assert.assertEquals(1L, test.n);
        Assert.assertEquals(Test3.MethodType.NUMBER, test.methodType);
        test = ReflectUtils.reflect(Test3.class).newInstance().method("method", 1).get();
        Assert.assertEquals(1, test.n);
        Assert.assertEquals(Test3.MethodType.INTEGER, test.methodType);
        test = ReflectUtils.reflect(Test3.class).newInstance().method("method", 'a').get();
        Assert.assertEquals('a', test.n);
        Assert.assertEquals(Test3.MethodType.OBJECT, test.methodType);
    }

    @Test
    public void field() {
        // instance field
        Test1 test1 = new Test1();
        ReflectUtils.reflect(test1).field("I_INT1", 1);
        Assert.assertEquals(1, ReflectUtils.reflect(test1).field("I_INT1").get());
        ReflectUtils.reflect(test1).field("I_INT2", 1);
        Assert.assertEquals(1, ReflectUtils.reflect(test1).field("I_INT2").get());
        ReflectUtils.reflect(test1).field("I_INT2", null);
        Assert.assertNull(ReflectUtils.reflect(test1).field("I_INT2").get());
        // static field
        ReflectUtils.reflect(Test1.class).field("S_INT1", 1);
        Assert.assertEquals(1, ReflectUtils.reflect(Test1.class).field("S_INT1").get());
        ReflectUtils.reflect(Test1.class).field("S_INT2", 1);
        Assert.assertEquals(1, ReflectUtils.reflect(Test1.class).field("S_INT2").get());
        ReflectUtils.reflect(Test1.class).field("S_INT2", null);
        Assert.assertNull(ReflectUtils.reflect(Test1.class).field("S_INT2").get());
        // hierarchies field
        TestHierarchicalMethodsSubclass test2 = new TestHierarchicalMethodsSubclass();
        ReflectUtils.reflect(test2).field("invisibleField1", 1);
        Assert.assertEquals(1, ReflectUtils.reflect(test2).field("invisibleField1").get());
        ReflectUtils.reflect(test2).field("invisibleField2", 1);
        Assert.assertEquals(1, ReflectUtils.reflect(test2).field("invisibleField2").get());
        ReflectUtils.reflect(test2).field("invisibleField3", 1);
        Assert.assertEquals(1, ReflectUtils.reflect(test2).field("invisibleField3").get());
        ReflectUtils.reflect(test2).field("visibleField1", 1);
        Assert.assertEquals(1, ReflectUtils.reflect(test2).field("visibleField1").get());
        ReflectUtils.reflect(test2).field("visibleField2", 1);
        Assert.assertEquals(1, ReflectUtils.reflect(test2).field("visibleField2").get());
        ReflectUtils.reflect(test2).field("visibleField3", 1);
        Assert.assertEquals(1, ReflectUtils.reflect(test2).field("visibleField3").get());
    }

    @Test
    public void fieldPrivate() {
        class Foo {
            private String bar;
        }
        Foo foo = new Foo();
        ReflectUtils.reflect(foo).field("bar", "FooBar");
        Assert.assertThat(foo.bar, Matchers.is("FooBar"));
        Assert.assertEquals("FooBar", ReflectUtils.reflect(foo).field("bar").get());
        ReflectUtils.reflect(foo).field("bar", null);
        Assert.assertNull(foo.bar);
        Assert.assertNull(ReflectUtils.reflect(foo).field("bar").get());
    }

    @Test
    public void fieldFinal() {
        // instance field
        Test8 test11 = new Test8();
        ReflectUtils.reflect(test11).field("F_INT1", 1);
        Assert.assertEquals(1, ReflectUtils.reflect(test11).field("F_INT1").get());
        ReflectUtils.reflect(test11).field("F_INT2", 1);
        Assert.assertEquals(1, ReflectUtils.reflect(test11).field("F_INT2").get());
        ReflectUtils.reflect(test11).field("F_INT2", null);
        Assert.assertNull(ReflectUtils.reflect(test11).field("F_INT2").get());
        // static field
        ReflectUtils.reflect(Test8.class).field("SF_INT1", 1);
        Assert.assertEquals(1, ReflectUtils.reflect(Test8.class).field("SF_INT1").get());
        ReflectUtils.reflect(Test8.class).field("SF_INT2", 1);
        Assert.assertEquals(1, ReflectUtils.reflect(Test8.class).field("SF_INT2").get());
        ReflectUtils.reflect(Test8.class).field("SF_INT2", null);
        Assert.assertNull(ReflectUtils.reflect(Test8.class).field("SF_INT2").get());
    }

    @Test
    public void fieldPrivateStaticFinal() {
        Assert.assertEquals(1, ReflectUtils.reflect(TestPrivateStaticFinal.class).field("I1").get());
        Assert.assertEquals(1, ReflectUtils.reflect(TestPrivateStaticFinal.class).field("I2").get());
        ReflectUtils.reflect(TestPrivateStaticFinal.class).field("I1", 2);
        ReflectUtils.reflect(TestPrivateStaticFinal.class).field("I2", 2);
        Assert.assertEquals(2, ReflectUtils.reflect(TestPrivateStaticFinal.class).field("I1").get());
        Assert.assertEquals(2, ReflectUtils.reflect(TestPrivateStaticFinal.class).field("I2").get());
    }

    @Test
    public void fieldAdvanced() {
        ReflectUtils.reflect(Test1.class).field("S_DATA", ReflectUtils.reflect(Test1.class).newInstance()).field("S_DATA").field("I_DATA", ReflectUtils.reflect(Test1.class).newInstance()).field("I_DATA").field("I_INT1", 1).field("S_INT1", 2);
        Assert.assertEquals(2, Test1.S_INT1);
        Assert.assertEquals(null, Test1.S_INT2);
        Assert.assertEquals(0, Test1.S_DATA.I_INT1);
        Assert.assertEquals(null, Test1.S_DATA.I_INT2);
        Assert.assertEquals(1, Test1.S_DATA.I_DATA.I_INT1);
        Assert.assertEquals(null, Test1.S_DATA.I_DATA.I_INT2);
    }

    @Test
    public void fieldFinalAdvanced() {
        ReflectUtils.reflect(Test8.class).field("S_DATA", ReflectUtils.reflect(Test8.class).newInstance()).field("S_DATA").field("I_DATA", ReflectUtils.reflect(Test8.class).newInstance()).field("I_DATA").field("F_INT1", 1).field("F_INT2", 1).field("SF_INT1", 2).field("SF_INT2", 2);
        Assert.assertEquals(2, Test8.SF_INT1);
        Assert.assertEquals(new Integer(2), Test8.SF_INT2);
        Assert.assertEquals(0, Test8.S_DATA.F_INT1);
        Assert.assertEquals(new Integer(0), Test8.S_DATA.F_INT2);
        Assert.assertEquals(1, Test8.S_DATA.I_DATA.F_INT1);
        Assert.assertEquals(new Integer(1), Test8.S_DATA.I_DATA.F_INT2);
    }

    @Test
    public void _hashCode() {
        Object object = new Object();
        Assert.assertEquals(ReflectUtils.reflect(object).hashCode(), object.hashCode());
    }

    @Test
    public void _toString() {
        Object object = new Object() {
            @Override
            public String toString() {
                return "test";
            }
        };
        Assert.assertEquals(ReflectUtils.reflect(object).toString(), object.toString());
    }

    @Test
    public void _equals() {
        Object object = new Object();
        ReflectUtils a = ReflectUtils.reflect(object);
        ReflectUtils b = ReflectUtils.reflect(object);
        ReflectUtils c = ReflectUtils.reflect(object);
        Assert.assertTrue(b.equals(a));
        Assert.assertTrue(a.equals(b));
        Assert.assertTrue(b.equals(c));
        Assert.assertTrue(a.equals(c));
        // noinspection ObjectEqualsNull
        Assert.assertFalse(a.equals(null));
    }

    @Test
    public void testProxy() {
        Assert.assertEquals("abc", ReflectUtils.reflect(((Object) ("abc"))).proxy(Test9.class).substring(0));
        Assert.assertEquals("bc", ReflectUtils.reflect(((Object) ("abc"))).proxy(Test9.class).substring(1));
        Assert.assertEquals("c", ReflectUtils.reflect(((Object) ("abc"))).proxy(Test9.class).substring(2));
        Assert.assertEquals("a", ReflectUtils.reflect(((Object) ("abc"))).proxy(Test9.class).substring(0, 1));
        Assert.assertEquals("b", ReflectUtils.reflect(((Object) ("abc"))).proxy(Test9.class).substring(1, 2));
        Assert.assertEquals("c", ReflectUtils.reflect(((Object) ("abc"))).proxy(Test9.class).substring(2, 3));
        Assert.assertEquals("abc", ReflectUtils.reflect(((Object) ("abc"))).proxy(Test9.class).substring(0));
        Assert.assertEquals("bc", ReflectUtils.reflect(((Object) ("abc"))).proxy(Test9.class).substring(1));
        Assert.assertEquals("c", ReflectUtils.reflect(((Object) ("abc"))).proxy(Test9.class).substring(2));
        Assert.assertEquals("a", ReflectUtils.reflect(((Object) ("abc"))).proxy(Test9.class).substring(0, 1));
        Assert.assertEquals("b", ReflectUtils.reflect(((Object) ("abc"))).proxy(Test9.class).substring(1, 2));
        Assert.assertEquals("c", ReflectUtils.reflect(((Object) ("abc"))).proxy(Test9.class).substring(2, 3));
    }

    @Test
    public void testMapProxy() {
        class MyMap extends HashMap<String, Object> {
            private String baz;

            public void setBaz(String baz) {
                this.baz = "MyMap: " + baz;
            }

            public String getBaz() {
                return baz;
            }
        }
        Map<String, Object> map = new MyMap();
        ReflectUtils.reflect(map).proxy(Test10.class).setFoo("abc");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("abc", map.get("foo"));
        Assert.assertEquals("abc", ReflectUtils.reflect(map).proxy(Test10.class).getFoo());
        ReflectUtils.reflect(map).proxy(Test10.class).setBar(true);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(true, map.get("bar"));
        Assert.assertEquals(true, ReflectUtils.reflect(map).proxy(Test10.class).isBar());
        ReflectUtils.reflect(map).proxy(Test10.class).setBaz("baz");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(null, map.get("baz"));
        Assert.assertEquals("MyMap: baz", ReflectUtils.reflect(map).proxy(Test10.class).getBaz());
        try {
            ReflectUtils.reflect(map).proxy(Test10.class).testIgnore();
            Assert.fail();
        } catch (ReflectUtils ignored) {
        }
    }
}

