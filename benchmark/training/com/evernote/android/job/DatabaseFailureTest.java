package com.evernote.android.job;


import Job.Result.SUCCESS;
import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.evernote.android.job.test.DummyJobs;
import com.evernote.android.job.test.JobRobolectricTestRunner;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author rwondratschek
 */
@RunWith(JobRobolectricTestRunner.class)
@FixMethodOrder(MethodSorters.JVM)
public class DatabaseFailureTest extends BaseJobManagerTest {
    @Test(expected = SQLException.class)
    public void testInsertFails() {
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(database.insert(ArgumentMatchers.anyString(), ArgumentMatchers.nullable(String.class), ArgumentMatchers.any(ContentValues.class))).thenReturn((-1L));
        Mockito.when(database.insertWithOnConflict(ArgumentMatchers.anyString(), ArgumentMatchers.nullable(String.class), ArgumentMatchers.any(ContentValues.class), ArgumentMatchers.anyInt())).thenThrow(SQLException.class);
        manager().getJobStorage().injectDatabase(database);
        DummyJobs.createOneOff().schedule();
    }

    @Test
    public void testUpdateDoesNotCrash() {
        JobRequest request = DummyJobs.createOneOff();
        int jobId = request.schedule();
        assertThat(request.getScheduledAt()).isGreaterThan(0L);
        assertThat(request.getFailureCount()).isEqualTo(0);
        assertThat(request.getLastRun()).isEqualTo(0);
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        Mockito.when(database.update(ArgumentMatchers.anyString(), ArgumentMatchers.any(ContentValues.class), ArgumentMatchers.nullable(String.class), ArgumentMatchers.any(String[].class))).thenThrow(SQLException.class);
        manager().getJobStorage().injectDatabase(database);
        request.updateStats(true, true);// updates the database value, but fails in this case

        assertThat(request.getFailureCount()).isEqualTo(1);// in memory value was updated, keep that

        assertThat(request.getLastRun()).isGreaterThan(0);
        // kinda hacky, this removes the request from the cache, but doesn't delete it in the database,
        // because we're using the mock at the moment
        manager().getJobStorage().remove(request);
        manager().getJobStorage().injectDatabase(null);// reset

        request = manager().getJobRequest(jobId);
        assertThat(request.getFailureCount()).isEqualTo(0);
        assertThat(request.getLastRun()).isEqualTo(0);
    }

    @Test
    public void testDeleteFailsAfterExecution() throws Exception {
        verifyDeleteOperationFailsAndGetsCleanedUp(new DatabaseFailureTest.DeleteOperation() {
            @Override
            public void delete(JobRequest request) {
                executeJob(request.getJobId(), SUCCESS);
            }
        });
    }

    @Test
    public void testDeleteFailsAfterCancel() throws Exception {
        verifyDeleteOperationFailsAndGetsCleanedUp(new DatabaseFailureTest.DeleteOperation() {
            @Override
            public void delete(JobRequest request) {
                manager().cancel(request.getJobId());
            }
        });
    }

    private interface DeleteOperation {
        void delete(JobRequest request);
    }
}

