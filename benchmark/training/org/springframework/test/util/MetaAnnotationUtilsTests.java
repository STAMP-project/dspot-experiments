/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.test.util;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;


/**
 * Unit tests for {@link MetaAnnotationUtils}.
 *
 * @author Sam Brannen
 * @since 4.0
 * @see OverriddenMetaAnnotationAttributesTests
 */
public class MetaAnnotationUtilsTests {
    @Test
    public void findAnnotationDescriptorWithNoAnnotationPresent() {
        Assert.assertNull(findAnnotationDescriptor(MetaAnnotationUtilsTests.NonAnnotatedInterface.class, Transactional.class));
        Assert.assertNull(findAnnotationDescriptor(MetaAnnotationUtilsTests.NonAnnotatedClass.class, Transactional.class));
    }

    @Test
    public void findAnnotationDescriptorWithInheritedAnnotationOnClass() {
        // Note: @Transactional is inherited
        Assert.assertEquals(MetaAnnotationUtilsTests.InheritedAnnotationClass.class, findAnnotationDescriptor(MetaAnnotationUtilsTests.InheritedAnnotationClass.class, Transactional.class).getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.InheritedAnnotationClass.class, findAnnotationDescriptor(MetaAnnotationUtilsTests.SubInheritedAnnotationClass.class, Transactional.class).getRootDeclaringClass());
    }

