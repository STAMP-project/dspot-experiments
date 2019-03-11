package net.bytebuddy.description.modifier;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class VisibilityTest extends AbstractModifierContributorTest {
    private final boolean isPublic;

    private final boolean isProtected;

    private final boolean isPackagePrivate;

    private final boolean isPrivate;

    public VisibilityTest(ModifierContributor modifierContributor, int expectedModifier, boolean defaultModifier, boolean isPublic, boolean isProtected, boolean isPackagePrivate, boolean isPrivate) {
        super(modifierContributor, expectedModifier, defaultModifier);
        this.isPublic = isPublic;
        this.isProtected = isProtected;
        this.isPackagePrivate = isPackagePrivate;
        this.isPrivate = isPrivate;
    }

    @Test
    public void testProperties() throws Exception {
        MatcherAssert.assertThat(((Visibility) (modifierContributor)).isPublic(), CoreMatchers.is(isPublic));
        MatcherAssert.assertThat(((Visibility) (modifierContributor)).isPackagePrivate(), CoreMatchers.is(isPackagePrivate));
        MatcherAssert.assertThat(((Visibility) (modifierContributor)).isProtected(), CoreMatchers.is(isProtected));
        MatcherAssert.assertThat(((Visibility) (modifierContributor)).isPrivate(), CoreMatchers.is(isPrivate));
    }
}

