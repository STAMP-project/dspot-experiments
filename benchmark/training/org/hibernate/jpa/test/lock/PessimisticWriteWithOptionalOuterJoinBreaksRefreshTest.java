/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.lock;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-13000")
public class PessimisticWriteWithOptionalOuterJoinBreaksRefreshTest extends BaseEntityManagerFunctionalTestCase {
    @Entity(name = "Parent")
    public static class Parent {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Long id;
    }

    @Entity(name = "Child")
    public static class Child {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Long id;

        @ManyToOne(cascade = { CascadeType.PERSIST })
        @JoinTable(name = "test")
        PessimisticWriteWithOptionalOuterJoinBreaksRefreshTest.Parent parent;
    }

    private PessimisticWriteWithOptionalOuterJoinBreaksRefreshTest.Child child;

    @Test
    public void pessimisticWriteWithOptionalOuterJoinBreaksRefreshTest() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            child = em.find(.class, child.id);
            em.lock(child, LockModeType.PESSIMISTIC_WRITE);
            em.flush();
            em.refresh(child);
        });
    }

    @Test
    public void pessimisticReadWithOptionalOuterJoinBreaksRefreshTest() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            child = em.find(.class, child.id);
            em.lock(child, LockModeType.PESSIMISTIC_READ);
            em.flush();
            em.refresh(child);
        });
    }
}

