package hex.tree.isofor;


import IsolationForestModel.IsolationForestParameters;
import org.junit.Assert;
import org.junit.Test;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;


public class IsolationForestTest extends TestUtil {
    @Test
    public void testBasic() {
        try {
            Scope.enter();
            Frame train = Scope.track(parse_test_file("smalldata/anomaly/ecg_discord_train.csv"));
            IsolationForestModel.IsolationForestParameters p = new IsolationForestModel.IsolationForestParameters();
            p._train = train._key;
            p._seed = 912559;
            p._ntrees = 7;
            p._min_rows = 1;
            p._sample_size = 5;
            IsolationForestModel model = trainModel().get();
            Assert.assertNotNull(model);
            Scope.track_generic(model);
            Frame preds = Scope.track(model.score(train));
            Assert.assertArrayEquals(new String[]{ "predict", "mean_length" }, preds.names());
            Assert.assertEquals(train.numRows(), preds.numRows());
            Assert.assertTrue(model.testJavaScoring(train, preds, 1.0E-8));
            Assert.assertTrue(((model._output._min_path_length) < (Integer.MAX_VALUE)));
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testEmptyOOB() {
        try {
            Scope.enter();
            Frame train = Scope.track(parse_test_file("smalldata/anomaly/ecg_discord_train.csv"));
            IsolationForestModel.IsolationForestParameters p = new IsolationForestModel.IsolationForestParameters();
            p._train = train._key;
            p._seed = 912559;
            p._ntrees = 7;
            p._sample_size = train.numRows();// => no OOB observations

            IsolationForestModel model = trainModel().get();
            Assert.assertNotNull(model);
            Scope.track_generic(model);
            Frame preds = Scope.track(model.score(train));
            Assert.assertArrayEquals(new String[]{ "predict", "mean_length" }, preds.names());
            Assert.assertEquals(train.numRows(), preds.numRows());
            Assert.assertTrue(model.testJavaScoring(train, preds, 1.0E-8));
            Assert.assertTrue(((model._output._min_path_length) < (Integer.MAX_VALUE)));
        } finally {
            Scope.exit();
        }
    }
}

