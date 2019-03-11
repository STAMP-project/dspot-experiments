package org.hibernate.test.bulkid;


import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
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
public abstract class AbstractBulkCompositeIdTest extends BaseCoreFunctionalTestCase {
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
            // tag::batch-bulk-hql-temp-table-delete-query-example[]
            int updateCount = session.createQuery("delete from Person where employed = :employed").setParameter("employed", false).executeUpdate();
            // end::batch-bulk-hql-temp-table-delete-query-example[]
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

    // tag::batch-bulk-hql-temp-table-base-class-example[]
    // tag::batch-bulk-hql-temp-table-base-class-example[]
    @Entity(name = "Person")
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class Person implements Serializable {
        @Id
        private Integer id;

        @Id
        private String companyName;

        private String name;

        private boolean employed;

        // Getters and setters are omitted for brevity
        // end::batch-bulk-hql-temp-table-base-class-example[]
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
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

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof AbstractBulkCompositeIdTest.Person)) {
                return false;
            }
            AbstractBulkCompositeIdTest.Person person = ((AbstractBulkCompositeIdTest.Person) (o));
            return (Objects.equals(getId(), person.getId())) && (Objects.equals(getCompanyName(), person.getCompanyName()));
        }

        @Override
        public int hashCode() {
            return Objects.hash(getId(), getCompanyName());
        }
    }

    // end::batch-bulk-hql-temp-table-base-class-example[]
    // tag::batch-bulk-hql-temp-table-sub-classes-example[]
    @Entity(name = "Doctor")
    public static class Doctor extends AbstractBulkCompositeIdTest.Person {}

    // tag::batch-bulk-hql-temp-table-sub-classes-example[]
    @Entity(name = "Engineer")
    public static class Engineer extends AbstractBulkCompositeIdTest.Person {
        private boolean fellow;

        // Getters and setters are omitted for brevity
        // end::batch-bulk-hql-temp-table-sub-classes-example[]
        public boolean isFellow() {
            return fellow;
        }

        public void setFellow(boolean fellow) {
            this.fellow = fellow;
        }
    }
}

