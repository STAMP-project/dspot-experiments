/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.beans;


import ExtendedBeanInfo.PropertyDescriptorComparator;
import java.awt.Window;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.1
 */
public class ExtendedBeanInfoTests {
    @Test
    public void standardReadMethodOnly() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            public String getFoo() {
                return null;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        ExtendedBeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(false));
    }

    @Test
    public void standardWriteMethodOnly() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            public void setFoo(String f) {
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        ExtendedBeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
    }

    @Test
    public void standardReadAndWriteMethods() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            public void setFoo(String f) {
            }

            public String getFoo() {
                return null;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        ExtendedBeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
    }

    @Test
    public void nonStandardWriteMethodOnly() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            public C setFoo(String foo) {
                return this;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        ExtendedBeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
    }

    @Test
    public void standardReadAndNonStandardWriteMethods() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            public String getFoo() {
                return null;
            }

            public C setFoo(String foo) {
                return this;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        ExtendedBeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
    }

    @Test
    public void standardReadAndNonStandardIndexedWriteMethod() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            public String[] getFoo() {
                return null;
            }

            public C setFoo(int i, String foo) {
                return this;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
    }

    @Test
    public void standardReadMethodsAndOverloadedNonStandardWriteMethods() throws Exception {
        @SuppressWarnings("unused")
        class C {
            public String getFoo() {
                return null;
            }

            public C setFoo(String foo) {
                return this;
            }

            public C setFoo(Number foo) {
                return this;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        ExtendedBeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        for (PropertyDescriptor pd : ebi.getPropertyDescriptors()) {
            if (pd.getName().equals("foo")) {
                Assert.assertThat(pd.getWriteMethod(), CoreMatchers.is(C.class.getMethod("setFoo", String.class)));
                return;
            }
        }
        Assert.fail("never matched write method");
    }

    @Test
    public void cornerSpr9414() throws IntrospectionException {
        @SuppressWarnings("unused")
        class Parent {
            public Number getProperty1() {
                return 1;
            }
        }
        class Child extends Parent {
            @Override
            public Integer getProperty1() {
                return 2;
            }
        }
        {
            // always passes
            ExtendedBeanInfo bi = new ExtendedBeanInfo(Introspector.getBeanInfo(Parent.class));
            Assert.assertThat(hasReadMethodForProperty(bi, "property1"), CoreMatchers.is(true));
        }
        {
            // failed prior to fix for SPR-9414
            ExtendedBeanInfo bi = new ExtendedBeanInfo(Introspector.getBeanInfo(Child.class));
            Assert.assertThat(hasReadMethodForProperty(bi, "property1"), CoreMatchers.is(true));
        }
    }

    @Test
    public void cornerSpr9453() throws IntrospectionException {
        final class Bean implements ExtendedBeanInfoTests.Spr9453<Class<?>> {
            @Override
            public Class<?> getProp() {
                return null;
            }
        }
        {
            // always passes
            BeanInfo info = Introspector.getBeanInfo(Bean.class);
            Assert.assertThat(info.getPropertyDescriptors().length, CoreMatchers.equalTo(2));
        }
        {
            // failed prior to fix for SPR-9453
            BeanInfo info = new ExtendedBeanInfo(Introspector.getBeanInfo(Bean.class));
            Assert.assertThat(info.getPropertyDescriptors().length, CoreMatchers.equalTo(2));
        }
    }

    @Test
    public void standardReadMethodInSuperclassAndNonStandardWriteMethodInSubclass() throws Exception {
        @SuppressWarnings("unused")
        class B {
            public String getFoo() {
                return null;
            }
        }
        @SuppressWarnings("unused")
        class C extends B {
            public C setFoo(String foo) {
                return this;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        ExtendedBeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
    }

    @Test
    public void standardReadMethodInSuperAndSubclassesAndGenericBuilderStyleNonStandardWriteMethodInSuperAndSubclasses() throws Exception {
        abstract class B<This extends B<This>> {
            @SuppressWarnings("unchecked")
            protected final This instance = ((This) (this));

            private String foo;

            public String getFoo() {
                return foo;
            }

            public This setFoo(String foo) {
                this.foo = foo;
                return this.instance;
            }
        }
        class C extends B<C> {
            private int bar = -1;

            public int getBar() {
                return bar;
            }

            public C setBar(int bar) {
                this.bar = bar;
                return this.instance;
            }
        }
        C c = new C().setFoo("blue").setBar(42);
        Assert.assertThat(c.getFoo(), CoreMatchers.is("blue"));
        Assert.assertThat(c.getBar(), CoreMatchers.is(42));
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(bi, "bar"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "bar"), CoreMatchers.is(false));
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(bi, "bar"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "bar"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasReadMethodForProperty(ebi, "bar"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "bar"), CoreMatchers.is(true));
    }

    @Test
    public void nonPublicStandardReadAndWriteMethods() throws Exception {
        @SuppressWarnings("unused")
        class C {
            String getFoo() {
                return null;
            }

            C setFoo(String foo) {
                return this;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(false));
    }

    /**
     * {@link ExtendedBeanInfo} should behave exactly like {@link BeanInfo}
     * in strange edge cases.
     */
    @Test
    public void readMethodReturnsSupertypeOfWriteMethodParameter() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            public Number getFoo() {
                return null;
            }

            public void setFoo(Integer foo) {
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        Assert.assertEquals(hasWriteMethodForProperty(bi, "foo"), hasWriteMethodForProperty(ebi, "foo"));
    }

    @Test
    public void indexedReadMethodReturnsSupertypeOfIndexedWriteMethodParameter() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            public Number getFoos(int index) {
                return null;
            }

            public void setFoos(int index, Integer foo) {
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasIndexedReadMethodForProperty(bi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasIndexedReadMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
        Assert.assertEquals(hasIndexedWriteMethodForProperty(bi, "foos"), hasIndexedWriteMethodForProperty(ebi, "foos"));
    }

    /**
     * {@link ExtendedBeanInfo} should behave exactly like {@link BeanInfo}
     * in strange edge cases.
     */
    @Test
    public void readMethodReturnsSubtypeOfWriteMethodParameter() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            public Integer getFoo() {
                return null;
            }

            public void setFoo(Number foo) {
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(false));
    }

    @Test
    public void indexedReadMethodReturnsSubtypeOfIndexedWriteMethodParameter() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            public Integer getFoos(int index) {
                return null;
            }

            public void setFoo(int index, Number foo) {
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasIndexedReadMethodForProperty(bi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "foos"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedReadMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasIndexedWriteMethodForProperty(ebi, "foos"), CoreMatchers.is(false));
    }

    @Test
    public void indexedReadMethodOnly() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            // indexed read method
            public String getFoos(int i) {
                return null;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        BeanInfo ebi = new ExtendedBeanInfo(Introspector.getBeanInfo(C.class));
        Assert.assertThat(hasReadMethodForProperty(bi, "foos"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedReadMethodForProperty(bi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foos"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedReadMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
    }

    @Test
    public void indexedWriteMethodOnly() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            // indexed write method
            public void setFoos(int i, String foo) {
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        BeanInfo ebi = new ExtendedBeanInfo(Introspector.getBeanInfo(C.class));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foos"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foos"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedWriteMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
    }

    @Test
    public void indexedReadAndIndexedWriteMethods() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            // indexed read method
            public String getFoos(int i) {
                return null;
            }

            // indexed write method
            public void setFoos(int i, String foo) {
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        BeanInfo ebi = new ExtendedBeanInfo(Introspector.getBeanInfo(C.class));
        Assert.assertThat(hasReadMethodForProperty(bi, "foos"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedReadMethodForProperty(bi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foos"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foos"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedReadMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foos"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedWriteMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
    }

    @Test
    public void readAndWriteAndIndexedReadAndIndexedWriteMethods() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            // read method
            public String[] getFoos() {
                return null;
            }

            // indexed read method
            public String getFoos(int i) {
                return null;
            }

            // write method
            public void setFoos(String[] foos) {
            }

            // indexed write method
            public void setFoos(int i, String foo) {
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        BeanInfo ebi = new ExtendedBeanInfo(Introspector.getBeanInfo(C.class));
        Assert.assertThat(hasReadMethodForProperty(bi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasIndexedReadMethodForProperty(bi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasIndexedReadMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasIndexedWriteMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
    }

    @Test
    public void indexedReadAndNonStandardIndexedWrite() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            // indexed read method
            public String getFoos(int i) {
                return null;
            }

            // non-standard indexed write method
            public C setFoos(int i, String foo) {
                return this;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        Assert.assertThat(hasIndexedReadMethodForProperty(bi, "foos"), CoreMatchers.is(true));
        // interesting! standard Inspector picks up non-void return types on indexed write methods by default
        Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "foos"), CoreMatchers.is(false));
        BeanInfo ebi = new ExtendedBeanInfo(Introspector.getBeanInfo(C.class));
        Assert.assertThat(hasIndexedReadMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasIndexedWriteMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
    }

    @Test
    public void indexedReadAndNonStandardWriteAndNonStandardIndexedWrite() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            // non-standard write method
            public C setFoos(String[] foos) {
                return this;
            }

            // indexed read method
            public String getFoos(int i) {
                return null;
            }

            // non-standard indexed write method
            public C setFoos(int i, String foo) {
                return this;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        Assert.assertThat(hasIndexedReadMethodForProperty(bi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foos"), CoreMatchers.is(false));
        // again as above, standard Inspector picks up non-void return types on indexed write methods by default
        Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "foos"), CoreMatchers.is(false));
        BeanInfo ebi = new ExtendedBeanInfo(Introspector.getBeanInfo(C.class));
        Assert.assertThat(hasIndexedReadMethodForProperty(bi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foos"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "foos"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedReadMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
        Assert.assertThat(hasIndexedWriteMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
    }

    @Test
    public void cornerSpr9702() throws IntrospectionException {
        {
            // baseline with standard write method
            @SuppressWarnings("unused")
            class C {
                // VOID-RETURNING, NON-INDEXED write method
                public void setFoos(String[] foos) {
                }

                // indexed read method
                public String getFoos(int i) {
                    return null;
                }
            }
            BeanInfo bi = Introspector.getBeanInfo(C.class);
            Assert.assertThat(hasReadMethodForProperty(bi, "foos"), CoreMatchers.is(false));
            Assert.assertThat(hasIndexedReadMethodForProperty(bi, "foos"), CoreMatchers.is(true));
            Assert.assertThat(hasWriteMethodForProperty(bi, "foos"), CoreMatchers.is(true));
            Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "foos"), CoreMatchers.is(false));
            BeanInfo ebi = Introspector.getBeanInfo(C.class);
            Assert.assertThat(hasReadMethodForProperty(ebi, "foos"), CoreMatchers.is(false));
            Assert.assertThat(hasIndexedReadMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
            Assert.assertThat(hasWriteMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
            Assert.assertThat(hasIndexedWriteMethodForProperty(ebi, "foos"), CoreMatchers.is(false));
        }
        {
            // variant with non-standard write method
            @SuppressWarnings("unused")
            class C {
                // NON-VOID-RETURNING, NON-INDEXED write method
                public C setFoos(String[] foos) {
                    return this;
                }

                // indexed read method
                public String getFoos(int i) {
                    return null;
                }
            }
            BeanInfo bi = Introspector.getBeanInfo(C.class);
            Assert.assertThat(hasReadMethodForProperty(bi, "foos"), CoreMatchers.is(false));
            Assert.assertThat(hasIndexedReadMethodForProperty(bi, "foos"), CoreMatchers.is(true));
            Assert.assertThat(hasWriteMethodForProperty(bi, "foos"), CoreMatchers.is(false));
            Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "foos"), CoreMatchers.is(false));
            BeanInfo ebi = new ExtendedBeanInfo(Introspector.getBeanInfo(C.class));
            Assert.assertThat(hasReadMethodForProperty(ebi, "foos"), CoreMatchers.is(false));
            Assert.assertThat(hasIndexedReadMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
            Assert.assertThat(hasWriteMethodForProperty(ebi, "foos"), CoreMatchers.is(true));
            Assert.assertThat(hasIndexedWriteMethodForProperty(ebi, "foos"), CoreMatchers.is(false));
        }
    }

    /**
     * Prior to SPR-10111 (a follow-up fix for SPR-9702), this method would throw an
     * IntrospectionException regarding a "type mismatch between indexed and non-indexed
     * methods" intermittently (approximately one out of every four times) under JDK 7
     * due to non-deterministic results from {@link Class#getDeclaredMethods()}.
     * See http://bugs.sun.com/view_bug.do?bug_id=7023180
     *
     * @see #cornerSpr9702()
     */
    @Test
    public void cornerSpr10111() throws Exception {
        new ExtendedBeanInfo(Introspector.getBeanInfo(BigDecimal.class));
    }

    @Test
    public void subclassWriteMethodWithCovariantReturnType() throws IntrospectionException {
        @SuppressWarnings("unused")
        class B {
            public String getFoo() {
                return null;
            }

            public Number setFoo(String foo) {
                return null;
            }
        }
        class C extends B {
            @Override
            public String getFoo() {
                return null;
            }

            @Override
            public Integer setFoo(String foo) {
                return null;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(ebi.getPropertyDescriptors().length, CoreMatchers.equalTo(bi.getPropertyDescriptors().length));
    }

    @Test
    public void nonStandardReadMethodAndStandardWriteMethod() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            public void getFoo() {
            }

            public void setFoo(String foo) {
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(true));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
    }

    /**
     * Ensures that an empty string is not passed into a PropertyDescriptor constructor. This
     * could occur when handling ArrayList.set(int,Object)
     */
    @Test
    public void emptyPropertiesIgnored() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            public Object set(Object o) {
                return null;
            }

            public Object set(int i, Object o) {
                return null;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(ebi.getPropertyDescriptors(), CoreMatchers.equalTo(bi.getPropertyDescriptors()));
    }

    @Test
    public void overloadedNonStandardWriteMethodsOnly_orderA() throws IntrospectionException, NoSuchMethodException, SecurityException {
        @SuppressWarnings("unused")
        class C {
            public Object setFoo(String p) {
                return new Object();
            }

            public Object setFoo(int p) {
                return new Object();
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        for (PropertyDescriptor pd : ebi.getPropertyDescriptors()) {
            if (pd.getName().equals("foo")) {
                Assert.assertThat(pd.getWriteMethod(), CoreMatchers.is(C.class.getMethod("setFoo", String.class)));
                return;
            }
        }
        Assert.fail("never matched write method");
    }

    @Test
    public void overloadedNonStandardWriteMethodsOnly_orderB() throws IntrospectionException, NoSuchMethodException, SecurityException {
        @SuppressWarnings("unused")
        class C {
            public Object setFoo(int p) {
                return new Object();
            }

            public Object setFoo(String p) {
                return new Object();
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(bi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(ebi, "foo"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "foo"), CoreMatchers.is(true));
        for (PropertyDescriptor pd : ebi.getPropertyDescriptors()) {
            if (pd.getName().equals("foo")) {
                Assert.assertThat(pd.getWriteMethod(), CoreMatchers.is(C.class.getMethod("setFoo", String.class)));
                return;
            }
        }
        Assert.fail("never matched write method");
    }

    /**
     * Corners the bug revealed by SPR-8522, in which an (apparently) indexed write method
     * without a corresponding indexed read method would fail to be processed correctly by
     * ExtendedBeanInfo. The local class C below represents the relevant methods from
     * Google's GsonBuilder class. Interestingly, the setDateFormat(int, int) method was
     * not actually intended to serve as an indexed write method; it just appears that way.
     */
    @Test
    public void reproSpr8522() throws IntrospectionException {
        @SuppressWarnings("unused")
        class C {
            public Object setDateFormat(String pattern) {
                return new Object();
            }

            public Object setDateFormat(int style) {
                return new Object();
            }

            public Object setDateFormat(int dateStyle, int timeStyle) {
                return new Object();
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(C.class);
        Assert.assertThat(hasReadMethodForProperty(bi, "dateFormat"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(bi, "dateFormat"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedReadMethodForProperty(bi, "dateFormat"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "dateFormat"), CoreMatchers.is(false));
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "dateFormat"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(bi, "dateFormat"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedReadMethodForProperty(bi, "dateFormat"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "dateFormat"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(ebi, "dateFormat"), CoreMatchers.is(false));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "dateFormat"), CoreMatchers.is(true));
        Assert.assertThat(hasIndexedReadMethodForProperty(ebi, "dateFormat"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedWriteMethodForProperty(ebi, "dateFormat"), CoreMatchers.is(false));
    }

    @Test
    public void propertyCountsMatch() throws IntrospectionException {
        BeanInfo bi = Introspector.getBeanInfo(TestBean.class);
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(ebi.getPropertyDescriptors().length, CoreMatchers.equalTo(bi.getPropertyDescriptors().length));
    }

    @Test
    public void propertyCountsWithNonStandardWriteMethod() throws IntrospectionException {
        class ExtendedTestBean extends TestBean {
            @SuppressWarnings("unused")
            public ExtendedTestBean setFoo(String s) {
                return this;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(ExtendedTestBean.class);
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        boolean found = false;
        for (PropertyDescriptor pd : ebi.getPropertyDescriptors()) {
            if (pd.getName().equals("foo")) {
                found = true;
            }
        }
        Assert.assertThat(found, CoreMatchers.is(true));
        Assert.assertThat(ebi.getPropertyDescriptors().length, CoreMatchers.equalTo(((bi.getPropertyDescriptors().length) + 1)));
    }

    /**
     * {@link BeanInfo#getPropertyDescriptors()} returns alphanumerically sorted.
     * Test that {@link ExtendedBeanInfo#getPropertyDescriptors()} does the same.
     */
    @Test
    public void propertyDescriptorOrderIsEqual() throws IntrospectionException {
        BeanInfo bi = Introspector.getBeanInfo(TestBean.class);
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        for (int i = 0; i < (bi.getPropertyDescriptors().length); i++) {
            Assert.assertThat((("element " + i) + " in BeanInfo and ExtendedBeanInfo propertyDescriptor arrays do not match"), ebi.getPropertyDescriptors()[i].getName(), CoreMatchers.equalTo(bi.getPropertyDescriptors()[i].getName()));
        }
    }

    @Test
    public void propertyDescriptorComparator() throws IntrospectionException {
        ExtendedBeanInfo.PropertyDescriptorComparator c = new ExtendedBeanInfo.PropertyDescriptorComparator();
        Assert.assertThat(c.compare(new PropertyDescriptor("a", null, null), new PropertyDescriptor("a", null, null)), CoreMatchers.equalTo(0));
        Assert.assertThat(c.compare(new PropertyDescriptor("abc", null, null), new PropertyDescriptor("abc", null, null)), CoreMatchers.equalTo(0));
        Assert.assertThat(c.compare(new PropertyDescriptor("a", null, null), new PropertyDescriptor("b", null, null)), lessThan(0));
        Assert.assertThat(c.compare(new PropertyDescriptor("b", null, null), new PropertyDescriptor("a", null, null)), greaterThan(0));
        Assert.assertThat(c.compare(new PropertyDescriptor("abc", null, null), new PropertyDescriptor("abd", null, null)), lessThan(0));
        Assert.assertThat(c.compare(new PropertyDescriptor("xyz", null, null), new PropertyDescriptor("123", null, null)), greaterThan(0));
        Assert.assertThat(c.compare(new PropertyDescriptor("a", null, null), new PropertyDescriptor("abc", null, null)), lessThan(0));
        Assert.assertThat(c.compare(new PropertyDescriptor("abc", null, null), new PropertyDescriptor("a", null, null)), greaterThan(0));
        Assert.assertThat(c.compare(new PropertyDescriptor("abc", null, null), new PropertyDescriptor("b", null, null)), lessThan(0));
        Assert.assertThat(c.compare(new PropertyDescriptor(" ", null, null), new PropertyDescriptor("a", null, null)), lessThan(0));
        Assert.assertThat(c.compare(new PropertyDescriptor("1", null, null), new PropertyDescriptor("a", null, null)), lessThan(0));
        Assert.assertThat(c.compare(new PropertyDescriptor("a", null, null), new PropertyDescriptor("A", null, null)), greaterThan(0));
    }

    @Test
    public void reproSpr8806() throws IntrospectionException {
        // does not throw
        Introspector.getBeanInfo(ExtendedBeanInfoTests.LawLibrary.class);
        // does not throw after the changes introduced in SPR-8806
        new ExtendedBeanInfo(Introspector.getBeanInfo(ExtendedBeanInfoTests.LawLibrary.class));
    }

    @Test
    public void cornerSpr8949() throws IntrospectionException {
        class A {
            @SuppressWarnings("unused")
            public boolean isTargetMethod() {
                return false;
            }
        }
        class B extends A {
            @Override
            public boolean isTargetMethod() {
                return false;
            }
        }
        BeanInfo bi = Introspector.getBeanInfo(B.class);
        // method equality. Spring's {@link ClassUtils#getMostSpecificMethod(Method, Class)}
        // helps out here, and is now put into use in ExtendedBeanInfo as well.
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertThat(hasReadMethodForProperty(bi, "targetMethod"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "targetMethod"), CoreMatchers.is(false));
        Assert.assertThat(hasReadMethodForProperty(ebi, "targetMethod"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(ebi, "targetMethod"), CoreMatchers.is(false));
    }

    @Test
    public void cornerSpr8937AndSpr12582() throws IntrospectionException {
        @SuppressWarnings("unused")
        class A {
            public void setAddress(String addr) {
            }

            public void setAddress(int index, String addr) {
            }

            public String getAddress(int index) {
                return null;
            }
        }
        // Baseline:
        BeanInfo bi = Introspector.getBeanInfo(A.class);
        boolean hasReadMethod = hasReadMethodForProperty(bi, "address");
        boolean hasWriteMethod = hasWriteMethodForProperty(bi, "address");
        boolean hasIndexedReadMethod = hasIndexedReadMethodForProperty(bi, "address");
        boolean hasIndexedWriteMethod = hasIndexedWriteMethodForProperty(bi, "address");
        // ExtendedBeanInfo needs to behave exactly like BeanInfo...
        BeanInfo ebi = new ExtendedBeanInfo(bi);
        Assert.assertEquals(hasReadMethod, hasReadMethodForProperty(ebi, "address"));
        Assert.assertEquals(hasWriteMethod, hasWriteMethodForProperty(ebi, "address"));
        Assert.assertEquals(hasIndexedReadMethod, hasIndexedReadMethodForProperty(ebi, "address"));
        Assert.assertEquals(hasIndexedWriteMethod, hasIndexedWriteMethodForProperty(ebi, "address"));
    }

    @Test
    public void shouldSupportStaticWriteMethod() throws IntrospectionException {
        {
            BeanInfo bi = Introspector.getBeanInfo(ExtendedBeanInfoTests.WithStaticWriteMethod.class);
            Assert.assertThat(hasReadMethodForProperty(bi, "prop1"), CoreMatchers.is(false));
            Assert.assertThat(hasWriteMethodForProperty(bi, "prop1"), CoreMatchers.is(false));
            Assert.assertThat(hasIndexedReadMethodForProperty(bi, "prop1"), CoreMatchers.is(false));
            Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "prop1"), CoreMatchers.is(false));
        }
        {
            BeanInfo bi = new ExtendedBeanInfo(Introspector.getBeanInfo(ExtendedBeanInfoTests.WithStaticWriteMethod.class));
            Assert.assertThat(hasReadMethodForProperty(bi, "prop1"), CoreMatchers.is(false));
            Assert.assertThat(hasWriteMethodForProperty(bi, "prop1"), CoreMatchers.is(true));
            Assert.assertThat(hasIndexedReadMethodForProperty(bi, "prop1"), CoreMatchers.is(false));
            Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "prop1"), CoreMatchers.is(false));
        }
    }

    // SPR-12434
    @Test
    public void shouldDetectValidPropertiesAndIgnoreInvalidProperties() throws IntrospectionException {
        BeanInfo bi = new ExtendedBeanInfo(Introspector.getBeanInfo(Window.class));
        Assert.assertThat(hasReadMethodForProperty(bi, "locationByPlatform"), CoreMatchers.is(true));
        Assert.assertThat(hasWriteMethodForProperty(bi, "locationByPlatform"), CoreMatchers.is(true));
        Assert.assertThat(hasIndexedReadMethodForProperty(bi, "locationByPlatform"), CoreMatchers.is(false));
        Assert.assertThat(hasIndexedWriteMethodForProperty(bi, "locationByPlatform"), CoreMatchers.is(false));
    }

    interface Spr9453<T> {
        T getProp();
    }

    interface Book {}

    interface TextBook extends ExtendedBeanInfoTests.Book {}

    interface LawBook extends ExtendedBeanInfoTests.TextBook {}

    interface BookOperations {
        ExtendedBeanInfoTests.Book getBook();

        void setBook(ExtendedBeanInfoTests.Book book);
    }

    interface TextBookOperations extends ExtendedBeanInfoTests.BookOperations {
        @Override
        ExtendedBeanInfoTests.TextBook getBook();
    }

    abstract class Library {
        public ExtendedBeanInfoTests.Book getBook() {
            return null;
        }

        public void setBook(ExtendedBeanInfoTests.Book book) {
        }
    }

    class LawLibrary extends ExtendedBeanInfoTests.Library implements ExtendedBeanInfoTests.TextBookOperations {
        @Override
        public ExtendedBeanInfoTests.LawBook getBook() {
            return null;
        }
    }

    static class WithStaticWriteMethod {
        public static void setProp1(String prop1) {
        }
    }
}

