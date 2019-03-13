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


public class TypePoolExplicitTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    private TypePool typePool;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypePool parent;

    @Test
    public void testSuccessfulLookup() throws Exception {
        TypePool.Resolution resolution = typePool.describe(TypePoolExplicitTest.FOO);
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(true));
        MatcherAssert.assertThat(resolution.resolve(), CoreMatchers.is(typeDescription));
        Mockito.verify(parent).describe(TypePoolExplicitTest.FOO);
        Mockito.verifyNoMoreInteractions(parent);
    }

    @Test
    public void testFailedLookup() throws Exception {
        TypePool.Resolution resolution = typePool.describe(TypePoolExplicitTest.BAR);
        MatcherAssert.assertThat(resolution.isResolved(), CoreMatchers.is(false));
        Mockito.verify(parent).describe(TypePoolExplicitTest.BAR);
        Mockito.verifyNoMoreInteractions(parent);
    }

    @Test
    public void testDelegation() throws Exception {
        TypePool.Resolution resolution = Mockito.mock(TypePool.Resolution.class);
        Mockito.when(resolution.isResolved()).thenReturn(true);
        Mockito.when(parent.describe(TypePoolExplicitTest.BAR)).thenReturn(resolution);
        MatcherAssert.assertThat(typePool.describe(TypePoolExplicitTest.BAR), CoreMatchers.sameInstance(resolution));
        Mockito.verify(parent).describe(TypePoolExplicitTest.BAR);
        Mockito.verifyNoMoreInteractions(parent);
    }
}

