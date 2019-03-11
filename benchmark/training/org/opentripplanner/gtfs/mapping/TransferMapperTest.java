package org.opentripplanner.gtfs.mapping;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Transfer;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.org.opentripplanner.model.Transfer;


public class TransferMapperTest {
    private static final RouteMapper ROUTE_MAPPER = new RouteMapper(new AgencyMapper());

    private static final TripMapper TRIP_MAPPER = new TripMapper(TransferMapperTest.ROUTE_MAPPER);

    private static final StopMapper STOP_MAPPER = new StopMapper();

    private static final AgencyAndId AGENCY_AND_ID = new AgencyAndId("A", "1");

    private static final Integer ID = 45;

    private static final Route FROM_ROUTE = new Route();

    private static final Stop FROM_STOP = new Stop();

    private static final Trip FROM_TRIP = new Trip();

    private static final Route TO_ROUTE = new Route();

    private static final Stop TO_STOP = new Stop();

    private static final Trip TO_TRIP = new Trip();

    private static final int MIN_TRANSFER_TIME = 200;

    private static final int TRANSFER_TYPE = 3;

    private static final Transfer TRANSFER = new Transfer();

    static {
        TransferMapperTest.FROM_ROUTE.setId(TransferMapperTest.AGENCY_AND_ID);
        TransferMapperTest.FROM_STOP.setId(TransferMapperTest.AGENCY_AND_ID);
        TransferMapperTest.FROM_TRIP.setId(TransferMapperTest.AGENCY_AND_ID);
        TransferMapperTest.TO_ROUTE.setId(TransferMapperTest.AGENCY_AND_ID);
        TransferMapperTest.TO_STOP.setId(TransferMapperTest.AGENCY_AND_ID);
        TransferMapperTest.TO_TRIP.setId(TransferMapperTest.AGENCY_AND_ID);
        TransferMapperTest.TRANSFER.setId(TransferMapperTest.ID);
        TransferMapperTest.TRANSFER.setFromRoute(TransferMapperTest.FROM_ROUTE);
        TransferMapperTest.TRANSFER.setFromStop(TransferMapperTest.FROM_STOP);
        TransferMapperTest.TRANSFER.setFromTrip(TransferMapperTest.FROM_TRIP);
        TransferMapperTest.TRANSFER.setToRoute(TransferMapperTest.TO_ROUTE);
        TransferMapperTest.TRANSFER.setToStop(TransferMapperTest.TO_STOP);
        TransferMapperTest.TRANSFER.setToTrip(TransferMapperTest.TO_TRIP);
        TransferMapperTest.TRANSFER.setMinTransferTime(TransferMapperTest.MIN_TRANSFER_TIME);
        TransferMapperTest.TRANSFER.setTransferType(TransferMapperTest.TRANSFER_TYPE);
    }

    private TransferMapper subject = new TransferMapper(TransferMapperTest.ROUTE_MAPPER, TransferMapperTest.STOP_MAPPER, TransferMapperTest.TRIP_MAPPER);

    @Test
    public void testMapCollection() throws Exception {
        Assert.assertNull(null, subject.map(((Collection<Transfer>) (null))));
        Assert.assertTrue(subject.map(Collections.emptyList()).isEmpty());
        Assert.assertEquals(1, subject.map(Collections.singleton(TransferMapperTest.TRANSFER)).size());
    }

    @Test
    public void testMap() throws Exception {
        org.opentripplanner.model.Transfer result = subject.map(TransferMapperTest.TRANSFER);
        Assert.assertNotNull(result.getFromRoute());
        Assert.assertNotNull(result.getFromTrip());
        Assert.assertNotNull(result.getFromStop());
        Assert.assertNotNull(result.getToRoute());
        Assert.assertNotNull(result.getToTrip());
        Assert.assertNotNull(result.getToStop());
        Assert.assertEquals(TransferMapperTest.MIN_TRANSFER_TIME, result.getMinTransferTime());
        Assert.assertEquals(TransferMapperTest.TRANSFER_TYPE, result.getTransferType());
    }

    @Test
    public void testMapWithNulls() throws Exception {
        org.opentripplanner.model.Transfer result = subject.map(new Transfer());
        Assert.assertNull(result.getFromRoute());
        Assert.assertNull(result.getFromTrip());
        Assert.assertNull(result.getFromStop());
        Assert.assertNull(result.getToRoute());
        Assert.assertNull(result.getToTrip());
        Assert.assertNull(result.getToStop());
        Assert.assertFalse(result.isMinTransferTimeSet());
        Assert.assertEquals(0, result.getTransferType());
    }

    /**
     * Mapping the same object twice, should return the the same instance.
     */
    @Test
    public void testMapCache() throws Exception {
        org.opentripplanner.model.Transfer result1 = subject.map(TransferMapperTest.TRANSFER);
        org.opentripplanner.model.Transfer result2 = subject.map(TransferMapperTest.TRANSFER);
        Assert.assertTrue((result1 == result2));
    }
}

