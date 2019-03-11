package org.opentripplanner.gtfs.mapping;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.FareAttribute;
import org.onebusaway.gtfs.model.org.opentripplanner.model.FareAttribute;


public class FareAttributeMapperTest {
    private static final FareAttribute FARE_ATTRIBUTE = new FareAttribute();

    private static final AgencyAndId ID = new AgencyAndId("A", "1");

    private static final String CURRENCY_TYPE = "NOK";

    private static final int JOURNEY_DURATION = 10;

    private static final int PAY_MENTMETHOD = 1;

    private static final float PRICE = 0.3F;

    private static final float SENIOR_PRICE = 0.7F;

    private static final float YOUTH_PRICE = 0.9F;

    private static final int TRANSFER_DURATION = 3;

    private static final int TRANSFERS = 2;

    static {
        FareAttributeMapperTest.FARE_ATTRIBUTE.setId(FareAttributeMapperTest.ID);
        FareAttributeMapperTest.FARE_ATTRIBUTE.setCurrencyType(FareAttributeMapperTest.CURRENCY_TYPE);
        FareAttributeMapperTest.FARE_ATTRIBUTE.setJourneyDuration(FareAttributeMapperTest.JOURNEY_DURATION);
        FareAttributeMapperTest.FARE_ATTRIBUTE.setPaymentMethod(FareAttributeMapperTest.PAY_MENTMETHOD);
        FareAttributeMapperTest.FARE_ATTRIBUTE.setPrice(FareAttributeMapperTest.PRICE);
        FareAttributeMapperTest.FARE_ATTRIBUTE.setSeniorPrice(FareAttributeMapperTest.SENIOR_PRICE);
        FareAttributeMapperTest.FARE_ATTRIBUTE.setYouthPrice(FareAttributeMapperTest.YOUTH_PRICE);
        FareAttributeMapperTest.FARE_ATTRIBUTE.setTransferDuration(FareAttributeMapperTest.TRANSFER_DURATION);
        FareAttributeMapperTest.FARE_ATTRIBUTE.setTransfers(FareAttributeMapperTest.TRANSFERS);
    }

    private FareAttributeMapper subject = new FareAttributeMapper();

    @Test
    public void testMapCollection() throws Exception {
        Assert.assertNull(subject.map(((Collection<FareAttribute>) (null))));
        Assert.assertTrue(subject.map(Collections.emptyList()).isEmpty());
        Assert.assertEquals(1, subject.map(Collections.singleton(FareAttributeMapperTest.FARE_ATTRIBUTE)).size());
    }

    @Test
    public void testMap() throws Exception {
        org.opentripplanner.model.FareAttribute result = subject.map(FareAttributeMapperTest.FARE_ATTRIBUTE);
        Assert.assertEquals("A_1", result.getId().toString());
        Assert.assertEquals(FareAttributeMapperTest.CURRENCY_TYPE, result.getCurrencyType());
        Assert.assertEquals(FareAttributeMapperTest.JOURNEY_DURATION, result.getJourneyDuration());
        Assert.assertEquals(FareAttributeMapperTest.PAY_MENTMETHOD, result.getPaymentMethod());
        Assert.assertEquals(FareAttributeMapperTest.PRICE, result.getPrice(), 1.0E-5F);
        Assert.assertEquals(FareAttributeMapperTest.SENIOR_PRICE, result.getSeniorPrice(), 1.0E-5F);
        Assert.assertEquals(FareAttributeMapperTest.YOUTH_PRICE, result.getYouthPrice(), 1.0E-5F);
        Assert.assertEquals(FareAttributeMapperTest.TRANSFER_DURATION, result.getTransferDuration());
        Assert.assertEquals(FareAttributeMapperTest.TRANSFERS, result.getTransfers());
    }

    @Test
    public void testMapWithNulls() throws Exception {
        FareAttribute orginal = new FareAttribute();
        orginal.setId(FareAttributeMapperTest.ID);
        org.opentripplanner.model.FareAttribute result = subject.map(orginal);
        Assert.assertNotNull(result.getId());
        Assert.assertNull(result.getCurrencyType());
        Assert.assertFalse(result.isJourneyDurationSet());
        Assert.assertEquals(0, result.getPaymentMethod());
        Assert.assertEquals(0, result.getPrice(), 0.001);
        Assert.assertEquals(0, result.getSeniorPrice(), 0.001);
        Assert.assertEquals(0, result.getYouthPrice(), 0.001);
        Assert.assertFalse(result.isTransferDurationSet());
        Assert.assertFalse(result.isTransfersSet());
    }

    /**
     * Mapping the same object twice, should return the the same instance.
     */
    @Test
    public void testMapCache() throws Exception {
        org.opentripplanner.model.FareAttribute result1 = subject.map(FareAttributeMapperTest.FARE_ATTRIBUTE);
        org.opentripplanner.model.FareAttribute result2 = subject.map(FareAttributeMapperTest.FARE_ATTRIBUTE);
        Assert.assertTrue((result1 == result2));
    }
}

