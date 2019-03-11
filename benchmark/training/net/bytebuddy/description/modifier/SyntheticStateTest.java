package net.bytebuddy.description.modifier;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class SyntheticStateTest extends AbstractModifierContributorTest {
    public SyntheticStateTest(ModifierContributor modifierContributor, int expectedModifier, boolean defaultModifier) {
        super(modifierContributor, expectedModifier, defaultModifier);
    }

    @Test
    public void testState() throws Exception {
        MatcherAssert.assertThat(((SyntheticState) (modifierContributor)).isSynthetic(), CoreMatchers.is(((expectedModifier) != 0)));
    }
}

