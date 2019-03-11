package net.bytebuddy.pool;


import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.pool.TypePool.AbstractBase.ArrayTypeResolution.of;


public class TypePoolResolutionTest {
    private static final String FOO = "foo";

    @Test
    public void testSimpleResolution() throws Exception {
        TypeDescription typeDescription = Mockito.mock(TypeDescription.class);
        MatcherAssert.assertThat(new TypePool.Resolution.Simple(typeDescription).isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(new TypePool.Resolution.Simple(typeDescription).resolve(), CoreMatchers.is(typeDescription));
    }

    @Test(expected = IllegalStateException.class)
    public void testIllegalResolution() throws Exception {
        MatcherAssert.assertThat(new TypePool.Resolution.Illegal(TypePoolResolutionTest.FOO).isResolved(), CoreMatchers.is(false));
        new TypePool.Resolution.Illegal(TypePoolResolutionTest.FOO).resolve();
        Assert.fail();
    }

    @Test
    public void testArrayResolutionZeroArity() throws Exception {
        TypePool.Resolution resolution = Mockito.mock(TypePool.Resolution.class);
        MatcherAssert.assertThat(of(resolution, 0), CoreMatchers.is(resolution));
    }

    @Test
    public void testArrayResolutionPositiveArity() throws Exception {
        TypePool.Resolution resolution = Mockito.mock(TypePool.Resolution.class);
        Mockito.when(resolution.isResolved()).thenReturn(true);
        Mockito.when(resolution.resolve()).thenReturn(Mockito.mock(TypeDescription.class));
        MatcherAssert.assertThat(of(resolution, 1), CoreMatchers.not(resolution));
        TypeDescription typeDescription = of(resolution, 1).resolve();
        MatcherAssert.assertThat(typeDescription.isArray(), CoreMatchers.is(true));
    }
}

