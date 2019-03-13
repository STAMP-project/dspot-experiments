/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.util;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.Assume;
import org.springframework.tests.TestGroup;
import org.springframework.tests.sample.objects.TestObject;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Arjen Poutsma
 */
public class ReflectionUtilsTests {
    @Test
    public void findField() {
        Field field = ReflectionUtils.findField(ReflectionUtilsTests.TestObjectSubclassWithPublicField.class, "publicField", String.class);
        Assert.assertNotNull(field);
        Assert.assertEquals("publicField", field.getName());
        Assert.assertEquals(String.class, field.getType());
        Assert.assertTrue("Field should be public.", Modifier.isPublic(field.getModifiers()));
        field = ReflectionUtils.findField(ReflectionUtilsTests.TestObjectSubclassWithNewField.class, "prot", String.class);
        Assert.assertNotNull(field);
        Assert.assertEquals("prot", field.getName());
        Assert.assertEquals(String.class, field.getType());
        Assert.assertTrue("Field should be protected.", Modifier.isProtected(field.getModifiers()));
        field = ReflectionUtils.findField(ReflectionUtilsTests.TestObjectSubclassWithNewField.class, "name", String.class);
        Assert.assertNotNull(field);
        Assert.assertEquals("name", field.getName());
        Assert.assertEquals(String.class, field.getType());
        Assert.assertTrue("Field should be private.", Modifier.isPrivate(field.getModifiers()));
    }

