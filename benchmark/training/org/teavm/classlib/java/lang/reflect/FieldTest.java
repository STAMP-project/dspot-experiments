/**
 * Copyright 2016 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.lang.reflect;


import java.lang.reflect.Field;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.classlib.support.Reflectable;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class FieldTest {
    @Test
    public void fieldsEnumerated() {
        new FieldTest.ReflectableType();
        StringBuilder sb = new StringBuilder();
        for (Field field : FieldTest.ReflectableType.class.getDeclaredFields()) {
            sb.append(field).append(";");
        }
        Assert.assertEquals(("" + (((((("public int org.teavm.classlib.java.lang.reflect.FieldTest$ReflectableType.a;" + "private boolean org.teavm.classlib.java.lang.reflect.FieldTest$ReflectableType.b;") + "java.lang.Object org.teavm.classlib.java.lang.reflect.FieldTest$ReflectableType.c;") + "java.lang.String org.teavm.classlib.java.lang.reflect.FieldTest$ReflectableType.d;") + "long org.teavm.classlib.java.lang.reflect.FieldTest$ReflectableType.e;") + "private static short org.teavm.classlib.java.lang.reflect.FieldTest$ReflectableType.f;") + "static boolean org.teavm.classlib.java.lang.reflect.FieldTest$ReflectableType.initialized;")), sb.toString());
    }

    @Test
    public void fieldRead() throws IllegalAccessException, NoSuchFieldException {
        FieldTest.ReflectableType instance = new FieldTest.ReflectableType();
        Field field = instance.getClass().getDeclaredField("a");
        Object result = field.get(instance);
        Assert.assertEquals(23, result);
    }

    @Test
    public void fieldWritten() throws IllegalAccessException, NoSuchFieldException {
        FieldTest.ReflectableType instance = new FieldTest.ReflectableType();
        Field field = instance.getClass().getDeclaredField("a");
        field.set(instance, 234);
        Assert.assertEquals(234, instance.a);
    }

    @Test(expected = IllegalAccessException.class)
    @SkipJVM
    public void fieldCannotBeRead() throws IllegalAccessException, NoSuchFieldException {
        FieldTest.ReflectableType instance = new FieldTest.ReflectableType();
        Field field = instance.getClass().getDeclaredField("e");
        field.get(instance);
    }

    @Test(expected = IllegalAccessException.class)
    @SkipJVM
    public void fieldCannotBeWritten() throws IllegalAccessException, NoSuchFieldException {
        FieldTest.ReflectableType instance = new FieldTest.ReflectableType();
        Field field = instance.getClass().getDeclaredField("e");
        field.set(instance, 1L);
    }

    @Test
    public void staticFieldRead() throws IllegalAccessException, NoSuchFieldException {
        Field field = FieldTest.ReflectableType.class.getDeclaredField("f");
        field.setAccessible(true);
        Object result = field.get(null);
        Assert.assertTrue(FieldTest.ReflectableType.initialized);
        Assert.assertEquals(FieldTest.ReflectableType.f, result);
    }

    @Test
    public void staticFieldWritten() throws IllegalAccessException, NoSuchFieldException {
        Field field = FieldTest.ReflectableType.class.getDeclaredField("f");
        field.setAccessible(true);
        field.set(null, ((short) (999)));
        Assert.assertTrue(FieldTest.ReflectableType.initialized);
        Assert.assertEquals(((short) (999)), FieldTest.ReflectableType.f);
    }

    @Test
    public void dependencyMaintainedForGet() throws IllegalAccessException, NoSuchFieldException {
        FieldTest.ReflectableType instance = new FieldTest.ReflectableType();
        instance.c = new FieldTest.Foo(123);
        Field field = FieldTest.ReflectableType.class.getDeclaredField("c");
        FieldTest.Foo result = ((FieldTest.Foo) (field.get(instance)));
        Assert.assertEquals(123, result.getValue());
    }

    @Test
    public void dependencyMaintainedForSet() throws IllegalAccessException, NoSuchFieldException {
        FieldTest.ReflectableType instance = new FieldTest.ReflectableType();
        Field field = FieldTest.ReflectableType.class.getDeclaredField("c");
        field.set(instance, new FieldTest.Foo(123));
        Assert.assertEquals(123, ((FieldTest.Foo) (instance.c)).getValue());
    }

    static class ReflectableType {
        @Reflectable
        public int a;

        @Reflectable
        private boolean b;

        @Reflectable
        Object c;

        @Reflectable
        String d;

        long e;

        @Reflectable
        private static short f = 99;

        static boolean initialized = true;

        public ReflectableType() {
            a = 23;
            b = true;
            c = "foo";
            d = "bar";
            e = 42;
        }
    }

    static class Foo {
        int value;

        public Foo(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}

