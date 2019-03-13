package com.orm;


import RuntimeEnvironment.application;
import com.orm.dsl.BuildConfig;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author jonatan.salas
 */
// @Test
// public void testNotNullConfigurationWithSugarDb() {
// SugarDbConfiguration configuration = new SugarDbConfiguration()
// .setDatabaseLocale(Locale.getDefault())
// .setMaxSize(100000L)
// .setPageSize(100000L);
// 
// SugarContext.init(RuntimeEnvironment.application, configuration);
// 
// SQLiteDatabase database = SugarContext.getSugarContext().getSugarDb().getDB();
// SQLiteDatabase sqLiteDatabase = SugarDb.getInstance().getDB();
// 
// assertEquals(database.getMaximumSize(), sqLiteDatabase.getMaximumSize());
// assertEquals(database.getPageSize(), sqLiteDatabase.getPageSize());
// 
// if (sqLiteDatabase.isOpen()) {
// sqLiteDatabase.close();
// }
// }
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 16, constants = BuildConfig.class)
public final class SugarDbConfigurationTest {
    @Test
    public void testNotNullConfiguration() {
        SugarDbConfiguration configuration = new SugarDbConfiguration().setDatabaseLocale(Locale.getDefault()).setMaxSize(1024L).setPageSize(400L);
        SugarContext.init(application, configuration);
        final SugarDbConfiguration config = SugarContext.getDbConfiguration();
        Assert.assertEquals(configuration.getDatabaseLocale(), config.getDatabaseLocale());
        Assert.assertEquals(configuration.getMaxSize(), config.getMaxSize());
        Assert.assertEquals(configuration.getPageSize(), config.getPageSize());
    }

    @Test
    public void testNullConfiguration() {
        SugarContext.init(application);
        Assert.assertNull(SugarContext.getDbConfiguration());
    }
}

