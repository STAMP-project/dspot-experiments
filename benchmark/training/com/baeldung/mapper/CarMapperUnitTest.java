package com.baeldung.mapper;


import CarMapper.INSTANCE;
import com.baeldung.dto.CarDTO;
import com.baeldung.entity.Car;
import org.junit.Assert;
import org.junit.Test;


public class CarMapperUnitTest {
    @Test
    public void givenCarEntitytoCar_whenMaps_thenCorrect() {
        Car entity = new Car();
        entity.setId(1);
        entity.setName("Toyota");
        CarDTO carDto = INSTANCE.carToCarDTO(entity);
        Assert.assertEquals(carDto.getId(), entity.getId());
        Assert.assertEquals(carDto.getName(), entity.getName());
    }
}

