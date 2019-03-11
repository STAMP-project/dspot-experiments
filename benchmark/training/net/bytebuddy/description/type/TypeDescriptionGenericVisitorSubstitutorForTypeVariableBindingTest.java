package net.bytebuddy.description.type;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TypeDescriptionGenericVisitorSubstitutorForTypeVariableBindingTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic parameterizedType;

    @Mock
    private TypeDescription.Generic source;

    @Mock
    private TypeDescription.Generic target;

    @Mock
    private TypeDescription.Generic unknown;

    @Mock
    private TypeDescription.Generic substituted;

    @Mock
    private TypeDescription.AbstractBase typeDefinition;

    @Mock
    private MethodDescription.AbstractBase methodDefinition;

    @Test
    public void testSimpleType() throws Exception {
        MatcherAssert.assertThat(new TypeDescription.Generic.Visitor.Substitutor.ForTypeVariableBinding(parameterizedType).onSimpleType(source), CoreMatchers.is(source));
    }

    @Test
    public void testNonGenericType() throws Exception {
        MatcherAssert.assertThat(new TypeDescription.Generic.Visitor.Substitutor.ForTypeVariableBinding(parameterizedType).onNonGenericType(source), CoreMatchers.is(source));
    }

    @Test
    public void testTypeVariableKnownOnType() throws Exception {
        Mockito.when(source.getTypeVariableSource()).thenReturn(typeDefinition);
        MatcherAssert.assertThat(new TypeDescription.Generic.Visitor.Substitutor.ForTypeVariableBinding(parameterizedType).onTypeVariable(source), CoreMatchers.is(target));
    }

    @Test
    public void testTypeVariableUnknownOnType() throws Exception {
        Mockito.when(unknown.getTypeVariableSource()).thenReturn(typeDefinition);
        TypeDescription.Generic rawType = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(unknown.asRawType()).thenReturn(rawType);
        MatcherAssert.assertThat(new TypeDescription.Generic.Visitor.Substitutor.ForTypeVariableBinding(parameterizedType).onTypeVariable(unknown), CoreMatchers.is(rawType));
    }

    @Test
    public void testTypeVariableKnownOnMethod() throws Exception {
        Mockito.when(source.getTypeVariableSource()).thenReturn(methodDefinition);
        MatcherAssert.assertThat(new TypeDescription.Generic.Visitor.Substitutor.ForTypeVariableBinding(parameterizedType).onTypeVariable(source), CoreMatchers.is(substituted));
    }

    @Test
    public void testTypeVariableUnknownOnMethod() throws Exception {
        Mockito.when(unknown.getTypeVariableSource()).thenReturn(methodDefinition);
        MatcherAssert.assertThat(new TypeDescription.Generic.Visitor.Substitutor.ForTypeVariableBinding(parameterizedType).onTypeVariable(unknown), CoreMatchers.is(substituted));
    }

    @Test
    public void testUnequalVariablesAndParameters() throws Exception {
        TypeDescription.Generic typeDescription = Mockito.mock(TypeDescription.Generic.class);
        Mockito.when(typeDescription.getTypeArguments()).thenReturn(new TypeList.Generic.Explicit(Mockito.mock(TypeDescription.Generic.class)));
        TypeDescription rawTypeDescription = Mockito.mock(TypeDescription.class);
        Mockito.when(typeDescription.asErasure()).thenReturn(rawTypeDescription);
        Mockito.when(rawTypeDescription.getTypeVariables()).thenReturn(new TypeList.Generic.Empty());
    }
}

