package org.jivesoftware.util;


import AutoCloseableReentrantLock.AutoCloseableLock;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jivesoftware.openfire.XMPPServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(MockitoJUnitRunner.class)
public class AutoCloseableReentrantLockTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoCloseableReentrantLockTest.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private Thread thread1;

    private Thread thread2;

    @Test(timeout = 1000)
    public void willLockAResource() {
        final AtomicBoolean lockAcquired = new AtomicBoolean(false);
        final AtomicInteger callCount = new AtomicInteger(0);
        // Create a thread that acquires a lock
        thread1 = new Thread(() -> {
            try (final AutoCloseableReentrantLock.AutoCloseableLock ignored = new AutoCloseableReentrantLock(AutoCloseableReentrantLockTest.class, "user1").lock()) {
                lockAcquired.set(true);
                callCount.incrementAndGet();
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                AutoCloseableReentrantLockTest.LOGGER.info("Interrupted whilst sleeping", e);
            }
        });
        thread1.start();
        // Create a thread that attempts (and fails) to acquire the same lock
        thread2 = new Thread(() -> {
            try (final AutoCloseableReentrantLock.AutoCloseableLock ignored = new AutoCloseableReentrantLock(AutoCloseableReentrantLockTest.class, "user1").lock()) {
                lockAcquired.set(true);
                callCount.incrementAndGet();
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                AutoCloseableReentrantLockTest.LOGGER.info("Interrupted whilst sleeping", e);
            }
        });
        thread2.start();
        // Wait until we can be sure both threads have started
        await().untilTrue(lockAcquired);
        MatcherAssert.assertThat(callCount.get(), Matchers.is(1));
    }

    @Test(timeout = 1000)
    public void willUseDifferentLocksForDifferentPartsOfTheSameClass() {
        final AtomicBoolean lockAcquired = new AtomicBoolean(false);
        final AtomicInteger callCount = new AtomicInteger(0);
        thread1 = new Thread(() -> {
            try (final AutoCloseableReentrantLock.AutoCloseableLock ignored = new AutoCloseableReentrantLock(AutoCloseableReentrantLockTest.class, "user1").lock()) {
                lockAcquired.set(true);
                callCount.incrementAndGet();
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                AutoCloseableReentrantLockTest.LOGGER.info("Interrupted whilst sleeping", e);
            }
        });
        thread1.start();
        // Create a lock for a different user on the same class - should be acquired
        thread2 = new Thread(() -> {
            try (final AutoCloseableReentrantLock.AutoCloseableLock ignored = new AutoCloseableReentrantLock(AutoCloseableReentrantLockTest.class, "user2").lock()) {
                lockAcquired.set(true);
                callCount.incrementAndGet();
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                AutoCloseableReentrantLockTest.LOGGER.info("Interrupted whilst sleeping", e);
            }
        });
        thread2.start();
        // Wait until we can be sure both threads have started
        await().untilTrue(lockAcquired);
        MatcherAssert.assertThat(callCount.get(), Matchers.is(2));
    }

    @Test(timeout = 1000)
    public void willUseDifferentLocksForDifferentClassesWithTheSamePart() {
        final AtomicBoolean lockAcquired = new AtomicBoolean(false);
        final AtomicInteger callCount = new AtomicInteger(0);
        thread1 = new Thread(() -> {
            try (final AutoCloseableReentrantLock.AutoCloseableLock ignored = new AutoCloseableReentrantLock(AutoCloseableReentrantLockTest.class, "user1").lock()) {
                lockAcquired.set(true);
                callCount.incrementAndGet();
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                AutoCloseableReentrantLockTest.LOGGER.info("Interrupted whilst sleeping", e);
            }
        });
        thread1.start();
        // Create a lock for the same user but for a different class - should be acquired
        thread2 = new Thread(() -> {
            try (final AutoCloseableReentrantLock.AutoCloseableLock ignored = new AutoCloseableReentrantLock(XMPPServer.class, "user1").lock()) {
                lockAcquired.set(true);
                callCount.incrementAndGet();
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                AutoCloseableReentrantLockTest.LOGGER.info("Interrupted whilst sleeping", e);
            }
        });
        thread2.start();
        // Wait until we can be sure both threads have started
        await().untilTrue(lockAcquired);
        MatcherAssert.assertThat(callCount.get(), Matchers.is(2));
    }

    @Test(timeout = 1000)
    public void locksWillBeAutoClosed() {
        final AtomicInteger callCount = new AtomicInteger(0);
        try (final AutoCloseableReentrantLock.AutoCloseableLock ignored = new AutoCloseableReentrantLock(AutoCloseableReentrantLockTest.class, "user1").lock()) {
            callCount.incrementAndGet();
        }
        try (final AutoCloseableReentrantLock.AutoCloseableLock ignored = new AutoCloseableReentrantLock(AutoCloseableReentrantLockTest.class, "user1").lock()) {
            callCount.incrementAndGet();
        }
        MatcherAssert.assertThat(callCount.get(), Matchers.is(2));
    }

    @Test(timeout = 1000)
    public void willNotReuseAReleasedLock() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("already been released");
        final AutoCloseableReentrantLock lock = new AutoCloseableReentrantLock(AutoCloseableReentrantLockTest.class, "user1");
        final AutoCloseableReentrantLock.AutoCloseableLock autoCloseable = lock.lock();
        autoCloseable.close();
        lock.lock();
    }

    @Test(timeout = 1000)
    public void willReuseAnUnreleasedLock() {
        final AutoCloseableReentrantLock lock = new AutoCloseableReentrantLock(AutoCloseableReentrantLockTest.class, "user1");
        final AutoCloseableReentrantLock.AutoCloseableLock autoCloseable1 = lock.lock();
        final AutoCloseableReentrantLock.AutoCloseableLock autoCloseable2 = lock.lock();
        autoCloseable1.close();
        autoCloseable2.close();
    }

    @Test(timeout = 1000)
    public void willIndicateTheLockIsHeldByTheCurrentThread() {
        final AutoCloseableReentrantLock lock = new AutoCloseableReentrantLock(AutoCloseableReentrantLockTest.class, "user1");
        try (final AutoCloseableReentrantLock.AutoCloseableLock ignored = lock.lock()) {
            MatcherAssert.assertThat(lock.isHeldByCurrentThread(), Matchers.is(true));
            MatcherAssert.assertThat(lock.isLocked(), Matchers.is(true));
        }
        MatcherAssert.assertThat(lock.isLocked(), Matchers.is(false));
    }

    @Test(timeout = 1000)
    public void willIndicateTheLockIsHeldByAnotherThread() {
        final AtomicBoolean lockAcquired = new AtomicBoolean(false);
        final AutoCloseableReentrantLock lock = new AutoCloseableReentrantLock(AutoCloseableReentrantLockTest.class, "user1");
        thread1 = new Thread(() -> {
            try (final AutoCloseableReentrantLock.AutoCloseableLock ignored = lock.lock()) {
                lockAcquired.set(true);
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                AutoCloseableReentrantLockTest.LOGGER.info("Interrupted whilst sleeping", e);
            }
        });
        thread1.start();
        await().untilTrue(lockAcquired);
        MatcherAssert.assertThat(lock.isHeldByCurrentThread(), Matchers.is(false));
        MatcherAssert.assertThat(lock.isLocked(), Matchers.is(true));
    }

    @Test(timeout = 1000)
    public void willReturnEmptyIfTheLockIsHeldByAnotherThread() {
        final AtomicBoolean lockAcquired = new AtomicBoolean(false);
        final AutoCloseableReentrantLock lock = new AutoCloseableReentrantLock(AutoCloseableReentrantLockTest.class, "user1");
        thread1 = new Thread(() -> {
            try (final AutoCloseableReentrantLock.AutoCloseableLock ignored = lock.lock()) {
                lockAcquired.set(true);
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                AutoCloseableReentrantLockTest.LOGGER.info("Interrupted whilst sleeping", e);
            }
        });
        thread1.start();
        await().untilTrue(lockAcquired);
        MatcherAssert.assertThat(lock.tryLock(), Matchers.is(Optional.empty()));
    }

    @Test(timeout = 1000)
    public void willReturnTheCloseableIfTheLockIsNotHeldByAnotherThread() {
        final AutoCloseableReentrantLock lock = new AutoCloseableReentrantLock(AutoCloseableReentrantLockTest.class, "user1");
        try (final AutoCloseableReentrantLock.AutoCloseableLock ignored = lock.lock()) {
            final Optional<AutoCloseableReentrantLock.AutoCloseableLock> optionalCloseable = lock.tryLock();
            MatcherAssert.assertThat(optionalCloseable.isPresent(), Matchers.is(true));
            optionalCloseable.get().close();
        }
    }

    @Test(timeout = 1000)
    public void willInterruptWhilstWaitingForALock() throws InterruptedException {
        final AtomicBoolean lock1Acquired = new AtomicBoolean(false);
        final AtomicBoolean thread2Started = new AtomicBoolean(false);
        final AtomicBoolean lock2Acquired = new AtomicBoolean(false);
        final AtomicBoolean interrupted = new AtomicBoolean(false);
        final AutoCloseableReentrantLock lock = new AutoCloseableReentrantLock(AutoCloseableReentrantLockTest.class, "user1");
        thread1 = new Thread(() -> {
            try (final AutoCloseableReentrantLock.AutoCloseableLock ignored = lock.lock()) {
                lock1Acquired.set(true);
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                AutoCloseableReentrantLockTest.LOGGER.info("Interrupted whilst sleeping", e);
            }
        });
        thread1.start();
        thread2 = new Thread(() -> {
            await().untilTrue(lock1Acquired);
            thread2Started.set(true);
            try (final AutoCloseableReentrantLock.AutoCloseableLock ignored = lock.lockInterruptibly()) {
                lock2Acquired.set(true);
            } catch (final InterruptedException e) {
                interrupted.set(true);
            }
        });
        thread2.start();
        await().untilTrue(thread2Started);
        thread2.interrupt();
        thread2.join();
        MatcherAssert.assertThat(interrupted.get(), Matchers.is(true));
        MatcherAssert.assertThat(lock2Acquired.get(), Matchers.is(false));
    }
}

