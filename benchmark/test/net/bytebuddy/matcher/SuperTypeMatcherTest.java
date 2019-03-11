package net.bytebuddy.matcher;


import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class SuperTypeMatcherTest extends AbstractElementMatcherTest<SuperTypeMatcher<?>> {
    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription otherType;

    @SuppressWarnings("unchecked")
    public SuperTypeMatcherTest() {
        super(((Class<? extends SuperTypeMatcher<?>>) ((Object) (SuperTypeMatcher.class))), "isSuperTypeOf");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(otherType.isAssignableFrom(typeDescription)).thenReturn(true);
        MatcherAssert.assertThat(new SuperTypeMatcher<TypeDescription>(typeDescription).matches(otherType), CoreMatchers.is(true));
        Mockito.verify(otherType).isAssignableFrom(typeDescription);
        Mockito.verifyNoMoreInteractions(otherType);
        Mockito.verifyZeroInteractions(typeDescription);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(otherType.isAssignableFrom(typeDescription)).thenReturn(false);
        MatcherAssert.assertThat(new SuperTypeMatcher<TypeDescription>(typeDescription).matches(otherType), CoreMatchers.is(false));
        Mockito.verify(otherType).isAssignableFrom(typeDescription);
        Mockito.verifyNoMoreInteractions(otherType);
        Mockito.verifyZeroInteractions(typeDescription);
    }
}

