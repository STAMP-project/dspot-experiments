package org.bukkit;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class WorldTypeTest {
    @Test
    public void getByName() {
        for (WorldType worldType : WorldType.values()) {
            Assert.assertThat(WorldType.getByName(worldType.getName()), CoreMatchers.is(worldType));
        }
    }
}

