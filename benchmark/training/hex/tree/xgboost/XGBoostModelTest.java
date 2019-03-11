package hex.tree.xgboost;


import H2O.ARGS;
import H2O.ARGS.nthreads;
import XGBoostModel.XGBoostParameters;
import XGBoostModel.XGBoostParameters.Backend;
import XGBoostModel.XGBoostParameters.GrowPolicy;
import XGBoostModel.XGBoostParameters.GrowPolicy.lossguide;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class XGBoostModelTest {
    @Test
    public void testCreateParamsNThreads() throws Exception {
        // default
        XGBoostModel.XGBoostParameters pDefault = new XGBoostModel.XGBoostParameters();
        pDefault._backend = Backend.cpu;// to disable the GPU check

        BoosterParms bpDefault = XGBoostModel.createParams(pDefault, 2, null);
        Assert.assertEquals(((int) (nthreads)), bpDefault.get().get("nthread"));
        // user specified
        XGBoostModel.XGBoostParameters pUser = new XGBoostModel.XGBoostParameters();
        pUser._backend = Backend.cpu;// to disable the GPU check

        pUser._nthread = (ARGS.nthreads) - 1;
        BoosterParms bpUser = XGBoostModel.createParams(pUser, 2, null);
        Assert.assertEquals((((int) (ARGS.nthreads)) - 1), bpUser.get().get("nthread"));
        // user specified (over the limit)
        XGBoostModel.XGBoostParameters pOver = new XGBoostModel.XGBoostParameters();
        pOver._backend = Backend.cpu;// to disable the GPU check

        pOver._nthread = (ARGS.nthreads) + 1;
        BoosterParms bpOver = XGBoostModel.createParams(pOver, 2, null);
        Assert.assertEquals(((int) (nthreads)), bpOver.get().get("nthread"));
    }

    @Test
    public void gpuIncompatibleParametersMaxDepth() {
        XGBoostModel.XGBoostParameters xgBoostParameters = new XGBoostModel.XGBoostParameters();
        xgBoostParameters._max_depth = 16;
        Map<String, Object> incompatibleParams = xgBoostParameters.gpuIncompatibleParams();
        Assert.assertEquals(incompatibleParams.size(), 1);
        Assert.assertEquals(incompatibleParams.get("max_depth"), (16 + " . Max depth must be greater than 0 and lower than 16 for GPU backend."));
        xgBoostParameters._max_depth = 0;
        incompatibleParams = xgBoostParameters.gpuIncompatibleParams();
        Assert.assertEquals(incompatibleParams.size(), 1);
        Assert.assertEquals(incompatibleParams.get("max_depth"), (0 + " . Max depth must be greater than 0 and lower than 16 for GPU backend."));
    }

    @Test
    public void gpuIncompatibleParametersGrowPolicy() {
        XGBoostModel.XGBoostParameters xgBoostParameters = new XGBoostModel.XGBoostParameters();
        xgBoostParameters._grow_policy = GrowPolicy.lossguide;
        Map<String, Object> incompatibleParams = xgBoostParameters.gpuIncompatibleParams();
        Assert.assertEquals(incompatibleParams.size(), 1);
        Assert.assertEquals(incompatibleParams.get("grow_policy"), lossguide);
    }
}

