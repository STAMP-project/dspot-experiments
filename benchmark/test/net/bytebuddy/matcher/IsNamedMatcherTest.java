package net.bytebuddy.matcher;


import net.bytebuddy.description.NamedElement;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class IsNamedMatcherTest extends AbstractElementMatcherTest<IsNamedMatcher<?>> {
    @Mock
    private NamedElement.WithOptionalName namedElement;

    @SuppressWarnings("unchecked")
    public IsNamedMatcherTest() {
        super(((Class<IsNamedMatcher<?>>) ((Object) (IsNamedMatcher.class))), "isNamed");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(namedElement.isNamed()).thenReturn(true);
        MatcherAssert.assertThat(new IsNamedMatcher<NamedElement.WithOptionalName>().matches(namedElement), CoreMatchers.is(true));
    }

    @Test
    public void testPositiveToNegative() throws Exception {
        MatcherAssert.assertThat(new IsNamedMatcher<NamedElement.WithOptionalName>().matches(namedElement), CoreMatchers.is(false));
    }
}

