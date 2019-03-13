package com.birbit.android.jobqueue.test.jobqueue;


import JobManager.NOT_DELAYED_JOB_DELAY;
import NetworkUtil.DISCONNECTED;
import NetworkUtil.METERED;
import NetworkUtil.UNMETERED;
import TagConstraint.ANY;
import com.birbit.android.jobqueue.Constraint;
import com.birbit.android.jobqueue.JobHolder;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.JobQueue;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.TagConstraint;
import com.birbit.android.jobqueue.TestConstraint;
import com.birbit.android.jobqueue.test.TestBase;
import com.birbit.android.jobqueue.test.timer.MockTimer;
import com.birbit.android.jobqueue.test.util.JobQueueFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.fest.reflect.field.Invoker;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public abstract class JobQueueTestBase extends TestBase {
    JobQueueFactory currentFactory;

    MockTimer mockTimer = new MockTimer();

    public JobQueueTestBase(JobQueueFactory factory) {
        currentFactory = factory;
    }

    @Test
    public void testBasicAddRemoveCount() throws Exception {
        final int ADD_COUNT = 6;
        JobQueue jobQueue = createNewJobQueue();
        MatcherAssert.assertThat(((int) (jobQueue.count())), CoreMatchers.equalTo(0));
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setExcludeRunning(true);
        MatcherAssert.assertThat(jobQueue.nextJobAndIncRunCount(constraint), CoreMatchers.nullValue());
        for (int i = 0; i < ADD_COUNT; i++) {
            JobHolder holder = createNewJobHolder();
            jobQueue.insert(holder);
            MatcherAssert.assertThat(((int) (jobQueue.count())), CoreMatchers.equalTo((i + 1)));
            MatcherAssert.assertThat(holder.getInsertionOrder(), CoreMatchers.equalTo((i + 1L)));
            jobQueue.insertOrReplace(holder);
            MatcherAssert.assertThat(((int) (jobQueue.count())), CoreMatchers.equalTo((i + 1)));
        }
        JobHolder firstHolder = jobQueue.nextJobAndIncRunCount(constraint);
        MatcherAssert.assertThat(firstHolder.getRunCount(), CoreMatchers.equalTo(1));
        // size should be down 1
        MatcherAssert.assertThat(((int) (jobQueue.count())), CoreMatchers.equalTo((ADD_COUNT - 1)));
        // should return another job
        JobHolder secondHolder = jobQueue.nextJobAndIncRunCount(constraint);
        MatcherAssert.assertThat(secondHolder.getRunCount(), CoreMatchers.equalTo(1));
        // size should be down 2
        MatcherAssert.assertThat(((int) (jobQueue.count())), CoreMatchers.equalTo((ADD_COUNT - 2)));
        // second holder and first holder should have different ids
        MatcherAssert.assertThat(firstHolder.getId(), CoreMatchers.not(secondHolder.getId()));
        jobQueue.remove(secondHolder);
        MatcherAssert.assertThat(((int) (jobQueue.count())), CoreMatchers.equalTo((ADD_COUNT - 2)));
        jobQueue.remove(secondHolder);
        // non existed job removed, count should be the same
        MatcherAssert.assertThat(((int) (jobQueue.count())), CoreMatchers.equalTo((ADD_COUNT - 2)));
        jobQueue.remove(firstHolder);
        MatcherAssert.assertThat(((int) (jobQueue.count())), CoreMatchers.equalTo((ADD_COUNT - 2)));
    }

    @Test
    public void testPriority() throws Exception {
        int JOB_LIMIT = 20;
        JobQueue jobQueue = createNewJobQueue();
        // create and add JOB_LIMIT jobs with random priority
        for (int i = 0; i < JOB_LIMIT; i++) {
            jobQueue.insert(createNewJobHolder(new Params(((int) ((Math.random()) * 10)))));
        }
        // ensure we get jobs in correct priority order
        int minPriority = Integer.MAX_VALUE;
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setExcludeRunning(true);
        for (int i = 0; i < JOB_LIMIT; i++) {
            JobHolder holder = jobQueue.nextJobAndIncRunCount(constraint);
            MatcherAssert.assertThat(((holder.getPriority()) <= minPriority), CoreMatchers.is(true));
        }
        MatcherAssert.assertThat(jobQueue.nextJobAndIncRunCount(constraint), CoreMatchers.nullValue());
    }

    @Test
    public void testDelayUntilWithPriority() throws Exception {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder lowPriorityHolder = createNewJobHolderWithDelayUntil(new Params(5), 1);
        JobHolder highPriorityHolder = createNewJobHolderWithDelayUntil(new Params(10), 2);
        jobQueue.insert(lowPriorityHolder);
        jobQueue.insert(highPriorityHolder);
        MatcherAssert.assertThat("when asked, if lower priority job has less delay until, we should return it", jobQueue.getNextJobDelayUntilNs(new TestConstraint(mockTimer)), CoreMatchers.equalTo(lowPriorityHolder.getDelayUntilNs()));
    }

    @Test
    public void testNoDeadline() throws Exception {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder requireNetwork = createNewJobHolder(new Params(0).requireNetwork());
        jobQueue.insert(requireNetwork);
        TestConstraint testConstraint = new TestConstraint(mockTimer);
        testConstraint.setMaxNetworkType(DISCONNECTED);
        MatcherAssert.assertThat("when a job w/o a deadline is given, it should not be returned if not ready", jobQueue.nextJobAndIncRunCount(testConstraint), CoreMatchers.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat("when a job w/o a deadline is given, it should not be returned in next ready", jobQueue.getNextJobDelayUntilNs(testConstraint), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testDeadlineWithRun() throws Exception {
        testDeadline(false);
    }

    @Test
    public void testDeadlineWithCancel() throws Exception {
        testDeadline(true);
    }

    @Test
    public void testDeadlineDoesNotAffectTags() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder jobHolder = createNewJobHolder(new Params(0).overrideDeadlineToRunInMs(10));
        jobQueue.insert(jobHolder);
        mockTimer.incrementMs(100);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setTags(new String[]{ "a" });
        constraint.setTagConstraint(ANY);
        MatcherAssert.assertThat(jobQueue.findJobs(constraint), CoreMatchers.is(Collections.EMPTY_SET));
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testDeadlineDoesNotAffectIdQuery() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder jobHolder = createNewJobHolder(new Params(0).overrideDeadlineToRunInMs(10));
        jobQueue.insert(jobHolder);
        mockTimer.incrementMs(100);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setExcludeJobIds(Collections.singletonList(jobHolder.getId()));
        MatcherAssert.assertThat(jobQueue.findJobs(constraint), CoreMatchers.is(Collections.EMPTY_SET));
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testDeadlineDoesNotAffectExcludeGroupQuery() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder jobHolder = createNewJobHolder(new Params(0).groupBy("g1").overrideDeadlineToRunInMs(10));
        jobQueue.insert(jobHolder);
        mockTimer.incrementMs(100);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setExcludeGroups(Arrays.asList("g1"));
        MatcherAssert.assertThat(jobQueue.findJobs(constraint), CoreMatchers.is(Collections.EMPTY_SET));
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testDeadlineDoesNotAffectExcludeRunning() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder jobHolder = createNewJobHolder(new Params(0).overrideDeadlineToRunInMs(10));
        jobQueue.insert(jobHolder);
        TestConstraint testConstraint = new TestConstraint(mockTimer);
        MatcherAssert.assertThat(jobQueue.nextJobAndIncRunCount(testConstraint).getId(), CoreMatchers.is(jobHolder.getId()));
        mockTimer.incrementMs(100);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setExcludeRunning(true);
        MatcherAssert.assertThat(jobQueue.findJobs(constraint), CoreMatchers.is(Collections.EMPTY_SET));
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testGroupId() throws Exception {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder jobHolder1 = createNewJobHolder(new Params(0).groupBy("group1"));
        JobHolder jobHolder2 = createNewJobHolder(new Params(0).groupBy("group1"));
        JobHolder jobHolder3 = createNewJobHolder(new Params(0).groupBy("group2"));
        JobHolder jobHolder4 = createNewJobHolder(new Params(0).groupBy("group2"));
        JobHolder jobHolder5 = createNewJobHolder(new Params(0).groupBy("group1"));
        jobQueue.insert(jobHolder1);
        jobQueue.insert(jobHolder2);
        jobQueue.insert(jobHolder3);
        jobQueue.insert(jobHolder4);
        jobQueue.insert(jobHolder5);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setExcludeRunning(true);
        constraint.setExcludeGroups(Arrays.asList(new String[]{ "group2" }));
        JobHolder received = jobQueue.nextJobAndIncRunCount(constraint);
        MatcherAssert.assertThat("first jobs should be from group group1 if group2 is excluded", received.getJob().getRunGroupId(), CoreMatchers.equalTo("group1"));
        MatcherAssert.assertThat("correct job should be returned if groupId is provided", received.getId(), CoreMatchers.equalTo(jobHolder1.getId()));
        constraint.setExcludeGroups(Arrays.asList(new String[]{ "group1", "group2" }));
        MatcherAssert.assertThat("no jobs should be returned if all groups are excluded", jobQueue.nextJobAndIncRunCount(constraint), CoreMatchers.is(CoreMatchers.nullValue()));
        JobHolder jobHolder6 = createNewJobHolder(new Params(0));
        jobQueue.insert(jobHolder6);
        JobHolder tmpReceived = jobQueue.nextJobAndIncRunCount(constraint);
        MatcherAssert.assertThat("both groups are disabled, null group job should be returned", tmpReceived.getId(), CoreMatchers.is(jobHolder6.getId()));
        constraint.setExcludeGroups(Arrays.asList(new String[]{ "group1" }));
        MatcherAssert.assertThat("if group1 is excluded, next job should be from group2", jobQueue.nextJobAndIncRunCount(constraint).getJob().getRunGroupId(), CoreMatchers.equalTo("group2"));
        // to test re-run case, add the job back in
        MatcherAssert.assertThat(jobQueue.insertOrReplace(received), CoreMatchers.is(true));
        // ask for it again, should return the same holder because it is grouped
        constraint.clear();
        constraint.setExcludeRunning(true);
        JobHolder received2 = jobQueue.nextJobAndIncRunCount(constraint);
        MatcherAssert.assertThat("for grouped jobs, re-fetching job should work fine", received2.getId(), CoreMatchers.equalTo(received.getId()));
        constraint.setExcludeGroups(Arrays.asList(new String[]{ "group1" }));
        JobHolder received3 = jobQueue.nextJobAndIncRunCount(constraint);
        MatcherAssert.assertThat("if a group is excluded, next available from another group should be returned", received3.getId(), CoreMatchers.equalTo(jobHolder4.getId()));
        // add two more non-grouped jobs
        JobHolder jobHolder7 = createNewJobHolder(new Params(0));
        jobQueue.insert(jobHolder7);
        JobHolder jobHolder8 = createNewJobHolder(new Params(0));
        jobQueue.insert(jobHolder8);
        constraint.setExcludeGroups(Arrays.asList(new String[]{ "group1", "group2" }));
        JobHolder holder4 = jobQueue.nextJobAndIncRunCount(constraint);
        MatcherAssert.assertThat("if all grouped jobs are excluded, next non-grouped job should be returned", holder4.getId(), CoreMatchers.equalTo(jobHolder7.getId()));
        jobQueue.insertOrReplace(holder4);
        // for non-grouped jobs, run counts should be respected
        MatcherAssert.assertThat("if all grouped jobs are excluded, re-inserted highest priority job should still be returned", jobQueue.nextJobAndIncRunCount(constraint).getId(), CoreMatchers.equalTo(jobHolder7.getId()));
    }

    @Test
    public void testDueDelayUntilWithPriority() throws Exception {
        JobQueue jobQueue = createNewJobQueue();
        mockTimer.setNow(2000);
        long now = mockTimer.nanoTime();
        JobHolder lowPriorityHolder = createNewJobHolderWithDelayUntil(new Params(5), (now - (1000 * (JobManager.NS_PER_MS))));
        JobHolder highPriorityHolder = createNewJobHolderWithDelayUntil(new Params(10), (now - (10000 * (JobManager.NS_PER_MS))));
        jobQueue.insert(lowPriorityHolder);
        jobQueue.insert(highPriorityHolder);
        long soonJobDelay = 2000;
        JobHolder highestPriorityDelayedJob = createNewJobHolderWithDelayUntil(new Params(12), (now + (soonJobDelay * (JobManager.NS_PER_MS))));
        jobQueue.insert(highestPriorityDelayedJob);
        Constraint constraint = new Constraint();
        constraint.setNowInNs(mockTimer.nanoTime());
        MatcherAssert.assertThat(("when asked, if job's due has passed, highest priority jobs's delay until should be " + "returned"), jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.equalTo(highPriorityHolder.getDelayUntilNs()));
        // make sure soon job is valid now
        mockTimer.incrementMs((soonJobDelay + 1));
        MatcherAssert.assertThat("when a job's time come, it should be returned", jobQueue.nextJobAndIncRunCount(constraint).getId(), CoreMatchers.equalTo(highestPriorityDelayedJob.getId()));
    }

    @Test
    public void testDelayUntil() throws Exception {
        JobQueue jobQueue = createNewJobQueue();
        long now = mockTimer.nanoTime();
        JobHolder networkJobHolder = createNewJobHolderWithDelayUntil(new Params(0).requireNetwork(), (now + 2));
        JobHolder noNetworkJobHolder = createNewJobHolderWithDelayUntil(new Params(0), (now + 5));
        jobQueue.insert(networkJobHolder);
        jobQueue.insert(noNetworkJobHolder);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(DISCONNECTED);
        MatcherAssert.assertThat("if there is no network, delay until should be provided for no network job", jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.equalTo(noNetworkJobHolder.getDelayUntilNs()));
        constraint.clear();
        constraint.setMaxNetworkType(UNMETERED);
        MatcherAssert.assertThat(("if there is network, delay until should be provided for network job because it is " + "sooner"), jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.equalTo(networkJobHolder.getDelayUntilNs()));
        JobHolder noNetworkJobHolder2 = createNewJobHolderWithDelayUntil(new Params(0), (now + 1));
        jobQueue.insert(noNetworkJobHolder2);
        MatcherAssert.assertThat("if there is network, any job's delay until should be returned", jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.equalTo(noNetworkJobHolder2.getDelayUntilNs()));
    }

    @Test
    public void testDelayUntilWithExcludeGroups() throws Exception {
        JobQueue jobQueue = createNewJobQueue();
        long now = mockTimer.nanoTime();
        JobHolder networkJobHolder = createNewJobHolderWithDelayUntil(new Params(0).requireNetwork().groupBy("group1"), (now + (200000 * (JobManager.NS_PER_MS))));
        JobHolder noNetworkJobHolder = createNewJobHolderWithDelayUntil(new Params(0).groupBy("group2"), (now + (500000 * (JobManager.NS_PER_MS))));
        jobQueue.insert(networkJobHolder);
        jobQueue.insert(noNetworkJobHolder);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(DISCONNECTED);
        constraint.setExcludeRunning(true);
        MatcherAssert.assertThat("if there is no network, delay until should be provided for no network job", jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.equalTo(noNetworkJobHolder.getDelayUntilNs()));
        MatcherAssert.assertThat("if there is no network, delay until should be provided for no network job", jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.equalTo(noNetworkJobHolder.getDelayUntilNs()));
        constraint.setExcludeGroups(Arrays.asList("group2"));
        constraint.setMaxNetworkType(DISCONNECTED);
        MatcherAssert.assertThat("if there is no network, but the group is disabled, delay until should be null", jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.nullValue());
        constraint.setMaxNetworkType(METERED);
        constraint.setExcludeGroups(Arrays.asList("group1", "group2"));
        MatcherAssert.assertThat("if there is network, but both groups are disabled, delay until should be null", jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.nullValue());
        constraint.setMaxNetworkType(METERED);
        constraint.setExcludeGroups(Arrays.asList("group1"));
        MatcherAssert.assertThat("if there is network, but group1 is disabled, delay should come from group2", jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.equalTo(noNetworkJobHolder.getDelayUntilNs()));
        constraint.setExcludeGroups(Arrays.asList("group2"));
        MatcherAssert.assertThat("if there is network, but group2 is disabled, delay should come from group1", jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.equalTo(networkJobHolder.getDelayUntilNs()));
        JobHolder noNetworkJobHolder2 = createNewJobHolderWithDelayUntil(new Params(0), (now + (100000 * (JobManager.NS_PER_MS))));
        constraint.setExcludeGroups(Arrays.asList("group1", "group2"));
        constraint.setMaxNetworkType(METERED);
        jobQueue.insert(noNetworkJobHolder2);
        MatcherAssert.assertThat(("if there is a 3rd job and other gorups are disabled. 3rd job's delay should be " + "returned"), jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.equalTo(noNetworkJobHolder2.getDelayUntilNs()));
    }

    @Test
    public void testTruncate() throws Exception {
        JobQueue jobQueue = createNewJobQueue();
        final int LIMIT = 20;
        for (int i = 0; i < LIMIT; i++) {
            jobQueue.insert(createNewJobHolder());
        }
        MatcherAssert.assertThat("queue should have all jobs", jobQueue.count(), CoreMatchers.equalTo(LIMIT));
        jobQueue.clear();
        MatcherAssert.assertThat("after clear, queue should be empty", jobQueue.count(), CoreMatchers.equalTo(0));
        for (int i = 0; i < LIMIT; i++) {
            jobQueue.insert(createNewJobHolder());
        }
        MatcherAssert.assertThat("if we add jobs again, count should match", jobQueue.count(), CoreMatchers.equalTo(LIMIT));
    }

    @Test
    public void testPriorityWithDelayedJobs() throws Exception {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder delayedPriority_5 = createNewJobHolder(new Params(5));
        Invoker<Long> delayUntilField = getDelayUntilNsField(delayedPriority_5);
        delayUntilField.set(((mockTimer.nanoTime()) - 1000));
        JobHolder delayedPriority_2 = createNewJobHolder(new Params(2));
        delayUntilField = getDelayUntilNsField(delayedPriority_2);
        delayUntilField.set(((mockTimer.nanoTime()) - 500));
        JobHolder nonDelayedPriority_6 = createNewJobHolder(new Params(6));
        JobHolder nonDelayedPriority_3 = createNewJobHolder(new Params(3));
        JobHolder nonDelayedPriority_2 = createNewJobHolder(new Params(2));
        jobQueue.insert(delayedPriority_5);
        jobQueue.insert(delayedPriority_2);
        jobQueue.insert(nonDelayedPriority_6);
        jobQueue.insert(nonDelayedPriority_2);
        jobQueue.insert(nonDelayedPriority_3);
        TestConstraint constraint = new TestConstraint(mockTimer);
        int lastPriority = Integer.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            JobHolder next = jobQueue.nextJobAndIncRunCount(constraint);
            MatcherAssert.assertThat("next job should not be null", next, CoreMatchers.notNullValue());
            MatcherAssert.assertThat(("next job's priority should be lower then previous for job " + i), ((next.getPriority()) <= lastPriority), CoreMatchers.is(true));
            lastPriority = next.getPriority();
        }
    }

    @Test
    public void testSessionId() throws Exception {
        long sessionId = ((long) ((Math.random()) * 100000));
        JobQueue jobQueue = createNewJobQueueWithSessionId(sessionId);
        JobHolder jobHolder = createNewJobHolder();
        jobQueue.insert(jobHolder);
        jobHolder = jobQueue.nextJobAndIncRunCount(new TestConstraint(mockTimer));
        MatcherAssert.assertThat("session id should be attached to next job", jobHolder.getRunningSessionId(), CoreMatchers.equalTo(sessionId));
    }

    @Test
    public void testPriorityWithReAdd() throws Exception {
        int JOB_LIMIT = 20;
        JobQueue jobQueue = createNewJobQueue();
        // create and add JOB_LIMIT jobs with random priority
        for (int i = 0; i < JOB_LIMIT; i++) {
            jobQueue.insert(createNewJobHolder(new Params(((int) ((Math.random()) * 10)))));
        }
        // ensure we get jobs in correct priority order
        int minPriority = Integer.MAX_VALUE;
        for (int i = 0; i < JOB_LIMIT; i++) {
            JobHolder holder = jobQueue.nextJobAndIncRunCount(new TestConstraint(mockTimer));
            MatcherAssert.assertThat(((holder.getPriority()) <= minPriority), CoreMatchers.is(true));
            jobQueue.insertOrReplace(holder);
        }
        MatcherAssert.assertThat(jobQueue.nextJobAndIncRunCount(new TestConstraint(mockTimer)), CoreMatchers.notNullValue());
    }

    @Test
    public void testRemove() throws Exception {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder = createNewJobHolder();
        jobQueue.insert(holder);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setExcludeRunning(true);
        MatcherAssert.assertThat(jobQueue.nextJobAndIncRunCount(constraint).getId(), CoreMatchers.equalTo(holder.getId()));
        MatcherAssert.assertThat(jobQueue.nextJobAndIncRunCount(constraint), CoreMatchers.is(CoreMatchers.nullValue()));
        jobQueue.remove(holder);
        MatcherAssert.assertThat(jobQueue.nextJobAndIncRunCount(constraint), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testNetwork() throws Exception {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder jobHolder = createNewJobHolder(new Params(0));
        jobQueue.insert(jobHolder);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(DISCONNECTED);
        MatcherAssert.assertThat("no network job should be returned even if there is no netowrk", jobQueue.nextJobAndIncRunCount(constraint), CoreMatchers.notNullValue());
        jobQueue.remove(jobHolder);
        jobHolder = createNewJobHolder(new Params(0).requireNetwork());
        MatcherAssert.assertThat("if there isn't any network, job with network requirement should not return", jobQueue.nextJobAndIncRunCount(constraint), CoreMatchers.nullValue());
        constraint.setMaxNetworkType(METERED);
        MatcherAssert.assertThat("if there is network, job with network requirement should be returned", jobQueue.nextJobAndIncRunCount(constraint), CoreMatchers.nullValue());
        jobQueue.remove(jobHolder);
        jobHolder = createNewJobHolder(new Params(1));
        JobHolder jobHolder2 = createNewJobHolder(new Params(5).requireNetwork());
        jobQueue.insert(jobHolder);
        jobQueue.insert(jobHolder2);
        constraint.setMaxNetworkType(DISCONNECTED);
        constraint.setExcludeRunning(true);
        JobHolder retrieved = jobQueue.nextJobAndIncRunCount(constraint);
        MatcherAssert.assertThat("one job should be returned w/o network", retrieved, CoreMatchers.notNullValue());
        if (retrieved != null) {
            MatcherAssert.assertThat("no network job should be returned although it has lower priority", retrieved.getId(), CoreMatchers.equalTo(jobHolder.getId()));
        }
        MatcherAssert.assertThat("no other job should be returned w/o network", jobQueue.nextJobAndIncRunCount(constraint), CoreMatchers.nullValue());
        constraint.setMaxNetworkType(METERED);
        retrieved = jobQueue.nextJobAndIncRunCount(constraint);
        MatcherAssert.assertThat("if network is back, network requiring job should be returned", retrieved, CoreMatchers.notNullValue());
        if (retrieved != null) {
            MatcherAssert.assertThat("when there is network, network job should be returned", retrieved.getId(), CoreMatchers.equalTo(jobHolder2.getId()));
        }
        // add first job back
        jobQueue.insertOrReplace(jobHolder);
        // add second job back
        jobQueue.insertOrReplace(jobHolder2);
        retrieved = jobQueue.nextJobAndIncRunCount(constraint);
        MatcherAssert.assertThat("if network is back, job w/ higher priority should be returned", retrieved, CoreMatchers.notNullValue());
        if (retrieved != null) {
            MatcherAssert.assertThat("if network is back, job w/ higher priority should be returned", retrieved.getId(), CoreMatchers.equalTo(jobHolder2.getId()));
        }
        jobQueue.insertOrReplace(jobHolder2);
        JobHolder highestPriorityJob = createNewJobHolder(new Params(10));
        jobQueue.insert(highestPriorityJob);
        retrieved = jobQueue.nextJobAndIncRunCount(constraint);
        MatcherAssert.assertThat("w/ or w/o network, highest priority should be returned", retrieved, CoreMatchers.notNullValue());
        if (retrieved != null) {
            MatcherAssert.assertThat("w/ or w/o network, highest priority should be returned", retrieved.getId(), CoreMatchers.equalTo(highestPriorityJob.getId()));
        }
        // TODO test delay until
    }

    @Test
    public void testCountReadyJobs() throws Exception {
        JobQueue jobQueue = createNewJobQueue();
        TestConstraint constraint = new TestConstraint(mockTimer);
        MatcherAssert.assertThat("initial count should be 0 for ready jobs", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(0));
        // add some jobs
        jobQueue.insert(createNewJobHolder());
        jobQueue.insert(createNewJobHolder(new Params(0).requireNetwork()));
        long now = mockTimer.nanoTime();
        long delay = 1000;
        constraint.setTimeLimit(now);
        constraint.setMaxNetworkType(DISCONNECTED);
        constraint.setExcludeRunning(true);
        jobQueue.insert(createNewJobHolderWithDelayUntil(new Params(0), (now + (TimeUnit.MILLISECONDS.toNanos(delay)))));
        MatcherAssert.assertThat("ready count should be 1 if there is no network", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(1));
        constraint.setMaxNetworkType(METERED);
        MatcherAssert.assertThat("ready count should be 2 if there is network", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(2));
        mockTimer.incrementMs((delay + 1));
        constraint.setTimeLimit(mockTimer.nanoTime());
        MatcherAssert.assertThat("when needed delay time passes, ready count should be 3", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(3));
        constraint.setMaxNetworkType(DISCONNECTED);
        MatcherAssert.assertThat("when needed delay time passes but no network, ready count should be 2", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(2));
        jobQueue.insert(createNewJobHolder(new Params(5).groupBy("group1")));
        jobQueue.insert(createNewJobHolder(new Params(5).groupBy("group1")));
        constraint.setMaxNetworkType(METERED);
        MatcherAssert.assertThat("when more than 1 job from same group is created, ready jobs should increment only by 1", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(4));
        constraint.setExcludeGroups(Arrays.asList(new String[]{ "group1" }));
        MatcherAssert.assertThat("excluding groups should work", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(3));
        constraint.setExcludeGroups(Arrays.asList(new String[]{ "group3423" }));
        MatcherAssert.assertThat("giving a non-existing group should not fool the count", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(4));
        jobQueue.insert(createNewJobHolder(new Params(3).groupBy("group2")));
        constraint.clear();
        constraint.setTimeLimit(mockTimer.nanoTime());
        constraint.setMaxNetworkType(UNMETERED);
        constraint.setExcludeRunning(true);
        MatcherAssert.assertThat("when a job from another group is added, ready job count should inc", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(5));
        now = mockTimer.nanoTime();
        jobQueue.insert(createNewJobHolderWithDelayUntil(new Params(3).groupBy("group3"), (now + (TimeUnit.MILLISECONDS.toNanos(delay)))));
        MatcherAssert.assertThat("when a delayed job from another group is added, ready count should not change", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(5));
        jobQueue.insert(createNewJobHolder(new Params(3).groupBy("group3")));
        MatcherAssert.assertThat("when another job from delayed group is added, ready job count should inc", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(6));
        mockTimer.incrementMs(delay);
        constraint.setTimeLimit(mockTimer.nanoTime());
        MatcherAssert.assertThat("when delay passes and a job from existing group becomes available, ready job count should not change", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(6));
        constraint.setExcludeGroups(Arrays.asList(new String[]{ "group1", "group3" }));
        MatcherAssert.assertThat("when some groups are excluded, count should be correct", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(4));
        // jobs w/ same group id but with different persistence constraints should not fool the count
        now = mockTimer.nanoTime();
        constraint.setTimeLimit(mockTimer.nanoTime());
        jobQueue.insert(createNewJobHolderWithDelayUntil(new Params(0).persist().groupBy("group10"), (now + 1000)));
        jobQueue.insert(createNewJobHolderWithDelayUntil(new Params(0).groupBy("group10"), (now + 1000)));
        jobQueue.insert(createNewJobHolderWithDelayUntil(new Params(0).persist().groupBy("group10"), (now - 1000)));
        jobQueue.insert(createNewJobHolderWithDelayUntil(new Params(0).groupBy("group10"), (now - 1000)));
        constraint.setExcludeGroups(Arrays.asList(new String[]{ "group1", "group3" }));
        MatcherAssert.assertThat("when many jobs are added w/ different constraints but same group id, ready count should not be fooled", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(5));
        constraint.clear();
        constraint.setExcludeRunning(true);
        constraint.setMaxNetworkType(UNMETERED);
        MatcherAssert.assertThat("when many jobs are added w/ different constraints but same group id, ready count should not be fooled", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(7));
        constraint.setMaxNetworkType(DISCONNECTED);
        constraint.setExcludeGroups(Arrays.asList(new String[]{ "group1", "group3" }));
        MatcherAssert.assertThat("when many jobs are added w/ different constraints but same group id, ready count should not be fooled", jobQueue.countReadyJobs(constraint), CoreMatchers.equalTo(4));
    }

    @Test
    public void testJobFields() throws Exception {
        long sessionId = ((long) ((Math.random()) * 1000));
        JobQueue jobQueue = createNewJobQueueWithSessionId(sessionId);
        int priority = ((int) ((Math.random()) * 1000));
        JobHolder jobHolder = createNewJobHolder(new Params(priority));
        int runCount = ((int) ((Math.random()) * 10));
        jobHolder.setRunCount(runCount);
        jobQueue.insert(jobHolder);
        for (int i = 0; i < 2; i++) {
            JobHolder received = jobQueue.nextJobAndIncRunCount(new TestConstraint(mockTimer));
            MatcherAssert.assertThat("job id should be preserved", received.getId(), CoreMatchers.equalTo(jobHolder.getId()));
            MatcherAssert.assertThat("job priority should be preserved", received.getPriority(), CoreMatchers.equalTo(priority));
            MatcherAssert.assertThat("job session id should be assigned", received.getRunningSessionId(), CoreMatchers.equalTo(sessionId));
            MatcherAssert.assertThat("job run count should be incremented", received.getRunCount(), CoreMatchers.equalTo(((runCount + i) + 1)));
            jobQueue.insertOrReplace(received);
        }
    }

    @Test
    public void testFindJobHolderById() {
        JobQueue jobQueue = createNewJobQueue();
        assertJob(jobQueue, "non existing job", UUID.randomUUID().toString(), null);
        final int LIMIT = 100;
        JobHolder[] holders = new JobHolder[LIMIT];
        String[] ids = new String[LIMIT];
        for (int i = 0; i < LIMIT; i++) {
            holders[i] = createNewJobHolder(new Params(((int) ((Math.random()) * 50))).setPersistent(((Math.random()) < 0.5)).setRequiresNetwork(((Math.random()) < 0.5)));
            ids[i] = holders[i].getId();
            jobQueue.insert(holders[i]);
            assertJob(jobQueue, "job by id should work for inserted job", ids[i], holders[i]);
        }
        final int REMOVE_CNT = LIMIT / 2;
        for (int i = 0; i < REMOVE_CNT; i++) {
            int ind = ((int) ((Math.random()) * LIMIT));
            if ((holders[ind]) == null) {
                continue;
            }
            // remove some randomly, up to half
            jobQueue.remove(holders[ind]);
            holders[ind] = null;
        }
        // re-query all, ensure we can still find non-removed jobs and not find removed jobs
        for (int i = 0; i < LIMIT; i++) {
            if ((holders[i]) != null) {
                assertJob(jobQueue, "if job is still in the Q, it should be returned", ids[i], holders[i]);
                // re add job
                jobQueue.insertOrReplace(holders[i]);
                // re-test after re-add
                assertJob(jobQueue, "after re-insert, if job is still in the Q, it should be returned", ids[i], holders[i]);
            } else {
                assertJob(jobQueue, "removed job should not be returned in id query", ids[i], null);
            }
        }
        jobQueue.clear();
        for (int i = 0; i < LIMIT; i++) {
            assertJob(jobQueue, "after clear, find by id should return null", ids[i], null);
        }
    }

    @Test
    public void testTagsWithMultipleHolders() {
        JobQueue jobQueue = createNewJobQueue();
        final String tag1 = UUID.randomUUID().toString();
        String tag2 = UUID.randomUUID().toString();
        while (tag2.equals(tag1)) {
            tag2 = UUID.randomUUID().toString();
        } 
        String tag3 = UUID.randomUUID().toString();
        while ((tag3.equals(tag1)) || (tag3.equals(tag2))) {
            tag3 = UUID.randomUUID().toString();
        } 
        JobHolder holder1 = createNewJobHolder(new Params(0).addTags(tag1, tag2));
        JobHolder holder2 = createNewJobHolder(new Params(0).addTags(tag1, tag3));
        jobQueue.insert(holder1);
        jobQueue.insert(holder2);
        Set<JobHolder> twoJobs = jobQueue.findJobs(TestConstraint.forTags(mockTimer, TagConstraint.ANY, Collections.<String>emptyList(), tag1));
        Set<String> resultIds = ids(twoJobs);
        MatcherAssert.assertThat("two jobs should be returned", twoJobs.size(), CoreMatchers.is(2));
        MatcherAssert.assertThat("should have job id 1", resultIds, CoreMatchers.hasItems(holder1.getId(), holder2.getId()));
        for (String tag : new String[]{ tag2, tag3 }) {
            Set<JobHolder> oneJob = jobQueue.findJobs(TestConstraint.forTags(mockTimer, TagConstraint.ANY, Collections.<String>emptyList(), tag));
            resultIds = ids(oneJob);
            MatcherAssert.assertThat("one job should be returned", oneJob.size(), CoreMatchers.is(1));
            if (tag.equals(tag2)) {
                MatcherAssert.assertThat("should have job id 1", resultIds, CoreMatchers.hasItems(holder1.getId()));
            } else {
                MatcherAssert.assertThat("should have job id 2", resultIds, CoreMatchers.hasItems(holder2.getId()));
            }
        }
        jobQueue.remove(holder1);
        assertTags("after one of the jobs is removed", jobQueue, holder2);
    }

    @Test
    public void testFindByMultipleTags() {
        JobQueue jobQueue = createNewJobQueue();
        final String tag1 = UUID.randomUUID().toString();
        String tag2 = UUID.randomUUID().toString();
        while (tag2.equals(tag1)) {
            tag2 = UUID.randomUUID().toString();
        } 
        JobHolder holder = createNewJobHolder(new Params(0).addTags(tag1, tag2));
        jobQueue.insert(holder);
        assertTags("job with two tags", jobQueue, holder);
        jobQueue.insertOrReplace(holder);
        assertTags("job with two tags, reinserted", jobQueue, holder);
        jobQueue.remove(holder);
        MatcherAssert.assertThat("when job is removed, it should return none", jobQueue.findJobs(TestConstraint.forTags(mockTimer, TagConstraint.ANY, Collections.<String>emptyList(), tag1)).size(), CoreMatchers.is(0));
        MatcherAssert.assertThat("when job is removed, it should return none", jobQueue.findJobs(TestConstraint.forTags(mockTimer, TagConstraint.ANY, Collections.<String>emptyList(), tag2)).size(), CoreMatchers.is(0));
    }

    @Test
    public void testFindByTags() {
        JobQueue jobQueue = createNewJobQueue();
        MatcherAssert.assertThat("empty queue should return 0", jobQueue.findJobs(TestConstraint.forTags(mockTimer, TagConstraint.ANY, Collections.<String>emptyList(), "abc")).size(), CoreMatchers.is(0));
        jobQueue.insert(createNewJobHolder());
        Set<JobHolder> result = jobQueue.findJobs(TestConstraint.forTags(mockTimer, TagConstraint.ANY, Collections.<String>emptyList(), "blah"));
        MatcherAssert.assertThat("if job does not have a tag, it should return 0", result.size(), CoreMatchers.is(0));
        final String tag1 = UUID.randomUUID().toString();
        JobHolder holder = createNewJobHolder(new Params(0).addTags(tag1));
        jobQueue.insert(holder);
        assertTags("holder with 1 tag", jobQueue, holder);
        jobQueue.insertOrReplace(holder);
        assertTags("holder with 1 tag reinserted", jobQueue, holder);
        jobQueue.remove(holder);
        MatcherAssert.assertThat("when job is removed, it should return none", jobQueue.findJobs(TestConstraint.forTags(mockTimer, TagConstraint.ANY, Collections.<String>emptyList(), tag1)).size(), CoreMatchers.is(0));
        JobHolder holder2 = createNewJobHolder(new Params(0).addTags(tag1));
        jobQueue.insert(holder2);
        MatcherAssert.assertThat("it should return the job", jobQueue.findJobs(TestConstraint.forTags(mockTimer, TagConstraint.ANY, Collections.<String>emptyList(), tag1)).size(), CoreMatchers.is(1));
        jobQueue.onJobCancelled(holder2);
        MatcherAssert.assertThat("when queried w/ exclude cancelled, it should not return the job", jobQueue.findJobs(TestConstraint.forTags(mockTimer, TagConstraint.ANY, Collections.<String>emptyList(), tag1)).size(), CoreMatchers.is(0));
    }

    @Test
    public void testDelayUntilWithNetworkRequirement() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).requireNetwork().overrideDeadlineToRunInMs(1000));
        jobQueue.insert(holder1);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(DISCONNECTED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(1000000000L));
    }

    @Test
    public void testDelayUntilWithNetworkRequirement2() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(3000).delayInMs(2000).requireNetwork());
        jobQueue.insert(holder1);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(DISCONNECTED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(3000000000L));
    }

    @Test
    public void testDelayUntilWithNetworkRequirement3() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(2000).delayInMs(1000).requireNetwork());
        jobQueue.insert(holder1);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(UNMETERED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(1000000000L));
    }

    @Test
    public void testDelayUntilWithNetworkRequirement4() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(3000).delayInMs(2000).requireNetwork());
        jobQueue.insert(holder1);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(UNMETERED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(2000000000L));
    }

    @Test
    public void testDelayUntilWithNetworkRequirement5() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(2000).delayInMs(1000).requireNetwork());
        jobQueue.insert(holder1);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(METERED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(1000000000L));
    }

    @Test
    public void testDelayUntilWithNetworkRequirementAndRegularDelayedJob() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(1000).requireNetwork());
        JobHolder holder2 = createNewJobHolder(new Params(2).delayInMs(500));
        jobQueue.insert(holder1);
        jobQueue.insert(holder2);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(DISCONNECTED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(500000000L));
    }

    @Test
    public void testDelayUntilWithNetworkRequirementAndRegularDelayedJob2() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(1000).requireUnmeteredNetwork());
        JobHolder holder2 = createNewJobHolder(new Params(2).delayInMs(500));
        jobQueue.insert(holder2);
        jobQueue.insert(holder1);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(METERED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(500000000L));
    }

    @Test
    public void testDelayUntilWithNetworkRequirementAndRegularDelayedJob3() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(500).requireNetwork());
        JobHolder holder2 = createNewJobHolder(new Params(2).delayInMs(1000));
        jobQueue.insert(holder1);
        jobQueue.insert(holder2);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(DISCONNECTED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(500000000L));
    }

    @Test
    public void testDelayUntilWithNetworkRequirementAndRegularDelayedJob4() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(500).requireNetwork());
        JobHolder holder2 = createNewJobHolder(new Params(2).delayInMs(1000));
        jobQueue.insert(holder2);
        jobQueue.insert(holder1);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(DISCONNECTED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(500000000L));
    }

    @Test
    public void testDelayUntilWithUnmeteredNetworkRequirement() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(1000).requireUnmeteredNetwork());
        jobQueue.insert(holder1);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(DISCONNECTED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(1000000000L));
        constraint.setMaxNetworkType(METERED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(1000000000L));
    }

    @Test
    public void testDelayUntilWithUnmeteredNetworkRequirement2() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(3000).requireUnmeteredNetwork().delayInMs(2000));
        jobQueue.insert(holder1);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(UNMETERED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(2000000000L));
        constraint.setMaxNetworkType(METERED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(3000000000L));
    }

    @Test
    public void testDelayUntilWithUnmeteredNetworkRequirement3() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(2000).requireUnmeteredNetwork().delayInMs(1000));
        jobQueue.insert(holder1);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(METERED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(2000000000L));
    }

    @Test
    public void testDelayUntilWithUnmeteredNetworkRequirement4() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(3000).requireUnmeteredNetwork().delayInMs(2000));
        jobQueue.insert(holder1);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(UNMETERED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(2000000000L));
    }

    @Test
    public void testDelayUntilWithUnmeteredNetworkRequirement5() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(2000).requireUnmeteredNetwork().delayInMs(1000));
        jobQueue.insert(holder1);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(UNMETERED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(1000000000L));
    }

    @Test
    public void testDelayUntilWithUnmeteredNetworkRequirementAndRegularDelayedJob() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(1000).requireUnmeteredNetwork());
        JobHolder holder2 = createNewJobHolder(new Params(2).delayInMs(500));
        jobQueue.insert(holder1);
        jobQueue.insert(holder2);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(METERED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(500000000L));
    }

    @Test
    public void testDelayUntilWithUnmeteredNetworkRequirementAndRegularDelayedJob2() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(1000).requireUnmeteredNetwork());
        JobHolder holder2 = createNewJobHolder(new Params(2).delayInMs(500));
        jobQueue.insert(holder2);
        jobQueue.insert(holder1);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(METERED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(500000000L));
    }

    @Test
    public void testDelayUntilWithUnmeteredNetworkRequirementAndRegularDelayedJob3() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(500).requireUnmeteredNetwork());
        JobHolder holder2 = createNewJobHolder(new Params(2).delayInMs(1000));
        jobQueue.insert(holder1);
        jobQueue.insert(holder2);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(METERED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(500000000L));
    }

    @Test
    public void testDelayUntilWithUnmeteredNetworkRequirementAndRegularDelayedJob4() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder1 = createNewJobHolder(new Params(2).overrideDeadlineToRunInMs(500).requireUnmeteredNetwork());
        JobHolder holder2 = createNewJobHolder(new Params(2).delayInMs(1000));
        jobQueue.insert(holder2);
        jobQueue.insert(holder1);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(METERED);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(500000000L));
    }

    @Test
    public void testDelayUntilWithRunningJobs() {
        JobQueue jobQueue = createNewJobQueue();
        JobHolder holder = createNewJobHolder();
        jobQueue.insert(holder);
        TestConstraint constraint = new TestConstraint(mockTimer);
        constraint.setMaxNetworkType(METERED);
        constraint.setExcludeRunning(true);
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(NOT_DELAYED_JOB_DELAY));
        JobHolder nextJob = jobQueue.nextJobAndIncRunCount(constraint);
        MatcherAssert.assertThat(nextJob, CoreMatchers.is(CoreMatchers.notNullValue()));
        MatcherAssert.assertThat(nextJob.getId(), CoreMatchers.is(holder.getId()));
        MatcherAssert.assertThat(jobQueue.getNextJobDelayUntilNs(constraint), CoreMatchers.is(CoreMatchers.nullValue()));
    }
}

