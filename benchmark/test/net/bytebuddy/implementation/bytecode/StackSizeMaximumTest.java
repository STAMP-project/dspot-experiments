package net.bytebuddy.implementation.bytecode;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class StackSizeMaximumTest {
    private final StackSize first;

    private final StackSize second;

    private final StackSize expected;

    public StackSizeMaximumTest(StackSize first, StackSize second, StackSize expected) {
        this.first = first;
        this.second = second;
        this.expected = expected;
    }

    @Test
    public void testMaximum() throws Exception {
        MatcherAssert.assertThat(first.maximum(second), CoreMatchers.is(expected));
    }
}

