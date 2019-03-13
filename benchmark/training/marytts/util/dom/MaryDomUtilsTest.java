/**
 *
 */
package marytts.util.dom;


import marytts.exceptions.MaryConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 *
 *
 * @author marc
 */
public class MaryDomUtilsTest {
    @Test
    public void canValidateMaryXML() throws MaryConfigurationException {
        // setup SUT
        Document doc = createValidMaryXML();
        // exercise / verify
        Assert.assertTrue(MaryDomUtils.isSchemaValid(doc));
    }

    @Test
    public void canDetectInvalidMaryXML() throws MaryConfigurationException {
        // setup SUT
        Document doc = createInvalidMaryXML();
        // exercise / verify
        Assert.assertFalse(MaryDomUtils.isSchemaValid(doc));
    }
}

