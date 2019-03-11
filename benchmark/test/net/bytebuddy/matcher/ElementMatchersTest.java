package net.bytebuddy.matcher;


import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.utility.JavaModule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;

import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;
import static net.bytebuddy.description.type.TypeDefinition.Sort.VARIABLE;
import static net.bytebuddy.description.type.TypeDefinition.Sort.describe;


public class ElementMatchersTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    private static final String SINGLE_DEFAULT_METHOD = "net.bytebuddy.test.precompiled.SingleDefaultMethodInterface";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Test
    @SuppressWarnings({ "unchecked", "row" })
    public void testFailSafe() throws Exception {
        ElementMatcher<Object> exceptional = Mockito.mock(ElementMatcher.class);
        ElementMatcher<Object> nonExceptional = Mockito.mock(ElementMatcher.class);
        Mockito.when(exceptional.matches(ArgumentMatchers.any())).thenThrow(RuntimeException.class);
        Mockito.when(nonExceptional.matches(ArgumentMatchers.any())).thenReturn(true);
        MatcherAssert.assertThat(ElementMatchers.failSafe(exceptional).matches(new Object()), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.failSafe(nonExceptional).matches(new Object()), CoreMatchers.is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCachedNegativeSize() throws Exception {
        ElementMatchers.cached(new BooleanMatcher<Object>(true), (-1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCachingMatcherEvictionSize() throws Exception {
        ElementMatcher<Object> delegate = Mockito.mock(ElementMatcher.class);
        ElementMatcher<Object> matcher = ElementMatchers.cached(delegate, 1);
        Object target = new Object();
        Mockito.when(delegate.matches(target)).thenReturn(true);
        MatcherAssert.assertThat(matcher.matches(target), CoreMatchers.is(true));
        MatcherAssert.assertThat(matcher.matches(target), CoreMatchers.is(true));
        Mockito.verify(delegate).matches(target);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCachingMatcherMap() throws Exception {
        ElementMatcher<Object> delegate = Mockito.mock(ElementMatcher.class);
        ConcurrentMap<Object, Boolean> map = new ConcurrentHashMap<Object, Boolean>();
        ElementMatcher<Object> matcher = ElementMatchers.cached(delegate, map);
        Object target = new Object();
        Mockito.when(delegate.matches(target)).thenReturn(true);
        MatcherAssert.assertThat(matcher.matches(target), CoreMatchers.is(true));
        MatcherAssert.assertThat(matcher.matches(target), CoreMatchers.is(true));
        Mockito.verify(delegate).matches(target);
        MatcherAssert.assertThat(map.get(target), CoreMatchers.is(true));
    }

    @Test
    public void testIs() throws Exception {
        Object value = new Object();
        MatcherAssert.assertThat(ElementMatchers.is(value).matches(value), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(value).matches(new Object()), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.is(((Object) (null))).matches(null), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(((Object) (null))).matches(new Object()), CoreMatchers.is(false));
    }

    @Test
    public void testIsInterface() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isInterface().matches(of(Collection.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isInterface().matches(of(ArrayList.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsType() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.is(Object.class).matches(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(String.class).matches(TypeDescription.OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testIsField() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.is(ElementMatchersTest.FieldSample.class.getDeclaredField("foo")).matches(new FieldDescription.ForLoadedField(ElementMatchersTest.FieldSample.class.getDeclaredField("foo"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(ElementMatchersTest.FieldSample.class.getDeclaredField("bar")).matches(new FieldDescription.ForLoadedField(ElementMatchersTest.FieldSample.class.getDeclaredField("foo"))), CoreMatchers.is(false));
    }

    @Test
    public void testIsVolatile() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isVolatile().matches(new FieldDescription.ForLoadedField(ElementMatchersTest.FieldSample.class.getDeclaredField("foo"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isVolatile().matches(new FieldDescription.ForLoadedField(ElementMatchersTest.FieldSample.class.getDeclaredField("qux"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isVolatile().matches(new FieldDescription.ForLoadedField(ElementMatchersTest.FieldSample.class.getDeclaredField("baz"))), CoreMatchers.is(false));
    }

    @Test
    public void testIsTransient() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isTransient().matches(new FieldDescription.ForLoadedField(ElementMatchersTest.FieldSample.class.getDeclaredField("foo"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isTransient().matches(new FieldDescription.ForLoadedField(ElementMatchersTest.FieldSample.class.getDeclaredField("qux"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isTransient().matches(new FieldDescription.ForLoadedField(ElementMatchersTest.FieldSample.class.getDeclaredField("baz"))), CoreMatchers.is(true));
    }

    @Test
    public void testIsFieldDefinedShape() throws Exception {
        Field field = ElementMatchersTest.GenericFieldType.class.getDeclaredField(ElementMatchersTest.FOO);
        FieldDescription fieldDescription = of(ElementMatchersTest.GenericFieldType.Inner.class).getSuperClass().getDeclaredFields().filter(ElementMatchers.named(ElementMatchersTest.FOO)).getOnly();
        MatcherAssert.assertThat(ElementMatchers.is(field).matches(fieldDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.definedField(ElementMatchers.is(fieldDescription.asDefined())).matches(fieldDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(fieldDescription.asDefined()).matches(fieldDescription.asDefined()), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(fieldDescription.asDefined()).matches(fieldDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(fieldDescription).matches(fieldDescription.asDefined()), CoreMatchers.is(false));
    }

    @Test
    public void testIsMethodOrConstructor() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.is(Object.class.getDeclaredMethod("toString")).matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("toString"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(Object.class.getDeclaredMethod("toString")).matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("hashCode"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.is(Object.class.getDeclaredConstructor()).matches(new MethodDescription.ForLoadedConstructor(Object.class.getDeclaredConstructor())), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(Object.class.getDeclaredConstructor()).matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("hashCode"))), CoreMatchers.is(false));
    }

    @Test
    public void testIsMethodDefinedShape() throws Exception {
        Method method = ElementMatchersTest.GenericMethodType.class.getDeclaredMethod("foo", Exception.class);
        MethodDescription methodDescription = of(ElementMatchersTest.GenericMethodType.Inner.class).getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(ElementMatchersTest.FOO)).getOnly();
        MatcherAssert.assertThat(ElementMatchers.is(method).matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.definedMethod(ElementMatchers.is(methodDescription.asDefined())).matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(methodDescription.asDefined()).matches(methodDescription.asDefined()), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(methodDescription.asDefined()).matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(methodDescription).matches(methodDescription.asDefined()), CoreMatchers.is(false));
    }

    @Test
    public void testIsConstructorDefinedShape() throws Exception {
        Constructor<?> constructor = ElementMatchersTest.GenericConstructorType.class.getDeclaredConstructor(Exception.class);
        MethodDescription methodDescription = of(ElementMatchersTest.GenericConstructorType.Inner.class).getSuperClass().getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly();
        MatcherAssert.assertThat(ElementMatchers.is(constructor).matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.definedMethod(ElementMatchers.is(methodDescription.asDefined())).matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(methodDescription.asDefined()).matches(methodDescription.asDefined()), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(methodDescription.asDefined()).matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(methodDescription).matches(methodDescription.asDefined()), CoreMatchers.is(false));
    }

    @Test
    public void testIsParameterDefinedShape() throws Exception {
        ParameterDescription parameterDescription = of(ElementMatchersTest.GenericMethodType.Inner.class).getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(ElementMatchersTest.FOO)).getOnly().getParameters().getOnly();
        MatcherAssert.assertThat(ElementMatchers.definedParameter(ElementMatchers.is(parameterDescription.asDefined())).matches(parameterDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(parameterDescription.asDefined()).matches(parameterDescription.asDefined()), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(parameterDescription.asDefined()).matches(parameterDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(parameterDescription).matches(parameterDescription.asDefined()), CoreMatchers.is(false));
    }

    @Test
    public void testIsAnnotation() throws Exception {
        AnnotationDescription annotationDescription = of(ElementMatchersTest.IsAnnotatedWith.class).getDeclaredAnnotations().ofType(ElementMatchersTest.IsAnnotatedWithAnnotation.class);
        MatcherAssert.assertThat(ElementMatchers.is(ElementMatchersTest.IsAnnotatedWith.class.getAnnotation(ElementMatchersTest.IsAnnotatedWithAnnotation.class)).matches(annotationDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(ElementMatchersTest.Other.class.getAnnotation(ElementMatchersTest.OtherAnnotation.class)).matches(annotationDescription), CoreMatchers.is(false));
    }

    @Test
    public void testNot() throws Exception {
        Object value = new Object();
        @SuppressWarnings("unchecked")
        ElementMatcher<Object> elementMatcher = Mockito.mock(ElementMatcher.class);
        Mockito.when(elementMatcher.matches(value)).thenReturn(true);
        MatcherAssert.assertThat(ElementMatchers.not(elementMatcher).matches(value), CoreMatchers.is(false));
        Mockito.verify(elementMatcher).matches(value);
        Object otherValue = new Object();
        MatcherAssert.assertThat(ElementMatchers.not(elementMatcher).matches(otherValue), CoreMatchers.is(true));
        Mockito.verify(elementMatcher).matches(otherValue);
        Mockito.verifyNoMoreInteractions(elementMatcher);
    }

    @Test
    public void testAny() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.any().matches(new Object()), CoreMatchers.is(true));
    }

    @Test
    public void testAnyOfType() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.anyOf(Object.class).matches(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(String.class, Object.class).matches(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(String.class).matches(TypeDescription.OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testAnyOfMethodOrConstructor() throws Exception {
        Method toString = Object.class.getDeclaredMethod("toString");
        Method hashCode = Object.class.getDeclaredMethod("hashCode");
        MatcherAssert.assertThat(ElementMatchers.anyOf(toString).matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("toString"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(toString, hashCode).matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("toString"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(toString).matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("hashCode"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.anyOf(Object.class.getDeclaredConstructor()).matches(new MethodDescription.ForLoadedConstructor(Object.class.getDeclaredConstructor())), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(Object.class.getDeclaredConstructor(), String.class.getDeclaredConstructor(String.class)).matches(new MethodDescription.ForLoadedConstructor(Object.class.getDeclaredConstructor())), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(Object.class.getDeclaredConstructor()).matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("hashCode"))), CoreMatchers.is(false));
    }

    @Test
    public void testAnyMethodDefinedShape() throws Exception {
        Method method = ElementMatchersTest.GenericMethodType.class.getDeclaredMethod("foo", Exception.class);
        MethodDescription methodDescription = of(ElementMatchersTest.GenericMethodType.Inner.class).getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(ElementMatchersTest.FOO)).getOnly();
        MatcherAssert.assertThat(ElementMatchers.anyOf(method).matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.definedMethod(ElementMatchers.anyOf(methodDescription.asDefined())).matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(methodDescription.asDefined()).matches(methodDescription.asDefined()), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(methodDescription.asDefined()).matches(methodDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.anyOf(methodDescription).matches(methodDescription.asDefined()), CoreMatchers.is(false));
    }

    @Test
    public void testAnyOfConstructorDefinedShape() throws Exception {
        Constructor<?> constructor = ElementMatchersTest.GenericConstructorType.class.getDeclaredConstructor(Exception.class);
        MethodDescription methodDescription = of(ElementMatchersTest.GenericConstructorType.Inner.class).getSuperClass().getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly();
        MatcherAssert.assertThat(ElementMatchers.anyOf(constructor).matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.definedMethod(ElementMatchers.anyOf(methodDescription.asDefined())).matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(methodDescription.asDefined()).matches(methodDescription.asDefined()), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(methodDescription.asDefined()).matches(methodDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.anyOf(methodDescription).matches(methodDescription.asDefined()), CoreMatchers.is(false));
    }

    @Test
    public void testAnyOfField() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.anyOf(Integer.class.getDeclaredField("MAX_VALUE")).matches(new FieldDescription.ForLoadedField(Integer.class.getDeclaredField("MAX_VALUE"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(Integer.class.getDeclaredField("MAX_VALUE"), Integer.class.getDeclaredField("MIN_VALUE")).matches(new FieldDescription.ForLoadedField(Integer.class.getDeclaredField("MAX_VALUE"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(Integer.class.getDeclaredField("MAX_VALUE"), Integer.class.getDeclaredField("MIN_VALUE")).matches(new FieldDescription.ForLoadedField(Integer.class.getDeclaredField("SIZE"))), CoreMatchers.is(false));
    }

    @Test
    public void testAnyOfFieldDefinedShape() throws Exception {
        Field field = ElementMatchersTest.GenericFieldType.class.getDeclaredField(ElementMatchersTest.FOO);
        FieldDescription fieldDescription = of(ElementMatchersTest.GenericFieldType.Inner.class).getSuperClass().getDeclaredFields().filter(ElementMatchers.named(ElementMatchersTest.FOO)).getOnly();
        MatcherAssert.assertThat(ElementMatchers.anyOf(field).matches(fieldDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.definedField(ElementMatchers.anyOf(fieldDescription.asDefined())).matches(fieldDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(fieldDescription.asDefined()).matches(fieldDescription.asDefined()), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(fieldDescription.asDefined()).matches(fieldDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.anyOf(fieldDescription).matches(fieldDescription.asDefined()), CoreMatchers.is(false));
    }

    @Test
    public void testAnyOfAnnotation() throws Exception {
        AnnotationDescription annotationDescription = of(ElementMatchersTest.IsAnnotatedWith.class).getDeclaredAnnotations().ofType(ElementMatchersTest.IsAnnotatedWithAnnotation.class);
        MatcherAssert.assertThat(ElementMatchers.anyOf(ElementMatchersTest.IsAnnotatedWith.class.getAnnotation(ElementMatchersTest.IsAnnotatedWithAnnotation.class)).matches(annotationDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(ElementMatchersTest.IsAnnotatedWith.class.getAnnotation(ElementMatchersTest.IsAnnotatedWithAnnotation.class), ElementMatchersTest.Other.class.getAnnotation(ElementMatchersTest.OtherAnnotation.class)).matches(annotationDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(ElementMatchersTest.Other.class.getAnnotation(ElementMatchersTest.OtherAnnotation.class)).matches(annotationDescription), CoreMatchers.is(false));
    }

    @Test
    public void testAnnotationType() throws Exception {
        AnnotationDescription annotationDescription = of(ElementMatchersTest.IsAnnotatedWith.class).getDeclaredAnnotations().ofType(ElementMatchersTest.IsAnnotatedWithAnnotation.class);
        MatcherAssert.assertThat(ElementMatchers.annotationType(ElementMatchersTest.IsAnnotatedWithAnnotation.class).matches(annotationDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.annotationType(ElementMatchersTest.OtherAnnotation.class).matches(annotationDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.annotationType(ElementMatchersTest.IsAnnotatedWithAnnotation.class).matches(AnnotationDescription.ForLoadedAnnotation.of(ElementMatchersTest.Other.class.getAnnotation(ElementMatchersTest.OtherAnnotation.class))), CoreMatchers.is(false));
    }

    @Test
    public void testNone() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.none().matches(new Object()), CoreMatchers.is(false));
    }

    @Test
    public void testNoneOfType() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.noneOf(Object.class).matches(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.noneOf(String.class, Object.class).matches(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.noneOf(String.class).matches(TypeDescription.OBJECT), CoreMatchers.is(true));
    }

    @Test
    public void testNoneOfConstructorDefinedShape() throws Exception {
        Constructor<?> constructor = ElementMatchersTest.GenericConstructorType.class.getDeclaredConstructor(Exception.class);
        MethodDescription methodDescription = of(ElementMatchersTest.GenericConstructorType.Inner.class).getSuperClass().getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly();
        MatcherAssert.assertThat(ElementMatchers.noneOf(constructor).matches(methodDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.definedMethod(ElementMatchers.noneOf(methodDescription.asDefined())).matches(methodDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.noneOf(methodDescription.asDefined()).matches(methodDescription.asDefined()), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.noneOf(methodDescription.asDefined()).matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.noneOf(methodDescription).matches(methodDescription.asDefined()), CoreMatchers.is(true));
    }

    @Test
    public void testNoneOfMethodDefinedShape() throws Exception {
        Method method = ElementMatchersTest.GenericMethodType.class.getDeclaredMethod("foo", Exception.class);
        MethodDescription methodDescription = of(ElementMatchersTest.GenericMethodType.Inner.class).getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.named(ElementMatchersTest.FOO)).getOnly();
        MatcherAssert.assertThat(ElementMatchers.noneOf(method).matches(methodDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.definedMethod(ElementMatchers.noneOf(methodDescription.asDefined())).matches(methodDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.noneOf(methodDescription.asDefined()).matches(methodDescription.asDefined()), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.noneOf(methodDescription.asDefined()).matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.noneOf(methodDescription).matches(methodDescription.asDefined()), CoreMatchers.is(true));
    }

    @Test
    public void testNoneOfField() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.noneOf(ElementMatchersTest.FieldSample.class.getDeclaredField("foo")).matches(new FieldDescription.ForLoadedField(ElementMatchersTest.FieldSample.class.getDeclaredField("foo"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.noneOf(ElementMatchersTest.FieldSample.class.getDeclaredField("bar")).matches(new FieldDescription.ForLoadedField(ElementMatchersTest.FieldSample.class.getDeclaredField("foo"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.noneOf(ElementMatchersTest.FieldSample.class.getDeclaredField("foo"), ElementMatchersTest.FieldSample.class.getDeclaredField("bar")).matches(new FieldDescription.ForLoadedField(ElementMatchersTest.FieldSample.class.getDeclaredField("foo"))), CoreMatchers.is(false));
    }

    @Test
    public void testNoneOfFieldDefinedShape() throws Exception {
        Field field = ElementMatchersTest.GenericFieldType.class.getDeclaredField(ElementMatchersTest.FOO);
        FieldDescription fieldDescription = of(ElementMatchersTest.GenericFieldType.Inner.class).getSuperClass().getDeclaredFields().filter(ElementMatchers.named(ElementMatchersTest.FOO)).getOnly();
        MatcherAssert.assertThat(ElementMatchers.noneOf(field).matches(fieldDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.definedField(ElementMatchers.noneOf(fieldDescription.asDefined())).matches(fieldDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.noneOf(fieldDescription.asDefined()).matches(fieldDescription.asDefined()), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.noneOf(fieldDescription.asDefined()).matches(fieldDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.noneOf(fieldDescription).matches(fieldDescription.asDefined()), CoreMatchers.is(true));
    }

    @Test
    public void testNoneAnnotation() throws Exception {
        AnnotationDescription annotationDescription = of(ElementMatchersTest.IsAnnotatedWith.class).getDeclaredAnnotations().ofType(ElementMatchersTest.IsAnnotatedWithAnnotation.class);
        MatcherAssert.assertThat(ElementMatchers.noneOf(ElementMatchersTest.IsAnnotatedWith.class.getAnnotation(ElementMatchersTest.IsAnnotatedWithAnnotation.class)).matches(annotationDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.noneOf(ElementMatchersTest.IsAnnotatedWith.class.getAnnotation(ElementMatchersTest.IsAnnotatedWithAnnotation.class), ElementMatchersTest.Other.class.getAnnotation(ElementMatchersTest.OtherAnnotation.class)).matches(annotationDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.noneOf(ElementMatchersTest.Other.class.getAnnotation(ElementMatchersTest.OtherAnnotation.class)).matches(annotationDescription), CoreMatchers.is(true));
    }

    @Test
    public void testAnyOf() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.anyOf(ElementMatchersTest.FOO, ElementMatchersTest.BAR).matches(ElementMatchersTest.FOO), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(ElementMatchersTest.FOO, ElementMatchersTest.BAR).matches(ElementMatchersTest.BAR), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.anyOf(ElementMatchersTest.FOO, ElementMatchersTest.BAR).matches(new Object()), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.anyOf(ElementMatchersTest.FOO, ElementMatchersTest.BAR).toString(), CoreMatchers.is((((("(is(" + (ElementMatchersTest.FOO)) + ") or is(") + (ElementMatchersTest.BAR)) + "))")));
    }

    @Test
    public void testNoneOf() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.noneOf(ElementMatchersTest.FOO, ElementMatchersTest.BAR).matches(ElementMatchersTest.FOO), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.noneOf(ElementMatchersTest.FOO, ElementMatchersTest.BAR).matches(ElementMatchersTest.BAR), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.noneOf(ElementMatchersTest.FOO, ElementMatchersTest.BAR).matches(new Object()), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.noneOf(ElementMatchersTest.FOO, ElementMatchersTest.BAR).toString(), CoreMatchers.is((((("(not(is(" + (ElementMatchersTest.FOO)) + ")) and not(is(") + (ElementMatchersTest.BAR)) + ")))")));
    }

    @Test
    public void testWhereAny() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.whereAny(ElementMatchers.is(ElementMatchersTest.FOO)).matches(Arrays.asList(ElementMatchersTest.FOO, ElementMatchersTest.BAR)), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.whereAny(ElementMatchers.is(ElementMatchersTest.FOO)).matches(Arrays.asList(ElementMatchersTest.BAR, ElementMatchersTest.QUX)), CoreMatchers.is(false));
    }

    @Test
    public void testWhereNone() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.whereNone(ElementMatchers.is(ElementMatchersTest.FOO)).matches(Arrays.asList(ElementMatchersTest.FOO, ElementMatchersTest.BAR)), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.whereNone(ElementMatchers.is(ElementMatchersTest.FOO)).matches(Arrays.asList(ElementMatchersTest.BAR, ElementMatchersTest.QUX)), CoreMatchers.is(true));
    }

    @Test
    public void testRawType() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.erasure(Exception.class).matches(describe(ElementMatchersTest.GenericMethodType.class.getTypeParameters()[0])), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.erasure(Object.class).matches(describe(ElementMatchersTest.GenericMethodType.class.getTypeParameters()[0])), CoreMatchers.is(false));
    }

    @Test
    public void testRawTypes() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.erasures(Exception.class).matches(Collections.singletonList(describe(ElementMatchersTest.GenericMethodType.class.getTypeParameters()[0]))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.erasures(Object.class).matches(Collections.singletonList(describe(ElementMatchersTest.GenericMethodType.class.getTypeParameters()[0]))), CoreMatchers.is(false));
    }

    @Test
    public void testIsTypeVariable() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isVariable("T").matches(of(ElementMatchersTest.GenericDeclaredBy.class).getTypeVariables().getOnly()), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isVariable(ElementMatchersTest.FOO).matches(of(ElementMatchersTest.GenericDeclaredBy.class).getTypeVariables().getOnly()), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isVariable(ElementMatchersTest.FOO).matches(TypeDescription.OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testMethodName() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.hasMethodName(MethodDescription.TYPE_INITIALIZER_INTERNAL_NAME).matches(new MethodDescription.Latent.TypeInitializer(TypeDescription.OBJECT)), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.hasMethodName(MethodDescription.CONSTRUCTOR_INTERNAL_NAME).matches(TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isConstructor()).getOnly()), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.hasMethodName("toString").matches(TypeDescription.OBJECT.getDeclaredMethods().filter(ElementMatchers.isToString()).getOnly()), CoreMatchers.is(true));
    }

    @Test
    public void testNamed() throws Exception {
        ByteCodeElement byteCodeElement = Mockito.mock(ByteCodeElement.class);
        Mockito.when(byteCodeElement.getActualName()).thenReturn(ElementMatchersTest.FOO);
        MatcherAssert.assertThat(ElementMatchers.named(ElementMatchersTest.FOO).matches(byteCodeElement), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.named(ElementMatchersTest.FOO.toUpperCase()).matches(byteCodeElement), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.named(ElementMatchersTest.BAR).matches(byteCodeElement), CoreMatchers.is(false));
    }

    @Test
    public void testNamedIgnoreCase() throws Exception {
        ByteCodeElement byteCodeElement = Mockito.mock(ByteCodeElement.class);
        Mockito.when(byteCodeElement.getActualName()).thenReturn(ElementMatchersTest.FOO);
        MatcherAssert.assertThat(ElementMatchers.namedIgnoreCase(ElementMatchersTest.FOO).matches(byteCodeElement), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.namedIgnoreCase(ElementMatchersTest.FOO.toUpperCase()).matches(byteCodeElement), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.namedIgnoreCase(ElementMatchersTest.BAR).matches(byteCodeElement), CoreMatchers.is(false));
    }

    @Test
    public void testNameStartsWith() throws Exception {
        ByteCodeElement byteCodeElement = Mockito.mock(ByteCodeElement.class);
        Mockito.when(byteCodeElement.getActualName()).thenReturn(ElementMatchersTest.FOO);
        MatcherAssert.assertThat(ElementMatchers.nameStartsWith(ElementMatchersTest.FOO.substring(0, 2)).matches(byteCodeElement), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.nameStartsWith(ElementMatchersTest.FOO.substring(0, 2).toUpperCase()).matches(byteCodeElement), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.nameStartsWith(ElementMatchersTest.BAR).matches(byteCodeElement), CoreMatchers.is(false));
    }

    @Test
    public void testNameStartsWithIgnoreCase() throws Exception {
        ByteCodeElement byteCodeElement = Mockito.mock(ByteCodeElement.class);
        Mockito.when(byteCodeElement.getActualName()).thenReturn(ElementMatchersTest.FOO);
        MatcherAssert.assertThat(ElementMatchers.nameStartsWithIgnoreCase(ElementMatchersTest.FOO.substring(0, 2)).matches(byteCodeElement), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.nameStartsWithIgnoreCase(ElementMatchersTest.FOO.substring(0, 2).toUpperCase()).matches(byteCodeElement), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.nameStartsWithIgnoreCase(ElementMatchersTest.BAR).matches(byteCodeElement), CoreMatchers.is(false));
    }

    @Test
    public void testNameEndsWith() throws Exception {
        ByteCodeElement byteCodeElement = Mockito.mock(ByteCodeElement.class);
        Mockito.when(byteCodeElement.getActualName()).thenReturn(ElementMatchersTest.FOO);
        MatcherAssert.assertThat(ElementMatchers.nameEndsWith(ElementMatchersTest.FOO.substring(1)).matches(byteCodeElement), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.nameEndsWith(ElementMatchersTest.FOO.substring(1).toUpperCase()).matches(byteCodeElement), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.nameEndsWith(ElementMatchersTest.BAR).matches(byteCodeElement), CoreMatchers.is(false));
    }

    @Test
    public void testNameEndsWithIgnoreCase() throws Exception {
        ByteCodeElement byteCodeElement = Mockito.mock(ByteCodeElement.class);
        Mockito.when(byteCodeElement.getActualName()).thenReturn(ElementMatchersTest.FOO);
        MatcherAssert.assertThat(ElementMatchers.nameEndsWithIgnoreCase(ElementMatchersTest.FOO.substring(1)).matches(byteCodeElement), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.nameEndsWithIgnoreCase(ElementMatchersTest.FOO.substring(1).toUpperCase()).matches(byteCodeElement), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.nameEndsWithIgnoreCase(ElementMatchersTest.BAR).matches(byteCodeElement), CoreMatchers.is(false));
    }

    @Test
    public void testNameContains() throws Exception {
        ByteCodeElement byteCodeElement = Mockito.mock(ByteCodeElement.class);
        Mockito.when(byteCodeElement.getActualName()).thenReturn(ElementMatchersTest.FOO);
        MatcherAssert.assertThat(ElementMatchers.nameContains(ElementMatchersTest.FOO.substring(1, 2)).matches(byteCodeElement), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.nameContains(ElementMatchersTest.FOO.substring(1, 2).toUpperCase()).matches(byteCodeElement), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.nameContains(ElementMatchersTest.BAR).matches(byteCodeElement), CoreMatchers.is(false));
    }

    @Test
    public void testNameContainsIgnoreCase() throws Exception {
        ByteCodeElement byteCodeElement = Mockito.mock(ByteCodeElement.class);
        Mockito.when(byteCodeElement.getActualName()).thenReturn(ElementMatchersTest.FOO);
        MatcherAssert.assertThat(ElementMatchers.nameContainsIgnoreCase(ElementMatchersTest.FOO.substring(1, 2)).matches(byteCodeElement), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.nameContainsIgnoreCase(ElementMatchersTest.FOO.substring(1, 2).toUpperCase()).matches(byteCodeElement), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.nameContainsIgnoreCase(ElementMatchersTest.BAR).matches(byteCodeElement), CoreMatchers.is(false));
    }

    @Test
    public void testNameMatches() throws Exception {
        ByteCodeElement byteCodeElement = Mockito.mock(ByteCodeElement.class);
        Mockito.when(byteCodeElement.getActualName()).thenReturn(ElementMatchersTest.FOO);
        MatcherAssert.assertThat(ElementMatchers.nameMatches((("^" + (ElementMatchersTest.FOO)) + "$")).matches(byteCodeElement), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.nameMatches(ElementMatchersTest.FOO.toUpperCase()).matches(byteCodeElement), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.nameMatches(ElementMatchersTest.BAR).matches(byteCodeElement), CoreMatchers.is(false));
    }

    @Test
    public void testIsNamed() throws Exception {
        NamedElement.WithOptionalName namedElement = Mockito.mock(NamedElement.WithOptionalName.class);
        MatcherAssert.assertThat(ElementMatchers.isNamed().matches(namedElement), CoreMatchers.is(false));
        Mockito.when(namedElement.isNamed()).thenReturn(true);
        MatcherAssert.assertThat(ElementMatchers.isNamed().matches(namedElement), CoreMatchers.is(true));
    }

    @Test
    public void testHasDescriptor() throws Exception {
        ByteCodeElement byteCodeElement = Mockito.mock(ByteCodeElement.class);
        Mockito.when(byteCodeElement.getDescriptor()).thenReturn(ElementMatchersTest.FOO);
        MatcherAssert.assertThat(ElementMatchers.hasDescriptor(ElementMatchersTest.FOO).matches(byteCodeElement), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.hasDescriptor(ElementMatchersTest.FOO.toUpperCase()).matches(byteCodeElement), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.hasDescriptor(ElementMatchersTest.BAR).matches(byteCodeElement), CoreMatchers.is(false));
    }

    @Test
    public void testIsDeclaredBy() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isDeclaredBy(ElementMatchersTest.IsDeclaredBy.class).matches(of(ElementMatchersTest.IsDeclaredBy.Inner.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isDeclaredBy(ElementMatchersTest.IsDeclaredBy.class).matches(Mockito.mock(ByteCodeElement.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isDeclaredBy(Object.class).matches(Mockito.mock(ByteCodeElement.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsDeclaredByGeneric() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isDeclaredByGeneric(ElementMatchersTest.GenericDeclaredBy.Inner.class.getGenericInterfaces()[0]).matches(of(ElementMatchersTest.GenericDeclaredBy.Inner.class).getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly()), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isDeclaredByGeneric(ElementMatchersTest.GenericDeclaredBy.Inner.class.getGenericInterfaces()[0]).matches(of(ElementMatchersTest.GenericDeclaredBy.class).getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly()), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isDeclaredByGeneric(ElementMatchersTest.GenericDeclaredBy.class).matches(of(ElementMatchersTest.GenericDeclaredBy.Inner.class).getInterfaces().getOnly().getDeclaredMethods().filter(ElementMatchers.isMethod()).getOnly()), CoreMatchers.is(false));
    }

    @Test
    public void testIsOverriddenFrom() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isOverriddenFrom(Object.class).matches(new MethodDescription.ForLoadedMethod(String.class.getDeclaredMethod("toString"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isOverriddenFrom(Object.class).matches(new MethodDescription.ForLoadedMethod(String.class.getDeclaredMethod("substring", int.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isOverriddenFrom(Comparable.class).matches(new MethodDescription.ForLoadedMethod(String.class.getDeclaredMethod("compareTo", String.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isOverriddenFromGeneric(Object.class).matches(new MethodDescription.ForLoadedMethod(String.class.getDeclaredMethod("toString"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isOverriddenFromGeneric(Object.class).matches(new MethodDescription.ForLoadedMethod(String.class.getDeclaredMethod("substring", int.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isOverriddenFromGeneric(Comparable.class).matches(new MethodDescription.ForLoadedMethod(String.class.getDeclaredMethod("compareTo", String.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isOverriddenFromGeneric(String.class.getGenericInterfaces()[1]).matches(new MethodDescription.ForLoadedMethod(String.class.getDeclaredMethod("compareTo", String.class))), CoreMatchers.is(true));
    }

    @Test
    public void testIsVisibleTo() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isVisibleTo(Object.class).matches(of(ElementMatchersTest.IsVisibleTo.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isVisibleTo(Object.class).matches(of(ElementMatchersTest.IsNotVisibleTo.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsAccessibleTo() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isAccessibleTo(Object.class).matches(of(ElementMatchersTest.IsVisibleTo.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isAccessibleTo(Object.class).matches(of(ElementMatchersTest.IsNotVisibleTo.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsAnnotatedWith() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isAnnotatedWith(ElementMatchersTest.IsAnnotatedWithAnnotation.class).matches(of(ElementMatchersTest.IsAnnotatedWith.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isAnnotatedWith(ElementMatchersTest.IsAnnotatedWithAnnotation.class).matches(TypeDescription.OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testIsPublic() throws Exception {
        ModifierReviewable.OfByteCodeElement modifierReviewable = Mockito.mock(ModifierReviewable.OfByteCodeElement.class);
        Mockito.when(modifierReviewable.getModifiers()).thenReturn(Opcodes.ACC_PUBLIC);
        MatcherAssert.assertThat(ElementMatchers.isPublic().matches(modifierReviewable), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isPublic().matches(Mockito.mock(ModifierReviewable.OfByteCodeElement.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsProtected() throws Exception {
        ModifierReviewable.OfByteCodeElement modifierReviewable = Mockito.mock(ModifierReviewable.OfByteCodeElement.class);
        Mockito.when(modifierReviewable.getModifiers()).thenReturn(Opcodes.ACC_PROTECTED);
        MatcherAssert.assertThat(ElementMatchers.isProtected().matches(modifierReviewable), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isProtected().matches(Mockito.mock(ModifierReviewable.OfByteCodeElement.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsPackagePrivate() throws Exception {
        ModifierReviewable.OfByteCodeElement modifierReviewable = Mockito.mock(ModifierReviewable.OfByteCodeElement.class);
        Mockito.when(modifierReviewable.getModifiers()).thenReturn((((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_PRIVATE)) | (Opcodes.ACC_PROTECTED)));
        MatcherAssert.assertThat(ElementMatchers.isPackagePrivate().matches(Mockito.mock(ModifierReviewable.OfByteCodeElement.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isPackagePrivate().matches(modifierReviewable), CoreMatchers.is(false));
    }

    @Test
    public void testIsPrivate() throws Exception {
        ModifierReviewable.OfByteCodeElement modifierReviewable = Mockito.mock(ModifierReviewable.OfByteCodeElement.class);
        Mockito.when(modifierReviewable.getModifiers()).thenReturn(Opcodes.ACC_PRIVATE);
        MatcherAssert.assertThat(ElementMatchers.isPrivate().matches(modifierReviewable), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isPrivate().matches(Mockito.mock(ModifierReviewable.OfByteCodeElement.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsAbstract() throws Exception {
        ModifierReviewable.OfAbstraction modifierReviewable = Mockito.mock(ModifierReviewable.OfAbstraction.class);
        Mockito.when(modifierReviewable.getModifiers()).thenReturn(Opcodes.ACC_ABSTRACT);
        MatcherAssert.assertThat(ElementMatchers.isAbstract().matches(modifierReviewable), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isAbstract().matches(Mockito.mock(ModifierReviewable.OfAbstraction.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsEnum() throws Exception {
        ModifierReviewable.OfEnumeration modifierReviewable = Mockito.mock(ModifierReviewable.OfEnumeration.class);
        Mockito.when(modifierReviewable.getModifiers()).thenReturn(Opcodes.ACC_ENUM);
        MatcherAssert.assertThat(ElementMatchers.isEnum().matches(modifierReviewable), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isEnum().matches(Mockito.mock(ModifierReviewable.OfEnumeration.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsMandated() throws Exception {
        ParameterDescription parameterDescription = Mockito.mock(ParameterDescription.class);
        Mockito.when(parameterDescription.getModifiers()).thenReturn(Opcodes.ACC_MANDATED);
        MatcherAssert.assertThat(ElementMatchers.isMandated().matches(parameterDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isMandated().matches(Mockito.mock(ParameterDescription.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsFinal() throws Exception {
        ModifierReviewable.OfByteCodeElement modifierReviewable = Mockito.mock(ModifierReviewable.OfByteCodeElement.class);
        Mockito.when(modifierReviewable.getModifiers()).thenReturn(Opcodes.ACC_FINAL);
        MatcherAssert.assertThat(ElementMatchers.isFinal().matches(modifierReviewable), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isFinal().matches(Mockito.mock(ModifierReviewable.OfByteCodeElement.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsStatic() throws Exception {
        ModifierReviewable.OfByteCodeElement modifierReviewable = Mockito.mock(ModifierReviewable.OfByteCodeElement.class);
        Mockito.when(modifierReviewable.getModifiers()).thenReturn(Opcodes.ACC_STATIC);
        MatcherAssert.assertThat(ElementMatchers.isStatic().matches(modifierReviewable), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isStatic().matches(Mockito.mock(ModifierReviewable.OfByteCodeElement.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsSynthetic() throws Exception {
        ModifierReviewable modifierReviewable = Mockito.mock(ModifierReviewable.class);
        Mockito.when(modifierReviewable.getModifiers()).thenReturn(Opcodes.ACC_SYNTHETIC);
        MatcherAssert.assertThat(ElementMatchers.isSynthetic().matches(modifierReviewable), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isSynthetic().matches(Mockito.mock(ModifierReviewable.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsSynchronized() throws Exception {
        MethodDescription methodDescription = Mockito.mock(MethodDescription.class);
        Mockito.when(methodDescription.getModifiers()).thenReturn(Opcodes.ACC_SYNCHRONIZED);
        MatcherAssert.assertThat(ElementMatchers.isSynchronized().matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isSynchronized().matches(Mockito.mock(MethodDescription.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsNative() throws Exception {
        MethodDescription methodDescription = Mockito.mock(MethodDescription.class);
        Mockito.when(methodDescription.getModifiers()).thenReturn(Opcodes.ACC_NATIVE);
        MatcherAssert.assertThat(ElementMatchers.isNative().matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isNative().matches(Mockito.mock(MethodDescription.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsStrict() throws Exception {
        MethodDescription methodDescription = Mockito.mock(MethodDescription.class);
        Mockito.when(methodDescription.getModifiers()).thenReturn(Opcodes.ACC_STRICT);
        MatcherAssert.assertThat(ElementMatchers.isStrict().matches(methodDescription), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isStrict().matches(Mockito.mock(MethodDescription.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsVarArgs() throws Exception {
        MethodDescription modifierReviewable = Mockito.mock(MethodDescription.class);
        Mockito.when(modifierReviewable.getModifiers()).thenReturn(Opcodes.ACC_VARARGS);
        MatcherAssert.assertThat(ElementMatchers.isVarArgs().matches(modifierReviewable), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isVarArgs().matches(Mockito.mock(MethodDescription.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsBridge() throws Exception {
        MethodDescription modifierReviewable = Mockito.mock(MethodDescription.class);
        Mockito.when(modifierReviewable.getModifiers()).thenReturn(Opcodes.ACC_BRIDGE);
        MatcherAssert.assertThat(ElementMatchers.isBridge().matches(modifierReviewable), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isBridge().matches(Mockito.mock(MethodDescription.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsMethod() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.is(ElementMatchersTest.IsEqual.class.getDeclaredMethod(ElementMatchersTest.FOO)).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.IsEqual.class.getDeclaredMethod(ElementMatchersTest.FOO))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(ElementMatchersTest.IsEqual.class.getDeclaredMethod(ElementMatchersTest.FOO)).matches(Mockito.mock(MethodDescription.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.is(ElementMatchersTest.IsEqual.class.getDeclaredConstructor()).matches(new MethodDescription.ForLoadedConstructor(ElementMatchersTest.IsEqual.class.getDeclaredConstructor())), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.is(ElementMatchersTest.IsEqual.class.getDeclaredConstructor()).matches(Mockito.mock(MethodDescription.class)), CoreMatchers.is(false));
    }

    @Test
    public void testReturnsGeneric() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.returnsGeneric(ElementMatchersTest.GenericMethodType.class.getTypeParameters()[0]).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.returnsGeneric(Exception.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.returns(Exception.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(true));
    }

    @Test
    public void testReturns() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.returns(void.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Returns.class.getDeclaredMethod(ElementMatchersTest.FOO))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.returns(void.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Returns.class.getDeclaredMethod(ElementMatchersTest.BAR))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.returns(String.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Returns.class.getDeclaredMethod(ElementMatchersTest.BAR))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.returns(String.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Returns.class.getDeclaredMethod(ElementMatchersTest.FOO))), CoreMatchers.is(false));
    }

    @Test
    public void testTakesArgumentsGeneric() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.takesGenericArguments(ElementMatchersTest.GenericMethodType.class.getTypeParameters()[0]).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.takesGenericArguments(describe(ElementMatchersTest.GenericMethodType.class.getTypeParameters()[0])).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.takesGenericArguments(Exception.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.takesGenericArguments(Collections.singletonList(of(Exception.class))).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.takesArguments(Exception.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.takesArguments(Collections.singletonList(of(Exception.class))).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(true));
    }

    @Test
    public void testTakesArguments() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.takesArguments(Void.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.TakesArguments.class.getDeclaredMethod(ElementMatchersTest.FOO, Void.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.takesArguments(Void.class, Object.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.TakesArguments.class.getDeclaredMethod(ElementMatchersTest.FOO, Void.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.takesArguments(String.class, int.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.TakesArguments.class.getDeclaredMethod(ElementMatchersTest.BAR, String.class, int.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.takesArguments(String.class, Integer.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.TakesArguments.class.getDeclaredMethod(ElementMatchersTest.BAR, String.class, int.class))), CoreMatchers.is(false));
    }

    @Test
    public void testTakesArgumentGeneric() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.takesGenericArgument(0, ElementMatchersTest.GenericMethodType.class.getTypeParameters()[0]).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.takesGenericArgument(0, Exception.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.takesGenericArgument(1, ElementMatchersTest.GenericMethodType.class.getTypeParameters()[0]).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(false));
    }

    @Test
    public void testTakesArgument() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.takesArgument(0, Void.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.TakesArguments.class.getDeclaredMethod(ElementMatchersTest.FOO, Void.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.takesArgument(0, Object.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.TakesArguments.class.getDeclaredMethod(ElementMatchersTest.FOO, Void.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.takesArgument(1, int.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.TakesArguments.class.getDeclaredMethod(ElementMatchersTest.BAR, String.class, int.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.takesArgument(1, Integer.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.TakesArguments.class.getDeclaredMethod(ElementMatchersTest.BAR, String.class, int.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.takesArgument(2, int.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.TakesArguments.class.getDeclaredMethod(ElementMatchersTest.BAR, String.class, int.class))), CoreMatchers.is(false));
    }

    @Test
    public void testTakesArgumentsLength() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.takesArguments(1).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.TakesArguments.class.getDeclaredMethod(ElementMatchersTest.FOO, Void.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.takesArguments(2).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.TakesArguments.class.getDeclaredMethod(ElementMatchersTest.FOO, Void.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.takesArguments(2).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.TakesArguments.class.getDeclaredMethod(ElementMatchersTest.BAR, String.class, int.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.takesArguments(3).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.TakesArguments.class.getDeclaredMethod(ElementMatchersTest.BAR, String.class, int.class))), CoreMatchers.is(false));
    }

    @Test
    public void testDeclaresException() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.declaresException(IOException.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.FOO))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.declaresException(SQLException.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.FOO))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.declaresException(Error.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.FOO))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.declaresException(RuntimeException.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.FOO))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.declaresException(IOException.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.BAR))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.declaresException(SQLException.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.BAR))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.declaresException(Error.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.BAR))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.declaresException(RuntimeException.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.BAR))), CoreMatchers.is(false));
    }

    @Test
    public void testDeclaresGenericException() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.declaresGenericException(ElementMatchersTest.GenericMethodType.class.getTypeParameters()[0]).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.declaresGenericException(Exception.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.declaresException(Exception.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericMethodType.class.getDeclaredMethod(ElementMatchersTest.FOO, Exception.class))), CoreMatchers.is(true));
    }

    @Test
    public void testCanThrow() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.canThrow(IOException.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.FOO))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.canThrow(SQLException.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.FOO))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.canThrow(Error.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.FOO))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.canThrow(RuntimeException.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.FOO))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.canThrow(IOException.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.BAR))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.canThrow(SQLException.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.BAR))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.canThrow(Error.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.BAR))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.canThrow(RuntimeException.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CanThrow.class.getDeclaredMethod(ElementMatchersTest.BAR))), CoreMatchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeclaresExceptionForNonThrowableType() throws Exception {
        ElementMatcher<Object> elementMatcher = ((ElementMatcher) (ElementMatchers.declaresException(((Class) (Object.class)))));
        MatcherAssert.assertThat(elementMatcher.matches(new Object()), CoreMatchers.is(false));
    }

    @Test
    public void testSortIsMethod() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isMethod().matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("toString"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isMethod().matches(new MethodDescription.ForLoadedConstructor(Object.class.getDeclaredConstructor())), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isMethod().matches(new MethodDescription.Latent.TypeInitializer(Mockito.mock(TypeDescription.class))), CoreMatchers.is(false));
    }

    @Test
    public void testSortIsConstructor() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isConstructor().matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("toString"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isConstructor().matches(new MethodDescription.ForLoadedConstructor(Object.class.getDeclaredConstructor())), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isConstructor().matches(new MethodDescription.Latent.TypeInitializer(Mockito.mock(TypeDescription.class))), CoreMatchers.is(false));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testIsDefaultMethod() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isDefaultMethod().matches(new MethodDescription.ForLoadedMethod(Class.forName(ElementMatchersTest.SINGLE_DEFAULT_METHOD).getDeclaredMethod(ElementMatchersTest.FOO))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isDefaultMethod().matches(new MethodDescription.ForLoadedMethod(Runnable.class.getDeclaredMethod("run"))), CoreMatchers.is(false));
    }

    @Test
    public void testSortIsTypeInitializer() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isTypeInitializer().matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("toString"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isTypeInitializer().matches(new MethodDescription.ForLoadedConstructor(Object.class.getDeclaredConstructor())), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isTypeInitializer().matches(new MethodDescription.Latent.TypeInitializer(Mockito.mock(TypeDescription.class))), CoreMatchers.is(true));
    }

    @Test
    public void testSortIsBridge() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isBridge().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericType.Extension.class.getDeclaredMethod("foo", Object.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isBridge().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.GenericType.Extension.class.getDeclaredMethod("foo", Void.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isBridge().matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("toString"))), CoreMatchers.is(false));
    }

    @Test
    public void testIsVirtual() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isVirtual().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.IsVirtual.class.getDeclaredMethod("baz"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isVirtual().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.IsVirtual.class.getDeclaredMethod("foo"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isVirtual().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.IsVirtual.class.getDeclaredMethod("bar"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isVirtual().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.IsVirtual.class.getDeclaredMethod("qux"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isVirtual().matches(new MethodDescription.ForLoadedConstructor(ElementMatchersTest.IsVirtual.class.getDeclaredConstructor())), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isVirtual().matches(new MethodDescription.Latent.TypeInitializer(TypeDescription.OBJECT)), CoreMatchers.is(false));
    }

    @Test
    public void testIsDefaultFinalizer() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isDefaultFinalizer().matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("finalize"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isDefaultFinalizer().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.ObjectMethods.class.getDeclaredMethod("finalize"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isDefaultFinalizer().matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("toString"))), CoreMatchers.is(false));
    }

    @Test
    public void testIsFinalizer() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isFinalizer().matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("finalize"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isFinalizer().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.ObjectMethods.class.getDeclaredMethod("finalize"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isFinalizer().matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("toString"))), CoreMatchers.is(false));
    }

    @Test
    public void testIsHashCode() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isHashCode().matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("hashCode"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isHashCode().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.ObjectMethods.class.getDeclaredMethod("hashCode"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isHashCode().matches(new MethodDescription.ForLoadedMethod(Runnable.class.getDeclaredMethod("run"))), CoreMatchers.is(false));
    }

    @Test
    public void testIsEquals() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isEquals().matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("equals", Object.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isEquals().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.ObjectMethods.class.getDeclaredMethod("equals", Object.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isEquals().matches(new MethodDescription.ForLoadedMethod(Runnable.class.getDeclaredMethod("run"))), CoreMatchers.is(false));
    }

    @Test
    public void testIsClone() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isClone().matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("clone"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isClone().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.ObjectMethods.class.getDeclaredMethod("clone"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isClone().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CloneMethods.class.getDeclaredMethod("clone"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isClone().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.CloneMethods.class.getDeclaredMethod("clone", int.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isClone().matches(new MethodDescription.ForLoadedMethod(Runnable.class.getDeclaredMethod("run"))), CoreMatchers.is(false));
    }

    @Test
    public void testIsToString() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isToString().matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("toString"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isToString().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.ObjectMethods.class.getDeclaredMethod("toString"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isToString().matches(new MethodDescription.ForLoadedMethod(Runnable.class.getDeclaredMethod("run"))), CoreMatchers.is(false));
    }

    @Test
    public void testIsDefaultConstructor() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isDefaultConstructor().matches(new MethodDescription.ForLoadedConstructor(Object.class.getDeclaredConstructor())), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isDefaultConstructor().matches(new MethodDescription.ForLoadedConstructor(String.class.getDeclaredConstructor(String.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isDefaultConstructor().matches(new MethodDescription.ForLoadedMethod(Runnable.class.getDeclaredMethod("run"))), CoreMatchers.is(false));
    }

    @Test
    public void testIsGetter() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isGetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getFoo"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isGetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("isQux"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isGetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getQux"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isGetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("isBar"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isGetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getBar"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isGetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("isBaz"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isGetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getBaz"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isGetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getBaz", Void.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isGetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("get"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isGetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("is"))), CoreMatchers.is(true));
    }

    @Test
    public void testPropertyGetter() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isGetter("qux").matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getQux"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isGetter("bar").matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getQux"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isGetter("foo").matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getFoo"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isGetter("").matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("get"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isGetter("").matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("is"))), CoreMatchers.is(true));
    }

    @Test
    public void testIsSetter() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isSetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setFoo"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isSetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setBar", boolean.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isSetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setQux", Boolean.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isSetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setBaz", String.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isSetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setBaz", String.class, Void.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isSetter().matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("set", Object.class))), CoreMatchers.is(true));
    }

    @Test
    public void testPropertySetter() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isSetter("foo").matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setFoo"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isSetter("qux").matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setQux", Boolean.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isSetter("bar").matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setQux", Boolean.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isSetter("").matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("set", Object.class))), CoreMatchers.is(true));
    }

    @Test
    public void testIsNonGenericGetter() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isGetter(String.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getBaz"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isGetter(Void.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getBaz"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isGetter(Object.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getQuxbaz"))), CoreMatchers.is(true));
    }

    @Test
    public void testIsNonGenericSetter() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isSetter(String.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setBaz", String.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isSetter(Void.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setBaz", String.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isSetter(Object.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setQuxbaz", Object.class))), CoreMatchers.is(true));
    }

    @Test
    public void testIsGenericGetter() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isGenericGetter(String.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getBaz"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isGenericGetter(Void.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getBaz"))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isGenericGetter(ElementMatchersTest.Getters.class.getTypeParameters()[0]).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getQuxbaz"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isGenericGetter(Object.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Getters.class.getDeclaredMethod("getQuxbaz"))), CoreMatchers.is(false));
    }

    @Test
    public void testIsGenericSetter() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isGenericSetter(String.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setBaz", String.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isGenericSetter(Void.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setBaz", String.class))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isGenericSetter(ElementMatchersTest.Setters.class.getTypeParameters()[0]).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setQuxbaz", Object.class))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isGenericSetter(Object.class).matches(new MethodDescription.ForLoadedMethod(ElementMatchersTest.Setters.class.getDeclaredMethod("setQuxbaz", Object.class))), CoreMatchers.is(false));
    }

    @Test
    public void testHasSignature() throws Exception {
        MethodDescription.SignatureToken signatureToken = new MethodDescription.SignatureToken("toString", TypeDescription.STRING, Collections.<TypeDescription>emptyList());
        MatcherAssert.assertThat(ElementMatchers.hasSignature(signatureToken).matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("toString"))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.hasSignature(signatureToken).matches(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("hashCode"))), CoreMatchers.is(false));
    }

    @Test
    public void testIsSubOrSuperType() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isSubTypeOf(String.class).matches(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isSubTypeOf(Object.class).matches(TypeDescription.STRING), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isSubTypeOf(Serializable.class).matches(TypeDescription.STRING), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isSuperTypeOf(Object.class).matches(TypeDescription.STRING), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isSuperTypeOf(String.class).matches(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isSuperTypeOf(String.class).matches(of(Serializable.class)), CoreMatchers.is(true));
    }

    @Test
    public void testHasSuperType() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.hasSuperType(ElementMatchers.is(Object.class)).matches(TypeDescription.STRING), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.hasSuperType(ElementMatchers.is(String.class)).matches(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.hasSuperType(ElementMatchers.is(Serializable.class)).matches(TypeDescription.STRING), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.hasSuperType(ElementMatchers.is(Serializable.class)).matches(TypeDescription.OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testIsAnnotatedInheritedWith() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.inheritsAnnotation(ElementMatchersTest.OtherAnnotation.class).matches(of(ElementMatchersTest.OtherInherited.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isAnnotatedWith(ElementMatchersTest.OtherAnnotation.class).matches(of(ElementMatchersTest.OtherInherited.class)), CoreMatchers.is(false));
    }

    @Test
    public void testTypeSort() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.ofSort(NON_GENERIC).matches(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.ofSort(VARIABLE).matches(TypeDescription.OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testDeclaresField() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.declaresField(ElementMatchers.isAnnotatedWith(ElementMatchersTest.OtherAnnotation.class)).matches(of(ElementMatchersTest.DeclaresFieldOrMethod.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.declaresField(ElementMatchers.isAnnotatedWith(ElementMatchersTest.OtherAnnotation.class)).matches(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.declaresMethod(ElementMatchers.isAnnotatedWith(ElementMatchersTest.OtherAnnotation.class)).matches(of(ElementMatchersTest.DeclaresFieldOrMethod.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.declaresMethod(ElementMatchers.isAnnotatedWith(ElementMatchersTest.OtherAnnotation.class)).matches(TypeDescription.OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testFieldType() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.fieldType(ElementMatchersTest.GenericFieldType.class).matches(new FieldDescription.ForLoadedField(ElementMatchersTest.GenericFieldType.class.getDeclaredField(ElementMatchersTest.FOO))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.fieldType(Object.class).matches(new FieldDescription.ForLoadedField(ElementMatchersTest.GenericFieldType.class.getDeclaredField(ElementMatchersTest.FOO))), CoreMatchers.is(false));
    }

    @Test
    public void testGenericFieldType() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.genericFieldType(ElementMatchersTest.GenericFieldType.class.getTypeParameters()[0]).matches(new FieldDescription.ForLoadedField(ElementMatchersTest.GenericFieldType.class.getDeclaredField(ElementMatchersTest.BAR))), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.genericFieldType(Object.class).matches(new FieldDescription.ForLoadedField(ElementMatchersTest.GenericFieldType.class.getDeclaredField(ElementMatchersTest.BAR))), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.fieldType(Object.class).matches(new FieldDescription.ForLoadedField(ElementMatchersTest.GenericFieldType.class.getDeclaredField(ElementMatchersTest.BAR))), CoreMatchers.is(true));
    }

    @Test
    public void testIsBootstrapClassLoader() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isBootstrapClassLoader().matches(null), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isBootstrapClassLoader().matches(Mockito.mock(ClassLoader.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsSystemClassLoader() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isSystemClassLoader().matches(ClassLoader.getSystemClassLoader()), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isSystemClassLoader().matches(null), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isSystemClassLoader().matches(ClassLoader.getSystemClassLoader().getParent()), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isSystemClassLoader().matches(Mockito.mock(ClassLoader.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsExtensionClassLoader() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.isExtensionClassLoader().matches(ClassLoader.getSystemClassLoader().getParent()), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isExtensionClassLoader().matches(ClassLoader.getSystemClassLoader()), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isExtensionClassLoader().matches(null), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isExtensionClassLoader().matches(Mockito.mock(ClassLoader.class)), CoreMatchers.is(false));
    }

    @Test
    public void testIsChildOf() throws Exception {
        ClassLoader parent = new URLClassLoader(new URL[0], null);
        MatcherAssert.assertThat(ElementMatchers.isChildOf(parent).matches(new URLClassLoader(new URL[0], parent)), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isChildOf(parent).matches(new URLClassLoader(new URL[0], null)), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isChildOf(parent).matches(null), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isChildOf(null).matches(Mockito.mock(ClassLoader.class)), CoreMatchers.is(true));
    }

    @Test
    public void testIsParentOf() throws Exception {
        ClassLoader parent = new URLClassLoader(new URL[0], null);
        MatcherAssert.assertThat(ElementMatchers.isParentOf(new URLClassLoader(new URL[0], parent)).matches(parent), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isParentOf(new URLClassLoader(new URL[0], null)).matches(parent), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isParentOf(null).matches(new URLClassLoader(new URL[0], null)), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.isParentOf(null).matches(null), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.isParentOf(Mockito.mock(ClassLoader.class)).matches(null), CoreMatchers.is(true));
    }

    @Test
    public void testOfType() throws Exception {
        ClassLoader classLoader = new URLClassLoader(new URL[0], null);
        MatcherAssert.assertThat(ElementMatchers.ofType(ElementMatchers.is(URLClassLoader.class)).matches(classLoader), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.ofType(ElementMatchers.is(ClassLoader.class)).matches(classLoader), CoreMatchers.is(false));
        MatcherAssert.assertThat(ElementMatchers.ofType(ElementMatchers.is(URLClassLoader.class)).matches(null), CoreMatchers.is(false));
    }

    @Test
    public void testIsPrimitive() {
        MatcherAssert.assertThat(isPrimitive().matches(TypeDescription.VOID), CoreMatchers.is(true));
        MatcherAssert.assertThat(isPrimitive().matches(of(int.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(isPrimitive().matches(TypeDescription.OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testIsArray() {
        MatcherAssert.assertThat(isArray().matches(TypeDescription.VOID), CoreMatchers.is(false));
        MatcherAssert.assertThat(isArray().matches(of(int[].class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(isArray().matches(TypeDescription.OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testSupportsModules() throws Exception {
        MatcherAssert.assertThat(ElementMatchers.supportsModules().matches(Mockito.mock(JavaModule.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(ElementMatchers.supportsModules().matches(null), CoreMatchers.is(false));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testConstructorIsHidden() throws Exception {
        MatcherAssert.assertThat(Modifier.isPrivate(ElementMatchers.class.getDeclaredConstructor().getModifiers()), CoreMatchers.is(true));
        Constructor<?> constructor = ElementMatchers.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            Assert.fail();
        } catch (InvocationTargetException exception) {
            throw ((UnsupportedOperationException) (exception.getCause()));
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface IsAnnotatedWithAnnotation {}

    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    public @interface OtherAnnotation {}

    public interface GenericMethodType<T extends Exception> {
        T foo(T t) throws T;

        /* empty */
        interface Inner extends ElementMatchersTest.GenericMethodType<RuntimeException> {}
    }

    @SuppressWarnings("unused")
    public interface GenericDeclaredBy<T> {
        void foo();

        /* empty */
        interface Inner extends ElementMatchersTest.GenericDeclaredBy<String> {}
    }

    @SuppressWarnings("unused")
    public static class FieldSample {
        String foo;

        Object bar;

        volatile Object qux;

        transient Object baz;
    }

    private static class IsDeclaredBy {
        /* empty */
        static class Inner {}
    }

    /* empty */
    public static class IsVisibleTo {}

    /* empty */
    private static class IsNotVisibleTo {}

    @ElementMatchersTest.IsAnnotatedWithAnnotation
    private static class IsAnnotatedWith {}

    @SuppressWarnings("unused")
    private abstract static class IsEqual {
        abstract void foo();
    }

    @SuppressWarnings("unused")
    private abstract static class Returns {
        abstract void foo();

        abstract String bar();
    }

    @SuppressWarnings("unused")
    private abstract static class TakesArguments {
        abstract void foo(Void a);

        abstract void bar(String a, int b);
    }

    private abstract static class CanThrow {
        protected abstract void foo() throws IOException;

        protected abstract void bar();
    }

    public static class GenericType<T> {
        public void foo(T t) {
            /* empty */
        }

        public static class Extension extends ElementMatchersTest.GenericType<Void> {
            public void foo(Void t) {
                /* empty */
            }
        }
    }

    private static class ObjectMethods {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            return super.equals(other);
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        @SuppressWarnings("deprecation")
        protected void finalize() throws Throwable {
            super.finalize();
        }
    }

    private static class CloneMethods {
        @Override
        public ElementMatchersTest.CloneMethods clone() {
            return new ElementMatchersTest.CloneMethods();
        }

        public Object clone(int someArgument) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static class IsVirtual {
        public static void bar() {
            /* empty */
        }

        private void foo() {
            /* empty */
        }

        public final void qux() {
            /* empty */
        }

        public void baz() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    public static class Getters<T> {
        public void getFoo() {
            /* empty */
        }

        public Boolean isBar() {
            return null;
        }

        public boolean isQux() {
            return false;
        }

        public Boolean getBar() {
            return null;
        }

        public boolean getQux() {
            return false;
        }

        public String isBaz() {
            return null;
        }

        public String getBaz() {
            return null;
        }

        public String getBaz(Void argument) {
            return null;
        }

        public T getQuxbaz() {
            return null;
        }

        public Object get() {
            return null;
        }

        public boolean is() {
            return false;
        }
    }

    @SuppressWarnings("unused")
    public static class Setters<T> {
        public void setFoo() {
            /* empty */
        }

        public void setBar(boolean argument) {
            /* empty */
        }

        public void setQux(Boolean argument) {
            /* empty */
        }

        public void setBaz(String argument) {
            /* empty */
        }

        public void setBaz(String argument, Void argument2) {
            /* empty */
        }

        public void setQuxbaz(T argument) {
            /* empty */
        }

        public void set(Object argument) {
            /* empty */
        }
    }

    /* empty */
    @ElementMatchersTest.OtherAnnotation
    public static class Other {}

    /* empty */
    public static class OtherInherited extends ElementMatchersTest.Other {}

    @SuppressWarnings("unused")
    public static class DeclaresFieldOrMethod {
        @ElementMatchersTest.OtherAnnotation
        Void field;

        @ElementMatchersTest.OtherAnnotation
        void method() {
        }
    }

    @SuppressWarnings("unused")
    public static class GenericFieldType<T> {
        ElementMatchersTest.GenericFieldType<?> foo;

        T bar;

        /* empty */
        public static class Inner extends ElementMatchersTest.GenericFieldType<Void> {}
    }

    @SuppressWarnings("unused")
    public static class GenericConstructorType<T extends Exception> {
        GenericConstructorType(T t) throws T {
            /* empty */
        }

        public static class Inner extends ElementMatchersTest.GenericConstructorType<RuntimeException> {
            public Inner(RuntimeException exception) throws RuntimeException {
                super(exception);
            }
        }
    }
}

