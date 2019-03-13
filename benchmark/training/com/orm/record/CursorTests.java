package com.orm.record;


import Build.VERSION_CODES;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import com.orm.SugarRecord;
import com.orm.app.ClientApp;
import com.orm.dsl.BuildConfig;
import com.orm.model.SimpleModel;
import com.orm.query.Select;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;


@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 18, constants = BuildConfig.class, application = ClientApp.class, packageName = "com.orm.model", manifest = Config.NONE)
public final class CursorTests {
    @Test
    public void testColumnNames() {
        SugarRecord.save(new SimpleModel());
        Cursor c = Select.from(SimpleModel.class).getCursor();
        for (String col : new String[]{ "STR", "INTEGER", "BOOL", "ID" }) {
            Assert.assertNotSame(("Missing column for field: " + col), (-1), c.getColumnIndex(col));
        }
    }

    @Test
    public void testSugarCursor() {
        SugarRecord.save(new SimpleModel());
        Cursor cursor = Select.from(SimpleModel.class).getCursor();
        Assert.assertNotSame("No _id", (-1), cursor.getColumnIndex("_id"));
        Assert.assertSame("_id != ID", cursor.getColumnIndex("_id"), cursor.getColumnIndex("ID"));
    }

    @Test
    public void testNoColumn() {
        SugarRecord.save(new SimpleModel());
        Cursor cursor = Select.from(SimpleModel.class).getCursor();
        Assert.assertSame((-1), cursor.getColumnIndex("nonexistent"));
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    @Test
    public void testMakeAdapter() {
        SugarRecord.save(new SimpleModel());
        Cursor c = Select.from(SimpleModel.class).getCursor();
        CursorAdapter adapter = new CursorAdapter(RuntimeEnvironment.application, c, true) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                TextView tv = new TextView(context);
                String s = cursor.getString(cursor.getColumnIndex("STR"));
                tv.setText(s);
                return null;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                String s = cursor.getString(cursor.getColumnIndex("STR"));
                setText(s);
            }
        };
        assertNotNull(adapter);
    }
}

