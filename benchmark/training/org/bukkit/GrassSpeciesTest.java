package org.bukkit;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class GrassSpeciesTest {
    @Test
    public void getByData() {
        for (GrassSpecies grassSpecies : GrassSpecies.values()) {
            Assert.assertThat(GrassSpecies.getByData(grassSpecies.getData()), CoreMatchers.is(grassSpecies));
        }
    }
}

