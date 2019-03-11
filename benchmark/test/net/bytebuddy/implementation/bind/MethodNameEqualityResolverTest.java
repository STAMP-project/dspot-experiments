package net.bytebuddy.implementation.bind;


import org.junit.Test;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.AMBIGUOUS;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.LEFT;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.RIGHT;


public class MethodNameEqualityResolverTest extends AbstractAmbiguityResolverTest {
    private static final String FOO = "foo";

    private static final String BAR = "bar";

    @Test
    public void testBothEqual() throws Exception {
        test(MethodNameEqualityResolverTest.FOO, MethodNameEqualityResolverTest.FOO, MethodNameEqualityResolverTest.FOO, AMBIGUOUS);
    }

    @Test
    public void testLeftEqual() throws Exception {
        test(MethodNameEqualityResolverTest.FOO, MethodNameEqualityResolverTest.BAR, MethodNameEqualityResolverTest.FOO, LEFT);
    }

    @Test
    public void testRightEqual() throws Exception {
        test(MethodNameEqualityResolverTest.BAR, MethodNameEqualityResolverTest.FOO, MethodNameEqualityResolverTest.FOO, RIGHT);
    }

    @Test
    public void testNoneEqual() throws Exception {
        test(MethodNameEqualityResolverTest.BAR, MethodNameEqualityResolverTest.BAR, MethodNameEqualityResolverTest.FOO, AMBIGUOUS);
    }
}

