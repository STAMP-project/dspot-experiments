package net.bytebuddy.implementation.bytecode.assign;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.DYNAMIC;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.STATIC;
import static net.bytebuddy.implementation.bytecode.assign.Assigner.Typing.of;


public class AssignerTypingTest {
    @Test
    public void testStatic() throws Exception {
        MatcherAssert.assertThat(of(false), CoreMatchers.is(STATIC));
        MatcherAssert.assertThat(STATIC.isDynamic(), CoreMatchers.is(false));
    }

    @Test
    public void testDynamic() throws Exception {
        MatcherAssert.assertThat(of(true), CoreMatchers.is(DYNAMIC));
        MatcherAssert.assertThat(DYNAMIC.isDynamic(), CoreMatchers.is(true));
    }
}

