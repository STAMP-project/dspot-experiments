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


import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;
import org.springframework.util.MultiValueMap;


/**
 * Unit tests for {@link AnnotatedElementUtils}.
 *
 * @author Sam Brannen
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 4.0.3
 * @see AnnotationUtilsTests
 * @see MultipleComposedAnnotationsOnSingleAnnotatedElementTests
 * @see ComposedRepeatableAnnotationsTests
 */
public class AnnotatedElementUtilsTests {
    private static final String TX_NAME = AnnotatedElementUtilsTests.Transactional.class.getName();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void getMetaAnnotationTypesOnNonAnnotatedClass() {
        Assert.assertTrue(getMetaAnnotationTypes(AnnotatedElementUtilsTests.NonAnnotatedClass.class, AnnotatedElementUtilsTests.TransactionalComponent.class).isEmpty());
        Assert.assertTrue(getMetaAnnotationTypes(AnnotatedElementUtilsTests.NonAnnotatedClass.class, AnnotatedElementUtilsTests.TransactionalComponent.class.getName()).isEmpty());
    }

    @Test
    public void getMetaAnnotationTypesOnClassWithMetaDepth1() {
        Set<String> names = getMetaAnnotationTypes(AnnotatedElementUtilsTests.TransactionalComponentClass.class, AnnotatedElementUtilsTests.TransactionalComponent.class);
        Assert.assertEquals(names(AnnotatedElementUtilsTests.Transactional.class, Component.class, Indexed.class), names);
        names = getMetaAnnotationTypes(AnnotatedElementUtilsTests.TransactionalComponentClass.class, AnnotatedElementUtilsTests.TransactionalComponent.class.getName());
        Assert.assertEquals(names(AnnotatedElementUtilsTests.Transactional.class, Component.class, Indexed.class), names);
    }

    @Test
    public void getMetaAnnotationTypesOnClassWithMetaDepth2() {
        Set<String> names = getMetaAnnotationTypes(AnnotatedElementUtilsTests.ComposedTransactionalComponentClass.class, AnnotatedElementUtilsTests.ComposedTransactionalComponent.class);
        Assert.assertEquals(names(AnnotatedElementUtilsTests.TransactionalComponent.class, AnnotatedElementUtilsTests.Transactional.class, Component.class, Indexed.class), names);
        names = getMetaAnnotationTypes(AnnotatedElementUtilsTests.ComposedTransactionalComponentClass.class, AnnotatedElementUtilsTests.ComposedTransactionalComponent.class.getName());
        Assert.assertEquals(names(AnnotatedElementUtilsTests.TransactionalComponent.class, AnnotatedElementUtilsTests.Transactional.class, Component.class, Indexed.class), names);
    }

    @Test
    public void hasMetaAnnotationTypesOnNonAnnotatedClass() {
        Assert.assertFalse(hasMetaAnnotationTypes(AnnotatedElementUtilsTests.NonAnnotatedClass.class, AnnotatedElementUtilsTests.TX_NAME));
    }

    @Test
    public void hasMetaAnnotationTypesOnClassWithMetaDepth0() {
        Assert.assertFalse(hasMetaAnnotationTypes(AnnotatedElementUtilsTests.TransactionalComponentClass.class, AnnotatedElementUtilsTests.TransactionalComponent.class.getName()));
    }

    @Test
    public void hasMetaAnnotationTypesOnClassWithMetaDepth1() {
        Assert.assertTrue(hasMetaAnnotationTypes(AnnotatedElementUtilsTests.TransactionalComponentClass.class, AnnotatedElementUtilsTests.TX_NAME));
        Assert.assertTrue(hasMetaAnnotationTypes(AnnotatedElementUtilsTests.TransactionalComponentClass.class, Component.class.getName()));
    }

    @Test
    public void hasMetaAnnotationTypesOnClassWithMetaDepth2() {
        Assert.assertTrue(hasMetaAnnotationTypes(AnnotatedElementUtilsTests.ComposedTransactionalComponentClass.class, AnnotatedElementUtilsTests.TX_NAME));
        Assert.assertTrue(hasMetaAnnotationTypes(AnnotatedElementUtilsTests.ComposedTransactionalComponentClass.class, Component.class.getName()));
        Assert.assertFalse(hasMetaAnnotationTypes(AnnotatedElementUtilsTests.ComposedTransactionalComponentClass.class, AnnotatedElementUtilsTests.ComposedTransactionalComponent.class.getName()));
    }

    @Test
    public void isAnnotatedOnNonAnnotatedClass() {
        Assert.assertFalse(isAnnotated(AnnotatedElementUtilsTests.NonAnnotatedClass.class, AnnotatedElementUtilsTests.TX_NAME));
    }

    @Test
    public void isAnnotatedOnClassWithMetaDepth0() {
        Assert.assertTrue(isAnnotated(AnnotatedElementUtilsTests.TransactionalComponentClass.class, AnnotatedElementUtilsTests.TransactionalComponent.class.getName()));
    }

    @Test
    public void isAnnotatedOnSubclassWithMetaDepth0() {
        Assert.assertFalse("isAnnotated() does not search the class hierarchy.", isAnnotated(AnnotatedElementUtilsTests.SubTransactionalComponentClass.class, AnnotatedElementUtilsTests.TransactionalComponent.class.getName()));
    }

    @Test
    public void isAnnotatedOnClassWithMetaDepth1() {
        Assert.assertTrue(isAnnotated(AnnotatedElementUtilsTests.TransactionalComponentClass.class, AnnotatedElementUtilsTests.TX_NAME));
        Assert.assertTrue(isAnnotated(AnnotatedElementUtilsTests.TransactionalComponentClass.class, Component.class.getName()));
    }

    @Test
    public void isAnnotatedOnClassWithMetaDepth2() {
        Assert.assertTrue(isAnnotated(AnnotatedElementUtilsTests.ComposedTransactionalComponentClass.class, AnnotatedElementUtilsTests.TX_NAME));
        Assert.assertTrue(isAnnotated(AnnotatedElementUtilsTests.ComposedTransactionalComponentClass.class, Component.class.getName()));
        Assert.assertTrue(isAnnotated(AnnotatedElementUtilsTests.ComposedTransactionalComponentClass.class, AnnotatedElementUtilsTests.ComposedTransactionalComponent.class.getName()));
    }

    @Test
    public void getAllAnnotationAttributesOnNonAnnotatedClass() {
        Assert.assertNull(getAllAnnotationAttributes(AnnotatedElementUtilsTests.NonAnnotatedClass.class, AnnotatedElementUtilsTests.TX_NAME));
    }

    @Test
    public void getAllAnnotationAttributesOnClassWithLocalAnnotation() {
        MultiValueMap<String, Object> attributes = getAllAnnotationAttributes(AnnotatedElementUtilsTests.TxConfig.class, AnnotatedElementUtilsTests.TX_NAME);
        Assert.assertNotNull("Annotation attributes map for @Transactional on TxConfig", attributes);
        Assert.assertEquals("value for TxConfig.", Arrays.asList("TxConfig"), attributes.get("value"));
    }

