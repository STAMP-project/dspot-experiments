package net.bytebuddy.dynamic.scaffold;


import java.util.Arrays;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.matcher.AbstractFilterableListTest;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class MethodGraphNodeListTest extends AbstractFilterableListTest<MethodGraph.Node, MethodGraph.NodeList, MethodGraph.Node> {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription first;

    @Mock
    private MethodDescription second;

    private MethodGraph.Node firstNode;

    private MethodGraph.Node secondNode;

    @Test
    @SuppressWarnings("unused")
    public void testAsMethodList() throws Exception {
        MatcherAssert.assertThat(new MethodGraph.NodeList(Arrays.asList(new MethodGraph.Node.Simple(first), new MethodGraph.Node.Simple(second))).asMethodList(), CoreMatchers.is(((MethodList) (new MethodList.Explicit<MethodDescription>(first, second)))));
    }
}

