package net.bytebuddy.implementation.bytecode;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class StackSizeTest {
    private final Class<?> type;

    private final int size;

    private final StackSize stackSize;

    public StackSizeTest(Class<?> type, int size, StackSize stackSize) {
        this.type = type;
        this.size = size;
        this.stackSize = stackSize;
    }

    @Test
    public void testStackSize() throws Exception {
        MatcherAssert.assertThat(StackSize.of(type), CoreMatchers.is(stackSize));
    }

    @Test
    public void testStackSizeValue() throws Exception {
        MatcherAssert.assertThat(StackSize.of(type).getSize(), CoreMatchers.is(size));
    }

    @Test
    public void testStackSizeResolution() throws Exception {
        MatcherAssert.assertThat(StackSize.of(size), CoreMatchers.is(stackSize));
    }
}

