package org.hibernate.test.hql;


import javax.persistence.InheritanceType;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-13169")
public class UpdateJoinedSubclassCorrelationTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testJoinedSubclassUpdateWithCorrelation() {
        // prepare
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.hql.Master m1 = new org.hibernate.test.hql.SubMaster(1, null);
            entityManager.persist(m1);
            org.hibernate.test.hql.Detail d11 = new org.hibernate.test.hql.Detail(10, m1);
            entityManager.persist(d11);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // DO NOT CHANGE this query: it used to trigger a very specific bug caused
            // by the root table alias being added to the generated subquery instead of the table name
            String u = "update SubMaster m set name = (select 'test' from Detail d where d.master = m)";
            Query updateQuery = entityManager.createQuery(u);
            updateQuery.executeUpdate();
            // so check if the name of the SubMaster has been correctly updated
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.test.hql.Master> query = builder.createQuery(.class);
            query.select(query.from(.class));
            List<org.hibernate.test.hql.Master> masters = entityManager.createQuery(query).getResultList();
            Assert.assertEquals(1, masters.size());
            Assert.assertEquals("test", ((org.hibernate.test.hql.SubMaster) (masters.get(0))).name);
        });
    }

    @Inheritance(strategy = InheritanceType.JOINED)
    @Entity(name = "Master")
    public abstract static class Master {
        @Id
        private Integer id;

        public Master() {
        }

        public Master(Integer id) {
            this.id = id;
        }
    }

    @Entity(name = "SubMaster")
    public static class SubMaster extends UpdateJoinedSubclassCorrelationTest.Master {
        private String name;

        public SubMaster() {
        }

        public SubMaster(Integer id, String name) {
            super(id);
            this.name = name;
        }
    }

    @Entity(name = "Detail")
    public static class Detail {
        @Id
        private Integer id;

        @ManyToOne(optional = false)
        private UpdateJoinedSubclassCorrelationTest.Master master;

        public Detail(Integer id, UpdateJoinedSubclassCorrelationTest.Master master) {
            this.id = id;
            this.master = master;
        }
    }
}

