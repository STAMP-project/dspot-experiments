/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.insertordering;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12105")
public class InsertOrderingWithBidirectionalOneToOneFlushProblem extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testInsertSortingWithFlushPersistLeftBeforeRight() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.insertordering.TopEntity top1 = new org.hibernate.test.insertordering.TopEntity();
            session.persist(top1);
            session.flush();
            org.hibernate.test.insertordering.LeftEntity left = new org.hibernate.test.insertordering.LeftEntity();
            org.hibernate.test.insertordering.RightEntity right = new org.hibernate.test.insertordering.RightEntity();
            org.hibernate.test.insertordering.TopEntity top2 = new org.hibernate.test.insertordering.TopEntity();
            top1.lefts.add(left);
            left.top = top1;
            top1.rights.add(right);
            right.top = top1;
            // This one-to-one triggers the problem
            right.left = left;
            // If you persist right before left the problem goes away
            session.persist(left);
            session.persist(right);
            session.persist(top2);
        });
    }

    @Test
    public void testInsertSortingWithFlushPersistRightBeforeLeft() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.insertordering.TopEntity top1 = new org.hibernate.test.insertordering.TopEntity();
            session.persist(top1);
            session.flush();
            org.hibernate.test.insertordering.LeftEntity left = new org.hibernate.test.insertordering.LeftEntity();
            org.hibernate.test.insertordering.RightEntity right = new org.hibernate.test.insertordering.RightEntity();
            org.hibernate.test.insertordering.TopEntity top2 = new org.hibernate.test.insertordering.TopEntity();
            top1.lefts.add(left);
            left.top = top1;
            top1.rights.add(right);
            right.top = top1;
            // This one-to-one triggers the problem
            right.left = left;
            // If you persist right before left the problem goes away
            session.persist(right);
            session.persist(left);
            session.persist(top2);
        });
    }

    @Entity(name = "LeftEntity")
    public static class LeftEntity {
        @GeneratedValue
        @Id
        private Long id;

        @ManyToOne
        private InsertOrderingWithBidirectionalOneToOneFlushProblem.TopEntity top;
    }

    @Entity(name = "RightEntity")
    public static class RightEntity {
        @GeneratedValue
        @Id
        private Long id;

        @ManyToOne
        private InsertOrderingWithBidirectionalOneToOneFlushProblem.TopEntity top;

        @OneToOne
        private InsertOrderingWithBidirectionalOneToOneFlushProblem.LeftEntity left;
    }

    @Entity(name = "TopEntity")
    public static class TopEntity {
        @GeneratedValue
        @Id
        private Long id;

        @OneToMany(mappedBy = "top")
        private List<InsertOrderingWithBidirectionalOneToOneFlushProblem.RightEntity> rights = new ArrayList<>();

        @OneToMany(mappedBy = "top")
        private List<InsertOrderingWithBidirectionalOneToOneFlushProblem.LeftEntity> lefts = new ArrayList<>();
    }
}