    @Test
    public void getAllAnnotationAttributesOnClassWithLocalComposedAnnotationAndInheritedAnnotation() {
        MultiValueMap<String, Object> attributes = getAllAnnotationAttributes(AnnotatedElementUtilsTests.SubClassWithInheritedAnnotation.class, AnnotatedElementUtilsTests.TX_NAME);
        Assert.assertNotNull("Annotation attributes map for @Transactional on SubClassWithInheritedAnnotation", attributes);
        Assert.assertEquals(Arrays.asList("composed2", "transactionManager"), attributes.get("qualifier"));
    }

    @Test
    public void getAllAnnotationAttributesFavorsInheritedAnnotationsOverMoreLocallyDeclaredComposedAnnotations() {
        MultiValueMap<String, Object> attributes = getAllAnnotationAttributes(AnnotatedElementUtilsTests.SubSubClassWithInheritedAnnotation.class, AnnotatedElementUtilsTests.TX_NAME);
        Assert.assertNotNull("Annotation attributes map for @Transactional on SubSubClassWithInheritedAnnotation", attributes);
        Assert.assertEquals(Arrays.asList("transactionManager"), attributes.get("qualifier"));
    }

    @Test
    public void getAllAnnotationAttributesFavorsInheritedComposedAnnotationsOverMoreLocallyDeclaredComposedAnnotations() {
        MultiValueMap<String, Object> attributes = getAllAnnotationAttributes(AnnotatedElementUtilsTests.SubSubClassWithInheritedComposedAnnotation.class, AnnotatedElementUtilsTests.TX_NAME);
        Assert.assertNotNull("Annotation attributes map for @Transactional on SubSubClassWithInheritedComposedAnnotation", attributes);
        Assert.assertEquals(Arrays.asList("composed1"), attributes.get("qualifier"));
    }

    /**
     * If the "value" entry contains both "DerivedTxConfig" AND "TxConfig", then
     * the algorithm is accidentally picking up shadowed annotations of the same
     * type within the class hierarchy. Such undesirable behavior would cause the
     * logic in {@link org.springframework.context.annotation.ProfileCondition}
     * to fail.
     *
     * @see org.springframework.core.env.EnvironmentSystemIntegrationTests#mostSpecificDerivedClassDrivesEnvironment_withDevEnvAndDerivedDevConfigClass
     */
    @Test
    public void getAllAnnotationAttributesOnClassWithLocalAnnotationThatShadowsAnnotationFromSuperclass() {
        MultiValueMap<String, Object> attributes = getAllAnnotationAttributes(AnnotatedElementUtilsTests.DerivedTxConfig.class, AnnotatedElementUtilsTests.TX_NAME);
        Assert.assertNotNull("Annotation attributes map for @Transactional on DerivedTxConfig", attributes);
        Assert.assertEquals("value for DerivedTxConfig.", Arrays.asList("DerivedTxConfig"), attributes.get("value"));
    }

    /**
     * Note: this functionality is required by {@link org.springframework.context.annotation.ProfileCondition}.
     *
     * @see org.springframework.core.env.EnvironmentSystemIntegrationTests
     */
    @Test
    public void getAllAnnotationAttributesOnClassWithMultipleComposedAnnotations() {
        MultiValueMap<String, Object> attributes = getAllAnnotationAttributes(AnnotatedElementUtilsTests.TxFromMultipleComposedAnnotations.class, AnnotatedElementUtilsTests.TX_NAME);
        Assert.assertNotNull("Annotation attributes map for @Transactional on TxFromMultipleComposedAnnotations", attributes);
        Assert.assertEquals("value for TxFromMultipleComposedAnnotations.", Arrays.asList("TxInheritedComposed", "TxComposed"), attributes.get("value"));
    }

    @Test
    public void getMergedAnnotationAttributesOnClassWithLocalAnnotation() {
        Class<?> element = AnnotatedElementUtilsTests.TxConfig.class;
        String name = AnnotatedElementUtilsTests.TX_NAME;
        AnnotationAttributes attributes = getMergedAnnotationAttributes(element, name);
        Assert.assertNotNull("Annotation attributes for @Transactional on TxConfig", attributes);
        Assert.assertEquals("value for TxConfig.", "TxConfig", attributes.getString("value"));
        // Verify contracts between utility methods:
        Assert.assertTrue(isAnnotated(element, name));
    }

    @Test
    public void getMergedAnnotationAttributesOnClassWithLocalAnnotationThatShadowsAnnotationFromSuperclass() {
        Class<?> element = AnnotatedElementUtilsTests.DerivedTxConfig.class;
        String name = AnnotatedElementUtilsTests.TX_NAME;
        AnnotationAttributes attributes = getMergedAnnotationAttributes(element, name);
        Assert.assertNotNull("Annotation attributes for @Transactional on DerivedTxConfig", attributes);
        Assert.assertEquals("value for DerivedTxConfig.", "DerivedTxConfig", attributes.getString("value"));
        // Verify contracts between utility methods:
        Assert.assertTrue(isAnnotated(element, name));
    }

    @Test
    public void getMergedAnnotationAttributesOnMetaCycleAnnotatedClassWithMissingTargetMetaAnnotation() {
        AnnotationAttributes attributes = getMergedAnnotationAttributes(AnnotatedElementUtilsTests.MetaCycleAnnotatedClass.class, AnnotatedElementUtilsTests.TX_NAME);
        Assert.assertNull("Should not find annotation attributes for @Transactional on MetaCycleAnnotatedClass", attributes);
    }

    @Test
    public void getMergedAnnotationAttributesFavorsLocalComposedAnnotationOverInheritedAnnotation() {
        Class<?> element = AnnotatedElementUtilsTests.SubClassWithInheritedAnnotation.class;
        String name = AnnotatedElementUtilsTests.TX_NAME;
        AnnotationAttributes attributes = getMergedAnnotationAttributes(element, name);
        Assert.assertNotNull("AnnotationAttributes for @Transactional on SubClassWithInheritedAnnotation", attributes);
        // Verify contracts between utility methods:
        Assert.assertTrue(isAnnotated(element, name));
        Assert.assertTrue("readOnly flag for SubClassWithInheritedAnnotation.", attributes.getBoolean("readOnly"));
    }

    @Test
    public void getMergedAnnotationAttributesFavorsInheritedAnnotationsOverMoreLocallyDeclaredComposedAnnotations() {
        Class<?> element = AnnotatedElementUtilsTests.SubSubClassWithInheritedAnnotation.class;
        String name = AnnotatedElementUtilsTests.TX_NAME;
        AnnotationAttributes attributes = getMergedAnnotationAttributes(element, name);
        Assert.assertNotNull("AnnotationAttributes for @Transactional on SubSubClassWithInheritedAnnotation", attributes);
        // Verify contracts between utility methods:
        Assert.assertTrue(isAnnotated(element, name));
        Assert.assertFalse("readOnly flag for SubSubClassWithInheritedAnnotation.", attributes.getBoolean("readOnly"));
    }

    @Test
    public void getMergedAnnotationAttributesFavorsInheritedComposedAnnotationsOverMoreLocallyDeclaredComposedAnnotations() {
        Class<?> element = AnnotatedElementUtilsTests.SubSubClassWithInheritedComposedAnnotation.class;
        String name = AnnotatedElementUtilsTests.TX_NAME;
        AnnotationAttributes attributes = getMergedAnnotationAttributes(element, name);
        Assert.assertNotNull("AnnotationAttributes for @Transactional on SubSubClassWithInheritedComposedAnnotation.", attributes);
        // Verify contracts between utility methods:
        Assert.assertTrue(isAnnotated(element, name));
        Assert.assertFalse("readOnly flag for SubSubClassWithInheritedComposedAnnotation.", attributes.getBoolean("readOnly"));
    }

