package org.bukkit;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class DifficultyTest {
    @Test
    public void getByValue() {
        for (Difficulty difficulty : Difficulty.values()) {
            Assert.assertThat(Difficulty.getByValue(difficulty.getValue()), CoreMatchers.is(difficulty));
        }
    }
}

