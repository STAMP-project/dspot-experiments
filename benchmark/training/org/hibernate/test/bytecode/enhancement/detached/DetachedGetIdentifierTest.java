package org.hibernate.test.bytecode.enhancement.detached;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.SessionFactory;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Luis Barreiro
 */
@TestForIssue(jiraKey = "HHH-11426")
@RunWith(BytecodeEnhancerRunner.class)
public class DetachedGetIdentifierTest extends BaseCoreFunctionalTestCase {
    @Test
    public void test() {
        DetachedGetIdentifierTest.SimpleEntity[] entities = new DetachedGetIdentifierTest.SimpleEntity[2];
        entities[0] = new DetachedGetIdentifierTest.SimpleEntity();
        entities[0].name = "test";
        TransactionUtil.doInJPA(this::sessionFactory, ( em) -> {
            entities[1] = em.merge(entities[0]);
            assertNotNull(em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entities[1]));
        });
        // Call as detached entity
        try (SessionFactory sessionFactory = sessionFactory()) {
            Assert.assertNotNull(sessionFactory.getPersistenceUnitUtil().getIdentifier(entities[1]));
        }
    }

    // --- //
    @Entity
    @Table(name = "SIMPLE_ENTITY")
    private static class SimpleEntity {
        @Id
        @GeneratedValue
        Long id;

        String name;
    }
}

