/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.insertordering;


import java.sql.SQLException;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12407")
public class InsertOrderingWithBaseClassReferencingSubclass extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testBatching() throws SQLException {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.insertordering.OwningTable rec_owningTable = new org.hibernate.test.insertordering.OwningTable();
            session.persist(rec_owningTable);
            session.flush();
            org.hibernate.test.insertordering.TableB rec_tableB = new org.hibernate.test.insertordering.TableB();
            rec_tableB.owning = rec_owningTable;
            session.persist(rec_tableB);
            org.hibernate.test.insertordering.TableA rec_tableA = new org.hibernate.test.insertordering.TableA();
            rec_tableA.owning = rec_owningTable;
            session.persist(rec_tableA);
            org.hibernate.test.insertordering.LinkTable rec_link = new org.hibernate.test.insertordering.LinkTable();
            rec_link.refToA = rec_tableA;
            rec_link.refToB = rec_tableB;
            session.persist(rec_link);
        });
    }

    @Entity(name = "RootTable")
    @Inheritance(strategy = InheritanceType.JOINED)
    public abstract static class RootTable {
        @Id
        @GeneratedValue
        public int sysId;
    }

    @Entity(name = "OwnedTable")
    public abstract static class OwnedTable extends InsertOrderingWithBaseClassReferencingSubclass.RootTable {
        @ManyToOne
        public InsertOrderingWithBaseClassReferencingSubclass.OwningTable owning;
    }

    @Entity(name = "OwningTable")
    public static class OwningTable extends InsertOrderingWithBaseClassReferencingSubclass.OwnedTable {}

    @Entity(name = "TableA")
    public static class TableA extends InsertOrderingWithBaseClassReferencingSubclass.OwnedTable {}

    @Entity(name = "TableB")
    public static class TableB extends InsertOrderingWithBaseClassReferencingSubclass.OwnedTable {}

    @Entity(name = "LinkTable")
    public static class LinkTable {
        @Id
        @GeneratedValue
        public int sysId;

        @ManyToOne
        public InsertOrderingWithBaseClassReferencingSubclass.TableA refToA;

        @ManyToOne
        public InsertOrderingWithBaseClassReferencingSubclass.TableB refToB;
    }
}

