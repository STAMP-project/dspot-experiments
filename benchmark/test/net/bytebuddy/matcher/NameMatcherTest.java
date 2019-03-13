package net.bytebuddy.matcher;


import net.bytebuddy.description.NamedElement;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class NameMatcherTest extends AbstractElementMatcherTest<NameMatcher<?>> {
    private static final String FOO = "foo";

    @Mock
    private NamedElement namedElement;

    @Mock
    private ElementMatcher<String> nameMatcher;

    @SuppressWarnings("unchecked")
    public NameMatcherTest() {
        super(((Class<NameMatcher<?>>) ((Object) (NameMatcher.class))), "name");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(nameMatcher.matches(NameMatcherTest.FOO)).thenReturn(true);
        MatcherAssert.assertThat(new NameMatcher<NamedElement>(nameMatcher).matches(namedElement), CoreMatchers.is(true));
        Mockito.verify(nameMatcher).matches(NameMatcherTest.FOO);
        Mockito.verifyNoMoreInteractions(nameMatcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(nameMatcher.matches(NameMatcherTest.FOO)).thenReturn(false);
        MatcherAssert.assertThat(new NameMatcher<NamedElement>(nameMatcher).matches(namedElement), CoreMatchers.is(false));
        Mockito.verify(nameMatcher).matches(NameMatcherTest.FOO);
        Mockito.verifyNoMoreInteractions(nameMatcher);
    }
}

