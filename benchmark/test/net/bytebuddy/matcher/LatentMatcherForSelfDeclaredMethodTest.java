package net.bytebuddy.matcher;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import net.bytebuddy.test.utility.MockitoRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.matcher.LatentMatcher.ForSelfDeclaredMethod.DECLARED;
import static net.bytebuddy.matcher.LatentMatcher.ForSelfDeclaredMethod.NOT_DECLARED;


public class LatentMatcherForSelfDeclaredMethodTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    @Test
    @SuppressWarnings("unchecked")
    public void testDeclared() throws Exception {
        Assert.assertThat(DECLARED.resolve(typeDescription), FieldByFieldComparison.hasPrototype(((ElementMatcher) (ElementMatchers.isDeclaredBy(typeDescription)))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotDeclared() throws Exception {
        Assert.assertThat(NOT_DECLARED.resolve(typeDescription), FieldByFieldComparison.hasPrototype(((ElementMatcher) (ElementMatchers.not(ElementMatchers.isDeclaredBy(typeDescription))))));
    }
}

