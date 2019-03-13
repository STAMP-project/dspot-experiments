package net.bytebuddy.matcher;


import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class SubTypeMatcherTest extends AbstractElementMatcherTest<SubTypeMatcher<?>> {
    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription otherType;

    @SuppressWarnings("unchecked")
    public SubTypeMatcherTest() {
        super(((Class<? extends SubTypeMatcher<?>>) ((Object) (SubTypeMatcher.class))), "isSubTypeOf");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(otherType.isAssignableTo(typeDescription)).thenReturn(true);
        MatcherAssert.assertThat(new SubTypeMatcher<TypeDescription>(typeDescription).matches(otherType), CoreMatchers.is(true));
        Mockito.verify(otherType).isAssignableTo(typeDescription);
        Mockito.verifyNoMoreInteractions(otherType);
        Mockito.verifyZeroInteractions(typeDescription);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(otherType.isAssignableTo(typeDescription)).thenReturn(false);
        MatcherAssert.assertThat(new SubTypeMatcher<TypeDescription>(typeDescription).matches(otherType), CoreMatchers.is(false));
        Mockito.verify(otherType).isAssignableTo(typeDescription);
        Mockito.verifyNoMoreInteractions(otherType);
        Mockito.verifyZeroInteractions(typeDescription);
    }
}

