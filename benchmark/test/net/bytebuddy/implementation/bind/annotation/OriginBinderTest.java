package net.bytebuddy.implementation.bind.annotation;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.utility.JavaType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;
import static net.bytebuddy.description.type.TypeDescription.Generic.VOID;
import static net.bytebuddy.implementation.bind.annotation.Origin.Binder.INSTANCE;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;


public class OriginBinderTest extends AbstractAnnotationBinderTest<Origin> {
    private static final String FOO = "foo";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();

    @Mock
    private TypeDescription targetType;

    @Mock
    private TypeDescription.Generic genericTargetType;

    @Mock
    private MethodDescription.InDefinedShape methodDescription;

    public OriginBinderTest() {
        super(Origin.class);
    }

    @Test
    public void testClassBinding() throws Exception {
        Mockito.when(targetType.getInternalName()).thenReturn(OriginBinderTest.FOO);
        Mockito.when(targetType.represents(Class.class)).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
        Mockito.verify(implementationTarget).getOriginType();
    }

    @Test
    public void testMethodBinding() throws Exception {
        Mockito.when(targetType.getInternalName()).thenReturn(OriginBinderTest.FOO);
        Mockito.when(targetType.represents(Method.class)).thenReturn(true);
        Mockito.when(source.isMethod()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testMethodBindingForNonMethod() throws Exception {
        Mockito.when(targetType.getInternalName()).thenReturn(OriginBinderTest.FOO);
        Mockito.when(targetType.represents(Method.class)).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testConstructorBinding() throws Exception {
        Mockito.when(targetType.getInternalName()).thenReturn(OriginBinderTest.FOO);
        Mockito.when(targetType.represents(Constructor.class)).thenReturn(true);
        Mockito.when(source.isConstructor()).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testConstructorBindingForNonConstructor() throws Exception {
        Mockito.when(targetType.getInternalName()).thenReturn(OriginBinderTest.FOO);
        Mockito.when(targetType.represents(Constructor.class)).thenReturn(true);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(false));
    }

    @Test
    public void testStringBinding() throws Exception {
        Mockito.when(targetType.getInternalName()).thenReturn(OriginBinderTest.FOO);
        Mockito.when(targetType.represents(String.class)).thenReturn(true);
        Mockito.when(targetType.getSort()).thenReturn(NON_GENERIC);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
    }

    @Test
    public void testModifierBinding() throws Exception {
        Mockito.when(targetType.getInternalName()).thenReturn(OriginBinderTest.FOO);
        Mockito.when(targetType.represents(int.class)).thenReturn(true);
        Mockito.when(targetType.getSort()).thenReturn(NON_GENERIC);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testMethodHandleBinding() throws Exception {
        Mockito.when(genericTargetType.asErasure()).thenReturn(of(JavaType.METHOD_HANDLE.load()));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getParameters()).thenReturn(new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription.InDefinedShape>());
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        Mockito.when(typeDescription.asErasure()).thenReturn(typeDescription);
        Mockito.when(methodDescription.getDeclaringType()).thenReturn(typeDescription);
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
    }

    @Test
    @JavaVersionRule.Enforce(7)
    public void testMethodTypeBinding() throws Exception {
        Mockito.when(genericTargetType.asErasure()).thenReturn(of(JavaType.METHOD_TYPE.load()));
        Mockito.when(methodDescription.getReturnType()).thenReturn(VOID);
        Mockito.when(methodDescription.getParameters()).thenReturn(new ParameterList.Empty<net.bytebuddy.description.method.ParameterDescription.InDefinedShape>());
        MethodDelegationBinder.ParameterBinding<?> parameterBinding = INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
        MatcherAssert.assertThat(parameterBinding.isValid(), CoreMatchers.is(true));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalBinding() throws Exception {
        Mockito.when(targetType.getInternalName()).thenReturn(OriginBinderTest.FOO);
        Mockito.when(targetType.getSort()).thenReturn(NON_GENERIC);
        INSTANCE.bind(annotationDescription, source, target, implementationTarget, assigner, STATIC);
    }
}

