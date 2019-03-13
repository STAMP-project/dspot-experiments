package marytts.modules;


import MaryDataType.TEXT;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * Assert that we can override the module part using a system property
 *
 * @author <a href="mailto:slemaguer@coli.uni-saarland.de">S?bastien Le Maguer</a>
 */
public class TestOverrideModules {
    MaryInterface mary;

    @Test
    public void testSystemPreferredModuleOverride() throws Exception {
        List<MaryModule> mod = ModuleRegistry.getPreferredModulesForInputType(TEXT);
        Assert.assertNotNull(mod);
        assert !(mod.isEmpty());
        Assert.assertEquals(mod.get(0).name(), "Dummy");
    }
}