    @Test
    public void getMergedAnnotationAttributesFromInterfaceImplementedBySuperclass() {
        Class<?> element = AnnotatedElementUtilsTests.ConcreteClassWithInheritedAnnotation.class;
        String name = AnnotatedElementUtilsTests.TX_NAME;
        AnnotationAttributes attributes = getMergedAnnotationAttributes(element, name);
        Assert.assertNull("Should not find @Transactional on ConcreteClassWithInheritedAnnotation", attributes);
        // Verify contracts between utility methods:
        Assert.assertFalse(isAnnotated(element, name));
    }

    @Test
    public void getMergedAnnotationAttributesOnInheritedAnnotationInterface() {
        Class<?> element = AnnotatedElementUtilsTests.InheritedAnnotationInterface.class;
        String name = AnnotatedElementUtilsTests.TX_NAME;
        AnnotationAttributes attributes = getMergedAnnotationAttributes(element, name);
        Assert.assertNotNull("Should find @Transactional on InheritedAnnotationInterface", attributes);
        // Verify contracts between utility methods:
        Assert.assertTrue(isAnnotated(element, name));
    }

    @Test
    public void getMergedAnnotationAttributesOnNonInheritedAnnotationInterface() {
        Class<?> element = AnnotatedElementUtilsTests.NonInheritedAnnotationInterface.class;
        String name = Order.class.getName();
        AnnotationAttributes attributes = getMergedAnnotationAttributes(element, name);
        Assert.assertNotNull("Should find @Order on NonInheritedAnnotationInterface", attributes);
        // Verify contracts between utility methods:
        Assert.assertTrue(isAnnotated(element, name));
    }

    @Test
    public void getMergedAnnotationAttributesWithConventionBasedComposedAnnotation() {
        Class<?> element = AnnotatedElementUtilsTests.ConventionBasedComposedContextConfigClass.class;
        String name = AnnotatedElementUtilsTests.ContextConfig.class.getName();
        AnnotationAttributes attributes = getMergedAnnotationAttributes(element, name);
        Assert.assertNotNull(("Should find @ContextConfig on " + (element.getSimpleName())), attributes);
        Assert.assertArrayEquals("locations", AnnotationUtilsTests.asArray("explicitDeclaration"), attributes.getStringArray("locations"));
        Assert.assertArrayEquals("value", AnnotationUtilsTests.asArray("explicitDeclaration"), attributes.getStringArray("value"));
        // Verify contracts between utility methods:
        Assert.assertTrue(isAnnotated(element, name));
    }

    @Test
    public void getMergedAnnotationAttributesWithAliasedComposedAnnotation() {
        Class<?> element = AnnotatedElementUtilsTests.AliasedComposedContextConfigClass.class;
        String name = AnnotatedElementUtilsTests.ContextConfig.class.getName();
        AnnotationAttributes attributes = getMergedAnnotationAttributes(element, name);
        Assert.assertNotNull(("Should find @ContextConfig on " + (element.getSimpleName())), attributes);
        Assert.assertArrayEquals("value", AnnotationUtilsTests.asArray("test.xml"), attributes.getStringArray("value"));
        Assert.assertArrayEquals("locations", AnnotationUtilsTests.asArray("test.xml"), attributes.getStringArray("locations"));
        // Verify contracts between utility methods:
        Assert.assertTrue(isAnnotated(element, name));
    }

    @Test
    public void getMergedAnnotationAttributesWithAliasedValueComposedAnnotation() {
        Class<?> element = AnnotatedElementUtilsTests.AliasedValueComposedContextConfigClass.class;
        String name = AnnotatedElementUtilsTests.ContextConfig.class.getName();
        AnnotationAttributes attributes = getMergedAnnotationAttributes(element, name);
        Assert.assertNotNull(("Should find @ContextConfig on " + (element.getSimpleName())), attributes);
        Assert.assertArrayEquals("locations", AnnotationUtilsTests.asArray("test.xml"), attributes.getStringArray("locations"));
        Assert.assertArrayEquals("value", AnnotationUtilsTests.asArray("test.xml"), attributes.getStringArray("value"));
        // Verify contracts between utility methods:
        Assert.assertTrue(isAnnotated(element, name));
    }

    @Test
    public void getMergedAnnotationAttributesWithImplicitAliasesInMetaAnnotationOnComposedAnnotation() {
        Class<?> element = AnnotatedElementUtilsTests.ComposedImplicitAliasesContextConfigClass.class;
        String name = AnnotatedElementUtilsTests.ImplicitAliasesContextConfig.class.getName();
        AnnotationAttributes attributes = getMergedAnnotationAttributes(element, name);
        String[] expected = AnnotationUtilsTests.asArray("A.xml", "B.xml");
        Assert.assertNotNull(("Should find @ImplicitAliasesContextConfig on " + (element.getSimpleName())), attributes);
        Assert.assertArrayEquals("groovyScripts", expected, attributes.getStringArray("groovyScripts"));
        Assert.assertArrayEquals("xmlFiles", expected, attributes.getStringArray("xmlFiles"));
        Assert.assertArrayEquals("locations", expected, attributes.getStringArray("locations"));
        Assert.assertArrayEquals("value", expected, attributes.getStringArray("value"));
        // Verify contracts between utility methods:
        Assert.assertTrue(isAnnotated(element, name));
    }

    @Test
    public void getMergedAnnotationWithAliasedValueComposedAnnotation() {
        assertGetMergedAnnotation(AnnotatedElementUtilsTests.AliasedValueComposedContextConfigClass.class, "test.xml");
    }

    @Test
    public void getMergedAnnotationWithImplicitAliasesForSameAttributeInComposedAnnotation() {
        assertGetMergedAnnotation(AnnotatedElementUtilsTests.ImplicitAliasesContextConfigClass1.class, "foo.xml");
        assertGetMergedAnnotation(AnnotatedElementUtilsTests.ImplicitAliasesContextConfigClass2.class, "bar.xml");
        assertGetMergedAnnotation(AnnotatedElementUtilsTests.ImplicitAliasesContextConfigClass3.class, "baz.xml");
    }

    @Test
    public void getMergedAnnotationWithTransitiveImplicitAliases() {
        assertGetMergedAnnotation(AnnotatedElementUtilsTests.TransitiveImplicitAliasesContextConfigClass.class, "test.groovy");
    }

    @Test
    public void getMergedAnnotationWithTransitiveImplicitAliasesWithSingleElementOverridingAnArrayViaAliasFor() {
        assertGetMergedAnnotation(AnnotatedElementUtilsTests.SingleLocationTransitiveImplicitAliasesContextConfigClass.class, "test.groovy");
    }

    @Test
    public void getMergedAnnotationWithTransitiveImplicitAliasesWithSkippedLevel() {
        assertGetMergedAnnotation(AnnotatedElementUtilsTests.TransitiveImplicitAliasesWithSkippedLevelContextConfigClass.class, "test.xml");
    }

