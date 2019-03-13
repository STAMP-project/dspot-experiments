package com.vaadin.tools;


import com.vaadin.tools.CvalChecker.CvalServer;
import com.vaadin.tools.CvalChecker.InvalidCvalException;
import com.vaadin.tools.CvalChecker.UnreachableCvalServerException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;


/**
 * The CvalChecker test.
 */
public class CvalCheckerTest {
    static final String productNameCval = "test.cval";

    static final String productTitleCval = "Vaadin Test";

    static final String productNameAgpl = "test.agpl";

    static final String productTitleAgpl = "Vaadin Test";

    static final String productNameApache = "test.apache";

    static final String VALID_KEY = "valid";

    static final String INVALID_KEY = "invalid";

    static final String responseJson = (((((("{'licenseKey':'" + (CvalCheckerTest.VALID_KEY)) + "',") + "'licensee':'Test User','type':'normal',") + "'expiredEpoch':1893511225000,") + "'product':{'name':'") + (CvalCheckerTest.productNameCval)) + "', 'version': 2}}";

    static final String responseJsonWithNullVersion = (((((("{'licenseKey':'" + (CvalCheckerTest.VALID_KEY)) + "',") + "'licensee':'Test User','type':'normal',") + "'expiredEpoch':1893511225000,") + "'product':{'name':'") + (CvalCheckerTest.productNameCval)) + "', 'version': null}}";

    private static ByteArrayOutputStream outContent;

