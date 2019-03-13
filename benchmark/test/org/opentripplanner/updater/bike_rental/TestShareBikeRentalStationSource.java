package org.opentripplanner.updater.bike_rental;


import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.List;
import junit.framework.TestCase;
import org.opentripplanner.routing.bike_rental.BikeRentalStation;


public class TestShareBikeRentalStationSource extends TestCase {
    public void testShareBike() throws UnsupportedEncodingException, MalformedURLException {
        ShareBikeRentalDataSource shareBikeSource = new ShareBikeRentalDataSource();
        shareBikeSource.setUrl("file:src/test/resources/bike/share-bike.json?SystemID=dummyid");
        TestCase.assertTrue(shareBikeSource.update());
        List<BikeRentalStation> rentalStations = shareBikeSource.getStations();
        TestCase.assertEquals(17, rentalStations.size());
        for (BikeRentalStation rentalStation : rentalStations) {
            System.out.println(rentalStation);
        }
        BikeRentalStation prinsen = rentalStations.get(0);
        TestCase.assertTrue(prinsen.networks.contains("dummyid"));
        TestCase.assertEquals("01", prinsen.name.toString());
        TestCase.assertEquals("dummyid_1", prinsen.id);
        TestCase.assertEquals(10.392981, prinsen.x);
        TestCase.assertEquals(63.426637, prinsen.y);
        TestCase.assertEquals(9, prinsen.spacesAvailable);
        TestCase.assertEquals(6, prinsen.bikesAvailable);
    }

    public void testShareBikeMissingSystemIDParameter() throws UnsupportedEncodingException, MalformedURLException {
        ShareBikeRentalDataSource shareBikeSource = new ShareBikeRentalDataSource();
        shareBikeSource.setUrl("file:src/test/resources/bike/share-bike.json");
        TestCase.assertTrue(shareBikeSource.update());
        List<BikeRentalStation> rentalStations = shareBikeSource.getStations();
        BikeRentalStation prinsen = rentalStations.get(0);
        // Should be random value
        TestCase.assertFalse(prinsen.networks.contains("dummyid"));
    }
}

