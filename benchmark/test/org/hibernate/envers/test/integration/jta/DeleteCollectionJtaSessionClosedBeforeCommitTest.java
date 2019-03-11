/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.jta;


import TestingJtaPlatformImpl.INSTANCE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jta.TestingJtaPlatformImpl;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-11580")
public class DeleteCollectionJtaSessionClosedBeforeCommitTest extends BaseEnversJPAFunctionalTestCase {
    private static final int ENTITY_ID = 1;

    private static final int OTHER_ENTITY_ID = 2;

    @Test
    @Priority(10)
    public void initData() throws Exception {
        INSTANCE.getTransactionManager().begin();
        EntityManager entityManager = getEntityManager();
        try {
            DeleteCollectionJtaSessionClosedBeforeCommitTest.TestEntity entity = new DeleteCollectionJtaSessionClosedBeforeCommitTest.TestEntity(DeleteCollectionJtaSessionClosedBeforeCommitTest.ENTITY_ID, "Fab");
            entityManager.persist(entity);
            DeleteCollectionJtaSessionClosedBeforeCommitTest.OtherTestEntity other = new DeleteCollectionJtaSessionClosedBeforeCommitTest.OtherTestEntity(DeleteCollectionJtaSessionClosedBeforeCommitTest.OTHER_ENTITY_ID, "other");
            entity.addOther(other);
            entityManager.persist(entity);
            entityManager.persist(other);
        } finally {
            entityManager.close();
            TestingJtaPlatformImpl.tryCommit();
        }
        INSTANCE.getTransactionManager().begin();
        entityManager = getEntityManager();
        try {
            DeleteCollectionJtaSessionClosedBeforeCommitTest.TestEntity entity = entityManager.find(DeleteCollectionJtaSessionClosedBeforeCommitTest.TestEntity.class, DeleteCollectionJtaSessionClosedBeforeCommitTest.ENTITY_ID);
            DeleteCollectionJtaSessionClosedBeforeCommitTest.OtherTestEntity other = entityManager.find(DeleteCollectionJtaSessionClosedBeforeCommitTest.OtherTestEntity.class, DeleteCollectionJtaSessionClosedBeforeCommitTest.OTHER_ENTITY_ID);
            entityManager.remove(entity);
            entityManager.remove(other);
        } finally {
            entityManager.close();
            TestingJtaPlatformImpl.tryCommit();
        }
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1, 2), getAuditReader().getRevisions(DeleteCollectionJtaSessionClosedBeforeCommitTest.TestEntity.class, DeleteCollectionJtaSessionClosedBeforeCommitTest.ENTITY_ID));
    }

    @Test
    public void testRevisionHistory() {
        Assert.assertEquals(new DeleteCollectionJtaSessionClosedBeforeCommitTest.TestEntity(1, "Fab"), getAuditReader().find(DeleteCollectionJtaSessionClosedBeforeCommitTest.TestEntity.class, DeleteCollectionJtaSessionClosedBeforeCommitTest.ENTITY_ID, 1));
    }

    @Audited
    @Entity
    @Table(name = "ENTITY")
    public static class TestEntity {
        @Id
        private Integer id;

        private String name;

        @OneToMany
        @JoinTable(name = "LINK_TABLE", joinColumns = @JoinColumn(name = "ENTITY_ID"))
        private List<DeleteCollectionJtaSessionClosedBeforeCommitTest.OtherTestEntity> others = new ArrayList<>();

        public TestEntity() {
        }

        public TestEntity(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void addOther(DeleteCollectionJtaSessionClosedBeforeCommitTest.OtherTestEntity other) {
            this.others.add(other);
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            DeleteCollectionJtaSessionClosedBeforeCommitTest.TestEntity that = ((DeleteCollectionJtaSessionClosedBeforeCommitTest.TestEntity) (o));
            if ((getId()) != null ? !(getId().equals(that.getId())) : (that.getId()) != null) {
                return false;
            }
            return (name) != null ? name.equals(that.name) : (that.name) == null;
        }

        @Override
        public int hashCode() {
            int result = ((getId()) != null) ? getId().hashCode() : 0;
            result = (31 * result) + ((name) != null ? name.hashCode() : 0);
            return result;
        }
    }

    @Audited
    @Entity
    @Table(name = "O_ENTITY")
    public static class OtherTestEntity {
        @Id
        private Integer id;

        private String name;

        public OtherTestEntity() {
        }

        public OtherTestEntity(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}

