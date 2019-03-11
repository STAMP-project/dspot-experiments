/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.query;


import JoinType.INNER;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11383")
public class InheritanceAssociationToOneInnerJoinTest extends BaseEnversJPAFunctionalTestCase {
    @Test
    @Priority(10)
    public void initData() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.query.EntityC c = new org.hibernate.envers.test.integration.query.EntityC();
            c.setId(1);
            c.setFoo("bar");
            entityManager.persist(c);
            final org.hibernate.envers.test.integration.query.EntityD d = new org.hibernate.envers.test.integration.query.EntityD();
            d.setId(1);
            d.setFoo("bar");
            entityManager.persist(d);
            final org.hibernate.envers.test.integration.query.EntityB b1 = new org.hibernate.envers.test.integration.query.EntityB();
            b1.setId(1);
            b1.setName("b1");
            b1.setRelationToC(c);
            b1.setRelationToD(d);
            entityManager.persist(b1);
            final org.hibernate.envers.test.integration.query.EntityB b2 = new org.hibernate.envers.test.integration.query.EntityB();
            b2.setId(2);
            b2.setName("b2");
            b2.setRelationToC(c);
            b2.setRelationToD(d);
            entityManager.persist(b2);
        });
    }

    @Test
    public void testAuditQueryWithJoinedInheritanceUsingWithSemanticsManyToOne() {
        List results = getAuditReader().createQuery().forEntitiesAtRevision(InheritanceAssociationToOneInnerJoinTest.EntityB.class, 1).add(disjunction().add(property("name").like("b1")).add(property("name").like("b2"))).traverseRelation("relationToC", INNER).add(property("foo").like("bar")).getResultList();
        Assert.assertEquals(2, results.size());
    }

    @Test
    public void testAuditQueryWithJoinedInheritanceUsingWithSemanticsOneToOne() {
        List results = getAuditReader().createQuery().forEntitiesAtRevision(InheritanceAssociationToOneInnerJoinTest.EntityB.class, 1).add(disjunction().add(property("name").like("b1")).add(property("name").like("b2"))).traverseRelation("relationToD", INNER).add(property("foo").like("bar")).getResultList();
        Assert.assertEquals(2, results.size());
    }

    @Test
    public void testAuditQueryWithJoinedInheritanceUsingWithSemanticsToOne() {
        List results = getAuditReader().createQuery().forEntitiesAtRevision(InheritanceAssociationToOneInnerJoinTest.EntityB.class, 1).add(disjunction().add(property("name").like("b1")).add(property("name").like("b2"))).traverseRelation("relationToC", INNER).add(property("foo").like("bar")).up().traverseRelation("relationToD", INNER).add(property("foo").like("bar")).getResultList();
        Assert.assertEquals(2, results.size());
    }

    @Test
    public void testAuditQueryWithJoinedInheritanceSubclassPropertyProjectionWithRelationTraversal() {
        // HHH-11383
        // This test was requested by the reporter so that we have a test that shows Hibernate is
        // automatically adding "INNER JOIN EntityA_AUD" despite the fact whether the query uses
        // the traverseRelation API or not.  This test makes sure that if the SQL generation is
        // changed in the future, Envers would properly fail if so.
        List results = getAuditReader().createQuery().forEntitiesAtRevision(InheritanceAssociationToOneInnerJoinTest.EntityB.class, 1).addProjection(property("name")).traverseRelation("relationToC", INNER).add(property("foo").like("bar")).getResultList();
        Assert.assertEquals(2, results.size());
    }

    @Entity(name = "EntityA")
    @Audited
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class EntityA {
        @Id
        private Integer id;

        @OneToOne
        private InheritanceAssociationToOneInnerJoinTest.EntityD relationToD;

        @ManyToOne
        private InheritanceAssociationToOneInnerJoinTest.EntityC relationToC;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public InheritanceAssociationToOneInnerJoinTest.EntityC getRelationToC() {
            return relationToC;
        }

        public void setRelationToC(InheritanceAssociationToOneInnerJoinTest.EntityC relationToC) {
            this.relationToC = relationToC;
        }

        public InheritanceAssociationToOneInnerJoinTest.EntityD getRelationToD() {
            return relationToD;
        }

        public void setRelationToD(InheritanceAssociationToOneInnerJoinTest.EntityD relationToD) {
            this.relationToD = relationToD;
        }
    }

    @Entity(name = "EntityB")
    @Audited
    public static class EntityB extends InheritanceAssociationToOneInnerJoinTest.EntityA {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Entity(name = "EntityC")
    @Audited
    public static class EntityC {
        @Id
        private Integer id;

        private String foo;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }

    @Entity(name = "EntityD")
    @Audited
    public static class EntityD {
        @Id
        private Integer id;

        private String foo;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }
}

