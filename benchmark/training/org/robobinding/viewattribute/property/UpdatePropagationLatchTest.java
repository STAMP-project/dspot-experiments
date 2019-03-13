package org.robobinding.viewattribute.property;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public class UpdatePropagationLatchTest {
    private UpdatePropagationLatch latch = new UpdatePropagationLatch();

    @Test
    public void givenLatchIsOn_whenTryToPass_thenFailed() {
        latch.turnOn();
        Assert.assertFalse(latch.tryToPass());
    }

    @Test
    public void givenLatchIsOn_whenTryToPassAfterFirstAttempt_thenAllAreSuccessful() {
        latch.turnOn();
        latch.tryToPass();
        assertAllTryToPassAttemptsAreSuccessful(anyNumAttempts());
    }

    @Test
    public void givenLatchIsOff_whenTryToPass_thenAllAreSuccessful() {
        latch.turnOff();
        assertAllTryToPassAttemptsAreSuccessful(anyNumAttempts());
    }
}

