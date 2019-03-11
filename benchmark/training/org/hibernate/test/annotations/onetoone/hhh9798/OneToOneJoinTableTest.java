/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.onetoone.hhh9798;


import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-9798")
public class OneToOneJoinTableTest extends BaseCoreFunctionalTestCase {
    @Test
    public void storeNonUniqueRelationship() throws Throwable {
        Session session = null;
        try {
            session = openSession();
            Transaction tx = session.beginTransaction();
            Item someItem = new Item("Some Item");
            session.save(someItem);
            Shipment shipment1 = new Shipment(someItem);
            session.save(shipment1);
            Shipment shipment2 = new Shipment(someItem);
            session.save(shipment2);
            tx.commit();
            Assert.fail();
        } catch (PersistenceException e) {
            ExtraAssertions.assertTyping(ConstraintViolationException.class, e.getCause());
            // expected
        } finally {
            if (session != null) {
                session.getTransaction().rollback();
                session.close();
            }
            cleanUpData();
        }
    }
}

