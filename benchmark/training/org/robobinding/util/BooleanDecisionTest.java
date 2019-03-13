package org.robobinding.util;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public class BooleanDecisionTest {
    @Test
    public void whenOrTwoFalse_thenResultIsFalse() {
        BooleanDecision decision = new BooleanDecision();
        decision.or(false).or(false);
        Assert.assertThat(decision.getResult(), Matchers.is(false));
    }

    @Test
    public void whenOrFalseTrueFalse_thenResultIsTrue() {
        BooleanDecision decision = new BooleanDecision();
        decision.or(false).or(true).or(false);
        Assert.assertThat(decision.getResult(), Matchers.is(true));
    }
}

