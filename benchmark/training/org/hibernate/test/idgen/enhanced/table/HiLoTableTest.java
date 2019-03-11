/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idgen.enhanced.table;


import org.hibernate.Session;
import org.hibernate.id.enhanced.HiLoOptimizer;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class HiLoTableTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testNormalBoundary() {
        EntityPersister persister = sessionFactory().getEntityPersister(Entity.class.getName());
        ExtraAssertions.assertClassAssignability(TableGenerator.class, persister.getIdentifierGenerator().getClass());
        TableGenerator generator = ((TableGenerator) (persister.getIdentifierGenerator()));
        ExtraAssertions.assertClassAssignability(HiLoOptimizer.class, generator.getOptimizer().getClass());
        HiLoOptimizer optimizer = ((HiLoOptimizer) (generator.getOptimizer()));
        int increment = optimizer.getIncrementSize();
        Entity[] entities = new Entity[increment + 1];
        Session s = openSession();
        s.beginTransaction();
        for (int i = 0; i < increment; i++) {
            entities[i] = new Entity(("" + (i + 1)));
            s.save(entities[i]);
            Assert.assertEquals(1, generator.getTableAccessCount());// initialization

            Assert.assertEquals(1, getActualLongValue());// initialization

            Assert.assertEquals((i + 1), getActualLongValue());
            Assert.assertEquals((increment + 1), getActualLongValue());
        }
        // now force a "clock over"
        entities[increment] = new Entity(("" + increment));
        s.save(entities[increment]);
        Assert.assertEquals(2, generator.getTableAccessCount());// initialization

        Assert.assertEquals(2, getActualLongValue());// initialization

        Assert.assertEquals((increment + 1), getActualLongValue());
        Assert.assertEquals(((increment * 2) + 1), getActualLongValue());
        s.getTransaction().commit();
        s.beginTransaction();
        for (int i = 0; i < (entities.length); i++) {
            Assert.assertEquals((i + 1), entities[i].getId().intValue());
            s.delete(entities[i]);
        }
        s.getTransaction().commit();
        s.close();
    }
}

