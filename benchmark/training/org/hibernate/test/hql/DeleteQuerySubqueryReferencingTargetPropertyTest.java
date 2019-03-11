package org.hibernate.test.hql;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12492")
public class DeleteQuerySubqueryReferencingTargetPropertyTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testSubQueryReferencingTargetProperty() {
        // prepare
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.hql.Master m1 = new org.hibernate.test.hql.Master();
            entityManager.persist(m1);
            org.hibernate.test.hql.Detail d11 = new org.hibernate.test.hql.Detail(m1);
            entityManager.persist(d11);
            org.hibernate.test.hql.Detail d12 = new org.hibernate.test.hql.Detail(m1);
            entityManager.persist(d12);
            org.hibernate.test.hql.Master m2 = new org.hibernate.test.hql.Master();
            entityManager.persist(m2);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // depending on the generated ids above this delete removes all masters or nothing
            // removal of all masters results in foreign key constraint violation
            // removal of nothing is incorrect since 2nd master does not have any details
            // DO NOT CHANGE this query: it used to trigger a very specific bug caused
            // by the alias not being added to the generated query
            String d = "delete from Master m where not exists (select d from Detail d where d.master=m)";
            Query del = entityManager.createQuery(d);
            del.executeUpdate();
            // so check for exactly one master after deletion
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.test.hql.Master> query = builder.createQuery(.class);
            query.select(query.from(.class));
            Assert.assertEquals(1, entityManager.createQuery(query).getResultList().size());
        });
    }

    @Entity(name = "Master")
    public static class Master {
        @Id
        @GeneratedValue
        private Integer id;
    }

    @Entity(name = "Detail")
    public static class Detail {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToOne(optional = false)
        private DeleteQuerySubqueryReferencingTargetPropertyTest.Master master;

        public Detail(DeleteQuerySubqueryReferencingTargetPropertyTest.Master master) {
            this.master = master;
        }
    }
}

