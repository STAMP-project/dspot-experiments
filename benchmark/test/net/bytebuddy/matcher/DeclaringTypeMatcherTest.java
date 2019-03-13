package net.bytebuddy.matcher;


import net.bytebuddy.description.DeclaredByType;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DeclaringTypeMatcherTest extends AbstractElementMatcherTest<DeclaringTypeMatcher<?>> {
    @Mock
    private ElementMatcher<? super TypeDescription.Generic> typeMatcher;

    @Mock
    private DeclaredByType declaredByType;

    @Mock
    private TypeDescription.Generic typeDescription;

    @SuppressWarnings("unchecked")
    public DeclaringTypeMatcherTest() {
        super(((Class<DeclaringTypeMatcher<?>>) ((Object) (DeclaringTypeMatcher.class))), "declaredBy");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(declaredByType.getDeclaringType()).thenReturn(typeDescription);
        Mockito.when(typeMatcher.matches(typeDescription)).thenReturn(true);
        MatcherAssert.assertThat(new DeclaringTypeMatcher<DeclaredByType>(typeMatcher).matches(declaredByType), CoreMatchers.is(true));
        Mockito.verify(typeMatcher).matches(typeDescription);
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verify(declaredByType).getDeclaringType();
        Mockito.verifyNoMoreInteractions(declaredByType);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(declaredByType.getDeclaringType()).thenReturn(typeDescription);
        Mockito.when(typeMatcher.matches(typeDescription)).thenReturn(false);
        MatcherAssert.assertThat(new DeclaringTypeMatcher<DeclaredByType>(typeMatcher).matches(declaredByType), CoreMatchers.is(false));
        Mockito.verify(typeMatcher).matches(typeDescription);
        Mockito.verifyNoMoreInteractions(typeMatcher);
        Mockito.verify(declaredByType).getDeclaringType();
        Mockito.verifyNoMoreInteractions(declaredByType);
    }

    @Test
    public void testNoMatchWhenNull() throws Exception {
        MatcherAssert.assertThat(new DeclaringTypeMatcher<DeclaredByType>(typeMatcher).matches(declaredByType), CoreMatchers.is(false));
        Mockito.verifyZeroInteractions(typeMatcher);
        Mockito.verify(declaredByType).getDeclaringType();
        Mockito.verifyNoMoreInteractions(declaredByType);
    }
}

