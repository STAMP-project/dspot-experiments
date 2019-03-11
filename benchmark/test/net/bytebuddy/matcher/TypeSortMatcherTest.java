package net.bytebuddy.matcher;


import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;


public class TypeSortMatcherTest extends AbstractElementMatcherTest<TypeSortMatcher<?>> {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic typeDescription;

    @Mock
    private ElementMatcher<TypeDefinition.Sort> matcher;

    @SuppressWarnings("unchecked")
    public TypeSortMatcherTest() {
        super(((Class<TypeSortMatcher<?>>) ((Object) (TypeSortMatcher.class))), "ofSort");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(typeDescription.getSort()).thenReturn(NON_GENERIC);
        Mockito.when(matcher.matches(NON_GENERIC)).thenReturn(true);
        MatcherAssert.assertThat(new TypeSortMatcher<TypeDescription.Generic>(matcher).matches(typeDescription), CoreMatchers.is(true));
        Mockito.verify(typeDescription).getSort();
        Mockito.verifyNoMoreInteractions(typeDescription);
        Mockito.verify(matcher).matches(NON_GENERIC);
        Mockito.verifyNoMoreInteractions(matcher);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(typeDescription.getSort()).thenReturn(NON_GENERIC);
        Mockito.when(matcher.matches(NON_GENERIC)).thenReturn(false);
        MatcherAssert.assertThat(new TypeSortMatcher<TypeDescription.Generic>(matcher).matches(typeDescription), CoreMatchers.is(false));
        Mockito.verify(typeDescription).getSort();
        Mockito.verifyNoMoreInteractions(typeDescription);
        Mockito.verify(matcher).matches(NON_GENERIC);
        Mockito.verifyNoMoreInteractions(matcher);
    }
}

