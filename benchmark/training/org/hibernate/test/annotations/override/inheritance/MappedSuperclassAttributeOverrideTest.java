/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.override.inheritance;


import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class MappedSuperclassAttributeOverrideTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12609")
    public void test() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.test.annotations.override.inheritance.TaxonEntity taxon1 = new org.hibernate.test.annotations.override.inheritance.TaxonEntity();
            taxon1.setId(1L);
            taxon1.setCode("Taxon");
            taxon1.setCatalogVersion("C1");
            entityManager.persist(taxon1);
            org.hibernate.test.annotations.override.inheritance.TaxonEntity taxon2 = new org.hibernate.test.annotations.override.inheritance.TaxonEntity();
            taxon2.setId(2L);
            taxon2.setCode("Taxon");
            taxon2.setCatalogVersion("C2");
            entityManager.persist(taxon2);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            assertEquals(2, ((Number) (entityManager.createQuery(("select count(t) " + ("from Taxon t " + "where t.code = :code"))).setParameter("code", "Taxon").getSingleResult())).intValue());
        });
    }

    @MappedSuperclass
    public static class AbstractEntity {
        @Id
        private Long id;

        @Column(name = "code", nullable = false, unique = true)
        private String code;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    @Entity(name = "Category")
    public static class CategoryEntity extends MappedSuperclassAttributeOverrideTest.AbstractEntity {}

    @Entity(name = "Taxon")
    @Table(name = "taxon", uniqueConstraints = @UniqueConstraint(columnNames = { "catalog_version_id", "code" }))
    @AttributeOverride(name = "code", column = @Column(name = "code", nullable = false, unique = false))
    public static class TaxonEntity extends MappedSuperclassAttributeOverrideTest.AbstractEntity {
        @Column(name = "catalog_version_id")
        private String catalogVersion;

        public String getCatalogVersion() {
            return catalogVersion;
        }

        public void setCatalogVersion(String catalogVersion) {
            this.catalogVersion = catalogVersion;
        }
    }
}

