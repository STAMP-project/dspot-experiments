/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.uniqueconstraint;


import javax.persistence.PersistenceException;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 * @author Brett Meyer
 */
public class UniqueConstraintTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testUniquenessConstraintWithSuperclassProperty() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Room livingRoom = new Room();
        livingRoom.setId(1L);
        livingRoom.setName("livingRoom");
        s.persist(livingRoom);
        s.flush();
        House house = new House();
        house.setId(1L);
        house.setCost(100);
        house.setHeight(1000L);
        house.setRoom(livingRoom);
        s.persist(house);
        s.flush();
        House house2 = new House();
        house2.setId(2L);
        house2.setCost(100);
        house2.setHeight(1001L);
        house2.setRoom(livingRoom);
        s.persist(house2);
        try {
            s.flush();
            Assert.fail("Database constraint non-existant");
        } catch (PersistenceException e) {
            ExtraAssertions.assertTyping(JDBCException.class, e.getCause());
            // success
        } finally {
            tx.rollback();
            s.close();
        }
    }
}

