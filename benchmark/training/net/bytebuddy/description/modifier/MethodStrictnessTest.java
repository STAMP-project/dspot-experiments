package net.bytebuddy.description.modifier;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MethodStrictnessTest extends AbstractModifierContributorTest {
    public MethodStrictnessTest(ModifierContributor modifierContributor, int expectedModifier, boolean defaultModifier) {
        super(modifierContributor, expectedModifier, defaultModifier);
    }

    @Test
    public void testState() throws Exception {
        MatcherAssert.assertThat(((MethodStrictness) (modifierContributor)).isStrict(), CoreMatchers.is(((expectedModifier) != 0)));
    }
}

