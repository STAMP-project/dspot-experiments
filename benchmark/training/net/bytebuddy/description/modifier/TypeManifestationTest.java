package net.bytebuddy.description.modifier;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.Opcodes;


@RunWith(Parameterized.class)
public class TypeManifestationTest extends AbstractModifierContributorTest {
    public TypeManifestationTest(ModifierContributor modifierContributor, int expectedModifier, boolean defaultModifier) {
        super(modifierContributor, expectedModifier, defaultModifier);
    }

    @Test
    public void testProperties() throws Exception {
        MatcherAssert.assertThat(((TypeManifestation) (modifierContributor)).isAbstract(), CoreMatchers.is(((((expectedModifier) & (Opcodes.ACC_ABSTRACT)) != 0) && (((expectedModifier) & (Opcodes.ACC_INTERFACE)) == 0))));
        MatcherAssert.assertThat(((TypeManifestation) (modifierContributor)).isFinal(), CoreMatchers.is((((expectedModifier) & (Opcodes.ACC_FINAL)) != 0)));
        MatcherAssert.assertThat(((TypeManifestation) (modifierContributor)).isInterface(), CoreMatchers.is((((expectedModifier) & (Opcodes.ACC_INTERFACE)) != 0)));
        MatcherAssert.assertThat(((TypeManifestation) (modifierContributor)).isAnnotation(), CoreMatchers.is((((expectedModifier) & (Opcodes.ACC_ANNOTATION)) != 0)));
    }
}

