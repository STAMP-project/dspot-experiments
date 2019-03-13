package com.orm.helper;


import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 *
 *
 * @author jonatan.salas
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class ManifestHelperTest {
    @Test(expected = IllegalAccessException.class)
    public void testPrivateConstructor() throws Exception {
        ManifestHelper helper = ManifestHelper.class.getDeclaredConstructor().newInstance();
        Assert.assertNull(helper);
    }

    @Test
    public void testGetDbName() {
        org.junit.Assert.assertEquals(ManifestHelper.DATABASE_DEFAULT_NAME, ManifestHelper.getDatabaseName());
    }

    @Test
    public void testGetDatabaseName() {
        org.junit.Assert.assertEquals(ManifestHelper.DATABASE_DEFAULT_NAME, ManifestHelper.getDatabaseName());
    }

    @Test
    public void testGetDatabaseVersion() {
        assertEquals(1, ManifestHelper.getDatabaseVersion());
    }

    @Test
    public void testGetDomainPackageName() {
        assertNotNull(ManifestHelper.getDomainPackageName());
    }

    @Test
    public void testGetDebugEnabled() {
        assertEquals(false, ManifestHelper.isDebugEnabled());
    }
}

