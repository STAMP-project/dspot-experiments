package org.bukkit;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class EntityEffectTest {
    @Test
    public void getByData() {
        for (EntityEffect entityEffect : EntityEffect.values()) {
            Assert.assertThat(EntityEffect.getByData(entityEffect.getData()), CoreMatchers.is(entityEffect));
        }
    }
}

