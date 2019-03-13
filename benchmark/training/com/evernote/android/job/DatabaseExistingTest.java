package com.evernote.android.job;


import com.evernote.android.job.test.JobRobolectricTestRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


/**
 * Databases should be created with UnitTestDatabaseCreator.java and then be pulled from the device.
 * Best is to use an emulator with API 23.
 *
 * @author rwondratschek
 */
@RunWith(JobRobolectricTestRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class DatabaseExistingTest extends BaseJobManagerTest {
    @Test
    public void upgradeFromV1() {
        testDatabase("evernote_jobs_v1.db");
    }

    @Test
    public void upgradeFromV2() {
        testDatabase("evernote_jobs_v2.db");
    }

    @Test
    public void upgradeFromV3() {
        testDatabase("evernote_jobs_v3.db");
    }

    @Test
    public void upgradeFromV4() {
        testDatabase("evernote_jobs_v4.db");
    }

    @Test
    public void upgradeFromV5() {
        testDatabase("evernote_jobs_v5.db");
    }

    @Test
    public void upgradeFromV6() {
        testDatabase("evernote_jobs_v6.db");
    }
}

