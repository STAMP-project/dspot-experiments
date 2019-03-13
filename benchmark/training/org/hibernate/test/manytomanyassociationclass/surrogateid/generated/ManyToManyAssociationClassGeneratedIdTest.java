/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.manytomanyassociationclass.surrogateid.generated;


import java.util.HashSet;
import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.test.manytomanyassociationclass.AbstractManyToManyAssociationClassTest;
import org.hibernate.test.manytomanyassociationclass.Membership;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests on many-to-many association using an association class with a surrogate ID that is generated.
 *
 * @author Gail Badner
 */
public class ManyToManyAssociationClassGeneratedIdTest extends AbstractManyToManyAssociationClassTest {
    @Test
    public void testRemoveAndAddEqualElement() {
        deleteMembership(getUser(), getGroup(), getMembership());
        addMembership(getUser(), getGroup(), createMembership("membership"));
        Session s = openSession();
        s.beginTransaction();
        try {
            // The new membership is transient (it has a null surrogate ID), so
            // Hibernate assumes that it should be added to the collection.
            // Inserts are done before deletes, so a ConstraintViolationException
            // will be thrown on the insert because the unique constraint on the
            // user and group IDs in the join table is violated. See HHH-2801.
            s.merge(getUser());
            s.getTransaction().commit();
            Assert.fail("should have failed because inserts are before deletes");
        } catch (PersistenceException e) {
            s.getTransaction().rollback();
            // expected
            ExtraAssertions.assertTyping(ConstraintViolationException.class, e.getCause());
        } finally {
            s.close();
        }
    }

    @Test
    public void testRemoveAndAddEqualCollection() {
        deleteMembership(getUser(), getGroup(), getMembership());
        getUser().setMemberships(new HashSet());
        getGroup().setMemberships(new HashSet());
        addMembership(getUser(), getGroup(), createMembership("membership"));
        Session s = openSession();
        s.beginTransaction();
        try {
            // The new membership is transient (it has a null surrogate ID), so
            // Hibernate assumes that it should be added to the collection.
            // Inserts are done before deletes, so a ConstraintViolationException
            // will be thrown on the insert because the unique constraint on the
            // user and group IDs in the join table is violated. See HHH-2801.
            s.merge(getUser());
            s.getTransaction().commit();
            Assert.fail("should have failed because inserts are before deletes");
        } catch (PersistenceException e) {
            s.getTransaction().rollback();
            // expected
            ExtraAssertions.assertTyping(ConstraintViolationException.class, e.getCause());
        } finally {
            s.close();
        }
    }

    @Test
    public void testRemoveAndAddEqualElementNonKeyModified() {
        deleteMembership(getUser(), getGroup(), getMembership());
        Membership membershipNew = createMembership("membership");
        addMembership(getUser(), getGroup(), membershipNew);
        membershipNew.setName("membership1");
        Session s = openSession();
        s.beginTransaction();
        try {
            // The new membership is transient (it has a null surrogate ID), so
            // Hibernate assumes that it should be added to the collection.
            // Inserts are done before deletes, so a ConstraintViolationException
            // will be thrown on the insert because the unique constraint on the
            // user and group IDs in the join table is violated. See HHH-2801.
            s.merge(getUser());
            s.getTransaction().commit();
            Assert.fail("should have failed because inserts are before deletes");
        } catch (PersistenceException e) {
            s.getTransaction().rollback();
            // expected
            ExtraAssertions.assertTyping(ConstraintViolationException.class, e.getCause());
        } finally {
            s.close();
        }
    }
}

