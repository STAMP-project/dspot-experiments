package org.opentripplanner.routing.fares;


import FareType.regular;
import FareType.student;
import java.util.ArrayList;
import java.util.Arrays;
import junit.framework.TestCase;
import org.opentripplanner.routing.core.Fare;
import org.opentripplanner.routing.core.WrappedCurrency;
import org.opentripplanner.routing.services.FareService;
import org.opentripplanner.routing.spt.GraphPath;


/**
 *
 *
 * @author laurent
 */
public class MultipleFareServiceTest extends TestCase {
    private class SimpleFareService implements FareService {
        private Fare fare;

        private SimpleFareService(Fare fare) {
            this.fare = fare;
        }

        @Override
        public Fare getCost(GraphPath path) {
            return fare;
        }
    }

    public void testAddingMultipleFareService() {
        Fare fare1 = new Fare();
        fare1.addFare(regular, new WrappedCurrency("EUR"), 100);
        FareService fs1 = new MultipleFareServiceTest.SimpleFareService(fare1);
        Fare fare2 = new Fare();
        fare2.addFare(regular, new WrappedCurrency("EUR"), 140);
        fare2.addFare(student, new WrappedCurrency("EUR"), 120);
        FareService fs2 = new MultipleFareServiceTest.SimpleFareService(fare2);
        /* Note: this fare is not very representative, as you should probably always compute a
        "regular" fare in case you want to add bike and transit fares.
         */
        Fare fare3 = new Fare();
        fare3.addFare(student, new WrappedCurrency("EUR"), 80);
        FareService fs3 = new MultipleFareServiceTest.SimpleFareService(fare3);
        AddingMultipleFareService mfs = new AddingMultipleFareService(new ArrayList<FareService>());
        Fare fare = mfs.getCost(null);
        TestCase.assertNull(fare);
        mfs = new AddingMultipleFareService(Arrays.asList(fs1));
        fare = mfs.getCost(null);
        TestCase.assertEquals(100, fare.getFare(regular).getCents());
        TestCase.assertEquals(null, fare.getFare(student));
        mfs = new AddingMultipleFareService(Arrays.asList(fs2));
        fare = mfs.getCost(null);
        TestCase.assertEquals(140, fare.getFare(regular).getCents());
        TestCase.assertEquals(120, fare.getFare(student).getCents());
        mfs = new AddingMultipleFareService(Arrays.asList(fs1, fs2));
        fare = mfs.getCost(null);
        TestCase.assertEquals(240, fare.getFare(regular).getCents());
        TestCase.assertEquals(220, fare.getFare(student).getCents());
        mfs = new AddingMultipleFareService(Arrays.asList(fs2, fs1));
        fare = mfs.getCost(null);
        TestCase.assertEquals(240, fare.getFare(regular).getCents());
        TestCase.assertEquals(220, fare.getFare(student).getCents());
        mfs = new AddingMultipleFareService(Arrays.asList(fs1, fs3));
        fare = mfs.getCost(null);
        TestCase.assertEquals(100, fare.getFare(regular).getCents());
        TestCase.assertEquals(180, fare.getFare(student).getCents());
        mfs = new AddingMultipleFareService(Arrays.asList(fs3, fs1));
        fare = mfs.getCost(null);
        TestCase.assertEquals(100, fare.getFare(regular).getCents());
        TestCase.assertEquals(180, fare.getFare(student).getCents());
        mfs = new AddingMultipleFareService(Arrays.asList(fs1, fs2, fs3));
        fare = mfs.getCost(null);
        TestCase.assertEquals(240, fare.getFare(regular).getCents());
        TestCase.assertEquals(300, fare.getFare(student).getCents());
    }
}

