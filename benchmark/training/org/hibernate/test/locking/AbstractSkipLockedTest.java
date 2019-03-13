package org.hibernate.test.locking;


import java.util.Arrays;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.dialect.SQLServer2005Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public abstract class AbstractSkipLockedTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @RequiresDialect({ SQLServer2005Dialect.class })
    public void testSQLServerSkipLocked() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            for (long i = 1; i <= 10; i++) {
                org.hibernate.test.locking.BatchJob batchJob = new org.hibernate.test.locking.BatchJob();
                batchJob.setId(i);
                session.persist(batchJob);
            }
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<org.hibernate.test.locking.BatchJob> firstFive = nextFiveBatchJobs(session);
            assertEquals(5, firstFive.size());
            assertTrue(firstFive.stream().map(org.hibernate.test.locking.BatchJob::getId).collect(Collectors.toList()).containsAll(Arrays.asList(1L, 2L, 3L, 4L, 5L)));
            executeSync(() -> {
                doInHibernate(this::sessionFactory, ( _session) -> {
                    List<org.hibernate.test.locking.BatchJob> nextFive = nextFiveBatchJobs(_session);
                    assertEquals(5, nextFive.size());
                    assertTrue(nextFive.stream().map(org.hibernate.test.locking.BatchJob::getId).collect(Collectors.toList()).containsAll(Arrays.asList(6L, 7L, 8L, 9L, 10L)));
                });
            });
        });
    }

    @Test
    @RequiresDialect({ PostgreSQL95Dialect.class })
    public void testPostgreSQLSkipLocked() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            for (long i = 1; i <= 10; i++) {
                org.hibernate.test.locking.BatchJob batchJob = new org.hibernate.test.locking.BatchJob();
                batchJob.setId(i);
                session.persist(batchJob);
            }
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<org.hibernate.test.locking.BatchJob> firstFive = nextFiveBatchJobs(session);
            assertEquals(5, firstFive.size());
            assertTrue(firstFive.stream().map(org.hibernate.test.locking.BatchJob::getId).collect(Collectors.toList()).containsAll(Arrays.asList(1L, 2L, 3L, 4L, 5L)));
            executeSync(() -> {
                doInHibernate(this::sessionFactory, ( _session) -> {
                    List<org.hibernate.test.locking.BatchJob> nextFive = nextFiveBatchJobs(_session);
                    assertEquals(5, nextFive.size());
                    if ((lockMode()) == LockMode.PESSIMISTIC_READ) {
                        assertTrue(nextFive.stream().map(org.hibernate.test.locking.BatchJob::getId).collect(Collectors.toList()).containsAll(Arrays.asList(1L, 2L, 3L, 4L, 5L)));
                    } else {
                        assertTrue(nextFive.stream().map(org.hibernate.test.locking.BatchJob::getId).collect(Collectors.toList()).containsAll(Arrays.asList(6L, 7L, 8L, 9L, 10L)));
                    }
                });
            });
        });
    }

    @Test
    @RequiresDialect({ Oracle8iDialect.class })
    public void testOracleSkipLocked() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            for (long i = 1; i <= 10; i++) {
                org.hibernate.test.locking.BatchJob batchJob = new org.hibernate.test.locking.BatchJob();
                batchJob.setId(i);
                session.persist(batchJob);
            }
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<org.hibernate.test.locking.BatchJob> firstFive = nextFiveBatchJobs(session);
            assertEquals(5, firstFive.size());
            assertTrue(firstFive.stream().map(org.hibernate.test.locking.BatchJob::getId).collect(Collectors.toList()).containsAll(Arrays.asList(1L, 2L, 3L, 4L, 5L)));
            executeSync(() -> {
                doInHibernate(this::sessionFactory, ( _session) -> {
                    List<org.hibernate.test.locking.BatchJob> nextFive = nextFiveBatchJobs(_session);
                    assertEquals(0, nextFive.size());
                    nextFive = nextFiveBatchJobs(_session, 10);
                    assertTrue(nextFive.stream().map(org.hibernate.test.locking.BatchJob::getId).collect(Collectors.toList()).containsAll(Arrays.asList(6L, 7L, 8L, 9L, 10L)));
                });
            });
        });
    }

    @Test
    @RequiresDialect({ MySQL8Dialect.class })
    public void testMySQLSkipLocked() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            for (long i = 1; i <= 10; i++) {
                org.hibernate.test.locking.BatchJob batchJob = new org.hibernate.test.locking.BatchJob();
                batchJob.setId(i);
                session.persist(batchJob);
            }
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<org.hibernate.test.locking.BatchJob> firstFive = nextFiveBatchJobs(session);
            assertEquals(5, firstFive.size());
            assertTrue(firstFive.stream().map(org.hibernate.test.locking.BatchJob::getId).collect(Collectors.toList()).containsAll(Arrays.asList(1L, 2L, 3L, 4L, 5L)));
            executeSync(() -> {
                doInHibernate(this::sessionFactory, ( _session) -> {
                    List<org.hibernate.test.locking.BatchJob> nextFive = nextFiveBatchJobs(_session);
                    assertEquals(5, nextFive.size());
                    if ((lockMode()) == LockMode.PESSIMISTIC_READ) {
                        assertTrue(nextFive.stream().map(org.hibernate.test.locking.BatchJob::getId).collect(Collectors.toList()).containsAll(Arrays.asList(1L, 2L, 3L, 4L, 5L)));
                    } else {
                        assertTrue(nextFive.stream().map(org.hibernate.test.locking.BatchJob::getId).collect(Collectors.toList()).containsAll(Arrays.asList(6L, 7L, 8L, 9L, 10L)));
                    }
                });
            });
        });
    }

    @Entity(name = "BatchJob")
    public static class BatchJob {
        @Id
        private Long id;

        private boolean processed;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public boolean isProcessed() {
            return processed;
        }

        public void setProcessed(boolean processed) {
            this.processed = processed;
        }
    }
}

