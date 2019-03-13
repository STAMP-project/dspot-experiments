package net.bytebuddy.description.modifier;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ModifierContributorTest {
    private final Class<? extends ModifierContributor> type;

    public ModifierContributorTest(Class<? extends ModifierContributor> type) {
        this.type = type;
    }

    @Test
    public void testRange() throws Exception {
        ModifierContributor[] modifierContributor = ((ModifierContributor[]) (type.getDeclaredMethod("values").invoke(null)));
        int mask = 0;
        for (ModifierContributor contributor : modifierContributor) {
            mask |= contributor.getMask();
        }
        for (ModifierContributor contributor : modifierContributor) {
            MatcherAssert.assertThat(mask, CoreMatchers.is(contributor.getRange()));
        }
    }
}