    @Test
    public void findAnnotationDescriptorWithInheritedAnnotationOnInterface() {
        // Note: @Transactional is inherited
        Transactional rawAnnotation = MetaAnnotationUtilsTests.InheritedAnnotationInterface.class.getAnnotation(Transactional.class);
        AnnotationDescriptor<Transactional> descriptor = findAnnotationDescriptor(MetaAnnotationUtilsTests.InheritedAnnotationInterface.class, Transactional.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(MetaAnnotationUtilsTests.InheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.InheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        Assert.assertEquals(rawAnnotation, descriptor.getAnnotation());
        descriptor = findAnnotationDescriptor(MetaAnnotationUtilsTests.SubInheritedAnnotationInterface.class, Transactional.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(MetaAnnotationUtilsTests.SubInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.InheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        Assert.assertEquals(rawAnnotation, descriptor.getAnnotation());
        descriptor = findAnnotationDescriptor(MetaAnnotationUtilsTests.SubSubInheritedAnnotationInterface.class, Transactional.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(MetaAnnotationUtilsTests.SubSubInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.InheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        Assert.assertEquals(rawAnnotation, descriptor.getAnnotation());
    }

    @Test
    public void findAnnotationDescriptorForNonInheritedAnnotationOnClass() {
        // Note: @Order is not inherited.
        Assert.assertEquals(MetaAnnotationUtilsTests.NonInheritedAnnotationClass.class, findAnnotationDescriptor(MetaAnnotationUtilsTests.NonInheritedAnnotationClass.class, Order.class).getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.NonInheritedAnnotationClass.class, findAnnotationDescriptor(MetaAnnotationUtilsTests.SubNonInheritedAnnotationClass.class, Order.class).getRootDeclaringClass());
    }

    @Test
    public void findAnnotationDescriptorForNonInheritedAnnotationOnInterface() {
        // Note: @Order is not inherited.
        Order rawAnnotation = MetaAnnotationUtilsTests.NonInheritedAnnotationInterface.class.getAnnotation(Order.class);
        AnnotationDescriptor<Order> descriptor = findAnnotationDescriptor(MetaAnnotationUtilsTests.NonInheritedAnnotationInterface.class, Order.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(MetaAnnotationUtilsTests.NonInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.NonInheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        Assert.assertEquals(rawAnnotation, descriptor.getAnnotation());
        descriptor = findAnnotationDescriptor(MetaAnnotationUtilsTests.SubNonInheritedAnnotationInterface.class, Order.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(MetaAnnotationUtilsTests.SubNonInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.NonInheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        Assert.assertEquals(rawAnnotation, descriptor.getAnnotation());
    }

    @Test
    public void findAnnotationDescriptorWithMetaComponentAnnotation() {
        assertAtComponentOnComposedAnnotation(MetaAnnotationUtilsTests.HasMetaComponentAnnotation.class, "meta1", MetaAnnotationUtilsTests.Meta1.class);
    }

    @Test
    public void findAnnotationDescriptorWithLocalAndMetaComponentAnnotation() {
        Class<Component> annotationType = Component.class;
        AnnotationDescriptor<Component> descriptor = findAnnotationDescriptor(MetaAnnotationUtilsTests.HasLocalAndMetaComponentAnnotation.class, annotationType);
        Assert.assertEquals(MetaAnnotationUtilsTests.HasLocalAndMetaComponentAnnotation.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals(annotationType, descriptor.getAnnotationType());
        Assert.assertNull(descriptor.getComposedAnnotation());
        Assert.assertNull(descriptor.getComposedAnnotationType());
    }

    @Test
    public void findAnnotationDescriptorForInterfaceWithMetaAnnotation() {
        assertAtComponentOnComposedAnnotation(MetaAnnotationUtilsTests.InterfaceWithMetaAnnotation.class, "meta1", MetaAnnotationUtilsTests.Meta1.class);
    }

    @Test
    public void findAnnotationDescriptorForClassWithMetaAnnotatedInterface() {
        Component rawAnnotation = AnnotationUtils.findAnnotation(MetaAnnotationUtilsTests.ClassWithMetaAnnotatedInterface.class, Component.class);
        AnnotationDescriptor<Component> descriptor = findAnnotationDescriptor(MetaAnnotationUtilsTests.ClassWithMetaAnnotatedInterface.class, Component.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(MetaAnnotationUtilsTests.ClassWithMetaAnnotatedInterface.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.Meta1.class, descriptor.getDeclaringClass());
        Assert.assertEquals(rawAnnotation, descriptor.getAnnotation());
        Assert.assertEquals(MetaAnnotationUtilsTests.Meta1.class, descriptor.getComposedAnnotation().annotationType());
    }

    @Test
    public void findAnnotationDescriptorForClassWithLocalMetaAnnotationAndAnnotatedSuperclass() {
        AnnotationDescriptor<ContextConfiguration> descriptor = findAnnotationDescriptor(MetaAnnotationUtilsTests.MetaAnnotatedAndSuperAnnotatedContextConfigClass.class, ContextConfiguration.class);
        Assert.assertNotNull("AnnotationDescriptor should not be null", descriptor);
        Assert.assertEquals("rootDeclaringClass", MetaAnnotationUtilsTests.MetaAnnotatedAndSuperAnnotatedContextConfigClass.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals("declaringClass", MetaAnnotationUtilsTests.MetaConfig.class, descriptor.getDeclaringClass());
        Assert.assertEquals("annotationType", ContextConfiguration.class, descriptor.getAnnotationType());
        Assert.assertNotNull("composedAnnotation should not be null", descriptor.getComposedAnnotation());
        Assert.assertEquals("composedAnnotationType", MetaAnnotationUtilsTests.MetaConfig.class, descriptor.getComposedAnnotationType());
        Assert.assertArrayEquals("configured classes", new Class<?>[]{ String.class }, descriptor.getAnnotationAttributes().getClassArray("classes"));
    }

    @Test
    public void findAnnotationDescriptorForClassWithLocalMetaAnnotationAndMetaAnnotatedInterface() {
        assertAtComponentOnComposedAnnotation(MetaAnnotationUtilsTests.ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface.class, "meta2", MetaAnnotationUtilsTests.Meta2.class);
    }

    @Test
    public void findAnnotationDescriptorForSubClassWithLocalMetaAnnotationAndMetaAnnotatedInterface() {
        assertAtComponentOnComposedAnnotation(MetaAnnotationUtilsTests.SubClassWithLocalMetaAnnotationAndMetaAnnotatedInterface.class, MetaAnnotationUtilsTests.ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface.class, "meta2", MetaAnnotationUtilsTests.Meta2.class);
    }

    /**
     *
     *
     * @since 4.0.3
     */
    @Test
    public void findAnnotationDescriptorOnMetaMetaAnnotatedClass() {
        Class<MetaAnnotationUtilsTests.MetaMetaAnnotatedClass> startClass = MetaAnnotationUtilsTests.MetaMetaAnnotatedClass.class;
        assertAtComponentOnComposedAnnotation(startClass, startClass, MetaAnnotationUtilsTests.Meta2.class, "meta2", MetaAnnotationUtilsTests.MetaMeta.class);
    }

    /**
     *
     *
     * @since 4.0.3
     */
    @Test
    public void findAnnotationDescriptorOnMetaMetaMetaAnnotatedClass() {
        Class<MetaAnnotationUtilsTests.MetaMetaMetaAnnotatedClass> startClass = MetaAnnotationUtilsTests.MetaMetaMetaAnnotatedClass.class;
        assertAtComponentOnComposedAnnotation(startClass, startClass, MetaAnnotationUtilsTests.Meta2.class, "meta2", MetaAnnotationUtilsTests.MetaMetaMeta.class);
    }

    /**
     *
     *
     * @since 4.0.3
     */
    @Test
    public void findAnnotationDescriptorOnAnnotatedClassWithMissingTargetMetaAnnotation() {
        // InheritedAnnotationClass is NOT annotated or meta-annotated with @Component
        AnnotationDescriptor<Component> descriptor = findAnnotationDescriptor(MetaAnnotationUtilsTests.InheritedAnnotationClass.class, Component.class);
        Assert.assertNull("Should not find @Component on InheritedAnnotationClass", descriptor);
    }

    /**
     *
     *
     * @since 4.0.3
     */
    @Test
    public void findAnnotationDescriptorOnMetaCycleAnnotatedClassWithMissingTargetMetaAnnotation() {
        AnnotationDescriptor<Component> descriptor = findAnnotationDescriptor(MetaAnnotationUtilsTests.MetaCycleAnnotatedClass.class, Component.class);
        Assert.assertNull("Should not find @Component on MetaCycleAnnotatedClass", descriptor);
    }

    // -------------------------------------------------------------------------
    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesWithNoAnnotationPresent() {
        Assert.assertNull(findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.NonAnnotatedInterface.class, Transactional.class, Component.class));
        Assert.assertNull(findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.NonAnnotatedClass.class, Transactional.class, Order.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesWithInheritedAnnotationOnClass() {
        // Note: @Transactional is inherited
        Assert.assertEquals(MetaAnnotationUtilsTests.InheritedAnnotationClass.class, findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.InheritedAnnotationClass.class, Transactional.class).getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.InheritedAnnotationClass.class, findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.SubInheritedAnnotationClass.class, Transactional.class).getRootDeclaringClass());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesWithInheritedAnnotationOnInterface() {
        // Note: @Transactional is inherited
        Transactional rawAnnotation = MetaAnnotationUtilsTests.InheritedAnnotationInterface.class.getAnnotation(Transactional.class);
        UntypedAnnotationDescriptor descriptor = findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.InheritedAnnotationInterface.class, Transactional.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(MetaAnnotationUtilsTests.InheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.InheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        Assert.assertEquals(rawAnnotation, descriptor.getAnnotation());
        descriptor = findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.SubInheritedAnnotationInterface.class, Transactional.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(MetaAnnotationUtilsTests.SubInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.InheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        Assert.assertEquals(rawAnnotation, descriptor.getAnnotation());
        descriptor = findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.SubSubInheritedAnnotationInterface.class, Transactional.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(MetaAnnotationUtilsTests.SubSubInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.InheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        Assert.assertEquals(rawAnnotation, descriptor.getAnnotation());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesForNonInheritedAnnotationOnClass() {
        // Note: @Order is not inherited.
        Assert.assertEquals(MetaAnnotationUtilsTests.NonInheritedAnnotationClass.class, findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.NonInheritedAnnotationClass.class, Order.class).getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.NonInheritedAnnotationClass.class, findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.SubNonInheritedAnnotationClass.class, Order.class).getRootDeclaringClass());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesForNonInheritedAnnotationOnInterface() {
        // Note: @Order is not inherited.
        Order rawAnnotation = MetaAnnotationUtilsTests.NonInheritedAnnotationInterface.class.getAnnotation(Order.class);
        UntypedAnnotationDescriptor descriptor = findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.NonInheritedAnnotationInterface.class, Order.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(MetaAnnotationUtilsTests.NonInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.NonInheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        Assert.assertEquals(rawAnnotation, descriptor.getAnnotation());
        descriptor = findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.SubNonInheritedAnnotationInterface.class, Order.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(MetaAnnotationUtilsTests.SubNonInheritedAnnotationInterface.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.NonInheritedAnnotationInterface.class, descriptor.getDeclaringClass());
        Assert.assertEquals(rawAnnotation, descriptor.getAnnotation());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesWithLocalAndMetaComponentAnnotation() {
        Class<Component> annotationType = Component.class;
        UntypedAnnotationDescriptor descriptor = findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.HasLocalAndMetaComponentAnnotation.class, Transactional.class, annotationType, Order.class);
        Assert.assertEquals(MetaAnnotationUtilsTests.HasLocalAndMetaComponentAnnotation.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals(annotationType, descriptor.getAnnotationType());
        Assert.assertNull(descriptor.getComposedAnnotation());
        Assert.assertNull(descriptor.getComposedAnnotationType());
    }

    @Test
    public void findAnnotationDescriptorForTypesWithMetaComponentAnnotation() {
        Class<MetaAnnotationUtilsTests.HasMetaComponentAnnotation> startClass = MetaAnnotationUtilsTests.HasMetaComponentAnnotation.class;
        assertAtComponentOnComposedAnnotationForMultipleCandidateTypes(startClass, "meta1", MetaAnnotationUtilsTests.Meta1.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesWithMetaAnnotationWithDefaultAttributes() {
        Class<?> startClass = MetaAnnotationUtilsTests.MetaConfigWithDefaultAttributesTestCase.class;
        Class<ContextConfiguration> annotationType = ContextConfiguration.class;
        UntypedAnnotationDescriptor descriptor = findAnnotationDescriptorForTypes(startClass, Service.class, ContextConfiguration.class, Order.class, Transactional.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(startClass, descriptor.getRootDeclaringClass());
        Assert.assertEquals(annotationType, descriptor.getAnnotationType());
        Assert.assertArrayEquals(new Class<?>[]{  }, value());
        Assert.assertArrayEquals(new Class<?>[]{ MetaAnnotationUtilsTests.MetaConfig.DevConfig.class, MetaAnnotationUtilsTests.MetaConfig.ProductionConfig.class }, descriptor.getAnnotationAttributes().getClassArray("classes"));
        Assert.assertNotNull(descriptor.getComposedAnnotation());
        Assert.assertEquals(MetaAnnotationUtilsTests.MetaConfig.class, descriptor.getComposedAnnotationType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesWithMetaAnnotationWithOverriddenAttributes() {
        Class<?> startClass = MetaAnnotationUtilsTests.MetaConfigWithOverriddenAttributesTestCase.class;
        Class<ContextConfiguration> annotationType = ContextConfiguration.class;
        UntypedAnnotationDescriptor descriptor = findAnnotationDescriptorForTypes(startClass, Service.class, ContextConfiguration.class, Order.class, Transactional.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(startClass, descriptor.getRootDeclaringClass());
        Assert.assertEquals(annotationType, descriptor.getAnnotationType());
        Assert.assertArrayEquals(new Class<?>[]{  }, value());
        Assert.assertArrayEquals(new Class<?>[]{ MetaAnnotationUtilsTests.class }, descriptor.getAnnotationAttributes().getClassArray("classes"));
        Assert.assertNotNull(descriptor.getComposedAnnotation());
        Assert.assertEquals(MetaAnnotationUtilsTests.MetaConfig.class, descriptor.getComposedAnnotationType());
    }

    @Test
    public void findAnnotationDescriptorForTypesForInterfaceWithMetaAnnotation() {
        Class<MetaAnnotationUtilsTests.InterfaceWithMetaAnnotation> startClass = MetaAnnotationUtilsTests.InterfaceWithMetaAnnotation.class;
        assertAtComponentOnComposedAnnotationForMultipleCandidateTypes(startClass, "meta1", MetaAnnotationUtilsTests.Meta1.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesForClassWithMetaAnnotatedInterface() {
        Component rawAnnotation = AnnotationUtils.findAnnotation(MetaAnnotationUtilsTests.ClassWithMetaAnnotatedInterface.class, Component.class);
        UntypedAnnotationDescriptor descriptor = findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.ClassWithMetaAnnotatedInterface.class, Service.class, Component.class, Order.class, Transactional.class);
        Assert.assertNotNull(descriptor);
        Assert.assertEquals(MetaAnnotationUtilsTests.ClassWithMetaAnnotatedInterface.class, descriptor.getRootDeclaringClass());
        Assert.assertEquals(MetaAnnotationUtilsTests.Meta1.class, descriptor.getDeclaringClass());
        Assert.assertEquals(rawAnnotation, descriptor.getAnnotation());
        Assert.assertEquals(MetaAnnotationUtilsTests.Meta1.class, descriptor.getComposedAnnotation().annotationType());
    }

    @Test
    public void findAnnotationDescriptorForTypesForClassWithLocalMetaAnnotationAndMetaAnnotatedInterface() {
        Class<MetaAnnotationUtilsTests.ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface> startClass = MetaAnnotationUtilsTests.ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface.class;
        assertAtComponentOnComposedAnnotationForMultipleCandidateTypes(startClass, "meta2", MetaAnnotationUtilsTests.Meta2.class);
    }

    @Test
    public void findAnnotationDescriptorForTypesForSubClassWithLocalMetaAnnotationAndMetaAnnotatedInterface() {
        assertAtComponentOnComposedAnnotationForMultipleCandidateTypes(MetaAnnotationUtilsTests.SubClassWithLocalMetaAnnotationAndMetaAnnotatedInterface.class, MetaAnnotationUtilsTests.ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface.class, "meta2", MetaAnnotationUtilsTests.Meta2.class);
    }

    /**
     *
     *
     * @since 4.0.3
     */
    @Test
    public void findAnnotationDescriptorForTypesOnMetaMetaAnnotatedClass() {
        Class<MetaAnnotationUtilsTests.MetaMetaAnnotatedClass> startClass = MetaAnnotationUtilsTests.MetaMetaAnnotatedClass.class;
        assertAtComponentOnComposedAnnotationForMultipleCandidateTypes(startClass, startClass, MetaAnnotationUtilsTests.Meta2.class, "meta2", MetaAnnotationUtilsTests.MetaMeta.class);
    }

    /**
     *
     *
     * @since 4.0.3
     */
    @Test
    public void findAnnotationDescriptorForTypesOnMetaMetaMetaAnnotatedClass() {
        Class<MetaAnnotationUtilsTests.MetaMetaMetaAnnotatedClass> startClass = MetaAnnotationUtilsTests.MetaMetaMetaAnnotatedClass.class;
        assertAtComponentOnComposedAnnotationForMultipleCandidateTypes(startClass, startClass, MetaAnnotationUtilsTests.Meta2.class, "meta2", MetaAnnotationUtilsTests.MetaMetaMeta.class);
    }

    /**
     *
     *
     * @since 4.0.3
     */
    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesOnAnnotatedClassWithMissingTargetMetaAnnotation() {
        // InheritedAnnotationClass is NOT annotated or meta-annotated with @Component,
        // @Service, or @Order, but it is annotated with @Transactional.
        UntypedAnnotationDescriptor descriptor = findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.InheritedAnnotationClass.class, Service.class, Component.class, Order.class);
        Assert.assertNull("Should not find @Component on InheritedAnnotationClass", descriptor);
    }

    /**
     *
     *
     * @since 4.0.3
     */
    @Test
    @SuppressWarnings("unchecked")
    public void findAnnotationDescriptorForTypesOnMetaCycleAnnotatedClassWithMissingTargetMetaAnnotation() {
        UntypedAnnotationDescriptor descriptor = findAnnotationDescriptorForTypes(MetaAnnotationUtilsTests.MetaCycleAnnotatedClass.class, Service.class, Component.class, Order.class);
        Assert.assertNull("Should not find @Component on MetaCycleAnnotatedClass", descriptor);
    }

    // -------------------------------------------------------------------------
    @Component("meta1")
    @Order
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    static @interface Meta1 {}

    @Component("meta2")
    @Transactional
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    static @interface Meta2 {}

    @MetaAnnotationUtilsTests.Meta2
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    @interface MetaMeta {}

    @MetaAnnotationUtilsTests.MetaMeta
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    @interface MetaMetaMeta {}

    @MetaAnnotationUtilsTests.MetaCycle3
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @Documented
    @interface MetaCycle1 {}

    @MetaAnnotationUtilsTests.MetaCycle1
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @Documented
    @interface MetaCycle2 {}

    @MetaAnnotationUtilsTests.MetaCycle2
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    @interface MetaCycle3 {}

    @ContextConfiguration
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    static @interface MetaConfig {
        static class DevConfig {}

        static class ProductionConfig {}

        Class<?>[] classes() default { MetaAnnotationUtilsTests.MetaConfig.DevConfig.class, MetaAnnotationUtilsTests.MetaConfig.ProductionConfig.class };
    }

    // -------------------------------------------------------------------------
    @MetaAnnotationUtilsTests.Meta1
    static class HasMetaComponentAnnotation {}

    @MetaAnnotationUtilsTests.Meta1
    @Component("local")
    @MetaAnnotationUtilsTests.Meta2
    static class HasLocalAndMetaComponentAnnotation {}

    @MetaAnnotationUtilsTests.Meta1
    static interface InterfaceWithMetaAnnotation {}

    static class ClassWithMetaAnnotatedInterface implements MetaAnnotationUtilsTests.InterfaceWithMetaAnnotation {}

    @MetaAnnotationUtilsTests.Meta2
    static class ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface implements MetaAnnotationUtilsTests.InterfaceWithMetaAnnotation {}

    static class SubClassWithLocalMetaAnnotationAndMetaAnnotatedInterface extends MetaAnnotationUtilsTests.ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface {}

    @MetaAnnotationUtilsTests.MetaMeta
    static class MetaMetaAnnotatedClass {}

    @MetaAnnotationUtilsTests.MetaMetaMeta
    static class MetaMetaMetaAnnotatedClass {}

    @MetaAnnotationUtilsTests.MetaCycle3
    static class MetaCycleAnnotatedClass {}

    @MetaAnnotationUtilsTests.MetaConfig
    public class MetaConfigWithDefaultAttributesTestCase {}

    @MetaAnnotationUtilsTests.MetaConfig(classes = MetaAnnotationUtilsTests.class)
    public class MetaConfigWithOverriddenAttributesTestCase {}

    // -------------------------------------------------------------------------
    @Transactional
    static interface InheritedAnnotationInterface {}

    static interface SubInheritedAnnotationInterface extends MetaAnnotationUtilsTests.InheritedAnnotationInterface {}

    static interface SubSubInheritedAnnotationInterface extends MetaAnnotationUtilsTests.SubInheritedAnnotationInterface {}

    @Order
    static interface NonInheritedAnnotationInterface {}

    static interface SubNonInheritedAnnotationInterface extends MetaAnnotationUtilsTests.NonInheritedAnnotationInterface {}

    static class NonAnnotatedClass {}

    static interface NonAnnotatedInterface {}

    @Transactional
    static class InheritedAnnotationClass {}

    static class SubInheritedAnnotationClass extends MetaAnnotationUtilsTests.InheritedAnnotationClass {}

    @Order
    static class NonInheritedAnnotationClass {}

    static class SubNonInheritedAnnotationClass extends MetaAnnotationUtilsTests.NonInheritedAnnotationClass {}

    @ContextConfiguration(classes = Number.class)
    static class AnnotatedContextConfigClass {}

    @MetaAnnotationUtilsTests.MetaConfig(classes = String.class)
    static class MetaAnnotatedAndSuperAnnotatedContextConfigClass extends MetaAnnotationUtilsTests.AnnotatedContextConfigClass {}
}

