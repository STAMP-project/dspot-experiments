/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.fileimport;


import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class SingleLineImportFileTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testImportFile() throws Exception {
        Session s = openSession();
        final Transaction tx = s.beginTransaction();
        final List<?> humans = s.createQuery(("from " + (Human.class.getName()))).list();
        Assert.assertEquals("humans.sql not imported", 3, humans.size());
        final List<?> dogs = s.createQuery(("from " + (Dog.class.getName()))).list();
        Assert.assertEquals("dogs.sql not imported", 3, dogs.size());
        for (Object entity : dogs) {
            s.delete(entity);
        }
        for (Object entity : humans) {
            s.delete(entity);
        }
        tx.commit();
        s.close();
    }
}

