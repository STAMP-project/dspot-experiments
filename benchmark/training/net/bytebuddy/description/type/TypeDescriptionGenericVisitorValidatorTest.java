package net.bytebuddy.description.type;


import java.io.Serializable;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDefinition.Sort.describe;
import static net.bytebuddy.description.type.TypeDescription.Generic.OBJECT;
import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.Validator.EXCEPTION;
import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.Validator.FIELD;
import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.Validator.INTERFACE;
import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.Validator.METHOD_PARAMETER;
import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.Validator.METHOD_RETURN;
import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.Validator.SUPER_CLASS;
import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.Validator.TYPE_VARIABLE;


public class TypeDescriptionGenericVisitorValidatorTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic typeDescription;

    @Test
    public void testWildcardNotValidated() throws Exception {
        MatcherAssert.assertThat(SUPER_CLASS.onWildcard(typeDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(INTERFACE.onWildcard(typeDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(TYPE_VARIABLE.onWildcard(typeDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(FIELD.onWildcard(typeDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(METHOD_RETURN.onWildcard(typeDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(METHOD_PARAMETER.onWildcard(typeDescription), CoreMatchers.is(false));
        MatcherAssert.assertThat(EXCEPTION.onWildcard(typeDescription), CoreMatchers.is(false));
    }

    @Test
    public void testExceptionType() throws Exception {
        MatcherAssert.assertThat(EXCEPTION.onNonGenericType(describe(Exception.class)), CoreMatchers.is(true));
        TypeDescription.Generic typeVariable = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription.Generic bound = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(typeVariable.getUpperBounds()).thenReturn(new TypeList.Generic.Explicit(bound));
        Mockito.when(bound.asGenericType()).thenReturn(bound);
        Mockito.when(bound.accept(EXCEPTION)).thenReturn(false);
        MatcherAssert.assertThat(EXCEPTION.onTypeVariable(typeVariable), CoreMatchers.is(false));
        Mockito.when(bound.accept(EXCEPTION)).thenReturn(true);
        MatcherAssert.assertThat(EXCEPTION.onTypeVariable(typeVariable), CoreMatchers.is(true));
        MatcherAssert.assertThat(EXCEPTION.onGenericArray(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(EXCEPTION.onParameterizedType(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(false));
    }

    @Test
    public void testSuperClassType() throws Exception {
        MatcherAssert.assertThat(SUPER_CLASS.onNonGenericType(OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(SUPER_CLASS.onNonGenericType(describe(Serializable.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(SUPER_CLASS.onNonGenericType(describe(void.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(SUPER_CLASS.onNonGenericType(describe(int.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(SUPER_CLASS.onNonGenericType(describe(void.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(SUPER_CLASS.onNonGenericType(describe(Object[].class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(SUPER_CLASS.onParameterizedType(OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(SUPER_CLASS.onParameterizedType(describe(Serializable.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(SUPER_CLASS.onTypeVariable(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(SUPER_CLASS.onGenericArray(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(false));
    }

    @Test
    public void testInterfaceType() throws Exception {
        MatcherAssert.assertThat(INTERFACE.onNonGenericType(OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(INTERFACE.onNonGenericType(describe(Serializable.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(INTERFACE.onNonGenericType(describe(void.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(INTERFACE.onNonGenericType(describe(int.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(INTERFACE.onNonGenericType(describe(void.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(INTERFACE.onNonGenericType(describe(Object[].class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(INTERFACE.onParameterizedType(OBJECT), CoreMatchers.is(false));
        MatcherAssert.assertThat(INTERFACE.onParameterizedType(describe(Serializable.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(INTERFACE.onTypeVariable(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(INTERFACE.onGenericArray(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(false));
    }

    @Test
    public void testFieldType() throws Exception {
        MatcherAssert.assertThat(FIELD.onNonGenericType(OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(FIELD.onNonGenericType(describe(Object[].class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(FIELD.onNonGenericType(describe(int.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(FIELD.onNonGenericType(describe(void.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(FIELD.onGenericArray(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(FIELD.onParameterizedType(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(FIELD.onTypeVariable(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(true));
    }

    @Test
    public void testMethodParameterType() throws Exception {
        MatcherAssert.assertThat(METHOD_PARAMETER.onNonGenericType(OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(METHOD_PARAMETER.onNonGenericType(describe(Object[].class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(METHOD_PARAMETER.onNonGenericType(describe(int.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(METHOD_PARAMETER.onNonGenericType(describe(void.class)), CoreMatchers.is(false));
        MatcherAssert.assertThat(METHOD_PARAMETER.onGenericArray(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(METHOD_PARAMETER.onParameterizedType(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(METHOD_PARAMETER.onTypeVariable(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(true));
    }

    @Test
    public void testMethodReturnType() throws Exception {
        MatcherAssert.assertThat(METHOD_RETURN.onNonGenericType(OBJECT), CoreMatchers.is(true));
        MatcherAssert.assertThat(METHOD_RETURN.onNonGenericType(describe(Object[].class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(METHOD_RETURN.onNonGenericType(describe(int.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(METHOD_RETURN.onNonGenericType(describe(void.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(METHOD_RETURN.onGenericArray(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(METHOD_RETURN.onParameterizedType(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(true));
        MatcherAssert.assertThat(METHOD_RETURN.onTypeVariable(Mockito.mock(TypeDescription.Generic.class)), CoreMatchers.is(true));
    }
}

