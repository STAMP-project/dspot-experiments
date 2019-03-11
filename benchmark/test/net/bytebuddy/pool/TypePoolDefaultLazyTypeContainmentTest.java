package net.bytebuddy.pool;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.pool.TypePool.Default.LazyTypeDescription.TypeContainment.SelfContained.INSTANCE;


public class TypePoolDefaultLazyTypeContainmentTest {
    private static final String FOO = "baz.foo";

    private static final String FOO_INTERNAL = "baz/foo";

    private static final String BAR = "bar";

    private static final String QUX = "qux";

    @Test
    public void testSelfDeclared() throws Exception {
        MatcherAssert.assertThat(INSTANCE.isLocalType(), CoreMatchers.is(false));
        MatcherAssert.assertThat(INSTANCE.isSelfContained(), CoreMatchers.is(true));
    }

    @Test
    public void testSelfDeclaredGetTypeIsNull() throws Exception {
        MatcherAssert.assertThat(INSTANCE.getEnclosingType(Mockito.mock(TypePool.class)), CoreMatchers.nullValue(TypeDescription.class));
    }

    @Test
    public void testSelfDeclaredGetMethodIsNull() throws Exception {
        MatcherAssert.assertThat(INSTANCE.getEnclosingMethod(Mockito.mock(TypePool.class)), CoreMatchers.nullValue(MethodDescription.class));
    }

    @Test
    public void testDeclaredInType() throws Exception {
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.TypeContainment.WithinType(TypePoolDefaultLazyTypeContainmentTest.FOO_INTERNAL, false).isLocalType(), CoreMatchers.is(false));
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.TypeContainment.WithinType(TypePoolDefaultLazyTypeContainmentTest.FOO_INTERNAL, false).isSelfContained(), CoreMatchers.is(false));
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.TypeContainment.WithinType(TypePoolDefaultLazyTypeContainmentTest.FOO_INTERNAL, true).isLocalType(), CoreMatchers.is(true));
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.TypeContainment.WithinType(TypePoolDefaultLazyTypeContainmentTest.FOO_INTERNAL, true).isSelfContained(), CoreMatchers.is(false));
    }

    @Test
    public void testDeclaredInTypeGetTypeIsNotNull() throws Exception {
        TypePool typePool = Mockito.mock(TypePool.class);
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        Mockito.when(typePool.describe(TypePoolDefaultLazyTypeContainmentTest.FOO)).thenReturn(new TypePool.Resolution.Simple(typeDescription));
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.TypeContainment.WithinType(TypePoolDefaultLazyTypeContainmentTest.FOO_INTERNAL, false).getEnclosingType(typePool), CoreMatchers.is(typeDescription));
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.TypeContainment.WithinType(TypePoolDefaultLazyTypeContainmentTest.FOO_INTERNAL, true).getEnclosingType(typePool), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testDeclaredInTypeGetMethodIsNull() throws Exception {
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.TypeContainment.WithinType(TypePoolDefaultLazyTypeContainmentTest.FOO_INTERNAL, false).getEnclosingMethod(Mockito.mock(TypePool.class)), CoreMatchers.nullValue(MethodDescription.class));
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.TypeContainment.WithinType(TypePoolDefaultLazyTypeContainmentTest.FOO_INTERNAL, false).getEnclosingMethod(Mockito.mock(TypePool.class)), CoreMatchers.nullValue(MethodDescription.class));
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.TypeContainment.WithinType(TypePoolDefaultLazyTypeContainmentTest.FOO_INTERNAL, true).getEnclosingMethod(Mockito.mock(TypePool.class)), CoreMatchers.nullValue(MethodDescription.class));
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.TypeContainment.WithinType(TypePoolDefaultLazyTypeContainmentTest.FOO_INTERNAL, true).getEnclosingMethod(Mockito.mock(TypePool.class)), CoreMatchers.nullValue(MethodDescription.class));
    }

    @Test
    public void testDeclaredInMethod() throws Exception {
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.TypeContainment.WithinMethod(TypePoolDefaultLazyTypeContainmentTest.FOO_INTERNAL, TypePoolDefaultLazyTypeContainmentTest.BAR, TypePoolDefaultLazyTypeContainmentTest.QUX).isLocalType(), CoreMatchers.is(true));
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.TypeContainment.WithinMethod(TypePoolDefaultLazyTypeContainmentTest.FOO_INTERNAL, TypePoolDefaultLazyTypeContainmentTest.BAR, TypePoolDefaultLazyTypeContainmentTest.QUX).isSelfContained(), CoreMatchers.is(false));
    }

    @Test
    public void testDeclaredInMethodGetTypeIsNotNull() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        TypePool typePool = Mockito.mock(TypePool.class);
        Mockito.when(typePool.describe(TypePoolDefaultLazyTypeContainmentTest.FOO)).thenReturn(new TypePool.Resolution.Simple(typeDescription));
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.TypeContainment.WithinMethod(TypePoolDefaultLazyTypeContainmentTest.FOO_INTERNAL, TypePoolDefaultLazyTypeContainmentTest.BAR, TypePoolDefaultLazyTypeContainmentTest.QUX).getEnclosingType(typePool), CoreMatchers.is(typeDescription));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeclaredInMethodGetMethodIsNull() throws Exception {
        MethodDescription.InDefinedShape methodDescription = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(methodDescription.getActualName()).thenReturn(TypePoolDefaultLazyTypeContainmentTest.BAR);
        Mockito.when(methodDescription.getDescriptor()).thenReturn(TypePoolDefaultLazyTypeContainmentTest.QUX);
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        TypePool typePool = Mockito.mock(TypePool.class);
        Mockito.when(typePool.describe(TypePoolDefaultLazyTypeContainmentTest.FOO)).thenReturn(new TypePool.Resolution.Simple(typeDescription));
        Mockito.when(typeDescription.getDeclaredMethods()).thenReturn(((MethodList) (new MethodList.Explicit<MethodDescription>(methodDescription))));
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.TypeContainment.WithinMethod(TypePoolDefaultLazyTypeContainmentTest.FOO_INTERNAL, TypePoolDefaultLazyTypeContainmentTest.BAR, TypePoolDefaultLazyTypeContainmentTest.QUX).getEnclosingMethod(typePool), CoreMatchers.is(methodDescription));
    }
}

