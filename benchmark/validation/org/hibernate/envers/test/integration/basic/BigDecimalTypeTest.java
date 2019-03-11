/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.basic;


import java.math.BigDecimal;
import java.util.Arrays;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11988")
public class BigDecimalTypeTest extends BaseEnversJPAFunctionalTestCase {
    private Integer entityId;

    private Double bigDecimalValue = 2.2;

    @Test
    @Priority(10)
    public void initData() {
        // Triggers RevisionType.ADD
        EntityManager entityManager = getOrCreateEntityManager();
        try {
            entityManager.getTransaction().begin();
            final BigDecimalTypeTest.BigDecimalEntity entity = new BigDecimalTypeTest.BigDecimalEntity(BigDecimal.valueOf(bigDecimalValue), "Test");
            System.out.println(entity.getBigDecimal().scale());
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
            this.entityId = entity.getId();
        } catch (Throwable t) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw t;
        } finally {
            entityManager.close();
        }
        // Should *not* trigger a revision
        entityManager = getOrCreateEntityManager();
        try {
            entityManager.getTransaction().begin();
            final BigDecimalTypeTest.BigDecimalEntity entity = entityManager.find(BigDecimalTypeTest.BigDecimalEntity.class, entityId);
            entity.setData("Updated");
            entity.setBigDecimal(BigDecimal.valueOf(bigDecimalValue));
            entityManager.merge(entity);
            entityManager.getTransaction().commit();
        } catch (Throwable t) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw t;
        } finally {
            entityManager.close();
        }
        // Triggers RevisionType.MOD
        entityManager = getOrCreateEntityManager();
        try {
            entityManager.getTransaction().begin();
            final BigDecimalTypeTest.BigDecimalEntity entity = entityManager.find(BigDecimalTypeTest.BigDecimalEntity.class, entityId);
            entity.setData("Updated2");
            entity.setBigDecimal(BigDecimal.valueOf(((bigDecimalValue) + 1.0)));
            entityManager.merge(entity);
            entityManager.getTransaction().commit();
        } catch (Throwable t) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw t;
        } finally {
            entityManager.close();
        }
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1, 2), getAuditReader().getRevisions(BigDecimalTypeTest.BigDecimalEntity.class, entityId));
    }

    @Test
    public void testRevisionHistory() {
        final BigDecimalTypeTest.BigDecimalEntity rev1 = getAuditReader().find(BigDecimalTypeTest.BigDecimalEntity.class, entityId, 1);
        Assert.assertTrue(((BigDecimal.valueOf(bigDecimalValue).compareTo(rev1.getBigDecimal())) == 0));
        Assert.assertNull(rev1.getData());
        final BigDecimalTypeTest.BigDecimalEntity rev2 = getAuditReader().find(BigDecimalTypeTest.BigDecimalEntity.class, entityId, 2);
        Assert.assertTrue(((BigDecimal.valueOf(((bigDecimalValue) + 1.0)).compareTo(rev2.getBigDecimal())) == 0));
        Assert.assertNull(rev2.getData());
    }

    @Entity(name = "BigDecimalEntity")
    @Audited
    public static class BigDecimalEntity {
        @Id
        @GeneratedValue
        private Integer id;

        @Column(precision = 23, scale = 6)
        private BigDecimal bigDecimal;

        @NotAudited
        private String data;

        BigDecimalEntity() {
        }

        BigDecimalEntity(BigDecimal bigDecimal, String data) {
            this.bigDecimal = bigDecimal;
            this.data = data;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public BigDecimal getBigDecimal() {
            return bigDecimal;
        }

        public void setBigDecimal(BigDecimal bigDecimal) {
            this.bigDecimal = bigDecimal;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}

