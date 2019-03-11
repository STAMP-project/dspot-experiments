package net.bytebuddy.description.modifier;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.Opcodes;


@RunWith(Parameterized.class)
public class EnumerationStateTest extends AbstractModifierContributorTest {
    public EnumerationStateTest(ModifierContributor modifierContributor, int expectedModifier, boolean defaultModifier) {
        super(modifierContributor, expectedModifier, defaultModifier);
    }

    @Test
    public void testProperties() throws Exception {
        MatcherAssert.assertThat(((EnumerationState) (modifierContributor)).isEnumeration(), CoreMatchers.is((((expectedModifier) & (Opcodes.ACC_ENUM)) != 0)));
    }
}

