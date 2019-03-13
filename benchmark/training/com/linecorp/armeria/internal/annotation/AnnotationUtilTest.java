/**
 * Copyright 2019 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.internal.annotation;


import FindOption.COLLECT_SUPER_CLASSES_FIRST;
import FindOption.LOOKUP_META_ANNOTATIONS;
import FindOption.LOOKUP_SUPER_CLASSES;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.EnumSet;
import java.util.List;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import org.junit.Test;


public class AnnotationUtilTest {
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestMetaOfMetaAnnotation {}

    @AnnotationUtilTest.TestMetaOfMetaAnnotation
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestMetaAnnotation {
        String value();
    }

    @AnnotationUtilTest.TestMetaAnnotation("TestAnnotation")
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestAnnotation {
        String value();
    }

    @AnnotationUtilTest.TestMetaAnnotation("TestRepeatables")
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestRepeatables {
        AnnotationUtilTest.TestRepeatable[] value();
    }

    @AnnotationUtilTest.TestMetaAnnotation("TestRepeatable")
    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(AnnotationUtilTest.TestRepeatables.class)
    @interface TestRepeatable {
        String value();
    }

    @AnnotationUtilTest.TestRepeatable("class-level:TestClass:Repeatable1")
    @AnnotationUtilTest.TestRepeatable("class-level:TestClass:Repeatable2")
    @AnnotationUtilTest.TestAnnotation("class-level:TestClass")
    static class TestClass {
        @AnnotationUtilTest.TestAnnotation("method-level:directlyPresent")
        public void directlyPresent() {
        }

        @AnnotationUtilTest.TestRepeatable("method-level:directlyPresentRepeatableSingle")
        public void directlyPresentRepeatableSingle() {
        }

        @AnnotationUtilTest.TestRepeatable("method-level:directlyPresentRepeatableMulti:1")
        @AnnotationUtilTest.TestRepeatable("method-level:directlyPresentRepeatableMulti:2")
        @AnnotationUtilTest.TestRepeatable("method-level:directlyPresentRepeatableMulti:3")
        public void directlyPresentRepeatableMulti() {
        }
    }

    @AnnotationUtilTest.TestRepeatable("class-level:TestChildClass:Repeatable1")
    @AnnotationUtilTest.TestRepeatable("class-level:TestChildClass:Repeatable2")
    @AnnotationUtilTest.TestAnnotation("class-level:TestChildClass")
    static class TestChildClass extends AnnotationUtilTest.TestClass {}

    @AnnotationUtilTest.TestRepeatable("class-level:TestGrandChildClass:Repeatable1")
    @AnnotationUtilTest.TestRepeatable("class-level:TestGrandChildClass:Repeatable2")
    @AnnotationUtilTest.TestAnnotation("class-level:TestGrandChildClass")
    static class TestGrandChildClass extends AnnotationUtilTest.TestChildClass {}

    @AnnotationUtilTest.TestRepeatable("class-level:SingleRepeatableTestClass")
    static class SingleRepeatableTestClass {}

    @AnnotationUtilTest.TestRepeatable("class-level:SingleRepeatableTestChildClass")
    static class SingleRepeatableTestChildClass extends AnnotationUtilTest.SingleRepeatableTestClass {}

    @AnnotationUtilTest.TestRepeatable("class-level:SingleRepeatableTestGrandChildClass")
    static class SingleRepeatableTestGrandChildClass extends AnnotationUtilTest.SingleRepeatableTestChildClass {}

    @AnnotationUtilTest.TestAnnotation("TestIface")
    interface TestIface {
        default void action() {
        }
    }

    @AnnotationUtilTest.TestAnnotation("TestChildIface")
    interface TestChildIface extends AnnotationUtilTest.TestIface {
        default void moreAction() {
        }
    }

    @AnnotationUtilTest.TestAnnotation("TestAnotherIface")
    interface TestAnotherIface {
        default void anotherAction() {
        }
    }

    @AnnotationUtilTest.TestAnnotation("TestClassWithIface")
    static class TestClassWithIface implements AnnotationUtilTest.TestChildIface {}

    @AnnotationUtilTest.TestAnnotation("TestChildClassWithIface")
    static class TestChildClassWithIface extends AnnotationUtilTest.TestClassWithIface implements AnnotationUtilTest.TestAnotherIface {}

    @AnnotationUtilTest.CyclicAnnotation
    @Retention(RetentionPolicy.RUNTIME)
    @interface CyclicAnnotation {}

    @AnnotationUtilTest.CyclicAnnotation
    static class TestClassWithCyclicAnnotation {
        @AnnotationUtilTest.CyclicAnnotation
        public void foo() {
        }
    }

    @Test
    public void declared() throws NoSuchMethodException {
        List<AnnotationUtilTest.TestAnnotation> list;
        list = AnnotationUtil.findDeclared(AnnotationUtilTest.TestClass.class.getMethod("directlyPresent"), AnnotationUtilTest.TestAnnotation.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).value()).isEqualTo("method-level:directlyPresent");
        list = AnnotationUtil.findDeclared(AnnotationUtilTest.TestClass.class, AnnotationUtilTest.TestAnnotation.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).value()).isEqualTo("class-level:TestClass");
        list = AnnotationUtil.findDeclared(AnnotationUtilTest.TestChildClass.class, AnnotationUtilTest.TestAnnotation.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).value()).isEqualTo("class-level:TestChildClass");
        list = AnnotationUtil.findDeclared(AnnotationUtilTest.TestGrandChildClass.class, AnnotationUtilTest.TestAnnotation.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).value()).isEqualTo("class-level:TestGrandChildClass");
    }

    @Test
    public void declared_repeatable() throws NoSuchMethodException {
        List<AnnotationUtilTest.TestRepeatable> list;
        list = AnnotationUtil.findDeclared(AnnotationUtilTest.TestClass.class.getMethod("directlyPresentRepeatableSingle"), AnnotationUtilTest.TestRepeatable.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).value()).isEqualTo("method-level:directlyPresentRepeatableSingle");
        list = AnnotationUtil.findDeclared(AnnotationUtilTest.SingleRepeatableTestClass.class, AnnotationUtilTest.TestRepeatable.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).value()).isEqualTo("class-level:SingleRepeatableTestClass");
        list = AnnotationUtil.findDeclared(AnnotationUtilTest.SingleRepeatableTestChildClass.class, AnnotationUtilTest.TestRepeatable.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).value()).isEqualTo("class-level:SingleRepeatableTestChildClass");
        list = AnnotationUtil.findDeclared(AnnotationUtilTest.SingleRepeatableTestGrandChildClass.class, AnnotationUtilTest.TestRepeatable.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).value()).isEqualTo("class-level:SingleRepeatableTestGrandChildClass");
    }

    @Test
    public void declared_repeatable_multi() throws NoSuchMethodException {
        List<AnnotationUtilTest.TestRepeatable> list;
        list = AnnotationUtil.findDeclared(AnnotationUtilTest.TestClass.class.getMethod("directlyPresentRepeatableMulti"), AnnotationUtilTest.TestRepeatable.class);
        assertThat(list).hasSize(3);
        assertThat(list.get(0).value()).isEqualTo("method-level:directlyPresentRepeatableMulti:1");
        assertThat(list.get(1).value()).isEqualTo("method-level:directlyPresentRepeatableMulti:2");
        assertThat(list.get(2).value()).isEqualTo("method-level:directlyPresentRepeatableMulti:3");
        list = AnnotationUtil.findDeclared(AnnotationUtilTest.TestClass.class, AnnotationUtilTest.TestRepeatable.class);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).value()).isEqualTo("class-level:TestClass:Repeatable1");
        assertThat(list.get(1).value()).isEqualTo("class-level:TestClass:Repeatable2");
        list = AnnotationUtil.findDeclared(AnnotationUtilTest.TestChildClass.class, AnnotationUtilTest.TestRepeatable.class);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).value()).isEqualTo("class-level:TestChildClass:Repeatable1");
        assertThat(list.get(1).value()).isEqualTo("class-level:TestChildClass:Repeatable2");
        list = AnnotationUtil.findDeclared(AnnotationUtilTest.TestGrandChildClass.class, AnnotationUtilTest.TestRepeatable.class);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).value()).isEqualTo("class-level:TestGrandChildClass:Repeatable1");
        assertThat(list.get(1).value()).isEqualTo("class-level:TestGrandChildClass:Repeatable2");
    }

    @Test
    public void lookupSuperClass() {
        List<AnnotationUtilTest.TestAnnotation> list;
        list = AnnotationUtil.findInherited(AnnotationUtilTest.TestClass.class, AnnotationUtilTest.TestAnnotation.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).value()).isEqualTo("class-level:TestClass");
        list = AnnotationUtil.findInherited(AnnotationUtilTest.TestChildClass.class, AnnotationUtilTest.TestAnnotation.class);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).value()).isEqualTo("class-level:TestChildClass");
        assertThat(list.get(1).value()).isEqualTo("class-level:TestClass");
        list = AnnotationUtil.findInherited(AnnotationUtilTest.TestGrandChildClass.class, AnnotationUtilTest.TestAnnotation.class);
        assertThat(list).hasSize(3);
        assertThat(list.get(0).value()).isEqualTo("class-level:TestGrandChildClass");
        assertThat(list.get(1).value()).isEqualTo("class-level:TestChildClass");
        assertThat(list.get(2).value()).isEqualTo("class-level:TestClass");
        list = AnnotationUtil.find(AnnotationUtilTest.TestGrandChildClass.class, AnnotationUtilTest.TestAnnotation.class, LOOKUP_SUPER_CLASSES, COLLECT_SUPER_CLASSES_FIRST);
        assertThat(list).hasSize(3);
        assertThat(list.get(0).value()).isEqualTo("class-level:TestClass");
        assertThat(list.get(1).value()).isEqualTo("class-level:TestChildClass");
        assertThat(list.get(2).value()).isEqualTo("class-level:TestGrandChildClass");
    }

    @Test
    public void lookupSuperClass_repeatable() {
        List<AnnotationUtilTest.TestRepeatable> list;
        list = AnnotationUtil.findInherited(AnnotationUtilTest.SingleRepeatableTestClass.class, AnnotationUtilTest.TestRepeatable.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).value()).isEqualTo("class-level:SingleRepeatableTestClass");
        list = AnnotationUtil.findInherited(AnnotationUtilTest.SingleRepeatableTestChildClass.class, AnnotationUtilTest.TestRepeatable.class);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).value()).isEqualTo("class-level:SingleRepeatableTestChildClass");
        assertThat(list.get(1).value()).isEqualTo("class-level:SingleRepeatableTestClass");
        list = AnnotationUtil.findInherited(AnnotationUtilTest.SingleRepeatableTestGrandChildClass.class, AnnotationUtilTest.TestRepeatable.class);
        assertThat(list).hasSize(3);
        assertThat(list.get(0).value()).isEqualTo("class-level:SingleRepeatableTestGrandChildClass");
        assertThat(list.get(1).value()).isEqualTo("class-level:SingleRepeatableTestChildClass");
        assertThat(list.get(2).value()).isEqualTo("class-level:SingleRepeatableTestClass");
        list = AnnotationUtil.find(AnnotationUtilTest.SingleRepeatableTestGrandChildClass.class, AnnotationUtilTest.TestRepeatable.class, LOOKUP_SUPER_CLASSES, COLLECT_SUPER_CLASSES_FIRST);
        assertThat(list).hasSize(3);
        assertThat(list.get(0).value()).isEqualTo("class-level:SingleRepeatableTestClass");
        assertThat(list.get(1).value()).isEqualTo("class-level:SingleRepeatableTestChildClass");
        assertThat(list.get(2).value()).isEqualTo("class-level:SingleRepeatableTestGrandChildClass");
    }

    @Test
    public void lookupSuperClass_repeatable_multi() {
        List<AnnotationUtilTest.TestRepeatable> list;
        list = AnnotationUtil.findInherited(AnnotationUtilTest.TestClass.class, AnnotationUtilTest.TestRepeatable.class);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).value()).isEqualTo("class-level:TestClass:Repeatable1");
        assertThat(list.get(1).value()).isEqualTo("class-level:TestClass:Repeatable2");
        list = AnnotationUtil.findInherited(AnnotationUtilTest.TestChildClass.class, AnnotationUtilTest.TestRepeatable.class);
        assertThat(list).hasSize(4);
        assertThat(list.get(0).value()).isEqualTo("class-level:TestChildClass:Repeatable1");
        assertThat(list.get(1).value()).isEqualTo("class-level:TestChildClass:Repeatable2");
        assertThat(list.get(2).value()).isEqualTo("class-level:TestClass:Repeatable1");
        assertThat(list.get(3).value()).isEqualTo("class-level:TestClass:Repeatable2");
        list = AnnotationUtil.findInherited(AnnotationUtilTest.TestGrandChildClass.class, AnnotationUtilTest.TestRepeatable.class);
        assertThat(list).hasSize(6);
        assertThat(list.get(0).value()).isEqualTo("class-level:TestGrandChildClass:Repeatable1");
        assertThat(list.get(1).value()).isEqualTo("class-level:TestGrandChildClass:Repeatable2");
        assertThat(list.get(2).value()).isEqualTo("class-level:TestChildClass:Repeatable1");
        assertThat(list.get(3).value()).isEqualTo("class-level:TestChildClass:Repeatable2");
        assertThat(list.get(4).value()).isEqualTo("class-level:TestClass:Repeatable1");
        assertThat(list.get(5).value()).isEqualTo("class-level:TestClass:Repeatable2");
        list = AnnotationUtil.find(AnnotationUtilTest.TestGrandChildClass.class, AnnotationUtilTest.TestRepeatable.class, LOOKUP_SUPER_CLASSES, COLLECT_SUPER_CLASSES_FIRST);
        assertThat(list).hasSize(6);
        assertThat(list.get(0).value()).isEqualTo("class-level:TestClass:Repeatable1");
        assertThat(list.get(1).value()).isEqualTo("class-level:TestClass:Repeatable2");
        assertThat(list.get(2).value()).isEqualTo("class-level:TestChildClass:Repeatable1");
        assertThat(list.get(3).value()).isEqualTo("class-level:TestChildClass:Repeatable2");
        assertThat(list.get(4).value()).isEqualTo("class-level:TestGrandChildClass:Repeatable1");
        assertThat(list.get(5).value()).isEqualTo("class-level:TestGrandChildClass:Repeatable2");
    }

    @Test
    public void lookupMetaAnnotations_declared() {
        List<AnnotationUtilTest.TestMetaAnnotation> list;
        for (final Class<?> clazz : ImmutableList.of(AnnotationUtilTest.TestClass.class, AnnotationUtilTest.TestChildClass.class, AnnotationUtilTest.TestGrandChildClass.class)) {
            list = AnnotationUtil.find(clazz, AnnotationUtilTest.TestMetaAnnotation.class, EnumSet.of(LOOKUP_META_ANNOTATIONS));
            assertThat(list).hasSize(2);
            assertThat(list.get(0).value()).isEqualTo("TestRepeatables");// From the container annotation.

            assertThat(list.get(1).value()).isEqualTo("TestAnnotation");
        }
        for (final Class<?> clazz : ImmutableList.of(AnnotationUtilTest.SingleRepeatableTestClass.class, AnnotationUtilTest.SingleRepeatableTestChildClass.class, AnnotationUtilTest.SingleRepeatableTestGrandChildClass.class)) {
            list = AnnotationUtil.find(clazz, AnnotationUtilTest.TestMetaAnnotation.class, EnumSet.of(LOOKUP_META_ANNOTATIONS));
            assertThat(list).hasSize(1);
            assertThat(list.get(0).value()).isEqualTo("TestRepeatable");
        }
    }

    @Test
    public void findAll_includingRepeatable() {
        List<AnnotationUtilTest.TestMetaAnnotation> list;
        list = AnnotationUtil.findAll(AnnotationUtilTest.SingleRepeatableTestClass.class, AnnotationUtilTest.TestMetaAnnotation.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).value()).isEqualTo("TestRepeatable");
        list = AnnotationUtil.findAll(AnnotationUtilTest.SingleRepeatableTestChildClass.class, AnnotationUtilTest.TestMetaAnnotation.class);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).value()).isEqualTo("TestRepeatable");
        assertThat(list.get(1).value()).isEqualTo("TestRepeatable");
        list = AnnotationUtil.findAll(AnnotationUtilTest.SingleRepeatableTestGrandChildClass.class, AnnotationUtilTest.TestMetaAnnotation.class);
        assertThat(list).hasSize(3);
        assertThat(list.get(0).value()).isEqualTo("TestRepeatable");
        assertThat(list.get(1).value()).isEqualTo("TestRepeatable");
        assertThat(list.get(2).value()).isEqualTo("TestRepeatable");
    }

    @Test
    public void findAll_includingRepeatable_multi() {
        List<AnnotationUtilTest.TestMetaAnnotation> list;
        list = AnnotationUtil.findAll(AnnotationUtilTest.TestClass.class, AnnotationUtilTest.TestMetaAnnotation.class);
        assertThat(list).hasSize(2);
        assertThat(list.get(0).value()).isEqualTo("TestRepeatables");
        assertThat(list.get(1).value()).isEqualTo("TestAnnotation");
        list = AnnotationUtil.findAll(AnnotationUtilTest.TestChildClass.class, AnnotationUtilTest.TestMetaAnnotation.class);
        assertThat(list).hasSize(4);
        assertThat(list.get(0).value()).isEqualTo("TestRepeatables");
        assertThat(list.get(1).value()).isEqualTo("TestAnnotation");
        assertThat(list.get(2).value()).isEqualTo("TestRepeatables");
        assertThat(list.get(3).value()).isEqualTo("TestAnnotation");
        list = AnnotationUtil.findAll(AnnotationUtilTest.TestGrandChildClass.class, AnnotationUtilTest.TestMetaAnnotation.class);
        assertThat(list).hasSize(6);
        assertThat(list.get(0).value()).isEqualTo("TestRepeatables");
        assertThat(list.get(1).value()).isEqualTo("TestAnnotation");
        assertThat(list.get(2).value()).isEqualTo("TestRepeatables");
        assertThat(list.get(3).value()).isEqualTo("TestAnnotation");
        assertThat(list.get(4).value()).isEqualTo("TestRepeatables");
        assertThat(list.get(5).value()).isEqualTo("TestAnnotation");
    }

    @Test
    public void findAll_interfaces() {
        List<AnnotationUtilTest.TestAnnotation> list;
        list = AnnotationUtil.findAll(AnnotationUtilTest.TestClassWithIface.class, AnnotationUtilTest.TestAnnotation.class);
        assertThat(list).hasSize(3);
        assertThat(list.get(0).value()).isEqualTo("TestClassWithIface");
        assertThat(list.get(1).value()).isEqualTo("TestChildIface");
        assertThat(list.get(2).value()).isEqualTo("TestIface");
        list = AnnotationUtil.findAll(AnnotationUtilTest.TestChildClassWithIface.class, AnnotationUtilTest.TestAnnotation.class);
        assertThat(list).hasSize(5);
        assertThat(list.get(0).value()).isEqualTo("TestChildClassWithIface");
        assertThat(list.get(1).value()).isEqualTo("TestAnotherIface");
        assertThat(list.get(2).value()).isEqualTo("TestClassWithIface");
        assertThat(list.get(3).value()).isEqualTo("TestChildIface");
        assertThat(list.get(4).value()).isEqualTo("TestIface");
    }

    @Test
    public void findAll_metaAnnotationOfMetaAnnotation() {
        List<AnnotationUtilTest.TestMetaOfMetaAnnotation> list;
        list = AnnotationUtil.findAll(AnnotationUtilTest.TestClass.class, AnnotationUtilTest.TestMetaOfMetaAnnotation.class);
        assertThat(list).hasSize(2);
        list = AnnotationUtil.findAll(AnnotationUtilTest.TestChildClass.class, AnnotationUtilTest.TestMetaOfMetaAnnotation.class);
        assertThat(list).hasSize(4);
        list = AnnotationUtil.findAll(AnnotationUtilTest.TestGrandChildClass.class, AnnotationUtilTest.TestMetaOfMetaAnnotation.class);
        assertThat(list).hasSize(6);
    }

    @Test
    public void findAll_cyclic() throws Exception {
        List<AnnotationUtilTest.CyclicAnnotation> list = AnnotationUtil.findAll(AnnotationUtilTest.TestClassWithCyclicAnnotation.class, AnnotationUtilTest.CyclicAnnotation.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isInstanceOf(AnnotationUtilTest.CyclicAnnotation.class);
        list = AnnotationUtil.findAll(AnnotationUtilTest.TestClassWithCyclicAnnotation.class.getDeclaredMethod("foo"), AnnotationUtilTest.CyclicAnnotation.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isInstanceOf(AnnotationUtilTest.CyclicAnnotation.class);
    }

    @Test
    public void cglibProxy() {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(AnnotationUtilTest.TestGrandChildClass.class);
        enhancer.setCallback(((FixedValue) (() -> null)));
        final AnnotationUtilTest.TestGrandChildClass proxy = ((AnnotationUtilTest.TestGrandChildClass) (enhancer.create()));
        // No declared annotations on proxy.
        assertThat(AnnotationUtil.findDeclared(proxy.getClass(), AnnotationUtilTest.TestAnnotation.class)).isEmpty();
        // No declared annotations on proxy, so no meta annotations as well.
        assertThat(AnnotationUtil.find(proxy.getClass(), AnnotationUtilTest.TestMetaAnnotation.class, EnumSet.of(LOOKUP_META_ANNOTATIONS))).isEmpty();
        final List<AnnotationUtilTest.TestAnnotation> list1 = AnnotationUtil.findAll(proxy.getClass(), AnnotationUtilTest.TestAnnotation.class);
        assertThat(list1).hasSize(3);
        assertThat(list1.get(0).value()).isEqualTo("class-level:TestGrandChildClass");
        assertThat(list1.get(1).value()).isEqualTo("class-level:TestChildClass");
        assertThat(list1.get(2).value()).isEqualTo("class-level:TestClass");
        final List<AnnotationUtilTest.TestMetaAnnotation> list2 = AnnotationUtil.findAll(proxy.getClass(), AnnotationUtilTest.TestMetaAnnotation.class);
        assertThat(list2).hasSize(6);
        assertThat(list2.get(0).value()).isEqualTo("TestRepeatables");
        assertThat(list2.get(1).value()).isEqualTo("TestAnnotation");
        assertThat(list2.get(2).value()).isEqualTo("TestRepeatables");
        assertThat(list2.get(3).value()).isEqualTo("TestAnnotation");
        assertThat(list2.get(4).value()).isEqualTo("TestRepeatables");
        assertThat(list2.get(5).value()).isEqualTo("TestAnnotation");
    }

    @Test
    public void getAnnotations_declared() {
        final List<Annotation> list = AnnotationUtil.getAnnotations(AnnotationUtilTest.TestGrandChildClass.class);
        assertThat(list).hasSize(2);
        assertThat(list.get(0)).isInstanceOf(AnnotationUtilTest.TestRepeatables.class);
        assertThat(list.get(1)).isInstanceOf(AnnotationUtilTest.TestAnnotation.class);
    }

    @Test
    public void getAnnotations_inherited() {
        final List<Annotation> list = AnnotationUtil.getAnnotations(AnnotationUtilTest.TestGrandChildClass.class, LOOKUP_SUPER_CLASSES);
        assertThat(list).hasSize(6);
        assertThat(list.get(0)).isInstanceOf(AnnotationUtilTest.TestRepeatables.class);
        assertThat(list.get(1)).isInstanceOf(AnnotationUtilTest.TestAnnotation.class);
        assertThat(list.get(2)).isInstanceOf(AnnotationUtilTest.TestRepeatables.class);
        assertThat(list.get(3)).isInstanceOf(AnnotationUtilTest.TestAnnotation.class);
        assertThat(list.get(4)).isInstanceOf(AnnotationUtilTest.TestRepeatables.class);
        assertThat(list.get(5)).isInstanceOf(AnnotationUtilTest.TestAnnotation.class);
    }

    @Test
    public void getAnnotations_all() {
        final List<Annotation> list = AnnotationUtil.getAllAnnotations(AnnotationUtilTest.TestGrandChildClass.class);
        assertThat(list).hasSize(18);
        for (int i = 0; i < (3 * 6);) {
            assertThat(list.get((i++))).isInstanceOf(AnnotationUtilTest.TestMetaOfMetaAnnotation.class);
            assertThat(list.get(i)).isInstanceOf(AnnotationUtilTest.TestMetaAnnotation.class);
            assertThat(((AnnotationUtilTest.TestMetaAnnotation) (list.get((i++)))).value()).isEqualTo("TestRepeatables");
            assertThat(list.get((i++))).isInstanceOf(AnnotationUtilTest.TestRepeatables.class);
            assertThat(list.get((i++))).isInstanceOf(AnnotationUtilTest.TestMetaOfMetaAnnotation.class);
            assertThat(list.get(i)).isInstanceOf(AnnotationUtilTest.TestMetaAnnotation.class);
            assertThat(((AnnotationUtilTest.TestMetaAnnotation) (list.get((i++)))).value()).isEqualTo("TestAnnotation");
            assertThat(list.get((i++))).isInstanceOf(AnnotationUtilTest.TestAnnotation.class);
        }
    }

    @Test
    public void getAnnotations_all_cyclic() throws Exception {
        List<Annotation> list = AnnotationUtil.getAllAnnotations(AnnotationUtilTest.TestClassWithCyclicAnnotation.class);
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isInstanceOf(AnnotationUtilTest.CyclicAnnotation.class);
        list = AnnotationUtil.getAllAnnotations(AnnotationUtilTest.TestClassWithCyclicAnnotation.class.getDeclaredMethod("foo"));
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isInstanceOf(AnnotationUtilTest.CyclicAnnotation.class);
    }
}