    @Test
    public void getMergedAnnotationWithTransitiveImplicitAliasesWithSkippedLevelWithSingleElementOverridingAnArrayViaAliasFor() {
        assertGetMergedAnnotation(AnnotatedElementUtilsTests.SingleLocationTransitiveImplicitAliasesWithSkippedLevelContextConfigClass.class, "test.xml");
    }

    @Test
    public void getMergedAnnotationWithImplicitAliasesInMetaAnnotationOnComposedAnnotation() {
        Class<?> element = AnnotatedElementUtilsTests.ComposedImplicitAliasesContextConfigClass.class;
        String name = AnnotatedElementUtilsTests.ImplicitAliasesContextConfig.class.getName();
        AnnotatedElementUtilsTests.ImplicitAliasesContextConfig config = getMergedAnnotation(element, AnnotatedElementUtilsTests.ImplicitAliasesContextConfig.class);
        String[] expected = AnnotationUtilsTests.asArray("A.xml", "B.xml");
        Assert.assertNotNull(("Should find @ImplicitAliasesContextConfig on " + (element.getSimpleName())), config);
        Assert.assertArrayEquals("groovyScripts", expected, config.groovyScripts());
        Assert.assertArrayEquals("xmlFiles", expected, config.xmlFiles());
        Assert.assertArrayEquals("locations", expected, config.locations());
        Assert.assertArrayEquals("value", expected, config.value());
        // Verify contracts between utility methods:
        Assert.assertTrue(isAnnotated(element, name));
    }

    @Test
    public void getMergedAnnotationAttributesWithInvalidConventionBasedComposedAnnotation() {
        Class<?> element = AnnotatedElementUtilsTests.InvalidConventionBasedComposedContextConfigClass.class;
        exception.expect(AnnotationConfigurationException.class);
        exception.expectMessage(either(containsString("attribute 'value' and its alias 'locations'")).or(containsString("attribute 'locations' and its alias 'value'")));
        exception.expectMessage(either(containsString("values of [{duplicateDeclaration}] and [{requiredLocationsDeclaration}]")).or(containsString("values of [{requiredLocationsDeclaration}] and [{duplicateDeclaration}]")));
        exception.expectMessage(containsString("but only one is permitted"));
        getMergedAnnotationAttributes(element, AnnotatedElementUtilsTests.ContextConfig.class);
    }

    @Test
    public void getMergedAnnotationAttributesWithShadowedAliasComposedAnnotation() {
        Class<?> element = AnnotatedElementUtilsTests.ShadowedAliasComposedContextConfigClass.class;
        AnnotationAttributes attributes = getMergedAnnotationAttributes(element, AnnotatedElementUtilsTests.ContextConfig.class);
        String[] expected = AnnotationUtilsTests.asArray("test.xml");
        Assert.assertNotNull(("Should find @ContextConfig on " + (element.getSimpleName())), attributes);
        Assert.assertArrayEquals("locations", expected, attributes.getStringArray("locations"));
        Assert.assertArrayEquals("value", expected, attributes.getStringArray("value"));
    }

    @Test
    public void findMergedAnnotationAttributesOnInheritedAnnotationInterface() {
        AnnotationAttributes attributes = findMergedAnnotationAttributes(AnnotatedElementUtilsTests.InheritedAnnotationInterface.class, AnnotatedElementUtilsTests.Transactional.class);
        Assert.assertNotNull("Should find @Transactional on InheritedAnnotationInterface", attributes);
    }

    @Test
    public void findMergedAnnotationAttributesOnSubInheritedAnnotationInterface() {
        AnnotationAttributes attributes = findMergedAnnotationAttributes(AnnotatedElementUtilsTests.SubInheritedAnnotationInterface.class, AnnotatedElementUtilsTests.Transactional.class);
        Assert.assertNotNull("Should find @Transactional on SubInheritedAnnotationInterface", attributes);
    }

    @Test
    public void findMergedAnnotationAttributesOnSubSubInheritedAnnotationInterface() {
        AnnotationAttributes attributes = findMergedAnnotationAttributes(AnnotatedElementUtilsTests.SubSubInheritedAnnotationInterface.class, AnnotatedElementUtilsTests.Transactional.class);
        Assert.assertNotNull("Should find @Transactional on SubSubInheritedAnnotationInterface", attributes);
    }

    @Test
    public void findMergedAnnotationAttributesOnNonInheritedAnnotationInterface() {
        AnnotationAttributes attributes = findMergedAnnotationAttributes(AnnotatedElementUtilsTests.NonInheritedAnnotationInterface.class, Order.class);
        Assert.assertNotNull("Should find @Order on NonInheritedAnnotationInterface", attributes);
    }

    @Test
    public void findMergedAnnotationAttributesOnSubNonInheritedAnnotationInterface() {
        AnnotationAttributes attributes = findMergedAnnotationAttributes(AnnotatedElementUtilsTests.SubNonInheritedAnnotationInterface.class, Order.class);
        Assert.assertNotNull("Should find @Order on SubNonInheritedAnnotationInterface", attributes);
    }

    @Test
    public void findMergedAnnotationAttributesOnSubSubNonInheritedAnnotationInterface() {
        AnnotationAttributes attributes = findMergedAnnotationAttributes(AnnotatedElementUtilsTests.SubSubNonInheritedAnnotationInterface.class, Order.class);
        Assert.assertNotNull("Should find @Order on SubSubNonInheritedAnnotationInterface", attributes);
    }

    @Test
    public void findMergedAnnotationAttributesInheritedFromInterfaceMethod() throws NoSuchMethodException {
        Method method = AnnotatedElementUtilsTests.ConcreteClassWithInheritedAnnotation.class.getMethod("handleFromInterface");
        AnnotationAttributes attributes = findMergedAnnotationAttributes(method, Order.class);
        Assert.assertNotNull("Should find @Order on ConcreteClassWithInheritedAnnotation.handleFromInterface() method", attributes);
    }

    @Test
    public void findMergedAnnotationAttributesInheritedFromAbstractMethod() throws NoSuchMethodException {
        Method method = AnnotatedElementUtilsTests.ConcreteClassWithInheritedAnnotation.class.getMethod("handle");
        AnnotationAttributes attributes = findMergedAnnotationAttributes(method, AnnotatedElementUtilsTests.Transactional.class);
        Assert.assertNotNull("Should find @Transactional on ConcreteClassWithInheritedAnnotation.handle() method", attributes);
    }

    @Test
    public void findMergedAnnotationAttributesInheritedFromBridgedMethod() throws NoSuchMethodException {
        Method method = AnnotatedElementUtilsTests.ConcreteClassWithInheritedAnnotation.class.getMethod("handleParameterized", String.class);
        AnnotationAttributes attributes = findMergedAnnotationAttributes(method, AnnotatedElementUtilsTests.Transactional.class);
        Assert.assertNotNull("Should find @Transactional on bridged ConcreteClassWithInheritedAnnotation.handleParameterized()", attributes);
    }

