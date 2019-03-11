package net.bytebuddy.matcher;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class LatentMatcherDisjunctionTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private LatentMatcher<?> left;

    @Mock
    private LatentMatcher<?> right;

    @Mock
    private ElementMatcher<?> leftMatcher;

    @Mock
    private ElementMatcher<?> rightMatcher;

    @Mock
    private TypeDescription typeDescription;

    @Test
    @SuppressWarnings("unchecked")
    public void testManifestation() throws Exception {
        MatcherAssert.assertThat(new LatentMatcher.Disjunction(left, right).resolve(typeDescription), FieldByFieldComparison.hasPrototype(((ElementMatcher) (ElementMatchers.none().or(((ElementMatcher) (leftMatcher))).or(((ElementMatcher) (rightMatcher)))))));
    }
}

