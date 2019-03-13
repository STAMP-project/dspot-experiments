/**
 *
 */
package org.hibernate.test.compositeusertype;


import java.util.Arrays;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Felix Feisst (feisst dot felix at gmail dot com)
 */
public class CompositeUserTypeTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9186")
    public void testRemovalWithNullableFields() {
        final Unit unit1 = Percent.INSTANCE;
        final Unit unit2 = new Currency("EUR");
        final Unit unit3 = new Currency("USD");
        final Integer id = 1;
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            TestEntity entity = new TestEntity();
            entity.setId(id);
            session.persist(entity);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            TestEntity entity = session.find(.class, id);
            assertNotNull("Expected an entity to be returned", entity);
            assertTrue("Expected no units", entity.getUnits().isEmpty());
            entity.getUnits().add(unit1);
            entity.getUnits().add(unit2);
            entity.getUnits().add(unit3);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            TestEntity entity = session.get(.class, id);
            assertNotNull("Expected an entity to be returned", entity);
            assertEquals("Unexpected units", new HashSet<>(Arrays.asList(unit1, unit2, unit3)), entity.getUnits());
            entity.getUnits().remove(unit2);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            TestEntity entity = session.get(.class, id);
            assertNotNull("Expected an entity to be returned", entity);
            assertEquals("Unexpected units", new HashSet<>(Arrays.asList(unit1, unit3)), entity.getUnits());
        });
    }
}

