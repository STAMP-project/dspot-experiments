package org.hibernate.test.schemafilter;


import AvailableSettings.DIALECT;
import AvailableSettings.FORMAT_SQL;
import DialectChecks.SupportCatalogCreation;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.ServiceRegistryBuilder;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.schema.internal.DefaultSchemaFilter;
import org.hibernate.tool.schema.spi.SchemaFilter;
import org.junit.Assert;
import org.junit.Test;

import static org.hibernate.test.schemafilter.RecordingTarget.Category.TABLE_CREATE;
import static org.hibernate.test.schemafilter.RecordingTarget.Category.TABLE_DROP;


@SuppressWarnings({ "rawtypes", "unchecked" })
@RequiresDialectFeature({ SupportCatalogCreation.class })
public class CatalogFilterTest extends BaseUnitTestCase {
    private final ServiceRegistry serviceRegistry;

    private final Metadata metadata;

    public CatalogFilterTest() {
        Map settings = new HashMap();
        settings.putAll(Environment.getProperties());
        settings.put(DIALECT, SQLServerDialect.class.getName());
        settings.put(FORMAT_SQL, false);
        this.serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry(settings);
        MetadataSources ms = new MetadataSources(serviceRegistry);
        ms.addAnnotatedClass(CatalogFilterTest.CatalogNoneEntity0.class);
        ms.addAnnotatedClass(CatalogFilterTest.Catalog1Entity1.class);
        ms.addAnnotatedClass(CatalogFilterTest.Catalog1Entity2.class);
        ms.addAnnotatedClass(CatalogFilterTest.Catalog2Entity3.class);
        ms.addAnnotatedClass(CatalogFilterTest.Catalog2Entity4.class);
        this.metadata = ms.buildMetadata();
    }

    @Test
    public void createCatalog_unfiltered() {
        RecordingTarget target = doCreation(new DefaultSchemaFilter());
        Assert.assertThat(target.getActions(TABLE_CREATE), containsExactly("the_entity_0", "the_catalog_1.the_entity_1", "the_catalog_1.the_entity_2", "the_catalog_2.the_entity_3", "the_catalog_2.the_entity_4"));
    }

    @Test
    public void createCatalog_filtered() {
        RecordingTarget target = doCreation(new CatalogFilterTest.TestSchemaFilter());
        Assert.assertThat(target.getActions(TABLE_CREATE), containsExactly("the_entity_0", "the_catalog_1.the_entity_1"));
    }

    @Test
    public void dropCatalog_unfiltered() {
        RecordingTarget target = doDrop(new DefaultSchemaFilter());
        Assert.assertThat(target.getActions(TABLE_DROP), containsExactly("the_entity_0", "the_catalog_1.the_entity_1", "the_catalog_1.the_entity_2", "the_catalog_2.the_entity_3", "the_catalog_2.the_entity_4"));
    }

    @Test
    public void dropCatalog_filtered() {
        RecordingTarget target = doDrop(new CatalogFilterTest.TestSchemaFilter());
        Assert.assertThat(target.getActions(TABLE_DROP), containsExactly("the_entity_0", "the_catalog_1.the_entity_1"));
    }

    private static class TestSchemaFilter implements SchemaFilter {
        @Override
        public boolean includeNamespace(Namespace namespace) {
            // exclude schema "the_catalog_2"
            Identifier identifier = namespace.getName().getCatalog();
            return (identifier == null) || (!("the_catalog_2".equals(identifier.getText())));
        }

        @Override
        public boolean includeTable(Table table) {
            // exclude table "the_entity_2"
            return !("the_entity_2".equals(table.getName()));
        }

        @Override
        public boolean includeSequence(Sequence sequence) {
            return true;
        }
    }

    @Entity
    @Table(name = "the_entity_1", catalog = "the_catalog_1")
    public static class Catalog1Entity1 {
        @Id
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    @Entity
    @Table(name = "the_entity_2", catalog = "the_catalog_1")
    public static class Catalog1Entity2 {
        @Id
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    @Entity
    @Table(name = "the_entity_3", catalog = "the_catalog_2")
    public static class Catalog2Entity3 {
        @Id
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    @Entity
    @Table(name = "the_entity_4", catalog = "the_catalog_2")
    public static class Catalog2Entity4 {
        @Id
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }

    @Entity
    @Table(name = "the_entity_0")
    public static class CatalogNoneEntity0 {
        @Id
        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}

