/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.id.sequences;


import SequenceStyleGenerator.DEF_SEQUENCE_NAME;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.mapping.Table;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.test.annotations.id.sequences.entities.HibernateSequenceEntity;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@TestForIssue(jiraKey = "HHH-6068")
@RequiresDialect(H2Dialect.class)
public class HibernateSequenceTest extends BaseCoreFunctionalTestCase {
    private static final String SCHEMA_NAME = "OTHER_SCHEMA";

    @Test
    public void testHibernateSequenceSchema() {
        EntityPersister persister = sessionFactory().getEntityPersister(HibernateSequenceEntity.class.getName());
        IdentifierGenerator generator = persister.getIdentifierGenerator();
        Assert.assertTrue(SequenceStyleGenerator.class.isInstance(generator));
        SequenceStyleGenerator seqGenerator = ((SequenceStyleGenerator) (generator));
        Assert.assertEquals(Table.qualify(null, HibernateSequenceTest.SCHEMA_NAME, DEF_SEQUENCE_NAME), seqGenerator.getDatabaseStructure().getName());
    }

    @Test
    public void testHibernateSequenceNextVal() {
        Session session = openSession();
        Transaction txn = session.beginTransaction();
        HibernateSequenceEntity entity = new HibernateSequenceEntity();
        entity.setText("sample text");
        session.save(entity);
        txn.commit();
        session.close();
        Assert.assertNotNull(entity.getId());
    }
}

