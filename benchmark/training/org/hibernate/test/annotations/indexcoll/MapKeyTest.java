/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.indexcoll;


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
public class MapKeyTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testMapKeyOnEmbeddedId() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Generation c = new Generation();
        c.setAge("a");
        c.setCulture("b");
        c.setSubGeneration(new Generation.SubGeneration("description"));
        GenerationGroup r = new GenerationGroup();
        r.setGeneration(c);
        s.persist(r);
        GenerationUser m = new GenerationUser();
        s.persist(m);
        m.getRef().put(c, r);
        s.flush();
        s.clear();
        m = ((GenerationUser) (s.get(GenerationUser.class, m.getId())));
        Generation cRead = m.getRef().keySet().iterator().next();
        Assert.assertEquals("a", cRead.getAge());
        Assert.assertEquals("description", cRead.getSubGeneration().getDescription());
        tx.rollback();
        s.close();
    }
}

