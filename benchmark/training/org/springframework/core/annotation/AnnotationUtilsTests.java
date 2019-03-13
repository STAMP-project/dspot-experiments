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
package org.springframework.core.annotation;


import Ordered.LOWEST_PRECEDENCE;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.annotation.subpackage.NonPublicAnnotatedClass;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;


/**
 * Unit tests for {@link AnnotationUtils}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Chris Beams
 * @author Phillip Webb
 * @author Oleg Zhurakousky
 */
public class AnnotationUtilsTests {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void findMethodAnnotationOnLeaf() throws Exception {
        Method m = AnnotationUtilsTests.Leaf.class.getMethod("annotatedOnLeaf");
        Assert.assertNotNull(m.getAnnotation(Order.class));
        Assert.assertNotNull(getAnnotation(m, Order.class));
        Assert.assertNotNull(findAnnotation(m, Order.class));
    }

    // @since 4.2
    @Test
    public void findMethodAnnotationWithAnnotationOnMethodInInterface() throws Exception {
        Method m = AnnotationUtilsTests.Leaf.class.getMethod("fromInterfaceImplementedByRoot");
        // @Order is not @Inherited
        Assert.assertNull(m.getAnnotation(Order.class));
        // getAnnotation() does not search on interfaces
        Assert.assertNull(getAnnotation(m, Order.class));
        // findAnnotation() does search on interfaces
        Assert.assertNotNull(findAnnotation(m, Order.class));
    }

    // @since 4.2
    @Test
    public void findMethodAnnotationWithMetaAnnotationOnLeaf() throws Exception {
        Method m = AnnotationUtilsTests.Leaf.class.getMethod("metaAnnotatedOnLeaf");
        Assert.assertNull(m.getAnnotation(Order.class));
        Assert.assertNotNull(getAnnotation(m, Order.class));
        Assert.assertNotNull(findAnnotation(m, Order.class));
    }

    // @since 4.2
    @Test
    public void findMethodAnnotationWithMetaMetaAnnotationOnLeaf() throws Exception {
        Method m = AnnotationUtilsTests.Leaf.class.getMethod("metaMetaAnnotatedOnLeaf");
        Assert.assertNull(m.getAnnotation(Component.class));
        Assert.assertNull(getAnnotation(m, Component.class));
        Assert.assertNotNull(findAnnotation(m, Component.class));
    }

    @Test
    public void findMethodAnnotationOnRoot() throws Exception {
        Method m = AnnotationUtilsTests.Leaf.class.getMethod("annotatedOnRoot");
        Assert.assertNotNull(m.getAnnotation(Order.class));
        Assert.assertNotNull(getAnnotation(m, Order.class));
        Assert.assertNotNull(findAnnotation(m, Order.class));
    }

    // @since 4.2
    @Test
    public void findMethodAnnotationWithMetaAnnotationOnRoot() throws Exception {
        Method m = AnnotationUtilsTests.Leaf.class.getMethod("metaAnnotatedOnRoot");
        Assert.assertNull(m.getAnnotation(Order.class));
        Assert.assertNotNull(getAnnotation(m, Order.class));
        Assert.assertNotNull(findAnnotation(m, Order.class));
    }

    @Test
    public void findMethodAnnotationOnRootButOverridden() throws Exception {
        Method m = AnnotationUtilsTests.Leaf.class.getMethod("overrideWithoutNewAnnotation");
        Assert.assertNull(m.getAnnotation(Order.class));
        Assert.assertNull(getAnnotation(m, Order.class));
        Assert.assertNotNull(findAnnotation(m, Order.class));
    }

    @Test
    public void findMethodAnnotationNotAnnotated() throws Exception {
        Method m = AnnotationUtilsTests.Leaf.class.getMethod("notAnnotated");
        Assert.assertNull(findAnnotation(m, Order.class));
    }

    @Test
    public void findMethodAnnotationOnBridgeMethod() throws Exception {
        Method bridgeMethod = AnnotationUtilsTests.SimpleFoo.class.getMethod("something", Object.class);
        Assert.assertTrue(bridgeMethod.isBridge());
        Assert.assertNull(bridgeMethod.getAnnotation(Order.class));
        Assert.assertNull(getAnnotation(bridgeMethod, Order.class));
        Assert.assertNotNull(findAnnotation(bridgeMethod, Order.class));
        boolean runningInEclipse = Arrays.stream(new Exception().getStackTrace()).anyMatch(( element) -> element.getClassName().startsWith("org.eclipse.jdt"));
        // As of JDK 8, invoking getAnnotation() on a bridge method actually finds an
        // annotation on its 'bridged' method [1]; however, the Eclipse compiler will not
        // support this until Eclipse 4.9 [2]. Thus, we effectively ignore the following
        // assertion if the test is currently executing within the Eclipse IDE.
        // 
        // [1] https://bugs.openjdk.java.net/browse/JDK-6695379
        // [2] https://bugs.eclipse.org/bugs/show_bug.cgi?id=495396
        // 
        if (!runningInEclipse) {
            Assert.assertNotNull(bridgeMethod.getAnnotation(AnnotationUtilsTests.Transactional.class));
        }
        Assert.assertNotNull(getAnnotation(bridgeMethod, AnnotationUtilsTests.Transactional.class));
        Assert.assertNotNull(findAnnotation(bridgeMethod, AnnotationUtilsTests.Transactional.class));
    }

    @Test
    public void findMethodAnnotationOnBridgedMethod() throws Exception {
        Method bridgedMethod = AnnotationUtilsTests.SimpleFoo.class.getMethod("something", String.class);
        Assert.assertFalse(bridgedMethod.isBridge());
        Assert.assertNull(bridgedMethod.getAnnotation(Order.class));
        Assert.assertNull(getAnnotation(bridgedMethod, Order.class));
        Assert.assertNotNull(findAnnotation(bridgedMethod, Order.class));
        Assert.assertNotNull(bridgedMethod.getAnnotation(AnnotationUtilsTests.Transactional.class));
        Assert.assertNotNull(getAnnotation(bridgedMethod, AnnotationUtilsTests.Transactional.class));
        Assert.assertNotNull(findAnnotation(bridgedMethod, AnnotationUtilsTests.Transactional.class));
    }

    @Test
    public void findMethodAnnotationFromInterface() throws Exception {
        Method method = AnnotationUtilsTests.ImplementsInterfaceWithAnnotatedMethod.class.getMethod("foo");
        Order order = findAnnotation(method, Order.class);
        Assert.assertNotNull(order);
    }

    // SPR-16060
    @Test
    public void findMethodAnnotationFromGenericInterface() throws Exception {
        Method method = AnnotationUtilsTests.ImplementsInterfaceWithGenericAnnotatedMethod.class.getMethod("foo", String.class);
        Order order = findAnnotation(method, Order.class);
        Assert.assertNotNull(order);
    }

    // SPR-17146
    @Test
    public void findMethodAnnotationFromGenericSuperclass() throws Exception {
        Method method = AnnotationUtilsTests.ExtendsBaseClassWithGenericAnnotatedMethod.class.getMethod("foo", String.class);
        Order order = findAnnotation(method, Order.class);
        Assert.assertNotNull(order);
    }

    @Test
    public void findMethodAnnotationFromInterfaceOnSuper() throws Exception {
        Method method = AnnotationUtilsTests.SubOfImplementsInterfaceWithAnnotatedMethod.class.getMethod("foo");
        Order order = findAnnotation(method, Order.class);
        Assert.assertNotNull(order);
    }

    @Test
    public void findMethodAnnotationFromInterfaceWhenSuperDoesNotImplementMethod() throws Exception {
        Method method = AnnotationUtilsTests.SubOfAbstractImplementsInterfaceWithAnnotatedMethod.class.getMethod("foo");
        Order order = findAnnotation(method, Order.class);
        Assert.assertNotNull(order);
    }

    // @since 4.1.2
    @Test
    public void findClassAnnotationFavorsMoreLocallyDeclaredComposedAnnotationsOverAnnotationsOnInterfaces() {
        Component component = findAnnotation(AnnotationUtilsTests.ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface.class, Component.class);
        Assert.assertNotNull(component);
        Assert.assertEquals("meta2", component.value());
    }

    // @since 4.0.3
    @Test
    public void findClassAnnotationFavorsMoreLocallyDeclaredComposedAnnotationsOverInheritedAnnotations() {
        AnnotationUtilsTests.Transactional transactional = findAnnotation(AnnotationUtilsTests.SubSubClassWithInheritedAnnotation.class, AnnotationUtilsTests.Transactional.class);
        Assert.assertNotNull(transactional);
        Assert.assertTrue("readOnly flag for SubSubClassWithInheritedAnnotation", transactional.readOnly());
    }

    // @since 4.0.3
    @Test
    public void findClassAnnotationFavorsMoreLocallyDeclaredComposedAnnotationsOverInheritedComposedAnnotations() {
        Component component = findAnnotation(AnnotationUtilsTests.SubSubClassWithInheritedMetaAnnotation.class, Component.class);
        Assert.assertNotNull(component);
        Assert.assertEquals("meta2", component.value());
    }

    @Test
    public void findClassAnnotationOnMetaMetaAnnotatedClass() {
        Component component = findAnnotation(AnnotationUtilsTests.MetaMetaAnnotatedClass.class, Component.class);
        Assert.assertNotNull("Should find meta-annotation on composed annotation on class", component);
        Assert.assertEquals("meta2", component.value());
    }

    @Test
    public void findClassAnnotationOnMetaMetaMetaAnnotatedClass() {
        Component component = findAnnotation(AnnotationUtilsTests.MetaMetaMetaAnnotatedClass.class, Component.class);
        Assert.assertNotNull("Should find meta-annotation on meta-annotation on composed annotation on class", component);
        Assert.assertEquals("meta2", component.value());
    }

    @Test
    public void findClassAnnotationOnAnnotatedClassWithMissingTargetMetaAnnotation() {
        // TransactionalClass is NOT annotated or meta-annotated with @Component
        Component component = findAnnotation(AnnotationUtilsTests.TransactionalClass.class, Component.class);
        Assert.assertNull("Should not find @Component on TransactionalClass", component);
    }

    @Test
    public void findClassAnnotationOnMetaCycleAnnotatedClassWithMissingTargetMetaAnnotation() {
        Component component = findAnnotation(AnnotationUtilsTests.MetaCycleAnnotatedClass.class, Component.class);
        Assert.assertNull("Should not find @Component on MetaCycleAnnotatedClass", component);
    }

    // @since 4.2
    @Test
    public void findClassAnnotationOnInheritedAnnotationInterface() {
        AnnotationUtilsTests.Transactional tx = findAnnotation(AnnotationUtilsTests.InheritedAnnotationInterface.class, AnnotationUtilsTests.Transactional.class);
        Assert.assertNotNull("Should find @Transactional on InheritedAnnotationInterface", tx);
    }

    // @since 4.2
    @Test
    public void findClassAnnotationOnSubInheritedAnnotationInterface() {
        AnnotationUtilsTests.Transactional tx = findAnnotation(AnnotationUtilsTests.SubInheritedAnnotationInterface.class, AnnotationUtilsTests.Transactional.class);
        Assert.assertNotNull("Should find @Transactional on SubInheritedAnnotationInterface", tx);
    }

    // @since 4.2
    @Test
    public void findClassAnnotationOnSubSubInheritedAnnotationInterface() {
        AnnotationUtilsTests.Transactional tx = findAnnotation(AnnotationUtilsTests.SubSubInheritedAnnotationInterface.class, AnnotationUtilsTests.Transactional.class);
        Assert.assertNotNull("Should find @Transactional on SubSubInheritedAnnotationInterface", tx);
    }

    // @since 4.2
    @Test
    public void findClassAnnotationOnNonInheritedAnnotationInterface() {
        Order order = findAnnotation(AnnotationUtilsTests.NonInheritedAnnotationInterface.class, Order.class);
        Assert.assertNotNull("Should find @Order on NonInheritedAnnotationInterface", order);
    }

    // @since 4.2
    @Test
    public void findClassAnnotationOnSubNonInheritedAnnotationInterface() {
        Order order = findAnnotation(AnnotationUtilsTests.SubNonInheritedAnnotationInterface.class, Order.class);
        Assert.assertNotNull("Should find @Order on SubNonInheritedAnnotationInterface", order);
    }

    // @since 4.2
    @Test
    public void findClassAnnotationOnSubSubNonInheritedAnnotationInterface() {
        Order order = findAnnotation(AnnotationUtilsTests.SubSubNonInheritedAnnotationInterface.class, Order.class);
        Assert.assertNotNull("Should find @Order on SubSubNonInheritedAnnotationInterface", order);
    }

    @Test
    public void findAnnotationDeclaringClassForAllScenarios() {
        // no class-level annotation
        Assert.assertNull(findAnnotationDeclaringClass(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.NonAnnotatedInterface.class));
        Assert.assertNull(findAnnotationDeclaringClass(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.NonAnnotatedClass.class));
        // inherited class-level annotation; note: @Transactional is inherited
        Assert.assertEquals(AnnotationUtilsTests.InheritedAnnotationInterface.class, findAnnotationDeclaringClass(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.InheritedAnnotationInterface.class));
        Assert.assertNull(findAnnotationDeclaringClass(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.SubInheritedAnnotationInterface.class));
        Assert.assertEquals(AnnotationUtilsTests.InheritedAnnotationClass.class, findAnnotationDeclaringClass(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.InheritedAnnotationClass.class));
        Assert.assertEquals(AnnotationUtilsTests.InheritedAnnotationClass.class, findAnnotationDeclaringClass(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.SubInheritedAnnotationClass.class));
        // non-inherited class-level annotation; note: @Order is not inherited,
        // but findAnnotationDeclaringClass() should still find it on classes.
        Assert.assertEquals(AnnotationUtilsTests.NonInheritedAnnotationInterface.class, findAnnotationDeclaringClass(Order.class, AnnotationUtilsTests.NonInheritedAnnotationInterface.class));
        Assert.assertNull(findAnnotationDeclaringClass(Order.class, AnnotationUtilsTests.SubNonInheritedAnnotationInterface.class));
        Assert.assertEquals(AnnotationUtilsTests.NonInheritedAnnotationClass.class, findAnnotationDeclaringClass(Order.class, AnnotationUtilsTests.NonInheritedAnnotationClass.class));
        Assert.assertEquals(AnnotationUtilsTests.NonInheritedAnnotationClass.class, findAnnotationDeclaringClass(Order.class, AnnotationUtilsTests.SubNonInheritedAnnotationClass.class));
    }

