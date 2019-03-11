package net.bytebuddy.matcher;


import net.bytebuddy.description.ByteCodeElement;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DefinedShapeMatcherTest extends AbstractElementMatcherTest<DefinedShapeMatcher<?, ?>> {
    @Mock
    private ByteCodeElement.TypeDependant<?, ?> dependent;

    @Mock
    private ByteCodeElement.TypeDependant<?, ?> resolvedDependant;

    @Mock
    private ByteCodeElement.TypeDependant<?, ?> otherResolvedDependant;

    @Mock
    private ElementMatcher<ByteCodeElement.TypeDependant<?, ?>> matcher;

    @SuppressWarnings("unchecked")
    public DefinedShapeMatcherTest() {
        super(((Class<? extends DefinedShapeMatcher<?, ?>>) ((Object) (DefinedShapeMatcher.class))), "isDefinedAs");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMatch() throws Exception {
        Mockito.when(matcher.matches(resolvedDependant)).thenReturn(true);
        Mockito.when(dependent.asDefined()).thenReturn(((ByteCodeElement.TypeDependant) (resolvedDependant)));
        MatcherAssert.assertThat(new DefinedShapeMatcher(matcher).matches(dependent), CoreMatchers.is(true));
        Mockito.verify(dependent).asDefined();
        Mockito.verifyNoMoreInteractions(dependent);
        Mockito.verify(matcher).matches(resolvedDependant);
        Mockito.verifyNoMoreInteractions(matcher);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoMatch() throws Exception {
        Mockito.when(matcher.matches(resolvedDependant)).thenReturn(true);
        Mockito.when(dependent.asDefined()).thenReturn(((ByteCodeElement.TypeDependant) (otherResolvedDependant)));
        MatcherAssert.assertThat(new DefinedShapeMatcher(matcher).matches(dependent), CoreMatchers.is(false));
        Mockito.verify(dependent).asDefined();
        Mockito.verifyNoMoreInteractions(dependent);
        Mockito.verify(matcher).matches(otherResolvedDependant);
        Mockito.verifyNoMoreInteractions(matcher);
    }
}

