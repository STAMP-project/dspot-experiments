package org.bukkit;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class InstrumentTest {
    @Test
    public void getByType() {
        for (Instrument instrument : Instrument.values()) {
            Assert.assertThat(Instrument.getByType(instrument.getType()), CoreMatchers.is(instrument));
        }
    }
}

