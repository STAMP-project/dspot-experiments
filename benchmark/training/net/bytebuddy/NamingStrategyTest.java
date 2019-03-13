package net.bytebuddy;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.StringStartsWith;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.NamingStrategy.SuffixingRandom.BaseNameResolver.ForUnnamedType.INSTANCE;
import static net.bytebuddy.NamingStrategy.SuffixingRandom.NO_PREFIX;


public class NamingStrategyTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String JAVA_QUX = "java.qux";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private NamingStrategy.SuffixingRandom.BaseNameResolver baseNameResolver;

    @Mock
    private TypeDescription.Generic typeDescription;

    @Mock
    private TypeDescription rawTypeDescription;

    @Test
    public void testSuffixingRandomSubclassNonConflictingPackage() throws Exception {
        Mockito.when(rawTypeDescription.getName()).thenReturn(NamingStrategyTest.FOO);
        NamingStrategy namingStrategy = new NamingStrategy.SuffixingRandom(NamingStrategyTest.BAR);
        MatcherAssert.assertThat(namingStrategy.subclass(typeDescription), StringStartsWith.startsWith(((((NamingStrategyTest.FOO) + "$") + (NamingStrategyTest.BAR)) + "$")));
        Mockito.verify(typeDescription, Mockito.atLeast(1)).asErasure();
        Mockito.verifyNoMoreInteractions(typeDescription);
        Mockito.verify(rawTypeDescription).getName();
        Mockito.verifyNoMoreInteractions(rawTypeDescription);
    }

    @Test
    public void testSuffixingRandomSubclassConflictingPackage() throws Exception {
        Mockito.when(baseNameResolver.resolve(rawTypeDescription)).thenReturn(NamingStrategyTest.JAVA_QUX);
        NamingStrategy namingStrategy = new NamingStrategy.SuffixingRandom(NamingStrategyTest.FOO, baseNameResolver, NamingStrategyTest.BAR);
        MatcherAssert.assertThat(namingStrategy.subclass(typeDescription), StringStartsWith.startsWith(((((((NamingStrategyTest.BAR) + ".") + (NamingStrategyTest.JAVA_QUX)) + "$") + (NamingStrategyTest.FOO)) + "$")));
        Mockito.verify(typeDescription).asErasure();
        Mockito.verifyNoMoreInteractions(typeDescription);
        Mockito.verifyZeroInteractions(rawTypeDescription);
        Mockito.verify(baseNameResolver).resolve(rawTypeDescription);
        Mockito.verifyNoMoreInteractions(baseNameResolver);
    }

    @Test
    public void testSuffixingRandomSubclassConflictingPackageDisabled() throws Exception {
        Mockito.when(baseNameResolver.resolve(rawTypeDescription)).thenReturn(NamingStrategyTest.JAVA_QUX);
        NamingStrategy namingStrategy = new NamingStrategy.SuffixingRandom(NamingStrategyTest.FOO, baseNameResolver, NO_PREFIX);
        MatcherAssert.assertThat(namingStrategy.subclass(typeDescription), StringStartsWith.startsWith(((((NamingStrategyTest.JAVA_QUX) + "$") + (NamingStrategyTest.FOO)) + "$")));
        Mockito.verify(typeDescription).asErasure();
        Mockito.verifyNoMoreInteractions(typeDescription);
        Mockito.verifyZeroInteractions(rawTypeDescription);
        Mockito.verify(baseNameResolver).resolve(rawTypeDescription);
        Mockito.verifyNoMoreInteractions(baseNameResolver);
    }

    @Test
    public void testSuffixingRandomRebase() throws Exception {
        Mockito.when(rawTypeDescription.getName()).thenReturn(NamingStrategyTest.FOO);
        NamingStrategy namingStrategy = new NamingStrategy.SuffixingRandom(NamingStrategyTest.BAR);
        MatcherAssert.assertThat(namingStrategy.rebase(rawTypeDescription), CoreMatchers.is(NamingStrategyTest.FOO));
        Mockito.verify(rawTypeDescription).getName();
        Mockito.verifyNoMoreInteractions(rawTypeDescription);
    }

    @Test
    public void testSuffixingRandomRedefine() throws Exception {
        Mockito.when(rawTypeDescription.getName()).thenReturn(NamingStrategyTest.FOO);
        NamingStrategy namingStrategy = new NamingStrategy.SuffixingRandom(NamingStrategyTest.BAR);
        MatcherAssert.assertThat(namingStrategy.redefine(rawTypeDescription), CoreMatchers.is(NamingStrategyTest.FOO));
        Mockito.verify(rawTypeDescription).getName();
        Mockito.verifyNoMoreInteractions(rawTypeDescription);
    }

    @Test
    public void testBaseNameResolvers() throws Exception {
        MatcherAssert.assertThat(new NamingStrategy.SuffixingRandom.BaseNameResolver.ForFixedValue(NamingStrategyTest.FOO).resolve(rawTypeDescription), CoreMatchers.is(NamingStrategyTest.FOO));
        Mockito.when(rawTypeDescription.getName()).thenReturn(NamingStrategyTest.FOO);
        MatcherAssert.assertThat(new NamingStrategy.SuffixingRandom.BaseNameResolver.ForGivenType(rawTypeDescription).resolve(rawTypeDescription), CoreMatchers.is(NamingStrategyTest.FOO));
        MatcherAssert.assertThat(INSTANCE.resolve(rawTypeDescription), CoreMatchers.is(NamingStrategyTest.FOO));
    }

    @Test
    public void testPrefixingRandom() throws Exception {
        Mockito.when(rawTypeDescription.getName()).thenReturn(NamingStrategyTest.BAR);
        NamingStrategy namingStrategy = new NamingStrategy.PrefixingRandom(NamingStrategyTest.FOO);
        MatcherAssert.assertThat(namingStrategy.subclass(typeDescription), StringStartsWith.startsWith((((NamingStrategyTest.FOO) + ".") + (NamingStrategyTest.BAR))));
        Mockito.verify(typeDescription).asErasure();
        Mockito.verifyNoMoreInteractions(typeDescription);
        Mockito.verify(rawTypeDescription).getName();
        Mockito.verifyNoMoreInteractions(rawTypeDescription);
    }

    @Test
    public void testPrefixingRandomRebase() throws Exception {
        Mockito.when(rawTypeDescription.getName()).thenReturn(NamingStrategyTest.FOO);
        NamingStrategy namingStrategy = new NamingStrategy.PrefixingRandom(NamingStrategyTest.BAR);
        MatcherAssert.assertThat(namingStrategy.rebase(rawTypeDescription), CoreMatchers.is(NamingStrategyTest.FOO));
        Mockito.verify(rawTypeDescription).getName();
        Mockito.verifyNoMoreInteractions(rawTypeDescription);
    }

    @Test
    public void testPrefixingRandomRedefine() throws Exception {
        Mockito.when(rawTypeDescription.getName()).thenReturn(NamingStrategyTest.FOO);
        NamingStrategy namingStrategy = new NamingStrategy.PrefixingRandom(NamingStrategyTest.BAR);
        MatcherAssert.assertThat(namingStrategy.redefine(rawTypeDescription), CoreMatchers.is(NamingStrategyTest.FOO));
        Mockito.verify(rawTypeDescription).getName();
        Mockito.verifyNoMoreInteractions(rawTypeDescription);
    }
}

