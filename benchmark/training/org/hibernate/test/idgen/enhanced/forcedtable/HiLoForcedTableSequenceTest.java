/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idgen.enhanced.forcedtable;


import org.hibernate.Session;
import org.hibernate.id.enhanced.HiLoOptimizer;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.id.enhanced.TableStructure;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * {@inheritDoc }
 *
 * @author Steve Ebersole
 */
public class HiLoForcedTableSequenceTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testNormalBoundary() {
        EntityPersister persister = sessionFactory().getEntityPersister(Entity.class.getName());
        Assert.assertTrue("sequence style generator was not used", SequenceStyleGenerator.class.isInstance(persister.getIdentifierGenerator()));
        SequenceStyleGenerator generator = ((SequenceStyleGenerator) (persister.getIdentifierGenerator()));
        Assert.assertTrue("table structure was not used", TableStructure.class.isInstance(generator.getDatabaseStructure()));
        Assert.assertTrue("hilo optimizer was not used", HiLoOptimizer.class.isInstance(generator.getOptimizer()));
        HiLoOptimizer optimizer = ((HiLoOptimizer) (generator.getOptimizer()));
        int increment = optimizer.getIncrementSize();
        Entity[] entities = new Entity[increment + 1];
        Session s = openSession();
        s.beginTransaction();
        for (int i = 0; i < increment; i++) {
            entities[i] = new Entity(("" + (i + 1)));
            s.save(entities[i]);
            long expectedId = i + 1;
            Assert.assertEquals(expectedId, entities[i].getId().longValue());
            Assert.assertEquals(1, getActualLongValue());
            Assert.assertEquals((i + 1), getActualLongValue());
            Assert.assertEquals((increment + 1), getActualLongValue());
        }
        // now force a "clock over"
        entities[increment] = new Entity(("" + increment));
        s.save(entities[increment]);
        long expectedId = (optimizer.getIncrementSize()) + 1;
        Assert.assertEquals(expectedId, entities[optimizer.getIncrementSize()].getId().longValue());
        Assert.assertEquals(2, getActualLongValue());// initialization + clock-over

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