    // A provider returning a valid license if productKey is valid or null if
    // invalid
    static final CvalServer validLicenseProvider = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout) {
            return CvalCheckerTest.VALID_KEY.equals(productKey) ? CvalCheckerTest.responseJson : null;
        }
    };

    // A provider returning a valid evaluation license
    static final CvalServer validEvaluationLicenseProvider = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout) {
            return CvalCheckerTest.responseJson.replace("normal", "evaluation");
        }
    };

    // A provider returning an expired license with a server message
    static final CvalServer expiredLicenseProviderWithMessage = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout) {
            return CvalCheckerTest.responseJson.replace("'expired", "\'message\':\'Custom\\\\nServer\\\\nMessage\',\'expired\':true,\'expired");
        }
    };

    // A provider returning an expired license with a server message
    static final CvalServer expiredLicenseProvider = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout) {
            return CvalCheckerTest.responseJson.replace("'expired", "'expired':true,'expired");
        }
    };

    // A provider returning an expired epoch license
    static final CvalServer expiredEpochLicenseProvider = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout) {
            long ts = ((System.currentTimeMillis()) - (CvalChecker.GRACE_DAYS_MSECS)) - 1000;
            return CvalCheckerTest.responseJson.replace("1893511225000", ("" + ts));
        }
    };

    // A provider returning an unlimited license
    static final CvalServer unlimitedLicenseProvider = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout) {
            return CvalCheckerTest.responseJson.replaceFirst("1893511225000", "null");
        }
    };

    // An unreachable provider
    static final CvalServer unreachableLicenseProvider = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout) throws IOException {
            // Normally there is no route for this ip in public routers, so we
            // should get a timeout.
            licenseUrl = "http://localhost:9999/";
            return super.askServer(productName, productKey, 1000);
        }
    };

    // A provider with 'null' in the version field
    static final CvalServer nullVersionLicenseProvider = new CvalServer() {
        @Override
        String askServer(String productName, String productKey, int timeout) throws IOException {
            return CvalCheckerTest.responseJsonWithNullVersion;
        }
    };

    private CvalChecker licenseChecker;

    private String licenseName;

    @Test
    public void testValidateProduct() throws Exception {
        CvalChecker.deleteCache(CvalCheckerTest.productNameCval);
        // If the license key in our environment is null, throw an exception
        try {
            licenseChecker.validateProduct(CvalCheckerTest.productNameCval, "2.1", CvalCheckerTest.productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            Assert.assertEquals(CvalCheckerTest.productNameCval, expected.name);
        }
        Assert.assertFalse(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
        // If the license key is empty, throw an exception
        System.setProperty(licenseName, "");
        try {
            licenseChecker.validateProduct(CvalCheckerTest.productNameCval, "2.1", CvalCheckerTest.productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            Assert.assertEquals(CvalCheckerTest.productNameCval, expected.name);
        }
        Assert.assertFalse(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
        // If license key is invalid, throw an exception
        System.setProperty(licenseName, "invalid");
        try {
            licenseChecker.validateProduct(CvalCheckerTest.productNameCval, "2.1", CvalCheckerTest.productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            Assert.assertEquals(CvalCheckerTest.productNameCval, expected.name);
        }
        Assert.assertFalse(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
        // Fail if version is bigger
        System.setProperty(licenseName, CvalCheckerTest.VALID_KEY);
        try {
            licenseChecker.validateProduct(CvalCheckerTest.productNameCval, "3.0", CvalCheckerTest.productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            Assert.assertEquals(CvalCheckerTest.productNameCval, expected.name);
            Assert.assertTrue(expected.getMessage().contains("is not valid"));
        }
        Assert.assertFalse(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
        // Success if license key and version are valid
        System.setProperty(licenseName, CvalCheckerTest.VALID_KEY);
        licenseChecker.validateProduct(CvalCheckerTest.productNameCval, "2.1", CvalCheckerTest.productTitleCval);
        Assert.assertTrue(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
        // Success if license and cache file are valid, although the license
        // server is offline
        licenseChecker.setLicenseProvider(CvalCheckerTest.unreachableLicenseProvider);
        licenseChecker.validateProduct(CvalCheckerTest.productNameCval, "2.1", CvalCheckerTest.productTitleCval);
        Assert.assertTrue(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
        // Fail if license key changes although cache file were validated
        // previously and it is ok, we are offline
        try {
            System.setProperty(licenseName, CvalCheckerTest.INVALID_KEY);
            licenseChecker.validateProduct(CvalCheckerTest.productNameCval, "2.1", CvalCheckerTest.productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            Assert.fail();
        } catch (UnreachableCvalServerException expected) {
            Assert.assertEquals(CvalCheckerTest.productNameCval, expected.name);
        }
        Assert.assertFalse(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
        // Fail with unreachable exception if license has never verified and
        // server is offline
        try {
            System.setProperty(licenseName, CvalCheckerTest.VALID_KEY);
            licenseChecker.validateProduct(CvalCheckerTest.productNameCval, "2.1", CvalCheckerTest.productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            Assert.fail();
        } catch (UnreachableCvalServerException expected) {
            Assert.assertEquals(CvalCheckerTest.productNameCval, expected.name);
        }
        Assert.assertFalse(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
        // Fail when expired flag comes in the server response, although the
        // expired is valid.
        CvalChecker.deleteCache(CvalCheckerTest.productNameCval);
        licenseChecker.setLicenseProvider(CvalCheckerTest.expiredLicenseProviderWithMessage);
        try {
            licenseChecker.validateProduct(CvalCheckerTest.productNameCval, "2.1", CvalCheckerTest.productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            Assert.assertEquals(CvalCheckerTest.productNameCval, expected.name);
            // Check that we use server customized message if it comes
            Assert.assertTrue(expected.getMessage().contains("Custom"));
        }
        Assert.assertTrue(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
        // Check an unlimited license
        CvalChecker.deleteCache(CvalCheckerTest.productNameCval);
        licenseChecker.setLicenseProvider(CvalCheckerTest.unlimitedLicenseProvider);
        licenseChecker.validateProduct(CvalCheckerTest.productNameCval, "2.1", CvalCheckerTest.productTitleCval);
        Assert.assertTrue(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
        // Fail if expired flag does not come, but expired epoch is in the past
        System.setProperty(licenseName, CvalCheckerTest.VALID_KEY);
        CvalChecker.deleteCache(CvalCheckerTest.productNameCval);
        licenseChecker.setLicenseProvider(CvalCheckerTest.expiredEpochLicenseProvider);
        try {
            licenseChecker.validateProduct(CvalCheckerTest.productNameCval, "2.1", CvalCheckerTest.productTitleCval);
            Assert.fail();
        } catch (InvalidCvalException expected) {
            Assert.assertEquals(CvalCheckerTest.productNameCval, expected.name);
        }
        Assert.assertTrue(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
        CvalChecker.deleteCache(CvalCheckerTest.productNameCval);
        licenseChecker.setLicenseProvider(CvalCheckerTest.nullVersionLicenseProvider);
        licenseChecker.validateProduct(CvalCheckerTest.productNameCval, "2.1", CvalCheckerTest.productTitleCval);
        Assert.assertTrue(CvalCheckerTest.cacheExists(CvalCheckerTest.productNameCval));
    }

    @Test(expected = FileNotFoundException.class)
    public void testReadKeyFromFile_NonexistingLicenseFile() throws Exception {
        licenseChecker.readKeyFromFile(new URL("file:///foobar.baz"), 4);
    }

    @Test
    public void testReadKeyFromFile_LicenseFileEmpty() throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        Assert.assertNull(licenseChecker.readKeyFromFile(tmpLicenseFile.toURI().toURL(), 4));
        tmpLicenseFile.delete();
    }

    @Test
    public void testReadKeyFromFile_LicenseFileHasSingleUnidentifiedKey() throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        PrintWriter out = new PrintWriter(tmpLicenseFile);
        out.println("this-is-a-license");
        out.close();
        Assert.assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI().toURL(), 4));
        tmpLicenseFile.delete();
    }

    @Test
    public void testReadKeyFromFile_LicenseFileHasSingleIdentifiedKey() throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        PrintWriter out = new PrintWriter(tmpLicenseFile);
        out.println("4=this-is-a-license");
        out.close();
        Assert.assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI().toURL(), 4));
        tmpLicenseFile.delete();
    }

    @Test
    public void testReadKeyFromFile_LicenseFileHasMultipleKeys() throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        PrintWriter out = new PrintWriter(tmpLicenseFile);
        out.println("4=this-is-a-license");
        out.println("5=this-is-another-license");
        out.close();
        Assert.assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI().toURL(), 4));
        Assert.assertEquals("this-is-another-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI().toURL(), 5));
        tmpLicenseFile.delete();
    }

    @Test
    public void testReadKeyFromFile_LicenseFileHasMultipleKeysWithWhitespace() throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        PrintWriter out = new PrintWriter(tmpLicenseFile);
        out.println("4 = this-is-a-license");
        out.println("5 = this-is-another-license");
        out.close();
        Assert.assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI().toURL(), 4));
        Assert.assertEquals("this-is-another-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI().toURL(), 5));
        tmpLicenseFile.delete();
    }

    @Test
    public void testReadKeyFromFile_RequestedVersionMissing() throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        PrintWriter out = new PrintWriter(tmpLicenseFile);
        out.println("4 = this-is-a-license");
        out.println("5 = this-is-another-license");
        out.close();
        Assert.assertNull(licenseChecker.readKeyFromFile(tmpLicenseFile.toURI().toURL(), 3));
        tmpLicenseFile.delete();
    }

    @Test
    public void testReadKeyFromFile_FallbackToDefaultKey() throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        PrintWriter out = new PrintWriter(tmpLicenseFile);
        out.println("this-is-a-license");
        out.println("5 = this-is-another-license");
        out.close();
        Assert.assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI().toURL(), 3));
        Assert.assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI().toURL(), 4));
        Assert.assertEquals("this-is-another-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI().toURL(), 5));
        tmpLicenseFile.delete();
    }

    @Test
    public void testReadKeyFromFile_FallbackToDefaultKeyReversed() throws Exception {
        File tmpLicenseFile = File.createTempFile("license", "lic");
        PrintWriter out = new PrintWriter(tmpLicenseFile);
        out.println("5 = this-is-another-license");
        out.println("this-is-a-license");
        out.close();
        Assert.assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI().toURL(), 3));
        Assert.assertEquals("this-is-a-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI().toURL(), 4));
        Assert.assertEquals("this-is-another-license", licenseChecker.readKeyFromFile(tmpLicenseFile.toURI().toURL(), 5));
        tmpLicenseFile.delete();
    }
}

