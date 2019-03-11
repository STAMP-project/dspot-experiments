package org.opentripplanner.updater.bike_park;


import java.util.List;
import junit.framework.TestCase;
import org.opentripplanner.routing.bike_park.BikePark;


public class TestKmlBikeParkSource extends TestCase {
    public void testKML() {
        KmlBikeParkDataSource kmlDataSource = new KmlBikeParkDataSource();
        kmlDataSource.setUrl("file:src/test/resources/bike/NSFietsenstallingen.kml");
        TestCase.assertTrue(kmlDataSource.update());
        List<BikePark> bikeParks = kmlDataSource.getBikeParks();
        TestCase.assertEquals(5, bikeParks.size());
        BikePark alkmaar = bikeParks.get(0);
        BikePark zwolle = bikeParks.get(4);
        TestCase.assertEquals("Station Alkmaar", alkmaar.name);
        TestCase.assertEquals("Station Zwolle", zwolle.name);
        TestCase.assertTrue((((alkmaar.x) >= 4.73985) && ((alkmaar.x) <= 4.739851)));
        TestCase.assertTrue((((alkmaar.y) >= 52.637531) && ((alkmaar.y) <= 52.637532)));
        TestCase.assertTrue((((zwolle.x) >= 6.09106) && ((zwolle.x) <= 6.091061)));
        TestCase.assertTrue((((zwolle.y) >= 52.50499) && ((zwolle.y) <= 52.504991)));
    }

    public void testKMLWithFolder() {
        KmlBikeParkDataSource kmlDataSource = new KmlBikeParkDataSource();
        kmlDataSource.setUrl("file:src/test/resources/bike/NSFietsenstallingen_folder.kml");
        TestCase.assertTrue(kmlDataSource.update());
        List<BikePark> bikeParks = kmlDataSource.getBikeParks();
        TestCase.assertEquals(5, bikeParks.size());
        BikePark alkmaar = bikeParks.get(0);
        BikePark almere = bikeParks.get(4);
        TestCase.assertEquals("Station Alkmaar", alkmaar.name);
        TestCase.assertEquals("Station Almere Centrum", almere.name);
        TestCase.assertTrue((((alkmaar.x) >= 4.73985) && ((alkmaar.x) <= 4.739851)));
        TestCase.assertTrue((((alkmaar.y) >= 52.637531) && ((alkmaar.y) <= 52.637532)));
        TestCase.assertTrue((((almere.x) >= 5.2178) && ((almere.x) <= 5.21782)));
        TestCase.assertTrue((((almere.y) >= 52.374619) && ((almere.y) <= 52.3746191)));
    }
}

