package org.bukkit;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class EffectTest {
    @Test
    public void getById() {
        for (Effect effect : Effect.values()) {
            Assert.assertThat(Effect.getById(effect.getId()), CoreMatchers.is(effect));
        }
    }
}

