package net.bytebuddy.implementation.bytecode;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ByteCodeAppenderSizeTest {
    private static final int LOWER = 3;

    private static final int BIGGER = 5;

    @Test
    public void testMerge() throws Exception {
        ByteCodeAppender.Size left = new ByteCodeAppender.Size(ByteCodeAppenderSizeTest.LOWER, ByteCodeAppenderSizeTest.BIGGER);
        ByteCodeAppender.Size right = new ByteCodeAppender.Size(ByteCodeAppenderSizeTest.BIGGER, ByteCodeAppenderSizeTest.LOWER);
        ByteCodeAppender.Size mergedLeft = left.merge(right);
        ByteCodeAppender.Size mergedRight = right.merge(left);
        MatcherAssert.assertThat(mergedLeft.getOperandStackSize(), CoreMatchers.is(ByteCodeAppenderSizeTest.BIGGER));
        MatcherAssert.assertThat(mergedLeft.getLocalVariableSize(), CoreMatchers.is(ByteCodeAppenderSizeTest.BIGGER));
        MatcherAssert.assertThat(mergedRight.getOperandStackSize(), CoreMatchers.is(ByteCodeAppenderSizeTest.BIGGER));
        MatcherAssert.assertThat(mergedRight.getLocalVariableSize(), CoreMatchers.is(ByteCodeAppenderSizeTest.BIGGER));
    }
}

