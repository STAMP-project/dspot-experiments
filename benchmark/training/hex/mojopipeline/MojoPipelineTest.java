package hex.mojopipeline;


import ai.h2o.mojos.runtime.MojoPipeline;
import ai.h2o.mojos.runtime.hex.mojopipeline.MojoPipeline;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import water.Scope;
import water.TestUtil;
import water.fvec.ByteVec;
import water.fvec.Frame;


@RunWith(Parameterized.class)
public class MojoPipelineTest extends TestUtil {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Parameterized.Parameter
    public String testCase;

    private String dataFile;

    private String mojoFile;

    @Test
    public void transform() throws Exception {
        try {
            Scope.enter();
            // get the expected data
            MojoPipeline model = null;
            MojoFrame expected = null;
            try {
                model = MojoPipeline.loadFrom(mojoFile);
                expected = transformDirect(model, dataFile);
            } catch (Exception e) {
                Assume.assumeNoException(e);
            }
            Assert.assertNotNull(model);
            Assert.assertNotNull(expected);
            final ByteVec mojoData = makeNfsFileVec(mojoFile);
            final Frame t = Scope.track(loadData(model));
            hex.mojopipeline.MojoPipeline mp = new hex.mojopipeline.MojoPipeline(mojoData);
            Frame transformed = mp.transform(t, false);
            System.out.println(transformed.toTwoDimTable().toString());
            MojoPipelineTest.assertFrameEquals(transformed, expected);
        } finally {
            Scope.exit();
        }
    }
}

