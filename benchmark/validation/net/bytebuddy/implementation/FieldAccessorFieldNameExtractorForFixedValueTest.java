package net.bytebuddy.implementation;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class FieldAccessorFieldNameExtractorForFixedValueTest {
    private static final String FOO = "foo";

    private static final String FOO_CAPITAL = "Foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription methodDescription;

    @Test
    public void testGetterMethod() throws Exception {
        MatcherAssert.assertThat(new FieldAccessor.FieldNameExtractor.ForFixedValue(FieldAccessorFieldNameExtractorForFixedValueTest.FOO).resolve(methodDescription), CoreMatchers.is(FieldAccessorFieldNameExtractorForFixedValueTest.FOO));
    }
}

