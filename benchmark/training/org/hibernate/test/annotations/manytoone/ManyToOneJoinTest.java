/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.manytoone;


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
public class ManyToOneJoinTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testManyToOneJoinTable() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        ForestType forest = new ForestType();
        forest.setName("Original forest");
        s.persist(forest);
        TreeType tree = new TreeType();
        tree.setForestType(forest);
        tree.setAlternativeForestType(forest);
        tree.setName("just a tree");
        s.persist(tree);
        s.flush();
        s.clear();
        tree = ((TreeType) (s.get(TreeType.class, tree.getId())));
        Assert.assertNotNull(tree.getForestType());
        Assert.assertNotNull(tree.getAlternativeForestType());
        s.clear();
        forest = ((ForestType) (s.get(ForestType.class, forest.getId())));
        Assert.assertEquals(1, forest.getTrees().size());
        Assert.assertEquals(tree.getId(), forest.getTrees().iterator().next().getId());
        tx.rollback();
        s.close();
    }

    @Test
    public void testOneToOneJoinTable() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        ForestType forest = new ForestType();
        forest.setName("Original forest");
        s.persist(forest);
        BiggestForest forestRepr = new BiggestForest();
        forestRepr.setType(forest);
        forest.setBiggestRepresentative(forestRepr);
        s.persist(forestRepr);
        s.flush();
        s.clear();
        forest = ((ForestType) (s.get(ForestType.class, forest.getId())));
        Assert.assertNotNull(forest.getBiggestRepresentative());
        Assert.assertEquals(forest, forest.getBiggestRepresentative().getType());
        tx.rollback();
        s.close();
    }
}

