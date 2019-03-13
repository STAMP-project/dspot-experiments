package org.opentripplanner.updater.bike_rental;


import java.util.List;
import junit.framework.TestCase;
import org.opentripplanner.routing.bike_rental.BikeRentalStation;


public class TestBikeRentalStationSource extends TestCase {
    public void testKeolisRennes() {
        KeolisRennesBikeRentalDataSource rennesSource = new KeolisRennesBikeRentalDataSource();
        rennesSource.setUrl("file:src/test/resources/bike/keolis-rennes.xml");
        TestCase.assertTrue(rennesSource.update());
        List<BikeRentalStation> rentalStations = rennesSource.getStations();
        TestCase.assertEquals(4, rentalStations.size());
        for (BikeRentalStation rentalStation : rentalStations) {
            System.out.println(rentalStation);
        }
        BikeRentalStation stSulpice = rentalStations.get(0);
        TestCase.assertEquals("ZAC SAINT SULPICE", stSulpice.name.toString());
        TestCase.assertEquals("75", stSulpice.id);
        TestCase.assertEquals((-1.63528), stSulpice.x);
        TestCase.assertEquals(48.1321, stSulpice.y);
        TestCase.assertEquals(24, stSulpice.spacesAvailable);
        TestCase.assertEquals(6, stSulpice.bikesAvailable);
        BikeRentalStation kergus = rentalStations.get(3);
        TestCase.assertEquals("12", kergus.id);
    }

    public void testSmoove() {
        SmooveBikeRentalDataSource source = new SmooveBikeRentalDataSource();
        source.setUrl("file:src/test/resources/bike/smoove.json");
        TestCase.assertTrue(source.update());
        List<BikeRentalStation> rentalStations = source.getStations();
        // Invalid station without coordinates shoulf be ignored, so only 3
        TestCase.assertEquals(3, rentalStations.size());
        for (BikeRentalStation rentalStation : rentalStations) {
            System.out.println(rentalStation);
        }
        BikeRentalStation hamn = rentalStations.get(0);
        TestCase.assertEquals("Hamn", hamn.name.toString());
        TestCase.assertEquals("A04", hamn.id);
        // Ignore whitespace in coordinates string
        TestCase.assertEquals(24.952269, hamn.x);
        TestCase.assertEquals(60.167913, hamn.y);
        TestCase.assertEquals(11, hamn.spacesAvailable);
        TestCase.assertEquals(1, hamn.bikesAvailable);
        BikeRentalStation fake = rentalStations.get(1);
        TestCase.assertEquals("Fake", fake.name.toString());
        TestCase.assertEquals("B05", fake.id);
        TestCase.assertEquals(24.0, fake.x);
        TestCase.assertEquals(60.0, fake.y);
        // operative: false overrides available bikes and slots
        TestCase.assertEquals(0, fake.spacesAvailable);
        TestCase.assertEquals(0, fake.bikesAvailable);
        BikeRentalStation foo = rentalStations.get(2);
        TestCase.assertEquals("Foo", foo.name.toString());
        TestCase.assertEquals("B06", foo.id);
        TestCase.assertEquals(25.0, foo.x);
        TestCase.assertEquals(61.0, foo.y);
        TestCase.assertEquals(5, foo.spacesAvailable);
        TestCase.assertEquals(5, foo.bikesAvailable);
        // Ignores mismatch with total_slots
    }
}

