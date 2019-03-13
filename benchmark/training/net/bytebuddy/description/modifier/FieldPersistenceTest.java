package net.bytebuddy.description.modifier;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class FieldPersistenceTest extends AbstractModifierContributorTest {
    private final boolean isTransient;

    public FieldPersistenceTest(ModifierContributor modifierContributor, int expectedModifier, boolean defaultModifier, boolean isTransient) {
        super(modifierContributor, expectedModifier, defaultModifier);
        this.isTransient = isTransient;
    }

    @Test
    public void testProperties() throws Exception {
        MatcherAssert.assertThat(((FieldPersistence) (modifierContributor)).isTransient(), CoreMatchers.is(isTransient));
    }
}

