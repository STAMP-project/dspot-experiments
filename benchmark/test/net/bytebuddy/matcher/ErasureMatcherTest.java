package net.bytebuddy.matcher;


import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;


public class ErasureMatcherTest extends AbstractElementMatcherTest<ErasureMatcher<?>> {
    @Mock
    private TypeDefinition typeDefinition;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private ElementMatcher<TypeDescription> elementMatcher;

    @SuppressWarnings("unchecked")
    public ErasureMatcherTest() {
        super(((Class<? extends ErasureMatcher<?>>) ((Object) (ErasureMatcher.class))), "erasure");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(elementMatcher.matches(typeDescription)).thenReturn(true);
        Mockito.when(typeDefinition.getSort()).thenReturn(NON_GENERIC);
        MatcherAssert.assertThat(new ErasureMatcher<TypeDefinition>(elementMatcher).matches(typeDefinition), CoreMatchers.is(true));
        Mockito.verify(typeDefinition).asErasure();
        Mockito.verifyNoMoreInteractions(typeDefinition);
        Mockito.verify(elementMatcher).matches(typeDescription);
        Mockito.verifyNoMoreInteractions(elementMatcher);
        Mockito.verifyZeroInteractions(typeDescription);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(elementMatcher.matches(typeDescription)).thenReturn(false);
        Mockito.when(typeDefinition.getSort()).thenReturn(NON_GENERIC);
        MatcherAssert.assertThat(new ErasureMatcher<TypeDefinition>(elementMatcher).matches(typeDefinition), CoreMatchers.is(false));
        Mockito.verify(typeDefinition).asErasure();
        Mockito.verifyNoMoreInteractions(typeDefinition);
        Mockito.verify(elementMatcher).matches(typeDescription);
        Mockito.verifyNoMoreInteractions(elementMatcher);
        Mockito.verifyZeroInteractions(typeDescription);
    }
}

