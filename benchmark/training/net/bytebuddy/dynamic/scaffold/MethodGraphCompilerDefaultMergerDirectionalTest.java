package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.dynamic.scaffold.MethodGraph.Compiler.Default.Merger.Directional.LEFT;
import static net.bytebuddy.dynamic.scaffold.MethodGraph.Compiler.Default.Merger.Directional.RIGHT;


public class MethodGraphCompilerDefaultMergerDirectionalTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription left;

    @Mock
    private MethodDescription right;

    @Test
    public void testLeft() throws Exception {
        MatcherAssert.assertThat(LEFT.merge(left, right), CoreMatchers.is(left));
    }

    @Test
    public void testRight() throws Exception {
        MatcherAssert.assertThat(RIGHT.merge(left, right), CoreMatchers.is(right));
    }
}