    /**
     * Bridge/bridged method setup code copied from
     * {@link org.springframework.core.BridgeMethodResolverTests#testWithGenericParameter()}.
     *
     * @since 4.2
     */
    @Test
    public void findMergedAnnotationAttributesFromBridgeMethod() {
        Method[] methods = AnnotatedElementUtilsTests.StringGenericParameter.class.getMethods();
        Method bridgeMethod = null;
        Method bridgedMethod = null;
        for (Method method : methods) {
            if (("getFor".equals(method.getName())) && (!(method.getParameterTypes()[0].equals(Integer.class)))) {
                if (method.getReturnType().equals(Object.class)) {
                    bridgeMethod = method;
                } else {
                    bridgedMethod = method;
                }
            }
        }
        Assert.assertTrue(((bridgeMethod != null) && (bridgeMethod.isBridge())));
        Assert.assertTrue(((bridgedMethod != null) && (!(bridgedMethod.isBridge()))));
        AnnotationAttributes attributes = findMergedAnnotationAttributes(bridgeMethod, Order.class);
        Assert.assertNotNull("Should find @Order on StringGenericParameter.getFor() bridge method", attributes);
    }

    @Test
    public void findMergedAnnotationAttributesOnClassWithMetaAndLocalTxConfig() {
        AnnotationAttributes attributes = findMergedAnnotationAttributes(AnnotatedElementUtilsTests.MetaAndLocalTxConfigClass.class, AnnotatedElementUtilsTests.Transactional.class);
        Assert.assertNotNull("Should find @Transactional on MetaAndLocalTxConfigClass", attributes);
        Assert.assertEquals("TX qualifier for MetaAndLocalTxConfigClass.", "localTxMgr", attributes.getString("qualifier"));
    }

    @Test
    public void findAndSynthesizeAnnotationAttributesOnClassWithAttributeAliasesInTargetAnnotation() {
        String qualifier = "aliasForQualifier";
        // 1) Find and merge AnnotationAttributes from the annotation hierarchy
        AnnotationAttributes attributes = findMergedAnnotationAttributes(AnnotatedElementUtilsTests.AliasedTransactionalComponentClass.class, AnnotatedElementUtilsTests.AliasedTransactional.class);
        Assert.assertNotNull("@AliasedTransactional on AliasedTransactionalComponentClass.", attributes);
        // 2) Synthesize the AnnotationAttributes back into the target annotation
        AnnotatedElementUtilsTests.AliasedTransactional annotation = AnnotationUtils.synthesizeAnnotation(attributes, AnnotatedElementUtilsTests.AliasedTransactional.class, AnnotatedElementUtilsTests.AliasedTransactionalComponentClass.class);
        Assert.assertNotNull(annotation);
        // 3) Verify that the AnnotationAttributes and synthesized annotation are equivalent
        Assert.assertEquals("TX value via attributes.", qualifier, attributes.getString("value"));
        Assert.assertEquals("TX value via synthesized annotation.", qualifier, annotation.value());
        Assert.assertEquals("TX qualifier via attributes.", qualifier, attributes.getString("qualifier"));
        Assert.assertEquals("TX qualifier via synthesized annotation.", qualifier, annotation.qualifier());
    }

    @Test
    public void findMergedAnnotationAttributesOnClassWithAttributeAliasInComposedAnnotationAndNestedAnnotationsInTargetAnnotation() {
        AnnotationAttributes attributes = assertComponentScanAttributes(AnnotatedElementUtilsTests.TestComponentScanClass.class, "com.example.app.test");
        AnnotatedElementUtilsTests.Filter[] excludeFilters = attributes.getAnnotationArray("excludeFilters", AnnotatedElementUtilsTests.Filter.class);
        Assert.assertNotNull(excludeFilters);
        List<String> patterns = Arrays.stream(excludeFilters).map(AnnotatedElementUtilsTests.Filter::pattern).collect(Collectors.toList());
        Assert.assertEquals(Arrays.asList("*Test", "*Tests"), patterns);
    }

    /**
     * This test ensures that {@link AnnotationUtils#postProcessAnnotationAttributes}
     * uses {@code ObjectUtils.nullSafeEquals()} to check for equality between annotation
     * attributes since attributes may be arrays.
     */
    @Test
    public void findMergedAnnotationAttributesOnClassWithBothAttributesOfAnAliasPairDeclared() {
        assertComponentScanAttributes(AnnotatedElementUtilsTests.ComponentScanWithBasePackagesAndValueAliasClass.class, "com.example.app.test");
    }

    @Test
    public void findMergedAnnotationAttributesWithSingleElementOverridingAnArrayViaConvention() {
        assertComponentScanAttributes(AnnotatedElementUtilsTests.ConventionBasedSinglePackageComponentScanClass.class, "com.example.app.test");
    }

    @Test
    public void findMergedAnnotationAttributesWithSingleElementOverridingAnArrayViaAliasFor() {
        assertComponentScanAttributes(AnnotatedElementUtilsTests.AliasForBasedSinglePackageComponentScanClass.class, "com.example.app.test");
    }

    @Test
    public void findMergedAnnotationWithAttributeAliasesInTargetAnnotation() {
        Class<?> element = AnnotatedElementUtilsTests.AliasedTransactionalComponentClass.class;
        AnnotatedElementUtilsTests.AliasedTransactional annotation = findMergedAnnotation(element, AnnotatedElementUtilsTests.AliasedTransactional.class);
        Assert.assertNotNull(("@AliasedTransactional on " + element), annotation);
        Assert.assertEquals("TX value via synthesized annotation.", "aliasForQualifier", annotation.value());
        Assert.assertEquals("TX qualifier via synthesized annotation.", "aliasForQualifier", annotation.qualifier());
    }

    @Test
    public void findMergedAnnotationForMultipleMetaAnnotationsWithClashingAttributeNames() {
        String[] xmlLocations = AnnotationUtilsTests.asArray("test.xml");
        String[] propFiles = AnnotationUtilsTests.asArray("test.properties");
        Class<?> element = AnnotatedElementUtilsTests.AliasedComposedContextConfigAndTestPropSourceClass.class;
        AnnotatedElementUtilsTests.ContextConfig contextConfig = findMergedAnnotation(element, AnnotatedElementUtilsTests.ContextConfig.class);
        Assert.assertNotNull(("@ContextConfig on " + element), contextConfig);
        Assert.assertArrayEquals("locations", xmlLocations, contextConfig.locations());
        Assert.assertArrayEquals("value", xmlLocations, contextConfig.value());
        // Synthesized annotation
        AnnotatedElementUtilsTests.TestPropSource testPropSource = AnnotationUtils.findAnnotation(element, AnnotatedElementUtilsTests.TestPropSource.class);
        Assert.assertArrayEquals("locations", propFiles, testPropSource.locations());
        Assert.assertArrayEquals("value", propFiles, testPropSource.value());
        // Merged annotation
        testPropSource = findMergedAnnotation(element, AnnotatedElementUtilsTests.TestPropSource.class);
        Assert.assertNotNull(("@TestPropSource on " + element), testPropSource);
        Assert.assertArrayEquals("locations", propFiles, testPropSource.locations());
        Assert.assertArrayEquals("value", propFiles, testPropSource.value());
    }

    @Test
    public void findMergedAnnotationWithLocalAliasesThatConflictWithAttributesInMetaAnnotationByConvention() {
        final String[] EMPTY = new String[0];
        Class<?> element = AnnotatedElementUtilsTests.SpringAppConfigClass.class;
        AnnotatedElementUtilsTests.ContextConfig contextConfig = findMergedAnnotation(element, AnnotatedElementUtilsTests.ContextConfig.class);
        Assert.assertNotNull(("Should find @ContextConfig on " + element), contextConfig);
        Assert.assertArrayEquals(("locations for " + element), EMPTY, contextConfig.locations());
        // 'value' in @SpringAppConfig should not override 'value' in @ContextConfig
        Assert.assertArrayEquals(("value for " + element), EMPTY, contextConfig.value());
        Assert.assertArrayEquals(("classes for " + element), new Class<?>[]{ Number.class }, contextConfig.classes());
    }

