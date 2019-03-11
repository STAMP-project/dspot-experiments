package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.dynamic.scaffold.MethodGraph.Node.Sort.RESOLVED;


public class MethodGraphNodeSimpleTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription methodDescription;

    @Test
    public void testRepresentative() throws Exception {
        MatcherAssert.assertThat(new MethodGraph.Node.Simple(methodDescription).getRepresentative(), CoreMatchers.is(methodDescription));
    }

    @Test
    public void testBridgesEmpty() throws Exception {
        MatcherAssert.assertThat(new MethodGraph.Node.Simple(methodDescription).getMethodTypes().size(), CoreMatchers.is(0));
    }

    @Test
    public void testSort() throws Exception {
        MatcherAssert.assertThat(new MethodGraph.Node.Simple(methodDescription).getSort(), CoreMatchers.is(RESOLVED));
    }
}

