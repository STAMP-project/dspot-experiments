package com.evernote.android.job;


import JobRequest.DEFAULT_BACKOFF_MS;
import JobRequest.DEFAULT_BACKOFF_POLICY;
import JobRequest.DEFAULT_NETWORK_TYPE;
import JobRequest.MIN_INTERVAL;
import JobStorage.COLUMN_BACKOFF_MS;
import JobStorage.COLUMN_BACKOFF_POLICY;
import JobStorage.COLUMN_END_MS;
import JobStorage.COLUMN_EXACT;
import JobStorage.COLUMN_EXTRAS;
import JobStorage.COLUMN_ID;
import JobStorage.COLUMN_INTERVAL_MS;
import JobStorage.COLUMN_NETWORK_TYPE;
import JobStorage.COLUMN_NUM_FAILURES;
import JobStorage.COLUMN_REQUIREMENTS_ENFORCED;
import JobStorage.COLUMN_REQUIRES_CHARGING;
import JobStorage.COLUMN_REQUIRES_DEVICE_IDLE;
import JobStorage.COLUMN_SCHEDULED_AT;
import JobStorage.COLUMN_START_MS;
import JobStorage.COLUMN_TAG;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.evernote.android.job.test.JobRobolectricTestRunner;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.robolectric.RuntimeEnvironment;

import static JobRequest.MIN_INTERVAL;


/**
 *
 *
 * @author rwondratschek
 */
@RunWith(JobRobolectricTestRunner.class)
@FixMethodOrder(MethodSorters.JVM)
@SuppressWarnings("unused")
public class DatabaseManualUpgradeTest extends BaseJobManagerTest {
    @Test
    public void testDatabaseUpgrade1to6() {
        Context context = RuntimeEnvironment.application;
        context.deleteDatabase(JobStorage.DATABASE_NAME);
        DatabaseManualUpgradeTest.JobOpenHelper1 openHelper = new DatabaseManualUpgradeTest.JobOpenHelper1(context);
        createDatabase(openHelper, false);
        createJobs(openHelper);
        checkJob();
    }

    @Test
    public void testDatabaseUpgrade2to6() {
        Context context = RuntimeEnvironment.application;
        context.deleteDatabase(JobStorage.DATABASE_NAME);
        DatabaseManualUpgradeTest.JobOpenHelper2 openHelper = new DatabaseManualUpgradeTest.JobOpenHelper2(context);
        createDatabase(openHelper, false);
        createJobs(openHelper);
        checkJob();
    }

    @Test
    public void testDatabaseUpgrade3to6() {
        Context context = RuntimeEnvironment.application;
        context.deleteDatabase(JobStorage.DATABASE_NAME);
        DatabaseManualUpgradeTest.JobOpenHelper3 openHelper = new DatabaseManualUpgradeTest.JobOpenHelper3(context);
        createDatabase(openHelper, false);
        createJobs(openHelper, true);
        checkJob();
    }

    @Test
    public void testDatabaseUpgrade4to6() {
        Context context = RuntimeEnvironment.application;
        context.deleteDatabase(JobStorage.DATABASE_NAME);
        DatabaseManualUpgradeTest.JobOpenHelper4 openHelper = new DatabaseManualUpgradeTest.JobOpenHelper4(context);
        createDatabase(openHelper, false);
        createJobs(openHelper, true);
        checkJob();
    }

    @Test
    public void testDatabaseUpgrade5to6() {
        Context context = RuntimeEnvironment.application;
        context.deleteDatabase(JobStorage.DATABASE_NAME);
        DatabaseManualUpgradeTest.JobOpenHelper5 openHelper = new DatabaseManualUpgradeTest.JobOpenHelper5(context);
        createDatabase(openHelper, false);
        createJobs(openHelper, true);
        checkJob();
    }

