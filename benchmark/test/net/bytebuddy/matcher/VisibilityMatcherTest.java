package net.bytebuddy.matcher;


import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class VisibilityMatcherTest extends AbstractElementMatcherTest<VisibilityMatcher<?>> {
    @Mock
    private TypeDescription typeDescription;

    @Mock
    private ByteCodeElement byteCodeElement;

    @SuppressWarnings("unchecked")
    public VisibilityMatcherTest() {
        super(((Class<? extends VisibilityMatcher<?>>) ((Object) (VisibilityMatcher.class))), "isVisibleTo");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(byteCodeElement.isVisibleTo(typeDescription)).thenReturn(true);
        MatcherAssert.assertThat(new VisibilityMatcher<ByteCodeElement>(typeDescription).matches(byteCodeElement), CoreMatchers.is(true));
        Mockito.verify(byteCodeElement).isVisibleTo(typeDescription);
        Mockito.verifyNoMoreInteractions(byteCodeElement);
        Mockito.verifyZeroInteractions(typeDescription);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(byteCodeElement.isVisibleTo(typeDescription)).thenReturn(false);
        MatcherAssert.assertThat(new VisibilityMatcher<ByteCodeElement>(typeDescription).matches(byteCodeElement), CoreMatchers.is(false));
        Mockito.verify(byteCodeElement).isVisibleTo(typeDescription);
        Mockito.verifyNoMoreInteractions(byteCodeElement);
        Mockito.verifyZeroInteractions(typeDescription);
    }
}