    @Test
    public void findMergedAnnotationWithSingleElementOverridingAnArrayViaConvention() throws Exception {
        assertWebMapping(AnnotationUtilsTests.WebController.class.getMethod("postMappedWithPathAttribute"));
    }

    @Test
    public void findMergedAnnotationWithSingleElementOverridingAnArrayViaAliasFor() throws Exception {
        assertWebMapping(AnnotationUtilsTests.WebController.class.getMethod("getMappedWithValueAttribute"));
        assertWebMapping(AnnotationUtilsTests.WebController.class.getMethod("getMappedWithPathAttribute"));
    }

    @Test
    public void javaLangAnnotationTypeViaFindMergedAnnotation() throws Exception {
        Constructor<?> deprecatedCtor = Date.class.getConstructor(String.class);
        Assert.assertEquals(deprecatedCtor.getAnnotation(Deprecated.class), findMergedAnnotation(deprecatedCtor, Deprecated.class));
        Assert.assertEquals(Date.class.getAnnotation(Deprecated.class), findMergedAnnotation(Date.class, Deprecated.class));
    }

    @Test
    public void javaxAnnotationTypeViaFindMergedAnnotation() throws Exception {
        Assert.assertEquals(AnnotatedElementUtilsTests.ResourceHolder.class.getAnnotation(Resource.class), findMergedAnnotation(AnnotatedElementUtilsTests.ResourceHolder.class, Resource.class));
        Assert.assertEquals(AnnotatedElementUtilsTests.SpringAppConfigClass.class.getAnnotation(Resource.class), findMergedAnnotation(AnnotatedElementUtilsTests.SpringAppConfigClass.class, Resource.class));
    }

    @Test
    public void getAllMergedAnnotationsOnClassWithInterface() throws Exception {
        Method m = AnnotatedElementUtilsTests.TransactionalServiceImpl.class.getMethod("doIt");
        Set<AnnotatedElementUtilsTests.Transactional> allMergedAnnotations = getAllMergedAnnotations(m, AnnotatedElementUtilsTests.Transactional.class);
        Assert.assertTrue(allMergedAnnotations.isEmpty());
    }

    @Test
    public void findAllMergedAnnotationsOnClassWithInterface() throws Exception {
        Method m = AnnotatedElementUtilsTests.TransactionalServiceImpl.class.getMethod("doIt");
        Set<AnnotatedElementUtilsTests.Transactional> allMergedAnnotations = findAllMergedAnnotations(m, AnnotatedElementUtilsTests.Transactional.class);
        Assert.assertEquals(1, allMergedAnnotations.size());
    }

    // SPR-16060
    @Test
    public void findMethodAnnotationFromGenericInterface() throws Exception {
        Method method = AnnotationUtilsTests.ImplementsInterfaceWithGenericAnnotatedMethod.class.getMethod("foo", String.class);
        Order order = findMergedAnnotation(method, Order.class);
        Assert.assertNotNull(order);
    }

    // SPR-17146
    @Test
    public void findMethodAnnotationFromGenericSuperclass() throws Exception {
        Method method = AnnotationUtilsTests.ExtendsBaseClassWithGenericAnnotatedMethod.class.getMethod("foo", String.class);
        Order order = findMergedAnnotation(method, Order.class);
        Assert.assertNotNull(order);
    }

    // -------------------------------------------------------------------------
    @AnnotatedElementUtilsTests.MetaCycle3
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface MetaCycle1 {}

    @AnnotatedElementUtilsTests.MetaCycle1
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    @interface MetaCycle2 {}

    @AnnotatedElementUtilsTests.MetaCycle2
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface MetaCycle3 {}

    @AnnotatedElementUtilsTests.MetaCycle3
    static class MetaCycleAnnotatedClass {}

    // -------------------------------------------------------------------------
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Inherited
    @interface Transactional {
        String value() default "";

        String qualifier() default "transactionManager";

        boolean readOnly() default false;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Inherited
    @interface AliasedTransactional {
        @AliasFor(attribute = "qualifier")
        String value() default "";

        @AliasFor(attribute = "value")
        String qualifier() default "";
    }

    @AnnotatedElementUtilsTests.Transactional(qualifier = "composed1")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    @interface InheritedComposed {}

    @AnnotatedElementUtilsTests.Transactional(qualifier = "composed2", readOnly = true)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Composed {}

    @AnnotatedElementUtilsTests.Transactional
    @Retention(RetentionPolicy.RUNTIME)
    @interface TxComposedWithOverride {
        String qualifier() default "txMgr";
    }

    @AnnotatedElementUtilsTests.Transactional("TxInheritedComposed")
    @Retention(RetentionPolicy.RUNTIME)
    @interface TxInheritedComposed {}

    @AnnotatedElementUtilsTests.Transactional("TxComposed")
    @Retention(RetentionPolicy.RUNTIME)
    @interface TxComposed {}

    @AnnotatedElementUtilsTests.Transactional
    @Component
    @Retention(RetentionPolicy.RUNTIME)
    @interface TransactionalComponent {}

    @AnnotatedElementUtilsTests.TransactionalComponent
    @Retention(RetentionPolicy.RUNTIME)
    @interface ComposedTransactionalComponent {}

    @AnnotatedElementUtilsTests.AliasedTransactional("aliasForQualifier")
    @Component
    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasedTransactionalComponent {}

    // Override default "txMgr" from @TxComposedWithOverride with "localTxMgr"
    @AnnotatedElementUtilsTests.TxComposedWithOverride
    @AnnotatedElementUtilsTests.Transactional(qualifier = "localTxMgr")
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface MetaAndLocalTxConfig {}

    /**
     * Mock of {@code org.springframework.test.context.TestPropertySource}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestPropSource {
        @AliasFor("locations")
        String[] value() default {  };

        @AliasFor("value")
        String[] locations() default {  };
    }

    /**
     * Mock of {@code org.springframework.test.context.ContextConfiguration}.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface ContextConfig {
        @AliasFor(attribute = "locations")
        String[] value() default {  };

        @AliasFor(attribute = "value")
        String[] locations() default {  };

        Class<?>[] classes() default {  };
    }

    @AnnotatedElementUtilsTests.ContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface ConventionBasedComposedContextConfig {
        String[] locations() default {  };
    }

    @AnnotatedElementUtilsTests.ContextConfig("duplicateDeclaration")
    @Retention(RetentionPolicy.RUNTIME)
    @interface InvalidConventionBasedComposedContextConfig {
        String[] locations();
    }

    /**
     * This hybrid approach for annotation attribute overrides with transitive implicit
     * aliases is unsupported. See SPR-13554 for details.
     */
    @AnnotatedElementUtilsTests.ContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface HalfConventionBasedAndHalfAliasedComposedContextConfig {
        String[] locations() default {  };

        @AliasFor(annotation = AnnotatedElementUtilsTests.ContextConfig.class, attribute = "locations")
        String[] xmlConfigFiles() default {  };
    }

