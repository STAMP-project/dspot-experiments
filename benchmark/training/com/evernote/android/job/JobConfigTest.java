package com.evernote.android.job;


import Build.VERSION_CODES;
import JobApi.V_14;
import JobApi.V_19;
import JobApi.V_21;
import JobApi.V_24;
import JobApi.V_26;
import JobRequest.MIN_FLEX;
import JobRequest.MIN_INTERVAL;
import android.database.sqlite.SQLiteDatabase;
import com.evernote.android.job.test.DummyJobs;
import com.evernote.android.job.test.JobRobolectricTestRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;

import static JobApi.GCM;


/**
 *
 *
 * @author rwondratschek
 */
@RunWith(JobRobolectricTestRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class JobConfigTest extends BaseJobManagerTest {
    @Test
    @Config(sdk = VERSION_CODES.LOLLIPOP)
    public void verifyReset() {
        assertThat(JobConfig.isApiEnabled(V_19)).isTrue();// default

        JobConfig.setApiEnabled(V_19, false);
        assertThat(JobConfig.isApiEnabled(V_19)).isFalse();// did change

        assertThat(JobConfig.isAllowSmallerIntervalsForMarshmallow()).isFalse();// default

        JobConfig.setAllowSmallerIntervalsForMarshmallow(true);
        assertThat(JobConfig.isAllowSmallerIntervalsForMarshmallow()).isTrue();// did change

        JobConfig.reset();
        assertThat(JobConfig.isApiEnabled(V_19)).isTrue();// default

        assertThat(JobConfig.isAllowSmallerIntervalsForMarshmallow()).isFalse();// default

    }

    @Test
    @Config(sdk = VERSION_CODES.LOLLIPOP)
    public void verifyMinIntervalChanged() {
        assertThat(JobRequest.getMinInterval()).isEqualTo(MIN_INTERVAL);
        assertThat(JobRequest.getMinFlex()).isEqualTo(MIN_FLEX);
        JobConfig.setAllowSmallerIntervalsForMarshmallow(true);
        assertThat(JobRequest.getMinInterval()).isLessThan(MIN_INTERVAL);
        assertThat(JobRequest.getMinFlex()).isLessThan(MIN_FLEX);
    }

    @Test(expected = IllegalStateException.class)
    @Config(sdk = VERSION_CODES.N)
    public void verifyMinIntervalCantBeChangedAfterN() {
        JobConfig.setAllowSmallerIntervalsForMarshmallow(true);
    }

    @Test
    @Config(sdk = VERSION_CODES.O)
    public void verifyApi26Supported() {
        assertThat(JobApi.getDefault(context())).isEqualTo(V_26);
    }

    @Test
    @Config(sdk = VERSION_CODES.N)
    public void verifyApi24Supported() {
        assertThat(JobApi.getDefault(context())).isEqualTo(V_24);
    }

    @Test
    @Config(sdk = VERSION_CODES.LOLLIPOP)
    public void verifyApi21Supported() {
        assertThat(JobApi.getDefault(context())).isEqualTo(V_21);
    }

    @Test
    @Config(sdk = VERSION_CODES.KITKAT)
    public void verifyApi19Supported() {
        assertThat(JobApi.getDefault(context())).isEqualTo(V_19);
    }

    @Test
    @Config(sdk = VERSION_CODES.O)
    public void verifyApiDisabled() {
        assertThat(JobApi.getDefault(context())).isEqualTo(V_26);
        JobConfig.setApiEnabled(V_26, false);
        assertThat(JobApi.getDefault(context())).isEqualTo(V_24);
        JobConfig.setApiEnabled(V_24, false);
        assertThat(JobApi.getDefault(context())).isEqualTo(V_21);
        JobConfig.setApiEnabled(V_21, false);
        assertThat(JobApi.getDefault(context())).isEqualTo(V_19);
        JobConfig.setApiEnabled(V_19, false);
        assertThat(JobApi.getDefault(context())).isEqualTo(V_14);
    }

    @Test
    public void verifyForceApiDisabledOtherApis() {
        JobApi forcedApi = GCM;
        for (JobApi api : JobApi.values()) {
            assertThat(JobConfig.isApiEnabled(api)).isTrue();
        }
        JobConfig.forceApi(forcedApi);
        for (JobApi api : JobApi.values()) {
            assertThat(JobConfig.isApiEnabled(api)).isEqualTo((api == forcedApi));
        }
    }

    @Test
    public void verifyJobIdOffset() {
        assertThat(JobConfig.getJobIdOffset()).isEqualTo(0);
        assertThat(manager().getJobStorage().getMaxJobId()).isEqualTo(0);
        int jobId = DummyJobs.createBuilder(DummyJobs.SuccessJob.class).setExecutionWindow(200000L, 400000L).build().schedule();
        assertThat(jobId).isEqualTo(1);
        JobConfig.setJobIdOffset(100);
        assertThat(JobConfig.getJobIdOffset()).isEqualTo(100);
        jobId = DummyJobs.createBuilder(DummyJobs.SuccessJob.class).setExecutionWindow(200000L, 400000L).build().schedule();
        assertThat(jobId).isEqualTo(101);
        JobConfig.setJobIdOffset(0);
        assertThat(JobConfig.getJobIdOffset()).isEqualTo(0);
        jobId = DummyJobs.createBuilder(DummyJobs.SuccessJob.class).setExecutionWindow(200000L, 400000L).build().schedule();
        assertThat(jobId).isEqualTo(102);
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyJobIdOffsetUpperBound() {
        JobConfig.setJobIdOffset(((2147480000 - 500) + 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyJobIdOffsetLowerBound() {
        JobConfig.setJobIdOffset((-1));
    }

    @Test
    public void verifyJobIdOffsetBounds() {
        JobConfig.setJobIdOffset(0);
        JobConfig.setJobIdOffset((2147480000 - 500));
    }

    @Test
    public void verifyCloseDatabase() {
        assertThat(JobConfig.isCloseDatabase()).isFalse();// default

        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        JobStorage storage = manager().getJobStorage();
        storage.injectDatabase(database);
        storage.get(1);
        Mockito.verify(database, Mockito.times(1)).query(ArgumentMatchers.anyString(), ArgumentMatchers.nullable(String[].class), ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class));
        Mockito.verify(database, Mockito.times(0)).close();
        JobConfig.setCloseDatabase(true);
        storage.get(1);
        Mockito.verify(database, Mockito.times(2)).query(ArgumentMatchers.anyString(), ArgumentMatchers.nullable(String[].class), ArgumentMatchers.anyString(), ArgumentMatchers.any(String[].class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.nullable(String.class));
        Mockito.verify(database, Mockito.times(1)).close();
    }
}

