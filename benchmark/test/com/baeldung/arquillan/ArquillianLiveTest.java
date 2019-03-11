package com.baeldung.arquillan;


import com.baeldung.arquillian.CapsService;
import com.baeldung.arquillian.Car;
import com.baeldung.arquillian.CarEJB;
import com.baeldung.arquillian.Component;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class ArquillianLiveTest {
    @Inject
    Component component;

    @Test
    public void givenMessage_WhenComponentSendMessage_ThenMessageReceived() {
        Assert.assertEquals("Message, MESSAGE", component.message("MESSAGE"));
        component.sendMessage(System.out, "MESSAGE");
    }

    @Inject
    private CapsService capsService;

    @Test
    public void givenWord_WhenUppercase_ThenLowercase() {
        Assert.assertTrue("capitalize".equals(capsService.getConvertedCaps("CAPITALIZE")));
        Assert.assertEquals("capitalize", capsService.getConvertedCaps("CAPITALIZE"));
    }

    @Inject
    private CarEJB carEJB;

    @Test
    public void testCars() {
        Assert.assertTrue(carEJB.findAllCars().isEmpty());
        Car c1 = new Car();
        c1.setName("Impala");
        Car c2 = new Car();
        c2.setName("Maverick");
        Car c3 = new Car();
        c3.setName("Aspen");
        Car c4 = new Car();
        c4.setName("Lincoln");
        carEJB.saveCar(c1);
        carEJB.saveCar(c2);
        carEJB.saveCar(c3);
        carEJB.saveCar(c4);
        Assert.assertEquals(4, carEJB.findAllCars().size());
        carEJB.deleteCar(c4);
        Assert.assertEquals(3, carEJB.findAllCars().size());
    }
}

