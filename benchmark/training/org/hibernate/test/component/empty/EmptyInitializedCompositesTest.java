/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.component.empty;


import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for empty embedded dirtiness computation.
 *
 * @author Laurent Almeras
 */
public class EmptyInitializedCompositesTest extends BaseCoreFunctionalTestCase {
    /**
     * Test empty composite initialization.
     */
    @Test
    @TestForIssue(jiraKey = "HHH-7610")
    public void testCompositesEmpty() {
        Session s = openSession();
        try {
            s.getTransaction().begin();
            ComponentEmptyEmbeddedOwner owner = new ComponentEmptyEmbeddedOwner();
            s.persist(owner);
            s.flush();
            s.getTransaction().commit();
            s.clear();
            s.getTransaction().begin();
            owner = ((ComponentEmptyEmbeddedOwner) (s.get(ComponentEmptyEmbeddedOwner.class, owner.getId())));
            Assert.assertNotNull(owner.getEmbedded());
            Assert.assertFalse(s.isDirty());
            owner.setEmbedded(null);
            Assert.assertFalse(s.isDirty());// must be false to avoid unnecessary updates

            s.getTransaction().rollback();
        } finally {
            s.close();
        }
    }
}

