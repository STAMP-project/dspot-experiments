package net.bytebuddy.description.type;


import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TypeDescriptionGenericVisitorReducingTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic typeDescription;

    @Mock
    private TypeDescription.Generic bound;

    @Mock
    private TypeDescription.Generic genericTypeDescription;

    @Mock
    private TypeDescription declaringType;

    @Mock
    private TypeDescription rawTypeDescription;

    @Mock
    private TypeVariableToken typeVariableToken;

    private TypeDescription.Generic.Visitor<TypeDescription> visitor;

    @Test(expected = IllegalStateException.class)
    public void testWildcardThrowsException() throws Exception {
        visitor.onWildcard(typeDescription);
    }

    @Test
    public void testGenericArray() throws Exception {
        Mockito.when(typeDescription.asErasure()).thenReturn(rawTypeDescription);
        MatcherAssert.assertThat(visitor.onGenericArray(typeDescription), CoreMatchers.is(rawTypeDescription));
        Mockito.verify(typeDescription).asErasure();
        Mockito.verifyNoMoreInteractions(typeDescription);
    }

    @Test
    public void testParameterizedType() throws Exception {
        Mockito.when(typeDescription.asErasure()).thenReturn(rawTypeDescription);
        MatcherAssert.assertThat(visitor.onParameterizedType(typeDescription), CoreMatchers.is(rawTypeDescription));
        Mockito.verify(typeDescription).asErasure();
        Mockito.verifyNoMoreInteractions(typeDescription);
    }

    @Test
    public void testNonGenericType() throws Exception {
        Mockito.when(typeDescription.asErasure()).thenReturn(rawTypeDescription);
        MatcherAssert.assertThat(visitor.onNonGenericType(typeDescription), CoreMatchers.is(rawTypeDescription));
        Mockito.verify(typeDescription).asErasure();
        Mockito.verifyNoMoreInteractions(typeDescription);
    }

    @Test
    public void testTypeVariableSelfDeclared() throws Exception {
        Mockito.when(typeDescription.getSymbol()).thenReturn(TypeDescriptionGenericVisitorReducingTest.FOO);
        Mockito.when(typeVariableToken.getSymbol()).thenReturn(TypeDescriptionGenericVisitorReducingTest.FOO);
        Mockito.when(typeVariableToken.getBounds()).thenReturn(new TypeList.Generic.Explicit(bound));
        Mockito.when(bound.accept(visitor)).thenReturn(rawTypeDescription);
        MatcherAssert.assertThat(visitor.onTypeVariable(typeDescription), CoreMatchers.is(rawTypeDescription));
        Mockito.verify(typeDescription).getSymbol();
        Mockito.verifyNoMoreInteractions(typeDescription);
        Mockito.verify(typeVariableToken).getSymbol();
        Mockito.verify(typeVariableToken).getBounds();
        Mockito.verifyNoMoreInteractions(typeVariableToken);
    }

    @Test
    public void testTypeVariableContextDeclared() throws Exception {
        Mockito.when(typeDescription.getSymbol()).thenReturn(TypeDescriptionGenericVisitorReducingTest.FOO);
        Mockito.when(typeVariableToken.getSymbol()).thenReturn(TypeDescriptionGenericVisitorReducingTest.BAR);
        Mockito.when(declaringType.findVariable(TypeDescriptionGenericVisitorReducingTest.FOO)).thenReturn(genericTypeDescription);
        Mockito.when(genericTypeDescription.asErasure()).thenReturn(rawTypeDescription);
        MatcherAssert.assertThat(visitor.onTypeVariable(typeDescription), CoreMatchers.is(rawTypeDescription));
        Mockito.verify(typeDescription, Mockito.times(2)).getSymbol();
        Mockito.verifyNoMoreInteractions(typeDescription);
        Mockito.verify(typeVariableToken).getSymbol();
        Mockito.verifyNoMoreInteractions(typeVariableToken);
        Mockito.verify(declaringType).findVariable(TypeDescriptionGenericVisitorReducingTest.FOO);
        Mockito.verifyNoMoreInteractions(declaringType);
    }

    @Test
    public void testTargetTypeResolution() throws Exception {
        MatcherAssert.assertThat(visitor.onGenericArray(TargetType.DESCRIPTION.asGenericType()), CoreMatchers.is(declaringType));
        MatcherAssert.assertThat(visitor.onParameterizedType(TargetType.DESCRIPTION.asGenericType()), CoreMatchers.is(declaringType));
        MatcherAssert.assertThat(visitor.onNonGenericType(TargetType.DESCRIPTION.asGenericType()), CoreMatchers.is(declaringType));
        Mockito.when(typeDescription.getSymbol()).thenReturn(TypeDescriptionGenericVisitorReducingTest.BAR);
        Mockito.when(declaringType.findVariable(TypeDescriptionGenericVisitorReducingTest.BAR)).thenReturn(TargetType.DESCRIPTION.asGenericType());
        MatcherAssert.assertThat(visitor.onTypeVariable(typeDescription), CoreMatchers.is(declaringType));
    }
}

