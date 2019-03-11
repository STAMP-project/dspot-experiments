package net.bytebuddy.description.modifier;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class FieldManifestationTest extends AbstractModifierContributorTest {
    private final boolean isFinal;

    private final boolean isVolatile;

    private final boolean isPlain;

    public FieldManifestationTest(ModifierContributor modifierContributor, int expectedModifier, boolean defaultModifier, boolean isFinal, boolean isVolatile, boolean isPlain) {
        super(modifierContributor, expectedModifier, defaultModifier);
        this.isFinal = isFinal;
        this.isVolatile = isVolatile;
        this.isPlain = isPlain;
    }

    @Test
    public void testProperties() throws Exception {
        MatcherAssert.assertThat(((FieldManifestation) (modifierContributor)).isFinal(), CoreMatchers.is(isFinal));
        MatcherAssert.assertThat(((FieldManifestation) (modifierContributor)).isVolatile(), CoreMatchers.is(isVolatile));
        MatcherAssert.assertThat(((FieldManifestation) (modifierContributor)).isPlain(), CoreMatchers.is(isPlain));
    }
}