    @Test
    public void testDatabaseUpgrade1to2to3to4to5to6() {
        Context context = RuntimeEnvironment.application;
        context.deleteDatabase(JobStorage.DATABASE_NAME);
        DatabaseManualUpgradeTest.JobOpenHelper1 openHelper = new DatabaseManualUpgradeTest.JobOpenHelper1(context);
        createDatabase(openHelper, false);
        createJobs(openHelper);
        createDatabase(new DatabaseManualUpgradeTest.JobOpenHelper2(context), true);
        createDatabase(new DatabaseManualUpgradeTest.JobOpenHelper3(context), true);
        createDatabase(new DatabaseManualUpgradeTest.JobOpenHelper4(context), true);
        createDatabase(new DatabaseManualUpgradeTest.JobOpenHelper5(context), true);
        checkJob();
    }

    private abstract static class UpgradeAbleJobOpenHelper extends SQLiteOpenHelper {
        private boolean mDatabaseCreated;

        private boolean mDatabaseUpgraded;

        UpgradeAbleJobOpenHelper(Context context, int version) {
            super(context, JobStorage.DATABASE_NAME, null, version);
        }

        @Override
        public final void onCreate(SQLiteDatabase db) {
            onCreateInner(db);
            mDatabaseCreated = true;
        }

        protected abstract void onCreateInner(SQLiteDatabase db);

        @Override
        public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            while (oldVersion < newVersion) {
                switch (oldVersion) {
                    case 1 :
                        upgradeFrom1To2(db);
                        oldVersion++;
                        break;
                    case 2 :
                        upgradeFrom2To3(db);
                        oldVersion++;
                        break;
                    case 3 :
                        upgradeFrom3To4(db);
                        oldVersion++;
                        break;
                    case 4 :
                        upgradeFrom4To5(db);
                        oldVersion++;
                        break;
                    case 5 :
                        upgradeFrom5To6(db);
                        oldVersion++;
                        break;
                    default :
                        throw new IllegalStateException("not implemented");
                }
            } 
            mDatabaseCreated = true;
            mDatabaseUpgraded = true;
        }

        protected void upgradeFrom1To2(SQLiteDatabase db) {
            // override me
        }

        protected void upgradeFrom2To3(SQLiteDatabase db) {
            // override me
        }

        protected void upgradeFrom3To4(SQLiteDatabase db) {
            // override me
        }

        protected void upgradeFrom4To5(SQLiteDatabase db) {
            // override me
        }

        protected void upgradeFrom5To6(SQLiteDatabase db) {
            // override me
        }

