/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idgen.identity;


import DialectChecks.SupportsIdentityColumns;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * The purpose of this test is to insure that when an entity that contains a single column
 * which also happens to be an identity-generated identifier, the generated insert SQL is
 * correct for the dialect.
 *
 * We found through research that SAP Hana doesn't support an empty values-list clause, the
 * omission of the values-list clause, or any default value in the values-list clause for
 * the identifier column; therefore we'll skip this test for that platform.
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-13104")
@RequiresDialectFeature(SupportsIdentityColumns.class)
@SkipForDialect(value = AbstractHANADialect.class, comment = "SAP HANA requires at least value in insert value-list clause.")
public class IdentityInsertSoleColumnTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testEntityInsertWithSingleIdentityAttributeColumn() {
        // insert the entity
        final Integer entityId = TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.test.idgen.identity.Animal animal = new org.hibernate.test.idgen.identity.Animal();
            entityManager.persist(animal);
            return animal.getId();
        });
        // make sure the identifier was generated
        Assert.assertNotNull(entityId);
        // verify the entity can be fetched
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.test.idgen.identity.Animal animal = entityManager.find(.class, entityId);
            assertNotNull(animal);
        });
    }

    @Entity(name = "Animal")
    public static class Animal {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}

