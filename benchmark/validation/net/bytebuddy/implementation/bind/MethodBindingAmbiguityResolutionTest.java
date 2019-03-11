package net.bytebuddy.implementation.bind;


import org.junit.Test;

import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.AMBIGUOUS;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.LEFT;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.RIGHT;
import static net.bytebuddy.implementation.bind.MethodDelegationBinder.AmbiguityResolver.Resolution.UNKNOWN;


public class MethodBindingAmbiguityResolutionTest {
    @Test
    public void testUnknownMerge() throws Exception {
        MethodBindingAmbiguityResolutionTest.testUnknownMerge(LEFT);
        MethodBindingAmbiguityResolutionTest.testUnknownMerge(RIGHT);
        MethodBindingAmbiguityResolutionTest.testUnknownMerge(UNKNOWN);
    }

    @Test
    public void testSelfMerge() throws Exception {
        MethodBindingAmbiguityResolutionTest.testSelfMerge(LEFT);
        MethodBindingAmbiguityResolutionTest.testSelfMerge(RIGHT);
        MethodBindingAmbiguityResolutionTest.testSelfMerge(UNKNOWN);
    }

    @Test
    public void testConflictMerge() throws Exception {
        MethodBindingAmbiguityResolutionTest.testConflictMerge(LEFT, RIGHT);
        MethodBindingAmbiguityResolutionTest.testConflictMerge(LEFT, AMBIGUOUS);
        MethodBindingAmbiguityResolutionTest.testConflictMerge(AMBIGUOUS, LEFT);
        MethodBindingAmbiguityResolutionTest.testConflictMerge(RIGHT, LEFT);
    }
}

