package water.rapids.ast.prims.misc;


import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.rapids.Rapids;
import water.rapids.Val;
import water.rapids.vals.ValStr;


public class SetClusterPropertyTaskTest extends TestUtil {
    @Test
    public void testSetClusterProperty() {
        Val val = Rapids.exec("(setproperty 'test.set.cluster.property' 'test-value')");
        Assert.assertTrue((val instanceof ValStr));
        doAllNodes();
        Val oldVal = Rapids.exec("(setproperty 'test.set.cluster.property' 'test-value2')");
        Assert.assertTrue(oldVal.getStr().startsWith("Old values of test.set.cluster.property (per node): test-value"));
    }
}

