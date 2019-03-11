package net.bytebuddy.matcher;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class MethodOverrideMatcherTest extends AbstractElementMatcherTest<MethodOverrideMatcher<?>> {
    @Mock
    private ElementMatcher<? super TypeDescription.Generic> typeMatcher;

    @Mock
    private TypeDescription.Generic declaringType;

    @Mock
    private TypeDescription.Generic superType;

    @Mock
    private TypeDescription.Generic interfaceType;

    @Mock
    private TypeDescription rawDeclaringType;

    @Mock
    private TypeDescription rawSuperType;

    @Mock
    private TypeDescription rawInterfaceType;

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private MethodDescription.InGenericShape declaredTypeMethod;

    @Mock
    private MethodDescription.InGenericShape superTypeMethod;

    @Mock
    private MethodDescription.InGenericShape interfaceTypeMethod;

    @Mock
    private MethodDescription.SignatureToken token;

    @Mock
    private MethodDescription.SignatureToken otherToken;

    @SuppressWarnings("unchecked")
    public MethodOverrideMatcherTest() {
        super(((Class<? extends MethodOverrideMatcher<?>>) ((Object) (MethodOverrideMatcher.class))), "isOverriddenFrom");
    }

    @Test
    public void testDirectMatch() throws Exception {
        Mockito.when(declaredTypeMethod.asSignatureToken()).thenReturn(token);
        Mockito.when(typeMatcher.matches(declaringType)).thenReturn(true);
        Assert.assertThat(new MethodOverrideMatcher<MethodDescription>(typeMatcher).matches(methodDescription), CoreMatchers.is(true));
        Mockito.verify(typeMatcher).matches(declaringType);
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }

    @Test
    public void testSuperTypeMatch() throws Exception {
        Mockito.when(declaredTypeMethod.asSignatureToken()).thenReturn(otherToken);
        Mockito.when(interfaceTypeMethod.asSignatureToken()).thenReturn(otherToken);
        Mockito.when(superTypeMethod.asSignatureToken()).thenReturn(token);
        Mockito.when(typeMatcher.matches(superType)).thenReturn(true);
        Assert.assertThat(new MethodOverrideMatcher<MethodDescription>(typeMatcher).matches(methodDescription), CoreMatchers.is(true));
        Mockito.verify(typeMatcher).matches(superType);
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }

    @Test
    public void testInterfaceTypeMatch() throws Exception {
        Mockito.when(declaredTypeMethod.asSignatureToken()).thenReturn(otherToken);
        Mockito.when(superTypeMethod.asSignatureToken()).thenReturn(otherToken);
        Mockito.when(interfaceTypeMethod.asSignatureToken()).thenReturn(token);
        Mockito.when(typeMatcher.matches(interfaceType)).thenReturn(true);
        Assert.assertThat(new MethodOverrideMatcher<MethodDescription>(typeMatcher).matches(methodDescription), CoreMatchers.is(true));
        Mockito.verify(typeMatcher).matches(interfaceType);
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }

    @Test
    public void testNoMatchMatcher() throws Exception {
        Mockito.when(declaredTypeMethod.asSignatureToken()).thenReturn(token);
        Mockito.when(superTypeMethod.asSignatureToken()).thenReturn(token);
        Mockito.when(interfaceTypeMethod.asSignatureToken()).thenReturn(token);
        Assert.assertThat(new MethodOverrideMatcher<MethodDescription>(typeMatcher).matches(methodDescription), CoreMatchers.is(false));
        Mockito.verify(typeMatcher).matches(declaringType);
        Mockito.verify(typeMatcher).matches(superType);
        Mockito.verify(typeMatcher).matches(interfaceType);
        Mockito.verifyNoMoreInteractions(typeMatcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(declaredTypeMethod.asSignatureToken()).thenReturn(otherToken);
        Mockito.when(superTypeMethod.asSignatureToken()).thenReturn(otherToken);
        Mockito.when(interfaceTypeMethod.asSignatureToken()).thenReturn(otherToken);
        Assert.assertThat(new MethodOverrideMatcher<MethodDescription>(typeMatcher).matches(methodDescription), CoreMatchers.is(false));
        Mockito.verifyZeroInteractions(typeMatcher);
    }
}

