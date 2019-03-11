package net.bytebuddy.matcher;


import net.bytebuddy.description.ModifierReviewable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
public class ModifierMatcherTest extends AbstractElementMatcherTest<ModifierMatcher<?>> {
    private final ModifierMatcher.Mode mode;

    private final int modifiers;

    @Mock
    private ModifierReviewable modifierReviewable;

    @SuppressWarnings("unchecked")
    public ModifierMatcherTest(ModifierMatcher.Mode mode, int modifiers) {
        super(((Class<ModifierMatcher<?>>) ((Object) (ModifierMatcher.class))), mode.getDescription());
        this.mode = mode;
        this.modifiers = modifiers;
    }

    @Test
    public void testMatch() throws Exception {
        Mockito.when(modifierReviewable.getModifiers()).thenReturn(modifiers);
        MatcherAssert.assertThat(new ModifierMatcher<ModifierReviewable>(mode).matches(modifierReviewable), CoreMatchers.is(true));
        Mockito.verify(modifierReviewable).getModifiers();
        Mockito.verifyNoMoreInteractions(modifierReviewable);
    }

    @Test
    public void testNoMatch() throws Exception {
        Mockito.when(modifierReviewable.getModifiers()).thenReturn(0);
        MatcherAssert.assertThat(new ModifierMatcher<ModifierReviewable>(mode).matches(modifierReviewable), CoreMatchers.is(false));
        Mockito.verify(modifierReviewable).getModifiers();
        Mockito.verifyNoMoreInteractions(modifierReviewable);
    }

    @Test
    public void testStringRepresentation() throws Exception {
        MatcherAssert.assertThat(new ModifierMatcher<ModifierReviewable>(mode).toString(), CoreMatchers.is(mode.getDescription()));
    }
}

