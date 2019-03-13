package com.orm;


import android.database.sqlite.SQLiteDatabase;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
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
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class SugarDbTest {
    private final SugarDb sugarDb = SugarDb.getInstance();

    // TODO check this better!
    @Test
    public void testGetReadableDatabase() {
        final SQLiteDatabase db = sugarDb.getReadableDatabase();
        Assert.assertEquals(false, db.isReadOnly());
    }

    @Test
    public void testGetWritableDatabase() {
        final SQLiteDatabase db = sugarDb.getWritableDatabase();
        Assert.assertEquals(false, db.isReadOnly());
    }

    @Test
    public void testGetDB() {
        final SQLiteDatabase db = sugarDb.getDB();
        Assert.assertEquals(false, db.isReadOnly());
    }
}

