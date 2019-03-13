/**
 *
 */
package marytts.config;


import marytts.server.MaryProperties;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author marc
 */
public class MainConfigTest {
    private MaryConfig mc;

    @Test
    public void isMainConfig() {
        Assert.assertTrue(mc.isMainConfig());
    }

    @Test
    public void hasProperties() {
        Assert.assertNotNull(mc.getProperties());
    }

    @Test
    public void hasModules() {
        Assert.assertNotNull(MaryProperties.moduleInitInfo());
    }

    @Test
    public void hasSynthesizers() {
        Assert.assertNotNull(MaryProperties.synthesizerClasses());
    }

    @Test
    public void hasEffects() {
        Assert.assertNotNull(MaryProperties.effectClasses());
    }
}

