package org.hibernate.test.pagination;


import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.dialect.Oracle9iDialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(Oracle9iDialect.class)
public class OraclePaginationTest extends BaseEntityManagerFunctionalTestCase {
    @Entity(name = "RootEntity")
    @Table(name = "V_MYTABLE_LAST")
    public static class RootEntity implements Serializable {
        @Id
        private Long id;

        @Id
        private Long version;

        private String caption;

        private Long status;

        public RootEntity() {
        }

        public RootEntity(Long id, Long version, String caption, Long status) {
            this.id = id;
            this.version = version;
            this.caption = caption;
            this.status = status;
        }

        public Long getId() {
            return id;
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12087")
    public void testPagination() throws Exception {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(1L, 7L, "t40", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(16L, 1L, "t47", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(11L, 2L, "t43", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(6L, 4L, "t31", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(15L, 1L, "t46", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(2L, 6L, "t39", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(14L, 1L, "t45", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(4L, 5L, "t38", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(8L, 2L, "t29", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(17L, 1L, "t48", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(3L, 3L, "t21", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(7L, 2L, "t23", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(9L, 2L, "t30", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(10L, 3L, "t42", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(12L, 1L, "t41", 2L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(5L, 6L, "t37", 1L));
            entityManager.persist(new org.hibernate.test.pagination.RootEntity(13L, 1L, "t44", 1L));
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<org.hibernate.test.pagination.RootEntity> rootEntitiesAllPages = getLimitedRows(entityManager, 0, 10);
            List<org.hibernate.test.pagination.RootEntity> rootEntitiesFirst = getLimitedRows(entityManager, 0, 5);
            List<org.hibernate.test.pagination.RootEntity> rootEntitiesSecond = getLimitedRows(entityManager, 5, 10);
            assertEquals(rootEntitiesAllPages.get(0).getId(), rootEntitiesFirst.get(0).getId());
            assertEquals(rootEntitiesAllPages.get(1).getId(), rootEntitiesFirst.get(1).getId());
            assertEquals(rootEntitiesAllPages.get(2).getId(), rootEntitiesFirst.get(2).getId());
            assertEquals(rootEntitiesAllPages.get(3).getId(), rootEntitiesFirst.get(3).getId());
            assertEquals(rootEntitiesAllPages.get(4).getId(), rootEntitiesFirst.get(4).getId());
            assertEquals(rootEntitiesAllPages.get(5).getId(), rootEntitiesSecond.get(0).getId());
            assertEquals(rootEntitiesAllPages.get(6).getId(), rootEntitiesSecond.get(1).getId());
            assertEquals(rootEntitiesAllPages.get(7).getId(), rootEntitiesSecond.get(2).getId());
            assertEquals(rootEntitiesAllPages.get(8).getId(), rootEntitiesSecond.get(3).getId());
            assertEquals(rootEntitiesAllPages.get(9).getId(), rootEntitiesSecond.get(4).getId());
        });
    }
}

