/**
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.core.util.asm;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class ClassFieldInspectorTest {
    @Test
    public void testIt() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector(ClassFieldInspectorTest.Person.class);
        Assert.assertEquals(7, ext.getFieldNames().size());
        Assert.assertEquals("getAge", ext.getGetterMethods().get("age").getName());
        Assert.assertEquals("isHappy", ext.getGetterMethods().get("happy").getName());
        Assert.assertEquals("getName", ext.getGetterMethods().get("name").getName());
        final Map<String, Integer> names = ext.getFieldNames();
        Assert.assertNotNull(names);
        Assert.assertEquals(7, names.size());
        Assert.assertNull(names.get("nAme"));
    }

    @Test
    public void testInterface() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector(TestInterface.class);
        Assert.assertEquals(2, ext.getFieldNames().size());
        Assert.assertEquals("getSomething", ext.getGetterMethods().get("something").getName());
        Assert.assertEquals("getAnother", ext.getGetterMethods().get("another").getName());
        final Map<String, Integer> names = ext.getFieldNames();
        Assert.assertNotNull(names);
        Assert.assertEquals(2, names.size());
    }

    @Test
    public void testAbstract() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector(TestAbstract.class);
        Assert.assertEquals(5, ext.getFieldNames().size());
        Assert.assertEquals("getSomething", ext.getGetterMethods().get("something").getName());
        Assert.assertEquals("getAnother", ext.getGetterMethods().get("another").getName());
        final Map<String, Integer> names = ext.getFieldNames();
        Assert.assertNotNull(names);
        Assert.assertEquals(5, names.size());
    }

    @Test
    public void testInheritedFields() throws Exception {
        ClassFieldInspector ext = new ClassFieldInspector(BeanInherit.class);
        Assert.assertEquals(5, ext.getFieldNames().size());
        Assert.assertNotNull(ext.getFieldTypesField().get("text"));
        Assert.assertNotNull(ext.getFieldTypesField().get("number"));
        ext = new ClassFieldInspector(InterfaceChildImpl.class);
        Assert.assertEquals(8, ext.getFieldNames().size());
        // test inheritence from abstract class
        Assert.assertNotNull(ext.getFieldNames().get("HTML"));
        Assert.assertNotNull(ext.getFieldTypesField().get("HTML"));
        // check normal field on child class
        Assert.assertNotNull(ext.getFieldNames().get("baz"));
        Assert.assertNotNull(ext.getFieldTypesField().get("baz"));
        // test inheritence from an interface
        Assert.assertNotNull(ext.getFieldNames().get("URI"));
        Assert.assertNotNull(ext.getFieldTypesField().get("URI"));
    }

    @Test
    public void testIntefaceInheritance() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector(InterfaceChild.class);
        final Map fields = ext.getFieldNames();
        Assert.assertTrue(fields.containsKey("foo"));
        Assert.assertTrue(fields.containsKey("bar"));
        Assert.assertTrue(fields.containsKey("baz"));
        Assert.assertTrue(fields.containsKey("URI"));
    }

    @Test
    public void testFieldIndexCalculation() {
        try {
            final ClassFieldInspector ext = new ClassFieldInspector(ClassFieldInspectorTest.SubPerson.class);
            final Map map = ext.getFieldNames();
            final String[] fields = new String[map.size()];
            for (final Iterator i = map.entrySet().iterator(); i.hasNext();) {
                final Map.Entry entry = ((Map.Entry) (i.next()));
                final String fieldName = ((String) (entry.getKey()));
                final int fieldIndex = ((Integer) (entry.getValue())).intValue();
                if ((fields[fieldIndex]) == null) {
                    fields[fieldIndex] = fieldName;
                } else {
                    Assert.fail((((((("Duplicate index found for 2 fields: index[" + fieldIndex) + "] = [") + (fields[fieldIndex])) + "] and [") + fieldName) + "]"));
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
            Assert.fail("Unexpected exception thrown");
        }
    }

    @Test
    public void testGetReturnTypes() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector(ClassFieldInspectorTest.Person.class);
        final Map types = ext.getFieldTypes();
        Assert.assertNotNull(types);
        Assert.assertEquals(boolean.class, types.get("happy"));
        Assert.assertEquals(int.class, types.get("age"));
        Assert.assertEquals(String.class, types.get("name"));
    }

    @Test
    public void testGetMethodForField() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector(ClassFieldInspectorTest.Person.class);
        final Map methods = ext.getGetterMethods();
        Assert.assertNotNull(methods);
        Assert.assertEquals("isHappy", ((Method) (methods.get("happy"))).getName());
        Assert.assertEquals("getName", ((Method) (methods.get("name"))).getName());
        // test case sensitive
        Assert.assertNull(methods.get("nAme"));
        Assert.assertEquals("getAge", ((Method) (methods.get("age"))).getName());
    }

    @Test
    public void testNonGetter() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector(ClassFieldInspectorTest.NonGetter.class);
        final Map methods = ext.getGetterMethods();
        Assert.assertEquals("getFoo", ((Method) (methods.get("foo"))).getName());
        Assert.assertEquals(5, methods.size());
        Assert.assertTrue(ext.getFieldNames().containsKey("foo"));
        Assert.assertTrue(ext.getFieldNames().containsKey("baz"));
        Assert.assertEquals(String.class, ext.getFieldTypes().get("foo"));
    }

    @Test
    public void testWierdCapsForField() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector(ClassFieldInspectorTest.Person.class);
        final Map methods = ext.getGetterMethods();
        Assert.assertEquals("getURI", ((Method) (methods.get("URI"))).getName());
        Assert.assertEquals(7, methods.size());
    }

    static class NonGetter {
        public int foo() {
            return 42;
        }

        public String getFoo() {
            return "foo";
        }

        public String baz() {
            return "";
        }

        public void bas() {
        }
    }

    static class Person {
        public static String aStaticString;

        private boolean happy;

        private String name;

        private int age;

        private String URI;

        static {
            ClassFieldInspectorTest.Person.aStaticString = "A static String";
        }

        public int getAge() {
            return this.age;
        }

        public void setAge(final int age) {
            this.age = age;
        }

        public boolean isHappy() {
            return this.happy;
        }

        public void setHappy(final boolean happy) {
            this.happy = happy;
        }

        public String getName() {
            return this.name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        // ignore this as it returns void type
        public void getNotAGetter() {
            return;
        }

        // ignore this as private
        private boolean isBogus() {
            return false;
        }

        // this will not show up as it is a getter that takes an argument
        public String getAlsoBad(final String s) {
            return "ignored";
        }

        // this should show up, as its a getter, but all CAPS
        public String getURI() {
            return this.URI;
        }

        public void setURI(final String URI) {
            this.URI = URI;
        }
    }

    static class SubPerson {
        private int childField;

        /**
         *
         *
         * @return the childField
         */
        public int getChildField() {
            return this.childField;
        }

        /**
         *
         *
         * @param childField
         * 		the childField to set
         */
        public void setChildField(final int childField) {
            this.childField = childField;
        }
    }

    @Test
    public void testOverridingMethodWithCovariantReturnType() throws Exception {
        final ClassFieldInspector ext = new ClassFieldInspector(ClassFieldInspectorTest.SuperCar.class);
        final Class<?> engine = ext.getFieldTypes().get("engine");
        Assert.assertEquals(ClassFieldInspectorTest.SuperEngine.class, engine);
    }

    static class Vehicle<T> {
        private T engine;

        public T getEngine() {
            return engine;
        }
    }

    static class Car extends ClassFieldInspectorTest.Vehicle<ClassFieldInspectorTest.NormalEngine> {
        @Override
        public ClassFieldInspectorTest.NormalEngine getEngine() {
            return new ClassFieldInspectorTest.NormalEngine();
        }
    }

    static class SuperCar extends ClassFieldInspectorTest.Car {
        @Override
        public ClassFieldInspectorTest.SuperEngine getEngine() {
            return new ClassFieldInspectorTest.SuperEngine();
        }
    }

    static class NormalEngine {}

    static class SuperEngine extends ClassFieldInspectorTest.NormalEngine {}
}

