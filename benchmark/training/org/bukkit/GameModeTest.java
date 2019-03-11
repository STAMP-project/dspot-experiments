package org.bukkit;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GameModeTest {
    @Test
    public void getByValue() {
        for (GameMode gameMode : GameMode.values()) {
            Assert.assertThat(GameMode.getByValue(gameMode.getValue()), CoreMatchers.is(gameMode));
        }
    }
}

