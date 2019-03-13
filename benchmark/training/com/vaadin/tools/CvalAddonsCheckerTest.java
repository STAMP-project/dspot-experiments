package com.vaadin.tools;


import com.vaadin.client.metadata.ConnectorBundleLoader.CValUiInfo;
import com.vaadin.tools.CvalChecker.InvalidCvalException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * The CvalAddonsChecker test.
 */
public class CvalAddonsCheckerTest {
    CvalAddonsChecker addonChecker;

    private String licenseName;

    @Test
    public void testRunChecker() throws Exception {
        // Create a product .jar with a cval license non required and add to our
        // classpath
        CvalCheckerTest.addLicensedJarToClasspath(CvalCheckerTest.productNameCval, CvalAddonsChecker.VAADIN_CVAL);
        // Remove other products in case other tests added them previously
        CvalCheckerTest.addLicensedJarToClasspath(CvalCheckerTest.productNameAgpl, null);
        CvalCheckerTest.addLicensedJarToClasspath(CvalCheckerTest.productNameApache, null);
        // No license
        // -> Break compilation
        System.getProperties().remove(licenseName);
        addonChecker.setLicenseProvider(CvalCheckerTest.validLicenseProvider);
        try {
            addonChecker.run();
            Assert.fail();
        } catch (InvalidCvalException expected) {
        }
        Assert.assertFalse(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
        // We have a license that has never been validated from the server and
        // we are offline
        // -> Show a message on compile time (?Your license for TouchKit 4 has
        // not been validated.")
        System.setProperty(licenseName, CvalCheckerTest.VALID_KEY);
        addonChecker.setLicenseProvider(CvalCheckerTest.unreachableLicenseProvider);
        CvalCheckerTest.captureSystemOut();
        addonChecker.run();
        Assert.assertTrue(CvalCheckerTest.readSystemOut().contains("has not been validated"));
        Assert.assertFalse(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
        // Valid license has previously been validated from the server and we
        // are offline
        // -> Use the cached server response
        System.setProperty(licenseName, CvalCheckerTest.VALID_KEY);
        addonChecker.setLicenseProvider(CvalCheckerTest.validLicenseProvider);
        CvalCheckerTest.captureSystemOut();
        addonChecker.run();
        Assert.assertTrue(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
        addonChecker.setLicenseProvider(CvalCheckerTest.unreachableLicenseProvider);
        addonChecker.run();
        // Expired license and we are offline
        // -> If it has expired less than 14 days ago, just work with no nag
        // messages
        System.setProperty(licenseName, CvalCheckerTest.VALID_KEY);
        addonChecker.setLicenseProvider(CvalCheckerTest.unreachableLicenseProvider);
        setCacheFileTs(((System.currentTimeMillis()) - ((CvalChecker.GRACE_DAYS_MSECS) / 2)), "normal");
        CvalCheckerTest.captureSystemOut();
        addonChecker.run();
        // Expired license and we are offline
        // -> After 14 days, interpret it as expired license
        setCacheFileTs(((System.currentTimeMillis()) - ((CvalChecker.GRACE_DAYS_MSECS) * 2)), "normal");
        try {
            addonChecker.run();
            Assert.fail();
        } catch (InvalidCvalException expected) {
        }
        // Invalid evaluation license
        // -> Fail compilation with a message
        // "Your evaluation license for TouchKit 4 is not valid"
        System.setProperty(licenseName, CvalCheckerTest.VALID_KEY);
        addonChecker.setLicenseProvider(CvalCheckerTest.unreachableLicenseProvider);
        setCacheFileTs(((System.currentTimeMillis()) - ((CvalChecker.GRACE_DAYS_MSECS) / 2)), "evaluation");
        try {
            addonChecker.run();
            Assert.fail();
        } catch (InvalidCvalException expected) {
            Assert.assertTrue(expected.getMessage().contains("expired"));
        }
        // Valid evaluation license
        // -> The choice on whether to show the message is generated in
        // widgetset
        // compilation phase. No license checks are done in application runtime.
        System.setProperty(licenseName, CvalCheckerTest.VALID_KEY);
        addonChecker.setLicenseProvider(CvalCheckerTest.unreachableLicenseProvider);
        setCacheFileTs(((System.currentTimeMillis()) + (CvalChecker.GRACE_DAYS_MSECS)), "evaluation");
        List<CValUiInfo> uiInfo = addonChecker.run();
        Assert.assertEquals(1, uiInfo.size());
        Assert.assertEquals(("Test " + (CvalCheckerTest.productNameCval)), uiInfo.get(0).product);
        Assert.assertEquals("evaluation", uiInfo.get(0).type);
        // Valid real license
        // -> Work as expected
        // -> Show info message ?Using TouchKit 4 license
        // 312-312321-321312-3-12-312-312
        // licensed to <licensee> (1 developer license)"
        System.setProperty(licenseName, CvalCheckerTest.VALID_KEY);
        addonChecker.setLicenseProvider(CvalCheckerTest.validLicenseProvider);
        CvalCheckerTest.captureSystemOut();
        addonChecker.run();
        Assert.assertTrue(CvalCheckerTest.readSystemOut().contains("valid"));
    }

    @Test
    public void validateMultipleLicenses() throws Exception {
        CvalCheckerTest.addLicensedJarToClasspath(CvalCheckerTest.productNameCval, CvalAddonsChecker.VAADIN_CVAL);
        CvalCheckerTest.addLicensedJarToClasspath(CvalCheckerTest.productNameAgpl, CvalAddonsChecker.VAADIN_AGPL);
        CvalCheckerTest.addLicensedJarToClasspath(CvalCheckerTest.productNameApache, "apache");
        // We have a valid license for all products
        System.setProperty(licenseName, CvalCheckerTest.VALID_KEY);
        CvalCheckerTest.captureSystemOut();
        addonChecker.run();
        String out = CvalCheckerTest.readSystemOut();
        Assert.assertTrue(out.contains("valid"));
        Assert.assertTrue(out.contains("AGPL"));
        Assert.assertTrue(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
    }
}