    @AnnotatedElementUtilsTests.ContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasedComposedContextConfig {
        @AliasFor(annotation = AnnotatedElementUtilsTests.ContextConfig.class, attribute = "locations")
        String[] xmlConfigFiles();
    }

    @AnnotatedElementUtilsTests.ContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasedValueComposedContextConfig {
        @AliasFor(annotation = AnnotatedElementUtilsTests.ContextConfig.class, attribute = "value")
        String[] locations();
    }

    @AnnotatedElementUtilsTests.ContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface ImplicitAliasesContextConfig {
        @AliasFor(annotation = AnnotatedElementUtilsTests.ContextConfig.class, attribute = "locations")
        String[] groovyScripts() default {  };

        @AliasFor(annotation = AnnotatedElementUtilsTests.ContextConfig.class, attribute = "locations")
        String[] xmlFiles() default {  };

        // intentionally omitted: attribute = "locations"
        @AliasFor(annotation = AnnotatedElementUtilsTests.ContextConfig.class)
        String[] locations() default {  };

        // intentionally omitted: attribute = "locations" (SPR-14069)
        @AliasFor(annotation = AnnotatedElementUtilsTests.ContextConfig.class)
        String[] value() default {  };
    }

    @AnnotatedElementUtilsTests.ImplicitAliasesContextConfig(xmlFiles = { "A.xml", "B.xml" })
    @Retention(RetentionPolicy.RUNTIME)
    @interface ComposedImplicitAliasesContextConfig {}

    @AnnotatedElementUtilsTests.ImplicitAliasesContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface TransitiveImplicitAliasesContextConfig {
        @AliasFor(annotation = AnnotatedElementUtilsTests.ImplicitAliasesContextConfig.class, attribute = "xmlFiles")
        String[] xml() default {  };

        @AliasFor(annotation = AnnotatedElementUtilsTests.ImplicitAliasesContextConfig.class, attribute = "groovyScripts")
        String[] groovy() default {  };
    }

    @AnnotatedElementUtilsTests.ImplicitAliasesContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface SingleLocationTransitiveImplicitAliasesContextConfig {
        @AliasFor(annotation = AnnotatedElementUtilsTests.ImplicitAliasesContextConfig.class, attribute = "xmlFiles")
        String xml() default "";

        @AliasFor(annotation = AnnotatedElementUtilsTests.ImplicitAliasesContextConfig.class, attribute = "groovyScripts")
        String groovy() default "";
    }

    @AnnotatedElementUtilsTests.ImplicitAliasesContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface TransitiveImplicitAliasesWithSkippedLevelContextConfig {
        @AliasFor(annotation = AnnotatedElementUtilsTests.ContextConfig.class, attribute = "locations")
        String[] xml() default {  };

        @AliasFor(annotation = AnnotatedElementUtilsTests.ImplicitAliasesContextConfig.class, attribute = "groovyScripts")
        String[] groovy() default {  };
    }

    @AnnotatedElementUtilsTests.ImplicitAliasesContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface SingleLocationTransitiveImplicitAliasesWithSkippedLevelContextConfig {
        @AliasFor(annotation = AnnotatedElementUtilsTests.ContextConfig.class, attribute = "locations")
        String xml() default "";

        @AliasFor(annotation = AnnotatedElementUtilsTests.ImplicitAliasesContextConfig.class, attribute = "groovyScripts")
        String groovy() default "";
    }

    /**
     * Although the configuration declares an explicit value for 'value' and
     * requires a value for the aliased 'locations', this does not result in
     * an error since 'locations' effectively <em>shadows</em> the 'value'
     * attribute (which cannot be set via the composed annotation anyway).
     *
     * If 'value' were not shadowed, such a declaration would not make sense.
     */
    @AnnotatedElementUtilsTests.ContextConfig("duplicateDeclaration")
    @Retention(RetentionPolicy.RUNTIME)
    @interface ShadowedAliasComposedContextConfig {
        @AliasFor(annotation = AnnotatedElementUtilsTests.ContextConfig.class, attribute = "locations")
        String[] xmlConfigFiles();
    }

    @AnnotatedElementUtilsTests.ContextConfig(locations = "shadowed.xml")
    @AnnotatedElementUtilsTests.TestPropSource(locations = "test.properties")
    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasedComposedContextConfigAndTestPropSource {
        @AliasFor(annotation = AnnotatedElementUtilsTests.ContextConfig.class, attribute = "locations")
        String[] xmlConfigFiles() default "default.xml";
    }

    /**
     * Mock of {@code org.springframework.boot.test.SpringApplicationConfiguration}.
     */
    @AnnotatedElementUtilsTests.ContextConfig
    @Retention(RetentionPolicy.RUNTIME)
    @interface SpringAppConfig {
        @AliasFor(annotation = AnnotatedElementUtilsTests.ContextConfig.class, attribute = "locations")
        String[] locations() default {  };

        @AliasFor("value")
        Class<?>[] classes() default {  };

        @AliasFor("classes")
        Class<?>[] value() default {  };
    }

