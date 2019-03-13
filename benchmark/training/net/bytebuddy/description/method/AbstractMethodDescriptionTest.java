package net.bytebuddy.description.method;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.TypeVariableSource;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.test.packaging.MethodDescriptionTestHelper;
import net.bytebuddy.test.utility.JavaVersionRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static net.bytebuddy.description.type.TypeDefinition.Sort.describe;
import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.description.type.TypeDescription.Generic.VOID;


public abstract class AbstractMethodDescriptionTest {
    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    protected Method firstMethod;

    protected Method secondMethod;

    protected Method thirdMethod;

    protected Method genericMethod;

    protected Method genericMethodWithRawException;

    protected Method genericMethodWithTypeVariable;

    protected Constructor<?> firstConstructor;

    protected Constructor<?> secondConstructor;

    @Test
    public void testPrecondition() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(describe(secondMethod)));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(describe(thirdMethod)));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.is(describe(firstMethod)));
        MatcherAssert.assertThat(describe(secondMethod), CoreMatchers.is(describe(secondMethod)));
        MatcherAssert.assertThat(describe(thirdMethod), CoreMatchers.is(describe(thirdMethod)));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.is(((MethodDescription) (new MethodDescription.ForLoadedMethod(firstMethod)))));
        MatcherAssert.assertThat(describe(secondMethod), CoreMatchers.is(((MethodDescription) (new MethodDescription.ForLoadedMethod(secondMethod)))));
        MatcherAssert.assertThat(describe(thirdMethod), CoreMatchers.is(((MethodDescription) (new MethodDescription.ForLoadedMethod(thirdMethod)))));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(describe(secondConstructor)));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.is(describe(firstConstructor)));
        MatcherAssert.assertThat(describe(secondConstructor), CoreMatchers.is(describe(secondConstructor)));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.is(((MethodDescription) (new MethodDescription.ForLoadedConstructor(firstConstructor)))));
        MatcherAssert.assertThat(describe(secondConstructor), CoreMatchers.is(((MethodDescription) (new MethodDescription.ForLoadedConstructor(secondConstructor)))));
    }

    @Test
    public void testReturnType() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).getReturnType(), CoreMatchers.is(((TypeDefinition) (of(firstMethod.getReturnType())))));
        MatcherAssert.assertThat(describe(secondMethod).getReturnType(), CoreMatchers.is(((TypeDefinition) (of(secondMethod.getReturnType())))));
        MatcherAssert.assertThat(describe(thirdMethod).getReturnType(), CoreMatchers.is(((TypeDefinition) (of(thirdMethod.getReturnType())))));
        MatcherAssert.assertThat(describe(firstConstructor).getReturnType(), CoreMatchers.is(VOID));
        MatcherAssert.assertThat(describe(secondConstructor).getReturnType(), CoreMatchers.is(VOID));
    }

    @Test
    public void testParameterTypes() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).getParameters().asTypeList(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(firstMethod.getParameterTypes())))));
        MatcherAssert.assertThat(describe(secondMethod).getParameters().asTypeList(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(secondMethod.getParameterTypes())))));
        MatcherAssert.assertThat(describe(thirdMethod).getParameters().asTypeList(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(thirdMethod.getParameterTypes())))));
        MatcherAssert.assertThat(describe(firstConstructor).getParameters().asTypeList(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(firstConstructor.getParameterTypes())))));
        MatcherAssert.assertThat(describe(secondConstructor).getParameters().asTypeList(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(secondConstructor.getParameterTypes())))));
    }

    @Test
    public void testMethodName() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).getName(), CoreMatchers.is(firstMethod.getName()));
        MatcherAssert.assertThat(describe(secondMethod).getName(), CoreMatchers.is(secondMethod.getName()));
        MatcherAssert.assertThat(describe(thirdMethod).getName(), CoreMatchers.is(thirdMethod.getName()));
        MatcherAssert.assertThat(describe(firstConstructor).getName(), CoreMatchers.is(firstConstructor.getDeclaringClass().getName()));
        MatcherAssert.assertThat(describe(secondConstructor).getName(), CoreMatchers.is(secondConstructor.getDeclaringClass().getName()));
        MatcherAssert.assertThat(describe(firstMethod).getInternalName(), CoreMatchers.is(firstMethod.getName()));
        MatcherAssert.assertThat(describe(secondMethod).getInternalName(), CoreMatchers.is(secondMethod.getName()));
        MatcherAssert.assertThat(describe(thirdMethod).getInternalName(), CoreMatchers.is(thirdMethod.getName()));
        MatcherAssert.assertThat(describe(firstConstructor).getInternalName(), CoreMatchers.is(MethodDescription.CONSTRUCTOR_INTERNAL_NAME));
        MatcherAssert.assertThat(describe(secondConstructor).getInternalName(), CoreMatchers.is(MethodDescription.CONSTRUCTOR_INTERNAL_NAME));
    }

    @Test
    public void testDescriptor() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).getDescriptor(), CoreMatchers.is(Type.getMethodDescriptor(firstMethod)));
        MatcherAssert.assertThat(describe(secondMethod).getDescriptor(), CoreMatchers.is(Type.getMethodDescriptor(secondMethod)));
        MatcherAssert.assertThat(describe(thirdMethod).getDescriptor(), CoreMatchers.is(Type.getMethodDescriptor(thirdMethod)));
        MatcherAssert.assertThat(describe(firstConstructor).getDescriptor(), CoreMatchers.is(Type.getConstructorDescriptor(firstConstructor)));
        MatcherAssert.assertThat(describe(secondConstructor).getDescriptor(), CoreMatchers.is(Type.getConstructorDescriptor(secondConstructor)));
    }

    @Test
    public void testMethodModifiers() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).getModifiers(), CoreMatchers.is(firstMethod.getModifiers()));
        MatcherAssert.assertThat(describe(secondMethod).getModifiers(), CoreMatchers.is(secondMethod.getModifiers()));
        MatcherAssert.assertThat(describe(thirdMethod).getModifiers(), CoreMatchers.is(thirdMethod.getModifiers()));
        MatcherAssert.assertThat(describe(firstConstructor).getModifiers(), CoreMatchers.is(firstConstructor.getModifiers()));
        MatcherAssert.assertThat(describe(secondConstructor).getModifiers(), CoreMatchers.is(secondConstructor.getModifiers()));
    }

    @Test
    public void testMethodDeclaringType() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).getDeclaringType(), CoreMatchers.is(((TypeDescription) (of(firstMethod.getDeclaringClass())))));
        MatcherAssert.assertThat(describe(secondMethod).getDeclaringType(), CoreMatchers.is(((TypeDescription) (of(secondMethod.getDeclaringClass())))));
        MatcherAssert.assertThat(describe(thirdMethod).getDeclaringType(), CoreMatchers.is(((TypeDescription) (of(thirdMethod.getDeclaringClass())))));
        MatcherAssert.assertThat(describe(firstConstructor).getDeclaringType(), CoreMatchers.is(((TypeDescription) (of(firstConstructor.getDeclaringClass())))));
        MatcherAssert.assertThat(describe(secondConstructor).getDeclaringType(), CoreMatchers.is(((TypeDescription) (of(secondConstructor.getDeclaringClass())))));
    }

    @Test
    public void testHashCode() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).hashCode(), CoreMatchers.is(AbstractMethodDescriptionTest.hashCode(firstMethod)));
        MatcherAssert.assertThat(describe(secondMethod).hashCode(), CoreMatchers.is(AbstractMethodDescriptionTest.hashCode(secondMethod)));
        MatcherAssert.assertThat(describe(thirdMethod).hashCode(), CoreMatchers.is(AbstractMethodDescriptionTest.hashCode(thirdMethod)));
        MatcherAssert.assertThat(describe(firstMethod).hashCode(), CoreMatchers.not(AbstractMethodDescriptionTest.hashCode(secondMethod)));
        MatcherAssert.assertThat(describe(firstMethod).hashCode(), CoreMatchers.not(AbstractMethodDescriptionTest.hashCode(thirdMethod)));
        MatcherAssert.assertThat(describe(firstMethod).hashCode(), CoreMatchers.not(AbstractMethodDescriptionTest.hashCode(firstConstructor)));
        MatcherAssert.assertThat(describe(firstMethod).hashCode(), CoreMatchers.not(AbstractMethodDescriptionTest.hashCode(secondConstructor)));
        MatcherAssert.assertThat(describe(firstConstructor).hashCode(), CoreMatchers.is(AbstractMethodDescriptionTest.hashCode(firstConstructor)));
        MatcherAssert.assertThat(describe(secondConstructor).hashCode(), CoreMatchers.is(AbstractMethodDescriptionTest.hashCode(secondConstructor)));
        MatcherAssert.assertThat(describe(firstConstructor).hashCode(), CoreMatchers.not(AbstractMethodDescriptionTest.hashCode(firstMethod)));
        MatcherAssert.assertThat(describe(firstConstructor).hashCode(), CoreMatchers.not(AbstractMethodDescriptionTest.hashCode(secondMethod)));
        MatcherAssert.assertThat(describe(firstConstructor).hashCode(), CoreMatchers.not(AbstractMethodDescriptionTest.hashCode(thirdMethod)));
        MatcherAssert.assertThat(describe(firstConstructor).hashCode(), CoreMatchers.not(AbstractMethodDescriptionTest.hashCode(secondConstructor)));
    }

    @Test
    public void testEqualsMethod() throws Exception {
        MethodDescription identical = describe(firstMethod);
        MatcherAssert.assertThat(identical, CoreMatchers.is(identical));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.is(describe(firstMethod)));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(describe(secondMethod)));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(describe(thirdMethod)));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(describe(firstConstructor)));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(describe(secondConstructor)));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.is(((MethodDescription) (new MethodDescription.ForLoadedMethod(firstMethod)))));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(((MethodDescription) (new MethodDescription.ForLoadedMethod(secondMethod)))));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(((MethodDescription) (new MethodDescription.ForLoadedMethod(thirdMethod)))));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(((MethodDescription) (new MethodDescription.ForLoadedConstructor(firstConstructor)))));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(((MethodDescription) (new MethodDescription.ForLoadedConstructor(secondConstructor)))));
        MethodDescription.InDefinedShape equalMethod = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(equalMethod.getInternalName()).thenReturn(firstMethod.getName());
        Mockito.when(equalMethod.getDeclaringType()).thenReturn(of(firstMethod.getDeclaringClass()));
        Mockito.when(equalMethod.getReturnType()).thenReturn(of(firstMethod.getReturnType()));
        Mockito.when(equalMethod.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(equalMethod, new TypeList.Generic.ForLoadedTypes(firstMethod.getParameterTypes())));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.is(equalMethod));
        MethodDescription.InDefinedShape equalMethodButName = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(equalMethodButName.getInternalName()).thenReturn(secondMethod.getName());
        Mockito.when(equalMethodButName.getDeclaringType()).thenReturn(of(firstMethod.getDeclaringClass()));
        Mockito.when(equalMethodButName.getReturnType()).thenReturn(of(firstMethod.getReturnType()));
        Mockito.when(equalMethodButName.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(equalMethodButName, new TypeList.Generic.ForLoadedTypes(firstMethod.getParameterTypes())));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(equalMethodButName));
        MethodDescription.InDefinedShape equalMethodButReturnType = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(equalMethodButReturnType.getInternalName()).thenReturn(firstMethod.getName());
        Mockito.when(equalMethodButReturnType.getDeclaringType()).thenReturn(TypeDescription.OBJECT);
        Mockito.when(equalMethodButReturnType.getReturnType()).thenReturn(of(firstMethod.getReturnType()));
        Mockito.when(equalMethodButReturnType.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(equalMethodButReturnType, new TypeList.Generic.ForLoadedTypes(firstMethod.getParameterTypes())));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(equalMethodButReturnType));
        MethodDescription.InDefinedShape equalMethodButDeclaringType = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(equalMethodButDeclaringType.getInternalName()).thenReturn(firstMethod.getName());
        Mockito.when(equalMethodButDeclaringType.getDeclaringType()).thenReturn(of(firstMethod.getDeclaringClass()));
        Mockito.when(equalMethodButDeclaringType.getReturnType()).thenReturn(of(secondMethod.getReturnType()));
        Mockito.when(equalMethodButDeclaringType.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(equalMethodButDeclaringType, new TypeList.Generic.ForLoadedTypes(firstMethod.getParameterTypes())));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(equalMethodButDeclaringType));
        MethodDescription.InDefinedShape equalMethodButParameterTypes = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(equalMethodButParameterTypes.getInternalName()).thenReturn(firstMethod.getName());
        Mockito.when(equalMethodButParameterTypes.getDeclaringType()).thenReturn(of(firstMethod.getDeclaringClass()));
        Mockito.when(equalMethodButParameterTypes.getReturnType()).thenReturn(of(firstMethod.getReturnType()));
        Mockito.when(equalMethodButParameterTypes.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(equalMethodButParameterTypes, new TypeList.Generic.ForLoadedTypes(secondMethod.getParameterTypes())));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(equalMethodButParameterTypes));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(describe(firstMethod), CoreMatchers.not(CoreMatchers.equalTo(null)));
    }

    @Test
    public void testEqualsConstructor() throws Exception {
        MethodDescription identical = describe(firstConstructor);
        MatcherAssert.assertThat(identical, CoreMatchers.is(identical));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.is(describe(firstConstructor)));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(describe(secondConstructor)));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(describe(firstMethod)));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(describe(secondMethod)));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(describe(thirdMethod)));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.is(((MethodDescription) (new MethodDescription.ForLoadedConstructor(firstConstructor)))));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(((MethodDescription) (new MethodDescription.ForLoadedConstructor(secondConstructor)))));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(((MethodDescription) (new MethodDescription.ForLoadedMethod(firstMethod)))));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(((MethodDescription) (new MethodDescription.ForLoadedMethod(secondMethod)))));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(((MethodDescription) (new MethodDescription.ForLoadedMethod(thirdMethod)))));
        MethodDescription.InDefinedShape equalMethod = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(equalMethod.getInternalName()).thenReturn(MethodDescription.CONSTRUCTOR_INTERNAL_NAME);
        Mockito.when(equalMethod.getDeclaringType()).thenReturn(of(firstConstructor.getDeclaringClass()));
        Mockito.when(equalMethod.getReturnType()).thenReturn(VOID);
        Mockito.when(equalMethod.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(equalMethod, new TypeList.ForLoadedTypes(firstConstructor.getParameterTypes())));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.is(equalMethod));
        MethodDescription.InDefinedShape equalMethodButName = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(equalMethodButName.getInternalName()).thenReturn(firstMethod.getName());
        Mockito.when(equalMethodButName.getDeclaringType()).thenReturn(of(firstConstructor.getDeclaringClass()));
        Mockito.when(equalMethodButName.getReturnType()).thenReturn(VOID);
        Mockito.when(equalMethodButName.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(equalMethodButName, new TypeList.ForLoadedTypes(firstConstructor.getParameterTypes())));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(equalMethodButName));
        MethodDescription.InDefinedShape equalMethodButReturnType = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(equalMethodButReturnType.getInternalName()).thenReturn(MethodDescription.CONSTRUCTOR_INTERNAL_NAME);
        Mockito.when(equalMethodButReturnType.getDeclaringType()).thenReturn(TypeDescription.OBJECT);
        Mockito.when(equalMethodButReturnType.getReturnType()).thenReturn(OBJECT);
        Mockito.when(equalMethodButReturnType.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(equalMethodButReturnType, new TypeList.ForLoadedTypes(firstConstructor.getParameterTypes())));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(equalMethodButReturnType));
        MethodDescription.InDefinedShape equalMethodButDeclaringType = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(equalMethodButDeclaringType.getInternalName()).thenReturn(MethodDescription.CONSTRUCTOR_INTERNAL_NAME);
        Mockito.when(equalMethodButDeclaringType.getDeclaringType()).thenReturn(TypeDescription.OBJECT);
        Mockito.when(equalMethodButDeclaringType.getReturnType()).thenReturn(VOID);
        Mockito.when(equalMethodButDeclaringType.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(equalMethodButDeclaringType, new TypeList.ForLoadedTypes(firstConstructor.getParameterTypes())));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(equalMethodButDeclaringType));
        MethodDescription.InDefinedShape equalMethodButParameterTypes = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(equalMethodButParameterTypes.getInternalName()).thenReturn(MethodDescription.CONSTRUCTOR_INTERNAL_NAME);
        Mockito.when(equalMethodButParameterTypes.getDeclaringType()).thenReturn(of(firstConstructor.getDeclaringClass()));
        Mockito.when(equalMethodButParameterTypes.getReturnType()).thenReturn(VOID);
        Mockito.when(equalMethodButParameterTypes.getParameters()).thenReturn(new ParameterList.Explicit.ForTypes(equalMethodButParameterTypes, new TypeList.ForLoadedTypes(secondConstructor.getParameterTypes())));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(equalMethodButParameterTypes));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(describe(firstConstructor), CoreMatchers.not(CoreMatchers.equalTo(null)));
    }

    @Test
    public void testToString() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).toString(), CoreMatchers.is(firstMethod.toString()));
        MatcherAssert.assertThat(describe(secondMethod).toString(), CoreMatchers.is(secondMethod.toString()));
        MatcherAssert.assertThat(describe(thirdMethod).toString(), CoreMatchers.is(thirdMethod.toString()));
        MatcherAssert.assertThat(describe(firstConstructor).toString(), CoreMatchers.is(firstConstructor.toString()));
        MatcherAssert.assertThat(describe(secondConstructor).toString(), CoreMatchers.is(secondConstructor.toString()));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testEqualsParameter() throws Exception {
        ParameterDescription identical = describe(secondMethod).getParameters().get(0);
        MatcherAssert.assertThat(identical, CoreMatchers.is(identical));
        MatcherAssert.assertThat(identical, CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(identical, CoreMatchers.not(CoreMatchers.equalTo(null)));
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(0), CoreMatchers.is(describe(secondMethod).getParameters().get(0)));
        ParameterDescription equal = Mockito.mock(ParameterDescription.class);
        Mockito.when(equal.getDeclaringMethod()).thenReturn(describe(secondMethod));
        Mockito.when(equal.getIndex()).thenReturn(0);
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(0), CoreMatchers.is(equal));
        ParameterDescription notEqualMethod = Mockito.mock(ParameterDescription.class);
        Mockito.when(equal.getDeclaringMethod()).thenReturn(Mockito.mock(MethodDescription.class));
        Mockito.when(equal.getIndex()).thenReturn(0);
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(0), CoreMatchers.not(notEqualMethod));
        ParameterDescription notEqualMethodIndex = Mockito.mock(ParameterDescription.class);
        Mockito.when(equal.getDeclaringMethod()).thenReturn(describe(secondMethod));
        Mockito.when(equal.getIndex()).thenReturn(1);
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(0), CoreMatchers.not(notEqualMethodIndex));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testHashCodeParameter() throws Exception {
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(0).hashCode(), CoreMatchers.is(hashCode(secondMethod, 0)));
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(1).hashCode(), CoreMatchers.is(hashCode(secondMethod, 1)));
        MatcherAssert.assertThat(describe(firstConstructor).getParameters().get(0).hashCode(), CoreMatchers.is(hashCode(firstConstructor, 0)));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testToStringParameter() throws Exception {
        Class<?> executable = Class.forName("java.lang.reflect.Executable");
        Method method = executable.getDeclaredMethod("getParameters");
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(0).toString(), CoreMatchers.is(((Object[]) (method.invoke(secondMethod)))[0].toString()));
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(1).toString(), CoreMatchers.is(((Object[]) (method.invoke(secondMethod)))[1].toString()));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testParameterNameAndModifiers() throws Exception {
        Class<?> type = Class.forName("net.bytebuddy.test.precompiled.ParameterNames");
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("foo", String.class, long.class, int.class)).getParameters().get(0).isNamed(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("foo", String.class, long.class, int.class)).getParameters().get(1).isNamed(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("foo", String.class, long.class, int.class)).getParameters().get(2).isNamed(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("foo", String.class, long.class, int.class)).getParameters().get(0).getName(), CoreMatchers.is("first"));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("foo", String.class, long.class, int.class)).getParameters().get(1).getName(), CoreMatchers.is("second"));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("foo", String.class, long.class, int.class)).getParameters().get(2).getName(), CoreMatchers.is("third"));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("foo", String.class, long.class, int.class)).getParameters().get(0).getModifiers(), CoreMatchers.is(Opcodes.ACC_FINAL));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("foo", String.class, long.class, int.class)).getParameters().get(1).getModifiers(), CoreMatchers.is(0));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("foo", String.class, long.class, int.class)).getParameters().get(2).getModifiers(), CoreMatchers.is(0));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("bar", String.class, long.class, int.class)).getParameters().get(0).isNamed(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("bar", String.class, long.class, int.class)).getParameters().get(1).isNamed(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("bar", String.class, long.class, int.class)).getParameters().get(2).isNamed(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("bar", String.class, long.class, int.class)).getParameters().get(0).getName(), CoreMatchers.is("first"));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("bar", String.class, long.class, int.class)).getParameters().get(1).getName(), CoreMatchers.is("second"));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("bar", String.class, long.class, int.class)).getParameters().get(2).getName(), CoreMatchers.is("third"));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("bar", String.class, long.class, int.class)).getParameters().get(0).getModifiers(), CoreMatchers.is(0));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("bar", String.class, long.class, int.class)).getParameters().get(1).getModifiers(), CoreMatchers.is(Opcodes.ACC_FINAL));
        MatcherAssert.assertThat(describe(type.getDeclaredMethod("bar", String.class, long.class, int.class)).getParameters().get(2).getModifiers(), CoreMatchers.is(0));
        MatcherAssert.assertThat(describe(type.getDeclaredConstructor(String.class, int.class)).getParameters().get(0).isNamed(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(type.getDeclaredConstructor(String.class, int.class)).getParameters().get(1).isNamed(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(type.getDeclaredConstructor(String.class, int.class)).getParameters().get(0).getName(), CoreMatchers.is("first"));
        MatcherAssert.assertThat(describe(type.getDeclaredConstructor(String.class, int.class)).getParameters().get(1).getName(), CoreMatchers.is("second"));
        MatcherAssert.assertThat(describe(type.getDeclaredConstructor(String.class, int.class)).getParameters().get(0).getModifiers(), CoreMatchers.is(0));
        MatcherAssert.assertThat(describe(type.getDeclaredConstructor(String.class, int.class)).getParameters().get(1).getModifiers(), CoreMatchers.is(Opcodes.ACC_FINAL));
    }

    @Test
    public void testNoParameterNameAndModifiers() throws Exception {
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(0).isNamed(), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(1).isNamed(), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(0).getName(), CoreMatchers.is("arg0"));
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(1).getName(), CoreMatchers.is("arg1"));
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(0).getModifiers(), CoreMatchers.is(0));
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(1).getModifiers(), CoreMatchers.is(0));
        MatcherAssert.assertThat(describe(firstConstructor).getParameters().get(0).isNamed(), CoreMatchers.is(canReadDebugInformation()));
        MatcherAssert.assertThat(describe(firstConstructor).getParameters().get(0).getName(), CoreMatchers.is((canReadDebugInformation() ? "argument" : "arg0")));
        MatcherAssert.assertThat(describe(firstConstructor).getParameters().get(0).getModifiers(), CoreMatchers.is(0));
    }

    @Test
    public void testSynthetic() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).isSynthetic(), CoreMatchers.is(firstMethod.isSynthetic()));
        MatcherAssert.assertThat(describe(secondMethod).isSynthetic(), CoreMatchers.is(secondMethod.isSynthetic()));
        MatcherAssert.assertThat(describe(thirdMethod).isSynthetic(), CoreMatchers.is(thirdMethod.isSynthetic()));
        MatcherAssert.assertThat(describe(firstConstructor).isSynthetic(), CoreMatchers.is(firstConstructor.isSynthetic()));
        MatcherAssert.assertThat(describe(secondConstructor).isSynthetic(), CoreMatchers.is(secondConstructor.isSynthetic()));
    }

    @Test
    public void testType() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).isMethod(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(firstMethod).isConstructor(), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(firstMethod).isTypeInitializer(), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(firstConstructor).isMethod(), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(firstConstructor).isConstructor(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(firstConstructor).isTypeInitializer(), CoreMatchers.is(false));
    }

    @Test
    public void testMethodIsVisibleTo() throws Exception {
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("publicMethod")).isVisibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("protectedMethod")).isVisibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("packagePrivateMethod")).isVisibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("privateMethod")).isVisibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("publicMethod")).isVisibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("protectedMethod")).isVisibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("packagePrivateMethod")).isVisibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("privateMethod")).isVisibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(ClassFileVersion.of(AbstractMethodDescriptionTest.Sample.class).isAtLeast(JAVA_V11)));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("publicMethod")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("protectedMethod")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("packagePrivateMethod")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("privateMethod")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("publicMethod")).isVisibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("protectedMethod")).isVisibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("packagePrivateMethod")).isVisibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("privateMethod")).isVisibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("publicMethod")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("protectedMethod")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("packagePrivateMethod")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("privateMethod")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("staticPublicMethod")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("staticProtectedMethod")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("staticPackagePrivateMethod")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("staticPrivateMethod")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testConstructorIsVisibleTo() throws Exception {
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor()).isVisibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Void.class)).isVisibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Object.class)).isVisibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(String.class)).isVisibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor()).isVisibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Void.class)).isVisibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Object.class)).isVisibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(String.class)).isVisibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(ClassFileVersion.of(AbstractMethodDescriptionTest.Sample.class).isAtLeast(JAVA_V11)));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor()).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Void.class)).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Object.class)).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(String.class)).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor()).isVisibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Void.class)).isVisibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Object.class)).isVisibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(String.class)).isVisibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredConstructor()).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredConstructor(Void.class)).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredConstructor(Object.class)).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredConstructor(String.class)).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.MethodVisibilityType.class.getDeclaredMethod("packagePrivateArgument", AbstractMethodDescriptionTest.PackagePrivateType.class)).isVisibleTo(of(AbstractMethodDescriptionTest.MethodVisibilityType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.MethodVisibilityType.class.getDeclaredMethod("packagePrivateReturnType")).isVisibleTo(of(AbstractMethodDescriptionTest.MethodVisibilityType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.MethodVisibilityType.class.getDeclaredMethod("packagePrivateArgument", AbstractMethodDescriptionTest.PackagePrivateType.class)).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.MethodVisibilityType.class.getDeclaredMethod("packagePrivateReturnType")).isVisibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
    }

    @Test
    public void testMethodIsAccessibleTo() throws Exception {
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("publicMethod")).isAccessibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("protectedMethod")).isAccessibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("packagePrivateMethod")).isAccessibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("privateMethod")).isAccessibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("publicMethod")).isAccessibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("protectedMethod")).isAccessibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("packagePrivateMethod")).isAccessibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("privateMethod")).isAccessibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(ClassFileVersion.of(AbstractMethodDescriptionTest.PublicType.class).isAtLeast(JAVA_V11)));// introduction of nest mates

        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("publicMethod")).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("protectedMethod")).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("packagePrivateMethod")).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("privateMethod")).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("publicMethod")).isAccessibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("protectedMethod")).isAccessibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("packagePrivateMethod")).isAccessibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredMethod("privateMethod")).isAccessibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("publicMethod")).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("protectedMethod")).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("packagePrivateMethod")).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("privateMethod")).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("staticPublicMethod")).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("staticProtectedMethod")).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("staticPackagePrivateMethod")).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredMethod("staticPrivateMethod")).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testConstructorIsAccessibleTo() throws Exception {
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor()).isAccessibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Void.class)).isAccessibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Object.class)).isAccessibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(String.class)).isAccessibleTo(of(AbstractMethodDescriptionTest.PublicType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor()).isAccessibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Void.class)).isAccessibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Object.class)).isAccessibleTo(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(String.class)).isAccessibleTo(of(AbstractMethodDescriptionTestNoNestMate.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor()).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Void.class)).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Object.class)).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(String.class)).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor()).isAccessibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Void.class)).isAccessibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(Object.class)).isAccessibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PublicType.class.getDeclaredConstructor(String.class)).isAccessibleTo(of(MethodDescriptionTestHelper.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredConstructor()).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredConstructor(Void.class)).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredConstructor(Object.class)).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.PackagePrivateType.class.getDeclaredConstructor(String.class)).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.MethodVisibilityType.class.getDeclaredMethod("packagePrivateArgument", AbstractMethodDescriptionTest.PackagePrivateType.class)).isAccessibleTo(of(AbstractMethodDescriptionTest.MethodVisibilityType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.MethodVisibilityType.class.getDeclaredMethod("packagePrivateReturnType")).isAccessibleTo(of(AbstractMethodDescriptionTest.MethodVisibilityType.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.MethodVisibilityType.class.getDeclaredMethod("packagePrivateArgument", AbstractMethodDescriptionTest.PackagePrivateType.class)).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.MethodVisibilityType.class.getDeclaredMethod("packagePrivateReturnType")).isAccessibleTo(TypeDescription.OBJECT), CoreMatchers.is(true));
    }

    @Test
    public void testExceptions() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).getExceptionTypes(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(firstMethod.getExceptionTypes())))));
        MatcherAssert.assertThat(describe(secondMethod).getExceptionTypes(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(secondMethod.getExceptionTypes())))));
        MatcherAssert.assertThat(describe(thirdMethod).getExceptionTypes(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(thirdMethod.getExceptionTypes())))));
        MatcherAssert.assertThat(describe(firstConstructor).getExceptionTypes(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(firstConstructor.getExceptionTypes())))));
        MatcherAssert.assertThat(describe(secondConstructor).getExceptionTypes(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(secondConstructor.getExceptionTypes())))));
    }

    @Test
    public void testAnnotations() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.Empty()))));
        MatcherAssert.assertThat(describe(secondMethod).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.Empty()))));
        MatcherAssert.assertThat(describe(thirdMethod).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.ForLoadedAnnotations(thirdMethod.getDeclaredAnnotations())))));
        MatcherAssert.assertThat(describe(firstConstructor).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.Empty()))));
        MatcherAssert.assertThat(describe(secondConstructor).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.ForLoadedAnnotations(secondConstructor.getDeclaredAnnotations())))));
    }

    @Test
    public void testParameterAnnotations() throws Exception {
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(0).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.Empty()))));
        MatcherAssert.assertThat(describe(secondMethod).getParameters().get(1).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.Empty()))));
        MatcherAssert.assertThat(describe(thirdMethod).getParameters().get(0).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.ForLoadedAnnotations(thirdMethod.getParameterAnnotations()[0])))));
        MatcherAssert.assertThat(describe(thirdMethod).getParameters().get(1).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.ForLoadedAnnotations(thirdMethod.getParameterAnnotations()[1])))));
        MatcherAssert.assertThat(describe(firstConstructor).getParameters().get(0).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.Empty()))));
        MatcherAssert.assertThat(describe(secondConstructor).getParameters().get(0).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.ForLoadedAnnotations(secondConstructor.getParameterAnnotations()[0])))));
        MatcherAssert.assertThat(describe(secondConstructor).getParameters().get(1).getDeclaredAnnotations(), CoreMatchers.is(((AnnotationList) (new AnnotationList.ForLoadedAnnotations(secondConstructor.getParameterAnnotations()[1])))));
    }

    @Test
    public void testRepresents() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).represents(firstMethod), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(firstMethod).represents(secondMethod), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(firstMethod).represents(thirdMethod), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(firstMethod).represents(firstConstructor), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(firstMethod).represents(secondConstructor), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(firstConstructor).represents(firstConstructor), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(firstConstructor).represents(secondConstructor), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(firstConstructor).represents(firstMethod), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(firstConstructor).represents(secondMethod), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(firstConstructor).represents(thirdMethod), CoreMatchers.is(false));
    }

    @Test
    public void testSpecializable() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).isSpecializableFor(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(secondMethod).isSpecializableFor(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(thirdMethod).isSpecializableFor(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(thirdMethod).isSpecializableFor(of(AbstractMethodDescriptionTest.SampleSub.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(thirdMethod).isSpecializableFor(TypeDescription.OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(firstConstructor).isSpecializableFor(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(firstConstructor).isSpecializableFor(of(AbstractMethodDescriptionTest.SampleSub.class)), CoreMatchers.is(false));
    }

    @Test
    public void testInvokable() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).isInvokableOn(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(secondMethod).isInvokableOn(of(AbstractMethodDescriptionTest.Sample.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(secondMethod).isInvokableOn(of(AbstractMethodDescriptionTest.SampleSub.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(secondMethod).isInvokableOn(TypeDescription.OBJECT), CoreMatchers.is(false));
    }

    @Test
    public void testGenericTypes() throws Exception {
        MatcherAssert.assertThat(describe(genericMethod).getReturnType(), CoreMatchers.is(describe(genericMethod.getGenericReturnType())));
        MatcherAssert.assertThat(describe(genericMethod).getParameters().asTypeList(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(genericMethod.getGenericParameterTypes())))));
        MatcherAssert.assertThat(describe(genericMethod).getExceptionTypes(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(genericMethod.getGenericExceptionTypes())))));
    }

    @Test
    public void testGenericTypesOfMethodWithoutException() throws Exception {
        MatcherAssert.assertThat(describe(genericMethodWithRawException).getReturnType(), CoreMatchers.is(describe(genericMethodWithRawException.getGenericReturnType())));
        MatcherAssert.assertThat(describe(genericMethodWithRawException).getParameters().asTypeList(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(genericMethodWithRawException.getGenericParameterTypes())))));
        MatcherAssert.assertThat(describe(genericMethodWithRawException).getExceptionTypes(), CoreMatchers.is(((TypeList.Generic) (new TypeList.Generic.ForLoadedTypes(genericMethodWithRawException.getGenericExceptionTypes())))));
    }

    @Test
    public void testToGenericString() throws Exception {
        MatcherAssert.assertThat(describe(genericMethod).toGenericString(), CoreMatchers.is(genericMethod.toGenericString()));
    }

    @Test
    public void testEnclosingSource() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).getEnclosingSource(), CoreMatchers.nullValue(TypeVariableSource.class));
        MatcherAssert.assertThat(describe(secondMethod).getEnclosingSource(), CoreMatchers.is(((TypeVariableSource) (of(AbstractMethodDescriptionTest.Sample.class)))));
    }

    @Test
    public void testIsGenerified() throws Exception {
        MatcherAssert.assertThat(describe(genericMethodWithTypeVariable).isGenerified(), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(firstMethod).isGenerified(), CoreMatchers.is(false));
    }

    @Test
    public void testImplicitReceiverTypes() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).getReceiverType(), CoreMatchers.nullValue(TypeDescription.Generic.class));
        MatcherAssert.assertThat(describe(secondMethod).getReceiverType(), CoreMatchers.is(describe(AbstractMethodDescriptionTest.Sample.class)));
        MatcherAssert.assertThat(describe(firstConstructor).getReceiverType(), CoreMatchers.is(describe(AbstractMethodDescriptionTest.class)));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.class.getDeclaredConstructor()).getReceiverType(), CoreMatchers.is(describe(AbstractMethodDescriptionTest.class)));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testGetActualModifiers() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).getActualModifiers(), CoreMatchers.is(((Opcodes.ACC_PRIVATE) | (Opcodes.ACC_STATIC))));
        MatcherAssert.assertThat(describe(firstMethod).getActualModifiers(true), CoreMatchers.is(((Opcodes.ACC_PRIVATE) | (Opcodes.ACC_STATIC))));
        MatcherAssert.assertThat(describe(firstMethod).getActualModifiers(false), CoreMatchers.is((((Opcodes.ACC_PRIVATE) | (Opcodes.ACC_STATIC)) | (Opcodes.ACC_ABSTRACT))));
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.DeprecationSample.class.getDeclaredMethod("foo")).getActualModifiers(), CoreMatchers.is(((Opcodes.ACC_PRIVATE) | (Opcodes.ACC_DEPRECATED))));
        MatcherAssert.assertThat(describe(firstMethod).getActualModifiers(true, Visibility.PUBLIC), CoreMatchers.is(((Opcodes.ACC_PUBLIC) | (Opcodes.ACC_STATIC))));
        MatcherAssert.assertThat(describe(secondMethod).getActualModifiers(false, Visibility.PRIVATE), CoreMatchers.is(((Opcodes.ACC_PROTECTED) | (Opcodes.ACC_ABSTRACT))));
    }

    @Test
    public void testBridgeCompatible() throws Exception {
        MatcherAssert.assertThat(describe(firstMethod).isBridgeCompatible(new MethodDescription.TypeToken(TypeDescription.VOID, Collections.<TypeDescription>emptyList())), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(firstMethod).isBridgeCompatible(new MethodDescription.TypeToken(TypeDescription.VOID, Collections.singletonList(TypeDescription.OBJECT))), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(firstMethod).isBridgeCompatible(new MethodDescription.TypeToken(TypeDescription.OBJECT, Collections.<TypeDescription>emptyList())), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(firstMethod).isBridgeCompatible(new MethodDescription.TypeToken(of(int.class), Collections.<TypeDescription>emptyList())), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(secondMethod).isBridgeCompatible(new MethodDescription.TypeToken(TypeDescription.OBJECT, Arrays.asList(of(String.class), of(long.class)))), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(secondMethod).isBridgeCompatible(new MethodDescription.TypeToken(TypeDescription.OBJECT, Arrays.asList(of(Object.class), of(long.class)))), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(secondMethod).isBridgeCompatible(new MethodDescription.TypeToken(of(String.class), Arrays.asList(of(Object.class), of(long.class)))), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(secondMethod).isBridgeCompatible(new MethodDescription.TypeToken(TypeDescription.VOID, Arrays.asList(of(String.class), of(long.class)))), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(secondMethod).isBridgeCompatible(new MethodDescription.TypeToken(TypeDescription.OBJECT, Arrays.asList(of(int.class), of(long.class)))), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(secondMethod).isBridgeCompatible(new MethodDescription.TypeToken(TypeDescription.OBJECT, Arrays.asList(of(String.class), of(Object.class)))), CoreMatchers.is(false));
        MatcherAssert.assertThat(describe(secondMethod).isBridgeCompatible(new MethodDescription.TypeToken(TypeDescription.OBJECT, Arrays.asList(of(String.class), of(int.class)))), CoreMatchers.is(false));
    }

    @Test
    public void testSyntethicParameter() throws Exception {
        MatcherAssert.assertThat(describe(AbstractMethodDescriptionTest.SyntheticParameter.class.getDeclaredConstructor(AbstractMethodDescriptionTest.class, Void.class)).getParameters().get(1).getDeclaredAnnotations().isAnnotationPresent(AbstractMethodDescriptionTest.SyntheticMarker.class), CoreMatchers.is(true));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsDefault() throws Exception {
        Map<String, AnnotationValue<?, ?>> properties = new LinkedHashMap<String, AnnotationValue<?, ?>>();
        properties.put("boolean_property", AnnotationValue.ForConstant.of(true));
        properties.put("boolean_property_array", AnnotationValue.ForConstant.of(new boolean[]{ true }));
        properties.put("byte_property", AnnotationValue.ForConstant.of(((byte) (0))));
        properties.put("byte_property_array", AnnotationValue.ForConstant.of(new byte[]{ 0 }));
        properties.put("short_property", AnnotationValue.ForConstant.of(((short) (0))));
        properties.put("short_property_array", AnnotationValue.ForConstant.of(new short[]{ 0 }));
        properties.put("int_property", AnnotationValue.ForConstant.of(0));
        properties.put("int_property_array", AnnotationValue.ForConstant.of(new int[]{ 0 }));
        properties.put("long_property", AnnotationValue.ForConstant.of(0L));
        properties.put("long_property_array", AnnotationValue.ForConstant.of(new long[]{ 0 }));
        properties.put("float_property", AnnotationValue.ForConstant.of(0.0F));
        properties.put("float_property_array", AnnotationValue.ForConstant.of(new float[]{ 0 }));
        properties.put("double_property", AnnotationValue.ForConstant.of(0.0));
        properties.put("double_property_array", AnnotationValue.ForConstant.of(new double[]{ 0.0 }));
        properties.put("string_property", AnnotationValue.ForConstant.of("foo"));
        properties.put("string_property_array", AnnotationValue.ForConstant.of(new String[]{ "foo" }));
        AnnotationDescription annotationDescription = Mockito.mock(AnnotationDescription.class);
        Mockito.when(annotationDescription.getAnnotationType()).thenReturn(of(AbstractMethodDescriptionTest.SampleAnnotation.class));
        properties.put("annotation_property", new AnnotationValue.ForAnnotationDescription(annotationDescription));
        properties.put("annotation_property_array", AnnotationValue.ForDescriptionArray.of(of(AbstractMethodDescriptionTest.SampleAnnotation.class), new AnnotationDescription[]{ annotationDescription }));
        EnumerationDescription enumerationDescription = Mockito.mock(EnumerationDescription.class);
        Mockito.when(enumerationDescription.getEnumerationType()).thenReturn(of(AbstractMethodDescriptionTest.SampleEnumeration.class));
        properties.put("enum_property", AnnotationValue.ForEnumerationDescription.<Enum>of(enumerationDescription));
        properties.put("enum_property_array", AnnotationValue.ForDescriptionArray.<Enum>of(of(AbstractMethodDescriptionTest.SampleEnumeration.class), new EnumerationDescription[]{ enumerationDescription }));
        MethodList<?> methods = of(AbstractMethodDescriptionTest.AnnotationValues.class).getDeclaredMethods();
        for (Map.Entry<String, AnnotationValue<?, ?>> entry : properties.entrySet()) {
            MatcherAssert.assertThat(methods.filter(ElementMatchers.named(entry.getKey())).getOnly().isDefaultValue(entry.getValue()), CoreMatchers.is(true));
            MatcherAssert.assertThat(methods.filter(ElementMatchers.named(entry.getKey())).getOnly().isDefaultValue(Mockito.mock(AnnotationValue.class)), CoreMatchers.is(false));
        }
        Mockito.when(annotationDescription.getAnnotationType()).thenReturn(TypeDescription.OBJECT);
        MatcherAssert.assertThat(methods.filter(ElementMatchers.named("annotation_property")).getOnly().isDefaultValue(new AnnotationValue.ForAnnotationDescription(annotationDescription)), CoreMatchers.is(false));
        MatcherAssert.assertThat(methods.filter(ElementMatchers.named("annotation_property_array")).getOnly().isDefaultValue(AnnotationValue.ForDescriptionArray.of(of(Object.class), new AnnotationDescription[]{ annotationDescription })), CoreMatchers.is(false));
        Mockito.when(enumerationDescription.getEnumerationType()).thenReturn(TypeDescription.OBJECT);
        MatcherAssert.assertThat(methods.filter(ElementMatchers.named("enum_property")).getOnly().isDefaultValue(AnnotationValue.ForEnumerationDescription.<Enum>of(enumerationDescription)), CoreMatchers.is(false));
        MatcherAssert.assertThat(methods.filter(ElementMatchers.named("enum_property_array")).getOnly().isDefaultValue(AnnotationValue.ForDescriptionArray.<Enum>of(of(Object.class), new EnumerationDescription[]{ enumerationDescription })), CoreMatchers.is(false));
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface SampleAnnotation {}

    private enum SampleEnumeration {

        INSTANCE;}

    @SuppressWarnings("unused")
    private abstract static class Sample {
        Sample(final Void argument) {
        }

        @AbstractMethodDescriptionTest.SampleAnnotation
        private Sample(int[] first, @AbstractMethodDescriptionTest.SampleAnnotation
        long second) throws IOException {
        }

        private static void first() {
            /* do nothing */
        }

        protected abstract Object second(String first, long second) throws IOException, RuntimeException;

        @AbstractMethodDescriptionTest.SampleAnnotation
        public boolean[] third(@AbstractMethodDescriptionTest.SampleAnnotation
        final Object[] first, int[] second) throws Throwable {
            return null;
        }
    }

    private abstract static class SampleSub extends AbstractMethodDescriptionTest.Sample {
        protected SampleSub(Void argument) {
            super(argument);
        }
    }

    @SuppressWarnings("unused")
    public abstract static class PublicType {
        public PublicType() {
            /* do nothing */
        }

        protected PublicType(Void protectedConstructor) {
            /* do nothing */
        }

        PublicType(Object packagePrivateConstructor) {
            /* do nothing */
        }

        private PublicType(String privateConstructor) {
            /* do nothing */
        }

        public abstract void publicMethod();

        protected abstract void protectedMethod();

        abstract void packagePrivateMethod();

        private void privateMethod() {
            /* do nothing */
        }
    }

    @SuppressWarnings("unused")
    abstract static class PackagePrivateType {
        public PackagePrivateType() {
            /* do nothing */
        }

        protected PackagePrivateType(Void protectedConstructor) {
            /* do nothing */
        }

        PackagePrivateType(Object packagePrivateConstructor) {
            /* do nothing */
        }

        private PackagePrivateType(String privateConstructor) {
            /* do nothing */
        }

        public abstract void publicMethod();

        protected abstract void protectedMethod();

        abstract void packagePrivateMethod();

        public static void staticPublicMethod() {
            /* empty */
        }

        protected static void staticProtectedMethod() {
            /* empty */
        }

        static void staticPackagePrivateMethod() {
            /* empty */
        }

        private static void staticPrivateMethod() {
            /* empty */
        }

        private void privateMethod() {
            /* do nothing */
        }
    }

    @SuppressWarnings("unused")
    public static class MethodVisibilityType {
        public void packagePrivateArgument(AbstractMethodDescriptionTest.PackagePrivateType arg) {
            /* do nothing */
        }

        public AbstractMethodDescriptionTest.PackagePrivateType packagePrivateReturnType() {
            return null;
        }
    }

    @SuppressWarnings("unused")
    static class GenericMethod<T extends Exception> {
        T foo(T t) throws T {
            return null;
        }

        T bar(T t) throws Exception {
            return null;
        }

        <Q> void qux() {
            /* empty */
        }
    }

    private static class DeprecationSample {
        @Deprecated
        private void foo() {
            /* empty */
        }
    }

    @SuppressWarnings("unused")
    private @interface AnnotationValues {
        boolean boolean_property();

        boolean[] boolean_property_array();

        byte byte_property();

        byte[] byte_property_array();

        short short_property();

        short[] short_property_array();

        int int_property();

        int[] int_property_array();

        long long_property();

        long[] long_property_array();

        float float_property();

        float[] float_property_array();

        double double_property();

        double[] double_property_array();

        String string_property();

        String[] string_property_array();

        AbstractMethodDescriptionTest.SampleAnnotation annotation_property();

        AbstractMethodDescriptionTest.SampleAnnotation[] annotation_property_array();

        AbstractMethodDescriptionTest.SampleEnumeration enum_property();

        AbstractMethodDescriptionTest.SampleEnumeration[] enum_property_array();
    }

    public class SyntheticParameter {
        public SyntheticParameter(@AbstractMethodDescriptionTest.SyntheticMarker
        Void unused) {
            /* empty */
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface SyntheticMarker {}
}

