/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idgen.enhanced.table;


import org.hibernate.Session;
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
public class BasicTableTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testNormalBoundary() {
        EntityPersister persister = sessionFactory().getEntityPersister(Entity.class.getName());
        ExtraAssertions.assertClassAssignability(TableGenerator.class, persister.getIdentifierGenerator().getClass());
        TableGenerator generator = ((TableGenerator) (persister.getIdentifierGenerator()));
        int count = 5;
        Entity[] entities = new Entity[count];
        Session s = openSession();
        s.beginTransaction();
        for (int i = 0; i < count; i++) {
            entities[i] = new Entity(("" + (i + 1)));
            s.save(entities[i]);
            long expectedId = i + 1;
            Assert.assertEquals(expectedId, entities[i].getId().longValue());
            Assert.assertEquals(expectedId, generator.getTableAccessCount());
            Assert.assertEquals(expectedId, getActualLongValue());
        }
        s.getTransaction().commit();
        s.beginTransaction();
        for (int i = 0; i < count; i++) {
            Assert.assertEquals((i + 1), entities[i].getId().intValue());
            s.delete(entities[i]);
        }
        s.getTransaction().commit();
        s.close();
    }
}