        protected ContentValues createBaseContentValues(int id) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ID, id);
            contentValues.put(COLUMN_TAG, "Tag");
            contentValues.put(COLUMN_START_MS, (-1L));
            contentValues.put(COLUMN_END_MS, (-1L));
            contentValues.put(COLUMN_BACKOFF_MS, DEFAULT_BACKOFF_MS);
            contentValues.put(COLUMN_BACKOFF_POLICY, DEFAULT_BACKOFF_POLICY.toString());
            contentValues.put(COLUMN_INTERVAL_MS, 0L);
            contentValues.put(COLUMN_REQUIREMENTS_ENFORCED, false);
            contentValues.put(COLUMN_REQUIRES_CHARGING, false);
            contentValues.put(COLUMN_REQUIRES_DEVICE_IDLE, false);
            contentValues.put(COLUMN_EXACT, false);
            contentValues.put(COLUMN_NETWORK_TYPE, DEFAULT_NETWORK_TYPE.toString());
            contentValues.put(COLUMN_EXTRAS, new PersistableBundleCompat().saveToXml());
            contentValues.put("persisted", false);
            contentValues.put(COLUMN_NUM_FAILURES, 0);
            contentValues.put(COLUMN_SCHEDULED_AT, System.currentTimeMillis());
            return contentValues;
        }
    }

    private static class JobOpenHelper1 extends DatabaseManualUpgradeTest.UpgradeAbleJobOpenHelper {
        JobOpenHelper1(Context context) {
            this(context, 1);
        }

        JobOpenHelper1(Context context, int version) {
            super(context, version);
        }

        @Override
        public void onCreateInner(SQLiteDatabase db) {
            db.execSQL((((((((((((((((((((((((((((((((((("create table " + (JobStorage.JOB_TABLE_NAME)) + " (") + (JobStorage.COLUMN_ID)) + " integer primary key, ") + (JobStorage.COLUMN_TAG)) + " text not null, ") + (JobStorage.COLUMN_START_MS)) + " integer, ") + (JobStorage.COLUMN_END_MS)) + " integer, ") + (JobStorage.COLUMN_BACKOFF_MS)) + " integer, ") + (JobStorage.COLUMN_BACKOFF_POLICY)) + " text not null, ") + (JobStorage.COLUMN_INTERVAL_MS)) + " integer, ") + (JobStorage.COLUMN_REQUIREMENTS_ENFORCED)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_CHARGING)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_DEVICE_IDLE)) + " integer, ") + (JobStorage.COLUMN_EXACT)) + " integer, ") + (JobStorage.COLUMN_NETWORK_TYPE)) + " text not null, ") + (JobStorage.COLUMN_EXTRAS)) + " text, ") + "persisted") + " integer, ") + (JobStorage.COLUMN_NUM_FAILURES)) + " integer, ") + (JobStorage.COLUMN_SCHEDULED_AT)) + " integer);"));
        }
    }

    private static class JobOpenHelper2 extends DatabaseManualUpgradeTest.JobOpenHelper1 {
        JobOpenHelper2(Context context) {
            this(context, 2);
        }

        JobOpenHelper2(Context context, int version) {
            super(context, version);
        }

        @Override
        public void onCreateInner(SQLiteDatabase db) {
            db.execSQL((((((((((((((((((((((((((((((((((((("create table " + (JobStorage.JOB_TABLE_NAME)) + " (") + (JobStorage.COLUMN_ID)) + " integer primary key, ") + (JobStorage.COLUMN_TAG)) + " text not null, ") + (JobStorage.COLUMN_START_MS)) + " integer, ") + (JobStorage.COLUMN_END_MS)) + " integer, ") + (JobStorage.COLUMN_BACKOFF_MS)) + " integer, ") + (JobStorage.COLUMN_BACKOFF_POLICY)) + " text not null, ") + (JobStorage.COLUMN_INTERVAL_MS)) + " integer, ") + (JobStorage.COLUMN_REQUIREMENTS_ENFORCED)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_CHARGING)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_DEVICE_IDLE)) + " integer, ") + (JobStorage.COLUMN_EXACT)) + " integer, ") + (JobStorage.COLUMN_NETWORK_TYPE)) + " text not null, ") + (JobStorage.COLUMN_EXTRAS)) + " text, ") + "persisted") + " integer, ") + (JobStorage.COLUMN_NUM_FAILURES)) + " integer, ") + (JobStorage.COLUMN_SCHEDULED_AT)) + " integer, ") + "isTransient") + " integer);"));
        }

        @Override
        protected ContentValues createBaseContentValues(int id) {
            ContentValues contentValues = super.createBaseContentValues(id);
            contentValues.put("isTransient", false);
            return contentValues;
        }

        protected void upgradeFrom1To2(SQLiteDatabase db) {
            db.execSQL((((("alter table " + (JobStorage.JOB_TABLE_NAME)) + " add column ") + "isTransient") + " integer;"));
        }
    }

    private static class JobOpenHelper3 extends DatabaseManualUpgradeTest.JobOpenHelper2 {
        JobOpenHelper3(Context context) {
            this(context, 3);
        }

        JobOpenHelper3(Context context, int version) {
            super(context, version);
        }

        @Override
        public void onCreateInner(SQLiteDatabase db) {
            db.execSQL((((((((((((((((((((((((((((((((((((((((("create table " + (JobStorage.JOB_TABLE_NAME)) + " (") + (JobStorage.COLUMN_ID)) + " integer primary key, ") + (JobStorage.COLUMN_TAG)) + " text not null, ") + (JobStorage.COLUMN_START_MS)) + " integer, ") + (JobStorage.COLUMN_END_MS)) + " integer, ") + (JobStorage.COLUMN_BACKOFF_MS)) + " integer, ") + (JobStorage.COLUMN_BACKOFF_POLICY)) + " text not null, ") + (JobStorage.COLUMN_INTERVAL_MS)) + " integer, ") + (JobStorage.COLUMN_REQUIREMENTS_ENFORCED)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_CHARGING)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_DEVICE_IDLE)) + " integer, ") + (JobStorage.COLUMN_EXACT)) + " integer, ") + (JobStorage.COLUMN_NETWORK_TYPE)) + " text not null, ") + (JobStorage.COLUMN_EXTRAS)) + " text, ") + "persisted") + " integer, ") + (JobStorage.COLUMN_NUM_FAILURES)) + " integer, ") + (JobStorage.COLUMN_SCHEDULED_AT)) + " integer, ") + "isTransient") + " integer, ") + (JobStorage.COLUMN_FLEX_MS)) + " integer, ") + (JobStorage.COLUMN_FLEX_SUPPORT)) + " integer);"));
        }

        protected void upgradeFrom2To3(SQLiteDatabase db) {
            db.execSQL((((("alter table " + (JobStorage.JOB_TABLE_NAME)) + " add column ") + (JobStorage.COLUMN_FLEX_MS)) + " integer;"));
            db.execSQL((((("alter table " + (JobStorage.JOB_TABLE_NAME)) + " add column ") + (JobStorage.COLUMN_FLEX_SUPPORT)) + " integer;"));
            // adjust interval to minimum value if necessary
            ContentValues contentValues = new ContentValues();
            contentValues.put(JobStorage.COLUMN_INTERVAL_MS, MIN_INTERVAL);
            db.update(JobStorage.JOB_TABLE_NAME, contentValues, (((((JobStorage.COLUMN_INTERVAL_MS) + ">0 AND ") + (JobStorage.COLUMN_INTERVAL_MS)) + "<") + (MIN_INTERVAL)), new String[0]);
            // copy interval into flex column, that's the default value and the flex support mode is not required
            db.execSQL((((((("update " + (JobStorage.JOB_TABLE_NAME)) + " set ") + (JobStorage.COLUMN_FLEX_MS)) + " = ") + (JobStorage.COLUMN_INTERVAL_MS)) + ";"));
        }
    }

    private static class JobOpenHelper4 extends DatabaseManualUpgradeTest.JobOpenHelper3 {
        JobOpenHelper4(Context context) {
            this(context, 4);
        }

        JobOpenHelper4(Context context, int version) {
            super(context, version);
        }

        @Override
        public void onCreateInner(SQLiteDatabase db) {
            db.execSQL((((((((((((((((((((((((((((((((((((((((((("create table " + (JobStorage.JOB_TABLE_NAME)) + " (") + (JobStorage.COLUMN_ID)) + " integer primary key, ") + (JobStorage.COLUMN_TAG)) + " text not null, ") + (JobStorage.COLUMN_START_MS)) + " integer, ") + (JobStorage.COLUMN_END_MS)) + " integer, ") + (JobStorage.COLUMN_BACKOFF_MS)) + " integer, ") + (JobStorage.COLUMN_BACKOFF_POLICY)) + " text not null, ") + (JobStorage.COLUMN_INTERVAL_MS)) + " integer, ") + (JobStorage.COLUMN_REQUIREMENTS_ENFORCED)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_CHARGING)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_DEVICE_IDLE)) + " integer, ") + (JobStorage.COLUMN_EXACT)) + " integer, ") + (JobStorage.COLUMN_NETWORK_TYPE)) + " text not null, ") + (JobStorage.COLUMN_EXTRAS)) + " text, ") + "persisted") + " integer, ") + (JobStorage.COLUMN_NUM_FAILURES)) + " integer, ") + (JobStorage.COLUMN_SCHEDULED_AT)) + " integer, ") + "isTransient") + " integer, ") + (JobStorage.COLUMN_FLEX_MS)) + " integer, ") + (JobStorage.COLUMN_FLEX_SUPPORT)) + " integer, ") + (JobStorage.COLUMN_LAST_RUN)) + " integer);"));
        }

        protected void upgradeFrom3To4(SQLiteDatabase db) {
            db.execSQL((((("alter table " + (JobStorage.JOB_TABLE_NAME)) + " add column ") + (JobStorage.COLUMN_LAST_RUN)) + " integer;"));
        }
    }

    private static class JobOpenHelper5 extends DatabaseManualUpgradeTest.JobOpenHelper4 {
        JobOpenHelper5(Context context) {
            this(context, 5);
        }

        JobOpenHelper5(Context context, int version) {
            super(context, version);
        }

        @Override
        protected ContentValues createBaseContentValues(int id) {
            ContentValues values = super.createBaseContentValues(id);
            values.remove("isTransient");
            values.remove("persisted");
            return values;
        }

        @Override
        public void onCreateInner(SQLiteDatabase db) {
            db.execSQL((((((((((((((((((((((((((((((((((((((((((("create table " + (JobStorage.JOB_TABLE_NAME)) + " (") + (JobStorage.COLUMN_ID)) + " integer primary key, ") + (JobStorage.COLUMN_TAG)) + " text not null, ") + (JobStorage.COLUMN_START_MS)) + " integer, ") + (JobStorage.COLUMN_END_MS)) + " integer, ") + (JobStorage.COLUMN_BACKOFF_MS)) + " integer, ") + (JobStorage.COLUMN_BACKOFF_POLICY)) + " text not null, ") + (JobStorage.COLUMN_INTERVAL_MS)) + " integer, ") + (JobStorage.COLUMN_REQUIREMENTS_ENFORCED)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_CHARGING)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_DEVICE_IDLE)) + " integer, ") + (JobStorage.COLUMN_EXACT)) + " integer, ") + (JobStorage.COLUMN_NETWORK_TYPE)) + " text not null, ") + (JobStorage.COLUMN_EXTRAS)) + " text, ") + (JobStorage.COLUMN_NUM_FAILURES)) + " integer, ") + (JobStorage.COLUMN_SCHEDULED_AT)) + " integer, ") + (JobStorage.COLUMN_STARTED)) + " integer, ") + (JobStorage.COLUMN_FLEX_MS)) + " integer, ") + (JobStorage.COLUMN_FLEX_SUPPORT)) + " integer, ") + (JobStorage.COLUMN_LAST_RUN)) + " integer, ") + (JobStorage.COLUMN_TRANSIENT)) + " integer);"));
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void upgradeFrom4To5(SQLiteDatabase db) {
            // remove "persisted" column and rename "isTransient" to "started", add "transient" column for O
            try {
                db.beginTransaction();
                String newTable = (JobStorage.JOB_TABLE_NAME) + "_new";
                db.execSQL((((((((((((((((((((((((((((((((((((((((("create table " + newTable) + " (") + (JobStorage.COLUMN_ID)) + " integer primary key, ") + (JobStorage.COLUMN_TAG)) + " text not null, ") + (JobStorage.COLUMN_START_MS)) + " integer, ") + (JobStorage.COLUMN_END_MS)) + " integer, ") + (JobStorage.COLUMN_BACKOFF_MS)) + " integer, ") + (JobStorage.COLUMN_BACKOFF_POLICY)) + " text not null, ") + (JobStorage.COLUMN_INTERVAL_MS)) + " integer, ") + (JobStorage.COLUMN_REQUIREMENTS_ENFORCED)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_CHARGING)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_DEVICE_IDLE)) + " integer, ") + (JobStorage.COLUMN_EXACT)) + " integer, ") + (JobStorage.COLUMN_NETWORK_TYPE)) + " text not null, ") + (JobStorage.COLUMN_EXTRAS)) + " text, ") + (JobStorage.COLUMN_NUM_FAILURES)) + " integer, ") + (JobStorage.COLUMN_SCHEDULED_AT)) + " integer, ") + (JobStorage.COLUMN_STARTED)) + " integer, ") + (JobStorage.COLUMN_FLEX_MS)) + " integer, ") + (JobStorage.COLUMN_FLEX_SUPPORT)) + " integer, ") + (JobStorage.COLUMN_LAST_RUN)) + " integer);"));
                db.execSQL(((((((((((((((((((((((((((((((((((((((((("INSERT INTO " + newTable) + " SELECT ") + (JobStorage.COLUMN_ID)) + ",") + (JobStorage.COLUMN_TAG)) + ",") + (JobStorage.COLUMN_START_MS)) + ",") + (JobStorage.COLUMN_END_MS)) + ",") + (JobStorage.COLUMN_BACKOFF_MS)) + ",") + (JobStorage.COLUMN_BACKOFF_POLICY)) + ",") + (JobStorage.COLUMN_INTERVAL_MS)) + ",") + (JobStorage.COLUMN_REQUIREMENTS_ENFORCED)) + ",") + (JobStorage.COLUMN_REQUIRES_CHARGING)) + ",") + (JobStorage.COLUMN_REQUIRES_DEVICE_IDLE)) + ",") + (JobStorage.COLUMN_EXACT)) + ",") + (JobStorage.COLUMN_NETWORK_TYPE)) + ",") + (JobStorage.COLUMN_EXTRAS)) + ",") + (JobStorage.COLUMN_NUM_FAILURES)) + ",") + (JobStorage.COLUMN_SCHEDULED_AT)) + ",") + "isTransient") + ",") + (JobStorage.COLUMN_FLEX_MS)) + ",") + (JobStorage.COLUMN_FLEX_SUPPORT)) + ",") + (JobStorage.COLUMN_LAST_RUN)) + " FROM ") + (JobStorage.JOB_TABLE_NAME)));
                db.execSQL(("DROP TABLE " + (JobStorage.JOB_TABLE_NAME)));
                db.execSQL(((("ALTER TABLE " + newTable) + " RENAME TO ") + (JobStorage.JOB_TABLE_NAME)));
                db.execSQL((((("alter table " + (JobStorage.JOB_TABLE_NAME)) + " add column ") + (JobStorage.COLUMN_TRANSIENT)) + " integer;"));
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }

    private static final class JobOpenHelper6 extends DatabaseManualUpgradeTest.JobOpenHelper5 {
        JobOpenHelper6(Context context) {
            this(context, 6);
        }

        JobOpenHelper6(Context context, int version) {
            super(context, version);
        }

        @Override
        public void onCreateInner(SQLiteDatabase db) {
            db.execSQL((((((((((((((((((((((((((((((((((((((((((((((("create table " + (JobStorage.JOB_TABLE_NAME)) + " (") + (JobStorage.COLUMN_ID)) + " integer primary key, ") + (JobStorage.COLUMN_TAG)) + " text not null, ") + (JobStorage.COLUMN_START_MS)) + " integer, ") + (JobStorage.COLUMN_END_MS)) + " integer, ") + (JobStorage.COLUMN_BACKOFF_MS)) + " integer, ") + (JobStorage.COLUMN_BACKOFF_POLICY)) + " text not null, ") + (JobStorage.COLUMN_INTERVAL_MS)) + " integer, ") + (JobStorage.COLUMN_REQUIREMENTS_ENFORCED)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_CHARGING)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_DEVICE_IDLE)) + " integer, ") + (JobStorage.COLUMN_EXACT)) + " integer, ") + (JobStorage.COLUMN_NETWORK_TYPE)) + " text not null, ") + (JobStorage.COLUMN_EXTRAS)) + " text, ") + (JobStorage.COLUMN_NUM_FAILURES)) + " integer, ") + (JobStorage.COLUMN_SCHEDULED_AT)) + " integer, ") + (JobStorage.COLUMN_STARTED)) + " integer, ") + (JobStorage.COLUMN_FLEX_MS)) + " integer, ") + (JobStorage.COLUMN_FLEX_SUPPORT)) + " integer, ") + (JobStorage.COLUMN_LAST_RUN)) + " integer, ") + (JobStorage.COLUMN_TRANSIENT)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_BATTERY_NOT_LOW)) + " integer, ") + (JobStorage.COLUMN_REQUIRES_STORAGE_NOT_LOW)) + " integer);"));
        }

        @Override
        protected void upgradeFrom5To6(SQLiteDatabase db) {
            db.execSQL((((("alter table " + (JobStorage.JOB_TABLE_NAME)) + " add column ") + (JobStorage.COLUMN_REQUIRES_BATTERY_NOT_LOW)) + " integer;"));
            db.execSQL((((("alter table " + (JobStorage.JOB_TABLE_NAME)) + " add column ") + (JobStorage.COLUMN_REQUIRES_STORAGE_NOT_LOW)) + " integer;"));
        }
    }
}

