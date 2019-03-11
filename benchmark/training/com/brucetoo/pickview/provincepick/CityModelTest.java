package com.brucetoo.pickview.provincepick;


import org.junit.Assert;
import org.junit.Test;


public class CityModelTest extends PickViewTestSupport {
    private CityModel model;

    @Test
    public void testToString() {
        // when
        model.id = PickViewTestSupport.CITY_ID;
        model.name = PickViewTestSupport.CITY_NAME;
        // then
        Assert.assertEquals("CityName[CityId]", model.toString());
    }

    @Test
    public void testGetText() {
        // when
        model.name = PickViewTestSupport.CITY_NAME;
        // then
        Assert.assertEquals(PickViewTestSupport.CITY_NAME, model.getText());
        // when
        model.name = null;
        // then
        Assert.assertEquals("", model.getText());
    }
}

