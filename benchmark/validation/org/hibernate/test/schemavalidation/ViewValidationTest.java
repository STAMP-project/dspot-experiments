/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemavalidation;


import AvailableSettings.ENABLE_SYNONYMS;
import AvailableSettings.HBM2DDL_JDBC_METADATA_EXTRACTOR_STRATEGY;
import JdbcMetadaAccessStrategy.GROUPED;
import JdbcMetadaAccessStrategy.INDIVIDUALLY;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.RequiresDialects;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.tool.hbm2ddl.SchemaValidator;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@RequiresDialects({ @RequiresDialect(PostgreSQL81Dialect.class), @RequiresDialect(H2Dialect.class) })
public class ViewValidationTest extends BaseCoreFunctionalTestCase {
    private StandardServiceRegistry ssr;

    @Test
    public void testSynonymUsingGroupedSchemaValidator() {
        ssr = new StandardServiceRegistryBuilder().applySetting(ENABLE_SYNONYMS, "true").applySetting(HBM2DDL_JDBC_METADATA_EXTRACTOR_STRATEGY, GROUPED).build();
        try {
            final MetadataSources metadataSources = new MetadataSources(ssr);
            metadataSources.addAnnotatedClass(ViewValidationTest.TestEntityWithSynonym.class);
            metadataSources.addAnnotatedClass(ViewValidationTest.TestEntity.class);
            new SchemaValidator().validate(metadataSources.buildMetadata());
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Test
    public void testSynonymUsingIndividuallySchemaValidator() {
        ssr = new StandardServiceRegistryBuilder().applySetting(ENABLE_SYNONYMS, "true").applySetting(HBM2DDL_JDBC_METADATA_EXTRACTOR_STRATEGY, INDIVIDUALLY).build();
        try {
            final MetadataSources metadataSources = new MetadataSources(ssr);
            metadataSources.addAnnotatedClass(ViewValidationTest.TestEntityWithSynonym.class);
            metadataSources.addAnnotatedClass(ViewValidationTest.TestEntity.class);
            new SchemaValidator().validate(metadataSources.buildMetadata());
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Entity
    @Table(name = "test_entity")
    private static class TestEntity {
        @Id
        @GeneratedValue
        private Long id;

        @Column(nullable = false)
        private String key;

        private String value;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Entity
    @Table(name = "test_synonym")
    private static class TestEntityWithSynonym {
        @Id
        @GeneratedValue
        private Long id;

        @Column(nullable = false)
        private String key;

        private String value;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

