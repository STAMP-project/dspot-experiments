package com.baeldung;


import com.baeldung.adapter.AstonMartin;
import com.baeldung.adapter.BugattiVeyron;
import com.baeldung.adapter.McLaren;
import com.baeldung.adapter.Movable;
import com.baeldung.adapter.MovableAdapter;
import org.junit.Assert;
import org.junit.Test;


public class AdapterPatternIntegrationTest {
    @Test
    public void givenMovableAdapter_WhenConvertingMPHToKMPH_thenSuccessfullyConverted() {
        Movable bugattiVeyron = new BugattiVeyron();
        MovableAdapter bugattiVeyronAdapter = new com.baeldung.adapter.MovableAdapterImpl(bugattiVeyron);
        Assert.assertEquals(bugattiVeyronAdapter.getSpeed(), 431.30312, 1.0E-5);
        Movable mcLaren = new McLaren();
        MovableAdapter mcLarenAdapter = new com.baeldung.adapter.MovableAdapterImpl(mcLaren);
        Assert.assertEquals(mcLarenAdapter.getSpeed(), 387.85094, 1.0E-5);
        Movable astonMartin = new AstonMartin();
        MovableAdapter astonMartinAdapter = new com.baeldung.adapter.MovableAdapterImpl(astonMartin);
        Assert.assertEquals(astonMartinAdapter.getSpeed(), 354.0548, 1.0E-5);
    }
}

