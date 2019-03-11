package net.bytebuddy.pool;


import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.pool.TypePool.Default.LazyTypeDescription.GenericTypeToken.ForPrimitiveType.VOID;
import static net.bytebuddy.pool.TypePool.Default.LazyTypeDescription.GenericTypeToken.ForUnboundWildcard.INSTANCE;


public class TypePoolDefaultLazyTypeDescriptionTest {
    private static final String FOO = "foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypePool.Default.LazyTypeDescription.GenericTypeToken genericTypeToken;

    @Mock
    private TypePool typePool;

    @Test(expected = IllegalStateException.class)
    public void testIllegalResolutionThrowsException() throws Exception {
        new TypePool.Default.LazyTypeDescription.AnnotationToken.Resolution.Illegal("foo").resolve();
    }

    @Test
    public void testIllegalResolutionIsNotResolved() throws Exception {
        MatcherAssert.assertThat(new TypePool.Default.LazyTypeDescription.AnnotationToken.Resolution.Illegal("foo").isResolved(), CoreMatchers.is(false));
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotResolveTypePathPrefixForPrimitiveType() throws Exception {
        VOID.getTypePathPrefix();
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotResolveTypePathPrefixForRawType() throws Exception {
        new TypePool.Default.LazyTypeDescription.GenericTypeToken.ForRawType(TypePoolDefaultLazyTypeDescriptionTest.FOO).getTypePathPrefix();
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotResolveTypePathPrefixForTypeVariable() throws Exception {
        new TypePool.Default.LazyTypeDescription.GenericTypeToken.ForTypeVariable(TypePoolDefaultLazyTypeDescriptionTest.FOO).getTypePathPrefix();
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotResolveTypePathPrefixForGenericArray() throws Exception {
        new TypePool.Default.LazyTypeDescription.GenericTypeToken.ForGenericArray(genericTypeToken).getTypePathPrefix();
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotResolveTypePathPrefixForUnboundWildcard() throws Exception {
        INSTANCE.getTypePathPrefix();
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotResolveTypePathPrefixForLowerBoundWildcard() throws Exception {
        new TypePool.Default.LazyTypeDescription.GenericTypeToken.ForLowerBoundWildcard(genericTypeToken).getTypePathPrefix();
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotResolveTypePathPrefixForUpperBoundWildcard() throws Exception {
        new TypePool.Default.LazyTypeDescription.GenericTypeToken.ForUpperBoundWildcard(genericTypeToken).getTypePathPrefix();
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotResolvePrimaryBoundPropertyForPrimitiveType() throws Exception {
        VOID.isPrimaryBound(typePool);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotResolvePrimaryBoundPropertyForGenericArray() throws Exception {
        new TypePool.Default.LazyTypeDescription.GenericTypeToken.ForGenericArray(genericTypeToken).isPrimaryBound(typePool);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotResolvePrimaryBoundPropertyForUnboundWildcard() throws Exception {
        INSTANCE.isPrimaryBound(typePool);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotResolvePrimaryBoundPropertyForLowerBoundWildcard() throws Exception {
        new TypePool.Default.LazyTypeDescription.GenericTypeToken.ForLowerBoundWildcard(genericTypeToken).isPrimaryBound(typePool);
    }

    @Test(expected = IllegalStateException.class)
    public void testCannotResolvePrimaryBoundPropertyForUpperBoundWildcard() throws Exception {
        new TypePool.Default.LazyTypeDescription.GenericTypeToken.ForUpperBoundWildcard(genericTypeToken).isPrimaryBound(typePool);
    }
}

