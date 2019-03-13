package org.robolectric.shadows;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class SQLiteOpenHelperTest {
    private SQLiteOpenHelperTest.TestOpenHelper helper;

    @Test
    public void testConstructorWithNullPathShouldCreateInMemoryDatabase() throws Exception {
        SQLiteOpenHelperTest.TestOpenHelper helper = new SQLiteOpenHelperTest.TestOpenHelper(null, null, null, 1);
        SQLiteDatabase database = getReadableDatabase();
        SQLiteOpenHelperTest.assertDatabaseOpened(database, helper);
        SQLiteOpenHelperTest.assertInitialDB(database, helper);
    }

    @Test
    public void testInitialGetReadableDatabase() throws Exception {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteOpenHelperTest.assertInitialDB(database, helper);
    }

    @Test
    public void testSubsequentGetReadableDatabase() throws Exception {
        helper.getReadableDatabase();
        helper.close();
        SQLiteDatabase database = getReadableDatabase();
        SQLiteOpenHelperTest.assertSubsequentDB(database, helper);
    }

    @Test
    public void testSameDBInstanceSubsequentGetReadableDatabase() throws Exception {
        SQLiteDatabase db1 = getReadableDatabase();
        SQLiteDatabase db2 = getReadableDatabase();
        assertThat(db1).isSameAs(db2);
    }

    @Test
    public void testInitialGetWritableDatabase() throws Exception {
        SQLiteDatabase database = getWritableDatabase();
        SQLiteOpenHelperTest.assertInitialDB(database, helper);
    }

    @Test
    public void testSubsequentGetWritableDatabase() throws Exception {
        helper.getWritableDatabase();
        helper.close();
        SQLiteOpenHelperTest.assertSubsequentDB(helper.getWritableDatabase(), helper);
    }

    @Test
    public void testSameDBInstanceSubsequentGetWritableDatabase() throws Exception {
        SQLiteDatabase db1 = getWritableDatabase();
        SQLiteDatabase db2 = getWritableDatabase();
        assertThat(db1).isSameAs(db2);
    }

    @Test
    public void testClose() throws Exception {
        SQLiteDatabase database = getWritableDatabase();
        assertThat(database.isOpen()).isTrue();
        helper.close();
        assertThat(database.isOpen()).isFalse();
    }

    @Test
    public void testGetPath() throws Exception {
        final String path1 = "path1";
        final String path2 = "path2";
        SQLiteOpenHelperTest.TestOpenHelper helper1 = new SQLiteOpenHelperTest.TestOpenHelper(ApplicationProvider.getApplicationContext(), path1, null, 1);
        String expectedPath1 = ApplicationProvider.getApplicationContext().getDatabasePath(path1).getAbsolutePath();
        assertThat(helper1.getReadableDatabase().getPath()).isEqualTo(expectedPath1);
        SQLiteOpenHelperTest.TestOpenHelper helper2 = new SQLiteOpenHelperTest.TestOpenHelper(ApplicationProvider.getApplicationContext(), path2, null, 1);
        String expectedPath2 = ApplicationProvider.getApplicationContext().getDatabasePath(path2).getAbsolutePath();
        assertThat(helper2.getReadableDatabase().getPath()).isEqualTo(expectedPath2);
    }

    @Test
    public void testCloseMultipleDbs() throws Exception {
        SQLiteOpenHelperTest.TestOpenHelper helper2 = new SQLiteOpenHelperTest.TestOpenHelper(ApplicationProvider.getApplicationContext(), "path2", null, 1);
        SQLiteDatabase database1 = getWritableDatabase();
        SQLiteDatabase database2 = getWritableDatabase();
        assertThat(database1.isOpen()).isTrue();
        assertThat(database2.isOpen()).isTrue();
        helper.close();
        assertThat(database1.isOpen()).isFalse();
        assertThat(database2.isOpen()).isTrue();
        helper2.close();
        assertThat(database2.isOpen()).isFalse();
    }

    @Test
    public void testOpenMultipleDbsOnCreate() throws Exception {
        SQLiteOpenHelperTest.TestOpenHelper helper2 = new SQLiteOpenHelperTest.TestOpenHelper(ApplicationProvider.getApplicationContext(), "path2", null, 1);
        assertThat(helper.onCreateCalled).isFalse();
        assertThat(helper2.onCreateCalled).isFalse();
        helper.getWritableDatabase();
        assertThat(helper.onCreateCalled).isTrue();
        assertThat(helper2.onCreateCalled).isFalse();
        helper2.getWritableDatabase();
        assertThat(helper.onCreateCalled).isTrue();
        assertThat(helper2.onCreateCalled).isTrue();
        helper.close();
        helper2.close();
    }

    @Test
    public void testMultipleDbsPreserveData() throws Exception {
        final String TABLE_NAME1 = "fart";
        final String TABLE_NAME2 = "fart2";
        SQLiteDatabase db1 = getWritableDatabase();
        setupTable(db1, TABLE_NAME1);
        insertData(db1, TABLE_NAME1, new int[]{ 1, 2 });
        SQLiteOpenHelperTest.TestOpenHelper helper2 = new SQLiteOpenHelperTest.TestOpenHelper(ApplicationProvider.getApplicationContext(), "path2", null, 1);
        SQLiteDatabase db2 = getWritableDatabase();
        setupTable(db2, TABLE_NAME2);
        insertData(db2, TABLE_NAME2, new int[]{ 4, 5, 6 });
        verifyData(db1, TABLE_NAME1, 2);
        verifyData(db2, TABLE_NAME2, 3);
    }

    @Test
    public void testCloseOneDbKeepsDataForOther() throws Exception {
        final String TABLE_NAME1 = "fart";
        final String TABLE_NAME2 = "fart2";
        SQLiteOpenHelperTest.TestOpenHelper helper2 = new SQLiteOpenHelperTest.TestOpenHelper(ApplicationProvider.getApplicationContext(), "path2", null, 1);
        SQLiteDatabase db1 = getWritableDatabase();
        SQLiteDatabase db2 = getWritableDatabase();
        setupTable(db1, TABLE_NAME1);
        setupTable(db2, TABLE_NAME2);
        insertData(db1, TABLE_NAME1, new int[]{ 1, 2 });
        insertData(db2, TABLE_NAME2, new int[]{ 4, 5, 6 });
        verifyData(db1, TABLE_NAME1, 2);
        verifyData(db2, TABLE_NAME2, 3);
        db1.close();
        verifyData(db2, TABLE_NAME2, 3);
        db1 = helper.getWritableDatabase();
        verifyData(db1, TABLE_NAME1, 2);
        verifyData(db2, TABLE_NAME2, 3);
    }

    @Test
    public void testCreateAndDropTable() throws Exception {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("CREATE TABLE foo(id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT);");
        database.execSQL("DROP TABLE IF EXISTS foo;");
    }

    @Test
    public void testCloseThenOpen() throws Exception {
        final String TABLE_NAME1 = "fart";
        SQLiteDatabase db1 = getWritableDatabase();
        setupTable(db1, TABLE_NAME1);
        insertData(db1, TABLE_NAME1, new int[]{ 1, 2 });
        verifyData(db1, TABLE_NAME1, 2);
        db1.close();
        db1 = helper.getWritableDatabase();
        assertThat(db1.isOpen()).isTrue();
    }

    private static class TestOpenHelper extends SQLiteOpenHelper {
        public boolean onCreateCalled;

        public boolean onUpgradeCalled;

        public boolean onOpenCalled;

        public TestOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            onCreateCalled = true;
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            onUpgradeCalled = true;
        }

        @Override
        public void onOpen(SQLiteDatabase database) {
            onOpenCalled = true;
        }

        @Override
        public synchronized void close() {
            onCreateCalled = false;
            onUpgradeCalled = false;
            onOpenCalled = false;
            super.close();
        }
    }
}

