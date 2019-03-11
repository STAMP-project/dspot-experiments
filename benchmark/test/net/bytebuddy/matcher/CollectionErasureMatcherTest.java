package net.bytebuddy.matcher;


import java.util.Arrays;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class CollectionErasureMatcherTest extends AbstractElementMatcherTest<CollectionErasureMatcher<?>> {
    @Mock
    private ElementMatcher<? super Iterable<? extends TypeDefinition>> matcher;

    @Mock
    private TypeDefinition first;

    @Mock
    private TypeDefinition second;

    @Mock
    private TypeDefinition other;

    @Mock
    private TypeDescription firstRaw;

    @Mock
    private TypeDescription secondRaw;

    @SuppressWarnings("unchecked")
    public CollectionErasureMatcherTest() {
        super(((Class<CollectionErasureMatcher<?>>) ((Object) (CollectionErasureMatcher.class))), "erasures");
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(matcher.matches(Arrays.asList(firstRaw, secondRaw))).thenReturn(true);
        MatcherAssert.assertThat(new CollectionErasureMatcher<Iterable<TypeDefinition>>(matcher).matches(Arrays.asList(first, second)), CoreMatchers.is(true));
        Mockito.verify(matcher).matches(Arrays.asList(firstRaw, secondRaw));
        Mockito.verifyNoMoreInteractions(matcher);
        Mockito.verify(first).asErasure();
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).asErasure();
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test
    public void testNoMatch() throws Exception {
        MatcherAssert.assertThat(new CollectionErasureMatcher<Iterable<TypeDefinition>>(matcher).matches(Arrays.asList(first, second)), CoreMatchers.is(false));
        Mockito.verify(matcher).matches(Arrays.asList(firstRaw, secondRaw));
        Mockito.verifyNoMoreInteractions(matcher);
        Mockito.verify(first).asErasure();
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).asErasure();
        Mockito.verifyNoMoreInteractions(second);
    }
}

