package net.bytebuddy.implementation.bytecode;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class StackManipulationSizeTest {
    @Test
    public void testSizeGrowth() throws Exception {
        StackManipulation.Size first = new StackManipulation.Size(2, 5);
        StackManipulation.Size second = new StackManipulation.Size(1, 1);
        StackManipulation.Size merged = first.aggregate(second);
        MatcherAssert.assertThat(merged.getSizeImpact(), CoreMatchers.is(3));
        MatcherAssert.assertThat(merged.getMaximalSize(), CoreMatchers.is(5));
    }

    @Test
    public void testSizeReduction() throws Exception {
        StackManipulation.Size first = new StackManipulation.Size((-3), 0);
        StackManipulation.Size second = new StackManipulation.Size((-2), 0);
        StackManipulation.Size merged = first.aggregate(second);
        MatcherAssert.assertThat(merged.getSizeImpact(), CoreMatchers.is((-5)));
        MatcherAssert.assertThat(merged.getMaximalSize(), CoreMatchers.is(0));
    }

    @Test
    public void testSizeGrowthAndReduction() throws Exception {
        StackManipulation.Size first = new StackManipulation.Size(3, 4);
        StackManipulation.Size second = new StackManipulation.Size((-5), 1);
        StackManipulation.Size merged = first.aggregate(second);
        MatcherAssert.assertThat(merged.getSizeImpact(), CoreMatchers.is((-2)));
        MatcherAssert.assertThat(merged.getMaximalSize(), CoreMatchers.is(4));
    }
}

