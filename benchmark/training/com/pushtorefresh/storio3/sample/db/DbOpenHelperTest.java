package com.pushtorefresh.storio3.sample.db;


import android.database.sqlite.SQLiteDatabase;
import com.pushtorefresh.storio3.contentresolver.BuildConfig;
import com.pushtorefresh.storio3.sample.SampleRobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;


@RunWith(SampleRobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public final class DbOpenHelperTest {
    @Test
    public void shouldCreateDb() {
        SQLiteDatabase database = getWritableDatabase();
        assertThat(database).isNotNull();
    }
}

