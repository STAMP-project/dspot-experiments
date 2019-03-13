/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.inheritance.joined;


import java.util.Arrays;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
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
@TestForIssue(jiraKey = "HHH-11133")
public class DiscriminatorJoinedInheritanceTest extends BaseEnversJPAFunctionalTestCase {
    @Test
    @Priority(10)
    public void initData() {
        EntityManager entityManager = getEntityManager();
        try {
            DiscriminatorJoinedInheritanceTest.ChildEntity childEntity = new DiscriminatorJoinedInheritanceTest.ChildEntity(1, "Child");
            entityManager.getTransaction().begin();
            entityManager.persist(childEntity);
            entityManager.getTransaction().commit();
            DiscriminatorJoinedInheritanceTest.ChildListHolder holder = new DiscriminatorJoinedInheritanceTest.ChildListHolder();
            holder.setId(1);
            holder.setChildren(Arrays.asList(childEntity));
            entityManager.getTransaction().begin();
            entityManager.persist(holder);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(DiscriminatorJoinedInheritanceTest.ChildEntity.class, 1));
        Assert.assertEquals(Arrays.asList(2), getAuditReader().getRevisions(DiscriminatorJoinedInheritanceTest.ChildListHolder.class, 1));
    }

    @Test
    public void testConfiguredDiscriminatorValue() {
        DiscriminatorJoinedInheritanceTest.ChildEntity entity = getAuditReader().find(DiscriminatorJoinedInheritanceTest.ChildEntity.class, 1, 1);
        Assert.assertEquals("ce", entity.getType());
    }

    @Test
    public void testDiscriminatorValuesViaRelatedEntityQuery() {
        DiscriminatorJoinedInheritanceTest.ChildListHolder holder = getAuditReader().find(DiscriminatorJoinedInheritanceTest.ChildListHolder.class, 1, 2);
        Assert.assertEquals(1, holder.getChildren().size());
        Assert.assertEquals("ce", holder.getChildren().get(0).getType());
    }

    @Entity(name = "ParentEntity")
    @Audited
    @Inheritance(strategy = InheritanceType.JOINED)
    @DiscriminatorValue("pe")
    @DiscriminatorColumn(name = "type", length = 255)
    public abstract static class ParentEntity {
        @Id
        private Integer id;

        @Column(insertable = false, updatable = false)
        private String type;

        ParentEntity() {
        }

        ParentEntity(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        private void setType(String type) {
            this.type = type;
        }
    }

    @Entity(name = "ChildEntity")
    @Audited
    @DiscriminatorValue("ce")
    public static class ChildEntity extends DiscriminatorJoinedInheritanceTest.ParentEntity {
        private String name;

        ChildEntity() {
        }

        ChildEntity(Integer id, String name) {
            super(id);
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Entity(name = "ChildListHolder")
    @Table(name = "CHILD_HOLDER")
    @Audited
    public static class ChildListHolder {
        @Id
        private Integer id;

        @OneToMany
        private List<DiscriminatorJoinedInheritanceTest.ChildEntity> children;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public List<DiscriminatorJoinedInheritanceTest.ChildEntity> getChildren() {
            return children;
        }

        public void setChildren(List<DiscriminatorJoinedInheritanceTest.ChildEntity> children) {
            this.children = children;
        }
    }
}

