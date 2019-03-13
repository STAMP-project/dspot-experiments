/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joor.test;


import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.joor.Reflect;
import org.joor.ReflectException;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.joor.test.Test2.ConstructorType.INTEGER;
import static org.joor.test.Test2.ConstructorType.NO_ARGS;
import static org.joor.test.Test2.ConstructorType.NUMBER;
import static org.joor.test.Test2.ConstructorType.OBJECT;


/**
 *
 *
 * @author Lukas Eder
 * @author Thomas Darimont
 */
public class ReflectTest {
    static final boolean JDK9 = false;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testOnClass() {
        Assert.assertEquals(Reflect.onClass(Object.class), Reflect.onClass("java.lang.Object", ClassLoader.getSystemClassLoader()));
        Assert.assertEquals(Reflect.onClass(Object.class), Reflect.onClass("java.lang.Object"));
        Assert.assertEquals(Reflect.onClass(Object.class).<Object>get(), Reflect.onClass("java.lang.Object").get());
        try {
            Reflect.onClass("asdf");
            Assert.fail();
        } catch (ReflectException expected) {
        }
        try {
            Reflect.onClass("asdf", ClassLoader.getSystemClassLoader());
            Assert.fail();
        } catch (ReflectException expected) {
        }
    }

    @Test
    public void testOnInstance() {
        Assert.assertEquals(Object.class, Reflect.onClass(Object.class).get());
        Assert.assertEquals("abc", Reflect.on(((Object) ("abc"))).get());
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(1).get()))));
    }

    @Test
    public void testConstructors() {
        Assert.assertEquals("", Reflect.onClass(String.class).create().get());
        Assert.assertEquals("abc", Reflect.onClass(String.class).create("abc").get());
        Assert.assertEquals("abc", Reflect.onClass(String.class).create("abc".getBytes()).get());
        Assert.assertEquals("abc", Reflect.onClass(String.class).create("abc".toCharArray()).get());
        Assert.assertEquals("b", Reflect.onClass(String.class).create("abc".toCharArray(), 1, 1).get());
        try {
            Reflect.onClass(String.class).create(new Object());
            Assert.fail();
        } catch (ReflectException expected) {
        }
    }

    @Test
    public void testPrivateConstructor() {
        Assert.assertNull(Reflect.onClass(PrivateConstructors.class).create().get("string"));
        Assert.assertEquals("abc", Reflect.onClass(PrivateConstructors.class).create("abc").get("string"));
    }

    @Test
    public void testConstructorsWithAmbiguity() {
        // [#5] Re-enact when this is implemented
        Assume.assumeTrue(false);
        Test2 test;
        test = Reflect.onClass(Test2.class).create().get();
        Assert.assertEquals(null, test.n);
        Assert.assertEquals(NO_ARGS, test.constructorType);
        test = Reflect.onClass(Test2.class).create("abc").get();
        Assert.assertEquals("abc", test.n);
        Assert.assertEquals(OBJECT, test.constructorType);
        test = Reflect.onClass(Test2.class).create(1L).get();
        Assert.assertEquals(1L, test.n);
        Assert.assertEquals(NUMBER, test.constructorType);
        test = Reflect.onClass(Test2.class).create(1).get();
        Assert.assertEquals(1, test.n);
        Assert.assertEquals(INTEGER, test.constructorType);
        test = Reflect.onClass(Test2.class).create('a').get();
        Assert.assertEquals('a', test.n);
        Assert.assertEquals(OBJECT, test.constructorType);
    }

    @Test
    public void testMethods() {
        // Instance methods
        // ----------------
        Assert.assertEquals("", Reflect.on(((Object) (" "))).call("trim").get());
        Assert.assertEquals("12", Reflect.on(((Object) (" 12 "))).call("trim").get());
        Assert.assertEquals("34", Reflect.on(((Object) ("1234"))).call("substring", 2).get());
        Assert.assertEquals("12", Reflect.on(((Object) ("1234"))).call("substring", 0, 2).get());
        Assert.assertEquals("1234", Reflect.on(((Object) ("12"))).call("concat", "34").get());
        Assert.assertEquals("123456", Reflect.on(((Object) ("12"))).call("concat", "34").call("concat", "56").get());
        Assert.assertEquals(2, ((int) ((Integer) (Reflect.on(((Object) ("1234"))).call("indexOf", "3").get()))));
        Assert.assertEquals(2.0F, ((Float) (Reflect.on(((Object) ("1234"))).call("indexOf", "3").call("floatValue").get())), 0.0F);
        Assert.assertEquals("2", Reflect.on(((Object) ("1234"))).call("indexOf", "3").call("toString").get());
        // Static methods
        // --------------
        Assert.assertEquals("true", Reflect.onClass(String.class).call("valueOf", true).get());
        Assert.assertEquals("1", Reflect.onClass(String.class).call("valueOf", 1).get());
        Assert.assertEquals("abc", Reflect.onClass(String.class).call("valueOf", "abc".toCharArray()).get());
        Assert.assertEquals("abc", Reflect.onClass(String.class).call("copyValueOf", "abc".toCharArray()).get());
        Assert.assertEquals("b", Reflect.onClass(String.class).call("copyValueOf", "abc".toCharArray(), 1, 1).get());
    }

    @Test
    public void testVoidMethods() {
        // Instance methods
        // ----------------
        Test4 test4 = new Test4();
        Assert.assertEquals(test4, Reflect.on(test4).call("i_method").get());
        // Static methods
        // --------------
        Assert.assertEquals(Test4.class, Reflect.onClass(Test4.class).call("s_method").get());
    }

    @Test
    public void testPrivateMethods() throws Exception {
        // Instance methods
        // ----------------
        Test8 test8 = new Test8();
        Assert.assertEquals(test8, Reflect.on(test8).call("i_method").get());
        // Static methods
        // --------------
        Assert.assertEquals(Test8.class, Reflect.onClass(Test8.class).call("s_method").get());
    }

    @Test
    public void testNullArguments() throws Exception {
        Test9 test9 = new Test9();
        Reflect.on(test9).call("put", "key", "value");
        Assert.assertTrue(test9.map.containsKey("key"));
        Assert.assertEquals("value", test9.map.get("key"));
        Reflect.on(test9).call("put", "key", null);
        Assert.assertTrue(test9.map.containsKey("key"));
        Assert.assertNull(test9.map.get("key"));
    }

    @Test
    public void testPublicMethodsAreFoundInHierarchy() throws Exception {
        TestHierarchicalMethodsSubclass subclass = new TestHierarchicalMethodsSubclass();
        Assert.assertEquals(TestHierarchicalMethodsBase.PUBLIC_RESULT, Reflect.on(subclass).call("pub_base_method", 1).get());
    }

    @Test
    public void testPrivateMethodsAreFoundInHierarchy() throws Exception {
        TestHierarchicalMethodsSubclass subclass = new TestHierarchicalMethodsSubclass();
        Reflect.on(subclass).call("very_priv_method").get();
    }

    @Test
    public void testPrivateMethodsAreFoundOnDeclaringClass() throws Exception {
        TestHierarchicalMethodsSubclass subclass = new TestHierarchicalMethodsSubclass();
        Assert.assertEquals(TestHierarchicalMethodsSubclass.PRIVATE_RESULT, Reflect.on(subclass).call("priv_method", 1).get());
        TestHierarchicalMethodsBase baseClass = new TestHierarchicalMethodsBase();
        Assert.assertEquals(TestHierarchicalMethodsBase.PRIVATE_RESULT, Reflect.on(baseClass).call("priv_method", 1).get());
    }

    @Test
    public void testMethodsWithAmbiguity() {
        // [#5] Re-enact when this is implemented
        Assume.assumeTrue(false);
        Test3 test;
        test = Reflect.onClass(Test3.class).create().call("method").get();
        Assert.assertEquals(null, test.n);
        Assert.assertEquals(Test3.MethodType.NO_ARGS, test.methodType);
        test = Reflect.onClass(Test3.class).create().call("method", "abc").get();
        Assert.assertEquals("abc", test.n);
        Assert.assertEquals(Test3.MethodType.OBJECT, test.methodType);
        test = Reflect.onClass(Test3.class).create().call("method", 1L).get();
        Assert.assertEquals(1L, test.n);
        Assert.assertEquals(Test3.MethodType.NUMBER, test.methodType);
        test = Reflect.onClass(Test3.class).create().call("method", 1).get();
        Assert.assertEquals(1, test.n);
        Assert.assertEquals(Test3.MethodType.INTEGER, test.methodType);
        test = Reflect.onClass(Test3.class).create().call("method", 'a').get();
        Assert.assertEquals('a', test.n);
        Assert.assertEquals(Test3.MethodType.OBJECT, test.methodType);
    }

    @Test
    public void testFields() throws Exception {
        // Instance methods
        // ----------------
        Test1 test1 = new Test1();
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test1).set("I_INT1", 1).get("I_INT1")))));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test1).field("I_INT1").get()))));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test1).set("I_INT2", 1).get("I_INT2")))));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test1).field("I_INT2").get()))));
        Assert.assertNull(Reflect.on(test1).set("I_INT2", null).get("I_INT2"));
        Assert.assertNull(Reflect.on(test1).field("I_INT2").get());
        // Static methods
        // --------------
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.onClass(Test1.class).set("S_INT1", 1).get("S_INT1")))));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.onClass(Test1.class).field("S_INT1").get()))));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.onClass(Test1.class).set("S_INT2", 1).get("S_INT2")))));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.onClass(Test1.class).field("S_INT2").get()))));
        Assert.assertNull(Reflect.onClass(Test1.class).set("S_INT2", null).get("S_INT2"));
        Assert.assertNull(Reflect.onClass(Test1.class).field("S_INT2").get());
        // Hierarchies
        // -----------
        TestHierarchicalMethodsSubclass test2 = new TestHierarchicalMethodsSubclass();
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test2).set("invisibleField1", 1).get("invisibleField1")))));
        Assert.assertEquals(1, Reflect.accessible(TestHierarchicalMethodsBase.class.getDeclaredField("invisibleField1")).get(test2));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test2).set("invisibleField2", 1).get("invisibleField2")))));
        Assert.assertEquals(0, Reflect.accessible(TestHierarchicalMethodsBase.class.getDeclaredField("invisibleField2")).get(test2));
        Assert.assertEquals(1, Reflect.accessible(TestHierarchicalMethodsSubclass.class.getDeclaredField("invisibleField2")).get(test2));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test2).set("invisibleField3", 1).get("invisibleField3")))));
        Assert.assertEquals(1, Reflect.accessible(TestHierarchicalMethodsSubclass.class.getDeclaredField("invisibleField3")).get(test2));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test2).set("visibleField1", 1).get("visibleField1")))));
        Assert.assertEquals(1, Reflect.accessible(TestHierarchicalMethodsBase.class.getDeclaredField("visibleField1")).get(test2));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test2).set("visibleField2", 1).get("visibleField2")))));
        Assert.assertEquals(0, Reflect.accessible(TestHierarchicalMethodsBase.class.getDeclaredField("visibleField2")).get(test2));
        Assert.assertEquals(1, Reflect.accessible(TestHierarchicalMethodsSubclass.class.getDeclaredField("visibleField2")).get(test2));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test2).set("visibleField3", 1).get("visibleField3")))));
        Assert.assertEquals(1, Reflect.accessible(TestHierarchicalMethodsSubclass.class.getDeclaredField("visibleField3")).get(test2));
        Assert.assertNull(Reflect.accessible(null));
    }

    @Test
    public void testFinalFields() {
        try {
            // Instance methods
            // ----------------
            Test11 test11 = new Test11();
            Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test11).set("F_INT1", 1).get("F_INT1")))));
            Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test11).field("F_INT1").get()))));
            Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test11).set("F_INT2", 1).get("F_INT1")))));
            Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test11).field("F_INT2").get()))));
            Assert.assertNull(Reflect.on(test11).set("F_INT2", null).get("F_INT2"));
            Assert.assertNull(Reflect.on(test11).field("F_INT2").get());
            // Static methods
            // ----------------
            Assert.assertEquals(1, ((int) ((Integer) (Reflect.onClass(Test11.class).set("SF_INT1", 1).get("SF_INT1")))));
            Assert.assertEquals(1, ((int) ((Integer) (Reflect.onClass(Test11.class).field("SF_INT1").get()))));
            Assert.assertEquals(1, ((int) ((Integer) (Reflect.onClass(Test11.class).set("SF_INT2", 1).get("SF_INT2")))));
            Assert.assertEquals(1, ((int) ((Integer) (Reflect.onClass(Test11.class).field("SF_INT2").get()))));
            Reflect.onClass(Test11.class).set("SF_INT2", 1).field("SF_INT2").get();
            Assert.assertNull(Reflect.onClass(Test11.class).set("SF_INT2", null).get("SF_INT2"));
            Assert.assertNull(Reflect.onClass(Test11.class).field("SF_INT2").get());
        } catch (ReflectException e) {
            // [#50] This may no longer work on JDK 9
            if (!(ReflectTest.JDK9))
                throw e;

        }
    }

    @Test
    public void testFinalFieldAdvanced() {
        try {
            Reflect.onClass(Test11.class).set("S_DATA", Reflect.onClass(Test11.class).create()).field("S_DATA").set("I_DATA", Reflect.onClass(Test11.class).create()).field("I_DATA").set("F_INT1", 1).set("F_INT2", 1).set("SF_INT1", 2).set("SF_INT2", 2);
            Assert.assertEquals(2, Test11.SF_INT1);
            Assert.assertEquals(2, ((int) (Test11.SF_INT2)));
            Assert.assertEquals(0, Test11.S_DATA.F_INT1);
            Assert.assertEquals(0, ((int) (Test11.S_DATA.F_INT2)));
            Assert.assertEquals(1, Test11.S_DATA.I_DATA.F_INT1);
            Assert.assertEquals(1, ((int) (Test11.S_DATA.I_DATA.F_INT2)));
        } catch (ReflectException e) {
            // [#50] This may no longer work on JDK 9
            if (!(ReflectTest.JDK9))
                throw e;

        }
    }

    @Test
    public void testFieldMap() {
        // Instance methods
        // ----------------
        Test1 test1 = new Test1();
        Assert.assertEquals(3, Reflect.on(test1).fields().size());
        Assert.assertTrue(Reflect.on(test1).fields().containsKey("I_INT1"));
        Assert.assertTrue(Reflect.on(test1).fields().containsKey("I_INT2"));
        Assert.assertTrue(Reflect.on(test1).fields().containsKey("I_DATA"));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test1).set("I_INT1", 1).fields().get("I_INT1").get()))));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test1).fields().get("I_INT1").get()))));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test1).set("I_INT2", 1).fields().get("I_INT2").get()))));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.on(test1).fields().get("I_INT2").get()))));
        Assert.assertNull(Reflect.on(test1).set("I_INT2", null).fields().get("I_INT2").get());
        Assert.assertNull(Reflect.on(test1).fields().get("I_INT2").get());
        // Static methods
        // --------------
        Assert.assertEquals(3, Reflect.onClass(Test1.class).fields().size());
        Assert.assertTrue(Reflect.onClass(Test1.class).fields().containsKey("S_INT1"));
        Assert.assertTrue(Reflect.onClass(Test1.class).fields().containsKey("S_INT2"));
        Assert.assertTrue(Reflect.onClass(Test1.class).fields().containsKey("S_DATA"));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.onClass(Test1.class).set("S_INT1", 1).fields().get("S_INT1").get()))));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.onClass(Test1.class).fields().get("S_INT1").get()))));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.onClass(Test1.class).set("S_INT2", 1).fields().get("S_INT2").get()))));
        Assert.assertEquals(1, ((int) ((Integer) (Reflect.onClass(Test1.class).fields().get("S_INT2").get()))));
        Assert.assertNull(Reflect.onClass(Test1.class).set("S_INT2", null).fields().get("S_INT2").get());
        Assert.assertNull(Reflect.onClass(Test1.class).fields().get("S_INT2").get());
        // Hierarchies
        // -----------
        TestHierarchicalMethodsSubclass test2 = new TestHierarchicalMethodsSubclass();
        Assert.assertEquals(6, Reflect.on(test2).fields().size());
        Assert.assertTrue(Reflect.on(test2).fields().containsKey("invisibleField1"));
        Assert.assertTrue(Reflect.on(test2).fields().containsKey("invisibleField2"));
        Assert.assertTrue(Reflect.on(test2).fields().containsKey("invisibleField3"));
        Assert.assertTrue(Reflect.on(test2).fields().containsKey("visibleField1"));
        Assert.assertTrue(Reflect.on(test2).fields().containsKey("visibleField2"));
        Assert.assertTrue(Reflect.on(test2).fields().containsKey("visibleField3"));
    }

    @Test
    public void testFieldAdvanced() {
        Reflect.onClass(Test1.class).set("S_DATA", Reflect.onClass(Test1.class).create()).field("S_DATA").set("I_DATA", Reflect.onClass(Test1.class).create()).field("I_DATA").set("I_INT1", 1).set("S_INT1", 2);
        Assert.assertEquals(2, Test1.S_INT1);
        Assert.assertEquals(null, Test1.S_INT2);
        Assert.assertEquals(0, Test1.S_DATA.I_INT1);
        Assert.assertEquals(null, Test1.S_DATA.I_INT2);
        Assert.assertEquals(1, Test1.S_DATA.I_DATA.I_INT1);
        Assert.assertEquals(null, Test1.S_DATA.I_DATA.I_INT2);
    }

    @Test
    public void testProxy() {
        Assert.assertEquals("abc", Reflect.on(((Object) ("abc"))).as(Test5.class).substring(0));
        Assert.assertEquals("bc", Reflect.on(((Object) ("abc"))).as(Test5.class).substring(1));
        Assert.assertEquals("c", Reflect.on(((Object) ("abc"))).as(Test5.class).substring(2));
        Assert.assertEquals("a", Reflect.on(((Object) ("abc"))).as(Test5.class).substring(0, 1));
        Assert.assertEquals("b", Reflect.on(((Object) ("abc"))).as(Test5.class).substring(1, 2));
        Assert.assertEquals("c", Reflect.on(((Object) ("abc"))).as(Test5.class).substring(2, 3));
        Assert.assertEquals("abc", Reflect.on(((Object) ("abc"))).as(Test5.class).substring(0));
        Assert.assertEquals("bc", Reflect.on(((Object) ("abc"))).as(Test5.class).substring(1));
        Assert.assertEquals("c", Reflect.on(((Object) ("abc"))).as(Test5.class).substring(2));
        Assert.assertEquals("a", Reflect.on(((Object) ("abc"))).as(Test5.class).substring(0, 1));
        Assert.assertEquals("b", Reflect.on(((Object) ("abc"))).as(Test5.class).substring(1, 2));
        Assert.assertEquals("c", Reflect.on(((Object) ("abc"))).as(Test5.class).substring(2, 3));
    }

    @Test
    public void testMapProxy() {
        @SuppressWarnings({ "unused", "serial" })
        class MyMap extends HashMap<String, Object> {
            String baz;

            public void setBaz(String baz) {
                this.baz = "MyMap: " + baz;
            }

            public String getBaz() {
                return baz;
            }
        }
        Map<String, Object> map = new MyMap();
        Reflect.on(map).as(Test6.class).setFoo("abc");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals("abc", map.get("foo"));
        Assert.assertEquals("abc", Reflect.on(map).as(Test6.class).getFoo());
        Reflect.on(map).as(Test6.class).setBar(true);
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(true, map.get("bar"));
        Assert.assertEquals(true, Reflect.on(map).as(Test6.class).isBar());
        Reflect.on(map).as(Test6.class).setBaz("baz");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(null, map.get("baz"));
        Assert.assertEquals("MyMap: baz", Reflect.on(map).as(Test6.class).getBaz());
        try {
            Reflect.on(map).as(Test6.class).testIgnore();
            Assert.fail();
        } catch (ReflectException expected) {
        }
    }

    @Test
    public void testPrivateField() throws Exception {
        class Foo {
            private String bar;
        }
        Foo foo = new Foo();
        Reflect.on(foo).set("bar", "FooBar");
        Assert.assertThat(foo.bar, CoreMatchers.is("FooBar"));
        Assert.assertEquals("FooBar", Reflect.on(foo).get("bar"));
        Reflect.on(foo).set("bar", null);
        Assert.assertNull(foo.bar);
        Assert.assertNull(Reflect.on(foo).get("bar"));
    }

    @Test
    public void testType() throws Exception {
        Assert.assertEquals(Object.class, Reflect.on(new Object()).type());
        Assert.assertEquals(Object.class, Reflect.onClass(Object.class).type());
        Assert.assertEquals(Integer.class, Reflect.on(1).type());
        Assert.assertEquals(Integer.class, Reflect.onClass(Integer.class).type());
    }

    @Test
    public void testCreateWithNulls() throws Exception {
        Test2 test2 = Reflect.onClass(Test2.class).create(((Object) (null))).<Test2>get();
        Assert.assertNull(test2.n);
        // Can we make any assertions about the actual construct being called?
        // assertEquals(Test2.ConstructorType.OBJECT, test2.constructorType);
    }

    @Test
    public void testCreateWithPrivateConstructor() throws Exception {
        Test10 t1 = Reflect.onClass(Test10.class).create(1).get();
        Assert.assertEquals(1, ((int) (t1.i)));
        Assert.assertNull(t1.s);
        Test10 t2 = Reflect.onClass(Test10.class).create("a").get();
        Assert.assertNull(t2.i);
        Assert.assertEquals("a", t2.s);
        Test10 t3 = Reflect.onClass(Test10.class).create("a", 1).get();
        Assert.assertEquals(1, ((int) (t3.i)));
        Assert.assertEquals("a", t3.s);
    }

    @Test
    public void testHashCode() {
        Object object = new Object();
        Assert.assertEquals(Reflect.on(object).hashCode(), object.hashCode());
    }

    @Test
    public void testToString() {
        Object object = new Object() {
            @Override
            public String toString() {
                return "test";
            }
        };
        Assert.assertEquals(Reflect.on(object).toString(), object.toString());
    }

    @Test
    public void testEquals() {
        Object object = new Object();
        Reflect a = Reflect.on(object);
        Reflect b = Reflect.on(object);
        Reflect c = Reflect.on(object);
        Assert.assertTrue(b.equals(a));
        Assert.assertTrue(a.equals(b));
        Assert.assertTrue(b.equals(c));
        Assert.assertTrue(a.equals(c));
        // noinspection ObjectEqualsNull
        Assert.assertFalse(a.equals(null));
    }

    @Test
    public void testNullStaticFieldType() {
        Map<String, Reflect> fields = Reflect.onClass(Test1.class).fields();
        Assert.assertEquals(3, fields.size());
        Assert.assertEquals(int.class, fields.get("S_INT1").type());
        Assert.assertEquals(Integer.valueOf(0), fields.get("S_INT1").get());
        Assert.assertEquals(Integer.class, fields.get("S_INT2").type());
        Assert.assertNull(fields.get("S_INT2").get());
        Assert.assertEquals(Test1.class, fields.get("S_DATA").type());
        Assert.assertNull(fields.get("S_DATA").get());
    }

    @Test
    public void testNullInstanceFieldType() {
        Map<String, Reflect> fields = Reflect.on(new Test1()).fields();
        Assert.assertEquals(3, fields.size());
        Assert.assertEquals(int.class, fields.get("I_INT1").type());
        Assert.assertEquals(Integer.valueOf(0), fields.get("I_INT1").get());
        Assert.assertEquals(Integer.class, fields.get("I_INT2").type());
        Assert.assertNull(fields.get("I_INT2").get());
        Assert.assertEquals(Test1.class, fields.get("I_DATA").type());
        Assert.assertNull(fields.get("I_DATA").get());
    }

    @Test
    public void testNullInstanceToString() {
        Assert.assertEquals("null", Reflect.on(((Object) (null))).toString());
    }

    @Test
    public void testInitValue() {
        Assert.assertEquals(((byte) (0)), ((byte) (Reflect.initValue(byte.class))));
        Assert.assertEquals(((short) (0)), ((short) (Reflect.initValue(short.class))));
        Assert.assertEquals(0, ((int) (Reflect.initValue(int.class))));
        Assert.assertEquals(0L, ((long) (Reflect.initValue(long.class))));
        Assert.assertEquals(0.0, ((double) (Reflect.initValue(double.class))), 0.0);
        Assert.assertEquals(0.0F, ((float) (Reflect.initValue(float.class))), 0.0F);
        Assert.assertEquals(((char) (0)), ((char) (Reflect.initValue(char.class))));
        Assert.assertEquals(false, ((boolean) (Reflect.initValue(boolean.class))));
        Assert.assertNull(Reflect.initValue(Object.class));
        Assert.assertNull(Reflect.initValue(Byte.class));
        Assert.assertNull(Reflect.initValue(Short.class));
        Assert.assertNull(Reflect.initValue(Integer.class));
        Assert.assertNull(Reflect.initValue(Long.class));
        Assert.assertNull(Reflect.initValue(Double.class));
        Assert.assertNull(Reflect.initValue(Float.class));
        Assert.assertNull(Reflect.initValue(Character.class));
        Assert.assertNull(Reflect.initValue(Boolean.class));
    }
}

