package net.bytebuddy.matcher;


import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class AccessibilityMatcherTest extends AbstractElementMatcherTest<AccessibilityMatcher<?>> {
    @Mock
    private TypeDescription typeDescription;

    @Mock
    private ByteCodeElement byteCodeElement;

    @SuppressWarnings("unchecked")
    public AccessibilityMatcherTest() {
        super(((Class<? extends AccessibilityMatcher<?>>) ((Object) (AccessibilityMatcher.class))), "isAccessibleTo");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(byteCodeElement.isAccessibleTo(typeDescription)).thenReturn(true);
        MatcherAssert.assertThat(new AccessibilityMatcher<ByteCodeElement>(typeDescription).matches(byteCodeElement), CoreMatchers.is(true));
        Mockito.verify(byteCodeElement).isAccessibleTo(typeDescription);
        Mockito.verifyNoMoreInteractions(byteCodeElement);
        Mockito.verifyZeroInteractions(typeDescription);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(byteCodeElement.isAccessibleTo(typeDescription)).thenReturn(false);
        MatcherAssert.assertThat(new AccessibilityMatcher<ByteCodeElement>(typeDescription).matches(byteCodeElement), CoreMatchers.is(false));
        Mockito.verify(byteCodeElement).isAccessibleTo(typeDescription);
        Mockito.verifyNoMoreInteractions(byteCodeElement);
        Mockito.verifyZeroInteractions(typeDescription);
    }
}

