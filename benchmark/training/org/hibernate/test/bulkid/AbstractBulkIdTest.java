package org.hibernate.test.bulkid;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::batch-bulk-hql-temp-table-sub-classes-example[]
public abstract class AbstractBulkIdTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testUpdate() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            int updateCount = session.createQuery("update Person set name = :name where employed = :employed").setParameter("name", "John Doe").setParameter("employed", true).executeUpdate();
            assertEquals(entityCount(), updateCount);
        });
    }

    @Test
    public void testDeleteFromPerson() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            int updateCount = session.createQuery("delete from Person where employed = :employed").setParameter("employed", false).executeUpdate();
            assertEquals(entityCount(), updateCount);
        });
    }

    @Test
    public void testDeleteFromEngineer() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            int updateCount = session.createQuery("delete from Engineer where fellow = :fellow").setParameter("fellow", true).executeUpdate();
            assertEquals(((entityCount()) / 2), updateCount);
        });
    }

    @Entity(name = "Person")
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        private boolean employed;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isEmployed() {
            return employed;
        }

        public void setEmployed(boolean employed) {
            this.employed = employed;
        }
    }

    // end::batch-bulk-hql-temp-table-base-class-example[]
    // tag::batch-bulk-hql-temp-table-sub-classes-example[]
    @Entity(name = "Doctor")
    public static class Doctor extends AbstractBulkIdTest.Person {}

    @Entity(name = "Engineer")
    public static class Engineer extends AbstractBulkIdTest.Person {
        private boolean fellow;

        public boolean isFellow() {
            return fellow;
        }

        public void setFellow(boolean fellow) {
            this.fellow = fellow;
        }
    }
}

