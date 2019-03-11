package com.evernote.android.job;


import android.support.annotation.NonNull;
import com.evernote.android.job.util.JobLogger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class JobCreatorHolderTest {
    @Mock
    JobLogger jobLogger;

    @Mock
    JobCreator mockJobCreator;

    private JobCreatorHolder holder;

    @Test
    public void createJobLogsWarningWhenNoCreatorsAreAdded() {
        holder.createJob("DOES_NOT_EXIST");
        // priority
        // tag
        // message
        Mockito.verify(jobLogger).log(ArgumentMatchers.anyInt(), ArgumentMatchers.eq("JobCreatorHolder"), ArgumentMatchers.eq("no JobCreator added"), ArgumentMatchers.<Throwable>isNull());
    }

    @Test
    public void createJobLogsNothingWhenAtLeastOneCreatorIsAdded() {
        holder.addJobCreator(mockJobCreator);
        holder.createJob("DOES_NOT_EXIST");
        Mockito.verifyZeroInteractions(jobLogger);
    }

    @Test
    public void createJobSucceedsWhenCreatorListIsModifiedConcurrently() {
        // This test verifies that modifying the list of job-creators while
        // another thread is in the middle of JobCreatorHolder#createJob(String)
        // is safe, in that createJob will finish unexceptionally.
        // 
        // We'll test thread-safety by beginning iteration through the
        // job-creator list, then adding another creator while the iterator
        // is active.  If we are thread-safe, then iteration will complete
        // without an exception.
        // 
        // To coordinate this, we'll need a custom job creator that blocks
        // until it receives a signal to continue.  A "reader" thread will
        // invoke "createJob", iterating over the list, and blocking. While
        // the reader is blocked, a "mutator" thread will modify the creator
        // list, then signal the reader thread to resume.  Any
        // ConcurrentModificationException will be caught and stored.  When
        // both threads are finished, we can verify that no error was thrown.
        final Lock lock = new ReentrantLock();
        final Condition listModified = lock.newCondition();
        final Condition iterationStarted = lock.newCondition();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        final AtomicBoolean isIteratorActive = new AtomicBoolean(false);
        class BlockingJobCreator implements JobCreator {
            @Override
            public Job create(@NonNull
            String tag) {
                lock.lock();
                try {
                    isIteratorActive.set(true);
                    iterationStarted.signal();
                    listModified.awaitUninterruptibly();
                } finally {
                    lock.unlock();
                }
                return null;
            }
        }
        class Mutator extends Thread {
            @Override
            public void run() {
                waitUntilIterationStarted();
                holder.addJobCreator(mockJobCreator);
                signalListModified();
            }

            private void waitUntilIterationStarted() {
                lock.lock();
                try {
                    if (!(isIteratorActive.get())) {
                        iterationStarted.awaitUninterruptibly();
                    }
                } finally {
                    lock.unlock();
                }
            }

            private void signalListModified() {
                lock.lock();
                try {
                    listModified.signal();
                } finally {
                    lock.unlock();
                }
            }
        }
        class Reader extends Thread {
            @Override
            public void run() {
                try {
                    holder.createJob("SOME_JOB_TAG");
                } catch (Throwable t) {
                    error.set(t);
                }
            }
        }
        holder.addJobCreator(new BlockingJobCreator());
        Mutator mutator = new Mutator();
        Reader reader = new Reader();
        reader.start();
        mutator.start();
        JobCreatorHolderTest.join(mutator);
        JobCreatorHolderTest.join(reader);
        assertThat(error.get()).isNull();
    }
}

