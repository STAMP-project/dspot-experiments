package com.ctrip.framework.foundation.internals.provider;


import java.io.File;
import java.io.FileInputStream;
import org.junit.Assert;
import org.junit.Test;


public class DefaultApplicationProviderTest {
    private DefaultApplicationProvider defaultApplicationProvider;

    String PREDEFINED_APP_ID = "110402";

    @Test
    public void testLoadAppProperties() throws Exception {
        defaultApplicationProvider.initialize();
        Assert.assertEquals(PREDEFINED_APP_ID, defaultApplicationProvider.getAppId());
        Assert.assertTrue(defaultApplicationProvider.isAppIdSet());
    }

    @Test
    public void testLoadAppPropertiesWithUTF8Bom() throws Exception {
        File baseDir = new File("src/test/resources/META-INF");
        File appProperties = new File(baseDir, "app-with-utf8bom.properties");
        defaultApplicationProvider.initialize(new FileInputStream(appProperties));
        Assert.assertEquals(PREDEFINED_APP_ID, defaultApplicationProvider.getAppId());
        Assert.assertTrue(defaultApplicationProvider.isAppIdSet());
    }

    @Test
    public void testLoadAppPropertiesWithSystemProperty() throws Exception {
        String someAppId = "someAppId";
        System.setProperty("app.id", someAppId);
        defaultApplicationProvider.initialize();
        System.clearProperty("app.id");
        Assert.assertEquals(someAppId, defaultApplicationProvider.getAppId());
        Assert.assertTrue(defaultApplicationProvider.isAppIdSet());
    }

    @Test
    public void testLoadAppPropertiesFailed() throws Exception {
        File baseDir = new File("src/test/resources/META-INF");
        File appProperties = new File(baseDir, "some-invalid-app.properties");
        defaultApplicationProvider.initialize(new FileInputStream(appProperties));
        Assert.assertEquals(null, defaultApplicationProvider.getAppId());
        Assert.assertFalse(defaultApplicationProvider.isAppIdSet());
    }
}

