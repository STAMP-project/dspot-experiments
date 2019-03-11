/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idgen.enhanced.forcedtable;


import org.hibernate.Session;
import org.hibernate.id.enhanced.PooledOptimizer;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.id.enhanced.TableStructure;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class PooledForcedTableSequenceTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testNormalBoundary() {
        EntityPersister persister = sessionFactory().getEntityPersister(Entity.class.getName());
        Assert.assertTrue("sequence style generator was not used", SequenceStyleGenerator.class.isInstance(persister.getIdentifierGenerator()));
        SequenceStyleGenerator generator = ((SequenceStyleGenerator) (persister.getIdentifierGenerator()));
        Assert.assertTrue("table structure was not used", TableStructure.class.isInstance(generator.getDatabaseStructure()));
        Assert.assertTrue("pooled optimizer was not used", PooledOptimizer.class.isInstance(generator.getOptimizer()));
        PooledOptimizer optimizer = ((PooledOptimizer) (generator.getOptimizer()));
        int increment = optimizer.getIncrementSize();
        Entity[] entities = new Entity[increment + 2];
        Session s = openSession();
        s.beginTransaction();
        for (int i = 0; i <= increment; i++) {
            entities[i] = new Entity(("" + (i + 1)));
            s.save(entities[i]);
            long expectedId = i + 1;
            Assert.assertEquals(expectedId, entities[i].getId().longValue());
            // NOTE : initialization calls table twice
            Assert.assertEquals(2, generator.getDatabaseStructure().getTimesAccessed());
            Assert.assertEquals((increment + 1), getActualLongValue());
            Assert.assertEquals((i + 1), getActualLongValue());
            Assert.assertEquals((increment + 1), getActualLongValue());
        }
        // now force a "clock over"
        entities[(increment + 1)] = new Entity(("" + increment));
        s.save(entities[(increment + 1)]);
        long expectedId = (optimizer.getIncrementSize()) + 2;
        Assert.assertEquals(expectedId, entities[(increment + 1)].getId().longValue());
        // initialization (2) + clock over
        Assert.assertEquals(3, generator.getDatabaseStructure().getTimesAccessed());
        Assert.assertEquals(((increment * 2) + 1), getActualLongValue());
        Assert.assertEquals((increment + 2), getActualLongValue());
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

