package net.bytebuddy.dynamic.scaffold.inline;


import java.util.Collections;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.dynamic.scaffold.inline.MethodRebaseResolver.Default.make;


public class MethodRebaseResolverDefaultTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription.InDefinedShape methodDescription;

    @Mock
    private MethodDescription.InDefinedShape otherMethod;

    @Mock
    private MethodDescription.Token token;

    @Mock
    private MethodDescription.Token otherToken;

    @Mock
    private MethodDescription.SignatureToken signatureToken;

    @Mock
    private MethodRebaseResolver.Resolution resolution;

    @Mock
    private DynamicType dynamicType;

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private ClassFileVersion classFileVersion;

    @Mock
    private AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy;

    @Mock
    private MethodNameTransformer methodNameTransformer;

    @Test
    public void testResolutionLookup() throws Exception {
        MethodRebaseResolver methodRebaseResolver = new MethodRebaseResolver.Default(Collections.singletonMap(methodDescription, resolution), Collections.singletonList(dynamicType));
        MatcherAssert.assertThat(methodRebaseResolver.resolve(methodDescription), CoreMatchers.is(resolution));
        MatcherAssert.assertThat(methodRebaseResolver.resolve(otherMethod).isRebased(), CoreMatchers.is(false));
        MatcherAssert.assertThat(methodRebaseResolver.resolve(otherMethod).getResolvedMethod(), CoreMatchers.is(otherMethod));
    }

    @Test
    public void testAuxiliaryTypes() throws Exception {
        MethodRebaseResolver methodRebaseResolver = new MethodRebaseResolver.Default(Collections.singletonMap(methodDescription, resolution), Collections.singletonList(dynamicType));
        MatcherAssert.assertThat(methodRebaseResolver.getAuxiliaryTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(methodRebaseResolver.getAuxiliaryTypes().contains(dynamicType), CoreMatchers.is(true));
    }

    @Test
    public void testTokenMap() throws Exception {
        MethodRebaseResolver methodRebaseResolver = new MethodRebaseResolver.Default(Collections.singletonMap(methodDescription, resolution), Collections.singletonList(dynamicType));
        MatcherAssert.assertThat(methodRebaseResolver.asTokenMap().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(methodRebaseResolver.asTokenMap().get(signatureToken), CoreMatchers.is(resolution));
    }

    @Test
    public void testCreationWithoutConstructor() throws Exception {
        MethodRebaseResolver methodRebaseResolver = make(instrumentedType, Collections.singleton(token), classFileVersion, auxiliaryTypeNamingStrategy, methodNameTransformer);
        MatcherAssert.assertThat(methodRebaseResolver.getAuxiliaryTypes().size(), CoreMatchers.is(0));
        MethodRebaseResolver.Resolution resolution = methodRebaseResolver.resolve(methodDescription);
        MatcherAssert.assertThat(resolution.isRebased(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.getResolvedMethod(), CoreMatchers.not(methodDescription));
        MatcherAssert.assertThat(resolution.getResolvedMethod().isConstructor(), CoreMatchers.is(false));
    }

    @Test
    public void testCreationWithConstructor() throws Exception {
        Mockito.when(methodDescription.isConstructor()).thenReturn(true);
        MethodRebaseResolver methodRebaseResolver = make(instrumentedType, Collections.singleton(token), classFileVersion, auxiliaryTypeNamingStrategy, methodNameTransformer);
        MatcherAssert.assertThat(methodRebaseResolver.getAuxiliaryTypes().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(methodRebaseResolver.getAuxiliaryTypes().get(0).getTypeDescription().getName(), CoreMatchers.is(MethodRebaseResolverDefaultTest.QUX));
        MethodRebaseResolver.Resolution resolution = methodRebaseResolver.resolve(methodDescription);
        MatcherAssert.assertThat(resolution.isRebased(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.getResolvedMethod(), CoreMatchers.not(methodDescription));
        MatcherAssert.assertThat(resolution.getResolvedMethod().isConstructor(), CoreMatchers.is(true));
    }
}