    @Test
    public void setField() {
        ReflectionUtilsTests.TestObjectSubclassWithNewField testBean = new ReflectionUtilsTests.TestObjectSubclassWithNewField();
        Field field = ReflectionUtils.findField(ReflectionUtilsTests.TestObjectSubclassWithNewField.class, "name", String.class);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, testBean, "FooBar");
        Assert.assertNotNull(testBean.getName());
        Assert.assertEquals("FooBar", testBean.getName());
        ReflectionUtils.setField(field, testBean, null);
        Assert.assertNull(testBean.getName());
    }

    @Test
    public void invokeMethod() throws Exception {
        String rob = "Rob Harrop";
        TestObject bean = new TestObject();
        bean.setName(rob);
        Method getName = TestObject.class.getMethod("getName");
        Method setName = TestObject.class.getMethod("setName", String.class);
        Object name = ReflectionUtils.invokeMethod(getName, bean);
        Assert.assertEquals("Incorrect name returned", rob, name);
        String juergen = "Juergen Hoeller";
        ReflectionUtils.invokeMethod(setName, bean, juergen);
        Assert.assertEquals("Incorrect name set", juergen, bean.getName());
    }

    @Test
    public void declaresException() throws Exception {
        Method remoteExMethod = ReflectionUtilsTests.A.class.getDeclaredMethod("foo", Integer.class);
        Assert.assertTrue(ReflectionUtils.declaresException(remoteExMethod, RemoteException.class));
        Assert.assertTrue(ReflectionUtils.declaresException(remoteExMethod, ConnectException.class));
        Assert.assertFalse(ReflectionUtils.declaresException(remoteExMethod, NoSuchMethodException.class));
        Assert.assertFalse(ReflectionUtils.declaresException(remoteExMethod, Exception.class));
        Method illegalExMethod = ReflectionUtilsTests.B.class.getDeclaredMethod("bar", String.class);
        Assert.assertTrue(ReflectionUtils.declaresException(illegalExMethod, IllegalArgumentException.class));
        Assert.assertTrue(ReflectionUtils.declaresException(illegalExMethod, NumberFormatException.class));
        Assert.assertFalse(ReflectionUtils.declaresException(illegalExMethod, IllegalStateException.class));
        Assert.assertFalse(ReflectionUtils.declaresException(illegalExMethod, Exception.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void copySrcToDestinationOfIncorrectClass() {
        TestObject src = new TestObject();
        String dest = new String();
        ReflectionUtils.shallowCopyFieldState(src, dest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullSrc() {
        TestObject src = null;
        String dest = new String();
        ReflectionUtils.shallowCopyFieldState(src, dest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullDest() {
        TestObject src = new TestObject();
        String dest = null;
        ReflectionUtils.shallowCopyFieldState(src, dest);
    }

    @Test
    public void validCopy() {
        TestObject src = new TestObject();
        TestObject dest = new TestObject();
        testValidCopy(src, dest);
    }

    @Test
    public void validCopyOnSubTypeWithNewField() {
        ReflectionUtilsTests.TestObjectSubclassWithNewField src = new ReflectionUtilsTests.TestObjectSubclassWithNewField();
        ReflectionUtilsTests.TestObjectSubclassWithNewField dest = new ReflectionUtilsTests.TestObjectSubclassWithNewField();
        src.magic = 11;
        // Will check inherited fields are copied
        testValidCopy(src, dest);
        // Check subclass fields were copied
        Assert.assertEquals(src.magic, dest.magic);
        Assert.assertEquals(src.prot, dest.prot);
    }

    @Test
    public void validCopyToSubType() {
        TestObject src = new TestObject();
        ReflectionUtilsTests.TestObjectSubclassWithNewField dest = new ReflectionUtilsTests.TestObjectSubclassWithNewField();
        dest.magic = 11;
        testValidCopy(src, dest);
        // Should have left this one alone
        Assert.assertEquals(11, dest.magic);
    }

    @Test
    public void validCopyToSubTypeWithFinalField() {
        ReflectionUtilsTests.TestObjectSubclassWithFinalField src = new ReflectionUtilsTests.TestObjectSubclassWithFinalField();
        ReflectionUtilsTests.TestObjectSubclassWithFinalField dest = new ReflectionUtilsTests.TestObjectSubclassWithFinalField();
        // Check that this doesn't fail due to attempt to assign final
        testValidCopy(src, dest);
    }

    @Test
    public void doWithProtectedMethods() {
        ReflectionUtilsTests.ListSavingMethodCallback mc = new ReflectionUtilsTests.ListSavingMethodCallback();
        ReflectionUtils.doWithMethods(TestObject.class, mc, new ReflectionUtils.MethodFilter() {
            @Override
            public boolean matches(Method m) {
                return Modifier.isProtected(m.getModifiers());
            }
        });
        Assert.assertFalse(mc.getMethodNames().isEmpty());
        Assert.assertTrue("Must find protected method on Object", mc.getMethodNames().contains("clone"));
        Assert.assertTrue("Must find protected method on Object", mc.getMethodNames().contains("finalize"));
        Assert.assertFalse("Public, not protected", mc.getMethodNames().contains("hashCode"));
        Assert.assertFalse("Public, not protected", mc.getMethodNames().contains("absquatulate"));
    }

    @Test
    public void duplicatesFound() {
        ReflectionUtilsTests.ListSavingMethodCallback mc = new ReflectionUtilsTests.ListSavingMethodCallback();
        ReflectionUtils.doWithMethods(ReflectionUtilsTests.TestObjectSubclass.class, mc);
        int absquatulateCount = 0;
        for (String name : mc.getMethodNames()) {
            if (name.equals("absquatulate")) {
                ++absquatulateCount;
            }
        }
        Assert.assertEquals("Found 2 absquatulates", 2, absquatulateCount);
    }

    @Test
    public void findMethod() throws Exception {
        Assert.assertNotNull(ReflectionUtils.findMethod(ReflectionUtilsTests.B.class, "bar", String.class));
        Assert.assertNotNull(ReflectionUtils.findMethod(ReflectionUtilsTests.B.class, "foo", Integer.class));
        Assert.assertNotNull(ReflectionUtils.findMethod(ReflectionUtilsTests.B.class, "getClass"));
    }

    @Test
    public void isCglibRenamedMethod() throws NoSuchMethodException, SecurityException {
        @SuppressWarnings("unused")
        class C {
            public void CGLIB$m1$123() {
            }

            public void CGLIB$m1$0() {
            }

            public void CGLIB$$0() {
            }

            public void CGLIB$m1$() {
            }

            public void CGLIB$m1() {
            }

            public void m1() {
            }

            public void m1$() {
            }

            public void m1$1() {
            }
        }
        Assert.assertTrue(ReflectionUtils.isCglibRenamedMethod(C.class.getMethod("CGLIB$m1$123")));
        Assert.assertTrue(ReflectionUtils.isCglibRenamedMethod(C.class.getMethod("CGLIB$m1$0")));
        Assert.assertFalse(ReflectionUtils.isCglibRenamedMethod(C.class.getMethod("CGLIB$$0")));
        Assert.assertFalse(ReflectionUtils.isCglibRenamedMethod(C.class.getMethod("CGLIB$m1$")));
        Assert.assertFalse(ReflectionUtils.isCglibRenamedMethod(C.class.getMethod("CGLIB$m1")));
        Assert.assertFalse(ReflectionUtils.isCglibRenamedMethod(C.class.getMethod("m1")));
        Assert.assertFalse(ReflectionUtils.isCglibRenamedMethod(C.class.getMethod("m1$")));
        Assert.assertFalse(ReflectionUtils.isCglibRenamedMethod(C.class.getMethod("m1$1")));
    }

    @Test
    public void getAllDeclaredMethods() throws Exception {
        class Foo {
            @Override
            public String toString() {
                return super.toString();
            }
        }
        int toStringMethodCount = 0;
        for (Method method : ReflectionUtils.getAllDeclaredMethods(Foo.class)) {
            if (method.getName().equals("toString")) {
                toStringMethodCount++;
            }
        }
        Assert.assertThat(toStringMethodCount, CoreMatchers.is(2));
    }

    @Test
    public void getUniqueDeclaredMethods() throws Exception {
        class Foo {
            @Override
            public String toString() {
                return super.toString();
            }
        }
        int toStringMethodCount = 0;
        for (Method method : ReflectionUtils.getUniqueDeclaredMethods(Foo.class)) {
            if (method.getName().equals("toString")) {
                toStringMethodCount++;
            }
        }
        Assert.assertThat(toStringMethodCount, CoreMatchers.is(1));
    }

    @Test
    public void getUniqueDeclaredMethods_withCovariantReturnType() throws Exception {
        class Parent {
            @SuppressWarnings("unused")
            public Number m1() {
                return Integer.valueOf(42);
            }
        }
        class Leaf extends Parent {
            @Override
            public Integer m1() {
                return Integer.valueOf(42);
            }
        }
        int m1MethodCount = 0;
        Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(Leaf.class);
        for (Method method : methods) {
            if (method.getName().equals("m1")) {
                m1MethodCount++;
            }
        }
        Assert.assertThat(m1MethodCount, CoreMatchers.is(1));
        Assert.assertTrue(ObjectUtils.containsElement(methods, Leaf.class.getMethod("m1")));
        Assert.assertFalse(ObjectUtils.containsElement(methods, Parent.class.getMethod("m1")));
    }

    @Test
    public void getUniqueDeclaredMethods_isFastEnough() {
        Assume.group(TestGroup.PERFORMANCE);
        @SuppressWarnings("unused")
        class C {
            void m00() {
            }

            void m01() {
            }

            void m02() {
            }

            void m03() {
            }

            void m04() {
            }

            void m05() {
            }

            void m06() {
            }

            void m07() {
            }

            void m08() {
            }

            void m09() {
            }

            void m10() {
            }

            void m11() {
            }

            void m12() {
            }

            void m13() {
            }

            void m14() {
            }

            void m15() {
            }

            void m16() {
            }

            void m17() {
            }

            void m18() {
            }

            void m19() {
            }

            void m20() {
            }

            void m21() {
            }

            void m22() {
            }

            void m23() {
            }

            void m24() {
            }

            void m25() {
            }

            void m26() {
            }

            void m27() {
            }

            void m28() {
            }

            void m29() {
            }

            void m30() {
            }

            void m31() {
            }

            void m32() {
            }

            void m33() {
            }

            void m34() {
            }

            void m35() {
            }

            void m36() {
            }

            void m37() {
            }

            void m38() {
            }

            void m39() {
            }

            void m40() {
            }

            void m41() {
            }

            void m42() {
            }

            void m43() {
            }

            void m44() {
            }

            void m45() {
            }

            void m46() {
            }

            void m47() {
            }

            void m48() {
            }

            void m49() {
            }

            void m50() {
            }

            void m51() {
            }

            void m52() {
            }

            void m53() {
            }

            void m54() {
            }

            void m55() {
            }

            void m56() {
            }

            void m57() {
            }

            void m58() {
            }

            void m59() {
            }

            void m60() {
            }

            void m61() {
            }

            void m62() {
            }

            void m63() {
            }

            void m64() {
            }

            void m65() {
            }

            void m66() {
            }

            void m67() {
            }

            void m68() {
            }

            void m69() {
            }

            void m70() {
            }

            void m71() {
            }

            void m72() {
            }

            void m73() {
            }

            void m74() {
            }

            void m75() {
            }

            void m76() {
            }

            void m77() {
            }

            void m78() {
            }

            void m79() {
            }

            void m80() {
            }

            void m81() {
            }

            void m82() {
            }

            void m83() {
            }

            void m84() {
            }

            void m85() {
            }

            void m86() {
            }

            void m87() {
            }

            void m88() {
            }

            void m89() {
            }

            void m90() {
            }

            void m91() {
            }

            void m92() {
            }

            void m93() {
            }

            void m94() {
            }

            void m95() {
            }

            void m96() {
            }

            void m97() {
            }

            void m98() {
            }

            void m99() {
            }
        }
        StopWatch sw = new StopWatch();
        sw.start();
        Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(C.class);
        sw.stop();
        long totalMs = sw.getTotalTimeMillis();
        Assert.assertThat(methods.length, Matchers.greaterThan(100));
        Assert.assertThat(totalMs, Matchers.lessThan(10L));
    }

    private static class ListSavingMethodCallback implements ReflectionUtils.MethodCallback {
        private List<String> methodNames = new LinkedList<>();

        private List<Method> methods = new LinkedList<>();

        @Override
        public void doWith(Method m) throws IllegalAccessException, IllegalArgumentException {
            this.methodNames.add(m.getName());
            this.methods.add(m);
        }

        public List<String> getMethodNames() {
            return this.methodNames;
        }

        @SuppressWarnings("unused")
        public List<Method> getMethods() {
            return this.methods;
        }
    }

    private static class TestObjectSubclass extends TestObject {
        @Override
        public void absquatulate() {
            throw new UnsupportedOperationException();
        }
    }

    private static class TestObjectSubclassWithPublicField extends TestObject {
        @SuppressWarnings("unused")
        public String publicField = "foo";
    }

    private static class TestObjectSubclassWithNewField extends TestObject {
        private int magic;

        protected String prot = "foo";
    }

    private static class TestObjectSubclassWithFinalField extends TestObject {
        @SuppressWarnings("unused")
        private final String foo = "will break naive copy that doesn't exclude statics";
    }

    private static class A {
        @SuppressWarnings("unused")
        private void foo(Integer i) throws RemoteException {
        }
    }

    @SuppressWarnings("unused")
    private static class B extends ReflectionUtilsTests.A {
        void bar(String s) throws IllegalArgumentException {
        }

        int add(int... args) {
            int sum = 0;
            for (int i = 0; i < (args.length); i++) {
                sum += args[i];
            }
            return sum;
        }
    }
}

