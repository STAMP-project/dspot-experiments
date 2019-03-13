package hex;


import GLMModel.GLMOutput;
import GLMModel.GLMParameters;
import Model.InteractionBuilder;
import Model.InteractionSpec;
import hex.glm.GLMModel;
import hex.splitframe.ShuffleSplitFrame;
import java.util.Random;
import org.junit.Test;
import water.Key;
import water.TestUtil;
import water.fvec.Frame;


// data info tests with interactions
public class DataInfoTestAdapt extends TestUtil {
    @Test
    public void testInteractionTrainTestSplitAdapt() {
        DataInfo dinfo = null;
        DataInfo scoreInfo = null;
        Frame fr = null;
        Frame expanded = null;
        Frame[] frSplits = null;
        Frame[] expandSplits = null;
        Model.InteractionSpec interactions = InteractionSpec.allPairwise(new String[]{ "class", "sepal_len" });
        boolean useAll = false;
        boolean standardize = false;// golden frame is standardized before splitting, while frame we want to check would be standardized post-split (not exactly what we want!)

        boolean skipMissing = true;
        try {
            fr = parse_test_file(Key.make("a.hex"), "smalldata/iris/iris_wheader.csv");
            fr.swap(3, 4);
            expanded = GLMOutput.expand(fr, interactions, useAll, standardize, skipMissing);// here's the "golden" frame

            // now split fr and expanded
            long seed;
            frSplits = ShuffleSplitFrame.shuffleSplitFrame(fr, new Key[]{ Key.make(), Key.make() }, new double[]{ 0.8, 0.2 }, (seed = new Random().nextLong()));
            expandSplits = ShuffleSplitFrame.shuffleSplitFrame(expanded, new Key[]{ Key.make(), Key.make() }, new double[]{ 0.8, 0.2 }, seed);
            // check1: verify splits. expand frSplits with DataInfo and check against expandSplits
            checkSplits(frSplits, expandSplits, interactions, useAll, standardize);
            // now take the test frame from frSplits, and adapt it to a DataInfo built on the train frame
            dinfo = DataInfoTestAdapt.makeInfo(frSplits[0], interactions, useAll, standardize);
            GLMModel.GLMParameters parms = new GLMModel.GLMParameters();
            parms._response_column = "petal_wid";
            Model.InteractionBuilder interactionBldr = DataInfoTestAdapt.interactionBuilder(dinfo);
            Model.adaptTestForTrain(frSplits[1], null, null, dinfo._adaptedFrame.names(), dinfo._adaptedFrame.domains(), parms, true, false, interactionBldr, null, null, false);
            scoreInfo = dinfo.scoringInfo(dinfo._adaptedFrame._names, frSplits[1]);
            checkFrame(scoreInfo, expandSplits[1]);
        } finally {
            cleanup(fr, expanded);
            cleanup(frSplits);
            cleanup(expandSplits);
            cleanup(dinfo, scoreInfo);
        }
    }

    @Test
    public void testInteractionTrainTestSplitAdaptAirlines() {
        DataInfo dinfo = null;
        DataInfo scoreInfo = null;
        Frame frA = null;
        Frame fr = null;
        Frame expanded = null;
        Frame[] frSplits = null;
        Frame[] expandSplits = null;
        Model.InteractionSpec interactions = InteractionSpec.allPairwise(new String[]{ "CRSDepTime", "Origin" });
        String[] keepColumns = new String[]{ "Year", "Month", "DayofMonth", "DayOfWeek", "CRSDepTime", "CRSArrTime", "UniqueCarrier", "CRSElapsedTime", "Origin", "Dest", "Distance", "IsDepDelayed" };
        boolean useAll = false;
        boolean standardize = false;// golden frame is standardized before splitting, while frame we want to check would be standardized post-split (not exactly what we want!)

        boolean skipMissing = false;
        try {
            frA = parse_test_file(Key.make("a.hex"), "smalldata/airlines/allyears2k_headers.zip");
            fr = frA.subframe(keepColumns);
            expanded = GLMOutput.expand(fr, interactions, useAll, standardize, skipMissing);// here's the "golden" frame

            // now split fr and expanded
            long seed;
            frSplits = ShuffleSplitFrame.shuffleSplitFrame(fr, new Key[]{ Key.make(), Key.make() }, new double[]{ 0.8, 0.2 }, (seed = new Random().nextLong()));
            expandSplits = ShuffleSplitFrame.shuffleSplitFrame(expanded, new Key[]{ Key.make(), Key.make() }, new double[]{ 0.8, 0.2 }, seed);
            // check1: verify splits. expand frSplits with DataInfo and check against expandSplits
            checkSplits(frSplits, expandSplits, interactions, useAll, standardize, skipMissing);
            // now take the test frame from frSplits, and adapt it to a DataInfo built on the train frame
            dinfo = DataInfoTestAdapt.makeInfo(frSplits[0], interactions, useAll, standardize, skipMissing);
            GLMModel.GLMParameters parms = new GLMModel.GLMParameters();
            parms._response_column = "IsDepDelayed";
            Model.InteractionBuilder interactionBldr = DataInfoTestAdapt.interactionBuilder(dinfo);
            Model.adaptTestForTrain(frSplits[1], null, null, dinfo._adaptedFrame.names(), dinfo._adaptedFrame.domains(), parms, true, false, interactionBldr, null, null, false);
            scoreInfo = dinfo.scoringInfo(dinfo._adaptedFrame._names, frSplits[1]);
            checkFrame(scoreInfo, expandSplits[1], skipMissing);
        } finally {
            cleanup(fr, frA, expanded);
            cleanup(frSplits);
            cleanup(expandSplits);
            cleanup(dinfo, scoreInfo);
        }
    }
}

