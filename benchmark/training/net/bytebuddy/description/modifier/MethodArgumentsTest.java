package net.bytebuddy.description.modifier;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class MethodArgumentsTest extends AbstractModifierContributorTest {
    public MethodArgumentsTest(ModifierContributor modifierContributor, int expectedModifier, boolean defaultModifier) {
        super(modifierContributor, expectedModifier, defaultModifier);
    }

    @Test
    public void testState() throws Exception {
        MatcherAssert.assertThat(((MethodArguments) (modifierContributor)).isVarArgs(), CoreMatchers.is(((expectedModifier) != 0)));
    }
}