    @Test
    public void findAnnotationDeclaringClassForTypesWithSingleCandidateType() {
        // no class-level annotation
        List<Class<? extends Annotation>> transactionalCandidateList = Collections.singletonList(AnnotationUtilsTests.Transactional.class);
        Assert.assertNull(findAnnotationDeclaringClassForTypes(transactionalCandidateList, AnnotationUtilsTests.NonAnnotatedInterface.class));
        Assert.assertNull(findAnnotationDeclaringClassForTypes(transactionalCandidateList, AnnotationUtilsTests.NonAnnotatedClass.class));
        // inherited class-level annotation; note: @Transactional is inherited
        Assert.assertEquals(AnnotationUtilsTests.InheritedAnnotationInterface.class, findAnnotationDeclaringClassForTypes(transactionalCandidateList, AnnotationUtilsTests.InheritedAnnotationInterface.class));
        Assert.assertNull(findAnnotationDeclaringClassForTypes(transactionalCandidateList, AnnotationUtilsTests.SubInheritedAnnotationInterface.class));
        Assert.assertEquals(AnnotationUtilsTests.InheritedAnnotationClass.class, findAnnotationDeclaringClassForTypes(transactionalCandidateList, AnnotationUtilsTests.InheritedAnnotationClass.class));
        Assert.assertEquals(AnnotationUtilsTests.InheritedAnnotationClass.class, findAnnotationDeclaringClassForTypes(transactionalCandidateList, AnnotationUtilsTests.SubInheritedAnnotationClass.class));
        // non-inherited class-level annotation; note: @Order is not inherited,
        // but findAnnotationDeclaringClassForTypes() should still find it on classes.
        List<Class<? extends Annotation>> orderCandidateList = Collections.singletonList(Order.class);
        Assert.assertEquals(AnnotationUtilsTests.NonInheritedAnnotationInterface.class, findAnnotationDeclaringClassForTypes(orderCandidateList, AnnotationUtilsTests.NonInheritedAnnotationInterface.class));
        Assert.assertNull(findAnnotationDeclaringClassForTypes(orderCandidateList, AnnotationUtilsTests.SubNonInheritedAnnotationInterface.class));
        Assert.assertEquals(AnnotationUtilsTests.NonInheritedAnnotationClass.class, findAnnotationDeclaringClassForTypes(orderCandidateList, AnnotationUtilsTests.NonInheritedAnnotationClass.class));
        Assert.assertEquals(AnnotationUtilsTests.NonInheritedAnnotationClass.class, findAnnotationDeclaringClassForTypes(orderCandidateList, AnnotationUtilsTests.SubNonInheritedAnnotationClass.class));
    }

    @Test
    public void findAnnotationDeclaringClassForTypesWithMultipleCandidateTypes() {
        List<Class<? extends Annotation>> candidates = Arrays.asList(AnnotationUtilsTests.Transactional.class, Order.class);
        // no class-level annotation
        Assert.assertNull(findAnnotationDeclaringClassForTypes(candidates, AnnotationUtilsTests.NonAnnotatedInterface.class));
        Assert.assertNull(findAnnotationDeclaringClassForTypes(candidates, AnnotationUtilsTests.NonAnnotatedClass.class));
        // inherited class-level annotation; note: @Transactional is inherited
        Assert.assertEquals(AnnotationUtilsTests.InheritedAnnotationInterface.class, findAnnotationDeclaringClassForTypes(candidates, AnnotationUtilsTests.InheritedAnnotationInterface.class));
        Assert.assertNull(findAnnotationDeclaringClassForTypes(candidates, AnnotationUtilsTests.SubInheritedAnnotationInterface.class));
        Assert.assertEquals(AnnotationUtilsTests.InheritedAnnotationClass.class, findAnnotationDeclaringClassForTypes(candidates, AnnotationUtilsTests.InheritedAnnotationClass.class));
        Assert.assertEquals(AnnotationUtilsTests.InheritedAnnotationClass.class, findAnnotationDeclaringClassForTypes(candidates, AnnotationUtilsTests.SubInheritedAnnotationClass.class));
        // non-inherited class-level annotation; note: @Order is not inherited,
        // but findAnnotationDeclaringClassForTypes() should still find it on classes.
        Assert.assertEquals(AnnotationUtilsTests.NonInheritedAnnotationInterface.class, findAnnotationDeclaringClassForTypes(candidates, AnnotationUtilsTests.NonInheritedAnnotationInterface.class));
        Assert.assertNull(findAnnotationDeclaringClassForTypes(candidates, AnnotationUtilsTests.SubNonInheritedAnnotationInterface.class));
        Assert.assertEquals(AnnotationUtilsTests.NonInheritedAnnotationClass.class, findAnnotationDeclaringClassForTypes(candidates, AnnotationUtilsTests.NonInheritedAnnotationClass.class));
        Assert.assertEquals(AnnotationUtilsTests.NonInheritedAnnotationClass.class, findAnnotationDeclaringClassForTypes(candidates, AnnotationUtilsTests.SubNonInheritedAnnotationClass.class));
        // class hierarchy mixed with @Transactional and @Order declarations
        Assert.assertEquals(AnnotationUtilsTests.TransactionalClass.class, findAnnotationDeclaringClassForTypes(candidates, AnnotationUtilsTests.TransactionalClass.class));
        Assert.assertEquals(AnnotationUtilsTests.TransactionalAndOrderedClass.class, findAnnotationDeclaringClassForTypes(candidates, AnnotationUtilsTests.TransactionalAndOrderedClass.class));
        Assert.assertEquals(AnnotationUtilsTests.TransactionalAndOrderedClass.class, findAnnotationDeclaringClassForTypes(candidates, AnnotationUtilsTests.SubTransactionalAndOrderedClass.class));
    }

    @Test
    public void isAnnotationDeclaredLocallyForAllScenarios() throws Exception {
        // no class-level annotation
        Assert.assertFalse(isAnnotationDeclaredLocally(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.NonAnnotatedInterface.class));
        Assert.assertFalse(isAnnotationDeclaredLocally(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.NonAnnotatedClass.class));
        // inherited class-level annotation; note: @Transactional is inherited
        Assert.assertTrue(isAnnotationDeclaredLocally(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.InheritedAnnotationInterface.class));
        Assert.assertFalse(isAnnotationDeclaredLocally(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.SubInheritedAnnotationInterface.class));
        Assert.assertTrue(isAnnotationDeclaredLocally(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.InheritedAnnotationClass.class));
        Assert.assertFalse(isAnnotationDeclaredLocally(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.SubInheritedAnnotationClass.class));
        // non-inherited class-level annotation; note: @Order is not inherited
        Assert.assertTrue(isAnnotationDeclaredLocally(Order.class, AnnotationUtilsTests.NonInheritedAnnotationInterface.class));
        Assert.assertFalse(isAnnotationDeclaredLocally(Order.class, AnnotationUtilsTests.SubNonInheritedAnnotationInterface.class));
        Assert.assertTrue(isAnnotationDeclaredLocally(Order.class, AnnotationUtilsTests.NonInheritedAnnotationClass.class));
        Assert.assertFalse(isAnnotationDeclaredLocally(Order.class, AnnotationUtilsTests.SubNonInheritedAnnotationClass.class));
    }

    @Test
    public void isAnnotationInheritedForAllScenarios() {
        // no class-level annotation
        Assert.assertFalse(isAnnotationInherited(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.NonAnnotatedInterface.class));
        Assert.assertFalse(isAnnotationInherited(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.NonAnnotatedClass.class));
        // inherited class-level annotation; note: @Transactional is inherited
        Assert.assertFalse(isAnnotationInherited(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.InheritedAnnotationInterface.class));
        // isAnnotationInherited() does not currently traverse interface hierarchies.
        // Thus the following, though perhaps counter intuitive, must be false:
        Assert.assertFalse(isAnnotationInherited(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.SubInheritedAnnotationInterface.class));
        Assert.assertFalse(isAnnotationInherited(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.InheritedAnnotationClass.class));
        Assert.assertTrue(isAnnotationInherited(AnnotationUtilsTests.Transactional.class, AnnotationUtilsTests.SubInheritedAnnotationClass.class));
        // non-inherited class-level annotation; note: @Order is not inherited
        Assert.assertFalse(isAnnotationInherited(Order.class, AnnotationUtilsTests.NonInheritedAnnotationInterface.class));
        Assert.assertFalse(isAnnotationInherited(Order.class, AnnotationUtilsTests.SubNonInheritedAnnotationInterface.class));
        Assert.assertFalse(isAnnotationInherited(Order.class, AnnotationUtilsTests.NonInheritedAnnotationClass.class));
        Assert.assertFalse(isAnnotationInherited(Order.class, AnnotationUtilsTests.SubNonInheritedAnnotationClass.class));
    }

    @Test
    public void getAnnotationAttributesWithoutAttributeAliases() {
        Component component = AnnotationUtilsTests.WebController.class.getAnnotation(Component.class);
        Assert.assertNotNull(component);
        AnnotationAttributes attributes = ((AnnotationAttributes) (getAnnotationAttributes(component)));
        Assert.assertNotNull(attributes);
        Assert.assertEquals("value attribute: ", "webController", attributes.getString(VALUE));
        Assert.assertEquals(Component.class, attributes.annotationType());
    }