    /**
     * Mock of {@code org.springframework.context.annotation.ComponentScan}
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface ComponentScan {
        @AliasFor("basePackages")
        String[] value() default {  };

        @AliasFor("value")
        String[] basePackages() default {  };

        AnnotatedElementUtilsTests.Filter[] excludeFilters() default {  };
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({  })
    @interface Filter {
        String pattern();
    }

    @AnnotatedElementUtilsTests.ComponentScan(excludeFilters = { @AnnotatedElementUtilsTests.Filter(pattern = "*Test"), @AnnotatedElementUtilsTests.Filter(pattern = "*Tests") })
    @Retention(RetentionPolicy.RUNTIME)
    @interface TestComponentScan {
        @AliasFor(attribute = "basePackages", annotation = AnnotatedElementUtilsTests.ComponentScan.class)
        String[] packages();
    }

    @AnnotatedElementUtilsTests.ComponentScan
    @Retention(RetentionPolicy.RUNTIME)
    @interface ConventionBasedSinglePackageComponentScan {
        String basePackages();
    }

    @AnnotatedElementUtilsTests.ComponentScan
    @Retention(RetentionPolicy.RUNTIME)
    @interface AliasForBasedSinglePackageComponentScan {
        @AliasFor(attribute = "basePackages", annotation = AnnotatedElementUtilsTests.ComponentScan.class)
        String pkg();
    }

    // -------------------------------------------------------------------------
    static class NonAnnotatedClass {}

    @AnnotatedElementUtilsTests.TransactionalComponent
    static class TransactionalComponentClass {}

    static class SubTransactionalComponentClass extends AnnotatedElementUtilsTests.TransactionalComponentClass {}

    @AnnotatedElementUtilsTests.ComposedTransactionalComponent
    static class ComposedTransactionalComponentClass {}

    @AnnotatedElementUtilsTests.AliasedTransactionalComponent
    static class AliasedTransactionalComponentClass {}

    @AnnotatedElementUtilsTests.Transactional
    static class ClassWithInheritedAnnotation {}

    @AnnotatedElementUtilsTests.Composed
    static class SubClassWithInheritedAnnotation extends AnnotatedElementUtilsTests.ClassWithInheritedAnnotation {}

    static class SubSubClassWithInheritedAnnotation extends AnnotatedElementUtilsTests.SubClassWithInheritedAnnotation {}

    @AnnotatedElementUtilsTests.InheritedComposed
    static class ClassWithInheritedComposedAnnotation {}

    @AnnotatedElementUtilsTests.Composed
    static class SubClassWithInheritedComposedAnnotation extends AnnotatedElementUtilsTests.ClassWithInheritedComposedAnnotation {}

    static class SubSubClassWithInheritedComposedAnnotation extends AnnotatedElementUtilsTests.SubClassWithInheritedComposedAnnotation {}

    @AnnotatedElementUtilsTests.MetaAndLocalTxConfig
    static class MetaAndLocalTxConfigClass {}

    @AnnotatedElementUtilsTests.Transactional("TxConfig")
    static class TxConfig {}

    @AnnotatedElementUtilsTests.Transactional("DerivedTxConfig")
    static class DerivedTxConfig extends AnnotatedElementUtilsTests.TxConfig {}

    @AnnotatedElementUtilsTests.TxInheritedComposed
    @AnnotatedElementUtilsTests.TxComposed
    static class TxFromMultipleComposedAnnotations {}

    @AnnotatedElementUtilsTests.Transactional
    static interface InterfaceWithInheritedAnnotation {
        @Order
        void handleFromInterface();
    }

    abstract static class AbstractClassWithInheritedAnnotation<T> implements AnnotatedElementUtilsTests.InterfaceWithInheritedAnnotation {
        @AnnotatedElementUtilsTests.Transactional
        public abstract void handle();

        @AnnotatedElementUtilsTests.Transactional
        public void handleParameterized(T t) {
        }
    }

    static class ConcreteClassWithInheritedAnnotation extends AnnotatedElementUtilsTests.AbstractClassWithInheritedAnnotation<String> {
        @Override
        public void handle() {
        }

        @Override
        public void handleParameterized(String s) {
        }

        @Override
        public void handleFromInterface() {
        }
    }

    public interface GenericParameter<T> {
        T getFor(Class<T> cls);
    }

    @SuppressWarnings("unused")
    private static class StringGenericParameter implements AnnotatedElementUtilsTests.GenericParameter<String> {
        @Order
        @Override
        public String getFor(Class<String> cls) {
            return "foo";
        }

        public String getFor(Integer integer) {
            return "foo";
        }
    }

    @AnnotatedElementUtilsTests.Transactional
    public interface InheritedAnnotationInterface {}

    public interface SubInheritedAnnotationInterface extends AnnotatedElementUtilsTests.InheritedAnnotationInterface {}

    public interface SubSubInheritedAnnotationInterface extends AnnotatedElementUtilsTests.SubInheritedAnnotationInterface {}

    @Order
    public interface NonInheritedAnnotationInterface {}

    public interface SubNonInheritedAnnotationInterface extends AnnotatedElementUtilsTests.NonInheritedAnnotationInterface {}

    public interface SubSubNonInheritedAnnotationInterface extends AnnotatedElementUtilsTests.SubNonInheritedAnnotationInterface {}

    @AnnotatedElementUtilsTests.ConventionBasedComposedContextConfig(locations = "explicitDeclaration")
    static class ConventionBasedComposedContextConfigClass {}

    @AnnotatedElementUtilsTests.InvalidConventionBasedComposedContextConfig(locations = "requiredLocationsDeclaration")
    static class InvalidConventionBasedComposedContextConfigClass {}

    @AnnotatedElementUtilsTests.HalfConventionBasedAndHalfAliasedComposedContextConfig(xmlConfigFiles = "explicitDeclaration")
    static class HalfConventionBasedAndHalfAliasedComposedContextConfigClassV1 {}

    @AnnotatedElementUtilsTests.HalfConventionBasedAndHalfAliasedComposedContextConfig(locations = "explicitDeclaration")
    static class HalfConventionBasedAndHalfAliasedComposedContextConfigClassV2 {}

    @AnnotatedElementUtilsTests.AliasedComposedContextConfig(xmlConfigFiles = "test.xml")
    static class AliasedComposedContextConfigClass {}

    @AnnotatedElementUtilsTests.AliasedValueComposedContextConfig(locations = "test.xml")
    static class AliasedValueComposedContextConfigClass {}

    @AnnotatedElementUtilsTests.ImplicitAliasesContextConfig("foo.xml")
    static class ImplicitAliasesContextConfigClass1 {}

    @AnnotatedElementUtilsTests.ImplicitAliasesContextConfig(locations = "bar.xml")
    static class ImplicitAliasesContextConfigClass2 {}

    @AnnotatedElementUtilsTests.ImplicitAliasesContextConfig(xmlFiles = "baz.xml")
    static class ImplicitAliasesContextConfigClass3 {}

    @AnnotatedElementUtilsTests.TransitiveImplicitAliasesContextConfig(groovy = "test.groovy")
    static class TransitiveImplicitAliasesContextConfigClass {}

    @AnnotatedElementUtilsTests.SingleLocationTransitiveImplicitAliasesContextConfig(groovy = "test.groovy")
    static class SingleLocationTransitiveImplicitAliasesContextConfigClass {}

    @AnnotatedElementUtilsTests.TransitiveImplicitAliasesWithSkippedLevelContextConfig(xml = "test.xml")
    static class TransitiveImplicitAliasesWithSkippedLevelContextConfigClass {}

    @AnnotatedElementUtilsTests.SingleLocationTransitiveImplicitAliasesWithSkippedLevelContextConfig(xml = "test.xml")
    static class SingleLocationTransitiveImplicitAliasesWithSkippedLevelContextConfigClass {}

    @AnnotatedElementUtilsTests.ComposedImplicitAliasesContextConfig
    static class ComposedImplicitAliasesContextConfigClass {}

    @AnnotatedElementUtilsTests.ShadowedAliasComposedContextConfig(xmlConfigFiles = "test.xml")
    static class ShadowedAliasComposedContextConfigClass {}

    @AnnotatedElementUtilsTests.AliasedComposedContextConfigAndTestPropSource(xmlConfigFiles = "test.xml")
    static class AliasedComposedContextConfigAndTestPropSourceClass {}

    @AnnotatedElementUtilsTests.ComponentScan(value = "com.example.app.test", basePackages = "com.example.app.test")
    static class ComponentScanWithBasePackagesAndValueAliasClass {}

    @AnnotatedElementUtilsTests.TestComponentScan(packages = "com.example.app.test")
    static class TestComponentScanClass {}

    @AnnotatedElementUtilsTests.ConventionBasedSinglePackageComponentScan(basePackages = "com.example.app.test")
    static class ConventionBasedSinglePackageComponentScanClass {}

    @AnnotatedElementUtilsTests.AliasForBasedSinglePackageComponentScan(pkg = "com.example.app.test")
    static class AliasForBasedSinglePackageComponentScanClass {}

    @AnnotatedElementUtilsTests.SpringAppConfig(Number.class)
    static class SpringAppConfigClass {}

    @Resource(name = "x")
    static class ResourceHolder {}

    interface TransactionalService {
        @AnnotatedElementUtilsTests.Transactional
        void doIt();
    }

    class TransactionalServiceImpl implements AnnotatedElementUtilsTests.TransactionalService {
        @Override
        public void doIt() {
        }
    }
}

