package net.bytebuddy.matcher;


import net.bytebuddy.description.ByteCodeElement;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DescriptorMatcherTest extends AbstractElementMatcherTest<DescriptorMatcher<?>> {
    private static final String FOO = "foo";

    @Mock
    private ElementMatcher<String> descriptorMatcher;

    @Mock
    private ByteCodeElement byteCodeElement;

    @SuppressWarnings("unchecked")
    public DescriptorMatcherTest() {
        super(((Class<DescriptorMatcher<?>>) ((Object) (DescriptorMatcher.class))), "hasDescriptor");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(descriptorMatcher.matches(DescriptorMatcherTest.FOO)).thenReturn(true);
        MatcherAssert.assertThat(new DescriptorMatcher<ByteCodeElement>(descriptorMatcher).matches(byteCodeElement), CoreMatchers.is(true));
        Mockito.verify(descriptorMatcher).matches(DescriptorMatcherTest.FOO);
        Mockito.verifyNoMoreInteractions(descriptorMatcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(descriptorMatcher.matches(DescriptorMatcherTest.FOO)).thenReturn(false);
        MatcherAssert.assertThat(new DescriptorMatcher<ByteCodeElement>(descriptorMatcher).matches(byteCodeElement), CoreMatchers.is(false));
        Mockito.verify(descriptorMatcher).matches(DescriptorMatcherTest.FOO);
        Mockito.verifyNoMoreInteractions(descriptorMatcher);
    }
}

