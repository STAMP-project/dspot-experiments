package com.brucetoo.pickview.provincepick;


import org.junit.Assert;
import org.junit.Test;


public class ProvinceModelTest extends PickViewTestSupport {
    private ProvinceModel model;

    private CityModel[] cities;

    @Test
    public void testGetCityIdList() {
        // when empty then
        Assert.assertTrue(model.getCityIdList().isEmpty());
        // when
        model.addCity(new CityModel());
        // then
        Assert.assertEquals(1, model.getCityIdList().size());
        Assert.assertNull(model.getCityIdList().get(0));
        // when
        for (CityModel city : cities) {
            model.addCity(city);
        }
        // then
        Assert.assertEquals(((PickViewTestSupport.CITIES_NUM) + 1), model.getCityIdList().size());
        Assert.assertNull(model.getCityIdList().get(0));
        for (int i = 0; i < (cities.length); i++) {
            Assert.assertEquals(cities[i].id, model.getCityIdList().get((i + 1)));
        }
    }

    @Test
    public void testGetCityNameList() {
        // when empty then
        Assert.assertTrue(model.getCityNameList().isEmpty());
        // when
        model.addCity(new CityModel());
        // then
        Assert.assertEquals(1, model.getCityNameList().size());
        Assert.assertNull(model.getCityNameList().get(0));
        // when
        for (CityModel city : cities) {
            model.addCity(city);
        }
        // then
        Assert.assertEquals(((PickViewTestSupport.CITIES_NUM) + 1), model.getCityNameList().size());
        Assert.assertNull(model.getCityNameList().get(0));
        for (int i = 0; i < (cities.length); i++) {
            Assert.assertEquals(cities[i].name, model.getCityNameList().get((i + 1)));
        }
    }

    @Test
    public void testGetCity() {
        // given
        CityModel cityModel = new CityModel();
        // when empty then
        Assert.assertNull(model.getCity(0));
        // when
        model.addCity(cityModel);
        // then
        Assert.assertEquals(cityModel, model.getCity(0));
        Assert.assertNull(model.getCity((-1)));
        Assert.assertNull(model.getCity(1));
        // when
        for (CityModel city : cities) {
            model.addCity(city);
        }
        // then
        Assert.assertEquals(cityModel, model.getCity(0));
        for (int i = 0; i < (cities.length); i++) {
            Assert.assertEquals(cities[i], model.getCity((i + 1)));
        }
        Assert.assertNull(model.getCity((-1)));
        Assert.assertNull(model.getCity(((PickViewTestSupport.CITIES_NUM) + 1)));
    }

    @Test
    public void testAddCity() {
        // when empty then
        Assert.assertEquals(0, model.getCityCount());
        // when
        model.addCity(null);
        Assert.assertEquals(0, model.getCityCount());
        // when
        model.addCity(new CityModel());
        // then
        Assert.assertEquals(1, model.getCityCount());
        // when
        for (CityModel city : cities) {
            model.addCity(city);
        }
        // then
        Assert.assertEquals(((PickViewTestSupport.CITIES_NUM) + 1), model.getCityCount());
    }

    @Test
    public void testGetCityList() {
        // when / then
        Assert.assertNull(model.getCityList());
        // given
        CityModel cityModel = new CityModel();
        // when
        model.addCity(cityModel);
        // then
        Assert.assertEquals(1, model.getCityList().size());
        Assert.assertTrue(model.getCityList().contains(cityModel));
        // when
        for (CityModel city : cities) {
            model.addCity(city);
        }
        // then
        Assert.assertEquals(((PickViewTestSupport.CITIES_NUM) + 1), model.getCityList().size());
        Assert.assertEquals(cityModel, model.getCityList().get(0));
    }

    @Test
    public void testToString() {
        // when
        model.id = PickViewTestSupport.PROVINCE_ID;
        model.name = PickViewTestSupport.PROVINCE_NAME;
        // then
        Assert.assertEquals("ProvinceName[ProvinceId][0 cities]", model.toString());
        // when
        model.addCity(new CityModel());
        // then
        Assert.assertEquals("ProvinceName[ProvinceId][1 cities]", model.toString());
        // when
        for (CityModel city : cities) {
            model.addCity(city);
        }
        // then
        Assert.assertEquals((("ProvinceName[ProvinceId][" + ((PickViewTestSupport.CITIES_NUM) + 1)) + " cities]"), model.toString());
    }

    @Test
    public void testGetText() {
        // when
        model.name = PickViewTestSupport.PROVINCE_NAME;
        // then
        Assert.assertEquals(PickViewTestSupport.PROVINCE_NAME, model.getText());
        // when
        model.name = null;
        // then
        Assert.assertEquals("", model.getText());
    }
}

