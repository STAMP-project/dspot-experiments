package net.bytebuddy.description.modifier;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public abstract class AbstractModifierContributorTest {
    protected final ModifierContributor modifierContributor;

    protected final int expectedModifier;

    protected final boolean defaultModifier;

    public AbstractModifierContributorTest(ModifierContributor modifierContributor, int expectedModifier, boolean defaultModifier) {
        this.modifierContributor = modifierContributor;
        this.expectedModifier = expectedModifier;
        this.defaultModifier = defaultModifier;
    }

    @Test
    public void testModifierContributor() throws Exception {
        MatcherAssert.assertThat(modifierContributor.getMask(), CoreMatchers.is(expectedModifier));
    }

    @Test
    public void testDefaultModifier() throws Exception {
        MatcherAssert.assertThat(modifierContributor.isDefault(), CoreMatchers.is(defaultModifier));
    }
}

