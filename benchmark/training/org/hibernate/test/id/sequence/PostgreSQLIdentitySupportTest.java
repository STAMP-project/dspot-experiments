/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.id.sequence;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.dialect.PostgreSQL10Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mhalcea
 */
@RequiresDialect(jiraKey = "HHH-13202", value = PostgreSQL10Dialect.class)
public class PostgreSQLIdentitySupportTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        PostgreSQLIdentitySupportTest.Role _role = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.id.sequence.Role role = new org.hibernate.test.id.sequence.Role();
            entityManager.persist(role);
            return role;
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.id.sequence.Role role = entityManager.find(.class, _role.getId());
            assertNotNull(role);
        });
    }

    @Entity(name = "Role")
    public static class Role {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(final Long id) {
            this.id = id;
        }
    }
}

