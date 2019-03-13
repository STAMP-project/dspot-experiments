package org.bukkit;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class CoalTypeTest {
    @Test
    public void getByData() {
        for (CoalType coalType : CoalType.values()) {
            Assert.assertThat(CoalType.getByData(coalType.getData()), CoreMatchers.is(coalType));
        }
    }
}

