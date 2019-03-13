package net.bytebuddy.dynamic.scaffold;


import java.util.Collections;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.dynamic.scaffold.MethodGraph.Simple.of;


public class MethodGraphSimpleTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription methodDescription;

    @Mock
    private MethodDescription.SignatureToken token;

    @Test
    public void testNodeList() throws Exception {
        MatcherAssert.assertThat(of(Collections.singletonList(methodDescription)).listNodes().getOnly(), FieldByFieldComparison.hasPrototype(((MethodGraph.Node) (new MethodGraph.Node.Simple(methodDescription)))));
    }

    @Test
    public void testNodeLocation() throws Exception {
        MatcherAssert.assertThat(of(Collections.singletonList(methodDescription)).locate(token), FieldByFieldComparison.hasPrototype(((MethodGraph.Node) (new MethodGraph.Node.Simple(methodDescription)))));
    }
}

