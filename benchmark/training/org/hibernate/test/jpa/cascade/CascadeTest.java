/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.jpa.cascade;


import org.hibernate.Session;
import org.hibernate.TransientObjectException;
import org.hibernate.test.jpa.AbstractJPATest;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 * According to the JPA spec, persist()ing an entity should throw an exception
 * when said entity contains a reference to a transient entity through a mapped
 * association where that association is not marked for cascading the persist
 * operation.
 * <p/>
 * This test-case tests that requirement in the various association style
 * scenarios such as many-to-one, one-to-one, many-to-one (property-ref),
 * one-to-one (property-ref).  Additionally, it performs each of these tests
 * in both generated and assigned identifier usages...
 *
 * @author Steve Ebersole
 */
public class CascadeTest extends AbstractJPATest {
    @Test
    public void testManyToOneGeneratedIdsOnSave() {
        // NOTES: Child defines a many-to-one back to its Parent.  This
        // association does not define persist cascading (which is natural;
        // a child should not be able to create its parent).
        try {
            Session s = openSession();
            s.beginTransaction();
            Parent p = new Parent("parent");
            Child c = new Child("child");
            c.setParent(p);
            s.save(c);
            try {
                s.getTransaction().commit();
                Assert.fail("expecting TransientObjectException on flush");
            } catch (IllegalStateException e) {
                ExtraAssertions.assertTyping(TransientObjectException.class, e.getCause());
                log.trace("handled expected exception", e);
                s.getTransaction().rollback();
            } finally {
                s.close();
            }
        } finally {
            cleanupData();
        }
    }
}

