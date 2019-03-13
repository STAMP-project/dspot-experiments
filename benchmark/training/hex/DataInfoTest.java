package hex;


import DataInfo.TransformType;
import Model.InteractionPair;
import Model.InteractionSpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

import static water.TestUtil.<init>;


// test cases:
// skipMissing = TRUE/FALSE
// useAllLevels = TRUE/FALSE
// limit enums
// (dont) standardize predictor columns
// data info tests with interactions
public class DataInfoTest extends TestUtil {
    @Test
    public void testAirlines1() {
        // just test that it works at all
        Frame fr = parse_test_file(Key.make("a.hex"), "smalldata/airlines/allyears2k_headers.zip");
        try {
            DataInfo dinfo = // train
            // valid
            // num responses
            // use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            // interactions
            new DataInfo(fr.clone(), null, 1, true, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false, InteractionSpec.allPairwise(new String[]{ fr.name(8), fr.name(16), fr.name(2) }));
            dinfo.dropInteractions();
            dinfo.remove();
        } finally {
            fr.delete();
        }
    }

    @Test
    public void testAirlines2() {
        Frame fr = parse_test_file(Key.make("a.hex"), "smalldata/airlines/allyears2k_headers.zip");
        try {
            Frame interactions = Model.makeInteractions(fr, false, InteractionPair.generatePairwiseInteractionsFromList(8, 16, 2), true, true, true);
            int len = 0;
            for (Vec v : interactions.vecs())
                len += expandedLength();

            interactions.delete();
            Assert.assertTrue((len == ((290 + 132) + 10)));
            DataInfo dinfo__noInteractions = // train
            // valid
            // num responses
            // use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            new DataInfo(fr.clone(), null, 1, true, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false, null);
            System.out.println(dinfo__noInteractions.fullN());
            System.out.println(dinfo__noInteractions.numNums());
            DataInfo dinfo__withInteractions = // train
            // valid
            // num responses
            // use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            // interactions
            new DataInfo(fr.clone(), null, 1, true, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false, InteractionSpec.allPairwise(new String[]{ fr.name(8), fr.name(16), fr.name(2) }));
            System.out.println(dinfo__withInteractions.fullN());
            Assert.assertTrue(((dinfo__withInteractions.fullN()) == ((dinfo__noInteractions.fullN()) + len)));
            dinfo__withInteractions.dropInteractions();
            dinfo__noInteractions.remove();
            dinfo__withInteractions.remove();
        } finally {
            fr.delete();
        }
    }

    @Test
    public void testAirlines3() {
        Frame fr = parse_test_file(Key.make("a.hex"), "smalldata/airlines/allyears2k_headers.zip");
        try {
            Frame interactions = Model.makeInteractions(fr, false, InteractionPair.generatePairwiseInteractionsFromList(8, 16, 2), false, true, true);
            int len = 0;
            for (Vec v : interactions.vecs())
                len += expandedLength();

            interactions.delete();
            Assert.assertTrue((len == 426));
            DataInfo dinfo__noInteractions = // train
            // valid
            // num responses
            // use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            new DataInfo(fr.clone(), null, 1, false, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false, null);
            System.out.println(dinfo__noInteractions.fullN());
            DataInfo dinfo__withInteractions = // train
            // valid
            // num responses
            // use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            // interactions
            new DataInfo(fr.clone(), null, 1, false, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false, InteractionSpec.allPairwise(new String[]{ fr.name(8), fr.name(16), fr.name(2) }));
            System.out.println(dinfo__withInteractions.fullN());
            Assert.assertTrue(((dinfo__withInteractions.fullN()) == ((dinfo__noInteractions.fullN()) + len)));
            dinfo__withInteractions.dropInteractions();
            dinfo__noInteractions.remove();
            dinfo__withInteractions.remove();
        } finally {
            fr.delete();
        }
    }

