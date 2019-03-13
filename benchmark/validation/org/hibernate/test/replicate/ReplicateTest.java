/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.replicate;


import DialectChecks.SupportsIdentityColumns;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 * Test trying to replicate HHH-11514
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-11514")
@RequiresDialectFeature(SupportsIdentityColumns.class)
@FailureExpected(jiraKey = "HHH-11514")
public class ReplicateTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void refreshTest() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.replicate.City city = new org.hibernate.test.replicate.City();
            city.setId(100L);
            city.setName("Cluj-Napoca");
            entityManager.unwrap(.class).replicate(city, ReplicationMode.OVERWRITE);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.replicate.City city = entityManager.find(.class, 100L);
            assertEquals("Cluj-Napoca", city.getName());
        });
    }

    @Entity(name = "City")
    public static class City {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
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

