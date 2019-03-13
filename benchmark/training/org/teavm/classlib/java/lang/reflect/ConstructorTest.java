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


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.classlib.support.Reflectable;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
public class ConstructorTest {
    @Test
    public void constructorsEnumerated() {
        callConstructors();
        Assert.assertEquals(("" + ((("ConstructorTest$ReflectableType(int,java.lang.Object);" + "ConstructorTest$ReflectableType(java.lang.String);") + "protected ConstructorTest$ReflectableType();") + "public ConstructorTest$ReflectableType(int);")), collectConstructors(ConstructorTest.ReflectableType.class.getDeclaredConstructors()));
    }

    @Test
    public void publicConstructorsEnumerated() {
        callConstructors();
        Assert.assertEquals("public ConstructorTest$ReflectableType(int);", collectConstructors(ConstructorTest.ReflectableType.class.getConstructors()));
    }

    @Test
    public void newInstance() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Constructor<ConstructorTest.ReflectableType> constructor = ConstructorTest.ReflectableType.class.getDeclaredConstructor();
        ConstructorTest.ReflectableType instance = constructor.newInstance();
        Assert.assertEquals(0, instance.getA());
        Assert.assertNull(instance.getB());
    }

    @Test
    public void newInstance2() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Constructor<ConstructorTest.ReflectableType> constructor = ConstructorTest.ReflectableType.class.getDeclaredConstructor(int.class, Object.class);
        ConstructorTest.ReflectableType instance = constructor.newInstance(23, "42");
        Assert.assertEquals(23, instance.getA());
        Assert.assertEquals("42", instance.getB());
    }

    static class ReflectableType {
        public int a;

        public Object b;

        @Reflectable
        protected ReflectableType() {
        }

        @Reflectable
        public ReflectableType(int a) {
            this.a = a;
        }

        @Reflectable
        ReflectableType(String b) {
            this.b = b;
        }

        @Reflectable
        ReflectableType(int a, Object b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public Object getB() {
            return b;
        }
    }
}

