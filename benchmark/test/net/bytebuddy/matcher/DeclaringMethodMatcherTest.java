package net.bytebuddy.matcher;


import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class DeclaringMethodMatcherTest extends AbstractElementMatcherTest<DeclaringMethodMatcher<?>> {
    @Mock
    private ElementMatcher<? super MethodList<?>> methodMatcher;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private MethodList<?> methodList;

    @SuppressWarnings("unchecked")
    public DeclaringMethodMatcherTest() {
        super(((Class<DeclaringMethodMatcher<?>>) ((Object) (DeclaringMethodMatcher.class))), "declaresMethods");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMatch() throws Exception {
        Mockito.when(typeDescription.getDeclaredMethods()).thenReturn(((MethodList) (methodList)));
        Mockito.when(methodMatcher.matches(methodList)).thenReturn(true);
        MatcherAssert.assertThat(new DeclaringMethodMatcher<TypeDescription>(methodMatcher).matches(typeDescription), CoreMatchers.is(true));
        Mockito.verify(methodMatcher).matches(methodList);
        Mockito.verifyNoMoreInteractions(methodMatcher);
        Mockito.verify(typeDescription).getDeclaredMethods();
        Mockito.verifyNoMoreInteractions(typeDescription);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoMatch() throws Exception {
        Mockito.when(typeDescription.getDeclaredMethods()).thenReturn(((MethodList) (methodList)));
        Mockito.when(methodMatcher.matches(methodList)).thenReturn(false);
        MatcherAssert.assertThat(new DeclaringMethodMatcher<TypeDescription>(methodMatcher).matches(typeDescription), CoreMatchers.is(false));
        Mockito.verify(methodMatcher).matches(methodList);
        Mockito.verifyNoMoreInteractions(methodMatcher);
        Mockito.verify(typeDescription).getDeclaredMethods();
        Mockito.verifyNoMoreInteractions(typeDescription);
    }
}

