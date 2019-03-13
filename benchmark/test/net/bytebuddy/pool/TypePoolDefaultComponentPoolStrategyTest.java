package net.bytebuddy.pool;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.pool.TypePool.Default.ComponentTypeLocator.Illegal.INSTANCE;


public class TypePoolDefaultComponentPoolStrategyTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    private static final String BAR_DESCRIPTOR = ("L" + (TypePoolDefaultComponentPoolStrategyTest.BAR)) + ";";

    private static final String QUX = "qux";

    private static final String BAZ = "baz";

    @Test(expected = IllegalStateException.class)
    public void testIllegal() throws Exception {
        INSTANCE.bind(TypePoolDefaultComponentPoolStrategyTest.FOO);
    }

    @Test
    public void testForAnnotationProperty() throws Exception {
        TypePool typePool = Mockito.mock(TypePool.class);
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        Mockito.when(typePool.describe(TypePoolDefaultComponentPoolStrategyTest.BAR)).thenReturn(new TypePool.Resolution.Simple(typeDescription));
        MethodDescription.InDefinedShape methodDescription = Mockito.mock(MethodDescription.InDefinedShape.class);
        Mockito.when(typeDescription.getDeclaredMethods()).thenReturn(new MethodList.Explicit<MethodDescription.InDefinedShape>(methodDescription));
        Mockito.when(methodDescription.getActualName()).thenReturn(TypePoolDefaultComponentPoolStrategyTest.FOO);
        TypeDescription.Generic returnType = Mockito.mock(TypeDescription.Generic.class);
        TypeDescription rawReturnType = Mockito.mock(TypeDescription.class);
        Mockito.when(returnType.asErasure()).thenReturn(rawReturnType);
        Mockito.when(methodDescription.getReturnType()).thenReturn(returnType);
        TypeDescription rawComponentType = Mockito.mock(TypeDescription.class);
        Mockito.when(rawReturnType.getComponentType()).thenReturn(rawComponentType);
        Mockito.when(rawComponentType.getName()).thenReturn(TypePoolDefaultComponentPoolStrategyTest.QUX);
        MatcherAssert.assertThat(new TypePool.Default.ComponentTypeLocator.ForAnnotationProperty(typePool, TypePoolDefaultComponentPoolStrategyTest.BAR_DESCRIPTOR).bind(TypePoolDefaultComponentPoolStrategyTest.FOO).lookup(), CoreMatchers.is(TypePoolDefaultComponentPoolStrategyTest.QUX));
    }

    @Test
    public void testForArrayType() throws Exception {
        MatcherAssert.assertThat(new TypePool.Default.ComponentTypeLocator.ForArrayType(("()[" + (TypePoolDefaultComponentPoolStrategyTest.BAR_DESCRIPTOR))).bind(TypePoolDefaultComponentPoolStrategyTest.FOO).lookup(), CoreMatchers.is(TypePoolDefaultComponentPoolStrategyTest.BAR));
    }
}

