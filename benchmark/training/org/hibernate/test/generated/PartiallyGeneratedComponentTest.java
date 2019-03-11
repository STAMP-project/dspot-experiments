/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.generated;


import org.hibernate.Session;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@inheritDoc }
 *
 * @author Steve Ebersole
 */
@RequiresDialect(Oracle9iDialect.class)
public class PartiallyGeneratedComponentTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testPartialComponentGeneration() {
        ComponentOwner owner = new ComponentOwner("initial");
        Session s = openSession();
        s.beginTransaction();
        s.save(owner);
        s.getTransaction().commit();
        s.close();
        Assert.assertNotNull("expecting insert value generation", owner.getComponent());
        int previousValue = owner.getComponent().getGenerated();
        Assert.assertFalse("expecting insert value generation", (0 == previousValue));
        s = openSession();
        s.beginTransaction();
        owner = ((ComponentOwner) (s.get(ComponentOwner.class, owner.getId())));
        Assert.assertEquals("expecting insert value generation", previousValue, owner.getComponent().getGenerated());
        owner.setName("subsequent");
        s.getTransaction().commit();
        s.close();
        Assert.assertNotNull(owner.getComponent());
        previousValue = owner.getComponent().getGenerated();
        s = openSession();
        s.beginTransaction();
        owner = ((ComponentOwner) (s.get(ComponentOwner.class, owner.getId())));
        Assert.assertEquals("expecting update value generation", previousValue, owner.getComponent().getGenerated());
        s.delete(owner);
        s.getTransaction().commit();
        s.close();
    }
}

