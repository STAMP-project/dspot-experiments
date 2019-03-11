package com.baeldung.measurement;


import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.UnitConverter;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.Volume;
import org.junit.Assert;
import org.junit.Test;
import tec.units.ri.format.SimpleUnitFormat;
import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;


public class WaterTankUnitTest {
    @Test
    public void givenQuantity_whenGetUnitAndConvertValue_thenSuccess() {
        WaterTank waterTank = new WaterTank();
        waterTank.setCapacityMeasure(Quantities.getQuantity(9.2, LITRE));
        Assert.assertEquals(LITRE, waterTank.getCapacityMeasure().getUnit());
        Quantity<Volume> waterCapacity = waterTank.getCapacityMeasure();
        double volumeInLitre = waterCapacity.getValue().doubleValue();
        Assert.assertEquals(9.2, volumeInLitre, 0.0F);
        double volumeInMilliLitre = waterCapacity.to(MetricPrefix.MILLI(LITRE)).getValue().doubleValue();
        Assert.assertEquals(9200.0, volumeInMilliLitre, 0.0F);
        // compilation error
        // volumeInMilliLitre = waterCapacity.to(MetricPrefix.MILLI(KILOGRAM));
        Unit<Length> Kilometer = MetricPrefix.KILO(METRE);
        // compilation error
        // Unit<Length> Centimeter = MetricPrefix.CENTI(LITRE);
    }

    @Test
    public void givenUnit_whenAlternateUnit_ThenGetAlternateUnit() {
        Unit<Pressure> PASCAL = NEWTON.divide(METRE.pow(2)).alternate("Pa").asType(Pressure.class);
        Assert.assertTrue(SimpleUnitFormat.getInstance().parse("Pa").equals(PASCAL));
    }

    @Test
    public void givenUnit_whenProduct_ThenGetProductUnit() {
        Unit<Area> squareMetre = METRE.multiply(METRE).asType(Area.class);
        Quantity<Length> line = Quantities.getQuantity(2, METRE);
        Assert.assertEquals(line.multiply(line).getUnit(), squareMetre);
    }

    @Test
    public void givenMeters_whenConvertToKilometer_ThenConverted() {
        double distanceInMeters = 50.0;
        UnitConverter metreToKilometre = METRE.getConverterTo(MetricPrefix.KILO(METRE));
        double distanceInKilometers = metreToKilometre.convert(distanceInMeters);
        Assert.assertEquals(0.05, distanceInKilometers, 0.0F);
    }

    @Test
    public void givenSymbol_WhenCompareToSystemUnit_ThenSuccess() {
        Assert.assertTrue(SimpleUnitFormat.getInstance().parse("kW").equals(MetricPrefix.KILO(WATT)));
        Assert.assertTrue(SimpleUnitFormat.getInstance().parse("ms").equals(SECOND.divide(1000)));
    }

    @Test
    public void givenUnits_WhenAdd_ThenSuccess() {
        Quantity<Length> total = Quantities.getQuantity(2, METRE).add(Quantities.getQuantity(3, METRE));
        Assert.assertEquals(total.getValue().intValue(), 5);
        // compilation error
        // Quantity<Length> total = Quantities.getQuantity(2, METRE).add(Quantities.getQuantity(3, LITRE));
        Quantity<Length> totalKm = Quantities.getQuantity(2, METRE).add(Quantities.getQuantity(3, MetricPrefix.KILO(METRE)));
        Assert.assertEquals(totalKm.getValue().intValue(), 3002);
    }
}

