/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.dialect;


import java.util.Arrays;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11747")
@RequiresDialect(DB2390Dialect.class)
public class DB2390DialectTestCase extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLegacyLimitHandlerWithNoOffset() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<org.hibernate.dialect.SimpleEntity> results = entityManager.createQuery("FROM SimpleEntity", .class).setMaxResults(2).getResultList();
            assertEquals(Arrays.asList(0, 1), results.stream().map(org.hibernate.dialect.SimpleEntity::getId).collect(Collectors.toList()));
        });
    }

    @Test
    public void testLegacyLimitHandlerWithOffset() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<org.hibernate.dialect.SimpleEntity> results = entityManager.createQuery("FROM SimpleEntity", .class).setFirstResult(2).setMaxResults(2).getResultList();
            assertEquals(Arrays.asList(2, 3), results.stream().map(org.hibernate.dialect.SimpleEntity::getId).collect(Collectors.toList()));
        });
    }

    @Entity(name = "SimpleEntity")
    public static class SimpleEntity {
        @Id
        private Integer id;

        private String name;

        public SimpleEntity() {
        }

        public SimpleEntity(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

