package net.bytebuddy.dynamic.scaffold.subclass;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.objectweb.asm.Opcodes;


public class SubclassDynamicTypeBuilderInstrumentableMatcherTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private TypeDescription rawTypeDescription;

    @Mock
    private TypeDescription rawOtherType;

    @Mock
    private TypeDescription.Generic typeDescription;

    @Mock
    private TypeDescription.Generic otherType;

    @Mock
    private LatentMatcher<? super MethodDescription> latentIgnoredMethods;

    @Mock
    private ElementMatcher<? super MethodDescription> ignoredMethods;

    private LatentMatcher<MethodDescription> matcher;

    @Test
    public void testMatchesVirtual() throws Exception {
        Mockito.when(methodDescription.isVirtual()).thenReturn(true);
        Mockito.when(methodDescription.getModifiers()).thenReturn(0);
        Mockito.when(ignoredMethods.matches(methodDescription)).thenReturn(false);
        Mockito.when(methodDescription.getDeclaringType()).thenReturn(rawOtherType);
        Mockito.when(methodDescription.isVisibleTo(rawTypeDescription)).thenReturn(true);
        MatcherAssert.assertThat(matcher.resolve(rawTypeDescription).matches(methodDescription), CoreMatchers.is(true));
    }

    @Test
    public void testNotMatchesVirtualIfNotVisible() throws Exception {
        Mockito.when(methodDescription.isVirtual()).thenReturn(true);
        Mockito.when(methodDescription.getModifiers()).thenReturn(0);
        Mockito.when(ignoredMethods.matches(methodDescription)).thenReturn(false);
        Mockito.when(methodDescription.getDeclaringType()).thenReturn(rawOtherType);
        Mockito.when(methodDescription.isVisibleTo(rawTypeDescription)).thenReturn(false);
        MatcherAssert.assertThat(matcher.resolve(rawTypeDescription).matches(methodDescription), CoreMatchers.is(false));
    }

    @Test
    public void testNotMatchesVirtualIfFinal() throws Exception {
        Mockito.when(methodDescription.isVirtual()).thenReturn(true);
        Mockito.when(methodDescription.getModifiers()).thenReturn(Opcodes.ACC_FINAL);
        Mockito.when(ignoredMethods.matches(methodDescription)).thenReturn(false);
        Mockito.when(methodDescription.getDeclaringType()).thenReturn(rawOtherType);
        Mockito.when(methodDescription.isVisibleTo(rawTypeDescription)).thenReturn(false);
        MatcherAssert.assertThat(matcher.resolve(rawTypeDescription).matches(methodDescription), CoreMatchers.is(false));
    }

    @Test
    public void testNotMatchesNonVirtualIfNotDeclared() throws Exception {
        Mockito.when(methodDescription.isVirtual()).thenReturn(false);
        Mockito.when(methodDescription.getModifiers()).thenReturn(0);
        Mockito.when(ignoredMethods.matches(methodDescription)).thenReturn(false);
        Mockito.when(methodDescription.getDeclaringType()).thenReturn(rawOtherType);
        MatcherAssert.assertThat(matcher.resolve(rawTypeDescription).matches(methodDescription), CoreMatchers.is(false));
    }

    @Test
    public void testNotMatchesIgnoredMethodIfNotDeclared() throws Exception {
        Mockito.when(methodDescription.isVirtual()).thenReturn(true);
        Mockito.when(methodDescription.getModifiers()).thenReturn(0);
        Mockito.when(ignoredMethods.matches(methodDescription)).thenReturn(true);
        Mockito.when(methodDescription.getDeclaringType()).thenReturn(rawOtherType);
        MatcherAssert.assertThat(matcher.resolve(rawTypeDescription).matches(methodDescription), CoreMatchers.is(false));
    }

    @Test
    public void testMatchesDeclaredMethod() throws Exception {
        Mockito.when(methodDescription.isVirtual()).thenReturn(true);
        Mockito.when(methodDescription.getModifiers()).thenReturn(0);
        Mockito.when(ignoredMethods.matches(methodDescription)).thenReturn(false);
        Mockito.when(methodDescription.getDeclaringType()).thenReturn(rawTypeDescription);
        MatcherAssert.assertThat(matcher.resolve(rawTypeDescription).matches(methodDescription), CoreMatchers.is(true));
    }

    @Test
    public void testMatchesDeclaredMethodIfIgnored() throws Exception {
        Mockito.when(methodDescription.isVirtual()).thenReturn(true);
        Mockito.when(methodDescription.getModifiers()).thenReturn(0);
        Mockito.when(ignoredMethods.matches(methodDescription)).thenReturn(true);
        Mockito.when(methodDescription.getDeclaringType()).thenReturn(rawTypeDescription);
        MatcherAssert.assertThat(matcher.resolve(rawTypeDescription).matches(methodDescription), CoreMatchers.is(true));
    }

    @Test
    public void testMatchesDeclaredMethodIfNotVirtual() throws Exception {
        Mockito.when(methodDescription.isVirtual()).thenReturn(false);
        Mockito.when(methodDescription.getModifiers()).thenReturn(0);
        Mockito.when(ignoredMethods.matches(methodDescription)).thenReturn(false);
        Mockito.when(methodDescription.getDeclaringType()).thenReturn(rawTypeDescription);
        MatcherAssert.assertThat(matcher.resolve(rawTypeDescription).matches(methodDescription), CoreMatchers.is(true));
    }

    @Test
    public void testMatchesDeclaredMethodIfFinal() throws Exception {
        Mockito.when(methodDescription.isVirtual()).thenReturn(true);
        Mockito.when(methodDescription.getModifiers()).thenReturn(Opcodes.ACC_FINAL);
        Mockito.when(ignoredMethods.matches(methodDescription)).thenReturn(false);
        Mockito.when(methodDescription.getDeclaringType()).thenReturn(rawTypeDescription);
        MatcherAssert.assertThat(matcher.resolve(rawTypeDescription).matches(methodDescription), CoreMatchers.is(true));
    }
}

