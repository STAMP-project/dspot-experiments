/**
 * Copyright (C) 2010 The Android Open Source Project
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
package libcore.java.lang.reflect;


import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;
import junit.framework.TestCase;


public final class ReflectionTest extends TestCase {
    String classA = "libcore.java.lang.reflect.ReflectionTest$A";

    String classB = "libcore.java.lang.reflect.ReflectionTest$B";

    String classC = "libcore.java.lang.reflect.ReflectionTest$C";

    /**
     * http://code.google.com/p/android/issues/detail?id=6636
     */
    public void testGenericSuperclassToString() throws Exception {
        TestCase.assertEquals((("java.util.ArrayList<" + (classA)) + ">"), ReflectionTest.AList.class.getGenericSuperclass().toString());
    }

    public void testClassGetName() {
        TestCase.assertEquals("int", int.class.getName());
        TestCase.assertEquals("[I", int[].class.getName());
        TestCase.assertEquals("java.lang.String", String.class.getName());
        TestCase.assertEquals("[Ljava.lang.String;", String[].class.getName());
        TestCase.assertEquals("libcore.java.lang.reflect.ReflectionTest", getClass().getName());
        TestCase.assertEquals(((getClass().getName()) + "$A"), ReflectionTest.A.class.getName());
        TestCase.assertEquals(((getClass().getName()) + "$B"), ReflectionTest.B.class.getName());
        TestCase.assertEquals(((getClass().getName()) + "$DefinesMember"), ReflectionTest.DefinesMember.class.getName());
    }

    public void testClassGetCanonicalName() {
        TestCase.assertEquals("int", int.class.getCanonicalName());
        TestCase.assertEquals("int[]", int[].class.getCanonicalName());
        TestCase.assertEquals("java.lang.String", String.class.getCanonicalName());
        TestCase.assertEquals("java.lang.String[]", String[].class.getCanonicalName());
        TestCase.assertEquals("libcore.java.lang.reflect.ReflectionTest", getClass().getCanonicalName());
        TestCase.assertEquals(((getClass().getName()) + ".A"), ReflectionTest.A.class.getCanonicalName());
        TestCase.assertEquals(((getClass().getName()) + ".B"), ReflectionTest.B.class.getCanonicalName());
        TestCase.assertEquals(((getClass().getName()) + ".DefinesMember"), ReflectionTest.DefinesMember.class.getCanonicalName());
    }

    public void testFieldToString() throws Exception {
        Field fieldOne = ReflectionTest.C.class.getDeclaredField("fieldOne");
        String fieldOneRaw = ((("public static " + (classA)) + " ") + (classC)) + ".fieldOne";
        TestCase.assertEquals(fieldOneRaw, fieldOne.toString());
        TestCase.assertEquals(fieldOneRaw, fieldOne.toGenericString());
        Field fieldTwo = ReflectionTest.C.class.getDeclaredField("fieldTwo");
        TestCase.assertEquals((("private transient volatile java.util.Map " + (classC)) + ".fieldTwo"), fieldTwo.toString());
        TestCase.assertEquals((((("private transient volatile java.util.Map<" + (classA)) + ", java.lang.String> ") + (classC)) + ".fieldTwo"), fieldTwo.toGenericString());
        Field fieldThree = ReflectionTest.C.class.getDeclaredField("fieldThree");
        String fieldThreeRaw = ("protected java.lang.Object[] " + (classC)) + ".fieldThree";
        TestCase.assertEquals(fieldThreeRaw, fieldThree.toString());
        String fieldThreeGeneric = ("protected K[] " + (classC)) + ".fieldThree";
        TestCase.assertEquals(fieldThreeGeneric, fieldThree.toGenericString());
        Field fieldFour = ReflectionTest.C.class.getDeclaredField("fieldFour");
        String fieldFourRaw = ("java.util.Map " + (classC)) + ".fieldFour";
        TestCase.assertEquals(fieldFourRaw, fieldFour.toString());
        String fieldFourGeneric = ("java.util.Map<? super java.lang.Integer, java.lang.Integer[]> " + (classC)) + ".fieldFour";
        TestCase.assertEquals(fieldFourGeneric, fieldFour.toGenericString());
        Field fieldFive = ReflectionTest.C.class.getDeclaredField("fieldFive");
        String fieldFiveRaw = ("java.lang.String[][][][][] " + (classC)) + ".fieldFive";
        TestCase.assertEquals(fieldFiveRaw, fieldFive.toString());
        TestCase.assertEquals(fieldFiveRaw, fieldFive.toGenericString());
    }

    public void testConstructorToString() throws Exception {
        Constructor constructorOne = ReflectionTest.C.class.getDeclaredConstructor(ReflectionTest.A.class);
        String constructorOneRaw = ((((classC) + "(") + (classA)) + ") throws ") + (classB);
        TestCase.assertEquals(constructorOneRaw, constructorOne.toString());
        TestCase.assertEquals(constructorOneRaw, constructorOne.toGenericString());
        Constructor constructorTwo = ReflectionTest.C.class.getDeclaredConstructor(Map.class, Object.class);
        String constructorTwoRaw = ("protected " + (classC)) + "(java.util.Map,java.lang.Object)";
        TestCase.assertEquals(constructorTwoRaw, constructorTwo.toString());
        String constructorTwoGeneric = ((("protected <T1> " + (classC)) + "(java.util.Map<? super ") + (classA)) + ", T1>,K)";
        TestCase.assertEquals(constructorTwoGeneric, constructorTwo.toGenericString());
    }

    public void testMethodToString() throws Exception {
        Method methodOne = ReflectionTest.C.class.getDeclaredMethod("methodOne", ReflectionTest.A.class, ReflectionTest.C.class);
        String methodOneRaw = (((((((("protected final synchronized " + (classA)) + " ") + (classC)) + ".methodOne(") + (classA)) + ",") + (classC)) + ") throws ") + (classB);
        TestCase.assertEquals(methodOneRaw, methodOne.toString());
        TestCase.assertEquals(methodOneRaw, methodOne.toGenericString());
        Method methodTwo = ReflectionTest.C.class.getDeclaredMethod("methodTwo", List.class);
        String methodTwoRaw = ("public abstract java.util.Map " + (classC)) + ".methodTwo(java.util.List)";
        TestCase.assertEquals(methodTwoRaw, methodTwo.toString());
        String methodTwoGeneric = ((((("public abstract java.util.Map<" + (classA)) + ", java.lang.String> ") + (classC)) + ".methodTwo(java.util.List<") + (classA)) + ">)";
        TestCase.assertEquals(methodTwoGeneric, methodTwo.toGenericString());
        Method methodThree = ReflectionTest.C.class.getDeclaredMethod("methodThree", ReflectionTest.A.class, Set.class);
        String methodThreeRaw = ((("private static java.util.Map " + (classC)) + ".methodThree(") + (classA)) + ",java.util.Set)";
        TestCase.assertEquals(methodThreeRaw, methodThree.toString());
        String methodThreeGeneric = ("private static <T1,T2> java.util.Map<T1, ?> " + (classC)) + ".methodThree(T1,java.util.Set<? super T2>)";
        TestCase.assertEquals(methodThreeGeneric, methodThree.toGenericString());
        Method methodFour = ReflectionTest.C.class.getDeclaredMethod("methodFour", Set.class);
        String methodFourRaw = ("public java.lang.Comparable " + (classC)) + ".methodFour(java.util.Set)";
        TestCase.assertEquals(methodFourRaw, methodFour.toString());
        String methodFourGeneric = ("public <T> T " + (classC)) + ".methodFour(java.util.Set<T>)";
        TestCase.assertEquals(methodFourGeneric, methodFour.toGenericString());
    }

    public void testTypeVariableWithMultipleBounds() throws Exception {
        TypeVariable t = ReflectionTest.C.class.getDeclaredMethod("methodFour", Set.class).getTypeParameters()[0];
        TestCase.assertEquals("T", t.toString());
        Type[] bounds = t.getBounds();
        ParameterizedType comparableT = ((ParameterizedType) (bounds[0]));
        TestCase.assertEquals(Comparable.class, comparableT.getRawType());
        TestCase.assertEquals("T", ((TypeVariable) (comparableT.getActualTypeArguments()[0])).getName());
        TestCase.assertEquals(3, bounds.length);
        TestCase.assertEquals(Serializable.class, bounds[1]);
        TestCase.assertEquals(RandomAccess.class, bounds[2]);
    }

    public void testGetFieldNotFound() throws Exception {
        try {
            ReflectionTest.D.class.getField("noField");
            TestCase.fail();
        } catch (NoSuchFieldException expected) {
        }
    }

    public void testGetDeclaredFieldNotFound() throws Exception {
        try {
            ReflectionTest.D.class.getDeclaredField("noField");
            TestCase.fail();
        } catch (NoSuchFieldException expected) {
        }
    }

    public void testGetFieldNull() throws Exception {
        try {
            ReflectionTest.D.class.getField(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testGetDeclaredFieldNull() throws Exception {
        try {
            ReflectionTest.D.class.getDeclaredField(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
    }

    public void testGetFieldIsRecursive() throws Exception {
        Field field = ReflectionTest.D.class.getField("fieldOne");
        TestCase.assertEquals(ReflectionTest.C.class, field.getDeclaringClass());
    }

    public void testGetDeclaredFieldIsNotRecursive() {
        try {
            ReflectionTest.D.class.getDeclaredField("fieldOne");
            TestCase.fail();
        } catch (NoSuchFieldException expected) {
        }
    }

    public void testGetFieldIsPublicOnly() throws Exception {
        ReflectionTest.C.class.getField("fieldOne");// public

        try {
            ReflectionTest.C.class.getField("fieldTwo");// private

            TestCase.fail();
        } catch (NoSuchFieldException expected) {
        }
        try {
            ReflectionTest.C.class.getField("fieldThree");// protected

            TestCase.fail();
        } catch (NoSuchFieldException expected) {
        }
        try {
            ReflectionTest.C.class.getField("fieldFour");// package-private

            TestCase.fail();
        } catch (NoSuchFieldException expected) {
        }
    }

    public void testGetDeclaredFieldIsAllVisibilities() throws Exception {
        ReflectionTest.C.class.getDeclaredField("fieldOne");// public

        ReflectionTest.C.class.getDeclaredField("fieldTwo");// private

        ReflectionTest.C.class.getDeclaredField("fieldThree");// protected

        ReflectionTest.C.class.getDeclaredField("fieldFour");// package-private

    }

    public void testGetFieldViaExtendsThenImplements() throws Exception {
        Field field = ReflectionTest.ExtendsImplementsDefinesMember.class.getField("field");
        TestCase.assertEquals(ReflectionTest.DefinesMember.class, field.getDeclaringClass());
    }

    public void testGetFieldViaImplementsThenExtends() throws Exception {
        Field field = ReflectionTest.ImplementsExtendsDefinesMember.class.getField("field");
        TestCase.assertEquals(ReflectionTest.DefinesMember.class, field.getDeclaringClass());
    }

    public void testGetFieldsViaExtendsThenImplements() throws Exception {
        Field[] fields = ReflectionTest.ExtendsImplementsDefinesMember.class.getFields();
        TestCase.assertTrue(names(fields).contains("field"));
    }

    public void testGetFieldsViaImplementsThenExtends() throws Exception {
        Field[] fields = ReflectionTest.ImplementsExtendsDefinesMember.class.getFields();
        TestCase.assertTrue(names(fields).contains("field"));
    }

    public void testGetMethodViaExtendsThenImplements() throws Exception {
        Method method = ReflectionTest.ExtendsImplementsDefinesMember.class.getMethod("method");
        TestCase.assertEquals(ReflectionTest.DefinesMember.class, method.getDeclaringClass());
    }

    public void testGetMethodViaImplementsThenExtends() throws Exception {
        Method method = ReflectionTest.ImplementsExtendsDefinesMember.class.getMethod("method");
        TestCase.assertEquals(ReflectionTest.DefinesMember.class, method.getDeclaringClass());
    }

    public void testGetMethodsViaExtendsThenImplements() throws Exception {
        Method[] methods = ReflectionTest.ExtendsImplementsDefinesMember.class.getMethods();
        TestCase.assertTrue(names(methods).contains("method"));
    }

    public void testGetMethodsViaImplementsThenExtends() throws Exception {
        Method[] methods = ReflectionTest.ImplementsExtendsDefinesMember.class.getMethods();
        TestCase.assertTrue(names(methods).contains("method"));
    }

    public void testGetMethodsContainsNoDuplicates() throws Exception {
        Method[] methods = ReflectionTest.ExtendsAndImplementsDefinesMember.class.getMethods();
        TestCase.assertEquals(1, count(names(methods), "method"));
    }

    public void testGetFieldsContainsNoDuplicates() throws Exception {
        Field[] fields = ReflectionTest.ExtendsAndImplementsDefinesMember.class.getFields();
        TestCase.assertEquals(1, count(names(fields), "field"));
    }

    /**
     * Class.isEnum() erroneously returned true for indirect descendants of
     * Enum. http://b/1062200.
     */
    public void testClassIsEnum() {
        Class<?> trafficClass = ReflectionTest.TrafficLights.class;
        Class<?> redClass = ReflectionTest.TrafficLights.RED.getClass();
        Class<?> yellowClass = ReflectionTest.TrafficLights.YELLOW.getClass();
        Class<?> greenClass = ReflectionTest.TrafficLights.GREEN.getClass();
        TestCase.assertSame(trafficClass, redClass);
        TestCase.assertNotSame(trafficClass, yellowClass);
        TestCase.assertNotSame(trafficClass, greenClass);
        TestCase.assertNotSame(yellowClass, greenClass);
        TestCase.assertTrue(trafficClass.isEnum());
        TestCase.assertTrue(redClass.isEnum());
        TestCase.assertFalse(yellowClass.isEnum());
        TestCase.assertFalse(greenClass.isEnum());
        TestCase.assertNotNull(trafficClass.getEnumConstants());
        TestCase.assertNull(yellowClass.getEnumConstants());
        TestCase.assertNull(greenClass.getEnumConstants());
    }

    static class A {}

    static class AList extends ArrayList<ReflectionTest.A> {}

    static class B extends Exception {}

    public abstract static class C<K> {
        public static ReflectionTest.A fieldOne;

        private volatile transient Map<ReflectionTest.A, String> fieldTwo;

        protected K[] fieldThree;

        Map<? super Integer, Integer[]> fieldFour;

        String[][][][][] fieldFive;

        C(ReflectionTest.A a) throws ReflectionTest.B {
        }

        protected <T1 extends ReflectionTest.A> C(Map<? super ReflectionTest.A, T1> a, K s) {
        }

        protected final synchronized ReflectionTest.A methodOne(ReflectionTest.A parameterOne, ReflectionTest.C parameterTwo) throws ReflectionTest.B {
            return null;
        }

        public abstract Map<ReflectionTest.A, String> methodTwo(List<ReflectionTest.A> onlyParameter);

        /**
         * this annotation is used because it has runtime retention
         */
        @Deprecated
        private static <T1 extends ReflectionTest.A, T2> Map<T1, ?> methodThree(T1 t, Set<? super T2> t2s) {
            return null;
        }

        public <T extends Comparable<T> & Serializable & RandomAccess> T methodFour(Set<T> t) {
            return null;
        }
    }

    public static class D extends ReflectionTest.C<String> {
        public D(ReflectionTest.A a) throws ReflectionTest.B {
            super(a);
        }

        @Override
        public Map<ReflectionTest.A, String> methodTwo(List<ReflectionTest.A> onlyParameter) {
            return null;
        }
    }

    interface DefinesMember {
        String field = "s";

        void method();
    }

    abstract static class ImplementsDefinesMember implements ReflectionTest.DefinesMember {}

    abstract static class ExtendsImplementsDefinesMember extends ReflectionTest.ImplementsDefinesMember {}

    interface ExtendsDefinesMember extends ReflectionTest.DefinesMember {}

    abstract static class ImplementsExtendsDefinesMember implements ReflectionTest.ExtendsDefinesMember {}

    abstract static class ExtendsAndImplementsDefinesMember extends ReflectionTest.ImplementsDefinesMember implements ReflectionTest.DefinesMember {}

    enum TrafficLights {

        RED,
        YELLOW() {},
        GREEN() {
            @SuppressWarnings("unused")
            int i;

            @SuppressWarnings("unused")
            void foobar() {
            }
        };}
}

