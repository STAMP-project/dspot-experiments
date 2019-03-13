/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.generatedkeys.seqidentity;


import org.hibernate.Session;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@RequiresDialect(Oracle9iDialect.class)
public class SequenceIdentityTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testSequenceIdentityGenerator() {
        Session session = openSession();
        session.beginTransaction();
        MyEntity e = new MyEntity("entity-1");
        session.save(e);
        // this insert should happen immediately!
        Assert.assertNotNull("id not generated through forced insertion", e.getId());
        session.delete(e);
        session.getTransaction().commit();
        session.close();
    }
}

