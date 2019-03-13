package net.bytebuddy.description.modifier;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.Opcodes;


@RunWith(Parameterized.class)
public class MethodManifestationTest extends AbstractModifierContributorTest {
    public MethodManifestationTest(ModifierContributor modifierContributor, int expectedModifier, boolean defaultModifier) {
        super(modifierContributor, expectedModifier, defaultModifier);
    }

    @Test
    public void testProperties() throws Exception {
        MatcherAssert.assertThat(((MethodManifestation) (modifierContributor)).isAbstract(), CoreMatchers.is((((expectedModifier) & (Opcodes.ACC_ABSTRACT)) != 0)));
        MatcherAssert.assertThat(((MethodManifestation) (modifierContributor)).isBridge(), CoreMatchers.is((((expectedModifier) & (Opcodes.ACC_BRIDGE)) != 0)));
        MatcherAssert.assertThat(((MethodManifestation) (modifierContributor)).isFinal(), CoreMatchers.is((((expectedModifier) & (Opcodes.ACC_FINAL)) != 0)));
        MatcherAssert.assertThat(((MethodManifestation) (modifierContributor)).isNative(), CoreMatchers.is((((expectedModifier) & (Opcodes.ACC_NATIVE)) != 0)));
    }
}