    @Test
    public void getAnnotationAttributesWithNestedAnnotations() {
        AnnotationUtilsTests.ComponentScan componentScan = AnnotationUtilsTests.ComponentScanClass.class.getAnnotation(AnnotationUtilsTests.ComponentScan.class);
        Assert.assertNotNull(componentScan);
        AnnotationAttributes attributes = getAnnotationAttributes(AnnotationUtilsTests.ComponentScanClass.class, componentScan);
        Assert.assertNotNull(attributes);
        Assert.assertEquals(AnnotationUtilsTests.ComponentScan.class, attributes.annotationType());
        AnnotationUtilsTests.Filter[] filters = attributes.getAnnotationArray("excludeFilters", AnnotationUtilsTests.Filter.class);
        Assert.assertNotNull(filters);
        List<String> patterns = Arrays.stream(filters).map(AnnotationUtilsTests.Filter::pattern).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("*Foo", "*Bar"), patterns);
    }

    @Test
    public void getAnnotationAttributesWithAttributeAliases() throws Exception {
        Method method = AnnotationUtilsTests.WebController.class.getMethod("handleMappedWithValueAttribute");
        AnnotationUtilsTests.WebMapping webMapping = method.getAnnotation(AnnotationUtilsTests.WebMapping.class);
        AnnotationAttributes attributes = ((AnnotationAttributes) (getAnnotationAttributes(webMapping)));
        Assert.assertNotNull(attributes);
        Assert.assertEquals(AnnotationUtilsTests.WebMapping.class, attributes.annotationType());
        Assert.assertEquals("name attribute: ", "foo", attributes.getString("name"));
        Assert.assertArrayEquals("value attribute: ", AnnotationUtilsTests.asArray("/test"), attributes.getStringArray(VALUE));
        Assert.assertArrayEquals("path attribute: ", AnnotationUtilsTests.asArray("/test"), attributes.getStringArray("path"));
        method = AnnotationUtilsTests.WebController.class.getMethod("handleMappedWithPathAttribute");
        webMapping = method.getAnnotation(AnnotationUtilsTests.WebMapping.class);
        attributes = ((AnnotationAttributes) (getAnnotationAttributes(webMapping)));
        Assert.assertNotNull(attributes);
        Assert.assertEquals(AnnotationUtilsTests.WebMapping.class, attributes.annotationType());
        Assert.assertEquals("name attribute: ", "bar", attributes.getString("name"));
        Assert.assertArrayEquals("value attribute: ", AnnotationUtilsTests.asArray("/test"), attributes.getStringArray(VALUE));
        Assert.assertArrayEquals("path attribute: ", AnnotationUtilsTests.asArray("/test"), attributes.getStringArray("path"));
    }

    @Test
    public void getAnnotationAttributesWithAttributeAliasesWithDifferentValues() throws Exception {
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(containsString("attribute 'value' and its alias 'path'"));
        exception.expectMessage(containsString("values of [{/enigma}] and [{/test}]"));
        Method method = AnnotationUtilsTests.WebController.class.getMethod("handleMappedWithDifferentPathAndValueAttributes");
        AnnotationUtilsTests.WebMapping webMapping = method.getAnnotation(AnnotationUtilsTests.WebMapping.class);
        getAnnotationAttributes(webMapping);
    }

    @Test
    public void getValueFromAnnotation() throws Exception {
        Method method = AnnotationUtilsTests.SimpleFoo.class.getMethod("something", Object.class);
        Order order = findAnnotation(method, Order.class);
        Assert.assertEquals(1, getValue(order, VALUE));
        Assert.assertEquals(1, getValue(order));
    }

    @Test
    public void getValueFromNonPublicAnnotation() throws Exception {
        Annotation[] declaredAnnotations = NonPublicAnnotatedClass.class.getDeclaredAnnotations();
        Assert.assertEquals(1, declaredAnnotations.length);
        Annotation annotation = declaredAnnotations[0];
        Assert.assertNotNull(annotation);
        Assert.assertEquals("NonPublicAnnotation", annotation.annotationType().getSimpleName());
        Assert.assertEquals(42, getValue(annotation, VALUE));
        Assert.assertEquals(42, getValue(annotation));
    }

    @Test
    public void getDefaultValueFromAnnotation() throws Exception {
        Method method = AnnotationUtilsTests.SimpleFoo.class.getMethod("something", Object.class);
        Order order = findAnnotation(method, Order.class);
        Assert.assertEquals(LOWEST_PRECEDENCE, getDefaultValue(order, VALUE));
        Assert.assertEquals(LOWEST_PRECEDENCE, getDefaultValue(order));
    }

    @Test
    public void getDefaultValueFromNonPublicAnnotation() {
        Annotation[] declaredAnnotations = NonPublicAnnotatedClass.class.getDeclaredAnnotations();
        Assert.assertEquals(1, declaredAnnotations.length);
        Annotation annotation = declaredAnnotations[0];
        Assert.assertNotNull(annotation);
        Assert.assertEquals("NonPublicAnnotation", annotation.annotationType().getSimpleName());
        Assert.assertEquals((-1), getDefaultValue(annotation, VALUE));
        Assert.assertEquals((-1), getDefaultValue(annotation));
    }

    @Test
    public void getDefaultValueFromAnnotationType() {
        Assert.assertEquals(LOWEST_PRECEDENCE, getDefaultValue(Order.class, VALUE));
        Assert.assertEquals(LOWEST_PRECEDENCE, getDefaultValue(Order.class));
    }

    @Test
    public void findRepeatableAnnotationOnComposedAnnotation() {
        Repeatable repeatable = findAnnotation(AnnotationUtilsTests.MyRepeatableMeta1.class, Repeatable.class);
        Assert.assertNotNull(repeatable);
        Assert.assertEquals(AnnotationUtilsTests.MyRepeatableContainer.class, repeatable.value());
    }

    @Test
    public void getRepeatableAnnotationsDeclaredOnMethod() throws Exception {
        Method method = AnnotationUtilsTests.InterfaceWithRepeated.class.getMethod("foo");
        Set<AnnotationUtilsTests.MyRepeatable> annotations = getRepeatableAnnotations(method, AnnotationUtilsTests.MyRepeatable.class, AnnotationUtilsTests.MyRepeatableContainer.class);
        Assert.assertNotNull(annotations);
        List<String> values = annotations.stream().map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(Arrays.asList("A", "B", "C", "meta1")));
    }

    @Test
    public void getRepeatableAnnotationsDeclaredOnClassWithMissingAttributeAliasDeclaration() throws Exception {
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(startsWith("Attribute 'value' in"));
        exception.expectMessage(containsString(AnnotationUtilsTests.BrokenContextConfig.class.getName()));
        exception.expectMessage(containsString("@AliasFor [location]"));
        getRepeatableAnnotations(AnnotationUtilsTests.BrokenConfigHierarchyTestCase.class, AnnotationUtilsTests.BrokenContextConfig.class, AnnotationUtilsTests.BrokenHierarchy.class);
    }

    @Test
    public void getRepeatableAnnotationsDeclaredOnClassWithAttributeAliases() {
        final List<String> expectedLocations = Arrays.asList("A", "B");
        Set<AnnotationUtilsTests.ContextConfig> annotations = getRepeatableAnnotations(AnnotationUtilsTests.ConfigHierarchyTestCase.class, AnnotationUtilsTests.ContextConfig.class, null);
        Assert.assertNotNull(annotations);
        Assert.assertEquals("size if container type is omitted: ", 0, annotations.size());
        annotations = getRepeatableAnnotations(AnnotationUtilsTests.ConfigHierarchyTestCase.class, AnnotationUtilsTests.ContextConfig.class, AnnotationUtilsTests.Hierarchy.class);
        Assert.assertNotNull(annotations);
        List<String> locations = annotations.stream().map(AnnotationUtilsTests.ContextConfig::location).collect(Collectors.toList());
        Assert.assertThat(locations, is(expectedLocations));
        List<String> values = annotations.stream().map(AnnotationUtilsTests.ContextConfig::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedLocations));
    }

    @Test
    public void getRepeatableAnnotationsDeclaredOnClass() {
        final List<String> expectedValuesJava = Arrays.asList("A", "B", "C");
        final List<String> expectedValuesSpring = Arrays.asList("A", "B", "C", "meta1");
        // Java 8
        AnnotationUtilsTests.MyRepeatable[] array = AnnotationUtilsTests.MyRepeatableClass.class.getAnnotationsByType(AnnotationUtilsTests.MyRepeatable.class);
        Assert.assertNotNull(array);
        List<String> values = Arrays.stream(array).map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesJava));
        // Spring
        Set<AnnotationUtilsTests.MyRepeatable> set = getRepeatableAnnotations(AnnotationUtilsTests.MyRepeatableClass.class, AnnotationUtilsTests.MyRepeatable.class, AnnotationUtilsTests.MyRepeatableContainer.class);
        Assert.assertNotNull(set);
        values = set.stream().map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesSpring));
        // When container type is omitted and therefore inferred from @Repeatable
        set = getRepeatableAnnotations(AnnotationUtilsTests.MyRepeatableClass.class, AnnotationUtilsTests.MyRepeatable.class);
        Assert.assertNotNull(set);
        values = set.stream().map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesSpring));
    }

    @Test
    public void getRepeatableAnnotationsDeclaredOnSuperclass() {
        final Class<?> clazz = AnnotationUtilsTests.SubMyRepeatableClass.class;
        final List<String> expectedValuesJava = Arrays.asList("A", "B", "C");
        final List<String> expectedValuesSpring = Arrays.asList("A", "B", "C", "meta1");
        // Java 8
        AnnotationUtilsTests.MyRepeatable[] array = clazz.getAnnotationsByType(AnnotationUtilsTests.MyRepeatable.class);
        Assert.assertNotNull(array);
        List<String> values = Arrays.stream(array).map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesJava));
        // Spring
        Set<AnnotationUtilsTests.MyRepeatable> set = getRepeatableAnnotations(clazz, AnnotationUtilsTests.MyRepeatable.class, AnnotationUtilsTests.MyRepeatableContainer.class);
        Assert.assertNotNull(set);
        values = set.stream().map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesSpring));
        // When container type is omitted and therefore inferred from @Repeatable
        set = getRepeatableAnnotations(clazz, AnnotationUtilsTests.MyRepeatable.class);
        Assert.assertNotNull(set);
        values = set.stream().map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesSpring));
    }

    @Test
    public void getRepeatableAnnotationsDeclaredOnClassAndSuperclass() {
        final Class<?> clazz = AnnotationUtilsTests.SubMyRepeatableWithAdditionalLocalDeclarationsClass.class;
        final List<String> expectedValuesJava = Arrays.asList("X", "Y", "Z");
        final List<String> expectedValuesSpring = Arrays.asList("X", "Y", "Z", "meta2");
        // Java 8
        AnnotationUtilsTests.MyRepeatable[] array = clazz.getAnnotationsByType(AnnotationUtilsTests.MyRepeatable.class);
        Assert.assertNotNull(array);
        List<String> values = Arrays.stream(array).map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesJava));
        // Spring
        Set<AnnotationUtilsTests.MyRepeatable> set = getRepeatableAnnotations(clazz, AnnotationUtilsTests.MyRepeatable.class, AnnotationUtilsTests.MyRepeatableContainer.class);
        Assert.assertNotNull(set);
        values = set.stream().map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesSpring));
        // When container type is omitted and therefore inferred from @Repeatable
        set = getRepeatableAnnotations(clazz, AnnotationUtilsTests.MyRepeatable.class);
        Assert.assertNotNull(set);
        values = set.stream().map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesSpring));
    }

    @Test
    public void getRepeatableAnnotationsDeclaredOnMultipleSuperclasses() {
        final Class<?> clazz = AnnotationUtilsTests.SubSubMyRepeatableWithAdditionalLocalDeclarationsClass.class;
        final List<String> expectedValuesJava = Arrays.asList("X", "Y", "Z");
        final List<String> expectedValuesSpring = Arrays.asList("X", "Y", "Z", "meta2");
        // Java 8
        AnnotationUtilsTests.MyRepeatable[] array = clazz.getAnnotationsByType(AnnotationUtilsTests.MyRepeatable.class);
        Assert.assertNotNull(array);
        List<String> values = Arrays.stream(array).map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesJava));
        // Spring
        Set<AnnotationUtilsTests.MyRepeatable> set = getRepeatableAnnotations(clazz, AnnotationUtilsTests.MyRepeatable.class, AnnotationUtilsTests.MyRepeatableContainer.class);
        Assert.assertNotNull(set);
        values = set.stream().map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesSpring));
        // When container type is omitted and therefore inferred from @Repeatable
        set = getRepeatableAnnotations(clazz, AnnotationUtilsTests.MyRepeatable.class);
        Assert.assertNotNull(set);
        values = set.stream().map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesSpring));
    }

    @Test
    public void getDeclaredRepeatableAnnotationsDeclaredOnClass() {
        final List<String> expectedValuesJava = Arrays.asList("A", "B", "C");
        final List<String> expectedValuesSpring = Arrays.asList("A", "B", "C", "meta1");
        // Java 8
        AnnotationUtilsTests.MyRepeatable[] array = AnnotationUtilsTests.MyRepeatableClass.class.getDeclaredAnnotationsByType(AnnotationUtilsTests.MyRepeatable.class);
        Assert.assertNotNull(array);
        List<String> values = Arrays.stream(array).map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesJava));
        // Spring
        Set<AnnotationUtilsTests.MyRepeatable> set = getDeclaredRepeatableAnnotations(AnnotationUtilsTests.MyRepeatableClass.class, AnnotationUtilsTests.MyRepeatable.class, AnnotationUtilsTests.MyRepeatableContainer.class);
        Assert.assertNotNull(set);
        values = set.stream().map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesSpring));
        // When container type is omitted and therefore inferred from @Repeatable
        set = getDeclaredRepeatableAnnotations(AnnotationUtilsTests.MyRepeatableClass.class, AnnotationUtilsTests.MyRepeatable.class);
        Assert.assertNotNull(set);
        values = set.stream().map(AnnotationUtilsTests.MyRepeatable::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedValuesSpring));
    }

    @Test
    public void getDeclaredRepeatableAnnotationsDeclaredOnSuperclass() {
        final Class<?> clazz = AnnotationUtilsTests.SubMyRepeatableClass.class;
        // Java 8
        AnnotationUtilsTests.MyRepeatable[] array = clazz.getDeclaredAnnotationsByType(AnnotationUtilsTests.MyRepeatable.class);
        Assert.assertNotNull(array);
        Assert.assertThat(array.length, is(0));
        // Spring
        Set<AnnotationUtilsTests.MyRepeatable> set = getDeclaredRepeatableAnnotations(clazz, AnnotationUtilsTests.MyRepeatable.class, AnnotationUtilsTests.MyRepeatableContainer.class);
        Assert.assertNotNull(set);
        Assert.assertThat(set.size(), is(0));
        // When container type is omitted and therefore inferred from @Repeatable
        set = getDeclaredRepeatableAnnotations(clazz, AnnotationUtilsTests.MyRepeatable.class);
        Assert.assertNotNull(set);
        Assert.assertThat(set.size(), is(0));
    }

    @Test
    public void getAttributeOverrideNameFromWrongTargetAnnotation() throws Exception {
        Method attribute = AnnotationUtilsTests.AliasedComposedContextConfig.class.getDeclaredMethod("xmlConfigFile");
        Assert.assertThat("xmlConfigFile is not an alias for @Component.", getAttributeOverrideName(attribute, Component.class), is(nullValue()));
    }

    @Test
    public void getAttributeOverrideNameForNonAliasedAttribute() throws Exception {
        Method nonAliasedAttribute = AnnotationUtilsTests.ImplicitAliasesContextConfig.class.getDeclaredMethod("nonAliasedAttribute");
        Assert.assertThat(getAttributeOverrideName(nonAliasedAttribute, AnnotationUtilsTests.ContextConfig.class), is(nullValue()));
    }

    @Test
    public void getAttributeOverrideNameFromAliasedComposedAnnotation() throws Exception {
        Method attribute = AnnotationUtilsTests.AliasedComposedContextConfig.class.getDeclaredMethod("xmlConfigFile");
        Assert.assertEquals("location", getAttributeOverrideName(attribute, AnnotationUtilsTests.ContextConfig.class));
    }

    @Test
    public void getAttributeAliasNamesFromComposedAnnotationWithImplicitAliases() throws Exception {
        Method xmlFile = AnnotationUtilsTests.ImplicitAliasesContextConfig.class.getDeclaredMethod("xmlFile");
        Method groovyScript = AnnotationUtilsTests.ImplicitAliasesContextConfig.class.getDeclaredMethod("groovyScript");
        Method value = AnnotationUtilsTests.ImplicitAliasesContextConfig.class.getDeclaredMethod("value");
        Method location1 = AnnotationUtilsTests.ImplicitAliasesContextConfig.class.getDeclaredMethod("location1");
        Method location2 = AnnotationUtilsTests.ImplicitAliasesContextConfig.class.getDeclaredMethod("location2");
        Method location3 = AnnotationUtilsTests.ImplicitAliasesContextConfig.class.getDeclaredMethod("location3");
        // Meta-annotation attribute overrides
        Assert.assertEquals("location", getAttributeOverrideName(xmlFile, AnnotationUtilsTests.ContextConfig.class));
        Assert.assertEquals("location", getAttributeOverrideName(groovyScript, AnnotationUtilsTests.ContextConfig.class));
        Assert.assertEquals("location", getAttributeOverrideName(value, AnnotationUtilsTests.ContextConfig.class));
        // Implicit aliases
        Assert.assertThat(getAttributeAliasNames(xmlFile), containsInAnyOrder("value", "groovyScript", "location1", "location2", "location3"));
        Assert.assertThat(getAttributeAliasNames(groovyScript), containsInAnyOrder("value", "xmlFile", "location1", "location2", "location3"));
        Assert.assertThat(getAttributeAliasNames(value), containsInAnyOrder("xmlFile", "groovyScript", "location1", "location2", "location3"));
        Assert.assertThat(getAttributeAliasNames(location1), containsInAnyOrder("xmlFile", "groovyScript", "value", "location2", "location3"));
        Assert.assertThat(getAttributeAliasNames(location2), containsInAnyOrder("xmlFile", "groovyScript", "value", "location1", "location3"));
        Assert.assertThat(getAttributeAliasNames(location3), containsInAnyOrder("xmlFile", "groovyScript", "value", "location1", "location2"));
    }

    @Test
    public void getAttributeAliasNamesFromComposedAnnotationWithImplicitAliasesForAliasPair() throws Exception {
        Method xmlFile = AnnotationUtilsTests.ImplicitAliasesForAliasPairContextConfig.class.getDeclaredMethod("xmlFile");
        Method groovyScript = AnnotationUtilsTests.ImplicitAliasesForAliasPairContextConfig.class.getDeclaredMethod("groovyScript");
        // Meta-annotation attribute overrides
        Assert.assertEquals("location", getAttributeOverrideName(xmlFile, AnnotationUtilsTests.ContextConfig.class));
        Assert.assertEquals("value", getAttributeOverrideName(groovyScript, AnnotationUtilsTests.ContextConfig.class));
        // Implicit aliases
        Assert.assertThat(getAttributeAliasNames(xmlFile), containsInAnyOrder("groovyScript"));
        Assert.assertThat(getAttributeAliasNames(groovyScript), containsInAnyOrder("xmlFile"));
    }

    @Test
    public void getAttributeAliasNamesFromComposedAnnotationWithImplicitAliasesWithImpliedAliasNamesOmitted() throws Exception {
        Method value = AnnotationUtilsTests.ImplicitAliasesWithImpliedAliasNamesOmittedContextConfig.class.getDeclaredMethod("value");
        Method location = AnnotationUtilsTests.ImplicitAliasesWithImpliedAliasNamesOmittedContextConfig.class.getDeclaredMethod("location");
        Method xmlFile = AnnotationUtilsTests.ImplicitAliasesWithImpliedAliasNamesOmittedContextConfig.class.getDeclaredMethod("xmlFile");
        // Meta-annotation attribute overrides
        Assert.assertEquals("value", getAttributeOverrideName(value, AnnotationUtilsTests.ContextConfig.class));
        Assert.assertEquals("location", getAttributeOverrideName(location, AnnotationUtilsTests.ContextConfig.class));
        Assert.assertEquals("location", getAttributeOverrideName(xmlFile, AnnotationUtilsTests.ContextConfig.class));
        // Implicit aliases
        Assert.assertThat(getAttributeAliasNames(value), containsInAnyOrder("location", "xmlFile"));
        Assert.assertThat(getAttributeAliasNames(location), containsInAnyOrder("value", "xmlFile"));
        Assert.assertThat(getAttributeAliasNames(xmlFile), containsInAnyOrder("value", "location"));
    }

    @Test
    public void getAttributeAliasNamesFromComposedAnnotationWithTransitiveImplicitAliases() throws Exception {
        Method xml = AnnotationUtilsTests.TransitiveImplicitAliasesContextConfig.class.getDeclaredMethod("xml");
        Method groovy = AnnotationUtilsTests.TransitiveImplicitAliasesContextConfig.class.getDeclaredMethod("groovy");
        // Explicit meta-annotation attribute overrides
        Assert.assertEquals("xmlFile", getAttributeOverrideName(xml, AnnotationUtilsTests.ImplicitAliasesContextConfig.class));
        Assert.assertEquals("groovyScript", getAttributeOverrideName(groovy, AnnotationUtilsTests.ImplicitAliasesContextConfig.class));
        // Transitive meta-annotation attribute overrides
        Assert.assertEquals("location", getAttributeOverrideName(xml, AnnotationUtilsTests.ContextConfig.class));
        Assert.assertEquals("location", getAttributeOverrideName(groovy, AnnotationUtilsTests.ContextConfig.class));
        // Transitive implicit aliases
        Assert.assertThat(getAttributeAliasNames(xml), containsInAnyOrder("groovy"));
        Assert.assertThat(getAttributeAliasNames(groovy), containsInAnyOrder("xml"));
    }

    @Test
    public void getAttributeAliasNamesFromComposedAnnotationWithTransitiveImplicitAliasesForAliasPair() throws Exception {
        Method xml = AnnotationUtilsTests.TransitiveImplicitAliasesForAliasPairContextConfig.class.getDeclaredMethod("xml");
        Method groovy = AnnotationUtilsTests.TransitiveImplicitAliasesForAliasPairContextConfig.class.getDeclaredMethod("groovy");
        // Explicit meta-annotation attribute overrides
        Assert.assertEquals("xmlFile", getAttributeOverrideName(xml, AnnotationUtilsTests.ImplicitAliasesForAliasPairContextConfig.class));
        Assert.assertEquals("groovyScript", getAttributeOverrideName(groovy, AnnotationUtilsTests.ImplicitAliasesForAliasPairContextConfig.class));
        // Transitive implicit aliases
        Assert.assertThat(getAttributeAliasNames(xml), containsInAnyOrder("groovy"));
        Assert.assertThat(getAttributeAliasNames(groovy), containsInAnyOrder("xml"));
    }

    @Test
    public void getAttributeAliasNamesFromComposedAnnotationWithTransitiveImplicitAliasesWithImpliedAliasNamesOmitted() throws Exception {
        Method xml = AnnotationUtilsTests.TransitiveImplicitAliasesWithImpliedAliasNamesOmittedContextConfig.class.getDeclaredMethod("xml");
        Method groovy = AnnotationUtilsTests.TransitiveImplicitAliasesWithImpliedAliasNamesOmittedContextConfig.class.getDeclaredMethod("groovy");
        // Meta-annotation attribute overrides
        Assert.assertEquals("location", getAttributeOverrideName(xml, AnnotationUtilsTests.ContextConfig.class));
        Assert.assertEquals("location", getAttributeOverrideName(groovy, AnnotationUtilsTests.ContextConfig.class));
        // Explicit meta-annotation attribute overrides
        Assert.assertEquals("xmlFile", getAttributeOverrideName(xml, AnnotationUtilsTests.ImplicitAliasesWithImpliedAliasNamesOmittedContextConfig.class));
        Assert.assertEquals("location", getAttributeOverrideName(groovy, AnnotationUtilsTests.ImplicitAliasesWithImpliedAliasNamesOmittedContextConfig.class));
        // Transitive implicit aliases
        Assert.assertThat(getAttributeAliasNames(groovy), containsInAnyOrder("xml"));
        Assert.assertThat(getAttributeAliasNames(xml), containsInAnyOrder("groovy"));
    }

    @Test
    public void synthesizeAnnotationWithoutAttributeAliases() throws Exception {
        Component component = AnnotationUtilsTests.WebController.class.getAnnotation(Component.class);
        Assert.assertNotNull(component);
        Component synthesizedComponent = synthesizeAnnotation(component);
        Assert.assertNotNull(synthesizedComponent);
        Assert.assertSame(component, synthesizedComponent);
        Assert.assertEquals("value attribute: ", "webController", synthesizedComponent.value());
    }

    @Test
    public void synthesizeAlreadySynthesizedAnnotation() throws Exception {
        Method method = AnnotationUtilsTests.WebController.class.getMethod("handleMappedWithValueAttribute");
        AnnotationUtilsTests.WebMapping webMapping = method.getAnnotation(AnnotationUtilsTests.WebMapping.class);
        Assert.assertNotNull(webMapping);
        AnnotationUtilsTests.WebMapping synthesizedWebMapping = synthesizeAnnotation(webMapping);
        Assert.assertNotSame(webMapping, synthesizedWebMapping);
        AnnotationUtilsTests.WebMapping synthesizedAgainWebMapping = synthesizeAnnotation(synthesizedWebMapping);
        Assert.assertThat(synthesizedAgainWebMapping, instanceOf(SynthesizedAnnotation.class));
        Assert.assertSame(synthesizedWebMapping, synthesizedAgainWebMapping);
        Assert.assertEquals("name attribute: ", "foo", synthesizedAgainWebMapping.name());
        Assert.assertArrayEquals("aliased path attribute: ", AnnotationUtilsTests.asArray("/test"), synthesizedAgainWebMapping.path());
        Assert.assertArrayEquals("actual value attribute: ", AnnotationUtilsTests.asArray("/test"), synthesizedAgainWebMapping.value());
    }

    @Test
    public void synthesizeAnnotationWhereAliasForIsMissingAttributeDeclaration() throws Exception {
        AnnotationUtilsTests.AliasForWithMissingAttributeDeclaration annotation = AnnotationUtilsTests.AliasForWithMissingAttributeDeclarationClass.class.getAnnotation(AnnotationUtilsTests.AliasForWithMissingAttributeDeclaration.class);
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(startsWith("@AliasFor declaration on attribute 'foo' in annotation"));
        exception.expectMessage(containsString(AnnotationUtilsTests.AliasForWithMissingAttributeDeclaration.class.getName()));
        exception.expectMessage(containsString("points to itself"));
        synthesizeAnnotation(annotation);
    }

    @Test
    public void synthesizeAnnotationWhereAliasForHasDuplicateAttributeDeclaration() throws Exception {
        AnnotationUtilsTests.AliasForWithDuplicateAttributeDeclaration annotation = AnnotationUtilsTests.AliasForWithDuplicateAttributeDeclarationClass.class.getAnnotation(AnnotationUtilsTests.AliasForWithDuplicateAttributeDeclaration.class);
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(startsWith("In @AliasFor declared on attribute 'foo' in annotation"));
        exception.expectMessage(containsString(AnnotationUtilsTests.AliasForWithDuplicateAttributeDeclaration.class.getName()));
        exception.expectMessage(containsString("attribute 'attribute' and its alias 'value' are present with values of [baz] and [bar]"));
        synthesizeAnnotation(annotation);
    }

    @Test
    public void synthesizeAnnotationWithAttributeAliasForNonexistentAttribute() throws Exception {
        AnnotationUtilsTests.AliasForNonexistentAttribute annotation = AnnotationUtilsTests.AliasForNonexistentAttributeClass.class.getAnnotation(AnnotationUtilsTests.AliasForNonexistentAttribute.class);
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(startsWith("Attribute 'foo' in"));
        exception.expectMessage(containsString(AnnotationUtilsTests.AliasForNonexistentAttribute.class.getName()));
        exception.expectMessage(containsString("is declared as an @AliasFor nonexistent attribute 'bar'"));
        synthesizeAnnotation(annotation);
    }

    @Test
    public void synthesizeAnnotationWithAttributeAliasWithoutMirroredAliasFor() throws Exception {
        AnnotationUtilsTests.AliasForWithoutMirroredAliasFor annotation = AnnotationUtilsTests.AliasForWithoutMirroredAliasForClass.class.getAnnotation(AnnotationUtilsTests.AliasForWithoutMirroredAliasFor.class);
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(startsWith("Attribute 'bar' in"));
        exception.expectMessage(containsString(AnnotationUtilsTests.AliasForWithoutMirroredAliasFor.class.getName()));
        exception.expectMessage(containsString("@AliasFor [foo]"));
        synthesizeAnnotation(annotation);
    }

    @Test
    public void synthesizeAnnotationWithAttributeAliasWithMirroredAliasForWrongAttribute() throws Exception {
        AnnotationUtilsTests.AliasForWithMirroredAliasForWrongAttribute annotation = AnnotationUtilsTests.AliasForWithMirroredAliasForWrongAttributeClass.class.getAnnotation(AnnotationUtilsTests.AliasForWithMirroredAliasForWrongAttribute.class);
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(startsWith("Attribute 'bar' in"));
        exception.expectMessage(containsString(AnnotationUtilsTests.AliasForWithMirroredAliasForWrongAttribute.class.getName()));
        exception.expectMessage(either(containsString("must be declared as an @AliasFor [foo], not [quux]")).or(containsString("is declared as an @AliasFor nonexistent attribute 'quux'")));
        synthesizeAnnotation(annotation);
    }

    @Test
    public void synthesizeAnnotationWithAttributeAliasForAttributeOfDifferentType() throws Exception {
        AnnotationUtilsTests.AliasForAttributeOfDifferentType annotation = AnnotationUtilsTests.AliasForAttributeOfDifferentTypeClass.class.getAnnotation(AnnotationUtilsTests.AliasForAttributeOfDifferentType.class);
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(startsWith("Misconfigured aliases"));
        exception.expectMessage(containsString(AnnotationUtilsTests.AliasForAttributeOfDifferentType.class.getName()));
        exception.expectMessage(containsString("attribute 'foo'"));
        exception.expectMessage(containsString("attribute 'bar'"));
        exception.expectMessage(containsString("same return type"));
        synthesizeAnnotation(annotation);
    }

    @Test
    public void synthesizeAnnotationWithAttributeAliasForWithMissingDefaultValues() throws Exception {
        AnnotationUtilsTests.AliasForWithMissingDefaultValues annotation = AnnotationUtilsTests.AliasForWithMissingDefaultValuesClass.class.getAnnotation(AnnotationUtilsTests.AliasForWithMissingDefaultValues.class);
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(startsWith("Misconfigured aliases"));
        exception.expectMessage(containsString(AnnotationUtilsTests.AliasForWithMissingDefaultValues.class.getName()));
        exception.expectMessage(containsString("attribute 'foo' in annotation"));
        exception.expectMessage(containsString("attribute 'bar' in annotation"));
        exception.expectMessage(containsString("default values"));
        synthesizeAnnotation(annotation);
    }

    @Test
    public void synthesizeAnnotationWithAttributeAliasForAttributeWithDifferentDefaultValue() throws Exception {
        AnnotationUtilsTests.AliasForAttributeWithDifferentDefaultValue annotation = AnnotationUtilsTests.AliasForAttributeWithDifferentDefaultValueClass.class.getAnnotation(AnnotationUtilsTests.AliasForAttributeWithDifferentDefaultValue.class);
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(startsWith("Misconfigured aliases"));
        exception.expectMessage(containsString(AnnotationUtilsTests.AliasForAttributeWithDifferentDefaultValue.class.getName()));
        exception.expectMessage(containsString("attribute 'foo' in annotation"));
        exception.expectMessage(containsString("attribute 'bar' in annotation"));
        exception.expectMessage(containsString("same default value"));
        synthesizeAnnotation(annotation);
    }

    @Test
    public void synthesizeAnnotationWithAttributeAliasForMetaAnnotationThatIsNotMetaPresent() throws Exception {
        AnnotationUtilsTests.AliasedComposedContextConfigNotMetaPresent annotation = AnnotationUtilsTests.AliasedComposedContextConfigNotMetaPresentClass.class.getAnnotation(AnnotationUtilsTests.AliasedComposedContextConfigNotMetaPresent.class);
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(startsWith("@AliasFor declaration on attribute 'xmlConfigFile' in annotation"));
        exception.expectMessage(containsString(AnnotationUtilsTests.AliasedComposedContextConfigNotMetaPresent.class.getName()));
        exception.expectMessage(containsString("declares an alias for attribute 'location' in meta-annotation"));
        exception.expectMessage(containsString(AnnotationUtilsTests.ContextConfig.class.getName()));
        exception.expectMessage(containsString("not meta-present"));
        synthesizeAnnotation(annotation);
    }

    @Test
    public void synthesizeAnnotationWithAttributeAliases() throws Exception {
        Method method = AnnotationUtilsTests.WebController.class.getMethod("handleMappedWithValueAttribute");
        AnnotationUtilsTests.WebMapping webMapping = method.getAnnotation(AnnotationUtilsTests.WebMapping.class);
        Assert.assertNotNull(webMapping);
        AnnotationUtilsTests.WebMapping synthesizedWebMapping1 = synthesizeAnnotation(webMapping);
        Assert.assertThat(synthesizedWebMapping1, instanceOf(SynthesizedAnnotation.class));
        Assert.assertNotSame(webMapping, synthesizedWebMapping1);
        Assert.assertEquals("name attribute: ", "foo", synthesizedWebMapping1.name());
        Assert.assertArrayEquals("aliased path attribute: ", AnnotationUtilsTests.asArray("/test"), synthesizedWebMapping1.path());
        Assert.assertArrayEquals("actual value attribute: ", AnnotationUtilsTests.asArray("/test"), synthesizedWebMapping1.value());
        AnnotationUtilsTests.WebMapping synthesizedWebMapping2 = synthesizeAnnotation(webMapping);
        Assert.assertThat(synthesizedWebMapping2, instanceOf(SynthesizedAnnotation.class));
        Assert.assertNotSame(webMapping, synthesizedWebMapping2);
        Assert.assertEquals("name attribute: ", "foo", synthesizedWebMapping2.name());
        Assert.assertArrayEquals("aliased path attribute: ", AnnotationUtilsTests.asArray("/test"), synthesizedWebMapping2.path());
        Assert.assertArrayEquals("actual value attribute: ", AnnotationUtilsTests.asArray("/test"), synthesizedWebMapping2.value());
    }

    @Test
    public void synthesizeAnnotationWithImplicitAliases() throws Exception {
        assertAnnotationSynthesisWithImplicitAliases(AnnotationUtilsTests.ValueImplicitAliasesContextConfigClass.class, "value");
        assertAnnotationSynthesisWithImplicitAliases(AnnotationUtilsTests.Location1ImplicitAliasesContextConfigClass.class, "location1");
        assertAnnotationSynthesisWithImplicitAliases(AnnotationUtilsTests.XmlImplicitAliasesContextConfigClass.class, "xmlFile");
        assertAnnotationSynthesisWithImplicitAliases(AnnotationUtilsTests.GroovyImplicitAliasesContextConfigClass.class, "groovyScript");
    }

    @Test
    public void synthesizeAnnotationWithImplicitAliasesWithImpliedAliasNamesOmitted() throws Exception {
        assertAnnotationSynthesisWithImplicitAliasesWithImpliedAliasNamesOmitted(AnnotationUtilsTests.ValueImplicitAliasesWithImpliedAliasNamesOmittedContextConfigClass.class, "value");
        assertAnnotationSynthesisWithImplicitAliasesWithImpliedAliasNamesOmitted(AnnotationUtilsTests.LocationsImplicitAliasesWithImpliedAliasNamesOmittedContextConfigClass.class, "location");
        assertAnnotationSynthesisWithImplicitAliasesWithImpliedAliasNamesOmitted(AnnotationUtilsTests.XmlFilesImplicitAliasesWithImpliedAliasNamesOmittedContextConfigClass.class, "xmlFile");
    }

    @Test
    public void synthesizeAnnotationWithImplicitAliasesForAliasPair() throws Exception {
        Class<?> clazz = AnnotationUtilsTests.ImplicitAliasesForAliasPairContextConfigClass.class;
        AnnotationUtilsTests.ImplicitAliasesForAliasPairContextConfig config = clazz.getAnnotation(AnnotationUtilsTests.ImplicitAliasesForAliasPairContextConfig.class);
        Assert.assertNotNull(config);
        AnnotationUtilsTests.ImplicitAliasesForAliasPairContextConfig synthesizedConfig = synthesizeAnnotation(config);
        Assert.assertThat(synthesizedConfig, instanceOf(SynthesizedAnnotation.class));
        Assert.assertEquals("xmlFile: ", "test.xml", synthesizedConfig.xmlFile());
        Assert.assertEquals("groovyScript: ", "test.xml", synthesizedConfig.groovyScript());
    }

    @Test
    public void synthesizeAnnotationWithTransitiveImplicitAliases() throws Exception {
        Class<?> clazz = AnnotationUtilsTests.TransitiveImplicitAliasesContextConfigClass.class;
        AnnotationUtilsTests.TransitiveImplicitAliasesContextConfig config = clazz.getAnnotation(AnnotationUtilsTests.TransitiveImplicitAliasesContextConfig.class);
        Assert.assertNotNull(config);
        AnnotationUtilsTests.TransitiveImplicitAliasesContextConfig synthesizedConfig = synthesizeAnnotation(config);
        Assert.assertThat(synthesizedConfig, instanceOf(SynthesizedAnnotation.class));
        Assert.assertEquals("xml: ", "test.xml", synthesizedConfig.xml());
        Assert.assertEquals("groovy: ", "test.xml", synthesizedConfig.groovy());
    }

    @Test
    public void synthesizeAnnotationWithTransitiveImplicitAliasesForAliasPair() throws Exception {
        Class<?> clazz = AnnotationUtilsTests.TransitiveImplicitAliasesForAliasPairContextConfigClass.class;
        AnnotationUtilsTests.TransitiveImplicitAliasesForAliasPairContextConfig config = clazz.getAnnotation(AnnotationUtilsTests.TransitiveImplicitAliasesForAliasPairContextConfig.class);
        Assert.assertNotNull(config);
        AnnotationUtilsTests.TransitiveImplicitAliasesForAliasPairContextConfig synthesizedConfig = synthesizeAnnotation(config);
        Assert.assertThat(synthesizedConfig, instanceOf(SynthesizedAnnotation.class));
        Assert.assertEquals("xml: ", "test.xml", synthesizedConfig.xml());
        Assert.assertEquals("groovy: ", "test.xml", synthesizedConfig.groovy());
    }

    @Test
    public void synthesizeAnnotationWithImplicitAliasesWithMissingDefaultValues() throws Exception {
        Class<?> clazz = AnnotationUtilsTests.ImplicitAliasesWithMissingDefaultValuesContextConfigClass.class;
        Class<AnnotationUtilsTests.ImplicitAliasesWithMissingDefaultValuesContextConfig> annotationType = AnnotationUtilsTests.ImplicitAliasesWithMissingDefaultValuesContextConfig.class;
        AnnotationUtilsTests.ImplicitAliasesWithMissingDefaultValuesContextConfig config = clazz.getAnnotation(annotationType);
        Assert.assertNotNull(config);
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(startsWith("Misconfigured aliases:"));
        exception.expectMessage(containsString((("attribute 'location1' in annotation [" + (annotationType.getName())) + "]")));
        exception.expectMessage(containsString((("attribute 'location2' in annotation [" + (annotationType.getName())) + "]")));
        exception.expectMessage(containsString("default values"));
        synthesizeAnnotation(config, clazz);
    }

    @Test
    public void synthesizeAnnotationWithImplicitAliasesWithDifferentDefaultValues() throws Exception {
        Class<?> clazz = AnnotationUtilsTests.ImplicitAliasesWithDifferentDefaultValuesContextConfigClass.class;
        Class<AnnotationUtilsTests.ImplicitAliasesWithDifferentDefaultValuesContextConfig> annotationType = AnnotationUtilsTests.ImplicitAliasesWithDifferentDefaultValuesContextConfig.class;
        AnnotationUtilsTests.ImplicitAliasesWithDifferentDefaultValuesContextConfig config = clazz.getAnnotation(annotationType);
        Assert.assertNotNull(config);
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(startsWith("Misconfigured aliases:"));
        exception.expectMessage(containsString((("attribute 'location1' in annotation [" + (annotationType.getName())) + "]")));
        exception.expectMessage(containsString((("attribute 'location2' in annotation [" + (annotationType.getName())) + "]")));
        exception.expectMessage(containsString("same default value"));
        synthesizeAnnotation(config, clazz);
    }

    @Test
    public void synthesizeAnnotationWithImplicitAliasesWithDuplicateValues() throws Exception {
        Class<?> clazz = AnnotationUtilsTests.ImplicitAliasesWithDuplicateValuesContextConfigClass.class;
        Class<AnnotationUtilsTests.ImplicitAliasesWithDuplicateValuesContextConfig> annotationType = AnnotationUtilsTests.ImplicitAliasesWithDuplicateValuesContextConfig.class;
        AnnotationUtilsTests.ImplicitAliasesWithDuplicateValuesContextConfig config = clazz.getAnnotation(annotationType);
        Assert.assertNotNull(config);
        AnnotationUtilsTests.ImplicitAliasesWithDuplicateValuesContextConfig synthesizedConfig = synthesizeAnnotation(config, clazz);
        Assert.assertNotNull(synthesizedConfig);
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(startsWith("In annotation"));
        exception.expectMessage(containsString(annotationType.getName()));
        exception.expectMessage(containsString("declared on class"));
        exception.expectMessage(containsString(clazz.getName()));
        exception.expectMessage(containsString("and synthesized from"));
        exception.expectMessage(either(containsString("attribute 'location1' and its alias 'location2'")).or(containsString("attribute 'location2' and its alias 'location1'")));
        exception.expectMessage(either(containsString("are present with values of [1] and [2]")).or(containsString("are present with values of [2] and [1]")));
        synthesizedConfig.location1();
    }

    @Test
    public void synthesizeAnnotationFromMapWithoutAttributeAliases() throws Exception {
        Component component = AnnotationUtilsTests.WebController.class.getAnnotation(Component.class);
        Assert.assertNotNull(component);
        Map<String, Object> map = Collections.singletonMap(VALUE, "webController");
        Component synthesizedComponent = synthesizeAnnotation(map, Component.class, AnnotationUtilsTests.WebController.class);
        Assert.assertNotNull(synthesizedComponent);
        Assert.assertNotSame(component, synthesizedComponent);
        Assert.assertEquals("value from component: ", "webController", component.value());
        Assert.assertEquals("value from synthesized component: ", "webController", synthesizedComponent.value());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void synthesizeAnnotationFromMapWithNestedMap() throws Exception {
        AnnotationUtilsTests.ComponentScanSingleFilter componentScan = AnnotationUtilsTests.ComponentScanSingleFilterClass.class.getAnnotation(AnnotationUtilsTests.ComponentScanSingleFilter.class);
        Assert.assertNotNull(componentScan);
        Assert.assertEquals("value from ComponentScan: ", "*Foo", componentScan.value().pattern());
        AnnotationAttributes attributes = getAnnotationAttributes(AnnotationUtilsTests.ComponentScanSingleFilterClass.class, componentScan, false, true);
        Assert.assertNotNull(attributes);
        Assert.assertEquals(AnnotationUtilsTests.ComponentScanSingleFilter.class, attributes.annotationType());
        Map<String, Object> filterMap = ((Map<String, Object>) (attributes.get("value")));
        Assert.assertNotNull(filterMap);
        Assert.assertEquals("*Foo", filterMap.get("pattern"));
        // Modify nested map
        filterMap.put("pattern", "newFoo");
        filterMap.put("enigma", 42);
        AnnotationUtilsTests.ComponentScanSingleFilter synthesizedComponentScan = synthesizeAnnotation(attributes, AnnotationUtilsTests.ComponentScanSingleFilter.class, AnnotationUtilsTests.ComponentScanSingleFilterClass.class);
        Assert.assertNotNull(synthesizedComponentScan);
        Assert.assertNotSame(componentScan, synthesizedComponentScan);
        Assert.assertEquals("value from synthesized ComponentScan: ", "newFoo", synthesizedComponentScan.value().pattern());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void synthesizeAnnotationFromMapWithNestedArrayOfMaps() throws Exception {
        AnnotationUtilsTests.ComponentScan componentScan = AnnotationUtilsTests.ComponentScanClass.class.getAnnotation(AnnotationUtilsTests.ComponentScan.class);
        Assert.assertNotNull(componentScan);
        AnnotationAttributes attributes = getAnnotationAttributes(AnnotationUtilsTests.ComponentScanClass.class, componentScan, false, true);
        Assert.assertNotNull(attributes);
        Assert.assertEquals(AnnotationUtilsTests.ComponentScan.class, attributes.annotationType());
        Map<String, Object>[] filters = ((Map[]) (attributes.get("excludeFilters")));
        Assert.assertNotNull(filters);
        List<String> patterns = Arrays.stream(filters).map(( m) -> ((String) (m.get("pattern")))).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("*Foo", "*Bar"), patterns);
        // Modify nested maps
        filters[0].put("pattern", "newFoo");
        filters[0].put("enigma", 42);
        filters[1].put("pattern", "newBar");
        filters[1].put("enigma", 42);
        AnnotationUtilsTests.ComponentScan synthesizedComponentScan = synthesizeAnnotation(attributes, AnnotationUtilsTests.ComponentScan.class, AnnotationUtilsTests.ComponentScanClass.class);
        Assert.assertNotNull(synthesizedComponentScan);
        Assert.assertNotSame(componentScan, synthesizedComponentScan);
        patterns = Arrays.stream(synthesizedComponentScan.excludeFilters()).map(AnnotationUtilsTests.Filter::pattern).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("newFoo", "newBar"), patterns);
    }

    @Test
    public void synthesizeAnnotationFromDefaultsWithoutAttributeAliases() throws Exception {
        AnnotationUtilsTests.AnnotationWithDefaults annotationWithDefaults = synthesizeAnnotation(AnnotationUtilsTests.AnnotationWithDefaults.class);
        Assert.assertNotNull(annotationWithDefaults);
        Assert.assertEquals("text: ", "enigma", annotationWithDefaults.text());
        Assert.assertTrue("predicate: ", annotationWithDefaults.predicate());
        Assert.assertArrayEquals("characters: ", new char[]{ 'a', 'b', 'c' }, annotationWithDefaults.characters());
    }

    @Test
    public void synthesizeAnnotationFromDefaultsWithAttributeAliases() throws Exception {
        AnnotationUtilsTests.ContextConfig contextConfig = synthesizeAnnotation(AnnotationUtilsTests.ContextConfig.class);
        Assert.assertNotNull(contextConfig);
        Assert.assertEquals("value: ", "", contextConfig.value());
        Assert.assertEquals("location: ", "", contextConfig.location());
    }

    @Test
    public void synthesizeAnnotationWithAttributeAliasesWithDifferentValues() throws Exception {
        AnnotationUtilsTests.ContextConfig contextConfig = synthesizeAnnotation(AnnotationUtilsTests.ContextConfigMismatch.class.getAnnotation(AnnotationUtilsTests.ContextConfig.class));
        exception.expect(AnnotationConfigurationException.class);
        getValue(contextConfig);
    }

    @Test
    public void synthesizeAnnotationFromMapWithMinimalAttributesWithAttributeAliases() throws Exception {
        Map<String, Object> map = Collections.singletonMap("location", "test.xml");
        AnnotationUtilsTests.ContextConfig contextConfig = synthesizeAnnotation(map, AnnotationUtilsTests.ContextConfig.class, null);
        Assert.assertNotNull(contextConfig);
        Assert.assertEquals("value: ", "test.xml", contextConfig.value());
        Assert.assertEquals("location: ", "test.xml", contextConfig.location());
    }

    @Test
    public void synthesizeAnnotationFromMapWithAttributeAliasesThatOverrideArraysWithSingleElements() throws Exception {
        Map<String, Object> map = Collections.singletonMap("value", "/foo");
        AnnotationUtilsTests.Get get = synthesizeAnnotation(map, AnnotationUtilsTests.Get.class, null);
        Assert.assertNotNull(get);
        Assert.assertEquals("value: ", "/foo", get.value());
        Assert.assertEquals("path: ", "/foo", get.path());
        map = Collections.singletonMap("path", "/foo");
        get = synthesizeAnnotation(map, AnnotationUtilsTests.Get.class, null);
        Assert.assertNotNull(get);
        Assert.assertEquals("value: ", "/foo", get.value());
        Assert.assertEquals("path: ", "/foo", get.path());
    }

    @Test
    public void synthesizeAnnotationFromMapWithImplicitAttributeAliases() throws Exception {
        assertAnnotationSynthesisFromMapWithImplicitAliases("value");
        assertAnnotationSynthesisFromMapWithImplicitAliases("location1");
        assertAnnotationSynthesisFromMapWithImplicitAliases("location2");
        assertAnnotationSynthesisFromMapWithImplicitAliases("location3");
        assertAnnotationSynthesisFromMapWithImplicitAliases("xmlFile");
        assertAnnotationSynthesisFromMapWithImplicitAliases("groovyScript");
    }

    @Test
    public void synthesizeAnnotationFromMapWithMissingAttributeValue() throws Exception {
        assertMissingTextAttribute(Collections.emptyMap());
    }

    @Test
    public void synthesizeAnnotationFromMapWithNullAttributeValue() throws Exception {
        Map<String, Object> map = Collections.singletonMap("text", null);
        Assert.assertTrue(map.containsKey("text"));
        assertMissingTextAttribute(map);
    }

    @Test
    public void synthesizeAnnotationFromMapWithAttributeOfIncorrectType() throws Exception {
        Map<String, Object> map = Collections.singletonMap(VALUE, 42L);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage(startsWith("Attributes map"));
        exception.expectMessage(containsString("returned a value of type [java.lang.Long]"));
        exception.expectMessage(containsString("for attribute 'value'"));
        exception.expectMessage(containsString("but a value of type [java.lang.String] is required"));
        exception.expectMessage(containsString((("as defined by annotation type [" + (Component.class.getName())) + "]")));
        synthesizeAnnotation(map, Component.class, null);
    }

    @Test
    public void synthesizeAnnotationFromAnnotationAttributesWithoutAttributeAliases() throws Exception {
        // 1) Get an annotation
        Component component = AnnotationUtilsTests.WebController.class.getAnnotation(Component.class);
        Assert.assertNotNull(component);
        // 2) Convert the annotation into AnnotationAttributes
        AnnotationAttributes attributes = getAnnotationAttributes(AnnotationUtilsTests.WebController.class, component);
        Assert.assertNotNull(attributes);
        // 3) Synthesize the AnnotationAttributes back into an annotation
        Component synthesizedComponent = synthesizeAnnotation(attributes, Component.class, AnnotationUtilsTests.WebController.class);
        Assert.assertNotNull(synthesizedComponent);
        // 4) Verify that the original and synthesized annotations are equivalent
        Assert.assertNotSame(component, synthesizedComponent);
        Assert.assertEquals(component, synthesizedComponent);
        Assert.assertEquals("value from component: ", "webController", component.value());
        Assert.assertEquals("value from synthesized component: ", "webController", synthesizedComponent.value());
    }

    @Test
    public void toStringForSynthesizedAnnotations() throws Exception {
        Method methodWithPath = AnnotationUtilsTests.WebController.class.getMethod("handleMappedWithPathAttribute");
        AnnotationUtilsTests.WebMapping webMappingWithAliases = methodWithPath.getAnnotation(AnnotationUtilsTests.WebMapping.class);
        Assert.assertNotNull(webMappingWithAliases);
        Method methodWithPathAndValue = AnnotationUtilsTests.WebController.class.getMethod("handleMappedWithSamePathAndValueAttributes");
        AnnotationUtilsTests.WebMapping webMappingWithPathAndValue = methodWithPathAndValue.getAnnotation(AnnotationUtilsTests.WebMapping.class);
        Assert.assertNotNull(webMappingWithPathAndValue);
        AnnotationUtilsTests.WebMapping synthesizedWebMapping1 = synthesizeAnnotation(webMappingWithAliases);
        Assert.assertNotNull(synthesizedWebMapping1);
        AnnotationUtilsTests.WebMapping synthesizedWebMapping2 = synthesizeAnnotation(webMappingWithAliases);
        Assert.assertNotNull(synthesizedWebMapping2);
        Assert.assertThat(webMappingWithAliases.toString(), is(not(synthesizedWebMapping1.toString())));
        assertToStringForWebMappingWithPathAndValue(synthesizedWebMapping1);
        assertToStringForWebMappingWithPathAndValue(synthesizedWebMapping2);
    }

    @Test
    public void equalsForSynthesizedAnnotations() throws Exception {
        Method methodWithPath = AnnotationUtilsTests.WebController.class.getMethod("handleMappedWithPathAttribute");
        AnnotationUtilsTests.WebMapping webMappingWithAliases = methodWithPath.getAnnotation(AnnotationUtilsTests.WebMapping.class);
        Assert.assertNotNull(webMappingWithAliases);
        Method methodWithPathAndValue = AnnotationUtilsTests.WebController.class.getMethod("handleMappedWithSamePathAndValueAttributes");
        AnnotationUtilsTests.WebMapping webMappingWithPathAndValue = methodWithPathAndValue.getAnnotation(AnnotationUtilsTests.WebMapping.class);
        Assert.assertNotNull(webMappingWithPathAndValue);
        AnnotationUtilsTests.WebMapping synthesizedWebMapping1 = synthesizeAnnotation(webMappingWithAliases);
        Assert.assertNotNull(synthesizedWebMapping1);
        AnnotationUtilsTests.WebMapping synthesizedWebMapping2 = synthesizeAnnotation(webMappingWithAliases);
        Assert.assertNotNull(synthesizedWebMapping2);
        // Equality amongst standard annotations
        Assert.assertThat(webMappingWithAliases, is(webMappingWithAliases));
        Assert.assertThat(webMappingWithPathAndValue, is(webMappingWithPathAndValue));
        // Inequality amongst standard annotations
        Assert.assertThat(webMappingWithAliases, is(not(webMappingWithPathAndValue)));
        Assert.assertThat(webMappingWithPathAndValue, is(not(webMappingWithAliases)));
        // Equality amongst synthesized annotations
        Assert.assertThat(synthesizedWebMapping1, is(synthesizedWebMapping1));
        Assert.assertThat(synthesizedWebMapping2, is(synthesizedWebMapping2));
        Assert.assertThat(synthesizedWebMapping1, is(synthesizedWebMapping2));
        Assert.assertThat(synthesizedWebMapping2, is(synthesizedWebMapping1));
        // Equality between standard and synthesized annotations
        Assert.assertThat(synthesizedWebMapping1, is(webMappingWithPathAndValue));
        Assert.assertThat(webMappingWithPathAndValue, is(synthesizedWebMapping1));
        // Inequality between standard and synthesized annotations
        Assert.assertThat(synthesizedWebMapping1, is(not(webMappingWithAliases)));
        Assert.assertThat(webMappingWithAliases, is(not(synthesizedWebMapping1)));
    }

    @Test
    public void hashCodeForSynthesizedAnnotations() throws Exception {
        Method methodWithPath = AnnotationUtilsTests.WebController.class.getMethod("handleMappedWithPathAttribute");
        AnnotationUtilsTests.WebMapping webMappingWithAliases = methodWithPath.getAnnotation(AnnotationUtilsTests.WebMapping.class);
        Assert.assertNotNull(webMappingWithAliases);
        Method methodWithPathAndValue = AnnotationUtilsTests.WebController.class.getMethod("handleMappedWithSamePathAndValueAttributes");
        AnnotationUtilsTests.WebMapping webMappingWithPathAndValue = methodWithPathAndValue.getAnnotation(AnnotationUtilsTests.WebMapping.class);
        Assert.assertNotNull(webMappingWithPathAndValue);
        AnnotationUtilsTests.WebMapping synthesizedWebMapping1 = synthesizeAnnotation(webMappingWithAliases);
        Assert.assertNotNull(synthesizedWebMapping1);
        AnnotationUtilsTests.WebMapping synthesizedWebMapping2 = synthesizeAnnotation(webMappingWithAliases);
        Assert.assertNotNull(synthesizedWebMapping2);
        // Equality amongst standard annotations
        Assert.assertThat(webMappingWithAliases.hashCode(), is(webMappingWithAliases.hashCode()));
        Assert.assertThat(webMappingWithPathAndValue.hashCode(), is(webMappingWithPathAndValue.hashCode()));
        // Inequality amongst standard annotations
        Assert.assertThat(webMappingWithAliases.hashCode(), is(not(webMappingWithPathAndValue.hashCode())));
        Assert.assertThat(webMappingWithPathAndValue.hashCode(), is(not(webMappingWithAliases.hashCode())));
        // Equality amongst synthesized annotations
        Assert.assertThat(synthesizedWebMapping1.hashCode(), is(synthesizedWebMapping1.hashCode()));
        Assert.assertThat(synthesizedWebMapping2.hashCode(), is(synthesizedWebMapping2.hashCode()));
        Assert.assertThat(synthesizedWebMapping1.hashCode(), is(synthesizedWebMapping2.hashCode()));
        Assert.assertThat(synthesizedWebMapping2.hashCode(), is(synthesizedWebMapping1.hashCode()));
        // Equality between standard and synthesized annotations
        Assert.assertThat(synthesizedWebMapping1.hashCode(), is(webMappingWithPathAndValue.hashCode()));
        Assert.assertThat(webMappingWithPathAndValue.hashCode(), is(synthesizedWebMapping1.hashCode()));
        // Inequality between standard and synthesized annotations
        Assert.assertThat(synthesizedWebMapping1.hashCode(), is(not(webMappingWithAliases.hashCode())));
        Assert.assertThat(webMappingWithAliases.hashCode(), is(not(synthesizedWebMapping1.hashCode())));
    }

    /**
     * Fully reflection-based test that verifies support for
     * {@linkplain AnnotationUtils#synthesizeAnnotation synthesizing annotations}
     * across packages with non-public visibility of user types (e.g., a non-public
     * annotation that uses {@code @AliasFor}).
     */
    @Test
    @SuppressWarnings("unchecked")
    public void synthesizeNonPublicAnnotationWithAttributeAliasesFromDifferentPackage() throws Exception {
        Class<?> clazz = ClassUtils.forName("org.springframework.core.annotation.subpackage.NonPublicAliasedAnnotatedClass", null);
        Class<? extends Annotation> annotationType = ((Class<? extends Annotation>) (ClassUtils.forName("org.springframework.core.annotation.subpackage.NonPublicAliasedAnnotation", null)));
        Annotation annotation = clazz.getAnnotation(annotationType);
        Assert.assertNotNull(annotation);
        Annotation synthesizedAnnotation = synthesizeAnnotation(annotation);
        Assert.assertNotSame(annotation, synthesizedAnnotation);
        Assert.assertNotNull(synthesizedAnnotation);
        Assert.assertEquals("name attribute: ", "test", getValue(synthesizedAnnotation, "name"));
        Assert.assertEquals("aliased path attribute: ", "/test", getValue(synthesizedAnnotation, "path"));
        Assert.assertEquals("aliased path attribute: ", "/test", getValue(synthesizedAnnotation, "value"));
    }

    @Test
    public void synthesizeAnnotationWithAttributeAliasesInNestedAnnotations() throws Exception {
        List<String> expectedLocations = Arrays.asList("A", "B");
        AnnotationUtilsTests.Hierarchy hierarchy = AnnotationUtilsTests.ConfigHierarchyTestCase.class.getAnnotation(AnnotationUtilsTests.Hierarchy.class);
        Assert.assertNotNull(hierarchy);
        AnnotationUtilsTests.Hierarchy synthesizedHierarchy = synthesizeAnnotation(hierarchy);
        Assert.assertNotSame(hierarchy, synthesizedHierarchy);
        Assert.assertThat(synthesizedHierarchy, instanceOf(SynthesizedAnnotation.class));
        AnnotationUtilsTests.ContextConfig[] configs = synthesizedHierarchy.value();
        Assert.assertNotNull(configs);
        Assert.assertTrue("nested annotations must be synthesized", Arrays.stream(configs).allMatch(( c) -> c instanceof SynthesizedAnnotation));
        List<String> locations = Arrays.stream(configs).map(AnnotationUtilsTests.ContextConfig::location).collect(Collectors.toList());
        Assert.assertThat(locations, is(expectedLocations));
        List<String> values = Arrays.stream(configs).map(AnnotationUtilsTests.ContextConfig::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedLocations));
    }

    @Test
    public void synthesizeAnnotationWithArrayOfAnnotations() throws Exception {
        List<String> expectedLocations = Arrays.asList("A", "B");
        AnnotationUtilsTests.Hierarchy hierarchy = AnnotationUtilsTests.ConfigHierarchyTestCase.class.getAnnotation(AnnotationUtilsTests.Hierarchy.class);
        Assert.assertNotNull(hierarchy);
        AnnotationUtilsTests.Hierarchy synthesizedHierarchy = synthesizeAnnotation(hierarchy);
        Assert.assertThat(synthesizedHierarchy, instanceOf(SynthesizedAnnotation.class));
        AnnotationUtilsTests.ContextConfig contextConfig = AnnotationUtilsTests.SimpleConfigTestCase.class.getAnnotation(AnnotationUtilsTests.ContextConfig.class);
        Assert.assertNotNull(contextConfig);
        AnnotationUtilsTests.ContextConfig[] configs = synthesizedHierarchy.value();
        List<String> locations = Arrays.stream(configs).map(AnnotationUtilsTests.ContextConfig::location).collect(Collectors.toList());
        Assert.assertThat(locations, is(expectedLocations));
        // Alter array returned from synthesized annotation
        configs[0] = contextConfig;
        // Re-retrieve the array from the synthesized annotation
        configs = synthesizedHierarchy.value();
        List<String> values = Arrays.stream(configs).map(AnnotationUtilsTests.ContextConfig::value).collect(Collectors.toList());
        Assert.assertThat(values, is(expectedLocations));
    }

    @Test
    public void synthesizeAnnotationWithArrayOfChars() throws Exception {
        AnnotationUtilsTests.CharsContainer charsContainer = AnnotationUtilsTests.GroupOfCharsClass.class.getAnnotation(AnnotationUtilsTests.CharsContainer.class);
        Assert.assertNotNull(charsContainer);
        AnnotationUtilsTests.CharsContainer synthesizedCharsContainer = synthesizeAnnotation(charsContainer);
        Assert.assertThat(synthesizedCharsContainer, instanceOf(SynthesizedAnnotation.class));
        char[] chars = synthesizedCharsContainer.chars();
        Assert.assertArrayEquals(new char[]{ 'x', 'y', 'z' }, chars);
        // Alter array returned from synthesized annotation
        chars[0] = '?';
        // Re-retrieve the array from the synthesized annotation
        chars = synthesizedCharsContainer.chars();
        Assert.assertArrayEquals(new char[]{ 'x', 'y', 'z' }, chars);
    }

    @Test
    public void interfaceWithAnnotatedMethods() {
        Assert.assertTrue(AnnotationUtils.AnnotationUtils.getAnnotatedMethodsInBaseType(AnnotationUtilsTests.NonAnnotatedInterface.class).isEmpty());
        Assert.assertFalse(AnnotationUtils.AnnotationUtils.getAnnotatedMethodsInBaseType(AnnotationUtilsTests.AnnotatedInterface.class).isEmpty());
        Assert.assertTrue(AnnotationUtils.AnnotationUtils.getAnnotatedMethodsInBaseType(AnnotationUtilsTests.NullableAnnotatedInterface.class).isEmpty());
    }

    @Component("meta1")
    @Order
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface Meta1 {}

    @Component("meta2")
    @AnnotationUtilsTests.Transactional(readOnly = true)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Meta2 {}

    @AnnotationUtilsTests.Meta2
    @Retention(RetentionPolicy.RUNTIME)
    @interface MetaMeta {}

    @AnnotationUtilsTests.MetaMeta
    @Retention(RetentionPolicy.RUNTIME)
    @interface MetaMetaMeta {}

    @AnnotationUtilsTests.MetaCycle3
    @Retention(RetentionPolicy.RUNTIME)
    @interface MetaCycle1 {}

    @AnnotationUtilsTests.MetaCycle1
    @Retention(RetentionPolicy.RUNTIME)
    @interface MetaCycle2 {}

    @AnnotationUtilsTests.MetaCycle2
    @Retention(RetentionPolicy.RUNTIME)
    @interface MetaCycle3 {}

    @AnnotationUtilsTests.Meta1
    interface InterfaceWithMetaAnnotation {}

    @AnnotationUtilsTests.Meta2
    static class ClassWithLocalMetaAnnotationAndMetaAnnotatedInterface implements AnnotationUtilsTests.InterfaceWithMetaAnnotation {}

    @AnnotationUtilsTests.Meta1
    static class ClassWithInheritedMetaAnnotation {}

    @AnnotationUtilsTests.Meta2
    static class SubClassWithInheritedMetaAnnotation extends AnnotationUtilsTests.ClassWithInheritedMetaAnnotation {}

    static class SubSubClassWithInheritedMetaAnnotation extends AnnotationUtilsTests.SubClassWithInheritedMetaAnnotation {}

    @AnnotationUtilsTests.Transactional
    static class ClassWithInheritedAnnotation {}

    @AnnotationUtilsTests.Meta2
    static class SubClassWithInheritedAnnotation extends AnnotationUtilsTests.ClassWithInheritedAnnotation {}

    static class SubSubClassWithInheritedAnnotation extends AnnotationUtilsTests.SubClassWithInheritedAnnotation {}

    @AnnotationUtilsTests.MetaMeta
    static class MetaMetaAnnotatedClass {}

    @AnnotationUtilsTests.MetaMetaMeta
    static class MetaMetaMetaAnnotatedClass {}

    @AnnotationUtilsTests.MetaCycle3
    static class MetaCycleAnnotatedClass {}

    public interface AnnotatedInterface {
        @Order(0)
        void fromInterfaceImplementedByRoot();
    }

    public interface NullableAnnotatedInterface {
        @Nullable
        void fromInterfaceImplementedByRoot();
    }

    public static class Root implements AnnotationUtilsTests.AnnotatedInterface {
        @Order(27)
        public void annotatedOnRoot() {
        }

        @AnnotationUtilsTests.Meta1
        public void metaAnnotatedOnRoot() {
        }

        public void overrideToAnnotate() {
        }

        @Order(27)
        public void overrideWithoutNewAnnotation() {
        }

        public void notAnnotated() {
        }

        @Override
        public void fromInterfaceImplementedByRoot() {
        }
    }

    public static class Leaf extends AnnotationUtilsTests.Root {
        @Order(25)
        public void annotatedOnLeaf() {
        }

        @AnnotationUtilsTests.Meta1
        public void metaAnnotatedOnLeaf() {
        }

        @AnnotationUtilsTests.MetaMeta
        public void metaMetaAnnotatedOnLeaf() {
        }

        @Override
        @Order(1)
        public void overrideToAnnotate() {
        }

        @Override
        public void overrideWithoutNewAnnotation() {
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface Transactional {
        boolean readOnly() default false;
    }

    public abstract static class Foo<T> {
        @Order(1)
        public abstract void something(T arg);
    }

    public static class SimpleFoo extends AnnotationUtilsTests.Foo<String> {
        @Override
        @AnnotationUtilsTests.Transactional
        public void something(final String arg) {
        }
    }

    @AnnotationUtilsTests.Transactional
    public interface InheritedAnnotationInterface {}

    public interface SubInheritedAnnotationInterface extends AnnotationUtilsTests.InheritedAnnotationInterface {}

    public interface SubSubInheritedAnnotationInterface extends AnnotationUtilsTests.SubInheritedAnnotationInterface {}

    @Order
    public interface NonInheritedAnnotationInterface {}

    public interface SubNonInheritedAnnotationInterface extends AnnotationUtilsTests.NonInheritedAnnotationInterface {}

    public interface SubSubNonInheritedAnnotationInterface extends AnnotationUtilsTests.SubNonInheritedAnnotationInterface {}

    public static class NonAnnotatedClass {}

    public interface NonAnnotatedInterface {}

    @AnnotationUtilsTests.Transactional
    public static class InheritedAnnotationClass {}

    public static class SubInheritedAnnotationClass extends AnnotationUtilsTests.InheritedAnnotationClass {}

    @Order
    public static class NonInheritedAnnotationClass {}

    public static class SubNonInheritedAnnotationClass extends AnnotationUtilsTests.NonInheritedAnnotationClass {}

    @AnnotationUtilsTests.Transactional
    public static class TransactionalClass {}

    @Order
    public static class TransactionalAndOrderedClass extends AnnotationUtilsTests.TransactionalClass {}

    public static class SubTransactionalAndOrderedClass extends AnnotationUtilsTests.TransactionalAndOrderedClass {}

    public interface InterfaceWithAnnotatedMethod {
        @Order
        void foo();
    }

    public static class ImplementsInterfaceWithAnnotatedMethod implements AnnotationUtilsTests.InterfaceWithAnnotatedMethod {
        @Override
        public void foo() {
        }
    }

    public static class SubOfImplementsInterfaceWithAnnotatedMethod extends AnnotationUtilsTests.ImplementsInterfaceWithAnnotatedMethod {
        @Override
        public void foo() {
        }
    }

    public abstract static class AbstractDoesNotImplementInterfaceWithAnnotatedMethod implements AnnotationUtilsTests.InterfaceWithAnnotatedMethod {}

    public static class SubOfAbstractImplementsInterfaceWithAnnotatedMethod extends AnnotationUtilsTests.AbstractDoesNotImplementInterfaceWithAnnotatedMethod {
        @Override
        public void foo() {
        }
    }

    public interface InterfaceWithGenericAnnotatedMethod<T> {
        @Order
        void foo(T t);
    }

    public static class ImplementsInterfaceWithGenericAnnotatedMethod implements AnnotationUtilsTests.InterfaceWithGenericAnnotatedMethod<String> {
        public void foo(String t) {
        }
    }

    public abstract static class BaseClassWithGenericAnnotatedMethod<T> {
        @Order
        abstract void foo(T t);
    }

    public static class ExtendsBaseClassWithGenericAnnotatedMethod extends AnnotationUtilsTests.BaseClassWithGenericAnnotatedMethod<String> {
        public void foo(String t) {
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface MyRepeatableContainer {
        AnnotationUtilsTests.MyRepeatable[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @Repeatable(AnnotationUtilsTests.MyRepeatableContainer.class)
    @interface MyRepeatable {
        String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @AnnotationUtilsTests.MyRepeatable("meta1")
    @interface MyRepeatableMeta1 {}

    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @AnnotationUtilsTests.MyRepeatable("meta2")
    @interface MyRepeatableMeta2 {}

    interface InterfaceWithRepeated {
        @AnnotationUtilsTests.MyRepeatable("A")
        @AnnotationUtilsTests.MyRepeatableContainer({ @AnnotationUtilsTests.MyRepeatable("B"), @AnnotationUtilsTests.MyRepeatable("C") })
        @AnnotationUtilsTests.MyRepeatableMeta1
        void foo();
    }

    @AnnotationUtilsTests.MyRepeatable("A")
    @AnnotationUtilsTests.MyRepeatableContainer({ @AnnotationUtilsTests.MyRepeatable("B"), @AnnotationUtilsTests.MyRepeatable("C") })
    @AnnotationUtilsTests.MyRepeatableMeta1
    static class MyRepeatableClass {}

    static class SubMyRepeatableClass extends AnnotationUtilsTests.MyRepeatableClass {}

    @AnnotationUtilsTests.MyRepeatable("X")
    @AnnotationUtilsTests.MyRepeatableContainer({ @AnnotationUtilsTests.MyRepeatable("Y"), @AnnotationUtilsTests.MyRepeatable("Z") })
    @AnnotationUtilsTests.MyRepeatableMeta2
    static class SubMyRepeatableWithAdditionalLocalDeclarationsClass extends AnnotationUtilsTests.MyRepeatableClass {}

    static class SubSubMyRepeatableWithAdditionalLocalDeclarationsClass extends AnnotationUtilsTests.SubMyRepeatableWithAdditionalLocalDeclarationsClass {}

    enum RequestMethod {

        GET,
        POST;}

    /**
     * Mock of {@code org.springframework.web.bind.annotation.RequestMapping}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface WebMapping {
        String name();

        @AliasFor("path")
        String[] value() default "";

        @AliasFor(attribute = "value")
        String[] path() default "";

        AnnotationUtilsTests.RequestMethod[] method() default {  };
    }

    /**
     * Mock of {@code org.springframework.web.bind.annotation.GetMapping}, except
     * that the String arrays are overridden with single String elements.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @AnnotationUtilsTests.WebMapping(method = AnnotationUtilsTests.RequestMethod.GET, name = "")
    @interface Get {
        @AliasFor(annotation = AnnotationUtilsTests.WebMapping.class)
        String value() default "";

        @AliasFor(annotation = AnnotationUtilsTests.WebMapping.class)
        String path() default "";
    }

    /**
     * Mock of {@code org.springframework.web.bind.annotation.PostMapping}, except
     * that the path is overridden by convention with single String element.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @AnnotationUtilsTests.WebMapping(method = AnnotationUtilsTests.RequestMethod.POST, name = "")
    @interface Post {
        String path() default "";
    }

    @Component("webController")
    static class WebController {
        @AnnotationUtilsTests.WebMapping(value = "/test", name = "foo")
        public void handleMappedWithValueAttribute() {
        }

        @AnnotationUtilsTests.WebMapping(path = "/test", name = "bar", method = { AnnotationUtilsTests.RequestMethod.GET, AnnotationUtilsTests.RequestMethod.POST })
        public void handleMappedWithPathAttribute() {
        }

        @AnnotationUtilsTests.Get("/test")
        public void getMappedWithValueAttribute() {
        }

        @AnnotationUtilsTests.Get(path = "/test")
        public void getMappedWithPathAttribute() {
        }

        @AnnotationUtilsTests.Post(path = "/test")
        public void postMappedWithPathAttribute() {
        }

        /**
         * mapping is logically "equal" to handleMappedWithPathAttribute().
         */
        @AnnotationUtilsTests.WebMapping(value = "/test", path = "/test", name = "bar", method = { AnnotationUtilsTests.RequestMethod.GET, AnnotationUtilsTests.RequestMethod.POST })
        public void handleMappedWithSamePathAndValueAttributes() {
        }

        @AnnotationUtilsTests.WebMapping(value = "/enigma", path = "/test", name = "baz")
        public void handleMappedWithDifferentPathAndValueAttributes() {
        }
    }

    /**
     * Mock of {@code org.springframework.test.context.ContextConfiguration}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface ContextConfig {
        @AliasFor("location")
        String value() default "";

        @AliasFor("value")
        String location() default "";

        Class<?> klass() default Object.class;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface BrokenContextConfig {
        // Intentionally missing:
        // @AliasFor("location")
        String value() default "";

        @AliasFor("value")
        String location() default "";
    }

    /**
     * Mock of {@code org.springframework.test.context.ContextHierarchy}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface Hierarchy {
        AnnotationUtilsTests.ContextConfig[] value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface BrokenHierarchy {
        AnnotationUtilsTests.BrokenContextConfig[] value();
    }

    @AnnotationUtilsTests.Hierarchy({ @AnnotationUtilsTests.ContextConfig("A"), @AnnotationUtilsTests.ContextConfig(location = "B") })
    static class ConfigHierarchyTestCase {}

    @AnnotationUtilsTests.BrokenHierarchy(@AnnotationUtilsTests.BrokenContextConfig)
    static class BrokenConfigHierarchyTestCase {}

    @AnnotationUtilsTests.ContextConfig("simple.xml")
    static class SimpleConfigTestCase {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface CharsContainer {
        @AliasFor(attribute = "chars")
        char[] value() default {  };

        @AliasFor(attribute = "value")
        char[] chars() default {  };
    }

    @AnnotationUtilsTests.CharsContainer(chars = { 'x', 'y', 'z' })
    static class GroupOfCharsClass {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasForWithMissingAttributeDeclaration {
        @AliasFor
        String foo() default "";
    }

    @AnnotationUtilsTests.AliasForWithMissingAttributeDeclaration
    static class AliasForWithMissingAttributeDeclarationClass {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasForWithDuplicateAttributeDeclaration {
        @AliasFor(value = "bar", attribute = "baz")
        String foo() default "";
    }

    @AnnotationUtilsTests.AliasForWithDuplicateAttributeDeclaration
    static class AliasForWithDuplicateAttributeDeclarationClass {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasForNonexistentAttribute {
        @AliasFor("bar")
        String foo() default "";
    }

    @AnnotationUtilsTests.AliasForNonexistentAttribute
    static class AliasForNonexistentAttributeClass {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasForWithoutMirroredAliasFor {
        @AliasFor("bar")
        String foo() default "";

        String bar() default "";
    }

    @AnnotationUtilsTests.AliasForWithoutMirroredAliasFor
    static class AliasForWithoutMirroredAliasForClass {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasForWithMirroredAliasForWrongAttribute {
        @AliasFor(attribute = "bar")
        String[] foo() default "";

        @AliasFor(attribute = "quux")
        String[] bar() default "";
    }

    @AnnotationUtilsTests.AliasForWithMirroredAliasForWrongAttribute
    static class AliasForWithMirroredAliasForWrongAttributeClass {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasForAttributeOfDifferentType {
        @AliasFor("bar")
        String[] foo() default "";

        @AliasFor("foo")
        boolean bar() default true;
    }

    @AnnotationUtilsTests.AliasForAttributeOfDifferentType
    static class AliasForAttributeOfDifferentTypeClass {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasForWithMissingDefaultValues {
        @AliasFor(attribute = "bar")
        String foo();

        @AliasFor(attribute = "foo")
        String bar();
    }

    @AnnotationUtilsTests.AliasForWithMissingDefaultValues(foo = "foo", bar = "bar")
    static class AliasForWithMissingDefaultValuesClass {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasForAttributeWithDifferentDefaultValue {
        @AliasFor("bar")
        String foo() default "X";

        @AliasFor("foo")
        String bar() default "Z";
    }

    @AnnotationUtilsTests.AliasForAttributeWithDifferentDefaultValue
    static class AliasForAttributeWithDifferentDefaultValueClass {}

    // @ContextConfig --> Intentionally NOT meta-present
    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasedComposedContextConfigNotMetaPresent {
        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String xmlConfigFile();
    }

    @AnnotationUtilsTests.AliasedComposedContextConfigNotMetaPresent(xmlConfigFile = "test.xml")
    static class AliasedComposedContextConfigNotMetaPresentClass {}

    @AnnotationUtilsTests.ContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasedComposedContextConfig {
        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String xmlConfigFile();
    }

    @AnnotationUtilsTests.ContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ImplicitAliasesContextConfig {
        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String xmlFile() default "";

        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String groovyScript() default "";

        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String value() default "";

        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String location1() default "";

        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String location2() default "";

        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String location3() default "";

        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "klass")
        Class<?> configClass() default Object.class;

        String nonAliasedAttribute() default "";
    }

    // Attribute value intentionally matches attribute name:
    @AnnotationUtilsTests.ImplicitAliasesContextConfig(groovyScript = "groovyScript")
    static class GroovyImplicitAliasesContextConfigClass {}

    // Attribute value intentionally matches attribute name:
    @AnnotationUtilsTests.ImplicitAliasesContextConfig(xmlFile = "xmlFile")
    static class XmlImplicitAliasesContextConfigClass {}

    // Attribute value intentionally matches attribute name:
    @AnnotationUtilsTests.ImplicitAliasesContextConfig("value")
    static class ValueImplicitAliasesContextConfigClass {}

    // Attribute value intentionally matches attribute name:
    @AnnotationUtilsTests.ImplicitAliasesContextConfig(location1 = "location1")
    static class Location1ImplicitAliasesContextConfigClass {}

    // Attribute value intentionally matches attribute name:
    @AnnotationUtilsTests.ImplicitAliasesContextConfig(location2 = "location2")
    static class Location2ImplicitAliasesContextConfigClass {}

    // Attribute value intentionally matches attribute name:
    @AnnotationUtilsTests.ImplicitAliasesContextConfig(location3 = "location3")
    static class Location3ImplicitAliasesContextConfigClass {}

    @AnnotationUtilsTests.ContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface ImplicitAliasesWithImpliedAliasNamesOmittedContextConfig {
        // intentionally omitted: attribute = "value"
        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class)
        String value() default "";

        // intentionally omitted: attribute = "locations"
        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class)
        String location() default "";

        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String xmlFile() default "";
    }

    @AnnotationUtilsTests.ImplicitAliasesWithImpliedAliasNamesOmittedContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface TransitiveImplicitAliasesWithImpliedAliasNamesOmittedContextConfig {
        @AliasFor(annotation = AnnotationUtilsTests.ImplicitAliasesWithImpliedAliasNamesOmittedContextConfig.class, attribute = "xmlFile")
        String xml() default "";

        @AliasFor(annotation = AnnotationUtilsTests.ImplicitAliasesWithImpliedAliasNamesOmittedContextConfig.class, attribute = "location")
        String groovy() default "";
    }

    // Attribute value intentionally matches attribute name:
    @AnnotationUtilsTests.ImplicitAliasesWithImpliedAliasNamesOmittedContextConfig("value")
    static class ValueImplicitAliasesWithImpliedAliasNamesOmittedContextConfigClass {}

    // Attribute value intentionally matches attribute name:
    @AnnotationUtilsTests.ImplicitAliasesWithImpliedAliasNamesOmittedContextConfig(location = "location")
    static class LocationsImplicitAliasesWithImpliedAliasNamesOmittedContextConfigClass {}

    // Attribute value intentionally matches attribute name:
    @AnnotationUtilsTests.ImplicitAliasesWithImpliedAliasNamesOmittedContextConfig(xmlFile = "xmlFile")
    static class XmlFilesImplicitAliasesWithImpliedAliasNamesOmittedContextConfigClass {}

    @AnnotationUtilsTests.ContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface ImplicitAliasesWithMissingDefaultValuesContextConfig {
        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String location1();

        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String location2();
    }

    @AnnotationUtilsTests.ImplicitAliasesWithMissingDefaultValuesContextConfig(location1 = "1", location2 = "2")
    static class ImplicitAliasesWithMissingDefaultValuesContextConfigClass {}

    @AnnotationUtilsTests.ContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface ImplicitAliasesWithDifferentDefaultValuesContextConfig {
        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String location1() default "foo";

        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String location2() default "bar";
    }

    @AnnotationUtilsTests.ImplicitAliasesWithDifferentDefaultValuesContextConfig(location1 = "1", location2 = "2")
    static class ImplicitAliasesWithDifferentDefaultValuesContextConfigClass {}

    @AnnotationUtilsTests.ContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface ImplicitAliasesWithDuplicateValuesContextConfig {
        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String location1() default "";

        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String location2() default "";
    }

    @AnnotationUtilsTests.ImplicitAliasesWithDuplicateValuesContextConfig(location1 = "1", location2 = "2")
    static class ImplicitAliasesWithDuplicateValuesContextConfigClass {}

    @AnnotationUtilsTests.ContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface ImplicitAliasesForAliasPairContextConfig {
        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, attribute = "location")
        String xmlFile() default "";

        @AliasFor(annotation = AnnotationUtilsTests.ContextConfig.class, value = "value")
        String groovyScript() default "";
    }

    @AnnotationUtilsTests.ImplicitAliasesForAliasPairContextConfig(xmlFile = "test.xml")
    static class ImplicitAliasesForAliasPairContextConfigClass {}

    @AnnotationUtilsTests.ImplicitAliasesContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface TransitiveImplicitAliasesContextConfig {
        @AliasFor(annotation = AnnotationUtilsTests.ImplicitAliasesContextConfig.class, attribute = "xmlFile")
        String xml() default "";

        @AliasFor(annotation = AnnotationUtilsTests.ImplicitAliasesContextConfig.class, attribute = "groovyScript")
        String groovy() default "";
    }

    @AnnotationUtilsTests.TransitiveImplicitAliasesContextConfig(xml = "test.xml")
    static class TransitiveImplicitAliasesContextConfigClass {}

    @AnnotationUtilsTests.ImplicitAliasesForAliasPairContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface TransitiveImplicitAliasesForAliasPairContextConfig {
        @AliasFor(annotation = AnnotationUtilsTests.ImplicitAliasesForAliasPairContextConfig.class, attribute = "xmlFile")
        String xml() default "";

        @AliasFor(annotation = AnnotationUtilsTests.ImplicitAliasesForAliasPairContextConfig.class, attribute = "groovyScript")
        String groovy() default "";
    }

    @AnnotationUtilsTests.TransitiveImplicitAliasesForAliasPairContextConfig(xml = "test.xml")
    static class TransitiveImplicitAliasesForAliasPairContextConfigClass {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target({  })
    @interface Filter {
        String pattern();
    }

    /**
     * Mock of {@code org.springframework.context.annotation.ComponentScan}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface ComponentScan {
        AnnotationUtilsTests.Filter[] excludeFilters() default {  };
    }

    @AnnotationUtilsTests.ComponentScan(excludeFilters = { @AnnotationUtilsTests.Filter(pattern = "*Foo"), @AnnotationUtilsTests.Filter(pattern = "*Bar") })
    static class ComponentScanClass {}

    /**
     * Mock of {@code org.springframework.context.annotation.ComponentScan}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface ComponentScanSingleFilter {
        AnnotationUtilsTests.Filter value();
    }

    @AnnotationUtilsTests.ComponentScanSingleFilter(@AnnotationUtilsTests.Filter(pattern = "*Foo"))
    static class ComponentScanSingleFilterClass {}

    @Retention(RetentionPolicy.RUNTIME)
    @interface AnnotationWithDefaults {
        String text() default "enigma";

        boolean predicate() default true;

        char[] characters() default { 'a', 'b', 'c' };
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface AnnotationWithoutDefaults {
        String text();
    }

    @AnnotationUtilsTests.ContextConfig(value = "foo", location = "bar")
    interface ContextConfigMismatch {}
}

