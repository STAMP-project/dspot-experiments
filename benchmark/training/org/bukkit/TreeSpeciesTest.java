package org.bukkit;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TreeSpeciesTest {
    @Test
    public void getByData() {
        for (TreeSpecies treeSpecies : TreeSpecies.values()) {
            Assert.assertThat(TreeSpecies.getByData(treeSpecies.getData()), CoreMatchers.is(treeSpecies));
        }
    }
}

