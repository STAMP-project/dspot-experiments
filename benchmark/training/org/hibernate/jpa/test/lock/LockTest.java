/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.lock;


import AvailableSettings.LOCK_TIMEOUT;
import DialectChecks.SupportSkipLocked;
import DialectChecks.SupportsLockTimeouts;
import QueryHints.SPEC_HINT_TIMEOUT;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.persistence.Query;
import org.hibernate.TransactionException;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.testing.util.ExceptionUtil;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class LockTest extends BaseEntityManagerFunctionalTestCase {
    private static final Logger log = Logger.getLogger(LockTest.class);

    @Test
    public void testFindWithTimeoutHint() {
        final Lock lock = new Lock();
        lock.setName("name");
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            em.persist(lock);
            return lock.getId();
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(AvailableSettings.LOCK_TIMEOUT, 0L);
            em.find(.class, 1, LockModeType.PESSIMISTIC_WRITE, properties);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.find(.class, lock.getId());
            em.remove(_lock);
        });
    }

    // 5 seconds
    @Test(timeout = 5 * 1000)
    @TestForIssue(jiraKey = "HHH-7252")
    @RequiresDialectFeature(value = SupportsLockTimeouts.class, comment = "Test verifies proper exception throwing when a lock timeout is specified.", jiraKey = "HHH-7252")
    public void testFindWithPessimisticWriteLockTimeoutException() {
        Lock lock = new Lock();
        lock.setName("name");
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(lock);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( _entityManager) -> {
            Lock lock2 = _entityManager.find(.class, lock.getId(), LockModeType.PESSIMISTIC_WRITE);
            assertEquals("lock mode should be PESSIMISTIC_WRITE ", LockModeType.PESSIMISTIC_WRITE, _entityManager.getLockMode(lock2));
            doInJPA(this::entityManagerFactory, ( entityManager) -> {
                try {
                    TransactionUtil.setJdbcTimeout(entityManager.unwrap(.class));
                    Map<String, Object> properties = new HashMap<String, Object>();
                    properties.put(AvailableSettings.LOCK_TIMEOUT, 0L);
                    entityManager.find(.class, lock.getId(), LockModeType.PESSIMISTIC_WRITE, properties);
                    fail("Exception should be thrown");
                } catch ( lte) {
                    // Proper exception thrown for dialect supporting lock timeouts when an immediate timeout is set.
                    lte.getCause();
                } catch ( pe) {
                    fail("Find with immediate timeout should have thrown LockTimeoutException.");
                } catch ( pe) {
                    log.info((((("EntityManager.find() for PESSIMISTIC_WRITE with timeout of 0 threw a PersistenceException.\n" + "This is likely a consequence of ") + (getDialect().getClass().getName())) + " not properly mapping SQL errors into the correct HibernateException subtypes.\n") + "See HHH-7251 for an example of one such situation."), pe);
                    fail("EntityManager should be throwing LockTimeoutException.");
                }
            });
        });
    }

    @Test
    @RequiresDialectFeature(SupportSkipLocked.class)
    public void testUpdateWithPessimisticReadLockSkipLocked() {
        Lock lock = new Lock();
        lock.setName("name");
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(lock);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( _entityManagaer) -> {
            Map<String, Object> properties = new HashMap<>();
            properties.put(org.hibernate.cfg.AvailableSettings.JPA_LOCK_TIMEOUT, LockOptions.SKIP_LOCKED);
            _entityManagaer.find(.class, lock.getId(), LockModeType.PESSIMISTIC_READ, properties);
            try {
                doInJPA(this::entityManagerFactory, ( entityManager) -> {
                    TransactionUtil.setJdbcTimeout(entityManager.unwrap(.class));
                    try {
                        entityManager.createNativeQuery(updateStatement()).setParameter("name", "changed").setParameter("id", lock.getId()).executeUpdate();
                        fail("Should throw Exception");
                    } catch ( e) {
                        if (!(ExceptionUtil.isSqlLockTimeout(e))) {
                            fail(("Unknown exception thrown: " + (e.getMessage())));
                        }
                    }
                });
            } catch ( e) {
                log.error("Failure", e);
            }
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Lock _lock = entityManager.merge(lock);
            entityManager.remove(_lock);
        });
    }

    @Test
    @RequiresDialectFeature(SupportsLockTimeouts.class)
    public void testUpdateWithPessimisticReadLockWithoutNoWait() {
        Lock lock = new Lock();
        lock.setName("name");
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(lock);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( _entityManager) -> {
            _entityManager.find(.class, lock.getId(), LockModeType.PESSIMISTIC_READ);
            AtomicBoolean failureExpected = new AtomicBoolean();
            try {
                doInJPA(this::entityManagerFactory, ( entityManager) -> {
                    try {
                        TransactionUtil.setJdbcTimeout(entityManager.unwrap(.class));
                        entityManager.createNativeQuery(updateStatement()).setParameter("name", "changed").setParameter("id", lock.getId()).executeUpdate();
                    } catch ( e) {
                        if (ExceptionUtil.isSqlLockTimeout(e)) {
                            failureExpected.set(true);
                        }
                    }
                });
            } catch ( e) {
                if (!(failureExpected.get())) {
                    fail("Should throw LockTimeoutException or PessimisticLockException");
                }
            }
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Lock _lock = entityManager.merge(lock);
            entityManager.remove(_lock);
        });
    }

    @Test
    public void testLockRead() throws Exception {
        final Lock lock = new Lock();
        lock.setName("name");
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            em.persist(lock);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.getReference(.class, lock.getId());
            em.lock(_lock, LockModeType.READ);
            _lock.setName("surname");
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.find(.class, lock.getId());
            assertEquals("surname", _lock.getName());
            em.remove(_lock);
        });
    }

    @Test
    public void testLockOptimistic() throws Exception {
        final Lock lock = new Lock();
        lock.setName("name");
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            em.persist(lock);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.getReference(.class, lock.getId());
            em.lock(_lock, LockModeType.OPTIMISTIC);
            _lock.setName("surname");
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.find(.class, lock.getId());
            assertEquals("surname", _lock.getName());
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.find(.class, lock.getId());
            em.remove(_lock);
        });
    }

    @Test
    public void testLockWrite() throws Exception {
        final Lock lock = new Lock();
        lock.setName("second");
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            em.persist(lock);
        });
        Integer version = TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.getReference(.class, lock.getId());
            Integer _version = _lock.getVersion();
            em.lock(_lock, LockModeType.WRITE);
            return _version;
        });
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                assertEquals("should increase the version number EJB-106", 1, ((_lock.getVersion()) - version));
            });
        } finally {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.remove(_lock);
            });
        }
    }

    @Test
    public void testLockWriteOnUnversioned() throws Exception {
        final UnversionedLock lock = new UnversionedLock();
        lock.setName("second");
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            em.persist(lock);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            UnversionedLock _lock = em.getReference(.class, lock.getId());
            try {
                // getting a READ (optimistic) lock on unversioned entity is not expected to work.
                // To get the same functionality as prior release, change the  LockModeType.READ lock to:
                // em.lock(lock,LockModeType.PESSIMISTIC_READ);
                em.lock(_lock, LockModeType.READ);
                fail("expected OptimisticLockException exception");
            } catch ( expected) {
            }
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            // the previous code block can be rewritten as follows (to get the previous behavior)
            UnversionedLock _lock = em.getReference(.class, lock.getId());
            em.lock(_lock, LockModeType.PESSIMISTIC_READ);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            UnversionedLock _lock = em.getReference(.class, lock.getId());
            em.remove(_lock);
        });
    }

    @Test
    public void testLockPessimisticForceIncrement() throws Exception {
        final Lock lock = new Lock();
        lock.setName("force");
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            em.persist(lock);
        });
        Integer version = TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.getReference(.class, lock.getId());
            Integer _version = _lock.getVersion();
            em.lock(_lock, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
            return _version;
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.getReference(.class, lock.getId());
            assertEquals("should increase the version number ", 1, ((_lock.getVersion()) - version));
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.find(.class, lock.getId());
            em.remove(_lock);
        });
    }

    @Test
    public void testLockOptimisticForceIncrement() throws Exception {
        final Lock lock = new Lock();
        lock.setName("force");
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            em.persist(lock);
        });
        Integer version = TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.getReference(.class, lock.getId());
            Integer _version = _lock.getVersion();
            em.lock(_lock, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            return _version;
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.getReference(.class, lock.getId());
            assertEquals("should increase the version number ", 1, ((_lock.getVersion()) - version));
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.find(.class, lock.getId());
            em.remove(_lock);
        });
    }

    @Test
    public void testLockOptimisticForceIncrementDifferentEm() throws Exception {
        final Lock lock = new Lock();
        lock.setName("force");
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            em.persist(lock);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.find(.class, lock.getId(), LockModeType.OPTIMISTIC);
            assertEquals("lock mode should be OPTIMISTIC ", LockModeType.OPTIMISTIC, em.getLockMode(_lock));
            em.lock(_lock, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
            assertEquals("lock mode should be OPTIMISTIC_FORCE_INCREMENT ", LockModeType.OPTIMISTIC_FORCE_INCREMENT, em.getLockMode(_lock));
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            Lock _lock = em.find(.class, lock.getId());
            em.remove(_lock);
        });
    }

    // ASE15.5 will generate select...holdlock and fail at this test, but ASE15.7 passes it. Skip it for ASE15.5
    // only.
    @Test
    @SkipForDialect(HSQLDialect.class)
    @SkipForDialect(value = { SybaseASE15Dialect.class }, strictMatching = true, jiraKey = "HHH-6820")
    @SkipForDialect({ SQLServerDialect.class })
    public void testContendedPessimisticLock() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final Lock lock = new Lock();
        final AtomicBoolean backgroundThreadHasReadNewValue = new AtomicBoolean();
        FutureTask<Boolean> bgTask = new FutureTask<>(() -> {
            try {
                TransactionUtil.doInJPA(this::entityManagerFactory, ( _entityManager) -> {
                    TransactionUtil.setJdbcTimeout(_entityManager.unwrap(.class));
                    log.info("testContendedPessimisticLock: (BG) about to issue (PESSIMISTIC_READ) query against write-locked entity");
                    try {
                        // we should block on the following read
                        Query query = _entityManager.createQuery("select L from Lock_ L where L.id < 10000 ");
                        query.setLockMode(LockModeType.PESSIMISTIC_READ);
                        List<Lock> resultList = query.getResultList();
                        Lock _lock = resultList.get(0);
                        backgroundThreadHasReadNewValue.set(_lock.getName().equals("foo"));
                    } catch ( e) {
                        if (!(ExceptionUtil.isSqlLockTimeout(e))) {
                            fail(("An error occurred waiting while attempting to read the entity: " + (e.getMessage())));
                        }
                        backgroundThreadHasReadNewValue.set(false);
                    }
                });
            } catch (TransactionException e) {
                if (!(ExceptionUtil.isConnectionClose(e))) {
                    Assert.fail(("Unexpected exception: " + (e.getMessage())));
                }
            } finally {
                latch.countDown();// signal that we finished

            }
            return backgroundThreadHasReadNewValue.get();
        });
        Thread t = new Thread(bgTask);
        t.setDaemon(true);
        t.setName("Lock timeout Test (bg)");
        try {
            lock.setName("testContendedPessimisticLock");
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                em.persist(lock);
            });
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.lock(_lock, LockModeType.PESSIMISTIC_WRITE);
                // modify and flush, but don't commit the transaction
                _lock.setName("foo");
                em.flush();
                log.info("testContendedPessimisticLock: got write lock");
                try {
                    t.start();
                    boolean backGroundThreadCompleted = latch.await(10, TimeUnit.SECONDS);// should return quickly on success

                    if (backGroundThreadCompleted) {
                        // the background thread read a value. At the very least we need to assert that he did not see the
                        // changed value
                        assertFalse("The background thread is not allowed to see the updated value while the first transaction has not committed yet", backgroundThreadHasReadNewValue.get());
                    } else {
                        log.debug("The background thread was blocked");
                        assertTrue("Background thread should read the new value after being unblocked", backgroundThreadHasReadNewValue.get());
                    }
                } catch ( e) {
                    Thread.interrupted();
                }
            });
        } finally {
            t.join();// wait for background thread to finish before deleting entity

            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.remove(_lock);
            });
        }
    }

    @Test
    @RequiresDialect(Oracle10gDialect.class)
    @RequiresDialectFeature(SupportsLockTimeouts.class)
    public void testContendedPessimisticReadLockTimeout() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final Lock lock = new Lock();
        FutureTask<Boolean> bgTask = new FutureTask<>(() -> {
            try {
                AtomicBoolean timedOut = new AtomicBoolean();// true (success) if LockTimeoutException occurred

                TransactionUtil.doInJPA(this::entityManagerFactory, ( _entityManager) -> {
                    log.info("testContendedPessimisticReadLockTimeout: (BG) about to read write-locked entity");
                    // we should block on the following read
                    Lock lock2 = _entityManager.getReference(.class, lock.getId());
                    lock2.getName();// force entity to be read

                    log.info("testContendedPessimisticReadLockTimeout: (BG) read write-locked entity");
                    Map<String, Object> props = new HashMap<String, Object>();
                    // timeout is in milliseconds
                    props.put(AvailableSettings.LOCK_TIMEOUT, 1000);
                    try {
                        _entityManager.lock(lock2, LockModeType.PESSIMISTIC_READ, props);
                    } catch ( e) {
                        // success
                        log.info("testContendedPessimisticReadLockTimeout: (BG) got expected timeout exception");
                        timedOut.set(true);
                    } catch ( e) {
                        log.info("Expected LockTimeoutException but got unexpected exception", e);
                        throw new <e>RuntimeException("Expected LockTimeoutException but got unexpected exception");
                    }
                });
                return timedOut.get();
            } finally {
                latch.countDown();// signal that we finished

            }
        });
        Thread t = new Thread(bgTask);
        t.setDaemon(true);
        t.setName("Lock timeout Test (bg)");
        try {
            lock.setName("testContendedPessimisticReadLockTimeout");
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                em.persist(lock);
            });
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.lock(_lock, LockModeType.PESSIMISTIC_WRITE);
                final Integer id = _lock.getId();
                _lock.getName();// force entity to be read

                log.info("testContendedPessimisticReadLockTimeout: got write lock");
                try {
                    t.start();
                    boolean latchSet = latch.await(10, TimeUnit.SECONDS);// should return quickly on success

                    assertTrue("background test thread finished (lock timeout is broken)", latchSet);
                    assertTrue("background test thread timed out on lock attempt", bgTask.get());
                } catch ( e) {
                    Thread.interrupted();
                } catch ( e) {
                    fail(e.getMessage());
                }
            });
        } finally {
            t.join();// wait for background thread to finish before deleting entity

            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.remove(_lock);
            });
        }
    }

    @Test
    @RequiresDialect(Oracle10gDialect.class)
    @RequiresDialectFeature(SupportsLockTimeouts.class)
    public void testContendedPessimisticWriteLockTimeout() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final Lock lock = new Lock();
        FutureTask<Boolean> bgTask = new FutureTask<>(() -> {
            try {
                AtomicBoolean timedOut = new AtomicBoolean();// true (success) if LockTimeoutException occurred

                TransactionUtil.doInJPA(this::entityManagerFactory, ( _entityManager) -> {
                    log.info("testContendedPessimisticWriteLockTimeout: (BG) about to read write-locked entity");
                    // we should block on the following read
                    Lock lock2 = _entityManager.getReference(.class, lock.getId());
                    lock2.getName();// force entity to be read

                    log.info("testContendedPessimisticWriteLockTimeout: (BG) read write-locked entity");
                    Map<String, Object> props = new HashMap<String, Object>();
                    // timeout is in milliseconds
                    props.put(AvailableSettings.LOCK_TIMEOUT, 1000);
                    try {
                        _entityManager.lock(lock2, LockModeType.PESSIMISTIC_WRITE, props);
                    } catch ( e) {
                        // success
                        log.info("testContendedPessimisticWriteLockTimeout: (BG) got expected timeout exception");
                        timedOut.set(true);
                    } catch ( e) {
                        log.info("Expected LockTimeoutException but got unexpected exception", e);
                    }
                });
                return timedOut.get();
            } finally {
                latch.countDown();// signal that we finished

            }
        });
        Thread t = new Thread(bgTask);
        t.setDaemon(true);
        t.setName("Lock timeout Test (bg)");
        try {
            lock.setName("testContendedPessimisticWriteLockTimeout");
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                em.persist(lock);
            });
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.lock(_lock, LockModeType.PESSIMISTIC_WRITE);
                final Integer id = _lock.getId();
                _lock.getName();// force entity to be read

                log.info("testContendedPessimisticWriteLockTimeout: got write lock");
                try {
                    t.start();
                    boolean latchSet = latch.await(10, TimeUnit.SECONDS);// should return quickly on success

                    assertTrue("background test thread finished (lock timeout is broken)", latchSet);
                    assertTrue("background test thread timed out on lock attempt", bgTask.get());
                } catch ( e) {
                    Thread.interrupted();
                } catch ( e) {
                    fail(e.getMessage());
                }
            });
        } finally {
            t.join();// wait for background thread to finish before deleting entity

            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.remove(_lock);
            });
        }
    }

    @Test
    @RequiresDialect({ Oracle10gDialect.class, PostgreSQL81Dialect.class })
    @RequiresDialectFeature(SupportsLockTimeouts.class)
    public void testContendedPessimisticWriteLockNoWait() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final Lock lock = new Lock();
        FutureTask<Boolean> bgTask = new FutureTask<>(() -> {
            try {
                AtomicBoolean timedOut = new AtomicBoolean();// true (success) if LockTimeoutException occurred

                TransactionUtil.doInJPA(this::entityManagerFactory, ( _entityManager) -> {
                    log.info("testContendedPessimisticWriteLockNoWait: (BG) about to read write-locked entity");
                    // we should block on the following read
                    Lock lock2 = _entityManager.getReference(.class, lock.getId());
                    lock2.getName();// force entity to be read

                    log.info("testContendedPessimisticWriteLockNoWait: (BG) read write-locked entity");
                    Map<String, Object> props = new HashMap<String, Object>();
                    // timeout of zero means no wait (for lock)
                    props.put(AvailableSettings.LOCK_TIMEOUT, 0);
                    try {
                        _entityManager.lock(lock2, LockModeType.PESSIMISTIC_WRITE, props);
                    } catch ( e) {
                        // success
                        log.info("testContendedPessimisticWriteLockNoWait: (BG) got expected timeout exception");
                        timedOut.set(true);
                    } catch ( e) {
                        log.info("Expected LockTimeoutException but got unexpected exception", e);
                    }
                });
                return timedOut.get();
            } finally {
                latch.countDown();// signal that we finished

            }
        });
        Thread t = new Thread(bgTask);
        t.setDaemon(true);
        t.setName("Lock timeout Test (bg)");
        try {
            lock.setName("testContendedPessimisticWriteLockNoWait");
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                em.persist(lock);
            });
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.lock(_lock, LockModeType.PESSIMISTIC_WRITE);
                final Integer id = _lock.getId();
                _lock.getName();// force entity to be read

                log.info("testContendedPessimisticWriteLockNoWait: got write lock");
                try {
                    t.start();
                    boolean latchSet = latch.await(10, TimeUnit.SECONDS);// should return quickly on success

                    assertTrue("background test thread finished (lock timeout is broken)", latchSet);
                    assertTrue("background test thread timed out on lock attempt", bgTask.get());
                } catch ( e) {
                    Thread.interrupted();
                } catch ( e) {
                    fail(e.getMessage());
                }
            });
        } finally {
            t.join();// wait for background thread to finish before deleting entity

            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.remove(_lock);
            });
        }
    }

    @Test
    @RequiresDialect(Oracle10gDialect.class)
    @RequiresDialectFeature(SupportsLockTimeouts.class)
    public void testQueryTimeout() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final Lock lock = new Lock();
        FutureTask<Boolean> bgTask = new FutureTask<>(() -> {
            try {
                AtomicBoolean timedOut = new AtomicBoolean();// true (success) if LockTimeoutException occurred

                TransactionUtil.doInJPA(this::entityManagerFactory, ( _entityManager) -> {
                    log.info("testQueryTimeout: (BG) about to read write-locked entity");
                    // we should block on the following read
                    Lock lock2 = _entityManager.getReference(.class, lock.getId());
                    lock2.getName();// force entity to be read

                    log.info("testQueryTimeout: (BG) read write-locked entity");
                    try {
                        // we should block on the following read
                        Query query = _entityManager.createQuery("select L from Lock_ L where L.id < 10000 ");
                        query.setLockMode(LockModeType.PESSIMISTIC_READ);
                        query.setHint(QueryHints.SPEC_HINT_TIMEOUT, 500);// 1 sec timeout

                        List<Lock> resultList = query.getResultList();
                        String name = resultList.get(0).getName();// force entity to be read

                        log.info(("testQueryTimeout: name read =" + name));
                    } catch ( e) {
                        // success
                        log.info("testQueryTimeout: (BG) got expected timeout exception");
                        timedOut.set(true);
                    } catch ( e) {
                        log.info("Expected LockTimeoutException but got unexpected exception", e);
                    }
                });
                return timedOut.get();
            } finally {
                latch.countDown();// signal that we finished

            }
        });
        Thread t = new Thread(bgTask);
        t.setDaemon(true);
        t.setName("Lock timeout Test (bg)");
        try {
            lock.setName("testQueryTimeout");
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                em.persist(lock);
            });
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.lock(_lock, LockModeType.PESSIMISTIC_WRITE);
                final Integer id = _lock.getId();
                _lock.getName();// force entity to be read

                log.info("testQueryTimeout: got write lock");
                try {
                    t.start();
                    boolean latchSet = latch.await(10, TimeUnit.SECONDS);// should return quickly on success

                    assertTrue("background test thread finished (lock timeout is broken)", latchSet);
                    assertTrue("background test thread timed out on lock attempt", bgTask.get());
                } catch ( e) {
                    Thread.interrupted();
                } catch ( e) {
                    fail(e.getMessage());
                }
            });
        } finally {
            t.join();// wait for background thread to finish before deleting entity

            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.remove(_lock);
            });
        }
    }

    @Test
    @RequiresDialect(Oracle10gDialect.class)
    @RequiresDialectFeature(SupportsLockTimeouts.class)
    public void testQueryTimeoutEMProps() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final java.util.Map<String, Object> timeoutProps = new java.util.HashMap<String, Object>();
        timeoutProps.put(SPEC_HINT_TIMEOUT, 500);// 1 sec timeout (should round up)

        final Lock lock = new Lock();
        FutureTask<Boolean> bgTask = new FutureTask<>(() -> {
            try {
                AtomicBoolean timedOut = new AtomicBoolean();// true (success) if LockTimeoutException occurred

                TransactionUtil.doInJPA(this::entityManagerFactory, ( _entityManager) -> {
                    log.info("testQueryTimeout: (BG) about to read write-locked entity");
                    // we should block on the following read
                    Lock lock2 = _entityManager.getReference(.class, lock.getId());
                    lock2.getName();// force entity to be read

                    log.info("testQueryTimeout: (BG) read write-locked entity");
                    try {
                        // we should block on the following read
                        Query query = _entityManager.createQuery("select L from Lock_ L where L.id < 10000 ");
                        query.setLockMode(LockModeType.PESSIMISTIC_READ);
                        List<Lock> resultList = query.getResultList();
                        String name = resultList.get(0).getName();// force entity to be read

                        log.info(("testQueryTimeout: name read =" + name));
                    } catch ( e) {
                        // success
                        log.info("testQueryTimeout: (BG) got expected timeout exception");
                        timedOut.set(true);
                    } catch ( e) {
                        log.info("Expected LockTimeoutException but got unexpected exception", e);
                    }
                }, timeoutProps);
                return timedOut.get();
            } finally {
                latch.countDown();// signal that we finished

            }
        });
        Thread t = new Thread(bgTask);
        t.setDaemon(true);
        t.setName("Lock timeout Test (bg)");
        try {
            lock.setName("testQueryTimeout");
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                em.persist(lock);
            });
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.lock(_lock, LockModeType.PESSIMISTIC_WRITE);
                final Integer id = _lock.getId();
                _lock.getName();// force entity to be read

                log.info("testQueryTimeout: got write lock");
                try {
                    t.start();
                    boolean latchSet = latch.await(10, TimeUnit.SECONDS);// should return quickly on success

                    assertTrue("background test thread finished (lock timeout is broken)", latchSet);
                    assertTrue("background test thread timed out on lock attempt", bgTask.get());
                } catch ( e) {
                    Thread.interrupted();
                } catch ( e) {
                    fail(e.getMessage());
                }
            });
        } finally {
            t.join();// wait for background thread to finish before deleting entity

            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.remove(_lock);
            });
        }
    }

    @Test
    @RequiresDialect(Oracle10gDialect.class)
    @RequiresDialectFeature(SupportsLockTimeouts.class)
    public void testLockTimeoutEMProps() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final java.util.Map<String, Object> timeoutProps = new java.util.HashMap<String, Object>();
        timeoutProps.put(LOCK_TIMEOUT, 1000);// 1 second timeout

        final Lock lock = new Lock();
        FutureTask<Boolean> bgTask = new FutureTask<>(() -> {
            try {
                AtomicBoolean timedOut = new AtomicBoolean();// true (success) if LockTimeoutException occurred

                TransactionUtil.doInJPA(this::entityManagerFactory, ( _entityManager) -> {
                    log.info("testLockTimeoutEMProps: (BG) about to read write-locked entity");
                    // we should block on the following read
                    Lock lock2 = _entityManager.getReference(.class, lock.getId());
                    lock2.getName();// force entity to be read

                    log.info("testLockTimeoutEMProps: (BG) read write-locked entity");
                    // em2 already has AvailableSettings.LOCK_TIMEOUT of 1 second applied
                    try {
                        _entityManager.lock(lock2, LockModeType.PESSIMISTIC_WRITE);
                    } catch ( e) {
                        // success
                        log.info("testLockTimeoutEMProps: (BG) got expected timeout exception");
                        timedOut.set(true);
                    } catch ( e) {
                        log.info("Expected LockTimeoutException but got unexpected exception", e);
                    }
                }, timeoutProps);
                return timedOut.get();
            } finally {
                latch.countDown();// signal that we finished

            }
        });
        Thread t = new Thread(bgTask);
        t.setDaemon(true);
        t.setName("Lock timeout Test (bg)");
        try {
            lock.setName("testLockTimeoutEMProps");
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                em.persist(lock);
            });
            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.lock(_lock, LockModeType.PESSIMISTIC_WRITE);
                final Integer id = _lock.getId();
                _lock.getName();// force entity to be read

                log.info("testLockTimeoutEMProps: got write lock");
                try {
                    t.start();
                    boolean latchSet = latch.await(10, TimeUnit.SECONDS);// should return quickly on success

                    assertTrue("background test thread finished (lock timeout is broken)", latchSet);
                    assertTrue("background test thread timed out on lock attempt", bgTask.get());
                } catch ( e) {
                    Thread.interrupted();
                } catch ( e) {
                    fail(e.getMessage());
                }
            });
        } finally {
            t.join();// wait for background thread to finish before deleting entity

            TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
                Lock _lock = em.getReference(.class, lock.getId());
                em.remove(_lock);
            });
        }
    }
}

