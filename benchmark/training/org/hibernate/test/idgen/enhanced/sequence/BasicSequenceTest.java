/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idgen.enhanced.sequence;


import org.hibernate.Session;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
public class BasicSequenceTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testNormalBoundary() {
        EntityPersister persister = sessionFactory().getEntityPersister(Entity.class.getName());
        ExtraAssertions.assertClassAssignability(SequenceStyleGenerator.class, persister.getIdentifierGenerator().getClass());
        SequenceStyleGenerator generator = ((SequenceStyleGenerator) (persister.getIdentifierGenerator()));
        int count = 5;
        Entity[] entities = new Entity[count];
        Session s = openSession();
        s.beginTransaction();
        for (int i = 0; i < count; i++) {
            entities[i] = new Entity(("" + (i + 1)));
            s.save(entities[i]);
            long expectedId = i + 1;
            Assert.assertEquals(expectedId, entities[i].getId().longValue());
            Assert.assertEquals(expectedId, generator.getDatabaseStructure().getTimesAccessed());
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

    @Test
    @TestForIssue(jiraKey = "HHH-6790")
    public void testSequencePerEntity() {
        final String overriddenEntityName = "SpecialEntity";
        EntityPersister persister = sessionFactory().getEntityPersister(overriddenEntityName);
        ExtraAssertions.assertClassAssignability(SequenceStyleGenerator.class, persister.getIdentifierGenerator().getClass());
        SequenceStyleGenerator generator = ((SequenceStyleGenerator) (persister.getIdentifierGenerator()));
        Assert.assertEquals((overriddenEntityName + (SequenceStyleGenerator.DEF_SEQUENCE_SUFFIX)), generator.getDatabaseStructure().getName());
        Session s = openSession();
        s.beginTransaction();
        Entity entity1 = new Entity("1");
        s.save(overriddenEntityName, entity1);
        Entity entity2 = new Entity("2");
        s.save(overriddenEntityName, entity2);
        s.getTransaction().commit();
        Assert.assertEquals(1, entity1.getId().intValue());
        Assert.assertEquals(2, entity2.getId().intValue());
    }
}

