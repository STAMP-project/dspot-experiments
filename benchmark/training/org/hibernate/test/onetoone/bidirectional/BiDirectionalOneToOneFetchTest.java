/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.onetoone.bidirectional;


import java.util.concurrent.atomic.AtomicInteger;
import org.hibernate.engine.internal.StatisticalLoggingSessionEventListener;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;

import static CascadeType.ALL;
import static FetchType.EAGER;


/**
 * Test cases for fetch joining a bi-directional one-to-one mapping.
 *
 * @author Christian Beikov
 */
public class BiDirectionalOneToOneFetchTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-3930")
    public void testEagerFetchBidirectionalOneToOneWithDirectFetching() {
        inTransaction(( session) -> {
            org.hibernate.test.onetoone.bidirectional.EntityA a = new org.hibernate.test.onetoone.bidirectional.EntityA(1L, new org.hibernate.test.onetoone.bidirectional.EntityB(2L));
            session.persist(a);
            session.flush();
            session.clear();
            // Use atomic integer because we need something mutable
            final AtomicInteger queryExecutionCount = new AtomicInteger();
            session.getEventListenerManager().addListener(new StatisticalLoggingSessionEventListener() {
                @Override
                public void jdbcExecuteStatementStart() {
                    super.jdbcExecuteStatementStart();
                    queryExecutionCount.getAndIncrement();
                }
            });
            session.find(.class, 1L);
            assertEquals("Join fetching inverse one-to-one didn't use the object already present in the result set!", 1, queryExecutionCount.get());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-3930")
    public void testFetchBidirectionalOneToOneWithOneJoinFetch() {
        inTransaction(( session) -> {
            org.hibernate.test.onetoone.bidirectional.EntityA a = new org.hibernate.test.onetoone.bidirectional.EntityA(1L, new org.hibernate.test.onetoone.bidirectional.EntityB(2L));
            session.persist(a);
            session.flush();
            session.clear();
            // Use atomic integer because we need something mutable
            final AtomicInteger queryExecutionCount = new AtomicInteger();
            session.getEventListenerManager().addListener(new StatisticalLoggingSessionEventListener() {
                @Override
                public void jdbcExecuteStatementStart() {
                    super.jdbcExecuteStatementStart();
                    queryExecutionCount.getAndIncrement();
                }
            });
            session.createQuery("from EntityA a join fetch a.b").list();
            assertEquals("Join fetching inverse one-to-one didn't use the object already present in the result set!", 1, queryExecutionCount.get());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-3930")
    public void testFetchBidirectionalOneToOneWithCircularJoinFetch() {
        inTransaction(( session) -> {
            org.hibernate.test.onetoone.bidirectional.EntityA a = new org.hibernate.test.onetoone.bidirectional.EntityA(1L, new org.hibernate.test.onetoone.bidirectional.EntityB(2L));
            session.persist(a);
            session.flush();
            session.clear();
            // Use atomic integer because we need something mutable
            final AtomicInteger queryExecutionCount = new AtomicInteger();
            session.getEventListenerManager().addListener(new StatisticalLoggingSessionEventListener() {
                @Override
                public void jdbcExecuteStatementStart() {
                    super.jdbcExecuteStatementStart();
                    queryExecutionCount.getAndIncrement();
                }
            });
            session.createQuery("from EntityA a join fetch a.b b join fetch b.a").list();
            assertEquals("Join fetching inverse one-to-one didn't use the object already present in the result set!", 1, queryExecutionCount.get());
        });
    }

    @Entity(name = "EntityA")
    public static class EntityA {
        @Id
        private Long id;

        @OneToOne(fetch = EAGER, cascade = ALL)
        @JoinColumn(name = "b_id")
        private BiDirectionalOneToOneFetchTest.EntityB b;

        public EntityA() {
        }

        public EntityA(Long id, BiDirectionalOneToOneFetchTest.EntityB b) {
            this.id = id;
            this.b = b;
            this.b.a = this;
        }
    }

    @Entity(name = "EntityB")
    public static class EntityB {
        @Id
        private Long id;

        @OneToOne(mappedBy = "b", fetch = FetchType.EAGER)
        private BiDirectionalOneToOneFetchTest.EntityA a;

        public EntityB() {
        }

        public EntityB(Long id) {
            this.id = id;
        }
    }
}