    @Test
    public void testAirlinesInteractionSpec() {
        try {
            Scope.enter();
            Frame fr = Scope.track(parse_test_file(Key.make("a.hex"), "smalldata/airlines/allyears2k_headers.zip"));
            Model.InteractionSpec interactionSpec = InteractionSpec.create(null, new StringPair[]{ new StringPair("UniqueCarrier", "Origin"), new StringPair("Origin", "DayofMonth") }, new String[]{ "UniqueCarrier" });
            DataInfo dinfo = // train
            // valid
            // num responses
            // use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            // interactions
            new DataInfo(fr.clone(), null, 1, false, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false, interactionSpec);
            Scope.track_generic(dinfo);
            Assert.assertArrayEquals(new String[]{ "TailNum", "UniqueCarrier_Origin", "Dest", "Origin", "CancellationCode", "IsArrDelayed", "Origin_DayofMonth", "Year", "Month", "DayofMonth", "DayOfWeek", "DepTime", "CRSDepTime", "ArrTime", "CRSArrTime", "FlightNum", "ActualElapsedTime", "CRSElapsedTime", "AirTime", "ArrDelay", "DepDelay", "Distance", "TaxiIn", "TaxiOut", "Cancelled", "Diverted", "CarrierDelay", "WeatherDelay", "NASDelay", "SecurityDelay", "LateAircraftDelay", "IsDepDelayed" }, dinfo._adaptedFrame._names);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testIris1() {
        // test that getting sparseRows and denseRows produce the same results
        Frame fr = parse_test_file(Key.make("a.hex"), "smalldata/iris/iris_wheader.csv");
        fr.swap(1, 4);
        Model[] ips = InteractionPair.generatePairwiseInteractionsFromList(0, 1);
        DataInfo di = null;
        try {
            di = // train
            // valid
            // num responses
            // use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            // interactions
            new DataInfo(fr.clone(), null, 1, true, TransformType.NONE, TransformType.NONE, true, false, false, false, false, false, InteractionSpec.allPairwise(new String[]{ fr.name(0), fr.name(1) }));
            DataInfoTest.checker(di, false);
        } finally {
            fr.delete();
            if (di != null) {
                di.dropInteractions();
                di.remove();
            }
        }
    }

    @Test
    public void testIris2() {
        // test that getting sparseRows and denseRows produce the same results
        Frame fr = parse_test_file(Key.make("a.hex"), "smalldata/iris/iris_wheader.csv");
        fr.swap(1, 4);
        Model[] ips = InteractionPair.generatePairwiseInteractionsFromList(0, 1);
        DataInfo di = null;
        try {
            di = // train
            // valid
            // num responses
            // use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            // interactions
            new DataInfo(fr.clone(), null, 1, true, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false, InteractionSpec.allPairwise(new String[]{ fr.name(0), fr.name(1) }));
            DataInfoTest.checker(di, true);
        } finally {
            fr.delete();
            if (di != null) {
                di.dropInteractions();
                di.remove();
            }
        }
    }

    @Test
    public void testIris3() {
        // test that getting sparseRows and denseRows produce the same results
        Frame fr = parse_test_file(Key.make("a.hex"), "smalldata/iris/iris_wheader.csv");
        fr.swap(2, 4);
        Model[] ips = InteractionPair.generatePairwiseInteractionsFromList(0, 1, 2, 3);
        DataInfo di = null;
        try {
            di = // train
            // valid
            // num responses
            // use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            // interactions
            new DataInfo(fr.clone(), null, 1, true, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false, InteractionSpec.allPairwise(new String[]{ fr.name(0), fr.name(1), fr.name(2), fr.name(3) }));
            DataInfoTest.checker(di, true);
        } finally {
            fr.delete();
            if (di != null) {
                di.dropInteractions();
                di.remove();
            }
        }
    }

    @Test
    public void testAirlines4() {
        Frame fr = parse_test_file(Key.make("a0.hex"), "smalldata/airlines/allyears2k_headers.zip");
        // fixme need to rebalance to 1 chunk, otherwise the test does not pass!
        Key k = Key.make("a.hex");
        H2O.submitTask(new RebalanceDataSet(fr, k, 1)).join();
        fr.delete();
        fr = DKV.getGet(k);
        Model[] ips = InteractionPair.generatePairwiseInteractionsFromList(8, 16, 2);
        DataInfo di = null;
        try {
            di = // train
            // valid
            // num responses
            // use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            // interactions
            new DataInfo(fr.clone(), null, 1, true, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false, InteractionSpec.allPairwise(new String[]{ fr.name(8), fr.name(16), fr.name(2) }));
            DataInfoTest.checker(di, true);
        } finally {
            fr.delete();
            if (di != null) {
                di.dropInteractions();
                di.remove();
            }
        }
    }

    @Test
    public void testAirlines5() {
        Frame fr = parse_test_file(Key.make("a0.hex"), "smalldata/airlines/allyears2k_headers.zip");
        // fixme need to rebalance to 1 chunk, otherwise the test does not pass!
        Key k = Key.make("a.hex");
        H2O.submitTask(new RebalanceDataSet(fr, k, 1)).join();
        fr.delete();
        fr = DKV.getGet(k);
        Model[] ips = InteractionPair.generatePairwiseInteractionsFromList(8, 16, 2);
        DataInfo di = null;
        try {
            di = // train
            // valid
            // num responses
            // use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            // interactions
            new DataInfo(fr.clone(), null, 1, false, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false, InteractionSpec.allPairwise(new String[]{ fr.name(8), fr.name(16), fr.name(2) }));
            DataInfoTest.checker(di, true);
        } finally {
            fr.delete();
            if (di != null) {
                di.dropInteractions();
                di.remove();
            }
        }
    }

    @Test
    public void testCoefNames() throws IOException {
        // just test that it works at all
        Frame fr = parse_test_file(Key.make("a.hex"), "smalldata/airlines/allyears2k_headers.zip");
        DataInfo dinfo = null;
        try {
            dinfo = // train
            // valid
            // num responses
            // use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            // interactions
            new DataInfo(fr.clone(), null, 1, true, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false, InteractionSpec.allPairwise(new String[]{ fr.name(8), fr.name(16), fr.name(2) }));
            Assert.assertNull(dinfo._coefNames);// coef names are not populated at first

            final String[] cn = dinfo.coefNames();
            Assert.assertNotNull(cn);
            Assert.assertArrayEquals(cn, dinfo._coefNames);// coef names are cached after first accessed

            DKV.put(dinfo);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            dinfo.writeAll(new AutoBuffer(baos, true)).close();
            baos.close();
            ByteArrayInputStream input = new ByteArrayInputStream(baos.toByteArray());
            DataInfo deserialized = ((DataInfo) (Keyed.readAll(new AutoBuffer(input))));
            Assert.assertNotNull(deserialized);
            Assert.assertArrayEquals(cn, deserialized._coefNames);// coef names were preserved in the deserialized object

        } finally {
            if (dinfo != null) {
                dinfo.dropInteractions();
                dinfo.remove();
            }
            fr.delete();
        }
    }

    @Test
    public void testInteractionsForcedAllFactors() {
        try {
            Scope.enter();
            Frame fr = Scope.track(parse_test_file(Key.make("a.hex"), "smalldata/airlines/allyears2k_headers.zip"));
            Frame sfr = fr.subframe(new String[]{ "Origin", "Distance" });
            Model.InteractionSpec interactionSpec = InteractionSpec.create(new String[]{ "Origin", "Distance" }, null, new String[]{ "Distance" });
            DataInfo dinfo = // train
            // valid
            // num responses
            // DON'T use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            // interaction spec
            new DataInfo(sfr, null, 1, false, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false, interactionSpec);
            Assert.assertEquals(fr.vec("Origin").domain().length, dinfo.coefNames().length);
            String[] expected = new String[dinfo.coefNames().length];
            for (int i = 0; i < (expected.length); i++)
                expected[i] = "Origin_Distance." + (sfr.vec("Origin").domain()[i]);

            Assert.assertArrayEquals(expected, dinfo.coefNames());
            dinfo.dropInteractions();
            dinfo.remove();
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testInteractionsSkip1stFactor() {
        try {
            Scope.enter();
            Frame fr = Scope.track(parse_test_file(Key.make("a.hex"), "smalldata/airlines/allyears2k_headers.zip"));
            Frame sfr = fr.subframe(new String[]{ "Origin", "Distance", "IsDepDelayed" });
            Model.InteractionSpec interactionSpec = InteractionSpec.create(new String[]{ "Origin", "Distance" }, null, new String[]{ "Origin" });
            DataInfo dinfo = // train
            // valid
            // num responses
            // DON'T use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            // interaction spec
            new DataInfo(sfr, null, 1, false, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false, interactionSpec);
            // Check that we get correct expanded coefficients and "Distance" is not dropped
            Assert.assertEquals(fr.vec("Origin").domain().length, dinfo.coefNames().length);
            String[] expected = new String[dinfo.coefNames().length];
            expected[((expected.length) - 1)] = "Distance";
            for (int i = 0; i < ((expected.length) - 1); i++)
                expected[i] = "Origin_Distance." + (fr.vec("Origin").domain()[(i + 1)]);

            Assert.assertArrayEquals(expected, dinfo.coefNames());
            // Check that we can look-up "Categorical Id" for valid levels
            for (/* don't use all factor levels */
            int j = 1; j < (dinfo._adaptedFrame.vec(0).domain().length); j++) {
                if ((dinfo.getCategoricalIdFromInteraction(0, j)) < 0)
                    Assert.fail(("Categorical value should be recognized: " + j));

            }
            // Check that we get "mode" for unknown level
            dinfo._valid = true;
            Assert.assertEquals(fr.vec("Origin").mode(), dinfo.getCategoricalIdFromInteraction(0, dinfo._adaptedFrame.vec(0).domain().length));
            dinfo.dropInteractions();
            dinfo.remove();
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testGetCategoricalIdFromInteraction() {
        try {
            Scope.enter();
            Frame fr = Scope.track(parse_test_file(Key.make("a.hex"), "smalldata/airlines/allyears2k_headers.zip"));
            Frame sfr = fr.subframe(new String[]{ "Origin", "Distance", "IsDepDelayed" });
            Model.InteractionSpec interactionSpec = InteractionSpec.create(new String[]{ "Origin", "Distance" }, null, new String[]{ "Origin" });
            DataInfo dinfo = // train
            // valid
            // num responses
            // DON'T use all factor levels
            // predictor transform
            // response  transform
            // skip missing
            // impute missing
            // missing bucket
            // weight
            // offset
            // fold
            // interaction spec
            new DataInfo(sfr, null, 1, false, TransformType.STANDARDIZE, TransformType.NONE, true, false, false, false, false, false, interactionSpec);
            // Check that we can look-up "Categorical Id" for valid levels
            for (/* don't use all factor levels */
            int j = 1; j < (dinfo._adaptedFrame.vec(0).domain().length); j++) {
                if ((dinfo.getCategoricalIdFromInteraction(0, j)) < 0)
                    Assert.fail(("Categorical value should be recognized: " + j));

            }
            // Check that we get "mode" for unknown level
            dinfo._valid = true;
            Assert.assertEquals(fr.vec("Origin").mode(), dinfo.getCategoricalIdFromInteraction(0, dinfo._adaptedFrame.vec(0).domain().length));
            dinfo.dropInteractions();
            dinfo.remove();
        } finally {
            Scope.exit();
        }
    }
}

