/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idgen.enhanced.sequence;


import org.hibernate.Session;
import org.hibernate.id.enhanced.PooledOptimizer;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
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
public class PooledSequenceTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testNormalBoundary() {
        EntityPersister persister = sessionFactory().getEntityPersister(Entity.class.getName());
        ExtraAssertions.assertClassAssignability(SequenceStyleGenerator.class, persister.getIdentifierGenerator().getClass());
        SequenceStyleGenerator generator = ((SequenceStyleGenerator) (persister.getIdentifierGenerator()));
        ExtraAssertions.assertClassAssignability(PooledOptimizer.class, generator.getOptimizer().getClass());
        PooledOptimizer optimizer = ((PooledOptimizer) (generator.getOptimizer()));
        int increment = optimizer.getIncrementSize();
        Entity[] entities = new Entity[increment + 2];
        Session s = openSession();
        s.beginTransaction();
        for (int i = 0; i <= increment; i++) {
            entities[i] = new Entity(("" + (i + 1)));
            s.save(entities[i]);
            Assert.assertEquals(2, generator.getDatabaseStructure().getTimesAccessed());// initialization calls seq twice

            Assert.assertEquals((increment + 1), getActualLongValue());// initialization calls seq twice

            Assert.assertEquals((i + 1), getActualLongValue());
            Assert.assertEquals((increment + 1), getActualLongValue());
        }
        // now force a "clock over"
        entities[(increment + 1)] = new Entity(("" + increment));
        s.save(entities[(increment + 1)]);
        Assert.assertEquals(3, generator.getDatabaseStructure().getTimesAccessed());// initialization (2) + clock over

        Assert.assertEquals(((increment * 2) + 1), getActualLongValue());// initialization (2) + clock over

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

