package net.bytebuddy.pool;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TypePoolLazyFacadeTest {
    private static final String FOO = "foo";

    private static final int MODIFIERS = 42;

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypePool typePool;

    @Mock
    private TypePool.Resolution resolution;

    @Mock
    private TypeDescription typeDescription;

    @Test
    public void testDoesNotQueryActualTypePoolForName() throws Exception {
        TypePool typePool = new TypePool.LazyFacade(this.typePool);
        MatcherAssert.assertThat(typePool.describe(TypePoolLazyFacadeTest.FOO).resolve().getName(), CoreMatchers.is(TypePoolLazyFacadeTest.FOO));
        Mockito.verifyZeroInteractions(this.typePool);
    }

    @Test
    public void testDoesQueryActualTypePoolForResolution() throws Exception {
        TypePool typePool = new TypePool.LazyFacade(this.typePool);
        MatcherAssert.assertThat(typePool.describe(TypePoolLazyFacadeTest.FOO).isResolved(), CoreMatchers.is(true));
        Mockito.verify(this.typePool).describe(TypePoolLazyFacadeTest.FOO);
        Mockito.verifyNoMoreInteractions(this.typePool);
        Mockito.verify(resolution).isResolved();
        Mockito.verifyNoMoreInteractions(resolution);
    }

    @Test
    public void testDoesQueryActualTypePoolForNonNameProperty() throws Exception {
        TypePool typePool = new TypePool.LazyFacade(this.typePool);
        MatcherAssert.assertThat(typePool.describe(TypePoolLazyFacadeTest.FOO).resolve().getModifiers(), CoreMatchers.is(TypePoolLazyFacadeTest.MODIFIERS));
        Mockito.verify(this.typePool).describe(TypePoolLazyFacadeTest.FOO);
        Mockito.verifyNoMoreInteractions(this.typePool);
        Mockito.verify(resolution).resolve();
        Mockito.verifyNoMoreInteractions(resolution);
        Mockito.verify(typeDescription).getModifiers();
        Mockito.verifyNoMoreInteractions(typeDescription);
    }
}

