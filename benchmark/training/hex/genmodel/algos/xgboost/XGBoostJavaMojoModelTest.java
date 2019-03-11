package hex.genmodel.algos.xgboost;


import XGBoostMojoModel.ObjectiveType;
import org.junit.Assert;
import org.junit.Test;


public class XGBoostJavaMojoModelTest {
    @Test
    public void testObjFunction() {
        // make sure we have implementation for all supported obj functions
        for (XGBoostMojoModel.ObjectiveType type : ObjectiveType.values()) {
            Assert.assertNotNull(type.getId());
            Assert.assertFalse(type.getId().isEmpty());
            // check we have an implementation of ObjFunction
            Assert.assertNotNull(XGBoostJavaMojoModel.getObjFunction(type.getId()));
        }
    }
}

