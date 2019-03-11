package hex;


import hex.drf.DRF;
import hex.drf.DRF.DRFModel;
import org.junit.Test;
import water.TestUtil;


public class DHistTest extends TestUtil {
    @Test
    public void testDBinom() {
        DRF drf = new DRF();
        Key destTrain = Key.make("data.hex");
        DRFModel model = null;
        try {
            // Configure DRF
            drf.source = TestUtil.parseFrame(destTrain, "../smalldata/histogram_test/alphabet_cattest.csv");
            drf.response = drf.source.vecs()[1];
            drf.classification = true;
            drf.ntrees = 100;
            drf.max_depth = 5;// = interaction.depth

            drf.min_rows = 10;// = nodesize

            drf.nbins = 100;
            drf.destination_key = Key.make("DRF_model_dhist.hex");
            // Invoke DRF and block till the end
            drf.invoke();
            // Get the model
            model = UKV.get(drf.dest());
        } finally {
            drf.source.delete();
            drf.remove();
            if (model != null)
                model.delete();
            // Remove the model

        }
    }
}

