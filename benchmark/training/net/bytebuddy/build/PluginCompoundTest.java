package net.bytebuddy.build;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class PluginCompoundTest {
    @Rule
    public TestRule mockutoRule = new MockitoRule(this);

    @Mock
    private Plugin first;

    @Mock
    private Plugin second;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private ClassFileLocator classFileLocator;

    @Mock
    private DynamicType.Builder<?> origin;

    @Mock
    private DynamicType.Builder<?> firstBuilder;

    @Mock
    private DynamicType.Builder<?> secondBuilder;

    @Test
    @SuppressWarnings("unchecked")
    public void testNone() {
        Plugin compound = new Plugin.Compound(first, second);
        MatcherAssert.assertThat(compound.matches(typeDescription), Is.is(false));
        Mockito.verify(first).matches(typeDescription);
        Mockito.verify(second).matches(typeDescription);
        MatcherAssert.assertThat(compound.apply(origin, typeDescription, classFileLocator), Is.is(((DynamicType.Builder) (origin))));
        Mockito.verify(first, Mockito.never()).apply(origin, typeDescription, classFileLocator);
        Mockito.verify(second, Mockito.never()).apply(origin, typeDescription, classFileLocator);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFirstOnly() {
        Mockito.when(first.matches(typeDescription)).thenReturn(true);
        Mockito.when(first.apply(origin, typeDescription, classFileLocator)).thenReturn(((DynamicType.Builder) (firstBuilder)));
        Plugin compound = new Plugin.Compound(first, second);
        MatcherAssert.assertThat(compound.matches(typeDescription), Is.is(true));
        Mockito.verify(first).matches(typeDescription);
        Mockito.verify(second, Mockito.never()).matches(typeDescription);
        MatcherAssert.assertThat(compound.apply(origin, typeDescription, classFileLocator), Is.is(((DynamicType.Builder) (firstBuilder))));
        Mockito.verify(first).apply(origin, typeDescription, classFileLocator);
        Mockito.verify(second, Mockito.never()).apply(origin, typeDescription, classFileLocator);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSecondOnly() {
        Mockito.when(second.matches(typeDescription)).thenReturn(true);
        Mockito.when(second.apply(origin, typeDescription, classFileLocator)).thenReturn(((DynamicType.Builder) (firstBuilder)));
        Plugin compound = new Plugin.Compound(first, second);
        MatcherAssert.assertThat(compound.matches(typeDescription), Is.is(true));
        Mockito.verify(first).matches(typeDescription);
        Mockito.verify(second).matches(typeDescription);
        MatcherAssert.assertThat(compound.apply(origin, typeDescription, classFileLocator), Is.is(((DynamicType.Builder) (firstBuilder))));
        Mockito.verify(first, Mockito.never()).apply(origin, typeDescription, classFileLocator);
        Mockito.verify(second).apply(origin, typeDescription, classFileLocator);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFirstAndSecond() {
        Mockito.when(first.matches(typeDescription)).thenReturn(true);
        Mockito.when(second.matches(typeDescription)).thenReturn(true);
        Mockito.when(first.apply(origin, typeDescription, classFileLocator)).thenReturn(((DynamicType.Builder) (firstBuilder)));
        Mockito.when(second.apply(firstBuilder, typeDescription, classFileLocator)).thenReturn(((DynamicType.Builder) (secondBuilder)));
        Plugin compound = new Plugin.Compound(first, second);
        MatcherAssert.assertThat(compound.matches(typeDescription), Is.is(true));
        Mockito.verify(first).matches(typeDescription);
        Mockito.verify(second, Mockito.never()).matches(typeDescription);
        MatcherAssert.assertThat(compound.apply(origin, typeDescription, classFileLocator), Is.is(((DynamicType.Builder) (secondBuilder))));
        Mockito.verify(first).apply(origin, typeDescription, classFileLocator);
        Mockito.verify(second).apply(firstBuilder, typeDescription, classFileLocator);
    }
}

